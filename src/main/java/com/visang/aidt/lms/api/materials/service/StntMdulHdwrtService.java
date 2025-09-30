package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.materials.mapper.StntMdulHdwrtMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntMdulHdwrtService {
    private final StntMdulHdwrtMapper stntMdulHdwrtMapper;

    public Object createStntEvalMdulHdwrtSave(Map<String, Object> paramData) throws Exception {

        final int MAX_SIZE_BYTES = 10 * 1024 * 1024; // 16MB
        final int MAX_SIZE_MB = MAX_SIZE_BYTES / (1024 * 1024);

        var returnMap = new LinkedHashMap<>();
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }
        // 손글씨 내용 길이 검증
        String hdwrtCn = MapUtils.getString(paramData, "hdwrtCn");
        if (StringUtils.hasText(hdwrtCn)) {
            int contentSize = hdwrtCn.getBytes().length;

            if (contentSize > MAX_SIZE_BYTES) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "Data too large. Max " + MAX_SIZE_MB + "MB.");
                returnMap.put("size", contentSize);
                returnMap.put("limitSize", MAX_SIZE_MB);
                return returnMap;
            }
        }

        int updateCount = stntMdulHdwrtMapper.modifyStntEvalMdulHdwrtSave(paramData);
        log.info("updateCount: {}", updateCount);

        if (updateCount > 0) {
            returnMap.put("resultDetailId", MapUtils.getInteger(paramData, "resultDetailId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            if (StringUtils.hasText(hdwrtCn)) {
                returnMap.put("size", hdwrtCn.getBytes().length);
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        paramData.remove("resultDetailId");

        return returnMap;
    }


    @Transactional(readOnly = true)
    public Object findStntEvalMdulHdwrtView(Map<String, Object> paramData) throws Exception {
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> hdwrtItem = Arrays.asList("resultDetailId", "hdwrtCn");

        return AidtCommonUtil.filterToMap(hdwrtItem, stntMdulHdwrtMapper.findStntEvalMdulHdwrtView(paramData));
    }

    @Transactional(readOnly = true)
    public Object findStntEvalMdulHdwrtShareList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> hdwrtItem = Arrays.asList("hdwrtDetailId", "hdwrtId", "hdwrtSeq", "hdwrtImgUrl");
        returnMap.put("hdwrtList", AidtCommonUtil.filterToList(hdwrtItem, stntMdulHdwrtMapper.findStntEvalMdulHdwrtShareList(paramData)));

        return returnMap;
    }

    public Object createStntHomewkMdulHdwrtSave(Map<String, Object> paramData) throws Exception {

        final int MAX_SIZE_BYTES = 10 * 1024 * 1024; // 16MB
        final int MAX_SIZE_MB = MAX_SIZE_BYTES / (1024 * 1024);

        var returnMap = new LinkedHashMap<>();
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        String hdwrtCn = MapUtils.getString(paramData, "hdwrtCn");
        if (StringUtils.hasText(hdwrtCn)) {
            int contentSize = hdwrtCn.getBytes().length;
            // 손글씨 내용 길이 검증
            if (contentSize > MAX_SIZE_BYTES) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "Data too large. Max " + MAX_SIZE_MB + "MB.");
                returnMap.put("size", contentSize);
                returnMap.put("limitSize", MAX_SIZE_MB);
                return returnMap;
            }
        }

        int updateCount = stntMdulHdwrtMapper.modifyStntHomewkMdulHdwrtSave(paramData);
        log.info("updateCount: {}", updateCount);

        if (updateCount > 0) {
            returnMap.put("resultDetailId", MapUtils.getInteger(paramData, "resultDetailId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            if (StringUtils.hasText(hdwrtCn)) {
                returnMap.put("size", hdwrtCn.getBytes().length);
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        paramData.remove("resultDetailId");

        return returnMap;
    }


    @Transactional(readOnly = true)
    public Object findStntHomewkMdulHdwrtView(Map<String, Object> paramData) throws Exception {
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> hdwrtItem = Arrays.asList("resultDetailId", "hdwrtCn");

        return AidtCommonUtil.filterToMap(hdwrtItem, stntMdulHdwrtMapper.findStntHomewkMdulHdwrtView(paramData));
    }

    @Transactional(readOnly = true)
    public Object findStntHomewkMdulHdwrtShareList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> hdwrtItem = Arrays.asList("hdwrtDetailId", "hdwrtId", "hdwrtSeq", "hdwrtImgUrl");
        returnMap.put("hdwrtList", AidtCommonUtil.filterToList(hdwrtItem, stntMdulHdwrtMapper.findStntHomewkMdulHdwrtShareList(paramData)));

        return returnMap;
    }

}
