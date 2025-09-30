package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.TchMdulQstnService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(교사) 수업 중", description = "(교사) 수업 중")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchMdulQstnController {
    private final TchMdulQstnService tchMdulQstnService;

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/answ", method = {RequestMethod.POST})
    @Operation(summary = "정답보기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "기존 학습 현황", value = "{" +
                            "\"tabId\":4510423," +
                            "\"textbkId\":1," +
                            "\"claId\":\"a98eb60eff0e43af980d88d726393082\"," +
                            "\"setsId\":\"ST-00000000000007591\"," +
                            "\"examTarget\":1," +
                            "\"page\":0," +
                            "\"size\":10," +
                            "\"pageYn\":\"Y\"" +
                            "}"
                    ),
                    @ExampleObject(name = "개인별 맞춤 학습", value = "{" +
                            "\"tabId\":4510423," +
                            "\"textbkId\":1," +
                            "\"claId\":\"a98eb60eff0e43af980d88d726393082\"," +
                            "\"examTarget\":2," +
                            "\"dtaResultInfoList\":[" +
                            "{" +
                            "\"id\":137005," +
                            "\"textbkTabId\":4510423," +
                            "\"mamoymId\":\"engbook1292-s1\"," +
                            "\"setsId\":\"ST-00000000000007591\"" +
                            "}," +
                            "{" +
                            "\"id\":137006," +
                            "\"textbkTabId\":4510423," +
                            "\"mamoymId\":\"engbook1292-s2\"," +
                            "\"setsId\":\"ST-00000000000007592\"" +
                            "}," +
                            "{" +
                            "\"id\":137007," +
                            "\"textbkTabId\":4510423," +
                            "\"mamoymId\":\"engbook1292-s3\"," +
                            "\"setsId\":\"ST-00000000000007593\"" +
                            "}," +
                            "{" +
                            "\"id\":137008," +
                            "\"textbkTabId\":4510423," +
                            "\"mamoymId\":\"engbook1292-s4\"," +
                            "\"setsId\":\"ST-00000000000007594\"" +
                            "}," +
                            "{" +
                            "\"id\":137009," +
                            "\"textbkTabId\":4510423," +
                            "\"mamoymId\":\"engbook1292-s5\"," +
                            "\"setsId\":\"ST-00000000000007595\"" +
                            "}" +
                            "]," +
                            "\"page\":0," +
                            "\"size\":10," +
                            "\"pageYn\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulQstnAnsw(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.findTchMdulQstnAnsw(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답보기");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/reset", method = {RequestMethod.POST})
    @Operation(summary = "다시하기/틀린학생만", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"setsId\":\"297\"," +
                            "\"articleId\":\"3\"," +
                            "\"claId\":\"308ad694ba8f11ee88c00242ac110002\"," +
                            "\"subId\":0 ," +
                            "\"stdtId\":[\"vsstu604\",\"vsstu605\"] " +
                        "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulQstnReset(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnResetWithBatch(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "다시하기/틀린학생만");

    }

//    @Loggable
//    @RequestMapping(value = "/tch/lecture/mdul/qstn/batch/reset", method = {RequestMethod.POST})
//    @Operation(summary = "다시하기", description = "")
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            content = @Content(examples = {
//                    @ExampleObject(name = "파라미터", value = "{" +
//                            "\"setsId\":\"297\"," +
//                            "\"articleId\":\"3\"," +
//                            "\"claId\":\"308ad694ba8f11ee88c00242ac110002\"," +
//                            "\"subId\":0" +
//                            "}"
//                    )
//            }
//            ))
//    public ResponseDTO<CustomBody> modifyTchMdulQstnBatchReset(
//            @RequestBody Map<String, Object> paramData
//    )throws Exception {
//
//        Object resultData = tchMdulQstnService.modifyTchMdulQstnBatchReset(paramData);
//        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "다시하기");
//
//    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/status", method = {RequestMethod.GET})
    @Operation(summary = "제출현황 조회", description = "")
    //@Parameter(name = "dtaResultId", description = "자료(학습)결과ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "dtaIemId", description = "자료평가항목ID", required = true, schema = @Schema(type = "string", example = "827"))
    @Parameter(name = "subId", description = "연쇄형 서브 문항 idx", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "tabId", description = "탭 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "textbkId", description = "교과서 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "claId", description = "클래스 ID", required = false, schema = @Schema(type = "string", example = "1dfd6267b8fb11ee88c00242ac110002"))
    @Parameter(name = "setsId", description = "세트지 ID", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "selfEvlYn", description = "자기평가 Y/N", required = false, schema = @Schema(type = "string", example = "N"))
    public ResponseDTO<CustomBody> tchMdulQstnStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "제출현황 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/exclnt", method = {RequestMethod.POST})
    @Operation(summary = "우수답안선정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"detailId\":[1,2,3]," +
                            "\"fdbExpAt\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulQstnExclnt(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnExclnt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우수답안선정");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/exclnt/reset", method = {RequestMethod.POST})
    @Operation(summary = "우수답안선정 취소", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"detailId\":[1,2,3]," +
                            "\"fdbExpAt\":\"N\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulQstnExclntCancel(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnExclntCancel(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우수답안선정취소");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/fdb", method = {RequestMethod.POST})
    @Operation(summary = "피드백 보내기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"detailId\":\"53\"," +
                            "\"stdFdbDc\":\"테스트 피드백\"," +
                            "\"stdFdbUrl\":\"\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulQstnFdb(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnFdb(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "피드백 보내기");

    }
    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/share", method = {RequestMethod.POST})
    @Operation(summary = "피드백 공유 여부 변경,", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"detailId\":\"53\"," +
                            "\"fdbExpAt\":\"Y\"," +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMdulQstnFdbShare(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnFdbShare(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "피드백 공유 여부");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/indi", method = {RequestMethod.GET})
    @Operation(summary = "제출현황,개별답안", description = "")
    //@Parameter(name = "dtaResultId", description = "학습자료결과 ID", required = true, schema = @Schema(type = "integer", example = "6"))
    @Parameter(name = "dtaIemId", description = "자료평가항목ID", required = true, schema = @Schema(type = "string", example = "827"))
    @Parameter(name = "subId", description = "연쇄형 서브 문항 idx", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "tabId", description = "탭 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "textbkId", description = "교과서 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "claId", description = "클래스 ID", required = false, schema = @Schema(type = "string", example = "1dfd6267b8fb11ee88c00242ac110002"))
    @Parameter(name = "setsId", description = "세트지 ID", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> tchMdulQstnIndi(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnIndi(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "제출현황,개별답안");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/auto/qstn/extr", method = RequestMethod.GET)
    @Operation(summary = "(수업) 문항 자동생성 추천 모듈정보 조회", description = "문항 자동 생성에 필요한 모듈을 추출한다. ( [교사] 수업 자료 > 교과자료 > 만들기 )")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook3108-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "860936a514f04169afa154459758f944"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "eamExmNum", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "200"))
    @Parameter(name = "eamGdExmMun", description = "출제문항수(상)", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "eamAvUpExmMun", description = "출제문항수(중상)", required = true, schema = @Schema(type = "integer", example = "50"))
    @Parameter(name = "eamAvExmMun", description = "출제문항수(중)", required = true, schema = @Schema(type = "integer", example = "50"))
    @Parameter(name = "eamAvLwExmMun", description = "출제문항수(중하)", required = true, schema = @Schema(type = "integer", example = "50"))
    @Parameter(name = "eamBdExmMun", description = "출제문항수(하)", required = true, schema = @Schema(type = "integer", example = "50"))
    @Parameter(name = "eamScp", description = "출제범위", required = true, schema = @Schema(type = "string", example = "26070"))
    public ResponseDTO<CustomBody> tchLectureAutoQstnExtr(
            @RequestParam(name = "wrterId", defaultValue = "") String wrterId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbookId", defaultValue = "") int textbookId,
            @RequestParam(name = "eamExmNum", defaultValue = "0") int eamExmNum,
            @RequestParam(name = "eamGdExmMun", defaultValue = "0") int eamGdExmMun,
            @RequestParam(name = "eamAvUpExmMun", defaultValue = "0") int eamAvUpExmMun,
            @RequestParam(name = "eamAvExmMun", defaultValue = "0") int eamAvExmMun,
            @RequestParam(name = "eamAvLwExmMun", defaultValue = "0") int eamAvLwExmMun,
            @RequestParam(name = "eamBdExmMun", defaultValue = "0") int eamBdExmMun,
            @RequestParam(name = "eamScp", defaultValue = "") String eamScp,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        /// 파라미터 유효성 검증
        List<String> requiredParams = Arrays.asList("wrterId","claId","textbookId","eamExmNum","eamGdExmMun","eamAvUpExmMun","eamAvExmMun","eamAvLwExmMun","eamBdExmMun","eamScp");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        paramData.put("eamScp", AidtCommonUtil.strToLongList((String)paramData.get("eamScp"))); // 출제 범위

        Map<String, Object> resultData = tchMdulQstnService.findLectureAutoQstnExtr(paramData);
        if(Boolean.FALSE.equals(resultData.get("resultOk"))) {
            return AidtCommonUtil.makeResultFail(paramData, null, (String) resultData.get("resultMsg"));
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(평가) 문항 자동생성 추천 모듈정보 조회");

    }

}
