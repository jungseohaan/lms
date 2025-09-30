package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.TchToolBarMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class TchToolsService {
    private final TchToolBarMapper tchToolBarMapper;
    /**
     * (툴 편집).교사/학생 초기 설정 도구 관리 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findInitToolsList(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "list": [
                {"id": 1, "item": "dummy-1"},
                {"id": 2, "item": "dummy-2"},
                {"id": 3, "item": "dummy-3"},
            ]}
        """).toMap();
    }

    /**
     * (툴 편집).교사/학생 도구 관리 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findToolsList(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "list": [
                {"id": 1, "item": "dummy-1"},
                {"id": 2, "item": "dummy-2"},
                {"id": 3, "item": "dummy-3"},
            ]}
        """).toMap();
    }

    /**
     * (툴 편집).교사/학생 도구 관리 목록 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyToolsList(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    // 펜툴바 조회
    public Object findToolBarCall(Map<String, Object> paramData) throws Exception {

        return tchToolBarMapper.findToolBarCall(paramData);
    }

    // 펜툴바 저장
    //@Transactional(transactionManager = "mybatisTrManager", rollbackFor = Exception.class)
    public void updateTchToolBar(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = tchToolBarMapper.findUserId(paramData);

        if (resultMap != null && resultMap.get("user_id").equals(paramData.get("userId"))) {
            tchToolBarMapper.updateTchToolBar(paramData);
        } else {
            tchToolBarMapper.insertTchToolBar(paramData);
        }
    }

    //@Transactional(transactionManager = "mybatisTrManager")
    public Map<String, Object> saveTchToolBar(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int cnt = tchToolBarMapper.updateTchTool(paramData);

            if(cnt <= 0) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "실패");
                //throw new AidtException("Not Found Modifiy Data: " + cnt);

                return resultMap;
            }

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }

        // Response
        return resultMap;
    }

    public Object selectTchTool(Object paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "tolId", "claId","textbkId","userSeCd", "monitor","attention","pentool", "mathtool","mathcanvas","aiSpeaking","aiWriting"
                ,"bookmark","quiz", "opinionBoard","whiteBoard","smartTool","sbjctCd"
        );

        Map<String, Object> isExist = tchToolBarMapper.selectTchToolExistCheck(paramData);

        if(isExist != null){
            return AidtCommonUtil.filterToMap(infoItem, isExist);
        }else{
            int cnt = tchToolBarMapper.insertTchToolInfo(paramData);
            if(cnt > 0){
                return AidtCommonUtil.filterToMap(infoItem, tchToolBarMapper.selectTchToolExistCheck(paramData));
            }
        }
        return null;
    }

    /* 교사펜툴(호출) */
    public Object initTchToolEdit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        if(tchToolBarMapper.deleteTchToolBar(paramData)>0){ //툴편집(초기화)
            return(selectTchTool(paramData)); //툴편집(호출)
        }

        return returnMap;
    }

    /* 교사펜툴(저장)*/
    public Object selectTchBoard(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // Response Parameters
        List<String> TchBoardItem = Arrays.asList(
                 "tchBoardId","textbkId","wrterId", "tabId","articleId","subId", "hdwrtCn"
            );

        LinkedHashMap<Object, Object> tchBoardMap = AidtCommonUtil.filterToMap(TchBoardItem, tchToolBarMapper.selectTchBoard(paramData));

            return tchBoardMap;
    }

    public Map<String, Object> saveTchBoard(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Map<String, Object> checkExist = tchToolBarMapper.selectTchBoard(paramData);

            int successYn = 0;
            if (MapUtils.isEmpty(checkExist)) {
                //결과 무
                successYn = tchToolBarMapper.insertTchBoard(paramData);
            } else {
                //결과 있을때
                successYn = tchToolBarMapper.updateTchBoard(paramData);
            }

            if(successYn <= 0) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "실패");

                return resultMap;
            }

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }

        // Response
        return resultMap;
    }

    /**
     * 화면 제어 설정 조회
     */
    public Object getScreenControlSettings(Map<String, Object> paramData) throws Exception {
        String claId = (String) paramData.get("claId");
        String userId = (String) paramData.get("userId"); // userId 추가 (rgtr, mdfr에 사용)

        // cla_id로 현재 설정 조회 (se_code 테이블과 JOIN)
        List<Map<String, Object>> settings = tchToolBarMapper.selectScreenControlSettings(paramData);

        // 설정이 없으면 se_code에서 기본값 조회하고 INSERT
        if (settings == null || settings.isEmpty()) {
            // se_code 테이블에서 화면 제어 코드 목록 조회
            List<Map<String, Object>> screenControlCodes = tchToolBarMapper.selectScreenControlCodes();

            // 조회된 코드들을 tc_screen_control_settings 테이블에 INSERT
            for (Map<String, Object> code : screenControlCodes) {
                Map<String, Object> insertParams = new HashMap<>();
                insertParams.put("claId", claId);
                insertParams.put("settingCode", Integer.parseInt(String.valueOf(code.get("codeCd"))));
                insertParams.put("settingValue", 1); // MySQL boolean은 0/1로 처리
                insertParams.put("comment", code.get("codeNm")); // se_code의 code_nm 사용
                insertParams.put("rgtr", userId != null ? userId : "system");
                insertParams.put("mdfr", userId != null ? userId : "system");

                tchToolBarMapper.insertScreenControlSetting(insertParams);
            }

            // INSERT 후 다시 조회하여 반환
            settings = tchToolBarMapper.selectScreenControlSettings(paramData);
        }

        return Map.of("settings", settings);
    }

    /**
     * 화면 제어 설정 일괄 저장
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> saveScreenControlSettings(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            String claId = (String) paramData.get("claId");
            String userId = (String) paramData.get("userId");
            List<Map<String, Object>> settings = (List<Map<String, Object>>) paramData.get("settings");

            if (settings == null || settings.isEmpty()) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "설정 데이터가 없습니다.");
                return resultMap;
            }

            // se_code에서 코드 목록 미리 조회
            List<Map<String, Object>> codes = tchToolBarMapper.selectScreenControlCodes();
            Map<String, String> codeMap = new HashMap<>();
            for (Map<String, Object> code : codes) {
                codeMap.put(String.valueOf(code.get("codeCd")), (String) code.get("codeNm"));
            }

            // 각 설정에 대해 UPDATE 처리
            for (Map<String, Object> setting : settings) {
                Map<String, Object> params = new HashMap<>();
                params.put("claId", claId);
                params.put("settingCode", setting.get("settingCode"));
                // Boolean을 Integer(0/1)로 변환
                Boolean boolValue = (Boolean) setting.get("settingValue");
                params.put("settingValue", boolValue ? 1 : 0);

                // 기존 설정 확인
                Map<String, Object> existingSetting = tchToolBarMapper.selectScreenControlSetting(params);

                if (existingSetting != null) {
                    // UPDATE - 값만 변경
                    params.put("mdfr", userId);
                    tchToolBarMapper.updateScreenControlSetting(params);
                } else {
                    // INSERT - 처음 설정하는 경우
                    String settingCodeStr = String.valueOf(setting.get("settingCode"));
                    String comment = codeMap.getOrDefault(settingCodeStr, "");

                    params.put("comment", comment);
                    params.put("rgtr", userId);
                    params.put("mdfr", userId);
                    tchToolBarMapper.insertScreenControlSetting(params);
                }
            }

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "화면 제어 설정이 저장되었습니다.");

        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "화면 제어 설정 저장 실패: " + e.getMessage());
        }

        return resultMap;
    }
}