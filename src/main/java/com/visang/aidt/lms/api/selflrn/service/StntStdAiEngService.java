package com.visang.aidt.lms.api.selflrn.service;

import com.visang.aidt.lms.api.selflrn.mapper.StntStdAiEngMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class StntStdAiEngService {
    private final StntStdAiEngMapper stntStdAiEngMapper;

    @Transactional(readOnly = true)
    public Object findStntStdAiInitEng(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Map<String, Object> stdAiMap = stntStdAiEngMapper.findStntStdAiInitEng(paramData);
        Map<String, Object> user = stntStdAiEngMapper.findUserById(paramData);
        Map<String, Object> tcCurriculum = stntStdAiEngMapper.findTcCurriculum(paramData);

        returnMap.put("userId", MapUtils.getString(paramData, "userId"));
        returnMap.put("flnm", MapUtils.getString(user, "flnm"));
        returnMap.put("textbkId", MapUtils.getInteger(paramData, "textbkId"));
        returnMap.put("stdNm", MapUtils.getString(stdAiMap, "stdNm"));
        returnMap.put("enLrngDivIds", MapUtils.getString(stdAiMap, "enLrngDivIds"));
        returnMap.put("curriUnit1", MapUtils.getInteger(tcCurriculum, "curriUnit1"));
        returnMap.put("unitNum", MapUtils.getInteger(paramData, "unitNum"));

        return returnMap;
    }
}
