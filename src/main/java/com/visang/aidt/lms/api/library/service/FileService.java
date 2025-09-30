package com.visang.aidt.lms.api.library.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.visang.aidt.lms.api.common.mngrAction.constant.MngrActionType;
import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import com.visang.aidt.lms.api.dashboard.model.VivaClassApiDto;
import com.visang.aidt.lms.api.dashboard.model.VivaClassStDto;
import com.visang.aidt.lms.api.dashboard.model.VivaClassTcDto;
import com.visang.aidt.lms.api.dashboard.service.EtcService;
import com.visang.aidt.lms.api.library.dao.FileDao;
import com.visang.aidt.lms.api.library.dto.FileDto;
import com.visang.aidt.lms.api.library.dto.FileLogDto;
import com.visang.aidt.lms.api.mq.service.NatsSendService;
import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import com.visang.aidt.lms.api.utility.utils.FileUtil;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.nas.path}")
    private String nasPath;

    private long MAX_FILE_SIZE = 1000 * 1024 * 1024; // 1000mb

    private final FileDao fileDao;

    private final JwtUtil jwtUtil;

    private final NatsSendService natsSendService;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Value("${spring.topic.mngraction-log-send-name}")
    private String mngrActionLogTopicName;

    @Value("${key.salt.main}")
    private String keySaltMain;

    /**
     * 파일 업로드
     *
     * @param files
     * @param uploadPath
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<LinkedHashMap<String, Object>> uploadFile(List<MultipartFile> files, String uploadPath, HttpServletRequest request) {
        return uploadFile(files, uploadPath, null, request);
    }

    /**
     * 개인정보 파일 업로드
     *
     * @param files
     * @param uploadPath
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<LinkedHashMap<String, Object>> uploadFile(List<MultipartFile> files, String uploadPath, String prsInfoYn, HttpServletRequest request) {

        if (StringUtils.startsWith(uploadPath, "/") == false) {
            uploadPath = "/" + uploadPath;
        }

        List<LinkedHashMap<String, Object>> urls = new ArrayList<>();

        String resultMsg = "파일 업로드 성공";
        File tempFile = null; // 임시 파일 저장용
        File movedFile = null; // 이동된 파일 저장용
        String userId = null;

        JsonArray arr = new JsonArray();
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                /*로컬 테스트 시 주석 변경하여 테스트*/
                /*userId = "mathbook253-t";*/
                log.error("Authorization 헤더 누락 또는 잘못된 형식");
                throw new AuthFailedException("Authorization 헤더 누락 또는 잘못된 형식");
            }
            else {
                String jwtToken = authorizationHeader.substring(7); // "Bearer " 제거
                Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
                userId = claims.get("id", String.class);
                if (userId == null) {
                    log.error("JWT에서 id 값이 누락되었습니다.");
                    throw new AuthFailedException("JWT에서 id 값이 누락되었습니다.");
                }
            }

            String requestSource = request.getHeader("Referer"); // 요청 출처를 헤더에서 추출
            if (requestSource == null) {
                requestSource = "";  // 기본 값 설정
            }

            for (MultipartFile file : files) {
                validateFile(file);
                log.warn("[0] check - keySaltMain: {}", keySaltMain != null ? "[SET]" : "[NULL]");
                // 업로드 경로 지정
                log.warn("[1] Starting file upload process");
                uploadPath = FileUtil.normalizeUploadPath(uploadPath);
                log.warn("[2] Upload path normalized: {}", uploadPath);

                String tempPath = nasPath + "/temp/";  // 임시저장
                log.warn("[3] Temp path set: {}", tempPath);

                // 파일 경로 생성
                FileUtil.mkdirs(tempPath);
                log.warn("[4] Temp directory created/verified");

                // 파일명 생성
                String saveFileName = FileUtil.getSaveFileName(file.getOriginalFilename());
                log.warn("[5] Save filename generated: {}", saveFileName);

                // 파일 저장
                tempFile = new File(tempPath + saveFileName);
                file.transferTo(tempFile);
                log.warn("[6] File transferred to temp location");

                // 파일 이동
                String copyPath = uploadPath;
                String copyFile = copyPath + "/" + saveFileName;
                movedFile = FileUtil.moveFile(tempFile, copyFile);
                log.warn("[7] File moved to final location: {}", copyFile);

                // DB 저장
                log.warn("[8] Before setFileDto - keySaltMain: {}", keySaltMain != null ? "[SET]" : "[NULL]");
                FileDto fileDto = setFileDto(file, saveFileName, copyPath + "/", userId, requestSource, prsInfoYn);
                log.warn("[9] setFileDto completed successfully");

                fileDao.insertUploadFile(fileDto);
                log.warn("[10] File info inserted to database");

                // 파일 저장 후 tempFile 참조를 null로 설정
                tempFile = null;
                movedFile = null;

                LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
                fileMap.put("url", fileDto.getFilePath() + fileDto.getFileName());
                urls.add(fileMap);

                JsonObject jsonObject = new JsonObject();

                String originalFilename = fileDto.getFileName();
                String filePath = fileDto.getFilePath() + fileDto.getSaveFileName();
                jsonObject.addProperty("originalFilename", originalFilename);
                jsonObject.addProperty("filePath", filePath);
                arr.add(jsonObject);
            }
        } catch (AuthFailedException e) {
            resultMsg = "인증 실패: " + e.getMessage();
            log.error("File upload - Authentication failed: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (IllegalArgumentException e) {
            resultMsg = "파일 업로드 실패: 잘못된 파라미터";
            log.error("File upload - Invalid argument error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (NullPointerException e) {
            resultMsg = "파일 업로드 실패: 필수 데이터 누락";
            log.error("File upload - Null pointer error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (IOException e) {
            resultMsg = "파일 업로드 실패: 파일 입출력 오류";
            log.error("File upload - IO error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (SecurityException e) {
            resultMsg = "파일 업로드 실패: 보안 오류";
            log.error("File upload - Security error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (DataAccessException e) {
            resultMsg = "파일 업로드 실패: 데이터베이스 오류";
            log.error("File upload - Database access error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (SQLException e) {
            resultMsg = "파일 업로드 실패: 데이터베이스 쿼리 오류";
            log.error("File upload - SQL error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (MultipartException e) {
            resultMsg = "파일 업로드 실패: 멀티파트 파일 처리 오류";
            log.error("File upload - Multipart error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (UnsupportedOperationException e) {
            resultMsg = "파일 업로드 실패: 지원하지 않는 작업";
            log.error("File upload - Unsupported operation error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (RuntimeException e) {
            resultMsg = "파일 업로드 실패: 런타임 오류";
            log.error("File upload - Runtime error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        } catch (Exception e) {
            resultMsg = "파일 업로드 실패: 예상치 못한 오류";
            log.error("File upload - Unexpected error: {}", e.getMessage());
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        }

        //사용자 접근 로그 커스텀 메시지
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            attributes.setAttribute(MngrActionType.MNGRACTION_CUSTOM_MSG, arr.toString(), RequestAttributes.SCOPE_REQUEST);
        }
        return urls;
    }

    public ResponseEntity<Object> downloadFile(String url, String jwtToken, HttpServletRequest request, boolean isAuth) throws Exception {
        return downloadFile(url, jwtToken, request, true, "N", "N");
    }

    /**
     * 파일 다운로드
     * @param url
     * @param jwtToken
     * @param request
     * @return
     * @throws Exception
     */
    public ResponseEntity<Object> downloadFile(String url, String jwtToken, HttpServletRequest request, boolean isAuth, String pionadaYn, String partnerActivityYn) throws Exception {
        Map<String, String> response = new HashMap<>();
        String userId = null;
        try {
            if (StringUtils.isEmpty(jwtToken)) {
                log.error("JWT 토큰 값이 누락되었습니다.");
                throw new AuthFailedException("JWT 토큰 값이 누락되었습니다.");
            } else {
                Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
                userId = claims.get("id", String.class);
            }
            if (StringUtils.isEmpty(userId)) {
                throw new AuthFailedException("JWT 토큰 값 오류 - 사용자 정보가 없습니다.");
            }

            String requestSource = request.getHeader("Referer"); // 요청 출처를 헤더에서 추출
            if (requestSource == null) {
                requestSource = "";  // 기본 값 설정
            }

            String fileUrl = StringUtils.substringBeforeLast(url, "/");
            String fileName = StringUtils.substringAfterLast(url, "/");

            FileDto paramFileDto = new FileDto();
            if (isAuth) {
                paramFileDto.setRgtr(userId);
            }
            paramFileDto.setFilePath(fileUrl + "/");
            paramFileDto.setFileName(fileName);
            // DB 조회
            FileDto fileDto = null;

            // 피어나다의 경우 학생 파일을 교사가 생성할 수 있음
            if (StringUtils.equals(pionadaYn, "Y")) {
                fileDto = fileDao.selectFileInfoWithPionada(paramFileDto);

                Map<String, Object> authChkMap = new HashMap<>();
                authChkMap.put("creator", fileDto.getRgtr());
                authChkMap.put("reader", userId);
                // 파일 생성한사람과 조회하는 사람이 동일한 경우(학생-학생 또는 교사-교사)
                String sameCase = StringUtils.equals(userId, fileDto.getRgtr()) ? "Y" : "N";

                if (sameCase.equals("N")) {
                    // 학생이 파일을 생성했고, 그 학급을 담당한 교사들은 볼 수 있어야함
                    List<String> tcList = fileDao.selectTcListFromCreator(authChkMap);
                    // 교사가 파일을 생성했고, 해당하는 학생은 볼 수 있어야함(공통의 학급이 존재해야함)
                    String stdtId = fileDao.selectFileAuthStudent(authChkMap);
                    if (CollectionUtils.isNotEmpty(tcList) || StringUtils.equals(userId, stdtId)) {
                        String authchk = tcList.contains(userId) || StringUtils.equals(userId, stdtId) ? "Y" : "N";
                        fileDto.setDownloadAuthYn(authchk);
                    }

                } else {
                    fileDto.setDownloadAuthYn("Y");
                }

            } else if (StringUtils.equals(partnerActivityYn, "Y")) { //짝꿍 활동일 경우 같은 반 소속일 경우 이미지 보기 가능
                fileDto = fileDao.selectFileInfoWithPartnerActivity(paramFileDto);
            } else {
                fileDto = fileDao.selectFileInfo(paramFileDto);
            }

            if (fileDto == null) {
                response.put("message", "파일 다운로드 실패: 파일 정보가 없습니다.");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
            // 파일 권한 체크
            if (isAuth && ObjectUtils.defaultIfNull(fileDto.getDownloadAuthYn(), "N").equals("N")) {
                response.put("message", "파일 다운로드 실패: 파일 열람 권한이 없습니다.");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            if ("Y".equals(fileDto.getDelYn()) && "Y".equals(fileDto.getPrsInfoYn())) {
                response.put("message", "파일 다운로드 실패: 개인정보 처리방침에 의해 삭제된 파일 입니다.");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }

            // 다운로드 로그
            FileLogDto fileLogDto = setFileLogDto(fileDto, FileUtil.getRemoteIP(request), requestSource);
            fileLogDto.setUserId(userId);
            fileDao.insertDownloadLog(fileLogDto);

            String saveFileName = fileDto.getSaveFileName();
            String originalFileName = fileDto.getFileName();

            String filePath = fileDto.getFilePath() + saveFileName;
            Path safePath = resolveSafePath(filePath); // 안전 경로 변환

            // 파일 정보
            Resource resource = new UrlResource(safePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // 체크섬 비교
                String checksum = fileDto.getChecksum();
                if (StringUtils.isEmpty(checksum)) {
                    throw new IOException("파일 다운로드 실패: checksum 정보가 누락되었습니다.");
                }

                String fileChecksum = null;
                // checksum이 32자일 경우 MD5 / 64자일 경우 SHA256
                /*if (checksum.length() == 32) {
                    fileChecksum = FileUtil.getMD5Checksum(filePath.toString());
                } else {
                    fileChecksum = FileUtil.getHmacSHA256Checksum(filePath.toString());
                }*/
                fileChecksum = FileUtil.getHmacSHA256Checksum(filePath.toString(), keySaltMain);
                // CSAP 대응 (파일 다운로드 불가 시 확인 필요 - 기존 파일 동작 안됨)
                if (StringUtils.equals(checksum, fileChecksum) == false) {
                    throw new IOException("파일 다운로드 실패: 파일이 손상되었습니다.");
                }
                /*if (StringUtils.equals(checksum, fileChecksum) == false) {
                    // 기존 데이터일 경우를 감안하여 한 번 더 체크한다
                    fileChecksum = FileUtil.getSHA256Checksum(filePath.toString());
                    if (StringUtils.equals(checksum, fileChecksum) == false) {
                        throw new IOException("파일 다운로드 실패: 파일이 손상되었습니다.");
                    }
                }*/

                String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replace("+", "%20");
                String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                response.put("message", "파일 다운로드 실패: 파일을 찾을 수 없습니다.");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            }
        } catch (AuthFailedException e) {
            log.error("File download - Authentication failed: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 인증 오류");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (IllegalArgumentException e) {
            log.error("File download - Invalid argument error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 잘못된 파라미터");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (NullPointerException e) {
            log.error("File download - Null pointer error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 필수 데이터 누락");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (IOException e) {
            log.error("File download - IO error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 파일 입출력 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (SecurityException e) {
            log.error("File download - Security error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 보안 오류");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (DataAccessException e) {
            log.error("File download - Database access error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 데이터베이스 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (SQLException e) {
            log.error("File download - SQL error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 데이터베이스 쿼리 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            log.error("File download - Runtime error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 런타임 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            log.error("File download - Unexpected error: {}", e.getMessage());
            response.put("message", "파일 다운로드 실패: 예상치 못한 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    public ResponseEntity<Resource> deleteFile(String url, String jwtToken) throws Exception {
        String userId = null;
        try {
            if (StringUtils.isEmpty(jwtToken)) {
                log.error("JWT 토큰 값이 누락되었습니다.");
                throw new AuthFailedException("JWT 토큰 값이 누락되었습니다.");
            } else {
                Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
                userId = claims.get("id", String.class);
            }
            if (StringUtils.isEmpty(userId)) {
                throw new AuthFailedException("JWT 토큰 값 오류 - 사용자 정보가 없습니다.");
            }



            File file = new File(url);
            boolean result = FileUtil.deleteFileReturn(file);

            if (result) {
                FileDto paramFileDto = new FileDto();

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String delTime = now.format(formatter);

                paramFileDto.setDelYn("Y");
                paramFileDto.setDelDt(delTime);

                log.info(paramFileDto.toString());
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AuthFailedException e) {
            log.error("File delete - Authentication failed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("File delete - Invalid argument error: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("File delete - Null pointer error: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            log.error("File delete - Security error: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("File delete - Database access error: {}", e.getMessage());
            throw e;
        } catch (DateTimeException e) {
            log.error("File delete - DateTime error: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("File delete - Runtime error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("File delete - Unexpected error: {}", e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> deleteFiles() {
        Map<String, Object> resultMap = new HashMap<>();
        List<FileDto> deletedFiles = new ArrayList<>();
        List<FileDto> failedFiles = new ArrayList<>();
        List<FileDto> fileDtoList = fileDao.selectFileInfoList();

        try {
            if (fileDtoList != null && !fileDtoList.isEmpty()) {
                for (FileDto fileDto : fileDtoList) {
                    try {
                        String filePath = fileDto.getFilePath();
                        Path safePath = resolveSafePath(filePath); // 안전 경로 변환
                        boolean result = FileUtil.deleteFileReturn(safePath.toFile());

                        if (result) {
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            String delTime = now.format(formatter);

                            fileDto.setDelYn("Y");
                            fileDto.setDelDt(delTime);
                            deletedFiles.add(fileDto);
                        } else {
                            failedFiles.add(fileDto);
                        }
                    } catch (SecurityException e) {
                        log.error("deleteFiles - Security error for file {}: {}", fileDto.getFileName(), e.getMessage());
                        failedFiles.add(fileDto);
                    } catch (IOException e) {
                        log.error("deleteFiles - IO error for file {}: {}", fileDto.getFileName(), e.getMessage());
                        failedFiles.add(fileDto);
                    } catch (IllegalArgumentException e) {
                        log.error("deleteFiles - Invalid argument error for file {}: {}", fileDto.getFileName(), e.getMessage());
                        failedFiles.add(fileDto);
                    } catch (NullPointerException e) {
                        log.error("deleteFiles - Null pointer error for file {}: {}", fileDto.getFileName(), e.getMessage());
                        failedFiles.add(fileDto);
                    } catch (DateTimeException e) {
                        log.error("deleteFiles - DateTime error for file {}: {}", fileDto.getFileName(), e.getMessage());
                        failedFiles.add(fileDto);
                    } catch (RuntimeException e) {
                        log.error("deleteFiles - Runtime error for file {}: {}", fileDto.getFileName(), e.getMessage());
                        failedFiles.add(fileDto);
                    } catch (Exception e) {
                        log.error("deleteFiles - Unexpected error for file {}: {}", fileDto.getFileName(), e.getMessage());
                        failedFiles.add(fileDto);
                    }
                }
            }
            resultMap.put("failDc", "success=" + deletedFiles.size() + ",fail=" + failedFiles.size());
        } catch (DataAccessException e) {
            log.error("deleteFiles - Database access error: {}", e.getMessage());
            resultMap.put("failDc", "데이터베이스 접근 오류: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("deleteFiles - Invalid argument error: {}", e.getMessage());
            resultMap.put("failDc", "잘못된 파라미터: " + e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("deleteFiles - Null pointer error: {}", e.getMessage());
            resultMap.put("failDc", "필수 데이터 누락: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("deleteFiles - Runtime error: {}", e.getMessage());
            resultMap.put("failDc", "런타임 오류: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("deleteFiles - Unexpected error: {}", e.getMessage());
            resultMap.put("failDc", "예상치 못한 오류: " + e.getMessage());
            throw e;
        } finally {
            if (!deletedFiles.isEmpty()) {
                fileDao.updateFileInfoList(deletedFiles);
            }

            log.info("파일 삭제 성공 cnt : {} / 파일 삭제 실패 cnt : {}", deletedFiles.size(), failedFiles.size());
        }

        resultMap.put("resultOk", true);
        resultMap.put("btchExcnRsltCnt", deletedFiles.size());

        return resultMap;
    }

    /**
     * 파일 객체 세팅
     *
     * @param file
     * @param saveFileName
     * @param filePath
     * @param regId
     * @param requestSource
     * @return
     */
    private FileDto setFileDto(MultipartFile file, String saveFileName, String filePath, String regId, String requestSource, String prsInfoYn) throws Exception {

        log.warn("[8-1] setFileDto started - keySaltMain: {}", keySaltMain != null ? "[SET]" : "[NULL]");

        if (StringUtils.startsWith(nasPath, "/") == false && StringUtils.startsWith(filePath, "/")) {
            filePath = StringUtils.removeStart(filePath, "/");
        }
        if (prsInfoYn == null || "".equals(prsInfoYn)) {
            prsInfoYn = "N";
        }
        String originFileName = file.getOriginalFilename();
        String ext = FileUtil.getFileExtension(originFileName);
        FileDto fileDto = new FileDto();
        fileDto.setFileName(originFileName);
        fileDto.setSaveFileName(saveFileName);
        fileDto.setFilePath(filePath);
        fileDto.setFileExtension(ext);
        fileDto.setFileSize(file.getSize());
        fileDto.setRgtr(regId);
        fileDto.setRequestSource(requestSource);
        //String checksum = FileUtil.getSHA256Checksum(filePath + "/" + saveFileName);

        log.warn("[8-2] About to call getHmacSHA256Checksum - keySaltMain: {}, filePath: {}",
                keySaltMain != null ? "[SET]" : "[NULL]", filePath + "/" + saveFileName);

        String checksum = FileUtil.getHmacSHA256Checksum(filePath + "/" + saveFileName, keySaltMain);

        log.warn("[8-3] getHmacSHA256Checksum completed - checksum: {}", checksum != null ? "[GENERATED]" : "[NULL]");

        fileDto.setChecksum(checksum);
        fileDto.setPrsInfoYn(prsInfoYn);

        // 파일 중복 안되도록 uuid를 붙인다 (파일 경로로 data 조회 필요 - 학생 간 파일 공유 등)
        String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
        originFileName = StringUtils.substringBeforeLast(originFileName, ".");
        originFileName = originFileName + "(" + StringUtils.replace(uuid, "-", "") + ")." + ext;
        fileDto.setFileName(originFileName);

        /* // 파일 중복 체크 (애초에 중복 안되도록 uuid를 붙인다)
        String uuid = fileDao.selectExistsFileUuid(fileDto);
        if (StringUtils.isNotEmpty(uuid)) {
            originFileName = StringUtils.substringBeforeLast(originFileName, ".");
            originFileName = originFileName + "(" + StringUtils.replace(uuid, "-", "") + ")." + ext;
            fileDto.setFileName(originFileName);
        }*/

        return fileDto;
    }

    /**
     * 파일 로그 객체 세팅
     *
     * @param fileDto
     * @return
     */
    private FileLogDto setFileLogDto(FileDto fileDto, String accessIp, String requestSource) {
        FileLogDto fileLogDto = new FileLogDto();
        fileLogDto.setFileIdx(fileDto.getFileIdx());
        fileLogDto.setUserId(fileDto.getRgtr());
        fileLogDto.setAccessIp(accessIp);
        fileLogDto.setRequestSource(requestSource);
        if(fileDto != null) {
            fileLogDto.setFileName(fileDto.getFileName());
        }
        return fileLogDto;
    }

    private FileLogDto setFileLogMapToDto(Map<String, Object> fileMap, String accessIp, String requestSource) {
        FileLogDto fileLogDto = new FileLogDto();
        fileLogDto.setFileIdx(MapUtils.getInteger(fileMap, "fileIdx", 0));
        fileLogDto.setUserId(MapUtils.getString(fileMap, "rgtr", ""));
        fileLogDto.setAccessIp(accessIp);
        fileLogDto.setRequestSource(requestSource);
        if(MapUtils.isNotEmpty(fileMap)) {
            fileLogDto.setFileName(MapUtils.getString(fileMap, "fileName"));
        }
        return fileLogDto;
    }

    /**
     * 파일 유효성 검사
     *
     * @param file
     * @throws FileNotFoundException
     */
    private void validateFile(MultipartFile file) throws FileNotFoundException {
        if (file.isEmpty()) {
            throw new FileNotFoundException("파일이 없습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }
        if (!FileUtil.isAllowedExtension(file)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
        }
    }

    /**
     * 파일 일괄 다운로드
     * @param jwtToken
     * @param request
     * @param isAuth
     * @param param
     * @return
     * @throws Exception
     */
    public ResponseEntity<StreamingResponseBody> dgnssDownloadAll(String jwtToken, HttpServletRequest request, boolean isAuth, Map<String, Object> param) throws Exception {
        Map<String, String> response = new HashMap<>();
        String userId = null;
        if (StringUtils.isEmpty(jwtToken)) {
            log.error("JWT 토큰 값이 누락되었습니다.");
            throw new AuthFailedException("JWT 토큰 값이 누락되었습니다.");
        } else {
            Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
            userId = claims.get("id", String.class);
        }
        if (StringUtils.isEmpty(userId)) {
            throw new AuthFailedException("JWT 토큰 값 오류 - 사용자 정보가 없습니다.");
        }

        String requestSource = request.getHeader("Referer"); // 요청 출처를 헤더에서 추출
        if (requestSource == null) {
            requestSource = "";  // 기본 값 설정
        }

        if (isAuth) {
            param.put("rgtr", userId);
        }

        // 피어나다의 경우 학생 파일을 교사가 생성할 수 있음
        List<Map<String, Object>> fileInfoList = fileDao.selectFileDgnssFileList(param);
        if (CollectionUtils.isEmpty(fileInfoList)) {
            throw new Exception("파일 정보가 없습니다");
        }

        // 비바클래스 API 호출 및 학생 이름정보 조회
        String token = "";
        String vivaClassUrl = "";

        Map<String, Object> dgnssInfo = fileDao.selectTcDgnssInfoWithId(param);
        if (MapUtils.isEmpty(dgnssInfo)) {
            throw new Exception("검사 정보가 없습니다");
        }
        String claId = MapUtils.getString(dgnssInfo, "claId", "");
        ObjectMapper objectMapper = new ObjectMapper();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.equals(serverEnv, "vs-math-develop") || StringUtils.equals(serverEnv, "local")) {
            vivaClassUrl = "https://dev-vivaclassapi.vivasam.com";
        } else if (StringUtils.equals(serverEnv, "vs-math-prod")) {
            vivaClassUrl = "https://vivaclassapi.vivasam.com";
        }
        if (StringUtils.isNotEmpty(vivaClassUrl)) {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> requestParam = new HashMap<>();
            String makeJwtTokenUrl = "/api/auth/login";
            requestParam.put("id", "metapsycho");
            requestParam.put("accessKey", "MDTMPmBACTNcYGsx+Pfk1lPDlBrCveACPzwtz1cPHBmv6KKhAZg+ikyD4/A/TCnKvFeD6GeLS5889Ic7HjwYv4TlpmpgNLUclPSoqkxe0ac=");

            VivaClassApiDto vivaApiResponse = this.vivaClassApiCall(vivaClassUrl + makeJwtTokenUrl, requestParam, requestHeaders);
            if (StringUtils.equals(vivaApiResponse.getCode(), "-1")) {
                throw new Exception("비바클래스 API 호출 실패(토큰 생성)");
            }
            token = "Bearer " + vivaApiResponse.getResponse();
            requestHeaders.set("Authorization", token);
        }

        Map<String, Object> tcUserParam = new HashMap<>();
        tcUserParam.put("teacherId", userId.substring(userId.lastIndexOf("-") + 1));
        tcUserParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));

        // 교사 학급 정보 호출
        VivaClassApiDto vivaTcInfoApiResponse = this.vivaClassApiCall(vivaClassUrl + "/api/metapsycho/class/info", tcUserParam, requestHeaders);
        if (StringUtils.equals(vivaTcInfoApiResponse.getCode(), "-1")) {
            throw new Exception("비바클래스 API 호출 실패(교사 정보)");
        }
        VivaClassTcDto vivaClassTcInfo = objectMapper.convertValue(vivaTcInfoApiResponse.getResponse(), VivaClassTcDto.class);
        Map<String, Object> stUserParam = new HashMap<>();
        stUserParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));

        // 학급 구성원 호출
        Map<String, Map<String, Object>> stVivaClassInfo = new HashMap<>();
        VivaClassApiDto vivaStInfoApiResponse = this.vivaClassApiCall(vivaClassUrl + "/api/metapsycho/student/list", stUserParam, requestHeaders);
        if (StringUtils.equals(vivaStInfoApiResponse.getCode(), "-1")) {
            throw new Exception("비바클래스 API 호출 실패(학급 구성원)");
        }
        // 학급 구성원을 List형태로 변환
        List<VivaClassStDto> vivaClassStList = new ArrayList<>();
        if (vivaStInfoApiResponse.getResponse() instanceof List<?>) {
            vivaClassStList = ((List<?>) vivaStInfoApiResponse.getResponse()).stream()
                    .map(item -> objectMapper.convertValue(item, VivaClassStDto.class))
                    .collect(Collectors.toList());
        } else {
            throw new Exception("비바클래스 API 호출 실패(학급 구성원 변환 실패)");
        }

        // 학급 구성원 이름 가져와서 담기
        Map<String, Object> userIdMap = new HashMap<>();
        for (VivaClassStDto stDto : vivaClassStList) {
            userIdMap.put("vivaclass-s-" + stDto.getMemberId(), stDto.getName() + claId.substring(claId.lastIndexOf("-") + 1));
        }

        for (Map<String, Object> fileMap : fileInfoList) {
            String fileUrl = StringUtils.substringBeforeLast(MapUtils.getString(fileMap, "fileUrl", ""), "/");
            String fileName = StringUtils.substringAfterLast(MapUtils.getString(fileMap, "fileUrl", ""), "/");
            fileMap.put("filePath", fileUrl + "/");
            fileMap.put("fileName", fileName);

            if (isAuth && MapUtils.getString(fileMap, "downloadAuthYn", "N").equals("N")) {
                throw new Exception("파일 열람 권한이 없습니다.");
            }

            if ("Y".equals(MapUtils.getString(fileMap, "delYn", "")) &&
                    "Y".equals(MapUtils.getString(fileMap, "prsInfoYn", ""))) {
                throw new Exception("개인정보 처리방침에 의해 삭제된 파일입니다.");
            }

            // 로그 기록
            FileLogDto fileLogDto = setFileLogMapToDto(fileMap, FileUtil.getRemoteIP(request), requestSource);
            fileLogDto.setUserId(userId);
            fileDao.insertDownloadLog(fileLogDto);

            // 파일 존재 및 체크섬 확인
            String saveFileName = MapUtils.getString(fileMap, "saveFileName", "");
            Path filePath = Paths.get(fileUrl).resolve(saveFileName).normalize();
            File file = filePath.toFile();

            String fileChecksum = null;

            String checksum = MapUtils.getString(fileMap, "checksum", "");
            if (StringUtils.isEmpty(checksum)) {
                throw new Exception("파일 체크섬 정보가 없습니다.");
            }

            // checksum이 32자일 경우 MD5 / 64자일 경우 SHA256
            /*if (checksum.length() == 32) {
                fileChecksum = FileUtil.getMD5Checksum(filePath.toString());
            } else {
                fileChecksum = FileUtil.getSHA256Checksum(filePath.toString());
            }*/
            fileChecksum = FileUtil.getHmacSHA256Checksum(filePath.toString(), keySaltMain);
            // CSAP 대응 (파일 다운로드 불가 시 확인 필요 - 기존 파일 동작 안됨)
            if (StringUtils.equals(checksum, fileChecksum) == false) {
                throw new IOException("파일 다운로드 실패: 파일이 손상되었습니다.");
            }
            /*if (StringUtils.equals(checksum, fileChecksum) == false) {
                // 기존 데이터일 경우를 감안하여 한 번 더 체크한다
                fileChecksum = FileUtil.getSHA256Checksum(filePath.toString());
                if (StringUtils.equals(checksum, fileChecksum) == false) {
                    throw new IOException("파일 다운로드 실패: 파일이 손상되었습니다.");
                }
            }*/
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
            for (Map<String, Object> fileMap : fileInfoList) {
                String stdtId = MapUtils.getString(fileMap, "userId", "");
                String saveFileName = MapUtils.getString(fileMap, "saveFileName", "");
                String fileUrl = MapUtils.getString(fileMap, "filePath", "");
                Path filePath = Paths.get(fileUrl).resolve(saveFileName).normalize();
                File file = filePath.toFile();
                if (!file.exists() || !file.isFile()) continue;

                try (InputStream fis = new FileInputStream(file)) {
                    String zipFileName = MapUtils.getString(userIdMap, stdtId, "파일") + ".pdf";
                    zipOut.putNextEntry(new ZipEntry(zipFileName));
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, len);
                    }
                    zipOut.closeEntry();
                }
            }
            zipOut.finish();
        } catch (Exception e) {
            log.error("ZIP 생성 오류: {}", e.getMessage());
        }
        byte[] zipBytes = baos.toByteArray();

        String dgnssName = StringUtils.equals(MapUtils.getString(dgnssInfo, "paperIdx", ""), "1") ? "종합학습검사" : "자기조절학습검사";
        String ordNo = StringUtils.equals(MapUtils.getString(dgnssInfo, "ordNo", ""), "1") ? "1차" : "2차";
        String zipFileName = "[" + vivaClassTcInfo.getClsName() + "]" + dgnssName + "_" + ordNo + ".zip";
        String encodedFileName = URLEncoder.encode(zipFileName, StandardCharsets.UTF_8).replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.setContentLength(zipBytes.length);

        StreamingResponseBody stream = outputStream -> {
            outputStream.write(zipBytes);
            outputStream.flush();
        };

        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<LinkedHashMap<String, Object>> uploadDgnssForBatch(List<MultipartFile> files, String uploadPath) {

        if (StringUtils.startsWith(uploadPath, "/") == false) {
            uploadPath = "/" + uploadPath;
        }

        List<LinkedHashMap<String, Object>> urls = new ArrayList<>();

        String resultMsg = "파일 업로드 성공";
        File tempFile = null; // 임시 파일 저장용
        File movedFile = null; // 이동된 파일 저장용
        String userId = null;

        JsonArray arr = new JsonArray();
        try {
            for (MultipartFile file : files) {
                validateFile(file);

                // 업로드 경로 지정
                uploadPath = FileUtil.normalizeUploadPath(uploadPath);
                String tempPath = nasPath + "/temp/";  // 임시저장

                // 파일 경로 생성
                FileUtil.mkdirs(tempPath);

                // 파일명 생성
                String saveFileName = FileUtil.getSaveFileName(file.getOriginalFilename());

                // 파일 저장
                tempFile = new File(tempPath + saveFileName);
                file.transferTo(tempFile);

                // 파일 이동
                String copyPath = uploadPath;
                String copyFile = copyPath + "/" + saveFileName;
                movedFile = FileUtil.moveFile(tempFile, copyFile);

                // DB 저장
                FileDto fileDto = setFileDto(file, saveFileName, copyPath + "/", userId, "Batch Dgnss", "Y");
                fileDao.insertUploadFile(fileDto);

                // 파일 저장 후 tempFile 참조를 null로 설정
                tempFile = null;
                movedFile = null;

                LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
                fileMap.put("url", fileDto.getFilePath() + fileDto.getFileName());
                urls.add(fileMap);

                JsonObject jsonObject = new JsonObject();

                String originalFilename = fileDto.getFileName();
                String filePath = fileDto.getFilePath() + fileDto.getSaveFileName();
                jsonObject.addProperty("originalFilename", originalFilename);
                jsonObject.addProperty("filePath", filePath);
                arr.add(jsonObject);
            }
        } catch (Exception e) {

            resultMsg = "파일 업로드 실패: " + e.getMessage();
            log.error(resultMsg);

            // 업로드 실패 시, 생성된 파일이 있다면 삭제
            FileUtil.deleteFile(tempFile);
            FileUtil.deleteFile(movedFile);
        }

        //사용자 접근 로그 커스텀 메시지
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            attributes.setAttribute(MngrActionType.MNGRACTION_CUSTOM_MSG, arr.toString(), RequestAttributes.SCOPE_REQUEST);
        }
        return urls;
    }

    public VivaClassApiDto vivaClassApiCall(String url, Map<String, Object> request, HttpHeaders headers) {

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<VivaClassApiDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                VivaClassApiDto.class
        );

        if (!response.getBody().getCode().equals("00000")) {
            log.error("vivaclass API Fail");
            VivaClassApiDto vivaClassApiDto = new VivaClassApiDto();
            vivaClassApiDto.setCode("-1");
            return vivaClassApiDto;
        }

        return response.getBody();
    }

    /**
     * CSAP 대응
     * nas 경로 검증
     * @param filePath
     * @return
     * @throws IOException
     */
    public Path resolveSafePath(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new SecurityException("Invalid path");
        }

        // 설정 주입된 NAS 루트 (예: /files/nas/engl)
        Path baseDir = Paths.get(nasPath).toAbsolutePath().normalize();
        Path baseReal = baseDir.toRealPath(LinkOption.NOFOLLOW_LINKS);

        Path candidate = Paths.get(filePath).normalize();
        Path resolved;

        if (candidate.isAbsolute()) {
            // 절대경로는 baseDir 하위일 때만 허용
            Path candidateReal = candidate.toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (!candidateReal.startsWith(baseReal)) {
                throw new SecurityException("Absolute path outside of baseDir not allowed: " + filePath);
            }
            resolved = candidateReal;
        } else {
            // 상대경로는 baseDir에 붙여서 해석
            resolved = baseDir.resolve(candidate).normalize().toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (!resolved.startsWith(baseReal)) {
                throw new SecurityException("Path escapes baseDir: " + filePath);
            }
        }

        // 심볼릭 링크 직접 대상 금지(정책에 따라 완화 가능)
        if (Files.isSymbolicLink(resolved)) {
            throw new SecurityException("Symbolic link not allowed: " + filePath);
        }

        return resolved;
    }
}
