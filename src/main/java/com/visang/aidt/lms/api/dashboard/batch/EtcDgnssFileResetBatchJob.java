package com.visang.aidt.lms.api.dashboard.batch;

import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"engl-dev","engl-stg","engl-prod-job", "math-prod-job", "engl-beta-job","engl-beta2-job","engl-release-job", "math-release-job", "beta-2e-engl-job"})
public class EtcDgnssFileResetBatchJob {

    private final EtcMapper etcMapper;

    @Scheduled(cron = "${batch-job.schedule.EtcBatchJob.executeDgnssFileReset}")
    public void executeDgnssFileResetJob() throws Exception {

        int targetTcDgnssFileCnt = etcMapper.tcDgnssFileReset();
        int targetStDgnssFileCnt = etcMapper.stDgnssFileReset();

        log.info("executeDgnssFileResetJob END =========================");
        log.info("teacher dgnss file reset : {}", targetTcDgnssFileCnt);
        log.info("student dgnss file reset : {}", targetStDgnssFileCnt);
        log.info("======================================================");
    }
}
