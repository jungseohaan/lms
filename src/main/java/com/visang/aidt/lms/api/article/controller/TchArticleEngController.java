package com.visang.aidt.lms.api.article.controller;

import com.visang.aidt.lms.api.article.service.TchArticleEngService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
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
@Tag(name = "[영어] 문항출제", description = "[영어] 문항출제")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchArticleEngController {
    private TchArticleEngService tchArticleEngService;

    @Loggable
    @RequestMapping(value = {"/tch/eval/repl-article/eng","/tch/homewk/repl-article/eng"}, method = {RequestMethod.GET})
    @Operation(summary = "문항교체-평가, 문항교체-과제", description = "")
    @Parameter(name = "articleId", description = "아티클 ID", required = true, schema = @Schema(type = "string", example = "3" ))
    public ResponseDTO<CustomBody> replArticleEng(
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = tchArticleEngService.findReplArticleEng(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제/평가 생성시 문항교체");
    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/other/eng", method = {RequestMethod.GET})
    @Operation(summary = "문항교체-수업", description = "")
    @Parameter(name = "articleId", description = "아티클 ID", required = true, schema = @Schema(type = "string", example = "3" ))
    public ResponseDTO<CustomBody> qstnOtherEng(
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = tchArticleEngService.findQstnOtherEng(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "문항교체-수업");
    }

}
