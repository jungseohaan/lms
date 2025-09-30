package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.QuestionMapper;
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
public class QuestionService {
    private final QuestionMapper questionMapper;

    @Transactional(readOnly = true)
    public Object findQuestionList(Map<String, Object> paramData) throws Exception {
        /* //paramData ex)
            paramData.put("articleId", 3484);
            paramData.put("limitNum", 3); //1~5
            paramData.put("gbCd", 2); //1~4
        */
        if (ObjectUtils.isEmpty(MapUtils.getInteger(paramData, "limitNum"))) {
            paramData.put("limitNum", 3);
        } else {
            paramData.put("limitNum", MapUtils.getIntValue(paramData, "limitNum"));
        }

        return questionMapper.findQuestionList(paramData);
    }

    /* ex)

    //import com.visang.aidt.lms.api.materials.service.QuestionService;
    //private final QuestionService questionService;

    public Object findQuestionListSample() {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> questionMap = new LinkedHashMap<>();
        questionMap.put("articleId", 3484);
        questionMap.put("limitNum", 3); //1~5
        questionMap.put("gbCd", 2); //1~4
        returnMap.put("questionList", questionService.findQuestionList(questionMap));

        return returnMap;
    }

    */
}
