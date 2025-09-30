package com.visang.aidt.lms.api.scenario.controller;

import com.visang.aidt.lms.api.assessment.service.StntEvalService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.EtcService;
import com.visang.aidt.lms.api.scenario.service.ScenarioService;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.dashboard.controller
 * fileName : EtcController
 * USER : kimjh21
 * date : 2024-02-29
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-02-29         kimjh21          최초 생성
 */
@Slf4j
@RestController
@Tag(name = "시나리오용 API", description = "시나리오용 평가, 교과서, 자기주도학습 등")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ScenarioController {

    private final ScenarioService scenarioService;


    @Loggable
    @RequestMapping(value = "/scen/stnt/eval/submit", method = {RequestMethod.POST})
    @Operation(summary = "평가 자료 제출하기(시나리오)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    //@Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"textbookId\":1," +
                            "\"submAt\":\"Y\"," +
                            "\"regDate\":\"2024-02-29 16:03:48\"" +
                            "}"
                    )
            }
            )
    )
    public ResponseDTO<CustomBody> stntEvalSubmit(
        /*
        @RequestParam(name = "evlId", defaultValue = "") String evlId,
        @RequestParam(name = "userId", defaultValue = "") String userId,
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
         */
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = scenarioService.modifyStntEvalSubmit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 자료 제출하기");

    }

    @Loggable
    @RequestMapping(value = "/scen/tch/eval/end", method = {RequestMethod.POST})
    @Operation(summary = "평가 종료 하기", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"timeoutAt\":\"N\"" +
                            "\"timeoutAt\":\"2024-07-01\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalEnd(
            //@RequestParam(name = "evlId", defaultValue = "") String evlId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = scenarioService.modifyEvalEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 종료 하기");

    }

    // 평가 목록 조회
    @Loggable
    @RequestMapping(value = "/scen/stnt/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 목록 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색조건 : 전체/예정(1)/진행중(2)/완료(3)", required = false, schema = @Schema(type = "string", allowableValues = {"","1","2","3"}, example = ""))
    public ResponseDTO<CustomBody> stntEvalList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbookId", defaultValue = "") String textbookId,
            @RequestParam(name = "condition", defaultValue = "") String condition
    )throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("condition", condition);
        Object resultData = scenarioService.findEvalList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 목록 조회(시나리오용)");

    }

    @Loggable
    @RequestMapping(value = "/scen/stnt/lecture/mdul/qstn/save", method = {RequestMethod.POST})
    @Operation(summary = "정답저장(시나리오)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"resultDetailId\":47," +
                            "\"subMitAnw\":\"1\"," +
                            "\"subMitAnwUrl\":\"subMitAnwUrl1\"," +
                            "\"errata\":1," +
                            "\"hntUseAt\":\"Y\"," +
                            "\"regDate\":\"2024-02-29 16:03:48\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntMdulQstnSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = scenarioService.modifyStntMdulQstnSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답저장");

    }

    @Loggable
    @RequestMapping(value =  {"/scen/stnt/self-lrn/end","/scen/stnt/self-lrn/end/eng"} , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 종료 하기(시나리오)", description = "자기주도학습 종료 하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "slfId" : "1",
                    "edAt" : "Y",
                    "regDate" : "2024-02-29 16:03:48"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnEnd( @RequestBody Map<String, Object> paramData) throws Exception {

        Map resultData = scenarioService.saveStntSelfLrnEnd(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 종료 하기");

    }

    @Loggable
    @RequestMapping(value = "/scen/stnt/self-lrn/create/eng" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 생성", description = "자기주도학습 생성")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "emaone1-s2",
                    "textbkId" : "464",
                    "stdCd" : "2",
                    "stdNm" : "listening",
                    "achId" : "1450",
                    "unitNum" : "1",
                    "lvlId" : "ED03",
                    "lvlNm" : "하"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateEng(@RequestBody Map<String, Object> paramData
    ) throws Exception {
//
        List<String> requiredParams = Arrays.asList("userId", "textbkId","stdCd","stdNm","unitNum","lvlId","lvlNm");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = scenarioService.saveStntSelfLrnCreateEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 생성");

    }

    @Loggable
    @RequestMapping(value = "/scen/stnt/self-lrn/similar-question/receive/eng" , method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습 (오답시) 유사문항 받기", description = "자기주도학습 (오답시) 유사문항 받기")
    @Parameter(name = "slfResultId", description = "결과정보 ID", required = true)
    public ResponseDTO<CustomBody> saveStntSelfLrnReceiveEng(
            @RequestParam(name = "slfResultId", defaultValue = "10164") String slfResultId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("slfResultId", slfResultId);

        Object resultData = scenarioService.saveStntSelfLrnReceiveEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 (오답시) 유사문항 받기");

    }

    // 자기주도학습 생성 - 단답형 제외하고 생성되도록
    @Loggable
    @RequestMapping(value = "/scen/stnt/self-lrn/create" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 생성 - 수학 (시나리오)", description = "자기주도학습 생성")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "emaone1-s2",
                    "textbkId" : "16",
                    "stdCd" : "2",
                    "stdNm" : "선택학습",
                    "stdUsdId" : "1450",
                    "unitNum" : "1",
                    "lvlId" : "MD05",
                    "lvlNm" : "하",
                    "kwgMainId" : 1341
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreate(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId", "textbkId","stdCd","stdNm","unitNum","lvlId","lvlNm");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = scenarioService.saveStntSelfLrnCreate(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 생성");

    }


    // 자기주도학습 (오답시) 유사문항 받기 - 단답형 제외로직 추가
    @Loggable
    @RequestMapping(value = "/scen/stnt/self-lrn/similar-question/receive" , method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습 (오답시) 유사문항 받기 - 수학 (시나리오)", description = "자기주도학습 (오답시) 유사문항 받기")
    @Parameter(name = "slfResultId", description = "결과정보 ID", required = true)
    public ResponseDTO<CustomBody> saveStntSelfLrnReceive(
            @RequestParam(name = "slfResultId", defaultValue = "1") String slfResultId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("slfResultId", slfResultId);

        Object resultData = scenarioService.saveStntSelfLrnReceive(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 (오답시) 유사문항 받기");

    }
}
