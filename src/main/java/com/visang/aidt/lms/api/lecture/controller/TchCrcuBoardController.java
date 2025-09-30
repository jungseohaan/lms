package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.lecture.service.TchCrcuBoardService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 커리큘럼 의견보드 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 보드 API", description = "(교사) 보드 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)

public class TchCrcuBoardController {
    private final TchCrcuBoardService tchCrcuBoardService;

    @Loggable
    @RequestMapping(value = "/tch/tool/board/save", method = {RequestMethod.POST})
    @Operation(summary = "의견보드(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1," +
                            "\"brdCd\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchToolBoardSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuBoardService.createTchToolBoardSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "의견보드(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/board/call", method = {RequestMethod.GET})
    @Operation(summary = "의견보드(호출)", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchToolBoardCall(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuBoardService.findTchToolBoardCall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "의견보드(호출)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/whiteboard/save", method = {RequestMethod.POST})
    @Operation(summary = "화이트보드(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1," +
                            "\"brdSeq\":1," +
                            "\"brdCn\":\"brdCn1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchToolWhiteboardSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuBoardService.createTchToolWhiteboardSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "화이트보드(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/whiteboard/list", method = {RequestMethod.GET})
    @Operation(summary = "화이트보드(리스트)", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchToolWhiteboardList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuBoardService.findTchToolWhiteboardList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "화이트보드(리스트)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/whiteboard/call", method = {RequestMethod.GET})
    @Operation(summary = "화이트보드(호출)", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "brdSeq", description = "보드순서", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchToolWhiteboardCall(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuBoardService.findTchToolWhiteboardCall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "화이트보드(호출)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/whiteboard/modify", method = {RequestMethod.POST})
    @Operation(summary = "화이트보드(갱신)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1," +
                            "\"brdSeq\":1," +
                            "\"brdCn\":\"brdCn1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchToolWhiteboardModify(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuBoardService.modifyTchToolWhiteboardModify(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "화이트보드(갱신)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/whiteboard/del", method = {RequestMethod.POST})
    @Operation(summary = "화이트보드(삭제)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchToolWhiteboardDel(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuBoardService.removeTchToolWhiteboardDel(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "화이트보드(삭제)");

    }
}
