package com.visang.aidt.lms.api.report.service;

import com.visang.aidt.lms.api.assessment.mapper.TchReportEvalMapper;
import com.visang.aidt.lms.api.learning.service.AiLearningEngService;
import com.visang.aidt.lms.api.report.constant.EvalDivision;
import com.visang.aidt.lms.api.report.constant.ReportStatusType;
import com.visang.aidt.lms.api.report.dto.EvalReportListReqDto;
import com.visang.aidt.lms.api.report.mapper.EvalReportMapper;
import com.visang.aidt.lms.api.user.mapper.UserMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.utility.utils.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EvalReportService {

    private final AiLearningEngService aiLearningEngService;
    private final EvalReportMapper evalReportMapper;
    private final TchReportEvalMapper tchReportEvalMapper;
    private final UserMapper userMapper;

    // 오답노트분류코드 (과제: 3, 평가: 4)
    private static final int WRONG_NOTE_EVALUATION_CODE = 4;


    /** 교사) 평가 리포트 요약 */
    public Object findEvlReportSummary(Map<String, Object> paramData) throws Exception {

        LinkedHashMap<Object, Object> evlReportSummaryInfo = new LinkedHashMap<>();

        // 평가 리포트 요약
        List<String> evlReportSummaryItem = Arrays.asList("evlId", "evlNm", "eamExmNum", "evlPrgDt", "evlCpDt", "timStAt", "timTime",
                "deadline", "avgSolvingTime", "submissionRate", "totalStudents", "submittedStudents");
        evlReportSummaryInfo.put("evlReportSummary",
                AidtCommonUtil.filterToList(evlReportSummaryItem, evalReportMapper.findEvlReportSummary(paramData))
        );

        // 평균 정답률
        List<String> avgCorrectRateInfoItem = List.of("avgCorrectRate");
        evlReportSummaryInfo.put("avgCorrectRate",
                AidtCommonUtil.filterToMap(avgCorrectRateInfoItem, evalReportMapper.findAvgCorrectRateInfo(paramData))
        );

        return evlReportSummaryInfo;
    }

    /** 학생) 평가 리포트 요약 */
    public Object findStntEvlReportSummary(Map<String, Object> paramData) throws Exception {

        LinkedHashMap<Object, Object> stntEvlReportSummaryInfo = new LinkedHashMap<>();

        List<String> evlReportSummaryItem = Arrays.asList("eamExmNum", "evlPrgDt", "evlCpDt", "deadline", "solvingTime",
                "correctRate", "submAt", "submDt", "timTime" ,"setsId"
        );

        List<LinkedHashMap<Object, Object>> summaryList = AidtCommonUtil.filterToList(evlReportSummaryItem,
                evalReportMapper.findStntEvlReportSummary(paramData));

        String firstSetsId;
        if (!summaryList.isEmpty()) {
            firstSetsId = (String) summaryList.get(0).get("setsId");

            paramData.put("setsId", firstSetsId);
        }

        stntEvlReportSummaryInfo.put("stntEvlReportSummary", summaryList);

        paramData.put("wonAnwClsfCd", WRONG_NOTE_EVALUATION_CODE);
        paramData.put("trgtId", Integer.parseInt(paramData.get("evlId").toString()));

        int wrongNoteCreatedCnt = evalReportMapper.selectWrongNoteCreatedAt(paramData);
        stntEvlReportSummaryInfo.put("wrongNoteAt", wrongNoteCreatedCnt > 0); // 오답 노트가 하나라도 있다면 true, 없다면 false 반환

        int textbookId = evalReportMapper.selectTextbookId(paramData);
        paramData.put("textbookId", textbookId);

        String claId = evalReportMapper.selectClaId(paramData);
        paramData.put("claId", claId);

        // 해당 커리큘럼ID가 없으면, 첫 차시인 1로 내려주게끔
        int crculId = 1;

        String crculIdStr = evalReportMapper.selectCrculId(paramData);
        if (crculIdStr != null && !crculIdStr.isEmpty()) {
            crculId = Integer.parseInt(crculIdStr);

        } else {
            Integer lastLessonCrculId = evalReportMapper.selectLastLessonCrculId(paramData);
            if (lastLessonCrculId != null) {
                crculId = lastLessonCrculId;
            }
        }
        stntEvlReportSummaryInfo.put("crculId", crculId);

        return stntEvlReportSummaryInfo;
    }

    /** AI 보조 (평가를 응시하지 않은 학생 조회) */
    public Object findUnsubmittedStudents(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        // 평가 미제출 학생 전체 조회
        List<Map> unsubmittedStudents = evalReportMapper.findUnsubmittedStudentsByEvlId(paramData);

        // 전체 미제출자 수
        int totalCount = unsubmittedStudents.size();

        // 3명만 추출
        List<Map> unsubmittedEvalStudents = unsubmittedStudents.stream()
                .limit(3)
                .collect(Collectors.toList());

        resultMap.put("students", unsubmittedEvalStudents);
        resultMap.put("totalCount", totalCount); // 전체 미제출자 수 (3명 포함)

        return resultMap;
    }

    /** 리포트 평가 목록 조회 */
    @Transactional(readOnly = true)
    public ValidationResult findEvalReportList(EvalReportListReqDto paramData, Pageable pageable) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        if (StringUtils.isNotEmpty(paramData.getEvlSeCd()) && !EvalDivision.isValidValue(paramData.getEvlSeCd())) {
            return ValidationResult.fail("잘못된 평가 구분값입니다. 허용값: " + EvalDivision.getAllowedValues());
        }

        if (!ReportStatusType.isValid(paramData.getReportStatusType())) {
            return ValidationResult.fail("잘못된 리포트 상태 값입니다. 허용값: " + ReportStatusType.getAllowedValues());
        }

        // 로그인 유저 타입 조회
        String loginUserId = paramData.getLoginUserId();
        Map<String, Object> loginUserInfo = userMapper.findUserInfoByUserId(loginUserId);
        if (ObjectUtils.isEmpty(loginUserInfo)) {
            return ValidationResult.fail("로그인 유저 정보를 확인해 주세요.");
        }

        paramData.setLoginUserSeCd((String) loginUserInfo.get("userSeCd"));

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // 리포트 목록 조회
        List<String> evalInfoItem = Arrays.asList(
                "fullCount", "id", "evlNm", "evlSeCd", "eamMth",
                "tmprStrgAt", "evlSttsCd", "evlSttsNm", "evlPrgDt", "evlCpDt",
                "regDt", "targetCnt", "submitCnt", "setsId", "tchRptChkAt"
        );
        List<Map> evalInfoList = evalReportMapper.findEvalList(pagingParam);
        List<LinkedHashMap<Object, Object>> conversionEvalInfoList = AidtCommonUtil.filterToList(evalInfoItem, evalInfoList);
        returnMap.put("evalList", conversionEvalInfoList);

        // 페이징 처리 정보 반환
        long total = evalInfoList.isEmpty() ? 0 : (long) evalInfoList.get(0).get("fullCount");
        PagingInfo page = AidtCommonUtil.ofPageInfo(evalInfoList, pageable, total);
        returnMap.put("page",page);

        // 평가 리포트 진행중, 종료 개수 조회
        List<String> progressItem = Arrays.asList("inProgressEvalCount", "completedEvalCount");
        List<LinkedHashMap<Object, Object>> progressStatus = AidtCommonUtil.filterToList(progressItem, evalReportMapper.findEvalProgressCount(paramData));
        returnMap.put("progressStatus", progressStatus);

        // 가장 최근 끝난 평가 조회
        Map<String, Object> latestEval = evalReportMapper.findLatestEval(paramData);
        returnMap.put("latestEval", latestEval);

        return ValidationResult.success(returnMap);
    }

    /** 우리반 채점 결과표 조회 */
    @Transactional(readOnly = true)
    public Object findReportEvalResultDetailList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // Response Parameters
        List<String> evlInfoItem = Arrays.asList("id", "evlNm", "evlStdrSet", "mdulTotScr", "submStntCnt", "applScrAt", "rptOthbcAt", "rptAutoOthbcAt", "rptOthbcDt", "modifyHistAt", "notSubmStntCnt","mdulEvlInfoList");
        List<String> mdulEvlInfoItem = Arrays.asList("evlIemId", "subId", "evlIemScr","mrkTy", "classAvgCorrectRate","classAvgSolvingTime", "articleType", "articleTypeNm", "submAt", "submCnt", "errataNotFourCnt", "stntEvlInfoList","fullCount");
        List<String> stntEvlInfoItem = Arrays.asList("userId", "flnm", "stntEvlResult");
        List<String> stntEvlResultItem = Arrays.asList("evlResultId", "evlIemId", "subId", "eakAt", "eakSttsCd", "eakSttsNm", "mrkTy", "errata", "submAt", "iemSubmAt", "evlIemScrResult","mdScrAt");
        List<String> stntEvlScrInfoItem = Arrays.asList("userId", "flnm", "submAt", "evlResultScr", "totalSolvingTime", "evlResultAnctNm", "allQstnMrkTyCd", "allManualMrkAt");


        // 1. 학생평가결과정보 조회
        List<LinkedHashMap<Object, Object>> stntEvlResultList = AidtCommonUtil.filterToList(stntEvlResultItem, tchReportEvalMapper.findReportEvalResultDetailList_result(paramData));

        // 2. 학생평가정보 조회
        List<Map> stntEvlInfoLists = evalReportMapper.findReportEvalResultDetailList_stnt(paramData);

        // 특정 학생 ID가 있으면 필터링, 없으면 전체 목록 사용
        String targetStudentId = MapUtils.getString(paramData, "stntId");
        final List<Map> filteredStntEvlInfoLists = StringUtils.isNotEmpty(targetStudentId)
            ? stntEvlInfoLists.stream()
                .filter(student -> StringUtils.equals(targetStudentId, MapUtils.getString(student, "userId")))
                .collect(Collectors.toList())
            : stntEvlInfoLists;

        // 3. 모듈평가정보 구성
        List<Map> scoringClassResultInfo = evalReportMapper.findScoringClassResultInfo(pagingParam);
        List<LinkedHashMap<Object, Object>> mdulEvlInfoList = CollectionUtils.emptyIfNull(
                scoringClassResultInfo
        ).stream().map(s -> {
            // 각 모듈에 대한 학생 평가 정보 목록 구성
            List<LinkedHashMap<Object, Object>> stntEvlInfoList = CollectionUtils.emptyIfNull(filteredStntEvlInfoLists).stream()
                    .map(r -> {
                        // 학생 평가 결과 찾기
                        LinkedHashMap<Object, Object> stntEvlResult = CollectionUtils.emptyIfNull(stntEvlResultList).stream()
                                .filter(t -> {
                                    return StringUtils.equals(MapUtils.getString(s,"evlIemId"), MapUtils.getString(t,"evlIemId"))
                                            && StringUtils.equals(MapUtils.getString(s,"subId"), MapUtils.getString(t,"subId"))
                                            && StringUtils.equals(MapUtils.getString(r,"id"), MapUtils.getString(t,"evlResultId"));
                                }).findFirst().orElse(null);
                        // 학생 정보에 평가 결과 추가
                        r.put("stntEvlResult", stntEvlResult);
                        return AidtCommonUtil.filterToMap(stntEvlInfoItem, r);
                    }).toList();

            // 모듈 정보 필터링 및 학생 평가 정보 추가
            LinkedHashMap<Object, Object> rMap = AidtCommonUtil.filterToMap(mdulEvlInfoItem, s);
            rMap.put("stntEvlInfoList", stntEvlInfoList);
            return rMap;
        }).toList();

        // 4. 학생평가점수정보 구성
        List<LinkedHashMap<Object, Object>> stntEvlScrInfoList = CollectionUtils.emptyIfNull(filteredStntEvlInfoLists).stream()
                .map(s -> {
                    // 평가 결과 등급명 계산
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

        List<Map> castedList = (List<Map>) (List<?>) mdulEvlInfoList;
        long total = mdulEvlInfoList.isEmpty() ? 0 : (long) mdulEvlInfoList.get(0).get("fullCount");
        PagingInfo page = AidtCommonUtil.ofPageInfo(castedList, pageable, total);
        evalInfo.put("page",page);
        evalInfo.put("mdulEvlInfoList", mdulEvlInfoList);
        evalInfo.put("stntEvlScrInfoList", stntEvlScrInfoList);
        return evalInfo;
    }

    /** 교사) 평가 리포트 확인 여부 변경 */
    public Map<String, Object> changeReportCheckAt(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        boolean isSuccess = true;
        try {
             int updateResult = evalReportMapper.modifyTeacherEvalReportChkAt(paramData);
            if (updateResult <= 0) {
                isSuccess = false;
            }
            returnMap.put("resultOk", isSuccess);
            returnMap.put("resultMsg", isSuccess ? "변경완료" : "변경실패");
        } catch (Exception e) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "변경실패");
        }

        return returnMap;
    }

    /** 평가리포트 독려알림 전송 */
    @Transactional
    public Map<String, Object> sendEvalEncouragementNotification(Map<String, Object> paramData) throws Exception{
        Map<String, Object> returnMap = new HashMap<>();

        // 독려 알림 대상 학생 목록 조회
        List<Map> targetStudents = evalReportMapper.selectEvalEncouragementTargets(paramData);

        if (targetStudents.isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "독려 알림 대상 학생이 없습니다.");
            return returnMap;
        }

        paramData.put("mdfr",
                Optional.ofNullable(paramData.get("tchId"))
                    .map(Object::toString)
                    .orElse("")
        );

        int evlId = Integer.parseInt(targetStudents.get(0).get("evlId").toString());

        // 수업 중 평가
        String stntNtcnCn = "평가가 진행되고 있습니다. 늦지 않게 제출해 주세요!";
        String tchNtcnCn = "평가가 진행되고 있습니다. 늦지 않게 제출해 주세요! 를 보냈습니다.";

        // 수업 외 평가
        if(!Objects.isNull(targetStudents.get(0).get("deadline"))){
            int deadline = Integer.parseInt(targetStudents.get(0).get("deadline").toString());

            stntNtcnCn = deadline == 0
                    ? "평가의 마감일 입니다. 마감 시간에 늦지 않도록 제출해 주세요!"
                    : String.format("평가의 마감일이 %d일 남았습니다. 마감 시간에 늦지 않도록 제출해 주세요!", deadline);

            tchNtcnCn = deadline == 0
                    ? String.format("미제출한 %d명의 학생들에게 독려 알림 '평가의 마감일 입니다'를 보냈습니다.", targetStudents.size())
                    : String.format("미제출한 %d명의 학생들에게 독려 알림 '평가의 마감일이 %d일 남았습니다.'를 보냈습니다.",  targetStudents.size(), deadline);
        }
        paramData.put("stntNtcnCn", stntNtcnCn);
        paramData.put("targetType", "eval");
        paramData.put("targetId", evlId);
        paramData.put("targetStudents", targetStudents);
        int stntInsertResult = evalReportMapper.insertStntEncouragementNotification(paramData);

        paramData.put("trgetTyCd", 7); // 대상유형코드 = 6: 과제리포트, 7: 평가 리포트
        paramData.put("tchNtcnCn", tchNtcnCn);
        paramData.put("targetStudent", targetStudents.get(0));
        evalReportMapper.insertTchEncouragementNotification(paramData);

        boolean isSuccess = stntInsertResult > 0;
        returnMap.put("resultOk", isSuccess);
        returnMap.put("resultMsg", isSuccess ? "저장완료" : "저장실패");

        if (isSuccess) {
            returnMap.put("stntNtcnCn", stntNtcnCn);
        }
        return returnMap;
    }

    /** 평가 리포트 총평 저장 */
    public Map<String, Object> createTchEvalGeneralReviewSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        List<String> studentIds = (List<String>) paramData.get("stntId");

        boolean isSuccess = true;

        paramData.put("mdfr",
                Optional.ofNullable(paramData.get("tchId"))
                    .map(Object::toString)
                    .orElse(""));

        try {
            for (String studentId : studentIds) {
                paramData.put("userId", studentId);
                int updateResult = evalReportMapper.updateTchReportEvlReviewSave(paramData);
                if (updateResult <= 0) {
                    isSuccess = false;
                }
            }

            returnMap.put("resultOk", isSuccess);
            returnMap.put("resultMsg", isSuccess ? "저장완료" : "저장실패");

        } catch (Exception e) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createAiPrscrEvlToTask(Map<String, Object> paramData) throws Exception {
        // 평가 완료 시점에서 호출예정
        paramData.put("lrnMethod", "2");        // 수업중 풀기 : 1, 과제 : 2
        paramData.put("eamTrget", "2");         // 공통 : 1, 개별 : 2

        // 처방학습 생성
        Map<String, Object> map =  aiLearningEngService.createAiPrscrEvlToTask(paramData);

        if (!(boolean) map.get("resultOk")) {
            log.error("createAiPrscrEvlToTaskEng result : {}", map.get("resultMsg"));
        } else {
            log.info("createAiPrscrEvlToTaskEng result : {}", map.get("resultMsg"));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> selectAiPrscrEvlToTask(Map<String, Object> paramData) throws Exception {
        // 평가 완료 시점에서 호출예정
        paramData.put("lrnMethod", "2");        // 수업중 풀기 : 1, 과제 : 2
        paramData.put("eamTrget", "2");         // 공통 : 1, 개별 : 2

        // 처방학습 확인
        return aiLearningEngService.selectAiPrscrEvlToTask(paramData);
    }

    public Map<String, Object> prscrCheck(Map<String, Object> paramData) throws Exception {
        Integer targetTaskId = evalReportMapper.selectTargetTaskId(paramData);
        return Map.of("targetTaskId", targetTaskId != null ? targetTaskId : 0);
    }

    /** 평가지의 개념학습 추천 문항 조회 */
    public Map<String, Object> findRecommendedQuestions(Pageable pageable, Map<String, Object> paramData)throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> responseParam = Arrays.asList("articleId","setId","thumbnail","name","evlId","description", "fullCount");
        List<LinkedHashMap<Object, Object>> recommendedQuestions = AidtCommonUtil.filterToList(responseParam, evalReportMapper.findRecommendedQuestions(pagingParam));

        List<Map> castedList = (List<Map>) (List<?>) recommendedQuestions;
        long total = recommendedQuestions.isEmpty() ? 0 : (long) recommendedQuestions.get(0).get("fullCount");
        PagingInfo page = AidtCommonUtil.ofPageInfo(castedList, pageable, total);

        returnMap.put("page",page);
        returnMap.put("recommendedQuestions", recommendedQuestions);
        return returnMap;
    }
}
