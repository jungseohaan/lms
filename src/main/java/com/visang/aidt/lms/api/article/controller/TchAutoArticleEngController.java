package com.visang.aidt.lms.api.article.controller;

import com.visang.aidt.lms.api.article.service.TchAutoArticleEngService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "[영어] 자동문항생성", description = "[영어] 자동문항생성")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchAutoArticleEngController {
    private final TchAutoArticleEngService tchAutoArticleEngService;

    @Loggable
    @RequestMapping(value = "/tch/homewk/auto/qstn/extr/eng", method = {RequestMethod.GET})
    @Operation(summary = "자동문항생성-과제", description = "자동문항생성-과제")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "emaone2-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "d27dff98537f4ff0af3535cf9788efce"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "223"))
    @Parameter(name = "eamExmNum", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "6"))
    @Parameter(name = "eamGdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamAvExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "2"))
    @Parameter(name = "eamBdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "eamScp", description = "출제범위", required = true, schema = @Schema(type = "string", example = "870,872,956"))
    public ResponseDTO<CustomBody> tchHomewkAutoQstnExtrEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        /// 파라미터 유효성 검증
        List<String> requiredParams = Arrays.asList("wrterId","claId","textbookId","eamExmNum","eamGdExmMun","eamAvExmMun","eamBdExmMun","eamScp");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        paramData.put("eamScp", AidtCommonUtil.strToLongList((String)paramData.get("eamScp"))); // 출제 범위

        Map<String, Object> resultData = tchAutoArticleEngService.findTchHomewkAutoQstnExtrEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문항생성-과제");
    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/auto/qstn/extr/eng", method = {RequestMethod.GET})
    @Operation(summary = "자동문항생성-수업", description = "자동문항생성-수업")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "emaone2-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "d27dff98537f4ff0af3535cf9788efce"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "223"))
    @Parameter(name = "eamExmNum", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "6"))
    @Parameter(name = "eamGdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamAvExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "2"))
    @Parameter(name = "eamBdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "eamScp", description = "출제범위", required = true, schema = @Schema(type = "string", example = "870,872,956"))
    public ResponseDTO<CustomBody> tchLectureAutoQstnExtrEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        /// 파라미터 유효성 검증
        List<String> requiredParams = Arrays.asList("wrterId","claId","textbookId","eamExmNum","eamGdExmMun","eamAvExmMun","eamBdExmMun","eamScp");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        paramData.put("eamScp", AidtCommonUtil.strToLongList((String)paramData.get("eamScp"))); // 출제 범위

        Map<String, Object> resultData = tchAutoArticleEngService.findTchLectureAutoQstnExtrEng(paramData);

        if(Boolean.FALSE.equals(resultData.get("resultOk"))) {
            return AidtCommonUtil.makeResultFail(paramData, null, (String) resultData.get("resultMsg"));
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문항생성-수업");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/auto/qstn/extr/eng", method = {RequestMethod.GET})
    @Operation(summary = "자동문항생성-평가", description = "자동문항생성-평가")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "emaone2-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "d27dff98537f4ff0af3535cf9788efce"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "223"))
    @Parameter(name = "eamExmNum", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "6"))
    @Parameter(name = "eamGdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamAvExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "2"))
    @Parameter(name = "eamBdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "eamScp", description = "출제범위", required = true, schema = @Schema(type = "string", example = "870,872,956"))
    public ResponseDTO<CustomBody> tchEvalAutoQstnExtrEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        /// 파라미터 유효성 검증
        List<String> requiredParams = Arrays.asList("wrterId","claId","textbookId","eamExmNum","eamGdExmMun","eamAvExmMun","eamBdExmMun","eamScp");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        paramData.put("eamScp", AidtCommonUtil.strToLongList((String)paramData.get("eamScp"))); // 출제 범위

        Map<String, Object> resultData = tchAutoArticleEngService.findTchEvalAutoQstnExtrEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문항생성-평가");
    }
}
