package com.visang.aidt.lms.api.common.mngrAction.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MngrActionDto {

    private String typeCd;
    private String typeCdNm;
    private String summary;
    private String service;
    private String url;
    private String userId;
    private String ip;
    private String host;
    private String log;
    private String returnData;
    private String regDate;
    private String userSeCd;

}
