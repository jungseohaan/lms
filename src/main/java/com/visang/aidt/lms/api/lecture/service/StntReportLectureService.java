package com.visang.aidt.lms.api.lecture.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.assessment.mapper.TchReportMapper;
import com.visang.aidt.lms.api.lecture.mapper.StntReportLectureMapper;
import com.visang.aidt.lms.api.lecture.mapper.TchLectureReportMapper;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@AllArgsConstructor
public class StntReportLectureService {
    private final TchLectureReportMapper tchLectureReportMapper;
    private final StntReportLectureMapper stntReportLectureMapper;
    private final TchReportMapper tchReportMapper;

    @Transactional(readOnly = true)
    public Object findStntReportLectureDetail(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("id", "stdDtaNm", "crcuNm", "modNum", "tabInfoList", "errataInfo", "dtaInfoList");
        List<String> tabInfoItem = Arrays.asList("tabId", "tabNm", "tabSeq", "categoryCd", "categoryNm", "setsId", "aiCstmzdStdMthdSeCd", "eamTrget"); // tab 정보

        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> myAnalysisInfoItem = Arrays.asList("dtaIemId", "subId", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "subMitAnw");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");
        List<String> mdulItemInfoItem = Arrays.asList("setsId", "dtaIemId", "subId", "mrkTy", "submAt", "thumbnail", "mdulImage", "mdulInfo","myAnalysisInfo", "commentary", "articleType", "articleTypeNm");

        List<String> errataInfoItem = Arrays.asList("anwNum", "triNum", "wrngNum");

        List<String> stntDtaInfoItem = Arrays.asList("userId", "flnm", "setsId", "dtaIemId", "subId", "mrkTy", "stntDtaResultList");
        List<String> stntDtaResultItem = Arrays.asList("id", "dtaResultId", "dtaIemId", "subId", "errata", "submAt", "subMitAnw", "subMitAnwUrl", "articleType", "questionType", "exltAnwAt", "reExmCnt", "mrkTy", "eakAt", "rubric","fdbDc", "fdbExpAt", "actList", "peerReview", "selfEvl", "histYn", "delYn", "selfEvlAt", "slfPerEvlAt");
        List<String> actInfoItem = Arrays.asList("id", "actIemId", "subId", "actWy", "actWyNm", "actSttsCd", "actSttsNm", "actStDt", "actEdDt", "actResult");
        List<String> actResultInfoItem = Arrays.asList("id", "actId", "thumbnail", "actSubmitUrl", "delYn", "actSubmitDc", "actStDt", "actEdDt", "fdbDc", "fdbUrl");

        // 교사 탭 정보
        List<LinkedHashMap<Object, Object>> tabInfoList = AidtCommonUtil.filterToList(tabInfoItem, stntReportLectureMapper.findStntReportLectureDetail_tab(paramData));
        if(tabInfoList.isEmpty()) {
            log.info("Not Found Tab information.");
            return null;
        }
        // 선택된 탭ID가 없는 경우 디폴트 탭ID 설정
        String tabId = (String) paramData.get("tabId");
        if(StringUtils.isEmpty(tabId)) {
            paramData.put("tabId", tabInfoList.get(0).get("tabId"));
        }

        /* 출제대상 (0,1:공통문항출제, 2:개별문항출제) */
        AtomicLong eamTrget = new AtomicLong(0);
        final String selTabId = MapUtils.getString(paramData, "tabId");
        tabInfoList.forEach(t -> {
            String vTabId = MapUtils.getString(t, "tabId");
            if(selTabId.equals(vTabId)) {
                eamTrget.set(MapUtils.getLongValue(t,"eamTrget"));
            }
        });

        paramData.put("eamTrget", eamTrget.get());

        /*--------------------------------------------------------------------------------------------*/
        // 모듈 아이템 정보
        /*--------------------------------------------------------------------------------------------*/
        // 모듈이미지정보
        List<Map> mdulImageList = stntReportLectureMapper.findStntReportLectureDetail_image(paramData);
        // 모듈(콘텐츠)정보
        List<Map> mdulInfoList = stntReportLectureMapper.findStntReportLectureDetail_mdul(paramData);
        // 내 분석
        List<Map> myAnalysisInfoList = stntReportLectureMapper.findStntReportLectureDetail_analysis(paramData);
        // 해설
        List<Map> commentaryList = stntReportLectureMapper.findStntReportLectureDetail_coment(paramData);
        // 모듈아이템정보
        List<LinkedHashMap<Object, Object>> mdulItemInfoList = AidtCommonUtil.filterToList(mdulItemInfoItem, stntReportLectureMapper.findStntReportLectureDetail_item(paramData));
        mdulItemInfoList.forEach(s -> {
            // 모듈이미지정보
            LinkedHashMap<Object, Object> mdulImage = CollectionUtils.emptyIfNull(mdulImageList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                .map(r -> AidtCommonUtil.filterToMap(mdulImageItem, r))
                .findFirst().orElse(null);
            s.put("mdulImage", mdulImage);

            // 모듈(콘텐츠)정보
            LinkedHashMap<Object, Object> mdulInfo = CollectionUtils.emptyIfNull(mdulInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                .map(r -> AidtCommonUtil.filterToMap(mdulInfoItem, r))
                .findFirst().orElse(null);
            s.put("mdulInfo", mdulInfo);

            // 내 분석
            LinkedHashMap<Object, Object> myAnalysisInfo = CollectionUtils.emptyIfNull(myAnalysisInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                .map(r -> AidtCommonUtil.filterToMap(myAnalysisInfoItem, r))
                .findFirst().orElse(null);
            s.put("myAnalysisInfo", myAnalysisInfo);

            // 해설
            LinkedHashMap<Object, Object> commentary = CollectionUtils.emptyIfNull(commentaryList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                .map(r -> AidtCommonUtil.filterToMap(commentaryItem, r))
                .findFirst().orElse(null);
            s.put("commentary", commentary);
        });

        /*--------------------------------------------------------------------------------------------*/
        // 정오답 정보
        /*--------------------------------------------------------------------------------------------*/
        LinkedHashMap<Object, Object> errataInfo = AidtCommonUtil.filterToMap(errataInfoItem, stntReportLectureMapper.findStntReportLectureDetail_errata(paramData));

        /*--------------------------------------------------------------------------------------------*/
        // 학생 학습자료 정보
        /*--------------------------------------------------------------------------------------------*/
        // 학생 학습자료 결과정보
        List<Map> stntDtaResultLists = stntReportLectureMapper.findStntReportLectureDetail_stntDtaResult(paramData);
        // 활동하기
        List<Map> actLists = stntReportLectureMapper.findStntReportLectureDetail_act(paramData);
        // 활동결과정보
        List<Map> actResultList = stntReportLectureMapper.findStntReportLectureDetail_actResult(paramData);

        // 학생 학습자료 다른문제 풀기
        List<Integer> sdrdIdList = new ArrayList<>(); // std_dta_result_detail.id 리스트
        sdrdIdList.add(-1); // id가 없을때 오류 방지
        CollectionUtils.emptyIfNull(stntDtaResultLists).forEach(s -> {
            if ("N".equals(MapUtils.getString(s, "histYn"))) {
                sdrdIdList.add(MapUtils.getInteger(s, "id"));
            }
        });
        Map<String, Object> innerParam = new HashMap<>();
        innerParam.put("ids", sdrdIdList);
        List<Map> stntDtaResultOtherLists = stntReportLectureMapper.findStntReportLectureDetail_stntDtaResultOther(innerParam);


        // 학생 학습자료 정보
        List<LinkedHashMap<Object, Object>> stntDtaInfoList = AidtCommonUtil.filterToList(stntDtaInfoItem, stntReportLectureMapper.findStntReportLectureDetail_stntDta(paramData));
        stntDtaInfoList.forEach(s -> {
            // 학생 학습자료 결과정보
            List<LinkedHashMap<Object, Object>> stntDtaResultList = CollectionUtils.emptyIfNull(stntDtaResultLists)
                    .stream()
                    .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                    .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                    .map(r -> {
                        if (!ObjectUtils.isEmpty(r.get("rubric"))) {
                            String rubricJsonString = (String) r.get("rubric");

                            JsonObject jsonObject = JsonParser.parseString(rubricJsonString).getAsJsonObject();
                            Map<String, Object> rubricMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                            r.put("rubric", rubricMap);
                        } else {
                            r.put("rubric", new HashMap<>());
                        }
                        return r;
                    })
                    .map(r -> AidtCommonUtil.filterToMap(stntDtaResultItem, r))
                    .toList();
            s.put("stntDtaResultList", stntDtaResultList);

            // 학생 학습자료 다른문제 풀기
            List<String> ids = new ArrayList<>();
            stntDtaResultList.forEach(x -> {
                if (StringUtils.equals(MapUtils.getString(x, "histYn"), "N")) { // 히스토리가 아닌것
                    ids.add(MapUtils.getString(x, "id"));
                }
            });

            List<Map> stntDtaResultOtherList = CollectionUtils.emptyIfNull(stntDtaResultOtherLists)
                    .stream()
                    .filter(t -> ids.contains(MapUtils.getString(t, "srcDetailId")))
                    .toList();

            s.put("stntOtherResultList", stntDtaResultOtherList);

            // 활동하기
            CollectionUtils.emptyIfNull(actLists).forEach(act -> {
                // 활동결과정보
                LinkedHashMap<Object, Object> actResult = CollectionUtils.emptyIfNull(actResultList).stream()
                    .filter(t -> StringUtils.equals(MapUtils.getString(act,"id"), MapUtils.getString(t, "actId")))
                    .map(r -> AidtCommonUtil.filterToMap(actResultInfoItem, r))
                    .findFirst().orElse(null);
                act.put("actResult", actResult);
            });
            List<LinkedHashMap<Object, Object>> actList = CollectionUtils.emptyIfNull(actLists).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                .map(r -> AidtCommonUtil.filterToMap(actInfoItem, r))
                .toList();
            s.put("actList", actList);
        });

        // 모듈 학습자료 정보
        List<LinkedHashMap<Object, Object>> dtaInfoList = new ArrayList<>();
        CollectionUtils.emptyIfNull(
            stntReportLectureMapper.findStntReportLectureDetail_tabMdul(paramData)
        ).forEach(s -> {
            LinkedHashMap<Object, Object> mdulItemInfo = CollectionUtils.emptyIfNull(mdulItemInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                .findFirst().orElse(null);
            LinkedHashMap<Object, Object> stntDtaInfo = CollectionUtils.emptyIfNull(stntDtaInfoList).stream()
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "dtaIemId"), MapUtils.getString(t, "dtaIemId")))
                .filter(t -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(t, "subId")))
                .findFirst().orElse(null);
            LinkedHashMap<Object, Object> dtaInfo = new LinkedHashMap<>();
            dtaInfo.put("mdulItemInfo", mdulItemInfo);
            dtaInfo.put("stntDtaInfo", stntDtaInfo);
            dtaInfoList.add(dtaInfo);
        });

        // Response
        var respMap = AidtCommonUtil.filterToMap(respItem, stntReportLectureMapper.findStntReportLectureDetail_info(paramData));

        String tabName = stntReportLectureMapper.findStntReportLectureTabName(paramData);

        // 평균 정답률 (교사/학생)
        Map<String, Object> stdCrrctInfo = new HashMap<>();
        if (tabName != null && tabName.contains("AI 맞춤 학습")) {
            // AI 맞춤 학습이 포함된 경우의 쿼리
            stdCrrctInfo = tchReportMapper.findStdReportForCrrctInfoWithAI(paramData);
        } else {
            // AI 맞춤 학습이 포함되지 않은 경우의 기존 쿼리
            stdCrrctInfo = tchReportMapper.findStdReportForCrrctInfo(paramData);
        }
        //respMap.put("totCnt"          ,MapUtils.getString(stdCrrctInfo, "totCnt",""));
        //respMap.put("crrctCnt"        ,MapUtils.getString(stdCrrctInfo, "crrctCnt",""));
        respMap.put("crrctRate"       ,MapUtils.getString(stdCrrctInfo, "crrctRate",""));

        respMap.put("tabInfoList", tabInfoList);
        respMap.put("errataInfo", errataInfo);
        respMap.put("dtaInfoList", dtaInfoList);

        Map<String, Object> crcuNmMap = new HashMap<>();
        String crcuNm = null;

        crcuNmMap = tchLectureReportMapper.tcCurriculumTextTabId(paramData);
        if (!ObjectUtils.isEmpty(crcuNmMap)){
            crcuNm = MapUtils.getString(crcuNmMap, "idPathNm", null);
            respMap.put("crcuNm", crcuNm);
        }

        return respMap;
    }
}
