package com.visang.aidt.lms.api.utility.aspect;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import com.visang.aidt.lms.api.utility.exception.JwtExpiredException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.ErrorMessages;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.ErrorCode;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

//@Aspect
//@Slf4j
//@Component("AidtExceptionLoggingAspect")
/*
GlobalErrorLoggingAspect로 에러로그 단일화
 */
public class ExceptionLoggingAspect {

    /*
    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Pointcut("@annotation(loggable)")
    public void loggableMethods(Loggable loggable) {}

    @Around("loggableMethods(loggable)")
    public Object handleException(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (AidtException e) { // validation 을 위한 사용자 정의 Exception 발생시 메세지 그대로 뿌림
            return handleException(joinPoint, e, e.getMessage());
        } catch (IllegalArgumentException e) {
            return handleException(joinPoint, e, ErrorMessages.ILLEGAL_ARGUMENT.getMessagesContents());
//        } catch (NullPointerException e) {
//            return handleException(joinPoint, e, ErrorMessages.NULL_POINTER.getMessagesContents());
//        } catch (IOException e) {
//            return handleException(joinPoint, e, ErrorMessages.IO_EXCEPTION.getMessagesContents());
//        } catch (SQLException e) {
//            return handleException(joinPoint, e, ErrorMessages.SQL_EXCEPTION.getMessagesContents());
//        } catch (NoSuchElementException e) {
//            return handleException(joinPoint, e, ErrorMessages.NO_SUCH_ELEMENT.getMessagesContents());
//        } catch (JwtExpiredException e) {
//            return handleException(joinPoint, e, ErrorMessages.AUTHORIZATION_EXPIRED.getMessagesContents());
//        } catch (AuthFailedException e) {
//            return handleException(joinPoint, e, ErrorMessages.AUTHORIZATION_FAIL.getMessagesContents());
        } catch (Exception e) {
            return handleException(joinPoint, e, ErrorMessages.GENERIC_ERROR.getMessagesContents());
        }
    }

    private Object handleException(ProceedingJoinPoint joinPoint, Exception e, String errMsg) {
        if ("local".equals(serverEnv)) {
            log.error("Only Local:", e);
        }
        logErrorDetails(joinPoint, errMsg);
        return handleExceptionInternal(e, errMsg, joinPoint.getArgs());
    }

    private void logErrorDetails(ProceedingJoinPoint joinPoint, String errMsg) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String methodSignature = joinPoint.getSignature().toString();
        String requestUrl = request.getRequestURL().toString();

        log.error("Exception occurred in method: {} \nRequest URL: {} \nExceptionMessage: {}",
                methodSignature, requestUrl, errMsg);
    }

    private Object handleExceptionInternal(Exception e, String errMsg, Object[] args) {
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }

        Map<String, Object> paramData = new HashMap<>();
        if (request != null) {
            request.getParameterMap().forEach((key, values) -> paramData.put(key, values[0]));
        }

        // 기존 로직 제거 - 이정훈
//        Object param = args.length > 0 ? args[0] : null;
//        if (param instanceof Map) {
//            // 파라미터가 Map
//            paramData.putAll((Map<String, Object>) param);
//            CustomLokiLog.errorLog(e);
//            return AidtCommonUtil.makeResultFail(paramData, null, errMsg);
//        } else {
//            // 파라미터가  DTO
//            CustomLokiLog.errorLog(e);
//            return AidtCommonUtil.makeResultFail(param, null, errMsg);
//        }

        // exception 정보만 담아서 전달하고 ApiAuthCheckAspect 의 try 내부 로직에서 throw 처리
        return ResponseDTO.of()
                .header(null) // optional 헤더 정의가 필요할때
                .fail()
                .resultCode(HttpStatus.BAD_REQUEST)
                .resultData(Map.of("exception", e))
                .build();
    }

     */
}
