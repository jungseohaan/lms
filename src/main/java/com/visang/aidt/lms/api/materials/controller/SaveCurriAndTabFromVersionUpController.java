package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.keris.service.TiosApiService;
import com.visang.aidt.lms.api.materials.service.PortalPzService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(테스트)공공존 진입 페이지", description = "(포털)공공존 진입 페이지")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SaveCurriAndTabFromVersionUpController {

    private final PortalPzService portalPzService;
    private final TiosApiService tiosApiService;

    @Value("${app.lcmsapi.deployServerCode}")
    public String deployServerCode;

    @Value("${app.lcmsapi.deployServerCodeMulti}")
    public String deployServerCodeMulti;/*민간존 운영(비바샘)의 경우에는 컨텐츠 배포를 여러 서버 다중 배포가 일어남*/

    @RequestMapping(value = "/portal/pz/save-curri-tab1", method = {RequestMethod.GET})
    @Operation(summary = "테스트 컨트롤러 버전업 - 교사의 textbook 정보를 기반으로 데이터 추출", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vivasam-mathmi1-086e5bdcbb68cf9-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "578246b7709042d7828261a4b2631025"))
    @Parameter(name = "emptyProccessYn", description = "`Y`일 경우 교사 데이터가 아에 없을 경우에는 CBS 데이터 기준으로 밀어 넣는 옵션 (비바샘의 경우 필요)", required = true, schema = @Schema(type = "string", example = "N"))
    public ResponseDTO<CustomBody> portalPzSaveCurriTab1(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Map<String, Object> tcTextbookInfo = portalPzService.getTcTextbookInfo(paramData);
            tcTextbookInfo.put("emptyProccessYn", paramData.get("emptyProccessYn"));
            Object resultData = portalPzService.saveCurriAndTabFromVersionUp(tcTextbookInfo);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "테스트 컨트롤러 버전업");
        } catch (IllegalArgumentException e) {
            log.error("portalPzSaveCurriTab1 - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("portalPzSaveCurriTab1 - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("portalPzSaveCurriTab1 - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("portalPzSaveCurriTab1 - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (SocketTimeoutException e) {
            log.error("portalPzSaveCurriTab1 - Timeout error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Request timeout occurred");
        } catch (HttpClientErrorException e) {
            log.error("portalPzSaveCurriTab1 - HTTP client error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service returned client error");
        } catch (HttpServerErrorException e) {
            log.error("portalPzSaveCurriTab1 - HTTP server error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service returned server error");
        } catch (RestClientException e) {
            log.error("portalPzSaveCurriTab1 - REST client error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service communication failed");
        } catch (RuntimeException e) {
            log.error("portalPzSaveCurriTab1 - Runtime error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Runtime error occurred");
        } catch (Exception e) {
            log.error("portalPzSaveCurriTab1 - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/pz/save-curri-tab2", method = {RequestMethod.GET})
    @Operation(summary = "테스트 컨트롤러 버전업 - tc_textbook 데이터 까지 수동 세팅", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "kiins_t1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772662"))
    @Parameter(name = "smteCd", description = "학기 코드", required = true, schema = @Schema(type = "string", example = "semester01"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "textbkIdxId", description = "목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "id", description = "tcTextbookId - tc_textbook 테이블의 id", required = true, schema = @Schema(type = "integer", example = "2"))
    @Parameter(name = "version", description = "버전", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "emptyProccessYn", description = "`Y`일 경우 교사 데이터가 아에 없을 경우에는 CBS 데이터 기준으로 밀어 넣는 옵션 (비바샘의 경우 필요)", required = true, schema = @Schema(type = "string", example = "N"))
    public ResponseDTO<CustomBody> portalPzSaveCurriTab2(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.saveCurriAndTabFromVersionUp(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "테스트 컨트롤러 버전업");
        } catch (IllegalArgumentException e) {
            log.error("portalPzSaveCurriTab2 - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("portalPzSaveCurriTab2 - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("portalPzSaveCurriTab2 - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("portalPzSaveCurriTab2 - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (SocketTimeoutException e) {
            log.error("portalPzSaveCurriTab2 - Timeout error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Request timeout occurred");
        } catch (HttpClientErrorException e) {
            log.error("portalPzSaveCurriTab2 - HTTP client error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service returned client error");
        } catch (HttpServerErrorException e) {
            log.error("portalPzSaveCurriTab2 - HTTP server error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service returned server error");
        } catch (RestClientException e) {
            log.error("portalPzSaveCurriTab2 - REST client error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service communication failed");
        } catch (RuntimeException e) {
            log.error("portalPzSaveCurriTab2 - Runtime error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Runtime error occurred");
        } catch (Exception e) {
            log.error("portalPzSaveCurriTab2 - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }


    @RequestMapping(value = "/portal/vivasam/save-curri-tab", method = {RequestMethod.GET})
    @Operation(summary = "테스트 컨트롤러 버전업 - 교사의 textbook 정보를 기반으로 데이터 추출", description = "")
    @Parameter(name = "textbkCd", description = "AIDT 교과서 코드 ", required = true, schema = @Schema(type = "string", example = "mathmi1"))
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vivasam-mathmi1-086e5bdcbb68cf9-t"))
    public ResponseDTO<CustomBody> portalPzVivasamSaveCurriTab(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            // 파트너 ID 조회
            paramData.put("textbkCd", paramData.get("textbkCd"));
            Map<String, Object> ptnInfo = tiosApiService.getPtnInfo(paramData);
            ptnInfo.put("userId", MapUtils.getString(paramData, "wrterId"));
            ptnInfo.put("deployServerCode", deployServerCode);
            ptnInfo.put("deployServerCodeMulti", deployServerCodeMulti);
            Map<String, Object> resultAddSaveMap = tiosApiService.saveContentsAndResultForTeacher(ptnInfo);
            String resultMessage = MapUtils.getString(resultAddSaveMap, "resultMessage");
            return AidtCommonUtil.makeResultSuccess(paramData, resultAddSaveMap, resultMessage);
        } catch (IllegalArgumentException e) {
            log.error("portalPzVivasamSaveCurriTab - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("portalPzVivasamSaveCurriTab - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (SocketTimeoutException e) {
            log.error("portalPzVivasamSaveCurriTab - Timeout error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Request timeout occurred");
        } catch (HttpClientErrorException e) {
            log.error("portalPzVivasamSaveCurriTab - HTTP client error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service returned client error");
        } catch (HttpServerErrorException e) {
            log.error("portalPzVivasamSaveCurriTab - HTTP server error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service returned server error");
        } catch (RestClientException e) {
            log.error("portalPzVivasamSaveCurriTab - REST client error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - External service communication failed");
        } catch (DataAccessException e) {
            log.error("portalPzVivasamSaveCurriTab - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (RuntimeException e) {
            log.error("portalPzVivasamSaveCurriTab - Runtime error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Runtime error occurred");
        } catch (Exception e) {
            log.error("portalPzVivasamSaveCurriTab - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }
}
