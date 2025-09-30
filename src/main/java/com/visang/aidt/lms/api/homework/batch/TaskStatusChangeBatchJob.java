package com.visang.aidt.lms.api.homework.batch;

import com.visang.aidt.lms.api.homework.mapper.TaskStatusChangeMapper;
import com.visang.aidt.lms.api.homework.service.TchReportHomewkService;
import com.visang.aidt.lms.api.mq.service.AssignmentGaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"math-dev","math-stg","engl-prod-job","math-prod-job","math-beta-job","engl-beta-job","math-beta2-job","engl-beta2-job","math-release-job","engl-release-job","beta-2e-math-job","beta-2e-engl-job","vs-math-prod-job","vs-engl-prod-job","vs-math-develop-job","vs-engl-develop-job"})
public class TaskStatusChangeBatchJob {

    private final TaskStatusChangeMapper taskStatusChangeMapper;
    private final TchReportHomewkService tchReportHomewkService;
    private final AssignmentGaveService assignmentGaveService;

    /**
     * '진행 중' 상태로 과제 상태를 변경하는 스케줄러 작업
     *
     * @throws Exception 작업 상태 업데이트 중 오류가 발생한 경우
     */
    @Scheduled(cron="${batch-job.schedule.TaskStatusChangeBatchJob.executeTasksInProgress}")
    public void executeTasksInProgress() throws Exception {
        log.info("TaskStatusChangeBatchJob > executeTasksInProgress()");

        // 업데이트 전 데이터 가져오기
        List<String> taskIds = taskStatusChangeMapper.selectBulkTaskMqTarget();

        if (CollectionUtils.isNotEmpty(taskIds)) {
            taskStatusChangeMapper.updateTaskStatusChangeToInProgress(taskIds);

            // 업데이트 완료 후 mq 발송을 위한 테이블에 insert
            assignmentGaveService.insertBulkTaskMqTrnLog(taskIds);
        }
    }

    /**
     * '완료' 상태로 과제 상태를 변경하는 스케줄러 작업 및 추가 작업 처리
     *
     * @throws Exception 작업 상태 업데이트 또는 처리 중 오류가 발생한 경우
     */
    @Scheduled(fixedDelayString = "${batch-job.schedule.TaskStatusChangeBatchJob.executeTasksToComplete}")
    public void executeTasksToComplete() throws Exception {
        log.info("TaskStatusChangeBatchJob > executeTasksToComplete()");
        taskStatusChangeMapper.updateTaskStatusChangeToComplete();

        // 2024-07-08
        // 미제출자 중에 답안제출 이력이 있는 학생 제출처리

        // 배치 수정 시 제출하기 api 도 수정이 필요 할 수 있습니다. (StntHomewkService.modifyStntHomewkSubmit)
        List<Map<String, Object>> taskSubmAtNlist = taskStatusChangeMapper.findTaskSubmAtN();

        if (CollectionUtils.isNotEmpty(taskSubmAtNlist)) {
            // 미제출자 처리
            // update task_result_detail
            taskStatusChangeMapper.modifyStntTaskSubmitResultDetail(taskSubmAtNlist);

            // update task_result_info
            taskStatusChangeMapper.modifyStntTaskSubmitResultInfo(taskSubmAtNlist);

            // 리워드 지급
            // insert rwd_earn_hist
            taskStatusChangeMapper.createRwdEarnHist(taskSubmAtNlist);

            // 리워드 조회 후 insert, update 분리
            List<Map<String, Object>> rwdEarnInfoList = taskStatusChangeMapper.findRwdEarnInfo(taskSubmAtNlist);

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
                taskStatusChangeMapper.createRwdEarnInfo(createRwdEarnInfoList);
            }

            // update rwd_earn_info
            if (CollectionUtils.isNotEmpty(updateRwdEarnInfoList)) {
                taskStatusChangeMapper.modifyRwdEarnInfo(updateRwdEarnInfoList);
            }

            // 리워드 지급 알림
            // insert ntcn_info
            taskStatusChangeMapper.createNtcnInfo(taskSubmAtNlist);
        }

        List<Map> notSubmittedTasks = taskStatusChangeMapper.findTaskResultInfoNotSubmitted();
        if (!notSubmittedTasks.isEmpty()) {
//            for (Map task : notSubmittedTasks) {
//                taskStatusChangeMapper.updateTaskResultDetailFinalProcess(task.get("id").toString());
//            }

            // 개별 루프 처리 대신 Bulk로 처리
            List<String> taskIds = notSubmittedTasks.stream()
                    .map(task -> task.get("id").toString())
                    .toList();

            taskStatusChangeMapper.bulkUpdateTaskResultDetailFinalProcess(taskIds);

            cleanupTaskResultInfo(notSubmittedTasks);
        }

        // 리포트 자동 공개 처리
        // 리포트 자동 공유 여부가 Y 인 경우. 리포트 공유 호출
        List<Map<String, Object>> taskAutoRptList = taskStatusChangeMapper.findTaskAutoRptList();
        for (Map<String, Object> m : taskAutoRptList) {
            tchReportHomewkService.modifyReportTaskOpen(m);
        }

        // 과제의 모든 학생의 응시상태가 채점완료(5)인 경우 task_info 상태 (완료 -> 채점완료) 변경
        cleanupTaskInfo();
    }

    /**
     * task_result_info 테이블 정리 작업
     *
     * @param taskList 미제출 과제 목록
     * @throws Exception 테이블 정리 중 오류가 발생한 경우
     */
    private void cleanupTaskResultInfo(List<Map> taskList) throws Exception {
        List<Map<String, Object>> uniqueTaskList = taskList.stream()
                .collect(Collectors.groupingBy(
                        task -> task.get("taskResultId"),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.get(0)
                        )
                ))
                .values().stream()
                .map(task -> (Map<String, Object>) task)
                .toList();
//        for (Map<String, Object> task : uniqueTaskList) {
//            taskStatusChangeMapper.updateTaskResultFinalProcesss(task.get("taskResultId").toString());
//        }

        // 개별 루프 처리 대신 Bulk로 처리
        List<String> taskResultIds = uniqueTaskList.stream()
                .map(task -> task.get("taskResultId").toString())
                .toList();

        taskStatusChangeMapper.bulkUpdateTaskResultFinalProcess(taskResultIds);

        // cleanupTaskInfo();
    }

    /**
     * task_info 테이블 정리 작업
     *
     * @throws Exception 테이블 정리 중 오류가 발생한 경우
     */
    private void cleanupTaskInfo() throws Exception {
        // 과제결과정보 (evl_result_info) 상태 완료 처리
        taskStatusChangeMapper.updateTaskResultStatusChangeToComplete();

        taskStatusChangeMapper.updateTaskInfoFinalProcess();
    }
}
