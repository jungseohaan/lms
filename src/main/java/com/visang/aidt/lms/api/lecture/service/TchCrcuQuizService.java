package com.visang.aidt.lms.api.lecture.service;

import com.visang.aidt.lms.api.lecture.mapper.TchCrcuQuizMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchCrcuQuizService {
    private final TchCrcuQuizMapper tchCrcuQuizMapper;

    public Object createTchToolQuizForm(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuQuizMapper.createTchToolQuizForm_spotQizInfo(paramData);
        log.info("result1:{}", result1);

//        List<Map<String, Object>> qizInfoList = (List<Map<String, Object>>) paramData.get("qizInfoList");
//        for (Map<String, Object> qizInfoMap : qizInfoList) {
//            qizInfoMap.put("qizId", paramData.get("id"));
//
//            int result2 = tchCrcuQuizMapper.createTchToolQuizForm_spotQizDistract(qizInfoMap);
//            log.info("result2:{}", result2);
//
//            qizInfoMap.remove("qizId");
//            qizInfoMap.remove("id");
//        }

        returnMap.put("qizId", paramData.get("id"));
        paramData.remove("id");
        if (result1 > 0) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchToolQuizView(Map<String, Object> paramData, Integer[] qizId) throws Exception {
        List<String> studentInfoItem = Arrays.asList("qizId", "claId", "textbkId", "textbkNm", "qizNum", "qizPosScript", "resultDispAt", "anonyAt", "qizSttsCd", "qizInfoList");
        List<String> evalResultDetItem = Arrays.asList("distrNum", "distrNm");

        paramData.put("qizId", qizId);

        var spotQizDistractList = tchCrcuQuizMapper.findTchToolQuizView_spotQizDistractList(paramData);

        List<LinkedHashMap<Object, Object>> spotQizInfoList = CollectionUtils.emptyIfNull(tchCrcuQuizMapper.findTchToolQuizView_spotQizInfoList(paramData)).stream()
            .map(s -> {
                List<LinkedHashMap<Object, Object>> qizInfoList = CollectionUtils.emptyIfNull(spotQizDistractList).stream()
                    .filter(t -> StringUtils.equals(MapUtils.getString(s,"id"), MapUtils.getString(t,"qizId")))
                    .map(t -> {
                        return AidtCommonUtil.filterToMap(evalResultDetItem, t);
                    }).toList();

                s.put("qizInfoList", qizInfoList);
                return AidtCommonUtil.filterToMap(studentInfoItem, s);
            }).toList();

        return spotQizInfoList;
    }

    public Object modifyTchToolQuizStart(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<Map<String, Object>> qizList = (List<Map<String, Object>>) paramData.get("qizList");
        int result1=0;
        int index=1;

        if (ObjectUtils.isNotEmpty(qizList)) {
            int result1_1 = tchCrcuQuizMapper.removeTchToolQuizForm_spotQizDistract(qizList);
            log.info("result1_1:{}", result1_1);
        }

        for (Map<String, Object> qizListMap : qizList) {
            qizListMap.put("qizNum", index);
           // if(index==1) {
            result1 = tchCrcuQuizMapper.updateTchToolQuizForm_spotQizInfo(qizListMap);
           // } else {
           //     result1 = tchCrcuQuizMapper.insertTchToolQuizForm_spotQizInfo(qizListMap);
           // }
            index++;
            log.info("result1:{}", result1);

            List<Map<String, Object>> qizInfoList = (List<Map<String, Object>>) qizListMap.get("qizInfoList");
            for (Map<String, Object> qizMap : qizInfoList) {
                qizMap.put("qizId", qizListMap.get("qizId"));
                int result2 = tchCrcuQuizMapper.createTchToolQuizForm_spotQizDistract(qizMap);
                log.info("result2:{}", result2);
            }
        }

        if (result1 > 0) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object modifyTchToolQuizEnd(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuQuizMapper.modifyTchToolQuizEnd(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchToolQuizResult(Map<String, Object> paramData) throws Exception {

        var returnMap = tchCrcuQuizMapper.findTchToolQuizResult(paramData);
        var qizInfoList = tchCrcuQuizMapper.findTchToolQuizResult_qizInfoList(paramData);
        var qizStntInfoList = tchCrcuQuizMapper.findTchToolQuizResult_qizStntInfoList(paramData);

        returnMap.put("qizInfoList", qizInfoList);
        returnMap.put("qizStntInfoList", qizStntInfoList);

        return returnMap;
    }

    public Object removeTchToolQuizDel(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuQuizMapper.removeTchToolQuizDel_spotQizDistract(paramData);
        log.info("result1:{}", result1);

        int result2 = tchCrcuQuizMapper.removeTchToolQuizDel_spotQizResult(paramData);
        log.info("result2:{}", result2);

        int result3 = tchCrcuQuizMapper.removeTchToolQuizDel_spotQizInfo(paramData);
        log.info("result3:{}", result3);

        if (result3 > 0) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchToolQuizNav(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> qizInfoItem = Arrays.asList("qizId", "textbkId", "textbkNm", "qizSttsCd", "qizSttsNm", "anonyAt");

        returnMap.putAll(paramData);
        returnMap.put("QizInfoList", AidtCommonUtil.filterToList(qizInfoItem, tchCrcuQuizMapper.findTchToolQuizNav(paramData)));

        return returnMap;
    }

    public Object removeTchToolQuizInit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchCrcuQuizMapper.removeTchToolQuizInit_spotQizResult(paramData);
        log.info("result1:{}", result1);

        returnMap.put("resultOK", true);
        returnMap.put("resultMsg", "성공");

        return returnMap;
    }
}
