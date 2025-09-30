package com.visang.aidt.lms.api.library.dto;

import lombok.Data;

@Data
public class FileLogDto {
    private int logIdx;
    private int fileIdx;
    private String fileName;
    private String downloadDate;
    private String userId;
    private String accessIp;
    private String requestSource;
}
