package com.visang.aidt.lms.api.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.assessment.mapper.StntEvalMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchEvalMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchReportEvalMapper;
import com.visang.aidt.lms.api.engvocal.service.StntMdulVocalScrService;
import com.visang.aidt.lms.api.homework.mapper.StntHomewkMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.repository.EvlInfoRepository;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.wrongnote.service.StntWrongnoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntEvalService {
    private final EvlInfoRepository evlInfoRepository;

    private final StntHomewkMapper stntHomewkMapper;
    private final TchEvalMapper tchEvalMapper;
    private final StntEvalMapper stntEvalMapper;
    private final StntRewardService stntRewardService;

    private final TchReportEvalMapper tchReportEvalMapper;
    private final StntWrongnoteService stntWrongnoteService;
    private final StntNtcnService stntNtcnService;

    private final StntMdulVocalScrService stntMdulVocalScrService;

    @Transactional(readOnly = true)
    public Object findEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> evalCheckItem = Arrays.asList("plnEvlCnt", "pgEvlCnt", "cpEvlCnt");
        List<String> evalInfoItem = Arrays.asList("no", "id", "evlNm", "evlPrgDt", "evlCpDt", "evlSttsCd", "evlSttsNm", "eakSttsCd", "eakSttsNm", "rptOthbcAt","reportUrl", "setsId", "creatorTyYn", "submAt", "slfSubmAt", "perSubmAt", "slfPerSubmAt", "rptAutoOthbcAt");

        LinkedHashMap<Object, Object> evalCheckMap = AidtCommonUtil.filterToMap(evalCheckItem, stntEvalMapper.findStntEvalListEvalCheck(paramData));

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

         List<Map> evalInfoList = (List<Map>) stntEvalMapper.findStntEvalListEvalInfo(pagingParam);

        if (!evalInfoList.isEmpty()) {
            total = (long) evalInfoList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(evalInfoList, pageable, total);

        returnMap.put("evalCheck", evalCheckMap);
        returnMap.put("evalList", AidtCommonUtil.filterToList(evalInfoItem, evalInfoList));
        returnMap.put("page",page);

        return returnMap;
    }

    public Object modifyEvalStart(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("id", "setsId","eakSttsCd", "eakSttsNm", "submAt", "eakStDt");

        /*
        int result1 =  tchEvalMapper.modifyTchEvalStartEvalInfo(paramData);
        log.info("result1:{}", result1);
        */

        // validation
        log.info("Eval start validate:{},{}", MapUtils.getString(paramData, "evlId"), MapUtils.getString(paramData, "userId"));
        Map<String, Object> evlInfo = tchEvalMapper.findTchEvalEndEvalInfo(paramData);
        // 평가정보 존재유무
        if (evlInfo == null) {
            return "평가정보가 존재하지 않습니다.";
        }
        // 평가상태, 기간 체크
        int evlSttsCd = MapUtils.getInteger(evlInfo, "evlSttsCd");
        Object objStDt = MapUtils.getObject(evlInfo, "evlPrgDt");
        Object objEndDt = MapUtils.getObject(evlInfo, "evlCpDt");
        String errMsg = AidtCommonUtil.validateStart(evlSttsCd, objStDt, objEndDt);

        if (StringUtils.isNotBlank(errMsg)) {
            return errMsg;
        }

        int result1 =  stntEvalMapper.modifyEvalStartResultInfo(paramData);
        log.info("result1:{}", result1);

        // /stnt/eval/recheck 호출시 처리하는 것으로 변경함. (2024-02-19)
        //int result2 =  stntEvalMapper.modifyEvalStartResultDetail(paramData);
        //log.info("result2:{}", result2);

        LinkedHashMap<Object, Object> evalInfoMap = AidtCommonUtil.filterToMap(evalInfoItem, stntEvalMapper.findStntEvalStart(paramData));
        evalInfoMap.put("eakStDt", AidtCommonUtil.stringToDateFormat((String) evalInfoMap.get("eakStDt"),"yyyy-MM-dd HH:mm:ss"));
        evalInfoMap.put("eakEdDt", null);

        return evalInfoMap;
    }

    public Object modifyStntEvalSubmit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> evalInfoItem = Arrays.asList("id", "setsId", "eakSttsCd", "eakSttsNm", "submAt", "eakStDt", "eakEdDt", "rwdSetAt", "rwdPoint", "edGidAt", "edGidDc");

        // 제출여부(submAt) 파라미터 디폴트값 셋팅
        String submAt = MapUtils.getString(paramData, "submAt");
        paramData.put("submAt", StringUtils.isEmpty(submAt) ? "Y" : submAt);
//        paramData.put("submAt", StringUtils.isEmpty(submAt) || submAt.equals("Y") ? "Y" : "N");

        var evlInfoMap = tchEvalMapper.findEvlInfo(paramData);

        int result3 = 0;
        int resultDetail = 0;

        // 2024-06-17. 타이머 마감 또는 기한 마감인 경우와 상관없이
        // 과제 응시 시작한 학생의 경우 무조건 제출하기 처리로 프로세스 변경 (최지연 CP님)
        /*
        boolean deadline = "Y".equals(MapUtils.getString(paramData, "submAt"))
                        && "Y".equals(MapUtils.getString(evlInfoMap, "pdSetAt"))
                        && MapUtils.getInteger(evlInfoMap, "deadline") == 1;
        */
        boolean deadline = false;

        if ("N".equals(paramData.get("submAt")) || deadline) {
            if (null == paramData.get("userId") || "".equals(paramData.get("userId"))) {
                paramData.put("userId", " ");
            }

            result3 = tchEvalMapper.modifyEvalSubmAtERD(paramData);
            log.info("result3:{}", result3);
            int result4 = tchEvalMapper.modifyEvalSubmAtERI(paramData);
            log.info("result4:{}", result4);
        } else {

            // failedEvlIemInfo List<Map<String, Object>> 조회하여 taskIemId 추출
            Object failedEvlIemInfoObj = paramData.get("failedEvlIemInfoList");
            List<Integer> evlIemIds = new ArrayList<>();
            List<Map<String, Object>> failedEvlIemInfoList = new ArrayList<>();

            if (failedEvlIemInfoObj != null && failedEvlIemInfoObj instanceof List) {
                failedEvlIemInfoList = (List<Map<String, Object>>) failedEvlIemInfoObj;
                for (Map<String, Object> itemMap : failedEvlIemInfoList) {
                    Integer evlIemId = MapUtils.getInteger(itemMap, "evlIemId");
                    if (evlIemId != null) {
                        evlIemIds.add(evlIemId);
                    }
                }
            }

            // taskIemIds를 paramData에 추가 (빈 리스트여도 상관없음)
            paramData.put("excludedEvlIemIds", evlIemIds);

            resultDetail = stntEvalMapper.modifyStntEvalSubmitResultDetail(paramData);
            log.info("resultDetail:{}", resultDetail);

            Map<String, Object> resultDetailCntMap = stntEvalMapper.selectStntEvalSubmitResultDetailCnt(paramData);

            if(resultDetailCntMap == null || resultDetailCntMap.isEmpty()) {
                returnMap.put("resultOk", false);
                paramData.remove("excludedEvlIemIds");

                returnMap.put("resultMsg", "요청 파라미터에 해당하는 데이터가 없습니다.요청 데이터 : " + paramData);
                return returnMap;
            }
            if (MapUtils.getInteger(resultDetailCntMap, "eakSttsCdCnt") == 0) {
                resultDetailCntMap.put("eakSttsCd", 5);
                resultDetailCntMap.put("mrkCpAt", "Y");

            } else {
                resultDetailCntMap.put("eakSttsCd", 3);
                resultDetailCntMap.put("mrkCpAt", "N");
            }
            resultDetailCntMap.put("evlId", paramData.get("evlId"));
            resultDetailCntMap.put("userId", paramData.get("userId"));
            resultDetailCntMap.put("submAt", paramData.get("submAt"));
            int result1 = stntEvalMapper.modifyStntEvalSubmitResultInfo(resultDetailCntMap);
            log.info("result1:{}", result1);

            //리워드
            //모든 문제를 푼 경우에는 resultDetail = 0 임.
            //if (resultDetail > 0) {
                Map<String, Object> rwdMap = new HashMap<>();

                rwdMap.put("userId", paramData.get("userId"));
                rwdMap.put("claId", evlInfoMap.get("claId"));
                rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                rwdMap.put("menuSeCd", "3"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝
                rwdMap.put("sveSeCd", "4"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트
                rwdMap.put("trgtId", paramData.get("evlId")); //대상ID
                rwdMap.put("textbkId", paramData.get("textbookId"));
                rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                rwdMap.put("rwdAmt", 10); //지급일때는 0
                rwdMap.put("rwdUseAmt", 0); //지급일때는 0

                Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);



            // 학생이 제출 시 오답노트 생성 하면서 알림 + 리포트 알림  ( 자동일때만) - 평가
            List<Map> sendNtcnEvlListAuto =  tchReportEvalMapper.findSendNtcnEvlListAuto(paramData);
            if (!sendNtcnEvlListAuto.isEmpty()) {
                stntWrongnoteService.createStntWrongnoteEvlId(paramData);
                for(Map<String, Object> map : sendNtcnEvlListAuto) {
                    stntNtcnService.createStntNtcnSave(map);
                }
            }

            // failedEvlIemInfo가 있을 때 modifyStntEvalSave 호출
            if (!failedEvlIemInfoList.isEmpty()) {
                for (Map<String, Object> item : failedEvlIemInfoList) {
                    try {

                        String errata = String.valueOf(item.get("errata"));
                        String evlIemScr = "0";

                        Integer subId = MapUtils.getInteger(item, "subId");
                        if (ObjectUtils.isEmpty(subId)) {
                            item.put("subId", 0);
                        }

                        if ("1".equals(errata)) {
                            // 점수 배점표 조회 evl_iem_info : 자동채점(1)일때 점수부여
                            Map<String, Object> evlIemInfoMap = stntEvalMapper.findStntEvalSaveIemScr(item);

                            if (evlIemInfoMap != null) {
                                evlIemScr = String.valueOf(evlIemInfoMap.get("evlIemScr"));
                            }
                        }

                        item.put("evlIemScr", evlIemScr);

                        // 점수 반영 evl_result_detail
                        int failedIemResult = stntEvalMapper.modifyStntEvalSaveResultDetail(item);
                        log.info("failedIemResult:{}", failedIemResult);

                        item.remove("evlIemScr");

                        if (failedIemResult > 0) {
                            Map<String, Object> evalResuldDetailMap = stntEvalMapper.findEvalResuldDetail(item);
                            String questionType = MapUtils.getString(evalResuldDetailMap, "questionType");
                            // questionType - ptqz (발음평가형) 인 경우
                            if ("ptqz".equals(questionType)) {
                                String menuSeCd = "3"; // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                                int trgtId = MapUtils.getInteger(evalResuldDetailMap, "resultDetailId");
                                String subMitAnw = MapUtils.getString(item, "subMitAnw");
                                // 발성평가 점수 등록 처리
                                stntMdulVocalScrService.saveVocalEvlScrInfo(menuSeCd, trgtId, subMitAnw);
                            }
                        }

                        // 첫번째, 응시상태가 3보다 작은게 없는 경우 - 3으로 변경 - 응시완료로 변경
                        // 두번째, 응시상태가 5보다 작은게 없는 경우 - 5로 변경 - 채점완료로 변경
                        int[] eakSttsCds = {3, 5};
                        for (int eakSttsCd : eakSttsCds) {
                            item.put("eakSttsCd", eakSttsCd);

                            Map<String, Object> evlResultMap = stntEvalMapper.findEvlResultDetailCount(item);
                            evlResultMap.put("evlId", item.get("evlId"));
                            evlResultMap.put("evlResultId", item.get("evlResultId"));
                            evlResultMap.put("userId", item.get("userId"));

                            if (MapUtils.getInteger(evlResultMap, "cnt") == 0) {
                                evlResultMap.put("eakSttsCd", eakSttsCd);

                                stntEvalMapper.modifyStntEvalResultInfo(evlResultMap);
                            }
                        }

                    } catch (Exception e) {
                        log.error("modifyStntEvalSave 호출 중 오류 발생: {}", item, e);
                        throw e;
                    }
                }
            }




            //}
        }

        // 평가 마스터 정보 상태 변경 (배치처리 X, 원복처리)
        // 2024-05-22: 평가 마스터 상태 처리 제외
        //int result2 = stntEvalMapper.modifyStntEvalSubmitEvlInfo(paramData);
        //log.info("result2:{}", result2);

        if  (deadline) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "제출 기한이 마감되어 제출할 수 없습니다.");
        } else if (resultDetail > 0 || result3 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        returnMap.putAll(AidtCommonUtil.filterToMap(evalInfoItem, stntEvalMapper.findTchEvalSubmit(paramData)));

        List<String> StntResultInfoItem = Arrays.asList("evlStdrSet", "evlStdrSetNm", "evlResult", "evlResultScr", "evlTotalScr");

        returnMap.put("stntResultInfo", AidtCommonUtil.filterToMap(StntResultInfoItem, stntEvalMapper.findStntResultInfo(paramData)));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findEvalInfo(Map<String, Object> paramData) throws Exception {

        List<String> evalInfoItem = Arrays.asList("id", "setsId", "evlNm", "evlPrgDt", "evlCpDt", "evlSttsCd", "evlSttsNm", "eamExmNum", "timTime", "rwdSetAt", "rwdPoint","eakSttsCd", "eakSttsNm", "aiTutSetAt", "rpOthbcAt", "rptAutoOthbcAt", "rpOthbcDt", "lesnEvalAt", "eakStDt", "submAt", "evlAdiSec");

        LinkedHashMap<Object, Object> evalInfoMap = AidtCommonUtil.filterToMap(evalInfoItem, stntEvalMapper.findStntEvalInfoEvalInfo(paramData));

        return evalInfoMap;
    }

    @Transactional(readOnly = true)
    public Object findStntEvalExam(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> evalInfoItem = Arrays.asList("id", "setsId", "evlNm", "evlPrgDt", "evlCpDt", "timTime", "eamExmNum");
        List<String> evalIemInfoItem = Arrays.asList("id", "evlResultId", "evlIemId", "subId", "name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen");

        int result1 =  stntEvalMapper.modifyStntEvalExamResultDetail(paramData);
        log.info("result1:{}", result1);

        returnMap = AidtCommonUtil.filterToMap(evalInfoItem, stntEvalMapper.findStntEvalExamEvalInfo(paramData));
        returnMap.put("evalIemList", AidtCommonUtil.filterToList(evalIemInfoItem, stntEvalMapper.findStntEvalExamEvalIemInfo(paramData)));

        return returnMap;
    }

    public Object modifyStntEvalSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int claBbsCheck = stntEvalMapper.selectClaEvalCheck(paramData);
        if(claBbsCheck == 0){
            returnMap.put("resultOk", false);
            returnMap.put("resultMessage", "삭제된 평가");
            return returnMap;
        }
        // 학생 문항지 조희 후 제출여부 확인
        Map<String, Object> resultInfoMap = stntEvalMapper.findStntEvalStart(paramData);
        if ("Y".equals(MapUtils.getString(resultInfoMap, "submAt", ""))) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }

        // 문항 상태 : mrk_ty {1 : 자동채점, 2 : 수동채점, 3 : 채점불가}
        // 문항 타입 : articleType {20 : 개념 concept, 21 : 문항 question, 22 : 활동 movement}
        Map<String, Object> ConceptCheckMap = stntEvalMapper.findConceptCheck(paramData);

        if (MapUtils.getInteger(ConceptCheckMap, "mrkTy", 0) == 3
                || "concept".equals(MapUtils.getString(ConceptCheckMap, "articleType"))
        ){
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {

            List<String> returnItem = Arrays.asList("userId", "evlResultId", "evlIemId", "subId");

            String errata = String.valueOf(paramData.get("errata"));
            String evlIemScr = "0";

            Integer subId = MapUtils.getInteger(paramData, "subId");
            if (ObjectUtils.isEmpty(subId)) {
                paramData.put("subId", 0);
            }

            if ("1".equals(errata)) {
                // 점수 배점표 조회 evl_iem_info : 자동채점(1)일때 점수부여
                Map<String, Object> evlIemInfoMap = stntEvalMapper.findStntEvalSaveIemScr(paramData);

                if (evlIemInfoMap != null) {
                    evlIemScr = String.valueOf(evlIemInfoMap.get("evlIemScr"));
                }
            }

            paramData.put("evlIemScr", evlIemScr);

            // 점수 반영 evl_result_detail
            int result1 = stntEvalMapper.modifyStntEvalSaveResultDetail(paramData);
            log.info("result1:{}", result1);

            paramData.remove("evlIemScr");

            if (result1 > 0) {
                Map<String, Object> evalResuldDetailMap = stntEvalMapper.findEvalResuldDetail(paramData);
                String questionType = MapUtils.getString(evalResuldDetailMap, "questionType");
                // questionType - ptqz (발음평가형) 인 경우
                if ("ptqz".equals(questionType)) {
                    String menuSeCd = "3"; // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                    int trgtId = MapUtils.getInteger(evalResuldDetailMap, "resultDetailId");
                    String subMitAnw = MapUtils.getString(paramData, "subMitAnw");
                    // 발성평가 점수 등록 처리
                    stntMdulVocalScrService.saveVocalEvlScrInfo(menuSeCd, trgtId, subMitAnw);
                }
            }

            returnMap.put("id", paramData.get("evlId"));
            returnItem.forEach(s -> {
                returnMap.put(s, paramData.get(s));
            });

            // 첫번째, 응시상태가 3보다 작은게 없는 경우 - 3으로 변경 - 응시완료로 변경
            // 두번째, 응시상태가 5보다 작은게 없는 경우 - 5로 변경 - 채점완료로 변경
            int[] eakSttsCds = {3, 5};
            for (int eakSttsCd : eakSttsCds) {
                paramData.put("eakSttsCd", eakSttsCd);

                Map<String, Object> evlResultMap = stntEvalMapper.findEvlResultDetailCount(paramData);
                evlResultMap.put("evlId", paramData.get("evlId"));
                evlResultMap.put("evlResultId", paramData.get("evlResultId"));
                evlResultMap.put("userId", paramData.get("userId"));

                if (MapUtils.getInteger(evlResultMap, "cnt") == 0) {
                    evlResultMap.put("eakSttsCd", eakSttsCd);

                    int result3 = stntEvalMapper.modifyStntEvalResultInfo(evlResultMap);
                    log.info("result3:{}", result3);
                }
            }

            //배치로 처리
            //evlResultMap.put("evlSttsCd", 5);
            //int result4 =  stntEvalMapper.modifyStntEvalInfo(evlResultMap);
            //log.info("result4:{}", result4);

            // 2024-06-14
            // 맨티스 - 0001981 이슈로 인해 서로간에 오해가 발생할 수 있어서 김나영 CP님과 협의해서 처리하지 않도록 함
            //int result5 = stntEvalMapper.modifyStntEvlResultDetail(paramData);  /* 수업중 평가만 적용한다 */
            //log.info("result5:{}", result5);

            if (result1 > 0) {
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "성공");

                int result2 = stntEvalMapper.modifyStntEvalSaveResultInfo(paramData);
                log.info("result2:{}", result2);
            } else {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "실패");
            }
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntEvalResult(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> evalInfoItem = Arrays.asList("id", "eamTrget", "setsId", "evlNm", "eakSttsCd", "eakSttsNm", "evlPrgDt", "timTime", "eamExmNum", "evalIemList", "rwdSetAt", "rwdPoint", "edGidAt", "edGidDc", "submAt");
        List<String> evalIemInfoItem = Arrays.asList("id", "setsId", "evlResultId", "evlIemId", "subId", "mrkTy", "mrkTyNm", "subMitAnw", "subMitAnwUrl", "errata", "errataNm", "eakAt", "fdbDc", "fdbUrl", "name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen","hntUseAt", "hint", "sbsChatting", "sbsSolution", "hintYN", "sbsChattingYN","sbsSolutionYN");
        List<String> StntResultInfoItem = Arrays.asList("evlStdrSet", "evlStdrSetNm", "evlResult", "evlResultScr", "evlTotalScr");

        returnMap = AidtCommonUtil.filterToMap(evalInfoItem, stntEvalMapper.findStntEvalResultEvalInfo(paramData));
        returnMap.put("evalIemList", AidtCommonUtil.filterToList(evalIemInfoItem, stntEvalMapper.findStntEvalResultEvalIemInfo(paramData)));
        returnMap.put("stntResultInfo", AidtCommonUtil.filterToMap(StntResultInfoItem, stntEvalMapper.findStntResultInfo(paramData)));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntEvalResultinfo(Map<String, Object> paramData) throws Exception {
        List<String> evalDetailItem = Arrays.asList("evlDetailId", "setsId", "evlResultId", "evlIemId", "subId", "subMitAnw", "subMitAnwUrl", "errata", "errataNm", "eakAt", "hdwrtCn", "questionStr");
        return AidtCommonUtil.filterToMap(evalDetailItem, stntEvalMapper.findStntEvalResultDetailInfo(paramData));
    }

    public Object modifyStntEvalRecheck(Map<String, Object> paramData)throws Exception {
        var returnMap = new LinkedHashMap<>();

        Map<String, Object> resultInfoMap = stntEvalMapper.findStntEvalStart(paramData);
        if ("Y".equals(MapUtils.getString(resultInfoMap, "submAt", ""))) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        int result1 =  stntEvalMapper.modifyStntEvalRecheck(paramData);
        log.info("result1:{}", result1);

        returnMap.put("evlId", paramData.get("evlId"));
        returnMap.put("userId", paramData.get("userId"));

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object modifyStntEvalInit(Map<String, Object> paramData)  throws Exception {
        List<String> item = Arrays.asList(   "evlId"
                                            ,"userId"
                                            ,"evlResultId"
                                            ,"evlIemId", "subId"
                                            ,"resultOk"
                                            ,"resultMsg");

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        // 피드백 저장
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(paramData);

            int cnt =  stntEvalMapper.modifyStntEvalInit(paramData);

            if (cnt > 0) {
                stntEvalMapper.modifyStntEvalInitForEvlResultInfo(paramData);
                stntEvalMapper.modifyStntEvalInitForEvlInfo(paramData);
                //throw new AidtException ("평가 답안 초기화 실패");
            } else {
                throw new AidtException("평가 답안 초기화 실패");
            }

            resultMap.put("evlId"       , paramData.get("evlId")           ); //평가 id
            resultMap.put("userId"      , paramData.get("userId")       ); //응시학생 id
            resultMap.put("evlResultId" , paramData.get("evlResultId")  ); //평가결과 id
            resultMap.put("evlIemId"    , paramData.get("evlIemId")     ); //평가항목 id
            resultMap.put("subId"       , paramData.get("subId")     );
            resultMap.put("resultOk"    , cnt > 0);
            resultMap.put("resultMsg"   , "성공"  );

        // Response
        return AidtCommonUtil.filterToMap(item, resultMap);
    }

    public Object modifyStntHomewkAitutorSumitChat(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // 채팅 내용 list
        List<Map<String, Object>> aiTutChtCnList = new ArrayList<>();

        int resultDetailId = stntEvalMapper.findStntEvalIdByArticle(paramData);
        if (resultDetailId == 0) {
            throw new AidtException("resultDetailId is empty - findStntEvalIdByArticle error");
        }

        paramData.put("evalResultDetailId", resultDetailId);

        // 채팅 내용 list 기존 채팅 내용 있는 경우 list 에 추가
        String aiTutChtCnOld = stntEvalMapper.findStntEvalAiTutCn(paramData);
        log.info("aiTutChtCnOld:{}", aiTutChtCnOld);
        if (aiTutChtCnOld != null) {
            aiTutChtCnList = objectMapper.readValue(aiTutChtCnOld, List.class);
            log.info("aiTutChtCnList old:{}", aiTutChtCnList);
        }

        // 새로 입력 받은 내용 list 에 추가
        Map<String, Object> aiTutChtCnMap = new HashMap<>();
        aiTutChtCnMap.put("chatOrder", paramData.get("chatOrder"));
        aiTutChtCnMap.put("chatType", paramData.get("chatType"));
        aiTutChtCnMap.put("aiCall", paramData.get("aiCall"));
        aiTutChtCnMap.put("aiReturn", paramData.get("aiReturn"));
        aiTutChtCnMap.put("articleId",paramData.get("articleId"));
        aiTutChtCnMap.put("subId",paramData.get("subId"));
        aiTutChtCnList.add(aiTutChtCnMap);
        log.info("aiTutChtCnList:{}", aiTutChtCnList);

        // 채팅 내용 list map to json (json 형태로 저장)
        String aiTutChtCn = objectMapper.writeValueAsString(aiTutChtCnList);
        paramData.put("aiTutChtCn", aiTutChtCn);
        log.info("aiTutChtCn:{}", aiTutChtCn);
        // update
        int result = stntEvalMapper.modifyStntEvalAiTutSave(paramData);
        log.info("result:{}", result);
        if (result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }
        return returnMap;
    }

    public Object modifyStntTrgtDone(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        int trgtSeCd = MapUtils.getIntValue(paramData, "trgtSeCd", 0);
        int result = 0;

        if (trgtSeCd == 2) {
            result = stntEvalMapper.modifyStntTaskDone(paramData);
            log.info("result:{}", result);
        } else if (trgtSeCd == 3) {
            result = stntEvalMapper.modifyStntEvlDone(paramData);
            log.info("result:{}", result);
        }


        if (result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }
        return returnMap;
    }

    public Object modifyClassMoveStdDataChange(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        int result = 0;

        result = stntEvalMapper.saveClassMoveHistory(paramData);
        log.info("result:{}", result);

        // 예정인 평가/과제 만들기 (신규)
        result = stntEvalMapper.saveClassMoveStdDataChange_evlResultInfo(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.saveClassMoveStdDataChange_taskResultInfo(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.saveClassMoveStdDataChange_evlResultDetail(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.saveClassMoveStdDataChange_taskResultDetail(paramData);
        log.info("result:{}", result);


        // 예정인 평가/과제 삭제하기 (이탈)
        result = stntEvalMapper.removeClassMoveStdDataChange_evlResultDetail(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.removeClassMoveStdDataChange_evlResultInfo(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.removeClassMoveStdDataChange_taskResultDetail(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.removeClassMoveStdDataChange_taskResultInfo(paramData);
        log.info("result:{}", result);


        // 리워드 샾 cla_id 갱신
        result = stntEvalMapper.modifyClassMoveStdDataChange_rwdEarnInfo(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.modifyClassMoveStdDataChange_rwdEarnHist(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.modifyClassMoveStdDataChange_spPrchsInfo(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.modifyClassMoveStdDataChange_spPrchsHist(paramData);
        log.info("result:{}", result);

        return returnMap;
    }

    public Object saveClassStdData(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        int result = 0;

        result = stntEvalMapper.saveClassAddHistory(paramData);
        log.info("result:{}", result);

        // 예정인 평가/과제 만들기 (신규)
        result = stntEvalMapper.saveClassStdData_evlResultInfo(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.saveClassStdData_evlResultDetail(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.saveClassStdData_taskResultInfo(paramData);
        log.info("result:{}", result);

        result = stntEvalMapper.saveClassStdData_taskResultDetail(paramData);
        log.info("result:{}", result);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntEvalTimeUsage(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "");
        
        // 문자열 파라미터 목록
        String[] requiredStringParams = {"studentId", "targetId"};

        // 문자열 파라미터 검증
        for (String param : requiredStringParams) {
            if (StringUtils.isEmpty(MapUtils.getString(paramData, param))) {
                return createErrorResponse(returnMap, param + " is null");
            }
        }

        returnMap.put("resultData", stntEvalMapper.findStntEvalTimeUsage(paramData));
        return returnMap;
    }

    public Object saveStntEvalTimeUsage(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "");

        // 문자열 파라미터 목록
        String[] requiredStringParams = {"studentId", "targetId", "startDate", "endDate", "errorMessage"};

        // 문자열 파라미터 검증
        for (String param : requiredStringParams) {
            if (StringUtils.isEmpty(MapUtils.getString(paramData, param))) {
                return createErrorResponse(returnMap, param + " is null");
            }
        }

        // timeUsage 파라미터 별도 검증 (Integer 타입)
        if (ObjectUtils.isEmpty(MapUtils.getInteger(paramData, "timeUsage"))) {
            return createErrorResponse(returnMap, "timeUsage is empty");
        }

        // 데이터 저장
        if (stntEvalMapper.saveStntEvalTimeUsage(paramData) > 0) {
            returnMap.put("resultOk", true);
        }

        return returnMap;
    }

    public Map<String,Object> findStntSetCheck (Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();

        if (paramData.get("trgtSeCd").equals("2")) {
            paramData.put("taskId", paramData.get("trgtId"));
            int claBbsCheck = stntHomewkMapper.selectClaHomewkCheck(paramData);

            if (claBbsCheck == 0) {
                returnMap.put("resultOk", false);
                returnMap.put("is_deleted", "Y");
                returnMap.put("resultMessage", "삭제된 과제");
                return returnMap;
            } else {
                returnMap = stntEvalMapper.findStntSetCheck(paramData);
                stntEvalMapper.updateTaskAt(paramData);
            }
        } else if (paramData.get("trgtSeCd").equals("3")) {
            paramData.put("evlId", paramData.get("trgtId"));
            int claBbsCheck = stntEvalMapper.selectClaEvalCheck(paramData);

            if (claBbsCheck == 0) {
                returnMap.put("resultOk", false);
                returnMap.put("is_deleted", "Y");
                returnMap.put("resultMessage", "삭제된 평가");
                return returnMap;
            } else {
                returnMap = stntEvalMapper.findStntSetCheck(paramData);
                stntEvalMapper.updateEvalAt(paramData);
            }
        }

        returnMap.put("is_deleted", "N");
        return returnMap;
    }

    private Map<String, Object> createErrorResponse(Map<String, Object> returnMap, String errorMessage) {
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", errorMessage);
        return returnMap;
    }

}