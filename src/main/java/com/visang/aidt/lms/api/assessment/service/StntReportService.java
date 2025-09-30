package com.visang.aidt.lms.api.assessment.service;

import com.visang.aidt.lms.api.assessment.mapper.StntReportMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@Service
@AllArgsConstructor
public class StntReportService {
    private final StntReportMapper stntReportMapper;


    @Transactional(readOnly = true)
    public Object getStntReportLastActivity(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);

        // 필수 파라미터 체크
        if (ObjectUtils.isEmpty(paramData.get("userId"))) {
            returnMap.put("resultMsg", "필수 파라미터가 누락되었습니다. userId");
            return returnMap;
        }

        if (ObjectUtils.isEmpty(paramData.get("claId"))) {
            returnMap.put("resultMsg", "필수 파라미터가 누락되었습니다. claId");
            return returnMap;
        }

        if (ObjectUtils.isEmpty(paramData.get("textbkId"))) {
            returnMap.put("resultMsg", "필수 파라미터가 누락되었습니다. textbkId");
            return returnMap;
        }

        // 쿼리 실행
        Map<String, Object> resultMap = stntReportMapper.findStntReportLastActivity(paramData);

        if (resultMap != null) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            returnMap.put("lastType", resultMap.get("lastType")); // L, H, E
            returnMap.put("lastDatetime", resultMap.get("lastDatetime"));
        } else {
            returnMap.put("resultMsg", "최근 활동 내역이 없습니다.");
        }

        return returnMap;
    }

}

