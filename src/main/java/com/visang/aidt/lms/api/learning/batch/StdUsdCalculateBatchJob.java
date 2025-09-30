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
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * 학생의 수학 이해도 점수 계산 배치 작업 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Profile({"math-beta-job","beta-2e-math-job","vs-math-develop-job","vs-math-prod-job"})
@Component
public class StdUsdCalculateBatchJob {

    @Value("${batch-job.schedule.StdUsdCalculateBatchJob.testMode:local}")
    private String testMode;
    @Value("${batch-job.schedule.StdUsdCalculateBatchJob.textbkIdList:#{null}}")
    private String textbkIdList;

    private final StdUsdCalculateService stdUsdCalculateService;
    private final BtchExcnLogService btchExcnLogService;

    private static final String BATCH_NAME = "StdUsdCalculateBatchJob.executeStdUsdCalculate";


    /**
     * 학생의 수학 이해도 점수를 계산하는 배치 작업을 실행
     * 설정된 `testMode`에 따라 로컬, 개발, 스테이징, 운영 환경에서 실행 여부를 결정
     * `textbkIdList`에 지정된 교과서 ID 목록을 기반으로 계산을 수행
     * 배치 작업 실행 전후에 로그를 남기고, 성공, 실패 건수 및 실패 사유를 기록
     *
     * @throws Exception 배치 작업 실행 중 예외 발생 시
     */
    @Loggable
    @Scheduled(cron = "${batch-job.schedule.StdUsdCalculateBatchJob.executeStdUsdCalculate}")
    // @Scheduled(fixedDelayString = "#{${batch-job.schedule.StdUsdCalculateBatchJob.fixedDelay:30} * 60 * 1000}")
    // Delay 30분마다
    public void executeStdUsdCalculate() throws Exception {
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
        processStdUsdCalculate(batchInfo);
    }

    /**
     * 학생의 수학 이해도 점수 계산을 처리하는 메소드
     *
     * @param batchInfo 배치 정보
     */
    private void processStdUsdCalculate(Map<String, Object> batchInfo) throws Exception {
        log.info("StdUsdCalculateBatchJob > processStdUsdCalculate() : testMode={}, textbkIdList={}", testMode, textbkIdList);

        int btchExcnRsltCntTotal = 0;
        int succCntTotal = 0;
        int failCntTotal = 0;
        List<String> failDcList = new ArrayList<>();

        Map<String, Object> paramData = new HashMap<>();
        // 테스트 모드인 경우 - 현재날짜로 처리
        paramData.put("stdDt", LocalDate.now());  // 현재 날짜
        stdUsdCalculateService.deleteStdUsdTarget(paramData);

        // 교과서ID별 배치 처리
        paramData.put("brandId",1); // 1: 수학 , 3: 영어
        paramData.put("textbkIdList", AidtCommonUtil.strToLongList(textbkIdList));
        List<Long> textbkIds = stdUsdCalculateService.findStdUsdTextbkTargetList(paramData);
        for (Long textbkId : textbkIds) {
            log.info("StdUsdCalculateBatchJob > executeStdUsdCalculate() > textbkId={}", textbkId);
            String prefixLog = "textbkId="+textbkId+":";
            paramData.put("textbkId", textbkId);

            Map<String, Object> batchparamData = new HashMap<>();
            try {
                // 배치 실행 전 DB로그
                batchparamData.put("btchId", batchInfo.get("btchId"));
                batchparamData.put("failDc", prefixLog + "processing");
                btchExcnLogService.createBtchDetailInfo(batchparamData);

                // 수학 이해도 점수 계산 및 등록
                Map<String, Object> resultMap = stdUsdCalculateService.createStdUsdCalculate(paramData);
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
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            }

            failDcList.add("total cnt: " + btchExcnRsltCntTotal + ", succ: " + succCntTotal + ", fail: " + failCntTotal);
            log.info(String.join(",\n", failDcList));
        }

        log.info(String.join(",\n", failDcList));
    }

    /**
     * 배치 정보가 유효한지 확인하는 메소드
     *
     * @param batchInfo 배치 정보
     * @return 배치 정보 유효 여부
     */
    private boolean isBatchInfoValid(Map<String, Object> batchInfo) {
        return (Boolean) batchInfo.get("resultOk");
    }

    /**
     * 배치 실행 여부를 결정하는 메소드
     *
     * @return 배치 실행 여부
     */
    private boolean shouldSkipExecution() {
        if ("local".equals(testMode)) {
            log.warn("executeStdUsdCalculate() is not supported by local");
            return true;
        }
        if (!Arrays.asList("dev", "stg", "prod", "beta", "beta2").contains(testMode)) {
            log.warn("executeStdUsdCalculate() is supported only by dev,stg,prod mode.:testMode={}", testMode);
            return true;
        }
        return false;
    }

}
