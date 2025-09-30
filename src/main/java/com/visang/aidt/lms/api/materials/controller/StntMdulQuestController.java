package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.StntMdulQuestService;
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
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.materials.controller
 * fileName : StntMdulQuestController
 * USER : hs84
 * date : 2024-01-23
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-23         hs84          최초 생성
 */
@Slf4j
@RestController
@Tag(name = "(학생) 질문 API", description = "(학생) 질문 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntMdulQuestController {
    private final StntMdulQuestService stntMdulQuestService;

    @Loggable
    @RequestMapping(value = {"/stnt/mdul/quest/list", "/tch/mdul/quest/list"}, method = {RequestMethod.GET})
    @Operation(summary = "질문보기", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subId", description = "연쇄형 서브 문항 idx", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> stntMdulQuestList(
            HttpServletRequest request,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "tabId", defaultValue = "") String tabId,
            @RequestParam(name = "moduleId", defaultValue = "") String moduleId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulQuestService.findStntMdulQuestList(paramData, request.getRequestURI());
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "질문보기");

    }

    @Loggable
    @RequestMapping(value = {"/stnt/mdul/quest", "/tch/mdul/quest"}, method = {RequestMethod.POST})
    @Operation(summary = "질문하기", description = "")
    /*
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "qestnCn", description = "질문내용", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "rcveId", description = "수신자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "anmAt", description = "익명여부", required = true, schema = @Schema(type = "string", allowableValues = {"Y","N"}, defaultValue = "N" ))
    @Parameter(name = "otoQestnAt", description = "1:1질문여부", required = true, schema = @Schema(type = "string", allowableValues = {"Y","N"}, defaultValue = "N" ))
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"tabId\":\"1\"," +
                            "\"moduleId\":\"1\"," +
                            "\"subId\":\"0\"," +
                            "\"qestnCn\":\"\"," +
                            "\"rcveId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"anmAt\":\"N\"," +
                            "\"otoQestnAt\":\"N\"," +
                            "\"textbkId\":1," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntMdulQuest(
            /*
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "tabId", defaultValue = "") String tabId,
            @RequestParam(name = "moduleId", defaultValue = "") String moduleId,
            @RequestParam(name = "qestnCn", defaultValue = "") String qestnCn,
            @RequestParam(name = "rcveId", defaultValue = "") String rcveId,
            @RequestParam(name = "anmAt", defaultValue = "") String anmAt,
            @RequestParam(name = "otoQestnAt", defaultValue = "") String otoQestnAt,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
             */
            HttpServletRequest request,
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulQuestService.createStntMdulQeust(paramData, request.getRequestURI());
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "질문하기");

    }

    @Loggable
    @RequestMapping(value = {"/stnt/mdul/quest/comment", "/tch/mdul/quest/comment"}, method = {RequestMethod.POST})
    @Operation(summary = "질문 댓글달기", description = "")
    /*
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "qestnId", description = "질문 ID", required = true, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "answCn", description = "댓글내용", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "anmAt", description = "익명여부", required = true, schema = @Schema(type = "string", allowableValues = {"Y","N"}, defaultValue = "N" ))
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"qestnId\":\"\"," +
                            "\"answCn\":\"\"," +
                            "\"anmAt\":\"N\"," +
                            "\"textbkId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntMdulQuestComment(
            /*
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "qestnId", defaultValue = "") String qestnId,
            @RequestParam(name = "answCn", defaultValue = "") String answCn,
            @RequestParam(name = "anmAt", defaultValue = "") String anmAt,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
             */
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulQuestService.createStntMdulQeustComment(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "질문 댓글달기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/mdul/quest/readall", method = {RequestMethod.POST})
    @Operation(summary = "질문하기(읽음처리)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vstea1\"," +
                            "\"tabId\":44," +
                            "\"subId\":\"0\"," +
                            "\"moduleId\":\"441\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntMdulQuestReadall(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulQuestService.modifyStntMdulQuestReadall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "질문하기(읽음처리)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/mdul/quest/call", method = {RequestMethod.GET})
    @Operation(summary = "질문하기(읽지않은갯수)", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "vstea1"))
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "44"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "string", example = "441"))
    @Parameter(name = "subId", description = "연쇄형 서브 문항 idx", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> stntMdulQuestCall(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "tabId", defaultValue = "") String tabId,
            @RequestParam(name = "moduleId", defaultValue = "") String moduleId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulQuestService.findStntMdulQuestCall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "질문하기(읽지않은갯수)");

    }

    @Loggable
    @RequestMapping(value = "/tch/mdul/quest/readall", method = {RequestMethod.POST})
    @Operation(summary = "질문하기(읽음처리)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vsstu1\"," +
                            "\"tabId\":44," +
                            "\"subId\":\"0\"," +
                            "\"moduleId\":\"441\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulQuestReadall(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulQuestService.modifyTchMdulQuestReadall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "질문하기(읽음처리)");

    }

    @Loggable
    @RequestMapping(value = "/tch/mdul/quest/call", method = {RequestMethod.GET})
    @Operation(summary = "질문하기 (읽지않은갯수)", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "44"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "string", example = "441"))
    @Parameter(name = "subId", description = "연쇄형 서브 문항 idx", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchMdulQuestCall(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "tabId", defaultValue = "") String tabId,
            @RequestParam(name = "moduleId", defaultValue = "") String moduleId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulQuestService.findTchMdulQuestCall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "질문하기 (읽지않은갯수)");

    }
}
