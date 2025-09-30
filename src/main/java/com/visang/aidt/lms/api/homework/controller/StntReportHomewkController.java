package com.visang.aidt.lms.api.homework.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.homework.service.StntReportHomewkService;
import com.visang.aidt.lms.api.homework.service.TchReportHomewkService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@Tag(name = "(학생) 리포트 과제 API", description = "(학생) 리포트 과제 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntReportHomewkController {

    private final StntReportHomewkService stntReportHomewkService;

    @Loggable
    @RequestMapping(value = "/stnt/report/homewk/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생조회)과제리포트 목록조회", description = "[학생] 학습관리 > 홈 대시보드 > 과제리포트")
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu536"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5f8ba8f11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색유형", required = false, schema = @Schema(type = "string", allowableValues = {"name", "gubun", "status", "date"}, defaultValue = "date"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "20240501~20240530"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> stntReportHomewkList(
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "condition", defaultValue = "") String condition,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        // 검색유형이 날짜(date) 인 경우
        if ("date".equals(String.valueOf(paramData.get("condition")))) {
            String[] keywords = StringUtils.split((String) paramData.get("keyword"), "~");
            paramData.put("st_dt", keywords[0]);
            paramData.put("ed_dt", keywords[1]);
        }

        Object resultData = stntReportHomewkService.findStntReportHomewkList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생조회)과제리포트 목록조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/report/homewk/detail", method = {RequestMethod.GET})
    @Operation(summary = "과제리포트 결과조회(자세히 보기)", description = "[학생] 학습관리 > 홈 대시보드 > 과제리포트 (자세히보기)")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "11"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student41"))
    public ResponseDTO<CustomBody> stntReportHomewkDetail(
            @RequestParam(name = "taskId", defaultValue = "") int taskId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        List<String> requiredParams = Arrays.asList("taskId", "stntId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = stntReportHomewkService.findStntReportHomewkDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제리포트 결과조회(자세히 보기)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/report/homewk/summary", method = {RequestMethod.GET})
    @Operation(summary = "과제리포트 결과조회(결과보기)", description = "과제 결과 조회 (자세히 보기 헤더 세팅)")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "userId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student51"))
    public ResponseDTO<CustomBody> stntReportHomewkSummary(
            @RequestParam(name = "taskId", defaultValue = "") int taskId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        List<String> requiredParams = Arrays.asList("taskId", "userId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = stntReportHomewkService.findStntReportHomewkSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제리포트 결과조회(결과보기)");

    }

}
