package com.visang.aidt.lms.api.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public class FaqInfoDto {

    private String faqId;
    private String link;
    private String title;
    private String content;
    private String rgtr;
    private String category;
    private String categoryNm;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String regDt;
    private String mdfr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String mdfyDt ;
    private String search;
    private String fileCnt;

}
