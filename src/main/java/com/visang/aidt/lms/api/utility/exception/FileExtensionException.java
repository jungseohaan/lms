package com.visang.aidt.lms.api.utility.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileExtensionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FileExtensionException(String message) {
        super(message);

        log.error("FileExtensionException: {}", message);
    }

    public FileExtensionException(String message, Throwable cause) {
        super(message, cause);

        log.error("FileExtensionException: {} - {}", message, cause);
    }

}
