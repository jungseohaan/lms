package com.visang.aidt.lms.api.utility.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Exception 클래스를 ErrorMessages enum으로 매핑하는 서비스
 *
 * <p>AOP 기반 예외 로깅 시스템에서 발생한 예외를 표준화된 에러 코드로 변환합니다.
 * 성능을 위해 ConcurrentHashMap으로 매핑 정보를 캐싱하고,
 * 상속 관계도 고려하여 적절한 ErrorMessages를 반환합니다.</p>
 *
 * <p>ErrorCodeMapper 컴포넌트 구현체입니다.
 * YAML 설정 대신 enum 기반으로 에러 코드 관리를 수행합니다.</p>
 *
 * @author AIDT LMS API Team
 * @since 2025-01-01
 */
@Slf4j
@Component
public class ErrorCodeMapper {

    /**
     * Exception 클래스 → ErrorMessages 직접 매핑 캐시
     */
    private final Map<Class<? extends Exception>, ErrorMessages> exceptionMapping = new ConcurrentHashMap<>();

    /**
     * Exception 클래스명 → ErrorMessages 매핑 캐시 (Simple Name, Full Name 모두 지원)
     */
    private final Map<String, ErrorMessages> classNameMapping = new ConcurrentHashMap<>();

    /**
     * 애플리케이션 시작 시 ErrorMessages enum의 모든 매핑 정보를 캐시에 로드
     *
     * <p>각 ErrorMessages에 정의된 Exception 클래스를 키로 하여 매핑을 생성하고,
     * 클래스명(Simple Name, Full Name) 기반 검색도 지원하도록 초기화합니다.</p>
     */
    @PostConstruct
    public void initializeMapping() {
        for (ErrorMessages errorMessage : ErrorMessages.values()) {
            Class<? extends Exception> exceptionClass = errorMessage.getExceptionClass();
            // 클래스 기반 매핑
            exceptionMapping.put(exceptionClass, errorMessage);
            // 클래스명 기반 매핑 (Simple Name, Full Name 모두 지원)
            classNameMapping.put(exceptionClass.getSimpleName(), errorMessage);
            classNameMapping.put(exceptionClass.getName(), errorMessage);
        }
        log.info("ErrorCodeMapper initialized with {} mappings", exceptionMapping.size());
    }

    /**
     * Exception 객체를 받아 해당하는 ErrorMessages를 반환
     *
     * <p>직접 매핑을 먼저 확인하고, 없으면 상속 관계를 검사하여
     * 가장 적합한 ErrorMessages를 찾습니다. 찾을 수 없으면 기본 에러를 반환합니다.</p>
     *
     * @param exception 변환할 Exception 객체
     * @return 매핑된 ErrorMessages (매핑 없으면 GENERIC_ERROR)
     */
    public ErrorMessages getErrorMessage(Exception exception) {
        if (exception == null) {
            return ErrorMessages.getDefaultError();
        }

        Class<? extends Exception> exceptionClass = exception.getClass();

        // 1. 직접 매핑 확인 (O(1) 성능)
        ErrorMessages errorMessage = exceptionMapping.get(exceptionClass);
        if (errorMessage != null) {
            return errorMessage;
        }

        // 2. 상속 관계 확인 (부모 클래스 매핑 검색)
        for (Map.Entry<Class<? extends Exception>, ErrorMessages> entry : exceptionMapping.entrySet()) {
            if (entry.getKey().isAssignableFrom(exceptionClass)) {
                // 3. 성능 향상을 위해 찾은 매핑을 캐시에 저장
                exceptionMapping.put(exceptionClass, entry.getValue());
                return entry.getValue();
            }
        }

        return ErrorMessages.getDefaultError();
    }

    /**
     * Exception 객체를 받아 해당하는 에러 코드(문자열)를 반환
     *
     * @param exception 변환할 Exception 객체
     * @return 에러 코드 (예: "ERRTRYC001")
     */
    public String getErrorCode(Exception exception) {
        return getErrorMessage(exception).getErrorCode();
    }

}