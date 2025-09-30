package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.lecture.service.StntCrcuQuizService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(학생) 즉석 퀴즈 API", description = "(학생) 즉석 퀴즈 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntCrcuQuizController {
    private final StntCrcuQuizService stntCrcuQuizService;

    @Loggable
    @RequestMapping(value = "/stnt/tool/quiz/list", method = {RequestMethod.GET})
    @Operation(summary = "학생 퀴즈풀기(목록출력)", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> stntToolQuizList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntCrcuQuizService.findStntToolQuizList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생 퀴즈풀기(목록출력)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/tool/quiz/call", method = {RequestMethod.GET})
    @Operation(summary = "학생 퀴즈풀기(호출)", description = "")
    @Parameter(name = "qizId", description = "퀴즈 ID", required = true, schema = @Schema(type = "integer", example = "3"))
    public ResponseDTO<CustomBody> stntToolQuizCall(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntCrcuQuizService.findStntToolQuizCall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생 퀴즈풀기(호출)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/tool/quiz/submit", method = {RequestMethod.POST})
    @Operation(summary = "학생 퀴즈풀기(제출하기)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"qizId\":3," +
                            "\"submdistrNum\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntToolQuizSubmit(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntCrcuQuizService.createStntToolQuizSubmit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생 퀴즈풀기(제출하기)");

    }
}
