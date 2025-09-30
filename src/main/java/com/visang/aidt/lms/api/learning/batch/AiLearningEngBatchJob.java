package com.visang.aidt.lms.api.learning.batch;

import com.visang.aidt.lms.api.learning.service.AiLearningEngService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"engl-dev","engl-stg","engl-prod-job","engl-beta-job","engl-beta2-job","engl-release-job","beta-2e-engl-job","vs-engl-prod-job"})
public class AiLearningEngBatchJob {
    private final AiLearningEngService aiLearningEngService;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Scheduled(fixedDelayString = "${batch-job.schedule.AiLearningEngBatchJob.executeCreateAiLearningEngSets:1800000}")
    public void executeCreateAiLearningSets() throws Exception {
        log.info("executeCreateAiLearningEngSets START==================");

        // 로컬환경에서는 실행 X
        log.info("spring.profiles.active:{}", serverEnv);
        if (serverEnv.equals("local")) {
            log.warn("LOCAL ENV: SKIP...");
            return;
        }

        Map<String, Object> evlResult = aiLearningEngService.createAiLearningBatchEvlEng(new HashMap<String, Object>());
        Map<String, Object> taskResult = aiLearningEngService.createAiLearningBatchTaskEng(new HashMap<String, Object>());

        log.info("executeCreateAiLearningEngSets END=======================");
        log.info("created evl Sets:{}", evlResult.get("setCount"));
        log.info("created task Sets:{}", taskResult.get("setCount"));
        log.info("======================================================");
    }
}
