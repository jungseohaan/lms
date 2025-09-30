package com.visang.aidt.lms.api.utility.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FilesizeExceedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FilesizeExceedException(String message) {
        super(message);

        log.error("FilesizeExceedException: {}", message);
    }

    public FilesizeExceedException(String message, Throwable cause) {
        super(message, cause);

        log.error("FilesizeExceedException: {} - {}", message, cause);
    }

}
