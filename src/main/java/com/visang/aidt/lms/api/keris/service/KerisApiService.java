package com.visang.aidt.lms.api.keris.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.visang.aidt.lms.api.assessment.mapper.StntEvalMapper;
import com.visang.aidt.lms.api.assessment.service.StntEvalService;
import com.visang.aidt.lms.api.common.mngrAction.constant.KerisLogUtils;
import com.visang.aidt.lms.api.common.mngrAction.dto.LokiLogContext;
import com.visang.aidt.lms.api.common.mngrAction.dto.MngrLogContext;
import com.visang.aidt.lms.api.common.mngrAction.service.KerisLoggerService;
import com.visang.aidt.lms.api.keris.mapper.KerisApiMapper;
import com.visang.aidt.lms.api.keris.utils.AidtWebClientSender;
import com.visang.aidt.lms.api.keris.utils.ParamOption;
import com.visang.aidt.lms.api.keris.utils.SchoolType;
import com.visang.aidt.lms.api.keris.utils.response.AidtClassInfoVo;
import com.visang.aidt.lms.api.keris.utils.response.AidtLectureInfoVo;
import com.visang.aidt.lms.api.keris.utils.response.AidtMemberInfoVo;
import com.visang.aidt.lms.api.keris.utils.response.AidtUserInfoResponse;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KerisApiService {

    @Value("${service.name}")
    private String serviceName;

    private final KerisApiMapper kerisApiMapper;

    private final AidtWebClientSender aidtWebClientSender;

    private final StntEvalService stntEvalService;

    private final KerisLogUtils kerisLogUtils;

    private final KerisLoggerService kerisLoggerService;

    private final StntEvalMapper stntEvalMapper;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Transactional(readOnly = true)
    public Map<String, Object> getUserInfo(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> userInfo = kerisApiMapper.getUserInfo(paramMap);
        if (MapUtils.isEmpty(userInfo)) {
            userInfo = new HashMap<>();
            userInfo.put("indvInfoAgreYn", "Y");
        }
        return userInfo;
    };

    @Transactional(readOnly = true)
    public Map<String, Object> getPtnInfo(Map<String, Object> paramMap) throws Exception {
        return kerisApiMapper.getPtnInfo(paramMap);
    }

    public String getPrevClaId(Map<String, Object> paramMap) throws Exception {
        return kerisApiMapper.getPrevClaId(paramMap);
    }

    public String getRegularClaExistsYn(Map<String, Object> paramMap) throws Exception {
        return kerisApiMapper.getRegularClaExistsYn(paramMap);
    }

    public void updatePtnInfo(Map<String, Object> paramMap) throws Exception {
        kerisApiMapper.updatePtnInfo(paramMap);
    }

    public Map<String, Object> saveStProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> accessTokenMap = (Map<String, Object>) paramData.getOrDefault("access_token", null);
        Map<String, Object> entrustedInfo = (Map<String, Object>) paramData.getOrDefault("entrusted_info", null);
        String apiDomain = MapUtils.getString(paramData, "api_domain", "");
        String userId = MapUtils.getString(paramData, "user_id", "");
        String userStatus = MapUtils.getString(paramData, "user_status", "");
        String userType = MapUtils.getString(paramData, "user_type", "");
        String apiVersion = MapUtils.getString(paramData, "api_version", "");
        String lectureCodeParam = MapUtils.getString(paramData, "lecture_code", "");

        Map<String, Object> userInsertMap = new HashMap<>();
        Map<String, Object> userUpdateMap = new HashMap<>();
        Map<String, Object> schlInsertMap = new HashMap<>();
        Map<String, Object> tcClaMbInfoInsertMap = new HashMap<>();

        //로그값 세팅
        kerisLoggerService.setUser(userId, userType);
        kerisLoggerService.setLectureCode(lectureCodeParam);

        String claId = "";
        try {
            //공공기관 partnerId 조회
            Map<String, Object> ptnInfo = kerisApiMapper.getPtnInfo(paramData);
            if (ptnInfo == null) {
                result.put("code", "40001");
                result.put("message", "파라메터오류:파트너 ID 조회 실패");
                return result;
            }
            String partnerId = (String) ptnInfo.getOrDefault("ptnId", "");

            // 학급 정보 세팅 (강의코드 학기정보값 제거하여 claId 셋팅)
            if (StringUtils.isNotEmpty(MapUtils.getString(paramData, "lecture_code", ""))) {
                String[] parts = MapUtils.getString(paramData, "lecture_code", "").split("_");
                if (parts.length > 1 && parts[1].length() == 5) {
                    parts[1] = parts[1].substring(0, parts[1].length() - 1);
                }
                claId = String.join("_", parts);
            }
            //학급 매핑 정보 조회 (교사가 B학급을 A학급으로 불러오기 선택시 )
            paramData.put("claId", claId);
            Map<String, Object> tcClaGroupInfo = kerisApiMapper.getTcClaGroupInfo(paramData);
            if (MapUtils.isNotEmpty(tcClaGroupInfo)) {
                claId = MapUtils.getString(tcClaGroupInfo, "originClaId", "");
            }
            //로그값 세팅
            kerisLoggerService.setClaId(claId);

            //공공기관 user 정보 조회
            JSONObject reqParam = new JSONObject();
            reqParam.put("access_token", accessTokenMap);
            reqParam.put("user_id", userId);
            ResponseEntity<AidtUserInfoResponse> userInfoResponse = this.getAidtUserInfoWithLog(apiDomain + "/aidt_userinfo/student/all", partnerId, reqParam, apiVersion, paramData);
            if (!userInfoResponse.getBody().getCode().equals("00000")) {
                result.put("code", userInfoResponse.getBody().getCode());
                result.put("message", userInfoResponse.getBody().getMessage());
                return result;
            }
            AidtUserInfoResponse userInfo = userInfoResponse.getBody();

            // 기존 등록된 사용자인지 조회
            Map<String, Object> lmsUserInfo = kerisApiMapper.getUserInfo(paramData);
            if (MapUtils.isEmpty(lmsUserInfo) || "N".equals(MapUtils.getString(lmsUserInfo, "useTermsAgreeYn", "N"))) {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", userId);
                data.put("userStatus", userStatus);
                data.put("userType", userType);
                data.put("userDivision", userInfo.getUser_division());
                data.put("userGrade", userInfo.getUser_grade());
                data.put("userClass", userInfo.getUser_class());
                data.put("userNumber", userInfo.getUser_number());
                data.put("year", LocalDate.now().getYear());
                data.put("partnerId", partnerId);
                data.put("schlCd", userInfo.getSchool_id());
                data.put("age14BlwLgrpCiNo", MapUtils.getString(entrustedInfo, "age14_blw_lgrp_ci_no", ""));
                data.put("age14BlwLgrpName", MapUtils.getString(entrustedInfo, "age14_blw_lgrp_name", ""));
                data.put("useTermsAgreeDt", MapUtils.getString(entrustedInfo, "use_terms_agree_dt", ""));
                data.put("useTermsAgreeYn", MapUtils.getString(entrustedInfo, "use_terms_agree_yn", ""));
                data.put("user_id", userId);
                if (MapUtils.isEmpty(lmsUserInfo)) {
                    userInsertMap.putAll(data);
                }
                // 학생 개인정보동의 값 업데이트 실행
                String useTermsAgreeYn = MapUtils.getString(entrustedInfo, "use_terms_agree_yn", "");
                if (StringUtils.isNotEmpty(useTermsAgreeYn)) {
                    if (!useTermsAgreeYn.equals(MapUtils.getString(lmsUserInfo, "useTermsAgreeYn", "N"))) {
                        userUpdateMap.putAll(data);
                    }
                }
            }

            if (MapUtils.isNotEmpty(lmsUserInfo) && StringUtils.equals(MapUtils.getString(lmsUserInfo, "rgtr", ""), "kerisPersonaIng")) {
                //학생 페르소나 학급ID가 처리가 안된경우
                result.put("code", "50001");
                result.put("message", "페르소나 학급ID 처리 안됨.");
                return result;
            }

            // 학교 정보 조회
            paramData.put("schlCd", userInfo.getSchool_id());
            Map<String, Object> schlInfo = kerisApiMapper.getSchlInfo(paramData);
            if (MapUtils.isEmpty(schlInfo)) {
                schlInsertMap.put("userDivision", userInfo.getUser_division());
                schlInsertMap.put("schlCd", userInfo.getSchool_id());
            }

            // 학생이 로그인 시 tc_cla_mb_info 테이블에 데이터 추가
            Map<String, Object> tcClaSearchMap = new HashMap<>();
            tcClaSearchMap.put("claId", claId);
            tcClaSearchMap.put("stdtId", userId);

            Map<String, Object> tcClaMbInfo = kerisApiMapper.getTcClaMbInfoSt(tcClaSearchMap);
            if (MapUtils.isNotEmpty(tcClaMbInfo)) {
                if (StringUtils.isEmpty(MapUtils.getString(tcClaMbInfo, "stdtId", ""))) {
                    tcClaMbInfoInsertMap.put("claId", claId);
                    tcClaMbInfoInsertMap.put("userId", MapUtils.getString(tcClaMbInfo, "userId", ""));
                    tcClaMbInfoInsertMap.put("stdtId", userId);
                    tcClaMbInfoInsertMap.put("year", LocalDate.now().getYear());
                }
            }
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }

        this.insertStInfo(userInsertMap, userUpdateMap, schlInsertMap, tcClaMbInfoInsertMap);

        result.put("code", "00000");
        result.put("message", "성공");


        return result;
    }

    public void insertStInfo(Map<String, Object> userInsertMap
            , Map<String, Object> userUpdateMap
            , Map<String, Object> schlInsertMap
            , Map<String, Object> tcClaMbInfoInsertMap) throws Exception {

        if (MapUtils.isNotEmpty(userInsertMap)) {
            // user 테이블 적재
            kerisApiMapper.insertUser(userInsertMap);
            // stdt_reg_info 테이블 적재
            kerisApiMapper.insertStdtRegInfo(userInsertMap);
        }
        //개인정보 수신동의 업데이트
        if (MapUtils.isNotEmpty(userUpdateMap)) {
            kerisApiMapper.updateUserindvInfoAgreYn(userUpdateMap);
        }
        // school 테이블 적재
        if (MapUtils.isNotEmpty(schlInsertMap)) {
            kerisApiMapper.insertSchool(schlInsertMap);
        }

        // 교사에 의해 추가되지 않은 학생이 추후에 들어오는 경우 세팅
        if (MapUtils.isNotEmpty(tcClaMbInfoInsertMap)) {
            kerisApiMapper.insertTcClaMbInfo(tcClaMbInfoInsertMap);

            // 평가, 과제 세팅
            Map<String, Object> map = new HashMap<>();
            map.put("claId", MapUtils.getString(tcClaMbInfoInsertMap, "claId", ""));
            stntEvalService.saveClassStdData(map);

            // 상점 및 상점 구매이력 세팅
            List<Map<String, Object>> listShopInsertMap = new ArrayList<>();
            Map<String, Object> shopMap = new HashMap<>();
            shopMap.put("userId", MapUtils.getString(tcClaMbInfoInsertMap, "stdtId", ""));
            shopMap.put("claId", MapUtils.getString(tcClaMbInfoInsertMap, "claId", ""));
            shopMap.put("userType", "S");
            listShopInsertMap.add(shopMap);

            // sp_prchs_hist 적재
            kerisApiMapper.insertShopSkinHist(listShopInsertMap);
            kerisApiMapper.insertShopGameHist(listShopInsertMap);
            kerisApiMapper.insertShopProfileHist(listShopInsertMap);

            // sp_prchs_info 적재
            kerisApiMapper.insertShopSkin(listShopInsertMap);
            kerisApiMapper.insertShopGame(listShopInsertMap);
            kerisApiMapper.insertShopProfile(listShopInsertMap);
        }
    }

    public Map<String, Object> saveTcProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> accessTokenMap = (Map<String, Object>) paramData.getOrDefault("access_token", null);
        Map<String, Object> entrustedInfo = (Map<String, Object>) paramData.getOrDefault("entrusted_info", null);
        String apiDomain = MapUtils.getString(paramData, "api_domain", "");
        String userId = MapUtils.getString(paramData, "user_id", "");
        String userStatus = MapUtils.getString(paramData, "user_status", "");
        String userType = MapUtils.getString(paramData, "user_type", "");
        String apiVersion = MapUtils.getString(paramData, "api_version", "");
        String lectureCodeParam = MapUtils.getString(paramData, "lecture_code", "");
        String teacherLectureCode = MapUtils.getString(paramData, "teacher_lecture_code", "");

        List<Map<String, Object>> listUserInsertMap = new ArrayList<>();
        List<Map<String, Object>> listTcRegInsertMap = new ArrayList<>();
        List<Map<String, Object>> listStdtRegInsertMap = new ArrayList<>();
        List<Map<String, Object>> listTcClaInsertMap = new ArrayList<>();
        List<Map<String, Object>> listTcClaUpdateMap = new ArrayList<>();
        List<Map<String, Object>> listTcClaMapInsertMap = new ArrayList<>();
        List<Map<String, Object>> listActvtnAtMap = new ArrayList<>();
        List<Map<String, Object>> listShopInsertMap = new ArrayList<>();
        Map<String, Object> schlInsertMap = new HashMap<>();
        Map<String, Object> tcClaUserInfoClassMap = new HashMap<>();
        String claId = "";

        //로그값 세팅
        kerisLoggerService.setUser(userId, userType);
        kerisLoggerService.setLectureCode(lectureCodeParam);

        try {
            // 공공기관 partnerId 조회
            Map<String, Object> ptnInfo = kerisApiMapper.getPtnInfo(paramData);
            if (ptnInfo == null) {
                result.put("code", "40001");
                result.put("message", "파라메터오류:파트너 ID 조회 실패");
                return result;
            }
            String partnerId = (String) ptnInfo.getOrDefault("ptnId", "");

            // 학급 정보 세팅 (강의코드 학기정보값 제거하여 claId 셋팅)
            String[] parts = lectureCodeParam.split("_");
            String smt = "";
            if (parts.length > 1 && parts[1].length() == 5) {
                smt = String.valueOf(parts[1].charAt(parts[1].length() - 1));
                parts[1] = parts[1].substring(0, parts[1].length() - 1);
            }
            claId = String.join("_", parts);
            paramData.put("claId", claId);
            paramData.put("userId", userId);

            //학급 매핑 정보 조회 (교사가 B학급을 A학급으로 불러오기 선택시)
            boolean isMapping = false;
            Map<String, Object> tcClaGroupInfo = kerisApiMapper.getTcClaGroupInfo(paramData);
            if (MapUtils.isNotEmpty(tcClaGroupInfo)) {
                claId = MapUtils.getString(tcClaGroupInfo, "originClaId", "");
                paramData.put("claId", claId);
                isMapping = true;
            }
            //로그값 세팅
            kerisLoggerService.setClaId(claId);

            // 케리스 API user 정보 조회
            JSONObject reqParam = new JSONObject();
            reqParam.put("access_token", accessTokenMap);
            reqParam.put("user_id", userId);
            reqParam.put("user_id_schdule_yn", "Y");
            ResponseEntity<AidtUserInfoResponse> userInfoResponse = this.getAidtUserInfoWithLog(apiDomain + "/aidt_userinfo/teacher/all", partnerId, reqParam, apiVersion, paramData);
            if (!userInfoResponse.getBody().getCode().equals("00000")) {
                result.put("code", userInfoResponse.getBody().getCode());
                result.put("message", userInfoResponse.getBody().getMessage());
                return result;
            }
            log.info("userInfoResponse: {}", userInfoResponse);
            AidtUserInfoResponse userInfo = userInfoResponse.getBody();
            Set<String> processedUserIds = new HashSet<>();

            // 케리스 API 호출 파라미터 세팅
            reqParam = new JSONObject();
            reqParam.put("access_token", accessTokenMap);
            reqParam.put("user_id", userId);
            reqParam.put("lecture_code", lectureCodeParam);

            // 케리스 API 강의 정보 조회
            ResponseEntity<AidtUserInfoResponse> lectureResponse = this.getAidtUserInfoWithLog(apiDomain + "/aidt_userinfo/teacher/open_subject_info", partnerId, reqParam, apiVersion, paramData);
            AidtUserInfoResponse lectureInfo = lectureResponse.getBody();

            // 케리스 API 학급구성원 정보 조회
            ResponseEntity<AidtUserInfoResponse> classMemResponse = this.getAidtUserInfoWithLog(apiDomain + "/aidt_userinfo/teacher/class_member", partnerId, reqParam, apiVersion, paramData);
            AidtUserInfoResponse classMem = classMemResponse.getBody();
            List<AidtMemberInfoVo> memberList = classMem.getMember_info();

            Map<String, Object> tcClaInfo = kerisApiMapper.getTcClaInfo(paramData);
            if (MapUtils.isEmpty(tcClaInfo)) {
                Map<String, Object> tcClaInsertMap = new HashMap<>();
                tcClaInsertMap.put("claId", claId);
                tcClaInsertMap.put("courseRmCd", lectureCodeParam);
                tcClaInsertMap.put("smt", smt);
                tcClaInsertMap.put("userId", userId);
                tcClaInsertMap.put("userGrade", lectureInfo.getUser_grade());
                tcClaInsertMap.put("year", LocalDate.now().getYear());
                tcClaInsertMap.put("teacherLectureCode", teacherLectureCode);
                listTcClaInsertMap.add(tcClaInsertMap);

                // 상점 기본 세팅
                tcClaInsertMap.put("userType", "T");
                listShopInsertMap.add(tcClaInsertMap);
            } else {
                //강의코드가 1학기에서 2학기로 변경 되었을 경우 courseRmCd 업데이트
                //교사별 강의 코드가 기존과 다를 경우 teacherLectureCode 업데이트
                if (!MapUtils.getString(tcClaInfo, "courseRmCd", "").equals(lectureCodeParam)
                        || !MapUtils.getString(tcClaInfo, "teacherLectureCode", "").equals(teacherLectureCode)
                        || !MapUtils.getString(tcClaInfo, "smt", "").equals(smt)) {
                    Map<String, Object> tcClaUpdateMap = new HashMap<>();
                    tcClaUpdateMap.put("claId", claId);
                    if (!MapUtils.getString(tcClaInfo, "teacherLectureCode", "").equals(teacherLectureCode)) {
                        tcClaUpdateMap.put("teacherLectureCode", teacherLectureCode);
                    }
                    if (!isMapping) {
                        if (!MapUtils.getString(tcClaInfo, "courseRmCd", "").equals(lectureCodeParam)) {
                            tcClaUpdateMap.put("courseRmCd", lectureCodeParam);
                        }
                        if (!MapUtils.getString(tcClaInfo, "smt", "").equals(smt)) {
                            tcClaUpdateMap.put("smt", smt);
                        }
                    }
                    listTcClaUpdateMap.add(tcClaUpdateMap);
                }
            }

            //교사 클래스 매핑 테이블
            tcClaUserInfoClassMap.put("claId", claId);
            tcClaUserInfoClassMap.put("userId", userId);

            //학급 구성원 처리
            if (!memberList.isEmpty()) {
                // API 에서 제공된 학생 ID 목록
                Set<String> apiStudentIds = memberList.stream()
                        .map(AidtMemberInfoVo::getUser_id)
                        .collect(Collectors.toSet());

                // 학생 학적정보 조회
                List<String> userIdList = memberList.stream()
                        .map(AidtMemberInfoVo::getUser_id)
                        .collect(Collectors.toList());
                paramData.put("userIdList", userIdList);
                List<Map<String, Object>> listStdtRegInfo = kerisApiMapper.listStdtRegInfo(paramData);
                Set<String> registeredStudentIds = listStdtRegInfo.stream()
                        .map(student -> (String) student.get("userId"))
                        .collect(Collectors.toSet());

                // 학급구성원 목록 조회
                List<Map<String, Object>> listTcClaMbInfo = kerisApiMapper.listTcClaMbInfo(paramData);
                Set<String> registeredStudentMbExistIds = listTcClaMbInfo.stream()
                        .map(student -> (String) student.get("userId"))
                        .collect(Collectors.toSet());

                // 학급구성원에 더 이상 존재하지 않아 삭제할 학급구성원 조회
                Set<String> classMembersToDelete = new HashSet<>(registeredStudentMbExistIds);
                classMembersToDelete.removeAll(apiStudentIds);
                String finalClaId = claId;
                classMembersToDelete.forEach(studentId -> {
                    Map<String, Object> tcClaMapInsertMap = new HashMap<>();
                    tcClaMapInsertMap.put("claId", finalClaId);
                    tcClaMapInsertMap.put("userId", userId);
                    tcClaMapInsertMap.put("stdtId", studentId);
                    tcClaMapInsertMap.put("year", LocalDate.now().getYear());
                    tcClaMapInsertMap.put("actvtnAt", "N");
                    listTcClaMapInsertMap.add(tcClaMapInsertMap);
                });

                // 학생이 다른 학급구성원으로 소속 되어있는지 조회
                List<Map<String, Object>> listOtherTcClaMbStdtInfo = kerisApiMapper.listOtherTcClaMbStdtInfo(paramData);
                Set<String> registeredStudentMbIds = listOtherTcClaMbStdtInfo.stream()
                        .map(student -> (String) student.get("userId"))
                        .collect(Collectors.toSet());

                // 학급 구성원 세팅
                for (AidtMemberInfoVo member : memberList) {
                    // 중복방지 코드
                    if (processedUserIds.contains(member.getUser_id())) {
                        continue;
                    }

                    // 학생 학정정보에 해당 studentId가 없으면 insert 대상
                    if (!registeredStudentIds.contains(member.getUser_id())) {
                        Map<String, Object> userInsertMap = new HashMap<>();
                        userInsertMap.put("userId", member.getUser_id());
                        userInsertMap.put("partnerId", partnerId);
                        userInsertMap.put("userStatus", "E");
                        userInsertMap.put("userType", "S");
                        userInsertMap.put("schlCd", lectureInfo.getSchool_id());
                        userInsertMap.put("year", LocalDate.now().getYear());
                        userInsertMap.put("userNumber", member.getUser_number());
                        listUserInsertMap.add(userInsertMap);
                        listStdtRegInsertMap.add(userInsertMap);

                        // 상점 기본 세팅
                        userInsertMap.put("claId", claId);
                        listShopInsertMap.add(userInsertMap);
                    }

                    // 학생이 다른 강의코드에 소속되어 있다면 이전 학급 활성여부 N으로 업데이트
                    if (registeredStudentMbIds.contains(member.getUser_id())) {
                        Map<String, Object> userInsertMap = new HashMap<>();
                        userInsertMap.put("stdtId", member.getUser_id());
                        String srcClaId = listOtherTcClaMbStdtInfo.stream()
                                .filter(student -> member.getUser_id().equals(student.get("userId")))
                                .map(student -> (String) student.get("claId"))
                                .findFirst()
                                .orElse("");
                        userInsertMap.put("srcClaId", srcClaId);
                        userInsertMap.put("trgtClaId", claId);
                        userInsertMap.put("userId", userId);
                        listActvtnAtMap.add(userInsertMap);
                    }

                    // 학급구성원 리스트에 해당 studentId가 없으면 insert 대상
                    if (!registeredStudentMbExistIds.contains(member.getUser_id())) {
                        Map<String, Object> tcClaMapInsertMap = new HashMap<>();
                        tcClaMapInsertMap.put("claId", claId);
                        tcClaMapInsertMap.put("userId", userId);
                        tcClaMapInsertMap.put("stdtId", member.getUser_id());
                        tcClaMapInsertMap.put("year", LocalDate.now().getYear());
                        tcClaMapInsertMap.put("actvtnAt", "Y");
                        listTcClaMapInsertMap.add(tcClaMapInsertMap);
                    }
                    processedUserIds.add(member.getUser_id());
                }
            }

            //기존 등록된 교사인지 조회
            Map<String, Object> lmsUserInfo = kerisApiMapper.getUserInfo(paramData);
            if (MapUtils.isEmpty(lmsUserInfo)) {
                Map<String, Object> userInsertMap = new HashMap<>();
                userInsertMap.put("userId", userId);
                userInsertMap.put("userStatus", userStatus);
                userInsertMap.put("userType", userType);
                userInsertMap.put("year", LocalDate.now().getYear());
                userInsertMap.put("partnerId", partnerId);
                userInsertMap.put("schlCd", userInfo.getSchool_id());
                userInsertMap.put("age14BlwLgrpCiNo", MapUtils.getString(entrustedInfo, "age14_blw_lgrp_ci_no", ""));
                userInsertMap.put("age14BlwLgrpName", MapUtils.getString(entrustedInfo, "age14_blw_lgrp_name", ""));
                userInsertMap.put("useTermsAgreeDt", MapUtils.getString(entrustedInfo, "use_terms_agree_dt", ""));
                userInsertMap.put("useTermsAgreeYn", MapUtils.getString(entrustedInfo, "use_terms_agree_yn", ""));
                listUserInsertMap.add(userInsertMap);
                listTcRegInsertMap.add(userInsertMap);
            }

            //학교 정보 조회
            paramData.put("schlCd", userInfo.getSchool_id());
            Map<String, Object> schlInfo = kerisApiMapper.getSchlInfo(paramData);
            if (MapUtils.isEmpty(schlInfo)) {
                //교사일경우 userDivision 응답이 없어, 학교 이름으로 구분
                int userDivision = SchoolType.getDivisionBySuffix(userInfo.getSchool_name());
                schlInsertMap.put("userDivision", userDivision);
                schlInsertMap.put("schlCd", userInfo.getSchool_id());
            }
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }

        this.insertTcInfo(listUserInsertMap, listTcRegInsertMap, listStdtRegInsertMap
                , listTcClaInsertMap, listTcClaUpdateMap, listActvtnAtMap
                , listTcClaMapInsertMap, schlInsertMap, listShopInsertMap
                , tcClaUserInfoClassMap);
        result.put("code", "00000");
        result.put("message", "성공");
        return result;
    }

    public void insertTcInfo(
            List<Map<String, Object>> listUserInsertMap
            , List<Map<String, Object>> listTcRegInsertMap
            , List<Map<String, Object>> listStdtRegInsertMap
            , List<Map<String, Object>> listTcClaInsertMap
            , List<Map<String, Object>> listTcClaUpdateMap
            , List<Map<String, Object>> listActvtnAtMap
            , List<Map<String, Object>> listTcClaMapInsertMap
            , Map<String, Object> schlInsertMap
            , List<Map<String, Object>> listShopInsertMap
            , Map<String, Object> tcClaUserInfoClassMap
    ) throws Exception {

        //user 테이블 적재
        if (CollectionUtils.isNotEmpty(listUserInsertMap)) {
            kerisApiMapper.insertUserBulk(listUserInsertMap);
        }
        //tc_reg_info 테이블 적재
        if (CollectionUtils.isNotEmpty(listTcRegInsertMap)) {
            kerisApiMapper.insertTcRegInfoBulk(listTcRegInsertMap);
        }
        //stdt_reg_info 테이블 적재
        if (CollectionUtils.isNotEmpty(listStdtRegInsertMap)) {
            kerisApiMapper.insertStdtRegInfoBulk(listStdtRegInsertMap);
        }
        //tc_cla_info 테이블 적재
        if (CollectionUtils.isNotEmpty(listTcClaInsertMap)) {
            kerisApiMapper.insertTcClaInfoBulk(listTcClaInsertMap);
        }
        //tc_cla_info 강의코드가 1학기에서 2학기로 변경 되었을 경우 courseRmCd 업데이트
        // + 교사별 강의 코드 teacherLectureCode 추가로 업데이트
        if (CollectionUtils.isNotEmpty(listTcClaUpdateMap)) {
            kerisApiMapper.updateTcClaInfoBulk(listTcClaUpdateMap);
        }
        //교사 클래스 매핑 테이블 적재
        if (MapUtils.isNotEmpty(tcClaUserInfoClassMap)) {
            kerisApiMapper.upsertTcClaUserInfo(tcClaUserInfoClassMap);
        }
        //tc_cla_mb_info 테이블 actvtn_at 업데이트
        if (CollectionUtils.isNotEmpty(listActvtnAtMap)) {
            List<String> userIdList = listActvtnAtMap.stream()
                    .map(map -> (String) map.get("stdtId"))
                    .collect(Collectors.toList());
            Map<String, Object> paramData = new HashMap<>();
            paramData.put("userIdList", userIdList);
            kerisApiMapper.updateTcClaMbInfoOutBulk(paramData);
        }
        //반 이동 학생 과제,평가,상점,리워드 처리
        if (CollectionUtils.isNotEmpty(listActvtnAtMap)) {
            for (Map<String, Object> data : listActvtnAtMap) {
                String stdtId = MapUtils.getString(data, "stdtId", "");
                String srcClaId = MapUtils.getString(data, "srcClaId", "");
                String trgtClaId = MapUtils.getString(data, "trgtClaId", "");

                Map<String, Object> param = new HashMap<>();
                param.put("userId", stdtId);
                param.put("oldClaId", srcClaId);
                param.put("newClaId", trgtClaId);

                log.info("param: {}", param.toString());
                stntEvalService.modifyClassMoveStdDataChange(param);
            }
        }
        //tc_cla_mb_info 테이블 적재
        if (CollectionUtils.isNotEmpty(listTcClaMapInsertMap)) {
            kerisApiMapper.insertTcClaMbInfoBulk(listTcClaMapInsertMap);

            //claId 추출 후 평가/과제 세팅
            List<Map<String, Object>> uniqueList = listTcClaMapInsertMap.stream()
                    .collect(Collectors.toMap(
                            map -> map.get("claId"),
                            map -> map,
                            (existing, replacement) -> existing))
                    .values()
                    .stream()
                    .collect(Collectors.toList());
            for (Map<String, Object> map : uniqueList) {
                stntEvalService.saveClassStdData(map);
            }
        }
        // school 테이블 적재
        if (MapUtils.isNotEmpty(schlInsertMap)) {
            kerisApiMapper.insertSchool(schlInsertMap);
        }
        // sp_prchs_hist 테이블 적재 후 sp_prchs_info 디폴트 값으로 적재 (게임, 스킨, 프로필)
        if (CollectionUtils.isNotEmpty(listShopInsertMap)) {
            // 학생이 새로 등록된 case, 학적정보는 있으나 학급구성원이 없는 case에서 중복 삽입 방지 추가
            List<Map<String, Object>> distinctList = new ArrayList<>(
                    new LinkedHashSet<>(listShopInsertMap)
            );

            // sp_prchs_hist 적재
            kerisApiMapper.insertShopSkinHist(distinctList);
            kerisApiMapper.insertShopGameHist(distinctList);
            kerisApiMapper.insertShopProfileHist(distinctList);

            // sp_prchs_info 적재
            kerisApiMapper.insertShopSkin(distinctList);
            kerisApiMapper.insertShopGame(distinctList);
            kerisApiMapper.insertShopProfile(distinctList);
        }
    }

    public ResponseEntity<AidtUserInfoResponse> getAidtUserInfo(String url, String partnerId, JSONObject reqParam, String apiVersion) throws Exception {
        ParamOption option = ParamOption.builder()
                .url(url)
                .method(HttpMethod.POST)
                .request(reqParam)
                .partnerId(partnerId)
                .apiVersion(apiVersion)
                .build();
        ParameterizedTypeReference<AidtUserInfoResponse> typeReference = new ParameterizedTypeReference<>() {};
        ResponseEntity<AidtUserInfoResponse> response = aidtWebClientSender.sendWithBlock(option, typeReference);

        if (HttpStatus.OK == response.getStatusCode()) {
            return response;
        } else {
            Optional<Throwable> throwable = Optional.ofNullable(response.getBody().getThrowable());
            throw throwable.map(Exception::new).orElseGet(() -> new Exception(response.toString()));
        }
    }

    public ResponseEntity<AidtUserInfoResponse> getAidtUserInfoWithLog(String url, String partnerId, JSONObject reqParam, String apiVersion, Map<String, Object> paramData) throws Exception {
        String sTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        ParamOption option = ParamOption.builder()
                .url(url)
                .method(HttpMethod.POST)
                .request(reqParam)
                .partnerId(partnerId)
                .apiVersion(apiVersion)
                .build();


        ParameterizedTypeReference<AidtUserInfoResponse> typeReference = new ParameterizedTypeReference<>() {};
        ResponseEntity<AidtUserInfoResponse> response = aidtWebClientSender.sendWithBlock(option, typeReference);

        //로그전송
        try {
            log.info("kerisNatsLogSend");
            this.kerisNatsLogSend(url, sTime, paramData, reqParam, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("kerisNatsLogSend err: {}", e.getMessage());
        }

        if (HttpStatus.OK == response.getStatusCode()) {
            return response;
        } else {
            Optional<Throwable> throwable = Optional.ofNullable(response.getBody().getThrowable());
            throw throwable.map(Exception::new).orElseGet(() -> new Exception(response.toString()));
        }

    }

    public void kerisNatsLogSend(String url, String sTime, Map<String, Object> paramData, JSONObject reqParam, ResponseEntity<AidtUserInfoResponse> response) throws Exception {
        String userId = MapUtils.getString(paramData, "user_id", "");
        String userSeCd = MapUtils.getString(paramData, "user_type", "");
        String apiDomain = MapUtils.getString(paramData, "api_domain", "");
        String lectureCode = "";
        String claId = "";

        AidtUserInfoResponse body = Optional.ofNullable(response)
                .map(ResponseEntity::getBody)
                .orElse(null);

        Object respObj = Optional.ofNullable(body)
                .map(b -> {
                    if (b.getThrowable() != null) {
                        Throwable t = b.getThrowable();
                        Map<String, Object> errMap = new HashMap<>();
                        errMap.put("exception", t.getClass().getName());
                        errMap.put("message", t.getMessage());
                        errMap.put("stackTrace", Arrays.stream(t.getStackTrace())
                                .limit(5)
                                .map(StackTraceElement::toString)
                                .collect(Collectors.toList()));
                        return errMap;
                    }
                    return b;
                })
                .orElse(response);

        String respJson = kerisLogUtils.writeValueAsString(respObj);

        String uName = Optional.ofNullable(body)
                .map(AidtUserInfoResponse::getUser_name)
                .filter(s -> !s.isBlank())
                .orElse("");

        String schlNm = Optional.ofNullable(body)
                .map(AidtUserInfoResponse::getSchool_name)
                .filter(s -> !s.isBlank())
                .orElse("");

        //Mngr 로그전송
        //Custom 데이터가 있을경우
        String ctxUserId = kerisLoggerService.getUserId();
        if (StringUtils.isNotEmpty(ctxUserId)) {
            userId = ctxUserId;
        }
        String ctxUserSeCd = kerisLoggerService.getUserSeCd();
        if (StringUtils.isNotEmpty(ctxUserSeCd)) {
            userSeCd = ctxUserSeCd;
        }
        String ctxLectureCode = kerisLoggerService.getLectureCode();
        if (StringUtils.isNotEmpty(ctxLectureCode)) {
            lectureCode = ctxLectureCode;
        }
        String ctxClaId = kerisLoggerService.getClaId();
        if (StringUtils.isNotEmpty(ctxClaId)) {
            claId = ctxClaId;
        }

        String typeCd = url.replace(apiDomain, "");
        JsonNode respon = kerisLogUtils.safeParseJson(respJson);
        kerisLogUtils.maskNode(respon);
        MngrLogContext mngrLogContext = MngrLogContext.builder()
                .typeCd(typeCd)
                .summary("공공포털 통합인증 API " + typeCd)
                .userId(userId)
                .userSeCd(userSeCd)
                .service(serviceName)
                .url(url)
                .req(kerisLogUtils.safeParseJson(reqParam.toString()))
                .resp(respon)
                .claId(claId)
                .lectureCode(lectureCode)
                .build();
        kerisLoggerService.logSendMngr(mngrLogContext);


        //로키 로그전송
        String eTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String hash = (response.toString() != null && !response.toString().isEmpty()) ? kerisLogUtils.sha256(response.toString()) : null;
        LokiLogContext lokiLogContext = LokiLogContext.builder()
                .uuid(userId)
                .uType(userSeCd)
                .appName("keris-call")
                .profile(kerisLogUtils.getProfileBasedLogMessage())
                .url(url)
                .req(kerisLogUtils.safeParseJson(reqParam.toString()))
                .resp(kerisLogUtils.safeParseJson(respJson))
                .sTime(sTime)
                .eTime(eTime)
                .uName(uName)
                .schlNm(schlNm)
                .hash(hash)
                .build();
        kerisLoggerService.logSendLoki(lokiLogContext);
    }

    public Map<String, Object> saveTransferProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "00000");
        result.put("message", "성공");
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) paramData.getOrDefault("data", null);
        if (CollectionUtils.isEmpty(dataList)) {
            result.put("code", "20004");
            result.put("message", "전입 데이터가 없음");
            return result;
        }
        String userId = MapUtils.getString(paramData, "user_id", "");
        for (Map<String, Object> data : dataList) {
            data.put("user_id", userId);
        }

        try {
            this.insertTransferSt(dataList, userId);
        } catch (Exception e) {
            log.error("err:", e);
            result.put("code", "50001");
            result.put("message", "시스템 오류");
            return result;
        }

        return result;
    }

    public void insertTransferSt(List<Map<String, Object>> dataList,
                                 String userId) throws Exception {
        // aidt_nw_stdt_info 테이블 초기화
        kerisApiMapper.deleteAidtNwStdtInfo(userId);
        // aidt_nw_stdt_info 테이블 적재
        for (Map<String, Object> data : dataList) {
            kerisApiMapper.insertAidtNwStdtInfo(data);
        }
    }

    public Map<String, Object> getUserStudyInfo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String userId = MapUtils.getString(paramData, "user_id", "");
        result.put("user_id", userId);
        result.put("code", "00000");
        result.put("message", "성공");

        Map<String, Object> ptnInfo = kerisApiMapper.getPtnInfo(paramData);
        String curriSchool = MapUtils.getString(ptnInfo, "curriSchool", "");
        String curriSubject = MapUtils.getString(ptnInfo, "curriSubject", "");

        Map<String, Object> tcInfo = kerisApiMapper.getUserTcInfo(paramData);
        paramData.put("tcId", MapUtils.getString(tcInfo, "tcId", ""));
        paramData.put("claId", MapUtils.getString(tcInfo, "claId", ""));
        // 경과도
        List<Map<String, Object>> userPercentInfo = kerisApiMapper.getUserStudyInfo(paramData);
        // 성취도
        List<Map<String, Object>> userStudyInfo = new ArrayList<>();

        if (StringUtils.equals(curriSubject, "mathematics")) {
            userStudyInfo = kerisApiMapper.getUserMathStudyInfo(paramData);
        } else if (StringUtils.equals(curriSubject, "english")) {
            userStudyInfo = kerisApiMapper.getUserEnglStudyInfo(paramData);
        } else {
            result.put("code", "40001");
            result.put("message", "과목 오류");
            return result;
        }

        if (MapUtils.isEmpty(ptnInfo)) {
            result.put("code", "40001");
            result.put("message", "파라메터오류:파트너 ID 조회 실패");
            return result;
        }

        //성취도 및 학습이력 없을 경우.
        if (CollectionUtils.isEmpty(userPercentInfo) || CollectionUtils.isEmpty(userStudyInfo)) {
            result.put("data", new ArrayList<>());
            result.put("count", 0);
            return result;
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Map<String, Object>> tempMap = new HashMap<>();
        List<String> percentList = new ArrayList<>();
        // 가공해야하는 표준체계만 추출(진도율에 존재하는 표준체계 기준)
        for (Map<String, Object> percentMap : userPercentInfo) {
            percentList.add(MapUtils.getString(percentMap, "curriculum", ""));
        }

        // 성취도의 경우 교과서, 평가, 자기주도학습 등 여러가지 활동을 통해 학습된 표준체계 별 이해도 점수가 있음
        // 그 중 교과서와 관련된(현재까지 진도를 나간 표준체계들로만 추출하여야 함)
        for (Map<String, Object> map : userStudyInfo) {
            String curriculums = MapUtils.getString(map, "val1", "");
            // #^|로 묶여있는 경우(복수건)
            if (curriculums.contains("#^|")) {
                String[] curriculum = curriculums.split("\\#\\^\\|");
                for (String splitCurriculum : curriculum) {
                    if (!percentList.contains(splitCurriculum)) continue;

                    Map<String, Object> splitMap = tempMap.get(splitCurriculum);
                    if (MapUtils.isEmpty(splitMap)) {
                        splitMap = new HashMap<>();
                        splitMap.put("curriculum", splitCurriculum);
                        splitMap.put("usd_scr", MapUtils.getDouble(map, "usd_scr", 0D));
                        splitMap.put("cnt", 1);
                    } else {
                        double usdScr = MapUtils.getDouble(map, "usd_scr", 0D);
                        int cnt = MapUtils.getInteger(map, "cnt", 0) + 1;

                        usdScr = (usdScr + MapUtils.getDouble(splitMap, "usd_scr", 0D)) / cnt;
                        splitMap.put("usd_scr", usdScr);
                        splitMap.put("cnt", cnt);
                    }
                    tempMap.put(splitCurriculum, splitMap);
                }
            } else {
                // #^|로 묶여있지 않은 경우(단건)
                if (!percentList.contains(curriculums)) continue;

                Map<String, Object> itemMap = tempMap.get(curriculums);
                if (MapUtils.isEmpty(itemMap)) {
                    itemMap = new HashMap<>();
                    itemMap.put("curriculum", curriculums);
                    itemMap.put("usd_scr", MapUtils.getDouble(map, "usd_scr", 0D));
                    itemMap.put("cnt", 1);
                } else {
                    double usdScr = MapUtils.getDouble(itemMap, "usd_scr", 0D) + MapUtils.getDouble(map, "usd_scr", 0D);
                    int cnt = MapUtils.getInteger(itemMap, "cnt", 0) + 1;

                    itemMap.put("usd_scr", usdScr);
                    itemMap.put("cnt", cnt);
                }
                tempMap.put(curriculums, itemMap);
            }
        }

        for (Map<String, Object> map2 : userPercentInfo) {
            Map<String, Object> resultMap = new HashMap<>();
            String curriculum = MapUtils.getString(map2, "curriculum", "");
            Map<String, Object> resultTempMap = tempMap.get(curriculum);
            double usdScr = MapUtils.getDouble(resultTempMap, "usd_scr", 0D);
            int cnt = MapUtils.getInteger(resultTempMap, "cnt", 0);

            resultMap.put("curriculum", curriculum);
            resultMap.put("achievement_level", this.calAchvLvl(curriSchool, usdScr / cnt));
            resultMap.put("percent", MapUtils.getString(map2, "percent", "0"));

            resultList.add(resultMap);
        }

        // 타 학교로 전학간 학생 전학 처리
        kerisApiMapper.updateStdtRegInfoOut(userId);
        kerisApiMapper.updateTcClaMbInfoOut(userId);

        result.put("data", resultList);
        result.put("count", resultList.size());

        return result;
    }

    public String calAchvLvl(String school, double score) {
        if (StringUtils.equals(school, "elementary")) {
            // 초등의 경우는 성취수준을 3단계로 분류
            if (score >= 80) {
                return "A";
            } else if (score >= 60) {
                return "B";
            } else {
                return "C";
            }
        } else {
            // 중, 고등의 경우는 성취수준을 5단계로 분류
            if (score >= 90) {
                return "A";
            } else if (score >= 80) {
                return "B";
            } else if (score >= 70) {
                return "C";
            } else if (score >= 60) {
                return "D";
            } else {
                return "E";
            }
        }
    }

    public void personaUserUpdate(Map<String, Object> paramData) throws Exception {
        String userType = MapUtils.getString(paramData, "user_type", "");

        String[] parts = MapUtils.getString(paramData, "lecture_code", "").split("_");
        if (parts.length > 1 && parts[1].length() == 5) {
            parts[1] = parts[1].substring(0, parts[1].length() - 1);
        }
        String targetClaId = String.join("_", parts);
        String originClaId = "";
        int textbkId = 0;

        if (StringUtils.equals(userType, "S") || StringUtils.equals(userType, "T")) {
            originClaId = kerisApiMapper.getClaIdFromTcClaMbInfo(paramData);
            if (StringUtils.equals(userType, "T")) {
                textbkId = kerisApiMapper.getTextbkIdFromTcTextbook(paramData);
            }
        } else {
            log.info("err userType : {}", userType);
        }

        try {
            Map<String, Object> insertMap = new HashMap<>();
            insertMap.put("originClaId", originClaId);
            insertMap.put("targetClaId", targetClaId);
            insertMap.put("textbkId", textbkId);
            insertMap.put("lectureCode", MapUtils.getString(paramData, "lecture_code", ""));
            insertMap.put("user_id", MapUtils.getString(paramData, "user_id", ""));
            insertMap.put("targetRgtr", MapUtils.getString(paramData, "targetRgtr", ""));

            if (StringUtils.isNotEmpty(originClaId)) {
                kerisApiMapper.updatePersonaClaIdUpdate(insertMap);
            }

            kerisApiMapper.updatePersonaUserUpdate(insertMap);
        } catch (Exception e) {
            log.info("err persona update / origin: {} , target: {} , msg : {}", originClaId, targetClaId, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> lectureList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        Map<String, Object> accessTokenMap = (Map<String, Object>) paramData.getOrDefault("access_token", null);
        String apiDomain = MapUtils.getString(paramData, "api_domain", "");
        String userId = MapUtils.getString(paramData, "user_id", "");
        String apiVersion = MapUtils.getString(paramData, "api_version", "");
        String partnerId = MapUtils.getString(paramData, "partner_id", "");
        String lectureCodeParam = MapUtils.getString(paramData, "lecture_code", "");
        String userType = MapUtils.getString(paramData, "user_type", "");

        //로그값 세팅
        kerisLoggerService.setUser(userId, userType);
        kerisLoggerService.setLectureCode(lectureCodeParam);

        resultData.put("code", "00000");
        resultData.put("message", "성공");
        resultData.put("api_version", apiVersion);
        resultData.put("lecture_info", new ArrayList<>());

        try {

            // 교사 강의코드 목록 조회
            List<Map<String, Object>> listClaInfo = kerisApiMapper.listClaInfo(paramData);
            Map<String, Map<String, Object>> courseRmCdToClaInfoMap = listClaInfo.stream()
                    .collect(Collectors.toMap(
                            claInfo -> (String) claInfo.get("courseRmCd"),
                            claInfo -> claInfo,
                            (existing, replacement) -> existing // 중복 키 처리 방식
                    ));
            if (listClaInfo.size() == 1) {
                String claId = (String) listClaInfo.get(0).getOrDefault("claId", "");
                //로그값 세팅
                kerisLoggerService.setClaId(claId);
            }

            // 공공기관 user 정보 조회
            JSONObject reqParam = new JSONObject();
            reqParam.put("access_token", accessTokenMap);
            reqParam.put("user_id", userId);
            reqParam.put("user_id_schdule_yn", "Y");
            ResponseEntity<AidtUserInfoResponse> userInfoResponse = this.getAidtUserInfo(apiDomain + "/aidt_userinfo/teacher/all", partnerId, reqParam, apiVersion);
            if (!userInfoResponse.getBody().getCode().equals("00000")) {
                resultData.put("code", userInfoResponse.getBody().getCode());
                resultData.put("message", userInfoResponse.getBody().getMessage());
                return resultData;
            }

            AidtUserInfoResponse userInfo = userInfoResponse.getBody();
            List<AidtLectureInfoVo> lectureInfo = Objects.requireNonNull(userInfo.getLecture_info());
            Map<String, Object> claIdMap = new HashMap<>();
            Iterator<AidtLectureInfoVo> iterator = lectureInfo.iterator();
            while (iterator.hasNext()) {
                AidtLectureInfoVo data = iterator.next();
                String claId = data.getLecture_code();

                // 강의코드 중복 체크
                if (!claIdMap.containsKey(claId)) {
                    claIdMap.put(claId, claId);
                } else {
                    iterator.remove();
                    continue;
                }

                // 매핑된 강의 정보 가져오기
                Map<String, Object> matchedClaInfo = courseRmCdToClaInfoMap.get(claId);
                if (matchedClaInfo == null) {
                    iterator.remove();
                    continue;
                }

                // 매핑된 claId로 교체
                String updatedClaId = (String) matchedClaInfo.get("claId");
                if (updatedClaId != null) {
                    data.setLecture_code(updatedClaId);
                }
                String pasteYn = (String) matchedClaInfo.get("pasteYn");
                data.setPaste_yn(pasteYn);
            }
            resultData.put("lecture_info", lectureInfo);
        } catch (Exception e) {
            log.error("err:", e);
            resultData.put("code", "50001");
            resultData.put("message", e.getMessage());
            return resultData;
        }
        return resultData;
    }

    public Map<String, Object> getUserTypeAndStatus(Map<String, Object> paramData) {
        return kerisApiMapper.getUserTypeAndStatus(paramData);
    }

    public static void main(String arsg[]) {
        String lectureCode = "4V100000207_20251_0000Q001";
         String[] parts = lectureCode.split("_");
        if (parts.length > 1 && parts[1].length() == 5) {
            char lastChar = parts[1].charAt(parts[1].length() - 1);
            parts[1] = parts[1].substring(0, parts[1].length() - 1);
        }
    }

    @Transactional
    public Map<String, Object> saveTcPrevProc(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> accessTokenMap = (Map<String, Object>) paramData.getOrDefault("access_token", null);
        String apiDomain = MapUtils.getString(paramData, "api_domain", "");
        String userId = MapUtils.getString(paramData, "user_id", "");
        String userStatus = "E";
        String userType = "T";
        String apiVersion = MapUtils.getString(paramData, "api_version", "");

        List<Map<String, Object>> listUserInsertMap = new ArrayList<>();
        List<Map<String, Object>> listTcRegInsertMap = new ArrayList<>();
        List<Map<String, Object>> listTcClaInsertMap = new ArrayList<>();
        List<Map<String, Object>> listShopInsertMap = new ArrayList<>();
        Map<String, Object> schlInsertMap = new HashMap<>();
        Map<String, Object> tcClaUserInfoClassMap = new HashMap<>();
        String prevClaId = "";
        String claExistsYn = "";
        try {
            // 공공기관 partnerId 조회
            Map<String, Object> ptnInfo = kerisApiMapper.getPtnInfo(paramData);
            if (ptnInfo == null) {
                result.put("code", "40001");
                result.put("message", "파라메터오류:파트너 ID 조회 실패");
                return result;
            }
            String partnerId = (String) ptnInfo.getOrDefault("ptnId", "");

            //본강의 존재 여부
            claExistsYn = this.getRegularClaExistsYn(paramData);

            // 공공기관 user 정보 조회
            JSONObject reqParam = new JSONObject();
            reqParam.put("access_token", accessTokenMap);
            reqParam.put("user_id", userId);
            ResponseEntity<AidtUserInfoResponse> userInfoResponse = this.getAidtUserInfoWithLog(apiDomain + "/aidt_userinfo/teacher/all", partnerId, reqParam, apiVersion, paramData);
            if (!userInfoResponse.getBody().getCode().equals("00000")) {
                result.put("code", userInfoResponse.getBody().getCode());
                result.put("message", userInfoResponse.getBody().getMessage());
                return result;
            }
            log.info("userInfoResponse: {}", userInfoResponse);
            AidtUserInfoResponse userInfo = userInfoResponse.getBody();

            //임시 강의 조회
            prevClaId = this.getPrevClaId(paramData);
            if (StringUtils.isEmpty(prevClaId) && claExistsYn.equals("N")) {
                Map<String, Object> tcClaInsertMap = new HashMap<>();
                prevClaId = "prev-"+ CommonUtils.encryptString(userId).substring(0,25);
                tcClaInsertMap.put("claId", prevClaId);
                tcClaInsertMap.put("courseRmCd", "-");
                tcClaInsertMap.put("userId", userId);
                tcClaInsertMap.put("userGrade", "-");
                tcClaInsertMap.put("rgtr", "preview");
                tcClaInsertMap.put("year", LocalDate.now().getYear());
                listTcClaInsertMap.add(tcClaInsertMap);

                //교사 클래스 매핑 테이블
                tcClaUserInfoClassMap.put("claId", prevClaId);
                tcClaUserInfoClassMap.put("userId", userId);
                
                // 상점 기본 세팅
                tcClaInsertMap.put("userType", "T");
                listShopInsertMap.add(tcClaInsertMap);
            }

            //기존 등록된 교사인지 조회
            Map<String, Object> lmsUserInfo = kerisApiMapper.getUserInfo(paramData);
            if (MapUtils.isEmpty(lmsUserInfo)) {
                Map<String, Object> userInsertMap = new HashMap<>();
                userInsertMap.put("userId", userId);
                userInsertMap.put("userStatus", userStatus);
                userInsertMap.put("userType", userType);
                userInsertMap.put("year", LocalDate.now().getYear());
                userInsertMap.put("partnerId", partnerId);
                userInsertMap.put("schlCd", userInfo.getSchool_id());
                listUserInsertMap.add(userInsertMap);
                listTcRegInsertMap.add(userInsertMap);
            }

            //학교 정보 조회
            paramData.put("schlCd", userInfo.getSchool_id());
            Map<String, Object> schlInfo = kerisApiMapper.getSchlInfo(paramData);
            if (MapUtils.isEmpty(schlInfo)) {
                //교사일경우 userDivision 응답이 없어, 학교 이름으로 구분
                int userDivision = SchoolType.getDivisionBySuffix(userInfo.getSchool_name());
                schlInsertMap.put("userDivision", userDivision);
                schlInsertMap.put("schlCd", userInfo.getSchool_id());
            }
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }

        this.insertTcInfo(listUserInsertMap, listTcRegInsertMap, null
                , listTcClaInsertMap, null, null
                , null, schlInsertMap, listShopInsertMap
                , tcClaUserInfoClassMap);

        result.put("code", "00000");
        result.put("message", "성공");
        result.put("prevClaId", prevClaId);
        result.put("claExistsYn", claExistsYn);
        return result;
    }

    @Transactional
    public Map<String, Object> pasteClaId(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();

        String prevClaId = kerisApiMapper.selectPrevClaId(paramData);
        List<String> trgtClaIdList = (List<String>) paramData.get("trgtClaIdList");
        if (trgtClaIdList.size() == 0) {
            result.put("code", "40001");
            result.put("message", "파라메터오류:trgtClaIdList");
            return result;
        }

        if (CollectionUtils.isNotEmpty(trgtClaIdList)) {
            Map<String, Object> pasteMap = new HashMap<>();
            pasteMap.put("tcId", MapUtils.getString(paramData, "userId", ""));
            pasteMap.put("prevClaId", prevClaId);

            for (String trgtClaId : trgtClaIdList) {
                pasteMap.put("trgtClaId", trgtClaId);
                List<String> stdtList = kerisApiMapper.selectStdtList(pasteMap);
                pasteMap.put("oldClaId", prevClaId);
                pasteMap.put("newClaId", trgtClaId);

                // tc_textbook
                kerisApiMapper.pasteTcTextbook(pasteMap);
                // tc_curriculum
                kerisApiMapper.pasteTcCurriculum(pasteMap);
                // tab_info
                kerisApiMapper.pasteTabInfo(pasteMap);

                // 평가, 과제 마스터테이블 세팅
                kerisApiMapper.pasteEvlInfo(pasteMap);
                kerisApiMapper.pasteTaskInfo(pasteMap);

                // 평가-아티클 매핑 테이블 세팅
                this.pasteEvlIemInfo(pasteMap);

                // 학생 별 평가, 과제 문항 세팅
                for (String stdtId : stdtList) {
                    pasteMap.put("userId", stdtId);
                    // 평가 세팅
                    stntEvalMapper.saveClassMoveStdDataChange_evlResultInfo(pasteMap);
                    stntEvalMapper.saveClassMoveStdDataChange_evlResultDetail(pasteMap);

                    // 과제 세팅
                    stntEvalMapper.saveClassMoveStdDataChange_taskResultInfo(pasteMap);
                    stntEvalMapper.saveClassMoveStdDataChange_taskResultDetail(pasteMap);
                }
            }
        }

        result.put("code", "00000");
        result.put("message", "성공");
        return result;
    }

    public void pasteEvlIemInfo(Map<String, Object> param) throws Exception {
        Map<String, Object> map = new HashMap<>();
        // 임시 학급의 evlId 조회
        map.put("claId", MapUtils.getString(param, "prevClaId", ""));
        List<Integer> prevEvlIdList = kerisApiMapper.selectEvlIdList(map);
        if (prevEvlIdList.size() == 0) {
            return;
        }

        // 대상 학급의 evlId 조회
        map.put("claId", MapUtils.getString(param, "trgtClaId", ""));
        List<Integer> trgtEvlIdList = kerisApiMapper.selectEvlIdList(map);

        if (prevEvlIdList.size() != trgtEvlIdList.size()) {
            throw new RuntimeException("err : 이관 오류");
        }

        Map<Integer, Integer> evlIdKeys = new HashMap<>();
        for (int i=0; i < prevEvlIdList.size(); i++) {
            evlIdKeys.put(prevEvlIdList.get(i), trgtEvlIdList.get(i));
        }

        // 복사할 데이터를 이관 데이터로 변경
        map.put("evlIdList", prevEvlIdList);
        List<Map<String, Object>> evlIemInfoList = kerisApiMapper.selectEvlIemInfo(map);
        for (Map<String, Object> evlIemInfoMap : evlIemInfoList) {
            evlIemInfoMap.put("evl_id", evlIdKeys.get(MapUtils.getInteger(evlIemInfoMap, "evl_id", 0)));
        }
        map.put("evlIemInfoList", evlIemInfoList);
        kerisApiMapper.insertEvlIemInfoBulk(map);
    }

    public void saveTeacherInfo(Map<String, Object> paramData) throws Exception {
        String userId = MapUtils.getString(paramData, "user_id", "");

        try {
            Map<String, Object> accessTokenMap = (Map<String, Object>) paramData.getOrDefault("access_token", null);
            String apiDomain = MapUtils.getString(paramData, "api_domain", "");
            String apiVersion = MapUtils.getString(paramData, "api_version", "");

            int successCount = 0;
            int failCount = 0;

            if (StringUtils.equals(serverEnv, "local") == false) {
                // 1. 파트너 정보 조회
                Map<String, Object> ptnInfo = kerisApiMapper.getPtnInfo(paramData);
                if (ptnInfo == null) {
                    throw new RuntimeException("파트너 ID 조회 실패");
                }
                String partnerId = (String) ptnInfo.getOrDefault("ptnId", "");

                // 2. 선생님 학급 정보 API 호출
                JSONObject classReqParam = new JSONObject();
                classReqParam.put("access_token", accessTokenMap);
                classReqParam.put("user_id", userId);

                ResponseEntity<AidtUserInfoResponse> classInfoResponse =
                        this.getAidtUserInfoWithLog(apiDomain + "/aidt_userinfo/teacher/all", partnerId, classReqParam, apiVersion, paramData);

                AidtUserInfoResponse classInfo = classInfoResponse.getBody();

                if (classInfo == null || !classInfo.getCode().equals("00000")) {
                    String errorCode = classInfo != null ? classInfo.getCode() : "unknown";
                    String errorMessage = classInfo != null ? classInfo.getMessage() : "응답 없음";
                    throw new RuntimeException("외부 API 호출 실패 - code: " + errorCode + ", message: " + errorMessage);
                }

                // 3. 학급 정보 저장 처리
                if (classInfo.getClass_info() == null || classInfo.getClass_info().isEmpty()) {
                    log.warn("저장할 학급 정보가 없습니다 - userId: {}", userId);
                    return;
                }


                for (AidtClassInfoVo info : classInfo.getClass_info()) {
                    try {
                        Map<String, Object> classInfoMap = new HashMap<>();
                        classInfoMap.put("claId", info.getClass_code());
                        classInfoMap.put("activeyn", "Y");
                        classInfoMap.put("userId", userId);
                        classInfoMap.put("refTcType", "N");

                        int checkCount = kerisApiMapper.selectTcClaUserInfoCheck(classInfoMap);
                        if (checkCount == 0) {
                            kerisApiMapper.insertTcClaUserInfo(classInfoMap);
                            successCount++;
                            log.debug("학급 정보 저장 완료 - userId: {}, classCode: {}", userId, info.getClass_code());
                        } else {
                            log.debug("이미 존재하는 학급 정보 - userId: {}, classCode: {}", userId, info.getClass_code());
                            successCount++;
                        }

                    } catch (Exception e) {
                        failCount++;
                        log.error("학급 정보 저장 실패 - userId: {}, classCode: {}, error: {}",
                                userId, info.getClass_code(), e.getMessage());

                        // 연속 실패가 많으면 전체 실패로 처리
                        if (failCount > 3) {
                            throw new RuntimeException("연속된 오류로 인한 처리 중단");
                        }
                    }
                }

            }else{
                paramData.put("claId",paramData.get("cla_id"));
                paramData.put("userId",paramData.get("user_id"));
                List<Map<String, Object>> classInfo = kerisApiMapper.getSelectClaInfo(paramData);
                for (Map<String, Object> classMap : classInfo) {
                    Map<String, Object> classInfoMap = new HashMap<>();
                    classInfoMap.put("claId", classMap.get("cla_id"));
                    classInfoMap.put("activeyn", "Y");
                    classInfoMap.put("userId", userId);
                    classInfoMap.put("refTcType", "N");

                    int checkCount = kerisApiMapper.selectTcClaUserInfoCheck(classInfoMap);
                    if (checkCount == 0) {
                        kerisApiMapper.insertTcClaUserInfo(classInfoMap);
                        successCount++;
                        log.debug("학급 정보 저장 완료 - userId: {}, classCode: {}", userId, classMap.get("cla_id"));
                    } else {
                        log.debug("이미 존재하는 학급 정보 - userId: {}, classCode: {}", userId, classMap.get("cla_id"));
                        successCount++;
                    }
                }
            }


        } catch (Exception e) {
            log.error("선생님 정보 저장 실패 - userId: {}, error: {}", userId, e.getMessage());
            throw e;
        }
    }


    /**
     * 선생님 플래그 등록 (첫 진입자는 주교사, 나머지는 보조교사)
     */
    public void saveTeacherFlagInsert(Map<String, Object> paramData) throws Exception {
        String userId = MapUtils.getString(paramData, "user_id", "");
        String claId = MapUtils.getString(paramData, "cla_id", "");

        try {
            // 1. 현재 활성화된 주교사 수 확인
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("claId", claId);

            int mainTeacherCount = kerisApiMapper.selectTcClaUserHistCount(checkMap);

            // 2. 주교사/보조교사 구분 결정
            String mainSubFlag = (mainTeacherCount == 0) ? "Y" : "N";
            String teacherType = (mainTeacherCount == 0) ? "주교사" : "보조교사";

            // 3. 히스토리 테이블에 등록
            Map<String, Object> histDataMap = new HashMap<>();
            histDataMap.put("userId", userId);
            histDataMap.put("claId", claId);
            histDataMap.put("mainSubFlag", mainSubFlag);
            histDataMap.put("userStatus", "A");
            int teacherCount = kerisApiMapper.selectTcClaUserTeachHistCount(histDataMap);
            if(teacherCount == 0){
                kerisApiMapper.insertTcClaUserHist(histDataMap);
                log.debug("히스토리 등록 완료 - userId: {}, claId: {}, type: {}", userId, claId, teacherType);

                // 4. 기존 사용자 정보 테이블의 main_sub_flag만 업데이트
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("userId", userId);
                updateMap.put("claId", claId);
                updateMap.put("mainSubFlag", mainSubFlag);

                kerisApiMapper.updateTcClaUserInfo(updateMap);
                log.debug("사용자 정보 main_sub_flag 업데이트 완료 - userId: {}, claId: {}, flag: {}", userId, claId, mainSubFlag);

                log.info("선생님 플래그 등록 완료 - userId: {}, claId: {}, 역할: {}", userId, claId, teacherType);
            }


        } catch (Exception e) {
            log.error("선생님 플래그 등록 실패 - userId: {}, claId: {}, error: {}", userId, claId, e.getMessage());
            throw new RuntimeException("선생님 플래그 데이터 저장에 실패했습니다");
        }
    }

    /**
     * 보조교사를 주교사로 승격 (기존 주교사는 나간 상태 처리)
     */
    public void saveTeacherFlagUpdate(Map<String, Object> paramData) throws Exception {
        String userId = MapUtils.getString(paramData, "user_id", "");
        String claId = MapUtils.getString(paramData, "cla_id", "");

        try {
            // 1. 기존 주교사들을 나간 상태로 처리
            Map<String, Object> leaveMap = new HashMap<>();
            leaveMap.put("claId", claId);

            // 히스토리 테이블에서 기존 주교사들을 나간 상태로 변경
            kerisApiMapper.updateTcClaUserHistMainTeacherToLeave(leaveMap);
            log.debug("기존 주교사 히스토리 나간 상태 처리 완료 - claId: {}", claId);

            // 사용자 정보 테이블에서 기존 주교사들을 빈값으로 변경
            kerisApiMapper.updateTcClaUserInfoMainTeacherToEmpty(leaveMap);
            log.debug("기존 주교사 정보 빈값 처리 완료 - claId: {}", claId);

            // 2. 승격할 보조교사의 기존 히스토리를 나간 상태로 처리
            Map<String, Object> userLeaveMap = new HashMap<>();
            userLeaveMap.put("userId", userId);
            userLeaveMap.put("claId", claId);

            kerisApiMapper.updateTcClaUserHistToLeave(userLeaveMap);
            log.debug("승격 대상자 기존 히스토리 나간 상태 처리 완료 - userId: {}, claId: {}", userId, claId);

            // 3. 보조교사를 주교사로 승격 - 새로운 히스토리 레코드 추가
            Map<String, Object> promoteMap = new HashMap<>();
            promoteMap.put("userId", userId);
            promoteMap.put("claId", claId);
            promoteMap.put("mainSubFlag", "Y");
            promoteMap.put("userStatus", "A");

            kerisApiMapper.insertTcClaUserHist(promoteMap);
            log.debug("보조교사 주교사 승격 히스토리 등록 완료 - userId: {}, claId: {}", userId, claId);

            // 4. 사용자 정보 테이블에서 주교사로 업데이트
            Map<String, Object> updateInfoMap = new HashMap<>();
            updateInfoMap.put("userId", userId);
            updateInfoMap.put("claId", claId);
            updateInfoMap.put("mainSubFlag", "Y");

            kerisApiMapper.updateTcClaUserInfo(updateInfoMap);
            log.debug("사용자 정보 주교사로 업데이트 완료 - userId: {}, claId: {}", userId, claId);

            log.info("보조교사 주교사 승격 완료 - 승격된 userId: {}, claId: {}", userId, claId);

        } catch (Exception e) {
            log.error("보조교사 주교사 승격 실패 - userId: {}, claId: {}, error: {}", userId, claId, e.getMessage());
            throw new RuntimeException("보조교사 주교사 승격 데이터 저장에 실패했습니다");
        }
    }

    /**
     * 선생님 나간 처리 (주교사든 보조교사든 관계없이 나간 상태로 처리)
     */
    public void saveTeacherLeave(Map<String, Object> paramData) throws Exception {
        String userId = MapUtils.getString(paramData, "user_id", "");
        String claId = MapUtils.getString(paramData, "cla_id", "");

        try {
            // 1. 현재 선생님의 역할 확인 (주교사인지 보조교사인지)
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("userId", userId);
            checkMap.put("claId", claId);

            String currentRole = kerisApiMapper.selectCurrentTeacherRole(checkMap);

            if (StringUtils.isBlank(currentRole)) {
                throw new RuntimeException("해당 반에서 활성화된 선생님 정보를 찾을 수 없습니다");
            }

            // 2. 히스토리 테이블에서 나간 상태로 업데이트
            Map<String, Object> leaveMap = new HashMap<>();
            leaveMap.put("userId", userId);
            leaveMap.put("claId", claId);

            kerisApiMapper.updateTcClaUserHistToLeave(leaveMap);
            log.debug("선생님 히스토리 나간 상태 처리 완료 - userId: {}, claId: {}, role: {}", userId, claId, currentRole);

            // 3. 사용자 정보 테이블에서 main_sub_flag를 빈값으로 처리
            Map<String, Object> updateInfoMap = new HashMap<>();
            updateInfoMap.put("userId", userId);
            updateInfoMap.put("claId", claId);
            updateInfoMap.put("mainSubFlag", "");

            kerisApiMapper.updateTcClaUserInfo(updateInfoMap);
            log.debug("선생님 정보 빈값 처리 완료 - userId: {}, claId: {}", userId, claId);

            String roleText = "Y".equals(currentRole) ? "주교사" : "보조교사";
            log.info("선생님 나간 처리 완료 - userId: {}, claId: {}, 이전역할: {}", userId, claId, roleText);

        } catch (Exception e) {
            log.error("선생님 나간 처리 실패 - userId: {}, claId: {}, error: {}", userId, claId, e.getMessage());
            throw new RuntimeException("선생님 나간 처리 데이터 저장에 실패했습니다");
        }
    }

    public Map<String, Object> getUserLectureCodeMappingInfo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();

        // 1. ptnId로 교과서 정보 조회 (aidt_ptn_info)
        Map<String, Object> textbookInfo = kerisApiMapper.getTextbookInfo(paramData);
        paramData.put("claId", paramData.get("cla_id"));
        Map<String, Object> tcClaInfo = kerisApiMapper.getTcClaInfo(paramData);

        if ((textbookInfo != null && !textbookInfo.isEmpty())
                || tcClaInfo == null
                || !Integer.valueOf(2).equals(tcClaInfo.get("smt"))) {
            // 1학기/2학기 나누어진 교과서인 경우 빈 배열로 리턴
            result.put("lectureList", new ArrayList<>());
            return result;
        }

        // 2. claId로 tc_cla_group_info 매핑 테이블 조회
        List<Map<String, Object>> groupMappingInfo = kerisApiMapper.getGroupMappingInfo(paramData);

        List<Map<String, Object>> lectureList;

        if (CollectionUtils.isNotEmpty(groupMappingInfo)) {
            // 2-1. 해당 claId로 이미 tc_cla_group_info 매핑된 값이 있을 경우
            lectureList = new ArrayList<>();
        } else {
            // 2-2. 해당 claId로 tc_cla_group_info 매핑된 값이 없을 경우
            // tc_cla_info 1학기 강의목록 조회
            lectureList = kerisApiMapper.getFirstSemesterLectureList(paramData);
        }

        result.put("lectureList", lectureList);

        return result;
    }

    @Transactional
    public Map<String, Object> setupLectureMapping(Map<String, Object> paramData) {
        
        Map<String, Object> result = new HashMap<>();
        
        String originClaId = MapUtils.getString(paramData, "origin_cla_id", "");
        String claId = MapUtils.getString(paramData, "cla_id", "");
        
        if (StringUtils.isBlank(originClaId) || StringUtils.isBlank(claId)) {
            result.put("success", false);
            result.put("message", "파라미터 오류: cla_id가 누락되었습니다");
            return result;
        }
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("claId", originClaId);
            Map<String, Object> resultUserInfo = kerisApiMapper.getClaIdFromLectureCode(param);

            // tc_cla_info에서 조회한 년도 사용
            String yr = MapUtils.getString(resultUserInfo, "yr", "");

            // 3-1. 첫 번째 레코드: group_cla_id = origin cla_id, cla_id = origin cla_id, group_index = 1
//            Map<String, Object> firstSemesterGroup = new HashMap<>();
//            firstSemesterGroup.put("groupClaId", originClaId);
//            firstSemesterGroup.put("claId", originClaId);
//            firstSemesterGroup.put("yr", yr);
//            firstSemesterGroup.put("smt", 1);
//            firstSemesterGroup.put("groupIndex", 1);
//            firstSemesterGroup.put("userId", resultUserInfo.get("userId"));
//            kerisApiMapper.insertTcClaGroupInfo(firstSemesterGroup);
            
            // 3-2. 두 번째 레코드: group_cla_id = origin cla_id, cla_id = cla_id, group_index = 2
            Map<String, Object> secondSemesterGroup = new HashMap<>();
            secondSemesterGroup.put("originClaId", originClaId);
            secondSemesterGroup.put("claId", claId);
            secondSemesterGroup.put("yr", yr);
            secondSemesterGroup.put("smt", 2);
            secondSemesterGroup.put("groupIndex", 0);
            secondSemesterGroup.put("userId", resultUserInfo.get("userId"));
            kerisApiMapper.insertTcClaGroupInfo(secondSemesterGroup);

            Map<String, Object> param2 = new HashMap<>();
            param2.put("userId", paramData.get("user_id"));
            param2.put("claId", claId);

            Map<String, Object> resultData = kerisApiMapper.selectTcClaUserInfo(param2);

            if (resultData != null) {
                Map<String, Object> groupInfo = kerisApiMapper.getTcClaGroupInfo(param2);

                Map<String, Object> param3 = new HashMap<>();

                param3.put("claId", groupInfo.get("originClaId"));
                param3.put("userId", paramData.get("user_id"));

                kerisApiMapper.upsertTcClaUserInfo(param3);
            }


            // 성공 결과 반환
            result.put("success", true);
            result.put("originClaId", originClaId);
            result.put("claId", claId);
            
        } catch (Exception e) {
            log.error("매핑 데이터 셋팅 실패: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "매핑 데이터 셋팅 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> updateClaMemberActivation(Map<String, Object> paramData) {
        
        String userId = MapUtils.getString(paramData, "user_id", "");
        String partnerId = MapUtils.getString(paramData, "partnerId", "");
        
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(partnerId)) {
            throw new RuntimeException("파라미터 오류: user_id 또는 partnerId가 누락되었습니다");
        }
        
        try {
            // 1. tc_cla_group_info에서 그룹화된 cla_id 조회
            Map<String, Object> groupInfo = kerisApiMapper.getGroupClaInfo(paramData);
            
            // 매핑 정보가 없으면 건너뛰기
            if (groupInfo == null || groupInfo.isEmpty()) {
                return Map.of(
                    "message", "매핑 정보가 없어 건너뜁니다"
                );
            }
            
            // origin_cla_id는 1학기, cla_id는 2학기
            String originClaId = MapUtils.getString(groupInfo, "originClaId");
            String claId = MapUtils.getString(groupInfo, "claId");
            
            // 매핑 정보가 하나라도 있으면 실행
            if (StringUtils.isNotBlank(originClaId) || StringUtils.isNotBlank(claId)) {
                
                // 2학기 데이터가 있으면 비활성화
                if (StringUtils.isNotBlank(claId)) {
                    Map<String, Object> deactivateParam = new HashMap<>();
                    deactivateParam.put("claId", claId);
                    deactivateParam.put("actvtnAt", "N");
                    deactivateParam.put("smt", 2);
                    kerisApiMapper.updateTcClaMbInfoActivation(deactivateParam);
                }
                
                // 1학기 데이터가 있으면 활성화
                if (StringUtils.isNotBlank(originClaId)) {
                    Map<String, Object> activateParam = new HashMap<>();
                    activateParam.put("claId", originClaId);
                    activateParam.put("actvtnAt", "Y");
                    activateParam.put("smt", 1);
                    kerisApiMapper.updateTcClaMbInfoActivation(activateParam);
                }
                
                // 결과 데이터 반환
                return Map.of(
                    "originClaId", originClaId != null ? originClaId : "",
                    "claId", claId != null ? claId : "",
                    "message", "학급 멤버 활성화 상태 업데이트 완료"
                );
            } else {
                return Map.of(
                    "message", "유효한 매핑 정보가 없어 건너뜁니다"
                );
            }
            
        } catch (Exception e) {
            log.error("학급 멤버 활성화 상태 업데이트 실패: {}", e.getMessage());
            throw new RuntimeException("학급 멤버 활성화 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
