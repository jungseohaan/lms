package com.visang.aidt.lms.api.materials.service;

import com.visang.aidt.lms.api.kafka.service.KafkaBatchService;
import com.visang.aidt.lms.api.keris.utils.AidtWebClientSender;
import com.visang.aidt.lms.api.keris.utils.ParamOption;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.materials.mapper.TchMdulQstnMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.global.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
@Slf4j
@Service
@RequiredArgsConstructor
public class TchMdulQstnService {
    private final TchMdulQstnMapper tchMdulQstnMapper;
    private final StntRewardService stntRewardService;
    private final KafkaBatchService kafkaBatchService;
    private final AiLearningMapper aiLearningMapper;

    @Value("${app.statapi.url}")
    public String appStatapiUrl;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    private final AidtWebClientSender aidtWebClientSender;
    @Transactional(readOnly = true)
    public Object findTchMdulQstnAnsw(Map<String, Object> paramData, Pageable pageable) throws Exception {

        System.err.println("pageable:::" + pageable);


        // 개인별 맞춤 학습 여부 확인
        boolean isCustomLearning = paramData.containsKey("examTarget") &&
                paramData.get("examTarget") != null &&
                "2".equals(String.valueOf(paramData.get("examTarget")));

        if (isCustomLearning) {
            // 개인별 맞춤 학습 처리
            return handleCustomLearning(paramData, pageable);
        } else {
            // 기존 일반 학습 처리
            return handleGeneralLearning(paramData, pageable);
        }
    }

    /**
     * 개인별 맞춤 학습 처리
     */
    private Object handleCustomLearning(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // dtaResultInfoList에서 학생 정보 추출
        List<Map<String, Object>> dtaResultInfoList = (List<Map<String, Object>>) paramData.get("dtaResultInfoList");

        if (CollectionUtils.isEmpty(dtaResultInfoList)) {
            return createEmptyResult();
        }

        // 페이징 처리를 위한 파라미터 설정
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // 개인별 맞춤 학습 문제 목록 조회
        List<Map> customLearningProblems = tchMdulQstnMapper.findCustomLearningProblemsWithCount(pagingParam);

        List<String> customLearningItem = Arrays.asList("dtaIemId", "subId", "submStntCnt", "bmkYn", "bmkId", "mrkTy", "setsId", "problemNumber","mamoymId", "fullCount");
        var problemList = AidtCommonUtil.filterToList(customLearningItem, customLearningProblems);
        long total = 0;
        if (!customLearningProblems.isEmpty()) {
            total = (long) customLearningProblems.get(0).get("fullCount");
        }


        // 페이징 정보 생성
        PagingInfo page = AidtCommonUtil.ofPageInfo(customLearningProblems, pageable, total);

        // 문제별 학생 답안 정보 조회
        if (!ObjectUtils.isEmpty(problemList)) {
            // 각 문제에 대한 학생 답안 정보 조회
            var customAnswerInfoList = AidtCommonUtil.filterToList(
                    Arrays.asList("userIdx", "subMitAnw", "userId", "flnm", "errata", "dtaIemId", "subId", "mamoymId"),
                    tchMdulQstnMapper.findCustomLearningAnswerInfo(problemList, paramData)
            );

            // 자습 정보 조회
            var customSelfStdList = AidtCommonUtil.filterToList(
                    Arrays.asList("userIdx", "userId", "flnm", "dtaIemId", "subId", "mamoymId"),
                    tchMdulQstnMapper.findCustomLearningSelfStd(problemList, paramData)
            );

            // 문제별로 답안 정보와 자습 정보 매핑
            for (LinkedHashMap<Object, Object> problem : problemList) {
                problem.put("qstnInfoList", CollectionUtils.emptyIfNull(customAnswerInfoList).stream()
                        .filter(answer -> StringUtils.equals(MapUtils.getString(problem, "dtaIemId"), MapUtils.getString(answer, "dtaIemId")))
                        .filter(answer -> StringUtils.equals(MapUtils.getString(problem, "subId"), MapUtils.getString(answer, "subId")))
                        .filter(answer -> StringUtils.equals(MapUtils.getString(problem, "mamoymId"), MapUtils.getString(answer, "mamoymId")))
                        .map(answer -> {
                            answer.remove("dtaIemId");
                            answer.remove("subId");
                            answer.remove("mamoymId");
                            return answer;
                        }).toList()
                );

                problem.put("selfStdList", CollectionUtils.emptyIfNull(customSelfStdList).stream()
                        .filter(selfStd -> StringUtils.equals(MapUtils.getString(problem, "dtaIemId"), MapUtils.getString(selfStd, "dtaIemId")))
                        .filter(selfStd -> StringUtils.equals(MapUtils.getString(problem, "subId"), MapUtils.getString(selfStd, "subId")))
                        .filter(selfStd -> StringUtils.equals(MapUtils.getString(problem, "mamoymId"), MapUtils.getString(selfStd, "mamoymId")))
                        .map(selfStd -> {
                            selfStd.remove("dtaIemId");
                            selfStd.remove("subId");
                            selfStd.remove("mamoymId");
                            return selfStd;
                        }).toList()
                );
            }
        }

        // AI 맞춤 학습 방법 정보 조회
        List<Map> resultMapList = aiLearningMapper.findAiCustomLearningSetInfo(paramData);
        if (!resultMapList.isEmpty()) {
            returnMap.put("aiCstmzdStdMthdSeCd", resultMapList.get(0).get("aiCstmzdStdMthdSeCd"));
        }

        returnMap.put("mdulList", problemList);

        // 개인별 맞춤 학습의 경우 학생별 정답률/제출률 계산
        var customStntRateList = calculateCustomLearningStudentRates(dtaResultInfoList, paramData);
        returnMap.put("stntRateList", customStntRateList);

        if ("Y".equals(MapUtils.getString(paramData, "pageYn", "N"))) {
            returnMap.put("page", page);
        }

        return returnMap;
    }

    /**
     * 기존 일반 학습 처리 (기존 로직 유지)
     */
    private Object handleGeneralLearning(Map<String, Object> paramData, Pageable pageable) throws Exception {
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        var returnMap = new LinkedHashMap<>();

        List<String> tchMdulQstnAnswItem = Arrays.asList("dtaIemId", "subId", "submStntCnt", "bmkYn", "bmkId", "mrkTy", "fullCount");
        List<String> tchMdulQstnAnswStntItem = Arrays.asList("userIdx", "subMitAnw", "userId", "flnm", "errata", "dtaIemId", "subId");
        List<String> tchMdulQstnAnswSelfStdItem = Arrays.asList("userIdx", "userId", "flnm", "dtaIemId", "subId");

        List<Map> mdulListMap = tchMdulQstnMapper.findTchMdulQstnAnswResultDetailInfo(pagingParam);
        var mdulList = AidtCommonUtil.filterToList(tchMdulQstnAnswItem, mdulListMap);


        if (!mdulListMap.isEmpty()) {
            total = (long) mdulListMap.get(0).get("fullCount");

            if (paramData.get("examTarget") != null) {
                List<Map> resultMapList = aiLearningMapper.findAiCustomLearningSetInfo(paramData);
                returnMap.put("aiCstmzdStdMthdSeCd", resultMapList.get(0).get("aiCstmzdStdMthdSeCd"));
                paramData.put("aiCstmzdStdMthdSeCd", resultMapList.get(0).get("aiCstmzdStdMthdSeCd"));
            } else {
                paramData.put("aiCstmzdStdMthdSeCd", 0);
            }
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(mdulListMap, pageable, total);

        if (!ObjectUtils.isEmpty(mdulList)) {
            var qstnInfoList = AidtCommonUtil.filterToList(tchMdulQstnAnswStntItem, tchMdulQstnMapper.findTchMdulQstnAnswResultInfo(mdulList, paramData));
            var selfStdList = AidtCommonUtil.filterToList(tchMdulQstnAnswSelfStdItem, tchMdulQstnMapper.findTchMdulQstnAnswSelfStd(mdulList, paramData));

            for (LinkedHashMap<Object, Object> s : mdulList) {
                s.put("qstnInfoList", CollectionUtils.emptyIfNull(qstnInfoList).stream()
                        .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"dtaIemId")))
                        .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                        .map(g -> {
                            g.remove("dtaIemId");
                            g.remove("subId");
                            return g;
                        }).toList()
                );
                s.put("selfStdList", CollectionUtils.emptyIfNull(selfStdList).stream()
                        .filter(g -> StringUtils.equals(MapUtils.getString(s,"dtaIemId"), MapUtils.getString(g,"dtaIemId")))
                        .filter(g -> StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(g,"subId")))
                        .map(g -> {
                            g.remove("dtaIemId");
                            g.remove("subId");
                            return g;
                        }).toList()
                );
            }
        }

        // 학생별 정답률, 제출률
        List<String> tchMdulQstnAnswStntRateItem = Arrays.asList("userId", "crrRate", "submRate");
        var stntRateList = AidtCommonUtil.filterToList(tchMdulQstnAnswStntRateItem, tchMdulQstnMapper.findTchMdulQstnAnswStntRate(paramData));

        returnMap.put("mdulList", mdulList);
        returnMap.put("stntRateList", stntRateList);
        if ("Y".equals(MapUtils.getString(paramData, "pageYn", "N"))) {
            returnMap.put("page", page);
        }

        return returnMap;
    }

    /**
     * 개인별 맞춤 학습 학생별 정답률/제출률 계산
     */
    private List<Map<String, Object>> calculateCustomLearningStudentRates(List<Map<String, Object>> dtaResultInfoList, Map<String, Object> paramData) {
        List<Map<String, Object>> studentRates = new ArrayList<>();

        for (Map<String, Object> student : dtaResultInfoList) {
            Map<String, Object> rateParam = new HashMap<>(paramData);
            rateParam.put("setsId", student.get("setsId"));
            rateParam.put("mamoymId", student.get("mamoymId"));

            // 각 학생별 정답률, 제출률 조회
            Map<String, Object> rate = tchMdulQstnMapper.findCustomLearningStudentRate(rateParam);
            if (rate != null) {
                studentRates.add(rate);
            }
        }

        return studentRates;
    }

    /**
     * 빈 결과 반환
     */
    private Object createEmptyResult() {
        var returnMap = new LinkedHashMap<>();
        returnMap.put("mdulList", Collections.emptyList());
        returnMap.put("stntRateList", Collections.emptyList());
        return returnMap;
    }

    public Object modifyTchMdulQstnReset(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //var sdriMap =  tchMdulQstnMapper.findTchMdulQstnSDRI(paramData);
        //paramData.put("textbkTabId", MapUtils.getInteger(sdriMap, "textbkTabId"));

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        // 기존 문항지 히스토리로 이관
        int result0 = 0, result1 = 0, updateSrcDetailIdCnt = 0;
        try {
            log.warn("다시풀기 기존 문항지 히스토리로 이관 - createTchMdulQstnResetSDRHist");
             result0 = tchMdulQstnMapper.createTchMdulQstnResetSDRHist(paramData);
            log.warn("result0:{}", result0);
        }
        catch (Exception e) {
            log.warn("다시풀기 기존 문항지 히스토리로 이관 - createTchMdulQstnResetSDRHist Exception : {}", e.getMessage(), e);
        }


        // 기존 문항지 초기화 업데이트(std_dta_result_detail)

        try {
            log.warn("다시풀기 기존 문항지 히스토리로 이관 - modifyTchMdulQstnResetSDRD");
             result1 = tchMdulQstnMapper.modifyTchMdulQstnResetSDRD(paramData);
            log.warn("result1:{}", result1);
        }
        catch (Exception e) {
            log.warn("다시풀기 기존 문항지 히스토리로 이관 - modifyTchMdulQstnResetSDRD Exception : {}", e.getMessage(), e);
        }


        // 기존 문항지에서 파생된(다른문제 풀기) 초기화 업데이트 (std_dta_result_detail.src_detail_id)
        try {
            log.warn("다시풀기 기존 문항지 히스토리로 이관 - modifyTchMdulQstnResetSrcDetailId");

             updateSrcDetailIdCnt = tchMdulQstnMapper.modifyTchMdulQstnResetSrcDetailId(paramData);
            log.warn("updateSrcDetailIdCnt:{}", updateSrcDetailIdCnt);
        }
        catch (Exception e) {
            log.warn("다시풀기 기존 문항지 히스토리로 이관 - modifyTchMdulQstnResetSrcDetailId Exception : {}", e.getMessage(), e);
        }




        // 기존 문항지에서 파생된(다른문제 풀기) 삭제 (std_dta_result_detail.src_detail_id)
        //int deleteSrcDetailIdCnt = tchMdulQstnMapper.removeTchMdulQstnResetSrcDetailId(paramData);
        //log.info("deleteSrcDetailIdCnt:{}", deleteSrcDetailIdCnt);

        // 오답노트 초기화
        int initWanCnt = tchMdulQstnMapper.initWanCnt(paramData);

        if (result1 > 0) {
            // 기존 문항 초기화(std_dta_result_info)
            int result2 = tchMdulQstnMapper.modifyTchMdulQstnResetSDRI(paramData);
            log.info("result2:{}", result2);
        }

        returnMap.putAll(paramData);
        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        paramData.remove("textbkTabId");
        return returnMap;
    }


    //수업 다시하기 + 집계 배치(비동기)
    public Object modifyTchMdulQstnResetWithBatch(Map<String, Object> paramData) throws Exception {

        // 1. 수업 다시하기
        Object resetResult = modifyTchMdulQstnReset(paramData);
        log.info("수업 다시하기 완료. evlId: {}", paramData.get("evlId"));

        Map<String, Object> batchParam = buildBatchParams(paramData);
        paramData.putAll(batchParam);

        // 2. 집계 배치  ( 비동기 처리)
        Map<String, Object> asyncParam = new LinkedHashMap<>(batchParam);
        CompletableFuture.runAsync(() -> {
            try {
                log.info("수업 다시하기 완료 후 배치 작업 시작. evlId: {}", asyncParam.get("evlId"));
                if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                        || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
                    modifyTchMdulQstnBatchReset(asyncParam);
                } else {
                    batchTextbkLearningReset(paramData);
                }
                log.info("배치 작업 완료. evlId: {}", asyncParam.get("evlId"));
            } catch (Exception e) {
                log.error("배치 처리 중 오류 발생. evlId: {}, error: {}", asyncParam.get("evlId"), e.getMessage(), e);
            }
        });

        return resetResult;
    }

    private Map<String, Object> buildBatchParams(Map<String, Object> source) {
        Map<String, Object> param = new LinkedHashMap<>(source);
        Integer subId = MapUtils.getInteger(param, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            param.put("subId", 0);
        }
        param.put("trgtSeCd", "1");
        return param;
    }

    public void modifyTchMdulQstnBatchReset(Map<String, Object> paramData) throws Exception {
        try {
            // trgtSeCd 값이 없을 경우 기본값 설정
            if (ObjectUtils.isEmpty(paramData.get("trgtSeCd"))) {
                paramData.put("trgtSeCd", "1");
            }

            kafkaBatchService.processContentReset(paramData);
        } catch (Exception e) {
            log.error("평가 배치 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public void batchTextbkLearningReset(Map<String, Object> paramData) throws Exception {
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("?claId=").append(URLEncoder.encode(String.valueOf(paramData.getOrDefault("claId", "")), StandardCharsets.UTF_8));
            queryBuilder.append("&setsId=").append(URLEncoder.encode(String.valueOf(paramData.getOrDefault("setsId", "")), StandardCharsets.UTF_8));
            queryBuilder.append("&articleId=").append(URLEncoder.encode(String.valueOf(paramData.getOrDefault("articleId", "")), StandardCharsets.UTF_8));
            queryBuilder.append("&subId=").append(URLEncoder.encode(String.valueOf(paramData.getOrDefault("subId", 0)), StandardCharsets.UTF_8));
            String queryString = queryBuilder.toString();
            ParamOption option = ParamOption.builder()
                    .url(appStatapiUrl + "/api/batch/textbk/learning/reset" + queryString)
                    .method(HttpMethod.GET)
                    .request(new JSONObject())
                    .build();
            ParameterizedTypeReference<ResponseVO> typeReference = new ParameterizedTypeReference<>() {};
            ResponseEntity<ResponseVO> response = aidtWebClientSender.sendWithBlock(option, typeReference);
            log.info("batchTextbkLearningReset response: {}", response);
        } catch (Exception e) {
            log.error("배치 처리 중 오류 발생: {}", e.getMessage());
        }
    }
    public Object modifyTchMdulQstnStatus(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        //var sdriMap =  tchMdulQstnMapper.findTchMdulQstnSDRI(paramData);
        //paramData.put("textbkTabId", MapUtils.getInteger(sdriMap, "textbkTabId"));

        List<String> resultInfoItem = Arrays.asList("id", "detailId", "mamoymId", "flnm", "profileImg", "thumbnail", "stdFdbDc", "stdFdbUrl", "exltAnwAt", "fdbExpAt", "oldExltAnwAt", "subMitAnw", "subMitAnwUrl", "hdwrtCn", "delYn" , "errata");
        List<String> stntInfoItem = Arrays.asList("userIdx", "userId", "flnm", "thumbnail", "profileImg");

        List<LinkedHashMap<Object, Object>> resultInfoList = AidtCommonUtil.filterToList(resultInfoItem, tchMdulQstnMapper.findTchMdulQstnStatusResultInfoList(paramData));
        List<LinkedHashMap<Object, Object>> stntInfoList = AidtCommonUtil.filterToList(stntInfoItem, tchMdulQstnMapper.findTchMdulQstnStatusStntInfoList(paramData));

        List<String> stntErrataItem = Arrays.asList(
                "userId", "dtaResultId", "dtaIemId",  "detailId",
                "subMitAnw", "errata",
                "eakSttsCd", "reExmCnt", "attemptType", "attemptDt"
        );
        List<LinkedHashMap<Object, Object>> stntErrataList = AidtCommonUtil.filterToList(stntErrataItem, tchMdulQstnMapper.findTchMdulQstnStatusStntErrataList(paramData));

        paramData.remove("textbkTabId");

        returnMap.putAll(paramData);

        returnMap.put("resultList", resultInfoList);
        returnMap.put("resultCnt", resultInfoList.size());
        returnMap.put("stntList", stntInfoList);
        returnMap.put("stntInfoCnt", stntInfoList.size());
        returnMap.put("stntErrataCnt", stntErrataList.size());
        returnMap.put("stntErrataList", stntErrataList);

        return returnMap;
    }

    public Object modifyTchMdulQstnExclnt(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchMdulQstnMapper.modifyTchMdulQstnExclnt(paramData);
        log.info("result1:{}", result1);

        returnMap.put("detailId", paramData.get("detailId"));
        if (result1 > 0) {

            //우수답안 선정 시 리워드 추가 (하트 : 10)
            String fdbExpAt = MapUtils.getString(paramData, "fdbExpAt");
            List<Map> findRwdInParamList = tchMdulQstnMapper.findRwdInParam(paramData);

            for (Map map : findRwdInParamList) {
                Map<String, Object> rwdMap = new HashMap<>();

                rwdMap.put("userId", MapUtils.getString(map, "mamoymId"));
                rwdMap.put("claId", MapUtils.getString(map, "claId"));
                rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                rwdMap.put("menuSeCd", "1"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:자기주도학습, 5:게임
                rwdMap.put("sveSeCd", "9"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습,  7:오답노트, 8:게임, 9:우수답안
                rwdMap.put("trgtId", MapUtils.getString(map, "id"));
                rwdMap.put("textbkId", MapUtils.getString(map, "textbkId"));
                rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                rwdMap.put("rwdAmt", 10); //지급
                rwdMap.put("rwdUseAmt", 0); //지급일때는 0

                Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);
            }

            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object modifyTchMdulQstnExclntCancel(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = tchMdulQstnMapper.modifyTchMdulQstnExclntCancel(paramData);
        log.info("result1:{}", result1);

        returnMap.put("detailId", paramData.get("detailId"));
        if (result1 > 0) {

            //우수답안 선정 취소 시 리워드 회수 (하트 : 10)
            String fdbExpAt = MapUtils.getString(paramData, "fdbExpAt");
            List<Map> findRwdInParamList = tchMdulQstnMapper.findRwdInParam(paramData);

            for (Map map : findRwdInParamList) {
                Map<String, Object> rwdMap = new HashMap<>();

                rwdMap.put("userId", MapUtils.getString(map, "mamoymId"));
                rwdMap.put("claId", MapUtils.getString(map, "claId"));
                rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                rwdMap.put("menuSeCd", "1"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:자기주도학습, 5:게임
                rwdMap.put("sveSeCd", "9"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습,  7:오답노트, 8:게임, 9:우수답안
                rwdMap.put("trgtId", MapUtils.getString(map, "id"));
                rwdMap.put("textbkId", MapUtils.getString(map, "textbkId"));
                rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                rwdMap.put("rwdAmt", 0); //지급
                rwdMap.put("rwdUseAmt", 10); //지급일때는 0

                Map<String, Object> rewardResult = stntRewardService.rewardReset(rwdMap);
            }

            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object modifyTchMdulQstnFdb(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.putAll(paramData);

        int result1 = tchMdulQstnMapper.modifyTchMdulQstnFdb(paramData);
        log.info("result1:{}", result1);

        returnMap.put("detailId", paramData.get("detailId"));
        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }
    public Object modifyTchMdulQstnFdbShare(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        returnMap.putAll(paramData);

        int result1 = tchMdulQstnMapper.modifyTchMdulQstnFdbShare(paramData);
        log.info("result1:{}", result1);

        returnMap.put("detailId", paramData.get("detailId"));
        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object modifyTchMdulQstnIndi(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        List<String> resultInfoItem = Arrays.asList("dtaResultId", "detailId", "mamoymId", "flnm", "pfUiImg", "subMitAnw", "subMitAnwUrl", "errata", "stdFdbDc", "stdFdbUrl", "exltAnwAt", "fdbExpAt", "hdwrtCn");

        returnMap.put("stdInfoList", AidtCommonUtil.filterToList(resultInfoItem, tchMdulQstnMapper.findTchMdulQstnIndi(paramData)));


        return returnMap;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findLectureAutoQstnExtr(Map<String, Object> paramData) {
        // Response Parameters
        List<String> articleInfoItem = Arrays.asList("id", "name", "thumbnail", "questionTypeNm", "difyNm");

        // 피드백 저장
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            int eamExmNum       = MapUtils.getIntValue(paramData,"eamExmNum");      // 출제 문항수
            int eamGdExmMun     = MapUtils.getIntValue(paramData,"eamGdExmMun");    // 상
            int eamAvUpExmMun   = MapUtils.getIntValue(paramData,"eamAvUpExmMun");  // 중상
            int eamAvExmMun     = MapUtils.getIntValue(paramData,"eamAvExmMun");    // 중
            int eamAvLwExmMun   = MapUtils.getIntValue(paramData,"eamAvLwExmMun");  // 중하
            int eamBdExmMun     = MapUtils.getIntValue(paramData,"eamBdExmMun");    // 하
            int difyExmNum      = eamGdExmMun + eamAvUpExmMun + eamAvExmMun + eamAvLwExmMun + eamBdExmMun; // 난이도 문항수
            if(eamExmNum != difyExmNum) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", String.format("출제 문항수와 난이도 문항수가 다릅니다: %s != %s", eamExmNum, difyExmNum));
                return resultMap;
            }
            Map<Object, Object> procParamData = new HashMap<Object, Object>(paramData);

            List<LinkedHashMap<Object, Object>> articleList = new ArrayList<>();
            Set<String> selectedArticleIds = new HashSet<>();
            Set<String> selectedStudyMap_1 = new HashSet<>();

            List eamScpList = (List) procParamData.get("eamScp");
            int eamScpSize = eamScpList.size();

            Object[][] difyArr = {
                {"MD05", "하", eamBdExmMun},
                {"MD04", "중하", eamAvLwExmMun},
                {"MD03", "중", eamAvExmMun},
                {"MD02", "중상", eamAvUpExmMun},
                {"MD01", "상", eamGdExmMun}
            };

            for (Object[] difyObj : difyArr) {
                int difyLimit = (int) difyObj[2];
                if(difyLimit <= 0) continue;

                procParamData.put("difyCode", difyObj[0]);
                procParamData.put("difyLimit", difyLimit);
                procParamData.put("excludeIds", selectedArticleIds); // 이미 선택된 ID 전달
                procParamData.put("excludeStudyMaps", selectedStudyMap_1); // 이미 선택된 지식요인 전달

                List<Map> evalAutoQstnExtr = tchMdulQstnMapper.findLectureAutoQstnExtr(procParamData);

                if (evalAutoQstnExtr.size() != difyLimit) {
                    resultMap.put("resultOk", false);
                    resultMap.put("resultMsg", "입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
                    return resultMap;
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

            resultMap.put("articleList", articleList);
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "조건에 맞는 모듈이 존재하지 않습니다.");
            resultMap.put("resultErr", e);
        }

        // Response
        return resultMap;
    }
}
