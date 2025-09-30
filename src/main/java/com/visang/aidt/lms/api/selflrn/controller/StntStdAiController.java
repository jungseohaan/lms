package com.visang.aidt.lms.api.selflrn.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.selflrn.service.StntStdAiService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(학생) AI학습", description = "(학생) AI학습")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntStdAiController {
    private StntStdAiService stntStdAiService;

    @Loggable
    @RequestMapping(value = "/stnt/std/ai/init", method = {RequestMethod.GET})
    @Operation(summary = "AI학습(초기)", description = "AI학습(초기)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu466"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "unitNum", description = "단원번호", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> findStntStdAiInit(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntStdAiService.findStntStdAiInit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI학습(초기)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/std/ai/submit", method = {RequestMethod.GET})
    @Operation(summary = "AI학습(제출-정답확인)", description = "AI학습(제출-정답확인)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu466"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "unitNum", description = "단원번호", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "id", description = "학습결과ID", required = true, schema = @Schema(type = "integer"))
    @Parameter(name = "stdAiId", description = "AI학습ID", required = true, schema = @Schema(type = "integer"))
    @Parameter(name = "moduleId", description = "모듈ID", required = true, schema = @Schema(type = "string"))
    @Parameter(name = "stdUsdId", description = "학습이해도정보Id", required = true, schema = @Schema(type = "integer"))
    @Parameter(name = "subMitAnw", description = "제출답안", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "subMitAnwUrl", description = "제출답안 url", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "errata", description = "정오표", required = false, schema = @Schema(type = "integer"))
    @Parameter(name = "aiTutId", description = "AI튜터ID", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "aiTutUseAt", description = "AI튜터사용여부", required = false, schema = @Schema(type = "integer"))
    @Parameter(name = "aiTutChtCn", description = "AI튜터채팅내용", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "hdwrtCn", description = "손글씨내용", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "hntUseAt", description = "힌트 사용 여부", required = false, schema = @Schema(type = "string"))
    public ResponseDTO<CustomBody> findStntStdAiSubmit(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntStdAiService.findStntStdAiSubmit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI학습(제출-정답확인)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/std/ai/end", method = {RequestMethod.POST})
    @Operation(summary = "AI학습(종료)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vsstu466\"," +
                            "\"stdAiId\":\"44\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> findStntStdAiEnd(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntStdAiService.findStntStdAiEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI학습(종료)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/ai/result", method = {RequestMethod.GET})
    @Operation(summary = "AI학습(결과보기)", description = "AI학습(결과보기)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu466"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "unitNum", description = "단원번호", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> findStntSelflrnAiResult(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntStdAiService.findStntSelflrnAiResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI학습(결과보기)");

    }
}
