package com.visang.aidt.lms.api.article.controller;

import com.visang.aidt.lms.api.article.service.TchArticleService;
import com.visang.aidt.lms.api.assessment.service.StntEvalService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.mapper.QuestionMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@Tag(name = "(교사) 과제/평가/수업자료 API", description = "(교사) 과제/평가/수업자료 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchArticleController {

    private TchArticleService tchArticleService;

    @Loggable
    @RequestMapping(value = {"/tch/eval/repl-article","/tch/homewk/repl-article", "/tch/lecture/repl-article"}, method = {RequestMethod.GET})
    @Operation(summary = "(교사) 과제/평가/수업자료 생성시 문항교체", description = "")
    @Parameter(name = "articleId", description = "아티클 ID", required = true, schema = @Schema(type = "string", example = "3484" ))
    public ResponseDTO<CustomBody> findQuestionList(
        @RequestParam(name = "articleId", defaultValue = "3484") String articleId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();

        paramData.put("articleId", articleId);
        paramData.put("limitNum", 1);
        paramData.put("gbCd", 2);  //구분코드 (1:다른문제풀기, 2:문항교체, 3:문항추천, 4:AI처방학습)

        Object resultData = tchArticleService.findQuestionList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제/평가/수업자료 생성시 문항교체");
    }

}