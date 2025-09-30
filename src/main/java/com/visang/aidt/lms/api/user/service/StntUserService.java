package com.visang.aidt.lms.api.user.service;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class StntUserService {
    
    public Map<String, Object> findStntInfo(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "stntInfo": {
                  "id": 1000,
                  "stntId": "user1",
                  "stntNm": "김비상",
                  "clsNm" : "2학년 1반",
                  "clsNum" : "1",
                  "textbookId" : "tb1200",
                  "textbookNm" : "수학2-1"
              }
            }
        """).toMap();
    }

    
    public Map<String, Object> findProfile(Map<String, Object> paramData) throws Exception {
        return null;
    }

    
    public Map<String, Object> modifyProfile(Map<String, Object> paramData) throws Exception {
        return null;
    }
}
