package com.visang.aidt.lms.api.common.mngrAction.constant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Enumeration;

@Component
public class KerisLogUtils {

    ObjectMapper objectMapper;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    String noCollectKey = "jwtToken";

    String maskingKey = "schlNm,flnm,classroomName,name,birthday,gender,lastName,firstName,age,stntNm,gradeNm,genderNm,wrterNm,stntNm,userName,user_name,school_name,user_grade," +
            "grade,user_class,classroom_name,rprsGdsAnct,tagNm";

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String writeValueAsString(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    public String getProfileBasedLogMessage() {
        if (StringUtils.contains(serverEnv, "math")) {
            return "math";
        } else if (StringUtils.contains(serverEnv, "engl")) {
            return "engl";
        } else if (StringUtils.contains(serverEnv, "vs")) {
            return "vs";
        } else {
            return "access";
        }
    }

    public String processReturnData(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        maskNode(rootNode);

        return objectMapper.writeValueAsString(rootNode);
    }

    public void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fieldNames().forEachRemaining(fieldName -> {
                JsonNode fieldNode = objectNode.get(fieldName);
                if (maskingKey.contains(fieldName) && fieldNode.isTextual()) {
                    String maskedValue = "*".repeat(fieldNode.asText().length());
                    objectNode.put(fieldName, maskedValue);
                } else {
                    maskNode(fieldNode);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode itemNode = arrayNode.get(i);
                maskNode(itemNode);
            }
        }
    }

    public JsonNode safeParseJson(String json) {
        try {
            return (json != null && !json.trim().isEmpty())
                    ? objectMapper.readTree(json)
                    : objectMapper.createObjectNode();
        } catch (Exception e) {
            return objectMapper.createObjectNode();
        }
    }

    public JsonObject getParams(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            String value = request.getParameter(param);
            if (noCollectKey.contains(replaceParam)) {
                continue;
            }

            if (maskingKey.contains(replaceParam)) {
                if (!StringUtils.isEmpty(value)) {
                    String masking = "";
                    for (int i=0; i<value.length(); i++) {
                        masking+="*";
                    }
                    value = masking;
                }
            }

            if (!StringUtils.isEmpty(value)) {
                String unescapedValue = StringEscapeUtils.unescapeJson(value);

                if (isJson(unescapedValue)) {
                    try {
                        JsonElement parsed = JsonParser.parseString(unescapedValue);
                        jsonObject.add(replaceParam, parsed);
                    } catch (Exception e) {
                        jsonObject.addProperty(replaceParam, unescapedValue);
                    }
                } else {
                    jsonObject.addProperty(replaceParam, unescapedValue);
                }
            }
        }
        return jsonObject;
    }


    public boolean isJson(String value) {
        try {
            JsonParser.parseString(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] salt = newSalt();
            digest.update(salt);
            byte[] hashBytes = digest.digest(value.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            return "";
        }
    }

    public byte[] newSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public String getUserIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
