package com.visang.aidt.lms.api.engtemp.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.engtemp.service.HomeworkService;
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
@Tag(name = "영어템플릿 학습 결과(숙제)", description = "숙제 영어템플릿 학습 결과")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class HomeworkController {

    private final HomeworkService homeworkService;

    @Loggable
    @RequestMapping(value = "/stnt/homewk/engtemp/start", method = {RequestMethod.POST})
    @Operation(summary = "(숙제)교과템플릿 시작", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"taskResultDetailId\":1397," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvId\":34622," +
                                "\"lessonId\":\"VOCA에서 사용\"," +
                                "\"tmpltTy\":\"VOCA에서 사용\"" +
                            "}"
                    )
            }
    ))
    public ResponseDTO<CustomBody> homeworkStart(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = homeworkService.insertHomework(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/engtemp/question", method = {RequestMethod.POST})
    @Operation(summary = "(숙제)템플릿 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"taskEngTempResultId\":9399," +
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
    public ResponseDTO<CustomBody> homeworkQuestion(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = homeworkService.insertHomeworkQuestion(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/engtemp/isstudy", method = {RequestMethod.POST})
    @Operation(summary = "(숙제)교과템플릿 활동 여부", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"taskId\":1397," +
                                "\"engTempId\":17," +
                                "\"scriptId\":2334," +
                                "\"tmpltActvIds\":\"34622,34618,34619\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> homeworkIsStudy(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = homeworkService.selectHomeworkIsStudy(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "(숙제)교과템플릿 활동 여부");
    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/engtemp/answer", method = {RequestMethod.POST})
    @Operation(summary = "(숙제)템플릿 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "[" +
                                "{" +
                                    "\"taskEngTempResultDetailId\":1," +
                                    "\"errata\":1," +
                                    "\"subMitAnw\":\"string\"," +
                                    "\"subMitAnwUrl\":\"string\"," +
                                    "\"notUdstdTf\":1," +
                                    "\"aitutorRslt\":\"string\"," +
                                    "\"lastVoiceFileId\":1" +
                                "}," +
                                "{" +
                                    "\"taskEngTempResultDetailId\":1," +
                                    "\"errata\":1," +
                                    "\"subMitAnw\":\"string\"," +
                                    "\"subMitAnwUrl\":\"string\"," +
                                    "\"notUdstdTf\":1," +
                                    "\"aitutorRslt\":\"string\"," +
                                    "\"lastVoiceFileId\":1" +
                                "}," +
                                "{" +
                                    "\"taskEngTempResultDetailId\":1," +
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
    public ResponseDTO<CustomBody> homeworkAnswer(
            @RequestBody List<Map<String, Object>> paramData
    ) throws Exception {
        Map<String, Object> resultMap = homeworkService.updateHomeworkResultDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/engtemp/notudstdcnt", method = {RequestMethod.POST})
    @Operation(summary = "(숙제)교과템플릿 word 이해못한 인원 카운트(클래스단위)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"taskId\":1341," +
                                "\"engTempId\":8," +
                                "\"scriptId\":1743," +
                                "\"tmpltActvId\":34619" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> homeworkNotUdstdCnt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = homeworkService.getHomeworkNotUdstdCnt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/engtemp/useransw", method = {RequestMethod.POST})
    @Operation(summary = "(숙제)교과템플릿 학생답 전달", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"engTempResultId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> homeworkUserAnswer(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultList = homeworkService.getHomeworkUserAnswer(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/engtemp/sbmtInfo", method = {RequestMethod.POST})
    @Operation(summary = "(숙제)교과템플릿 제출인원, 총인원, 제출률", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":1341," +
                            "\"engTempId\":8," +
                            "\"scriptId\":1743," +
                            "\"tmpltActvId\":34622" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> homeworkSubmitInfo(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = homeworkService.getHomeworkSubmitInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "성공");
    }
}
