package com.visang.aidt.lms.api.utility.aspect;

import com.visang.aidt.lms.api.utility.exception.*;
import com.visang.aidt.lms.api.utility.exception.IllegalStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//@RestControllerAdvice
public class ExceptionTranslaterAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(new ErrorResponse("Internal Server Error"), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException ex) {
        log.error("DataNotFoundException: {}", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException: {}", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(TimeoutException ex) {
        log.error("TimeoutException: {}", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), headers, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AuthFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthFailedException(AuthFailedException ex) {
        log.error("AuthFailedException: {}", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), headers, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(FileExtensionException.class)
    public ResponseEntity<ErrorResponse> handleFileExtensionException(FileExtensionException ex) {
        log.error("FileExtensionException: {}", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilesizeExceedException.class)
    public ResponseEntity<ErrorResponse> handleFileziseExceedException(FilesizeExceedException ex) {
        log.error("FilesizeExceedException: {}", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), headers, HttpStatus.BAD_REQUEST);
    }


    // TODO : 예외 처리 관련 클래스 추가 필요



    // JSON 응답의 형식을 나타내는 클래스
    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}
