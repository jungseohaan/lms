package com.visang.aidt.lms.api.utility.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 사용자 요청 자료가 없을 때 런타임 예외 처리 클래스
 */
@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataNotFoundException(String errorMsg) {
        super(errorMsg);
    }

    public DataNotFoundException(String errorMsg, Throwable e) {
        super(errorMsg, e);
    }

}
