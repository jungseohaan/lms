package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.materials.service.PortalService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(포털) 진입 페이지", description = "(포털) 진입 페이지")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PortalController {

    private final PortalService portalService;

    @RequestMapping(value = "/portal/schoolList", method = {RequestMethod.GET})
    @Operation(summary = "교사 학교목록조회", description = "교사 학교목록조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "portal-t1"))
    public ResponseDTO<CustomBody> schoolList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalService.findSchoolList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 학교목록조회");
        } catch (IllegalArgumentException e) {
            log.error("schoolList - Invalid argument error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("schoolList - Null pointer error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("schoolList - Database access error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("schoolList - SQL error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (RuntimeException e) {
            log.error("schoolList - Runtime error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected runtime error");
        } catch (Exception e) {
            log.error("schoolList - Unexpected error: {}", CustomLokiLog.errorLog(e), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/gradeList", method = {RequestMethod.GET})
    @Operation(summary = "교사 학년목록조회", description = "교사 학교목록조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "portal-t1"))
    @Parameter(name = "schlNm", description = "학교이름", required = true, schema = @Schema(type = "string", example = "비상초등학교"))
    public ResponseDTO<CustomBody> gradeList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalService.findGradeList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 학년목록조회");
        } catch (IllegalArgumentException e) {
            log.error("gradeList - Invalid argument error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("gradeList - Null pointer error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("gradeList - Database access error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("gradeList - SQL error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (RuntimeException e) {
            log.error("gradeList - Runtime error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected runtime error");
        } catch (Exception e) {
            log.error("gradeList - Unexpected error: {}", CustomLokiLog.errorLog(e), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/classList", method = {RequestMethod.GET})
    @Operation(summary = "교사 반목록조회", description = "교사 반목록조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "portal-t1"))
    @Parameter(name = "schlNm", description = "학교이름", required = true, schema = @Schema(type = "string", example = "비상초등학교"))
    @Parameter(name = "gradeCd", description = "학년코드", required = true, schema = @Schema(type = "string", example = "4"))
    public ResponseDTO<CustomBody> classList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalService.findClassList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 반목록조회");
        } catch (IllegalArgumentException e) {
            log.error("classList - Invalid argument error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("classList - Null pointer error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("classList - Database access error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("classList - SQL error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (RuntimeException e) {
            log.error("classList - Runtime error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected runtime error");
        } catch (Exception e) {
            log.error("classList - Unexpected error: {}", CustomLokiLog.errorLog(e), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/tcTextbookList", method = {RequestMethod.POST})
    @Operation(summary = "교사 교과서목록조회", description = "교사 교과서목록조회")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "portal-t1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a5ea87f15011ee9bb8f220af648621"))
    @Parameter(
            name = "semester",
            description = "학기",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"semester01", "semester02"}
            ),
            in = ParameterIn.QUERY
    )
    @Parameter(
            name = "subject",
            description = "과목",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"mathematics", "english"}
            ),
            in = ParameterIn.QUERY
    )
    public ResponseDTO<CustomBody> tcTextbookList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalService.tcTextbookList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 교과서목록조회");
        } catch (IllegalArgumentException e) {
            log.error("tcTextbookList - Invalid argument error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("tcTextbookList - Null pointer error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("tcTextbookList - Database access error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("tcTextbookList - SQL error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (RuntimeException e) {
            log.error("tcTextbookList - Runtime error: {}", e.getMessage(), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected runtime error");
        } catch (Exception e) {
            log.error("tcTextbookList - Unexpected error: {}", CustomLokiLog.errorLog(e), e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/saveTcTextbook", method = {RequestMethod.POST})
    @Operation(summary = "교사 교과서선택", description = "교사 교과서선택")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "portal-t1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a5ea87f15011ee9bb8f220af648621"))
    @Parameter(
            name = "semester",
            description = "학기",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"semester01", "semester02"}
            ),
            in = ParameterIn.QUERY
    )
    @Parameter(name = "textbkCrltnId", description = "교과서 배포 ID", required = true, schema = @Schema(type = "integer", example = "39"))
    public ResponseDTO<CustomBody> saveTcTextbook(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalService.saveTcTextbook(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 교과서선택");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", paramData, e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Invalid request values");
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("Database error occurred", e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Database processing error");
        } catch (org.springframework.http.converter.HttpMessageNotReadableException e) {
            log.warn("Failed to parse request body", e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Invalid request data format");
        } catch (RuntimeException e) {
            log.error("Unexpected runtime exception", e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Unexpected processing error");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Teacher textbook selection failed");
        }
    }

    @RequestMapping(value = "/portal/stTextbookInfo", method = {RequestMethod.GET})
    @Operation(summary = "학생 교과서조회", description = "학생 교과서조회")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "portal-s11"))
    public ResponseDTO<CustomBody> stTextbookList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalService.stTextbookInfo(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생 교과서조회 실패");
        } catch (IllegalArgumentException e) {
            log.error("stTextbookList - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("stTextbookList - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("stTextbookList - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("stTextbookList - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (Exception e) {
            log.error("stTextbookList - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }


}
