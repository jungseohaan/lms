package com.visang.aidt.lms.api.assessment.batch;

import com.visang.aidt.lms.api.assessment.service.EvalNtcnSendService;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({ "math-dev", "math-stg", "engl-stg", "engl-prod-job", "math-prod-job", "math-beta-job", "engl-beta-job",
        "math-beta2-job", "engl-beta2-job", "math-release-job", "engl-release-job", "beta-2e-math-job",
        "beta-2e-engl-job", "vs-math-develop-job", "vs-engl-develop-job", "vs-math-prod-job", "vs-engl-prod-job" })
public class EvalNtcnSendBatchJob {

    @Value("${batch-job.schedule.EvalNtcnSendBatchJob.brandIdList:#{null}}")
    private String brandIdList;

    private final EvalNtcnSendService evalNtcnSendService;
    private final BtchExcnLogService btchExcnLogService;

    /**
     * 교사에게 평가 미제출 알림 전송
     *
     * @throws Exception
     */
    @Scheduled(cron = "${batch-job.schedule.EvalNtcnSendBatchJob.executeTchEvalUnsubListSend}")
    public void executeTchEvalUnsubListSend() throws Exception {
        log.info("EvalNtcnSendBatchJob > executeTchEvalUnsubListSend()");

        String btchNm = "EvalNtcnSendBatchJob.executeTchEvalUnsubListSend";
        Map<String, Object> resultOfBatchInfoExist = btchExcnLogService.checkBatchInfoExist(btchNm);

        // 배치 대상사 존재 확인
        Boolean isBatchInfoExist = (Boolean) resultOfBatchInfoExist.get("resultOk");
        if (isBatchInfoExist) {
            // 배치 실행 로그 기록
            Map<String, Object> resultOfCreateBtchExcnLog = btchExcnLogService
                    .createBtchExcnLog(resultOfBatchInfoExist.get("btchId").toString());

            // 배치 실행 로그 생성 성공 여부 확인
            Boolean isCreateBtchExcnLog = (Boolean) resultOfCreateBtchExcnLog.get("resultOk");
            if (isCreateBtchExcnLog) {
                Map<String, Object> batchparamData = new HashMap<>();
                batchparamData.put("btchDetId", resultOfCreateBtchExcnLog.get("btchDetId"));

                // 배치 서비스 호출
                Map<String, Object> resultOfSendToTch = new HashMap<>();
                try {
                    resultOfSendToTch = evalNtcnSendService.sendNtcnUnsubListToTch();
                    if ((Boolean) resultOfSendToTch.get("resultOk")) {
                        batchparamData.put("btchExcnRsltCnt", resultOfSendToTch.get("btchExcnRsltCnt"));
                        resultOfSendToTch.put("resultOk", true);
                    } else {
                        batchparamData.put("failDc", resultOfSendToTch.get("failDc")); // 배치 실패사유
                        batchparamData.put("btchRsltAt", "N"); // 배치 결과 여부(실패)
                        resultOfSendToTch.put("resultOk", false);
                    }
                } catch (DataAccessException e) {
                    log.error("Database access error in executeTchEvalUnsubListSend: {}", CustomLokiLog.errorLog(e));
                    batchparamData.put("failDc", "데이터베이스 처리 중 오류가 발생했습니다.");
                    batchparamData.put("btchRsltAt", "N");
                    resultOfSendToTch.put("resultOk", false);
                } catch (IllegalArgumentException e) {
                    log.error("Illegal argument in executeTchEvalUnsubListSend: {}", CustomLokiLog.errorLog(e));
                    batchparamData.put("failDc", "잘못된 인수가 전달되었습니다.");
                    batchparamData.put("btchRsltAt", "N");
                    resultOfSendToTch.put("resultOk", false);
                } catch (NullPointerException e) {
                    log.error("Null pointer exception in executeTchEvalUnsubListSend: {}", CustomLokiLog.errorLog(e));
                    batchparamData.put("failDc", "필수 객체가 null입니다.");
                    batchparamData.put("btchRsltAt", "N");
                    resultOfSendToTch.put("resultOk", false);
                } catch (Exception e) {
                    batchparamData.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));  // 배치 실패사유
                    batchparamData.put("btchRsltAt", "N");  // 배치 결과 여부(실패)
                    resultOfSendToTch.put("resultOk", false);
                } finally {
                    // 배치 결과 업데이트
                    btchExcnLogService.modifyBtchExcnLog(batchparamData);
                }
            }
        }
    }

    /**
     * 학생에게 평가 미제출 알림 전송
     *
     * @throws Exception
     */
    @Scheduled(cron = "${batch-job.schedule.EvalNtcnSendBatchJob.executeStntEvalUnsubListSend}")
    public void executeStntEvalUnsubListSend() throws Exception {
        log.info("EvalNtcnSendBatchJob > executeStntEvalUnsubListSend()");

        String btchNm = "EvalNtcnSendBatchJob.executeStntEvalUnsubListSend";
        Map<String, Object> resultOfBatchInfoExist = btchExcnLogService.checkBatchInfoExist(btchNm);

        Boolean isBatchInfoExist = (Boolean) resultOfBatchInfoExist.get("resultOk");
        if (isBatchInfoExist) {
            // 배치 실행 로그 기록
            Map<String, Object> resultOfCreateBtchExcnLog = btchExcnLogService
                    .createBtchExcnLog(resultOfBatchInfoExist.get("btchId").toString());

            // 배치 실행 로그 생성 성공 여부 확인
            Boolean isCreateBtchExcnLog = (Boolean) resultOfCreateBtchExcnLog.get("resultOk");
            if (isCreateBtchExcnLog) {
                Map<String, Object> batchparamData = new HashMap<>();
                batchparamData.put("btchDetId", resultOfCreateBtchExcnLog.get("btchDetId"));

                // 배치 서비스 호출
                Map<String, Object> resultOfSendToStnt = new HashMap<>();
                try {
                    resultOfSendToStnt = evalNtcnSendService.sendNtcnUnsubListToStnt();
                    if ((Boolean) resultOfSendToStnt.get("resultOk")) {
                        batchparamData.put("btchExcnRsltCnt", resultOfSendToStnt.get("btchExcnRsltCnt"));
                        resultOfSendToStnt.put("resultOk", true);
                    } else {
                        batchparamData.put("failDc", resultOfSendToStnt.get("failDc")); // 배치실패사유
                        batchparamData.put("btchRsltAt", "N"); // 배치결과여부(실패)
                        resultOfSendToStnt.put("resultOk", false);
                    }
                } catch (DataAccessException e) {
                    log.error("Database access error in executeStntEvalUnsubListSend: {}", CustomLokiLog.errorLog(e));
                    batchparamData.put("failDc", "데이터베이스 처리 중 오류가 발생했습니다.");
                    batchparamData.put("btchRsltAt", "N");
                    resultOfSendToStnt.put("resultOk", false);
                } catch (IllegalArgumentException e) {
                    log.error("Illegal argument in executeStntEvalUnsubListSend: {}", CustomLokiLog.errorLog(e));
                    batchparamData.put("failDc", "잘못된 인수가 전달되었습니다.");
                    batchparamData.put("btchRsltAt", "N");
                    resultOfSendToStnt.put("resultOk", false);
                } catch (NullPointerException e) {
                    log.error("Null pointer exception in executeStntEvalUnsubListSend: {}", CustomLokiLog.errorLog(e));
                    batchparamData.put("failDc", "필수 객체가 null입니다.");
                    batchparamData.put("btchRsltAt", "N");
                    resultOfSendToStnt.put("resultOk", false);
                } catch (Exception e) {
                    log.error("Unexpected error in executeStntEvalUnsubListSend: {}", CustomLokiLog.errorLog(e));
                    batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                    batchparamData.put("failDc", CustomLokiLog.errorLog(e).substring(0, 100));
                    resultOfSendToStnt.put("resultOk", false);
                } finally {
                    // 배치결과 업데이트
                    btchExcnLogService.modifyBtchExcnLog(batchparamData);
                }
            }
        }
    }

    /**
     * 평가 알림 배치
     *
     * @throws Exception
     */
    @Scheduled(cron = "${batch-job.schedule.EvalNtcnSendBatchJob.excuteSendTchEvalCreateReportList}")
    public void excuteSendTchEvalCreateReportList() throws Exception {
        evalNtcnSendService.executeSendTchEvalCreateReportNtcn(brandIdList);
    }

}
