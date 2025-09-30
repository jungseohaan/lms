package com.visang.aidt.lms.api.dashboard.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.TchDsbdEvalService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(교사) 대시보드 API(평가)", description = "(교사) 대시보드 API(평가)")
@AllArgsConstructor
@RequestMapping
public class TchDsbdEvalController {
    private final TchDsbdEvalService tchDsbdEvalService;

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "과제_평가 현황 (평가)", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> findTchDsbdStatusEvalList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchDsbdEvalService.findTchDsbdStatusEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제_평가 현황 (평가)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/eval/detail", method = {RequestMethod.GET})
    @Operation(summary = "과제_평가 현황 상세 (평가)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> findTchDsbdStatusEvalDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchDsbdEvalService.findTchDsbdStatusEvalDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제_평가 현황 상세 (평가)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/eval/result", method = {RequestMethod.GET})
    @Operation(summary = "과제_평가 현황 결과 (평가)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> findTchDsbdStatusEvalResult(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchDsbdEvalService.findTchDsbdStatusEvalResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제_평가 현황 결과 (평가)");
    }
}
