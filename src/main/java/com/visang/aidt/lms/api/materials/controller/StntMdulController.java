package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.StntMdulService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * (학생) 모듈 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(학생) 모듈 API", description = "(학생) 모듈 API")
//@Api(tags = "(학생) 모듈 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntMdulController {
    private final StntMdulService stntMdulService;


    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/note/save", method = {RequestMethod.POST})
    @Operation(summary = "손글씨 저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"resultDetailId\":\"1\"," +
                            "\"hdwrtCn\":\"testtestHHEeessstest\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> updateStntMdulNote(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = stntMdulService.updateStntMdulNote(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "손글씨 저장");
    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/note/view", method = {RequestMethod.GET})
    @Operation(summary = "손글씨 호출", description = "손글씨 호출")
    @Parameter(name = "resultDetailId", description = "학습자료결과상세ID", required = true, schema = @Schema(type = "string", example = "47"))
    public ResponseDTO<CustomBody> getStntMdulNoteView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulService.getStntMdulNoteView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "손글씨 호출");
    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/note/share", method = {RequestMethod.GET})
    @Operation(summary = "판서(교사 필기)공유 목록 조회", description = "판서(교사 필기)공유 목록 조회")
    @Parameter(name = "moduleId", description = "모듈id", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "tabId", description = "탭id", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getStntMdulNoteShare(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulService.getStntMdulNoteShare(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "판서(교사 필기)공유 목록 조회");
    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/fdb/share", method = {RequestMethod.GET})
    @Operation(summary = "교사 피드백 보기", description = "교사 피드백 보기")
    @Parameter(name = "resultDetailId", description = "학습자료결과상세ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getStntMdulFdbShared(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulService.getStntMdulFdbShare(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 피드백 보기");
    }

    @Loggable
    @RequestMapping(value = "/stnt/lecture/mdul/exlt/share", method = {RequestMethod.GET})
    @Operation(summary = "우수답안보기", description = "우수답안보기")
    @Parameter(name = "resultDetailId", description = "학습자료결과상세ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getStntMdulExltShared(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulService.getStntMdulExltShared(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우수답안보기");
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
