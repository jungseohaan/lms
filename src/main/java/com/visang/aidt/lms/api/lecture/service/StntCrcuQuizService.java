package com.visang.aidt.lms.api.lecture.service;

import com.visang.aidt.lms.api.lecture.mapper.StntCrcuQuizMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntCrcuQuizService {
    private final StntCrcuQuizMapper stntCrcuQuizMapper;

    @Transactional(readOnly = true)
    public Object findStntToolQuizList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> qizInfoItem = Arrays.asList("qizId", "qizNum", "qizPosScript", "submAt", "resultDispAt", "anonyAt", "qizSttsCd", "qizSttsNm");

        returnMap.putAll(paramData);
        returnMap.put("qizList", AidtCommonUtil.filterToList(qizInfoItem, stntCrcuQuizMapper.findStntToolQuizList(paramData)));
        returnMap.remove("userId");

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntToolQuizCall(Map<String, Object> paramData) throws Exception {

        List<String> qizInfoItem = Arrays.asList("qizId", "claId", "textbkId", "textbkNm", "qizNum", "qizPosScript", "resdultDispAt", "anonyAt", "qizSttsCd", "qizSttsNm");
        List<String> qizDistractItem = Arrays.asList("distrNum", "distrNm");

        var returnMap = AidtCommonUtil.filterToMap(qizInfoItem, stntCrcuQuizMapper.findStntToolQuizCall_spotQizInfo(paramData));
        returnMap.put("qizItemList", AidtCommonUtil.filterToList(qizDistractItem, stntCrcuQuizMapper.findStntToolQuizCall_spotQizDistract(paramData)));

        return returnMap;
    }

    public Object createStntToolQuizSubmit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        stntCrcuQuizMapper.findStntTollQuizHist(paramData)
                .ifPresentOrElse(stntTollQuizHist -> {
                            returnMap.put("resultOK", true);
                            returnMap.put("resultMsg", "성공");
                            returnMap.put("submdistrNum", stntTollQuizHist.get("submdistrNum"));
                        },
                        () -> {
                            int result1 = 0;
                            try {
                                result1 = stntCrcuQuizMapper.createStntToolQuizSubmit(paramData);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            log.info("result1:{}", result1);

                            if (result1 > 0) {
                                returnMap.put("resultOK", true);
                                returnMap.put("resultMsg", "성공");
                            } else {
                                returnMap.put("resultOK", false);
                                returnMap.put("resultMsg", "실패");
                            }
                        });

        return returnMap;
    }
}
