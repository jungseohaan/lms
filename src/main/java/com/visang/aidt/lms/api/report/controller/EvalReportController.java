package com.visang.aidt.lms.api.report.controller;


import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.report.dto.EvalReportListReqDto;
import com.visang.aidt.lms.api.report.service.EvalReportService;
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
 * 테스트파라미터 DB: s1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "(리포트) 평가 API", description = "(리포트) 평가 API")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EvalReportController {

    private final EvalReportService evalReportService;

    @Loggable
    @GetMapping(value = "/report/eval/tch/summary")
    @Operation(summary = "(교사) 평가 리포트 개요 조회", description = "(교사) 평가 리포트 개요 조회")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "122096"))
    public ResponseDTO<CustomBody> evlReportSummary (
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = evalReportService.findEvlReportSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 리포트 개요 조회");
    }

    @Loggable
    @GetMapping(value = "/report/eval/stnt/summary")
    @Operation(summary = "(학생) 평가 리포트 개요 조회", description = "(학생) 평가 리포트 개요 조회")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "122096"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "engbook229-s1"))
    public ResponseDTO<CustomBody> stntEvlReportSummary (
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = evalReportService.findStntEvlReportSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생) 평가 리포트 개요 조회");
    }

    @Loggable
    @GetMapping(value = "/report/eval/unsubmitted")
    @Operation(summary = "평가 미응시 학생 목록 조회", description = "평가 미응시 학생 목록 조회")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "122096"))
    public ResponseDTO<CustomBody> evalUnsubmittedStudents (
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = evalReportService.findUnsubmittedStudents(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 미응시 학생 목록 조회");
    }

    @Loggable
    @GetMapping(value = "/report/eval/list")
    @Operation(summary = "리포트 평가 목록 조회", description = "")
    @Parameter(name = "page", description = "페이지번호", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = true, schema = @Schema(type = "integer", example = "4"))
    public ResponseDTO<CustomBody> tchEvalReportList(
            @Valid @ModelAttribute @ParameterObject EvalReportListReqDto paramData,
            @Parameter(hidden = true) @PageableDefault(size = 6) Pageable pageable
    ) throws Exception {
        ValidationResult resultData = evalReportService.findEvalReportList(paramData, pageable);
        if(!resultData.isValid()){
            return AidtCommonUtil.makeResultFail(paramData, null, resultData.getMessage());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData.getData(), "평가 목록 조회");
    }

    @Loggable
    @GetMapping(value = "/report/eval/scoring/list")
    @Operation(summary = "우리반 채점 결과표 조회", description = "")
    @Parameter(name = "evlId", description = "평가ID", required = true, schema = @Schema(type = "integer", example = "122096"))
    @Parameter(name = "submAt", description = "제출 여부", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "tchId", description = "교사 ID", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "stntId", description = "학생 Id", required = false, schema = @Schema(type = "string", example = "engbook229-s1"))
    @Parameter(name = "page", description = "페이지번호", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchReportEvalResultDetailList(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = evalReportService.findReportEvalResultDetailList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 채점 결과표 조회");
    }

    @Loggable
    @PostMapping(value = "/report/eval/change/check")
    @Operation(summary = "교사) 리포트 확인여부 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "evlId": "122096"
                        }
                        """
                    )
            }))
    public ResponseDTO<CustomBody> changeReportCheckAt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = evalReportService.changeReportCheckAt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사) 리포트 확인여부 변경");

    }

    @Loggable
    @PostMapping(value = "/report/eval/encouragement/notify")
    @Operation(summary = "독려 알림 보내기", description = "미제출 학생들에게 독려 알림 전송.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "evlId": "124931",
                            "tchId": "engbook229-t"
                        }
                        """
                    )
            }))
    public ResponseDTO<CustomBody> sendEvalEncouragementNotification(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = evalReportService.sendEvalEncouragementNotification(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "독려 알림 전송");
    }

    @Loggable
    @PostMapping("/report/eval/general-review/saveAll")
    @Operation(summary = "평가 리포트 총평 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "evlId": "8",
                            "stntId": ["student47", "student48", "student49"],
                            "genrvw": "1",
                            "stdtPrntRlsAt": "Y",
                            "tchId": "aidt3"
                        }
                        """
                    )
            }
            ))
    public ResponseDTO<CustomBody> createTchReportEvalGeneralReviewSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = evalReportService.createTchEvalGeneralReviewSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 리포트 총평 저장");
    }

    @GetMapping("/ai/prscr/check")
    @Operation(summary = "처방학습 출제여부 확인", description = "해당 평가에 출제된 처방학습을 확인 합니다.")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "69791"))
    public ResponseDTO<CustomBody> createAiPrescription(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = evalReportService.prscrCheck(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI 처방학습 학생 조회");
    }

    @Loggable
    @GetMapping(value = "/report/eval/recommended-questions")
    @Operation(summary = "개념영상문항 목록 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "87479"))
    @Parameter(name = "page", description = "페이지번호", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = true, schema = @Schema(type = "integer", example = "3"))
    public ResponseDTO<CustomBody> recommendedQuestions (
            @Parameter(hidden = true) @PageableDefault(size = 3) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = evalReportService.findRecommendedQuestions(pageable, paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "개념영상문항 목록 조회");
    }

    @Loggable
    @GetMapping(value = "/report/eval/aiPrscr")
    @Operation(summary = "AI 처방 과제 생성 데이터 확인", description = "데이터 확인을 위한 테스트 코드")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "87479"))
    public ResponseDTO<CustomBody> selectAiPrscrEvlToTask (
            @Parameter(hidden = true) @RequestParam Map<String, Object> evlId
    ) throws Exception {

        Map<String, Object> resultData = evalReportService.selectAiPrscrEvlToTask(evlId);

        return AidtCommonUtil.makeResultSuccess(evlId, resultData, "개념영상문항 목록 조회");
    }
}
