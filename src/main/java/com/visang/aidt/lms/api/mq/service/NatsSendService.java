package com.visang.aidt.lms.api.mq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.configuration.NatsConfig;
import com.visang.aidt.lms.api.configuration.NatsLogConfig;
import com.visang.aidt.lms.api.mq.MessageConstants;
import com.visang.aidt.lms.api.mq.dto.real.*;
import com.visang.aidt.lms.api.mq.mapper.real.RealMessageQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class NatsSendService {

    @Value(value = "${spring.topic.realtime-send-name}")
    private String topicName;
    private final NatsConfig natsConfig;
    private final NatsLogConfig natsLogConfig;
    private final RealMessageQueueMapper realMessageQueueMapper;
    private final ObjectMapper objectMapper;
    
    public NatsSendService(NatsConfig natsConfig, NatsLogConfig natsLogConfig, 
                          @Lazy RealMessageQueueMapper realMessageQueueMapper, ObjectMapper objectMapper) {
        this.natsConfig = natsConfig;
        this.natsLogConfig = natsLogConfig;
        this.realMessageQueueMapper = realMessageQueueMapper;
        this.objectMapper = objectMapper;
    }
    
    // Circuit Breaker 패턴 구현을 위한 변수들
    private final AtomicBoolean natsCircuitOpen = new AtomicBoolean(false);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    
    // Circuit Breaker 설정값
    private static final long CIRCUIT_OPEN_DURATION_MS = 60000; // 1분
    private static final long MAX_FAILURE_COUNT = 5; // 5회 실패 시 Circuit 열림

    public void pushNatsMQ(String topicName, String data) throws Exception {
        natsConfig.natsConnection().publish(topicName, data.getBytes(StandardCharsets.UTF_8));
        log.info("pushNatsMQ topicName:{}\n", topicName);
    }

    public void pushNatsLogMQ(String topicName, String data) throws Exception {
        // Circuit Breaker 체크
        if (isCircuitOpen()) {
            log.debug("NATS Circuit is open, skipping log message");
            return;
        }

        if (StringUtils.isEmpty(topicName) || StringUtils.isEmpty(data)) {
            log.debug("NATS topic {} or message {} empty", topicName, data);
            return;
        }

        try {
            natsLogConfig.natsConnection().publish(topicName, data.getBytes(StandardCharsets.UTF_8));
            // 성공 시 Circuit Breaker 리셋
            resetCircuitBreaker();
//            log.info("pushNatsLogMQ topicName:{}\n", topicName);
        } catch (IllegalArgumentException | IllegalStateException e) {
            handleNatsFailure();
            throw new IllegalStateException("NATS connection not established.");
        } catch (RuntimeException e) {
            // 예상치 못한 런타임 예외 안전망
            handleNatsFailure();
            throw new IllegalStateException("NATS connection not established.");
        } catch (Exception e) {
            // 실패 시 Circuit Breaker 상태 업데이트
            handleNatsFailure();// 내부에서 오류 메세지 로깅
            throw new IllegalStateException("NATS connection not established.");
        }
    }
    
    /**
     * Circuit Breaker가 열려있는지 확인
     */
    private boolean isCircuitOpen() {
        if (!natsCircuitOpen.get()) {
            return false;
        }
        
        // Circuit이 열린 상태에서 일정 시간이 지나면 Half-Open 상태로 전환
        long currentTime = System.currentTimeMillis();
        long lastFailure = lastFailureTime.get();
        
        if (currentTime - lastFailure > CIRCUIT_OPEN_DURATION_MS) {
            log.info("NATS Circuit breaker transitioning to half-open state");
            natsCircuitOpen.set(false);
            return false;
        }
        
        return true;
    }
    
    /**
     * NATS 실패 처리
     */
    private void handleNatsFailure() {
        long failures = failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        if (failures >= MAX_FAILURE_COUNT) {
            natsCircuitOpen.set(true);
            log.warn("NATS Circuit breaker opened after {} failures. Circuit will remain open for {} ms", 
                     failures, CIRCUIT_OPEN_DURATION_MS);
        }
    }
    
    /**
     * Circuit Breaker 리셋 (연결 성공 시)
     */
    private void resetCircuitBreaker() {
        if (failureCount.get() > 0 || natsCircuitOpen.get()) {
            failureCount.set(0);
            natsCircuitOpen.set(false);
            log.info("NATS Circuit breaker reset after successful connection");
        }
    }

    public Object sendClassStartInfo(RealMqReqDto realMqReqDto)throws Exception {
        return sendClassInfo(realMqReqDto, "Start");
    }

    public Object sendClassFinishInfo(RealMqReqDto realMqReqDto)throws Exception {
        return sendClassInfo(realMqReqDto, "Finish");
    }

    private Object sendClassInfo(RealMqReqDto paramData, String callType)throws Exception {
        RealClassStartDto classStatusData = realMessageQueueMapper.findClassStatusByClaIdx(paramData);
        if (classStatusData == null) {
            var resultData = new LinkedHashMap<>();
            resultData.put("resultOk", false);
            resultData.put("resultMsg", "RealClassStartDto is Empty");
            return resultData;
        }

        String useTermsAgreeYn = ObjectUtils.defaultIfNull(classStatusData.getUseTermsAgreeYn(), "N");

        // date format 변경(UTC)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (StringUtils.isNotBlank(classStatusData.getTchOpenDate())) {
            LocalDateTime localOpenDateTime = LocalDateTime.parse(classStatusData.getTchOpenDate(), sourceFormatter);

            String taskOpenRegDt = formatter.format(localOpenDateTime.atZone(ZoneId.systemDefault()).toInstant());

            classStatusData.setTchOpenDate(taskOpenRegDt);
        }

        if (StringUtils.isNotBlank(classStatusData.getTchCloseDate())) {
            LocalDateTime localCloseDateTime = LocalDateTime.parse(classStatusData.getTchCloseDate(), sourceFormatter);

            String taskCloseRegDt = formatter.format(localCloseDateTime.atZone(ZoneId.systemDefault()).toInstant());

            classStatusData.setTchCloseDate(taskCloseRegDt);
        }

        if ("Start".equals(callType)) {
            RealStartResDto resultData;
            String jsonData;

            paramData.setTchId(classStatusData.getTchId());
            paramData.setClaId(classStatusData.getClaId());

            /* 학습 시작 시 시작 전송 */
            /* 교사 마지막 수업의 표준체계 리스트 추출 */
            List<String> curriculumStandardList = realMessageQueueMapper.selectTcLastlessonInfo(paramData);

            if (!curriculumStandardList.isEmpty()) {
                for (String curriculum : curriculumStandardList) {
                    // REAL_표준체계별 시작 전송
                    resultData = RealStartResDto.builder()
                            .partnerId(paramData.getPartnerId())
                            .accessToken(paramData.getAccessToken())
                            .userId(classStatusData.getStntId())
                            .type(MessageConstants.Type.INITIALIZED)
                            .curriculum(ObjectUtils.isNotEmpty(curriculum) ? curriculum : "-1")
                            .reqTime(classStatusData.getTchOpenDate())
                            .useTermsAgreeYn(useTermsAgreeYn)
                            .build();

                    jsonData = objectMapper.writeValueAsString(resultData);
                    pushNatsMQ(topicName, jsonData);
                }
            } else {
                resultData = RealStartResDto.builder()
                        .partnerId(paramData.getPartnerId())
                        .accessToken(paramData.getAccessToken())
                        .userId(classStatusData.getStntId())
                        .type(MessageConstants.Type.INITIALIZED)
                        .curriculum("-1")
                        .reqTime(classStatusData.getTchOpenDate())
                        .useTermsAgreeYn(useTermsAgreeYn)
                        .build();

                jsonData = objectMapper.writeValueAsString(resultData);
                pushNatsMQ(topicName, jsonData);
            }
        } else {
            paramData.setStntId(classStatusData.getStntId());
            paramData.setTchId(classStatusData.getTchId());
            LearningProgressVO learningProgressVO = LearningProgressVO.builder()
                    .partnerId(paramData.getPartnerId())
                    .accessToken(paramData.getAccessToken())
                    .stntId(classStatusData.getStntId())
                    .tchId(classStatusData.getTchId())
                    .claIdx(paramData.getClaIdx())
                    .claId(classStatusData.getClaId())
                    .textbkId(paramData.getTextbkId())
                    .tchOpenDate(classStatusData.getTchOpenDate())
                    .tchCloseDate(classStatusData.getTchCloseDate())
                    .build();

            //학생별 수업표준체계목록 조회
            List<LearningProgressDto> learningList = realMessageQueueMapper.findLearningProgressList(learningProgressVO);
            RealStartResDto resultData;
            String jsonData;

            if (!learningList.isEmpty()) {
                for (LearningProgressDto LearningProgress : learningList) {
                    // 표준체계ID 별로 메세지큐 전송
                    if (LearningProgress.getCurriculum() != null) {

                        String curriculum = LearningProgress.getCurriculum();

                        /* 학습 종료 시 수업 경과, 종료 전송 */
                        // REAL_표준체계별 종료 전송
                        resultData = RealStartResDto.builder()
                                .partnerId(paramData.getPartnerId())
                                .accessToken(paramData.getAccessToken())
                                .userId(classStatusData.getStntId())
                                .type(MessageConstants.Type.TERMINATED)
                                .curriculum(ObjectUtils.isNotEmpty(curriculum) ? curriculum : "-1")
                                .reqTime(classStatusData.getTchCloseDate())
                                .useTermsAgreeYn(useTermsAgreeYn)
                                .build();

                        jsonData = objectMapper.writeValueAsString(resultData);
                        pushNatsMQ(topicName, jsonData);

                        // REAL_표준체계별 경과 전송
                        sendCommunicateProgressInLearning(paramData, classStatusData, LearningProgress, curriculum, classStatusData.getStntId());

                        // 표준체계별 경과 진도율이 100 일때 완료 메세지와 점수 전송
                        if (LearningProgress.getPercent().equals(100)) {
                            sendLearningCompletionMessage(paramData, classStatusData, LearningProgress, curriculum);
                            sendScoreMessage(paramData, LearningProgress, curriculum, useTermsAgreeYn);
                        }
                    }
                }
            } else {
                /* 학습 종료 시 수업 경과, 종료 전송 */
                // REAL_표준체계별 종료 전송
                resultData = RealStartResDto.builder()
                        .partnerId(paramData.getPartnerId())
                        .accessToken(paramData.getAccessToken())
                        .userId(classStatusData.getStntId())
                        .type(MessageConstants.Type.TERMINATED)
                        .curriculum("-1")
                        .reqTime(classStatusData.getTchCloseDate())
                        .useTermsAgreeYn(useTermsAgreeYn)
                        .build();

                jsonData = objectMapper.writeValueAsString(resultData);
                pushNatsMQ(topicName, jsonData);
            }
        }

        var resultData = new LinkedHashMap<>();
        resultData.put("resultOk", true);
        resultData.put("resultMsg", "성공");
        return resultData;
    }

    // REAL_표준체계별 경과 전송
    private void sendCommunicateProgressInLearning(RealMqReqDto paramData, RealClassStartDto classStatusData,
                                                   LearningProgressDto learningProgress,String curriculum,String studentId) throws Exception {
        RealFinishResDto dto = RealFinishResDto.builder()
                .partnerId(paramData.getPartnerId())
                .accessToken(paramData.getAccessToken())
                .userId(studentId)
                .type(MessageConstants.Type.CURRICULUM_PROGRESSED)
                .curriculum(curriculum)
                .reqTime(classStatusData.getTchCloseDate())
                .percent(learningProgress.getPercent().toString())
                .useTermsAgreeYn(ObjectUtils.defaultIfNull(classStatusData.getUseTermsAgreeYn(), "N"))
                .build();

        String jsonData = objectMapper.writeValueAsString(dto);
        pushNatsMQ(topicName, jsonData);
    }

    // REAL_표준체계 완료
    private void sendLearningCompletionMessage(RealMqReqDto paramData, RealClassStartDto classStatusData,
                                               LearningProgressDto learningProgress, String curriculum)throws Exception {
        RealFinishResDto dto = RealFinishResDto.builder()
                .partnerId(paramData.getPartnerId())
                .accessToken(paramData.getAccessToken())
                .userId(paramData.getUserId())
                .type(MessageConstants.Type.CURRICULUM_COMPLETED)
                .curriculum(curriculum)
                .reqTime(classStatusData.getTchCloseDate())
                .useTermsAgreeYn(ObjectUtils.defaultIfNull(classStatusData.getUseTermsAgreeYn(), "N"))
                .build();

        String jsonData = objectMapper.writeValueAsString(dto);
        pushNatsMQ(topicName, jsonData);
    }

    // REAL_표준체계별 점수
    private void sendScoreMessage(RealMqReqDto paramData, LearningProgressDto learningProgress,String curriculum, String useTermsAgreeYn) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String currentTime = formatter.format(Instant.now());

        RealFinishResDto dto = RealFinishResDto.builder()
                .partnerId(paramData.getPartnerId())
                .accessToken(paramData.getAccessToken())
                .userId(paramData.getUserId())
                .type(MessageConstants.Type.CURRICULUM_SCORE)
                .curriculum(curriculum)
                .reqTime(currentTime)
                .score(Integer.toString(learningProgress.getScore()))
                .useTermsAgreeYn(useTermsAgreeYn)
                .build();

        String jsonData = objectMapper.writeValueAsString(dto);
        pushNatsMQ(topicName, jsonData);
    }

    /**  #^|를 기준으로 문자열 분리 */
    private List<String> splitCurriculum(String curriculum) {
        List<String> curriculumList = new ArrayList<>();
        if (curriculum != null && !curriculum.isEmpty()) {
            String[] curriculumArray = curriculum.split("#\\^\\|");
            curriculumList.addAll(Arrays.asList(curriculumArray));
        }
        return curriculumList;
    }

}
