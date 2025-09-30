package com.visang.aidt.lms.api.common.mngrAction.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Builder
@Value
public class LokiLogContext {

    private String uuid;
    private String uType;
    private String appName;
    private String profile;
    private String url;
    private JsonNode req;
    private JsonNode resp;
    private String sTime;
    private String eTime;
    private String hash;
    private String uName;
    private String schlNm;

}
