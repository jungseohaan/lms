package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.StntMdulQstnService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(학생) 수업 중", description = "(학생) 수업 중")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntMdulQstnController {
    private final StntMdulQstnService stntMdulQstnService;

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/save", method = {RequestMethod.POST})
    @Operation(summary = "정답저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"resultDetailId\":47," +
                            "\"subMitAnw\":\"1\"," +
                            "\"subMitAnwUrl\":\"subMitAnwUrl1\"," +
                            "\"errata\":1," +
                            "\"hntUseAt\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntMdulQstnSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntMdulQstnService.modifyStntMdulQstnSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/recheck", method = {RequestMethod.POST})
    @Operation(summary = "재확인횟수저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vsstu566\"," +
                            "\"textbkTabId\":1820," +
                            "\"setsId\":\"9\"," +
                            "\"qstnList\":[" +
                            "{" +
                            "\"articleId\":\"47\"," +
                            "\"subId\":0," +
                            "\"articleTypeSttsCd\":1" +
                            "}" +
                            ",{" +
                            "\"articleId\":\"45\"," +
                            "\"subId\":1," +
                            "\"articleTypeSttsCd\":1" +
                            "}" +
                            ",{" +
                            "\"articleId\":\"62\"," +
                            "\"subId\":1," +
                            "\"articleTypeSttsCd\":1" +
                            "}" +
                            "]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntMdulQstnRecheck(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntMdulQstnService.createStntMdulQstnRecheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "재확인횟수저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/view/bak", method = {RequestMethod.GET})
    @Operation(summary = "정답있는모듈상세조회", description = "")
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "8813"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "re22mma14-s2"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "204"))
    public ResponseDTO<CustomBody> stntMdulQstnViewBak(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntMdulQstnService.findStntMdulQstnViewBak(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답있는모듈상세조회");

    }


    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/view", method = {RequestMethod.GET})
    @Operation(summary = "정답있는모듈상세조회", description = "")
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "8813"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "re22mma14-s2"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "204"))
    public ResponseDTO<CustomBody> stntMdulQstnView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = stntMdulQstnService.findStntMdulQstnView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답있는모듈상세조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/other", method = {RequestMethod.POST})
    @Operation(summary = "다른문제풀기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"detailId\":47," +
                            "\"articleId\":\"1433\"," +
                            "\"limitNum\":\"3\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntMdulQstnOther(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntMdulQstnService.createStntMdulQstnOther(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "다른문제풀기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/answ", method = {RequestMethod.GET})
    @Operation(summary = "정답확인", description = "")
    @Parameter(name = "resultDetailId", description = "학습자료결과상세 ID", required = true, schema = @Schema(type = "integer", example = "47"))
    public ResponseDTO<CustomBody> stntMdulQstnAnsw(
            @RequestParam(name = "resultDetailId", defaultValue = "") String resultDetailId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntMdulQstnService.findStntMdulQstnAnsw(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답확인");

    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/result-info", method = {RequestMethod.GET})
    @Operation(summary = "모듈수업결과조회", description = "정답확인이 있는모듈>정답확인 해당 모듈활동 상세 출력")
    @Parameter(name = "detailId", description = "자료결과ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> stntMdulQstnResultinfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntMdulQstnService.stntMdulQstnResultinfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "모듈수업결과조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/qstn/other/check", method = {RequestMethod.GET})
    @Operation(summary = "다른문제풀기 사전 체크", description = "")
    @Parameter(name = "detailId", description = "자료결과ID", required = true, schema = @Schema(type = "integer", example = "54"))
    @Parameter(name = "articleId", description = "자료항목ID", required = true, schema = @Schema(type = "string", example = "3099"))
    public ResponseDTO<CustomBody> stntMdulQstnOtherCheck(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntMdulQstnService.findStntMdulQstnOtherCheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "다른문제풀기 사전 체크");

    }
}
