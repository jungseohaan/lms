package com.visang.aidt.lms.api.utility.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 인증 실패에 따른 런타임 예외 처리 클래스
 */
@Slf4j
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthFailedException(String errorMsg) {
        super(errorMsg);
    }

    public AuthFailedException(String errorMsg, Throwable e) {
        super(errorMsg, e);
    }

}
