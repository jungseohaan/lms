package com.visang.aidt.lms.api.lecture.service;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class TchCrcuMdulService {
    /**
     * (모듈).모듈 정보 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findMdulInfo(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (모듈).모듈 정답보기(조회)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findMdulAnswer(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (모듈).제출현황 및 정답(률) 리셋
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createSubmitStatusAndReset(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (모듈).제출현황 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findSubmitStatus(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (모듈).모듈 상시툴 답안 제출 방식 내려주기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyMdulSubmitTools(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (모듈).상시툴 활동 종료 (생성)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createAlwaysOnToolsActEnd(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (모듈).상시툴 활동 종료 (수정)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyAlwaysOnToolsActEnd(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (모듈).상시툴 유형별 제출 현황 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> findAlwaysOnToolsStatus(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (모듈).우수학생 답안 공유하기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createShareOutstandAnswer(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (차시).탭안의 모듈간의 정렬순서 변경(수정)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyTabMdulSort(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (차시).모듈(콘텐츠) 노출여부 설정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> modifyMdulShow(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (차시).모듈(콘텐츠) 복사
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> createMdulCopy(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (차시).모듈(콘텐츠) 삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> removeMdul(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (차시).모듈(콘텐츠) 일괄삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    
    public Map<String, Object> removeAllMdul(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }
}
