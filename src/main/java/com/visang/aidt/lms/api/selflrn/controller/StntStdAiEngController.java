package com.visang.aidt.lms.api.selflrn.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.selflrn.service.StntStdAiEngService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "[영어](학생) AI학습", description = "[영어](학생) AI학습")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntStdAiEngController {
    private StntStdAiEngService stntStdAiEngService;

    @Loggable
    @RequestMapping(value = "/stnt/std/ai/init/eng", method = {RequestMethod.GET})
    @Operation(summary = "AI학습(초기)", description = "AI학습(초기)")
    @Parameter(name = "userId", description = "사용자(학생) ID", required = true, schema = @Schema(type = "string", example = "userId1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "claId1"))
    @Parameter(name = "textbkId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "unitNum", description = "단원번호", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> stntStdAiInitEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = stntStdAiEngService.findStntStdAiInitEng(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI학습(초기)");
    }
}
