package com.visang.aidt.lms.api.homework.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.homework.service.StntHomewkService;
import com.visang.aidt.lms.api.mq.service.AssignmentFinishedService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.controller
 * fileName : StntHomewkController
 * USER : hs84
 * date : 2024-01-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-25         hs84          최초 생성
 */
@Slf4j
@RestController
@Tag(name = "(학생) 과제 API", description = "(학생) 과제 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntHomewkController {
    private StntHomewkService stntHomewkService;
    private AssignmentFinishedService assignmentFinishedService;

    @Loggable
    @RequestMapping(value = "/stnt/homewk/list", method = {RequestMethod.GET})
    @Operation(summary = "과제 목록 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student41"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772669"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색 유형", required = false, schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "taskSttsCd", description = "과제 상태 : 전체/예정(1)/진행중(2)/완료(3)", required = false, schema = @Schema(type = "string", allowableValues = {"", "1", "2", "3"}, example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))

    public ResponseDTO<CustomBody> stntHomewkList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.findStntHomewkList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/info", method = {RequestMethod.GET})
    @Operation(summary = "과제 정보 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student41"))
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "string", example = "11"))
    public ResponseDTO<CustomBody> stntHomewkInfo(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.findStntHomewkInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk", method = {RequestMethod.POST})
    @Operation(summary = "과제 정보 수정(시작하기)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":11," +
                            "\"userId\":\"student41\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntHomewkStart(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.modifyStntHomewk(paramData);

        if (resultData instanceof String) {
            return AidtCommonUtil.makeResultFail(paramData, null, resultData.toString());
        } else {
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 정보 수정(시작하기)");
        }
    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/exam", method = {RequestMethod.GET})
    @Operation(summary = "(공통) 과제 응시", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "string", example = "11"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student41"))
    public ResponseDTO<CustomBody> stntHomewkExam(
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.findStntHomewkExam(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 과제 응시");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/save", method = {RequestMethod.POST})
    @Operation(summary = "(공통) 응시(article)정답처리", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":11," +
                            "\"userId\":\"student41\"," +
                            "\"taskResultId\":42," +
                            "\"taskIemId\":2580," +
                            "\"subId\":0," +
                            "\"errata\":1," +
                            "\"subMitAnw\":1," +
                            "\"subMitAnwUrl\":\"\"," +
                            "\"taskTime\":\"\"," +
                            "\"hntUseAt\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntHomewkSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.modifyStntHomewkSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 응시(article)정답처리");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/submit", method = {RequestMethod.POST})
    @Operation(summary = "과제 자료 제출하기", description = "")
    //@Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    //@Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":11," +
                            "\"userId\":\"student41\"," +
                            "\"submAt\":\"Y\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntHomewkSubmit(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = stntHomewkService.modifyStntHomewkSubmit(paramData);
        assignmentFinishedService.insertBulkTaskMqTrnLogStntSubmit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 자료 제출하기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/result", method = {RequestMethod.GET})
    @Operation(summary = "과제 응시 후, 채점 및 채점불필요", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "string", example = "11"))
    @Parameter(name = "userId", description = "과제대상 학생 ID", required = true, schema = @Schema(type = "string", example = "student41"))
    public ResponseDTO<CustomBody> stntHomewkResult(
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.findStntHomewkResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 응시 후, 채점 및 채점불필요");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/result-info", method = {RequestMethod.GET})
    @Operation(summary = "모듈 과제 결과 조회", description = "")
    @Parameter(name = "taskResultId", description = "과제결과 ID", required = true, schema = @Schema(type = "string", example = "22"))
    @Parameter(name = "taskIemId", description = "과제항목(모듈) ID", required = true, schema = @Schema(type = "string", example = "2580"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "string", example = "0"))
    public ResponseDTO<CustomBody> stntEvalResultinfo(
            @RequestParam(name = "taskResultId", defaultValue = "") String taskResultId,
            @RequestParam(name = "taskIemId", defaultValue = "") String taskIemId,
            @RequestParam(name = "subId", defaultValue = "0") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.findStntHomewkResultinfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "모듈 과제 결과 조회");

    }


    /**
     * 응시답안 초기화
     *
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/stnt/homewk/init", method = {RequestMethod.POST})
    @Operation(summary = "과제 답안 초기화", description = "과제 답안 초기화")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":7," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"taskResultId\":15," +
                            "\"taskIemId\":2581," +
                            "\"subId\":0" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntTaskInit(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = (Map<String, Object>) stntHomewkService.modifyStntTaskInit(paramData);
        String resultMessage = "응시 과제 초기화 API";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "응시 과제 초기화 성공");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/recheck", method = {RequestMethod.POST})
    @Operation(summary = "(공통) 응시(article) 재확인 횟수저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":7," +
                            "\"userId\":\"student46\"," +
                            "\"taskResultId\":\"\"," +
                            "\"taskIemId\":2580," +
                            "\"subId\":0" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntHomewkRecheck(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.modifyStntHomewkRecheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 응시(article) 재확인 횟수저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/aitutor/submit/chat", method = {RequestMethod.POST})
    @Operation(summary = "과제 ai tutor 대화 내용 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":11," +
                            "\"userId\":\"student41\"," +
                            "\"taskResultId\":42," +
                            "\"taskIemId\":2580," +
                            "\"chatType\":\"auto\"," +
                            "\"aiCall\":\"연관된 개념을 알려줘\"," +
                            "\"aiReturn\":\"튜터러스 랩스 api return 값\"" +
                            "\"articleId\":0" +
                            "\"subId\":0" +
                            "}"
                    )
            })
    )
    public ResponseDTO<CustomBody> stntTaskAitutorSubmitChat(@RequestBody Map<String, Object> paramData) throws Exception {
        Object resultData = stntHomewkService.modifyStntHomewkAiTutSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 ai tutor 대화 내용 저장 ");
    }

    @Loggable
    @GetMapping(value = "/stnt/homewk/time/usage")
    @Operation(summary = "과제 누적사용 시간 조회", description = "")
    @Parameter(name = "studentId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook1644-s1"))
    @Parameter(name = "targetId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "MSTG291460"))
    public ResponseDTO<CustomBody> stntHomewkTimeUsage(
            @RequestParam(name = "studentId", defaultValue = "") String studentId,
            @RequestParam(name = "targetId", defaultValue = "") String targetId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.findStntHomewkTimeUsage(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 누적사용 시간 조회");

    }

    @Loggable
    @PostMapping(value = "/stnt/homewk/time/usage")
    @Operation(summary = "과제 누적사용 시간 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(
                            name = "파라미터",
                            value = """
                                    {
                                        "studentId": "mathbook1644-s1",
                                        "targetId": "MSTG291460",
                                        "timeUsage": 3600,
                                        "startDate": "2025-05-21 13:27:31",
                                        "endDate": "2025-05-21 13:30:29",
                                        "errorMessage": "컴퓨터 재부팅"
                                    }
                                    """
                    )
            })

    )
    public ResponseDTO<CustomBody> stntHomewkTimeSave(
        /*
        @RequestParam(name = "evlId", defaultValue = "") String evlId,
        @RequestParam(name = "userId", defaultValue = "") String userId,
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
         */
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.saveStntHomewkTimeUsage(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 누적사용 시간 저장");

    }
}