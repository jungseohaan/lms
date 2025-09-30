package com.visang.aidt.lms.api.learning.batch;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.learning.service.StdUsdCalculateService;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"engl-beta-job","beta-2e-engl-job","vs-engl-prod-job","vs-engl-develop-job"})
public class EngStdUsdCalculateBatchJob {

    @Value("${batch-job.schedule.EngStdUsdCalculateBatchJob.engTestMode:local}")
    private String engTestMode;

    @Value("${batch-job.schedule.EngStdUsdCalculateBatchJob.engTextbkIdList:#{null}}")
    private String engTextbkIdList;

    private final BtchExcnLogService btchExcnLogService;
    private final StdUsdCalculateService stdUsdCalculateService;

    private static final String BATCH_NAME = "EngStdUsdCalculateBatchJob.executeEngStdUsdCalculate";

    @Loggable
    @Scheduled(cron = "${batch-job.schedule.EngStdUsdCalculateBatchJob.executeEngStdUsdCalculate}")
//     @Scheduled(fixedDelayString="#{${batch-job.schedule.EngStdUsdCalculateBatchJob.fixedDelay:2} * 60 * 1000}") // Delay 30분마다
    public void executeEngStdUsdCalculate() throws Exception {
        // 특정 상황(로컬)에서는 실행되지 않도록 함
        if (shouldSkipExecution()) {
            return;
        }

        // 배치 실행 전 확인
        Map<String, Object> batchInfo = btchExcnLogService.checkBatchInfoExist(BATCH_NAME);
        if (!isBatchInfoValid(batchInfo)) {
            return;
        }

        // 배치 실행
        processEngStdUsdCalculate(batchInfo);
    }

    private void processEngStdUsdCalculate(Map<String, Object> batchInfo) throws Exception {
        log.info("StdEngUsdCalculateBatchJob > processEngStdUsdCalculate() : testMode={}, textbkIdList={}", engTestMode, engTextbkIdList);

        int btchExcnRsltCntTotal = 0;
        int succCntTotal = 0;
        int failCntTotal = 0;
        List<String> failDcList = new ArrayList<>(); // 실패사유 목록

        Map<String, Object> paramData = new HashMap<>();
        paramData.put("stdDt", LocalDate.now()); // 현재 날짜
        // 이전 배치 삭제
        stdUsdCalculateService.deleteEngStdUsdTarget(paramData);

        // 교과서ID별 배치 처리
        paramData.put("brandId",3); // 1: 수학 , 3: 영어
        paramData.put("textbkIdList", AidtCommonUtil.strToLongList(engTextbkIdList));
        List<Long> textbkIds = stdUsdCalculateService.findStdUsdTextbkTargetList(paramData);
        for (Long textbkId : textbkIds) {
            log.info("EngStdUsdCalculateBatchJob > createEngStdUsdCalculate() > textbkId={}", textbkId);
            String prefixLog = "textbkId="+textbkId+":";
            paramData.put("textbkId", textbkId);

            Map<String, Object> batchparamData = new HashMap<>();
            try {
                // 배치 실행 전 DB로그
                batchparamData.put("btchId", batchInfo.get("btchId"));
                batchparamData.put("failDc", prefixLog + "processing");
                btchExcnLogService.createBtchDetailInfo(batchparamData);

                // 영어 이해도 점수 계산 및 등록
                Map<String, Object> resultMap = stdUsdCalculateService.createEngStdUsdCalculate(paramData);
                boolean resultOk = MapUtils.getBooleanValue(resultMap, "resultOk");    // 정상 작동여부 확인
                int btchExcnRsltCnt = MapUtils.getIntValue(resultMap, "btchExcnRsltCnt"); // 배치실행결과 건수
                String failDc = prefixLog + (resultOk ? btchExcnRsltCnt + " processed" : MapUtils.getString(resultMap, "failDc"));

                // 배치 실행 후 DB로그
                batchparamData.put("btchExcnRsltCnt", btchExcnRsltCnt); // 배치실행결과건수(실행후)
                batchparamData.put("btchRsltAt", resultOk ? "Y" : "N"); // 배치결과여부(실행후)
                batchparamData.put("failDc", failDc); // 실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                // 처리 누계 계산(로그용)
                succCntTotal += (resultOk ? 1 : 0);
                failCntTotal += (resultOk ? 0 : 1);
                btchExcnRsltCntTotal += btchExcnRsltCnt;
                failDcList.add(failDc);
            } catch (IllegalArgumentException e) {
                log.error("배치 처리 - 잘못된 파라미터 (textbkId={}): {}", textbkId, e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N");
                batchparamData.put("failDc", prefixLog + "잘못된 파라미터: " + e.getMessage().substring(0, Math.min(100, e.getMessage().length())));
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + "잘못된 파라미터: " + e.getMessage().substring(0, Math.min(100, e.getMessage().length())));
            } catch (DataAccessException e) {
                log.error("배치 처리 - 데이터베이스 접근 오류 (textbkId={}): {}", textbkId, e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N");
                batchparamData.put("failDc", prefixLog + "DB접근오류: " + CustomLokiLog.errorLog(e).substring(0, 100));
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + "DB접근오류: " + CustomLokiLog.errorLog(e).substring(0, 100));
            } catch (Exception e) {
                log.error("배치 처리 - 예상치 못한 오류 (textbkId={}): {}", textbkId, e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N");
                batchparamData.put("failDc", prefixLog + CustomLokiLog.errorLog(e).substring(0, 100));
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.errorLog(e).substring(0, 100));
            }

            failDcList.add("total cnt: " + btchExcnRsltCntTotal + ", succ: " + succCntTotal + ", fail: " + failCntTotal);
            log.info(String.join(",\n", failDcList));
        }

        log.info(String.join(",\n", failDcList));
    }

    private boolean isBatchInfoValid(Map<String, Object> batchInfo) {
        return (Boolean) batchInfo.get("resultOk");
    }

    private boolean shouldSkipExecution() {
        if ("local".equals(engTestMode)) {
            log.warn("executeEngStdUsdCalculate() is not supported by local");
            return true;
        }
        if (!Arrays.asList("dev", "stg", "prod", "beta", "beta2").contains(engTestMode)) {
            log.warn("executeEngStdUsdCalculate() is supported only by dev,stg,prod mode.:testMode={}", engTestMode);
            return true;
        }
        return false;
    }
}
