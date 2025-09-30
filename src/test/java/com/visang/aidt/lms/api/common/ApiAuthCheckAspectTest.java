package com.visang.aidt.lms.api.common;

import com.visang.aidt.lms.api.utility.aspect.ApiAuthCheckAspect;
import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import com.visang.aidt.lms.api.utility.exception.JwtExpiredException;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiAuthCheckAspectTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private ApiAuthCheckAspect apiAuthCheckAspect;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apiAuthCheckAspect, "subject", "testSubject");
        ReflectionTestUtils.setField(apiAuthCheckAspect, "isAuthSkip", false);
        ReflectionTestUtils.setField(apiAuthCheckAspect, "whitelistPatterns", Arrays.asList("/shop/userinfo", "/member/_login.json"));
    }

    @Test
    void testAuthCheckSuccess() throws Throwable {
        // given
        String token = "validToken";
        String hmac = "validHmac";
        Claims claims = mock(Claims.class);

        when(request.getRequestURI()).thenReturn("/test/api");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("HMAC")).thenReturn(hmac);
        when(jwtUtil.getAllClaimsFromToken(anyString())).thenReturn(claims);
        when(claims.getSubject()).thenReturn("testSubject");
        when(claims.get("timestamp", String.class)).thenReturn("123456789");
        when(claims.get("id", String.class)).thenReturn("testId");
        when(jwtUtil.calculateHmac(anyString())).thenReturn(hmac);

        // when
        apiAuthCheckAspect.authCheck(joinPoint);

        // then
        verify(joinPoint).proceed();
    }

    @Test
    void testAuthCheckFailedDueToInvalidToken() {
        // given
        String invalidToken = "invalidToken";

        when(request.getRequestURI()).thenReturn("/test/api");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtil.getAllClaimsFromToken(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        // when & then
        assertThrows(AuthFailedException.class, () -> apiAuthCheckAspect.authCheck(joinPoint));
    }

    @Test
    void testAuthCheckFailedDueToInvalidHmac() {
        // given
        String token = "validToken";
        String hmac = "invalidHmac";
        Claims claims = mock(Claims.class);

        when(request.getRequestURI()).thenReturn("/test/api");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("HMAC")).thenReturn(hmac);
        when(jwtUtil.getAllClaimsFromToken(anyString())).thenReturn(claims);
        when(claims.getSubject()).thenReturn("testSubject");
        when(claims.get("timestamp", String.class)).thenReturn("123456789");
        when(claims.get("id", String.class)).thenReturn("testId");
        when(jwtUtil.calculateHmac(anyString())).thenReturn("differentHmac");

        // when & then
        assertThrows(AuthFailedException.class, () -> apiAuthCheckAspect.authCheck(joinPoint));
    }

    @Test
    void testAuthCheckFailedDueToExpiredToken() {
        // given
        String expiredToken = "expiredToken";

        when(request.getRequestURI()).thenReturn("/test/api");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtUtil.getAllClaimsFromToken(expiredToken)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // when & then
        assertThrows(JwtExpiredException.class, () -> apiAuthCheckAspect.authCheck(joinPoint));
    }

    @Test
    void testAuthCheckFailedDueToMissingAuthorizationHeader() {
        // given
        when(request.getRequestURI()).thenReturn("/test/api");
        when(request.getHeader("Authorization")).thenReturn(null);

        // when & then
        assertThrows(AuthFailedException.class, () -> apiAuthCheckAspect.authCheck(joinPoint));
    }

    @Test
    void testAuthCheckSkipped() throws Throwable {
        // given
        ReflectionTestUtils.setField(apiAuthCheckAspect, "isAuthSkip", true);

        // when
        apiAuthCheckAspect.authCheck(joinPoint);

        // then
        verify(joinPoint).proceed();
        verify(request, never()).getHeader(anyString());
    }

    @Test
    void testAuthCheckSkippedForWhitelistUrl() throws Throwable {
        // given
        when(request.getRequestURI()).thenReturn("/shop/userinfo");

        // when
        apiAuthCheckAspect.authCheck(joinPoint);

        // then
        verify(joinPoint).proceed();
        verify(request, never()).getHeader(anyString());
    }

    @Test
    void testAuthCheckProcessedForNonWhitelistUrl() throws Throwable {
        // given
        String nonWhitelistUrl = "/some/other/api";
        when(request.getRequestURI()).thenReturn(nonWhitelistUrl);
        String token = "validToken";
        String hmac = "validHmac";
        Claims claims = mock(Claims.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("HMAC")).thenReturn(hmac);
        when(jwtUtil.getAllClaimsFromToken(anyString())).thenReturn(claims);
        when(claims.getSubject()).thenReturn("testSubject");
        when(claims.get("timestamp", String.class)).thenReturn("123456789");
        when(claims.get("id", String.class)).thenReturn("testId");
        when(jwtUtil.calculateHmac(anyString())).thenReturn(hmac);

        // when
        apiAuthCheckAspect.authCheck(joinPoint);

        // then
        verify(joinPoint).proceed();
    }
}