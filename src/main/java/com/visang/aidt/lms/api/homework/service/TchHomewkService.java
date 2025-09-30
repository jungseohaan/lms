package com.visang.aidt.lms.api.homework.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchEvalMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchSlfperEvalMapper;
import com.visang.aidt.lms.api.assessment.service.TchSlfperEvalService;
import com.visang.aidt.lms.api.homework.mapper.TchHomewkMapper;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName : com.visang.aidt.lms.api.homework.service
 * fileName : TchHomewkService
 * USER : hs84
 * date : 2024-01-24
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-24         hs84          최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TchHomewkService {
    private final TchHomewkMapper tchHomewkMapper;
    private final TchEvalMapper tchEvalMapper;

    private final TchSlfperEvalService tchSlfperEvalService;
    private final TchSlfperEvalMapper tchSlfperEvalMapper;

    private final TchReportHomewkService tchReportHomewkService;

    @Transactional(readOnly = true)
    public Object findTchHomewkList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        List<String> taskListItem = new ArrayList<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> taskInfoList = new ArrayList<>();
        taskListItem = Arrays.asList("id", "taskNm", "eamMth", "eamTrget", "tmprStrgAt", "taskSttsCd", "taskSttsNm", "taskPrgDt", "taskCpDt", "targetCnt", "submitCnt", "setsId", "regDt" ,"delYn");
        taskInfoList = (List<Map>) tchHomewkMapper.findTchHomewkListTaskList(pagingParam);
        /*
        if ("N".equals(paramData.get("tmprStrgAt"))) {
            taskListItem = Arrays.asList("no", "id", "eamMth", "taskNm", "eamTrget", "taskSttsCd", "taskSttsNm", "taskPrgDt", "taskCpDt", "targetCnt", "submitCnt", "isEncouragement", "reportLinkYn");
            taskInfoList = (List<Map>) tchHomewkMapper.findTchHomewkListTaskList_bak(pagingParam);
        } else {
            taskListItem = Arrays.asList("no", "id", "eamMth", "taskNm", "regDt");
            taskInfoList = (List<Map>) tchHomewkMapper.findTchHomewkListTaskListTmpr(pagingParam);
        }
         */

        if (!taskInfoList.isEmpty()) {
            total = (long) taskInfoList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(taskInfoList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        List<String> finalTaskListItem = taskListItem;
        List<LinkedHashMap<Object, Object>> taskList = CollectionUtils.emptyIfNull(taskInfoList).stream().map(s -> {
            var tgtMap = new LinkedHashMap<>();
            if(Objects.isNull(s)) return tgtMap;

            var srcMap = new ObjectMapper().convertValue(s, Map.class);
            finalTaskListItem.forEach(ss -> {
                if ("isEncouragement".equals(ss)){
                    //if (((Integer) srcMap.get(ss) == 1)){
                    if (MapUtils.getInteger(srcMap, ss) == 1) {
                        tgtMap.put(ss, true);
                    } else {
                        tgtMap.put(ss, false);
                    }
                } else {
                    tgtMap.put(ss, srcMap.get(ss));
                }
            });
            return tgtMap;
        }).toList();

        returnMap.put("taskList", taskList);
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchHomewkInfo(Map<String, Object> paramData) throws Exception {
        List<String> taskInfoItem = Arrays.asList("id", "setsId", "taskNm", "eamMth", "eamMthNm", "eamTrget", "eamExmNum", "timTime", "rwdSetAt", "rwdPoint", "taskPrgDt", "taskCpDt", "taskSttsCd", "taskSttsNm", "rpOthbcAt", "rpOthbcDt", "aiTutSetAt", "reportLinkYn", "submitCnt", "taskDivision");

        LinkedHashMap<Object, Object> taskInfoMap = AidtCommonUtil.filterToMap(taskInfoItem, tchHomewkMapper.findTchHomewkListTaskInfo(paramData));

        return taskInfoMap;
    }

    @Transactional(readOnly = true)
    public Object findTchHomewkPreview(Map<String, Object> paramData) throws Exception {
        var resultMap = new LinkedHashMap<>();

        List<String> taskInfoItem = Arrays.asList("id", "eamTrgetYn", "setsId", "taskNm", "taskPrgDt", "taskCpDt", "timTime");
        List<String> studentInfoItem = Arrays.asList("id", "userId", "flnm", "setsId");

        resultMap = AidtCommonUtil.filterToMap(taskInfoItem, tchHomewkMapper.findTchHomewkPreviewTaskInfo(paramData));
        log.info("eamTrgetYn: {}",resultMap.get("eamTrgetYn"));

        if( "Y".equals(String.valueOf(resultMap.get("eamTrgetYn"))) ) {
            List<LinkedHashMap<Object, Object>> studentInfoList = AidtCommonUtil.filterToList(studentInfoItem, tchHomewkMapper.findTchHomewkPreviewStudentList(paramData));

            resultMap.put("studentList", studentInfoList);
        }

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findTchHomewkResultStatus(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> taskInfoItem = Arrays.asList("id", "setsId", "taskNm", "taskPrgDt", "taskCpDt", "timTime", "targetCnt", "examCnt", "eamExmNum", "taskSttsCd", "taskSttsNm");
        List<String> taskIemInfoItem = Arrays.asList("id", "taskIemId", "subId", "name", "url", "image", "thumbnail", "questionStr", "hashTags", "isActive", "isPublicOpen", "isEditable", "correctRate");
        List<String> taskResultDetItem = Arrays.asList("taskIemId", "subId", "subMitAnw", "subMitAnwUrl", "userIdx", "userId", "flnm", "submAt", "actvtnAt");

        List<String> subMitAnwStntItem = Arrays.asList("taskIemId", "subId", "subMitAnw", "anwStntList");
        List<String> anwStntItem = Arrays.asList("userIdx", "userId", "flnm");

        LinkedHashMap<Object, Object> taskInfoMap = AidtCommonUtil.filterToMap(taskInfoItem, tchHomewkMapper.findTchHomewkResultStatusTaskInfo(paramData));
        List<Map> taskResultDetList = tchHomewkMapper.findTchHomewkResultStatusTaskResultDet(paramData);

        List<Map> subMitAnwStntList = tchHomewkMapper.findTchHomewkResultStatusSubMitAnwStnt(paramData);
        List<Map> anwStntList = tchHomewkMapper.findTchHomewkResultStatusAnwStudent(paramData);

        List<LinkedHashMap<Object, Object>> taskIemInfoList = AidtCommonUtil.filterToList(taskIemInfoItem, tchHomewkMapper.findTchHomewkResultStatusTaskIemInfo(paramData)).stream()
                .map(s -> {
                    List<LinkedHashMap<Object, Object>> returnDetList = CollectionUtils.emptyIfNull(taskResultDetList).stream()
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId")))
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                            .map(r -> {
                                return AidtCommonUtil.filterToMap(taskResultDetItem, r);
                            }).toList();
                    s.put("taskResultDetList" , returnDetList);

                    List<LinkedHashMap<Object, Object>> returnSubMitAnwStntList = CollectionUtils.emptyIfNull(subMitAnwStntList).stream()
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId")))
                            .filter(r -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                            .map(r -> {
                                List<LinkedHashMap<Object, Object>> returnAnwStntList = CollectionUtils.emptyIfNull(anwStntList).stream()
                                        .filter(g -> StringUtils.equals(MapUtils.getString(r,"taskIemId"), MapUtils.getString(g,"taskIemId")))
                                        .filter(g -> StringUtils.equals(MapUtils.getString(r,"subMitAnw"), MapUtils.getString(g,"subMitAnw")))
                                        .filter(g -> StringUtils.equals(MapUtils.getString(r,"subId"), MapUtils.getString(g,"subId")))
                                        .map(g -> {
                                            return AidtCommonUtil.filterToMap(anwStntItem, g);
                                        }).toList();
                                r.put("anwStntList" , returnAnwStntList);
                                return AidtCommonUtil.filterToMap(subMitAnwStntItem, r);
                            }).toList();
                    s.put("subMitAnwStntList" , returnSubMitAnwStntList);

                    return s;
                }).toList();

        taskInfoMap.put("taskIemList", taskIemInfoList);

        returnMap.put("taskInfo", taskInfoMap);

        return returnMap;
    }

    public Object removeTchHomewkDelete(Map<String, Object> paramData) throws Exception {
        var resultMap = new LinkedHashMap<>();

        Map<String, Object> taskInfo = tchHomewkMapper.findTaskInfo(paramData);

        resultMap.put("taskId", paramData.get("taskId"));

        //과제 예정/진행/종료 모두 삭제 가능
        if (taskInfo == null || taskInfo.isEmpty()) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "예정건만 삭제할 수 있습니다.");
        } else {
            int result1 = tchHomewkMapper.removeTchTaskResultDetail(paramData);
            int result2 = tchHomewkMapper.removeTchTaskResultInfo(paramData);
            int result3 = tchHomewkMapper.removeTchTaskTrnTrget(paramData);
            int result4 = tchHomewkMapper.removeTchTaskInfo(paramData);
            int result5 = tchHomewkMapper.modifyTchAiCustomTaskInfo(paramData);



            log.info("result1:{}", result1);
            log.info("result2:{}", result2);
            log.info("result3:{}", result3);
            log.info("result4:{}", result4);
            log.info("result5:{}", result5);

            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "삭제완료");
        }

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findTchHomewkReadInfo(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("wrterId", "claId", "textbookId", "setsId", "eamMth", "eamMthNm", "taskNm", "pdEvlStDt", "pdEvlEdDt", "taskPrgDt", "taskCpDt", "ntTrnAt", "bbsSvAt", "bbsNm", "tag", "cocnrAt", "timStAt", "timTime", "prscrStdSetAt", "prscrStdStDt", "prscrStdEdDt", "prscrStdNtTrnAt", "aiTutSetAt", "rwdSetAt", "edGidAt", "edGidDc", "stdSetAt","rptAutoOthbcAt");

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap = AidtCommonUtil.filterToMap(evalInfoItem, tchHomewkMapper.findTchHomewkReadInfo(paramData));

        // 모둠 출제 일때 학생 목록 조회
        /*
        if (MapUtils.getIntValue(returnMap, "eamMth", 0) == 8) {
            List<Map> stntList = tchHomewkMapper.findTchHomewkReadInfoStntList(paramData);
            returnMap.put("stntList", stntList);
        }

         */

        // 모든 과제 에서 학생 목록 조회
        List<Map> stntListAll = tchHomewkMapper.findTchHomewkStntList(paramData);
        // returnMap.put("stntListAll", stntListAll);
        returnMap.put("stntList", stntListAll);

        return returnMap;
    }

    public Map<String, Object> createTchHomewkSave(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        Map<String, Object> setsInsertParamMap = new HashMap<>();

        Map<String, Object> taskInfoOriginMap = tchHomewkMapper.findTaskInfo(paramData);

        // 기간 변경 여부 체크
        String originalStartDate = String.valueOf(taskInfoOriginMap.get("taskPrgDt"));
        String originalEndDate = String.valueOf(taskInfoOriginMap.get("taskCpDt"));
        String newStartDate = String.valueOf(paramData.get("pdEvlStDt"));
        String newEndDate = String.valueOf(paramData.get("pdEvlEdDt"));

        // T를 공백으로 치환하여 정규화
        String normalizedOriginalStart = originalStartDate.replace("T", " ");
        String normalizedOriginalEnd = originalEndDate.replace("T", " ");
        String normalizedNewStart = newStartDate.replace("T", " ");
        String normalizedNewEnd = newEndDate.replace("T", " ");


        List<Map<String, Object>> prevStntList = tchHomewkMapper.findExistingTaskResultInfo(paramData);
        Set<String> existingStntIds = prevStntList.stream()
                .map(m -> String.valueOf(m.get("stntId")))
                .collect(Collectors.toSet());


        List<Map<String, Object>> newStntList = (List<Map<String, Object>>) paramData.get("stntList");
        Set<String> newStntIds = newStntList.stream()
                .map(m -> String.valueOf(m.get("stntId")))
                .collect(Collectors.toSet());

        boolean isStntListChanged = !existingStntIds.equals(newStntIds);
        paramData.put("isStntListChanged", isStntListChanged ? "Y" : "N");

        if ((originalStartDate != null && !originalStartDate.isEmpty() && !"null".equals(originalStartDate) &&
                originalEndDate != null && !originalEndDate.isEmpty() && !"null".equals(originalEndDate)) &&
                "N".equals(taskInfoOriginMap.get("tmprStrgAt")) &&
                (!normalizedOriginalStart.equals(normalizedNewStart) || !normalizedOriginalEnd.equals(normalizedNewEnd))) {
            paramData.put("isPeriodChanged", "Y");
        }else{
            paramData.put("isPeriodChanged", "N");
        }

        if("N".equals(taskInfoOriginMap.get("tmprStrgAt")) && "N".equals(paramData.get("isPeriodChanged")) && "N".equals(paramData.get("isStntListChanged"))) {
            tchHomewkMapper.updateTaskInfo(paramData);

            returnMap.put("taskId", paramData.get("taskId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }else if("Y".equals(paramData.get("isPeriodChanged")) && "1".equals(taskInfoOriginMap.get("evlSttusCd"))){
            // 기간변경이 있고 평가 상태가 1인 경우
            tchHomewkMapper.updateTaskInfo(paramData);

            returnMap.put("taskId", paramData.get("taskId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }else{
            //update evl info
            int result1 =  tchHomewkMapper.modifyTchTaskSave(paramData);
            log.info("result1:{}", result1);

            Map<String, Object> evlInfoMap = tchHomewkMapper.findTaskInfo(paramData);

            List<Map<String, Object>> taskResultList = tchHomewkMapper.finTaskResultInfo(paramData);
            paramData.put("taskResultList", taskResultList);

            tchEvalMapper.increaseModuleUseCnt(evlInfoMap);

            if ("Y".equals(paramData.get("bbsSvAt"))) {
                //isnert sets tables
                int result0 = tchHomewkMapper.createTchTaskSaveSets(paramData);
                log.info("result0:{}", result0);

                setsInsertParamMap.put("newSetsid", MapUtils.getString(paramData, "newSetsid"));
                setsInsertParamMap.put("oldSetsId", evlInfoMap.get("setsId"));

                int result2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
                log.info("result2:{}", result2);

                int result3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
                log.info("result3:{}", result3);

                int result4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
                log.info("result4:{}", result4);

                int result6 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
                log.info("result6:{}", result6);

                setsInsertParamMap.put("taskId", paramData.get("taskId"));
                int result5 =  tchHomewkMapper.modifyTchTaskSaveBbsSetId(setsInsertParamMap);
                log.info("result5:{}", result5);
            }

            // 삭제 전 기존 task_result_info 데이터 조회
            List<Map<String, Object>> existingTaskResultList = tchHomewkMapper.findExistingTaskResultInfo(paramData);
            Map<String, String> stntPeriodChangedMap = new HashMap<>();

            // 학생별 isPeriodChanged 결정 (eak_stts_cd 기준)
            for (Map<String, Object> existingData : existingTaskResultList) {
                String stntId = String.valueOf(existingData.get("stntId"));
                String eakSttsCd = String.valueOf(existingData.get("eakSttsCd"));

                // eak_stts_cd에 따른 isPeriodChanged 결정 로직
                // eak_stts_cd가 2이면 period_changed_at = 'Y', 그 외는 'N'
                if (Integer.parseInt(eakSttsCd) == 2) {
                    stntPeriodChangedMap.put(stntId, "Y");
                } else {
                    stntPeriodChangedMap.put(stntId, "N");
                }
            }
            paramData.put("stntPeriodChangedMap", stntPeriodChangedMap);

            int result7 = tchHomewkMapper.removeTchTaskSaveTRD(paramData);
            log.info("result7:{}", result7);

            int result8 = tchHomewkMapper.removeTchTaskSaveTRI(paramData);
            log.info("result8:{}", result8);
            paramData.put("eamMth", MapUtils.getIntValue(evlInfoMap, "eamMth", 0));
            // 학생 목록이 존재 할 경우 해당 학생의 데이터만 생성
            List<Map<String, Object>> stntList = (List<Map<String, Object>>) Optional
                    .ofNullable(paramData.get("stntList"))
                    .orElse(Collections.emptyList());
            if (stntList.size() > 0) {
                paramData.put("stntListSize",stntList.size());
            }

            int eamTrget = tchHomewkMapper.findTchHomewkEamTrget(paramData);

            /* AI 맞춤학습 > 과제로 내기 > 개인별 맞춤 출제일 경우 */
            if (eamTrget == 2) {
                if (taskResultList.size() > 0 ) {
                    int result13 = tchHomewkMapper.createTchTaskSaveTriForEamtrget2(paramData);
                    log.info("result13:{}", result13);
                }

            } else {
                int result10 = tchHomewkMapper.createTchTaskSaveTRI(paramData);
                log.info("result10:{}", result10);
            }

            int resultCountCreateTRD = tchHomewkMapper.createTchTaskSaveTRD(paramData);
            log.info("resultCountCreateTRD:{}", resultCountCreateTRD);

            paramData.put("resultCountCreateTRD", resultCountCreateTRD);
            int result11 =  tchHomewkMapper.modifyTchTaskSaveEEN(paramData);
            log.info("result11:{}", result11);

            paramData.remove("id");
            returnMap.put("taskId", paramData.get("taskId"));
            paramData.remove("resultCountCreateTRD");

            Map<String, Object> slfEvlInfo = (Map<String, Object>) paramData.get("slfEvlInfo");
            Map<String, Object> perEvlInfo = (Map<String, Object>) paramData.get("perEvlInfo");

            int slfEvlInfoId = 0;
            if (paramData.containsKey("slfEvlInfo")) {
                if (!ObjectUtils.isEmpty(slfEvlInfo) && !slfEvlInfo.isEmpty()) {
                    Object resultSlfEvlInfo = tchSlfperEvalService.saveTchSlfperEvlSet(slfEvlInfo);
                    log.info("resultSlfEvlInfo:{}", resultSlfEvlInfo);
                }
                slfEvlInfoId = MapUtils.getInteger(slfEvlInfo, "id", 0);
            }

            int perEvlInfoId = 0;
            if (paramData.containsKey("perEvlInfo")) {
                if (!ObjectUtils.isEmpty(perEvlInfo) && !perEvlInfo.isEmpty()) {
                    Object resultPerEvlInfo = tchSlfperEvalService.saveTchSlfperEvlSet(perEvlInfo);
                    log.info("resultPerEvlInfo:{}", resultPerEvlInfo);
                }
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

            int result12 =  tchHomewkMapper.modifyTaskStatusToInProgress(paramData);
            log.info("result12:{}", result12);

            if (result1 > 0) {
                returnMap.put("taskId", paramData.get("taskId"));
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "성공");
            } else {
                returnMap.put("taskId", paramData.get("taskId"));
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "실패");
            }

            return returnMap;
        }

    }


    public Object removeTaskInit(Map<String, Object> paramData) throws Exception {
        List<String> taskInfoItem = Arrays.asList("id", "taskNm", "taskPrgDt", "taskSttsCd", "taskSttsNm");

        Map<String, Object> taskInfoMap = tchHomewkMapper.findTaskInfo(paramData);

        // 과제의 경우에는 기간설정여부 컬럼이 없음. 무조건 기간 설정이 됨.
        //if ("Y".equals(MapUtils.getString(taskInfoMap, "pdSetAt"))) {
        int result1 =  tchHomewkMapper.modifyTchTaskInitTaskInfoY(paramData);
        log.info("result1:{}", result1);
        /*} else {
            int result1 =  tchHomewkMapper.modifyTchTaskInitTaskInfoN(paramData);
            log.info("result1:{}", result1);
        }*/

        int result2 =  tchHomewkMapper.modifyTchTaskInitTaskResultInfo(paramData);
        log.info("result2:{}", result2);

        int result3 =  tchHomewkMapper.modifyTchTaskInitTaskResultDetail(paramData);
        log.info("result3:{}", result3);

        return AidtCommonUtil.filterToMap(taskInfoItem, tchHomewkMapper.findTchTaskInit(paramData));
    }

    public Map<String, Object> createTchHomewkCreate(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

        int result1 = tchHomewkMapper.createTchTaskCreate(paramData);
        log.info("result1:{}", result1);

        paramData.put("taskId", paramData.get("id"));
        returnMap.put("taskId", paramData.get("id"));
        paramData.remove("id");

        // 모둠 출제 일때 학생 목록 저장 (수정 됨, 과제 > 특정 학생 선택하여 과제 출제하는 기능)
        // 학생 목록이 존재 할 경우 학생 목록 저장
        List<Map<String, Object>> stntList = (List<Map<String, Object>>) Optional
                .ofNullable(paramData.get("stntList"))
                .orElse(Collections.emptyList());
        if (stntList.size() > 0) {
            paramData.put("stntListSize",stntList.size());
            // task_result_info insert in (stntList)
            int resultTRI = tchHomewkMapper.createTchTaskSaveTRI(paramData);
            log.info("resultTRI:{}", resultTRI);
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

    public Map<String, Object> copyHomewkInfo(Map<String, Object> paramData) throws Exception {

        log.info("copyHomewkInfo START:{}", paramData.get("taskId"));

        // 1. 기존 task_info 를 가져옴
        LinkedHashMap orgTaskInfo = tchHomewkMapper.getTaskInfoById(paramData);

        if (orgTaskInfo == null) {
            throw new AidtException("ID 에 해당하는 과제정보가 없습니다.");
        }

        LinkedHashMap newTaskInfo = ObjectUtils.clone(orgTaskInfo);
        newTaskInfo.remove("id");

        newTaskInfo.put("task_prg_dt", orgTaskInfo.get("pd_evl_st_dt"));
        newTaskInfo.put("task_cp_dt", orgTaskInfo.get("pd_evl_ed_dt"));
        newTaskInfo.put("task_stts_cd", 2);
        newTaskInfo.put("mrk_cp_dt", null);

        log.info("+++++++++++++++++++++++++++++++");
        for (Object key : newTaskInfo.keySet()) {
            Object value = newTaskInfo.get(key);
            log.info("{}:{}", key, value);
        }
        log.info("+++++++++++++++++++++++++++++++");

        // task_info copy - CSAP_250812_lhr
        int taskInfoCloneCnt = tchHomewkMapper.cloneTaskInfo(newTaskInfo);
        log.info("1.task_info 복사 성공: cnt:{}",taskInfoCloneCnt );

        var newTaskId = newTaskInfo.get("id");
        log.info("[task_info]gen id:{}", newTaskId);

        // evl_result_info copy
        List<LinkedHashMap> orgTaskResultInfoList = tchHomewkMapper.findTaskResultInfoListByTaskId(paramData);

        log.info("orgTaskResultInfoList.size() = {}", orgTaskResultInfoList.size());

        int idx = 0;
        for (LinkedHashMap orgTaskResultInfo : orgTaskResultInfoList) {
            var orgTaskResultId = orgTaskResultInfo.get("id");
            orgTaskResultInfo.remove("id");

            LinkedHashMap newTaskResultInfo = ObjectUtils.clone(orgTaskResultInfo);
            newTaskResultInfo.put("task_id", newTaskId); // 새로 copy한 task_id
            newTaskResultInfo.put("eak_stts_cd", 1);
            newTaskResultInfo.put("eak_at", 'N');
            newTaskResultInfo.put("subm_at", 'N');
            newTaskResultInfo.put("mrk_cp_at", 'N');
            newTaskResultInfo.put("eak_st_dt", null);
            newTaskResultInfo.put("eak_ed_dt", null);
            newTaskResultInfo.put("task_result_scr", null);
            newTaskResultInfo.put("task_result_anct", null);
            newTaskResultInfo.put("reg_dt", new Date());
            newTaskResultInfo.put("mdfy_dt", new Date());

            // task_result_info copy - CSAP_250812_lhr
            int taskResultInfoCloneCnt = tchHomewkMapper.cloneTaskResultInfo(newTaskResultInfo);
            log.info("2-{}.task_result_info 복사 성공: cnt={}", ++idx, taskResultInfoCloneCnt);
            var newTaskResultId = newTaskResultInfo.get("id");
            log.info("[task_result_info]gen id:{}", newTaskResultId);

            // copy task_result_detail
            Map tmpParamMap = new HashMap();
            tmpParamMap.clear();
            tmpParamMap.put("oldTaskResultId", orgTaskResultId);
            tmpParamMap.put("newTaskResultId", newTaskResultId);

            int taskResultDetailCloneCnt = tchHomewkMapper.copyTaskResultDetailByTaskId(tmpParamMap);
            log.info("3-{}.task_result_detail 복사 성공: cnt={}", idx, taskResultDetailCloneCnt);
        }


//        tchEvalMapper.cloneAnyTableByMap(null); // 강제오류발생

        Map resultMap = new HashMap();
        resultMap.put("taskId", paramData.get("taskId"));
        resultMap.put("newTaskId", newTaskId);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findHomewkAutoQstnExtr(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> articleInfoItem = Arrays.asList("id", "name", "thumbnail", "questionTypeNm", "difyNm");

        // 피드백 저장
        Map<String, Object> resultMap = new LinkedHashMap<>();
        int eamExmNum = MapUtils.getIntValue(paramData,"eamExmNum"); // 출제 문항수
        int eamGdExmMun = MapUtils.getIntValue(paramData,"eamGdExmMun"); // 상
        int eamAvUpExmMun = MapUtils.getIntValue(paramData,"eamAvUpExmMun"); // 중상
        int eamAvExmMun = MapUtils.getIntValue(paramData,"eamAvExmMun"); // 중
        int eamAvLwExmMun = MapUtils.getIntValue(paramData,"eamAvLwExmMun"); // 중하
        int eamBdExmMun = MapUtils.getIntValue(paramData,"eamBdExmMun"); // 하
        int difyExmNum = eamGdExmMun + eamAvUpExmMun + eamAvExmMun + eamAvLwExmMun + eamBdExmMun; // 난이도 문항수
        if(eamExmNum != difyExmNum) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", String.format("출제 문항수와 난이도 문항수가 다릅니다: %s != %s", eamExmNum, difyExmNum));
            return resultMap;
        }
        Map<Object, Object> procParamData = new HashMap<Object, Object>(paramData);

        // eamScp 값들 중에서 studyMap_1에 해당하는 값들만 필터링
        List eamScpList = (List) procParamData.get("eamScp");
        if (eamScpList != null && !eamScpList.isEmpty()) {
            List<Long> studyMap1MetaIds = tchEvalMapper.findStudyMap1MetaIds(eamScpList);
            procParamData.put("eamScp", studyMap1MetaIds);
            eamScpList = studyMap1MetaIds;
        }

        List<LinkedHashMap<Object, Object>> articleList = new ArrayList<>();
        Set<String> selectedArticleIds = new HashSet<>();
        Set<String> selectedStudyMap_1 = new HashSet<>();

        // List eamScpList = (List) procParamData.get("eamScp");
        int eamScpSize = eamScpList.size();

        Object[][] difyArr = {
                {"MD05", "하", eamBdExmMun},
                {"MD04", "중하", eamAvLwExmMun},
                {"MD03", "중", eamAvExmMun},
                {"MD02", "중상", eamAvUpExmMun},
                {"MD01", "상", eamGdExmMun}
        };

        StringBuffer sb = new StringBuffer();
        Boolean articleCntCheck = true;
        for (Object[] difyObj : difyArr) {
            int difyLimit = (int) difyObj[2];
            if(difyLimit <= 0) continue;

            procParamData.put("difyCode", difyObj[0]);
            procParamData.put("difyLimit", difyLimit);
            procParamData.put("excludeIds", selectedArticleIds); // 이미 선택된 ID 전달
            procParamData.put("excludeStudyMaps", selectedStudyMap_1); // 이미 선택된 지식요인 전달

            List<Map> evalAutoQstnExtr = tchHomewkMapper.findHomewkAutoQstnExtr(procParamData);

            if(evalAutoQstnExtr.size() != difyLimit) {
                articleCntCheck = false;
                sb.append(difyObj[1]+":").append(difyLimit-evalAutoQstnExtr.size()).append(",");
                //throw new AidtException(String.format("난이도(%s) 문항 개수가 부족합니다.: %s < %s",difyObj[0],difyLimit,evalAutoQstnExtr.size()));
            }

            // 선택된 ID, 지식요인 추가
            if (CollectionUtils.isNotEmpty(evalAutoQstnExtr)) {
                for (Map item : evalAutoQstnExtr) {
                    selectedArticleIds.add(MapUtils.getString(item, "id"));
                    selectedStudyMap_1.add(MapUtils.getString(item, "studymap1"));

                    if (eamScpSize <= selectedStudyMap_1.size()) {
                        selectedStudyMap_1.clear();
                    }
                }
            }

            articleList.addAll(AidtCommonUtil.filterToList(articleInfoItem, evalAutoQstnExtr));
        }

        if (!articleCntCheck) {
            String cntString = sb.toString();
            if (cntString.endsWith(",")) {
                cntString = cntString.substring(0, cntString.length() - 1);
            }
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
            return resultMap;
        }

        resultMap.put("articleList", articleList);
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");

        // Response
        return resultMap;
    }

    public Object modifyHomewkSaveByMagicWand(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        Map<String, Object> setsInsertParamMap = new HashMap<>();
        int resultTask = 0;

        //과제가 설정미완료인지 설정완료이고 예정상태인지 확인한다.
        //   설정미완료: tmpr_strg_at = 'Y' 인 경우
        //   설정완료이고 예정상태: tmpr_strg_at = 'N' 이고 task_stts_cd = 1 인 경우

        Map<String, Object> taskInfoMap = tchHomewkMapper.findTaskInfo(paramData);
        if(MapUtils.isEmpty(taskInfoMap)) {
            returnMap.put("taskId", paramData.get("taskId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패: 과제 정보가 존재하지 않습니다.");
            return returnMap;
        }

//        if (MapUtils.getIntValue(taskInfoMap, "taskSttsCd", 0) == 2) {
//            returnMap.put("taskId", paramData.get("taskId"));
//            returnMap.put("resultOk", false);
//            returnMap.put("resultMsg", "진행중인 과제는 편집할 수 없습니다.");
//            return returnMap;
//        }

        //1. 설정미완료: tmpr_strg_at = 'Y' 인 경우
        if("Y".equals(MapUtils.getString(taskInfoMap,"tmprStrgAt"))) {
            //task_info 의 sets_id 업데이트한다.
            resultTask = tchHomewkMapper.modifyTchTaskSaveSetsId(paramData);
        }
        //2. 설정완료이고 예정상태: tmpr_strg_at = 'N' 이고 예정, 진행인 경우
        else if("N".equals(MapUtils.getString(taskInfoMap,"tmprStrgAt")) && ("1".equals(MapUtils.getString(taskInfoMap,"taskSttsCd")) || "2".equals(MapUtils.getString(taskInfoMap,"taskSttsCd")))) {
            Map<String,Object> setsHistInfo = new HashMap<>();
            setsHistInfo.put("parentSetsId",taskInfoMap.get("setsId"));
            setsHistInfo.put("claId",taskInfoMap.get("claId"));
            setsHistInfo.put("trgtId",taskInfoMap.get("id"));
            setsHistInfo.put("trgtSeCd","2");
            setsHistInfo.put("sets",paramData.get("setsId"));
            setsHistInfo.put("userId",taskInfoMap.get("wrterId"));

            setsHistInfo.put("setsUpdatedAt", "2".equals(String.valueOf(taskInfoMap.get("taskSttsCd"))) ? "Y" : "N");


            int resultset = tchEvalMapper.insertSetHist(setsHistInfo);
            //task_info 의 sets_id 업데이트한다.
            resultTask = tchHomewkMapper.modifyTchTaskSaveSetsId(paramData);

            // task_result_info 삭제 하기 전 학생 목록 조회
            List<Map> stntList = tchHomewkMapper.findTchHomewkStntList(paramData);
            List<Map> filteredList = stntList.stream()
                    .filter(map -> "Y".equals(map.get("trgtAt")))
                    .collect(Collectors.toList());

            //task_result_info, task_result_detail 삭제
            int result1 = tchHomewkMapper.removeTchTaskResultDetail(paramData);
            int result2 = tchHomewkMapper.removeTchTaskResultInfo(paramData);

            //모듈(아티클) 사용 횟수 정보(module_use_cnt) 테이블에 등록/수정
            taskInfoMap.put("setsId", MapUtils.getString(paramData,"setsId"));
            tchEvalMapper.increaseModuleUseCnt(taskInfoMap);

            //task_result_info 생성
            //조회 된 학생이 있을 경우 조회된 학생만 생성
            //조회 된 학생이 없을 경우 모든 학생 생성
            if (stntList.size() > 0) {
                paramData.put("stntListSize",filteredList.size());
                paramData.put("stntList",filteredList);
                int resultTRI = tchHomewkMapper.createTchTaskSaveTRI(paramData);
                log.info("resultTRI:{}", resultTRI);

                paramData.remove("stntListSize");
                paramData.remove("stntList");
            } else {
                int resultTRI = tchHomewkMapper.createTchTaskSaveTRI(paramData);
                log.info("resultTRI:{}", resultTRI);
            }

            //task_result_detail 생성
            int resultCountCreateTRD = tchHomewkMapper.createTchTaskSaveTRD(paramData);
            log.info("resultCountCreateTRD:{}", resultCountCreateTRD);


            //bbsSvAt = 'Y' 일때 처리하는 로직 수행
            if ( "Y".equals(MapUtils.getString(taskInfoMap,"bbsSvAt")) ) {
                //isnert sets tables

                //세트지 삭제
                //평가에 있는 서비스랑 같은것 사용
                int resultRmv2 = tchEvalMapper.removeTchEvalSaveSAM(taskInfoMap);
                log.info("resultRmv2:{}", resultRmv2);
                int resultRmv3 = tchEvalMapper.removeTchEvalSaveSKM(taskInfoMap);
                log.info("resultRmv3:{}", resultRmv3);
                int resultRmv4 = tchEvalMapper.removeTchEvalSaveSMM(taskInfoMap);
                log.info("resultRmv4:{}", resultRmv4);
                int resultRmv5 = tchEvalMapper.removeTchEvalSaveSummary(taskInfoMap);
                log.info("resultRmv5:{}", resultRmv5);
                int resultRmv1 = tchEvalMapper.removeTchEvalSaveSets(taskInfoMap);
                log.info("resultRmv1:{}", resultRmv1);

                //세트지 생성
                //기존 bbsSetsId 값으로 생성해야한다. (2024.05.28)
                setsInsertParamMap.put("createdByBbsSetsId", "Y");
                setsInsertParamMap.put("taskId", paramData.get("taskId"));
                setsInsertParamMap.put("bbsSetsId", taskInfoMap.get("bbsSetsId"));
                setsInsertParamMap.put("oldSetsId", taskInfoMap.get("setsId"));

                int resultBbs1 = tchHomewkMapper.createTchTaskSaveSets(setsInsertParamMap);
                log.info("resultBbs1:{}", resultBbs1);

                int resultBbs2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
                log.info("resultBbs2:{}", resultBbs2);

                int resultBbs3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
                log.info("resultBbs3:{}", resultBbs3);

                int resultBbs4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
                log.info("resultBbs4:{}", resultBbs4);

                int resultBbs5 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
                log.info("resultBbs5:{}", resultBbs5);

                //기존 bbsSetsId 값으로 세트지를 생성했기 때문에 업데이트가 불필요함. (2024.05.28)
                //setsInsertParamMap.put("taskId", paramData.get("taskId"));
                //int resultBbs6 =  tchHomewkMapper.modifyTchTaskSaveBbsSetId(setsInsertParamMap);
                //log.info("resultBbs6:{}", resultBbs6);
            }

            //task_info 의 문항수 업데이트
            int result4 =  tchHomewkMapper.modifyTchTaskSaveEEN(paramData);
            log.info("result4:{}", result4);

            tchHomewkMapper.updateTaskAt(setsHistInfo);

            //해당 과제ID에 대한 자기/동료평가(이)가 존재하는지 확인한다.
            //자기동료평가설정정보(slf_per_evl_set_info) sets_id 업데이트 ( 불필요함 ) - 작성하지 않는다.
            //과제의 자료실저장여부(bbsSvAt)가 = 'Y' 이면 자기동료평가세트지매핑(slf_per_sets_mapng)에 등록한다.

            //기존 bbsSetsId 값으로 세트지를 생성했기 때문에 자기/동료평가 등록이 불필요함. (2024.05.28)
            /*
            Map<String, Object> slfPerParamMap = new HashMap<>();
            //자기평가 설정정보 조회
            slfPerParamMap.put("gbCd", 2);   //1:교과자료, 2:과제, 3:평가
            slfPerParamMap.put("slfPerEvlSetInfo", 1);
            slfPerParamMap.put("taskId", paramData.get("taskId"));

            var selInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(slfPerParamMap);

            //동료평가 설정정보 조회
            slfPerParamMap.put("slfPerEvlSetInfo", 2);
            var perInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(slfPerParamMap);

            //평가의 자료실저장여부(bbsSvAt)가 = 'Y' 이면
            if ( "Y".equals(MapUtils.getString(taskInfoMap,"bbsSvAt")) ) {

                //자기평가
                if (!ObjectUtils.isEmpty(selInfoIdMap) && !selInfoIdMap.isEmpty()) {
                    setsInsertParamMap.put("slfPerEvlSetId", selInfoIdMap.get("id"));
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }

                //동료평가
                if (!ObjectUtils.isEmpty(perInfoIdMap) && !perInfoIdMap.isEmpty()) {
                    setsInsertParamMap.put("slfPerEvlSetId", perInfoIdMap.get("id"));
                    tchSlfperEvalMapper.saveSlfPerSetsMapng(setsInsertParamMap);
                }
            }
            */
        }

        if (resultTask > 0) {
            returnMap.put("taskId", paramData.get("taskId"));
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("taskId", paramData.get("taskId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object createTchHomewkCreateForTextbk(List<Map<String, Object>> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        for (Map<String, Object> paramMap : paramData) {

            /* [S] 로그인을 연속으로 할 경우 중복 등록의 우려가 있어 로직 추가 */
            Map<String, Object> dupChkMap = tchEvalMapper.findTchTaskInfoBySet(paramMap);
            // 이미 있을 경우 continue
            if (MapUtils.isNotEmpty(dupChkMap)) {
                continue;
            }
            /* [E] 로그인을 연속으로 할 경우 중복 등록의 우려가 있어 로직 추가 */

            //입력 값
            Map<String, Object> createMap = ObjectUtils.clone(paramMap);

            //기본 설정 값
            createMap.put("eamMth", 3); //3(직접출제)
            createMap.put("eamTrget", 1); //1(공통문항출제)
            //createMap.put("eamExmNum", n); //셋트지의 setsummary 항목갯수 //쿼리에서 select count
            if (ObjectUtils.isNotEmpty(MapUtils.getString(createMap, "timTime"))) {
                createMap.put("timStAt", "Y"); //타이머 시/분/초(timTime) 값이 존재하면 Y로 설정
            }
            createMap.put("rwdSetAt", "Y");
            createMap.put("taskSttsCd", 1);

            int createTaskInfoCount = tchHomewkMapper.createTchTaskCreateForTextbk_taskInfo(createMap);
            log.info("createTaskInfoCount:{}", createTaskInfoCount);

            int createTaskResultInfoCount = tchHomewkMapper.createTchTaskCreateForTextbk_taskResultInfo(createMap);
            log.info("createTaskResultInfoCount:{}", createTaskResultInfoCount);

            int createTaskResultDetailCount = tchHomewkMapper.createTchTaskCreateForTextbk_taskResultDetail(createMap);
            log.info("createTaskResultDetailCount:{}", createTaskResultDetailCount);
        }

        return returnMap;
    }

    public Object modifyTchHomewkPeriodChange(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "저장실패");

        // 선택된 과제의 상태값이 요청 파라미터의 상태값과 동일하지 않은 경우
        LinkedHashMap taskInfoMap = tchHomewkMapper.getTaskInfoById(paramData);
        int sttsCd = MapUtils.getIntValue(taskInfoMap, "task_stts_cd", -1);
        int paramSttsCd = MapUtils.getIntValue(paramData, "taskSttsCd", -2);

        if (sttsCd != paramSttsCd) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg","진행중인 과제의 응시 기간은 수정할 수 없습니다.");
            return returnMap;
        }

        int modifyCnt = tchHomewkMapper.modifyTchHomewkPeriodChange(paramData);
        log.info("result1:{}", modifyCnt);

        // 진행중(taskSttsCd:2) 인 경우
        // 학생의 상태가 진행 중 일 경우 (where result_info.eak_stts_cd = 2)
        // result_info의 응시 종료일(eak_ed_dt) 값도 같이 변경
        if (paramSttsCd == 2) {
            int modifyResultInfoCnt = tchHomewkMapper.modifyTchHomewkResultInfo(paramData);
            log.info("result1:{}", modifyResultInfoCnt);
        }

        if (modifyCnt > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        }

        return returnMap;
    }

    public Object findTchHomewkStatusList(Map<String, Object> paramData) throws Exception {
        List<String> currentTaskListItem = Arrays.asList("id", "taskNm", "targetCnt", "submitCnt","gradeSttsNm","rptOthbcAt"
                ,"rpOthbcDt","applScrAt","modifyHistAt","fullCount","manualCnt","modifyHistAt");
        //List<String> reqGradeTaskListItem = Arrays.asList("id", "taskNm", "eamTrget");
        List<String> reqGradeTaskListItem = Arrays.asList("id", "taskNm", "eamTrget","gradeSttsNm","rptOthbcAt"
                ,"rpOthbcDt","submitCnt","applScrAt","modifyHistAt","fullCount","manualCnt","modifyHistAt");

        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

        List<LinkedHashMap<Object, Object>> currentTaskList = AidtCommonUtil.filterToList(currentTaskListItem, tchHomewkMapper.findTchHomewkStatusList_currentTaskList(paramData));
        List<LinkedHashMap<Object, Object>> reqGradeTaskList = AidtCommonUtil.filterToList(reqGradeTaskListItem, tchHomewkMapper.findTchHomewkStatusList_reqGradeTaskListItem(paramData));

        returnMap.put("currentTaskList", currentTaskList);
        returnMap.put("reqGradeTaskList", reqGradeTaskList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findHomewkSubmStatus(Map<String, Object> paramData) throws Exception {
        var resultMap = new LinkedHashMap<>();

        List<String> taskInfoItem = Arrays.asList("id", "taskNm","taskSttsCd","taskPrgDtHm","taskPrgDt","taskCpDt","targetCnt","submitCnt");
        List<String> stntListItem = Arrays.asList("no","id", "triId", "mamoymId", "submAt", "submDt", "num", "flnm", "actvtnAt");

        resultMap = AidtCommonUtil.filterToMap(taskInfoItem, tchHomewkMapper.findTaskSubmStatus_ti(paramData));
        List<LinkedHashMap<Object, Object>> stntListList = AidtCommonUtil.filterToList(stntListItem, tchHomewkMapper.findTaskSubmStatus_tri(paramData));

        resultMap.put("stntListItem", stntListList);

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findTchHomewkStntList(Map<String, Object> paramData) throws Exception {
        return tchHomewkMapper.findTchHomewkStntList(paramData);
    }

    public Object modifyHomewkEnd(Map<String, Object> paramData) throws Exception {
        List<String> homewkInfoItem = Arrays.asList("id", "taskNm", "taskPrgDt", "taskCpDt", "taskSttsCd", "taskSttsNm","rptAutoOthbcAt");

        // 타임아웃여부(timeoutAt) 파라미터 디폴트값 셋팅
        String timeoutAt = MapUtils.getString(paramData, "timeoutAt");
        paramData.put("timeoutAt", StringUtils.isEmpty(timeoutAt) ? "N" : timeoutAt);


        // 과제 마스터 정보 상태 수정
        int result1 =  tchHomewkMapper.modifyTchHomewkEnd(paramData);
        log.info("result1:{}", result1);

        LinkedHashMap<Object, Object> homewkInfoMap = AidtCommonUtil.filterToMap(homewkInfoItem, tchHomewkMapper.findTchHomewkEndHomewkInfo(paramData));

        // 리포트 자동 공유 여부가 Y 인 경우. 리포트 공유 호출
        String strRptAutoOthbcAt = MapUtils.getString(homewkInfoMap, "rptAutoOthbcAt", "N");
        if ("Y".equals(strRptAutoOthbcAt)) {
            tchReportHomewkService.modifyReportTaskOpen(paramData);
        }

        homewkInfoMap.put("taskPrgDt", AidtCommonUtil.stringToDateFormat((String) homewkInfoMap.get("taskPrgDt"),"yyyy-MM-dd HH:mm:ss"));
        homewkInfoMap.put("taskCpDt", AidtCommonUtil.stringToDateFormat((String) homewkInfoMap.get("taskCpDt"),"yyyy-MM-dd HH:mm:ss"));

        homewkInfoMap.remove("rptAutoOthbcAt");
        return homewkInfoMap;
    }
}
