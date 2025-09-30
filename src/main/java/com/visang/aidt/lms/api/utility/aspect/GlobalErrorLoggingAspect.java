package com.visang.aidt.lms.api.utility.aspect;

import com.visang.aidt.lms.api.utility.utils.ErrorCodeMapper;
import com.visang.aidt.lms.api.utility.utils.JsonLogFormatter;
import com.visang.aidt.lms.api.utility.utils.NatsLogUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 전체 API 메소드 예외 로깅 AOP
 *
 * <p>모든 API 메소드에서 발생하는 예외를 일괄적으로 감지하고 JSON 형태로 로깅합니다.
 * 기존 예외 처리 로직에 영향을 주지 않기 위해 로깅 후 원본 예외를 그대로 재 throw합니다.</p>
 *
 * <p>주요 특징:
 * - 전체 API 패키지 (`com.visang.aidt.lms.api`) 메소드 감시
 * - 환경별 로깅 레벨 (로컬: 상세, 운영: 간소)
 * - JSON 구조화된 로그 출력
 * - 로깅 실패가 애플리케이션에 미치는 영향 최소화
 * - 예외 전파 유지
 * </p>
 *
 * <p>기존 {@code ExceptionLoggingAspect}와의 차이점:
 * - 기존: {@code @Loggable} 어노테이션 기반 선택적 적용
 * - 신규: 전체 API 메소드 자동 적용
 * - 기존: 예외를 응답 객체로 변환
 * - 신규: 로깅 후 예외 재throw
 * </p>
 *
 * @see com.visang.aidt.lms.api.utility.aspect.ExceptionLoggingAspect
 */
@Aspect
@Slf4j
@Component("GlobalErrorLoggingAspect")
public class GlobalErrorLoggingAspect {

    @Value("${spring.profiles.active:local}")
    private String serverEnv;

    private final ErrorCodeMapper errorCodeMapper;
    private final JsonLogFormatter jsonLogFormatter;
    private final NatsLogUtil natsLogUtil;

    /**
     * GlobalErrorLoggingAspect 생성자
     *
     * @param errorCodeMapper  Exception 매핑 서비스
     * @param jsonLogFormatter JSON 로그 포맷터
     * @param natsLogUtil     NATS 로그 유틸
     */
    public GlobalErrorLoggingAspect(ErrorCodeMapper errorCodeMapper, JsonLogFormatter jsonLogFormatter, NatsLogUtil natsLogUtil) {
        this.errorCodeMapper = errorCodeMapper;
        this.jsonLogFormatter = jsonLogFormatter;
        this.natsLogUtil = natsLogUtil;
    }

    /**
     * 모든 API 메소드를 대상으로 하는 포인트컷
     *
     * <p>com.visang.aidt.lms.api 패키지 하위 모든 메소드에 적용</p>
     */
    @Pointcut("execution(* com.visang.aidt.lms.api..*(..))")
    public void allApiMethods() {
    }

    /**
     * Aspect 패키지 메소드 제외를 위한 포인트컷
     *
     * <p>무한 재귀 방지를 위해 Aspect 클래스들은 제외</p>
     */
    @Pointcut("execution(* com.visang.aidt.lms.api.utility.aspect..*(..))")
    public void aspectMethods() {
    }

    /**
     * Utility 패키지 메소드 제외를 위한 포인트컷
     *
     * <p>로깅 유틸리티들의 무한 재귀 방지</p>
     */
    @Pointcut("execution(* com.visang.aidt.lms.api.utility.utils..*(..))")
    public void utilityMethods() {
    }

    /**
     * MQ 서비스 패키지 메소드 제외를 위한 포인트컷
     *
     * <p>NATS 서비스의 무한 재귀 방지</p>
     */
    @Pointcut("execution(* com.visang.aidt.lms.api.mq.service..*(..))")
    public void mqServiceMethods() {
    }

    /**
     * Configuration 패키지 메소드 제외를 위한 포인트컷
     *
     * <p>Spring 설정 클래스들은 AOP 대상에서 제외</p>
     */
    @Pointcut("execution(* com.visang.aidt.lms.api.configuration..*(..))")
    public void configMethods() {
    }

    /**
     * 실제 예외 로깅을 수행하는 Around 어드바이스
     *
     * <p>모든 API 메소드에 적용되지만 aspect, utils, configuration 패키지는 제외합니다.
     * 예외 발생 시 로깅을 수행한 후 원본 예외를 그대로 재 throw하여
     * 기존 예외 처리 로직에 영향을 주지 않습니다.</p>
     *
     * @param joinPoint AOP 조인포인트 (메소드 실행 지점)
     * @return 원본 메소드의 반환값
     * @throws Throwable 원본 메소드에서 발생한 예외 (로깅 후 재throw)
     */
    @Around("allApiMethods() && !aspectMethods() && !utilityMethods() && !configMethods() && !mqServiceMethods()")
    public Object logExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 원본 메소드 실행
            return joinPoint.proceed();
        } catch (Exception exception) {

            // 예외 로깅 수행
            handleExceptionLogging(joinPoint, exception);
            // ApiAuthCheckAspect가 처리할 수 있도록 ResponseDTO 형태로 변환
            if (isControllerMethod(joinPoint)) {
                return ResponseDTO.of()
                        .header(null)
                        .fail()
                        .resultCode(HttpStatus.BAD_REQUEST)
                        .resultData(Map.of("exception", exception))
                        .build();
            } else {
                // 컨트롤러가 아닌 경우 예외를 그대로 throw
                throw exception;
            }
        }
    }

    /**
     * 예외 로깅 처리를 담당하는 메소드
     *
     * <p>일반 로그와 JSON 로그를 모두 출력하며, 로깅 실패 시에도
     * 애플리케이션에 영향을 주지 않도록 예외 처리를 수행합니다.</p>
     *
     * @param joinPoint AOP 조인포인트
     * @param exception 발생한 예외
     */
    private void handleExceptionLogging(ProceedingJoinPoint joinPoint, Exception exception) {
        try {
            // 상세 예외 정보 수집
            String url = getRequestUrl();
            Object req = getRequestParameters(joinPoint);
            String resp = "Exception occurred - no response generated";
            String errMsg = exception.getMessage() != null ? exception.getMessage() : exception.getClass().getSimpleName();
            // errMsg 크기 제한 (NATS MQ 크기 문제 해결)
            if (errMsg.length() > 300) {
                errMsg = errMsg.substring(0, 297) + "...";
            }
            String message = "Exception occurred in API method: " + joinPoint.getSignature().getName();
            
            // 상세 로깅
            logExceptionDetails(joinPoint, exception, url, req, resp, errMsg, message);
            
            // JSON 포맷 로깅
            logJsonFormat(exception, url, req, resp, errMsg, message);
            
            // NATS 로그 전송 (예외 발생 시에도 애플리케이션에 영향 주지 않음)
            try {
                String methodSignature = joinPoint.getSignature().toString();
                String requestInfo = getRequestInfo();
                natsLogUtil.sendErrorLog(methodSignature, exception, requestInfo);
            } catch (Exception natsException) {
                // NATS 로그 전송 실패는 조용히 처리
                log.debug("NATS error log transmission failed: {}", natsException.getMessage());
            }
        } catch (Exception loggingException) {
            // 로깅 실패가 애플리케이션에 영향을 주지 않도록 예외 처리
            log.error("Error occurred during exception logging", loggingException);
        }
    }

    /**
     * 상세한 예외 정보를 로깅하는 메소드
     *
     * <p>환경별로 다른 로깅 레벨을 적용:
     * - 로컬 환경: 스택트레이스 포함 상세 로깅
     * - 운영 환경: 보안을 고려한 간소 로깅
     * </p>
     *
     * @param joinPoint AOP 조인포인트 (메소드 시그니처 추출용)
     * @param exception 발생한 예외
     * @param url API 호출 경로
     * @param req request parameter
     * @param resp front-end에 전달하는 result
     * @param errMsg exception 최상단 타이틀 (간소화됨)
     * @param message 로깅 상황에 대한 간단한 설명
     */
    private void logExceptionDetails(ProceedingJoinPoint joinPoint, Exception exception, String url, Object req, String resp, String errMsg, String message) {
        String methodSignature = joinPoint.getSignature().toString();
        String errCd = errorCodeMapper.getErrorCode(exception);

        if ("local".equals(serverEnv)) {
            // 로컬 환경: 스택트레이스 포함 상세 로깅
            log.warn("Exception Details:\n" +
                    "LogType: error\n" +
                    "URL: {}\n" +
                    "Request: {}\n" +
                    "Response: {}\n" +
                    "Exception: {}\n" +
                    "ErrorMsg: {}\n" +
                    "Message: {}\n" +
                    "ErrCd: {}\n" +
                    "Method: {}",
                    url, req, resp, exception.getClass().getName(), errMsg, message, errCd, methodSignature, exception);
        } else {
            // 운영 환경: 보안을 고려한 간소 로깅
            log.warn("Exception Details:\n" +
                    "LogType: error\n" +
                    "URL: {}\n" +
                    "ErrorMsg: {}\n" +
                    "Message: {}\n" +
                    "ErrCd: {}\n" +
                    "Method: {}",
                    url, errMsg, message, errCd, methodSignature);
        }
    }

    /**
     * JSON 형태로 에러 로그 출력
     *
     * <p>JsonLogFormatter를 사용하여 구조화된 JSON 로그를 생성하고,
     * 실패 시 fallback 메커니즘으로 수동 JSON 생성을 수행합니다.</p>
     *
     * @param exception 발생한 예외
     * @param url API 호출 경로
     * @param req request parameter
     * @param resp front-end에 전달하는 result
     * @param errMsg exception 최상단 타이틀 (간소화됨)
     * @param message 로깅 상황에 대한 간단한 설명
     */
    private void logJsonFormat(Exception exception, String url, Object req, String resp, String errMsg, String message) {
        try {
            String jsonLog = jsonLogFormatter.formatErrorLog(exception, url, req, resp, errMsg, message);
            log.error("JSON_ERROR_LOG: {}", jsonLog);
        } catch (Exception jsonException) {
            // JSON 로깅 실패 시 fallback 메커니즘
            String errorCode = errorCodeMapper.getErrorCode(exception);
            log.error("JSON_ERROR_LOG_FALLBACK: {{\"logType\":\"error\",\"url\":\"{}\",\"req\":\"{}\",\"resp\":\"{}\",\"exception\":\"{}\",\"errMsg\":\"{}\",\"message\":\"{}\",\"errCd\":\"{}\"}}",
                    escapeForLog(url),
                    escapeForLog(req != null ? req.toString() : "null"),
                    escapeForLog(resp),
                    exception.getClass().getSimpleName(),
                    escapeForLog(errMsg),
                    escapeForLog(message),
                    errorCode);
        }
    }


    /**
     * HTTP 요청 URL을 반환
     *
     * <p>RequestContextHolder를 사용하여 현재 요청의 URL을 추출합니다.</p>
     *
     * @return HTTP 요청 URL 문자열
     */
    private String getRequestUrl() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURL().toString();
            }
        } catch (Exception e) {
            // Request 정보 획득 실패 시 무시
        }
        return "No request context";
    }

    /**
     * 요청 파라미터를 수집하는 메소드
     *
     * <p>HTTP 요청 파라미터와 메소드 아규먼트를 조합하여 반환합니다.</p>
     *
     * @param joinPoint AOP 조인포인트
     * @return 요청 파라미터 객체
     */
    private Object getRequestParameters(ProceedingJoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Map<String, Object> paramData = new HashMap<>();
                
                // HTTP 파라미터 추가
                request.getParameterMap().forEach((key, values) -> 
                    paramData.put(key, values.length > 0 ? values[0] : null));
                
                // 메소드 아규먼트는 개수와 타입만 포함 (크기 축소)
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    paramData.put("argsCount", args.length);
                    paramData.put("argsTypes", java.util.Arrays.stream(args)
                        .map(arg -> arg != null ? arg.getClass().getSimpleName() : "null")
                        .toArray());
                }
                
                return paramData;
            }
        } catch (Exception e) {
            // 파라미터 수집 실패 시 무시
        }
        return "No parameters available";
    }

    /**
     * HTTP 요청 정보를 문자열로 반환
     *
     * <p>RequestContextHolder를 사용하여 현재 요청의 HTTP 메소드와 URL을 추출합니다.
     * 웹 컨텍스트가 아닌 경우 "No request context"를 반환합니다.</p>
     *
     * @return HTTP 요청 정보 문자열
     */
    private String getRequestInfo() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return String.format("%s %s", request.getMethod(), request.getRequestURL());
            }
        } catch (Exception e) {
            // Request 정보 획득 실패 시 무시 (로깅 실패가 애플리케이션에 영향 없도록)
        }
        return "No request context";
    }

    /**
     * 로그 출력용 문자열 이스케이프 처리
     *
     * <p>로그 포맷 오류를 방지하기 위해 따옴표와 개행문자를 처리합니다.
     * JSON 로그와 달리 간단한 이스케이프만 수행합니다.</p>
     *
     * @param input 이스케이프할 문자열
     * @return 이스케이프 처리된 문자열
     */
    private String escapeForLog(String input) {
        if (input == null) return "null";
        return input.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    /**
     * 컨트롤러 메소드인지 확인
     */
    private boolean isControllerMethod(ProceedingJoinPoint joinPoint) {
        String signature = joinPoint.getSignature().toString();
        return signature.contains(".controller.");
    }

}