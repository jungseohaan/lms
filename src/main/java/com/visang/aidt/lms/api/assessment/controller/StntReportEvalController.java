package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.StntReportEvalService;
import com.visang.aidt.lms.api.assessment.service.TchReportEvalService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * (학생) 리포트 평가 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(학생) 리포트 평가 API", description = "(학생) 리포트 평가 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntReportEvalController {
    private final TchReportEvalService tchReportEvalService;
    private final StntReportEvalService stntReportEvalService;

    @Loggable
    @RequestMapping(value = "/stnt/report/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 리포트 목록조회", description = "")
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbookId", description = "교과서(과목)ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "condition", description = "검색유형", required = false, schema = @Schema(type = "string", allowableValues = {"name", "gubun", "status", "date"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> stntReportEvalList(
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbookId", defaultValue = "") String textbookId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @RequestParam(name = "condition", defaultValue = "") String condition,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        // 검색유형이 날짜(date) 인 경우
        if ("date".equals(String.valueOf(paramData.get("condition")))) {
            String[] keywords = StringUtils.split(MapUtils.getString(paramData, "keyword"), "~");
            paramData.put("st_dt", keywords[0]);
            paramData.put("ed_dt", keywords[1]);
        }

        Object resultData = stntReportEvalService.findStntReportEvalList(paramData, pageable);
        String resultMessage = "평가 리포트 목록조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/stnt/report/eval/result/detail", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과 조회(자세히 보기)", description = "")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    public ResponseDTO<CustomBody> stntReportEvalResultDetail(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("evlId", "stntId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = stntReportEvalService.findStntReportEvalResultDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과 조회(자세히 보기)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/report/eval/result/header", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학습관리 > 리포트 > 평가 결과 조회(결과보기) Header", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생아이디 ID(로그인한 본인)", schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> stntReportEvalResultHeader(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        // 평가 리포트 공개여부 체크
        if (!StringUtils.equals(stntReportEvalService.findReportEvalPublicYn(paramData), "Y")) {
            return AidtCommonUtil.makeResultFail(paramData, null, "공개된 평가정보가 아닙니다.");
        }

        //TODO Session 에서 학생 아이디를 가져와야함
//        String sessionId = ""; //세션에서 가져온 학생 아이디
//        sessionId =  "532e8642-e29b-41d4-a746-446655441253"; // 테스트용

        if (stntId == null) {
            return AidtCommonUtil.makeResultFail(paramData, null, "로그인 정보가 없습니다.");
        }
        paramData.put("stntId", stntId);

        Object resultData = stntReportEvalService.findStntReportEvalResultHeader(paramData);
        String resultMessage = "(학생) 학습관리 > 리포트 > 평가 결과 조회(결과보기) Header";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/eval/result/summary", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학습관리 > 리포트 > 평가 결과 조회(결과보기)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> stntReportEvalResultSummary(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        // 평가 공개여부 체크
        if (!StringUtils.equals(stntReportEvalService.findReportEvalPublicYn(paramData), "Y")) {
            return AidtCommonUtil.makeResultFail(paramData, null, "공개된 평가정보가 아닙니다.");
        }

        //TODO Session 에서 학생 아이디를 가져와야함 - 세션 없음.
//        String sessionId = ""; //세션에서 가져온 학생 아이디
//        sessionId =  "532e8642-e29b-41d4-a746-446655441253"; // 테스트용

        if (stntId == null) {
            return AidtCommonUtil.makeResultFail(paramData, null, "로그인 정보가 없습니다.");
        }
        paramData.put("stntId", stntId);

        Object resultData = stntReportEvalService.findStntSrchReportEvalResultSummary(paramData, pageable);
        String resultMessage = "(학생) 학습관리 > 리포트 > 평가 결과 조회(결과보기)";

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
    @RequestMapping(value = "/stnt/report/eval/result/insite", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학습관리 > 리포트 > 평가 결과 조회(인사이트)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", schema = @Schema(type = "integer", example = "4"))
    @Parameter(name = "stntId", description = "학생 ID", schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "condition", description = "0.번호순, 1. 정답률 낮은 순, 2.풀이 시간 긴 순, 3.재확인 횟수 순, 4.답안 변경 횟수 순, 5.빨리 푼 순", schema = @Schema(type = "string", allowableValues = {"0", "1", "2", "3", "4", "5"}, defaultValue = "0"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "paramData", hidden = true)
    public ResponseDTO<CustomBody> stntReportEvalResultInsite(
            @RequestParam(name = "evlId", required = true, defaultValue = "") int evlId,
            @RequestParam(name = "stntId", required = true, defaultValue = "") String stntId,
            @RequestParam(name = "condition", defaultValue = "0") String condition,
            @Parameter(hidden = true) @PageableDefault(size = 1) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        // 평가 공개여부 체크
        if (!StringUtils.equals(stntReportEvalService.findReportEvalPublicYn(paramData), "Y")) {
            return AidtCommonUtil.makeResultFail(paramData, null, "공개된 평가정보가 아닙니다.");
        }

        //TODO Session 에서 학생 아이디를 가져와야함
//        String sessionId = ""; //세션에서 가져온 학생 아이디
//        sessionId =  "532e8642-e29b-41d4-a746-446655441253"; // 테스트용

        if (stntId == null) {
            return AidtCommonUtil.makeResultFail(paramData, null, "로그인 정보가 없습니다.");
        }
        paramData.put("stntId", stntId);

        log.debug("condition:{}", condition);
        Object resultData = stntReportEvalService.findStntSrchReportEvalResultInsite(paramData, pageable);
        String resultMessage = "(학생) 학습관리 > 리포트 > 평가 결과 조회(인사이트)";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


}
