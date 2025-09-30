package com.visang.aidt.lms.api.common.mngrAction.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class MngrLogContext {

    @Builder.Default
    private String kerisYn = "Y";
    private String typeCd;
    private String summary;
    private String userId;
    private String userSeCd;
    private String service;
    private String url;
    @Builder.Default
    private String ip = "";
    @Builder.Default
    private String host = "";
    private JsonNode req;
    private JsonNode resp;
    private String claId;
    private String lectureCode;

}
