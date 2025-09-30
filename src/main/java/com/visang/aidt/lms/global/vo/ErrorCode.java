package com.visang.aidt.lms.global.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
    // JWT 관련 에러
    JWT_EXPIRED("J001", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    JWT_INVALID("J002", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_MALFORMED("J003", "잘못된 형식의 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_SIGNATURE_ERROR("J004", "JWT 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    JWT_MISSING("J005", "JWT 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED),
    
    // 인증 관련 에러
    AUTH_HEADER_MISSING("A001", "Authorization 헤더가 누락되었습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_HEADER_INVALID("A002", "Authorization 헤더 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    HMAC_VERIFICATION_FAILED("A003", "HMAC 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    
    // 일반 에러
    INTERNAL_SERVER_ERROR("E001", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String code;
    private final String message;
    private final HttpStatus status;
    
    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
    
    @JsonValue
    public String getCode() {
        return code;
    }
}
