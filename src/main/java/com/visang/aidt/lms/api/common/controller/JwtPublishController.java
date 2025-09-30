package com.visang.aidt.lms.api.common.controller;

import com.visang.aidt.lms.api.common.dto.JwtResponse;
import com.visang.aidt.lms.api.common.dto.RefreshTokenRequest;
import com.visang.aidt.lms.api.socket.service.StudentService;
import com.visang.aidt.lms.api.socket.service.TeacherService;
import com.visang.aidt.lms.api.user.service.UserService;
import com.visang.aidt.lms.api.utility.aspect.ApiAuthCheckAspect;
import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰 발급 및 갱신을 위한 컨트롤러
 * 이 클래스는 JWT 토큰의 생성과 갱신을 담당
 */
@RestController
@Tag(name = "(공통) JWT 토큰 관리 API", description = "(공통) JWT 토큰 관리 API")
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class JwtPublishController {
    private static final String HMAC_HEADER = "HMAC";

    // JWT 유틸리티 클래스 주입
    private final JwtUtil jwtUtil;

    private final UserService userService;

    private final Environment environment;
    private final TeacherService teacherService;
    private final StudentService studentService;

    private static final String STUDENT = "S";
    private static final String TEACHER = "T";

    private final ApiAuthCheckAspect aspect;
    /**
     * JWT 토큰 생성 메서드
     *
     * @param timestamp 요청 시간
     * @param id 사용자 ID
     * @return JWT 토큰 정보를 포함한 ResponseEntity
     */
    @GetMapping("/common/jwt")
    @Operation(summary = "JWT 토큰 생성", description = "timestamp와 id를 받아 JWT 토큰과 HMAC을 생성")
    public ResponseEntity<JwtResponse> generateJwt(
            @RequestParam("timestamp") String timestamp,
            @RequestParam("id") String id,
            @RequestParam(value = "userSeCd", required = false) String userSeCd,
            @RequestParam(value ="claId", required = false) String claId ) {
        if (StringUtils.isEmpty(userSeCd)) {
            try {
                userSeCd = userService.findUserSeCdByUserId(id);
            } catch (DataAccessException e) {
                log.error("token 발급 시 사용자 정보 조회 실패 - 데이터베이스 오류 - userId : {}", id, e);
            } catch (IllegalArgumentException e) {
                log.error("token 발급 시 사용자 정보 조회 실패 - 잘못된 파라미터 - userId : {}", id, e);
            } catch (NullPointerException e) {
                log.error("token 발급 시 사용자 정보 조회 실패 - 필수 데이터 누락 - userId : {}", id, e);
            } catch (Exception e) {
                log.error("token 발급 시 사용자 정보 조회 실패 - 예상치 못한 오류 - userId : {}", id, e);
            }
        }

        // Profile에 따라 Subject를 결정
        String subject = this.extractSubjectFromProfile();

        //  userSeCd가 있을 때만 claId 조회 시도  // claId가 null이거나 비어있을 경우 DB 조회 후 처리
        if (StringUtils.isNotEmpty(userSeCd) && StringUtils.isEmpty(claId)) {

            String tempUserSeCd = userSeCd.toUpperCase();
            if (tempUserSeCd.equals(TEACHER)) {
                // 교사 인 경우 tc_cla_info 테이블에서 cla_id 조회
                try {
                    claId = teacherService.getClaIdByUserId(id);
                } catch (DataAccessException e) {
                    // CSAP 보안 취약점 수정 - 구체적인 예외 처리
                    log.error("token 발급 시 교사 claId 조회 중 DB 오류: {}", CustomLokiLog.errorLog(e));
                } catch (Exception e) {
                    // throws Exception 때문에 체크 예외 처리 필요
                    log.error("token 발급 시 교사 claId 조회 중 오류: {}", CustomLokiLog.errorLog(e));
                }
            } else if (tempUserSeCd.equals(STUDENT)) {
                // 학생 인 경우 tc_cla_mb_info 테이블에서 cla_id 조회
                try {
                    claId = studentService.getClaIdByUserId(id);
                } catch (DataAccessException e) {
                    // CSAP 보안 취약점 수정 - 구체적인 예외 처리
                    log.error("token 발급 시 학생 claId 조회 중 DB 오류: {}", CustomLokiLog.errorLog(e));
                } catch (Exception e) {
                    // throws Exception 때문에 체크 예외 처리 필요
                    log.error("token 발급 시 학생 claId 조회 중 오류: {}", CustomLokiLog.errorLog(e));
                }
            }
        }

        // Access 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(id, userSeCd, timestamp, claId, subject);

        // Refresh 토큰 생성
        String refreshToken = jwtUtil.generateRefreshToken(id, userSeCd, timestamp, claId, subject);

        // HMAC 계산 (timestamp + id + accessToken을 이용)
        // 토큰 변조에 대한 무결성 확인
        String hmac = jwtUtil.calculateHmac(timestamp + id + accessToken);

        // JwtResponse 객체를 생성하여 ResponseEntity로 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new JwtResponse(accessToken, refreshToken, hmac));
    }

    /**
     * JWT 토큰 갱신 메서드
     *
     * @param refreshTokenRequest Refresh 토큰을 포함한 요청 객체
     * @return 새로운 Access 토큰 정보를 포함한 ResponseEntity
     */
    @PostMapping("/common/jwt/refresh")
    @Operation(summary = "JWT 토큰 갱신", description = "Refresh 토큰을 이용해 새로운 Access 토큰을 발급")
    public ResponseEntity<JwtResponse> refreshJwt(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        try {
            if (jwtUtil.isRefreshTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JwtResponse("Refresh 토큰이 만료되었습니다. 다시 토큰을 발급 받아 주세요.", null, null));
            }

            jwtUtil.validateToken(refreshToken, true);  // Validate as refresh token
            String id = jwtUtil.getIdFromToken(refreshToken);
            String userSeCd = jwtUtil.getUserSeCdFromToken(refreshToken);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String newAccessToken = jwtUtil.generateAccessToken(id, userSeCd, timestamp);
            String hmac = jwtUtil.calculateHmac(timestamp + id + newAccessToken);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new JwtResponse(newAccessToken, refreshToken, hmac));

        } catch (ExpiredJwtException e) {
            log.error("Refresh 토큰이 만료됨", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JwtResponse("Refresh 토큰이 만료되었습니다. 다시 토큰을 발급 받아 주세요.", null, null));
        } catch (JwtException e) {
            log.error("유효하지 않은 Refresh 토큰", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JwtResponse("유효하지 않은 Refresh 토큰입니다.", null, null));
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JwtResponse("토큰 갱신 중 오류가 발생했습니다.", null, null));
        }
    }

    @GetMapping("/common/jwt/check")
    @Operation(summary = "JWT 토큰 확인", description = "토큰정보를 통해 유효한 토큰인지 확인")
    public ResponseEntity<Object> checkJwt() {
        Map<String, Object> resultData = new HashMap<>();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        try {
            String jwtToken = jwtUtil.extractJwtToken(request);
            String hmacHeader = request.getHeader(HMAC_HEADER);

            aspect.validateJwtToken(jwtToken, hmacHeader);

            resultData.put("code", "20000");
            resultData.put("message", "SUCCESS");

            return ResponseEntity.status(HttpStatus.OK).body(resultData);
        }catch(AuthFailedException e ){
            resultData.put("code", "40101");
            resultData.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultData);
        }catch(ExpiredJwtException e){
            resultData.put("code", "40102");
            resultData.put("message", "토큰이 만료되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultData);
        }catch (Exception e) {
            resultData.put("code", "50000");
            resultData.put("message", "JWT 검증 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultData);
        }
    }

    /**
     * 현재 활성화된 프로파일에 따라 Subject를 추출하는 메서드
     * @return "engl" 또는 "math" 중 하나의 문자열
     */
    private String extractSubjectFromProfile() {
        String[] activeProfiles = environment.getActiveProfiles();

        for (String profile : activeProfiles) {
            if (profile.startsWith("engl")) {
                return "engl";
            } else if (profile.startsWith("math")) {
                return "math";
            }
        }

        return "math";
    }
}