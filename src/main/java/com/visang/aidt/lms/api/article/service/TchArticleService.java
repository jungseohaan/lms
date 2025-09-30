package com.visang.aidt.lms.api.article.service;

import com.visang.aidt.lms.api.materials.mapper.QuestionMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchArticleService {
    private final QuestionMapper questionMapper;

    @Transactional(readOnly = true)
    public Object findQuestionList(Map<String, Object> paramData) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();

        List<String> infoItem = Arrays.asList("id", "subId", "thumbnail", "difyNm", "questionTypeNm");

        List<LinkedHashMap<Object, Object>> list = AidtCommonUtil.filterToList(infoItem, questionMapper.findQuestionList(paramData));

        LinkedHashMap<Object, Object> resultList = new LinkedHashMap<Object, Object>();
        List<LinkedHashMap<Object, Object>> resultList1 = new ArrayList<>();

        if(!list.isEmpty()){
            Map<Object, Object> info = list.get(0);
            resultList.put("articleId", info.get("id"));
            resultList.put("subId", info.get("subId"));
            resultList.put("thumbnail", info.get("thumbnail"));
            resultList.put("difyNm", info.get("difyNm"));
            resultList.put("questionTypeNm", info.get("questionTypeNm"));
            resultList1.add(resultList);
        }

        resultMap.put("orgArticleId",paramData.get("articleId"));
        resultMap.put("otherList",resultList1);

        return resultMap;
    }

}