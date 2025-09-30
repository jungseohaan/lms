package com.visang.aidt.lms.api.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 성능 모니터링 AOP
 * - REST Controller 메서드 실행 시간 측정
 * - MyBatis Mapper 메서드 실행 시간 측정
 * - 3초 이상 소요 시 WARN 로그 출력
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAop {

    @Value("${performance.monitoring.api.threshold:3000}")
    private long apiThresholdMs;

    @Value("${performance.monitoring.enabled:true}")
    private boolean monitoringEnabled;

    /**
     * REST Controller 포인트컷 정의
     * - @RestController가 붙은 클래스의 모든 public 메서드
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerMethods() {
    }

    /**
     * REST Controller 성능 모니터링
     * - 실행 시간이 임계값을 초과할 경우 WARN 로그 출력
     * - 정상 실행 시 DEBUG 로그 출력
     */
    @Around("restControllerMethods()")
    public Object monitorApiPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!monitoringEnabled) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String url = getCurrentRequestUrl();
        String parameters = getMethodParameters(joinPoint);

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > apiThresholdMs) {
            log.error("[ERRDLAY001] {}.{}({}) took {}ms\n - URL: {}",
                    className, methodName, parameters, executionTime, url);
        }

        return result;
    }

    /**
     * 현재 HTTP 요청 URL 조회
     */
    private String getCurrentRequestUrl() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String queryString = request.getQueryString();
                String uri = request.getRequestURI();
                return queryString != null ? uri + "?" + queryString : uri;
            }
        } catch (IllegalStateException e) {
            log.debug("Request context not available: {}", e.getMessage());
        } catch (ClassCastException e) {
            log.debug("Request attributes type mismatch: {}", e.getMessage());
        } catch (NullPointerException e) {
            log.debug("Null request attributes: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("Unable to get current request URL: {}", e.getMessage());
        }
        return "N/A";
    }

    /**
     * 메서드 파라미터 문자열 생성
     */
    private String getMethodParameters(ProceedingJoinPoint joinPoint) {
        StringBuilder params = new StringBuilder();
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) params.append(", ");
                if (args[i] != null) {
                    String argValue = args[i].toString();
                    // 파라미터가 너무 길면 잘라내기
                    if (argValue.length() > 100) {
                        argValue = argValue.substring(0, 100) + "...";
                    }
                    params.append(argValue);
                } else {
                    params.append("null");
                }
            }
        }

        return params.toString();
    }
}