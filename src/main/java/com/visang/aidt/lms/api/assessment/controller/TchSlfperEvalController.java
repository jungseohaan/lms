package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.TchSlfperEvalService;
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
@Tag(name = "(교사) 자기동료평가 API", description = "(교사) 자기동료평가 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchSlfperEvalController {

    private TchSlfperEvalService tchSlfperEvalService;

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/tmplt/save", method = {RequestMethod.POST})
    @Operation(summary = "자기동료평가템플릿저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"textbkId\":1," +
                            "\"tmpltId\":1," +
                            "\"slfPerEvlClsfCd\":1," +
                            "\"slfPerEvlNm\":\"테스트\"," +
                            "\"stExposAt\":\"N\"," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"evlDmi1\",\"evlIem\":\"evlIem1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"evlStdrDc1\"}," +
                            "{\"evlDmi\":\"evlDmi2\",\"evlIem\":\"evlIem2\",\"evlStdrCd\":\"2\",\"evlStdrDc\":\"evlStdrDc2\"}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> createTchSlfperEvlTmplt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.createTchSlfperEvlTmplt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가템플릿저장");

    }

    @Loggable
    //@RequestMapping(value = "/tch/slfper/evl/tmplt/save", method = {RequestMethod.POST})
    @Operation(summary = "자기동료평가템플릿저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"tmpltId\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"1\"," +
                            "\"slfPerEvlNm\":\"테스트\"," +
                            "\"stExposAt\":\"Y\"," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
                            "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveTchSlfperEvlTmplt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = tchSlfperEvalService.saveTchSlfperEvlTmplt2(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "손글씨 저장");

    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/tmplt/list", method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가템플릿조회", description = "자기동료평가템플릿조회")
    @Parameter(name = "userId", description = "유저ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> getTchSlfperEvlTmpltList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.getTchSlfperEvlTmpltList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가템플릿조회");


    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/tmplt/detail", method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가템플릿조회상세", description = "자기동료평가템플릿조회상세")
    @Parameter(name = "tmpltId", description = "템플릿ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchSlfperEvlTmpltDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.getTchSlfperEvlTmpltDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가템플릿조회상세");


    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/set/save", method = {RequestMethod.POST})
    @Operation(summary = "자기동료평가설정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"gbCd\":\"1\"," +
                            "\"wrterId\":\"1\"," +
                            "\"wrtDt\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"1\"," +
                            "\"slfPerEvlNm\":\"테스트\"," +
                            "\"stExposAt\":\"Y\"," +
                            "\"textbkId\":\"1\"," +
                            "\"tabId\":\"1\"," +
                            "\"taskId\":\"1\"," +
                            "\"evlId\":\"1\"," +
                            "\"setsId\":\"1\"," +
                            "\"resultDtlId\":\"1\"," +
                            "\"tmpltId\":\"1\"," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
                            "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveTchSlfperEvlSet(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.saveTchSlfperEvlSet(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가설정");

    }

    @Loggable
    @RequestMapping(value = {"/tch/slfper/evl/slf/view","/stnt/slfper/evl/slf/view"}, method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가결과보기", description = "자기동료평가결과보기")
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "gbCd", description = "구분코드 (교과 자료: 1, 과제: 2, 평가: 3)", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3"}, example = "1"))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "taskId", description = "과제ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "evlId", description = "평가ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "setsId", description = "셋트지번호", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "moduleId", description = "(선택된) 모듈 ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subId", description = "서브 ID", required = false, schema = @Schema(type = "Integer", example = "0"))
    public ResponseDTO<CustomBody> getTchSlfperEvlSlfView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.getTchSlfperEvlSlfView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가결과보기");

    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/slf/form", method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가설정보기", description = "")
    @Parameter(name = "gbCd", description = "구분코드 (교과 자료: 1, 과제: 2, 평가: 3)", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3"}, example = "1"))
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "tabId", description = "탭ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "setsId", description = "셋트지번호", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchSlfperEvlSlfFrom(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.getTchSlfperEvlSlfForm(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가설정보기");

    }

    @Loggable
    @RequestMapping(value = {"/tch/slfper/evl/per/view", "/stnt/slfper/evl/per/view"}, method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가결과보기(동료평가)", description = "자기동료평가결과보기(동료평가)")
    @Parameter(name = "perInfoId", description = "설정정보ID(동료)", required = true, schema = @Schema(type = "string", example = "5"))
    @Parameter(name = "stntId", description = "평가자ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "perApraserId", description = "피평가자ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    public ResponseDTO<CustomBody> getTchSlfperEvlperView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.getTchSlfperEvlperView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가결과보기(동료평가)");


    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/per/status", method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가제출현황(자기평가)", description = "자기동료평가제출현황(자기평가)")
    @Parameter(name = "gbCd", description = "구분코드 : 교과 자료: 1, 과제: 2, 평가: 3", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3"}, example = "1"))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "taskId", description = "과제ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "evlId", description = "평가ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "setsId", description = "셋트지번호", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "moduleId", description = "모듈ID", schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subId", description = "sub ID", schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchSlfperEvlPerStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.getTchSlfperEvlPerStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가제출현황(자기평가)");


    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/per/status/per", method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가제출현황(동료평가)", description = "자기동료평가제출현황(동료평가)")
    @Parameter(name = "gbCd", description = "구분코드 : 교과 자료: 1, 과제: 2, 평가: 3", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3"}, example = "1"))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "taskId", description = "과제ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "evlId", description = "평가ID", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "setsId", description = "셋트지번호", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "moduleId", description = "모듈ID", schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subId", description = "sub ID", schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchSlfperEvlPerStatusPer(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.getTchSlfperEvlPerStatusPer(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가제출현황(동료평가)");


    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/slf/perinfo", method = {RequestMethod.GET})
    @Operation(summary = "동료평가대상정보", description = "자기동료평가제출현황")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea46" ))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002" ))
    public ResponseDTO<CustomBody> findStntSlfperEvlSlfPerinfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.findTchSlfperEvlSlfPerinfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "동료평가대상정보");


    }

    @Loggable
    @RequestMapping(value = {"/tch/slfper/evl/set-info","/stnt/slfper/evl/set-info"}, method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가(설정정보) 조회", description = "자기동료평가(설정정보) 조회")
    @Parameter(name = "slfPerEvlClsfCd", description = "자기/동료평가 분류코드", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "gbCd", description = "구분코드", required = true, schema = @Schema(type = "integer", example = "3" ))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "taskId", description = "과제 ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "evlId", description = "평가 ID", required = false, schema = @Schema(type = "integer", example = "3060" ))
    @Parameter(name = "setsId", description = "셋트지 번호", required = false, schema = @Schema(type = "string", example = "" ))
    @Parameter(name = "moduleId", description = "모듈 ID", required = false, schema = @Schema(type = "string", example = "" ))
    @Parameter(name = "subId", description = "서브 ID", required = false, schema = @Schema(type = "integer", example = "" ))
    public ResponseDTO<CustomBody> findTchSlfperEvlSetInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.findTchSlfperEvlSetInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가(설정정보) 조회");

    }

    @Loggable
    @RequestMapping(value = {"/tch/slfper/evl/result","/stnt/slfper/evl/result"}, method = {RequestMethod.GET})
    @Operation(summary = "자기동료평가(결과정보) 조회", description = "자기동료평가(결과정보) 조회")
    @Parameter(name = "slfPerEvlClsfCd", description = "자기/동료평가 분류코드", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathreal36-s1" ))
    @Parameter(name = "gbCd", description = "구분코드", required = true, schema = @Schema(type = "integer", example = "3" ))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "taskId", description = "과제 ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "evlId", description = "평가 ID", required = false, schema = @Schema(type = "integer", example = "3060" ))
    @Parameter(name = "setsId", description = "셋트지 번호", required = false, schema = @Schema(type = "string", example = "" ))
    @Parameter(name = "moduleId", description = "모듈 ID", required = false, schema = @Schema(type = "string", example = "" ))
    @Parameter(name = "subId", description = "서브 ID", required = false, schema = @Schema(type = "integer", example = "" ))
    public ResponseDTO<CustomBody> findTchSlfperEvlResult(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchSlfperEvalService.findTchSlfperEvlResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가(결과정보) 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/slfper/evl/result/detail/list", method = {RequestMethod.GET})
    @Operation(summary = "리포트 - 자기동료평가(결과정보) 조회", description = "평가/과제/수업에서 교사/학생의 자기/동료평가 결과 정보(자기동료평가 답안제출 여부)를 조회한다.")
    @Parameter(name = "gbCd", description = "구분코드", required = true, schema = @Schema(type = "integer", allowableValues = {"1", "2", "3"}, defaultValue = "1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "2d002eb955d6454689104372919d6e87" ))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "re22mma5-t" ))
    @Parameter(name = "stntId", description = "학생 ID", required = false, schema = @Schema(type = "string", example = "" ))
    @Parameter(name = "textbkId", description = "교과서ID", required = false, schema = @Schema(type = "integer", example = "204" ))
    @Parameter(name = "tabId", description = "탭ID", required = false, schema = @Schema(type = "integer", example = "8814" ))
    @Parameter(name = "setsId", description = "셋트지 번호", required = false, schema = @Schema(type = "string", example = "1228" ))
    @Parameter(name = "taskId", description = "과제 ID", required = false, schema = @Schema(type = "integer", example = "1452" ))
    @Parameter(name = "evlId", description = "평가 ID", required = false, schema = @Schema(type = "integer", example = "4513" ))
    public ResponseDTO<CustomBody> tchSlfperEvlResultDetailList(
            @RequestParam(name = "gbCd", required = true,   defaultValue = "") String gbCd,
            @RequestParam(name = "userId", required = true,   defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        // 구분값에 따른 필수항목 체크
        List<String> requiredParams = new ArrayList<>();
        requiredParams.add("gbCd");
        requiredParams.add("claId");
        requiredParams.add("userId");
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

        Object resultData = tchSlfperEvalService.findTchSlfperEvlResultDetailList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리포트 - 자기동료평가(결과정보) 조회");

    }

}