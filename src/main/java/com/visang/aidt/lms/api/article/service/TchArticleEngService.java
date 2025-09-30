package com.visang.aidt.lms.api.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.materials.mapper.QuestionEngMapper;
import com.visang.aidt.lms.api.materials.service.QuestionEngService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchArticleEngService {
    private final QuestionEngService questionEngService;

    @Transactional(readOnly = true)
    public Object findReplArticleEng(Map<String, Object> paramData) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();

        resultMap.put("orgArticleId", MapUtils.getString(paramData, "articleId"));
        resultMap.put("otherList", questionEngService.findQuestionListEng(paramData));

        paramData.remove("limitNum");

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findQstnOtherEng(Map<String, Object> paramData) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();

        /*
        List<Map<String,Object>> questionListEngList = (List<Map<String, Object>>) questionEngService.findQuestionListEng(paramData);


        List<Map<String,Object>> resultList = CollectionUtils.emptyIfNull(questionListEngList).stream()
            .map(s -> {
                //추가 로직 필요
                s.put("detailId", null);
                s.put("dtaIemId", MapUtils.getInteger(s, "articleId"));
                s.remove("articleId");
                return s;
            }).toList();

        resultMap.put("oriDetailId", MapUtils.getInteger(paramData, "detailId"));
        resultMap.put("otherList", resultList);

        return resultMap;
        */
        if (ObjectUtils.isEmpty(MapUtils.getInteger(paramData, "limitNum"))) {
            paramData.put("limitNum", 3);
        }

        resultMap.put("orgArticleId", MapUtils.getString(paramData, "articleId"));
        resultMap.put("otherList", questionEngService.findQuestionListEng(paramData));

        paramData.remove("limitNum");

        return resultMap;
    }
}
