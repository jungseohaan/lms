package com.visang.aidt.lms.api.integration.controller;

import com.visang.aidt.lms.api.integration.service.IntegCommonService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(연동) 공통 API", description = "(연동) 공통 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class IntegCommonController {

    private final IntegCommonService integCommonService;

    @RequestMapping(value = "/integ/common/ptninfo/list", method = {RequestMethod.GET})
    @Operation(summary = "파트너 정보 조회", description = "")
    @Parameter(name = "ptnId", description = "파트너 아이디", required = false, schema = @Schema(type = "string", example = "d79366e1-d506-51c4-a758-601f2945a7a4"))
    @Parameter(name = "textbkCd", description = "교과서코드", required = false, schema = @Schema(type = "string", example = "mathel31"))
    @Parameter(name = "curriSchool", description = "학교급", required = false, schema = @Schema(type = "string", example = "elementary"))
    @Parameter(name = "curriGrade", description = "학년", required = false, schema = @Schema(type = "string", example = "grade03"))
    @Parameter(name = "curriSemester", description = "학기", required = false, schema = @Schema(type = "string", example = "semester01"))
    @Parameter(name = "curriSubject", description = "과목", required = false, schema = @Schema(type = "string", example = "mathematics"))
    @Parameter(name = "curriBook", description = "교재번호", required = false, schema = @Schema(type = "string", example = "0021"))
    public ResponseDTO<CustomBody> listPtnInfo(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integCommonService.listPtnInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "파트너 정보 조회");
    }

    @RequestMapping(value = "/integ/common/cla/list", method = {RequestMethod.GET})
    @Operation(summary = "클래스 ID 목록 조회", description = "")
    @Parameter(name = "claKey", description = "클래스 key", required = true, schema = @Schema(type = "string", example = "d79366e1-"))
    public ResponseDTO<CustomBody> listClaId(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integCommonService.listClaId(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "클래스 ID 목록 조회");
    }

    @RequestMapping(value = "/integ/common/cla/info", method = {RequestMethod.GET})
    @Operation(summary = "클래스 조회", description = "")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "viva-2430-2025-1-4930-106380"))
    public ResponseDTO<CustomBody> getClaInfo(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integCommonService.getClaInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "클래스 조회");
    }
}
