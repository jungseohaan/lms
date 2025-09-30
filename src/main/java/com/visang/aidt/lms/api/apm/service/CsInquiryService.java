package com.visang.aidt.lms.api.apm.service;

import com.visang.aidt.lms.api.apm.mapper.CsInquiryMapper;
import com.visang.aidt.lms.api.library.dao.FileDao;
import com.visang.aidt.lms.api.library.dto.FileDto;
import com.visang.aidt.lms.api.library.service.FileService;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.FileUtil;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsInquiryService {

    private final CsInquiryMapper csInquiryMapper;
    private final FileService fileService;
    private final FileDao fileDao;
    private final JwtUtil jwtUtil;

    @Value("${cloud.aws.nas.path}")
    private String nasPath;

    @Value("${key.salt.main}")
    private String keySaltMain;

    @Transactional
    public Map<String, Object> insertInquiry(Map<String, Object> paramMap, List<MultipartFile> files, HttpServletRequest request) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("문의 등록 요청: {}", paramMap);
        Map<String, Object> innerParam = new HashMap<>(paramMap);

        // 필수 팔라미터 체크
        Map<String, Object> check = insertParamCheck(innerParam);

        if (!MapUtils.getBoolean(check, "success", false)) {
            return check;
        }

        Map<String, Object> serverInfoData = extractUserEnvironmentInfo(request);

        innerParam.put("systemBrowser", MapUtils.getString(serverInfoData, "browser", ""));
        innerParam.put("systemOs", MapUtils.getString(serverInfoData, "operatingSystem", ""));
        innerParam.put("systemIp", MapUtils.getString(serverInfoData, "clientIp", ""));

        int result = csInquiryMapper.insertInquiry(innerParam);

        if (result > 0) {
            // 새로 등록된 문의 ID 가져오기 (MyBatis useGeneratedKeys로 자동 설정됨)
            Integer newInquiryId = ((BigInteger) innerParam.get("inquiryId")).intValue();

            log.info("새로 등록된 문의 ID: {}", newInquiryId);

            // 첨부파일 처리 - 새로 등록된 문의 ID 사용
            if (files != null && !files.isEmpty() && newInquiryId > 0) {
                try {
                    List<LinkedHashMap<String, Object>> uploadResults = handleFileUpload(files, request);
                    log.info("첨부파일 업로드 완료: {}", uploadResults.size());

                    // 새로 등록된 문의 ID와 업로드된 파일 정보 연결
                    csInquiryMapper.insertCsInquiryFile(newInquiryId, uploadResults);

                    resultMap.put("uploadedFiles", uploadResults);
                    resultMap.put("newInquiryId", newInquiryId);
                } catch (java.io.IOException e) {
                    // CSAP 보안 취약점 수정 - 구체적인 예외 처리
                    log.error("첨부파일 업로드 중 I/O 오류 발생: {}", CustomLokiLog.errorLog(e));
                    resultMap.put("fileUploadError", "첨부파일 업로드 중 파일 입출력 오류가 발생했습니다: " + e.getMessage());
                } catch (DataAccessException e) {
                    log.error("첨부파일 업로드 중 DB 오류 발생: {}", CustomLokiLog.errorLog(e));
                    resultMap.put("fileUploadError", "첨부파일 업로드 중 데이터베이스 오류가 발생했습니다: " + e.getMessage());
                } catch (RuntimeException e) {
                    log.error("첨부파일 업로드 중 런타임 오류 발생: {}", CustomLokiLog.errorLog(e));
                    resultMap.put("fileUploadError", "첨부파일 업로드 중 처리 오류가 발생했습니다: " + e.getMessage());
                }
            }

            resultMap.put("success", true);
            resultMap.put("resultMsg", "문의가 성공적으로 등록되었습니다.");
            resultMap.put("inquiryId", newInquiryId);
        } else {
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 등록에 실패했습니다.");
        }
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInquiryList(Map<String, Object> paramMap) {
        log.info("문의 목록 조회 요청: {}", paramMap);
        Map<String, Object> resultMap = new HashMap<>();

        // 페이징 계산
        int page = MapUtils.getIntValue(paramMap, "page", 1);
        int size = MapUtils.getIntValue(paramMap, "size", 10);
        int offset = (page - 1) * size;
        
        paramMap.put("offset", offset);
        paramMap.put("limit", size);

        // 검색 조건 정리
        String searchKeyword = MapUtils.getString(paramMap, "searchKeyword");
        if (searchKeyword != null && searchKeyword.trim().isEmpty()) {
            paramMap.put("searchKeyword", null);
        }

        // 데이터 조회
        List<Map<String, Object>> inquiryList = csInquiryMapper.selectInquiryList(paramMap);
        int totalCount = csInquiryMapper.selectInquiryCountWithSearch(paramMap);

        if (CollectionUtils.isNotEmpty(inquiryList) || totalCount == 0) {
            // 페이징 정보 계산
            Map<String, Object> pagination = getPagination(totalCount, size, page);

            resultMap.put("inquiries", inquiryList);
            resultMap.put("pagination", pagination);
            resultMap.put("success", true);
        } else {
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 목록 조회 중 오류가 발생했습니다.");
        }

        return resultMap;
    }

    private static Map<String, Object> getPagination(int totalCount, int size, int page) {
        int totalPages = (int) Math.ceil((double) totalCount / size);
        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page);
        pagination.put("totalPages", totalPages);
        pagination.put("totalCount", totalCount);
        pagination.put("size", size);
        pagination.put("hasNext", hasNext);
        pagination.put("hasPrevious", hasPrevious);
        return pagination;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInquiryDetail(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();

        // inquiryId 필수값 검증
        String inquiryId = MapUtils.getString(paramMap, "inquiryId", "");
        if (StringUtils.isEmpty(inquiryId.trim())) {
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 ID가 필요합니다.");
            return resultMap;
        }
        
        try {
            Map<String, Object> inquiry = csInquiryMapper.selectInquiryDetail(paramMap);
            
            if (MapUtils.isEmpty(inquiry)) {
                resultMap.put("success", false);
                resultMap.put("resultMsg", "해당 상세 문의를 찾을 수 없습니다.");
                return resultMap;
            }

            // 재문의 모드일 경우 제목과 내용 가공
            String reInquiry = MapUtils.getString(paramMap, "reopenedYn", "N");
            if ("Y".equalsIgnoreCase(reInquiry)) {
                processReInquiryData(inquiry);
            }
            
            resultMap.put("success", true);
            resultMap.put("resultMsg", "문의 상세 조회 성공");
            
            return inquiry;
            
        } catch (DataAccessException e) {
            // CSAP 보안 취약점 수정 - 구체적인 예외 처리
            log.error("문의 상세 조회 중 DB 오류 발생: {}", CustomLokiLog.errorLog(e));
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 상세 조회 중 데이터베이스 오류가 발생했습니다.");
            return resultMap;
        } catch (RuntimeException e) {
            log.error("문의 상세 조회 중 런타임 오류 발생: {}", CustomLokiLog.errorLog(e));
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 상세 조회 중 오류가 발생했습니다.");
            return resultMap;
        }
    }

    private Map<String, Object> insertParamCheck(Map<String, Object> paramMap) {
        Map<String, Object> result = new HashMap<>();

        // 필수 필드 검증
        String[] requiredFields = {
                "claId", "textbookId", "userId", "userSeCd", "submitter",
                "phoneNumber", "email", "schoolName", "className",
                "inquiryType", "feedbackMethod", "inquiryTitle", "inquiryContent",
                "privacyAgreementYn"
        };

        for (String field : requiredFields) {
            Object fieldValue = paramMap.get(field);

            if (!paramMap.containsKey(field) || fieldValue == null) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", "필수 필드 누락" + field);
                errorResult.put("errorMsg", "필수 필드가 누락되었습니다: " + field);
                return errorResult;
            }

            // String인 경우만 빈 문자열 체크
            if (fieldValue instanceof String && ((String) fieldValue).trim().isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", "필수 필드 누락" + field);
                errorResult.put("errorMsg", "필수 필드가 비어있습니다: " + field);
                return errorResult;
            }
        }

        // 개인정보 동의 확인
        if (!"Y".equals(paramMap.get("privacyAgreementYn"))) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "개인정보 처리 동의 필요");
            errorResult.put("errorMsg", "개인정보 처리 동의가 필요합니다");
            return errorResult;
        }


        result.put("success", true);
        return result;
    }

    private Map<String, Object> extractUserEnvironmentInfo(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();

        // 브라우저, 운영체제 정보 (User-Agent)
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            resultMap.put("userAgent", userAgent);
            resultMap.put("browser", parseBrowser(userAgent));
            resultMap.put("operatingSystem", parseOperatingSystem(userAgent));
        }

        // IP 주소
        String clientIp = getClientIpAddress(request);
        resultMap.put("clientIp", clientIp);

        // 클라이언트에서 screen.width, screen.height 값을 전송받아 사용
        log.info("추출된 환경 정보 - IP: {}, Browser: {}, OS: {}",
                clientIp, resultMap.get("browser"), resultMap.get("operatingSystem"));

        return resultMap;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For 헤더는 여러 IP가 쉼표로 구분될 수 있음
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";

        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edg/")) return "Microsoft Edge";
        if (userAgent.contains("chrome/") && !userAgent.contains("edg/")) return "Google Chrome";
        if (userAgent.contains("firefox/")) return "Mozilla Firefox";
        if (userAgent.contains("safari/") && !userAgent.contains("chrome/")) return "Safari";
        if (userAgent.contains("opera/") || userAgent.contains("opr/")) return "Opera";
        if (userAgent.contains("msie") || userAgent.contains("trident/")) return "Internet Explorer";

        return "Unknown";
    }

    private String parseOperatingSystem(String userAgent) {
        if (userAgent == null) return "Unknown";

        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows nt 10")) return "Windows 10";
        if (userAgent.contains("windows nt 6.3")) return "Windows 8.1";
        if (userAgent.contains("windows nt 6.2")) return "Windows 8";
        if (userAgent.contains("windows nt 6.1")) return "Windows 7";
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac os x")) return "macOS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone") || userAgent.contains("ipad")) return "iOS";

        return "Unknown";
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCommonCodes(HttpServletRequest request) {
        log.info("공통 코드 조회 요청");
        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> codes = new LinkedHashMap<>();
        Map<String, Object> serverInfo = new LinkedHashMap<>();

        try {
            // 문의 유형 코드 조회
            // 임시로 전체 코드 전송
            List<Map<String, Object>> inquiryTypes = csInquiryMapper.selectInquiryTypeCodes();

            List<Map<String, Object>> firstDepth = new ArrayList<>();
            List<Map<String, Object>> secondDepth = new ArrayList<>();

            for (Map<String, Object> type : inquiryTypes) {
                String codeCd = (String) type.get("codeCd");
                if (!codeCd.contains("_")) {
                    firstDepth.add(type);
                } else {
                    secondDepth.add(type);
                }
            }

            // 과목 코드 조회
            // 코드 양이 너무 많은 관계로 limit 10 추가 함
            // 코드 데이터 기준을 정하거나 코드 선택 방법을 검색과 같이 바꾸거나
            List<Map<String, Object>> feedbackMethods = csInquiryMapper.selectSubjectsCodes();

            codes.put("firstDepth", firstDepth);
            codes.put("secondDepth", secondDepth);
            codes.put("feedbackMethods", feedbackMethods);

            // 서버에서 추출 가능한 정보 추가
            Map<String, Object> serverInfoData = extractUserEnvironmentInfo(request);

            serverInfo.put("systemBrowser", MapUtils.getString(serverInfoData, "browser", ""));
            serverInfo.put("systemOs", MapUtils.getString(serverInfoData, "operatingSystem", ""));
            serverInfo.put("systemIp", MapUtils.getString(serverInfoData, "clientIp", ""));
            
            resultMap.put("success", true);
            resultMap.put("resultMsg", "공통 코드 조회 성공");
            resultMap.put("codes", codes);
            resultMap.put("serverInfo", serverInfo);

        } catch (DataAccessException e) {
            // CSAP 보안 취약점 수정 - 구체적인 예외 처리
            log.error("공통 코드 조회 중 DB 오류 발생: {}", CustomLokiLog.errorLog(e));
            resultMap.put("success", false);
            resultMap.put("resultMsg", "공통 코드 조회 중 데이터베이스 오류가 발생했습니다.");
        } catch (RuntimeException e) {
            log.error("공통 코드 조회 중 런타임 오류 발생: {}", CustomLokiLog.errorLog(e));
            resultMap.put("success", false);
            resultMap.put("resultMsg", "공통 코드 조회 중 오류가 발생했습니다.");
        }

        return resultMap;
    }

    private List<LinkedHashMap<String, Object>> handleFileUpload(List<MultipartFile> files, HttpServletRequest request) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String filePath = nasPath + LocalDate.now().format(formatter);

        // CS 문의 전용 파일 업로드 처리
        return uploadCsInquiryFiles(files, filePath, request);
    }

    private List<LinkedHashMap<String, Object>> uploadCsInquiryFiles(List<MultipartFile> files, String uploadPath, HttpServletRequest request) throws Exception {
        if (!uploadPath.startsWith("/")) {
            uploadPath = "/" + uploadPath;
        }

        List<LinkedHashMap<String, Object>> urls = new ArrayList<>();
        
        // JWT 토큰에서 사용자 ID 추출
        String userId = extractUserIdFromRequest(request);

        // 로컬 환경 여부 판단
        boolean isLocal = isLocalEnvironment();
        String basePath = isLocal ? System.getProperty("user.home") + "/temp/cs-inquiry" : nasPath;
        
        log.info("File upload environment: {} (basePath: {})", isLocal ? "LOCAL" : "SERVER", basePath);

        for (MultipartFile file : files) {
            // 파일 유효성 검사
            if (file.isEmpty()) continue;

            // 업로드 경로 정규화
            uploadPath = normalizeUploadPath(uploadPath);
            String tempPath = basePath + "/temp/";
            String finalUploadPath = basePath + uploadPath;

            // 디렉토리 생성 (확실하게)
            createDirectoryIfNotExists(tempPath);
            createDirectoryIfNotExists(finalUploadPath);

            // 파일명 생성
            String saveFileName = generateSaveFileName(file.getOriginalFilename());

            // 임시 파일 저장
            File tempFile = new File(tempPath + saveFileName);
            log.info("Saving temp file to: {}", tempFile.getAbsolutePath());
            file.transferTo(tempFile);

            // 최종 위치로 파일 이동
            String finalPath = finalUploadPath + "/" + saveFileName;
            File finalFile = new File(finalPath);
            createDirectoryIfNotExists(finalFile.getParent());
            
            java.nio.file.Files.move(tempFile.toPath(), finalFile.toPath());

            // DB에 파일 정보 저장 (aidt_file 테이블)
            String requestSource = request.getHeader("Referer"); // 요청 출처를 헤더에서 추출
            if (requestSource == null) {
                requestSource = "";  // 기본 값 설정
            }
            FileDto fileDto = createFileDto(file, saveFileName, uploadPath + "/", userId, requestSource, "N", finalPath);
            fileDao.insertUploadFile(fileDto);

            LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();
            fileMap.put("url", uploadPath + "/" + saveFileName);
            urls.add(fileMap);

            log.info("File uploaded successfully: {} (DB ID: {})", finalPath, fileDto.getFileIdx());
        }

        return urls;
    }

    private boolean isLocalEnvironment() {
        // nasPath가 로컬 시스템에 존재하지 않으면 로컬 환경으로 판단
        File nasDir = new File(nasPath);
        return !nasDir.exists() || nasPath.contains("/files/nas/") && System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private void createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            log.info("Directory created: {} -> {}", dir.getAbsolutePath(), created);
        }
    }

    private String normalizeUploadPath(String uploadPath) {
        if (uploadPath == null || uploadPath.isEmpty()) {
            return "/cs-inquiry/";
        }
        return uploadPath.replaceAll("//+", "/");
    }

    private String generateSaveFileName(String originalFileName) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }
        return java.util.UUID.randomUUID().toString() + extension;
    }

    private FileDto createFileDto(MultipartFile file, String saveFileName, String filePath, String userId, String requestSource, String prsInfoYn, String finalPath) throws Exception {
        String originFileName = file.getOriginalFilename();
        String ext = FileUtil.getFileExtension(originFileName);
        
        FileDto fileDto = new FileDto();
        fileDto.setFileName(originFileName);
        fileDto.setSaveFileName(saveFileName);
        fileDto.setFilePath(filePath);
        fileDto.setFileExtension(ext);
        fileDto.setFileSize(file.getSize());
        fileDto.setRgtr(userId);
        fileDto.setRequestSource(requestSource);
        fileDto.setPrsInfoYn(prsInfoYn != null ? prsInfoYn : "N");

        // 체크섬 계산 (실제 파일 경로로 계산)
        String fullFilePath = finalPath != null ? finalPath : (filePath + saveFileName);
        String checksum;
        try {
            checksum = FileUtil.getHmacSHA256Checksum(fullFilePath, keySaltMain);
        } catch (java.io.IOException e) {
            // CSAP 보안 취약점 수정 - 구체적인 예외 처리
            log.warn("체크섬 계산 I/O 오류, 기본값 사용: {}", e.getMessage());
            checksum = "cs-inquiry-" + System.currentTimeMillis();
        } catch (java.security.NoSuchAlgorithmException e) {
            log.warn("체크섬 알고리즘 없음, 기본값 사용: {}", e.getMessage());
            checksum = "cs-inquiry-" + System.currentTimeMillis();
        } catch (RuntimeException e) {
            log.warn("체크섬 계산 런타임 오류, 기본값 사용: {}", e.getMessage());
            checksum = "cs-inquiry-" + System.currentTimeMillis();
        }
        fileDto.setChecksum(checksum);

        // 파일명에 UUID 추가 (중복 방지)
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String finalFileName = StringUtils.substringBeforeLast(originFileName, ".") + 
                              "(" + uuid + ")." + ext;
        fileDto.setFileName(finalFileName);

        return fileDto;
    }

    private String extractUserIdFromRequest(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                // 로컬 테스트 시 기본값
                boolean isLocal = isLocalEnvironment();
                if (isLocal) {
                    log.warn("로컬 환경: Authorization 헤더 없음, 기본 사용자 ID 사용");
                    return "mathreal71-s5";
                } else {
                    log.error("Authorization 헤더 누락 또는 잘못된 형식");
                    throw new AuthFailedException("Authorization 헤더 누락 또는 잘못된 형식");
                }
            }
            
            String jwtToken = authorizationHeader.substring(7); // "Bearer " 제거
            Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
            String userId = claims.get("id", String.class);
            
            if (userId == null) {
                log.error("JWT에서 id 값이 누락되었습니다.");
                throw new AuthFailedException("JWT에서 id 값이 누락되었습니다.");
            }
            
            log.info("JWT에서 사용자 ID 추출: {}", userId);
            return userId;
            
        } catch (AuthFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 오류 발생", e);
            boolean isLocal = isLocalEnvironment();
            if (isLocal) {
                log.warn("로컬 환경: JWT 처리 오류, 기본 사용자 ID 사용");
                return "mathreal71-s5";
            } else {
                throw new AuthFailedException("JWT 토큰 처리 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
    }

    /**
     * 재문의 모드일 때 제목과 내용을 가공하는 메소드
     */
    private void processReInquiryData(Map<String, Object> inquiry) {
        String originalTitle = MapUtils.getString(inquiry, "inquiryTitle", "");
        String originalContent = MapUtils.getString(inquiry, "inquiryContent", "");
        
        // 제목에 [재문의] 접두사 추가 (이미 있으면 추가하지 않음)
        if (!originalTitle.startsWith("[재문의]")) {
            inquiry.put("inquiryTitle", "[재문의] " + originalTitle);
        }
        
        // 내용 하단에 재문의 템플릿 추가
        String reInquiryTemplate = "\n\n" + 
                "----------------------------------------------------------------------------------------------------------------\n" +
                "[ 재문의 하실 내용을 입력해 주세요 ]\n\n";
        
        inquiry.put("inquiryContent", originalContent + reInquiryTemplate);
        
        log.info("재문의 데이터 가공 완료 - 원본 제목: {}, 가공된 제목: {}", 
                originalTitle, inquiry.get("inquiryTitle"));
    }

    @Transactional
    public Map<String, Object> updateInquiry(Map<String, Object> paramMap, List<MultipartFile> files, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        
        log.info("문의 수정 요청: {}", paramMap);
        
        String inquiryId = MapUtils.getString(paramMap, "inquiryId", "");
        if (StringUtils.isEmpty(inquiryId.trim())) {
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 ID가 필요합니다.");
            return resultMap;
        }
        
        try {
            String userId = MapUtils.getString(paramMap, "userId", "");
            if (StringUtils.isEmpty(userId.trim())) {
                resultMap.put("success", false);
                resultMap.put("resultMsg", "사용자 ID가 필요합니다.");
                return resultMap;
            }
            
            // 문의 정보 업데이트
            int updateResult = csInquiryMapper.updateInquiry(paramMap);
            
            if (updateResult > 0) {
                // 기존 파일 삭제 (논리적 삭제)
                if (files != null && !files.isEmpty()) {
                    // 기존 파일들을 논리 삭제
                    csInquiryMapper.deleteInquiryFiles(inquiryId);
                    
                    // 새 파일 업로드
                    List<LinkedHashMap<String, Object>> uploadResults = handleFileUpload(files, request);
                    csInquiryMapper.insertCsInquiryFile(Integer.parseInt(inquiryId), uploadResults);
                    
                    resultMap.put("uploadedFiles", uploadResults);
                }
                
                resultMap.put("success", true);
                resultMap.put("resultMsg", "문의가 성공적으로 수정되었습니다.");
                resultMap.put("inquiryId", inquiryId);
            } else {
                resultMap.put("success", false);
                resultMap.put("resultMsg", "문의 수정에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("문의 수정 중 오류 발생", e);
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return resultMap;
    }

    @Transactional
    public Map<String, Object> deleteInquiry(Map<String, Object> paramMap, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        
        String inquiryId = MapUtils.getString(paramMap, "inquiryId", "");
        if (StringUtils.isEmpty(inquiryId.trim())) {
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 ID가 필요합니다.");
            return resultMap;
        }
        
        try {
            String userId = MapUtils.getString(paramMap, "userId", "");
            if (StringUtils.isEmpty(userId.trim())) {
                resultMap.put("success", false);
                resultMap.put("resultMsg", "사용자 ID가 필요합니다.");
                return resultMap;
            }
            
            // 문의 논리적 삭제
            int deleteResult = csInquiryMapper.deleteInquiry(paramMap);
            
            if (deleteResult > 0) {
                // 관련 파일들도 논리적 삭제
                csInquiryMapper.deleteInquiryFiles(inquiryId);
                
                resultMap.put("success", true);
                resultMap.put("resultMsg", "문의가 성공적으로 삭제되었습니다.");
                resultMap.put("inquiryId", inquiryId);
            } else {
                resultMap.put("success", false);
                resultMap.put("resultMsg", "문의 삭제에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("문의 삭제 중 오류 발생", e);
            resultMap.put("success", false);
            resultMap.put("resultMsg", "문의 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return resultMap;
    }
}