package com.visang.aidt.lms.api.report.service;

import com.visang.aidt.lms.api.homework.mapper.TchReportHomewkMapper;
import com.visang.aidt.lms.api.report.constant.ReportStatusType;
import com.visang.aidt.lms.api.report.constant.TaskDivision;
import com.visang.aidt.lms.api.report.dto.HomewkReportListReqDto;
import com.visang.aidt.lms.api.report.mapper.EvalReportMapper;
import com.visang.aidt.lms.api.report.mapper.HomewkReportMapper;
import com.visang.aidt.lms.api.user.mapper.UserMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.utility.utils.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomewkReportService {

    private final HomewkReportMapper homewkReportMapper;
    private final TchReportHomewkMapper tchReportHomewkMapper;
    private final EvalReportMapper evalReportMapper;
    private final UserMapper userMapper;

    /** 교사) 과제 리포트 요약 */
    public Object findTaskReportSummary(Map<String, Object> paramData) throws Exception {

        LinkedHashMap<Object, Object> taskReportSummaryInfo = new LinkedHashMap<>();

        // 평가 리포트 요약
        List<String> taskReportSummaryItem = Arrays.asList("taskId", "taskNm", "eamExmNum", "taskPrgDt", "taskCpDt", "timStAt", "timTime",
                "deadline", "avgSolvingTime", "submissionRate", "totalStudents", "submittedStudents");
        taskReportSummaryInfo.put("evlReportSummary",
                AidtCommonUtil.filterToList(taskReportSummaryItem, homewkReportMapper.findTaskReportSummary(paramData))
        );

        // 평균 정답률
        List<String> avgCorrectRateInfoItem = Arrays.asList("avgCorrectRate");
        taskReportSummaryInfo.put("avgCorrectRate",
                AidtCommonUtil.filterToMap(avgCorrectRateInfoItem, homewkReportMapper.findAvgCorrectRateInfo(paramData))
        );

        return taskReportSummaryInfo;
    }

    /** 학생) 평가 리포트 요약 */
    public Object findStntTaskReportSummary(Map<String, Object> paramData) throws Exception {

        LinkedHashMap<Object, Object> stntTaskReportSummaryInfo = new LinkedHashMap<>();

        List<String> taskReportSummaryItem = Arrays.asList("eamExmNum", "taskPrgDt", "taskCpDt", "deadline", "solvingTime",
                "avgCorrectRate", "submAt", "submDt", "timTime"
        );
        stntTaskReportSummaryInfo.put("stntTaskReportSummary",
                AidtCommonUtil.filterToList(taskReportSummaryItem, homewkReportMapper.findStntTaskReportSummary(paramData))
        );

        paramData.put("wonAnwClsfCd", 3); /* 과제: 3, 평가: 4 */
        paramData.put("trgtId", Integer.parseInt(paramData.get("taskId").toString()));

        int wrongNoteCreatedCnt = evalReportMapper.selectWrongNoteCreatedAt(paramData);
        stntTaskReportSummaryInfo.put("wrongNoteAt", wrongNoteCreatedCnt > 0);

        return stntTaskReportSummaryInfo;
    }

    /** AI 보조 (평가를 응시하지 않은 학생 조회) */
    public Object findUnsubmittedStudents(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

        // 과제 미제출 학생 전체 조회
        List<Map> unsubmittedStudents = homewkReportMapper.findUnsubmittedStudentsByTaskId(paramData);

        // 전체 미제출자 수
        int totalCount = unsubmittedStudents.size();

        // 상위 3명만 추출
        List<Map> topThreeStudents = unsubmittedStudents.stream()
                .limit(3)
                .collect(Collectors.toList());

        resultMap.put("students", topThreeStudents);    // 상위 3명
        resultMap.put("totalCount", totalCount);        // 전체 미제출자 수 (3명 포함)

        return resultMap;
    }

    /** 리포트 과제 목록 조회 */
    @Transactional(readOnly = true)
    public ValidationResult findHomewkList(HomewkReportListReqDto paramData, Pageable pageable) throws Exception {

        if (!TaskDivision.isValidValue(paramData.getTaskDivision())) {
            return ValidationResult.fail("잘못된 과제 구분 값입니다. 허용값: " + TaskDivision.getAllowedValues());
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

        List<Map> taskInfoList = new ArrayList<>();
        Map<String, Object> latestTask = new HashMap<>();
        List<LinkedHashMap<Object, Object>> progressStatus = new ArrayList<>();
        List<String> progressItem = Arrays.asList("inProgressTaskCount", "completedTaskCount");

        if ("T".equals(loginUserInfo.get("userSeCd")) && !Objects.isNull(paramData.getStntId())) {
            //교사가 특정 학생의 리포트를 조회하는 경우
            taskInfoList = homewkReportMapper.findHomewkListForStudent(pagingParam);
            latestTask = homewkReportMapper.findLatestTaskForStudent(paramData);
            progressStatus = AidtCommonUtil.filterToList(progressItem, homewkReportMapper.findTaskkProgressCountForStudent(paramData)); // 과제 리포트 진행중, 종료 개수 조회

        } else if ("T".equals(loginUserInfo.get("userSeCd")) && Objects.isNull(paramData.getStntId())) {
            // 교사가 전체 학급의 리포트를 조회하는 경우
            taskInfoList = homewkReportMapper.findHomewkListForTeacher(pagingParam);
            latestTask = homewkReportMapper.findLatestTaskForTeacher(paramData);
            progressStatus = AidtCommonUtil.filterToList(progressItem, homewkReportMapper.findTaskkProgressCountForTeacher(paramData));

        } else if ("S".equals(loginUserInfo.get("userSeCd"))) {
            // 학생이 자신의 리포트를 조회하는 경우
            taskInfoList = homewkReportMapper.findHomewkListForStudent(pagingParam);
            latestTask = homewkReportMapper.findLatestTaskForStudent(paramData);
            progressStatus = AidtCommonUtil.filterToList(progressItem, homewkReportMapper.findTaskkProgressCountForStudent(paramData));
        }

        long total = taskInfoList.isEmpty() ? 0 : (long) taskInfoList.get(0).get("fullCount");
        List taskList = taskInfoList.stream()
                .map(LinkedHashMap::new)
                .toList();
        PagingInfo page = AidtCommonUtil.ofPageInfo(taskInfoList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        if(!ObjectUtils.isEmpty(latestTask)) {
            latestTask.put("taskDivision", paramData.getTaskDivision());
            returnMap.put("latestTask", latestTask);
        }

        returnMap.put("progressStatus", progressStatus);
        returnMap.put("taskList", taskList);
        returnMap.put("page",page);

        return ValidationResult.success(returnMap);
    }


    /** 우리반 과제 채점 결과표 조회 */
    @Transactional(readOnly = true)
    public Object findReportHomewkResultList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // stntId 파라미터가 있는 경우 추출
        String filterStntId = Optional.ofNullable(paramData.get("stntId"))
                .map(Object::toString)
                .orElse(null);

        // 학생과제결과정보 main
        Map<String, Object> result = (Map<String, Object>) tchReportHomewkMapper.findReportHomewkResultList_main(paramData);

        // 학생 과제 정오표 정보
        List<Map<String, Object>> stntTaskErrataInfList = homewkReportMapper.findReportHomewkResultList_stntTaskErrataInfList(paramData);

        List<Map<String, Object>> mdulTaskInfoList = new ArrayList<>();

        /*
         * 교사 > 리포트 > AI 처방학습 경우에만 적용
         * 처방학습 값 eamMth: 4
         * */
        int eamMth = tchReportHomewkMapper.findEamMthInTaskInfo(String.valueOf(paramData.get("taskId")));

        List<Map<String, Object>> studentList = new ArrayList<>();
        if (null == filterStntId && 4 == eamMth) {
            /*
            * 학생 별 문항 수 추출
            * */
            for (Map<String, Object> stntInfo : stntTaskErrataInfList) {
                Map<String, Object> studentModulMap = new HashMap<>();
                String stntId = String.valueOf(stntInfo.get("userId"));

                paramData.put("stntId", stntId);

                // 학생 별 조건 추가를 위해 새롭게 선언
                PagingParam<?> studentPagingParam = PagingParam.builder()
                    .param(paramData)
                    .pageable(pageable)
                    .build();
                List<Map<String, Object>> studentMdulList = homewkReportMapper.findReportHomewkResultList_mdulList(studentPagingParam);
                studentList.add(Map.of(
                        "stntId", stntId,
                        "name", String.valueOf(stntInfo.get("flnm")),
                        "modules", studentMdulList
                ));
                mdulTaskInfoList.addAll(studentMdulList);

                // 해당 조건문에서만 사용되므로 제거
                paramData.remove("stntId");
            }

        } else {
            mdulTaskInfoList = homewkReportMapper.findReportHomewkResultList_mdulList(pagingParam);
        }
        // 우리반 과제 채점 결과표 헤더 (모듈별 반평균 풀이시간, 정답률)
        List<Map<String, Object>> scoringClassResultIemList = homewkReportMapper.findScoringClassResultIemInfoHeader(pagingParam);

        // 반평균 풀이시간 포맷 변환
        for (Map<String, Object> resultMap : scoringClassResultIemList) {
            String avgSolvingTime = (String) resultMap.get("classAvgSolvingTime");
            if (avgSolvingTime != null) {
                resultMap.put("classAvgSolvingTime", formatSolvingTime(avgSolvingTime));
            }
        }

        List<Map<String, Object>> allStntTaskInfoList = new ArrayList<>();

        // AI 처방학습 파라미터 추가
        if (null == filterStntId && 4 == eamMth) {
            paramData.put("eamMth", eamMth);
            int mdulTaskInfoListIndex = 0;
            List<List<Map<String, Object>>> studentsTaskInfoList = new ArrayList<>();
            List<Map<String, Object>> taskIemInfoListAll = new ArrayList<>();
            for (Map<String, Object> studentInfo : studentList) {
                // 학생 ID 조회
                String stntId = studentInfo.get("stntId").toString();
                List<Map<String, Object>> taskIemInfoList = new ArrayList<>();

                // 학생 ID 일치 검사
                for (; mdulTaskInfoListIndex < mdulTaskInfoList.size(); mdulTaskInfoListIndex++) {
                    Map<String, Object> module = mdulTaskInfoList.get(mdulTaskInfoListIndex);
                    // 일치하는 경우 조회 파라미터로 추가
                    if(module.get("mamoymId").equals(stntId)) {
                        // taskIemId와 subId를 수집
                        Map<String, Object> taskIemInfo = new HashMap<>();
                        taskIemInfo.put("taskIemId", module.get("taskIemId"));
                        taskIemInfo.put("subId", module.get("subId"));
                        taskIemInfoList.add(taskIemInfo);
                    } else {
                        break;
                    }
                }
                paramData.put("stntId", stntId);
                paramData.put("taskIemInfoList", taskIemInfoList);
                taskIemInfoListAll.addAll(taskIemInfoList);
                studentsTaskInfoList.add(tchReportHomewkMapper.findReportHomewkResultList_allStntList(paramData));
            }

            studentsTaskInfoList.forEach(allStntTaskInfoList::addAll);
            paramData.remove("stntId");
            paramData.put("taskIemInfoList", taskIemInfoListAll);
        } else {
            allStntTaskInfoList = tchReportHomewkMapper.findReportHomewkResultList_allStntList(paramData);
        }

        // 모듈별로 학생 정보 그룹화
        for (Map<String, Object> module : mdulTaskInfoList) {
            Object taskIemId = module.get("taskIemId");
            Object subId = module.get("subId");

            // 해당 모듈에 속하는 학생 정보만 필터링
            List<Map<String, Object>> stntTaskInfoList = allStntTaskInfoList.stream()
                .filter(map -> {
                    boolean conditionCheck = taskIemId.equals(map.get("taskIemId")) && subId.equals(map.get("subId"));

                    // AI 처방 학습 경우에 추가 확인
                    if(null == filterStntId && 4 == eamMth) {
                        conditionCheck = conditionCheck && module.get("mamoymId").equals(map.get("userId"));
                    }
                    return conditionCheck;
                })
                .collect(Collectors.toList());

            // 해당 taskIemId와 매칭되는 반 평균 데이터 구하기
            scoringClassResultIemList.stream()
                    .filter(resultMap -> resultMap.get("taskIemId").equals(taskIemId))
                    .findFirst()
                    .ifPresent(scoringResult -> {
                        module.put("classAvgCorrectRate", scoringResult.get("avgCorrectRate"));
                        module.put("classAvgSolvingTime", scoringResult.get("classAvgSolvingTime"));
                    });

            module.put("stntTaskInfoList", stntTaskInfoList);
            module.remove("avgCorrectRate");
        }

        List<Map> castedList = (List<Map>) (List<?>) scoringClassResultIemList;

        // AI 처방학습의 경우 총 문항수가 가장 높은 값을 사용
        long total;
        if (null == filterStntId && 4 == eamMth) {

            total = stntTaskErrataInfList.stream()
                .filter(map -> map.get("totalQuestionNum") != null)
                .mapToLong(map -> {
                    Object count = map.get("totalQuestionNum");
                    if (count instanceof Number) {
                        return ((Number) count).longValue();
                    }
                    return 0L;
                })
                .max()
                .orElse(0L);

        } else {

            total = scoringClassResultIemList.isEmpty() ? 0 : (long) scoringClassResultIemList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(castedList, pageable, total);

        // stntId 파라미터가 있는 경우, 최종 출력 전에 학생 과제 정오표 정보도 필터링
        if (filterStntId != null) {
            stntTaskErrataInfList = stntTaskErrataInfList.stream()
                .filter(map -> filterStntId.equals(map.get("userId")))
                .collect(Collectors.toList());
        }

        // AI 처방학습이고 학생 ID가 null인 경우 totalQuestionNum 제거
        if (null == filterStntId && 4 == eamMth) {
            for (Map<String, Object> stntInfo : stntTaskErrataInfList) {
                stntInfo.remove("totalQuestionNum");
            }
        }

        result.put("page", page);
        result.put("mdulTaskInfoList", mdulTaskInfoList);
        result.put("stntTaskErrataInfList", stntTaskErrataInfList);

        return result;
    }

    /**
     * 풀이시간 포맷 변환
     * HH:MM:SS -> MM:SS 또는 HH:MM:SS
     */
    private String formatSolvingTime(String time) {
        if (time == null || time.isEmpty()) {
            return "00:00";
        }

        String[] parts = time.split(":");
        if (parts.length != 3) {
            return time;
        }

        int hours = Integer.parseInt(parts[0]);
        String minutes = parts[1];
        String seconds = parts[2];

        // 시간이 0인 경우 분:초만 표시
        if (hours == 0) {
            return String.format("%s:%s", minutes, seconds);
        }

        // 시간이 있는 경우 시:분:초 표시
        return time;
    }

    private Double calculateStudentAvgScore(Map<String, Object> studentData) {
        // errata 값에 따른 점수 계산
        // 1: 정답 (100%), 3: 부분점수 (50%), 그 외: 0%
        Integer errata = (Integer) studentData.get("errata");
        if (errata == null) return 0.0;

        switch(errata) {
            case 1: return 100.0;
            case 3: return 50.0;
            default: return 0.0;
        }
    }

    /** 교사) 과제 리포트 확인 여부 변경 */
    public Map<String, Object> changeReportCheckAt(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        boolean isSuccess = true;
        try {
            int updateResult = homewkReportMapper.modifyTeacherTaskReportChkAt(paramData);
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

    /** 과제리포트 독려알림 전송 */
    public Map<String, Object> sendTaskEncouragementNotification(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            // 독려 알림 대상 학생 목록 조회
            List<Map> targetStudents = homewkReportMapper.selectTaskEncouragementTargets(paramData);

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

            // 조회된 첫 번째 학생의 데이터에서 마감일, 과제id 가져오기
            int deadline = Integer.parseInt(targetStudents.get(0).get("deadline").toString());
            int taskId = Integer.parseInt(targetStudents.get(0).get("taskId").toString());

            // 학생 알림 전송
            String stntNtcnCn = deadline == 0
                ? "과제의 마감일입니다. 마감 시간에 늦지 않도록 제출해 주세요!"
                : String.format("과제의 마감일이 %d일 남았습니다. 마감 시간에 늦지 않도록 제출해 주세요!", deadline);
            paramData.put("stntNtcnCn", stntNtcnCn);
            paramData.put("targetType", "task");
            paramData.put("targetId", taskId);
            paramData.put("targetStudents", targetStudents);
            int stntInsertResult = evalReportMapper.insertStntEncouragementNotification(paramData);

            // 교사 알림 전송
            String tchNtcnCn = deadline == 0
                ? String.format("미제출한 %d명의 학생들에게 독려 알림 '과제의 마감일입니다.'를 보냈습니다.", targetStudents.size())
                : String.format("미제출한 %d명의 학생들에게 독려 알림 '과제의 마감일이 %d일 남았습니다.'를 보냈습니다.",
                    targetStudents.size(), deadline);
            paramData.put("trgetTyCd", 6); // 대상유형코드 = 6: 과제리포트, 7: 평가 리포트
            paramData.put("tchNtcnCn", tchNtcnCn);
            paramData.put("targetStudent", targetStudents.get(0));
            evalReportMapper.insertTchEncouragementNotification(paramData);

            boolean isSuccess = stntInsertResult > 0;
            returnMap.put("resultOk", isSuccess);
            returnMap.put("resultMsg", isSuccess ? "저장완료" : "저장실패");

            if (isSuccess) {
                returnMap.put("stntNtcnCn", stntNtcnCn);
            }
        } catch (Exception e) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }
            return returnMap;
    }


    public Map<String, Object> createTchReportHomewkGeneralReviewSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        List<String> studentIds = (List<String>) paramData.get("stntId");

        boolean isSuccess = true;

        paramData.put("mdfr",
                Optional.ofNullable(paramData.get("tchId"))
                    .map(Object::toString)
                    .orElse("")
        );

        try {
            for (String studentId : studentIds) {
                paramData.put("userId", studentId);
                int updateResult = homewkReportMapper.updateTchReportHomewkReviewSave(paramData);
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
}
