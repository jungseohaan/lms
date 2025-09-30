package com.visang.aidt.lms.api.library.batch;

import com.visang.aidt.lms.api.library.service.FileService;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"math-dev","math-stg","engl-prod-job","math-prod-job","math-beta-job","engl-beta-job","math-beta2-job","engl-beta2-job","math-release-job","engl-release-job","beta-2e-math-job","beta-2e-engl-job"})
public class FilesBatchJob {

    private final FileService fileService;
    private final BtchExcnLogService btchExcnLogService;

    /**
     * 개인정보 파일 삭제
     *
     * @throws Exception
     */
    @Scheduled(cron = "${batch-job.schedule.FilesBatchJob.executeDeleteFileList}")
    public void executeDeleteFileList() throws Exception {
        log.info("FilesBatchJob > executeDeleteFileList()");

        String btchNm = "FilesBatchJob.executeDeleteFileList";
        Map<String, Object> resultOfBatchInfoExist = btchExcnLogService.checkBatchInfoExist(btchNm);

        // 배치 대상사 존재 확인
        Boolean isBatchInfoExist = (Boolean) resultOfBatchInfoExist.get("resultOk");
        if (isBatchInfoExist) {
            // 배치 실행 로그 기록
            Map<String, Object> resultOfCreateBtchExcnLog = btchExcnLogService.createBtchExcnLog(resultOfBatchInfoExist.get("btchId").toString());

            // 배치 실행 로그 생성 성공 여부 확인
            Boolean isCreateBtchExcnLog = (Boolean) resultOfCreateBtchExcnLog.get("resultOk");
            if (isCreateBtchExcnLog) {
                Map<String, Object> batchparamData = new HashMap<>();
                batchparamData.put("btchDetId", resultOfCreateBtchExcnLog.get("btchDetId"));

                // 배치 서비스 호출
                Map<String, Object> resultOfSendToTch = new HashMap<>();
                try { // 기본 적인 catch 처리를 deleteFiles 내에서 하고 있음
                    resultOfSendToTch = fileService.deleteFiles();
                    if ((Boolean) resultOfSendToTch.get("resultOk")) {
                        batchparamData.put("failDc", resultOfSendToTch.get("failDc"));  // 배치 결과 확인용
                        batchparamData.put("btchExcnRsltCnt", resultOfSendToTch.get("btchExcnRsltCnt"));
                        resultOfSendToTch.put("resultOk", true);
                    } else {
                        batchparamData.put("failDc", resultOfSendToTch.get("failDc"));  // 배치 실패사유
                        batchparamData.put("btchRsltAt", "N");  // 배치 결과 여부(실패)
                        resultOfSendToTch.put("resultOk", false);
                    }
                } catch (IllegalArgumentException e) {
                    batchparamData.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));  // 배치 실패사유
                    batchparamData.put("btchRsltAt", "N");  // 배치 결과 여부(실패)
                    resultOfSendToTch.put("resultOk", false);
                } catch (NullPointerException e) {
                    batchparamData.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));  // 배치 실패사유
                    batchparamData.put("btchRsltAt", "N");  // 배치 결과 여부(실패)
                    resultOfSendToTch.put("resultOk", false);
                } catch (RuntimeException e) {
                    batchparamData.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));  // 배치 실패사유
                    batchparamData.put("btchRsltAt", "N");  // 배치 결과 여부(실패)
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
}
