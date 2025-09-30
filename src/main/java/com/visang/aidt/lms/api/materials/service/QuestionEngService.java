package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.QuestionEngMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class QuestionEngService {
    private final QuestionEngMapper questionEngMapper;

    @Transactional(readOnly = true)
    public Object findQuestionListEng(Map<String, Object> paramData)  throws Exception {
        if (ObjectUtils.isEmpty(MapUtils.getInteger(paramData, "limitNum"))) {
            paramData.put("limitNum", 1);
        } else {
            paramData.put("limitNum", MapUtils.getIntValue(paramData, "limitNum"));
        }

        return questionEngMapper.findQuestionListEng(paramData);
    }
}
