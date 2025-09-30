package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.TchEvalService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * (교사) 평가 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 평가 API", description = "(교사) 평가 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchEvalController {
    private final TchEvalService tchEvalService;

    @Loggable
    @GetMapping(value = "/tch/eval/list")
    @Operation(summary = "평가 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathtest104-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "60263afa38fe4cdf9fe775c2865a6062"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    @Parameter(name = "condition", description = "검색 유형", required = false, schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "tmprStrgAt", description = "공유완료/설정미완료 구분", required = false, schema = @Schema(type = "string", allowableValues = {"","Y","N"}, defaultValue = "" ))
    @Parameter(name = "evlSttsCd", description = "필터조건(평가상태) : 1: 예정, 2: 진행중, 3: 완료, 4: 채점중, 5: 채점완료", required = false, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5"}, defaultValue = "" ))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchEvalList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchEvalService.findEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 목록 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/time/info", method = {RequestMethod.GET})
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer"), example = "70125")
    @Operation(summary = "평가 응시시간 추가하기 조회")
    public ResponseDTO<CustomBody> tchEvalTimeInfo(
            @RequestParam(name = "evlId") String evlId
            , @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchEvalService.findEvalTimeAdd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 응시시간 추가하기 조회");
    }

    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer"), example = "1")
    //@Parameter(name = "isSelAll", description = "전체 선택", required = true, schema = @Schema(type = "boolean"), example = "true")
    //@Parameter(name = "evlAdiSec", description = "추가시간", required = true, schema = @Schema(type = "integer"), example = "120")
    //@Parameter(name = "studentList", description = "응시대상 학생정보", schema = @Schema(type = "array", implementation = Map.class), example = "")

    @Loggable
    @RequestMapping(value = "/tch/eval/time", method = RequestMethod.POST)
    @Operation(summary = "평가 응시시간 추가하기 저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":70125," +
                            "\"isSelAll\":true," +
                            "\"evlAdiSec\":60," +
                            "\"studentList\":[\"mathtest104-s1\",\"mathtest104-s2\",\"mathtest104-s3\"]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalTime(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchEvalService.createEvalTimeAdd(paramData);
        String resultMessage = "평가 응시시간 추가하기 저장";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/info", method = {RequestMethod.GET})
    @Operation(summary = "평가 정보 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "70125"))
    public ResponseDTO<CustomBody> tchEvalInfo(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.findEvalInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/status", method = {RequestMethod.GET})
    @Operation(summary = "평가 현황 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "70125"))
    public ResponseDTO<CustomBody> tchEvalStatus(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.findEvalStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 현황 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/preview", method = {RequestMethod.GET})
    @Operation(summary = "평가 정보 조회 (상세 미리보기)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "70125"))
    public ResponseDTO<CustomBody> tchEvalPreview(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.findEvalPreview(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 조회 (상세 미리보기)");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/result/status", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회 (응시중_응시완료)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "70125"))
    @Parameter(name = "pageYn", description = "pageYn", required = false, schema = @Schema(type = "string", example = "N"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchEvalResultStatus(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.findEvalResultStatus(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회 (응시중_응시완료)");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/start", method = {RequestMethod.POST})
    @Operation(summary = "평가 정보 수정(시작하기)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{\"evlId\":70125}")
            }
            ))
    public ResponseDTO<CustomBody> tchEvalStart(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = tchEvalService.modifyEvalStart(paramData);


        Boolean resultOk = MapUtils.getBoolean(resultData, "resultOk", false);
        String resultMsg = MapUtils.getString(resultData, "resultMsg", "");

        if (resultOk) {
            return AidtCommonUtil.makeResultSuccess(paramData, resultData.get("resultData"), resultMsg);
        } else {
            return AidtCommonUtil.makeResultFail(paramData, resultData, resultMsg);
        }
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/end", method = {RequestMethod.POST})
    @Operation(summary = "평가 종료 하기", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":70125," +
                            "\"timeoutAt\":\"N\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalEnd(
            //@RequestParam(name = "evlId", defaultValue = "") String evlId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.modifyEvalEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 종료 하기");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/reset", method = {RequestMethod.POST})
    @Operation(summary = "평가 정보 수정(다시시작)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{\"evlId\":70125}")
            }
            ))
    public ResponseDTO<CustomBody> tchEvalReset(
            //@RequestParam(name = "evlId", defaultValue = "") String evlId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.removeEvalResetWithBatch(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 수정(다시시작)");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/batch/reset", method = {RequestMethod.POST})
    @Operation(summary = "평가 정보 수정(다시시작)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{\"evlId\":70125}")
            }
            ))
    public ResponseDTO<CustomBody> tchEvalBatchReset(
            //@RequestParam(name = "evlId", defaultValue = "") String evlId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.removeEvalResetWithBatch(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 수정(다시시작)");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/delete", method = {RequestMethod.POST})
    @Operation(summary = "평가 정보 삭제", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = ""))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{\"evlId\":70015}")
            }
            ))
    public ResponseDTO<CustomBody> tchEvalDelete(
            //@RequestParam(name = "evlId", defaultValue = "") String evlId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.removeTchEvalDelete(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 삭제");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/init", method = {RequestMethod.POST})
    @Operation(summary = "평가 정보 초기화(개발편의 임시성)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{\"evlId\":70015}")
            }
            ))
    public ResponseDTO<CustomBody> tchEvalInit(
            //@RequestParam(name = "evlId", defaultValue = "") String evlId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.removeEvalInit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 초기화(개발편의 임시성)");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/read-info", method = {RequestMethod.GET})
    @Operation(summary = "평가 자료설정 정보 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "70015"))
    public ResponseDTO<CustomBody> tchEvalReadInfo(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.findTchEvalReadInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 자료설정 수정(조회)");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/save", method = {RequestMethod.POST})
    @Operation(summary = "평가 자료설정 저장(수정)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":70015," +
                            "\"rptAutoOthbcAt\":\"Y\"," +
                            "\"evlSeCd\":1," +
                            "\"evlNm\":\"설정 미완료 목록 > 자료설정 테스트\"," +
                            "\"bbsSvAt\":\"Y\"," +
                            "\"bbsNm\":\"자료실에 저장 테스트\"," +
                            "\"tag\":\"테스트태그\"," +
                            "\"cocnrAt\":\"N\"," +
                            "\"pdSetAt\":\"N\"," +
                            "\"pdEvlStDt\":\"\"," +
                            "\"pdEvlEdDt\":\"\"," +
                            "\"ntTrnAt\":\"N\"," +
                            "\"timStAt\":\"Y\"," +
                            "\"timTime\":\"11:00\"," +
                            /*"\"prscrStdSetAt\":\"N\"," +
                            "\"prscrStdStDt\":\"\"," +
                            "\"prscrStdEdDt\":\"\"," +
                            "\"prscrStdNtTrnAt\":\"N\"," +*/
                            "\"aiTutSetAt\":\"N\"," +
                            "\"rwdSetAt\":\"Y\"," +
                            "\"scrSetAt\":\"N\"," +
                            "\"evlStdrSetAt\":\"Y\"," +
                            "\"evlStdrSet\":\"3\"," +
                            "\"evlGdStdrScr\":\"\"," +
                            "\"evlAvStdrScr\":\"\"," +
                            "\"evlPsStdrScr\":\"\"," +
                            "\"edGidAt\":\"Y\"," +
                            "\"edGidDc\":\"자료설정 테스트 - 자동출제테스트 - 평가_세트지_19_수정\"," +
                            "\"stdSetAt\":\"Y\"," +
                            "\"studentInfoList\":[" +
                            "{" +
                            "\"id\":1," +
                            "\"evlId\":70015," +
                            "\"trnTrgetId\":\"mathbook227-s1\"," +
                            "\"trnTrgetNm\":\"이학생\"," +
                            "\"isTrnTrget\":true" +
                            "}" +
                            ",{" +
                            "\"id\":2," +
                            "\"evlId\":70015," +
                            "\"trnTrgetId\":\"mathbook227-s2\"," +
                            "\"trnTrgetNm\":\"김학생\"," +
                            "\"isTrnTrget\":false" +
                            "}" +
                            "]," +
                            "\"slfEvlInfo\":{" +
                            "\"userId\":\"mathbook227-t\"," +
                            "\"gbCd\":\"3\"," +
                            "\"wrterId\":\"mathbook227-s3\"," +
                            "\"wrtDt\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"1\"," +
                            "\"slfPerEvlNm\":\"테스트\"," +
                            "\"stExposAt\":\"Y\"," +
                            "\"textbkId\":\"1133\"," +
                            "\"tabId\":\"1\"," +
                            "\"taskId\":\"1\"," +
                            "\"evlId\":\"1\"," +
                            "\"setsId\":\"1\"," +
                            "\"resultDtlId\":\"1\"," +
                            "\"tmpltId\":\"1\"," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
                            "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
                            "}," +
                            "\"perEvlInfo\":{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"gbCd\":\"3\"," +
                            "\"wrterId\":\"1\"," +
                            "\"wrtDt\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"2\"," +
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
                            "\"is_change\":\"Y\"," +
                            "}" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchEvalService.createTchEvalSave(paramData);

        int evlId = MapUtils.getIntValue(resultData, "evlId", 0);
        Boolean resultOk = MapUtils.getBoolean(resultData, "resultOk", false);
        String resultMsg = MapUtils.getString(resultData, "resultMsg", "");

        if (resultOk) {
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 자료설정 저장(수정)");
        } else {
            return AidtCommonUtil.makeResultFail(paramData, resultData, resultMsg);
        }
    }

    @Loggable
    @PostMapping(value = "/tch/eval/create")
    @Operation(summary = "평가 생성(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":\"mathtest110-t\"," +
                            "\"claId\":\"821bf76183e943b3adf2a8e0b2064b46\"," +
                            "\"textbookId\":1199," +
                            "\"evlNm\":\"테스트 평가 수학\"," +
                            "\"evlSeCd\":1," + // /tch/eval/save api 에서 처리 하는 것으로 변경 됨 // 화면 수정 완료 후 삭제 예정
                            "\"eamMth\":3," +
                            "\"eamExmNum\":0," +
                            "\"eamGdExmMun\":0," +
                            "\"eamAvUpExmMun\":0," +
                            "\"eamAvExmMun\":0," +
                            "\"eamAvLwExmMun\":0," +
                            "\"eamBdExmMun\":0," +
                            "\"eamScp\":\"4,5\"," +
                            "\"setsId\":\"MSTG88922\"," +
                            "\"prscrStdSetAt\":\"N\"," +
                            "\"prscrStdStDt\":\"2024.09.30 09:00\"," +
                            "\"prscrStdEdDt\":\"2024.09.30 18:00\"," +
                            "\"prscrStdNtTrnAt\":\"N\"," +
                            "\"prscrStdPdSet\":0" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalCreate(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchEvalService.createTchEvalCreate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 생성(저장)");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/copy", method = RequestMethod.POST)
    @Operation(summary = "평가 정보 복사")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "evlId" : 69962
                }
            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalCopy(
            @RequestBody Map<String, Object> paramData
    )throws Exception {


        Map<String, Object> resultData = tchEvalService.copyEvalInfo(paramData);
        String resultMessage = "평가 정보 복사";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);


    }

    @Loggable
    @RequestMapping(value = "/tch/eval/auto/qstn/extr", method = RequestMethod.GET)
    @Operation(summary = "(평가) 문항 자동생성 추천 모듈정보 조회", description = "문항 자동 생성에 필요한 모듈을 추출한다. ( [교사] 수업 자료 > 평가 > 만들기 )")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathtest110-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "821bf76183e943b3adf2a8e0b2064b46"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1199"))
    @Parameter(name = "eamExmNum", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "6"))
    @Parameter(name = "eamGdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "eamAvUpExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "eamAvExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamAvLwExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamBdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamScp", description = "출제범위", required = true, schema = @Schema(type = "string", example = "870,872,956"))
    public ResponseDTO<CustomBody> tchEvalAutoQstnExtr(
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

        Map<String, Object> resultData = tchEvalService.findEvalAutoQstnExtr(paramData);
        if(Boolean.FALSE.equals(resultData.get("resultOk"))) {
            return AidtCommonUtil.makeResultFail(paramData, null, (String) resultData.get("resultMsg"));
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(평가) 문항 자동생성 추천 모듈정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/m-save", method = {RequestMethod.POST})
    @Operation(summary = "평가 마법봉 수정(저장)", description = "평가에 대한 세트지 정보의 변경된 내용을 반영(수정)한다. ( [교사] 학급 자료 > 평가 > 공유 완료 목록/설정 미완료 목록 > 마법봉 )")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":72347," +
                            "\"setsId\":\"919\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalSaveByMagicWand(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.modifyEvalSaveByMagicWand(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 마법봉 수정(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/task/info", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 교과서 배포 평가_과제 ID 조회", description = "")
    // 평가
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1192"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook524-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "2caa25e39288465a8845b991cc2a98da"))
    @Parameter(name = "setsId", description = "세트지 ID", required = true, schema = @Schema(type = "string", example = "4276"))
    @Parameter(name = "matrialType", description = "과제 : 1 / 평가  : 2", required = false, schema = @Schema(type = "integer", example = "1", allowableValues = {"1", "2"}))
    /*
    // 과제
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002"))
    @Parameter(name = "setsId", description = "세트지 ID", required = true, schema = @Schema(type = "string", example = "287"))
    */
    public ResponseDTO<CustomBody> tchEvalTaskInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchEvalService.findTchEvalTaskInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 교과서 배포 평가_과제 ID 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/create/for/textbk", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 교과서 평가 생성 메소드", description = "메소드 테스트를 위한 API 입니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                [
                                    {
                                        "wrterId" : "vstea22" ,
                                        "claId" : "308ad2eaba8f11ee88c00242ac110002",
                                        "textbookId" : 1,
                                        "evlNm" : "evlNm_1",
                                        "evlSeCd" : 2,
                                        "setsId" : "1003",
                                        "timTime" : "01:30:00"
                                    },
                                    {
                                        "wrterId" : "vstea23" ,
                                        "claId" : "308ad304ba8f11ee88c00242ac110002",
                                        "textbookId" : 1,
                                        "evlNm" : "evlNm_2",
                                        "evlSeCd" : 3,
                                        "setsId" : "1006",
                                        "timTime" : "01:30:00"
                                    }
                                ]
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchEvalCreateForTextbk(
            @RequestBody List<Map<String, Object>> paramData
    )throws Exception {
        Object resultData = tchEvalService.createTchEvalCreateForTextbk(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 교과서 평가 생성 메소드");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/period/change", method = {RequestMethod.POST})
    @Operation(summary = "평가 기간 수정", description = "평가 기간 수정")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "evlId" : 111,
                                    "evlSttsCd" : 1,
                                    "pdSetAt" : "Y",
                                    "pdEvlStDt" : "2024.07.26 09:00",
                                    "pdEvlEdDt" : "2024.07.28 18:00"
                                }
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchEvalPeriodChange(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        // 구분값에 따른 필수항목 체크
        int evlSttsCd = MapUtils.getIntValue(paramData, "evlSttsCd", 0);
        String pdSetAt = MapUtils.getString(paramData, "pdSetAt");

        List<String> requiredParams = new ArrayList<>();
        requiredParams.add("evlId");
        requiredParams.add("evlSttsCd");

        if (evlSttsCd == 1) { // 예정
            requiredParams.add("pdSetAt");

            if ("Y".equals(pdSetAt)) { // 수업 외
                requiredParams.add("pdEvlStDt");
                requiredParams.add("pdEvlEdDt");
            }
        } else if (evlSttsCd == 2) { // 진행 중
            requiredParams.add("pdEvlEdDt");
        }

        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = tchEvalService.modifyTchEvalPeriodChange(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 기간 수정");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/status/list", method = {RequestMethod.GET})
    @Operation(summary = "진행중,채점이 필요한 평가 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook732-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "44e701f580a34e17b2a030a02527dc4a"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1522"))
    public ResponseDTO<CustomBody> tchEvalStatusList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchEvalService.findTchEvalStatusList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "진행중,채점이 필요한 평가 목록 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/subm/status", method = {RequestMethod.GET})
    @Operation(summary = "평가 제출 현황 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "72249"))
    public ResponseDTO<CustomBody> tchEvalSubmStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchEvalService.findEvalSubmStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 제출 현황 조회");

    }
}