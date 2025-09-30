package com.visang.aidt.lms.api.utility.exception;

/**
 * JWT 만료 예외 클래스
 */
public class JwtExpiredException extends AuthFailedException {

    public JwtExpiredException(String message) {
        super(message);
    }

    public JwtExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}