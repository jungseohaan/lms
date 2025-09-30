package com.visang.aidt.lms.api.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeInfoDto {

    private String noticeId;
    private String link;
    private String title;
    private String content;
    private String isPinned;
    private String rgtr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String regDt;
    private String mdfr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String mdfyDt;
    private String search;
    private String fileCnt;
    private int rownum;

}
