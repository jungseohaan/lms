package com.visang.aidt.lms.api.utility.utils;

import lombok.Getter;
import java.util.Optional;

/**
 * AOP 기반 예외 로깅 시스템에서 사용하는 에러 메시지 및 코드 정의 enum
 * 
 * <p>각 예외 타입별로 표준화된 에러 코드(ERRTRYC001~009)와 메시지, 
 * 그리고 해당 Exception 클래스 매핑을 관리합니다.</p>
 * 
 * <p>enum 방식으로 에러 코드를 관리하여
 * 타입 안정성과 IDE 지원을 제공합니다.</p>
 * 
 * @author AIDT LMS API Team
 * @since 2025-01-01
 */
@Getter
public enum ErrorMessages {
    /** 기본 에러: 매핑되지 않은 모든 예외에 사용 */
    GENERIC_ERROR("ERRTRYC001", "일반 에러", Exception.class),
    
    /** Null 포인터 예외: 객체 참조가 null일 때 발생 */
//    NULL_POINTER("ERRTRYC001", "Null 포인터", NullPointerException.class),
    
    /** 입출력 예외: 파일, 네트워크 등 I/O 작업 실패 */
//    IO_EXCEPTION("ERRTRYC001", "입출력 에러", java.io.IOException.class),
    
    /** SQL 예외: 데이터베이스 관련 오류 */
//    SQL_EXCEPTION("ERRTRYC001", "SQL 에러", java.sql.SQLException.class),
    
    /** 요소 없음 예외: 존재하지 않는 데이터에 접근 시도 */
//    NO_SUCH_ELEMENT("ERRTRYC001", "존재하지 않는 요소", java.util.NoSuchElementException.class),
    
    /** JWT 만료 예외: 토큰 유효시간 초과 */
//    AUTHORIZATION_EXPIRED("ERRTRYC001", "인증 만료", com.visang.aidt.lms.api.utility.exception.JwtExpiredException.class),
    
    /** 인증 실패 예외: 로그인 또는 권한 검증 실패 */
//    AUTHORIZATION_FAIL("ERRTRYC001", "인증 실패", com.visang.aidt.lms.api.utility.exception.AuthFailedException.class),
    
    /** AIDT 커스텀 예외: 비즈니스 로직 검증 실패 */
//    AIDT_EXCEPTION("ERRTRYC001", "사용자 정의 예외", com.visang.aidt.lms.api.utility.exception.AidtException.class),

    /** 잘못된 인수 예외: 메소드에 잘못된 파라미터가 전달된 경우 */
    ILLEGAL_ARGUMENT("ERRTRYC001", "잘못된 인수", IllegalArgumentException.class);

    /** 표준화된 에러 코드 */
    private final String errorCode;
    
    /** 사용자에게 표시할 에러 메시지 */
    private final String messagesContents;
    
    /** 해당 에러와 매핑되는 Exception 클래스 */
    private final Class<? extends Exception> exceptionClass;

    /**
     * ErrorMessages enum 생성자
     * 
     * @param errorCode 표준화된 에러 코드
     * @param messagesContents 사용자 표시 메시지
     * @param exceptionClass 매핑될 Exception 클래스
     */
    ErrorMessages(String errorCode, String messagesContents, Class<? extends Exception> exceptionClass) {
        this.errorCode = errorCode;
        this.messagesContents = messagesContents;
        this.exceptionClass = exceptionClass;
    }

    /**
     * 매핑되지 않은 예외에 대한 기본 에러 반환
     * 
     * @return GENERIC_ERROR 상수
     */
    public static ErrorMessages getDefaultError() {
        return GENERIC_ERROR;
    }

}

