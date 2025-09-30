package com.visang.aidt.lms.api.keris.utils;

import lombok.Builder;
import lombok.Value;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Value
@Builder
public class ParamOption {

    String url;
    HttpMethod method;
    @Builder.Default MediaType mediaType = MediaType.APPLICATION_JSON;
    @Builder.Default String contentType = "application/json";
    @Builder.Default JSONObject request = new JSONObject();
    String partnerId;
    String apiVersion;
    Map<String, Object> data;

    public static class ParamOptionBuilder {
        private Map<String, Object> data = new HashMap<>();

        public ParamOptionBuilder putData(String key, Object value) {
            this.data.put(key, value);
            return this;
        }
    }
}