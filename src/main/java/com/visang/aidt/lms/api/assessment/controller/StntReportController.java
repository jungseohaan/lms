package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.StntReportService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * (학생) 리포트 평가 API Controller
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@RestController
@Tag(name = "(학생) 리포트 API", description = "(학생) 리포트 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntReportController {
    private final StntReportService stntReportService;


    /**
     * 교과서/평가/과제 최신 활동 조회
     */
    @Loggable
    @RequestMapping(value = "/stnt/report/last-activity", method = {RequestMethod.GET})
    @Operation(summary = "교과서/평가/과제/스스로학습 최신 활동 조회", description = "교과서(수업), 평가, 과제, 스스로학습 중 가장 최근에 진행된 활동을 조회한다.")
    @Parameter(name = "userId", description = "유저 ID", schema = @Schema(type = "string", example = "neweng2163-s1"))
    @Parameter(name = "claId", description = "클래스 ID", schema = @Schema(type = "string", example = "595e74742d7f49d9ae93f07d4d202483"))
    @Parameter(name = "textbkId", description = "교과서 ID", schema = @Schema(type = "string", example = "6982"))
    public ResponseDTO<CustomBody> getTchReportLastActivity(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntReportService.getStntReportLastActivity(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서/평가/과제/스스로학습 최신 활동 조회");

    }

}
