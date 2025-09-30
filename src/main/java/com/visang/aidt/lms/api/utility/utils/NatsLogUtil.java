package com.visang.aidt.lms.api.utility.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.mq.service.NatsSendService;
import com.visang.aidt.lms.api.utility.exception.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * NATS 로그 전송을 위한 공통 유틸리티 클래스
 * ApiAuthCheckAspect의 로깅 로직을 공통화하여 다른 AOP/Interceptor에서도 사용할 수 있도록 함
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NatsLogUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final NatsSendService natsSendService;
    private final ObjectMapper objectMapper;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Value("${lms.api.log.encode}")
    private boolean isLogEncode;

    @Value("${spring.topic.loki-log-send-name}")
    private String lokiLogTopicName;

    @Value("${key.salt.prefix}")
    private String keySaltPrefix;

    @Value("${key.salt.suffix:}")
    private String keySaltSuffix;

    /**
     * 성능 모니터링용 로그 데이터를 NATS로 전송
     *
     * @param className     클래스명
     * @param methodName    메서드명
     * @param executionTime 실행시간(밀리초)
     * @param url           요청 URL
     * @param logType       로그 타입 (PERFORMANCE)
     */
    public void sendPerformanceLog(String className, String methodName, long executionTime, String url, String logType) {
        try {
            Map<String, Object> logData = createBaseLogData();
            logData.put("logType", logType);
            logData.put("className", className);
            logData.put("methodName", methodName);
            logData.put("durationMs", executionTime);
            logData.put("url", url);

            sendLogToNats(logData);

        } catch (TimeoutException e) {
            // NATS 타임아웃 - 로컬에 성능 로그 기록
            log.warn("NATS 타임아웃 - 로컬 성능 로깅: {}.{} {}ms [{}]",
                    className, methodName, executionTime, url, e);

        } catch (Exception e) {
            // 기타 예외 - 성능 정보는 반드시 어딘가에 기록
            log.error("NATS 전송 실패 - 로컬 성능 로깅: {}.{} {}ms [{}]",
                    className, methodName, executionTime, url, e);
        }
    }

    /**
     * 느린 SQL 쿼리용 로그 데이터를 NATS로 전송
     *
     * @param sqlId         SQL ID
     * @param sqlType       SQL 타입 (SELECT, INSERT, UPDATE, DELETE)
     * @param executionTime 실행시간(밀리초)
     * @param parameter     SQL 파라미터
     * @param sql           실행된 SQL 쿼리
     */
    public void sendSlowQueryLog(String sqlId, String sqlType, long executionTime, Object parameter, String sql) {
        try {
            Map<String, Object> logData = createBaseLogData();
            logData.put("logType", "SLOW_QUERY");
            logData.put("sqlId", sqlId);
            logData.put("sqlType", sqlType);
            logData.put("durationMs", executionTime);
            logData.put("parameter", parameter != null ? parameter.toString() : "null");
            logData.put("sql", sql);

            sendLogToNats(logData);

        } catch (TimeoutException e) {
            // NATS 타임아웃 - 로컬에 느린 쿼리 로그 기록
            log.warn("NATS 타임아웃 - 로컬 느린 쿼리 로깅: sqlId={}, 실행시간={}ms", sqlId, executionTime, e);

        } catch (Exception e) {
            // 기타 예외 - 느린 쿼리 정보는 반드시 로컬에 기록
            log.error("NATS 로그 전송 실패 - 로컬 느린 쿼리 로깅: sqlId={}, 실행시간={}ms, sql={}",
                    sqlId, executionTime, sql, e);
        }
    }

    /**
     * 예외 발생 시 로그 데이터를 NATS로 전송
     *
     * @param methodSignature 메서드 시그니처
     * @param exception       발생한 예외
     * @param requestInfo     요청 정보
     */
    public void sendErrorLog(String methodSignature, Exception exception, String requestInfo) {
        try {
            Map<String, Object> logData = createBaseLogData();
            logData.put("logType", "ERROR");
            logData.put("methodSignature", methodSignature);
            logData.put("exceptionClass", exception.getClass().getSimpleName());
            logData.put("exceptionMessage", exception.getMessage());
            logData.put("requestInfo", requestInfo);

            sendLogToNats(logData);

        } catch (TimeoutException e) {
            // NATS 타임아웃 - 로컬 로그만 기록
            log.error("NATS 로그 전송 타임아웃 - 로컬 로깅으로 대체: {}", methodSignature, e);

        } catch (Exception e) {
            // 기타 예외 - 로컬 로그 기록 및 메트릭 증가
            log.error("NATS 로그 전송 실패: {}", methodSignature, e);
        }
    }

    /**
     * 기본 로그 데이터 구조 생성
     * ApiAuthCheckAspect의 logLmsApiAccess 메서드를 참고하여 구현
     *
     * @return 기본 로그 데이터 Map
     */
    private Map<String, Object> createBaseLogData() {
        Map<String, Object> logData = new HashMap<>();

        // 현재 시간
        String currentTime = LocalDateTime.now().format(DATE_FORMATTER);
        logData.put("timestamp", currentTime);

        // HTTP 요청 정보
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            logData.put("url", request.getRequestURI());
            logData.put("method", request.getMethod());
        }

        // UUID (사용자 식별자)
        String uuid = extractUuidFromRequest();
        logData.put("uuid", uuid);

        // 애플리케이션 정보
        logData.put("appName", "vlmsapi");
        logData.put("profile", getProfileBasedLogMessage());

        return logData;
    }

    /**
     * 현재 HTTP 요청 객체 가져오기
     *
     * @return HttpServletRequest 객체 또는 null
     */
    private HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            try {
                return ((ServletRequestAttributes) requestAttributes).getRequest();
            } catch (ClassCastException e) {
                log.debug("ServletRequestAttributes로 캐스팅 실패: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 요청에서 UUID 추출
     * JWT가 있으면 JWT에서, 없으면 새로 생성
     *
     * @return UUID 문자열
     */
    private String extractUuidFromRequest() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // JWT가 있는 경우 처리 로직
                // 실제 JWT 파싱 로직이 들어가면 그때 예외 처리 추가
            }
        }

        // JWT에서 추출하지 못했거나 요청이 없는 경우 새 UUID 생성
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * NATS로 로그 데이터 전송
     *
     * @param logData 전송할 로그 데이터
     */
    private void sendLogToNats(Map<String, Object> logData) {
        try {
            // MDC에 hash 값 추가
            String jsonMessage = objectMapper.writeValueAsString(logData);
            String hash = sha256(jsonMessage);
            logData.put("hash", hash);

            // 최종 JSON 메시지 생성
            jsonMessage = objectMapper.writeValueAsString(logData);

            // MDC에 hash 값 추가
            org.slf4j.MDC.put("hash", hash != null ? hash : "");

            // CustomLokiLog를 사용하여 APM 형식의 로그 생성 또는 평문 처리
            String apmLog = "";
            if (isLogEncode) {
                HttpServletRequest request = getCurrentRequest();
                apmLog = CustomLokiLog.logTemplateForApp(
                        jsonMessage,
                        "",
                        logData.get("uuid").toString(),
                        request != null ? request.getRequestURI() : ""
                );
            } else {
                apmLog = jsonMessage;
            }

            // NATS로 전송
            natsSendService.pushNatsLogMQ(lokiLogTopicName, apmLog);
        } catch (JsonProcessingException e) {
            log.error(CustomLokiLog.errorLog(e));
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
        } finally {
            // MDC에서 hash 값 제거
            org.slf4j.MDC.remove("hash");
        }
    }

    /**
     * 문자열 SHA-256 해시값 생성
     * ApiAuthCheckAspect의 sha256 메서드와 동일
     *
     * @param value 해시를 생성할 문자열
     * @return SHA-256 해시값(16진수 문자열)
     */
    private String sha256(String value) {
        // salt 처리
        value = keySaltPrefix + value + keySaltSuffix;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 알고리즘을 찾을 수 없습니다", e);
            throw new RuntimeException("해시 생성 실패: 알고리즘 오류", e);
        } catch (Exception e) {
            log.error("SHA-256 기타오류", e);
            throw new RuntimeException("SHA-256 기타오류", e);
        }
    }

    /**
     * 현재 프로필에 따른 로그 메시지 생성
     * ApiAuthCheckAspect의 getProfileBasedLogMessage 메서드와 동일
     *
     * @return 프로필 기반 로그 메시지
     */
    private String getProfileBasedLogMessage() {
        if (StringUtils.equals(serverEnv, "math-dev")) {
            return "devlop";
        } else if (StringUtils.equals(serverEnv, "math-stg")) {
            return "stg";
        } else if (StringUtils.equals(serverEnv, "stg1")) {
            return "stg1";
        } else if (StringUtils.equals(serverEnv, "math-releas")) {
            return "r-math";
        } else if (StringUtils.equals(serverEnv, "engl-release")) {
            return "r-engl";
        } else if (StringUtils.equals(serverEnv, "math-prod")) {
            return "math";
        } else if (StringUtils.equals(serverEnv, "engl-prod")) {
            return "engl";
        } else if (StringUtils.equals(serverEnv, "vs-dev")) {
            return "vs-devlop";
        } else if (StringUtils.equals(serverEnv, "vs-prod")) {
            return "vs-prod";
        } else if (StringUtils.equals(serverEnv, "math-beta2")) {
            return "b2-math";
        } else if (StringUtils.equals(serverEnv, "engl-beta2")) {
            return "b2-engl";
        } else {
            return "access";
        }
    }
}