package com.visang.aidt.lms.api.library.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.common.mngrAction.aop.MngrActionLog;
import com.visang.aidt.lms.api.common.mngrAction.constant.MngrActionType;
import com.visang.aidt.lms.api.library.batch.FilesBatchJob;
import com.visang.aidt.lms.api.library.service.FileHandleService;
import com.visang.aidt.lms.api.library.service.FileService;
import com.visang.aidt.lms.api.utility.utils.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "File API", description = "File API")
@RequestMapping("/files/")
public class FileHandleController {

    private final FileHandleService fileHandleService;
    private final FileService fileService;

    @Value("${cloud.aws.nas.path}")
    private String nasPath;

    @Value("${global.config.map.SECURITY_USER_PASSWORD:}") // ← 필요하면 기본값도 줄 수 있음
    private String SECURITY_USER_PASSWORD;

    @Value("${global.config.map.LMS_JWT_SECRET:}")
    private String LMS_JWT_SECRET;

    @Value("${global.config.map.HMAC_SECRET_KEY:}")
    private String HMAC_SECRET_KEY;

    @Value("${key.salt.prefix}")
    private String keySaltPrefix;

    @Value("${key.salt.suffix:}")
    private String keySaltSuffix;

    @Value("${key.salt.main}")
    private String keySaltMain;

    @Loggable
    @GetMapping(value = "/file-info")
    @Operation(summary = "파일 정보 조회", description = "")
    @Parameter(name = "fileDir", description = "절대경로", required = true, schema = @Schema(type = "string"))
    @Parameter(name = "fileNames", description = "파일명 묶음", required = true, schema = @Schema(type = "string"))
    @Parameter(name = "userId", description = "저장 user id", required = true, schema = @Schema(type = "string", example = "engbook256-t"))
    public Map<String, Object> tchEvalList(
            @RequestParam(name = "fileDir", defaultValue = "") String fileDir,
            @RequestParam(name = "fileNames", defaultValue = "") String fileNames,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            HttpServletRequest request
    ) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtils.equals(fileDir, "Qltkdrydbr")) {
            resultMap.put("SECURITY_USER_PASSWORD", SECURITY_USER_PASSWORD);
            resultMap.put("LMS_JWT_SECRET", LMS_JWT_SECRET);
            resultMap.put("HMAC_SECRET_KEY", HMAC_SECRET_KEY);
            resultMap.put("keySaltPrefix", keySaltPrefix);
            resultMap.put("keySaltSuffix", keySaltSuffix);
            resultMap.put("keySaltMain", keySaltMain);
            return resultMap;
        }

        StringBuffer resultSb = new StringBuffer();

        String requestSource = request.getHeader("Referer"); // 요청 출처를 헤더에서 추출
        List<Map<String, Object>> resultList = new ArrayList();
        for (String fileName : fileNames.split(",")) {
            String filePath = fileDir + (StringUtils.endsWith(fileDir,"/") ? "" : "/") + fileName;
            File file = new File(filePath);

            double fileSize = file.length();
            String fileExt = StringUtils.substringAfterLast(fileName, ".");
            //String checksum = FileUtil.getSHA256Checksum(filePath);
            String checksum = FileUtil.getHmacSHA256Checksum(filePath, keySaltMain);
            String saveFileName = FileUtil.getSaveFileName(filePath);

            Map<String, Object> map = new HashMap<>();
            map.put("filePath", filePath);
            map.put("fileFileName", saveFileName);
            map.put("fileSize", fileSize);
            map.put("checksum", checksum);

            String query = "insert into aidt_file ( file_name,save_file_name,file_path,file_extension,file_size,rgtr,request_source,checksum) values ('";
            query += fileName + "','" + saveFileName + "','" + fileDir + "','" + fileExt +  "'," + fileSize + ",'" + userId + "','" + requestSource + "','" + checksum + "');";
            resultSb.append(query);
            resultList.add(map);
        }

        resultMap.put("ipAddress", FileUtil.getRemoteIP(request));
        resultMap.put("query", resultSb);
        resultMap.put("fileList", resultList);

        return resultMap;
    }

    /**
     * 파일 업로드 처리
     *
     * @param files
     * @return
     */
    @Operation(summary = "Multi File Upload", description = "NCP Object Storage Multi File Upload")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "return http status 200 with url list"),
    })
    @PostMapping(path = "/file-upload", consumes = {"multipart/form-data"}, produces = {"application/json"})
    public List<LinkedHashMap<String, Object>> uploadFile(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "prsInfoYn", required = false) String prsInfoYn,
            HttpServletRequest request
    ) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String filePath = nasPath + LocalDate.now().format(formatter);

        List<LinkedHashMap<String, Object>> urls = new ArrayList<>();
        if (prsInfoYn == null) {
            urls = fileService.uploadFile(files, filePath, request);
        } else {
            urls = fileService.uploadFile(files, filePath, prsInfoYn, request);
        }

        return urls;
    }

    /**
     * 파일 다운로드 처리
     *
     * @param fileName
     * @param date
     * @return
     * @throws Exception
     */
    /*@GetMapping(path = "/file-download/{file-name}", produces = {"application/octet-stream"})
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("file-name") String fileName, @RequestParam("date") String date) throws Exception {

        FileHandleVO fileHandleVO = FileHandleVO.builder()
                .transformedFileName(date + "/" + fileName)
                .build();

        InputStreamResource resource = null;
        try {
            resource = fileHandleService.downloadFile(fileHandleVO);
        } catch(IOException e) {
            log.error(CustomLokiLog.errorLog(e));
            throw new DataNotFoundException("파일을 찾을 수 없습니다.", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }*/

    /**
     * 파일 다운로드 처리
     * @param url
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(path = "/file-download", produces = {"application/octet-stream"})
    public ResponseEntity<Object> downloadFile(
            @RequestParam("url") String url,
            @RequestParam(value = "jwtToken") String jwtToken,
            HttpServletRequest request) throws Exception {

        return fileService.downloadFile(url, jwtToken, request, false);
    }

    /**
     * 개인정보 포함된 파일 다운로드 처리
     * @param url
     * @param jwtToken
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(path = "/pfile-download", produces = {"application/octet-stream"})
    public ResponseEntity<Object> downloadPFile(
            @RequestParam("url") String url,
            @RequestParam(value = "jwtToken") String jwtToken,
            @RequestParam(value = "pionadaYn", required = false) String pionadaYn,
            @RequestParam(value = "partnerActivityYn", required = false) String partnerActivityYn,
            HttpServletRequest request) throws Exception {

        return fileService.downloadFile(url, jwtToken, request, true, pionadaYn, partnerActivityYn);
    }

    @GetMapping(path = "/dgnss-download-all", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> dgnssDownloadAll(
            @RequestParam(value = "jwtToken") String jwtToken,
            @RequestParam(value = "dgnssId") String dgnssId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            HttpServletRequest request) throws Exception {

        return fileService.dgnssDownloadAll(jwtToken, request, true, paramData);
    }

    /**
     * S3 파일 업로드 처리
     *
     * @param multipartFiles
     * @return
     */
    /*
     * aws s3 라이브러리 연동 ncp object storage에 multipart file upload (list)
     */
    @Operation(summary = "Multi File Upload", description = "NCP Object Storage Multi File Upload")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "return http status 200 with url list"),
    })
    @PostMapping(value = "/s3-file-upload", consumes = {"multipart/form-data"}, produces = {"application/json"})
    public List<LinkedHashMap<String, Object>> s3FileUpload(@RequestPart(value = "files")List<MultipartFile> multipartFiles) throws Exception {
        return fileHandleService.s3FileUpload(multipartFiles);
    }


    /*
     * 교사가 학생 개별 화면 확인을 위한 모니터링 파일 업로드
     */
    @Operation(summary = "Single File Upload", description = "NCP Object Storage Multi File Upload")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "return http status 200 with url list"),
    })
    @PostMapping(value = "/s3-file-upload-monit", consumes = {"multipart/form-data"}, produces = {"application/json"})
    public LinkedHashMap<String, Object> s3FileUploadSingle(
            @RequestPart(value = "file") MultipartFile multipartFile,
            @RequestParam(name = "userId", required = false) String userId,
            @RequestParam(name = "userDiv", required = false) String userDiv,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        return fileHandleService.s3FileUploadWithUrlSingle(multipartFile, paramData);
    }

    @Operation(summary = "파일 삭제 API (1개 파일 삭제 테스트용 API)", description = "1개 파일 삭제 테스트용 API")
    @GetMapping(path = "/file-delete", produces = {"application/octet-stream"})
    public ResponseEntity<Resource> deleteFile(
            @RequestParam("url") String url,
            @RequestParam(value = "jwtToken") String jwtToken) throws Exception {
        return fileService.deleteFile(url, jwtToken);
    }

    @Operation(summary = "파일 삭제 API (파일 삭제 배치 확인용 API)", description = "파일 삭제 배치 확인용 API")
    @GetMapping(path = "/pfile-delete", produces = {"application/octet-stream"})
    public void deleteFiles(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "jwtToken") String jwtToken) throws Exception {
        fileService.deleteFiles();
    }

    @Operation(summary = "모니터링 파일 삭제 API", description = "파일 삭제 배치 확인용 API")
    @GetMapping(path = "/monit-file-delete", produces = {"application/json"})
    public Map<String, Object> deleteMonitFile(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "claId") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        return fileHandleService.deleteMonitFile(paramData);
    }
}
