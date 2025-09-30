package com.visang.aidt.lms.api.homework.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.homework.service.TchReportHomewkService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.controller
 * fileName : TchReportHomewkController
 * USER : 조승현
 * date : 2024-01-29
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-29         조승현          최초 생성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@RestController
@Tag(name = "(교사) 리포트 과제 API", description = "(교사) 리포트 과제 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchReportHomewkController {

    private final TchReportHomewkService tchReportHomewkService;

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 과제리포트", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "aidt2"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772669"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색유형", required = false, schema = @Schema(type = "string", allowableValues = {"name", "gubun", "status", "date"}, defaultValue = "gubun"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "3"))
    @Parameter(name = "reqGradeTaskAt", description = "채점 필요한 과제 여부", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchReportHomewkTaskList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        // 검색유형이 날짜(date) 인 경우
        if ("date".equals(String.valueOf(paramData.get("condition")))) {
            String[] keywords = StringUtils.split(MapUtils.getString(paramData, "keyword"), "~");
            paramData.put("st_dt", keywords[0]);
            paramData.put("ed_dt", keywords[1]);
        }

        Object resultData = tchReportHomewkService.findTchReportHomewkTaskList(paramData, pageable);
        String resultMessage = "[교사] 학급관리 > 홈 대시보드 > 과제리포트";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 숙제리포트(자세히보기-공통문항)", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "3049"))
    @Parameter(name = "submAt", description = "제출여부", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> tchReportHomewkResultDetailList(
            @RequestParam(name = "taskId", defaultValue = "") int taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findReportHomewkResultList(paramData);
        String resultMessage = "[교사] 학급관리 > 홈 대시보드 > 숙제리포트(자세히보기-공통문항)";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/detail/mdul", method = {RequestMethod.GET})
    @Operation(summary = "과제 리포트 결과 조회(자세히 보기) > 모듈", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "taskIemId", description = "모듈ID", required = true, schema = @Schema(type = "string", example = "2580"))
    @Parameter(name = "subId", description = "서브ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchReportHomewkResultDetail_mdul(
            @RequestParam(name = "subId", defaultValue = "0") int subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        if (paramData.get("subId") == null) {
            paramData.put("subId", 0);
        }

        Object resultData = tchReportHomewkService.tchReportHomewkResultDetailMdul(paramData);
        String resultMessage = "과제 리포트 결과 조회(자세히 보기) > 모듈";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/homewk/summary", method = {RequestMethod.GET})
    @Operation(summary = "(학생조회)과제 결과 조회", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "userId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student51"))
    public ResponseDTO<CustomBody> tchStntSrchReportTaskSummary(
            @RequestParam(name = "taskId", defaultValue = "") int taskId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findTchStntSrchReportTaskSummary(paramData);
        String resultMessage = "(학생조회)과제 결과 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/detail/stnt", method = {RequestMethod.GET})
    @Operation(summary = "과제 리포트 결과 조회(자세히 보기) > 학생", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "taskIemId", description = "모듈ID", required = true, schema = @Schema(type = "string", example = "2580"))
    @Parameter(name = "subId", description = "서브ID", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "userId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student51"))
    public ResponseDTO<CustomBody> tchReportHomewkResultDetail_stnt(
            @RequestParam(name = "subId", defaultValue = "0") int subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        if (paramData.get("subId") == null) {
            paramData.put("subId", 0);
        }

        Object resultData = tchReportHomewkService.tchReportHomewkResultDetailStnt(paramData);
        String resultMessage = "과제 리포트 결과 조회(자세히 보기) > 학생";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/errata/mod", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 과제 결과 조회 (자세히 보기 : 정오표 수정)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "taskId": 1640,
                                    "stntId": "mathreal103-s1",
                                    "taskIemId": "MSTG72353",
                                    "subId": 0,
                                    "errata": 3
                                }
                            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportHomewkResultErrataMod(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        AidtCommonUtil.checkSubIdParameter(paramData);

        List<String> requiredParams = Arrays.asList("taskId", "stntId", "taskIemId", "subId", "errata");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Map resultData = tchReportHomewkService.modifyTchReportHomewkResultErrataMod(paramData);
        if (Boolean.FALSE.equals(resultData.get("resultOk"))) {
            Exception err = (Exception) resultData.get("resultErr");
            return AidtCommonUtil.makeResultFail(paramData, null, err.getMessage());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 결과 조회 (자세히 보기 : 정오표 수정)");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/errata/appl", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 과제 모듈 배점 수정반영", description = "과제 리포트 목록 조회에서 [수정 반영] 클릭시 처리하는 기능")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "taskId" : 8
                                }
                            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportHomewkResultErrataAppl(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("taskId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Map resultData = tchReportHomewkService.modifyTchReportHomewkResultErrataAppl(paramData);
        if (Boolean.FALSE.equals(resultData.get("resultOk"))) {
            Exception err = (Exception) resultData.get("resultErr");
            return AidtCommonUtil.makeResultFail(paramData, null, err.getMessage());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 모듈 배점 수정반영");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/fdb/mod", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 과제 결과 조회 (자세히 보기 : 피드백 저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "taskId" : 8,
                                    "stntId" : "student51",
                                    "taskIemId" : 2580,
                                    "subId" : "0",
                                    "fdbDc" : "테스트 피드백"
                                }
                            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportHomewkResultFdbMod(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        AidtCommonUtil.checkSubIdParameter(paramData);

        List<String> requiredParams = Arrays.asList("taskId", "stntId", "taskIemId", "subId", "fdbDc");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Map resultData = tchReportHomewkService.modifyTchReportHomewkResultFdbMod(paramData);
        if (Boolean.FALSE.equals(resultData.get("resultOk"))) {
            Exception err = (Exception) resultData.get("resultErr");
            return AidtCommonUtil.makeResultFail(paramData, null, err.getMessage());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 결과 조회 (자세히 보기 : 피드백 저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/mod", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 과제 결과 보기(결과보기) : 과제 결과 수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "taskId" : 8,
                                    "stntId" : "student51",
                                    "taskIemId" : 2580,
                                    "subId" : 0,
                                    "taskResultAnct" : 2
                                }
                            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportHomewkResultMod(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        AidtCommonUtil.checkSubIdParameter(paramData);

        List<String> requiredParams = Arrays.asList("taskId", "stntId", "taskIemId", "subId", "taskResultAnct");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Map resultData = tchReportHomewkService.modifyTchReportHomewkResultMod(paramData);
        if (Boolean.FALSE.equals(resultData.get("resultOk"))) {
            Exception err = (Exception) resultData.get("resultErr");
            return AidtCommonUtil.makeResultFail(paramData, null, err.getMessage());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 결과 보기(결과보기) : 과제 결과 수정");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/summary", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 과제 결과 보기(결과보기)", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    public ResponseDTO<CustomBody> tchReportHomewkResultSummary(
            @RequestParam(name = "taskId") String taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findReportHomewkResultSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 결과 보기(결과보기)");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/ind/result", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기):목록", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    public ResponseDTO<CustomBody> tchReportHomewkResultIndResult(
            @RequestParam(name = "taskId") String taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findReportHomewkResultIndResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기):목록");

    }

    @RequestMapping(value = "/tch/report/homewk/result/ind/mdul", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기):모듈", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "11"))
    @Parameter(name = "userId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student41"))
    public ResponseDTO<CustomBody> tchReportHomewkResultIndMdul(
            @RequestParam(name = "taskId") String taskId,
            @RequestParam(name = "userId") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findReportHomewkResultIndMdul(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기):모듈");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/ind/summary", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기)", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "userId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student51"))
    public ResponseDTO<CustomBody> tchReportHomewkResultIndSummary(
            @RequestParam(name = "taskId") String taskId,
            @RequestParam(name = "userId") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findReportHomewkResultIndSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기)");

    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/homewk/list", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 과제 리포트 목록조회", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "aidt4"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772671"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student51"))
    @Parameter(name = "textbkId", description = "교과서(과목)ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색유형", required = false, schema = @Schema(type = "string", allowableValues = {"name", "gubun", "status", "date"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchStntSrchReportTaskList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
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

        Object resultData = tchReportHomewkService.findStntSrchReportTaskList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생조회 > 과제 리포트 목록조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/stnt-srch/report/homewk/detail", method = {RequestMethod.GET})
    @Operation(summary = "학생조회 > 과제 결과 조회(자세히 보기)", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "student51"))
    public ResponseDTO<CustomBody> tchStntSrchReportTaskDetail(
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findStntSrchReportTaskDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생조회 > 과제 결과 조회(자세히 보기)");

    }

    /**
     * 과제 공개 처리 API
     *
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/tch/report/homewk/open", method = {RequestMethod.POST})
    @Operation(summary = "교사 과제리포트 > 공개처리", description = "교사 과제리포트 > 공개처리")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"taskId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchReportTaskOepn(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = (Map<String, Object>) tchReportHomewkService.modifyReportTaskOpen(paramData);
        String resultMessage = "과제리포트 공개처리 API";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/general-review/save", method = {RequestMethod.POST})
    @Operation(summary = "과제 리포트 총평 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":\"11\"," +
                            "\"userId\":\"aidt3\"," +
                            "\"genrvw\":\"1\"," +
                            "\"stdtPrntRlsAt\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> createTchReportHomewkGeneralReviewSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.createTchReportHomewkGeneralReviewSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 리포트 총평 저장");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/general-review/info", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 홈 대시보드 > 과제 리포트 > 자세히보기 > 총평조회", description = "[교사] 홈 대시보드 > 과제 리포트 > 자세히보기 > 총평조회")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "7"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student50"))
    public ResponseDTO<CustomBody> tchReportEvalGeneralReviewInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchReportHomewkService.findTchReportTaskGeneralReviewInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 홈 대시보드 > 과제 리포트 > 자세히보기 > 총평조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/general-review/ai-evl-word", method = {RequestMethod.GET})
    @Operation(summary = "과제 리포트 총평 AI 평어", description = "[교사] 홈 대시보드 > 과제 리포트 > 자세히보기 (AI 평어) - 총평 등록시 사용될 대단원 목록과 학생의 정답률에 따른 상/중/하 정보를 조회한다.")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "23"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student52"))
    public ResponseDTO<CustomBody> tchReportHomewkGeneralReviewAiEvlWord(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("taskId", "userId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = tchReportHomewkService.findTchReportHomewkGeneralReviewAiEvlWord(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 리포트 총평 AI 평어");

    }

    @Loggable
    @RequestMapping(value = "/tch/report/homewk/result/detail/summary", method = {RequestMethod.GET})
    @Operation(summary = "과제 리포트 결과 조회 (자세히보기 (4))", description = "")
    @Parameter(name = "taskId", description = "과제ID", required = true, schema = @Schema(type = "integer", example = "2392"))
    public ResponseDTO<CustomBody> tchReportEvalResultDetailSummary(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchReportHomewkService.findReportHomewkResultDetailSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 리포트 결과 조회 (자세히보기 (4))");

    }
}
