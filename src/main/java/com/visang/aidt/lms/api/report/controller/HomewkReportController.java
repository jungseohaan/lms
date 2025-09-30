package com.visang.aidt.lms.api.report.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.report.dto.HomewkReportListReqDto;
import com.visang.aidt.lms.api.report.service.HomewkReportService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.ValidationResult;
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
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * (교사) 리포트 평가 API Controller
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@RestController
@Tag(name = "(리포트) 과제 API", description = "(리포트) 과제 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class HomewkReportController {

    private final HomewkReportService homewkReportService;

    @Loggable
    @GetMapping(value = "/report/homewk/tch/summary")
    @Operation(summary = "교사) 과제 리포트 개요 조회", description = "(교사) 과제 리포트 개요 조회")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "3880"))
    public ResponseDTO<CustomBody> evlReportSummary (
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = homewkReportService.findTaskReportSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 리포트 개요 조회");
    }

    @Loggable
    @GetMapping(value = "/report/homewk/stnt/summary")
    @Operation(summary = "학생) 과제 리포트 개요 조회", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "3880"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "engbook229-s1"))
    public ResponseDTO<CustomBody> stntEvlReportSummary (
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = homewkReportService.findStntTaskReportSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생) 과제 리포트 개요 조회");
    }

    @Loggable
    @GetMapping(value = "/report/homewk/unsubmitted")
    @Operation(summary = "과제 미응시 학생 목록 조회", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "3880"))
    public ResponseDTO<CustomBody> evalUnsubmittedStudents (
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = homewkReportService.findUnsubmittedStudents(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 미응시 학생 목록 조회");
    }

    @Loggable
    @GetMapping(value = "/report/homewk/list")
    @Operation(summary = "리포트 과제 목록 조회", description = "")
    public ResponseDTO<CustomBody> tchEvalList(
            @Valid @ModelAttribute @ParameterObject HomewkReportListReqDto paramData,
            @Parameter(hidden = true) @PageableDefault(size = 6) Pageable pageable
    ) throws Exception {
        ValidationResult resultData = homewkReportService.findHomewkList(paramData, pageable);
        if(!resultData.isValid()){
            return AidtCommonUtil.makeResultFail(paramData, null, resultData.getMessage());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData.getData(), "과제 목록 조회");
    }

    @Loggable
    @GetMapping(value = "/report/homewk/scoring/list")
    @Operation(summary = "우리반 채점 결과표 조회", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "3880"))
    @Parameter(name = "submAt", description = "제출 여부", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "tchId", description = "교사 ID", required = false, schema = @Schema(type = "string", example = "engbook229-s1"))
    @Parameter(name = "stntId", description = "학생 Id", required = false, schema = @Schema(type = "string", example = "engbook229-s1"))
    @Parameter(name = "page", description = "페이지번호", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchReportEvalResultDetailList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = homewkReportService.findReportHomewkResultList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 채점 결과표 조회");
    }

    @Loggable
    @PostMapping(value = "/report/homewk/change/check")
    @Operation(summary = "교사) 리포트 확인여부 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "taskResultId": "1090017"
                        }
                        """
                    )
            }))
    public ResponseDTO<CustomBody> changeReportCheckAt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = homewkReportService.changeReportCheckAt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사) 리포트 확인여부 변경");

    }

    @Loggable
    @PostMapping(value = "/report/homewk/encouragement/notify")
    @Operation(summary = "독려 알림 보내기", description = "미제출 학생들에게 독려 알림 전송.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "taskId": "3880",
                            "tchId" : "engbook229-t"
                        }
                        """
                    )
            }))
    public ResponseDTO<CustomBody> sendEvalEncouragementNotification(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = homewkReportService.sendTaskEncouragementNotification(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "독려 알림 전송");
    }


    @Loggable
    @PostMapping(value = "/report/homewk/general-review/saveAll")
    @Operation(summary = "과제 리포트 총평 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "taskId": "8",
                            "stntId": ["student47", "student48", "student49"],
                            "genrvw": "1",
                            "stdtPrntRlsAt": "Y",
                            "tchId" : "engbook229-t"
                        }
                        """
                    )
            }
            ))
    public ResponseDTO<CustomBody> createTchReportHomewkGeneralReviewSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = homewkReportService.createTchReportHomewkGeneralReviewSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 리포트 총평 저장");
    }
}
