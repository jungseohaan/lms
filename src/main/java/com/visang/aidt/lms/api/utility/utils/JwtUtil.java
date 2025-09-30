package com.visang.aidt.lms.api.utility.utils;

import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import com.visang.aidt.lms.api.utility.exception.JwtExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    @Value("${lms.jwt.subject}")
    private String subject;

    @Value("${lms.jwt.secret}")
    private String secret;

    @Value("${lms.jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${lms.jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @Value("${lms.api.hmac.skip}")
    private boolean isHmacSkip;

    private Key key;

    /**
     * 시크릿 키 생성
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 액세스 토큰 생성
     * @param id 사용자 ID
     * @param userSeCd 사용자 유형 (T : 교사 / S : 학생)
     * @param timestamp 타임스탬프
     * @return 생성된 액세스 토큰
     */
    public String generateAccessToken(String id, String userSeCd, String timestamp) {
        return generateToken(id, userSeCd, timestamp, accessTokenValidity);
    }

    /**
     * 액세스 토큰 생성 (클래스 ID 및 주체 포함)
     * @param id 사용자 ID
     * @param userSeCd 사용자 유형 (T : 교사 / S : 학생)
     * @param timestamp 타임스탬프
     * @param claId 클래스 ID
     * @param subject 주체
     * @return 생성된 액세스 토큰
     */
    public String generateAccessToken(String id, String userSeCd, String timestamp, String claId, String subject) {
        return generateToken(id, userSeCd, timestamp, claId, subject, accessTokenValidity);
    }

    /**
     * 리프레시 토큰 생성
     * @param id 사용자 ID
     * @param userSeCd 사용자 유형 (T : 교사 / S : 학생)
     * @param timestamp 타임스탬프
     * @return 생성된 리프레시 토큰
     */
    public String generateRefreshToken(String id, String userSeCd, String timestamp) {
        return generateToken(id, userSeCd, timestamp, refreshTokenValidity);
    }

    /**
     * 리프레시 토큰 생성 (클래스 ID 및 주체 포함)
     * @param id 사용자 ID
     * @param userSeCd 사용자 유형 (T : 교사 / S : 학생)
     * @param timestamp 타임스탬프
     * @param claId 클래스 ID
     * @param subject 주체
     * @return 생성된 리프레시 토큰
     */
    public String generateRefreshToken(String id, String userSeCd, String timestamp, String claId, String subject) {
        return generateToken(id, userSeCd, timestamp, claId, subject, refreshTokenValidity);
    }

    /**
     * JWT 토큰 생성
     * @param id 사용자 ID
     * @param userSeCd 사용자 유형 (T : 교사 / S : 학생)
     * @param timestamp 타임스탬프
     * @param validity 토큰 유효 기간
     * @return 생성된 JWT 토큰
     */
    private String generateToken(String id, String userSeCd, String timestamp, long validity) {

        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validity))
                .claim("timestamp", timestamp)
                .claim("id", id)
                .claim("userSeCd", userSeCd)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰 생성 (클래스 ID 포함)
     * @param id 사용자 ID
     * @param userSeCd 사용자 유형 (T : 교사 / S : 학생)
     * @param timestamp 타임스탬프
     * @param claId 클래스 ID
     * @param validity 토큰 유효 기간
     * @return 생성된 JWT 토큰
     */
    private String generateToken(String id, String userSeCd, String timestamp, String claId, long validity) {

        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validity))
                .claim("timestamp", timestamp)
                .claim("id", id)
                .claim("userSeCd", userSeCd)
                .claim("claId", claId)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰 생성 (클래스 ID 및 주체 포함)
     * @param id 사용자 ID
     * @param userSeCd 사용자 유형 (T : 교사 / S : 학생)
     * @param timestamp 타임스탬프
     * @param claId 클래스 ID
     * @param subject 주체
     * @param validity 토큰 유효 기간
     * @return 생성된 JWT 토큰
     */
    private String generateToken(String id, String userSeCd, String timestamp, String claId, String subject, long validity) {

        JwtBuilder builder =
         Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + validity))
                .claim("timestamp", timestamp)
                .claim("id", id)
                .claim("subject", subject);

        // 선택 값은 null/빈문자열이면 건너뜀
        if (StringUtils.isNotEmpty(userSeCd)) {
            builder.claim("userSeCd", userSeCd);
        }
        if (StringUtils.isNotEmpty(claId)) {
            builder.claim("claId", claId);
        }
        return builder
                .signWith(key)
                .compact();
    }

    /**
     * 토큰 유효성 검사
     * @param token 검사할 토큰
     * @param isRefreshToken 리프레시 토큰 여부
     * @return 유효성 여부
     */
    public boolean validateToken(String token, Boolean isRefreshToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getPayload();

            // 토큰 만료 검사
            if (claims.getExpiration().before(new Date())) {
                log.warn("Token is expired");
                throw new ExpiredJwtException(null, claims, "Token has expired");
            }

            // 추가적인 검증 로직
            String tokenSubject = claims.getSubject();
            if (tokenSubject == null || !tokenSubject.equals(this.subject)) {
                log.warn("Token subject is invalid");
                throw new JwtException("Invalid token subject");
            }

            // 필요한 클레임 존재 여부 확인
            if (!claims.containsKey("id") || !claims.containsKey("timestamp")) {
                log.warn("Token is missing required claims");
                throw new JwtException("Token is missing required claims");
            }

            return true;
        } catch (ExpiredJwtException e) {
            if (isRefreshToken) {
                log.error("Refresh token has expired");
            }
            throw e;
        } catch (JwtException e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 토큰에서 ID 추출
     * @param token JWT 토큰
     * @return 추출된 ID
     */
    public String getIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("id", String.class));
    }

    /**
     * 토큰에서 userSeCd 추출
     * @param token JWT 토큰
     * @return 추출된 ID
     */
    public String getUserSeCdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("userSeCd", String.class));
    }

    /**
     * 토큰의 만료 날짜 반환
     * @param token JWT 토큰
     * @return 만료 날짜
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 클레임 추출
     * @param token JWT 토큰
     * @param claimsResolver 클레임 추출 함수
     * @return 추출된 클레임 값
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임 추출
     * @param token JWT 토큰
     * @return 모든 클레임
     * @throws JwtExpiredException JWT 토큰이 만료된 경우
     * @throws AuthFailedException JWT 토큰이 유효하지 않은 경우
     */
    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("JWT 토큰이 만료되었습니다.", e);
        } catch (MalformedJwtException e) {
            throw new AuthFailedException("잘못된 형식의 JWT 토큰입니다.", e);
        } catch (SignatureException e) {
            throw new AuthFailedException("JWT 서명 검증에 실패했습니다.", e);
        } catch (IllegalArgumentException e) {
            throw new AuthFailedException("JWT 토큰이 필요합니다.", e);
        } catch (Exception e) {
            throw new AuthFailedException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }

    /**
     * 주어진 데이터의 HMAC 계산
     * @param data HMAC을 계산할 데이터
     * @return 계산된 HMAC
     */
    public String calculateHmac(String data) {
        return Base64.encodeBase64String(HmacUtils.hmacSha256(secret.getBytes(), data.getBytes()));
    }

    /**
     * Refresh 토큰 만료 여부 확인
     * @param refreshToken Refresh 토큰
     * @return 만료 여부
     */
    public boolean isRefreshTokenExpired(String refreshToken) {
        try {
            validateToken(refreshToken, true);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            log.error("Error while checking refresh token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출
     * Authorization 헤더가 없거나 잘못된 형식인 경우 예외 발생
     *
     * @param request 현재의 HTTP 요청 객체
     * @return 추출된 JWT 토큰
     * @throws AuthFailedException Authorization 헤더가 없거나 잘못된 형식일 때 발생
     */
    public String extractJwtToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTH_HEADER))
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()))
                .orElseThrow(() -> new AuthFailedException("Authorization 헤더 누락 또는 잘못된 형식"));
    }

    /**
     * JWT 토큰의 유효성을 검증
     * 토큰에서 클레임을 추출하고 HMAC 값을 검증
     *
     * @param jwtToken   JWT 토큰 문자열
     * @param hmacHeader HMAC 헤더 값
     * @throws AuthFailedException JWT 또는 HMAC 검증 실패 시 발생
     */
    public void validateJwtToken(String jwtToken, String hmacHeader) {
        Claims claims = getAllClaimsFromToken(jwtToken);

        String subjectFromToken = claims.getSubject();
        if (!subject.equals(subjectFromToken)) {
            log.error("JWT 주체 검증 실패: subjectFromToken={}", subjectFromToken);
            throw new AuthFailedException("JWT 주체 검증 실패");
        }
        String timestamp = getClaimOrThrow(claims, "timestamp");
        String id = getClaimOrThrow(claims, "id");

        // isHmacSkip이 false면 hmac 값 검증 skip
        if (!isHmacSkip) {
            String calculatedHmac = calculateHmac(timestamp + id + jwtToken);
            if (!calculatedHmac.equals(hmacHeader)) {
                log.error("HMAC 검증 실패: calculatedHmac={}, hmacHeader={}", calculatedHmac, hmacHeader);
                throw new AuthFailedException("HMAC 검증 실패");
            }
        }
    }

    /**
     * 클레임에서 특정 값을 추출하고, 값이 없을 경우 예외 발생
     *
     * @param claims    JWT 클레임 객체
     * @param claimName 추출할 클레임의 이름
     * @return 추출된 클레임 값
     * @throws AuthFailedException 클레임 값이 없을 때 발생
     */
    private String getClaimOrThrow(Claims claims, String claimName) {
        return Optional.ofNullable(claims.get(claimName, String.class))
                .orElseThrow(() -> {
                    log.error("JWT에서 {} 값이 누락되었습니다.", claimName);
                    return new AuthFailedException("JWT에서 " + claimName + " 값이 누락되었습니다.");
                });
    }

}