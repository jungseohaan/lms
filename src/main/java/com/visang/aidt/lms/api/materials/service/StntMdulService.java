package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.StntMdulMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class StntMdulService {

    private final StntMdulMapper stntMdulMapper;

    /**
     * (학생).손글씨 저장
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public  Map<String, Object> updateStntMdulNote(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        final int MAX_SIZE_BYTES = 10 * 1024 * 1024; // 16MB
        final int MAX_SIZE_MB = MAX_SIZE_BYTES / (1024 * 1024);

        returnMap.put("resultDetailId", paramData.get("resultDetailId"));
        returnMap.put("hdwrtCn", paramData.get("hdwrtCn"));
        returnMap.put("resultOK", false);
        returnMap.put("resultMsg", "실패");

        //1. Validation
        if (paramData.get("userId") == null || ("").equals(paramData.get("userId").toString())) {
            return returnMap;
        }
        if (paramData.get("resultDetailId") == null || ("").equals(paramData.get("resultDetailId").toString())) {
            return returnMap;
        }
        if (paramData.get("hdwrtCn") == null || ("").equals(paramData.get("hdwrtCn").toString())) {
            return returnMap;
        }

        // 손글씨 내용 길이 검증
        String hdwrtCn = MapUtils.getString(paramData, "hdwrtCn");
        int contentSize = hdwrtCn.getBytes().length;

        if (contentSize > MAX_SIZE_BYTES) {
            returnMap.put("resultMsg", "Data too large. Max " + MAX_SIZE_MB + "MB.");
            returnMap.put("size", contentSize);
            returnMap.put("limitSize", MAX_SIZE_MB);
            return returnMap;
        }


        try {
            stntMdulMapper.updateStntNoteInfo(paramData); //std_dta_result_detail update 성공
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
            returnMap.put("size", contentSize);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * (학생). 손글씨 호출
     *
     * @param paramData 입력 파라메터
     * @return Object
     */
    @Transactional(readOnly = true)
    public Object getStntMdulNoteView(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //제출자 수 Map - resultCnt
        List<String> selectStntMdulHdwrntCnForm = Arrays.asList("hdwrtCn");
        LinkedHashMap<Object, Object> selectStntMdulHdwrntCn = AidtCommonUtil.filterToMap(selectStntMdulHdwrntCnForm, stntMdulMapper.selectStntMdulHdwrntCn(paramData));
        
        if(selectStntMdulHdwrntCn.size() == 0) {
            returnMap.put("hdwrtCn", null);
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        } else {
            returnMap.put("hdwrtCn", selectStntMdulHdwrntCn.get("hdwrtCn"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }
            
        
        return returnMap;
    }

    /**
     * (학생)판서(교사 필기)학생 공유
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getStntMdulNoteShare(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> itemList = Arrays.asList("noteId", "noteSeq", "noteImgUrl");
        List<Map> resultList = stntMdulMapper.selectTcNoteConts(paramData);
        List<LinkedHashMap<Object, Object>> stntMdulNoteShareList = AidtCommonUtil.filterToList(itemList, resultList);


        returnMap.put("moduleId", paramData.get("moduleId"));
        if(resultList.size()>0) {
            returnMap.put("textbkId", resultList.get(0).get("textbkId"));
        } else {
            returnMap.put("textbkId", null);
        }
        returnMap.put("noteSharedList", stntMdulNoteShareList);
        returnMap.put("sharedCnt", stntMdulNoteShareList.size());

        return returnMap;
    }

    /**
     * (학생)교사 피드백 보기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getStntMdulFdbShare(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> selectStdDtaResultForm = Arrays.asList("stdFdbDc","stdFdbUrl", "subMitAnw", "subMitAnwUrl", "hdwrtCn");
        List<LinkedHashMap<Object, Object>> selectStdDtaResultList = AidtCommonUtil.filterToList(selectStdDtaResultForm, stntMdulMapper.getStntMdulFdbShare_selectStdDtaResult(paramData));

        returnMap.put("resultDetailId", paramData.get("resultDetailId"));
        returnMap.put("fdbSharedList", selectStdDtaResultList);
        returnMap.put("fdbCnt", selectStdDtaResultList.size());

        return returnMap;
    }


    /**
     * (학생)우수답안보기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getStntMdulExltShared(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> selectStdDtaResultForm = Arrays.asList("stdFdbDc","stdFdbUrl", "exltAnwAt", "fdbExpAt", "errata", "subMitAnw", "subMitAnwUrl", "hdwrtCn", "profileImg");

        List<LinkedHashMap<Object, Object>> selectStdDtaResultList = AidtCommonUtil.filterToList(selectStdDtaResultForm, stntMdulMapper.selectStdDtaResult(paramData));

        returnMap.put("resultDetailId", paramData.get("resultDetailId"));
        returnMap.put("exltSharedList", selectStdDtaResultList);
        //returnMap.put("exltCnt", stntMdulMapper.selectExltCnt(paramData).get("exltCnt"));
        returnMap.put("exltCnt", selectStdDtaResultList.size());

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
