package com.visang.aidt.lms.api.shop.batch;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.shop.service.ShopService;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"math-dev","math-stg","engl-prod-job","math-prod-job","math-beta-job","engl-beta-job","math-beta2-job","engl-beta2-job","math-release-job","engl-release-job","beta-2e-math-job","beta-2e-engl-job","vs-math-develop-job","vs-engl-develop-job","vs-math-prod-job","vs-engl-prod-job"})
public class ShopPrchsHistBatchJob {

    private final ShopService shopService;
    private final BtchExcnLogService btchExcnLogService;

    /**
     * 신규 사용자에게 디폴트 프로필 상품 구매내역 자동 등록
     * (교사/학생에게 제공하는 디폴트 상품이 구매내역이 없는 관계로 모든 사용자(학부모 제외)에 대해서 구매정보, 구매이력 정보를 생성)
     *
     * 매일 오전 6시에 실행되어 상점구매정보(`sp_prchs_info`)가 없는
     * 교사/학생 사용자에게 디폴트 프로필 상품 구매내역을 생성
     *
     * @details
     * 1. 대상: 상점구매정보(`sp_prchs_info`)가 없는 교사, 학생 사용자
     * 2. 실행 주기: 매일 오전 06:00:00 (1일 1회)
     * 3. 처리 방식:
     *     - `user` 테이블에서 교사, 학생 사용자 중 `sp_prchs_info` 테이블에 정보가 없는 사용자 조회
     *     - `sp_pf_info` 테이블에서 `initl_at` 값이 'Y'인 디폴트 프로필 상품 조회
     *     - `insert...select` 구문을 사용하여  `sp_prchs_info` 및 `sp_prchs_hist` 테이블에 구매내역 일괄 등록
     * 4. 등록 정보:
     *     - 상품구매금액: 0원
     *     - 구매상품구분(`prchs_gds_se_cd`): 'P'
     *     - `inv_se_cd`: 1
     *     - `rwd_se_cd`: 1 (하트)
     *     - `prchs_gds_id`: 디폴트 프로필 상품 ID
     */
    @Loggable
    @Scheduled(cron = "${batch-job.schedule.ShopPrchsHistBatchJob.executeMakePrchsHistBatch}")
    public void executeMakePrchsHistBatch() throws Exception {
        log.info("ShopPrchsHistBatchJob > makePrchsHistBatch() > START");

        Map<String, Object> batchparamData = new HashMap<>();

        // 배치 ID 조회
        String btchNm = "ShopPrchsHistBatchJob.executeMakePrchsHistBatch";

        Map<String, Object> result = btchExcnLogService.checkBatchInfoExist(btchNm);
        Boolean isBatchExist = (Boolean) result.get("resultOk");

        if (isBatchExist) {
            // 배치실행전 상세 내역 생성
            result = btchExcnLogService.createBtchExcnLog(result.get("btchId").toString());
            Boolean detailOk = (Boolean) result.get("resultOk");

            if (detailOk) {
                //배치상세 ID
                batchparamData.put("btchDetId", result.get("btchDetId"));

                Map<String, Object> resultMap = new HashMap<>();
                try {
                    //배치 서비스 호출
                    resultMap = shopService.insertSpPrchsHist();
                } catch (NullPointerException e) {
                    log.error("insertSpPrchsHist - NullPointerException:", e);
                    CustomLokiLog.errorLog(e);
                    batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                    batchparamData.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
                    resultMap.put("resultOk", false);
                } catch (IllegalArgumentException e) {
                    log.error("insertSpPrchsHist - IllegalArgumentException:", e);
                    CustomLokiLog.errorLog(e);
                    batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                    batchparamData.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
                    resultMap.put("resultOk", false);
                } catch (DataAccessException e) {
                    log.error("insertSpPrchsHist - DataAccessException:", e);
                    CustomLokiLog.errorLog(e);
                    batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                    batchparamData.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
                    resultMap.put("resultOk", false);
                } catch (RuntimeException e) {
                    log.error("insertSpPrchsHist - RuntimeException:", e);
                    CustomLokiLog.errorLog(e);
                    batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                    batchparamData.put("failDc", CustomLokiLog.errorLog(e).toString().substring(0, 100));
                    resultMap.put("resultOk", false);
                } catch (Exception e) {
                    log.error("insertSpPrchsHist - Exception:", e);
                    CustomLokiLog.errorLog(e);
                    batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                    batchparamData.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));
                    resultMap.put("resultOk", false);
                } finally {
                    //배치종료후 상세 입력
                    if ((Boolean) resultMap.get("resultOk")) {
                        batchparamData.put("btchExcnRsltCnt", resultMap.get("btchExcnRsltCnt")); //배치실행결과건수
                    } else { //Exception
                        batchparamData.put("failDc", resultMap.get("failDc")); //실패사유
                        batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                    }

                    // 배치결과 업데이트
                    btchExcnLogService.modifyBtchExcnLog(batchparamData);
                }
            }
        }
    }
}
