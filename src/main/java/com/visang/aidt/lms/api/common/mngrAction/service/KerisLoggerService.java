package com.visang.aidt.lms.api.common.mngrAction.service;

import com.visang.aidt.lms.api.common.mngrAction.constant.KerisLogUtils;
import com.visang.aidt.lms.api.common.mngrAction.constant.MngrActionType;
import com.visang.aidt.lms.api.common.mngrAction.dto.LokiLogContext;
import com.visang.aidt.lms.api.common.mngrAction.dto.MngrLogContext;
import com.visang.aidt.lms.api.mq.service.NatsSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KerisLoggerService {

    private final KerisLogUtils kerisLogUtils;

    private final NatsSendService natsSendService;

    @Value("${spring.topic.loki-log-send-name}")
    private String lokiLogTopicName;

    @Value("${spring.topic.mngraction-log-send-name}")
    private String mngrActionLogTopicName;

    public void logSendLoki(LokiLogContext context) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("uuid", context.getUuid());
            logData.put("uType", context.getUType());
            logData.put("uName", context.getUName());
            logData.put("schlNm", context.getSchlNm());
            logData.put("appName", context.getAppName());
            logData.put("profile", context.getProfile());
            logData.put("url", context.getUrl());
            logData.put("req", context.getReq());
            logData.put("resp", context.getResp());
            logData.put("sTime", context.getSTime());
            logData.put("eTime", context.getETime());
            logData.put("hash", context.getHash());
            String jsonMessage = kerisLogUtils.writeValueAsString(logData);
            natsSendService.pushNatsLogMQ(lokiLogTopicName, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("Loki 로그 JSON 직렬화 실패 - Jackson 오류: {}", e.getMessage());
        } catch (TimeoutException e) {
            log.error("Loki 로그 NATS 전송 타임아웃: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Loki 로그 NATS 연결 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Loki 로그 전송 중 예상치 못한 오류: {}", e.getMessage(), e);
        }
    }

    public void logSendMngr(MngrLogContext context) {
        try {
            Map<String, Object> logData = new LinkedHashMap<>();
            logData.put("kerisYn", context.getKerisYn());
            logData.put("typeCd", context.getTypeCd());
            logData.put("summary", context.getSummary());
            logData.put("service", context.getService());
            logData.put("userId", context.getUserId());
            logData.put("userSeCd", context.getUserSeCd());
            logData.put("claId", context.getClaId());
            logData.put("lectureCode", context.getLectureCode());
            logData.put("url", context.getUrl());
            logData.put("ip", context.getIp());
            logData.put("host",context.getHost());
            logData.put("log", context.getReq());
            logData.put("returnData", context.getResp());
            String jsonMessage = kerisLogUtils.writeValueAsString(logData);
            natsSendService.pushNatsMQ(mngrActionLogTopicName, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("Mngr 로그 JSON 직렬화 실패 - Jackson 오류: {}", e.getMessage());
        } catch (TimeoutException e) {
            log.error("Mngr 로그 NATS 전송 타임아웃: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Mngr 로그 NATS 연결 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Mngr 로그 전송 중 예상치 못한 오류: {}", e.getMessage(), e);
        }
    }

    public void setUser(String userId, String userSeCd) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            attrs.setAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
            attrs.setAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_SE_CD, userSeCd, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public void setLectureCode(String lectureCode) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            attrs.setAttribute(MngrActionType.MNGRACTION_CUSTOM_LECTURE_CODE, lectureCode, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public void setClaId(String claId) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            attrs.setAttribute(MngrActionType.MNGRACTION_CUSTOM_CLA_ID, claId, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public String getUserId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            Object val = attrs.getAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_ID, RequestAttributes.SCOPE_REQUEST);
            return val != null ? val.toString() : null;
        }
        return null;
    }

    public String getUserSeCd() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            Object val = attrs.getAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_SE_CD, RequestAttributes.SCOPE_REQUEST);
            return val != null ? val.toString() : null;
        }
        return null;
    }

    public String getLectureCode() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            Object val = attrs.getAttribute(MngrActionType.MNGRACTION_CUSTOM_LECTURE_CODE, RequestAttributes.SCOPE_REQUEST);
            return val != null ? val.toString() : null;
        }
        return null;
    }

    public String getClaId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            Object val = attrs.getAttribute(MngrActionType.MNGRACTION_CUSTOM_CLA_ID, RequestAttributes.SCOPE_REQUEST);
            return val != null ? val.toString() : null;
        }
        return null;
    }

}
