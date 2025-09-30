package com.visang.aidt.lms.api.engtemp.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.engtemp.service.AssessmentService;
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

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "영어템플릿 학습 결과(평가)", description = "숙제 영어템플릿 학습 결과")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AssessmentController {

    private final AssessmentService assessmentService;

    @Loggable
    @RequestMapping(value = "/stnt/assessment/engtemp/start", method = {RequestMethod.POST})
    @Operation(summary = "(평가)교과템플릿 시작", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"evlResultDetailId\":1397," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34622" +
                            "}"
                    )
            }
    ))
    public ResponseDTO<CustomBody> assessmentStart(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        if (MapUtils.getInteger(paramData, "evlResultDetailId", 0) == 0 ||
                MapUtils.getInteger(paramData, "engTempId", 0) == 0 ||
                MapUtils.getInteger(paramData, "scriptId", 0) == 0 ||
                MapUtils.getInteger(paramData, "tmpltActvId", 0) == 0) {
            return AidtCommonUtil.makeResultFail(paramData, null, "필수값 누락");
        }
        Map<String, Object> resultMap = assessmentService.insertAssessment(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/stnt/assessment/engtemp/isstudy", method = {RequestMethod.POST})
    @Operation(summary = "(평가)교과템플릿 활동 여부", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"evlResultDetailId\":1397," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvIds\":\"34622,34618,34619\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> assessmentIsStudy(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = assessmentService.selectAssessmentIsStudy(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "(평가)교과템플릿 활동 여부");
    }

    @Loggable
    @RequestMapping(value = "/stnt/assessment/engtemp/end", method = {RequestMethod.POST})
    @Operation(summary = "(평가)교과템플릿 종료", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"engTempResultId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> assessmentEnd(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        if (MapUtils.getInteger(paramData, "engTempResultId", 0) == 0) {
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 값 누락");
        }
        Map<String, Object> resultMap = assessmentService.updateAssessment(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/stnt/assessment/engtemp/answer", method = {RequestMethod.POST})
    @Operation(summary = "(평가)템플릿 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"engTempResultId\":1," +
                                "\"libtextId\":1," +
                                "\"libtextDialogId\":1," +
                                "\"articleId\":1," +
                                "\"dfcltLvlYy\":1," +
                                "\"anwInptTy\":1," +
                                "\"tmpltDtlActvVl\":\"string\"," +
                                "\"errata\":1," +
                                "\"subMitAnw\":\"string\"," +
                                "\"subMitAnwUrl\":\"string\"," +
                                "\"notUdstdTf\":1," +
                                "\"aitutorRslt\":\"string\"," +
                                "\"eakStDt\":\"string\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> assessmentAnswer(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        if (MapUtils.getInteger(paramData, "engTempResultId", 0) == 0 ||
                (StringUtils.isEmpty(MapUtils.getString(paramData, "subMitAnw", "")) && StringUtils.isEmpty(MapUtils.getString(paramData, "subMitAnwUrl", ""))) ||
                (MapUtils.getInteger(paramData, "libtextId", 0) == 0 && MapUtils.getInteger(paramData, "articleId", 0) == 0) ||
                MapUtils.getInteger(paramData, "errata", 0) == 0) {
            return AidtCommonUtil.makeResultFail(paramData, null, "필수 값 누락");
        }
        Map<String, Object> resultMap = assessmentService.insertAssessmentResultDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/assessment/engtemp/notudstdcnt", method = {RequestMethod.POST})
    @Operation(summary = "(평가)교과템플릿 word 이해못한 인원 카운트(클래스단위)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"evlId\":5059," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34619" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> assessmentNotUdstdCnt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultList = assessmentService.getAssessmentNotUdstdCnt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/assessment/engtemp/rsltrlsat", method = {RequestMethod.POST})
    @Operation(summary = "(평가)교과템플릿 결과공개 여부 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"engTempResultId\":1," +
                                "\"rsltRlsAt\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> assessmentRsltRlsAt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = assessmentService.updateAssessmentRsltRlsAt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/assessment/engtemp/useransw", method = {RequestMethod.POST})
    @Operation(summary = "(평가)교과템플릿 학생답 전달", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"engTempResultId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> assessmentUserAnswer(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultList = assessmentService.getAssessmentUserAnswer(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/assessment/engtemp/sbmtInfo", method = {RequestMethod.POST})
    @Operation(summary = "(평가)교과템플릿 제출인원, 총인원, 제출률", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1341," +
                            "\"engTempId\":8," +
                            "\"scriptId\":1743," +
                            "\"tmpltActvId\":34622" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> assessmentSubmitInfo(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = assessmentService.getAssessmentSubmitInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }
}
