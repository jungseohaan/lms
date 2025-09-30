package com.visang.aidt.lms.api.act.controller;

import com.visang.aidt.lms.api.act.service.TchActService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "(교사) 활동 도구 API", description = "(교사) 활동 도구 API")
@AllArgsConstructor
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchActController {
    private final TchActService tchActService;

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/start", method = {RequestMethod.POST})
    @Operation(summary = "활동도구 시작하기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "userId": "aidt2",
                                "textbkTabId": 34,
                                "actIemId": "10",
                                "subId": 0,
                            	"actWy": 3,
                            	"actProcCd": 2,
                            	"groupList": [
                                     {"stntId": "student1", "groupId": 1},
                                     {"stntId": "student2", "groupId": 1},
                                     {"stntId": "student3", "groupId": 2},
                                     {"stntId": "student4", "groupId": 2},
                                     {"stntId": "student5", "groupId": 3},
                                     {"stntId": "student6", "groupId": 3},
                                     {"stntId": "student7", "groupId": 4},
                                     {"stntId": "student8", "groupId": 4},
                                     {"stntId": "student9", "groupId": 4}
                                 ]
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchActMdulStart(
            @RequestBody Map<String, Object> paramData
    )throws Exception {


        Object resultData = tchActService.createActToolInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "활동도구 시작하기");

    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/end", method = {RequestMethod.POST})
    @Operation(summary = "교사 > 모듈별 기능 > 활동 도구 : 종료하기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "userId": "aidt2",
                                "actId": 6
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchActMdulEnd(
            @RequestBody Map<String, Object> paramData
    )throws Exception {


        Object resultData = tchActService.modifyActToolInfoEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "활동도구 종료하기");

    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/list", method = {RequestMethod.GET})
    @Operation(summary = "교사 > 모듈별 기능 > 활동 도구 목록조회", description = "")
    @Parameter(name = "textbkTabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "34"))
    @Parameter(name = "actIemId", description = "활동모듈 ID", required = true, schema = @Schema(type = "string", example = "10"))
    @Parameter(name = "subId", description = "서브 ID", required = false, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchActMdulList(
            @RequestParam(name = "textbkTabId", defaultValue = "") String textbkTabId,
            @RequestParam(name = "actIemId", defaultValue = "") String actIemId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchActService.findActToolList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "활동도구 목록조회");


    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/activity/check", method = {RequestMethod.GET})
    @Operation(summary = "교사 > 모듈별 기능 > 활동도구 활동중 체크", description = "")
    @Parameter(name = "textbkTabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "34"))
    @Parameter(name = "actIemId", description = "활동모듈 ID", required = true, schema = @Schema(type = "string", example = "10"))
    @Parameter(name = "subId", description = "서브 ID", required = false, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchActMdulActiveList(
            @RequestParam(name = "textbkTabId", defaultValue = "") String textbkTabId,
            @RequestParam(name = "actIemId", defaultValue = "") String actIemId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        paramData.put("actSttsCd", 1); // 활동중

        Object resultData = tchActService.findActMdulActiveList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "활동도구 활동중 체크");


    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/status", method = {RequestMethod.GET})
    @Operation(summary = "교사 > 모듈별 기능 > 활동 도구 : 제출 현황", description = "")
    @Parameter(name = "textbkTabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "34"))
    @Parameter(name = "actId", description = "활동도구 ID", required = true, schema = @Schema(type = "integer", example = "5"))
    @Parameter(name = "actIemId", description = "활동모듈 ID", required = true, schema = @Schema(type = "string", example = "10"))
    @Parameter(name = "subId", description = "서브 ID", required = false, schema = @Schema(type = "integer", example = "0"))
    //@Parameter(name = "actProcCd", description = "활동진행방식. 1 : 개별, 2 : 짝꿍", required = true, schema = @Schema(type = "integer", example = "1", allowableValues = {"1", "2"}))
    public ResponseDTO<CustomBody> tchActMdulStatus(
            @RequestParam(name = "textbkTabId", defaultValue = "") String textbkTabId,
            @RequestParam(name = "actId", defaultValue = "") String actId,
            @RequestParam(name = "actIemId", defaultValue = "") String actIemId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {


        Object resultData = tchActService.findActMdulStatusList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "활동도구 제출 현황");


    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/status/tab/list", method = {RequestMethod.GET})
    @Operation(summary = "교사 > 모듈별 기능 > 활동 도구 : 탭목록조회", description = "")
    @Parameter(name = "textbkTabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "34"))
    @Parameter(name = "actIemId", description = "활동모듈 ID", required = true, schema = @Schema(type = "string", example = "10"))
    @Parameter(name = "subId", description = "서브 ID", required = false, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchActMdulStatusTabList(
            @RequestParam(name = "textbkTabId", defaultValue = "") String textbkTabId,
            @RequestParam(name = "actIemId", defaultValue = "") String actIemId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {


        Object resultData = tchActService.findActMdulStatusTabList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "활동도구 탭목록조회");


    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/fdb/save", method = {RequestMethod.POST})
    @Operation(summary = "활동결과 피드백 저장(수정)", description = "활동결과 피드백 저장(수정)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "id": 6,
                                "fdbDc": "test",
                                "fdbUrl": "fdbUrl_test"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchActFdbSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {


        Object resultData = tchActService.modifyActToolInfoSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "활동결과 피드백 저장(수정)");

    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/exchange", method = {RequestMethod.POST})
    @Operation(summary = "바꿔보기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "wrterId": "engbook1400-t",
                                "actId": 1472
                            }
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchActMdulExchange(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchActService.modifyTchActMdulExchange(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "바꿔보기");
    }

    @Loggable
    @RequestMapping(value = "/tch/act/mdul/mate", method = {RequestMethod.POST})
    @Operation(summary = "짝꿍맺기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                 "actId": 1472,
                                 "groupList": [
                                     {"stntId": "student1", "groupId": 1},
                                     {"stntId": "student2", "groupId": 1},
                                     {"stntId": "student3", "groupId": 2},
                                     {"stntId": "student4", "groupId": 2},
                                     {"stntId": "student5", "groupId": 3},
                                     {"stntId": "student6", "groupId": 3},
                                     {"stntId": "student7", "groupId": 4},
                                     {"stntId": "student8", "groupId": 4},
                                     {"stntId": "student9", "groupId": 4}
                                 ]
                             }
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchActMdulMate(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchActService.createtchActMdulMate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "짝꿍맺기");
    }
}
