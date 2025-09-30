package com.visang.aidt.lms.api.utility.aspect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.mq.service.NatsSendService;
import com.visang.aidt.lms.api.utility.exception.AuthFailedException;
import com.visang.aidt.lms.api.utility.exception.JwtExpiredException;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.ExcBriefLiteUtil;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import com.visang.aidt.lms.api.utility.utils.LogSizerUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import com.visang.aidt.lms.global.vo.ErrorCode;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API 인증 체크 Aspect 클래스
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Lazy
public class ApiAuthCheckAspect {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";
    private static final String HMAC_HEADER = "HMAC";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String MISSING_AUTH = "__MISSING_AUTH__";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${lms.jwt.subject}")
    private String subject;

    @Value("${lms.api.auth.skip}")
    private boolean isAuthSkip;

    @Value("${lms.api.hmac.skip}")
    private boolean isHmacSkip;

    @Value("#{'${lms.api.auth.whitelist}'.split('\\s*,\\s*')}")
    private List<String> whitelistPatterns;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Value("${lms.api.log.encode}")
    private boolean isLogEncode;

    @Value("${spring.topic.loki-log-send-name}")
    private String lokiLogTopicName;

    private final NatsSendService natsSendService;

    /**
     * API 메서드 호출 시 JWT 및 HMAC 검증을 수행
     * 인증이 필요하지 않거나 요청 URI가 화이트리스트에 있는 경우 인증을 건너뜀
     *
     * @param pjp 진행 중인 메서드 호출에 대한 정보를 담고 있는 ProceedingJoinPoint 객체
     * @return 원래의 API 호출 결과를 반환
     * @throws Throwable 예외 발생 시 해당 예외를 그대로 던짐
     */
    @Around("execution(* com.visang.aidt.lms.api..controller..*.*(..)) && !within(com.visang.aidt.lms.api.utility.aspect.ApiAuthCheckAspect)")
    public Object authCheck(ProceedingJoinPoint pjp) throws Throwable {

        List<Map<String, Object>> paramList = new ArrayList<>();
        String sTime = LocalDateTime.now().format(DATE_FORMATTER);
        HttpServletRequest request = null;

        try {
            if (RequestContextHolder.getRequestAttributes() != null) {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            }
            paramList = extractRequestParams(request, pjp);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
        }


        Claims claims = null;
        Object result = null;
        String eTime = null;
        String respStr = null;
        List<String> respList = new ArrayList<>();
        String hash = null;
        boolean isException = false;
        Exception exception = null;

        try {
            // local과 테섭에서는 인증 제외
            if (StringUtils.equals(serverEnv, "local")
                    || StringUtils.equals(serverEnv, "engl-dev")
                    || StringUtils.equals(serverEnv, "math-dev")
                    || StringUtils.equals(serverEnv, "vs-dev")
                    || StringUtils.equals(serverEnv, "vs-math-develop")
                    || isAuthSkip
                    || RequestContextHolder.getRequestAttributes() == null) {
                log.info("API 인증 체크 건너뛰기 됨");
                result = pjp.proceed();
            } else {
                if (request != null && isWhitelisted(request.getRequestURI())) {
                    result = pjp.proceed();
                } else {
                    // null 체크 로직 추가
                    if (request == null) {
                        throw new IllegalArgumentException("Request 객체가 null입니다.");
                    }

                    String jwtToken = extractJwtToken(request);
                    String hmacHeader = request.getHeader(HMAC_HEADER);

                    // 헤더 누락일 경우: 예외 대신 조용히 401 응답 반환 (에러 로그 최소화)
                    if (StringUtils.equals(jwtToken, MISSING_AUTH)) {
                        Map<String, Object> errorData = new HashMap<>();
                        errorData.put("code", ErrorCode.AUTH_HEADER_MISSING.getCode());
                        errorData.put("message", ErrorCode.AUTH_HEADER_MISSING.getMessage());
                        errorData.put("path", request != null ? request.getRequestURI() : "");
                        errorData.put("name", "AuthHeaderMissing");

                        ResponseDTO<?> errorResponse = ResponseDTO.of()
                                .fail()
                                .resultCode(ErrorCode.AUTH_HEADER_MISSING.getStatus())
                                .resultMessage(ErrorCode.AUTH_HEADER_MISSING.getMessage())
                                .resultData(errorData)
                                .build();
                        return errorResponse;
                    }

                    // JWT 토큰 null 체크
                    if (jwtToken == null || jwtToken.trim().isEmpty()) {
                        throw new IllegalArgumentException("JWT 토큰이 null이거나 빈 문자열입니다.");
                    }

//                    // HMAC 헤더 null 체크
//                    if (hmacHeader == null || hmacHeader.trim().isEmpty()) {
//                        throw new IllegalArgumentException("HMAC 헤더가 null이거나 빈 문자열입니다.");
//                    }

                    claims = validateJwtToken(jwtToken, hmacHeader);
                    request.setAttribute("auth.userId", getClaimOrThrow(claims, "id"));
                    result = pjp.proceed();
                }
            }
            // ResponseDTO인 경우에만 로깅 처리
            if (result instanceof ResponseDTO) {
                // 응답값 처리
                respStr = (result != null) ? objectMapper.writeValueAsString(result) : "";
                respList.add(respStr);
                hash = (respStr != null && !respStr.isEmpty()) ? sha256(respStr) : null;

                // ResponseDTO에 시간 정보 추가
                ResponseDTO<?> responseDTO = (ResponseDTO<?>) result;
                if (responseDTO.getBody() instanceof CustomBody) {
                    CustomBody oldBody = (CustomBody) responseDTO.getBody();
                    /**
                     * ExceptionLoggingAspect 에서 선 검출되기 때문에 throw 없이는 catch 안탐
                     * ExceptionLoggingAspect return 시 resultData에 exceeption 객체 할당하여 처리
                     */
                    if (oldBody.success() == false) {
                        Object oldResultObj = oldBody.resultData();
                        if (oldResultObj != null && oldResultObj instanceof java.util.Map) {
                            Map<String, Object> oldResultMap = (Map<String, Object>) oldResultObj;
                            Object exceptionObj = oldResultMap.get("exception");
                            // exception 객체 존재 시 에러 상황이므로 현 try의 exception catch 되도록 throw
                            if (exceptionObj != null && exceptionObj instanceof Exception) {
                                exception = (Exception) oldResultMap.get("exception");
                                throw exception;
                            }
                        }
                    }
                    // 오류 검출 상태가 아닐 경우 throw 없이 기존 로직 동작
                    ResponseDTO<?> newResponseDTO = ResponseDTO.of()
                            .header(responseDTO.getHeaders())
                            .success()
                            .resultCode(HttpStatus.valueOf(oldBody.resultCode()))
                            .paramData(oldBody.paramData())
                            .resultData(oldBody.resultData())
                            .resultMessage(oldBody.resultMessage())
                            .sTime(sTime)
                            .eTime(eTime)
                            .currentTime(sTime)
                            .hash(hash)
                            .build();

                    // HttpServletResponse를 통해 헤더 추가
                    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                    if (response != null) {
                        response.setHeader("X-Response-Time", eTime);
                        response.setHeader("X-Response-Hash", hash);
                        response.setHeader("Access-Control-Expose-Headers", "X-Response-Hash");
                    }

                    result = newResponseDTO;
                    respStr = objectMapper.writeValueAsString(newResponseDTO);
                }
            } else {
                // ResponseDTO가 아닌 경우는 로깅하지 않고 원본 응답 반환
                // 헤더에 해시 값만 추가
                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                if (response != null) {
                    String responseStr = null;
                    String responseHash = null;
                    
                    try {
                        responseStr = objectMapper.writeValueAsString(result);
                        responseHash = (responseStr != null && !responseStr.isEmpty()) ? sha256(responseStr) : null;
                    } catch (Exception e) {
                        responseHash = sha256(result.getClass().getSimpleName() + "_" + System.currentTimeMillis());
                    }
                    
                    response.setHeader("X-Response-Hash", responseHash);
                    response.setHeader("Access-Control-Expose-Headers", "X-Response-Hash");
                }
                return result;
            }
        } catch (JwtExpiredException ex) {
            //isException = true; // JWT 토큰은 오류 처리에서 빼도록 함
            //exception = ex; // JWT 토큰은 오류 처리에서 빼도록 함
//            JWT 만료관련 에러 비활성화
//            log.warn(CustomLokiLog.errorLog(ex));

            // 요청 정보 수집
            String method = request.getMethod();                  // 요청 메서드 (GET/POST 등)
            String uri    = request.getRequestURI();              // 요청 URI
            String query  = request.getQueryString();             // 쿼리스트링
            String ip     = request.getRemoteAddr();              // 요청자 IP
            String userAgent = request.getHeader("User-Agent");
            String jwtToken = extractJwtToken(request);           // Authorization 헤더에서 JWT 추출

            // 만료된 토큰에서도 ExpiredJwtException 안에는 Claims 정보가 남아있음
            // timestamp, id, subject 등을 꺼내어 로그로 기록 가능
            Claims expiredClaims = extractClaimsFrom(ex);

            // 보안상 토큰 원문을 직접 남기지 않고, 해시값으로 대체
//            log.warn(
//                    "JWT 토큰 만료: msg={} uri={}{} method={} ip={} userAgent{} tokenHash={} subject={} userId={} timestamp={}",
//                    ex.getMessage(),
//                    uri,
//                    (query != null ? ("?" + query) : ""),
//                    method,
//                    ip,
//                    userAgent,
//                    jwtToken,
//                    (expiredClaims != null ? expiredClaims.getSubject() : null),
//                    (expiredClaims != null ? expiredClaims.get("id") : null),
//                    (expiredClaims != null ? expiredClaims.get("timestamp") : null)
//            );


            Map<String, Object> errorData = new HashMap<>();
            errorData.put("code", ErrorCode.JWT_EXPIRED.getCode());
            errorData.put("message", ErrorCode.JWT_EXPIRED.getMessage());
            errorData.put("path", request != null ? request.getRequestURI() : "");
            errorData.put("name", "JwtExpiredException");
            
            ResponseDTO<?> errorResponse = ResponseDTO.of()
                    .fail()
                    .resultCode(ErrorCode.JWT_EXPIRED.getStatus())
                    .resultMessage(ErrorCode.JWT_EXPIRED.getMessage())
                    .resultData(errorData)
                    .build();
            result = errorResponse; // 할당하지 않으면 finally에서 null pointer 발생
            respStr = objectMapper.writeValueAsString(errorResponse);
            respList.add(respStr);
            hash = (respStr != null && !respStr.isEmpty()) ? sha256(respStr) : null;

            return errorResponse;
        } catch (AuthFailedException ex) {
            isException = true;
            exception = ex;
            log.error("인증 실패: {}", ex.getMessage());
            log.error(CustomLokiLog.errorLog(ex));

            ErrorCode errorCode = determineAuthErrorCode(ex.getMessage());

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("code", errorCode.getCode());
            errorData.put("message", errorCode.getMessage());
            errorData.put("path", request != null ? request.getRequestURI() : "");
            errorData.put("name", "AuthFailedException");
            
            ResponseDTO<?> errorResponse = ResponseDTO.of()
                    .fail()
                    .resultCode(errorCode.getStatus())
                    .resultMessage(errorCode.getMessage())
                    .resultData(errorData)
                    .build();
            result = errorResponse; // 할당하지 않으면 finally에서 null pointer 발생
            respStr = objectMapper.writeValueAsString(errorResponse);
            respList.add(respStr);
            hash = (respStr != null && !respStr.isEmpty()) ? sha256(respStr) : null;

            return errorResponse;
        } catch (Exception ex) {
            isException = true;
            exception = ex;
            log.error("오류: {}", ex.getMessage());
            log.error(CustomLokiLog.errorLog(ex));

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("code", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
            errorData.put("message", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
            errorData.put("path", request != null ? request.getRequestURI() : "");
            errorData.put("name", ex.getClass().getSimpleName());
            
            ResponseDTO<?> errorResponse = ResponseDTO.of()
                    .fail()
                    .resultCode(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                    .resultMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                    .resultData(errorData)
                    .build();
            result = errorResponse; // 할당하지 않으면 finally에서 null pointer 발생
            respStr = objectMapper.writeValueAsString(errorResponse);
            respList.add(respStr);
            hash = (respStr != null && !respStr.isEmpty()) ? sha256(respStr) : null;

            return errorResponse;
        } finally {
            eTime = LocalDateTime.now().format(DATE_FORMATTER);

            // ResponseDTO인 경우에만 로깅
            if (result instanceof ResponseDTO) {
                // 수정된 응답으로 로그 기록
                respList.clear();
                respList.add(respStr);
                logLmsApiAccess(request, pjp, claims, paramList, respList, sTime, eTime, hash, isException, exception);
            }
        }

        // ResponseDTO가 아닌 경우는 로깅하지 않고 바로 반환
        return result;
    }

    /**
     * 요청 파라미터를 List<String> 형태로 추출
     * 쿼리 파라미터와 메서드 인자(Body) 모두 포함
     * @param request HttpServletRequest 객체
     * @param pjp AOP JoinPoint 객체
     * @return 요청 파라미터 리스트
     */
    private List<Map<String, Object>> extractRequestParams(HttpServletRequest request, ProceedingJoinPoint pjp) throws Exception {
        List<Map<String, Object>> paramList = new ArrayList<>();
        // 1. 쿼리 파라미터
        if (request != null) {
            Map<String, Object> flatParams = new HashMap<>();
            request.getParameterMap().forEach((k, v) -> {
                if (v != null && v.length > 0) {
                    flatParams.put(k, v[0]);
                }
            });
            paramList.add(flatParams);
        }


        // 2. 바디 파라미터 (메서드 인자)
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg != null && !(arg instanceof HttpServletRequest)) {
                try {
                    Map<String, Object> argMap = objectMapper.convertValue(arg, Map.class);
                    paramList.add(argMap);
                } catch (IllegalArgumentException ignore) {
                    // 객체를 Map으로 변환할 수 없는 경우 처리 (예: String, Integer 등)
                    Map<String, Object> fallback = new HashMap<>();
                    fallback.put("unknown", arg.toString());
                    paramList.add(fallback);
                }
            }
        }

        return paramList;
    }

    /**
     * LMSAPI 요청/응답 전체 로깅
     * 정상/비정상 응답 모두 고객 요구 파라미터 기록
     * @param request HttpServletRequest 객체
     * @param pjp AOP JoinPoint 객체
     * @param claims JWT Claims (사용자 정보)
     * @param paramList 요청 파라미터 리스트
     * @param respList 응답값 리스트
     * @param sTime 요청 시간
     * @param eTime 응답 시간
     * @param hash 응답값 해시
     * @param isException 예외 발생 여부
     * @param exception 예외 객체
     */
    private void logLmsApiAccess(HttpServletRequest request, ProceedingJoinPoint pjp, Claims claims,
                                 List<Map<String, Object>> paramList, List<String> respList,
                                 String sTime, String eTime, String hash,
                                 boolean isException, Exception exception) {
        try {
            // MDC에 hash 값 추가
            org.slf4j.MDC.put("hash", hash != null ? hash : "");

            Map<String, Object> logData = new HashMap<>();

            // URL과 메서드 정보
            if (request != null) {
                logData.put("url", request.getRequestURI());
                logData.put("method", request.getMethod());
            }

            // UUID (JWT claims에서 가져오거나 새로 생성)
            String uuid = null;
            String cid = null; // cid req에서 뺴서 세팅
            String uType = null;
            if (claims != null) {
                // uuid(사용자 아이디) 세팅
                if (claims.get("uuid") != null) {
                    uuid = claims.get("uuid", String.class);
                } else if (claims.get("id") != null) {
                    uuid = claims.get("id", String.class);
                }
                // cid(클래스 아이디) 세팅
                if (claims.get("claId") != null) {
                    cid = claims.get("claId", String.class);
                }
                // uType(사용자 구분 T/S) 세팅
                if (claims.get("userSeCd") != null) {
                    uType = claims.get("userSeCd", String.class);
                }
            }

            // 요청 본문
            if (!paramList.isEmpty()) {
                Map<String, Object> paramMap = paramList.get(0);
                if (MapUtils.isNotEmpty(paramMap)) {
                    logData.put("req", paramMap);  // 첫 번째 파라미터를 요청 본문으로 사용
                    // uuid 세팅이 안되었을 경우 parameter에서 추출하여 할당한다 (예 : jwt 토큰 만료되어 claims null 일 경우)
                    if (StringUtils.isEmpty(uuid)) {
                        uuid = MapUtils.getString(paramMap, "userId");
                    }
                    // cid 값이 비어 있을 경우 (jwt claim에 없음)
                    if (StringUtils.isEmpty(cid)) {
                        cid = MapUtils.getString(paramMap, "claId");
                    }
                    // userSeCd 값이 비어 있을 경우 (jwt claim에 없음)
                    if (StringUtils.isEmpty(uType)) {
                        uType = MapUtils.getString(paramMap, "userSeCd");
                    }
                }
            }
            // 마지막에 체크했는데 없을 경우 랜덤 uuid 세팅 (uuid는 필수 값이기 때문에 이렇게 하는 듯 한데 통계치 계산에 오류 발생 여지 있음)
            if (StringUtils.isEmpty(uuid)) {
                uuid = java.util.UUID.randomUUID().toString();
            }
            // 마지막에 체크했는데 없을 경우 내부 계정에 한해서만 세팅 되도록 함 (DB 조회 하면 로그 쌓을 때 부하 발생 여지 있음)
            if (StringUtils.isEmpty(uType) && StringUtils.length(uuid) < 36) {
                if (uuid.endsWith("-t")) {
                    uType = "T";
                } else if (uuid.matches(".*-s\\d+$")) {
                    uType = "S";
                }
            }

            // 응답 본문 - 실제 API 응답만 사용 - 에러시에는 result 전달하지 않음
            if (!respList.isEmpty() && isException == false) {
                String responseBody = respList.get(0);
                // DB 쿼리 결과가 아닌 실제 API 응답만 사용
                if (!responseBody.contains("executed in") && !responseBody.contains("|---------|")) {
                    try {
                        // JSON 문자열을 Map으로 파싱
                        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
                        logData.put("resp", responseMap); // 객체 형태로 넣기
                    } catch (Exception e) {
                        // JSON이 아닌 일반 문자열이라면 그대로 넣기
                        logData.put("resp", responseBody);
                    }
                }
            }

            // 응답 시간 계산 (밀리초)
            LocalDateTime start = LocalDateTime.parse(sTime, DATE_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(eTime, DATE_FORMATTER);
            //long durationMs = java.time.Duration.between(start, end).toMillis();
            long durationMs = Math.max(0L, Duration.between(start, end).toMillis());
            logData.put("duration", durationMs);

            logData.put("uuid", uuid);
            logData.put("uType", uType);
            logData.put("cid", cid);

            // 프로필 정보 추가
            logData.put("appName", "vlmsapi");
            // logData.put("beforePprofile", getProfileBasedLogMessage()); // profile 있기 떄문에 필요없을 듯 하여 제거
            logData.put("profile", getProfileBasedLogMessage());
            // 오류 플래그 true 체크 하여 오류 로깅 정보 추가 or 10초 이상 걸린 경우
            if (isException || durationMs >= 10_000) {
                logData.put("logType", "error");
                if (exception != null) {
                    logData.put("exception", exception.getClass().getSimpleName());
                    logData.put("errCd", "ERRTRYC001");
                    /**
                     * 간소화 로직
                     * 단순 오류 메세지 전달이 아닌 메소드 및 오류 상세 정보를 간단하게 출력하기 위함
                     */
                    String errMsg = ExcBriefLiteUtil.brief(exception, "com.visang.aidt.lms.api");
                    logData.put("errMsg", errMsg);
                    logData.put("message", "try-catch 에서 검출된 오류 로깅");
                } else {
                    logData.put("errCd", "ERRDLAY001");
                    logData.put("errMsg", "API 호출 완료 까지 10초 이상 지연");
                    logData.put("message", "호출 완료 시간(ms) : " + durationMs);
                }
            }

            // 기존 로직 주석 (로그 길이가 nats 허용보다 길 경우 줄이는 로직 적용)
            /*
            // JSON 문자열로 변환
            String jsonMessage = objectMapper.writeValueAsString(logData);
            // CustomLokiLog를 사용하여 APM 형식의 로그 생성 또는 평문 처리
            String apmLog = "";
            if (isLogEncode) {
                apmLog = CustomLokiLog.logTemplateForApp(
                        jsonMessage,
                        "",
                        uuid,
                        request != null ? request.getRequestURI() : ""
                );
            } else {
                apmLog = jsonMessage;
            }
//            log.info(apmLog);
            */
            // 로그 사이즈 조절 util 추가
            String apmLog = LogSizerUtil.buildCappedJson(logData, objectMapper, isLogEncode);

            natsSendService.pushNatsLogMQ(lokiLogTopicName, apmLog);
        } catch (Exception e) {
            log.error("LMSAPI 접근 로깅 실패: {}", e.getMessage());
            log.error(CustomLokiLog.errorLog(e));
        } finally {
            // MDC에서 hash 값 제거
            org.slf4j.MDC.remove("hash");
        }
    }

    /**
     * 문자열 SHA-256 해시값 생성
     * @param value 해시를 생성할 문자열
     * @return SHA-256 해시값(16진수 문자열)
     */
    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] salt = newSalt();
            digest.update(salt);
            byte[] hashBytes = digest.digest(value.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] newSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }


    /**
     * 클라이언트 IP 주소를 가져옴
     * X-Forwarded-For 헤더가 있으면 해당 값을 우선 사용하고, 없으면 요청 IP 사용
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트 IP 주소
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출
     * Authorization 헤더가 없거나 잘못된 형식인 경우 예외 발생
     *
     * @param request 현재의 HTTP 요청 객체
     * @return 추출된 JWT 토큰
     * @throws AuthFailedException Authorization 헤더가 없거나 잘못된 형식일 때 발생
     */
    /*
    private String extractJwtToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTH_HEADER))
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()))
                .orElseThrow(() -> new AuthFailedException("[call : " + getFullURL(request) + "] - Authorization 헤더 누락 또는 잘못된 형식"));
    }
     */

    private String extractJwtToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header == null) {
            // 예외 대신 특수 토큰으로 누락 표시
            log.debug("[auth] Authorization 헤더 누락: {}", getFullURL(request));
            return MISSING_AUTH;
        }
        if (!header.startsWith(BEARER_PREFIX)) {
            throw new AuthFailedException("[call : " + getFullURL(request) + "] - Authorization 헤더 잘못된 형식");
        }
        return header.substring(BEARER_PREFIX.length());
    }

    /**
     * JWT 토큰의 유효성을 검증
     * 토큰에서 클레임을 추출하고 HMAC
     * @param jwtToken   JWT 토큰 문자열
     * @param hmacHeader HMAC 헤더 값
     * @return 검증된 JWT 클레임
     * @throws AuthFailedException JWT 또는 HMAC 검증 실패 시 발생
     */
    public Claims validateJwtToken(String jwtToken, String hmacHeader) {
        Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
        validateSubject(claims.getSubject());
        String timestamp = getClaimOrThrow(claims, "timestamp");
        String id = getClaimOrThrow(claims, "id");

        // isHmacSkip이 false면 hmac 값 검증 skip
        if (!isHmacSkip) {
            validateHmac(timestamp, id, jwtToken, hmacHeader);
        }

        return claims;
    }

    /**
     * JWT의 주체(subject) 값이 올바른지 검증
     * 일치하지 않을 경우 예외 발생
     *
     * @param subjectFromToken JWT 토큰에서 추출한 주체(subject) 값
     * @throws AuthFailedException 주체 검증 실패 시 발생
     */
    private void validateSubject(String subjectFromToken) {
        if (!"math".equals(subjectFromToken)&& !"engl".equals(subjectFromToken)) {
            log.error("JWT 주체 검증 실패: subjectFromToken={}", subjectFromToken);
            throw new AuthFailedException("JWT 주체 검증 실패");
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

    /**
     * HMAC 값을 계산하고 요청 헤더의 HMAC 값과 비교하여 일치하는지 검증
     * 일치하지 않을 경우 예외 발생
     *
     * @param timestamp  JWT 토큰에서 추출한 timestamp 값
     * @param id         JWT 토큰에서 추출한 id 값
     * @param jwtToken   JWT 토큰 문자열
     * @param hmacHeader 요청 헤더에서 받은 HMAC 값
     * @throws AuthFailedException HMAC 검증 실패 시 발생
     */
    private void validateHmac(String timestamp, String id, String jwtToken, String hmacHeader) {
        String calculatedHmac = jwtUtil.calculateHmac(timestamp + id + jwtToken);
        if (!calculatedHmac.equals(hmacHeader)) {
            log.error("HMAC 검증 실패: calculatedHmac={}, hmacHeader={}", calculatedHmac, hmacHeader);
            throw new AuthFailedException("HMAC 검증 실패");
        }
    }

    /**
     * 요청 URI가 화이트리스트에 포함되는지 확인
     *
     * @param uri 요청 URI
     * @return 화이트리스트에 포함되면 true, 그렇지 않으면 false 반환
     */
    private boolean isWhitelisted(String uri) {
        return whitelistPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    public String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        return queryString == null ? requestURL.toString() : requestURL.append("?").append(queryString).toString();
    }

    /**
     * 인증 실패 메시지에 따라 적절한 ErrorCode를 결정
     * @param errorMessage 인증 실패 메시지
     * @return 해당하는 ErrorCode
     */
    private ErrorCode determineAuthErrorCode(String errorMessage) {
        if (errorMessage.contains("Authorization 헤더 누락")) {
            return ErrorCode.AUTH_HEADER_MISSING;
        } else if (errorMessage.contains("Authorization 헤더") && errorMessage.contains("잘못된 형식")) {
            return ErrorCode.AUTH_HEADER_INVALID;
        } else if (errorMessage.contains("HMAC 검증 실패")) {
            return ErrorCode.HMAC_VERIFICATION_FAILED;
        } else if (errorMessage.contains("JWT")) {
            return ErrorCode.JWT_INVALID;
        } else {
            return ErrorCode.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * 현재 프로필에 따른 로그 메시지 생성
     * @return 프로필 기반 로그 메시지
     */
//    private String getProfileBasedLogMessage() {
//        if (StringUtils.contains(serverEnv, "math")) {
//            return "math";
//        } else if (StringUtils.contains(serverEnv, "engl")) {
//            return "engl";
//        } else if (StringUtils.contains(serverEnv, "vs")) {
//            return "vs";
//        } else {
//            return "access";
//        }
//    }
    /* 20250729 비상측 요청*/
    private String getProfileBasedLogMessage() {
        if (StringUtils.equals(serverEnv, "math-dev")) {
            return "dev";
        } else if (StringUtils.equals(serverEnv, "math-stg")) {
            return "stg";
        } else if (StringUtils.equals(serverEnv, "stg1")) {
            return "stg1";
        } else if (StringUtils.equals(serverEnv, "math-releas")) {
            return "r-math";
        } else if (StringUtils.equals(serverEnv, "engl-release")) {
            return "r-engl";
        } else if (StringUtils.equals(serverEnv, "math-prod")) {
            return "math";
        } else if (StringUtils.equals(serverEnv, "engl-prod")) {
            return "engl";
        } else if (StringUtils.equals(serverEnv, "vs-dev")) {
            return "vs-devlop";
        } else if (StringUtils.equals(serverEnv, "vs-prod")) {
            return "vs-prod";
        } else if (StringUtils.equals(serverEnv, "math-beta2")) {
            return "b2-math";
        } else if (StringUtils.equals(serverEnv, "engl-beta2")) {
            return "b2-engl";
        } else {
            return "access";
        }
    }

    /**
     * JwtExpiredException 내부의 cause 에 있는 ExpiredJwtException 에서
     * Claims 객체를 안전하게 추출한다.
     *
     * @param t 예외 객체 (JwtExpiredException)
     * @return Claims 객체 (없으면 null)
     */
    private Claims extractClaimsFrom(Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof ExpiredJwtException) {
                return ((ExpiredJwtException) cur).getClaims();
            }
            cur = cur.getCause();
        }
        return null;
    }

}