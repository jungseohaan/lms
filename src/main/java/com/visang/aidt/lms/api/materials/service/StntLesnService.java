package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.QuestionMapper;
import com.visang.aidt.lms.api.materials.mapper.StntLesnMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntLesnService {

    private final StntLesnMapper stntLesnMapper;

    public Object getStntLesnStart(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //Session session

        returnMap.put("type", "initialized");
        returnMap.put("user_Id", "430e8400-e29b-41d4-a746-446655440000");
        returnMap.put("curriculum", "0cc175b9c0f1b6a831c399e269772661");
        return returnMap;
    }

    public Object getStntLesnEnd(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //Session session

        returnMap.put("type", "progressed");
        returnMap.put("user_Id", "430e8400-e29b-41d4-a746-446655440000");
        returnMap.put("curriculum", "0cc175b9c0f1b6a831c399e269772661");
        return returnMap;
    }

    //학습진도율정보전송
    @Transactional(readOnly = true)
    public Object getStntLesnProgRate(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();


        Map<String, Object> percentMap = stntLesnMapper.getStntLesnProgRate(paramData);
        returnMap.put("type", "terminated");
        returnMap.put("user_Id", paramData.get("userId"));
        returnMap.put("curriculum", paramData.get("claId"));
        returnMap.put("percent", percentMap.get("percent"));
        return returnMap;
}

    /**
     * 학습 과정(표준체계 단위)을 기준으로 학생이 수행한 경과 - 수업종료 버튼 클릭시
     * @param paramData
     * @return
     */
    @Transactional(readOnly = true)
    public Object checkLesnProgStd(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        // 진도율 확인
        int percent = new BigDecimal(stntLesnMapper.checkLesnProgStd(paramData).get("percent").toString()).intValue();

        if(percent == 100) {
            returnMap.put("type", "complete");
            returnMap.put("user_Id", "430e8400-e29b-41d4-a746-446655440000");
            returnMap.put("curriculum", "0cc175b9c0f1b6a831c399e269772661");
        }
        return percent == 100 ? returnMap : null;
    }

    @Transactional(readOnly = true)
    public Object findLesnProgStdRate(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Map<String, Object> rate = stntLesnMapper.findLesnProgStdRate(paramData);

        returnMap.put("type", "score");
        returnMap.put("user_Id", "430e8400-e29b-41d4-a746-446655440000");
        returnMap.put("curriculum", "0cc175b9c0f1b6a831c399e269772661");
        returnMap.put("score", rate.get("score"));

        return returnMap;
    }
}
