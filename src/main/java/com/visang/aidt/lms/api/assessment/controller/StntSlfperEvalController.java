package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.StntSlfperEvalService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(학생) 자기 동료 평가 API", description = "(학생) 자기 동료 평가 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntSlfperEvalController {

    private StntSlfperEvalService stntSlfperEvalService;

    @Loggable
    @RequestMapping(value = "/stnt/slfper/evl/slf/set", method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가세팅", description = "")
    @Parameter(name = "stntId", description = "조회하고자 하는 학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu537" ))
    @Parameter(name = "gbCd", description = "구분코드 : 교과 자료: 1, 과제: 2, 평가: 3", required = true, schema = @Schema(type = "integer", allowableValues = {"1","2","3"}, example = ""))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "taskId", description = "과제 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "evlId", description = "평가 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "setsId", description = "셋트지 번호", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "moduleId", description = "모듈 아이디", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "subId", description = "sub 아이디", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "308ad5f8ba8f11ee88c00242ac110002" ))
    public ResponseDTO<CustomBody> stntSlfperEvlSlfSet(
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @RequestParam(name = "gbCd", defaultValue = "") String gbCd,
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "tabId", defaultValue = "") String tabId,
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "setsId", defaultValue = "") String setsId,
            @RequestParam(name = "moduleId", defaultValue = "") String moduleId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntSlfperEvalService.findStntSlfperEvlSlfSet(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가세팅");

    }

    @Loggable
    @RequestMapping(value = "/stnt/slfper/evl/set/save", method = {RequestMethod.POST})
    @Operation(summary = "자기동료평가설정(모듈)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                 {
                   "userId" : "student1",
                   "gbCd" : 1,
                   "wrterId" : "student1",
                   "slfPerEvlNm" : "평가명",
                   "textbkId" : 1,
                   "tabId" : 1,
                   "taskId" : null,
                   "evlId" : null,
                   "setsId" : null,
                   "resultDtlId" : null,
                   "moduleId" : null,
                   "subId" : 0,
                   "slfEvlInfoList" : [{
                                           "tmpltItmSeq" : 1,
                                           "evlDmi" : "evlDmi1",
                                           "evlIem" : "evlIem1",
                                           "evlStdrCd" : "1",
                                           "evlstdrDc" : "evlstdrDc1"
                                       },{
                                            "tmpltItmSeq" : 2,
                                           "evlDmi" : "evlDmi2",
                                           "evlIem" : "evlIem2",
                                           "evlStdrCd" : "2",
                                           "evlstdrDc" : "evlstdrDc2"
                                       }],
                   "slfStExposAt" : "Y",                  
                   "perEvlInfoList" : [{
                                           "tmpltItmSeq" : 3,
                                           "evlDmi" : "evlDmi3",
                                           "evlIem" : "evlIem3",
                                           "evlStdrCd" : "3",
                                           "evlstdrDc" : "evlstdrDc3"
                                       },{
                                            "tmpltItmSeq" : 4,
                                           "evlDmi" : "evlDmi4",
                                           "evlIem" : "evlIem4",
                                           "evlStdrCd" : "4",
                                           "evlstdrDc" : "evlstdrDc4"
                                       }],
                   "perStExposAt" : "Y"
                 }
                 """
                    )
            })
    )
    public ResponseDTO<CustomBody> stntSlfperEvlSetSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntSlfperEvalService.createStntSlfperEvlSetSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가설정(모듈)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/slfper/evl/slf/save", method = {RequestMethod.POST})
    @Operation(summary = "자기동료평가저장(통)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                 {
                   "slfEvlInfoList" : [{
                                           "selInfoId" : 7,
                                           "slfPerEvlDetailId" : 1,
                                           "apraserId" : "student46",
                                           "evlAsw" : "evlAsw1"
                                       },{
                                          "selInfoId" : 7,
                                          "slfPerEvlDetailId" : 2,
                                          "apraserId" : "student46",
                                          "evlAsw" : "evlAsw2"
                                      }],
                   "perEvlInfoList" : [{
                                           "perInfoId" : 13,
                                           "slfPerEvlDetailId" : "3",
                                           "perEvlIArrList" : [{
                                               "apraserId" : "student51",
                                               "perApraserId" : "student3",
                                               "evlAsw" : "evlAsw3"
                                           },{
                                               "apraserId" : "student51",
                                               "perApraserId" : "student4",
                                               "evlAsw" : "evlAsw4"
                                           }]
                                       },{
                                          "perInfoId" : 14,
                                          "slfPerEvlDetailId" : "4",
                                          "perEvlIArrList" : [{
                                              "apraserId" : "532e8642-e29b-41d4-a746-446655441253",
                                              "perApraserId" : "student5",
                                              "evlAsw" : "evlAsw5"
                                          },{
                                              "apraserId" : "532e8642-e29b-41d4-a746-446655441253",
                                              "perApraserId" : "student6",
                                              "evlAsw" : "evlAsw6"
                                          }]
                                      }],
                   "moduleSubmAt" : "N"
                 }
                 """
                    )
            })
    )
    public ResponseDTO<CustomBody> stntSlfperEvlSlfSet(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntSlfperEvalService.createStntSlfperEvlSlfSet(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가저장(통)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/slfper/evl/slf/perinfo", method = {RequestMethod.GET})
    @Operation(summary = "동료평가대상정보", description = "자기동료평가제출현황")
    @Parameter(name = "stntId", description = "조회하고자 하는 학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661" ))
    public ResponseDTO<CustomBody> findStntSlfperEvlSlfPerinfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntSlfperEvalService.findStntSlfperEvlSlfPerinfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "동료평가대상정보");


    }

    @Loggable
    @RequestMapping(value = "/stnt/slfper/evl/result/detail/list", method = {RequestMethod.GET})
    @Operation(summary = "리포트 - 자기동료평가(결과정보) 조회", description = "평가/과제/수업에서 교사/학생의 자기/동료평가 결과 정보(자기동료평가 답안제출 여부)를 조회한다.")
    @Parameter(name = "gbCd", description = "구분코드", required = true, schema = @Schema(type = "integer", allowableValues = {"1", "2", "3"}, defaultValue = "1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "2d002eb955d6454689104372919d6e87" ))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "re22mma5-s1" ))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "integer", example = "204" ))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "integer", example = "8814" ))
    @Parameter(name = "setsId", description = "셋트지 번호", required = false, schema = @Schema(type = "string", example = "1228" ))
    @Parameter(name = "taskId", description = "과제 ID", required = false, schema = @Schema(type = "integer", example = "1452" ))
    @Parameter(name = "evlId", description = "평가 ID", required = false, schema = @Schema(type = "integer", example = "7735" ))
    public ResponseDTO<CustomBody> stntSlfperEvlResultDetailList(
            @RequestParam(name = "gbCd", required = true,   defaultValue = "") String gbCd,
            @RequestParam(name = "stntId", required = true,   defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        // 구분값에 따른 필수항목 체크
        List<String> requiredParams = new ArrayList<>();
        requiredParams.add("gbCd");
        requiredParams.add("claId");
        requiredParams.add("stntId");
        switch (gbCd) {
            case "1" : /* 교과자료 */
                requiredParams.add("textbkId");
                requiredParams.add("tabId");
                requiredParams.add("setsId");
                break;
            case "2" : /* 과제 */
                requiredParams.add("taskId");
                break;
            case "3" : /* 평가 */
                requiredParams.add("evlId");
                break;
        }
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = stntSlfperEvalService.findStntSlfperEvlResultDetailList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리포트 - 자기동료평가(결과정보) 조회");

    }


}
