package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.TchReportEvalService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.mq.service.AssessmentSubmittedService;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * (교사) 리포트 평가 API Controller
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@RestController
@Tag(name = "(교사) 리포트 평가 API", description = "(교사) 리포트 평가 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchReportEvalController {
    private final TchReportEvalService tchReportEvalService;
    private final AssessmentSubmittedService assessmentSubmittedService;

    @Loggable
    @RequestMapping(value = "/tch/report/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 리포트 목록조회", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbookId", description = "교과서(과목)ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색유형", required = false, schema = @Schema(type = "string", allowableValues = {"name", "gubun", "status", "date"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "reqGradeEvalAt", description = "채점 필요한 평가 여부", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchReportEvalList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        // 검색유형이 날짜(date) 인 경우
        if ("date".equals(String.valueOf(paramData.get("condition")))) {
            String[] keywords = StringUtils.split(MapUtils.getString(paramData, "keyword"), "~");
            paramData.put("st_dt", keywords[0]);
            paramData.put("ed_dt", keywords[1]);
        }

        Object resultData = tchReportEvalService.findReportEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 리포트 목록조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/detail/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(자세히 보기) > 목록", description = "")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "100717"))
    @Parameter(name = "submAt", description = "제출 여부", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> tchReportEvalResultDetailList(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findReportEvalResultDetailList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(자세히 보기) > 목록");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/detail/mdul", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(자세히 보기) > 모듈", description = "")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "57"))
    @Parameter(name = "evlIemId", description = "모듈ID", required = true, schema = @Schema(type = "string", example = "2490"))
    @Parameter(name = "subId", description = "서브ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchReportEvalResultDetailMdul(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "evlIemId", defaultValue = "") String evlIemId,
            @RequestParam(name = "subId", defaultValue = "0") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        AidtCommonUtil.checkSubIdParameter(paramData);

        List<String> requiredParams = Arrays.asList("evlId", "evlIemId", "subId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchReportEvalService.findReportEvalResultDetailMdul(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(자세히 보기) > 모듈");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/detail/stnt", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(자세히 보기) > 학생", description = "")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "19"))
    @Parameter(name = "evlIemId", description = "모듈ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subId", description = "서브ID", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "userId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "vsstu76"))
    public ResponseDTO<CustomBody> tchReportEvalResultDetailStnt(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "evlIemId", defaultValue = "") String evlIemId,
            @RequestParam(name = "subId", defaultValue = "0") String subId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        AidtCommonUtil.checkSubIdParameter(paramData);

        List<String> requiredParams = Arrays.asList("evlId", "evlIemId", "subId", "userId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchReportEvalService.findReportEvalResultDetailStnt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(자세히 보기) > 학생");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/detail/stnt/fdb/mod", method = {RequestMethod.POST})
    @Operation(summary = "평가 결과 조회(자세히 보기) > 학생 > 피드백", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "evlId" : 1,
                                    "evlIemId" : "1",
                                    "subId" : 0,
                                    "userId" : "430e8400-e29b-41d4-a746-446655440000",
                                    "fdbDc" : "테스트 피드백"
                                }
                            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportEvalResultDetailStntFdbMod(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        AidtCommonUtil.checkSubIdParameter(paramData);

        List<String> requiredParams = Arrays.asList("evlId", "evlIemId", "subId", "userId", "fdbDc");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Map resultData = tchReportEvalService.modifyReportEvalResultDetailStntFdbMod(paramData);
        if (Boolean.FALSE.equals(resultData.get("resultOk"))) {
            Exception err = (Exception) resultData.get("resultErr");
            return AidtCommonUtil.makeResultFail(paramData, null, err.getMessage());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(자세히 보기) > 학생 > 피드백");

    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/find/stnt", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 리포트화면 학생 검색", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbookId", description = "교과서(과목)ID", required = true, schema = @Schema(type = "Integer", example = "1"))
    @Parameter(name = "stntCondition", description = "학생검색유형", required = false, schema = @Schema(type = "string", allowableValues = {"name", "id"}, defaultValue = "name"))
    @Parameter(name = "stntKeyword", description = "학생검색어", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "reportType", description = "리포트구분", required = false, schema = @Schema(type = "string", example = "task"))
    @Parameter(name = "reportTargetId", description = "리포트 대상ID", required = false, schema = @Schema(type = "Integer", example = "4245"))
    public ResponseDTO<CustomBody> tchStntSrchReportFindStnt(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findStntSrchReportFindStnt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생조회 > 리포트화면 학생 검색");

    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 평가 리포트 목록조회", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbookId", description = "교과서(과목)ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "stntId", description = "(검색된) 학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "condition", description = "검색유형", required = false, schema = @Schema(type = "string", allowableValues = {"name", "gubun", "status", "date"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchStntSrchReportEvalList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbookId", defaultValue = "") String textbookId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @RequestParam(name = "condition", defaultValue = "") String condition,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        // 검색유형이 날짜(date) 인 경우
        if ("date".equals(String.valueOf(paramData.get("condition")))) {
            String[] keywords = StringUtils.split(MapUtils.getString(paramData, "keyword"), "~");
            paramData.put("st_dt", keywords[0]);
            paramData.put("ed_dt", keywords[1]);
        }

        Object resultData = tchReportEvalService.findStntSrchReportEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생조회 > 평가 리포트 목록조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/eval/result/detail", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 평가 결과 조회(자세히 보기)", description = "")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student46"))
    public ResponseDTO<CustomBody> tchStntSrchReportEvalResultDetail(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findStntSrchReportEvalResultDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생조회 > 평가 결과 조회(자세히 보기)");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/header", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회-상단정보", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchReportEvalResultHeader(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchReportEvalService.findReportEvalResultHeader(paramData);
        String resultMessage = "평가 결과 조회-상단정보";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    /**
     * 평가결과 조회 인사이트 본문
     *
     * @param pageable
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/insite", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회-인사이트 본문", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "19"))
    @Parameter(name = "condition", description = "0.번호순, 1. 정답률 낮은 순, 2.풀이 시간 긴 순, 3.재확인 횟수 순, 4.답안 변경 횟수 순, 5.빨리 푼 순", schema = @Schema(type = "string", allowableValues = {"0", "1", "2", "3", "4", "5"}, defaultValue = "1"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchReportEvalResultInsite(
//            @RequestParam(name = "evlId", required = true,   defaultValue = "") int evlId,
//            @RequestParam(name = "condition", defaultValue = "1") String condition,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchReportEvalService.findReportEvalResultInsite(paramData, pageable);
        String resultMessage = "평가 결과 조회-인사이트 본문";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    /**
     * 평가결과 조회(결과보기)
     *
     * @param evlId
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/summary", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(결과보기)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "3"))
//    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
//    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchReportEvalResultSummary(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
//            @Parameter(hidden = true) @PageableDefault(size = 3) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
//        Object resultData = tchReportEvalService.findReportEvalResultSummary(paramData, pageable);
        Object resultData = tchReportEvalService.findReportEvalResultSummary(paramData);
        String resultMessage = "평가 결과 조회(결과보기)";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/modi/score", method = {RequestMethod.POST})
    @Operation(summary = "평가 결과 배점 수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                              "evlId": 2,
                              "userId": "430e8400-e29b-41d4-a746-446655440000",
                              "evlIemId": "1",
                              "subId": 0,
                              "evlIemScrResult": 44,
                              "tchId": "550e8400-e29b-41d4-a716-446655440000"
                            }
                            """
                    )
            }
            )
    )
    public ResponseDTO<CustomBody> tchReportEvalResultModiScore(
            @RequestParam(name = "subId", defaultValue = "0") int subId,
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        if (paramData.get("subId") == null) {
            paramData.put("subId", 0);
        }

        Map<String, Object> resultData = tchReportEvalService.modifyReportEvalResultScore(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 모듈 배점 수정");


    }

    /*
    //@ApiOperation(value = "학생 모듈 평가 정보 수정", notes = "")
    @RequestMapping(value = "/tch/report/eval/mdul", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchReportEvalMdul() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchReportEvalService.modifyReportEvalMdul(paramData);
        String resultMessage = "학생 모듈 평가 정보 수정";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "평가 결과 피드백 작성_수정", notes = "")
    @RequestMapping(value = "/tch/report/eval/feedback/create", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchReportEvalFeedback(HttpServletRequest request) {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = tchReportEvalService.createReportEvalFeedback(paramData);
        resultMessage = "평가 결과 피드백 작성";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "평가 결과 피드백 작성_수정", notes = "")
    @RequestMapping(value = "/tch/report/eval/feedback/modify", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchReportEvalFeedbackModify(HttpServletRequest request) {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = tchReportEvalService.modifyReportEvalFeedback(paramData);
        resultMessage = "평가 결과 피드백 수정";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
    */
    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/eval/result/header", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 평가 결과 조회-상단정보(선택된 학생)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchStntSrchReportEvalResultHeader(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchReportEvalService.findStntSrchReportEvalResultHeader(paramData);
        String resultMessage = "학생조회 > 평가 결과 조회-상단정보(선택된 학생)";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/eval/result/summary", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 평가 결과 조회(결과보기)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchStntSrchReportEvalResultSummary(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchReportEvalService.findStntSrchReportEvalResultSummary(paramData, pageable);
        String resultMessage = "학생조회 > 평가 결과 조회(결과보기)";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    /**
     * 평가결과 조회 인사이트 본문
     *
     * @param evlId
     * @param condition
     * @param pageable
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/eval/result/insite", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 평가 결과 조회-인사이트 본문", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "condition", description = "0.번호순, 1. 정답률 낮은 순, 2.풀이 시간 긴 순, 3.재확인 횟수 순, 4.답안 변경 횟수 순, 5.빨리 푼 순", schema = @Schema(type = "string", allowableValues = {"0", "1", "2", "3", "4", "5"}, defaultValue = "0"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchStntSrchReportEvalResultInsite(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @RequestParam(name = "condition", defaultValue = "0") String condition,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        log.debug("condition:{}", condition);
        Object resultData = tchReportEvalService.findStntSrchReportEvalResultInsite(paramData, pageable);
        String resultMessage = "학생조회 > 평가 결과 조회-인사이트 본문";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    /**
     * 평가 공개 처리 API
     *
     * @return
     */
    @Loggable
    @RequestMapping(value = "/tch/report/eval/open", method = {RequestMethod.POST})
    @Operation(summary = "교사 평가리포트 > 공개처리", description = "교사 평가리포트 > 공개처리")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "evlId" : 1
                                }
                            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportEvalOepn(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = (Map<String, Object>) tchReportEvalService.modifyReportEvalOpen(paramData);
        assessmentSubmittedService.insertAssessmentInfo(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가리포트 공개처리 성공");
    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/appl/score", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 평가 > 평가 공개후 배점 수정반영 ", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                              "evlId": 2
                            }
                            """
                    )
            }
            )
    )
    public ResponseDTO<CustomBody> tchReportEvalResultApplScore(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = tchReportEvalService.modifyReportEvalResultApplScore(paramData);
        assessmentSubmittedService.modifyAssessmentScore(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 평가 > 평가 공개후 배점 수정반영 ");


    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/general-review/save", method = {RequestMethod.POST})
    @Operation(summary = "평가 리포트 총평 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":\"8\"," +
                            "\"userId\":\"student47\"," +
                            "\"genrvw\":\"1\"," +
                            "\"stdtPrntRlsAt\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> createTchReportEvalGeneralReviewSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.createTchEvalGeneralReviewSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 리포트 총평 저장");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/ind/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(개별문항출제) 자세히 보기 1", description = "[교사] 학급관리 > 홈 대시보드 > 평가리포트 '처방학습' (자세히보기 - 정오표)")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "2"))
    public ResponseDTO<CustomBody> tchReportEvalResultIndList(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findReportEvalResultIndList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(개별문항출제) 자세히 보기 1");

    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-list", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(개별문항)결과_인사이트)-반별학생목록조회", description = "교사의 우리반 학생들의 목록(콤보박스) 를 표시하는 상단정보")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea27"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad368ba8f11ee88c00242ac110002"))
    public ResponseDTO<CustomBody> tchStntList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findStntList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(개별문항)결과_인사이트)-반별학생목록조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/ind/mdul", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(개별문항출제) 자세히 보기 2", description = "[교사] 학급관리 > 홈 대시보드 > 평가리포트 '처방학습' (자세히보기-콘텐츠정보)")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student46"))
    public ResponseDTO<CustomBody> tchReportEvalResultIndMdul(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findStntSrchReportEvalResultDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(개별문항출제) 자세히 보기 2");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/ind/header", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(개별문항)결과_인사이트)-공통Header", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchReportEvalResultIndHeader(
            @RequestParam(name = "evlId", required = true, defaultValue = "") String evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findStntSrchReportEvalResultHeader(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(개별문항)결과_인사이트)-공통Header");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/ind/summary", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(개별문항출제) - 결과보기", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchReportEvalResultIndSummary(
            @RequestParam(name = "evlId", required = true, defaultValue = "") String evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findStntSrchReportEvalResultSummary(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(개별문항출제) - 결과보기");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/ind/insite", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(개별문항출제) - 인사이트", description = "[교사] 학급관리 > 홈 대시보드 > 평가리포트 '처방학습' (인사이트)")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "condition", description = "0.번호순, 1. 정답률 낮은 순, 2.풀이 시간 긴 순, 3.재확인 횟수 순, 4.답안 변경 횟수 순, 5.빨리 푼 순", schema = @Schema(type = "string", allowableValues = {"0", "1", "2", "3", "4", "5"}, defaultValue = "0"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> tchReportEvalResultIndInsite(
            @RequestParam(name = "evlId", required = true, defaultValue = "") String evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @RequestParam(name = "condition", defaultValue = "0") String condition,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findStntSrchReportEvalResultInsite(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(개별문항출제) - 인사이트");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/general-review/info", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 홈 대시보드 > 평가 리포트 > 자세히보기 > 총평조회", description = "[교사] 홈 대시보드 > 평가 리포트 > 자세히보기 > 총평조회 - 총평 등록시 사용될 대단원 목록과 학생의 정답률에 따른 상/중/하 정보를 조회한다.")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu10"))
    public ResponseDTO<CustomBody> tchReportEvalGeneralReviewInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findTchReportEvalGeneralReviewInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 홈 대시보드 > 평가 리포트 > 자세히보기 > 총평조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/general-review/ai-evl-word", method = {RequestMethod.GET})
    @Operation(summary = "평가 리포트 총평 AI 평어", description = "'[교사] 홈 대시보드 > 평가 리포트 > 자세히보기 (AI 평어)")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "387"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu479"))
    public ResponseDTO<CustomBody> tchReportEvalGeneralReviewAiEvlWord(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("evlId", "userId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchReportEvalService.findTchReportEvalGeneralReviewAiEvlWord(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 리포트 총평 AI 평어");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/eval/result/detail/summary", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회 (자세히 보기 (4))", description = "")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "17449"))
    public ResponseDTO<CustomBody> tchReportEvalResultDetailSummary(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportEvalService.findReportEvalResultDetailSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회 (자세히 보기 (4))");

    }
}
