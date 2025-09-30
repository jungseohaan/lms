package com.visang.aidt.lms.api.mq.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.configuration.NatsTopicConfig;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.mq.dto.assessment.AssessmentSubmittedMqDto;
import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentFinishedMqDto;
import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentGaveMqDto;
import com.visang.aidt.lms.api.mq.dto.media.MediaDto;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskInfo;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskMqDto;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import com.visang.aidt.lms.api.mq.dto.teaching.ReorganizedInfo;
import com.visang.aidt.lms.api.mq.dto.teaching.TeachingReorganizedMqDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.StdLessonReconMapper;
import com.visang.aidt.lms.api.mq.service.*;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"math-stg","math-prod-job","engl-prod-job","math-beta-job","engl-beta-job","math-beta2-job","engl-beta2-job","math-release-job","engl-release-job","beta-2e-math-job","beta-2e-engl-job","vs-math-develop-job","vs-engl-develop-job","vs-math-prod-job","vs-engl-prod-job"})
public class MessageQueueBatchJob {
    /*
    * TODO: testStartTime,testEndTime 테스트 후 삭제
    * */

    private final ObjectMapper objectMapper;
    private final NatsSendService natsSendService;
    private final NatsTopicConfig natsTopicConfig;
    private final AssessmentSubmittedService assessmentSubmittedService;
    private final AssignmentGaveService assignmentGaveService;
    private final AssignmentFinishedService assignmentFinishedService;
    private final MediaPlayedService mediaPlayedService;
    private final QueryAskService queryAskService;
    private final StdLessonReconMapper stdLessonReconMapper;
    private final TeachingReorganizedService teachingReorganizedService;
    private final BtchExcnLogService btchExcnLogService;

    private final String BATCH_NAME = "MessageQueueBatchJob.sendBulkMessageQueue";

    @Loggable
    @Scheduled(cron = "${batch-job.schedule.MessageQueueBatchJob.sendBulkMessageQueue:0 0 2 * * ?}")
    public void sendBulkMessageQueue() throws Exception {
        log.info("MessageQueueBatchJob > MessageQueueBatchJob() : currentTime={}", LocalDateTime.now());

        // 배치 실행 전 정보 확인
        Map<String, Object> batchInfo = btchExcnLogService.checkBatchInfoExist(BATCH_NAME);
        if(Boolean.FALSE.equals(batchInfo.get("resultOk"))){
            log.info("MessageQueueBatchJob > MessageQueueBatchJob() : status={}", "batchInfo is empty");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Integer btchId = Integer.valueOf(batchInfo.get("btchId").toString());
        // 작일 02시 ~ 금일 02시
        LocalDateTime eventEndTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 2, 0, 0);
        LocalDateTime eventStartTime = eventEndTime.minusDays(1);

        Map<String, Object> testParamData = Map.of(
                "eventStartTime", eventStartTime,
                "eventEndTime", eventEndTime
        );

        sendMessageQueue(testParamData, btchId); // 메세지큐 전송
    }

    private void sendMessageQueue(Map<String, Object> testParamData, int btchId) throws Exception {
        // 작업 목록
        Map<String, Callable<Integer>> tasks = Map.of(
                "assessmentSubmitted", this::sendBulkAssessmentSubmitted,
                "assignmentGave", () -> sendBulkAssignmentGave(testParamData),
                "assignmentFinished", () -> sendBulkAssignmentFinished(testParamData),
                "mediaPlayed", () -> sendBulkMediaPlayed(testParamData),
                "queryAsk", () -> sendBulkQueryAsk(testParamData),
                "teachingReorganized", () -> sendBulkTeachingReorganized(testParamData)
        );

        // 각 작업 실행 및 결과 처리
        for (Map.Entry<String, Callable<Integer>> task : tasks.entrySet()) {
            String taskName = task.getKey();
            try {
                int result = task.getValue().call(); // 작업 실행
                saveBatchDetails(Map.of("resultOk", true, "btchExcnRsltCnt", result), btchId, taskName);
            } catch (NullPointerException e) {
                log.error("Error in {} - NullPointerException: ", taskName, e);
                String failureMessage = CustomLokiLog.errorLog(e).substring(0, 100);
                saveBatchDetails(Map.of("resultOk", false, "failDc", failureMessage), btchId, taskName);
            } catch (IllegalArgumentException e) {
                log.error("Error in {} - IllegalArgumentException: ", taskName, e);
                String failureMessage = CustomLokiLog.errorLog(e).substring(0, 100);
                saveBatchDetails(Map.of("resultOk", false, "failDc", failureMessage), btchId, taskName);
            } catch (InterruptedException e) {
                log.error("Error in {} - InterruptedException: ", taskName, e);
                String failureMessage = CustomLokiLog.errorLog(e).substring(0, 100);
                saveBatchDetails(Map.of("resultOk", false, "failDc", failureMessage), btchId, taskName);
            } catch (DataAccessException e) {
                log.error("Error in {} - DataAccessException: ", taskName, e);
                String failureMessage = CustomLokiLog.errorLog(e).substring(0, 100);
                saveBatchDetails(Map.of("resultOk", false, "failDc", failureMessage), btchId, taskName);
            } catch (RuntimeException e) {
                log.error("Error in {} - RuntimeException: ", taskName, e);
                String failureMessage = CustomLokiLog.errorLog(e).substring(0, 100);
                saveBatchDetails(Map.of("resultOk", false, "failDc", failureMessage), btchId, taskName);
            } catch (Exception e) {
                log.error("Error in {}: ", taskName, e);
                String failureMessage = CustomLokiLog.shortErrorLog(e, 0, 100);
                saveBatchDetails(Map.of("resultOk", false, "failDc", failureMessage), btchId, taskName);
            }
        }

        log.info("MessageQueueBatchJob > MessageQueueBatchJob() > batchWorkCompletedTime={}", LocalDateTime.now());
    }

    private void saveBatchDetails(Map<String, Object> messageQueueInfo, Integer btchId, String taskName) throws Exception {
        String prefixLog = "messageQueue: " + taskName + ": ";
        Map<String, Object> batchparamData = new HashMap<>();

        batchparamData.put("btchId", btchId);
        batchparamData.put("failDc", prefixLog + "processing");
        btchExcnLogService.createBtchDetailInfo(batchparamData);

        boolean resultOk = MapUtils.getBooleanValue(messageQueueInfo, "resultOk");    // 정상 작동여부 확인
        int btchExcnRsltCnt = MapUtils.getIntValue(messageQueueInfo, "btchExcnRsltCnt"); // 배치실행결과 건수
        String failDc = prefixLog + (resultOk ? btchExcnRsltCnt + " processed" : MapUtils.getString(messageQueueInfo, "failDc"));

        batchparamData.put("btchExcnRsltCnt", btchExcnRsltCnt); // 배치실행결과건수(실행후)
        batchparamData.put("btchRsltAt", resultOk ? "Y" : "N"); // 배치결과여부(실행후)
        batchparamData.put("failDc", failDc); // 실패사유(textbkId 저장시에도 활용)
        btchExcnLogService.modifyBtchExcnLog(batchparamData);
    }


    /** bulk 평가결과 전송 */
    @Loggable
    private Integer sendBulkAssessmentSubmitted() throws Exception {
        List<AssessmentSubmittedMqDto> resultList = assessmentSubmittedService.createAssessmentResultRequest(RealMqReqDto.builder().build());
        sendToNats(resultList);
        return assessmentSubmittedService.modifyEvlMqTrnAt();
    }


    /**과제등록*/
    @Loggable
    private Integer sendBulkAssignmentGave(Map<String, Object> paramData) throws Exception {
        RealMqReqDto realMqReqDto = RealMqReqDto.builder()
                .startTime(paramData.get("eventStartTime").toString())
                .endTime(paramData.get("eventEndTime").toString())
                .build();

        List<AssignmentGaveMqDto> resultData = assignmentGaveService.createAssignmentGaveMq(realMqReqDto);
        sendToNats(resultData);
        return assignmentGaveService.updateBulkTaskMqTrnLog();
    }


    /**과제제출*/
    @Loggable
    private Integer sendBulkAssignmentFinished(Map<String, Object> paramData) throws Exception {
        RealMqReqDto realMqReqDto = RealMqReqDto.builder()
                .startTime(paramData.get("eventStartTime").toString())
                .endTime(paramData.get("eventEndTime").toString())
                .build();

        List<AssignmentFinishedMqDto> resultData = assignmentFinishedService.createAssignmentFinishedMq(realMqReqDto);
        sendToNats(resultData);
        return assignmentFinishedService.updateAssignmentFinishedSendAt();
    }


    /**미디어 제출*/
    @Loggable
    private Integer sendBulkMediaPlayed(Map<String, Object> paramData) throws Exception {
        String eventStartTime = paramData.get("eventStartTime").toString();
        String eventEndTime = paramData.get("eventEndTime").toString();

        List<MediaDto> mediaDtoList = mediaPlayedService.createMediaPlayedMq(eventStartTime, eventEndTime, "");
        sendToNats(mediaDtoList);
        mediaPlayedService.modifyMediaPlayedUpdate(eventStartTime, eventEndTime);
        return mediaDtoList.size();
    }


    /**학생) 질의*/
    @Loggable
    private Integer sendBulkQueryAsk(Map<String, Object> paramData) throws Exception {
        String eventStartTime = paramData.get("eventStartTime").toString();
        String eventEndTime = paramData.get("eventEndTime").toString();

        QueryAskInfo queryAskInfo = QueryAskInfo.builder()
                .startTime(eventStartTime)
                .endTime(eventEndTime)
                .trnAt("N")
                .build();

        List<QueryAskMqDto> resultList = queryAskService.createQueryAskMq(queryAskInfo);
        sendToNats(resultList);

        return queryAskService.modifyQueryAskTrnAt(queryAskInfo);
    }


    /** 교수활동 (수업재구성)*/
    @Loggable
    private int sendBulkTeachingReorganized(Map<String, Object> paramData) throws Exception {
        String eventStartTime = paramData.get("eventStartTime").toString();
        String eventEndTime = paramData.get("eventEndTime").toString();

        ReorganizedInfo reorganizedInfo = ReorganizedInfo.builder()
                .startTime(eventStartTime)
                .endTime(eventEndTime)
                .build();

        List<TeachingReorganizedMqDto> resultList = teachingReorganizedService.createTeachingReorganized(reorganizedInfo);
        sendToNats(resultList);
        stdLessonReconMapper.updateStdMqTrnLog(reorganizedInfo);
        return resultList.size();
    }


    /** 공통 메서드: JSON 변환 및 메세지큐 전송 */
    private <T> void sendToNats(List<T> resultList) throws Exception {
        if (resultList == null || resultList.isEmpty()) {
            return;
        }
        for (T resultData : resultList) {
            String jsonData = objectMapper.writeValueAsString(resultData);
            natsSendService.pushNatsMQ(natsTopicConfig.getBulkSendName(), jsonData);
        }
    }


}
