package com.visang.aidt.lms.api.common;

import com.visang.aidt.lms.api.common.controller.JwtPublishController;
import com.visang.aidt.lms.api.common.dto.JwtResponse;
import com.visang.aidt.lms.api.common.dto.RefreshTokenRequest;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtPublishController 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class) // Mockito와 JUnit 5를 통합하기 위한 확장 설정
class JwtPublishControllerTest {

    @Mock // JwtUtil 목 객체 생성
    private JwtUtil jwtUtil;

    @InjectMocks // 테스트 대상 클래스에 목 객체 주입
    private JwtPublishController jwtPublishController;

    /**
     * generateJwt 메소드에 대한 테스트 그룹
     */
    @Nested
    @DisplayName("generateJwt 메소드 테스트")
    class GenerateJwtTests {

        @Test
        @DisplayName("유효한 입력으로 JWT 토큰 생성 성공")
        void shouldGenerateJwtSuccessfully() {
            // Given: 테스트 데이터 및 목 객체 동작 설정
            String timestamp = "1234567890";
            String id = "testUser";
            String accessToken = "accessToken123";
            String refreshToken = "refreshToken456";
            String hmac = "hmac789";

            // JwtUtil 메소드 호출 시 반환할 값 설정
            when(jwtUtil.generateAccessToken(id, "T", timestamp)).thenReturn(accessToken);
            when(jwtUtil.generateRefreshToken(id, "T", timestamp)).thenReturn(refreshToken);
            when(jwtUtil.calculateHmac(timestamp + id + accessToken)).thenReturn(hmac);

            // When: 테스트 대상 메소드 실행
            ResponseEntity<JwtResponse> response = jwtPublishController.generateJwt(timestamp, "T", id, "");

            // Then: 결과 검증
            assertAll(
                    // 그룹화된 어서션을 사용하여 여러 조건을 한 번에 검증
                    () -> assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP 상태 코드가 CREATED여야 합니다."),
                    () -> assertNotNull(response.getBody(), "응답 본문이 null이 아니어야 합니다."),
                    () -> assertEquals(accessToken, response.getBody().getAccessToken(), "Access 토큰이 일치해야 합니다."),
                    () -> assertEquals(refreshToken, response.getBody().getRefreshToken(), "Refresh 토큰이 일치해야 합니다."),
                    () -> assertEquals(hmac, response.getBody().getHmac(), "HMAC이 일치해야 합니다.")
            );

            // JwtUtil 메소드 호출 여부 검증
            verify(jwtUtil).generateAccessToken(id, "T", timestamp);
            verify(jwtUtil).generateRefreshToken(id, "T", timestamp);
            verify(jwtUtil).calculateHmac(timestamp + id + accessToken);
        }
    }

    /**
     * refreshJwt 메소드에 대한 테스트 그룹
     */
    @Nested
    @DisplayName("refreshJwt 메소드 테스트")
    class RefreshJwtTests {

        @Test
        @DisplayName("유효한 Refresh 토큰으로 JWT 갱신 성공")
        void shouldRefreshJwtSuccessfully() {
            // Given
            String refreshToken = "validRefreshToken";
            String id = "testUser";
            String newAccessToken = "newAccessToken123";
            String hmac = "newHmac789";
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken(refreshToken);

            // JwtUtil 메소드 호출 시 반환할 값 설정
            when(jwtUtil.validateToken(refreshToken, true)).thenReturn(true);
            when(jwtUtil.getIdFromToken(refreshToken)).thenReturn(id);
            when(jwtUtil.generateAccessToken(eq(id), "T", anyString())).thenReturn(newAccessToken);
            when(jwtUtil.calculateHmac(anyString())).thenReturn(hmac);

            // When: 테스트 대상 메소드 실행
            ResponseEntity<JwtResponse> response = jwtPublishController.refreshJwt(request);

            // Then: 결과 검증
            assertAll(
                    () -> assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP 상태 코드가 CREATED여야 합니다."),
                    () -> assertNotNull(response.getBody(), "응답 본문이 null이 아니어야 합니다."),
                    () -> assertEquals(newAccessToken, response.getBody().getAccessToken(), "새로운 Access 토큰이 일치해야 합니다."),
                    () -> assertEquals(refreshToken, response.getBody().getRefreshToken(), "Refresh 토큰이 변경되지 않아야 합니다."),
                    () -> assertEquals(hmac, response.getBody().getHmac(), "새로운 HMAC이 일치해야 합니다.")
            );

            // JwtUtil 메소드 호출 여부 검증
            verify(jwtUtil).validateToken(refreshToken, true);
            verify(jwtUtil).getIdFromToken(refreshToken);
            verify(jwtUtil).generateAccessToken(eq(id), "T", anyString());
            verify(jwtUtil).calculateHmac(anyString());
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalidToken", "expiredToken", ""})
        @DisplayName("유효하지 않은 Refresh 토큰으로 JWT 갱신 실패")
        void shouldFailToRefreshJwtWithInvalidToken(String invalidRefreshToken) {
            // Given
            when(jwtUtil.isRefreshTokenExpired(invalidRefreshToken)).thenReturn(true);

            RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
            refreshTokenRequest.setRefreshToken(invalidRefreshToken);

            // When & Then
            ResponseEntity<JwtResponse> response = jwtPublishController.refreshJwt(refreshTokenRequest);

            // Then: 결과 검증
            assertAll(
                    () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "HTTP 상태 코드가 UNAUTHORIZED여야 합니다."),
                    () -> assertNotNull(response.getBody(), "응답 본문이 null이 아니어야 합니다.")
            );

            // JwtUtil 메소드 호출 여부 검증
            verify(jwtUtil).isRefreshTokenExpired(invalidRefreshToken);
        }
    }
}
