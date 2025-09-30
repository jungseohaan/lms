package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.assessment.mapper.TchEvalMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchSlfperEvalMapper;
import com.visang.aidt.lms.api.assessment.service.TchSlfperEvalService;
import com.visang.aidt.lms.api.lecture.mapper.TchCrcuTabMapper;
import com.visang.aidt.lms.api.lecture.service.TchCrcuTabService;
import com.visang.aidt.lms.api.materials.mapper.TchMaterialsMapper;
import com.visang.aidt.lms.api.materials.mapper.TchStdMapper;
import com.visang.aidt.lms.api.mq.mapper.bulk.StdLessonReconMapper;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchStdService {
    private final TchStdMapper tchStdMapper;
    private final TchEvalMapper tchEvalMapper;
    private final TchCrcuTabMapper tchCrcuTabMapper;

    private final TchSlfperEvalService tchSlfperEvalService;
    private final TchSlfperEvalMapper tchSlfperEvalMapper;
    private final TchCrcuTabService tchCrcuTabService;

    private final StdLessonReconMapper stdLessonReconMapper;

    private final TchMaterialsServcie tchMaterialsServcie;

    @Transactional(readOnly = true)
    public Object findTchStdList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> stdListItemN = Arrays.asList("no", "id", "eamMth", "setsId", "textbkNm", "tmprStrgAt", "stdDatNm", "crculId", "textbkTabId", "stdCategoryCd", "stdCategoryNm", "regDt", "targetCnt" ,"submitCnt", "eakCnt", "studyCnt");
        List<String> stdListItemY = Arrays.asList("no", "id", "eamMth", "setsId", "textbkNm", "tmprStrgAt", "stdDatNm", "crculId", "textbkTabId", "stdCategoryCd", "stdCategoryNm", "regDt");

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        var stdList = (List<Map>) tchStdMapper.findTchStdList(pagingParam);

        if (!stdList.isEmpty()) {
            total = (long) stdList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(stdList, pageable, total);

        if ("N".equals(paramData.get("tmprStrgAt"))) {
            returnMap.put("stdList", AidtCommonUtil.filterToList(stdListItemN, stdList));
        } else {
            returnMap.put("stdList", AidtCommonUtil.filterToList(stdListItemY, stdList));
        }
        returnMap.put("page",page);

        return returnMap;
    }

    public Object removeTchStdDel(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Map<String, Object> eakAtMap  = tchStdMapper.findEakAtCnt(paramData);
        int cnt = MapUtils.getInteger(eakAtMap, "cnt");

        if (cnt > 0) {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "학습 이력이 있는 교과자료는 삭제할 수 없습니다.");
        } else {
            Map<String, Object> stdDtaInfoMap = tchStdMapper.findStdDtaInfoById(paramData);

            int result4 = tchStdMapper.removeTchStdDel_tabInfo(stdDtaInfoMap);
            int result3 = tchStdMapper.removeTchStdDel_stdDtaResultDetail(paramData);
            int result2 = tchStdMapper.removeTchStdDel_stdDtaResultInfo(paramData);
            int result1 = tchStdMapper.removeTchStdDel_stdDtaInfo(paramData);

            List<Map> tabInfoMap = tchStdMapper.findTabInfo(stdDtaInfoMap);

            if (ObjectUtils.isEmpty(tabInfoMap)) {
                int result0 = tchStdMapper.modifyTcCurriculum(stdDtaInfoMap);
                log.info("result0:{}", result0);
            }

            log.info("result4:{}", result4);
            log.info("result3:{}", result3);
            log.info("result2:{}", result2);
            log.info("result1:{}", result1);

            returnMap.put("stdId", paramData.get("stdId"));
            if (result1 > 0 || result2 > 0 || result3 > 0) {
                returnMap.put("resultOK", true);
                returnMap.put("resultMsg", "성공");
            } else {
                returnMap.put("resultOK", false);
                returnMap.put("resultMsg", "실패");
            }
        }

        return returnMap;
    }

    public Object createTchStd(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchStdMapper.createTchStd(paramData);
        log.info("result1:{}", result1);
        log.info("createTchStd INSERT id:{}", paramData.get("id"));

        Object stdIdObj = paramData.get("id");
        if(stdIdObj == null) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "std_id 값이 없습니다.");
            return returnMap;
        }

        returnMap.put("stdId", paramData.get("id"));
        paramData.remove("id");

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchStdReadInfo(Map<String, Object> paramData) throws Exception {
        List<String> stdDtaInfoItem = Arrays.asList("stdId", "wrterId", "claId", "textbkId", "setsId", "eamScp", "eamMth", "eamMthNm", "stdDatNm", "textbkTabNm", "crculId", "bbsSvAt", "bbsNm", "tag", "cocnrAt");
        return AidtCommonUtil.filterToMap(stdDtaInfoItem, tchStdMapper.findTchStdReadInfo(paramData));
    }

    public Object createTchStdSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("stdId", paramData.get("stdId"));

        Map<String, Object> stdDtaInfoMap = tchStdMapper.findStdDtaInfoById(paramData);

        if (ObjectUtils.isEmpty(stdDtaInfoMap)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
            return returnMap;
        }

        String setsId = MapUtils.getString(paramData, "setsId");
        if (ObjectUtils.isEmpty(setsId)) {
            //paramData.put("setsId", MapUtils.getString(stdDtaInfoMap, "setsId"));
            setsId = MapUtils.getString(stdDtaInfoMap, "setsId");
        } else {
            //stdDtaInfoMap.put("setsId", MapUtils.getString(paramData, "setsId"));
            setsId = MapUtils.getString(paramData, "setsId");
        }

        paramData.put("setsId", setsId);

        //insert sets tables
        Map<String, Object> setsInsertParamMapNewSetId = new HashMap<>();
        int result0NewSetId = tchStdMapper.createTchStdSaveSets2(paramData);
        log.info("result0:{}", result0NewSetId);

        setsInsertParamMapNewSetId.put("newSetsid", MapUtils.getString(paramData, "newSetsid"));
        setsInsertParamMapNewSetId.put("oldSetsId", MapUtils.getString(paramData, "setsId"));
        setsInsertParamMapNewSetId.put("wrterId", MapUtils.getString(paramData, "wrterId"));

        int result2NewSetId = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMapNewSetId);
        log.info("result2:{}", result2NewSetId);

        int result3NewSetId = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMapNewSetId);
        log.info("result3:{}", result3NewSetId);

        int result4NewSetId = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMapNewSetId);
        log.info("result4:{}", result4NewSetId);

        int result6NewSetId = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMapNewSetId);
        log.info("result6:{}", result6NewSetId);

        paramData.put("setsId", MapUtils.getString(paramData, "newSetsid"));
        stdDtaInfoMap.put("setsId", MapUtils.getString(paramData, "newSetsid"));

        int result1 = tchStdMapper.modifyTchStdSave_stdDtaInfo(paramData);
        log.info("result1:{}", result1);

        tchEvalMapper.increaseModuleUseCnt(stdDtaInfoMap);

        Map<String, Object> setsInsertParamMap = new HashMap<>();
        if (result1 > 0) {
            Integer selTabId = MapUtils.getInteger(paramData, "selTabId");
            if (!ObjectUtils.isEmpty(selTabId)) {
                int updateTabSeq = tchStdMapper.modifyTchStdSave_tabInfo(paramData);
                log.info("updateTabSeq:{}", updateTabSeq);
            }

            int resultTabInfo = tchStdMapper.createTchStdSave_tabInfo(paramData);
            int tabId = MapUtils.getInteger(paramData, "id");
            log.info("resultTabInfo:{}", resultTabInfo);

            int result1_1 = tchStdMapper.modifyTchStdSave_stdDtaInfo_tabId(paramData);
            log.info("result1_1:{}", result1_1);


            if ("Y".equals(paramData.get("bbsSvAt"))) {
                //insert sets tables
                int result0 = tchStdMapper.createTchStdSaveSets(paramData);
                log.info("result0:{}", result0);

                setsInsertParamMap.put("newSetsid", MapUtils.getString(paramData, "newSetsid"));
                setsInsertParamMap.put("oldSetsId", stdDtaInfoMap.get("setsId"));

                int result2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
                log.info("result2:{}", result2);

                int result3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
                log.info("result3:{}", result3);

                int result4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
                log.info("result4:{}", result4);

                int result6 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
                log.info("result6:{}", result6);

                setsInsertParamMap.put("stdId", paramData.get("stdId"));
                int result5 =  tchStdMapper.modifyTchStdSaveBbsSetId(setsInsertParamMap);
                log.info("result5:{}", result5);
            }

            int result7 = tchStdMapper.removeTchStdSaveSDRD(paramData);
            log.info("result7:{}", result7);

            int result8 = tchStdMapper.removeTchStdSaveSDRI(paramData);
            log.info("result8:{}", result8);

            int result9 = tchStdMapper.createTchStdSaveSDRI(paramData);
            log.info("result9:{}", result9);

            int result10 = tchStdMapper.createTchStdSaveSDRD(paramData);
            log.info("result10:{}", result10);

            paramData.put("resultCountCreateSDRD", result10);
            int result11 =  tchStdMapper.modifyTchStdSaveEEN(paramData);
            log.info("result11:{}", result11);

            paramData.remove("id");
            paramData.remove("resultCountCreateSDRD");

            Map<String, Object> slfEvlInfo = (Map<String, Object>) paramData.get("slfEvlInfo");
            Map<String, Object> perEvlInfo = (Map<String, Object>) paramData.get("perEvlInfo");

            int slfEvlInfoId = 0;
            if (!ObjectUtils.isEmpty(slfEvlInfo)) {
                slfEvlInfo.put("tabId", tabId);
                Object resultSlfEvlInfo = tchSlfperEvalService.saveTchSlfperEvlSet(slfEvlInfo);
                log.info("resultSlfEvlInfo:{}", resultSlfEvlInfo);
                slfEvlInfoId = MapUtils.getInteger(slfEvlInfo, "id", 0);
            }

            int perEvlInfoId = 0;
            if (!ObjectUtils.isEmpty(perEvlInfo)) {
                perEvlInfo.put("tabId", tabId);
                Object resultPerEvlInfo = tchSlfperEvalService.saveTchSlfperEvlSet(perEvlInfo);
                log.info("resultPerEvlInfo:{}", resultPerEvlInfo);
                perEvlInfoId = MapUtils.getInteger(perEvlInfo, "id", 0);
            }

            if ("Y".equals(paramData.get("bbsSvAt"))) {
                if (slfEvlInfoId != 0) {
                    setsInsertParamMap.put("slfPerEvlSetId", slfEvlInfoId);
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }

                if (perEvlInfoId != 0) {
                    setsInsertParamMap.put("slfPerEvlSetId", perEvlInfoId);
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }
            }

            int result12 =  tchStdMapper.modifyTchStdSaveCrcul(paramData);
            log.info("result12:{}", result12);

            returnMap.put("tabId", tabId);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createTchStdLastPageSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        int result = 0;
        String flag = "";

        if (paramData.get("wrterId") == null || ("").equals(paramData.get("wrterId"))) {
            returnMap.put("resultMsg", "wrterId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("claId") == null || ("").equals(paramData.get("claId"))) {
            returnMap.put("resultMsg", "claId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("textbkId") == null || ("").equals(paramData.get("textbkId"))) {
            returnMap.put("resultMsg", "textbkId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("scrnSeCd") == null || ("").equals(paramData.get("scrnSeCd"))) {
            returnMap.put("resultMsg", "scrnSeCd를 입력해주세요");
            return returnMap;
        }

        // 1. 저장여부 확인
        Map info  = tchStdMapper.findTchStdLastPagYN(paramData);

        if(MapUtils.isEmpty(info)) {
            result = tchStdMapper.createTchStdLastPageSave(paramData);
            flag = "저장";
        } else {
            result = tchStdMapper.modifyTchStdLastPageSave(paramData);
            flag = "수정";
        }

        if(result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", flag + "완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", flag + "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchStdLastPageCall(Map<String, Object> paramData) throws Exception {
        return tchStdMapper.findTchStdLastPageCall(paramData);
    }

    public Object createTchStdMSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("stdId", paramData.get("stdId"));
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        Map<String, Object> stdDtaInfoMap = tchStdMapper.findStdDtaInfoById(paramData);
        if (ObjectUtils.isEmpty(stdDtaInfoMap)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "학습자료정보가 없습니다.");
            return returnMap;
        }

        Map<String, Object> stdDtaResultDetailMap = tchStdMapper.findstdDtaResultDetailByStdId(paramData);
        if (MapUtils.isNotEmpty(stdDtaResultDetailMap)) {
            returnMap.put("resultOk", false);

            String metaVal = MapUtils.getString(stdDtaResultDetailMap, "val", "");
            if ("게임".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 게임은 편집할 수 없습니다.");
            } else if ("교과자료".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 교과자료는 편집할 수 없습니다.");
            } else {
                returnMap.put("resultMsg", "학습이력이 있는 "+metaVal+"은(는) 편집할 수 없습니다.");
            }

            return returnMap;
        }

        String tmprStrgAt = MapUtils.getString(stdDtaInfoMap, "tmprStrgAt", "N");
        String bbsSvAt = MapUtils.getString(stdDtaInfoMap, "bbsSvAt", "N");

        if ("Y".equals(tmprStrgAt)) {
            tchStdMapper.modifyStdDtaInfoSetsId(paramData);
        } else if ("N".equals(tmprStrgAt)) {
            Map<String, Object> subMitCntMap = tchStdMapper.findSubMitCnt(paramData);
            int subMitCnt = MapUtils.getIntValue(subMitCntMap, "cnt", 0);
            if (subMitCnt > 0) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "답안제출이력이 존재 합니다.");
                return returnMap;
            }

            tchStdMapper.modifyStdDtaInfoSetsId(paramData);
            //subMitCntMap.put("slfPerSetsId", MapUtils.getString(subMitCntMap, "setsId"));
            //subMitCntMap.put("setsId", MapUtils.getString(paramData, "setsId"));

            stdDtaInfoMap.put("setsId", MapUtils.getString(paramData, "setsId"));


            int result7 = tchStdMapper.removeTchStdSaveSDRD(paramData);
            log.info("result7:{}", result7);

            int result8 = tchStdMapper.removeTchStdSaveSDRI(paramData);
            log.info("result8:{}", result8);

            int result9 = tchStdMapper.createTchStdSaveSDRI(paramData);
            log.info("result9:{}", result9);

            int result10 = tchStdMapper.createTchStdSaveSDRD(paramData);
            log.info("result10:{}", result10);

            tchEvalMapper.increaseModuleUseCnt(stdDtaInfoMap);

            int result11 =  tchStdMapper.modifyTchStdSaveEEN(paramData);
            log.info("result11:{}", result11);

            tchStdMapper.modifyTabInfoSetsId(stdDtaInfoMap);

            Map<String, Object> setsInsertParamMap = new HashMap<>();
            if ("Y".equals(bbsSvAt)) {
                //delete sets tables
                int removeSetsTablesCnt = tchCrcuTabMapper.removeTchCrcuTabChginfo_setsTables(stdDtaInfoMap);
                log.info("removeSetsTablesCnt:{}", removeSetsTablesCnt);

                int removeSetsCnt = tchCrcuTabMapper.removeTchCrcuTabChginfo_sets(stdDtaInfoMap);
                log.info("removeSetsCnt:{}", removeSetsCnt);

                //insert sets tables
                //기존 bbsSetsId 값으로 세트지를 생성해야한다. (2024.05.28)
                setsInsertParamMap.put("createdByBbsSetsId", "Y");
                setsInsertParamMap.put("stdId", paramData.get("stdId"));
                setsInsertParamMap.put("bbsSetsId", stdDtaInfoMap.get("bbsSetsId"));
                setsInsertParamMap.put("oldSetsId", stdDtaInfoMap.get("setsId"));

                int result0 = tchStdMapper.createTchStdSaveSets(setsInsertParamMap);
                log.info("result0:{}", result0);

                //setsInsertParamMap.put("newSetsid", MapUtils.getString(paramData, "newSetsid"));
                //setsInsertParamMap.put("oldSetsId", stdDtaInfoMap.get("setsId"));

                int result2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
                log.info("result2:{}", result2);

                int result3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
                log.info("result3:{}", result3);

                int result4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
                log.info("result4:{}", result4);

                int result6 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
                log.info("result6:{}", result6);

                //기존 bbsSetsId 값으로 세트지를 생성했기 때문에 업데이트가 불필요함. (2024.05.28)
                //setsInsertParamMap.put("stdId", paramData.get("stdId"));
                //int result5 =  tchStdMapper.modifyTchStdSaveBbsSetId(setsInsertParamMap);
                //log.info("result5:{}", result5);

                //기존 bbsSetsId 값으로 세트지를 생성했기 때문에 자기/동료평가 등록이 불필요함. (2024.05.28)
                /*
                List<Map> findSlfPerEvlSetInfoList = tchStdMapper.findSlfPerEvlSetInfo(stdDtaInfoMap);

                for (Map map : findSlfPerEvlSetInfoList) {
                    setsInsertParamMap.put("slfPerEvlSetId", MapUtils.getInteger(map, "id"));
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }
                */
            }
        }
        paramData.remove("newSetsid");

        return returnMap;
    }

    public Object tchStdLrnHistCheck(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "학습자료정보가 없습니다.");

        Map<String, Object> stdDtaResultDetailMap = tchStdMapper.findstdDtaResultDetailByStdId(paramData);
        if (MapUtils.isNotEmpty(stdDtaResultDetailMap)) {
            returnMap.put("resultOk", true);

            String metaVal = MapUtils.getString(stdDtaResultDetailMap, "val", "");
            if ("게임".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 게임은 편집할 수 없습니다.");
            } else if ("교과자료".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 교과자료는 편집할 수 없습니다.");
            } else {
                returnMap.put("resultMsg", "학습이력이 있는 " + metaVal + "은(는) 편집할 수 없습니다.");
            }
        }
        return returnMap;
    }

    public Object tchTabStdLrnHistCheck(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "학습자료정보가 없습니다.");

        Map<String, Object> stdDtaResultDetailMap = tchStdMapper.findstdDtaResultDetailByTabId(paramData);
        if (MapUtils.isNotEmpty(stdDtaResultDetailMap)) {
            returnMap.put("resultOk", true);

            String metaVal = MapUtils.getString(stdDtaResultDetailMap, "val", "");
            if ("게임".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 게임은 편집할 수 없습니다.");
            } else if ("교과자료".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 교과자료는 편집할 수 없습니다.");
            } else {
                returnMap.put("resultMsg", "학습이력이 있는 " + metaVal + "은(는) 편집할 수 없습니다.");
            }
        }
        return returnMap;
    }

    public Object createTchTabStdMSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("tabId", paramData.get("tabId"));
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        Map<String, Object> tabInfoMap = tchStdMapper.findTabInfoById(paramData);
        if (ObjectUtils.isEmpty(tabInfoMap)) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "탭정보가 없습니다.");
            return returnMap;
        }

        Map<String, Object> stdDtaResultDetailMap = tchStdMapper.findstdDtaResultDetailByTabId(paramData);
        if (MapUtils.isNotEmpty(stdDtaResultDetailMap)) {
            returnMap.put("resultOk", false);

            String metaVal = MapUtils.getString(stdDtaResultDetailMap, "val", "");
            if ("게임".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 게임은 편집할 수 없습니다.");
            } else if ("교과자료".equals(metaVal)) {
                returnMap.put("resultMsg", "학습이력이 있는 교과자료는 편집할 수 없습니다.");
            } else {
                returnMap.put("resultMsg", "학습이력이 있는 "+metaVal+"은(는) 편집할 수 없습니다.");
            }

            return returnMap;
        }

        String tabId = MapUtils.getString(paramData, "tabId");
        if(tabId == null || tabId.isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "tabId가 null이거나 비어있습니다.");
            return returnMap;
        }
        String setsId = MapUtils.getString(paramData, "setsId");
        if(setsId == null || setsId.isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "setsId가 null이거나 비어있습니다.");
            return returnMap;
        }


        tabInfoMap.put("textbkTabId", tabId);
        tabInfoMap.put("setsId", setsId);

        int result1 = tchStdMapper.modifyTabInfoSetsId(tabInfoMap);
        log.info("result1:{}", result1);

        int result2 = tchStdMapper.removeTchTabStdSaveSDRD(paramData);
        log.info("result2:{}", result2);

        int result3 = tchStdMapper.removeTchTabStdSaveSDRI(paramData);
        log.info("result3:{}", result3);

        int result4 = tchStdMapper.createTchTabStdSaveSDRI(paramData);
        log.info("result4:{}", result4);

        int result5 = tchStdMapper.createTchTabStdSaveSDRD(paramData);
        log.info("result5:{}", result5);

        tchEvalMapper.increaseModuleUseCnt(tabInfoMap);

        return returnMap;
    }

    public Object createTchStdUseSharedCreate(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("stdId", null);
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "저장완료");

        Map<String, Object> stdDtaInfoMap = tchStdMapper.findStdDtaInfo(paramData);
        if (stdDtaInfoMap == null || stdDtaInfoMap.isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
            return returnMap;
        }

        /*
        //insert sets tables
        Map<String, Object> setsInsertParamMap = new HashMap<>();
        int result0 = tchStdMapper.createTchStdSaveSets2(stdDtaInfoMap);
        log.info("result0:{}", result0);

        setsInsertParamMap.put("newSetsid", MapUtils.getString(stdDtaInfoMap, "newSetsid"));
        setsInsertParamMap.put("oldSetsId", MapUtils.getString(stdDtaInfoMap, "setsId"));

        System.err.println(setsInsertParamMap);

        int result2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
        log.info("result2:{}", result2);

        int result3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
        log.info("result3:{}", result3);

        int result4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
        log.info("result4:{}", result4);

        int result6 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
        log.info("result6:{}", result6);
        */


        stdDtaInfoMap.put("eamMth",7);
        //stdDtaInfoMap.put("setsId",MapUtils.getString(stdDtaInfoMap, "newSetsid"));

        Map<String, Object> createTchStdReturnMap = (Map<String, Object>) createTchStd(stdDtaInfoMap);

        if ((Boolean) createTchStdReturnMap.get("resultOk")) {
            stdDtaInfoMap.put("crculId", paramData.get("crculId"));
            stdDtaInfoMap.put("bbsSvAt", "N");
            stdDtaInfoMap.put("selTabId", paramData.get("selTabId"));
            stdDtaInfoMap.put("stdId", createTchStdReturnMap.get("stdId"));

            returnMap.put("stdId",createTchStdReturnMap.get("stdId"));

            Map<String, Object> createTchStdSaveReturnMap = (Map<String, Object>) createTchStdSave(stdDtaInfoMap);

            if (!((Boolean) createTchStdSaveReturnMap.get("resultOk"))) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "저장실패");
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }

    public Object createTchStdUseSetCreate(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("stdId", null);
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "저장완료");

        Map<String, Object> setsNameMap = tchStdMapper.findSetsName(paramData);
        if (setsNameMap == null || setsNameMap.isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
            return returnMap;
        }

        Map<String, Object> innerParam = new HashMap<>();
        innerParam.putAll(paramData);

        innerParam.put("eamMth", 7);
        innerParam.put("stdDatNm",setsNameMap.get("name"));
        innerParam.put("textbkTabNm",setsNameMap.get("name"));

        // tabNm : 전달 받은 데이터가 있을때 변경
        if (!"".equals(paramData.get("tabNm")) && paramData.get("tabNm") != null) {
            innerParam.put("stdDatNm",paramData.get("tabNm"));
            innerParam.put("textbkTabNm",paramData.get("tabNm"));
        }

        Map<String, Object> createTchStdReturnMap = (Map<String, Object>) createTchStd(innerParam);

        if ((Boolean) createTchStdReturnMap.get("resultOk")) {
            innerParam.put("bbsSvAt", "N");
            innerParam.put("stdId", createTchStdReturnMap.get("stdId"));

            returnMap.put("stdId",createTchStdReturnMap.get("stdId"));

            Map<String, Object> createTchStdSaveReturnMap = (Map<String, Object>) createTchStdSave(innerParam);

            if (!((Boolean) createTchStdSaveReturnMap.get("resultOk"))) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "저장실패");
            } else {
                returnMap.put("tabId", MapUtils.getIntValue(createTchStdSaveReturnMap, "tabId", 0));
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }

    public Object createtchStdUseMystdCreate(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("stdId", null);
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "저장완료");

        Map<String, Object> innerParam = new HashMap<>();
        innerParam.putAll(paramData);
        innerParam.put("eamMth",7);

        Map<String, Object> createTchStdReturnMap = (Map<String, Object>) createTchStd(innerParam);

        if ((Boolean) createTchStdReturnMap.get("resultOk")) {
            innerParam.put("bbsSvAt", "N");
            innerParam.put("stdId", createTchStdReturnMap.get("stdId"));

            returnMap.put("stdId",createTchStdReturnMap.get("stdId"));

            Map<String, Object> createTchStdSaveReturnMap = (Map<String, Object>) createTchStdSave(innerParam);

            if (!((Boolean) createTchStdSaveReturnMap.get("resultOk"))) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "저장실패");
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }



    public Object createTchStdStart(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);

        //수업 당시 해당 클래스 전체 학생 목록 기존 데이터 삭제
        int deleteStdEnrollmentCnt = tchStdMapper.deleteStdEnrollment(paramData);

        //수업 당시 해당 클래스 전체 학생 목록 저장
        int createStdEnrollmentCnt = tchStdMapper.createStdEnrollment(paramData);

        //해당 {사용자, 탭id, 클래스id,교과서id,차시} 기준으로 std_ed_dt(종료시간)이 없는 데이터 삭제
        //종료시간이 없는 데이터 : 비정상적으로 종료됨 -> 통계데이터에서 제외
        int deleteNoDataStdRecodeInfoCnt = tchStdMapper.deleteNoDataStdRecodeInfo(paramData);

        int createStdRecodeInfoCnt = tchStdMapper.createStdRecodeInfo(paramData);

        if(createStdRecodeInfoCnt > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object modifyTchStdEnd(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int modifyStdRecodeInfoForEndDtCnt = tchStdMapper.modifyStdRecodeInfoForEndDt(paramData);
        log.info("modifyStdRecodeInfoForEndDtCnt:{}", modifyStdRecodeInfoForEndDtCnt);

        if(modifyStdRecodeInfoForEndDtCnt > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createTchStdLessonReconSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<Map<String, Object>> returnTabList = new ArrayList();
        List<Map<String, Object>> returnMyStdList = new ArrayList();
        Map<String, Object> resultData;

        Integer createdExtLearnCntsId = null;
        List<Integer> processedTabIds = new ArrayList<>();  // tabId 수집용 리스트 추가

        //삭제 대상 리턴
        List<Integer> deletedTabInfoList = new ArrayList<>();

        String wrterId = MapUtils.getString(paramData, "wrterId", "");
        String claId = MapUtils.getString(paramData, "claId", "");
        int textbkId = MapUtils.getIntValue(paramData, "textbkId", 0);
        int crculId = MapUtils.getIntValue(paramData, "crculId", 0);

        // myStdList
        List<Map<String, Object>> myStdList = (List<Map<String, Object>>) paramData.get("myStdList");
        if (myStdList == null) {
            myStdList = new ArrayList<>();
        }

        Map<String, Object> myStdMap;
        for (Map<String, Object> m : myStdList) {
            myStdMap = new HashMap<>();
            myStdMap.put("wrterId", wrterId);
            myStdMap.put("claId", claId);
            myStdMap.put("textbkId", textbkId);
            myStdMap.put("crculId", crculId);
            myStdMap.putAll(m);

            if ("Y".equals(MapUtils.getString(myStdMap, "deleteAt", "N"))) {
                // 삭제 대상 일 경우

                if (MapUtils.getIntValue(myStdMap, "stdId", 0) != 0) {
                    // 학습자료ID 있음. 학습자료 삭제
                    myStdMap.put("case", "/tch/std/del (학습자료 삭제)");
                    myStdMap.put("resultData", removeTchStdDel(myStdMap));

                    resultData = (Map<String, Object>) MapUtils.getMap(myStdMap, "resultData");
                    if (!(MapUtils.getBoolean(resultData, "resultOK", false))) {
                        throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                    }
                } else if (MapUtils.getIntValue(myStdMap, "extLearnCntsId", 0) != 0) {
                    // 콘텐츠자료ID 있음. 콘텐츠자료 삭제
                    myStdMap.put("case", "콘텐츠자료 삭제");
                    myStdMap.put("resultData", removeTchExtLearnCnts(myStdMap));

                    resultData = (Map<String, Object>) MapUtils.getMap(myStdMap, "resultData");
                    if (!(MapUtils.getBoolean(resultData, "resultOk", false))) {
                        throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                    }
                }
            } else {
                // 내 자료 삭제가 아닌 경우 (신규 - 내 자료는 수정 없음)

                if ("".equals(MapUtils.getString(myStdMap, "setsId", ""))) {
                    // setsId 없음. 콘텐츠자료
                    if (MapUtils.getIntValue(myStdMap, "extLearnCntsId", 0) == 0) {
                        // setsID 없음
                        // 콘텐츠자료ID 없음. 신규
                        myStdMap.put("case", "콘텐츠 자료 생성");
                        myStdMap.put("resultData", createExtLearnCnts(myStdMap));

                        resultData = (Map<String, Object>) MapUtils.getMap(myStdMap, "resultData");
                        if (!(MapUtils.getBoolean(resultData, "resultOk", false))) {
                            throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                        }

                        int extLearnCntsId = MapUtils.getIntValue(resultData, "extLearnCntsId", 0);
                        myStdMap.put("extLearnCntsId", extLearnCntsId);
                        createdExtLearnCntsId = extLearnCntsId;
                    }
                } else {
                    // setsId 있음
                    if (MapUtils.getIntValue(myStdMap, "stdId", 0) == 0) {
                        // 학습자료ID 없음. 신규
                        myStdMap.put("case", "/tch/std/create (수업자료 생성)");
                        myStdMap.put("resultData", createTchStd(myStdMap));

                        resultData = (Map<String, Object>) MapUtils.getMap(myStdMap, "resultData");
                        if (!(MapUtils.getBoolean(resultData, "resultOk", false))) {
                            throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                        }
                    }
                }
            }

            returnMyStdList.add(myStdMap);
        }

        // tabList
        List<Map<String, Object>> tabList = (List<Map<String, Object>>) paramData.get("tabList");

        if (tabList == null) {
            tabList = new ArrayList<>();
        }
        Map<String, Object> tabMap;
        for (Map<String, Object> m : tabList) {
            tabMap = new HashMap<>();
            tabMap.put("wrterId", wrterId);
            tabMap.put("claId", claId);
            tabMap.put("textbkId", textbkId);
            tabMap.put("crculId", crculId);
            tabMap.putAll(m);

            if ("Y".equals(MapUtils.getString(tabMap, "deleteAt", "N"))) {
                // 삭제 대상 일 경우

                if (MapUtils.getIntValue(tabMap, "stdId", 0) != 0) {
                    // 학습자료ID 있음. 탭, 학습자료 삭제
                    tabMap.put("case", "/tch/std/del (교과삭제)");
                    tabMap.put("resultData", removeTchStdDel(tabMap));

                    resultData = (Map<String, Object>) MapUtils.getMap(tabMap, "resultData");
                    if (!(MapUtils.getBoolean(resultData, "resultOK", false))) {
                        throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                    }
                } else if (MapUtils.getIntValue(tabMap, "tabId", 0) != 0) {
                    // 학습자료ID 없음 (콘텐츠자료)
                    // tabId 있음. 탭, 콘텐츠자료 삭제
                    tabMap.put("case", "콘텐츠자료 삭제");
                    tabMap.put("resultData", removeTchStdCntsMap(tabMap));

                    resultData = (Map<String, Object>) MapUtils.getMap(tabMap, "resultData");
                    if (!(MapUtils.getBoolean(resultData, "resultOk", false))) {
                        throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                    }
                }

                deletedTabInfoList.add((Integer) tabMap.get("tabId"));
            } else {
                if (MapUtils.getIntValue(tabMap, "tabId", 0) == 0) {
                    // tabId 없음. 신규
                    // 신규건의 경우 파일 업로드 건과 setsId가 있는 건으로 분기 처리
                    if ("".equals(MapUtils.getString(tabMap, "setsId", ""))) {
                        // setsId 없음

                        if (MapUtils.getIntValue(tabMap, "extLearnCntsId", 0) == 0) {
                            // 콘텐츠자료ID 없음. 신규 생성되는 콘텐츠자료로 판단
                            // newCntsMap 동일한 데이터의 cntsNm, extLearnCntsId 추출
                            // defaultValue 삭제, 필수 값 없는 경우 널포인트 에러
                            String strTabNewCntsMap = MapUtils.getString(tabMap, "newCntsMap");

                            for (Map<String, Object> newCntsMap : returnMyStdList) {
                                if (strTabNewCntsMap.equals(MapUtils.getString(newCntsMap, "newCntsMap", ""))) {
                                    tabMap.put("textbkTabNm", MapUtils.getString(newCntsMap, "cntsNm"));
                                    tabMap.put("extLearnCntsId", MapUtils.getIntValue((Map<String, Object>) MapUtils.getMap(newCntsMap, "resultData"), "extLearnCntsId", 0));
                                }
                            }
                        }

                        // 콘텐츠자료ID 가 있는 경우 입력 받은 값을 사용
                        tabMap.put("case", "콘텐츠자료 신규 탭");
                        tabMap.put("resultData", createTchStdCntsMap(tabMap));

                        resultData = (Map<String, Object>) MapUtils.getMap(tabMap, "resultData");
                        tabMap.put("tabId", MapUtils.getIntValue(resultData, "tabId", 0));
                        if (!(MapUtils.getBoolean(resultData, "resultOk", false))) {
                            throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                        }
                    } else {
                        // setsId 있음. 탭, 수업자료 생성
                        tabMap.put("case", "/tch/std/use-set/create (셋트지를 사용해서 수업자료 생성)");
                        tabMap.put("resultData", createTchStdUseSetCreate(tabMap));

                        resultData = (Map<String, Object>) MapUtils.getMap(tabMap, "resultData");
                        tabMap.put("tabId", MapUtils.getIntValue(resultData, "tabId", 0));
                        if (!(MapUtils.getBoolean(resultData, "resultOk", false))) {
                            throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                        }
                    }

                } else if ("Y".equals(MapUtils.getString(tabMap, "setsChgAt", "N"))) {
                    // tabId 가 있음. 기존 탭의 세트 정보가 변경 된 경우 (setsChgAt:Y)

                    if (MapUtils.getIntValue(tabMap, "eamMth", 0) == 3) {
                        tabMap.put("userId", MapUtils.getString(tabMap, "wrterId", ""));
                        tabMap.put("case", "/tch/crcu/tab/chg-info (수업 마법봉 수정(저장)");
                        tabMap.put("resultData", tchCrcuTabService.modifyTchCrcuTabChginfo(tabMap));

                        resultData = (Map<String, Object>) MapUtils.getMap(tabMap, "resultData");
                        if (!(MapUtils.getBoolean(resultData, "resultOK", false))) {
                            throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                        }

                        tabMap.put("wrterId", MapUtils.getString(tabMap, "userId", ""));
                    } else {
                        tabMap.put("case", "/tch/std/m-save (수업 마법봉 수정(저장)");
                        tabMap.put("resultData", createTchStdMSave(tabMap));

                        resultData = (Map<String, Object>) MapUtils.getMap(tabMap, "resultData");
                        if (!(MapUtils.getBoolean(resultData, "resultOk", false))) {
                            throw new AidtException(MapUtils.getString(resultData, "resultMsg", ""));
                        }
                    }
                }

                // 탭순서, 활성화 비활성화 수정 (tabSeq, exposAt)
                tchStdMapper.modifyTabSeqExposAt(tabMap);
            }
            returnTabList.add(tabMap);

            // MQ 테이블에 tab 정보 insert
            int tabId = MapUtils.getIntValue(tabMap, "tabId", 0);
            processedTabIds.add(tabId);  // tabId 수집
            if (tabId != 0) stdLessonReconMapper.seveStdMqTrnLog(tabId);
        }

        // returnMap.put("returnTabList", returnTabList);
        // returnMap.put("returnMyStdList", returnMyStdList);
        if (createdExtLearnCntsId != null) {
            returnMap.put("extLearnCntsId", createdExtLearnCntsId);
        }

        // 처리된 tabId들을 returnMap에 추가
        if (!processedTabIds.isEmpty()) {
            returnMap.put("tabIds", processedTabIds);
        }

        // 삭제 tabInfo 정보 전달
        if (!deletedTabInfoList.isEmpty()) {
            returnMap.put("deletedTabInfoList", deletedTabInfoList);
        }

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        return returnMap;
    }

    public Object createExtLearnCnts(Map<String, Object> paramData) throws Exception {
        // 콘텐츠 자료 생성
        var returnMap = new LinkedHashMap<>();

        // INSERT INTO aidt_lms.ext_learn_cnts
        int result1 = tchStdMapper.createExtLearnCnts(paramData);
        log.info("result1:{}", result1);

        returnMap.put("extLearnCntsId", paramData.get("id"));
       /* paramData.remove("id"); */

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    // 콘텐츠자료 삭제
    public Object removeTchExtLearnCnts(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // update aidt_lms.ext_learn_cnts SET use_at = 'N'
        int result1 = tchStdMapper.removeTchExtLearnCnts(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createTchStdCntsMap(Map<String, Object> paramData) throws Exception {
        // 콘텐츠자료 신규 탭
        var returnMap = new LinkedHashMap<>();

        // tab 생성
        paramData.put("textbkTabNm", MapUtils.getString(paramData,"cntsNm", ""));
        int resultTabInfo = tchStdMapper.createTchStdSave_tabInfo(paramData);
        int tabId = MapUtils.getInteger(paramData, "id");
        log.info("resultTabInfo:{}", resultTabInfo);

        paramData.put("tabId", tabId);
        paramData.remove("id");

        // 콘텐츠자료. 탭 매핑 데이터 생성
        int result1 = tchStdMapper.createTchStdCntsMap(paramData);
        log.info("result1:{}", result1);
        log.info("id value: {}", paramData.get("id"));

        if (result1 > 0) {
            paramData.put("stdCntsMapId", paramData.get("id"));
            returnMap.put("stdCntsMapId", paramData.get("id"));
            paramData.remove("id");
            // tab nm 수정
            int resultTabModify = tchStdMapper.modifyTchStdTabNm(paramData);
            log.info("resultTabModify:{}", resultTabModify);

            if (resultTabModify > 0) {
                returnMap.put("tabId", tabId);
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "성공");
            } else {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "실패");
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object removeTchStdCntsMap(Map<String, Object> paramData) throws Exception {
        // 탭, 콘텐츠자료 삭제
        var returnMap = new LinkedHashMap<>();

        // 탭 삭제
        paramData.put("textbkTabId", MapUtils.getIntValue(paramData,"tabId", 0));
        int result4 = tchStdMapper.removeTchStdDel_tabInfo(paramData);
        log.info("result4:{}", result4);

        // 탭_콘텐츠자료_매핑 삭제
        int result1 = tchStdMapper.removeTchStdCntsMap(paramData);
        log.info("result1:{}", result1);

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

}
