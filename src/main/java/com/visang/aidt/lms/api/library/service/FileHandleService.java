package com.visang.aidt.lms.api.library.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import com.visang.aidt.lms.api.library.dto.FileHandleVO;
import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import com.visang.aidt.lms.api.utility.exception.FileExtensionException;
import com.visang.aidt.lms.api.utility.exception.FilesizeExceedException;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.FileUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileHandleService {

    private final Environment environment;

    private final AmazonS3Client amazonS3Client;

    private final EtcMapper etcMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.url}")
    private String s3Url;

    @Value("${cloud.aws.s3.path}")
    private String path;

    /**
     * 파일 업로드 처리
     *
     * @param fileHandleVO
     * @return
     */
    public FileHandleVO uploadFile(FileHandleVO fileHandleVO) throws Exception {

        String originalFileName = fileHandleVO.getOriginalFileName();
        String contentType = fileHandleVO.getContentType();
        String uploadPath = environment.getProperty("file.upload.root-path");
        int totalSize = fileHandleVO.getTotalSize();

        // 최대 단일 파일 허용 사이즈
        long maxFileSize = CommonUtils.parseMaxFileSize(environment.getProperty("file.upload.max-file-size"));
        if (totalSize <= 0 || totalSize > maxFileSize) {
            throw new FilesizeExceedException("파일 사이즈가 허용 범위를 초과하였습니다. (최대 허용 사이즈: " + maxFileSize + "MB)");
        }

        // 정해진 파일 형식 외의 파일 업로드 금지
        if (!CommonUtils.isAllowedFileType(contentType)) {
            throw new FileExtensionException("허용되지 않은 파일 형식입니다. (허용 형식: " + CommonUtils.getAllowedFileTypes() + ")");
        }

        // 임의의 파일명 생성 및 경로 생성
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        StringBuilder directoryBuilder = new StringBuilder();
        directoryBuilder.append(uploadPath).append("/").append(currentDate);

        String transformedFileName = CommonUtils.generateFileName(fileHandleVO.getOriginalFileName());
        Path destinationPath = Path.of(directoryBuilder.toString(), transformedFileName);

        // 경로가 없는 경우 생성
        File directory = new File(directoryBuilder.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일 업로드
        Files.copy(fileHandleVO.getFileInputStream(), destinationPath);

        return FileHandleVO.builder()
                .originalFileName(originalFileName)
                .transformedFileName(transformedFileName)
                .contentType(contentType)
                .fileExt(CommonUtils.getFileExtension(originalFileName))
                .totalSize(totalSize)
                .uploadRealPath(destinationPath.toString())
                .downloadUrlPath(transformedFileName + "?date=" + currentDate)
                .build();
    }

    /**
     * 파일 다운로드 처리
     *
     * @param fileHandleVO
     */
    public InputStreamResource downloadFile(FileHandleVO fileHandleVO) throws Exception {

        String transformedFileName = fileHandleVO.getTransformedFileName();

        // 정해진 파일 형식 외의 파일 다운로드 금지
        if (!CommonUtils.isAllowedFileExts(transformedFileName)) {
            throw new FileExtensionException("허용되지 않은 파일 형식입니다. (허용 형식: " + CommonUtils.getAllowedFileExts() + ")");
        }

        String downloadPath = environment.getProperty("file.upload.root-path");
        Path fileDownloadPath = Path.of(downloadPath, transformedFileName);

        // InputStreamResource 생성
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(fileDownloadPath));
        return resource;
    }

    /**
     * 파일 업로드 처리 (aws s3 라이브러리 연동)
     *
     * @param multipartFile
     * @return
     */
    public LinkedHashMap<String, Object> s3FileUploadSingle(MultipartFile multipartFile, String filePath) throws Exception {
        LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();

        if(multipartFile.isEmpty()) {
            fileMap.put("resultMessage", "등록할 파일이 없습니다.");
            fileMap.put("url", "");
            return fileMap;
        }

        String fileName = multipartFile.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        String bucketPath = bucketName + "/aws-origin" + filePath;

        try(InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketPath, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.Private));
        } catch (IOException e) {
            log.error(CustomLokiLog.errorLog(e));
        }

        String url = s3Url+filePath+"/"+fileName;
        fileMap.put("url", url);

        return fileMap;
    }

    /**
     * 파일 업로드 처리 (aws s3 라이브러리 연동) - list
     *
     * @param multipartFiles
     * @return
     */
    public List<LinkedHashMap<String, Object>> s3FileUpload(List<MultipartFile> multipartFiles) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String filePath = path + LocalDate.now().format(formatter);

        List<LinkedHashMap<String, Object>> urls = new ArrayList<>();
        for(MultipartFile multipartFile : multipartFiles) {
            urls.add(s3FileUploadSingle(multipartFile, filePath));
        }
        return urls;
    }

    public LinkedHashMap<String, Object> s3FileUploadWithUrlSingle(MultipartFile multipartFile, Map<String, Object> paramMap) throws Exception {
        String filePath = path + MapUtils.getString(paramMap, "userDiv", "defaultDiv") + "/" + MapUtils.getString(paramMap, "userId", "defaultId");
        String fileName = MapUtils.getString(paramMap, "userId", "defaultName");
        return s3FileUploadSingleMonit(multipartFile, filePath, fileName);
    }

    /**
     * 파일 업로드 처리 (모니터링 이미지 저장을 위한 업로드)
     *
     * @param multipartFile
     * @return
     */
    public LinkedHashMap<String, Object> s3FileUploadSingleMonit(MultipartFile multipartFile, String filePath, String fileName) throws Exception {
        LinkedHashMap<String, Object> fileMap = new LinkedHashMap<>();

        if(multipartFile.isEmpty()) {
            fileMap.put("resultMessage", "등록할 파일이 없습니다.");
            fileMap.put("url", "");
            return fileMap;
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        String bucketPath = bucketName + "/aws-origin" + filePath;

        try(InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketPath, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.Private));
        } catch (IOException e) {
            log.error(CustomLokiLog.errorLog(e));
        }

        String url = s3Url+filePath+"/"+fileName;
        fileMap.put("url", url);
        if (!StringUtils.equals(fileName, "defaultName")) {
            fileMap.put("userId", fileName);
            etcMapper.updateMonitFile(fileMap);
        }

        return fileMap;
    }

    public Map<String, Object> deleteMonitFile(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", "success");
        // 비동기로 파일 삭제 및 DB에서 모니터링 URL 제거
        this.asyncDeleteMonitFileAndUpdate(paramData);
        return resultMap;
    }

    public void asyncDeleteMonitFileAndUpdate(Map<String, Object> paramData) {
        ExecutorService trackThreadPool = Executors.newFixedThreadPool(2,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("Track-Worker-Monit-" + t.getId());
                        return t;
                    }
                });

        CompletableFuture.runAsync(() -> {
            String userId = MapUtils.getString(paramData, "userId", "");
            String filePath = "aws-origin" + path + "S/" + userId + "/" + userId;
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, filePath));
            etcMapper.updateNullMonitFile(paramData);

        }, trackThreadPool);
    }
}

