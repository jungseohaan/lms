package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.assessment.service.TchSlfperEvalService;
import com.visang.aidt.lms.api.engtemp.mapper.EngTempMapper;
import com.visang.aidt.lms.api.engvocal.service.StntMdulVocalScrService;
import com.visang.aidt.lms.api.materials.mapper.QuestionMapper;
import com.visang.aidt.lms.api.materials.mapper.StntMdulQstnMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import com.visang.aidt.lms.api.wrongnote.service.StntWrongnoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntMdulQstnService {
    private final StntMdulQstnMapper stntMdulQstnMapper;
    private final TchSlfperEvalService tchSlfperEvalService;
    private final StntRewardService stntRewardService;
    private final QuestionMapper questionMapper;
    private final EngTempMapper engTempMapper;
    private final StntWrongnoteMapper stntWrongnoteMapper;
    private final StntWrongnoteService stntWrongnoteService;

    private final StntMdulVocalScrService stntMdulVocalScrService;

    public Object modifyStntMdulQstnSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = stntMdulQstnMapper.modifyStntMdulQstnSave(paramData);
        log.info("result1:{}", result1);

        returnMap.put("ntcnCn", "");
        returnMap.put("resultDetailId", paramData.get("resultDetailId"));

        if(result1 > 0) {
            List<Map> stntMdulQstnList = stntMdulQstnMapper.findStntMdulQstnInfo(paramData);
            if(!ObjectUtils.isEmpty(stntMdulQstnList)) {
                for(Map<String,Object> map : stntMdulQstnList) {
                    int mrkTy  = MapUtils.getInteger(map, "mrkTy"); // 1:자동채점, 2:수동채점
                    int errata = MapUtils.getInteger(map, "errata");

                    // 2024-06-18
                    // [영어] 발음평가형 처리
                    String questionType = MapUtils.getString(map, "questionType");
                    // questionType - ptqz (발음평가형) 인 경우
                    if("ptqz".equals(questionType) || "wcom".equals(questionType)) {
                        String menuSeCd  = "1"; // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                        int trgtId       = MapUtils.getInteger(paramData,"resultDetailId");
                        String subMitAnw = MapUtils.getString(paramData, "subMitAnw");
                        // 발성평가 점수 등록 처리
                        stntMdulVocalScrService.saveVocalEvlScrInfo(menuSeCd, trgtId, subMitAnw);
                    }

                    // 수동채점유형 이거나 자동채점유형이면서 정답인 경우 리워드 발급대상임.
                    boolean isRewardTarget = (mrkTy == 2 || (mrkTy == 1 && errata == 1)) ? true : false;
                    if (isRewardTarget) {
                        Map<String, Object> rwdMap = new HashMap<>();

                        if(!"0".equals(map.get("srcDetailId").toString()) && "Y".equals(map.get("smExmAt"))){
                            // 다른문제풀기
//                                rwdMap.put("rwdAmt", 5); //지급    정책 테이블에서 넣어주고 있음
                            rwdMap.put("sveSeCd", "7");  //서비스구분 - 1:문제, 2:활동, 5:AI학습, 6:선택학습, 7:다른문제풀기
                        } else {
                            // 문제풀기
//                                rwdMap.put("rwdAmt", 1); //지급
                            rwdMap.put("sveSeCd", (mrkTy == 2) ? "2" : "1");  //서비스구분 - 1:문제, 2:활동, 5:AI학습, 6:선택학습, 7:다른문제풀기
                        }
                        rwdMap.put("userId", map.get("userId"));
                        rwdMap.put("claId", map.get("claId"));
                        rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                        rwdMap.put("menuSeCd", "1"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝
                        rwdMap.put("trgtId", map.get("trgtId")); //대상ID
                        rwdMap.put("textbkId", map.get("textbkId"));
                        rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                        rwdMap.put("rwdUseAmt", 0); //획득 0
                        Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);

                        returnMap.put("ntcnCn", MapUtils.getString(rewardResult, "ntcnCn", ""));
                    }
                }
            }

            int errata = MapUtils.getIntValue(paramData, "errata");

            if (errata == 2 || errata == 3) {
                Map<String, Object> findStdDtaResultDetailMap = stntMdulQstnMapper.findStdDtaResultDetail(paramData);

                // null이거나 비어있으면 더 이상 진행하지 않고 빠져나감
                if (findStdDtaResultDetailMap == null || findStdDtaResultDetailMap.isEmpty()) {
                    return returnMap;
                }

                findStdDtaResultDetailMap.put("wonAnwClsfCd", 1);

                // 등록된 오답노트 여부 조회
                int wonAswNoteCnt = stntWrongnoteMapper.findWonAswNoteCount(findStdDtaResultDetailMap);
                log.info("checkWonAswNoteCnt:{}", wonAswNoteCnt);

                if (wonAswNoteCnt == 0) {
                    String wonAnwNm = stntWrongnoteService.getWonAnwNm(findStdDtaResultDetailMap);
                    findStdDtaResultDetailMap.put("wonAnwNm", wonAnwNm);

                    int createWonAswNoteCnt = stntWrongnoteMapper.createWonAswNote(findStdDtaResultDetailMap);
                    log.info("createWonAswNoteCnt:{}", createWonAswNoteCnt);
                }
            }
        }

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createStntMdulQstnRecheck(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "실패");

        List<Map<String, Object>> qstnList = (List<Map<String, Object>>) paramData.get("qstnList");

        if (ObjectUtils.isEmpty(qstnList)) {
            return returnMap;
        }

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        Map<String, Object> setsMap = stntMdulQstnMapper.findStntMdulQstnResetSetsId(paramData);

        if (MapUtils.isEmpty(setsMap)) {
            return returnMap;
        }

        if (MapUtils.getString(paramData, "setsId") == null) {
            paramData.put("setsId", MapUtils.getString(setsMap, "setsId"));
        }
        log.info("setsId::" + MapUtils.getString(paramData, "setsId"));
        String strTabAddAt = MapUtils.getString(setsMap, "tabAddAt");

        Map<String, Object> resultSDRI = stntMdulQstnMapper.findStntMdulQstnResetSDRI(paramData);

        if (ObjectUtils.isEmpty(resultSDRI)) {
            int result2 = 0;
            result2 = stntMdulQstnMapper.createStntMdulQstnResetSDRI(paramData);
            log.info("result2:{}", result2);
        } else {
            if (MapUtils.getInteger(resultSDRI, "eakSttsCd") == 1) {
                int result1 = stntMdulQstnMapper.modifyStntMdulQstnResetSDRI(paramData);
                log.info("result1:{}", result1);
            }
        }

        List<Map> recheckList = new ArrayList<>();

        for (Map<String, Object> qstnMap : qstnList) {
            Integer subId2 = MapUtils.getInteger(qstnMap, "subId");
            if (ObjectUtils.isEmpty(subId2)) {
                qstnMap.put("subId", 0);
            }

            paramData.put("articleId", MapUtils.getString(qstnMap,"articleId"));
            paramData.put("subId", MapUtils.getInteger(qstnMap,"subId"));
            paramData.put("articleTypeSttsCd", MapUtils.getInteger(qstnMap,"articleTypeSttsCd"));

            Map<String, Object> resultSDRD = stntMdulQstnMapper.findStntMdulQstnResetSDRD(paramData);
            if (ObjectUtils.isEmpty(resultSDRD)) {
                int result4 = stntMdulQstnMapper.createStntMdulQstnResetSDRD(paramData);
                log.info("result4:{}", result4);
                /*
                if ("Y".equals(strTabAddAt)) { //tab_info.tab_add_at 값이 Y 일 경우 setsummary 테이블 존재
                    int result4 = stntMdulQstnMapper.createStntMdulQstnResetSDRD2(paramData);
                    log.info("result4:{}", result4);
                } else {
                    int result4 = stntMdulQstnMapper.createStntMdulQstnResetSDRD(paramData);
                    log.info("result4:{}", result4);
                }
                */
            } else {
                if (MapUtils.getInteger(resultSDRD, "eakSttsCd") != 3) {
                    int result3 = stntMdulQstnMapper.modifyStntMdulQstnResetSDRD(paramData);
                    log.info("result3:{}", result3);
                }
            }

            List<String> findStntMdulQstnRecheckItem = Arrays.asList("infoId", "detailId");
            //returnMap.put("infoId", "");
            //returnMap.put("detailId", "");

            var recheckMap = new HashMap<>();
            recheckMap.put("infoId", "");
            recheckMap.put("detailId", "");
            recheckMap.putAll(AidtCommonUtil.filterToMap(findStntMdulQstnRecheckItem, stntMdulQstnMapper.findStntMdulQstnRecheck(paramData)));

            // 영어교과템 유형일 경우 교과템 mother 데이터를 쌓는다
            if (MapUtils.getString(paramData, "articleTypeSttsCd", "1").equals("2")) {
                String engTempErrorMessage = null;
                String articleId = MapUtils.getString(paramData, "articleId", "0");
                Integer subId3 = MapUtils.getInteger(paramData, "subId", 0);
                int resultDetailId = MapUtils.getInteger(recheckMap, "detailId", 0);
                // 영교템에서 오류가 나더라도 정상동작 할 수 있도록 함
                if (articleId == "0" || resultDetailId == 0) {
                    engTempErrorMessage = "error5:실패 - empty → articleId : " + articleId + " / resultDetailId : " + resultDetailId;
                    /*returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "실패 - empty → articleId : " + articleId + " / resultDetailId : " + resultDetailId);
                    return returnMap;*/
                } else {
                    List<Map<String, Object>> engtempInfoList = engTempMapper.selectEngtempInfoByArticleId(articleId, subId3);
                    if (CollectionUtils.isEmpty(engtempInfoList)) {
                        engTempErrorMessage = "error6:실패 - empty → selectEngtempInfoByArticleId | articleId : " + articleId + " / resultDetailId : " + resultDetailId;
                        /*returnMap.put("resultOk", false);
                        returnMap.put("resultMsg", "실패 - empty → selectEngtempInfoByArticleId");
                        return returnMap;*/
                    } else {
                        Map<String, Object> paramMap = new HashMap<>();
                        paramMap.put("resultDetailId", resultDetailId);
                        for (Map<String, Object> engTempMap : engtempInfoList) {
                            int engTempId = MapUtils.getInteger(engTempMap, "engTempId", 0);
                            // 영교템에서 오류가 나더라도 정상동작 할 수 있도록 함
                            if (engTempId == 0) {
                                engTempErrorMessage = "error7:실패 - empty → engTempId | articleId : " + articleId + " / resultDetailId : " + resultDetailId;
                                break;
                                /*returnMap.put("resultOk", false);
                                returnMap.put("resultMsg", "실패 - empty → engTempId");
                                return returnMap;*/
                            }
                            String templateDivCode = MapUtils.getString(engTempMap, "templateDivCode", "").toUpperCase();
                            paramMap.put("engTempId", engTempId);
                            // voca일 경우 scriptId와 활동Id는 0 설정
                            if (templateDivCode.equals("VOCA") || templateDivCode.equals("VC")) {
                                paramMap.put("scriptId", 0);
                                paramMap.put("tmpltActvId", 0);
                                // 학생 학습 상세 id 정보와 교과템Id로 교과템 존재 여부를 조회한다
                                String existsYn = engTempMapper.selectEngtempExistsYn(resultDetailId, engTempId);
                                if (existsYn.equals("Y")) {
                                    continue;
                                }
                                engTempMapper.insertLesnRsc(paramMap);
                            } else {
                                paramMap.put("scriptId", engTempMap.get("scriptId"));
                                // 학생 학습 상세 id 정보와 교과템Id로 교과템 목록을 조회한다 (교과템 활동이 있을 경우 해당 교과템 결과의 id subquery로 전달됨)
                                List<Map<String, Object>> engtempResultInfoList = engTempMapper.selectEngtempAtivityList(resultDetailId, engTempId);
                                if (CollectionUtils.isEmpty(engtempResultInfoList)) {
                                    continue;
                                }
                                for (Map<String, Object> map : engtempResultInfoList) {
                                    // 교과템플릿 활동 결과 id
                                    int engTempResultInfoId = MapUtils.getInteger(map, "engTempResultInfoId", 0);
                                    // 이미 insert되어 있을 경우 해당 값이 0보다 크다
                                    if (engTempResultInfoId > 0) {
                                        continue;
                                    }
                                    int scriptId = MapUtils.getInteger(map, "scriptId", 0);
                                    int tmpltActvId = MapUtils.getInteger(map, "tmpltActvId", 0);
                                    // voca가 아닌데도 scriptId와 활동Id가 없으면 오류 / 영교템에서 오류가 나더라도 정상동작 할 수 있도록 함
                                    if (scriptId == 0 || tmpltActvId == 0) {
                                        engTempErrorMessage = "error7:실패 - empty → scriptId : " + scriptId + " / tmpltActvId : " + tmpltActvId + " | articleId : " + articleId + " / resultDetailId : " + resultDetailId;
                                        break;
                                        /*returnMap.put("resultOk", false);
                                        returnMap.put("resultMsg", "실패 - empty → scriptId : " + scriptId + " / tmpltActvId : " + tmpltActvId);
                                        return returnMap;*/
                                    }
                                    paramMap.put("scriptId", scriptId);
                                    paramMap.put("tmpltActvId", tmpltActvId);
                                    engTempMapper.insertLesnRsc(paramMap);
                                }
                            }
                        } // Map<String, Object> engTempMap : engtempInfoList
                    } // CollectionUtils.isEmpty(engtempInfoList)
                } // articleId == "0" || resultDetailId == 0
                // 교과템에서 오류가 난 경우 오류 메세지를 추가한다
                if (StringUtils.isNotEmpty(engTempErrorMessage)) {
                    recheckMap.put("engTempErrorMessage", engTempErrorMessage);
                }
            } // MapUtils.getString(paramData, "articleTypeSttsCd", "1").equals("2")

            recheckMap.put("articleId", MapUtils.getString(qstnMap,"articleId"));
            recheckMap.put("subId", MapUtils.getInteger(qstnMap,"subId"));
            recheckMap.put("articleTypeSttsCd", MapUtils.getInteger(qstnMap,"articleTypeSttsCd"));

            recheckList.add(recheckMap);
        }

        paramData.remove("articleId");
        paramData.remove("subId");
        paramData.remove("articleTypeSttsCd");

        //returnMap.putAll(recheckMap);
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");
        returnMap.put("recheckList", recheckList);

        return returnMap;
    }

    public Object findStntMdulQstnViewBak(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> stntMdulQstnViewItem = Arrays.asList("detailId", "dtaIemId", "subId", "mrkTy", "eakSttsCd", "eakAt", "mrkCpAt", "eakStDt", "eakEdDt", "subMitAnw", "subMitAnwUrl", "errata", "reIdfCnt", "anwChgCnt", "smExmAt", "textbkDtaIemId", "reExmCnt", "stdFdbAt", "stdFdbDc", "exltAnwAt", "fdbExpAt", "hdwrtCn","bmkYn","bmkId","bmkModuleId","bmkSubId","bmkTchYn","bmkTchId","bmkTchModuleId","bmkTchSubId","noteYn","actYn","preCheckAt","hntUseAt");

        var mdulList = AidtCommonUtil.filterToList(stntMdulQstnViewItem, stntMdulQstnMapper.findStntMdulQstnView(paramData));

        List<Map> histInfoList = null;
        List<Map> otherList = null;
        if (ObjectUtils.isNotEmpty(mdulList)) {
            histInfoList = stntMdulQstnMapper.findStntMdulQstnViewHistBak(mdulList);
            otherList = stntMdulQstnMapper.findStntMdulQstnViewOtherListBak(mdulList);
        }

        for (LinkedHashMap<Object, Object> s : mdulList) {
            s.put("histInfoList", CollectionUtils.emptyIfNull(histInfoList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"detailId"), MapUtils.getString(g,"dtaResultDetailId")))
                .map(g -> {
                    g.remove("dtaResultDetailId");
                    return g;
                }).toList()
            );

            Map<String, Object> slfPerMap = new HashMap<>();
            slfPerMap.put("stntId", paramData.get("userId"));
            slfPerMap.put("gbCd", 1);
            slfPerMap.put("textbkId", paramData.get("textbkId"));
            slfPerMap.put("tabId", paramData.get("tabId"));
            slfPerMap.put("dtaIemId", s.get("dtaIemId"));
            s.put("slfPerList", tchSlfperEvalService.getTchSlfperEvlSlfView(slfPerMap));

            s.put("otherList", CollectionUtils.emptyIfNull(otherList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"detailId"), MapUtils.getString(g,"srcDetailId")))
                .map(g -> {
                    g.remove("srcDetailId");
                    return g;
                }).toList()
            );

            /*
            Map<String, Object> otherCheckParamMap = new HashMap<>();
            otherCheckParamMap.put("detailId", MapUtils.getInteger(s, "detailId"));
            otherCheckParamMap.put("articleId", MapUtils.getInteger(s, "dtaIemId"));

            Map<String, Object> otherCheckReturnMap = this.findStntMdulQstnOtherCheck(otherCheckParamMap);
            s.put("othersOk", MapUtils.getBoolean(otherCheckReturnMap, "resultOk"));
            s.put("othersMsg", MapUtils.getString(otherCheckReturnMap, "resultMsg"));
            */

        }
        returnMap.put("mdulList", mdulList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntMdulQstnView(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> stntMdulQstnViewItem = Arrays.asList("detailId", "dtaIemId", "subId", "mrkTy", "eakSttsCd", "eakAt", "mrkCpAt", "eakStDt", "eakEdDt", "subMitAnw", "subMitAnwUrl", "errata", "reIdfCnt", "anwChgCnt", "smExmAt", "textbkDtaIemId", "reExmCnt", "stdFdbAt", "stdFdbDc", "exltAnwAt", "fdbExpAt", "hdwrtCn","bmkYn","bmkId","bmkModuleId","bmkSubId","bmkTchYn","bmkTchId","bmkTchModuleId","bmkTchSubId","noteYn","actYn","preCheckAt","hntUseAt","tchErrataChgAt");

        var mdulList = AidtCommonUtil.filterToList(stntMdulQstnViewItem, stntMdulQstnMapper.findStntMdulQstnView(paramData));

        Map<String, Object> actYnMap = null;
        List<Map> noteYnList = null;
        List<Map> exltAnwAtList  = null;
        List<Map> bkmkList = null;
        List<Map> bkmkTchList = null;
        List<Map> histInfoList = null;
        List<Map> otherList = null;
        List<Map> slfSetList = null;
        List<Map> evlAtMap = null;
        List<Map<String, Object>> perSetList = null;

        if (ObjectUtils.isNotEmpty(mdulList)) {
            Map<String, Object> slfPerMap = new HashMap<>();
            slfPerMap.put("stntId", paramData.get("userId"));
            slfPerMap.put("textbkId", paramData.get("textbkId"));
            slfPerMap.put("tabId", paramData.get("tabId"));

            actYnMap = stntMdulQstnMapper.findActYnMap(slfPerMap);                  //활동 여부
            noteYnList = stntMdulQstnMapper.findNoteYnList(slfPerMap);              //교사 노트 공유 여부
            exltAnwAtList = stntMdulQstnMapper.findExltAnwAtList(slfPerMap);        //우수 답안 여부
            bkmkList = stntMdulQstnMapper.findBkmkList(slfPerMap);                  //북마크 여부
            bkmkTchList = stntMdulQstnMapper.findBkmkTchList(slfPerMap);            //교사 북마크 공유 여부
            histInfoList = stntMdulQstnMapper.findStntMdulQstnViewHist(slfPerMap);  //다시풀기 결과
            otherList = stntMdulQstnMapper.findStntMdulQstnViewOtherList(slfPerMap);//다른문제풀기
            slfSetList = stntMdulQstnMapper.findTchSlfperEvlSlfSetList(slfPerMap);  //자기 평가
            perSetList = stntMdulQstnMapper.findTchSlfperEvlPerSetList(slfPerMap);  //동료 평가
            evlAtMap = stntMdulQstnMapper.findMdulSlfPerEvlAt(slfPerMap);           //아티클 자기동료평가 유무
        } else {
            returnMap.put("mdulList", mdulList);
            return returnMap;
        }

        // 동료
        List<Map> perInfoList = new ArrayList<>();
        List<Map> perInfoIdList = new ArrayList<>();
        List<Map> PerResultList = new ArrayList<>();
        List<Map<String, Object>> templtList = new ArrayList<>();

        Map<String, Object> perInfoMap = new HashMap<>();
        Map<String, Object> perInfoIdMap = new HashMap<>();
        Map<String, Object> PerResultMap = new HashMap<>();
        Map<String, Object> templtMap = new HashMap<>();

        for (Map<String, Object> map : perSetList) {
            if (ObjectUtils.isNotEmpty(MapUtils.getString(map, "perApraserId"))) {
                perInfoMap = new HashMap<>();
                perInfoMap.put("id", MapUtils.getIntValue(map, "id"));
                perInfoMap.put("perApraserId", MapUtils.getString(map, "perApraserId"));
                perInfoMap.put("flnm", MapUtils.getString(map, "flnm"));
                perInfoMap.put("moduleId", MapUtils.getString(map, "moduleId"));
                perInfoMap.put("subId", MapUtils.getIntValue(map, "subId"));
                perInfoMap.put("stExposAt", MapUtils.getString(map, "stExposAt"));
                perInfoList.add(perInfoMap);

                PerResultMap = new HashMap<>();
                PerResultMap.put("perApraserId", MapUtils.getString(map, "perApraserId"));
                PerResultMap.put("moduleId", MapUtils.getString(map, "moduleId"));
                PerResultMap.put("subId", MapUtils.getIntValue(map, "subId"));
                PerResultMap.put("slfPerEvlDetailId", MapUtils.getIntValue(map, "slfPerEvlDetailId"));
                PerResultMap.put("tmpltItmSeq", MapUtils.getIntValue(map, "tmpltItmSeq"));
                PerResultMap.put("evlDmi", MapUtils.getString(map, "evlDmi"));
                PerResultMap.put("evlIem", MapUtils.getString(map, "evlIem"));
                PerResultMap.put("evlStdrCd", MapUtils.getString(map, "evlStdrCd"));
                PerResultMap.put("evlStdrDc", MapUtils.getString(map, "evlStdrDc"));
                PerResultMap.put("evlResult", MapUtils.getString(map, "evlResult"));
                PerResultMap.put("evlAsw", MapUtils.getString(map, "evlAsw"));
                PerResultList.add(PerResultMap);
            } else {
                perInfoIdMap = new HashMap<>();
                perInfoIdMap.put("id", MapUtils.getIntValue(map, "id"));
                perInfoIdMap.put("stExposAt", MapUtils.getIntValue(map, "stExposAt"));
                perInfoIdMap.put("moduleId", MapUtils.getString(map, "moduleId"));
                perInfoIdMap.put("subId", MapUtils.getIntValue(map, "subId"));
                perInfoIdList.add(perInfoIdMap);
            }

            templtMap = new HashMap<>();
            templtMap.put("moduleId", MapUtils.getString(map, "moduleId"));
            templtMap.put("subId", MapUtils.getIntValue(map, "subId"));
            templtMap.put("slfPerEvlDetailId", MapUtils.getIntValue(map, "slfPerEvlDetailId"));
            templtMap.put("tmpltItmSeq", MapUtils.getIntValue(map, "tmpltItmSeq"));
            templtMap.put("evlDmi", MapUtils.getString(map, "evlDmi"));
            templtMap.put("evlIem", MapUtils.getString(map, "evlIem"));
            templtMap.put("evlStdrCd", MapUtils.getString(map, "evlStdrCd"));
            templtMap.put("evlStdrDc", MapUtils.getString(map, "evlStdrDc"));
            templtList.add(templtMap);
        }
        perInfoList = perInfoList.stream().distinct().collect(Collectors.toList());

        for (Map s : perInfoList) {
            s.put("PerResult", CollectionUtils.emptyIfNull(PerResultList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"perApraserId"), MapUtils.getString(g,"perApraserId")))
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"moduleId"), MapUtils.getString(g,"moduleId")))
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                .map(g -> {
                    g.remove("perApraserId");
                    g.remove("moduleId");
                    g.remove("subId");
                    return g;
                }).toList()
            );
        }

        perInfoIdList = perInfoIdList.stream().distinct().collect(Collectors.toList());

        templtList = templtList.stream().distinct().collect(Collectors.toList());

        templtList.sort(
            Comparator.comparing((Map<String, Object> map) -> MapUtils.getIntValue(map, "tmpltItmSeq"))
        );

        String actYn = MapUtils.getString(actYnMap, "actYn", "N");

        for (LinkedHashMap<Object, Object> s : mdulList) {
            s.put("actYn", actYn);

            s.put("noteYn", "N");
            for (Map map : noteYnList) {
                if (MapUtils.getString(s, "dtaIemId").equals(MapUtils.getString(map, "moduleId"))
                        && MapUtils.getString(s, "subId").equals(MapUtils.getString(map, "subId"))
                ) {
                    s.put("noteYn", MapUtils.getString(map, "noteYn"));
                    break;
                }
            }

            s.put("exltAnwAt", "N");
            for (Map map : exltAnwAtList) {
                if (MapUtils.getString(s, "dtaIemId").equals(MapUtils.getString(map, "dtaIemId"))
                        && MapUtils.getString(s, "subId").equals(MapUtils.getString(map, "subId"))
                ) {
                    s.put("exltAnwAt", MapUtils.getString(map, "exltAnwAt"));
                    break;
                }
            }

            s.put("bmkYn", "N");
            for (Map map : bkmkList) {
                if (MapUtils.getString(s, "dtaIemId").equals(MapUtils.getString(map, "bmkModuleId"))
                        && MapUtils.getString(s, "subId").equals(MapUtils.getString(map, "bmkSubId"))
                ) {
                    s.put("bmkYn", "Y");
                    s.put("bmkId", MapUtils.getIntValue(map, "bmkId"));
                    s.put("bmkModuleId", MapUtils.getString(map, "bmkModuleId"));
                    s.put("bmkSubId", MapUtils.getIntValue(map, "bmkSubId"));
                    break;
                }
            }

            for (Map map : bkmkTchList) {
                s.put("bmkTchYn", "N");
                if (MapUtils.getString(s, "dtaIemId").equals(MapUtils.getString(map, "bmkTchModuleId"))
                        && MapUtils.getString(s, "subId").equals(MapUtils.getString(map, "bmkTchSubId"))
                ) {
                    s.put("bmkTchYn", "Y");
                    s.put("bmkTchId", MapUtils.getIntValue(map, "bmkTchYn"));
                    s.put("bmkTchModuleId", MapUtils.getString(map, "bmkTchModuleId"));
                    s.put("bmkTchSubId", MapUtils.getIntValue(map, "bmkTchSubId"));
                    break;
                }
            }

            s.put("histInfoList", CollectionUtils.emptyIfNull(histInfoList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"detailId"), MapUtils.getString(g,"dtaResultDetailId")))
                .map(g -> {
                    g.remove("dtaResultDetailId");
                    return g;
                }).toList()
            );

            // 자기 동료 평가
            var slfPerMap = new LinkedHashMap<>();
            slfPerMap.put("stntId", paramData.get("userId"));
            slfPerMap.put("selInfoId", 0);
            slfPerMap.put("perInfoId", 0);
            slfPerMap.put("slfStExposAt", null);
            slfPerMap.put("perStExposAt", null);

            // 자기
            List<Map> slResult = CollectionUtils.emptyIfNull(slfSetList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"moduleId")))
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                .map(g -> {
                    slfPerMap.put("selInfoId", MapUtils.getIntValue(g, "id", 0));
                    slfPerMap.put("slfStExposAt", MapUtils.getString(g, "stExposAt"));
                    g.remove("id");
                    g.remove("moduleId");
                    g.remove("subId");
                    g.remove("stExposAt");
                    return g;
                }).toList();

            // 동료
            List<Map> slfPerInfoList = CollectionUtils.emptyIfNull(perInfoList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"moduleId")))
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                .map(g -> {
                    slfPerMap.put("perInfoId", MapUtils.getIntValue(g, "id", 0));
                    slfPerMap.put("perStExposAt", MapUtils.getString(g, "stExposAt"));
                    g.remove("id");
                    g.remove("moduleId");
                    g.remove("subId");
                    g.remove("stExposAt");
                    return g;
                }).toList();

            if (MapUtils.getIntValue(slfPerMap, "perInfoId") == 0) {
                for (Map map : perInfoIdList) {
                    if (MapUtils.getString(s, "dtaIemId").equals(MapUtils.getString(map, "moduleId"))
                            && MapUtils.getString(s, "subId").equals(MapUtils.getString(map, "subId"))
                    ) {
                        slfPerMap.put("perInfoId", MapUtils.getIntValue(map, "id", 0));
                        slfPerMap.put("perStExposAt", MapUtils.getString(map,"stExposAt"));
                        break;
                    }
                }
            }

            //templt
            List<Map<String, Object>> slfTempltList = CollectionUtils.emptyIfNull(templtList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"moduleId")))
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                .map(g -> {
                    g.remove("moduleId");
                    g.remove("subId");
                    return g;
                }).toList();

            for (Map map : evlAtMap) {
                if (MapUtils.getString(s,"dtaIemId").equals(MapUtils.getString(map,"moduleId"))) {
                    slfPerMap.put("mdulSlfPerEvlAt", MapUtils.getString(map,"mdulSlfPerEvlAt"));
                    break;
                }
            }

            slfPerMap.put("slResult", slResult);
            slfPerMap.put("perInfoList", slfPerInfoList);
            slfPerMap.put("templtList", slfTempltList);
            slfPerMap.put("slfNum", "");
            slfPerMap.put("slfTotNum", slResult.size());
            slfPerMap.put("perNum", "");
            slfPerMap.put("perTotNum", slfPerInfoList.size());

            s.put("slfPerList", slfPerMap);

            s.put("otherList", CollectionUtils.emptyIfNull(otherList).stream()
                .filter(g -> StringUtils.equals(MapUtils.getString(s,"detailId"), MapUtils.getString(g,"srcDetailId")))
                .map(g -> {
                    g.remove("srcDetailId");
                    return g;
                }).toList()
            );
        }
        returnMap.put("mdulList", mdulList);

        return returnMap;
    }

    public Object createStntMdulQstnOther(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "id", ""
        );

        Map<String, Object> questionListMap = ObjectUtils.clone(paramData);
        Map<String, Object> tabInfoMap = questionMapper.findTabInfo(paramData);
        if (ObjectUtils.isNotEmpty(tabInfoMap)) {
            questionListMap.putAll(tabInfoMap);
        }

        // 1. 아티클(유사,쌍둥이) 맵(article_article_map)에서 조회
        List<LinkedHashMap<Object, Object>> list = AidtCommonUtil.filterToList(infoItem, questionMapper.findQuestionList3(questionListMap));
        if(CollectionUtils.isEmpty(list)) {
            // 2. 기존 로직 실행
            list = AidtCommonUtil.filterToList(infoItem, questionMapper.findQuestionList2(questionListMap)); // frequency 적용
        }

        int result = 0;

        if(CollectionUtils.isNotEmpty(list)) {
            for(Map<Object, Object> info : list) {
                paramData.put("dtaIemId", info.get("id"));
                result = stntMdulQstnMapper.createStntMdulQstnOther(paramData);
                log.info("result:{}", result);
            }
        }

        if (result > 0) {
            List<String> listItem = Arrays.asList(
                    "detailId", "dtaIemId"
            );

            List<LinkedHashMap<Object, Object>> resultList = new ArrayList<>();

            if (paramData.get("detailId") != null) {
                resultList = AidtCommonUtil.filterToList(listItem, stntMdulQstnMapper.findStntMdulQstnOther(paramData));
            }

            returnMap.put("oriDetailId", paramData.get("detailId"));
            returnMap.put("otherList", resultList);
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntMdulQstnAnsw(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> stntMdulQstnViewItem = Arrays.asList("fdbDc", "fdbUrl ", "exltAnwAt", "fdbExpAt");
        List<LinkedHashMap<Object, Object>> stntMdulQstnViewList = AidtCommonUtil.filterToList(stntMdulQstnViewItem, stntMdulQstnMapper.findStntMdulQstnAnsw(paramData));

        returnMap.putAll(paramData);
        returnMap.put("exltSharedList", stntMdulQstnViewList);
        returnMap.put("exltCnt", stntMdulQstnViewList.size());

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object stntMdulQstnResultinfo(Map<String, Object> paramData) throws Exception {
        return stntMdulQstnMapper.stntMdulQstnResultinfo(paramData);
    }

    public Map<String, Object> findStntMdulQstnOtherCheck(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        List<Map> srcDetailIdList = List.of();

        if (paramData.get("detailId") != null) {
            srcDetailIdList = stntMdulQstnMapper.findStntMdulQstnOther(paramData);
        }


        if (ObjectUtils.isEmpty(srcDetailIdList)) {
            Map<String, Object> questionListMap = ObjectUtils.clone(paramData);
            Map<String, Object> tabInfoMap = questionMapper.findTabInfo(paramData);
            if (ObjectUtils.isNotEmpty(tabInfoMap)) {
                questionListMap.putAll(tabInfoMap);
            }
            questionListMap.put("limitNum", 1);

            // 1. 아티클(유사,쌍둥이) 맵(article_article_map)에서 조회
            List<Map> questionList = questionMapper.findQuestionList3(questionListMap);
            if(CollectionUtils.isEmpty(questionList)) {
                // 2. 기존 로직 실행
                questionList = questionMapper.findQuestionList2(questionListMap); // frequency 적용
            }

            // 다른문제사전체크및유사문항존재여부
            String dftQuesBeffatChkAt = "N";

            if (CollectionUtils.isEmpty(questionList)) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "유사문항이 존재하지 않습니다.");
            } else {
                dftQuesBeffatChkAt = "Y";
            }

            // 다른문제사전체크및유사문항존재여부(dft_ques_beffat_chk_at) 업데이트
            paramData.put("dftQuesBeffatChkAt",dftQuesBeffatChkAt);

            int result1 = stntMdulQstnMapper.modifyStntMdulQstnOther(paramData);
            log.info("result1:{}", result1);
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "출제한 이력이 존재 합니다.");
        }

        return returnMap;
    }
}
