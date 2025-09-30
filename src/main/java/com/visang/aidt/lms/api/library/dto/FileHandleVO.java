package com.visang.aidt.lms.api.library.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class FileHandleVO {
    /** 원본 파일명 */
    String originalFileName;
    /** 변환된 파일명 */
    String transformedFileName;
    /** 파일 확장자 */
    String fileExt;
    /** 파일 Content-Type */
    String contentType;
    /** 업로드 실제 경로 */
    String uploadRealPath;
    /** 다운로드 URL 경로 */
    String downloadUrlPath;
    /** 파일 사이즈 */
    int totalSize;
    /** 파일 InputStream */
    InputStream fileInputStream;

}
