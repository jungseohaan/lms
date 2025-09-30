package com.visang.aidt.lms.api.assessment.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.assessment.mapper.TchReportEvalMapper;
import com.visang.aidt.lms.api.keris.utils.AidtWebClientSender;
import com.visang.aidt.lms.api.keris.utils.ParamOption;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.repository.dto.EvlInfoResultHeaderDTO;
import com.visang.aidt.lms.api.repository.dto.EvlInfoResultSummaryDTO;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import com.visang.aidt.lms.api.wrongnote.service.StntWrongnoteService;
import com.visang.aidt.lms.global.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@Service
@RequiredArgsConstructor
public class TchReportEvalService {
    private final TchReportEvalMapper tchReportEvalMapper;

    private final StntWrongnoteService stntWrongnoteService;

    private final StntNtcnService stntNtcnService;

//    private final TchRewardService tchRewardService;

    private final StntWrongnoteMapper stntWrongnoteMapper;

    @Value("${app.statapi.url}")
    public String appStatapiUrl;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    private final AidtWebClientSender aidtWebClientSender;

    /**
     * (평가 리포트).평가 리포트 목록조회
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     *
     */
    @Transactional(readOnly = true)
    public Object findReportEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> evlInfoItem = Arrays.asList(
            "no", "id", "eamMth", "eamMthNm", "evlNm", "evlSttsCd",
            "evlSttsNm", "evlPrgDt", "evlCpDt", "targetCnt", "submitCnt", "manualCnt",
            "gradeSttsNm", "rptOthbcAt", "rptOthbcDt", "applScrAt",
            "modifyHistAt"
        );

        List<Map> evalList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = tchReportEvalMapper.findReportEvalList(pagingParam);
        if(!entityList.isEmpty()) {
            boolean isFirst = true;
            Gson gson = new Gson();

            for (Map entity : entityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(evlInfoItem, entity);
                if(!ObjectUtils.isEmpty(entity.get("extraInfo"))) {
                    String extraInfo = MapUtils.getString(entity, "extraInfo", "");
                    if (StringUtils.isNotEmpty(extraInfo)) {
                        tmap.putAll(gson.fromJson(extraInfo, Map.class));
                    }
                }
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
     * (평가 리포트).평가 리포트 결과 조회 > 자세히보기 : 목록
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findReportEvalResultDetailList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> evlInfoItem = Arrays.asList("id", "evlNm", "evlStdrSet", "mdulTotScr", "submStntCnt", "notSubmStntCnt","mdulEvlInfoList");
        List<String> mdulEvlInfoItem = Arrays.asList("evlIemId", "subId", "evlIemScr", "thumbnail", "avgCorrectRate", "articleType", "submAt", "submCnt", "errataNotFourCnt", "articleTypeNm", "stntEvlInfoList");
        List<String> stntEvlInfoItem = Arrays.asList("userId", "flnm", "stntEvlResult", "actvtnAt");
        List<String> stntEvlResultItem = Arrays.asList("evlResultId", "evlIemId", "subId", "eakAt", "eakSttsCd", "eakSttsNm", "mrkTy", "errata", "submAt", "evlIemScrResult","mdScrAt");
        List<String> stntEvlScrInfoItem = Arrays.asList("userId", "flnm", "submAt", "evlResultScr", "evlResultAnctNm", "allQstnMrkTyCd", "allManualMrkAt", "actvtnAt");

        // 학생평가결과정보
        List<LinkedHashMap<Object, Object>> stntEvlResultList = AidtCommonUtil.filterToList(stntEvlResultItem, tchReportEvalMapper.findReportEvalResultDetailList_result(paramData));

        // 학생평가정보
        List<Map> stntEvlInfoLists = tchReportEvalMapper.findReportEvalResultDetailList_stnt(paramData);

        // 모듈평가정보
        List<LinkedHashMap<Object, Object>> mdulEvlInfoList = CollectionUtils.emptyIfNull(
            tchReportEvalMapper.findReportEvalResultDetailList_mdul(paramData)
        ).stream().map(s -> {
            List<LinkedHashMap<Object, Object>> stntEvlInfoList = CollectionUtils.emptyIfNull(stntEvlInfoLists).stream()
                .map(r -> {
                    LinkedHashMap<Object, Object> stntEvlResult = CollectionUtils.emptyIfNull(stntEvlResultList).stream()
                        .filter(t -> {
                            return StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(t,"evlIemId"))
                                && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId"))
                                && StringUtils.equals(MapUtils.getString(r,"id"), MapUtils.getString(t,"evlResultId"));
                        }).findFirst().orElse(null); ;

                    r.put("stntEvlResult", stntEvlResult);
                    return AidtCommonUtil.filterToMap(stntEvlInfoItem, r);
                }).toList();

            LinkedHashMap<Object, Object> rMap = AidtCommonUtil.filterToMap(mdulEvlInfoItem, s);
            rMap.put("stntEvlInfoList", stntEvlInfoList);
            return rMap;
        }).toList();

        // 학생평가점수정보
        List<LinkedHashMap<Object, Object>> stntEvlScrInfoList = CollectionUtils.emptyIfNull(stntEvlInfoLists).stream()
            .map(s -> {
                String evlResultAnctNm = AidtCommonUtil.getEvlResultGradeNmNew(
                    MapUtils.getString(s, "evlStdrSetAt"),
                    MapUtils.getInteger(s, "evlStdrSet"),
                    MapUtils.getDouble(s, "evlIemScrTotal"),
                    MapUtils.getDouble(s, "evlResultScr"),
                    MapUtils.getInteger(s, "evlGdStdrScr"),
                    MapUtils.getInteger(s, "evlAvStdrScr"),
                    MapUtils.getInteger(s, "evlPsStdrScr"),
                    MapUtils.getString(s, "submAt")
                );
                s.put("evlResultAnctNm", evlResultAnctNm);
                return AidtCommonUtil.filterToMap(stntEvlScrInfoItem, s);
            }).toList();

        // Response
        Map evlInfoEntity = tchReportEvalMapper.findReportEvalResultDetailList_eval(paramData);
        var evalInfo = AidtCommonUtil.filterToMap(evlInfoItem, evlInfoEntity);
        evalInfo.put("mdulEvlInfoList", mdulEvlInfoList);
        evalInfo.put("stntEvlScrInfoList", stntEvlScrInfoList);
        return evalInfo;
    }

    /**
     * (평가 리포트).평가 리포트 결과 조회 > 자세히보기 : 모듈
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findReportEvalResultDetailMdul(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> evlInfoItem = Arrays.asList("id", "evlNm", "setsId");
        List<String> mdulEvlInfoItem = Arrays.asList(
            "evlIemId", "subId", "evlIemScr", "thumbnail", "targetCnt", "submitCnt",
            "avgCorrectRate", "mdulImageList", "mdulInfo", "classAnalysisInfo", "description"
        );
        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade", "curriSemester", "articleType", "setsType", "textbookType");
        List<String> classAnalysisInfoItem = Arrays.asList("correctRate", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "answerRateStr");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");

        // 모듈이미지정보
        List<LinkedHashMap<Object, Object>> mdulImageList = AidtCommonUtil.filterToList(mdulImageItem, tchReportEvalMapper.findReportEvalResultDetailMdul_image(paramData));
        // 모듈(콘텐츠)정보
        LinkedHashMap<Object, Object> mdulInfo = AidtCommonUtil.filterToMap(mdulInfoItem, tchReportEvalMapper.findReportEvalResultDetailMdul_mdul(paramData));
        // 우리반 분석
        LinkedHashMap<Object, Object> classAnalysisInfo = AidtCommonUtil.filterToMap(classAnalysisInfoItem, tchReportEvalMapper.findReportEvalResultDetailMdul_class(paramData));
        List<String> answers = tchReportEvalMapper.findReportEvalResultDetailMdul_class_answers(paramData);
        if (CollectionUtils.isNotEmpty(answers)) {
            classAnalysisInfo.put("answerRateStr", AidtCommonUtil.getAnswerCountString(answers));
        }

        // 해설
        LinkedHashMap<Object, Object> commentary = AidtCommonUtil.filterToMap(commentaryItem, tchReportEvalMapper.findReportEvalResultDetailMdul_coment(paramData));
        // 모듈평가정보
        LinkedHashMap<Object, Object> mdulEvlInfo = AidtCommonUtil.filterToMap(mdulEvlInfoItem, tchReportEvalMapper.findReportEvalResultDetailMdul_info(paramData));
        mdulEvlInfo.put("mdulImageList", mdulImageList);
        mdulEvlInfo.put("mdulInfo", mdulInfo);
        mdulEvlInfo.put("classAnalysisInfo", classAnalysisInfo);
        mdulEvlInfo.put("commentary", commentary);

        // Response
        Map evlInfoEntity = tchReportEvalMapper.findReportEvalResultDetailList_eval(paramData);
        var evalInfo = AidtCommonUtil.filterToMap(evlInfoItem, evlInfoEntity);
        evalInfo.put("mdulEvlInfo", mdulEvlInfo);
        return evalInfo;
    }

    /**
     * (평가 리포트).평가 리포트 결과 조회 > 자세히보기 : 학생
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findReportEvalResultDetailStnt(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("id", "evlNm", "setsId", "mdulEvlInfo", "stntEvlInfo");
        List<String> mdulEvlInfoItem = Arrays.asList("evlIemId", "subId", "evlIemScr", "thumbnail", "avgCorrectRate");
        List<String> stntEvlInfoItem = Arrays.asList("userId", "flnm", "submAt", "evlResultScr", "stntEvlResult");
        List<String> stntEvlResultItem = Arrays.asList(
            "id", "evlResultId", "evlIemId", "subId", "eakAt", "eakSttsCd","eakSttsNm",
            "errata", "evlIemScrResult","solvSecAvr", "subMitAnw", "subMitAnwUrl",
            "rubric", "fdbDc","peerReview", "selfEvl", "mrkTy"
        );
        List<String> erratInfoItem = Arrays.asList("evlIemId", "subId", "errata", "mrkTy", "eakSttsCd", "eakAt", "submAt");

        // 정오답 정보
        List<LinkedHashMap<Object, Object>> erratInfoList = AidtCommonUtil.filterToList(erratInfoItem, tchReportEvalMapper.findReportErrataInfoList(paramData));

        // 모듈평가정보
        LinkedHashMap<Object, Object> mdulEvlInfo = AidtCommonUtil.filterToMap(mdulEvlInfoItem, tchReportEvalMapper.findReportEvalResultDetailMdul_info(paramData));

        // 학생평가결과정보
        List<LinkedHashMap<Object, Object>> stntEvlResultList = AidtCommonUtil.filterToList(stntEvlResultItem, tchReportEvalMapper.findReportEvalResultDetailStnt_result(paramData));
        CollectionUtils.emptyIfNull(stntEvlResultList)
                .stream()
                .forEach(stntEvlResult -> {
                    if (!ObjectUtils.isEmpty(stntEvlResult.get("rubric"))) {
                        String rubricJsonString = (String) stntEvlResult.get("rubric");

                        JsonObject jsonObject = JsonParser.parseString(rubricJsonString).getAsJsonObject();
                        Map<String, Object> rubricMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                        stntEvlResult.put("rubric", rubricMap);
                    } else {
                        stntEvlResult.put("rubric", new HashMap<>());
                    }
                });

        // 학생평가정보
        LinkedHashMap<Object, Object> stntEvlInfo = AidtCommonUtil.filterToMap(stntEvlInfoItem, tchReportEvalMapper.findReportEvalResultDetailStnt_stnt(paramData));
        stntEvlInfo.put("stntEvlResult", stntEvlResultList.stream().findFirst().orElse(null));
        stntEvlInfo.put("erratInfoResult", erratInfoList);

        // Response
        var evalInfo = AidtCommonUtil.filterToMap(respItem, tchReportEvalMapper.findReportEvalResultDetailList_eval(paramData));
        evalInfo.put("mdulEvlInfo", mdulEvlInfo);
        evalInfo.put("stntEvlInfo", stntEvlInfo);
        return evalInfo;
    }

    /**
     * (평가 리포트).평가 리포트 결과 조회 > 자세히보기 : 학생 > 피드백
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map modifyReportEvalResultDetailStntFdbMod(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("evlId", "evlIemId", "userId","resultOk","resultMsg");

        // 피드백 저장
        Map<Object, Object> resultMap = new LinkedHashMap<>();
        resultMap.putAll(AidtCommonUtil.filterToMap(respItem, paramData));
        try {
            int cnt =  tchReportEvalMapper.modifyReportEvalResultDetailStntFdbMod(paramData);
            if(cnt <= 0) {
                log.info("Not Found Modifiy Data: {}", cnt);
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "저장실패");
                resultMap.put("resultErr", new Exception("Not Found Modifiy Data: " + cnt));

                return resultMap;
            }
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "저장완료");
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "저장실패");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }

    /**
     * (교사) 학급관리 > 학생조회  > 리포트화면 학생 검색
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findStntSrchReportFindStnt(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> stntInfoItem = Arrays.asList("stntId", "stntNm", "gradeNm", "claCd", "num", "genderNm", "actvtnAt");

        // 학생정보
        List<LinkedHashMap<Object, Object>> stntInfoList = AidtCommonUtil.filterToList(stntInfoItem, tchReportEvalMapper.findStntSrchReportFindStnt(paramData));

        // Response
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("stntInfoList",stntInfoList);
        return returnMap;

    }

    /**
     * (교사) 학급관리 > 학생조회  > 평가 리포트 목록조회
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findStntSrchReportEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> evlListItem = Arrays.asList(
            "no", "id", "eamMth", "eamMthNm", "evlNm", "evlSttsCd",
            "evlSttsNm", "evlPrgDt", "evlCpDt", "submAt", "evlResultScr",
            "evlStdr", "gradeSttsNm"
        );

        List<Map> evalList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> entityList = tchReportEvalMapper.findStntSrchReportEvalList(pagingParam);
        if(!entityList.isEmpty()) {
            boolean isFirst = true;
            for (Map entity : entityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                String evlStdrSetAt = MapUtils.getString(entity, "evlStdrSetAt");  // 평가기준 설정여부
                Integer evlStdrSet = MapUtils.getInteger(entity, "evlStdrSet");    // 평가기준 (1: 상/중/하, 2: 통과/실패, 3: 점수)
                Double evlIemScrTotal = MapUtils.getDouble(entity, "evlIemScrTotal");     // 만점 배점
                Double stntTotScr = MapUtils.getDouble(entity, "evlResultScr");     // 학생의 총점
                Integer evlGdStdrScr = MapUtils.getInteger(entity, "evlGdStdrScr"); // 상 기준점수
                Integer evlAvStdrScr = MapUtils.getInteger(entity, "evlAvStdrScr"); // 중 기준정부
                Integer evlPsStdrScr = MapUtils.getInteger(entity, "evlPsStdrScr"); // 통과 기준점수
                String submAt = MapUtils.getString(entity, "submAt");              // 제출여부
                String evlStdr = AidtCommonUtil.getEvlResultGradeNmNew(evlStdrSetAt,evlStdrSet, evlIemScrTotal ,stntTotScr ,evlGdStdrScr ,evlAvStdrScr ,evlPsStdrScr, submAt);

                var tmap = AidtCommonUtil.filterToMap(evlListItem, entity);
                tmap.put("evlPrgDt", AidtCommonUtil.stringToDateFormat((String) tmap.get("evlPrgDt"),"yyyy-MM-dd HH:mm:ss"));
                tmap.put("evlCpDt", AidtCommonUtil.stringToDateFormat((String) tmap.get("evlCpDt"),"yyyy-MM-dd HH:mm:ss"));
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
     * (교사) 학급관리 > 학생조회  > 평가 결과 조회(자세히보기)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findStntSrchReportEvalResultDetail(Map<String, Object> paramData) throws Exception {
        List<String> respItem = Arrays.asList("id", "evlNm", "setsId", "modNum", "mdulTotScr", "evlStdrSetAt", "evlStdrSet", "evlResultScr", "evlResultAnctNm", "stntTotScr", "errataInfo", "evlStdr", "evlInfoList", "allQstnMrkTyCd", "allManualMrkAt");
        List<String> errataInfoItem = Arrays.asList("anwNum", "triNum", "wrngNum");
        List<String> mdulItemInfoItem = Arrays.asList("evlIemId", "subId", "evlIemScr", "targetCnt", "submitCnt", "thumbnail", "mdulImageList", "mdulInfo", "myAnalysisInfo", "commentary");
        List<String> mdulImageItem = Arrays.asList("url", "image");
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> myAnalysisInfoItem = Arrays.asList("evlIemId", "subId", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "subMitAnw");
        List<String> commentaryItem = Arrays.asList("hint", "modelAnswer", "explanation");
        List<String> mdulEvlInfoItem = Arrays.asList("id", "evlResultId", "evlIemId", "subId", "errata", "evlIemScrResult", "solvSecAvr", "subMitAnw", "subMitAnwUrl", "rubric", "fdbDc", "peerReview", "selfEvl", "mrkTy", "articleType", "eakAt", "submAt");

        // 정오답정보
        LinkedHashMap<Object, Object> errataInfo = AidtCommonUtil.filterToMap(errataInfoItem, tchReportEvalMapper.findStntSrchReportEvalResultDetail_errata(paramData));
        // 모듈이미지정보
        List<Map> mdulImageLists = tchReportEvalMapper.findStntSrchReportEvalResultDetail_image(paramData);
        // 모듈(콘텐츠)정보
        List<Map> mdulInfoLists = tchReportEvalMapper.findStntSrchReportEvalResultDetail_mdul(paramData);
        // 학생 분석
        List<Map> myAnalysisInfoLists = tchReportEvalMapper.findStntSrchReportEvalResultDetail_analysis(paramData);
        // 해설
        List<Map> commentaryList = tchReportEvalMapper.findStntSrchReportEvalResultDetail_coment(paramData);
        // 모듈아이템정보
        List<LinkedHashMap<Object, Object>> mdulItemInfoLists = CollectionUtils.emptyIfNull(
                tchReportEvalMapper.findStntSrchReportEvalResultDetailMdul_info(paramData)
            ).stream().map(s -> {
                // 모듈이미지정보
                List<LinkedHashMap<Object, Object>> mdulImageList = CollectionUtils.emptyIfNull(mdulImageLists).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(mdulImageItem, r);
                    }).toList();
                s.put("mdulImageList", mdulImageList);

                // 모듈(콘텐츠)정보
                LinkedHashMap<Object, Object> mdulInfo = CollectionUtils.emptyIfNull(mdulInfoLists).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(mdulInfoItem, r);
                    }).findFirst().orElse(null);
                s.put("mdulInfo", mdulInfo);

                // 학생 분석
                LinkedHashMap<Object, Object> myAnalysisInfo = CollectionUtils.emptyIfNull(myAnalysisInfoLists).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(myAnalysisInfoItem, r);
                    }).findFirst().orElse(null);
                s.put("myAnalysisInfo", myAnalysisInfo);

                // 해설
                LinkedHashMap<Object, Object> commentary = CollectionUtils.emptyIfNull(commentaryList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(commentaryItem, r);
                    }).findFirst().orElse(null);
                s.put("commentary", commentary);

                return AidtCommonUtil.filterToMap(mdulItemInfoItem, s);
            }).toList();

        // 모듈평가정보
        List<Map> mdulEvalInfoLists = tchReportEvalMapper.findStntSrchReportEvalResultDetailMdul_eval(paramData);

        // 평가정보
        List<LinkedHashMap<Object, Object>> evlInfoList = new ArrayList<>();
        mdulItemInfoLists.forEach(s -> {
            // 모듈평가정보
            LinkedHashMap<Object, Object> mdulEvlInfo = CollectionUtils.emptyIfNull(mdulEvalInfoLists).stream()
                .filter(r -> StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(r,"evlIemId"))
                        && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(r,"subId")) )
                .map(r -> {
                    return AidtCommonUtil.filterToMap(mdulEvlInfoItem, r);
                }).findFirst().orElse(null);

            var evlInfo = new LinkedHashMap<Object, Object>();
            evlInfo.put("mdulItemInfo", s);
            evlInfo.put("mdulEvlInfo", mdulEvlInfo);
            evlInfoList.add(evlInfo);
        });

        Map<String, Object> respMap = tchReportEvalMapper.findStntSrchReportEvalResultDetail(paramData);
        String evlStdrSetAt = MapUtils.getString(respMap, "evlStdrSetAt");  // 평가기준 설정여부
        Integer evlStdrSet = MapUtils.getInteger(respMap, "evlStdrSet");    // 평가기준 (1: 상/중/하, 2: 통과/실패, 3: 점수)
        Double evlIemScrTotal = MapUtils.getDouble(respMap, "evlIemScrTotal");       // 만점 배점
        Double stntTotScr = MapUtils.getDouble(respMap, "stntTotScr");       // 학생의 총점
        Integer evlGdStdrScr = MapUtils.getInteger(respMap, "evlGdStdrScr"); // 상 기준점수
        Integer evlAvStdrScr = MapUtils.getInteger(respMap, "evlAvStdrScr"); // 중 기준정부
        Integer evlPsStdrScr = MapUtils.getInteger(respMap, "evlPsStdrScr"); // 통과 기준점수
        String submAt = MapUtils.getString(respMap, "submAt");              // 제출여부
        String evlStdr = AidtCommonUtil.getEvlResultGradeNmNew(evlStdrSetAt,evlStdrSet ,evlIemScrTotal, stntTotScr ,evlGdStdrScr ,evlAvStdrScr ,evlPsStdrScr, submAt);

        // Response
        var resp = AidtCommonUtil.filterToMap(respItem, respMap);
        resp.put("errataInfo", errataInfo);
        resp.put("evlStdr", evlStdr);
        resp.put("evlResultAnctNm", evlStdr);
        resp.put("evlResultScr", stntTotScr);
        resp.put("evlInfoList", evlInfoList);
        return resp;
    }



    /**
     * (평가 리포트).평가 결과 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> modifyReportEvalMdul(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (평가 리포트).평가 결과 피드백 작성(등록)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> createReportEvalFeedback(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * (평가 리포트).평가 결과 피드백 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> modifyReportEvalFeedback(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            { "proc-count": 1 }
        """).toMap();
    }

    /**
     * 평가 모듈 배점 수정
     * - 배점정보 수정에 따라 평가점수를 재계산해서 평가결과정보의 평가점수를 수정해줘야함
     * - 배점정보 수정에 따라 평가결과상세의 정오표를 수정해줘야함.
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> modifyReportEvalResultScore(Map<String, Object> paramData) throws Exception {
        Map<String, Object> evelResultMap = tchReportEvalMapper.findReportEvalResultForStudent(paramData);

        Map<String, Object> returnMap = new LinkedHashMap<>();

        if (evelResultMap == null) {
            returnMap.put("errorMsg", "조건에 해당하는 평가결과값이 없습니다.");
            return returnMap;
        }

        int evlDetailId = (int) evelResultMap.get("evlDetailId"); // 평가결과 상세정보 아이디
        float evlIemScrResult = ObjectUtils.isEmpty(evelResultMap.get("evlIemScrResult")) ? 0 : (float) evelResultMap.get("evlIemScrResult"); // 최종 배점결과(현재 db에 저장된 값)
        float evlIemScr = (float) evelResultMap.get("evlIemScr"); // 문항배점
        int evlResultId = (int) evelResultMap.get("evlResultId"); // 평가결과정보 아이디
        float modifiedScore = Float.valueOf(Objects.requireNonNull(paramData.get("evlIemScrResult")).toString()); ; // 교사로부터 받아온 수정할 점수 -필수값이라 널체크 안했음


        // 배점을 초과한 점수를 입력했을때 오류
        if (evlIemScr < modifiedScore) {
            returnMap.put("errorMsg", String.format("항목 배점(%f)을 초과하여 입력 할 수 없습니다.", evlIemScr));
            return returnMap;
        }

        log.info("rptOthbcAt:{}", evelResultMap.get("rptOthbcAt"));

        int errata = 4; // 채점불가 디폴트로?
        // 문항배점과 받아온 점수를 비교하여 정오표를 계산한다.
        if (modifiedScore == 0) {
            errata = 2; //오답
        } else if (modifiedScore > 0 && modifiedScore < evlIemScr) {
            errata = 3; //부분정답
        } else if (modifiedScore == evlIemScr) {
            errata = 1; //정답
        }

        double totalScore = 0d; // 총합

        // 평가결과 상세의 배점결과와 정오표를 수정하기 위한 param 생성
        Map<String, Object> modiMap = new HashMap<>();
        modiMap.put("modifiedScore", modifiedScore);
        modiMap.put("errata", errata);
        modiMap.put("evlDetailId", evlDetailId);
        modiMap.put("tchId", paramData.get("tchId"));

        log.info("modifiedScore:{}", evelResultMap.get("modifiedScore"));
        log.info("errata:{}", evelResultMap.get("errata"));
        log.info("evlDetailId:{}", evelResultMap.get("evlDetailId"));

        // 교사가 학생 배점 수정 시 제출 처리 필요
        tchReportEvalMapper.modifyReportEvalSubmAt(evlResultId);

        if ("Y".equals(evelResultMap.get("rptOthbcAt"))) // 공개여부가 Y 일때
        {
            // 수정반영여부(md_rflt_at) 값이 'N' 인 건이 존재하는지 확인
            Map _map =  tchReportEvalMapper.findEvlScrMdInfo(modiMap);

            if (_map == null) { // 없으면 insert
                int _cnt = tchReportEvalMapper.insertEvlScrMdInfo(modiMap);

                log.info("insertEvlScrMdInfo.cnt:{}", _cnt);
            } else { // 있으면 update
                Object mdId = _map.get("id");
                log.info("mdId:{}", mdId);
                modiMap.put("mdId", mdId);

                int _cnt = tchReportEvalMapper.updateEvlScrMdInfo(modiMap);
                log.info("updateEvlScrMdInfo.cnt:{}", _cnt);
            }

            // 평가결과 상세정보의 합산 점수를 계산한다.
            Map<String, Object> totalMap = (Map<String, Object>) tchReportEvalMapper.findReportEvalResultScoreSum(evlResultId);
            totalScore = (double) totalMap.get("totalScore");

            // 리포트가 공유 된 경우에도 상태 값은 변경 되어야 함. (eak_stts_cd, mrk_cp_at)
            tchReportEvalMapper.modifyReportEvalResultScore_rpt_y(modiMap);
            Map<String, Object> totalMap2 = (Map<String, Object>) tchReportEvalMapper.findReportEvalResultScoreSum(evlResultId);
            long inCompleteCnt = (long) totalMap2.get("inCompleteCnt");
            modiMap = new HashMap<>();
            modiMap.put("evlResultId", evlResultId);
            modiMap.put("eakSttsCd", (inCompleteCnt == 0)? 5:4); // eak_stts_cd < 5 인 개수가 0 이면 "5" 로 세팅, 아니면 4로 세팅
            tchReportEvalMapper.modifyReportEvalResultScoreSum_resultInfo_rpt_y(modiMap);
            tchReportEvalMapper.modifyReportEvalResultScoreSum_info(paramData);
        } else {

            // evl_result_detail 수정
            int result1 =  tchReportEvalMapper.modifyReportEvalResultScore(modiMap);
            log.info("1.evl_result_detail 수정 결과:{}", result1);

            // 평가결과 상세정보의 합산 점수를 계산한다.
            Map<String, Object> totalMap = (Map<String, Object>) tchReportEvalMapper.findReportEvalResultScoreSum(evlResultId);
            totalScore = (double) totalMap.get("totalScore");
            long inCompleteCnt = (long) totalMap.get("inCompleteCnt");
            log.info("totalScore:{}", totalScore);
            log.info("inCompleteCnt:{}", inCompleteCnt);

            // 평가결과 정보의 합산 점수를 수정한다.
            modiMap = new HashMap<>();
            modiMap.put("totalScore", totalScore);
            modiMap.put("evlResultId", evlResultId);
            modiMap.put("eakSttsCd", (inCompleteCnt == 0)? 5:4); // eak_stts_cd < 5 인 개수가 0 이면 "5" 로 세팅, 아니면 4로 세팅

            // evl_result_info 수정
            int result2 = tchReportEvalMapper.modifyReportEvalResultScoreSum_resultInfo(modiMap);
            log.info("2.evl_result_info 수정 결과:{}", result2);

            Integer evlSttsCd = (Integer) evelResultMap.get("evlSttsCd");

            // 평가 진행 중 수정 시 상태값 변경 하지 않음
            if (evlSttsCd != 2) {
                // evl_info 수정
                int result3 = tchReportEvalMapper.modifyReportEvalResultScoreSum_info(paramData);
                log.info("3.evl_info 수정 결과:{}", result3);
            }

        }

        return new JSONObject()
                .put("id", paramData.get("evlId"))
                .put("userId", paramData.get("userId"))
                .put("evlIemId", paramData.get("evlIemId"))
                .put("subId", paramData.get("subId"))
                .put("evlResultScr", totalScore)
                .put("errata", errata)
                .toMap();




    }

    /**
     * 평가결과조회 상단정보(결과보기/인사이트 공통사용)
     * @param paramData 입력 파라메터
     * @return Object
     */
    @Transactional(readOnly = true)
    public Object findReportEvalResultHeader(Map<String, Object> paramData) throws Exception {

        // 평가결과 정보
        Map headerMap = (Map) tchReportEvalMapper.findReportEvalResultHeader(paramData);

        Integer evlGdStdrScr = MapUtils.getInteger(headerMap, "evlGdStdrScr"); // 상 기준점수
        Integer evlAvStdrScr = MapUtils.getInteger(headerMap, "evlAvStdrScr"); // 중 기준점수
        Integer evlPsStdrScr = MapUtils.getInteger(headerMap, "evlPsStdrScr"); // 통과 기준점수

        ModelMapper modelMapper = new ModelMapper();
        EvlInfoResultHeaderDTO headerDTO = modelMapper.map(headerMap, EvlInfoResultHeaderDTO.class);

        // 평가 관련 계산정보(각종 평균값등)
        Map<String, Object> calcInfo = findReportEvalResultInsiteCalculate(paramData);
        if (calcInfo != null) {
            headerDTO.setScoreAvr((Double) calcInfo.getOrDefault("scoreAvr", 0)); // 평균점수
            headerDTO.setSolvDurationAvr((String) calcInfo.get("solvDurationAvr")); // 평균 소요시간
        } else {
            headerDTO.setScoreAvr(0.0);
            headerDTO.setSolvDurationAvr("00:00:00");
        }
        headerDTO.setResultTypeNm("평가");

        // 제출여부 - 학급전체라서 애매함. 0명이상 제출이고, 모든 학생이 제출시에 "Y" 로 세팅하는 걸로 일단 정함
        String submAt = headerDTO.getTargetCnt() > 0 && headerDTO.getTargetCnt() == headerDTO.getSubmitCnt()? "Y":"N";

        String evlStdr = AidtCommonUtil.getEvlResultGradeNmNew(
                MapUtils.getString(headerMap, "evlStdrSetAt"),
                MapUtils.getInteger(headerMap, "evlStdrSet"),
                MapUtils.getDouble(headerMap, "evlIemScrTotal"),
                headerDTO.getScoreAvr(),
                evlGdStdrScr,
                evlAvStdrScr,
                evlPsStdrScr,
                submAt
        );
        headerDTO.setEvlResultAnctNm(evlStdr);

        return headerDTO;
    }

    /**
     * 평가 결과 정보관련 각종 계산정보
     * return map 의 key
     * - 평균소요시간(00:00:00) durationAvr
     * - 평균소요시간 밀리Sec durationAvrMilsec
     * - 평균점수 scoreAvr
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findReportEvalResultInsiteCalculate(Map<String, Object> paramData) throws Exception {

        return tchReportEvalMapper.findReportEvalResultInsiteCalculate(paramData);
    }

    /**
     * 평가결과조회 인사이트 본문
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findReportEvalResultInsite(Map<String, Object> paramData, Pageable pageable) throws Exception {

        // Response Parameters
        // 본문
        List<String> item = Arrays.asList(
                "evlId", "setsId", "evlIemId", "subId", "thumbnail", "url",
                "image"
        );

        // MdulInfo
        List<String> mdulInfo = Arrays.asList(
                "curriYear", "curriSchool", "curriSubject", "curriGrade"
        );

        // ClassAnalysisInfo
        List<String> classAnalysisInfo = Arrays.asList(
                "correctRate", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "answerRateStr"
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

        List<Map> tmpList = tchReportEvalMapper.findReportEvalResultInsite(pagingParam);

        List<Map> resultList = new ArrayList<>();
        if (!tmpList.isEmpty()) {
            total = (long) tmpList.get(0).get("fullCount");

            for (Map map:tmpList) {
                var tmap = AidtCommonUtil.filterToMap(item, map);

                var mdulInfoMap = AidtCommonUtil.filterToMap(mdulInfo, map);
                var classAnalysisInfoMap = AidtCommonUtil.filterToMap(classAnalysisInfo, map);
                // 지문별 응답율 세팅
                Map<String, Object> innerParam = new HashMap<>();
                innerParam.put("evlId", paramData.get("evlId"));
                innerParam.put("evlIemId", map.get("evlIemId"));
                innerParam.put("subId", String.valueOf(map.get("subId")));
                List<String> answers = tchReportEvalMapper.findReportEvalResultDetailMdul_class_answers(innerParam);
                String answer = AidtCommonUtil.getAnswerCountString(answers);
                classAnalysisInfoMap.put("answerRateStr", answer);

                var commentaryMap = AidtCommonUtil.filterToMap(commentary, map);

                tmap.put("mdulInfo", mdulInfoMap);
                tmap.put("classAnalysisInfo", classAnalysisInfoMap);
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

    /**
     * 평가결과조회(결과보기)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findReportEvalResultSummary(Map<String, Object> paramData) throws Exception {

        // 상중하 심볼
        final String LEVEL_1 = "상";
        final String LEVEL_2 = "중";
        final String LEVEL_3 = "하";

        // 통과/실패 심볼
        final String PASS_Y = "통과";
        final String PASS_N = "실패";

        // y/n 심볼
        final String YES = "Y";
        final String NO = "N";



        ModelMapper modelMapper = new ModelMapper();

        float mdulTotScr = tchReportEvalMapper.findReportMdulTotScr(paramData); // 총 배점

        List<Map> mapList = tchReportEvalMapper.findReportEvalResultSummary(paramData);

        List<EvlInfoResultSummaryDTO> list = mapList.stream()
                .map(evlInfoResultSummaryDTO -> {
                    EvlInfoResultSummaryDTO dto = modelMapper.map(evlInfoResultSummaryDTO, EvlInfoResultSummaryDTO.class);
                    dto.setEvlIemScrTotal(mdulTotScr); // 평가항목의 총 배점
                    return dto;
                })
                .collect(Collectors.toList());

//        List<Map> evlStdrScr1List = new ArrayList<Map>(); // 상/중/하별 인원 정보
//        List<Map> evlStdrScr2List = new ArrayList<Map>(); // 통과/실패별 인원 정보
//        List<Map> evlStdrScr3List = new ArrayList<Map>(); // 점수별 인원 정보
//        List<Map> evlStdrScr4List = new ArrayList<Map>(); // 완료/미완료별 인원 정보


        Map<String, Integer> map1 = new HashMap<String, Integer>();
        Map<String, Integer> map2 = new HashMap<String, Integer>();
        Map<Object, Integer> map3 = new HashMap<Object, Integer>();
        Map<String, Integer> map4 = new HashMap<String, Integer>();

        for (EvlInfoResultSummaryDTO dto : list) {
            if (StringUtils.equals("Y", dto.getEvlStdrSetAt())){
                switch (dto.getEvlStdrSet()){
                    case 1:
                        try {
                            if (dto.getEvlResultScrTotal() >= (double)dto.getEvlGdStdrScr()) {
                                map1.put(LEVEL_1, map1.getOrDefault(LEVEL_1, 0) + 1);
                            } else if(dto.getEvlResultScrTotal() >= (double)dto.getEvlAvStdrScr()) {
                                map1.put(LEVEL_2, map1.getOrDefault(LEVEL_2, 0) + 1);
                            } else {
                                map1.put(LEVEL_3, map1.getOrDefault(LEVEL_3, 0) + 1);
                            }
                        } catch (Exception e) {
                            log.error(CustomLokiLog.errorLog(e));
                            log.error("calculate ResultGrade err 1: ", e);
                        }
                        break;
                    case 2:
                        try {
                            if (dto.getEvlResultScrTotal() >= (double)dto.getEvlPsStdrScr()) {
                                map2.put(PASS_Y, map2.getOrDefault(PASS_Y, 0) + 1);
                            } else {
                                map2.put(PASS_N, map2.getOrDefault(PASS_Y, 0) + 1);
                            }
                        } catch (Exception e) {
                            log.error(CustomLokiLog.errorLog(e));
                            log.error("calculate ResultGrade err 2: ", e);
                        }
                        break;
                    case 3:
                        try {
                            map3.put(dto.getEvlResultScrTotal(), map3.getOrDefault(dto.getEvlResultScrTotal(), 0) + 1);
                        } catch (Exception e) {
                            log.error(CustomLokiLog.errorLog(e));
                            log.error("calculate ResultGrade err 3: ", e);
                        }
                        break;
                    default:
                }

            } else {
                Map<String, Object> map = new HashMap<String, Object>();

                if (StringUtils.equals("Y", dto.getSubmAt())) {
                    map4.put(YES, map4.getOrDefault(YES, 0) + 1);
                } else {
                    map4.put(NO, map4.getOrDefault(NO, 0) + 1);
                }
            }
        }

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        // 상중하 배열
        if (!map1.isEmpty()) {
            int[] ArrEvlStdrScr1 = new int[3]; // 상/중/하별 인원 정보
            for(String key:map1.keySet()) {
                switch (key) {
                    case LEVEL_1 :
                        ArrEvlStdrScr1[0] = map1.get(key);
                        break;
                    case LEVEL_2:
                        ArrEvlStdrScr1[1] = map1.get(key);
                        break;
                    case LEVEL_3:
                        ArrEvlStdrScr1[2] = map1.get(key);
                        break;
                }
            }

            returnMap.put("evlStdrScr1List", ArrEvlStdrScr1);
        }else {
            returnMap.put("evlStdrScr1List", CollectionUtils.emptyCollection());
        }

        // 통과/실패 배열
        if (!map2.isEmpty()) {
            int[] ArrEvlStdrScr2 = new int[2]; // 통과/실패별 인원 정보
            for(String key:map2.keySet()) {
                switch (key) {
                    case PASS_Y:
                        ArrEvlStdrScr2[0] = map2.get(key);
                        break;
                    case PASS_N:
                        ArrEvlStdrScr2[1] = map2.get(key);
                        break;
                }
            }

            returnMap.put("evlStdrScr2List", ArrEvlStdrScr2);

        } else {
            returnMap.put("evlStdrScr2List", CollectionUtils.emptyCollection());
        }

        // 점수구간별 인원수 계산
        if (!map3.isEmpty()) {

            int[] arrScoreCnt = getScoreCnt(map3, mdulTotScr);
            returnMap.put("evlStdrScr3List", arrScoreCnt);
        } else {
            returnMap.put("evlStdrScr3List", CollectionUtils.emptyCollection());
        }

        // 완료/미완료 배열
        if (!map4.isEmpty()) {
            int[] ArrEvlStdrScr4 = new int[2]; // 완료/미완료별 인원 정보
            for(String key:map4.keySet()) {
                switch (key) {
                    case YES:
                        ArrEvlStdrScr4[0] = map4.get(key);
                        break;
                    case NO:
                        ArrEvlStdrScr4[1] = map4.get(key);
                        break;
                }
            }

            returnMap.put("evlStdrScr4List", ArrEvlStdrScr4);
        } else {
            returnMap.put("evlStdrScr4List", CollectionUtils.emptyCollection());
        }

        returnMap.put("mdulTotScr", mdulTotScr);
        returnMap.put("list", list);

        return returnMap;
    }

    /**
     * 점수 구간별 인원수 배열 계산
     * @param map3 점수별 인원수
     * @param totalScore 총점
     * @return
     */
    private int[] getScoreCnt(Map<Object, Integer> map3, float totalScore) {

        // 구간수(배열길이)
        int numRange = (int) Math.ceil(totalScore / 10.0);

        // 구간별 배열 생성
        int[] scoreCounts = new int [numRange];

        for (Object key:map3.keySet()) {
            int index = (int) Math.floor((double) key / 10.0);

            if(index < numRange) {
                scoreCounts[index] += map3.get(key);
            } else {
                log.error("{} is over totalScore({totalScore})", (double) key, totalScore);
            }

        }
        return scoreCounts;
    }

    /**
     * 학생 선택 후 평가결과 조회(결과보기) Header
     * @param paramData 입력 파라메터
     * @return Object
     */
    @Transactional(readOnly = true)
    public Object findStntSrchReportEvalResultHeader(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = (Map<String, Object>) tchReportEvalMapper.findStntSrchReportEvalResultHeader(paramData);
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
        resultMap.put("evlResultAnctNm", evlStdr);

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

        List<Map> list = (List<Map>) tchReportEvalMapper.findStntSrchReportEvalResultSummary(pagingParam);

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

        // 학생 분석
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

        List<Map> tmpList = (List<Map>) tchReportEvalMapper.findStntSrchReportEvalResultInsite(pagingParam);

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

    public Object modifyReportEvalOpen(Map<String, Object> paramData) throws Exception {
        List<String> item = Arrays.asList("evlId","rptOthbcAt","rptOthbcDt","resultOk","resultMsg");

        int cnt =  tchReportEvalMapper.modifyReportEvalOpen(paramData);
        Map<String, Object> resultMapOpenData = new HashMap<>();

        if ( cnt > 0 ) {
            try {
                // 오답노트 생성
                List<Map> sendNtcnEvlListPassivity =  tchReportEvalMapper.findSendNtcnEvlListPassivity(paramData);
                if (!sendNtcnEvlListPassivity.isEmpty()) {
                    stntWrongnoteService.createStntWrongnoteEvlId(paramData);
                }

            } catch(Exception e) {
                log.error(CustomLokiLog.errorLog(e));
            }

            resultMapOpenData = tchReportEvalMapper.findReportEvalOpenData(paramData);
        } else {
            throw new AidtException("평가 리포트 공개 실패");
        }

        resultMapOpenData.put("resultOk", cnt > 0);
        resultMapOpenData.put("resultMsg", "성공");

        // Response
        return AidtCommonUtil.filterToMap(item, resultMapOpenData);
    }


    /**
     * 평가 공개 후 배점 수정 결과 반영
     * @param paramData
     * @return  resultMap
     */
    public Map<String, Object> modifyReportEvalResultApplScore(Map<String, Object> paramData) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();

        Map<String, String> map = tchReportEvalMapper.findEvlResultIdForApplScore(paramData);

        if (map == null) {
            throw new AidtException("저장할 배점수정 정보가 없습니다.");
        }

        String evl_result_id_list = map.get("evl_result_id_list");
        String evl_md_id_list = map.get("evl_md_id_list");


        // evl_result_detail 의 점수 수정
        // evl_scr_md_info 의 md_rflt_at = 'Y' 처리
        String[] idArray = evl_md_id_list.split(",");
        List<String> idList = Arrays.asList(idArray);
        Map<String, Object> idParams = new HashMap<>();
        idParams.put("ids", idList);
        int cnt = tchReportEvalMapper.modifyReportEvalResultApplScore(idParams);
        log.debug("evl_scr_md_info, evl_result_detail 수정:{}", cnt);

        // evl_result_info 총점 수정
        idArray = evl_result_id_list.split(",");
        idList = Arrays.asList(idArray);
        idParams = new HashMap<>();
        idParams.put("ids", idList);
        cnt = tchReportEvalMapper.modifyReportEvalResultScoreSumByResultIds(idParams);
        log.debug("evl_result_info 총점 수정:{}", cnt);

        // evl_info 상태 수정
        cnt = tchReportEvalMapper.modifyReportEvalApplScore(paramData);
        log.debug("evl_result_info 총점 수정:{}", cnt);

        // 관련 오답노트 삭제
        cnt = stntWrongnoteMapper.deleteReportEvalWrongnote(paramData);
        log.debug("won_asw_note del cnt:{}", cnt);

        // 오답노트 재입력
        cnt = stntWrongnoteMapper.insertReportEvalWrongnote(paramData);
        log.debug("won_asw_note ins cnt:{}", cnt);

        resultMap.put("evlId", paramData.get("evlId"));
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");

        // 평가 점수 수정 대시보드 재집계
       CompletableFuture.runAsync(() -> {
           try {
               if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                       || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
               } else {
                   String claId = tchReportEvalMapper.findClaIdInEvalInfo(String.valueOf(paramData.get("evlId")));
                   Map<String, Object> batchParamData = new HashMap<>();
                   batchParamData.put("claId", claId);
                   batchParamData.put("trgtSeCd", 3);
                   batchParamData.put("trgtId", paramData.get("evlId"));
                   batchTextbkMdulReset(batchParamData);
               }
           } catch (Exception e) {
               log.error("배치 처리 중 오류 발생. param: {}, error: {}", paramData, e.getMessage(), e);
           }
       });
        return resultMap;
    }

    public void batchTextbkMdulReset(Map<String, Object> paramData) throws Exception {
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("?claId=").append(paramData.get("claId"));
            queryBuilder.append("&trgtSeCd=").append(paramData.get("trgtSeCd"));
            queryBuilder.append("&trgtId=").append(paramData.get("trgtId"));
            String queryString = queryBuilder.toString();
            ParamOption option = ParamOption.builder()
                    .url(appStatapiUrl + "/api/batch/mdul/reset" + queryString)
                    .method(HttpMethod.GET)
                    .request(new JSONObject())
                    .build();
            ParameterizedTypeReference<ResponseVO> typeReference = new ParameterizedTypeReference<>() {};
            ResponseEntity<ResponseVO> response = aidtWebClientSender.sendWithBlock(option, typeReference);
            log.info("batchTextbkMdulReset response: {}", response);
        } catch (Exception e) {
            log.error("배치 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    public Map<String, Object> createTchEvalGeneralReviewSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //TODO:: mdfr = 교사ID session
        int result = tchReportEvalMapper.updateTchReportEvlReviewSave(paramData);

        if(result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }


        return returnMap;
    }


    /**
     * [교사] 학급관리 > 홈 대시보드 > 평가리포트 '처방학습' (자세히보기 - 정오표)
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findReportEvalResultIndList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> respItem = Arrays.asList("id", "evlNm", "maxCnt", "stntEvlInfoList","stntEvlScrInfoList");
        List<String> stntEvlInfoItem = Arrays.asList("userId", "flnm", "setsId", "stntEvlResultList");
        List<String> stntEvlResultItem = Arrays.asList("evlResultId", "evlIemId", "subId", "mrkTy", "errata");
        List<String> stntEvlScrInfoItem = Arrays.asList("userId", "flnm", "evlResultScr","evlResultAnctNm");


        // 학생평가결과정보
        List<Map> stntEvlResultLists = tchReportEvalMapper.findReportEvalResultDetailList_result(paramData);

        // 학생평가정보
        List<Map> stntEvlInfoLists = tchReportEvalMapper.findReportEvalResultIndList_stnt(paramData);
        List<LinkedHashMap<Object, Object>> stntEvlInfoList = CollectionUtils.emptyIfNull(stntEvlInfoLists).stream()
            .map(s -> {
                List<LinkedHashMap<Object, Object>> stntEvlResultList = CollectionUtils.emptyIfNull(stntEvlResultLists).stream()
                    .filter(t -> {
                        return StringUtils.equals(MapUtils.getString(t,"evlResultId"), MapUtils.getString(s,"id"));
                    })
                    .map(t -> {
                        return AidtCommonUtil.filterToMap(stntEvlResultItem, t);
                    }).toList();

                s.put("stntEvlResultList", stntEvlResultList);
                return AidtCommonUtil.filterToMap(stntEvlInfoItem, s);
            }).toList();

        // 평가정보
        Map<String, Object> respMap = tchReportEvalMapper.findReportEvalResultIndList_eval(paramData);
        String evlStdrSetAt = MapUtils.getString(respMap, "evlStdrSetAt");  // 평가기준 설정여부
        Integer evlStdrSet = MapUtils.getInteger(respMap, "evlStdrSet");    // 평가기준 (1: 상/중/하, 2: 통과/실패, 3: 점수)
        Double evlIemScrTotal = MapUtils.getDouble(respMap, "evlIemScrTotal");       // 만점 배점
        Integer evlGdStdrScr = MapUtils.getInteger(respMap, "evlGdStdrScr"); // 상 기준점수
        Integer evlAvStdrScr = MapUtils.getInteger(respMap, "evlAvStdrScr"); // 중 기준정부
        Integer evlPsStdrScr = MapUtils.getInteger(respMap, "evlPsStdrScr"); // 통과 기준점수


        // 학생평가점수정보
        List<LinkedHashMap<Object, Object>> stntEvlScrInfoList = CollectionUtils.emptyIfNull(stntEvlInfoLists).stream()
            .map(s -> {
                String submAt = MapUtils.getString(s, "submAt");              // 제출여부
                Double evlResultScr = MapUtils.getDouble(s, "evlResultScr");       // 학생의 총점
                String evlResultAnctNm = AidtCommonUtil.getEvlResultGradeNmNew(evlStdrSetAt,evlStdrSet ,evlIemScrTotal, evlResultScr ,evlGdStdrScr ,evlAvStdrScr ,evlPsStdrScr, submAt);
                s.put("evlResultAnctNm", evlResultAnctNm);
                return AidtCommonUtil.filterToMap(stntEvlScrInfoItem, s);
            }).toList();

        // Response
        var resp = AidtCommonUtil.filterToMap(respItem, respMap);
        resp.put("stntEvlInfoList", stntEvlInfoList);
        resp.put("stntEvlScrInfoList", stntEvlScrInfoList);
        return resp;
    }

    @Transactional(readOnly = true)
    public Object findStntList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> stntInfoItem = Arrays.asList("userId", "flnm");

        // 학생목록 정보
        List<LinkedHashMap<Object, Object>> stntList = AidtCommonUtil.filterToList(stntInfoItem, tchReportEvalMapper.findStntList(paramData));

        // Response
        var resp = new LinkedHashMap<Object, Object>();
        resp.put("stntList", stntList);
        return resp;
    }

    @Transactional(readOnly = true)
    public Object findTchReportEvalGeneralReviewInfo(Map<String, Object> paramData) throws Exception {

        Map resultMap = tchReportEvalMapper.findTchReportEvalGeneralReviewInfo(paramData);

        return resultMap;

    }

    @Transactional(readOnly = true)
    public Object findTchReportEvalGeneralReviewAiEvlWord(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> metaIdInfoItem = Arrays.asList("metaId");

        // 학습맵 대단원 목록정보
        List<LinkedHashMap<Object, Object>> metaIdList = AidtCommonUtil.filterToList(metaIdInfoItem, tchReportEvalMapper.findTchReportEvalGeneralReviewAiEvlWord(paramData));

        // 수준
        Map<String, Object> resultMap = tchReportEvalMapper.findTchReportEvalResultDetail(paramData);
        Integer level = MapUtils.getInteger(resultMap,"level",null);

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("metaIdList",metaIdList);
        respMap.put("level",level);
        return respMap;
    }

    @Transactional(readOnly = true)
    public Object findReportEvalResultDetailSummary(Map<String, Object> paramData) throws Exception {

        List<String> evlInfoItem = Arrays.asList("id", "evlNm", "rptOthbcAt", "rpOthbcDt", "applScrAt", "modifyHistAt");
        List<String> stntTopFiveListItem = Arrays.asList("num", "evlIemId", "subId", "correctRate", "articleType", "articleTypeNm", "isGradingRequired");
        List<String> stntGuideNeededListItem = Arrays.asList("userId", "flnm", "mdulTotScr");
        List<String> avgCorrectRateInfoItem = Arrays.asList("avgCorrectAnwNum", "eamExmNum", "avgCorrectRate");

        Map evlInfoEntity = tchReportEvalMapper.findReportEvalResultDetailList_eval(paramData);
        var evalInfo = AidtCommonUtil.filterToMap(evlInfoItem, evlInfoEntity);

        List<LinkedHashMap<Object, Object>> stntTopFiveList = AidtCommonUtil.filterToList(stntTopFiveListItem, tchReportEvalMapper.findStntTopFiveList(paramData));
        List<LinkedHashMap<Object, Object>> stntGuideNeededList = AidtCommonUtil.filterToList(stntGuideNeededListItem, tchReportEvalMapper.findStntGuideNeededList(paramData));

        evalInfo.put("avgCorrectRateInfo", AidtCommonUtil.filterToMap(avgCorrectRateInfoItem, tchReportEvalMapper.findAvgCorrectRateInfo(paramData)));
        evalInfo.put("stntTopFiveList", stntTopFiveList);
        evalInfo.put("stntGuideNeededList", stntGuideNeededList);

        return evalInfo;
    }
}

