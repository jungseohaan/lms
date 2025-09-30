package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.lecture.service.TchCrcuQuizService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataAccessException;

/**
 * (교사) 커리큘럼 퀴즈 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 즉석 퀴즈 API", description = "(교사) 즉석 퀴즈 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchCrcuQuizController {
    private final TchCrcuQuizService tchCrcuQuizService;

    @RequestMapping(value = "/tch/tool/quiz/form", method = {RequestMethod.POST})
    @Operation(summary = "즉석퀴즈생성", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = {
            @ExampleObject(name = "파라미터", value = "{"+
                   "\"userId\": \"550e8400-e29b-41d4-a716-446655440000\","+
                   "\"claId\": \"0cc175b9c0f1b6a831c399e269772661\","+
                   "\"schlNm\": \"schlNm1\","+
                   "\"textbkId\": 1,"+
                   "\"qizNum\": 1"+
                "}"
            )
        }
    ))
    public ResponseDTO<CustomBody> tchToolQuizForm(
        @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchCrcuQuizService.createTchToolQuizForm(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈생성");
        } catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/tool/quiz/view", method = {RequestMethod.GET})
    @Operation(summary = "즉석퀴즈보기", description = "")
    @Parameter(name = "qizId", description = "퀴즈 ID", required = true, schema = @Schema(type = "array", example = "[1,2]"))
    public ResponseDTO<CustomBody> tchToolQuizView(
        @RequestParam(name = "qizId", defaultValue = "") Integer[] qizId,
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchCrcuQuizService.findTchToolQuizView(paramData, qizId);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈보기");
        } catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/tool/quiz/start", method = {RequestMethod.POST})
    @Operation(summary = "즉석퀴즈시작", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = {
            @ExampleObject(name = "파라미터", value = "{" +
                        "\"qizList\":[" +
                        "{\"qizId\":\"1\",\"distrNm\":\"평가항목1\",\"textbkNm\":\"교과서명1\",\"qizPosScript\":\"퀴즈발문1\",\"resultDispAt\":\"Y\",\"anonyAt\":\"N\",\"qizSttsCd\":2" +
                        ",\"qizInfoList\":  [" +
                                            "{\"distrNum\":1,\"distrNm\":\"평가항목이름1\"},{\"distrNum\":2,\"distrNm\":\"평가항목이름2\"}" +
                                            "]" +
                        "}," +
                        "{\"qizId\":\"1\",\"distrNm\":\"평가항목1\",\"textbkNm\":\"교과서명1\",\"qizPosScript\":\"퀴즈발문1\",\"resultDispAt\":\"Y\",\"anonyAt\":\"N\",\"qizSttsCd\":2" +
                        ",\"qizInfoList\":  [" +
                                            "{\"distrNum\":1,\"distrNm\":\"평가항목이름1\"},{\"distrNum\":2,\"distrNm\":\"평가항목이름2\"}" +
                                            "]" +
                        "}]" +
                "}"
            )
        }
    ))
    public ResponseDTO<CustomBody> tchToolQuizStart(
        @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchCrcuQuizService.modifyTchToolQuizStart(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈시작");
        } catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/tool/quiz/end", method = {RequestMethod.POST})
    @Operation(summary = "즉석퀴즈종료", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = {
            @ExampleObject(name = "파라미터", value = "{" +
                    "\"qizId\":[1,2]" +
                "}"
            )
        }
    ))
    public ResponseDTO<CustomBody> tchToolQuizEnd(
        @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchCrcuQuizService.modifyTchToolQuizEnd(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈종료");
        } catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/tool/quiz/result", method = {RequestMethod.GET})
    @Operation(summary = "즉석퀴즈결과", description = "")
    @Parameter(name = "qizId", description = "퀴즈 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "클래스 ID", required = false, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    public ResponseDTO<CustomBody> tchToolQuizResult(
        @RequestParam(name = "qizId", defaultValue = "") String qizId,
        @RequestParam(name = "claId", defaultValue = "") String claId,
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchCrcuQuizService.findTchToolQuizResult(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈결과");
        } catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/tool/quiz/del", method = {RequestMethod.POST})
    @Operation(summary = "즉석퀴즈삭제", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = {
            @ExampleObject(name = "파라미터", value = "{" +
                    "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                    "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                    "\"textbkId\":1" +
                "}"
            )
        }
    ))
    public ResponseDTO<CustomBody> tchToolQuizDel(
        @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchCrcuQuizService.removeTchToolQuizDel(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈삭제");
        } catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/tch/tool/quiz/nav", method = {RequestMethod.POST})
    @Operation(summary = "즉석퀴즈정보(네비)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = {
            @ExampleObject(name = "파라미터", value = "{" +
                    "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                    "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                    "\"textbkId\":1" +
                "}"
            )
        }
    ))
    public ResponseDTO<CustomBody> tchToolQuizNav(
        @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = tchCrcuQuizService.findTchToolQuizNav(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈정보(네비)");
        } catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @RequestMapping(value = {"/tch/tool/quiz/init"}, method = {RequestMethod.POST})
    @Operation(summary = "즉석퀴즈(초기화)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = {
                        @ExampleObject(name = "파라미터", value = "{" +
                                "\"qizId\":[1,2,3]" +
                            "}"
                        )
                }
    ))
    public ResponseDTO<CustomBody> tchMdulBmkDelete(
            @RequestBody Map<String, Object> paramData
    ) {
        try{
            Object resultData = tchCrcuQuizService.removeTchToolQuizInit(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "즉석퀴즈(초기화)");
        }  catch (IllegalArgumentException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "잘못된 파라미터입니다: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "데이터베이스 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "시스템 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
