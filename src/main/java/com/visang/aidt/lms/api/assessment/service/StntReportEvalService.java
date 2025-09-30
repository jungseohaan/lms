package com.visang.aidt.lms.api.assessment.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.assessment.mapper.StntReportEvalMapper;
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

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@Service
@AllArgsConstructor
public class StntReportEvalService {
    private final StntReportEvalMapper stntReportEvalMapper;

    /**
     * 평가 리포트 공개여부 조회
     * @param paramData 입력 파라메터
     * @return String
     */
    @Transactional(readOnly = true)
    public String findReportEvalPublicYn(Map<String, Object> paramData) throws Exception {

        return stntReportEvalMapper.findReportEvalPublicYn(paramData);
    }

    /**
     * (학생) 평가 리포트 목록조회
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findStntReportEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> evlListItem = Arrays.asList(
            "no", "id", "eamMth", "eamMthNm", "evlNm", "evlSttsCd",
            "evlSttsNm", "evlPrgDt", "evlCpDt", "eakSttsCd", "eakSttsNm",
            "submAt", "evlResultScr", "evlStdr", "allQstnMrkTyCd", "allManualMrkAt"
        );

        List<Map> evalList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = stntReportEvalMapper.findStntReportEvalList(pagingParam);
        if(!entityList.isEmpty()) {
            boolean isFirst = true;
            for (Map entity : entityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                String evlStdrSetAt = MapUtils.getString(entity, "evlStdrSetAt");  // 평가기준 설정여부
                Integer evlStdrSet = MapUtils.getInteger(entity, "evlStdrSet");    // 평가기준 (1: 상/중/하, 2: 통과/실패, 3: 점수)
                Double evlIemScrTotal = MapUtils.getDouble(entity, "evlIemScrTotal");     // 아티클의 총 배점
                Double stntTotScr = MapUtils.getDouble(entity, "evlResultScr");     // 학생의 총점
                Integer evlGdStdrScr = MapUtils.getInteger(entity, "evlGdStdrScr"); // 상 기준점수
                Integer evlAvStdrScr = MapUtils.getInteger(entity, "evlAvStdrScr"); // 중 기준정부
                Integer evlPsStdrScr = MapUtils.getInteger(entity, "evlPsStdrScr"); // 통과 기준점수
                String submAt = MapUtils.getString(entity, "submAt");              // 제출여부
                String evlStdr = AidtCommonUtil.getEvlResultGradeNmNew(evlStdrSetAt, evlStdrSet, evlIemScrTotal, stntTotScr, evlGdStdrScr ,evlAvStdrScr ,evlPsStdrScr, submAt);

                var tmap = AidtCommonUtil.filterToMap(evlListItem, entity);
                tmap.put("evlPrgDt", AidtCommonUtil.stringToDateFormat(MapUtils.getString(tmap,"evlPrgDt"),"yyyy-MM-dd HH:mm:ss"));
                tmap.put("evlCpDt", AidtCommonUtil.stringToDateFormat(MapUtils.getString(tmap,"evlCpDt"),"yyyy-MM-dd HH:mm:ss"));
                tmap.put("evlStdr", evlStdr);
                evalList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(evalList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("evalList",evalList);
        returnMap.put("page",page);
        return returnMap;
    }

    /**
     * (학생) 평가 결과 조회(자세히보기)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findStntReportEvalResultDetail(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("id", "evlNm", "setsId", "mdulTotScr", "stntTotScr", "errataInfo", "evlStdr", "evlInfoList","allQstnMrkTyCd","allManualMrkAt");
        List<String> errataInfoItem = Arrays.asList("anwNum", "triNum", "wrngNum");
        List<String> mdulItemInfoItem = Arrays.asList("evlIemId", "subId", "evlIemScr", "targetCnt", "submitCnt", "thumbnail", "mdulImageList", "mdulInfo", "myAnalysisInfo", "commentary");
        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> myAnalysisInfoItem = Arrays.asList("evlIemId", "subId", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "subMitAnw");
        List<String> mdulEvlInfoItem = Arrays.asList("id", "evlResultId", "evlIemId", "subId", "eakAt","eakSttsCd","eakSttsNm","errata", "evlIemScrResult", "solvSecAvr", "submAt", "subMitAnw", "subMitAnwUrl", "rubric", "fdbInfo", "peerReview", "selfEvl", "mrkTy", "articleType");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");

        // 정오답정보
        LinkedHashMap<Object, Object> errataInfo = AidtCommonUtil.filterToMap(errataInfoItem, stntReportEvalMapper.findStntReportEvalResultDetail_errata(paramData));
        // 모듈이미지정보
        List<Map> mdulImageLists = stntReportEvalMapper.findStntReportEvalResultDetail_image(paramData);
        // 모듈(콘텐츠)정보
        List<Map> mdulInfoList = stntReportEvalMapper.findStntReportEvalResultDetail_mdul(paramData);
        // 내 분석
        List<Map> myAnalysisInfoList = stntReportEvalMapper.findStntReportEvalResultDetail_analysis(paramData);
        // 해결
        List<Map> commentaryList = stntReportEvalMapper.findStntReportEvalResultDetail_coment(paramData);
        // 모듈아이템정보
        List<LinkedHashMap<Object, Object>> mdulItemInfoLists = CollectionUtils.emptyIfNull(
                stntReportEvalMapper.findStntReportEvalResultDetailMdul_info(paramData)
            ).stream().map(s -> {
                // 모듈이미지정보
                List<LinkedHashMap<Object, Object>> mdulImageList = CollectionUtils.emptyIfNull(mdulImageLists).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId"))  )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(mdulImageItem, r);
                    }).toList();
                s.put("mdulImageList", mdulImageList);

                // 모듈(콘텐츠)정보
                LinkedHashMap<Object, Object> mdulInfo = CollectionUtils.emptyIfNull(mdulInfoList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId"))  )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(mdulInfoItem, r);
                    }).findFirst().orElse(null);
                s.put("mdulInfo", mdulInfo);

                // 내 분석
                LinkedHashMap<Object, Object> myAnalysisInfo = CollectionUtils.emptyIfNull(myAnalysisInfoList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId"))  )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(myAnalysisInfoItem, r);
                    }).findFirst().orElse(null);
                s.put("myAnalysisInfo", myAnalysisInfo);

                // 해설
                LinkedHashMap<Object, Object> commentary = CollectionUtils.emptyIfNull(commentaryList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId"))  )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(commentaryItem, r);
                    }).findFirst().orElse(null);
                s.put("commentary", commentary);

                return AidtCommonUtil.filterToMap(mdulItemInfoItem, s);
            }).toList();

        // 모듈평가정보
        List<Map> mdulEvalInfoLists = stntReportEvalMapper.findStntReportEvalResultDetailMdul_eval(paramData);
        CollectionUtils.emptyIfNull(mdulEvalInfoLists)
                .stream()
                .forEach(mdulEvlResult -> {
                    if (!ObjectUtils.isEmpty(mdulEvlResult.get("rubric"))) {
                        String rubricJsonString = (String) mdulEvlResult.get("rubric");

                        JsonObject jsonObject = JsonParser.parseString(rubricJsonString).getAsJsonObject();
                        Map<String, Object> rubricMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                        mdulEvlResult.put("rubric", rubricMap);
                    } else {
                        mdulEvlResult.put("rubric", new HashMap<>());
                    }
                });

        // 평가정보
        List<LinkedHashMap<Object, Object>> evlInfoList = new ArrayList<>();
        Gson gson = new Gson();
        mdulItemInfoLists.forEach(s -> {
            // 모듈평가정보
            LinkedHashMap<Object, Object> mdulEvlInfo = CollectionUtils.emptyIfNull(mdulEvalInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId"))  )
                .map(r -> {
                    r.put("fdbInfo",gson.fromJson((String) r.get("fdbInfo"), Map.class));
                    return AidtCommonUtil.filterToMap(mdulEvlInfoItem, r);
                }).findFirst().orElse(null);

            var evlInfo = new LinkedHashMap<Object, Object>();
            evlInfo.put("mdulItemInfo", s);
            evlInfo.put("mdulEvlInfo", mdulEvlInfo);
            evlInfoList.add(evlInfo);
        });

        String evlStdr = "";
        Map<String, Object> respMap = stntReportEvalMapper.findStntReportEvalResultDetail(paramData);
        if(respMap != null) {
            String evlStdrSetAt = MapUtils.getString(respMap, "evlStdrSetAt");  // 평가기준 설정여부
            Integer evlStdrSet = MapUtils.getInteger(respMap, "evlStdrSet");    // 평가기준 (1: 상/중/하, 2: 통과/실패, 3: 점수)
            Double evlIemScrTotal = MapUtils.getDouble(respMap, "evlIemScrTotal");       // 만점 배점
            Double stntTotScr = MapUtils.getDouble(respMap, "stntTotScr");       // 학생의 총점
            Integer evlGdStdrScr = MapUtils.getInteger(respMap, "evlGdStdrScr"); // 상 기준점수
            Integer evlAvStdrScr = MapUtils.getInteger(respMap, "evlAvStdrScr"); // 중 기준정부
            Integer evlPsStdrScr = MapUtils.getInteger(respMap, "evlPsStdrScr"); // 통과 기준점수
            String submAt = (String) respMap.get("submAt");              // 제출여부
            evlStdr = AidtCommonUtil.getEvlResultGradeNmNew(evlStdrSetAt,evlStdrSet ,evlIemScrTotal, stntTotScr ,evlGdStdrScr ,evlAvStdrScr ,evlPsStdrScr, submAt);
        }

        // Response
        var resp = AidtCommonUtil.filterToMap(respItem, respMap);
        resp.put("errataInfo", errataInfo);
        resp.put("evlStdr", evlStdr);
        resp.put("evlInfoList", evlInfoList);
        return resp;
    }

    @Transactional(readOnly = true)
    public Object findStntReportEvalResultHeader(Map<String, Object> paramData) throws Exception {

        Map<String, Object> resultMap = (Map<String, Object>) stntReportEvalMapper.findStntReportEvalResultHeader(paramData);
        if(resultMap == null) {return new HashMap<String, Object>();}

        String evlStdr = AidtCommonUtil.getEvlResultGradeNmNew(
                MapUtils.getString(resultMap, "evlStdrSetAt"),
                MapUtils.getInteger(resultMap, "evlStdrSet"),
                MapUtils.getDouble(resultMap, "evlIemScrTotal"),
                MapUtils.getDouble(resultMap, "evlResultScr"),
                MapUtils.getInteger(resultMap, "evlGdStdrScr"),
                MapUtils.getInteger(resultMap, "evlAvStdrScr"),
                MapUtils.getInteger(resultMap, "evlPsStdrScr"),
                MapUtils.getString(resultMap, "submAt")
        );

        resultMap.put("evlStdr", evlStdr);

        return resultMap;
    }

    /**
     * 학생 평가결과조회(결과보기)
     *
     * @param paramData 입력 파라메터
     * @param pageable
     * @return Object
     */
    @Transactional(readOnly = true)
    public Object findStntSrchReportEvalResultSummary(Map<String, Object> paramData, Pageable pageable) throws Exception {

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> list = (List<Map>) stntReportEvalMapper.findStntSrchReportEvalResultSummary(pagingParam);

        if (list!=null && !list.isEmpty()) {
            total = (Long)list.get(0).get("fullCount");
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(list, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("evlItemResultList",list);
        returnMap.put("page",page);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSrchReportEvalResultInsite(Map<String, Object> paramData, Pageable pageable) throws Exception {

        // Response Parameters
        // 본문
        List<String> item = Arrays.asList(
            "evlId", "setsId", "evlIemId", "subId", "thumbnail", "url",
            "image", "description"
        );

        // MdulInfo
        List<String> mdulInfo = Arrays.asList(
            "curriYear", "curriSchool", "curriSubject", "curriGrade"
        );

        // 내 분석
        List<String> myAnalysisInfo = Arrays.asList(
            "evlIemId", "subId", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "subMitAnw"
        );

        // Commentary
        List<String> commentary = Arrays.asList(
            "hint", "modelAnswer", "explanation"
        );



        long total = 0;
        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> tmpList = (List<Map>) stntReportEvalMapper.findStntSrchReportEvalResultInsite(pagingParam);

        List<Map> resultList = new ArrayList<>();
        if (!tmpList.isEmpty()) {
            total = (long) tmpList.get(0).get("fullCount");

            for (Map map:tmpList) {
                var tmap = AidtCommonUtil.filterToMap(item, map);

                var mdulInfoMap = AidtCommonUtil.filterToMap(mdulInfo, map);
                var myAnalysisInfoMap = AidtCommonUtil.filterToMap(myAnalysisInfo, map);
                var commentaryMap = AidtCommonUtil.filterToMap(commentary, map);

                tmap.put("mdulInfo", mdulInfoMap);
                tmap.put("myAnalysisInfo", myAnalysisInfoMap);
                tmap.put("commentary", commentaryMap);

                resultList.add(tmap);
            }

        }
        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("list",resultList);
        returnMap.put("page",page);
        return returnMap;
    }
}
