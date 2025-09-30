package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.TchToolsService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 수업도구 API Controller
 */
@RestController
//@Api(tags = "(교사) 수업도구 API")
@Tag(name = "(교사) 수업도구 API", description = "(교사) 수업도구 API")
@Slf4j
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchToolsController {
    private final TchToolsService tchToolsService;

    @Loggable
    //@ApiOperation(value = "교과 도구 정보 조회", notes = "")
    //@RequestMapping(value = "/tch/tools/info", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchToolsInfo() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchToolsService.findInitToolsList(paramData);
        String resultMessage = "교과 도구 정보 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    //@ApiOperation(value = "교과/학생 도구 관리 수정하기", notes = "")
    //@RequestMapping(value = "/tch/tools", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchTools() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchToolsService.modifyToolsList(paramData);
        String resultMessage = "교과/학생 도구 관리 수정하기";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping( value = {"/stnt/tool/bar/call","/tch/tool/bar/call"}, method = {RequestMethod.GET})
    @Operation(summary = "펜툴바 조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    public ResponseDTO<CustomBody> tchToolBarCall(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchToolsService.findToolBarCall(paramData);
        String resultMessage = "Result OK";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value =  {"/stnt/tool/bar/save", "/tch/tool/bar/save"}, method = {RequestMethod.POST})
    @Operation(summary = "펜툴바 저장하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"penClor\":\"black\"" +
                            "}"
                    )
            })
    )
    public ResponseDTO<CustomBody> updateTchToolBar(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        tchToolsService.updateTchToolBar(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, null, "Result OK");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/edit/bar/save", method = {RequestMethod.POST})
    @Operation(summary = "툴편집(저장)", description = "툴편집(저장)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "tolId" : "1",
                    "userId" : "2,3",
                    "claId" : "www.naver.com",
                    "textbkId" : "2",
                    "userSeCd" : "Y",
                    "monitor" : "1",
                    "attention" : "1",
                    "pentool" : "1",
                    "mathtool" : "1",
                    "bookmark" : "1",
                    "quiz" : "1",
                    "opinionBoard" : "1",
                    "whiteBoard" : "1",
                    "smartTool" : "1",
                    "sbjctCd" : "1"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveTchTool(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String,Object> resultData = tchToolsService.saveTchToolBar(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "툴편집(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/edit/bar/call", method = {RequestMethod.GET})
    @Operation(summary = "툴편집(호출)", description = "툴편집(호출)")
    public ResponseDTO<CustomBody> selectTchTool(
            @RequestParam(name = "userId", defaultValue = "vstea1") String userId,
            @RequestParam(name = "claId", defaultValue = "1") String claId,
            @RequestParam(name = "textbkId", defaultValue = "1") String textbkId,
            @RequestParam(name = "userSeCd", defaultValue = "T") String userSeCd,
            @RequestParam(name = "sbjctCd", defaultValue = "1") String sbjctCd
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();

        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbkId", textbkId);
        paramData.put("userSeCd", userSeCd);
        paramData.put("sbjctCd", sbjctCd);

        Object resultData = tchToolsService.selectTchTool(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "툴편집(호출)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/edit/bar/init", method = {RequestMethod.POST})
    @Operation(summary = "툴편집(초기화)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1," +
                            "\"userSeCd\":\"S\"," +
                            "\"sbjctCd\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> initTchToolEdit(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchToolsService.initTchToolEdit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "툴편집(초기화)");

    }

    @Loggable
    @RequestMapping(value = "/tch/tool/edit/bar/board/call", method = {RequestMethod.GET})
    @Operation(summary = "교사펜툴(호출)", description = "교사펜툴(호출)")
    public ResponseDTO<CustomBody> selectTchBoard(
            @RequestParam(name = "wrterId", defaultValue = "vstea1") String wrterId,
            @RequestParam(name = "textbkId", defaultValue = "1") String textbkId,
            @RequestParam(name = "tabId", defaultValue = "1") String tabId,
            @RequestParam(name = "articleId", defaultValue = "1") String articleId,
            @RequestParam(name = "subId", defaultValue = "1") String subId
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();

        paramData.put("wrterId", wrterId);
        paramData.put("textbkId", textbkId);
        paramData.put("tabId", tabId);
        paramData.put("articleId", articleId);
        paramData.put("subId", subId);

        Object resultData = tchToolsService.selectTchBoard(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사펜툴(호출)");

    }

    @Loggable
        @RequestMapping(value = "/tch/tool/edit/bar/board/save", method = {RequestMethod.POST})
        @Operation(summary = "교사펜툴(저장)", description = "교사펜툴(저장)")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = {
                        @ExampleObject(name = "파라미터", value = "{" +
                                "\"textbkId\":1," +
                                "\"wrterId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                                "\"tabId\":\"1\"," +
                                "\"articleId\":\"1\"," +
                                "\"subId\":\"1\"," +
                                "\"hdwrtCn\":\"1234\"" +
                                "}"
                        )
                }
                ))
        public ResponseDTO<CustomBody> saveTchBoard(
                @RequestBody Map<String, Object> paramData
        )throws Exception {

            Object resultData = tchToolsService.saveTchBoard(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사펜툴(저장)");

        }

    @Loggable
    @RequestMapping(value = "/tch/screen/control/settings", method = {RequestMethod.GET})
    @Operation(summary = "교사 화면 제어 설정 조회",
        description = "학급별 화면 제어 설정 조회. 첫 조회 시 자동으로 설정이 false로 생성됩니다.")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "4f3e0f23d8254b13bb7e40767a36af67"))
    @Parameter(name = "userId", description = "사용자 ID (첫 생성 시 rgtr, mdfr로 사용)", required = false, schema = @Schema(type = "string", example = "codebsample002-t"))
    public ResponseDTO<CustomBody> getScreenControlSettings(
            @RequestParam(name = "claId", required = true) String claId,
            @RequestParam(name = "userId", required = false) String userId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("claId", claId);
        paramData.put("userId", userId);

        Object resultData = tchToolsService.getScreenControlSettings(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "화면 제어 설정 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/screen/control/settings", method = {RequestMethod.POST})
    @Operation(summary = "화면 제어 설정 저장", description = "학급별 화면 제어 설정 일괄 저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "claId": "4f3e0f23d8254b13bb7e40767a36af67",
                    "userId": "codebsample002-t",
                    "settings": [
                        {"settingCode": 1, "settingValue": true},
                        {"settingCode": 2, "settingValue": false},
                        {"settingCode": 3, "settingValue": true},
                        {"settingCode": 4, "settingValue": false},
                        {"settingCode": 5, "settingValue": true},
                        {"settingCode": 6, "settingValue": false}
                    ]
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveScreenControlSettings(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchToolsService.saveScreenControlSettings(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "화면 제어 설정 저장");
    }
}
