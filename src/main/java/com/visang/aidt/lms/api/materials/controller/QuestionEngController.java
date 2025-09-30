package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.QuestionEngService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
//@Tag(name = "[영어] 자동문제출제", description = "[영어] 자동문제출제")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionEngController {
    private final QuestionEngService questionEngService;

    @Loggable
    @RequestMapping(value = "/question/questionList/eng", method = {RequestMethod.GET})
    @Operation(summary = "자동문제출제", description = "자동문제출제")
    @Parameter(name = "articleId", description = "articleId", required = true, schema = @Schema(type = "string", example = "3"))
    @Parameter(name = "limitNum", description = "limitNum", required = false, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> questionListEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = questionEngService.findQuestionListEng(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문제출제");
    }
}
