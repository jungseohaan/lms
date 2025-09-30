package com.visang.aidt.lms.api.utility.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * AOP 예외 로깅을 위한 JSON 포맷터 유틸리티
 *
 * <p>JSON 구조를 생성합니다:
 * <pre>
 * {
 *   "logType": "error",
 *   "errCd": "ERRTRYC001",
 *   "errMsg": "실제 예외 메시지",
 *   "exception": "예외 클래스명"
 * }
 * </pre>
 * </p>
 *
 * <p>Jackson ObjectMapper를 사용하여 구조화된 로그를 생성하며,
 * JSON 직렬화 실패 시 수동으로 생성하는 fallback 메커니즘을 제공합니다.</p>
 *
 * @author AIDT LMS API Team
 * @since 2025-01-01
 */
@Slf4j
@Component
public class JsonLogFormatter {

    /**
     * Jackson ObjectMapper for JSON serialization
     */
    private ObjectMapper objectMapper;

    /**
     * Exception을 ErrorMessages로 변환하는 매퍼
     */
    private final ErrorCodeMapper errorCodeMapper;

    /**
     * JsonLogFormatter 생성자
     *
     * @param errorCodeMapper Exception 매핑을 위한 ErrorCodeMapper
     */
    public JsonLogFormatter(ErrorCodeMapper errorCodeMapper) {
        this.errorCodeMapper = errorCodeMapper;
    }

    /**
     * 애플리케이션 시작 시 ObjectMapper 초기화
     */
    @PostConstruct
    public void init() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Exception과 커스텀 메시지를 받아 JSON 형태의 에러 로그를 생성
     *
     * <p>ErrorCodeMapper를 통해 Exception을 ErrorMessages로 변환하고,
     * JSON 구조로 포맷팅합니다.</p>
     *
     * @param exception     발생한 예외
     * @param customMessage 사용자 정의 메시지 (null일 경우 exception.getMessage() 사용)
     * @return JSON 형태의 에러 로그 문자열
     */
    public String formatErrorLog(Exception exception, String customMessage) {
        try {
            ErrorMessages errorMessage = errorCodeMapper.getErrorMessage(exception);

            String actualMessage = customMessage != null && !customMessage.trim().isEmpty()
                    ? customMessage
                    : (exception.getMessage() != null ? exception.getMessage() : errorMessage.getMessagesContents());

            ErrorLogEntry logEntry = new ErrorLogEntry(
                    "error",
                    errorMessage.getErrorCode(),
                    actualMessage,
                    exception.getClass().getSimpleName()
            );

            return objectMapper.writeValueAsString(logEntry);

        } catch (JsonProcessingException e) {
            // JSON 직렬화 관련 예외만 처리
            log.warn("JSON 직렬화 실패, fallback 사용: {}", e.getMessage());
            return createFallbackLog(exception, customMessage);

        } catch (NullPointerException e) {
            // ErrorMessages가 null이거나 필드가 null인 경우
            log.warn("에러 메시지 처리 중 NPE, fallback 사용: {}", e.getMessage());
            return createFallbackLog(exception, customMessage);

        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("로그 엔트리 생성 중 예상치 못한 오류, fallback 사용", e);
            return createFallbackLog(exception, customMessage);
        }
    }

    /**
     * Exception만 받아 JSON 형태의 에러 로그를 생성
     *
     * @param exception 발생한 예외
     * @return JSON 형태의 에러 로그 문자열
     */
    public String formatErrorLog(Exception exception) {
        return formatErrorLog(exception, null);
    }

    /**
     * 상세 정보를 포함한 JSON 형태의 에러 로그를 생성
     *
     * @param exception 발생한 예외
     * @param url API 호출 경로
     * @param req request parameter
     * @param resp front-end에 전달하는 result
     * @param errMsg exception 최상단 타이틀 (간소화됨)
     * @param message 로깅 상황에 대한 간단한 설명
     * @return JSON 형태의 에러 로그 문자열
     */
    public String formatErrorLog(Exception exception, String url, Object req, String resp, String errMsg, String message) {
        try {
            String errorCode = null;
            try {
                errorCode = errorCodeMapper.getErrorCode(exception);
            } catch (Exception mapperException) {
                log.debug("에러코드 매핑 실패, 기본값 사용: {}", mapperException.getMessage());
                errorCode = "UNKNOWN_ERROR";
            }

            DetailedErrorLogEntry logEntry = new DetailedErrorLogEntry(
                    "error",
                    url,
                    req != null ? req.toString() : null,
                    resp,
                    exception.getClass().getSimpleName(),
                    errMsg,
                    message,
                    errorCode
            );

            return objectMapper.writeValueAsString(logEntry);

        } catch (JsonProcessingException e) {
            log.warn("JSON 직렬화 실패, fallback 사용: {}", e.getMessage());
            return createDetailedFallbackLog(exception, url, req, resp, errMsg, message);
        }
    }


    /**
     * ObjectMapper 직렬화 실패 시 수동으로 JSON 문자열 생성
     *
     * <p>로깅 실패로 인해 애플리케이션이 중단되는 것을 방지하는 안전장치입니다.</p>
     *
     * @param exception     발생한 예외
     * @param customMessage 사용자 정의 메시지
     * @return 수동 생성된 JSON 문자열
     */
    private String createFallbackLog(Exception exception, String customMessage) {
        ErrorMessages errorMessage = errorCodeMapper.getErrorMessage(exception);
        String actualMessage = customMessage != null && !customMessage.trim().isEmpty()
                ? customMessage
                : (exception.getMessage() != null ? exception.getMessage() : errorMessage.getMessagesContents());

        return String.format("{\"logType\":\"error\",\"errCd\":\"%s\",\"errMsg\":\"%s\",\"exception\":\"%s\"}",
                errorMessage.getErrorCode(),
                escapeJson(actualMessage),
                exception.getClass().getSimpleName());
    }


    /**
     * 상세 정보를 포함한 ObjectMapper 직렬화 실패 시 수동으로 JSON 문자열 생성
     *
     * @param exception 발생한 예외
     * @param url API 호출 경로
     * @param req request parameter
     * @param resp front-end에 전달하는 result
     * @param errMsg exception 최상단 타이틀 (간소화됨)
     * @param message 로깅 상황에 대한 간단한 설명
     * @return 수동 생성된 JSON 문자열
     */
    private String createDetailedFallbackLog(Exception exception, String url, Object req, String resp, String errMsg, String message) {
        String errorCode = errorCodeMapper.getErrorCode(exception);
        
        return String.format("{\"logType\":\"error\",\"url\":\"%s\",\"req\":\"%s\",\"resp\":\"%s\",\"exception\":\"%s\",\"errMsg\":\"%s\",\"message\":\"%s\",\"errCd\":\"%s\"}",
                escapeJson(url),
                escapeJson(req != null ? req.toString() : "null"),
                escapeJson(resp),
                exception.getClass().getSimpleName(),
                escapeJson(errMsg),
                escapeJson(message),
                errorCode);
    }

    /**
     * JSON 문자열에서 특수 문자를 이스케이프 처리
     *
     * <p>JSON 구문 오류를 방지하기 위해 따옴표, 개행문자 등을 이스케이프합니다.</p>
     *
     * @param input 이스케이프할 문자열
     * @return 이스케이프 처리된 문자열
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * JSON 로그 엔트리를 나타내는 데이터 클래스
     *
     * <p>JSON 구조:
     * - logType: 항상 "error"
     * - errCd: 에러 코드 (ERRTRYC001~009)
     * - errMsg: 에러 메시지
     * - exception: 예외 클래스명
     * </p>
     */
    @Getter
    @AllArgsConstructor
    public static class ErrorLogEntry {
        /**
         * 로그 타입 (항상 "error")
         */
        private String logType;

        /**
         * 표준화된 에러 코드
         */
        private String errCd;

        /**
         * 실제 에러 메시지
         */
        private String errMsg;

        /**
         * 예외 클래스명
         */
        private String exception;
    }

    /**
     * 상세 정보를 포함한 JSON 로그 엔트리를 나타내는 데이터 클래스
     *
     * <p>JSON 구조:
     * - logType: 항상 "error"
     * - url: API 호출 경로
     * - req: request parameter
     * - resp: front-end에 전달하는 result
     * - exception: exception 객체
     * - errMsg: exception 최상단 타이틀 (간소화됨)
     * - message: 로깅 상황에 대한 간단한 설명
     * - errCd: 에러 코드
     * </p>
     */
    @Getter
    @AllArgsConstructor
    public static class DetailedErrorLogEntry {
        /**
         * 로그 타입 (항상 "error")
         */
        private String logType;

        /**
         * API 호출 경로
         */
        private String url;

        /**
         * request parameter
         */
        private String req;

        /**
         * front-end에 전달하는 result
         */
        private String resp;

        /**
         * exception 객체 클래스명
         */
        private String exception;

        /**
         * exception 최상단 타이틀 (간소화됨)
         */
        private String errMsg;

        /**
         * 로깅 상황에 대한 간단한 설명
         */
        private String message;

        /**
         * 표준화된 에러 코드
         */
        private String errCd;
    }


}