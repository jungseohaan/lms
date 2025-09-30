package com.visang.aidt.lms.api.socket.service;

import com.visang.aidt.lms.api.materials.service.PortalPzService;
import com.visang.aidt.lms.api.repository.*;
import com.visang.aidt.lms.api.repository.entity.*;
import com.visang.aidt.lms.api.socket.mapper.SocketMapper;
import com.visang.aidt.lms.api.socket.vo.UserDiv;
import com.visang.aidt.lms.api.user.mapper.UserMapper;
import com.visang.aidt.lms.api.user.service.UserService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import com.visang.aidt.lms.api.utility.utils.PasswordHashUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.QueryTimeoutException;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final StdtRegInfoRepository stdtRegInfoRepository;
    private final TcRegInfoRepository tcRegInfoRepository;
    private final TcClaInfoRepository tcClaInfoRepository;
    private final TcClaMbInfoRepository tcClaMbInfoRepository;
    private final SchoolRepository schoolRepository;
    private final CntnLogRepository cntnLogRepository;
    private final UserMapper userMapper;
    private final PortalPzService portalPzService;

    private final JwtUtil jwtUtil;
    private final Environment environment;
    private final UserService userService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    private final SocketMapper socketMapper;

    @Value("${key.salt.main}")
    private String keySaltMain;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    private final PasswordHashUtil passwordHashUtil;

    /* 내부에서 insert/update 처리하는 부분이 있어서 @Transactional(readOnly = true) 처리 하지 않음. */
    public Map<String, Object> getUserInfo(Map<String, Object> paramData, boolean isCheck) throws Exception {

        int result = 0;
        String returnType = null;

        Map<String, Object> resultMap = new HashMap<>();

        String userId = String.valueOf(paramData.get("uuid"));
        String semester = (String) paramData.getOrDefault("semester", "");
        String claId = (String) paramData.getOrDefault("claId", "");

        // 아이디 미 입력
        if (StringUtils.isEmpty(userId)) {
            resultMap.put("result", 100);
            resultMap.put("returnType", "Error - uuid required");
            return resultMap;
        }

        //로그인 여부 업데이트
        paramData.put("lgnSttsAt", 1);
        updateLoginStatus(paramData);

        User user = userRepository.findByUserId(userId);
        if (user == null || !MapUtils.getString(paramData, "userDiv").equals(user.getUserSeCd())) {
            resultMap.put("result", 102);
            resultMap.put("returnType", "Error - No User exists");
            return resultMap;
        }

        // 로그인 시 pwd 파라미터 처리
        /**
         * isCheck : 로그인에서 호출 시 매개변수 true로 전달
         * userId.length() < 36 체크 : 내부 계정에 한해서만 동작하도록 함
         */
        boolean isAuthCheck = isCheck && userId.length() < 36;
        if (isAuthCheck) {
            Map<String, Object> authMap = checkUserAuth(userId, MapUtils.getString(paramData, "pwd"));
            if (MapUtils.isNotEmpty(authMap)) {
                // 운영에서는 password 오류 발생 시 데이터 처리 없이 바로 return (front-end 작업 및 공지 전 까지 주석)
                /*if (Arrays.asList("math-prod", "engl-prod").contains(serverEnv)) {
                    return authMap;
                }*/
                result = MapUtils.getInteger(authMap, "result", 0);
                returnType = MapUtils.getString(authMap, "returnType", "");
            }
        }
        // 이후 필수 로직 그대로 진행

        // ---> [S] 소켓 및 학습 필수 로직
        String userDiv = user.getUserSeCd();
        String schlCd = null;
        Long classid = 0L;
        long textbkId = 0;
        long textbkIdxId = 0;

        if (StringUtils.equals(userDiv, UserDiv.S.getCode())) {
            StdtRegInfoEntity userInfo = stdtRegInfoRepository.findByUserId(user.getUserId()).orElse(null);
            if (userInfo != null) {
                schlCd = userInfo.getSchlCd();
            }
            List<TcClaMbInfoEntity> tcClaMbInfoList = tcClaMbInfoRepository.findByStdtIdAndActvtnAt(userId, "Y");
            // 학생이 속해있는 클래스가 다수일 경우 선생님이 로그인 후 학생에게 클래스를 전달하는 등의 프로세스 검토
            String tcId = "";
            if (CollectionUtils.isNotEmpty(tcClaMbInfoList)) {
                // 현재는 학생 1:1 매치 가정하여 로직 구현
                claId = tcClaMbInfoList.get(0).getClaId();
                tcId = tcClaMbInfoList.get(0).getUserId();
            }
            // 학급 구성원의 ID 정보로 선생님 학급 테이블에서 id 조회
            if (StringUtils.isNotEmpty(claId)) {
                TcClaInfoEntity tcClaInfo = tcClaInfoRepository.findByClaIdAndUserId(claId, tcId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                }
            }
            //학생 교과서조회
            Map<String, Object> stParam = new HashMap<>();
            stParam.put("tcId", tcId);
            stParam.put("claId", claId);
            Map<String, Object> stTextbookInfo = portalPzService.getStTextbookInfo(stParam);
            textbkId = MapUtils.getLong(stTextbookInfo, "textbkId", 0L);
            textbkIdxId = MapUtils.getLong(stTextbookInfo, "textbkIdxId", 0L);
        }
        // 선생일 경우 선생 매치 클래스 테이블에서 직접 데이터 조회 (선생님과 클래스간 구조가 바뀔 경우 로직 수정 검토 - 예 n:n )
        else if (StringUtils.equals(userDiv, UserDiv.T.getCode())) {
            TcRegInfoEntity userInfo = tcRegInfoRepository.findByUserId(user.getUserId()).orElse(null);

            Map<String, Object> tcClaUserInfo = portalPzService.getTcClaUserInfo(paramData);
            String mainUserId = null;
            if (tcClaUserInfo != null && tcClaUserInfo.get("userId") != null) {
                mainUserId = tcClaUserInfo.get("userId").toString();
            }
            if (StringUtils.isEmpty(mainUserId)) {
                mainUserId = user.getUserId(); // 기본값으로 현재 사용자 ID 사용
            }
            if (userInfo != null) {
                schlCd = userInfo.getSchlCd();
            }

            if (StringUtils.isNotEmpty(claId)) {

                TcClaInfoEntity tcClaInfo = tcClaInfoRepository.findByClaIdAndUserId(claId, mainUserId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                }
            } else {
                TcClaInfoEntity tcClaInfo = tcClaInfoRepository.findTop1ByUserId(mainUserId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                    claId = tcClaInfo.getClaId();
                }
            }

            //교사 교과서조회
            Map<String, Object> tcParam = new HashMap<>();
            tcParam.put("wrterId", user.getUserId());
            tcParam.put("claId", claId);
            tcParam.put("smteCd", semester);

            Object userIds = user.getUserId();
            if (userIds != null && !userIds.toString().isEmpty()) {
                tcParam.put("wrterId", userIds);
            } else {
                // null이나 빈값일 때는 기본값이나 다른 처리
                tcParam.put("wrterId", user.getUserId()); // 또는 적절한 기본값
            }

            Map<String, Object> tcTextbookInfo = portalPzService.getTcTextbookInfo(tcParam);
            textbkId = MapUtils.getLong(tcTextbookInfo, "textbkId", 0L);
            textbkIdxId = MapUtils.getLong(tcTextbookInfo, "textbkIdxId", 0L);
        }
        int frIdx = 0;
        if (StringUtils.isNotEmpty(schlCd)) {
            School school = schoolRepository.findBySchlCd(schlCd).orElse(null);
            if (school != null) {
                frIdx = school.getId();
            }
        }

        resultMap.put("frIdx", frIdx);
        resultMap.put("classid", classid);
        resultMap.put("claId", claId);
        resultMap.put("textbkId", textbkId);
        resultMap.put("textbkIdxId", textbkIdxId);
        // --/> [E] 소켓 및 학습 필수 로직

        resultMap.put("birthday", user.getBrth());
        resultMap.put("thumbnail", "");

        /*SSOToken 제거*/
        resultMap.put("gender", user.getSex());
        /*schIdx 제거*/
        /*brcIdx 제거*/
        resultMap.put("nickName", user.getFlnm()); // nick name이 없어서 이름으로 대체
        resultMap.put("defaultThumbnail", "");
        resultMap.put("uuid", userId);
        /*token 제거*/

        if (isAuthCheck) {
            resultMap.put("result", result);
            if (StringUtils.isNotEmpty(returnType)) {
                resultMap.put("returnType", returnType);
            }
        } else {
            resultMap.put("result", 0);
        }

        // 학생일때는 프로필 이미지를 랜덤으로 생성해서 보내준다.
        // resultMap.put("profileThumbnail", MemberUtil.getStudentProfileImage());
        resultMap.put("profileThumbnail", "");
        resultMap.put("name", user.getFlnm());
        resultMap.put("id", user.getId());
        resultMap.put("enc", "");
        resultMap.put("pwd", "");
        resultMap.put("userDiv", paramData.get("userDiv"));
        resultMap.put("resultType", "Success");
        resultMap.put("key", "");

        return resultMap;
    }


    //@Transactional(transactionManager = "transactionManager" )
    public Map<String, Object> insertAccessLog(Map<String, Object> paramData, HttpServletRequest request) throws Exception {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            String userDiv = (String) paramData.get("userDiv");
            if ("S".equals(userDiv) || "T".equals(userDiv)) { // 학생 & 선생 모두 기록 기록
                LocalDateTime now = LocalDateTime.now();
                String uuid = (String) paramData.get("uuid");

                // ip 값이 NULL 이면 저장하지 않는다.

                /*기존에는 front-end 안들어오면 pass 했지만 server 값으로 default 처리*/
                /*resultMap.put("resultOk",false);
                resultMap.put("resultMsg","IP 주소값이 존재하지않습니다.");
                return resultMap;*/
                String ip = MapUtils.getString(paramData, "ip");
                String device = MapUtils.getString(paramData, "device");
                String os = MapUtils.getString(paramData, "os");
                String browser = MapUtils.getString(paramData, "browser");
                if (request != null) {
                    String userAgent = request.getHeader("User-Agent");
                    if (StringUtils.isEmpty(ip)) {
                        ip = AidtCommonUtil.getClientIp(request);
                    }
                    if (StringUtils.isEmpty(device)) {
                        device = AidtCommonUtil.detectDevice(userAgent);
                    }
                    if (StringUtils.isEmpty(os)) {
                        os = AidtCommonUtil.detectOS(userAgent);
                    }
                    if (StringUtils.isEmpty(browser)) {
                        browser = AidtCommonUtil.detectBrowser(userAgent);
                    }
                }
                if (StringUtils.isEmpty(ip)) {
                    ip = "0.0.0.0";
                }
                if (StringUtils.isEmpty(device)) {
                    device = "Empty";
                }
                if (StringUtils.isEmpty(os)) {
                    os = "Empty";
                }
                if (StringUtils.isEmpty(browser)) {
                    browser = "Empty";
                }
                // cntnLog 저장 시 접속 정보가 모두 not null 이라 default 세팅 로직 추가 하여 파라미터 세팅
                CntnLogEntity cntnLog = CntnLogEntity.builder()
                        .userId(uuid)
                        .userSeCd(userDiv)
                        .cntnDt(now)
                        .cntnIpAddr(ip)
                        .deviceInfo(device)
                        .osInfo(os)
                        .brwrInfo(browser)
                        .rgtr(uuid)
                        .regDt(now)
                        .mdfr(uuid)
                        .mdfyDt(now)
                        .build();
                CntnLogEntity saved = cntnLogRepository.save(cntnLog);
                resultMap.put("id", saved.getId());
                resultMap.put("resultOk", true);
                resultMap.put("resultMsg", "저장완료");
            }
        } catch (NullPointerException e) {
            log.error("saveCntnLog - NullPointerException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "NullPointerException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("saveCntnLog - IllegalArgumentException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "IllegalArgumentException: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error("saveCntnLog - DataAccessException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "DataAccessException: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("saveCntnLog - RuntimeException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "RuntimeException: " + e.getMessage());
        } catch (Exception e) {
            log.error("saveCntnLog - Exception:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "Exception: " + e.getMessage());
        }
        return resultMap;
    }

    public Map<String, Object> updateLoginStatus(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            userMapper.updateLgnSttsAt(paramData);
            resultMap.put("result", 0);
        } catch (NullPointerException e) {
            log.error("updateLgnSttsAt - NullPointerException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "NullPointerException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("updateLgnSttsAt - IllegalArgumentException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "IllegalArgumentException: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error("updateLgnSttsAt - DataAccessException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "DataAccessException: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("updateLgnSttsAt - RuntimeException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "RuntimeException: " + e.getMessage());
        } catch (Exception e) {
            log.error("updateLgnSttsAt - Exception:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "Exception: " + e.getMessage());
        }
        return resultMap;
    }

    public Map<String, Object> updateLoginStatusAll(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            userMapper.updateLgnSttsAtAll(paramData);
            resultMap.put("result", 0);
        } catch (NullPointerException e) {
            log.error("updateLoginStatusAll - NullPointerException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "NullPointerException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("updateLoginStatusAll - IllegalArgumentException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "IllegalArgumentException: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error("updateLoginStatusAll - DataAccessException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "DataAccessException: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("updateLoginStatusAll - RuntimeException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "RuntimeException: " + e.getMessage());
        } catch (Exception e) {
            log.error("updateLoginStatusAll - Exception:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "Exception: " + e.getMessage());
        }
        return resultMap;
    }

    public Map<String, Object> getUserInfoForSocket(Map<String, Object> paramData) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        String userId = String.valueOf(paramData.get("uuid"));
        String claId = (String) paramData.getOrDefault("claId", "");

        // 아이디 미 입력
        if (StringUtils.isEmpty(userId)) {
            resultMap.put("result", 100);
            resultMap.put("returnType", "Error - uuid required");
            return resultMap;
        }

        User user = userRepository.findByUserId(userId);
        if (user == null || !MapUtils.getString(paramData, "userDiv").equals(user.getUserSeCd())) {
            resultMap.put("result", 102);
            resultMap.put("returnType", "Error - No User exists");
            return resultMap;
        }

        // ---> [S] 소켓 및 학습 필수 로직
        String userDiv = user.getUserSeCd();
        Long classid = 0L;

        if (StringUtils.equals(userDiv, UserDiv.S.getCode())) {
            List<TcClaMbInfoEntity> tcClaMbInfoList = tcClaMbInfoRepository.findByStdtIdAndActvtnAt(userId, "Y");
            // 학생이 속해있는 클래스가 다수일 경우 선생님이 로그인 후 학생에게 클래스를 전달하는 등의 프로세스 검토
            String tcId = "";
            if (CollectionUtils.isNotEmpty(tcClaMbInfoList)) {
                // 현재는 학생 1:1 매치 가정하여 로직 구현
                claId = tcClaMbInfoList.get(0).getClaId();
                tcId = tcClaMbInfoList.get(0).getUserId();
            }
            // 학급 구성원의 ID 정보로 선생님 학급 테이블에서 id 조회
            if (StringUtils.isNotEmpty(claId)) {
                TcClaInfoEntity tcClaInfo = tcClaInfoRepository.findByClaIdAndUserId(claId, tcId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                }
            }
        }
        // 선생일 경우 선생 매치 클래스 테이블에서 직접 데이터 조회 (선생님과 클래스간 구조가 바뀔 경우 로직 수정 검토 - 예 n:n )
        else if (StringUtils.equals(userDiv, UserDiv.T.getCode())) {
            Map<String, Object> tcClaUserInfo = portalPzService.getTcClaUserInfo(paramData);
            String mainUserId = MapUtils.getString(tcClaUserInfo, "userId");
            if (StringUtils.isEmpty(mainUserId)) {
                mainUserId = user.getUserId(); // 기본값으로 현재 사용자 ID 사용
            }
            if (StringUtils.isNotEmpty(claId)) {
                TcClaInfoEntity tcClaInfo = tcClaInfoRepository.findByClaIdAndUserId(claId, mainUserId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                }
            } else {
                TcClaInfoEntity tcClaInfo = tcClaInfoRepository.findTop1ByUserId(mainUserId);
                if (tcClaInfo != null) {
                    classid = tcClaInfo.getId();
                    claId = tcClaInfo.getClaId();
                }
            }
        }

        resultMap.put("uuid", userId);
        resultMap.put("result", 0);
        resultMap.put("classid", classid);
        resultMap.put("claId", claId);
        resultMap.put("name", user.getFlnm());
        resultMap.put("id", user.getId());
        resultMap.put("userDiv", paramData.get("userDiv"));

        return resultMap;
    }

    public Map<String, Object> getCurrentTokenInfo(Map<String, Object> paramMap) {

        String id = MapUtils.getString(paramMap, "uuid", "");
        if (StringUtils.isEmpty(id)) {
            log.error("token 발급 시 user 정보 이상 - userId : {}", id);
            return null;
        }
        String userSeCd = MapUtils.getString(paramMap, "userDiv", "");
        if (StringUtils.isEmpty(userSeCd)) {
            try {
                userSeCd = userService.findUserSeCdByUserId(id);
            } catch (EmptyResultDataAccessException e) {
                log.error("조회 결과 없음 : id {}", id);
            } catch (DataIntegrityViolationException e) {
                log.error("DB 제약조건 위반 : id {}", id);
            } catch (QueryTimeoutException e) {
                log.error("쿼리 실행 시간 초과 : id {}", id);
            } catch (PersistenceException e) {
                log.error("JPA 처리 중 오류 : id {}", id);
            } catch (Exception e) {
                log.error("token 발급 시 user 정보 이상 : id {} - {}", id, e);
            }
        }

        // Profile에 따라 Subject를 결정
        String subject = "math";
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (profile.startsWith("engl")) {
                subject = "engl";
            } else if (profile.startsWith("math")) {
                subject = "math";
            }
        }

        String claId = MapUtils.getString(paramMap, "claId", "");

        //  userSeCd가 있을 때만 claId 조회 시도  // claId가 null이거나 비어있을 경우 DB 조회 후 처리
        if (StringUtils.isNotEmpty(userSeCd) && StringUtils.isEmpty(claId)) {

            String tempUserSeCd = userSeCd.toUpperCase();
            if (tempUserSeCd.equals("T")) {
                // 교사 인 경우 tc_cla_info 테이블에서 cla_id 조회
                try {
                    claId = teacherService.getClaIdByUserId(id);
                } catch (DuplicateKeyException e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DataIntegrityViolationException e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DeadlockLoserDataAccessException
                        | CannotAcquireLockException
                        | CannotSerializeTransactionException e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (org.springframework.dao.QueryTimeoutException e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DataAccessResourceFailureException e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (BadSqlGrammarException e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DataAccessException e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (Exception e) {
                    log.error("token 발급 시 교사 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                }
            } else if (tempUserSeCd.equals("S")) {
                // 학생 인 경우 tc_cla_mb_info 테이블에서 cla_id 조회
                try {
                    claId = studentService.getClaIdByUserId(id);
                } catch (DuplicateKeyException e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DataIntegrityViolationException e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DeadlockLoserDataAccessException
                        | CannotAcquireLockException
                        | CannotSerializeTransactionException e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (org.springframework.dao.QueryTimeoutException e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DataAccessResourceFailureException e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (BadSqlGrammarException e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (DataAccessException e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                } catch (Exception e) {
                    log.error("token 발급 시 학생 아이디를 통한 claId 얻기 이상 {}", e.getMessage());
                }
            }
        }

        Long currentTimeMillis = System.currentTimeMillis();
        String timestamp = currentTimeMillis.toString();

        // Access 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(id, userSeCd, timestamp, claId, subject);

        // Refresh 토큰 생성
        String refreshToken = jwtUtil.generateRefreshToken(id, userSeCd, timestamp, claId, subject);

        // HMAC 계산 (timestamp + id + accessToken을 이용)
        // 토큰 변조에 대한 무결성 확인
        String hmac = jwtUtil.calculateHmac(timestamp + id + accessToken);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("accessToken", accessToken);
        resultMap.put("refreshToken", refreshToken);
        resultMap.put("hmac", hmac);

        return resultMap;
    }

    public Map<String, Object> upsertPassword(String userId, String rawPassword, String accountExpireDt) throws NoSuchAlgorithmException {

        // 🔎 0) 유저 존재여부 MyBatis로 선검사
        int exists = socketMapper.existsUserByUserId(userId);
        if (exists == 0) {
            // 컨트롤러에서 실패로 내려보낼 수 있게 result 코드/메시지 포함
            Map<String, Object> fail = new HashMap<>();
            fail.put("result", 102);
            fail.put("returnType", "Error - No User exists");
            fail.put("userId", userId);
            return fail;
        }

        // 1) 사용자 salt 생성
        String userSalt = passwordHashUtil.generateUserSaltBase64();
        // 2) 해시 계산 (CSAP 방식)
        String hexHash = passwordHashUtil.computeSha256Hex(userSalt, rawPassword);
        // 3) 저장 문자열 생성
        String encoded = passwordHashUtil.buildEncoded(userSalt, hexHash);
        // Asia/Seoul 기준 올해 말 23:59:59
        LocalDateTime expireDt = passwordHashUtil.parseOrDefaultEndOfYear(accountExpireDt);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("passwordBytes", encoded.getBytes(StandardCharsets.UTF_8)); // varbinary(128)
        params.put("accountExpireDt", expireDt);

        int affected = socketMapper.upsertUserAuth(params);

        Map<String, Object> result = new HashMap<>();
        result.put("affected", affected);
        result.put("userId", userId);
        result.put("accountExpireDt", expireDt.toString());
        result.put("result", 0); // 성공 코드

        return result;
    }

    private Map<String, Object> checkUserAuth(String userId, String pwd) {

        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtils.isBlank(pwd)) {
            resultMap.put("result", 0);//정식 운영 시 104
            resultMap.put("returnType", "Warn - pwd parmeter required");
            return resultMap;
        }

        Map<String, Object> authInfo = socketMapper.selectUserAuthInfo(Collections.singletonMap("userId", userId));
        if (authInfo == null) {
            resultMap.put("result", 0);//정식 운영 시 105
            resultMap.put("returnType", "Warn - current user pwd is empty");
            return resultMap;
        }

        String encodedPwd = null;
        Object pwdObj = authInfo.get("userPwd");
        if (pwdObj instanceof byte[]) {
            encodedPwd = new String((byte[]) pwdObj, StandardCharsets.UTF_8);
        } else {
            encodedPwd = pwdObj == null ? "" : pwdObj.toString(); // btye가 아니면 string
        }
        if (StringUtils.isBlank(encodedPwd)) {
            resultMap.put("result", 0);//정식 운영 시 105
            resultMap.put("returnType", "Warn - current user pwd is empty");
            return resultMap;
        }

        if (passwordHashUtil.verify(encodedPwd, pwd) == false) {
            resultMap.put("result", 103);
            resultMap.put("returnType", "Login failed: wrong password.");
            return resultMap;
        }

        LocalDateTime expireDt = null;
        Object expObj = authInfo.get("accountExpireDt");
        if (expObj == null) return null;
        if (expObj instanceof java.sql.Timestamp) {
            expireDt = ((java.sql.Timestamp) expObj).toLocalDateTime();
        }
        if (expObj instanceof LocalDateTime) {
            expireDt = (LocalDateTime) expObj;
        }
        try {
            expireDt = LocalDateTime.parse(expObj.toString().replace(" ", "T"));
        } catch (DateTimeParseException ex) {
            log.error("checkUserAuth expireDt pase error! {}", expObj);
        } catch (RuntimeException ex) {
            log.error("checkUserAuth expireDt runtime error! {}", expObj);
        }

        if (expireDt != null && LocalDateTime.now().isAfter(expireDt)) {
            resultMap.put("result", 106);
            resultMap.put("returnType", "Login failed: account expired.");
            return resultMap;
        }

        // resultMap에 아무것도 안담겨 있으면 정상
        return resultMap;
    }

}
