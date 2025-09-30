package com.visang.aidt.lms.api.engtemp.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.engtemp.service.SelfLrnService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "영어템플릿 학습 결과(자기주도학습)", description = "자기주도학습 영어템플릿 학습 결과")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SelfLrnController {

    private final SelfLrnService selfLrnService;

    @Loggable
    @RequestMapping(value = "/tch/self-lrn/engtemp/start", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)교과템플릿 시작", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                        "{" +
                            "\"selfResultDetailId\":1," +
                            "\"engTempId\":1," +
                            "\"scriptId\":1," +
                            "\"tmpltActvId\":1" +
                        "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> selfLrnStart(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        if (MapUtils.getInteger(paramData, "selfResultDetailId", 0) == 0
            || MapUtils.getInteger(paramData, "engTempId", 0) == 0
            || MapUtils.getInteger(paramData, "scriptId", 0) == 0
            || MapUtils.getInteger(paramData, "tmpltActvId", 0) == 0) {
            return AidtCommonUtil.makeResultFail(paramData, null, "필수값 누락");
        }
        resultMap.put("result", selfLrnService.insertSelfLrn(paramData));
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "");
    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/engtemp/question", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)템플릿 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                    "\"selfEngTempResultId\":9399," +
                                    "\"engTempId\":17," +
                                    "\"scriptId\":2334," +
                                    "\"tmpltActvId\":34618," +
                                    "\"libtextId\":21321," +
                                    "\"libtextDialogId\":32131," +
                                    "\"pkey\":213," +
                                    "\"skey\":231," +
                                    "\"skeys\":\"1,2,3,4\"," +
                                    "\"articleId\":\"1\"," +
                                    "\"dfcltLvlTy\":1," +
                                    "\"anwInptTy\":1," +
                                    "\"tmpltDtlActvVl\":\"\"" +
                                    "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> selfLrnQuestion(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = selfLrnService.insertSelfLrnQuestion(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/self-lrn/engtemp/isstudy", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)교과템플릿 활동 여부", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"resultDetailId\":1," +
                                "\"engTempId\":1," +
                                "\"scriptId\":1," +
                                "\"tmpltActvIds\":\"34604,34609,34610,34612,34613,34614,34615,34616,34617\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscIsStudy(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = selfLrnService.selectSelfLrnIsStudy(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "(자기주도)교과템플릿 활동 여부");
    }

    @Loggable
    @RequestMapping(value = "/tch/self-lrn/engtemp/end", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)교과템플릿 종료", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"engTempResultId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> selfLrnEnd(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        if (MapUtils.getInteger(paramData, "engTempResultId", 0) == 0) {
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 값 누락");
        }
        Map<String, Object> resultMap = selfLrnService.updateSelfLrn(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/engtemp/answer", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)교과템플릿 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "[" +
                                "{" +
                                    "\"selfEngTempResultDetailId\":1," +
                                    "\"errata\":1," +
                                    "\"subMitAnw\":\"string\"," +
                                    "\"subMitAnwUrl\":\"string\"," +
                                    "\"notUdstdTf\":1," +
                                    "\"aitutorRslt\":\"string\"," +
                                    "\"lastVoiceFileId\":1" +
                                "}," +
                                "{" +
                                    "\"selfEngTempResultDetailId\":1," +
                                    "\"errata\":1," +
                                    "\"subMitAnw\":\"string\"," +
                                    "\"subMitAnwUrl\":\"string\"," +
                                    "\"notUdstdTf\":1," +
                                    "\"aitutorRslt\":\"string\"," +
                                    "\"lastVoiceFileId\":1" +
                                "}," +
                                "{" +
                                    "\"selfEngTempResultDetailId\":1," +
                                    "\"errata\":1," +
                                    "\"subMitAnw\":\"string\"," +
                                    "\"subMitAnwUrl\":\"string\"," +
                                    "\"notUdstdTf\":1," +
                                    "\"aitutorRslt\":\"string\"," +
                                    "\"lastVoiceFileId\":1" +
                                "}" +
                            "]"
                    )
            }
            ))
    public ResponseDTO<CustomBody> selfLrnAnswer(
            @RequestBody List<Map<String, Object>> paramData
    ) throws Exception {
        Map<String, Object> resultMap = selfLrnService.updateSelfLrnResultDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }


//    @RequestMapping(value = "/tch/self-lrn/engtemp/deadline", method = {RequestMethod.POST})
//    @Operation(summary = "(자기주도)교과템플릿 마감하기", description = "")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            content = @Content(examples = {
//                    @ExampleObject(name = "파라미터", value =
//                            "{" +
//                                "\"resultDetailId\":1," +
//                                "\"engTempId\":1," +
//                                "\"scriptId\":1," +
//                                "\"tmpltActvId\":1" +
//                            "}"
//                    )
//            }
//            ))
//    public ResponseDTO<CustomBody> selfLrnDdln(
//            @RequestBody Map<String, Object> paramData
//    ) throws Exception {
//
//        Map<String, Object> resultMap = selfLrnService.updateSelfLrnDdln(paramData);
//
//        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
//    }

    @Loggable
    @RequestMapping(value = "/tch/self-lrn/engtemp/rsltrlsat", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)교과템플릿 결과공개 여부 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"engTempResultId\":1," +
                                "\"rsltRlsAt\":,\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> selfLrnRsltRlsAt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultMap = selfLrnService.updateSelfLrnRsltRlsAt(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/self-lrn/engtemp/notudstdcnt", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)교과템플릿 word 이해못한 인원 카운트(클래스단위)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"resultDetailId\":1," +
                                "\"engTempId\":1," +
                                "\"scriptId\":1," +
                                "\"tmpltActvId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> selfLrnNotUdstdCnt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultList = selfLrnService.getSelfLrnNotUdstdCnt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/self-lrn/engtemp/useransw", method = {RequestMethod.POST})
    @Operation(summary = "(자기주도)교과템플릿 학생답 전달", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"engTempResultId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> selfLrnUserAnswer(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultList = selfLrnService.getSelfLrnUserAnswer(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "성공");
    }
}
