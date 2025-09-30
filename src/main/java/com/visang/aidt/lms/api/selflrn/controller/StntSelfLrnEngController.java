package com.visang.aidt.lms.api.selflrn.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.selflrn.service.StntSelfLrnEngService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "[영어] (학생) 자기주도학습 API", description = "자기주도학습 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntSelfLrnEngController {
    private final StntSelfLrnEngService stntSelfLrnEngService;

    @Loggable
    @GetMapping(value = "/stnt/self-lrn/chapter/concept/list/eng")
    @Operation(summary = "자기주도학습 단원 개념 목록 조회", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "engreal80-s1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "335"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "2137e0fadd2840c79745c9c9769ffdc1"))
    @Parameter(name = "unitNum", description = "단원 번호", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchEvalList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = stntSelfLrnEngService.findStntSelfLrnChapterConceptListEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 단원 개념 목록 조회");
    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/create/eng" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 생성", description = "자기주도학습 생성")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "emaone1-s2",
                    "textbkId" : "464",
                    "claId" : "0cc175b9c0f1b6a831c399e269772661",
                    "stdCd" : "2",
                    "stdNm" : "listening",
                    "achId" : "1450",
                    "unitNum" : "1",
                    "lvlId" : "ED03",
                    "lvlNm" : "하",
                    "metaId" : "35192"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateEng(@RequestBody Map<String, Object> paramData
    ) throws Exception {
//
        List<String> requiredParams = Arrays.asList("userId", "textbkId","stdCd","stdNm","unitNum","lvlId","lvlNm","metaId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = stntSelfLrnEngService.saveStntSelfLrnCreateEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 생성");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/createTest/high" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 - 진단하기 (중고등)", description = "자기주도학습 - 진단하기 (중고등)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "appleeng119-s1",
                    "textbkId" : "1150",
                    "claId" : "0cc175b9c0f1b6a831c399e269772661",
                    "unitNum" : "1",
                    "achId" : "0",
                    "stdCd" : "2",
                    "stdNm" : "Unit2",
                    "metaId" : "35192"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateTestEng(@RequestBody Map<String, Object> paramData
    ) throws Exception {
//
        List<String> requiredParams = Arrays.asList("userId", "textbkId","unitNum");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = stntSelfLrnEngService.saveStntSelfLrnCreateTestEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 - 진단하기 (중고등)");

    }

    @Loggable
    @GetMapping("/stnt/self-lrn/result/summary/eng")
    @Operation(summary = "자기주도학습 결과 보기", description = "자기주도학습 결과 보기")
    @Parameter(name = "slfId", description = "학습 ID", required = true)
    public ResponseDTO<CustomBody> findStntSelfLrnResultSummary(
            @RequestParam(name = "slfId", defaultValue = "1") String slfId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("slfId", slfId);


        Object resultData = this.stntSelfLrnEngService.findStntSelfLrnResultSummaryEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 결과 보기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/similar-question/receive/eng" , method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습 (오답시) 유사문항 받기", description = "자기주도학습 (오답시) 유사문항 받기")
    @Parameter(name = "slfResultId", description = "결과정보 ID", required = true)
    public ResponseDTO<CustomBody> saveStntSelfLrnReceive(
            @RequestParam(name = "slfResultId", defaultValue = "10164") String slfResultId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("slfResultId", slfResultId);

        Object resultData = stntSelfLrnEngService.saveStntSelfLrnReceiveEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 (오답시) 유사문항 받기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/create/elementary/eng" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도학습 - (초등-영어)", description = "자기주도학습 - (초등-영어)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                    {
                        "userId" : "engbook1401-s1",
                        "textbkId" : 6981,
                        "claId" : "3bdf5a9481a9403e8d0a392a47133c72",
                        "stdCd" : 2,
                        "unitNum" : 1,
                        "setId": "11505",
                        "setName": "초5_스스로학습 문장 드릴 세트지 (테스트용)",
                        "evaluationAreaIdx": 4,
                        "evaluationAreaNm": "문장 드릴",
                        "difficulty": 3,
                        "difficultyNm" : "중",
                        "stdCnt" : 0
                    }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnCreateElementaryEng(@RequestBody Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("userId", "textbkId","claId","stdCd","unitNum","setId","setName","evaluationAreaIdx","evaluationAreaNm","difficulty","difficultyNm","stdCnt");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = stntSelfLrnEngService.saveStntSelfLrnCreateElementaryEng(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 - (초등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/unit/eng" , method = {RequestMethod.GET})
    @Operation(summary = "단원별 평균 성취도 (영어)", description = "단원별 평균 성취도 (영어)")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "engreal80-s1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "335"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "2137e0fadd2840c79745c9c9769ffdc1"))
    public ResponseDTO<CustomBody> findStntSelfLrnUnitEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = stntSelfLrnEngService.findStntSelfLrnUnitEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "단원별 평균 성취도 (영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/sets/elementary/eng" , method = {RequestMethod.GET})
    @Operation(summary = "자기주도학습 -활동별 세트지 조회- (초등-영어)", description = "자기주도학습 -활동별 세트지 조회- (초등-영어)")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "engreal80-s1"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "6981"))
    @Parameter(name = "unitNum", description = "단원", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> findStntSelfLrnEvaluationListElementaryEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        // 샘플데이터만 생성되어 있음. (2025.04.01)
        // set id 에 설정된 article 의 article_meta_map 데이가 없음.
        // 유사 문항 테스트 불가. 정식 콘텐츠 생성 후 테스트 필요.
        Object resultData = stntSelfLrnEngService.findStntSelfLrnSetsElementaryEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도학습 -활동별 세트지 조회- (초등-영어)");
    }
}
