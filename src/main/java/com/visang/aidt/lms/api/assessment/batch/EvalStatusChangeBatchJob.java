package com.visang.aidt.lms.api.assessment.batch;

import com.visang.aidt.lms.api.assessment.mapper.EvlStatusChangeMapper;
import com.visang.aidt.lms.api.assessment.service.TchReportEvalService;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.mq.service.AssessmentSubmittedService;
import com.visang.aidt.lms.api.mq.service.AssignmentGaveService;
import com.visang.aidt.lms.api.report.service.EvalReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"math-dev","math-stg","engl-prod-job","math-prod-job","math-beta-job","engl-beta-job","math-beta2-job","engl-beta2-job","math-release-job","engl-release-job","vs-dev-job","vs-stg-job","vs-prod-job","beta-2e-math-job","beta-2e-engl-job","vs-math-develop-job","vs-engl-develop-job","vs-math-prod-job","vs-engl-prod-job"})
public class EvalStatusChangeBatchJob {

    private final EvlStatusChangeMapper evlStatusChangeMapper;
    private final TchReportEvalService tchReportEvalService;
    private final EvalReportService evalReportService;
    private final AssessmentSubmittedService assessmentSubmittedService;
    private final AiLearningMapper aiLearningMapper;
    private final AssignmentGaveService assignmentGaveService;

    /**
     * 평가 상태를 '진행 중'으로 변경하는 배치 작업
     *
     * @throws Exception 배치 작업 중 오류 발생 시
     */
    @Scheduled(cron = "${batch-job.schedule.EvalStatusChangeBatchJob.executeTasksInProgress}")
    @Async("statusChangeExecutor") // 추가된 부분
    public void executeTasksInProgress() throws Exception {
        log.info("EvalStatusChangeBatchJob > executeTasksInProgress()");

        try {
            // 평가 상태 진행중 변경
            this.changeEvalStatusToInProgress();

        } catch (Exception e) {
             throw e; // 또는 다른 에러 처리 로직
        }
    }

    /**
     * 평가 상태를 '완료'로 변경하고 관련 테이블을 정리하는 배치 작업
     *
     * @throws Exception 배치 작업 중 오류 발생 시
     */
    @Scheduled(fixedDelayString = "${batch-job.schedule.EvalStatusChangeBatchJob.executeTasksToComplete}")
    @Async("statusChangeExecutor") // 추가된 부분
    public void executeTasksToComplete() throws Exception {
        log.info("EvalStatusChangeBatchJob > executeTasksToComplete()");

        // 평가 상태 완료로 변경
        this.changeEvalStatusToCompleted();
    }

    /**
     * 처방 과제 출제 배치 작업
     */
    @Scheduled(fixedDelayString = "${batch-job.schedule.EvalStatusChangeBatchJob.executePrescriptionTasks:300000}")
    @Async("prescriptionExecutor") // 별도 스레드 풀 사용
    public void executePrescriptionTasks() throws Exception {
        log.info("EvalStatusChangeBatchJob > executePrescriptionTasks()");

        // 처방 과제 출제
        this.createPrescriptionTasks();
    }

    /**
     * 평가 상태를 '진행 중'으로 변경
     *
     * @throws Exception 상태 업데이트 중 오류 발생 시
     */
    private void changeEvalStatusToInProgress() throws Exception {
        log.info("EvalStatusChangeBatchJob > changeEvalStatusToInProgress()");

        this.evlStatusChangeMapper.updateEvlStatusChangeToInProgress();
    }

    /**
     * 평가 상태를 '완료'로 변경하고, 미제출 평가 데이터 정리
     *
     * @throws Exception 상태 업데이트 또는 데이터 정리 중 오류 발생 시
     */
    public void changeEvalStatusToCompleted() throws Exception {

        // 평가 상태 완료 처리
        this.evlStatusChangeMapper.updateEvlStatusChangeToComplete();

        // 2024-07-08
        // 미제출자 중에 답안제출 이력이 있는 학생 제출처리

        // 배치 수정 시 제출하기 api 도 수정이 필요 할 수 있습니다. (StntEvalService.modifyStntEvalSubmit)
        List<Map<String, Object>> evlSubmAtNlist = evlStatusChangeMapper.findEvlSubmAtN();

        if (CollectionUtils.isNotEmpty(evlSubmAtNlist)) {
            // 미제출자 처리
            // update evl_result_detail
            evlStatusChangeMapper.modifyStntEvalSubmitResultDetail(evlSubmAtNlist);

            // update evl_result_info
            evlStatusChangeMapper.modifyStntEvalSubmitResultInfo(evlSubmAtNlist);

            // 리워드 지급
            // insert rwd_earn_hist
            evlStatusChangeMapper.createRwdEarnHist(evlSubmAtNlist);

            // 리워드 조회 후 insert, update 분리
            List<Map<String, Object>> rwdEarnInfoList = evlStatusChangeMapper.findRwdEarnInfo(evlSubmAtNlist);

            List<Integer> createRwdEarnInfoList = rwdEarnInfoList.stream()
                    .filter(map -> MapUtils.getInteger(map, "rwdEarnInfoId", 0) == 0)
                    .map(map -> MapUtils.getInteger(map, "resultInfoId"))
                    .toList();

            List<Integer> updateRwdEarnInfoList = rwdEarnInfoList.stream()
                    .filter(map -> MapUtils.getInteger(map, "rwdEarnInfoId", 0) != 0)
                    .map(map -> MapUtils.getInteger(map, "rwdEarnInfoId"))
                    .toList();

            // insert rwd_earn_info
            if (CollectionUtils.isNotEmpty(createRwdEarnInfoList)) {
                evlStatusChangeMapper.createRwdEarnInfo(createRwdEarnInfoList);
            }

            // update rwd_earn_info
            if (CollectionUtils.isNotEmpty(updateRwdEarnInfoList)) {
                evlStatusChangeMapper.modifyRwdEarnInfo(updateRwdEarnInfoList);
            }

            // 리워드 지급 알림
            // insert ntcn_info
            evlStatusChangeMapper.createNtcnInfo(evlSubmAtNlist);
        }

        // 평가 미제출자 목록 조회
        List<Map> entityList = this.evlStatusChangeMapper.findEvlResultInfoNotSubmitted();
        if (!entityList.isEmpty()) {
//            for (Map entity : entityList) {
//                this.evlStatusChangeMapper.updateEvlResultDetailFinalProcess(entity.get("id").toString());
//            }

            // 개별 루프 처리 대신 Bulk로 처리
            List<String> ids = entityList.stream()
                    .map(entity -> entity.get("id").toString())
                    .toList();

            this.evlStatusChangeMapper.bulkUpdateEvlResultDetailFinalProcess(ids);

            // evl_result_info 테이블 정리
            this.cleanupEvlResultInfo(entityList);
        }

        // 리포트 자동 공개 처리
        // 리포트 자동 공유 여부가 Y 인 경우. 리포트 공유 호출
        List<Map<String, Object>> evlAutoRptList = evlStatusChangeMapper.findEvlAutoRptList();
        for (Map<String, Object> m : evlAutoRptList) {
            tchReportEvalService.modifyReportEvalOpen(m);
            assessmentSubmittedService.insertAssessmentInfo(m);

        }

        // 평가의 모든 학생의 응시상태가 채점완료(5)인 경우 evl_info 상태 (완료 -> 채점완료) 변경
        this.cleanupEvlInfo();
    }

    /**
     * 처방 과제 출제
     */
    private void createPrescriptionTasks() throws Exception {
        log.info("EvalStatusChangeBatchJob > createPrescriptionTasks()");

        List<Map<String, Object>> evlTargetList = evlStatusChangeMapper.selectrCeateAiPrscrEvlToTaskTarget();

        if (CollectionUtils.isEmpty(evlTargetList)) {
            log.info("처방 과제 출제 대상이 없습니다.");
            return;
        }

        log.info("처방 과제 출제 대상 수: {}", evlTargetList.size());

        int successCount = 0;
        int failureCount = 0;

        for (Map<String, Object> item : evlTargetList) {
            if (MapUtils.isEmpty(item)) {
                continue;
            }

            try {
                // 초등 영어일 경우
                if (Stream.of("초등", "영어").allMatch(
                        val -> MapUtils.getString(item, "val", "").contains(val))) {
                    // 총괄평가(evl_se_cd = 3)일 경우만 출제
                    if (MapUtils.getInteger(item, "evlSeCd", 0) == 3) {
                        // 처방 과제 출제 진행
                        evalReportService.createAiPrscrEvlToTask(
                                new LinkedHashMap<>(
                                        Map.of("evlId", MapUtils.getInteger(item, "id", 0)))
                        );
                        successCount++;
                    } else {
                        // 대상이 아닐 경우
                        Map<String, Object> paramData = new LinkedHashMap<>(
                                Map.of(
                                        "evlId", MapUtils.getInteger(item, "id", 0),
                                        "prscrStdCrtAt", "X",
                                        "creator", MapUtils.getString(item, "wrterId", "")
                                )
                        );

                        try {
                            // 제외 한 대상은 조회 되지 않도록 조치
                            aiLearningMapper.updateAfterCreateEvlInfo(paramData);
                        } catch (Exception e) {
                            log.error("오류 발생 평가 ID : {}", MapUtils.getInteger(item, "id", 0));
                            log.error("AI 처방 과제 제외 대상 처리 중 오류 : {}", e.getMessage());
                        }
                    }
                } else {
                    // 초등 영어가 아닌 경우 처방 과제 출제
                    evalReportService.createAiPrscrEvlToTask(
                            new LinkedHashMap<>(
                                    Map.of("evlId", MapUtils.getInteger(item, "id", 0)))
                    );
                    successCount++;
                }
            } catch (Exception e) {
                failureCount++;
                log.error("처방 과제 출제 실패 - 평가 ID: {}, 오류: {}",
                        MapUtils.getInteger(item, "id", 0), e.getMessage());
            }
        }

        log.info("처방 과제 출제 완료 - 성공: {}, 실패: {}", successCount, failureCount);

        if (successCount > 0) {
            try {
                /* 처방 과제 출제 완료된 데이터 조회 */
                List<String> taskIds = aiLearningMapper.selectBulkTaskMqTarget();
                if (CollectionUtils.isNotEmpty(taskIds)) {
                    assignmentGaveService.insertBulkTaskMqTrnLog(taskIds);
                    log.info("MQ 로그 처리 완료 - 대상 과제 수: {}", taskIds.size());
                }
            } catch (Exception e) {
                log.error("MQ 로그 처리 중 오류 발생: {}", e.getMessage());
            }
        }
    }

    /**
     * evl_result_info 테이블에서 완료된 평가 정보 정리
     *
     * @param entityList 미제출 평가 정보 목록
     * @throws Exception 테이블 정리 중 오류 발생 시
     */
    private void cleanupEvlResultInfo(List<Map> entityList) throws Exception {
        List<Map<String, Object>> uniqueEntityList = entityList.stream()
                .collect(Collectors.groupingBy(
                        map -> map.get("evlResultId"),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.get(0)
                        )
                ))
                .values().stream()
                .map(obj -> (Map<String, Object>) obj)
                .toList();
//        for (Map<String, Object> entity : uniqueEntityList) {
//            this.evlStatusChangeMapper.updateEvlResultFinalProcesss(entity.get("evlResultId").toString());
//        }

        // 개별 루프 처리 대신 Bulk로 처리
        List<String> evlResultIds = uniqueEntityList.stream()
                .map(task -> task.get("evlResultId").toString())
                .toList();

        this.evlStatusChangeMapper.bulkUpdateEvlResultFinalProcess(evlResultIds);

        // evl_info 테이블 정리
        // this.cleanupEvlInfo();
    }

    /**
     * evl_info 테이블에서 완료된 평가 정보 정리
     *
     * @throws Exception 테이블 정리 중 오류 발생 시
     */
    @Transactional
    public void cleanupEvlInfo() throws Exception {
        // 평가결과정보 (evl_result_info) 상태 완료 처리
        this.evlStatusChangeMapper.updateEvlResultStatusChangeToComplete();

        // 평가결과정보 (evl_result_info) 상태 완료 처리
        // result_detail 의 상태가 모두 5인 경우 and result_info 의 subm_at 값이 Y 인 경우
        // result_info 의 상태 값 5로 변경
        this.evlStatusChangeMapper.updateEvlResultStatusChangeToComplete2();

        // 평가 상태 최종 완료(5)로 변경 후 평가 id 반환
        this.evlStatusChangeMapper.updateEvlInfoFinalProcess();

        String updatedIds = this.evlStatusChangeMapper.findUpdatedEvlIds();
        // 최종 완료된 평가 id로 리포트 확인여부 초기화
        if (updatedIds != null && !updatedIds.isEmpty()) {
            List<String> evlIds = Arrays.asList(updatedIds.split(","));
            this.evlStatusChangeMapper.modifyTchEvalReportChkAtOnCompleteList(evlIds);
        }
    }

}