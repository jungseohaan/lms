package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.lecture.service.StntReportLectureService;
import com.visang.aidt.lms.api.lecture.service.TchLectureReportService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@RestController
@Tag(name = "(교사) 수업리포트 API", description = "(교사) 수업리포트 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchLectureReportController {
    private final TchLectureReportService tchLectureReportService;
    private final StntReportLectureService stntReportLectureService;


    @RequestMapping(value = "/tch/report/lecture/result/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기) 1", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "vstea1" ))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7" ))
    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = false, schema = @Schema(type = "integer", example = "0" ))
    public ResponseDTO<CustomBody> tchReportLectureResultList(
            @RequestParam(name = "userId",   defaultValue = "") String userId,
            @RequestParam(name = "claId",   defaultValue = "") String claId,
            @RequestParam(name = "textbkId",   defaultValue = "") int textbkId,
            @RequestParam(name = "crculId",   defaultValue = "") int crculId,
            @RequestParam(name = "tabId",   defaultValue = "0") int tabId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {

        try {
            Object resultData = tchLectureReportService.findReportLectureResultList(paramData);
            String resultMessage = "[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기) 1";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/result/detail/mdul", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기2-콘텐츠 정보)", description = "")
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7" ))
    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = false, schema = @Schema(type = "integer", example = "127" ))
    @Parameter(name = "dtaIemId", description = "(선택된) 모듈 ID", required = false, schema = @Schema(type = "string", example = "1" ))
    @Parameter(name = "subId", description = "(선택된) 서브 ID", required = false, schema = @Schema(type = "integer", example = "0" ))
    public ResponseDTO<CustomBody> tchReportLectureResultDetailMdul(
            @RequestParam(name = "subId",   defaultValue = "0") int subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {

        try {
            if (paramData.get("subId") == null ) {
                paramData.put("subId", 0);
            }

            Object resultData = tchLectureReportService.findReportLectureResultDetailMdulList(paramData);
            String resultMessage = "[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기2-콘텐츠 정보)";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/result/detail/stnt", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기3-학생답안)", description = "")
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7" ))
    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = true, schema = @Schema(type = "integer", example = "29771" ))
    @Parameter(name = "dtaIemId", description = "(선택된) 모듈 ID", required = true, schema = @Schema(type = "string", example = "MSTG29533" ))
    @Parameter(name = "subId", description = "(선택된) 서브 ID", required = true, schema = @Schema(type = "integer", example = "0" ))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathreal151-s1" ))
    @Parameter(name = "reExmNum", description = "다시풀기 회차", required = false, schema = @Schema(type = "integer", example = "0" ))
    public ResponseDTO<CustomBody> tchReportLectureResultDetailStnt(
            @RequestParam(name = "subId",   defaultValue = "0") int subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {

        try {
            if (paramData.get("subId") == null ) {
                paramData.put("subId", 0);
            }

            Object resultData = tchLectureReportService.findReportLectureResultDetailStnt(paramData);
            String resultMessage = "[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기3-학생답안)";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }




    @RequestMapping(value = "/tch/report/lecture/result/errata/mod", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 수업리포트 결과 조회 (자세히 보기 : 정오표 수정)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "dtaResultDetailId" : 8,
                    "errata" : 3
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportLectureResultErrataMod(
            @RequestBody Map<String, Object> paramData
    ) {
        try {
            List<String> requiredParams = Arrays.asList("dtaResultDetailId","errata");
            AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

            Map resultData = tchLectureReportService.modifyTchReportLectureResultErrataMod(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 수업리포트 결과 조회 (자세히 보기 : 정오표 수정)");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));

            Map resultMap = new HashMap();
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");

            return AidtCommonUtil.makeResultFail(paramData, resultMap, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/result/fdb/mod", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 수업리포트 결과 조회 (자세히 보기 : 피드백 저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "dtaResultDetailId" : 8,
                    "fdbDc" : "테스트 피드백"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> tchReportLectureResultFdbMod(
            @RequestBody Map<String, Object> paramData
    ) {
        try {
            // 2. 필수 파라미터 누락 확인
            if (!paramData.containsKey("dtaResultDetailId") || !paramData.containsKey("fdbDc")) {
                log.error("필수 파라미터 누락: {}", paramData);

                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "실패: 필수 파라미터 누락");

                return AidtCommonUtil.makeResultFail(paramData, resultMap, "필수 파라미터 누락");
            }

            List<String> requiredParams = Arrays.asList("dtaResultDetailId","fdbDc");
            AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

            Map resultData = tchLectureReportService.modifyTchReportLectureResultFdbMod(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 수업리포트 결과 조회 (자세히 보기 : 피드백 저장)");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));

            Map resultMap = new HashMap();
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");

            return AidtCommonUtil.makeResultFail(paramData, resultMap, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/stnt-srch/report/lecture/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드(학생조회) > 수업리포트 (자세히보기)", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea38" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad483ba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7" ))
    @Parameter(name = "tabId", description = "탭 ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu388" ))
    public ResponseDTO<CustomBody> tchStntSrchReportLectureResultDetail(
            @RequestParam(name = "userId",   defaultValue = "") String userId,
            @RequestParam(name = "claId",   defaultValue = "") String claId,
            @RequestParam(name = "textbkId",   defaultValue = "") String textbkId,
            @RequestParam(name = "crculId",   defaultValue = "") String crculId,
            @RequestParam(name = "tabId",   defaultValue = "") String tabId,
            @RequestParam(name = "stntId",   defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {

        try {
            // 학생 로직을 그대로 사용 (/stnt/report/lecture/detail) - 변경시 분리 필요
            paramData.put("svc_call_type","tch");
            Object resultData = stntReportLectureService.findStntReportLectureDetail(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드(학생조회) > 수업리포트 (자세히보기-콘텐츠정보)");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/general-review/save", method = {RequestMethod.POST})
    @Operation(summary = "수업 리포트 총평 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"textbkTabId\":\"1941\"," +
                            "\"userId\":\"vsstu615\"," +
                            "\"genrvw\":\"N\"," +
                            "\"stdtPrntRlsAt\":\"N\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> createTchReportLectureGeneralReviewSave(
            @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchLectureReportService.createTchReportLectureGeneralReviewSave(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 리포트 총평 저장");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/general-review/info", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 홈 대시보드 > 수업 리포트 > 자세히보기 > 총평조회", description = "[교사] 홈 대시보드 > 수업 리포트 > 자세히보기 > 총평조회")
    @Parameter(name = "textbkTabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "2272"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "qa5-s20"))
    public ResponseDTO<CustomBody> tchReportEvalGeneralReviewInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchLectureReportService.findTchReportStdDtaGeneralReviewInfo(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 홈 대시보드 > 수업 리포트 > 자세히보기 > 총평조회");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/general-review/ai-evl-word", method = {RequestMethod.GET})
    @Operation(summary = "수업 리포트 총평 AI 평어", description = "[교사] 홈 대시보드 > 수업 리포트 > 자세히보기 (AI 평어) - 총평 등록시 사용될 대단원 목록과 학생의 정답률에 따른 상/중/하 정보를 조회한다.")
    @Parameter(name = "textbkTabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "2"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu615"))
    public ResponseDTO<CustomBody> tchReportLectureGeneralReviewAiEvlWord(
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            List<String> requiredParams = Arrays.asList("textbkTabId","userId");
            AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

            Object resultData = tchLectureReportService.findTchReportLectureGeneralReviewAiEvlWord(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 리포트 총평 AI 평어");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/result/mdul", method = {RequestMethod.GET})
    @Operation(summary = "답안보기 - 해당 목차의 모든 콘텐츠", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "engbook1400-t" ))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "29" ))
    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = false, schema = @Schema(type = "integer", example = "" ))
    @Parameter(name = "subMitAt", description = "답안 제출한 문제만 보기", required = true, schema = @Schema(type = "string", example = "Y", allowableValues = {"Y", "N"}))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "9"))
    public ResponseDTO<CustomBody> tchReportLectureResultMdul(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchLectureReportService.findTchReportLectureResultMdul(paramData, pageable);
            String resultMessage = "답안보기 - 해당 목차의 모든 콘텐츠";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/result/mdul/detail", method = {RequestMethod.POST})
    @Operation(summary = "답안보기 - 해당 목차의 모든 콘텐츠 (상세)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                 "tabId": "1405484",
                                "subId": "0",
                                "textbkId": "1150",
                                "claId": "a498e4c7d7634773b147b5de262ba762",
                                "dtaIemId": "39127",
                                "userId": "engbook1400-t",
                                "crculId": "29",
                                "setsId": "1325"
                            }
                            """
                    )
            }
        ))
    /*
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "engbook1400-t" ))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "29" ))
    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = true, schema = @Schema(type = "integer", example = "1405484" ))
    @Parameter(name = "setsId", description = "(선택된) set ID", required = true, schema = @Schema(type = "integer", example = "1325" ))
    @Parameter(name = "dtaIemId", description = "(선택된) 모듈 ID", required = true, schema = @Schema(type = "integer", example = "39127" ))
    @Parameter(name = "subId", description = "(선택된) 서브 ID", required = true, schema = @Schema(type = "integer", example = "0" ))
     */
    public ResponseDTO<CustomBody> tchReportLectureResultMdulDetail(
            @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchLectureReportService.findTchReportLectureResultMdulDetail(paramData);
            String resultMessage = "답안보기 - 해당 목차의 모든 콘텐츠 (상세)";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/report/lecture/result/act", method = {RequestMethod.GET})
    @Operation(summary = "활동 결과 - 학생별 활동 횟수", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "engbook1400-t" ))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "29" ))
    public ResponseDTO<CustomBody> tchReportLectureResultAct(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchLectureReportService.findTchReportLectureResultAct(paramData);
            String resultMessage = "활동 결과 - 학생별 활동 횟수";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/stnt/report/lecture/result/act", method = {RequestMethod.GET})
    @Operation(summary = "활동 결과 - 해당 목차의 활동", description = "")
    @Parameter(name = "userId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "engbook1400-t" ))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "29" ))
    @Parameter(name = "stntId", description = "학생ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s1" ))
    @Parameter(name = "tabId", description = "(선택된) 탭 ID", required = false, schema = @Schema(type = "integer", example = "1405484" ))
    @Parameter(name = "dtaIemId", description = "(선택된) 모듈 ID", required = false, schema = @Schema(type = "integer", example = "183356" ))
    public ResponseDTO<CustomBody> stntReportLectureResultAct(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchLectureReportService.findStntReportLectureResultAct(paramData);
            String resultMessage = "활동 결과 - 해당 목차의 활동";
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @Loggable
    @RequestMapping(value = "/tch/report/lecture/general-review/saveAll", method = {RequestMethod.POST})
    @Operation(summary = "수업 리포트 총평 저장 (선택된 학생)", description = "수업 리포트 총평 저장 (선택된 학생)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
            @ExampleObject(name = "파라미터", value = """ 
                            {
                                "textbkTabId": 1941,
                                "genrvw": "N",
                                "stdtPrntRlsAt": "N",
                            	"stntList": [
                                     {"userId": "student1"},
                                     {"userId": "student2"}
                                 ]
                            }
                    """
                    )
            }
        ))
    public ResponseDTO<CustomBody> createTchReportLectureGeneralReviewSaveAll(
            @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchLectureReportService.createTchReportLectureGeneralReviewSaveAll(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "수업 리포트 총평 저장 (선택된 학생)");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    /* 현재 사용하지 않는걸로 알고 있어서 소스코드 최하단으로 이동 */
    @RequestMapping(value = "/tch/site-set/dash-report/expos/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 사이트 설정 > 학부모 설정 > 대시보드/리포트 노출 설정 (수학/영어) 조회", description = "")
    @Parameter(name = "wrterId", description = "작성자 ID(교사 ID)", required = true, schema = @Schema(type = "string", example = "vsstu1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad21cba8f11ee88c00242ac110002" ))
    @Parameter(name = "yr", description = "년도", schema = @Schema(type = "string", example = "2024" ))
    @Parameter(name = "smt", description = "학기", schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "exposCd", description = "노출구분", required = true, schema = @Schema(type = "integer", example = "1" )) // 1 : 대시보드 2 : 리포트 3 : 학부모
    public ResponseDTO<CustomBody> tchSitesetDashreportExposList(
            @RequestParam(name = "wrterId",   defaultValue = "") String wrterId,
            @RequestParam(name = "claId",   defaultValue = "") String claId,
            @RequestParam(name = "yr",   defaultValue = "") String yr,
            @RequestParam(name = "smt",   defaultValue = "") Integer smt,
            @RequestParam(name = "textbkId",   defaultValue = "") Integer textbkId,
            @RequestParam(name = "exposCd",   defaultValue = "") Integer exposCd,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {

        try {
            Object resultData = tchLectureReportService.findSitesetDashreportExposList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 사이트 설정 > 학부모 설정 > 대시보드/리포트 노출 설정 (수학/영어)");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/site-set/dash-report/expos/save", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 사이트 설정 > 학부모 설정 > 대시보드/리포트 노출 설정 (수학/영어) (수정)등록", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
//                            "{" +
//                            "\"wrterId\":\"vsstu1\"," +
//                            "\"claId\":\"308ad21cba8f11ee88c00242ac110002\"," +
//                            "\"yr\":\"2024\"," +
//                            "\"smt\":1," +
//                            "\"textbkId\":1," +
//                            "\"exposCd\":1," +
//                            "\"exposList\":\"Y\"" +
//                            "}"


                            """
                            {"wrterId": "vsstu1"
                            ,"claId": "308ad21cba8f11ee88c00242ac110002"
                            ,"yr": "2024"
                            ,"smt": 1
                            ,"textbkId": 1
                            ,"exposCd": 1
                            ,"exposList": [
                                            {"exposGrpCd":   "expos_trget_cd_1_1"
                                                            ,"exposGrpNm": "대시보드"
                                                            ,"exposTrgetList": [{"exposTrgetCd":1,"exposTrgetNm": "단원별 이해도 그래프","exposAt": "Y"}
                                                                            ,{"exposTrgetCd":2,"exposTrgetNm": "개념별 이해도 그래프","exposAt": "Y"} ]
                                                                            },
                                          {"exposGrpCd":   "expos_trget_cd_2_1"
                                                          ,"exposGrpNm": "리포트"
                                                          ,"exposTrgetList": [{"exposTrgetCd":1,"exposTrgetNm": "수업리포트","exposAt": "Y"}
                                                                          ,{"exposTrgetCd":2,"exposTrgetNm": "평가리포트","exposAt": "Y"} ]
                                                                          }
                                          ]                                             
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveSitesetDashreportExpos(
            @RequestBody Map<String, Object> paramData
    ) {
        try{
            Object resultData = tchLectureReportService.saveSitesetDashreportExpos(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 사이트 설정 > 학부모 설정 > 대시보드/리포트 노출 설정 (수학/영어) (수정)등록");
        }  catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }
}
