/*
package com.visang.aidt.lms.api.kafka.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.kafka.service.KafkaBatchService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!engl-prod-job & !engl-beta-job & !engl-beta2-job & !math-prod-job & !math-beta-job & !math-beta2-job")
@ConditionalOnProperty(name = "spring.kafka.listener.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaMessageListener {
    private final KafkaBatchService KafkaBatchService;
    private final AtomicLong lastMessageTime = new AtomicLong(0);
    private final AtomicBoolean canProcessMessage = new AtomicBoolean(false);
    private final AtomicBoolean isJobRunning = new AtomicBoolean(false);
    private static final long FIVE_MINUTES = 600000; // 10분을 밀리초로 표현
    private final RestTemplate restTemplate;

    @Value("${spring.kafka.topic-prefix}")
    private String topicPrefix;

    @Value("${spring.flink.jobmanager.url}")
    private String flinkJobManagerUrl;

    @Value("${spring.flink.jobmanager.env}")
    private String env;

    @Value("${spring.kafka.listener.enabled:false}")
    private boolean kafkaListenerEnabled;


    @PostConstruct
    public void init() {
        lastMessageTime.set(System.currentTimeMillis());
        log.info("Kafka 리스너 초기화 완료");
    }

    public String[] getTopics() {
        log.info("토픽 선택을 위한 topicPrefix: {}", topicPrefix);

        if (topicPrefix.contains("math")) {
            log.info("math 토픽만 구독합니다");
            return new String[] {
                    topicPrefix + ".aidt_lms.mv_lms_usd_cac_src_info"
            };
        } else if (topicPrefix.contains("eng")) {
            log.info("eng 토픽만 구독합니다");
            return new String[] {
                    topicPrefix + ".aidt_lms.mv_lms_eng_dashboard_final"
            };
        } else {
            log.info("모든 토픽을 구독합니다");
            return new String[] {
                    topicPrefix + ".aidt_lms.mv_lms_eng_dashboard_final",
                    topicPrefix + ".aidt_lms.mv_lms_usd_cac_src_info"
            };
        }
    }

    @Scheduled(fixedDelay = 10000)  // 10초마다 체크
    public void checkFlinkStatus() {
        if (!kafkaListenerEnabled) {
            log.debug("Kafka 리스너가 비활성화되어 있어 Flink 작업 상태를 확인하지 않습니다.");
            return;
        }
        try {
            boolean currentJobStatus = checkFlinkJobStatusFromAPI();
            boolean previousJobStatus = isJobRunning.getAndSet(currentJobStatus);

            if (previousJobStatus && !currentJobStatus) {
                log.info("Flink job이 중단되었습니다. 메시지 처리를 일시 중지합니다.");
            }
        } catch (Exception e) {
            log.error("Error checking Flink status: {}", e.getMessage());
        }
    }

    private boolean checkFlinkJobStatusFromAPI() {
        try {
            String jobsUrl = flinkJobManagerUrl + "/jobs/overview";
            List<String> targetJobNames = Arrays.asList(
                    String.format("flink-job-flink-job-%s-01", env.toLowerCase()),
                    String.format("flink-job-flink-job-%s-02", env.toLowerCase()),
                    String.format("flink-job-flink-job-%s-eng", env.toLowerCase()),
                    String.format("flink-job-flink-job-%s-math", env.toLowerCase()),
                    String.format("flink-job-flink-job-%s-all", env.toLowerCase())
            );

            ResponseEntity<JobsResponse> response = restTemplate.getForEntity(jobsUrl, JobsResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getJobs().stream()
                        .anyMatch(job -> targetJobNames.contains(job.getName()) && "RUNNING".equals(job.getState()));
            }
            return false;
        } catch (Exception e) {
            log.error("Flink 잡 상태 확인 중 오류: {}", e.getMessage());
            return false;
        }
    }

    @KafkaListener(
            topics = "#{@kafkaMessageListener.getTopics()}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleLearningActivity(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {

        if (!kafkaListenerEnabled) {
            log.debug("Kafka 리스너가 비활성화되어 있어 메시지를 처리하지 않고 확인만 합니다.");
            acknowledgment.acknowledge();
            return;
        }

        try {
            long currentTime = System.currentTimeMillis();
            long timeSinceLastMessage = currentTime - lastMessageTime.get();

            // 5분 이상 메시지가 없었다면 처리 가능 상태로 변경
            */
/*if (!canProcessMessage.get() && timeSinceLastMessage >= FIVE_MINUTES) {
                canProcessMessage.set(true);
                log.info("5분 이상 메시지가 없었습니다. 메시지 처리를 시작합니다.");
            }*//*


            // 처리 가능 상태일 때만 메시지 처리
            if (canProcessMessage.get()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode data = objectMapper.readTree(message);
                String operation = data.get("op").asText();
                List<String> opList = List.of("c", "u");

                if (opList.contains(operation)) {
                    Map<String, Object> mapData = (Map) objectMapper.convertValue(data, Map.class);

                    if (ObjectUtils.isNotEmpty(mapData.get("after"))) {
                        Map<String, Object> source = (Map<String, Object>) mapData.get("source");

                        log.info("KafkaMessageListener 시작 : {}", source.get("table"));
                        processMessage(data, topic);
                    }
                }
            } else {
                log.debug("초기 데이터 적재 중... 메시지 스킵 (마지막 메시지로부터 {}ms 경과)", timeSinceLastMessage);
            }

            // 메시지 수신 시간 업데이트
            lastMessageTime.set(currentTime);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("메시지 파싱 중 오류. Topic: {}", topic, e);
            throw new RuntimeException("메시지 처리 실패", e);
        }
    }

    private void processMessage(JsonNode data, String topic) {
        try {
            KafkaBatchService.processFullInsertCycle(data);
        } catch (Exception e) {
            log.error("데이터 처리 중 오류. Topic: {}", topic, e);
            throw new RuntimeException(e);
        }
    }

    @Data
    private static class JobsResponse {
        private List<Job> jobs;
    }

    @Data
    private static class Job {
        private String id;
        private String name;
        private String state;
    }
}*/
