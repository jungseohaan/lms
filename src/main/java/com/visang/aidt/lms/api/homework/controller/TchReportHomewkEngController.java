package com.visang.aidt.lms.api.homework.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.homework.service.TchReportHomewkEngService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.controller
 * fileName : TchReportHomewkEngController
 * USER : 조승현
 * date : 2024-04-04
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-04-04         조승현          최초 생성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@RestController
@Tag(name = "(교사) 리포트 과제 API[영어]", description = "(교사) 리포트 과제 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchReportHomewkEngController {

    private final TchReportHomewkEngService tchReportHomewkEngService;

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/homewk/detail/eng", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 과제 결과 조회(자세히 보기) [영어]", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8" ))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student51" ))
    public ResponseDTO<CustomBody> tchStntSrchReportTaskDetail(
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = tchReportHomewkEngService.findStntSrchReportTaskDetail(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생조회 > 과제 결과 조회(자세히 보기) [영어]");
    }
}
