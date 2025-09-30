package com.visang.aidt.lms.api.homework.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.engvocal.service.StntMdulVocalScrService;
import com.visang.aidt.lms.api.homework.mapper.StntHomewkMapper;
import com.visang.aidt.lms.api.homework.mapper.TchHomewkMapper;
import com.visang.aidt.lms.api.homework.mapper.TchReportHomewkMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
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

/**
 * packageName : com.visang.aidt.lms.api.homework.service
 * fileName : StntHomewkService
 * USER : hs84
 * date : 2024-01-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-25         hs84          최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StntHomewkService {
    private final StntHomewkMapper stntHomewkMapper;
    private final TchHomewkMapper tchHomewkMapper;
    private final StntRewardService stntRewardService;

    private final TchReportHomewkMapper tchReportHomewkMapper;
    private final StntNtcnService stntNtcnService;

    private final StntMdulVocalScrService stntMdulVocalScrService;
    private final StntWrongnoteService stntWrongnoteService;

    @Transactional(readOnly = true)
    public Object findStntHomewkList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> taskCheckItem = Arrays.asList("plnTaskCnt", "pgTaskCnt", "cpTaskCnt");
        List<String> taskInfoItem = Arrays.asList("no", "id", "taskNm", "taskPrgDt", "taskCpDt", "taskSttsCd", "taskSttsNm", "eakSttsCd", "eakSttsNm", "rptOthbcAt", "reportUrl", "setsId", "submAt", "slfSubmAt", "perSubmAt", "slfPerSubmAt", "rptAutoOthbcAt");

        LinkedHashMap<Object, Object> taskCheckMap = AidtCommonUtil.filterToMap(taskCheckItem, stntHomewkMapper.findStntHomewkListCheck(paramData));

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> homewkInfoList = (List<Map>) stntHomewkMapper.findStntHomewkList(pagingParam);

        if (!homewkInfoList.isEmpty()) {
            total = (long) homewkInfoList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(homewkInfoList, pageable, total);

        returnMap.put("taskCheck", taskCheckMap);
        returnMap.put("taskList", AidtCommonUtil.filterToList(taskInfoItem, homewkInfoList));
        returnMap.put("page", page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntHomewkInfo(Map<String, Object> paramData) throws Exception {
        List<String> taskInfoItem = Arrays.asList("id", "setsId", "textbkNm", "taskNm", "taskPrgDt", "taskCpDt", "taskSttsCd", "taskSttsNm", "eamExmNum", "timTime", "rwdSetAt", "rwdPoint", "eakSttsCd", "eakSttsNm", "eamTrget", "rpOthbcAt", "rpOthbcDt", "rptAutoOthbcAt", "aiTutSetAt", "eakStDt", "submAt", "aiTutSetAt");

        return AidtCommonUtil.filterToMap(taskInfoItem, stntHomewkMapper.findStntHomewkInfo(paramData));
    }

    public Object modifyStntHomewk(Map<String, Object> paramData) throws Exception {
        List<String> taskInfoItem = Arrays.asList("id", "setsId", "eakSttsCd", "eakSttsNm", "submAt", "eakStDt", "eakEdDt");

        //int result1 =  tchHomewkMapper.modifyTchHomewkStartHomewkInfo(paramData);
        //log.info("result1:{}", result1);

        // validation
        log.info("Task start validate:{},{}", MapUtils.getString(paramData, "taskId"), MapUtils.getString(paramData, "userId"));
        Map<String, Object> taskInfo = tchHomewkMapper.findTaskInfo(paramData);
        // 평가정보 존재유무
        if (taskInfo == null) {
            return "과제정보가 존재하지 않습니다.";
        }
        // 과제상태, 기간 체크
        int evlSttsCd = MapUtils.getInteger(taskInfo, "taskSttsCd");
        Object objStDt = MapUtils.getObject(taskInfo, "taskPrgDt");
        Object objEndDt = MapUtils.getObject(taskInfo, "taskCpDt");
        String errMsg = AidtCommonUtil.validateStart(evlSttsCd, objStDt, objEndDt);

        if (StringUtils.isNotBlank(errMsg)) {
            return errMsg;
        }

        int result1 = stntHomewkMapper.modifyStntHomewkResultInfo(paramData);
        log.info("result1:{}", result1);

        // /stnt/homewk/recheck 호출시 처리하는 것으로 변경함. (2024-02-19)
        //int result2 =  stntHomewkMapper.modifyStntHomewkResultDetail(paramData);
        //log.info("result2:{}", result2);

        return AidtCommonUtil.filterToMap(taskInfoItem, stntHomewkMapper.findStntHomewk(paramData));
    }

    public Object findStntHomewkExam(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> taskInfoItem = Arrays.asList("id", "setsId", "taskNm", "taskPrgDt", "taskCpDt", "timTime", "eamExmNum", "taskInfo");
        List<String> taskResultInfoItem = Arrays.asList("id", "taskResultId", "taskIemId", "subId", "name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen");

        int result1 = stntHomewkMapper.modifyStntHomewkExam(paramData);
        log.info("result1:{}", result1);

        returnMap = AidtCommonUtil.filterToMap(taskInfoItem, stntHomewkMapper.findStntHomewkExam(paramData));

        returnMap.put("taskInfoList", AidtCommonUtil.filterToList(taskResultInfoItem, stntHomewkMapper.findStntHomewkExamResult(paramData)));

        return returnMap;
    }

    public Object modifyStntHomewkSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int claBbsCheck = stntHomewkMapper.selectClaHomewkCheck(paramData);
        if(claBbsCheck == 0){
            returnMap.put("resultOk", false);
            returnMap.put("resultMessage", "삭제된 과제");
            return returnMap;
        }

        Map<String, Object> resultInfoMap = stntHomewkMapper.findStntHomewk(paramData);
        if ("Y".equals(MapUtils.getString(resultInfoMap, "submAt", ""))) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }

        Map<String, Object> ConceptCheckMap = stntHomewkMapper.findConceptCheck(paramData);

        if (MapUtils.getInteger(ConceptCheckMap, "mrkTy", 0) == 3
                || "concept".equals(MapUtils.getString(ConceptCheckMap, "articleType"))
        ){
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            Integer subId = MapUtils.getInteger(paramData, "subId");
            if (ObjectUtils.isEmpty(subId)) {
                paramData.put("subId", 0);
            }

            List<String> returnItem = Arrays.asList("userId", "taskResultId", "taskIemId", "subId");

            int result1 = stntHomewkMapper.modifyStntHomewkSave(paramData);
            log.info("result1:{}", result1);

            if (result1 > 0) {
                Map<String, Object> taskResuldDetailMap = stntHomewkMapper.findTaskResuldDetail(paramData);
                String questionType = MapUtils.getString(taskResuldDetailMap, "questionType");
                // questionType - ptqz (발음평가형) 인 경우
                if("ptqz".equals(questionType)) {
                    String menuSeCd  = "2"; // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                    int trgtId       = MapUtils.getInteger(taskResuldDetailMap,"resultDetailId");
                    String subMitAnw = MapUtils.getString(paramData, "subMitAnw");
                    // 발성평가 점수 등록 처리
                    stntMdulVocalScrService.saveVocalEvlScrInfo(menuSeCd, trgtId, subMitAnw);
                }
            }

            returnMap.put("id", paramData.get("taskId"));
            returnItem.forEach(s -> {
                returnMap.put(s, paramData.get(s));
            });

            Map<String, Object> taskResultMap = stntHomewkMapper.findTaskResultDetailCount(paramData);
            taskResultMap.put("taskId", paramData.get("taskId"));
            taskResultMap.put("taskResultId", paramData.get("taskResultId"));
            taskResultMap.put("userId", paramData.get("userId"));

            if (MapUtils.getInteger(taskResultMap, "cnt") == 0) {
                taskResultMap.put("eakSttsCd", 5);

                int result3 = stntHomewkMapper.modifyStntTaskResultInfo(taskResultMap);
                log.info("result3:{}", result3);
            }

            //자동제출 처리하는 부분 삭제
            //int result5 = stntHomewkMapper.modifyStntTaskResultDetail(paramData);
            //log.info("result5:{}", result5);

            //배치로 처리
            //taskResultMap.put("evlSttsCd", 5);
            //int result4 =  stntHomewkMapper.modifyStntTaskInfo(taskResultMap);
            //log.info("result4:{}", result4);

            if (result1 > 0) {
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "성공");
            } else {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "실패");
            }
        }

        return returnMap;
    }

    public Object modifyStntHomewkSubmit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> homewkInfoItem = Arrays.asList("id", "setsId", "eakSttsCd", "eakSttsNm", "submAt", "submDt", "eakStDt", "eakEdDt", "rwdSetAt", "rwdPoint", "edGidAt", "edGidDc");

        // 제출여부(submAt) 파라미터 디폴트값 셋팅
        String submAt = MapUtils.getString(paramData, "submAt");
        paramData.put("submAt", StringUtils.isEmpty(submAt) ? "Y" : submAt);
//        paramData.put("submAt", StringUtils.isEmpty(submAt) || submAt.equals("Y") ? "Y" : "N");

        var taskInfoMap = tchHomewkMapper.findTaskInfo(paramData);
        // 교과서 ID 설정
        paramData.put("textbkId", taskInfoMap.get("textbkId"));

        int result3 = 0;
        int resultDetail = 0;
        // 2024-06-17. 타이머 마감 또는 기한 마감인 경우와 상관없이
        // 과제 응시 시작한 학생의 경우 무조건 제출하기 처리로 프로세스 변경 (최지연 CP님)
        /*
        boolean deadline = "Y".equals(MapUtils.getString(paramData, "submAt"))
                        && MapUtils.getInteger(taskInfoMap, "deadline") == 1;
        */
        boolean deadline = false;

        if ("N".equals(paramData.get("submAt")) || deadline) {
            if (null == paramData.get("userId") || "".equals(paramData.get("userId"))) {
                paramData.put("userId", " ");
            }

            result3 = tchHomewkMapper.modifyHomewkSubmAtTRD(paramData);
            log.info("result3:{}", result3);
            int result4 = tchHomewkMapper.modifyHomewkSubmAtTRI(paramData);
            log.info("result4:{}", result4);
        } else {

            // failedTaskIemInfo List<Map<String, Object>> 조회하여 taskIemId 추출
            Object failedTaskIemInfoObj = paramData.get("failedTaskIemInfoList");
            List<Integer> taskIemIds = new ArrayList<>();
            List<Map<String, Object>> failedTaskIemInfoList = new ArrayList<>();

            if (failedTaskIemInfoObj != null && failedTaskIemInfoObj instanceof List) {
                failedTaskIemInfoList = (List<Map<String, Object>>) failedTaskIemInfoObj;
                for (Map<String, Object> itemMap : failedTaskIemInfoList) {
                    Integer taskIemId = MapUtils.getInteger(itemMap, "taskIemId");
                    if (taskIemId != null) {
                        taskIemIds.add(taskIemId);
                    }
                }
            }

            // taskIemIds를 paramData에 추가 (빈 리스트여도 상관없음)
            paramData.put("excludedTaskIemIds", taskIemIds);

            resultDetail = stntHomewkMapper.modifyStntHomewkSubmitResultDetail(paramData);
            log.info("resultDetail:{}", resultDetail);

            Map<String, Object> resultDetailCntMap = stntHomewkMapper.findStntHomewkSubmitResultDetailCnt(paramData);

            if (MapUtils.getInteger(resultDetailCntMap, "eakSttsCdCnt") == 0) {
                resultDetailCntMap.put("eakSttsCd", 5);
                resultDetailCntMap.put("mrkCpAt", "Y");


            } else {
                resultDetailCntMap.put("eakSttsCd", 3);
                resultDetailCntMap.put("mrkCpAt", "N");
            }
            resultDetailCntMap.put("taskId", paramData.get("taskId"));
            resultDetailCntMap.put("userId", paramData.get("userId"));
            resultDetailCntMap.put("submAt", paramData.get("submAt"));
            int result1 = stntHomewkMapper.modifyStntHomewkSubmitResultInfo(resultDetailCntMap);
            log.info("result1:{}", result1);

            //리워드
            //모든 문제를 푼 경우에는 resultDetail = 0 임.
            //if (resultDetail > 0) {
                Map<String, Object> rwdMap = new HashMap<>();

                rwdMap.put("userId", paramData.get("userId"));
                rwdMap.put("claId", taskInfoMap.get("claId"));
                rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                rwdMap.put("menuSeCd", "2"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝
                rwdMap.put("sveSeCd", "3"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트
                rwdMap.put("trgtId", paramData.get("taskId")); //대상ID
                rwdMap.put("textbkId", paramData.get("textbkId"));
                rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                rwdMap.put("rwdAmt", 10); //지급일때는 0
                rwdMap.put("rwdUseAmt", 0); //지급일때는 0

                Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);

            // 학생이 제출 시 오답노트 생성 하면서 알림 + 리포트 알림  ( 자동일때만) - 과제
            List<Map> sendNtcnTaskListAuto =  tchReportHomewkMapper.findSendNtcnTaskListAuto(paramData);
            if (!sendNtcnTaskListAuto.isEmpty()) {
                stntWrongnoteService.createStntWrongnoteTaskId(paramData);
                for(Map<String, Object> map : sendNtcnTaskListAuto) {
                    stntNtcnService.createStntNtcnSave(map);
                }
            }
            //}

            // failedTaskIemInfo가 있을 때 modifyStntHomewkSave 호출
            if (!failedTaskIemInfoList.isEmpty()) {
                for (Map<String, Object> item : failedTaskIemInfoList) {
                    try {

                        int failedIemResult = stntHomewkMapper.modifyStntHomewkSave(item);
                        log.info("failedIemResult:{}", failedIemResult);

                        if (failedIemResult > 0) {
                            Map<String, Object> taskResuldDetailMap = stntHomewkMapper.findTaskResuldDetail(item);
                            String questionType = MapUtils.getString(taskResuldDetailMap, "questionType");
                            // questionType - ptqz (발음평가형) 인 경우
                            if("ptqz".equals(questionType)) {
                                String menuSeCd  = "2"; // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                                int trgtId       = MapUtils.getInteger(taskResuldDetailMap,"resultDetailId");
                                String subMitAnw = MapUtils.getString(item, "subMitAnw");
                                // 발성평가 점수 등록 처리
                                stntMdulVocalScrService.saveVocalEvlScrInfo(menuSeCd, trgtId, subMitAnw);
                            }
                        }

                        Map<String, Object> taskResultMap = stntHomewkMapper.findTaskResultDetailCount(item);
                        taskResultMap.put("taskId", item.get("taskId"));
                        taskResultMap.put("taskResultId", item.get("taskResultId"));
                        taskResultMap.put("userId", item.get("userId"));

                        if (MapUtils.getInteger(taskResultMap, "cnt") == 0) {
                            taskResultMap.put("eakSttsCd", 5);

                            stntHomewkMapper.modifyStntTaskResultInfo(taskResultMap);
                        }

                    } catch (Exception e) {
                        log.error("modifyStntHomewkSave 호출 중 오류 발생: {}", item, e);
                        throw e;
                    }
                }
            }


        }

        // 과제 마스터 정보 상태 변경 (배치처리 X, 원복처리)
        // 2024-05-22: 과제 마스터 상태 처리 제외
        //int result2 = stntHomewkMapper.modifyStntHomewkSubmitTaskInfo(paramData);
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

        returnMap.putAll(AidtCommonUtil.filterToMap(homewkInfoItem, stntHomewkMapper.findStntHomewkSubmit(paramData)));
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntHomewkResult(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> homewkInfoItem = Arrays.asList("id", "eamTrget", "setsId", "taskNm", "taskPrgDt", "timTime", "eamExmNum", "rwdSetAt", "rwdPoint", "edGidAt", "edGidDc", "submAt");
        List<String> homewkResultInfoItem = Arrays.asList("id", "setsId", "taskResultId", "taskIemId", "subId", "mrkTy", "mrkTyNm", "errata", "errataNm", "eakAt", "submAt", "fdbDc", "name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen", "subMitAnw", "subMitAnwUrl", "hint", "sbsChatting", "sbsSolution", "hintYN", "sbsChattingYN","sbsSolutionYN");

        returnMap = AidtCommonUtil.filterToMap(homewkInfoItem, stntHomewkMapper.findStntHomewkResult(paramData));
        returnMap.put("taskItemList", AidtCommonUtil.filterToList(homewkResultInfoItem, stntHomewkMapper.findStntHomewkResultDetail(paramData)));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntHomewkResultinfo(Map<String, Object> paramData) throws Exception {
        List<String> taskDetailItem = Arrays.asList("taskDetailId", "setsId", "taskResultId", "taskIemId", "subId", "subMitAnw", "subMitAnwUrl", "errata", "errataNm", "hdwrtCn", "eakAt", "submAt", "questionStr");
        return AidtCommonUtil.filterToMap(taskDetailItem, stntHomewkMapper.findStntHomewkResultinfo(paramData));
    }

    public Object modifyStntTaskInit(Map<String, Object> paramData) throws Exception {
        List<String> item = Arrays.asList("taskId"
                , "userId"
                , "taskResultId"
                , "taskIemId"
                , "subId"
                , "resultOk"
                , "resultMsg");
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }
        // 피드백 저장
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.putAll(paramData);

        int cnt = stntHomewkMapper.modifyStntTaskInit(paramData);

        if (cnt > 0) {
            stntHomewkMapper.modifyStntEvalInitForTaskResultInfo(paramData);
            stntHomewkMapper.modifyStntEvalInitForTaskInfo(paramData);
        } else {
            throw new AidtException("과제 답안 초기화 실패");
        }
        resultMap.put("taskId", paramData.get("taskId")); //평가 id
        resultMap.put("userId", paramData.get("userId")); //응시학생 id
        resultMap.put("taskResultId", paramData.get("taskResultId")); //평가결과 id
        resultMap.put("taskIemId", paramData.get("taskIemId")); //평가항목 id
        resultMap.put("subId", paramData.get("subId")); //평가항목 id
        resultMap.put("resultOk", cnt > 0);
        resultMap.put("resultMsg", "성공");
        // Response
        return AidtCommonUtil.filterToMap(item, resultMap);
    }

    public Object modifyStntHomewkRecheck(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Map<String, Object> resultInfoMap = stntHomewkMapper.findStntHomewk(paramData);
        if ("Y".equals(MapUtils.getString(resultInfoMap, "submAt", ""))) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }
        int result1 = stntHomewkMapper.modifyStntHomewkRecheck(paramData);
        log.info("result1:{}", result1);



        returnMap.put("taskId", paramData.get("taskId"));
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

    public Object modifyStntHomewkAiTutSave(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        // 채팅 내용 list
        List<Map<String, Object>> aiTutChtCnList = new ArrayList<>();

        int resultDetailId = stntHomewkMapper.findStntHomewkIdByArticle(paramData);
        if (resultDetailId == 0) {
            throw new AidtException("resultDetailId is empty - findStntHomewkIdByArticle error");
        }

        paramData.put("taskResultDetailId", resultDetailId);

        // 채팅 내용 list 기존 채팅 내용 있는 경우 list 에 추가
        String aiTutChtCnOld = stntHomewkMapper.findStntHomewkAiTutCn(paramData);
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
        int result = stntHomewkMapper.modifyStntHomewkAiTutSave(paramData);
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


    @Transactional(readOnly = true)
    public Object findStntHomewkTimeUsage(Map<String, Object> paramData) throws Exception {
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
        returnMap.put("resultData", stntHomewkMapper.findStntHomewkTimeUsage(paramData));
        return returnMap;

    }

    public Object saveStntHomewkTimeUsage(Map<String, Object> paramData) throws Exception {
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
        if (stntHomewkMapper.saveStntHomewkTimeUsage(paramData) > 0) {
            returnMap.put("resultOk", true);
        }

        return returnMap;
    }

    private Map<String, Object> createErrorResponse(Map<String, Object> returnMap, String errorMessage) {
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", errorMessage);
        return returnMap;
    }

}
