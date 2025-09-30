package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.lecture.service.TchCrcuTabService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 커리큘럼 탭 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 커리큘럼 탭 API", description = "(교사) 커리큘럼 탭 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchCrcuTabController {
    private final TchCrcuTabService tchCrcuTabService;

    @RequestMapping(value = "/tch/crcu/tab", method = {RequestMethod.POST})
    @Operation(summary = "차시 탭 활성/비활성화 처리", description = "")
    //@Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    //@Parameter(name = "exposAt", description = "활성(Y)/비활성화(N)", required = true, schema = @Schema(type = "string", example = "Y"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"tabId\":1," +
                            "\"exposAt\":\"Y\"" +
                            "}"
                    )
            }
            )
    )
    @Loggable
    public ResponseDTO<CustomBody> tchCrcuTab(
            //@RequestParam(name = "tabId",   defaultValue = "1") long tabId,
            //@RequestParam(name = "exposAt",   defaultValue = "Y") String exposAt
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        /*
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("tabId", tabId);
        paramData.put("exposAt", exposAt);
        */


        Map<String, Object> resultData = tchCrcuTabService.modifyCrcuTabAvailable(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "차시 탭 활성/비활성화 처리");

    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/tab/info", method = {RequestMethod.GET})
    @Operation(summary = "차시 탭 정보 조회", description = "")
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchCrcuTabInfo(@RequestParam(name = "tabId",   defaultValue = "1") long tabId) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("tabId", tabId);


        Map<String, Object> resultData = tchCrcuTabService.findCrcuTabInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "차시 탭 정보 조회");

    }


    // 추후 사용하지 않을 예정 (2023-01-23일, 어윤석 책임님과 확인)
    // (추후 삭제 예정)
    @Loggable
    @RequestMapping(value = "/tch/crcu/tab/list", method = {RequestMethod.GET})
    @Operation(summary = "탭의 모듈 목록 조회 (미사용)", description = "")
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchCrcuTabList(
            @RequestParam(name = "tabId",   defaultValue = "1") Long tabId) throws Exception {

        // 탭 정보 포함
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("tabId", tabId);
        paramData.put("isIncludeTabInfo", true);


        Map<String, Object> resultData = tchCrcuTabService.findCrcuTabMdulList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "탭의 모듈 목록 조회");

    }

    /*
    //@ApiOperation(value = "탭별 학생들의 모듈 진행 상황 조회", notes = "")
    @RequestMapping(value = "/tch/crcu/tab/status", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuTabStatus() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuTabService.findCrcuTabMdulStatus(paramData);
        String resultMessage = "탭별 학생들의 모듈 진행 상황 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
    */
    @Loggable
    @RequestMapping(value = "/tch/crcu/tab/save", method = {RequestMethod.POST})
    @Operation(summary = "수업자료탭 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                     {
                       "TabList" : [{
                                       "tabId" : 1,
                                       "tabSeq" : 2
                                   },{
                                       "tabId" : 2,
                                       "tabSeq" : 2
                                   }]
                     }
                     """
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchCrcuTabSave(
            @RequestBody Map<String, Object> paramData
    ) {

        Object resultData = tchCrcuTabService.modifyTchCrcuTabSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업자료탭 변경");

    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/tab/chg-info", method = {RequestMethod.POST})
    @Operation(summary = "탭 세트지 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                         {
                               "userId" : "550e8400-e29b-41d4-a716-446655440000",
                               "tabId" : 1,
                               "setsId" : "1"
                         }
                         """
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchCrcuTabChginfo(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchCrcuTabService.modifyTchCrcuTabChginfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "탭 세트지 변경");

    }

    /* e북 페이지에 해당하는 탭 정보 조회 */
    @Loggable
    @RequestMapping(value = "/tch/crcu/ebook/tab/info", method = {RequestMethod.GET})
    @Operation(summary = "차시 e북 탭 정보 조회", description = "")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "375"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "ebook001-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "dc6816b0135a4b02841ef5426b3d8325"))
    @Parameter(name = "ebkId", description = "e북 ID", required = true, schema = @Schema(type = "integer", example = "378"))
    @Parameter(name = "currPage", description = "현재 페이지 번호", required = true, schema = @Schema(type = "integer", example = "77"))
    public ResponseDTO<CustomBody> tchCrcuInfo(
            @RequestParam(name = "textbkId",   defaultValue = "375") long textbkId,
            @RequestParam(name = "userId",   defaultValue = "ebook001-t") String userId,
            @RequestParam(name = "claId",   defaultValue = "dc6816b0135a4b02841ef5426b3d8325") String claId,
            @RequestParam(name = "ebkId",   defaultValue = "378") long ebkId,
            @RequestParam(name = "currPage",   defaultValue = "77") long currPage,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {
        Map<String, Object> resultData = tchCrcuTabService.findCrcuEbookTabInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "차시 탭 정보 조회");
    }
}
