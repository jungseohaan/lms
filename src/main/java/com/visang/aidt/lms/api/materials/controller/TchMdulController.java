package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.TchMdulService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * (교사) 모듈 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 모듈 API", description = "(교사) 모듈 API")
//@Api(tags = "(교사) 모듈 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchMdulController {
    private final TchMdulService tchMdulService;

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/note/save", method = {RequestMethod.POST})
    @Operation(summary = "(교사)손글씨 저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1," +
                            "\"textbkNm\":\"1\"," +
                            "\"tabId\":1," +
                            "\"moduleId\":\"1\"," +
                            "\"subId\":1," +
                            "\"hdwrtCn\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveTchMdulNote(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchMdulService.saveTchMdulNote(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)손글씨 저장");
    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/note/view", method = {RequestMethod.POST})
    @Operation(summary = "(교사)손글씨 호출")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1," +
                            "\"tabId\":1," +
                            "\"moduleId\":\"0\"," +
                            "\"subId\":0" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchLectureMdulNoteView(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = tchMdulService.tchLectureMdulNoteView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)손글씨 저장");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/note/share", method = {RequestMethod.POST})
    @Operation(summary = "판서(교사 필기)학생 공유")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"notId\":1," +
                            "\"noteImgUrl\":\"urlurl\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveTchMdulNoteShare(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = tchMdulService.saveTchMdulNoteShare(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "판서(교사 필기)학생 공유");

    }

/*
    //@ApiOperation(value = "피드백 보내기", notes = "")
    @RequestMapping(value = "/tch/mdul/feedback", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchMdulFeedback() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchMdulService.createFeedback(paramData);
        String resultMessage = "피드백 보내기";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "질문 목록 조회", notes = "")
    @RequestMapping(value = "/tch/mdul/quest/list", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchMdulQeuestList() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchMdulService.findMdulQuestList(paramData);
        String resultMessage = "질문 목록 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "질문 댓글 달기", notes = "")
    @RequestMapping(value = "/tch/mdul/quest/comment", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchMdulQeuestComment() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchMdulService.createQuestComment(paramData);
        String resultMessage = "질문 댓글 달기";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
 */
}
