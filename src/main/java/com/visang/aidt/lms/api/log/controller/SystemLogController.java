package com.visang.aidt.lms.api.log.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.log.service.SystemLogService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(Socket) SystemLog API", description = "로깅 API")
@RequiredArgsConstructor
@RequestMapping(value = "/syslog", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemLogController {

    private final SystemLogService systemLogService;

    @Loggable
    @GetMapping(value="/log-check")
    @Operation(summary = "로깅 대상 목록 조회", description = "로그 조회 API")
    @Parameter(name = "inspSrvc", description = "서비스 명 (git url 마지막 key)", schema = @Schema(type = "string", example = "visang-aidt-launcher"))
    @Parameter(name = "inspAreaKey", description = "상세 영역 (중복 시 구분 영역)", schema = @Schema(type = "string", example = "default"))
    @Parameter(name = "inspNm", description = "검사 명칭", schema = @Schema(type = "string", example = ""))
    @Parameter(
            name = "logTy",
            description = "로그 적용 대상 - ALL : 전체 적용 / TCH : 교사일 경우 / STU : 학생일 경우 / USR : 개별 설정한 유저만",
            required = true,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"ALL", "TCH", "STU", "USR"}
            ),
            in = ParameterIn.QUERY
    )
	@ResponseBody
	public ResponseDTO<CustomBody> saveUserCheckInfo(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        try {
            Object resultMap = systemLogService.getLogCheckList(paramData, pageable);
            return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "Log target list retrieval success");
        } catch (IllegalArgumentException e) {
            log.error("saveUserCheckInfo - Invalid argument error: {}, param: {}", e.getMessage(), paramData, e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("saveUserCheckInfo - Null pointer error: {}, param: {}", e.getMessage(), paramData, e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("saveUserCheckInfo - Database access error: {}, param: {}", e.getMessage(), paramData, e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("saveUserCheckInfo - SQL error: {}, param: {}", e.getMessage(), paramData, e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (RuntimeException e) {
            log.error("saveUserCheckInfo - Runtime error: {}, param: {}", e.getMessage(), paramData, e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected runtime error");
        } catch (Exception e) {
            log.error("saveUserCheckInfo - Unexpected error: {}, param: {}", e.getMessage(), paramData, e);
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }

    }
}
