package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.TchMdulHdwrtMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchMdulHdwrtService {
    private final TchMdulHdwrtMapper tchMdulHdwrtMapper;

    public Object createTchEvalMdulHdwrtSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        int result1 = tchMdulHdwrtMapper.createTchEvalMdulHdwrtSave(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("hdwrtId", MapUtils.getInteger(paramData,"id"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }
        paramData.remove("id");

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchEvalMdulHdwrtView(Map<String, Object> paramData) throws Exception {
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> hdwrtItem = Arrays.asList("hdwrtId", "evlId", "moduleId", "subId", "hdwrtCn");

        return AidtCommonUtil.filterToMap(hdwrtItem, tchMdulHdwrtMapper.findTchEvalMdulHdwrtView(paramData));
    }

    public Object createTchEvalMdulHdwrtShare(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchMdulHdwrtMapper.createTchEvalMdulHdwrtShare(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("hdwrtId", MapUtils.getInteger(paramData,"hdwrtId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createTchHomewkMdulHdwrtSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        int result1 = tchMdulHdwrtMapper.createTchHomewkMdulHdwrtSave(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("hdwrtId", MapUtils.getInteger(paramData,"id"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }
        paramData.remove("id");

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchHomewkMdulHdwrtView(Map<String, Object> paramData) throws Exception {
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> hdwrtItem = Arrays.asList("hdwrtId", "taskId", "moduleId", "subId", "hdwrtCn");

        return AidtCommonUtil.filterToMap(hdwrtItem, tchMdulHdwrtMapper.findTchHomewkMdulHdwrtView(paramData));
    }

    public Object createTchHomewkMdulHdwrtShare(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchMdulHdwrtMapper.createTchHomewkMdulHdwrtShare(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("hdwrtId", MapUtils.getInteger(paramData,"hdwrtId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }
}
