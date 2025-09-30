package com.visang.aidt.lms.api.materials.service;

import org.apache.commons.collections4.MapUtils;
import com.visang.aidt.lms.api.materials.mapper.TchMdulMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class TchMdulService {

    private final TchMdulMapper tchMdulMapper;

    /**
     * (교사).손글씨 저장
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public  Map<String, Object> saveTchMdulNote(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOK", false);
        returnMap.put("resultMsg", "실패");
        //1. Validation
        if (paramData.get("userId") == null || ("").equals(paramData.get("userId").toString())) {
            return returnMap;
        }
        if (paramData.get("claId") == null || ("").equals(paramData.get("claId").toString())) {
            return returnMap;
        }
        if (paramData.get("textbkId") == null || ("").equals(paramData.get("textbkId").toString())) {
            return returnMap;
        }
        if (paramData.get("tabId") == null || ("").equals(paramData.get("tabId").toString())) {
               return returnMap;
        }
        if (ObjectUtils.isEmpty(MapUtils.getString(paramData, "moduleId"))) {
            return returnMap;
        }
        if (paramData.get("hdwrtCn") == null || ("").equals(paramData.get("hdwrtCn").toString())) {
           return returnMap;
        }

        paramData.put("hdwrtCn", String.valueOf(paramData.get("hdwrtCn")));

        List<String> selectTcNoteInfoForm = Arrays.asList("id");
        LinkedHashMap<Object, Object> selectTcNoteInfo= AidtCommonUtil.filterToMap(selectTcNoteInfoForm, tchMdulMapper.selectTcNoteInfo(paramData));

        if(selectTcNoteInfo.get("id") == null) {
            if(tchMdulMapper.insertTcNoteInfo(paramData) > 0) { //tc_note_info insert 성공
                returnMap.put("resultOK", true);
                returnMap.put("resultMsg", "성공");
            }
        } else {    //insert
            paramData.put("id", selectTcNoteInfo.get("id"));
            if(tchMdulMapper.updateTcNoteInfo(paramData) > 0) {
                returnMap.put("resultOK", true);
                returnMap.put("resultMsg", "성공");
            }
        }

        paramData.remove("id");

        return returnMap;
    }

    /**
     * (교사) 손글씨 호출
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public  Map<String, Object> tchLectureMdulNoteView(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //제출자 수 Map - resultCnt
        List<String> selectTchMdulHdwrntCnForm = Arrays.asList("hdwrtCn", "noteId");
        LinkedHashMap<Object, Object> selectTchMdulHdwrntCn = AidtCommonUtil.filterToMap(selectTchMdulHdwrntCnForm, tchMdulMapper.selectTchMdulHdwrntCn(paramData));

        if(MapUtils.isEmpty(selectTchMdulHdwrntCn)) {
            returnMap.put("noteId", null);
            returnMap.put("hdwrtCn", null);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        } else {
            returnMap.put("noteId", selectTchMdulHdwrntCn.get("noteId"));
            returnMap.put("hdwrtCn", selectTchMdulHdwrntCn.get("hdwrtCn"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        return returnMap;
    }

    /**
     * (교사)판서(교사 필기)학생 공유
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public  Map<String, Object> saveTchMdulNoteShare(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("resultOK", false);
        returnMap.put("resultMsg", "실패");

        //1. Validation
        if (paramData.get("noteId") == null || ("").equals(paramData.get("noteId").toString())) {
            return returnMap;
        }
        if (paramData.get("noteImgUrl") == null || ("").equals(paramData.get("noteImgUrl").toString())) {
            return returnMap;
        }

        List<Map> cchModulCheck = tchMdulMapper.findTchModulCheck(paramData);

        if (cchModulCheck.isEmpty()) { //값이 없을때
            paramData.put("check", "N");
        }

        //tc_note_info table 확인
        Map<String, Object> tchNoteInfoMap = tchMdulMapper.selectTchNoteInfoById(paramData);

        if(!MapUtils.isEmpty(tchNoteInfoMap)) {
            paramData.put("moduleId", tchNoteInfoMap.get("moduleId"));
            if(tchMdulMapper.insertTcNoteConts(paramData) > 0) { //tc_note_conts insert 성공
                returnMap.put("resultOK", true);
                returnMap.put("resultMsg", "성공");
            }
        }

        paramData.remove("check");
        paramData.remove("moduleId");
        paramData.remove("sltNoteSeq");
        return returnMap;
    }


    /**
     * (모듈).피드백 보내기(생성)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createFeedback(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (질문).모듈 질문(목록) 상세정보 조회 (댓글포함)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findMdulQuestList(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "list": [
                {"id": 1, "item": "dummy-1"},
                {"id": 2, "item": "dummy-2"},
                {"id": 3, "item": "dummy-3"},
            ]}
        """).toMap();
    }

    /**
     * (질문).모듈 질문에 대한 댓글 저장
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createQuestComment(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }
}
