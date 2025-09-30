package com.visang.aidt.lms.api.homework.service;

import com.visang.aidt.lms.api.homework.mapper.TchReportHomewkEngMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * packageName : com.visang.aidt.lms.api.homework.service
 * fileName : TchReportHomewkEngService
 * USER : 조승현
 * date : 2024-04-04
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-04-04         조승현          최초 생성
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@Service
@AllArgsConstructor
public class TchReportHomewkEngService {
    private final TchReportHomewkEngMapper tchReportHomewkEngMapper;

    @Transactional(readOnly = true)
    public Object findStntSrchReportTaskDetail(Map<String, Object> paramData) throws Exception {
        List<String> respItem = Arrays.asList("id", "taskNm", "errataInfo", "modNum", "taskInfoList");
        List<String> errataInfoItem = Arrays.asList("anwNum", "triNum", "wrngNum");
        List<String> mdulItemInfoItem = Arrays.asList("taskIemId", "submAt", "thumbnail", "mdulImageList", "mdulInfo", "myAnalysisInfo", "commentary");
        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> myAnalysisInfoItem = Arrays.asList("taskIemId", "correctRate", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "answerRateStr");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");
        List<String> mdulEvlInfoItem = Arrays.asList("id", "taskResultId", "taskIemId", "errata", "submAt", "subMitAnw", "subMitAnwUrl", "rubric", "fdbDc", "peerReview", "selfEvl");

        // 정오답정보
        LinkedHashMap<Object, Object> errataInfo = AidtCommonUtil.filterToMap(errataInfoItem, tchReportHomewkEngMapper.findStntSrchReportTaskDetail_errata(paramData));
        // 모듈이미지정보
        List<Map> mdulImageLists = tchReportHomewkEngMapper.findStntSrchReportTaskDetail_image(paramData);
        // 모듈(콘텐츠)정보
        List<Map> mdulInfoLists = tchReportHomewkEngMapper.findStntSrchReportTaskDetail_mdul(paramData);
        // 내 분석
        List<Map> myAnalysisInfoLists = tchReportHomewkEngMapper.findStntSrchReportTaskDetail_analysis(paramData);
        // 해설
        List<Map> commentaryList = tchReportHomewkEngMapper.findStntSrchReportTaskDetail_coment(paramData);
        // 모듈아이템정보
        List<LinkedHashMap<Object, Object>> mdulItemInfoLists = CollectionUtils.emptyIfNull(
            tchReportHomewkEngMapper.findStntSrchReportTaskDetailMdul_info(paramData)
        ).stream().map(s -> {
            // 모듈이미지정보
            List<LinkedHashMap<Object, Object>> mdulImageList = CollectionUtils.emptyIfNull(mdulImageLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                .map(r -> {
                    return AidtCommonUtil.filterToMap(mdulImageItem, r);
                }).toList();
            s.put("mdulImageList", mdulImageList);

            // 모듈(콘텐츠)정보
            LinkedHashMap<Object, Object> mdulInfo = CollectionUtils.emptyIfNull(mdulInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                .map(r -> {
                    return AidtCommonUtil.filterToMap(mdulInfoItem, r);
                }).findFirst().orElse(null);
            s.put("mdulInfo", mdulInfo);

            // 우리반 분석
            LinkedHashMap<Object, Object> myAnalysisInfo = CollectionUtils.emptyIfNull(myAnalysisInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                .map(r -> {
                    return AidtCommonUtil.filterToMap(myAnalysisInfoItem, r);
                }).findFirst().orElse(null);
            s.put("myAnalysisInfo", myAnalysisInfo);

            // 해설
            LinkedHashMap<Object, Object> commentary = CollectionUtils.emptyIfNull(commentaryList).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                .map(r -> {
                    return AidtCommonUtil.filterToMap(commentaryItem, r);
                }).findFirst().orElse(null);
            s.put("commentary", commentary);

            return AidtCommonUtil.filterToMap(mdulItemInfoItem, s);
        }).toList();

        // 모듈평가정보
        List<Map> mdulTaskInfoLists = tchReportHomewkEngMapper.findStntSrchReportTaskDetailMdul_task(paramData);

        // 과제정보
        List<LinkedHashMap<Object, Object>> taskInfoList = new ArrayList<>();
        mdulItemInfoLists.forEach(s -> {
            // 모듈평가정보
            LinkedHashMap<Object, Object> mdulTaskInfo = CollectionUtils.emptyIfNull(mdulTaskInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"taskIemId"), MapUtils.getString(r,"taskIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")))
                .map(r -> {
                    return AidtCommonUtil.filterToMap(mdulEvlInfoItem, r);
                }).findFirst().orElse(null);

            var taskInfo = new LinkedHashMap<Object, Object>();
            taskInfo.put("mdulItemInfo", s);
            taskInfo.put("mdulTaskInfo", mdulTaskInfo);
            taskInfoList.add(taskInfo);
        });

        // Response
        var resp = AidtCommonUtil.filterToMap(respItem, tchReportHomewkEngMapper.findStntSrchReportTaskDetail(paramData));
        resp.put("errataInfo", errataInfo);
        resp.put("taskInfoList", taskInfoList);
        return resp;
    }

}
