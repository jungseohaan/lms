package com.visang.aidt.lms.api.learning.controller;

import com.visang.aidt.lms.api.learning.service.StdUsdCalculateService;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.dao.*;
import org.springframework.http.MediaType;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@Tag(name = "학생의 영어/수학 이해도 점수 계산 배치 작업 API", description = "학생의 영어/수학 이해도 점수 계산 배치 작업 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StdUsdCalculateApiJobController {

    private final StdUsdCalculateService stdUsdCalculateService;
    private final BtchExcnLogService btchExcnLogService;

    private static final String MATH_BATCH_NAME = "StdUsdCalculateApiJobController.executeStdUsdCalculate";
    private static final String ENGL_BATCH_NAME = "StdUsdCalculateApiJobController.executeEngStdUsdCalculate";

    @GetMapping("/batch/calculate/math")
    @Operation(summary = "학생의 수학 이해도 점수 계산 배치 작업 API", description = "기존 스케쥴링을 API 형태로 호출할 수 있도록 하여 시나리오 작업에 활용")
    @Parameter(name = "stdDt", description = "from Date(yyyy-MM-dd HH:mm:ss) - 00:00:00 고정", required = true, schema = @Schema(type = "string", example = "2024-07-15 00:00:00"))
    @Parameter(name = "textbkIdList", description = "교과서ID (콤마 구분자 숫자 값)", required = true, schema = @Schema(type = "string", example = "464"))
    @Parameter(name = "wrterId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "eqa8-t"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "8f8baa6d33c4437bbd88d7c221c5f160"))
    public ResponseDTO<CustomBody> batchCalculateMath(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            paramData.put("apiYn", "Y");

            Map<String, Object> resultData = new HashMap<>();

            String tempStdDt = MapUtils.getString(paramData, "stdDt", "");
            String stdDt = null;
            if (tempStdDt.endsWith("00:00:00")) {
                stdDt = tempStdDt;
            } else {
                stdDt = StringUtils.substring(tempStdDt, 0, 4);
                if (tempStdDt.length() == 8) {
                    stdDt += "-" + StringUtils.substring(stdDt, 4, 6);
                    stdDt += "-" + StringUtils.substring(stdDt, 6, 8);
                } else {

                }
                stdDt += " 00:00:00";
            }
            if (StringUtils.isEmpty(stdDt)) {
                return AidtCommonUtil.makeResultFail(paramData, null, "error! stdDt layout check - " + stdDt);
            }
            paramData.put("stdDt", stdDt);

            // 배치 실행 전 확인
            Map<String, Object> batchInfo = btchExcnLogService.checkBatchInfoExist(MATH_BATCH_NAME);
            String btchId = MapUtils.getString(batchInfo, "btchId");
            if (MapUtils.getBoolean(batchInfo, "resultOk") == false || StringUtils.isEmpty(btchId)) {
                return AidtCommonUtil.makeResultFail(paramData, null, "error! batch data error - btch_info table check");
            }
            paramData.put("btchId", btchId);

            StopWatch sw = new StopWatch();
            sw.reset();
            sw.start();
            // 배치 실행
            String resultMessage = processStdUsdCalculateFromApi(paramData);
            // after advice
            sw.stop();
            Long total = sw.getTime();
            resultData.put("api-url", "/batch/ai/remedy-lrn/create/evl/get-target");
            resultData.put("api-calltime", total + "(ms)");
            resultData.put("resultMessage", resultMessage);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생의 수학 이해도 점수 계산 배치 작업 API");
        } catch (DuplicateKeyException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DeadlockLoserDataAccessException
                | CannotAcquireLockException
                | CannotSerializeTransactionException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (QueryTimeoutException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DataAccessResourceFailureException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (BadSqlGrammarException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DataAccessException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (Exception e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        }
    }

    @GetMapping("/batch/calculate/engl")
    @Operation(summary = "학생의 영어 이해도 점수 계산 배치 작업 API", description = "기존 스케쥴링을 API 형태로 호출할 수 있도록 하여 시나리오 작업에 활용")
    @Parameter(name = "stdDt", description = "from Date(yyyy-MM-dd HH:mm:ss) - 00:00:00 고정", required = true, schema = @Schema(type = "string", example = "2024-07-15 00:00:00"))
    @Parameter(name = "textbkIdList", description = "교과서ID (콤마 구분자 숫자 값)", required = true, schema = @Schema(type = "string", example = "464"))
    @Parameter(name = "wrterId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "eqa8-t"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "8f8baa6d33c4437bbd88d7c221c5f160"))
    public ResponseDTO<CustomBody> batchCalculateEngl(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            paramData.put("apiYn", "Y");

            Map<String, Object> resultData = new HashMap<>();

            String tempStdDt = MapUtils.getString(paramData, "stdDt", "");
            String stdDt = null;
            if (tempStdDt.endsWith("00:00:00")) {
                stdDt = tempStdDt;
            } else {
                stdDt = StringUtils.substring(tempStdDt, 0, 4);
                if (tempStdDt.length() == 8) {
                    stdDt += "-" + StringUtils.substring(stdDt, 4, 6);
                    stdDt += "-" + StringUtils.substring(stdDt, 6, 8);
                } else {

                }
                stdDt += " 00:00:00";
            }
            if (StringUtils.isEmpty(stdDt)) {
                return AidtCommonUtil.makeResultFail(paramData, null, "error! stdDt layout check - " + stdDt);
            }
            paramData.put("stdDt", stdDt);

            // 배치 실행 전 확인
            Map<String, Object> batchInfo = btchExcnLogService.checkBatchInfoExist(ENGL_BATCH_NAME);
            String btchId = MapUtils.getString(batchInfo, "btchId");
            if (MapUtils.getBoolean(batchInfo, "resultOk") == false || StringUtils.isEmpty(btchId)) {
                return AidtCommonUtil.makeResultFail(paramData, null, "error! batch data error - btch_info table check");
            }
            paramData.put("btchId", btchId);

            StopWatch sw = new StopWatch();
            sw.reset();
            sw.start();
            // 배치 실행
            String resultMessage = processEngStdUsdCalculateFromApi(paramData);
            // after advice
            sw.stop();
            Long total = sw.getTime();
            resultData.put("api-url", "/batch/ai/remedy-lrn/create/evl/get-target");
            resultData.put("api-calltime", total + "(ms)");
            resultData.put("resultMessage", resultMessage);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생의 수학 이해도 점수 계산 배치 작업 API");
        } catch (DuplicateKeyException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DeadlockLoserDataAccessException
                | CannotAcquireLockException
                | CannotSerializeTransactionException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (QueryTimeoutException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DataAccessResourceFailureException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (BadSqlGrammarException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (DataAccessException e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        } catch (Exception e) {
            String errorMessage = CustomLokiLog.errorLog(e);
            log.error(errorMessage);
            return AidtCommonUtil.makeResultFail(paramData, null, errorMessage);
        }
    }

    /**
     * 학생의 수학 이해도 점수 계산을 처리하는 메소드
     *
     * @param paramData 배치 정보
     *                  btchId :
     *                  stdDt : yyyy-MM-dd HH:mm:ss
     *                  textbkIdList : 교과서ID(콤마 구분자 숫자)
     *                  wrterId : 교사ID
     *                  claId : 학급ID
     */
    private String processStdUsdCalculateFromApi(Map<String, Object> paramData) throws Exception {

        if (MapUtils.getString(paramData, "apiYn", "N").equals("Y") == false) {
            return "error! not api call";
        }

        String stdDt = MapUtils.getString(paramData, "stdDt");
        if (StringUtils.isEmpty(stdDt)
                || StringUtils.isEmpty(MapUtils.getString(paramData, "wrterId"))
                || StringUtils.isEmpty(MapUtils.getString(paramData, "claId"))) {
            log.error("stdDt, wrterId, claId empty");
            return "stdDt, wrterId, claId empty";
        }

        String textbkIdList = MapUtils.getString(paramData, "textbkIdList", "");

        log.info("StdUsdCalculateApiJobController > processStdUsdCalculate() : testMode={}, textbkIdList={}", "math-apijob", textbkIdList);

        int btchExcnRsltCntTotal = 0;
        int succCntTotal = 0;
        int failCntTotal = 0;
        List<String> failDcList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(stdDt));
        cal.add(Calendar.DATE, 1);

        String calcDt = sdf.format(cal.getTime());
        calcDt = StringUtils.substring(calcDt, 0, 10) + " 00:00:00";

        paramData.put("calcDt", calcDt); // 누적 계산 날짜는 +1 한 00:00:00

        // 교과서ID별 배치 처리
        paramData.put("brandId", 1); // 1: 수학 , 3: 영어
        paramData.put("textbkIdList", AidtCommonUtil.strToLongList(textbkIdList));
        List<Long> textbkIds = stdUsdCalculateService.findStdUsdTextbkTargetList(paramData);

        for (Long textbkId : textbkIds) {
            log.info("StdUsdCalculateBatchJob > executeStdUsdCalculate() > textbkId={}", textbkId);
            String prefixLog = "textbkId=" + textbkId + ":";
            paramData.put("textbkId", textbkId);

            stdUsdCalculateService.deleteStdUsdTarget(paramData);

            Map<String, Object> batchparamData = new HashMap<>();
            try {
                // 배치 실행 전 DB로그
                batchparamData.put("btchId", paramData.get("btchId"));
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
            } catch (DuplicateKeyException e) {

                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;

            } catch (DataIntegrityViolationException e) {

                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;

            } catch (DeadlockLoserDataAccessException
                  | CannotAcquireLockException
                  | CannotSerializeTransactionException e) {

                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;

            } catch (QueryTimeoutException e) {

                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;

            } catch (DataAccessResourceFailureException e) {

                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;

            } catch (BadSqlGrammarException e) {

                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;

            } catch (DataAccessException e) {

                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;

            } catch (Exception e) {
                String errorMessage = CustomLokiLog.errorLog(e);

                log.error(errorMessage);

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + errorMessage.substring(0, 100)); //실패사유

                return errorMessage;
            }

            failDcList.add("total cnt: " + btchExcnRsltCntTotal + ", succ: " + succCntTotal + ", fail: " + failCntTotal);
            log.info(String.join(",\n", failDcList));
        }

        String logMessage = String.join(",\n", failDcList);

        log.info(logMessage);
        return logMessage;
    }

    /**
     * 학생의 수학 이해도 점수 계산을 처리하는 메소드
     *
     * @param paramData 배치 정보
     *                  btchId :
     *                  stdDt : yyyy-MM-dd HH:mm:ss
     *                  textbkIdList : 교과서ID(콤마 구분자 숫자)
     *                  wrterId : 교사ID
     *                  claId : 학급ID
     * @return
     * @throws Exception
     */
    private String processEngStdUsdCalculateFromApi(Map<String, Object> paramData) throws Exception {

        if (MapUtils.getString(paramData, "apiYn", "N").equals("Y") == false) {
            return "error! not api call";
        }

        String stdDt = MapUtils.getString(paramData, "stdDt");
        if (StringUtils.isEmpty(stdDt)
                || StringUtils.isEmpty(MapUtils.getString(paramData, "wrterId"))
                || StringUtils.isEmpty(MapUtils.getString(paramData, "claId"))) {
            log.error("stdDt, wrterId, claId empty");
            return "stdDt, wrterId, claId empty";
        }

        String engTextbkIdList = MapUtils.getString(paramData, "textbkIdList", "");

        log.info("StdUsdCalculateApiJobController > processEngStdUsdCalculate() : testMode={}, textbkIdList={}", "eng-apijob", engTextbkIdList);

        int btchExcnRsltCntTotal = 0;
        int succCntTotal = 0;
        int failCntTotal = 0;
        List<String> failDcList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(stdDt));
        cal.add(Calendar.DATE, 1);

        String calcDt = sdf.format(cal.getTime());
        calcDt = StringUtils.substring(calcDt, 0, 10) + " 00:00:00";

        paramData.put("calcDt", calcDt); // 누적 계산 날짜는 +1 한 00:00:00

        // 교과서ID별 배치 처리
        paramData.put("brandId", 3); // 1: 수학 , 3: 영어
        paramData.put("textbkIdList", AidtCommonUtil.strToLongList(engTextbkIdList));
        List<Long> textbkIds = stdUsdCalculateService.findStdUsdTextbkTargetList(paramData);

        for (Long textbkId : textbkIds) {
            log.info("EngStdUsdCalculateBatchJob > createEngStdUsdCalculate() > textbkId={}", textbkId);
            String prefixLog = "textbkId=" + textbkId + ":";
            paramData.put("textbkId", textbkId);

            stdUsdCalculateService.deleteEngStdUsdTarget(paramData);

            Map<String, Object> batchparamData = new HashMap<>();
            try {
                // 배치 실행 전 DB로그
                batchparamData.put("btchId", paramData.get("btchId"));
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
            } catch (DuplicateKeyException e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            } catch (DataIntegrityViolationException e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            } catch (DeadlockLoserDataAccessException
                  | CannotAcquireLockException
                  | CannotSerializeTransactionException e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            } catch (QueryTimeoutException e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            } catch (DataAccessResourceFailureException e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            } catch (BadSqlGrammarException e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            } catch (DataAccessException e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            } catch (Exception e) {
                log.error(e.getMessage());

                // 배치실행 중 오류 DB로그
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실행후)
                batchparamData.put("failDc", prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유(textbkId 저장시에도 활용)
                btchExcnLogService.modifyBtchExcnLog(batchparamData);

                failDcList.add(prefixLog + CustomLokiLog.shortErrorLog(e, 0, 100)); //실패사유
            }

            failDcList.add("total cnt: " + btchExcnRsltCntTotal + ", succ: " + succCntTotal + ", fail: " + failCntTotal);
            log.info(String.join(",\n", failDcList));
        }

        String logMessage = String.join(",\n", failDcList);

        log.info(logMessage);
        return logMessage;
    }
}