package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.lecture.service.StntReportLectureService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * (학생) 수업리포트 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(학생) 수업리포트 API", description = "(학생) 수업리포트 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntReportLectureController {
    private final StntReportLectureService stntReportLectureService;

    @Loggable
    @RequestMapping(value = "/stnt/report/lecture/detail", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 수업리포트 결과 조회 (자세히보기)", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathreal151-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5a0a89a258bd48968a4eedcc229e2b04" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "373" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7" ))
    @Parameter(name = "tabId", description = "탭 ID", required = false, schema = @Schema(type = "integer", example = "29771" ))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathreal151-s1" ))
    public ResponseDTO<CustomBody> stntReportLectureDetail(
        @RequestParam(name = "userId",   defaultValue = "") String userId,
        @RequestParam(name = "claId",   defaultValue = "") String claId,
        @RequestParam(name = "textbkId",   defaultValue = "") String textbkId,
        @RequestParam(name = "crculId",   defaultValue = "") String crculId,
        @RequestParam(name = "tabId",   defaultValue = "") String tabId,
        @RequestParam(name = "stntId",   defaultValue = "") String stntId,
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            List<String> requiredParams = Arrays.asList("userId","claId","textbkId","crculId","stntId");
            AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);
            Object resultData = stntReportLectureService.findStntReportLectureDetail(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 수업리포트 결과 조회 (자세히보기)");
    }

}
