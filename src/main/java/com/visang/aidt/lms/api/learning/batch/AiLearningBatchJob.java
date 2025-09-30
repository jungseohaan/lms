package com.visang.aidt.lms.api.learning.batch;

import com.visang.aidt.lms.api.learning.service.AiLearningService;
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
@Profile({"math-dev","math-stg","math-prod-job","math-beta-job","math-beta2-job","math-release-job","beta-2e-math-job","vs-math-develop-job","vs-math-prod-job"})
public class AiLearningBatchJob {

    private final AiLearningService aiLearningService;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Scheduled (fixedDelayString = "${batch-job.schedule.AiLearningBatchJob.executeCreateAiLearningSets:1800000}")
    public void executeCreateAiLearningSets() throws Exception {
        log.info("executeCreateAiLearningSets START==================");

        // 로컬환경에서는 실행 X
        log.info("spring.profiles.active:{}", serverEnv);
        if ("local".equals(serverEnv)) {
            log.warn("LOCAL ENV: SKIP...");
            return;
        }

        Map<String, Object> evlResult = aiLearningService.createAiLearningBatchEvl(new HashMap<String, Object>());
        Map<String, Object> taskResult = aiLearningService.createAiLearningBatchTask(new HashMap<String, Object>());

        log.info("executeCreateAiLearningSets END=======================");
        log.info("created evl Sets:{}", evlResult.get("setCount"));
        log.info("created task Sets:{}", taskResult.get("setCount"));
        log.info("======================================================");


    }

//    @Scheduled (fixedDelayString = "${batch-job.schedule.AiLearningBatchJob.executeCreateAiLearningSets}")
////    @Scheduled (fixedDelay=2000)
//    public void executeTestSchedule()  {
//
//        log.info("executeTestSchedule START=======================");
//        try {
//            time1();
//        } catch (InterruptedException e) {
//            log.error("err:", e);
//        }
//        log.info("executeTestSchedule END=======================");
//    }
//
//    private void time1() throws InterruptedException {
//        Date now = new Date();
//        System.out.println("START : " + new SimpleDateFormat("MM-dd-yyyy / hh:mm:ss").format(now));
//
//        Thread.sleep(5000);
//
//        System.out.println("END : " + new SimpleDateFormat("MM-dd-yyyy / hh:mm:ss").format(now));
//    }


}
