package com.visang.aidt.lms.api.assessment.service;

import com.visang.aidt.lms.api.assessment.mapper.StntSlfperEvalMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntSlfperEvalService {
    private final StntSlfperEvalMapper stntSlfperEvalMapper;

    @Transactional(readOnly = true)
    public Object findStntSlfperEvlSlfSet(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
/* 검색 조건 제외 (module_subm_at)
        String moduleSubmAt = "N";
        String moduleId = MapUtils.getString(paramData, "moduleId");
        if (ObjectUtils.isNotEmpty(moduleId)) {
            moduleSubmAt = "Y";
        }
        paramData.put("moduleSubmAt", moduleSubmAt);
*/
        paramData.put("slfPerEvlSetInfo", 1);
        var selInfoIdMap = stntSlfperEvalMapper.findStntSlfperEvlSlfSet(paramData);

        paramData.put("slfPerEvlSetInfo", 2);
        var perInfoIdMap = stntSlfperEvalMapper.findStntSlfperEvlSlfSet(paramData);

        paramData.put("selInfoId", MapUtils.getInteger(selInfoIdMap, "id"));
        paramData.put("perInfoId", MapUtils.getInteger(perInfoIdMap, "id"));

        var slList = stntSlfperEvalMapper.findStntSlfperEvlSlfSetSlList(paramData);
        //var perInfoList = stntSlfperEvalMapper.findStntSlfperEvlSlfSetPerInfoList(paramData);
        var perInfoList = stntSlfperEvalMapper.findStntSlfperEvlSlfPerinfo(paramData);
        var templtList = stntSlfperEvalMapper.findStntSlfperEvlSlfSetTempltList(paramData);
        var slfperYnMap = stntSlfperEvalMapper.findStntSlfperEvlSlfSetSlfperYn(paramData);
        var templtYnMap = stntSlfperEvalMapper.findStntSlfperEvlSlfSetTempltYn(paramData);

        returnMap.put("apraserId", MapUtils.getString(paramData, "stntId"));
        returnMap.put("selInfoId", MapUtils.getInteger(paramData, "selInfoId"));
        returnMap.put("perInfoId", MapUtils.getInteger(paramData, "perInfoId"));
        returnMap.put("slList", slList);
        returnMap.put("perInfoList", perInfoList);
        returnMap.put("templtList", templtList);
        returnMap.put("slfTotNum", slList.size());
        returnMap.put("perTotNum", perInfoList.size());

        String slfperYn = MapUtils.getString(slfperYnMap, "slfperYn", "N");
        String templtYn = MapUtils.getString(templtYnMap, "slfperYn", "N");

        if ("Y".equals(slfperYn) || "Y".equals(templtYn)) {
            slfperYn = "Y";
        }

        returnMap.put("slfperYn", slfperYn);

        returnMap.put("stSlExposAt", MapUtils.getString(selInfoIdMap, "stExposAt"));
        returnMap.put("stPltExposAt", MapUtils.getString(perInfoIdMap, "stExposAt"));

        paramData.remove("slfPerEvlSetInfo");
        paramData.remove("selInfoId");
        paramData.remove("perInfoId");

        return returnMap;
    }

    public Object createStntSlfperEvlSetSave(Map<String, Object> paramData) throws Exception {
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        String slfPerEvlNm = MapUtils.getString(paramData, "slfPerEvlNm");
        if (ObjectUtils.isEmpty(slfPerEvlNm)) {
            paramData.put("slfPerEvlNm", "-");
        }

        var returnMap = new LinkedHashMap<>();
        int createEvlSetSave = 0;

        Map<String, Object> evlSetMap = stntSlfperEvalMapper.findEvlSetSave(paramData);

        String moduleSubmAt = "N";
        String moduleId = MapUtils.getString(paramData, "moduleId");
        if (ObjectUtils.isNotEmpty(moduleId)) {
            moduleSubmAt = "Y";
        }

        try {
            if (ObjectUtils.isEmpty(evlSetMap)) {
                List<Map<String, Object>> slfEvlInfoList = (List<Map<String, Object>>) paramData.get("slfEvlInfoList");
                paramData.put("moduleSubmAt", moduleSubmAt);
                if(!ObjectUtils.isEmpty(slfEvlInfoList)) {
                    paramData.put("slfPerEvlClsfCd", 1);
                    createEvlSetSave = stntSlfperEvalMapper.createEvlSetSave(paramData);
                    log.info("createEvlSetSave:{}", createEvlSetSave);

                    for (Map<String, Object> slfEvlInfoMap : slfEvlInfoList) {
                        slfEvlInfoMap.put("setId", paramData.get("id"));
                        int createSlfEvlInfo = stntSlfperEvalMapper.createSetDetailInfo(slfEvlInfoMap);
                        log.info("createEvlSetSave:{}", createSlfEvlInfo);
                    }
                }

                List<Map<String, Object>> perEvlInfoList = (List<Map<String, Object>>) paramData.get("perEvlInfoList");
                if(!ObjectUtils.isEmpty(perEvlInfoList)) {
                    paramData.put("slfPerEvlClsfCd", 2);
                    createEvlSetSave = stntSlfperEvalMapper.createEvlSetSave(paramData);
                    log.info("createEvlSetSave:{}", createEvlSetSave);

                    for (Map<String, Object> perEvlInfoMap : perEvlInfoList) {
                        perEvlInfoMap.put("setId", paramData.get("id"));
                        int createPerEvlInfo = stntSlfperEvalMapper.createSetDetailInfo(perEvlInfoMap);
                        log.info("createEvlSetSave:{}", createPerEvlInfo);
                    }
                }
                paramData.remove("id");
                paramData.remove("slfPerEvlClsfCd");
                paramData.remove("moduleSubmAt");

                if (createEvlSetSave > 0) {
                    returnMap.put("resultOK", true);
                    returnMap.put("resultMsg", "성공");
                } else {
                    returnMap.put("resultOK", false);
                    returnMap.put("resultMsg", "실패");
                }
            } else {
                returnMap.put("resultOK", false);
                returnMap.put("resultMsg", "중복되는 데이터가 존재 합니다.");
            }
        } catch(Exception e) {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "중복되는 데이터가 존재 합니다.");
        }

        return returnMap;
    }

    public Object createStntSlfperEvlSlfSet(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<Map<String, Object>> slfEvlInfoList = (List<Map<String, Object>>) paramData.get("slfEvlInfoList");
        int createSlfEvlInfo = 0;

        if (!ObjectUtils.isEmpty(slfEvlInfoList)) {
            for (Map<String, Object> slfEvlInfoMap : slfEvlInfoList){
                createSlfEvlInfo = stntSlfperEvalMapper.createEvlSlfSave(slfEvlInfoMap);
                log.info("createSlfEvlInfo:{}", createSlfEvlInfo);

                Map<String, Object> evlSetInfoMap = stntSlfperEvalMapper.findEvlSetInfo(slfEvlInfoMap);
                int gbCd = 0;
                gbCd = MapUtils.getInteger(evlSetInfoMap, "gbCd");

                evlSetInfoMap.putAll(slfEvlInfoMap);

                if (gbCd == 1) {
                    //향후 재정의 해야 함.
                    int result8 = stntSlfperEvalMapper.modifyEvlSlfSaveSetInfo(evlSetInfoMap);
                    log.info("result8:{}", result8);
                } else if (gbCd == 2) {
                    int result3 = stntSlfperEvalMapper.modifyEvlSlfSaveTRI(evlSetInfoMap);
                    log.info("result3:{}", result3);
                    int result4 = stntSlfperEvalMapper.modifyEvlSlfSaveSetInfoTask(evlSetInfoMap);
                    log.info("result4:{}", result4);
                } else if (gbCd == 3) {
                    int result5 = stntSlfperEvalMapper.modifyEvlSlfSaveERI(evlSetInfoMap);
                    log.info("result5:{}", result5);
                    int result6 = stntSlfperEvalMapper.modifyEvlSlfSaveSetInfoEvl(evlSetInfoMap);
                    log.info("result6:{}", result6);
                }

                if (ObjectUtils.isNotEmpty(MapUtils.getString(evlSetInfoMap, "moduleId"))) {
                    int result7 = stntSlfperEvalMapper.modifyEvlSlfSaveSetInfo(evlSetInfoMap);
                    log.info("result7:{}", result7);
                }

                slfEvlInfoMap.remove("id");
            }
        }

        List<Map<String, Object>> perEvlInfoList = (List<Map<String, Object>>) paramData.get("perEvlInfoList");
        int createPerEvlInfo = 0;

        if (!ObjectUtils.isEmpty(perEvlInfoList)) {
            for (Map<String, Object> perEvlInfoMap : perEvlInfoList) {
                List<Map<String, Object>> perEvlIArrList = (List<Map<String, Object>>) perEvlInfoMap.get("perEvlIArrList");

                perEvlInfoMap.put("selInfoId", MapUtils.getInteger(perEvlInfoMap, "perInfoId"));
                Map<String, Object> perSetInfoMap = stntSlfperEvalMapper.findEvlSetInfo(perEvlInfoMap);
                int gbCd = 0;
                gbCd = MapUtils.getInteger(perSetInfoMap, "gbCd");
                for (Map<String, Object> perEvlIArrMap : perEvlIArrList) {
                    perEvlIArrMap.put("perInfoId", perEvlInfoMap.get("perInfoId"));
                    perEvlIArrMap.put("slfPerEvlDetailId", perEvlInfoMap.get("slfPerEvlDetailId"));
                    perEvlIArrMap.put("taskId", perSetInfoMap.get("taskId"));
                    perEvlIArrMap.put("evlId", perSetInfoMap.get("evlId"));
                    perEvlIArrMap.put("selInfoId", perEvlIArrMap.get("perInfoId"));

                    createPerEvlInfo = createPerEvlInfo + stntSlfperEvalMapper.createEvlSlfSave(perEvlIArrMap);

                    if (gbCd == 1) {
                        //향후 재정의 해야 함.
                        int result8 = stntSlfperEvalMapper.modifyEvlSlfSaveSetInfo(perEvlIArrMap);
                        log.info("result8:{}", result8);
                    } else if (gbCd == 2) {
                        int result13 = stntSlfperEvalMapper.modifyEvlPerSaveTRI(perEvlIArrMap);
                        log.info("result13:{}", result13);
                        int result14 = stntSlfperEvalMapper.modifyEvlPerSaveSetInfoTask(perEvlIArrMap);
                        log.info("result14:{}", result14);
                    } else if (gbCd == 3) {
                        int result15 = stntSlfperEvalMapper.modifyEvlPerSaveERI(perEvlIArrMap);
                        log.info("result15:{}", result15);
                        int result16 = stntSlfperEvalMapper.modifyEvlPerSaveSetInfoEvl(perEvlIArrMap);
                        log.info("result16:{}", result16);
                    }

                    perEvlIArrMap.remove("perInfoId");
                    perEvlIArrMap.remove("slfPerEvlDetailId");
                    perEvlIArrMap.remove("taskId");
                    perEvlIArrMap.remove("evlId");
                    perEvlIArrMap.remove("id");
                    perEvlIArrMap.remove("selInfoId");
                }
                log.info("createPerEvlInfoInner:{}", createPerEvlInfo);

                perSetInfoMap.put("selInfoId", MapUtils.getInteger(perEvlInfoMap, "perInfoId"));
                if (ObjectUtils.isNotEmpty(MapUtils.getString(perSetInfoMap, "moduleId"))) {
                    int result17 = stntSlfperEvalMapper.modifyEvlSlfSaveSetInfo(perSetInfoMap);
                    log.info("result17:{}", result17);
                }
                perEvlInfoMap.remove("selInfoId");
            }
        }

        if (createSlfEvlInfo > 0 || createPerEvlInfo > 0) {

            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSlfperEvlSlfPerinfo(Map<String, Object> paramData) throws Exception {
        return stntSlfperEvalMapper.findStntSlfperEvlSlfPerinfo(paramData);
    }

    @Transactional(readOnly = true)
    public Object findStntSlfperEvlResultDetailList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> slfPerResultInfoItem = Arrays.asList("stntId", "flnm", "evlAswAt");

        // 평가정보
        List<Map> slfperEvlResultDetailList = stntSlfperEvalMapper.findStntSlfperEvlResultDetailList(paramData);

        // 자기평가정보
        List<LinkedHashMap<Object, Object>> slfResultList = CollectionUtils.emptyIfNull(slfperEvlResultDetailList).stream()
            .filter(r -> StringUtils.equals(MapUtils.getString(r,"slfPerEvlClsfCd"), "1"))
            .map(r -> {
                return AidtCommonUtil.filterToMap(slfPerResultInfoItem, r);
            }).toList();
        String slfEvlSetAt = slfResultList.isEmpty() ? "N" : "Y";

        // 동료평가정보
        List<LinkedHashMap<Object, Object>> perResultList = CollectionUtils.emptyIfNull(slfperEvlResultDetailList).stream()
            .filter(r -> StringUtils.equals(MapUtils.getString(r,"slfPerEvlClsfCd"), "2"))
            .map(r -> {
                return AidtCommonUtil.filterToMap(slfPerResultInfoItem, r);
            }).toList();
        String perEvlSetAt = perResultList.isEmpty() ? "N" : "Y";

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("slfEvlSetAt",slfEvlSetAt);
        respMap.put("perEvlSetAt",perEvlSetAt);
        respMap.put("slfResultList",slfResultList);
        respMap.put("perResultList",perResultList);
        return respMap;
    }
}
