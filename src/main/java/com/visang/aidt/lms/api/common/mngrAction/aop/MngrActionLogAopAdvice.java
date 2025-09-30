package com.visang.aidt.lms.api.common.mngrAction.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.visang.aidt.lms.api.common.mngrAction.constant.MngrActionType;
import com.visang.aidt.lms.api.common.mngrAction.dto.MngrActionDto;
import com.visang.aidt.lms.api.mq.service.NatsSendService;
import com.visang.aidt.lms.api.utility.exception.JwtExpiredException;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MngrActionLogAopAdvice {

    private final NatsSendService natsSendService;
    private final JwtUtil jwtUtil;

    @Value("${spring.topic.mngraction-log-send-name}")
    private String mngrActionLogTopicName;

    String noCollectKey = "jwtToken";

    String maskingKey = "schlNm,flnm,classroomName,name,birthday,gender,lastName,firstName,age,stntNm,gradeNm,genderNm,wrterNm,stntNm,userName,user_name,school_name,user_grade," +
            "grade,user_class,classroom_name,rprsGdsAnct,tagNm";

    @Value("${service.name}")
    private String serviceName;

    // NATS 메시지 최대 크기 (1MB - 여유분 고려하여 900KB로 설정)
    private static final int MAX_MESSAGE_SIZE = 900 * 1024;

    @AfterReturning(pointcut = "within(com.visang.aidt.lms.api..controller..*)" , returning = "returnData")
    public void afterReturningService(JoinPoint joinPoint, Object returnData) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            String userId = "";
            String userSeCd = "";

            // 메서드 또는 클래스에 @KerisActionLog가 있으면 로그 제외
            if (method.isAnnotationPresent(KerisActionLog.class)) {
                return;
            }

            //header에 jwtToken 있을경우.
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwtToken = authorizationHeader.substring(7); // "Bearer " 제거
                Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
                userId = claims.get("id", String.class);
                userSeCd = jwtUtil.getUserSeCdFromToken(jwtToken);
            }
            //parameter에 jwtToken 있을경우.
            if (StringUtils.isEmpty(userId) && StringUtils.isNotEmpty(request.getParameter("jwtToken"))) {
                String jwtToken = request.getParameter("jwtToken");
                Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
                userId = claims.get("id", String.class);
                userSeCd = jwtUtil.getUserSeCdFromToken(jwtToken);
            }
            //Custom 메시지에 userId가 있을경우
            if (attributes != null) {
                Object customUserId = attributes.getAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_ID, RequestAttributes.SCOPE_REQUEST);
                if (customUserId != null) {
                    userId = (String) customUserId;
                }

                Object customUserSeCd = attributes.getAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_SE_CD, RequestAttributes.SCOPE_REQUEST);
                if (customUserSeCd != null) {
                    userSeCd = (String) customUserSeCd;
                }
            }
            //log.info("userId : {}, {}", userId, userSeCd);
            //아이디가 없을때 return
            if (StringUtils.isEmpty(userId)) {
                return;
            }

            String params = "";
            if (!getParams(request).entrySet().isEmpty()) {
                params = getParams(request).toString();
            }

                if (attributes != null) {
                    String actionLog = (String) attributes.getAttribute(MngrActionType.MNGRACTION_CUSTOM_MSG, RequestAttributes.SCOPE_REQUEST);
                if (actionLog != null) {
                    params = actionLog;
                }
            }

            //String[] values = anno.value();
            String controllerName = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            String summary = "";
            Operation operation = method.getAnnotation(Operation.class);
            if (operation != null) {
                summary = operation.summary();
            }
            String typeCd = controllerName + "|" + methodName + "|" +summary;
            MngrActionDto mngrActionDto = new MngrActionDto();
            mngrActionDto.setTypeCd(typeCd);
            mngrActionDto.setSummary(summary);
            mngrActionDto.setService(serviceName);
            mngrActionDto.setUserId(userId);
            mngrActionDto.setUserSeCd(userSeCd);
            mngrActionDto.setUrl(String.valueOf(request.getRequestURL()));
            mngrActionDto.setIp(this.getUserIp(request));
            mngrActionDto.setHost(request.getRemoteHost());
            mngrActionDto.setLog(params);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            try {
                String jsonString = objectMapper.writeValueAsString(returnData);
                jsonString = processReturnData(jsonString);
                mngrActionDto.setReturnData(jsonString);
            } catch (Exception e) {
                mngrActionDto.setReturnData(returnData.toString());
            }

            //Nats 전송
            Gson gson = new Gson();
            String json = gson.toJson(mngrActionDto);

            // 메시지 크기 검사 및 축약
            if (json.getBytes(StandardCharsets.UTF_8).length > MAX_MESSAGE_SIZE) {
                log.warn("Message size ({} bytes) exceeds limit ({} bytes), truncating data", 
                        json.getBytes(StandardCharsets.UTF_8).length, MAX_MESSAGE_SIZE);
                mngrActionDto = truncateMessageData(mngrActionDto);
                json = gson.toJson(mngrActionDto);
            }

            natsSendService.pushNatsMQ(mngrActionLogTopicName, json);
        } catch (IllegalStateException e) {
            log.error("Failed to retrieve the current request: {}", e.getMessage());
        } catch (NullPointerException e) {
            log.error("NullPointerException encountered: {}", e.getMessage());
        } catch (JwtExpiredException e) {
//            log.error("JWT Token Expired: {}", e.getMessage());
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("payload size exceed")) {
                log.error("NATS message payload size exceeded: {}", e.getMessage());
                log.info("Consider reducing the size of returnData or increasing NATS server max_payload configuration");
            } else {
                log.error("Unexpected error: {}", e.getMessage());
            }
        }
    }

    public String processReturnData(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        maskNode(rootNode);

        return objectMapper.writeValueAsString(rootNode);
    }

    private void maskNode(JsonNode node) {
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

    private JsonObject getParams(HttpServletRequest request) {
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
                jsonObject.addProperty(replaceParam, value);
            }
        }
        return jsonObject;
    }

    private String getUserIp(HttpServletRequest request) {
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

    private String paramMapToString(HttpServletRequest request) {
        String params = "";
        Enumeration<String> names = request.getParameterNames();

        String privateData = "";
        while(names.hasMoreElements()) {
            String key = (String) names.nextElement();
            log.info("key: "+ key);
            if (noCollectKey.contains(key.toString())) {
                continue;
            }

            if (maskingKey.contains(key.toString())) {
                if (!StringUtils.isEmpty(request.getParameter(key))) {
                    String masking = "";
                    for (int i=0; i<request.getParameter(key).length(); i++) {
                        masking+="*";
                    }
                    privateData = key + ":" + masking;
                }
            }

            if (!StringUtils.isEmpty(request.getParameter(key))) {
                if (params.length() != 0) {
                    params += ", ";
                }
                if (privateData.contains(key.toString())) {
                    params += privateData;
                } else {
                    params += key + ":" + request.getParameter(key) ;
                }

            }
        }
        return params;
    }

    public static List<?> convertObjectToList(Object obj) {
        List<Object> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        } else {
            list= new ArrayList<>();
            list.add(obj);
        }
        return list;
    }

    public static Map<String, Object> convertObjectToMap(Object obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for(int i=0; i <fields.length; i++){
            fields[i].setAccessible(true);
            try{
                map.put((fields[i].getName()), fields[i].get(obj));
            }catch(Exception e){
                log.error(e.getMessage());
            }
        }
        return map;
    }

    /**
     * 메시지 크기가 제한을 초과할 때 데이터를 축약하는 메서드
     */
    private MngrActionDto truncateMessageData(MngrActionDto originalDto) {
        MngrActionDto truncatedDto = new MngrActionDto();
        truncatedDto.setTypeCd(originalDto.getTypeCd());
        truncatedDto.setSummary(originalDto.getSummary());
        truncatedDto.setService(originalDto.getService());
        truncatedDto.setUserId(originalDto.getUserId());
        truncatedDto.setUserSeCd(originalDto.getUserSeCd());
        truncatedDto.setUrl(originalDto.getUrl());
        truncatedDto.setIp(originalDto.getIp());
        truncatedDto.setHost(originalDto.getHost());
        truncatedDto.setLog(originalDto.getLog());

        // returnData를 크기 제한에 맞게 축약
        String returnData = originalDto.getReturnData();
        if (returnData != null) {
            int maxReturnDataSize = MAX_MESSAGE_SIZE / 2; // 전체 메시지의 절반까지만 returnData로 사용
            if (returnData.length() > maxReturnDataSize) {
                String truncatedData = returnData.substring(0, maxReturnDataSize - 100) + "...[TRUNCATED DUE TO SIZE LIMIT]";
                truncatedDto.setReturnData(truncatedData);
            } else {
                truncatedDto.setReturnData(returnData);
            }
        }

        return truncatedDto;
    }

}

