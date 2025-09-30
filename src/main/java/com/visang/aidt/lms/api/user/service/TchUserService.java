package com.visang.aidt.lms.api.user.service;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class TchUserService {
    /**
     * (유저).교사정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findTchInfo(Map<String, Object> paramData) throws Exception {
        Map<String,Object> rtnMap = new JSONObject("""
            { 
                "tchId" : "tch0001",
                "tchNm" : "김이비상",
                "clsNm" : "2학년 1반",
                "clsNum" : "1",
                "textbookId" : "tb1200",
                "textbookNm" : "수학2-1"
            }
        """).toMap();

        // 학생목록 포함
        rtnMap.putAll(this.findStntListOfClass(paramData));
        return rtnMap;
    }

    /**
     * (유저).학급에 포함된 학생 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findStntListOfClass(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "stntList": [
                {"stntId": "user1", "stntNm": "김비상"},
                {"stntId": "user2", "stntNm": "이비상"},
                {"stntId": "user3", "stntNm": "박비상"},
                {"stntId": "user4", "stntNm": "최비상"},
                {"stntId": "user5", "stntNm": "정비상"},
                {"stntId": "user6", "stntNm": "강비상"}
            ]}
        """).toMap();
    }

}
