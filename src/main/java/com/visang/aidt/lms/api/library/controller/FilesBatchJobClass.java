package com.visang.aidt.lms.api.library.controller;

import com.visang.aidt.lms.api.library.service.FileService;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/airflow/batch")
public class FilesBatchJobClass {

    private final FileService fileService;
    private final BtchExcnLogService btchExcnLogService;

    /**
     * 교사에게 평가 미제출 알림 전송
     *
     * @throws Exception
     */
    @GetMapping(value = "/files/delete", produces = "application/json")
    public Map<String, Object> executeDeleteFileList() throws Exception {
        log.info("FilesBatchJob > executeDeleteFileList()");

        String btchNm = "FilesBatchJob.executeDeleteFileList";
        Map<String, Object> resultOfBatchInfoExist = btchExcnLogService.checkBatchInfoExist(btchNm);
        Map<String, Object> response = new HashMap<>();
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
                    response = resultOfSendToTch;
                }
            } else {
                // 당일 이미 실행된 소스
                response.put("resultOk", false);
                response.put("failDc", "이미 실행된 배치");
            }
        }

        if (response.isEmpty()) {
            response.put("resultOk", false);
        }

        return response;
    }
}
