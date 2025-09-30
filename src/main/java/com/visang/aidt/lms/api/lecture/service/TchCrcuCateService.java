package com.visang.aidt.lms.api.lecture.service;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class TchCrcuCateService {
    /**
     * (커리큘럼).분류 생성
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createCrcuCate(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (커리큘럼).분류 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyCrcuCate(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (커리큘럼).분류 삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> removeCrcuCate(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (커리큘럼).분류 복제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createCrcuCateCopy(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (커리큘럼).동일 Depth 이동
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyCrcuCateMove(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }
}
