package com.visang.aidt.lms.api.engtemp.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.engtemp.service.LesnRscService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "영어템플릿 학습 결과(2View)", description = "2View 영어템플릿 학습 결과")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class LesnRscController {

    private final LesnRscService lesnRscService;

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/engtemp/start", method = {RequestMethod.POST})
    @Operation(summary = "교과템플릿 시작(선생님)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"textbkTabId\":5059," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34619," +
                                "\"lessonId\":\"VOCA에서 사용\"," +
                                "\"tmpltTy\":\"VOCA에서 사용\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscStart(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        lesnRscService.updateLesnRsc(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, null, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/engtemp/isstudy", method = {RequestMethod.POST})
    @Operation(summary = "교과템플릿 활동 여부(선생님)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"textbkTabId\":5059," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvIds\":\"34618,34619,34622\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscIsStudy(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = lesnRscService.selectLesnRscIsStudy(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "(2View)교과템플릿 활동 여부");
    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/engtemp/end", method = {RequestMethod.POST})
    @Operation(summary = "교과템플릿 종료(선생님)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"textbkTabId\":5059," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34619," +
                                "\"lessonId\":\"VOCA에서 사용\"," +
                                "\"tmpltTy\":\"VOCA에서 사용\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscEnd(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        lesnRscService.updateLesnRscEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, null, "성공");
    }

    @Loggable
    @RequestMapping(value = "/stnt/lesn-rsc/engtemp/question", method = {RequestMethod.POST})
    @Operation(summary = "교과템플릿 문제 내려주기(학생)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"resultDetailId\":9399," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34618," +
                                "\"lessonId\":2334," +
                                "\"tmpltTy\":2334," +
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
    public ResponseDTO<CustomBody> lesnRscQuestion(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultMap = lesnRscService.insertLesnRscQuestion(paramData);
        if (MapUtils.getBoolean(resultMap, "success", true)) {
            return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
        } else {
            return AidtCommonUtil.makeResultFail(paramData, resultMap, "데이터가 없습니다.");
        }

    }

    @Loggable
    @RequestMapping(value = "/stnt/lesn-rsc/engtemp/answer", method = {RequestMethod.POST})
    @Operation(summary = "(2View)교과템플릿 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "[" +
                                "{" +
                                    "\"engTempResultDetailId\":1," +
                                    "\"errata\":1," +
                                    "\"subMitAnw\":\"string\"," +
                                    "\"subMitAnwUrl\":\"string\"," +
                                    "\"notUdstdTf\":1," +
                                    "\"aitutorRslt\":\"string\"," +
                                    "\"lastVoiceFileId\":1" +
                                "}," +
                                "{" +
                                    "\"engTempResultDetailId\":1," +
                                    "\"errata\":1," +
                                    "\"subMitAnw\":\"string\"," +
                                    "\"subMitAnwUrl\":\"string\"," +
                                    "\"notUdstdTf\":1," +
                                    "\"aitutorRslt\":\"string\"," +
                                    "\"lastVoiceFileId\":1" +
                                "}," +
                                "{" +
                                    "\"engTempResultDetailId\":1," +
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
    public ResponseDTO<CustomBody> lesnRscAnswer(
            @RequestBody List<Map<String, Object>> paramData
    ) throws Exception {

        Map<String, Object> resultMap = lesnRscService.updateLesnRscResultDetail(paramData);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("paramData", paramData);

        return AidtCommonUtil.makeResultSuccess(paramMap, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/engtemp/deadline", method = {RequestMethod.POST})
    @Operation(summary = "(2View)교과템플릿 마감하기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"textbkTabId\":5059," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34619" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscDdln(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultMap = lesnRscService.updateLesnRscDdln(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }


//    @RequestMapping(value = "/tch/lesn-rsc/engtemp/rsltrlsat", method = {RequestMethod.POST})
//    @Operation(summary = "(2View)교과템플릿 결과공개 여부 변경", description = "")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            content = @Content(examples = {
//                    @ExampleObject(name = "파라미터", value =
//                            "{" +
//                                "\"engTempResultId\":1," +
//                                "\"rsltRlsAt\":,\"Y\"" +
//                            "}"
//                    )
//            }
//            ))
//    public ResponseDTO<CustomBody> lesnRscRslt(
//            @RequestBody Map<String, Object> paramData
//    ) throws Exception {
//        Map<String, Object> resultMap = lesnRscService.updateLesnRscRsltRlsAt(paramData);
//        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
//    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/engtemp/notudstdcnt", method = {RequestMethod.POST})
    @Operation(summary = "(2View)교과템플릿 word 이해못한 인원 카운트(클래스단위)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"textbkTabId\":5059," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34619" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscNotUdstdCnt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultList = lesnRscService.getLesnRscNotUdstdCnt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/engtemp/useransw", method = {RequestMethod.POST})
    @Operation(summary = "교과템플릿 학생답 전달(학생)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"engTempResultId\":52" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscUserAnswer(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultList = lesnRscService.getLesnRscUserAnswer(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/lesn-rsc/engtemp/sbmtInfo", method = {RequestMethod.POST})
    @Operation(summary = "교과템플릿 제출인원별 정답, 총인원, 제출률(선생님)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                        "{" +
                            "\"textbkTabId\":5059," +
                            "\"engTempId\":17," +
                            "\"scriptId\":2334," +
                            "\"tmpltActvId\":34618" +
                        "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> lesnRscSubmitInfo(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = lesnRscService.getLesnRscSubmitInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }
}
