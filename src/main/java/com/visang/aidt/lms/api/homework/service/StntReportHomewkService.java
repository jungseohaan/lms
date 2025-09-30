package com.visang.aidt.lms.api.homework.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.homework.mapper.StntReportHomewkMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@SuppressWarnings("rawtypes")
@Slf4j
@Service
@AllArgsConstructor
public class StntReportHomewkService {
    private final StntReportHomewkMapper stntReportHomewkMapper;

    @Transactional(readOnly = true)
    public Object findStntReportHomewkList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> taskInfoItem = Arrays.asList(
            "no", "id", "eamMth", "eamMthNm", "taskNm", "taskSttsCd",
            "taskSttsNm", "taskPrgDt", "taskCpDt", "submAt", "anwNum",
            "wrngNum", "triNum", "taskResultAnct", "taskResultAnctNm", "gradeSttsNm", "allQstnMrkTyCd", "allManualMrkAt"
        );

        List<Map> taskList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = stntReportHomewkMapper.findStntReportHomewkList(pagingParam);
        if(!CollectionUtils.isEmpty(entityList)) {
            boolean isFirst = true;
            Gson gson = new Gson();

            for(Map entity : entityList) {
                if(isFirst) {
                    total = (Long) entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(taskInfoItem, entity);
                if(!ObjectUtils.isEmpty(entity.get("extraInfo"))) {
                    String extraInfo = MapUtils.getString(entity, "extraInfo", "");
                    if (StringUtils.isNotEmpty(extraInfo)) {
                        tmap.putAll(gson.fromJson(extraInfo, Map.class));
                    }
                }
                taskList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(taskList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("taskList",taskList);
        returnMap.put("page",page);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntReportHomewkDetail(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("id", "taskNm", "errataInfo", "modNum", "setsId", "taskInfoList");
        List<String> errataInfoItem = Arrays.asList("anwNum", "triNum", "wrngNum");
        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> myAnalysisInfoItem = Arrays.asList("taskIemId", "subId", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "subMitAnw");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");
        List<String> mdulItemInfoItem = Arrays.asList("taskIemId", "subId", "submAt", "thumbnail", "mdulImageList", "mdulInfo","myAnalysisInfo", "commentary");
        List<String> mdulTaskInfoItem = Arrays.asList("id", "taskResultId", "taskIemId", "subId","mrkTy", "eakAt", "errata", "submAt", "subMitAnw", "subMitAnwUrl","rubric","fdbDc", "solvTime", "peerReview","selfEvl", "articleType", "articleTypeNm");

        // 정오답 정보
        List<LinkedHashMap<Object, Object>> errataInfo = AidtCommonUtil.filterToList(errataInfoItem, stntReportHomewkMapper.findStntReportHomewkDetail_errata(paramData));
        // 모듈이미지정보
        List<Map> mdulImageLists = stntReportHomewkMapper.findStntReportHomewkDetail_image(paramData);
        // 모듈(콘텐츠)정보
        List<Map> mdulInfoList = stntReportHomewkMapper.findStntReportHomewkDetail_mdul(paramData);
        // 내 분석
        List<Map> myAnalysisInfoList = stntReportHomewkMapper.findStntReportHomewkDetail_analysis(paramData);
        // 해설
        List<Map> commentaryList = stntReportHomewkMapper.findStntReportHomewkDetail_coment(paramData);
        // 모듈아이템정보
        List<LinkedHashMap<Object, Object>> mdulItemInfoList = AidtCommonUtil.filterToList(mdulItemInfoItem, stntReportHomewkMapper.findStntReportHomewkDetail_item(paramData));
        mdulItemInfoList.forEach(s -> {
            // 모듈이미지정보
            List<LinkedHashMap<Object, Object>> mdulImageList = CollectionUtils.emptyIfNull(mdulImageLists).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(mdulImageItem, r))
                .toList();
            s.put("mdulImageList", mdulImageList);

            // 모듈(콘텐츠)정보
            LinkedHashMap<Object, Object> mdulInfo = CollectionUtils.emptyIfNull(mdulInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(mdulInfoItem, r))
                .findFirst().orElse(null);
            s.put("mdulInfo", mdulInfo);

            // 내 분석
            LinkedHashMap<Object, Object> myAnalysisInfo = CollectionUtils.emptyIfNull(myAnalysisInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(myAnalysisInfoItem, r))
                .findFirst().orElse(null);
            s.put("myAnalysisInfo", myAnalysisInfo);

            // 해설
            LinkedHashMap<Object, Object> commentary = CollectionUtils.emptyIfNull(commentaryList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .map(r -> AidtCommonUtil.filterToMap(commentaryItem, r))
                .findFirst().orElse(null);
            s.put("commentary", commentary);
        });

        // 모듈과제정보
        List<LinkedHashMap<Object, Object>> mdulTaskInfoList = AidtCommonUtil.filterToList(mdulTaskInfoItem, stntReportHomewkMapper.findStntReportHomewkDetail_task(paramData));
        CollectionUtils.emptyIfNull(mdulTaskInfoList)
                .stream()
                .forEach(mdulTaskResult -> {
                    if (!ObjectUtils.isEmpty(mdulTaskResult.get("rubric"))) {
                        String rubricJsonString = (String) mdulTaskResult.get("rubric");

                        JsonObject jsonObject = JsonParser.parseString(rubricJsonString).getAsJsonObject();
                        Map<String, Object> rubricMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                        mdulTaskResult.put("rubric", rubricMap);
                    } else {
                        mdulTaskResult.put("rubric", new HashMap<>());
                    }
                });

        List<LinkedHashMap<Object, Object>> taskInfoList = new ArrayList<>();
        mdulTaskInfoList.forEach( s -> {
            LinkedHashMap<Object, Object> taskInfo = new LinkedHashMap<>();
            LinkedHashMap<Object, Object> mdulItemInfo = CollectionUtils.emptyIfNull(mdulItemInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(t,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId")) )
                .findFirst().orElse(null);
            taskInfo.put("mdulItemInfo", mdulItemInfo);
            taskInfo.put("mdulTaskInfo", s);
            taskInfoList.add(taskInfo);
        });

        // Response
        var resultMap = AidtCommonUtil.filterToMap(respItem, stntReportHomewkMapper.findStntReportHomewkDetail_info(paramData));
        resultMap.put("errataInfo", errataInfo);
        resultMap.put("taskInfoList", taskInfoList);
        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findStntReportHomewkSummary(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList(
            "id", "taskNm", "setsId", "classNm", "resultTypeNm", "taskPrgDt",
            "taskCpDt", "submAt", "submDt", "timTime", "eamExmNum",
            "durationAvr", "taskResultAnct", "taskResultAnctNm", "taskItemResultList"
        );
        List<String> taskItemResultItem = Arrays.asList(
            "num", "taskIemId", "subId", "taskResultId", "questionType", "submAt",
            "solvDuration", "subMitAnw", "subMitAnwUrl", "errata"
        );

        // 학생과제모듈정보
        List<LinkedHashMap<Object, Object>> taskItemResultList = AidtCommonUtil.filterToList(taskItemResultItem, stntReportHomewkMapper.findStntReportHomewkSummary_mdul(paramData));

        // 과제정보
        LinkedHashMap<Object, Object> respMap = AidtCommonUtil.filterToMap(respItem, stntReportHomewkMapper.findStntReportHomewkSummary(paramData));
        respMap.put("taskItemResultList",taskItemResultList);

        // Response
        return respMap;
    }
}
