package com.visang.aidt.lms.api.utility.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalStateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalStateException(String errorMsg) {
        super(errorMsg);
    }

    public IllegalStateException(String errorMsg, Throwable e) {
        super(errorMsg, e);
    }

}

