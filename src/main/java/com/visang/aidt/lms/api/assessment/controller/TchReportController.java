package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.TchReportService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.StntDsbdService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * (교사) 리포트 평가 API Controller
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@RestController
@Tag(name = "(교사) 리포트 API", description = "(교사) 리포트 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchReportController {
    private final TchReportService tchReportService;
    private final StntDsbdService stntDsbdService;

    /**
     * 종합리포트
     */
    @Loggable
    @RequestMapping(value = "/tch/report/total", method = {RequestMethod.GET})
    @Operation(summary = "종합리포트", description = "")
    @Parameter(name = "userId", description = "유저 ID", schema = @Schema(type = "string", example = "vstea10"))
    @Parameter(name = "claId", description = "클래스 ID", schema = @Schema(type = "string", example = "1dfd61e6b8fb11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "smstr", description = "학기", schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchReportTotal(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportService.getTchReportTotal(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "종합리포트");


    }


    /**
     * 종합리포트(학생조회)
     */
    @Loggable
    @RequestMapping(value = "/tch/report/search/stnt", method = {RequestMethod.GET})
    @Operation(summary = "종합리포트(학생조회)", description = "")
    @Parameter(name = "userId", description = "유저 ID", schema = @Schema(type = "string", example = "vstea10"))
    @Parameter(name = "stntId", description = "학생 ID", schema = @Schema(type = "string", example = "vstea10"))
    @Parameter(name = "claId", description = "클래스 ID", schema = @Schema(type = "string", example = "1dfd61e6b8fb11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "smstr", description = "학기", schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchReportSearchStnt(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        paramData.put("svc_call_type","tch");
        Object resultData = stntDsbdService.getStntDsbdReportTotal(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "종합리포트(학생조회)");


    }

    /**
     * 리포트 범위 정보 조회
     */
    @Loggable
    @RequestMapping(value = "/tch/report/exam-scope", method = {RequestMethod.GET})
    @Operation(summary = "리포트 결과보기 (리포트 범위 보기)", description = "리포트에 사용된 셋트지에 속한 콘텐츠(아티클)의 출제 범위를 조회한다.")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "16"))
    @Parameter(name = "setsId", description = "셋트지 ID", required = true, schema = @Schema(type = "string", example = "690"))
    public ResponseDTO<CustomBody> getTchReportExamScope(
            @RequestParam(name = "textbkId", defaultValue = "16") int textbkId,
            @RequestParam(name = "setsId", defaultValue = "690") String setsId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportService.getTchReportExamScope(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리포트 범위 정보 조회");

    }



    /**
     * 종합리포트(학생조회)
     */
    @Loggable
    @RequestMapping(value = {"/tch/report/std/statis","/stnt/report/std/statis"}, method = {RequestMethod.GET})
    @Operation(summary = "수업리포트 통계", description = "수업리포트 통계")
    @Parameter(name = "userId", description = "유저 ID", schema = @Schema(type = "string", example = "vstea10"))
    @Parameter(name = "stntId", description = "학생 ID", schema = @Schema(type = "string", example = "vstea10"))
    @Parameter(name = "claId", description = "클래스 ID", schema = @Schema(type = "string", example = "1dfd61e6b8fb11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "tabId", description = "탭ID", schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "crculId", description = "차시", schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getStdReportStatis(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {


        Object resultData = tchReportService.getStdReportStatis(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업리포트 통계");

    }

    /**
     * 교과서/평가/과제 최신 활동 조회
     */
    @Loggable
    @RequestMapping(value = "/tch/report/last-activity", method = {RequestMethod.GET})
    @Operation(summary = "교과서/평가/과제 최신 활동 조회", description = "교과서(수업), 평가, 과제 중 가장 최근에 진행된 활동을 조회한다.")
    @Parameter(name = "userId", description = "유저 ID", schema = @Schema(type = "string", example = "vstea10"))
    @Parameter(name = "claId", description = "클래스 ID", schema = @Schema(type = "string", example = "1dfd61e6b8fb11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchReportLastActivity(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportService.getTchReportLastActivity(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서/평가/과제 최신 활동 조회");

    }

}
