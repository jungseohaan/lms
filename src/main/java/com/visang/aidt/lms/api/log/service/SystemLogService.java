package com.visang.aidt.lms.api.log.service;


import com.visang.aidt.lms.api.log.mapper.SystemLogMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.dao.DataAccessException;

@Slf4j
@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class SystemLogService {

    private final SystemLogMapper systemLogMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> getUserCheckInfo(Map<String, Object> paramData) throws Exception {

        String userId = MapUtils.getString(paramData, "userId");

        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("userId", userId);

        // 기본 사용자 정보 조회
        Map<String, Object> userMap = systemLogMapper.getUserInfoByUserId(paramData);
        int userIdx = MapUtils.getInteger(userMap, "userIdx", 0);
        String userSeCd = MapUtils.getString(userMap, "userSeCd", "");
        // 사용자 없을 경우 오류
        if (userIdx == 0) {
            // 오류가 났을 경우 로깅 안하는 것이 default
            resultMap.put("userIdx", 0);
            resultMap.put("logRegAt", "N");/*user 오류일 경우 default 로깅 안함*/
            resultMap.put("resultMessage", userId + " - 사용자 정보가 없습니다");
            return resultMap;
        }
        resultMap.put("userIdx", userIdx);

        // log default 세팅 정보 조회 (서비스 및 영역 별)
        Map<String, Object> logInfoMap = systemLogMapper.getLogCheckInfo(paramData);
        String logTy = MapUtils.getString(logInfoMap, "logTy", "OFF");

        // 로그 쌓는 것은 default N 으로 초기화
        String setLogRegAt = "N";
        if (logTy.equals("OFF")) {
            setLogRegAt = "N";
        }
        else if (logTy.equals("ALL")) {
            // 전체 허용
            setLogRegAt = "Y";
        }
        /*로그 옵션이 교사 일괄일 경우*/
        else if (logTy.equals("TCH")) {
            if (userSeCd.equals("T")) {
                setLogRegAt =  "Y";
            }/*교사 외에는 안쌓이도록 한다*/else {
                setLogRegAt = "N";
            }
        }
        /*로그 옵션이 학생 일괄일 경우*/
        else if (logTy.equals("STU")) {
            if (userSeCd.equals("S")) {
                setLogRegAt = "Y";
            }/*학생 외에는 안쌓이도록 한다*/else {
                setLogRegAt = "N";
            }
        }

        // 개별 로깅이 아닌 경우 현재 시점에서 flag 확인 / 로그를 끄는 세팅일 경우 이후 로직 수행하지 않고 바로 return
        if (logTy.equals("USR") == false && setLogRegAt.equals("N")) {
            resultMap.put("logRegAt", "N");/*user 오류일 경우 default 로깅 안함*/
            return resultMap;
        }

        List<Map<String, Object>> logCheckInspList = systemLogMapper.getLogCheckInspListForFe(logInfoMap);
        List<Map<String, Object>> userCheckInspList = null;
        // 사용자 개별 로그 설정 정보 조회
        Map<String, Object> userCheckMap = systemLogMapper.getUserCheckInfo(paramData);
        String userLogRegAt = "N";
        if (userCheckMap != null) {
            userLogRegAt = MapUtils.getString(userCheckMap, "logRegAt", "N");
        }
        // 로깅 옵션이 USR 일 경우 사용자 로그 기준 처리
        if (logTy.equals("USR")) {
            // 사용자 옵션으로 로깅 처리 했을 때 flag가 N 이면 로그를 끄는 세팅으로 처리
            if (userLogRegAt.equals("Y")) {
                // 사용자 개별 체크일 경우 로그 등록 여부는 유저를 따라감
                setLogRegAt = "Y";
            } else {
                resultMap.put("logRegAt", "N");
                return resultMap;
            }
        }
        // 사용자 개별 로깅이 Y 일 경우에만 세부 로깅 처리를 한다
        if (userLogRegAt.equals("Y")) {
            userCheckInspList = systemLogMapper.getUserCheckInspListForFe(userCheckMap);
        }
        resultMap.put("logRegAt", setLogRegAt);
        resultMap.put("sysLogInspList", logCheckInspList);
        resultMap.put("userLogInspList", userCheckInspList);

        return resultMap;
    }

    public Map<String, Object> saveUserCheckInfo(Map<String, Object> paramData) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        String userId = MapUtils.getString(paramData, "userId", "");
        if (StringUtils.isEmpty(userId)) {
            resultMap.put("resultMessage", "userId parameter가 없습니다.");
            return resultMap;
        }

        String logRegAt = MapUtils.getString(paramData, "logRegAt", "N");
        int logLevel = MapUtils.getInteger(paramData, "logLevel", 800);

        resultMap = systemLogMapper.getUserCheckInfo(paramData);
        int userCheckId = MapUtils.getInteger(resultMap, "userCheckId", 0);
        // 이미 등록되어 있을 경우 update 후 return
        if (userCheckId > 0) {
            paramData.put("userCheckId", userCheckId);
            systemLogMapper.updateUserCheck(paramData);
            resultMap.put("userIdx", MapUtils.getInteger(resultMap, "userIdx", 0));
            resultMap.put("userId", userId);
            resultMap.put("userCheckId", userCheckId);
            resultMap.put("logRegAt", logRegAt);
            resultMap.put("logLevel", logLevel);
            return resultMap;
        } else {
            resultMap = new HashMap<>();
        }

        resultMap.put("userId", userId);
        Map<String, Object> userMap = systemLogMapper.getUserInfoByUserId(paramData);
        int userIdx = MapUtils.getInteger(userMap, "userIdx", 0);
        if (userIdx == 0) {
            resultMap.put("resultMessage", "user 테이블에 " + userId + " ID 정보가 없습니다.");
            return resultMap;
        }
        systemLogMapper.insertUserCheck(paramData);
        resultMap.put("userIdx", userIdx);
        resultMap.put("userId", userId);
        resultMap.put("userCheckId", 0);
        resultMap.put("logRegAt", logRegAt);
        resultMap.put("logLevel", logLevel);
        return resultMap;
    }

    /**
     * 서비스 별 시스템 로그 설정 목록 조회
     *
     * @param paramMap inspSrvc : 서비스 명 (git url 마지막 key)
     *                 inspAreaKey : 상세 영역 (중복 시 구분 영역)
     *                 inspNm : 검사 명칭
     *                 logTy : 검사 유형 (ALL, TCH, STU, USR)
     *                 page : page
     *                 size : size
     * @param pageable
     * @return
     */
    public Object getLogCheckList(Map<String, Object> paramMap, Pageable pageable) throws Exception {

        var returnMap = new LinkedHashMap<>();

        Long total = systemLogMapper.getLogCheckTotalCnt(paramMap);
        if (total == 0) {
            PagingInfo emptyPage = AidtCommonUtil.ofPageInfo(new ArrayList<>(), pageable, total);
            returnMap.put("logCheckList", null);
            returnMap.put("page", emptyPage);
            return returnMap;
        }

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramMap)
                .pageable(pageable)
                .build();

        List<Map> logCheckList = null;
        try {
            logCheckList = systemLogMapper.getLogCheckList(pagingParam);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 파라미터로 인한 로그 조회 실패: {}", e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("데이터베이스 접근 오류로 인한 로그 조회 실패: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("시스템 오류로 인한 로그 조회 실패: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("예상치 못한 오류로 인한 로그 조회 실패: {}", e.getMessage(), e);
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(logCheckList, pageable, total);
        returnMap.put("list", logCheckList);
        returnMap.put("page", page);

        return returnMap;
    }
}
