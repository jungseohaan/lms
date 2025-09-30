package com.visang.aidt.lms.api.assessment.controller;

import com.visang.aidt.lms.api.assessment.service.StntEvalService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
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

@Slf4j
@RestController
@Tag(name = "(학생) 평가 API", description = "(학생) 평가 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntEvalController {

    private StntEvalService stntEvalService;

    @Loggable
    @RequestMapping(value = "/stnt/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 목록 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook732-s1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "44e701f580a34e17b2a030a02527dc4a" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1522"))
    @Parameter(name = "condition", description = "검색 유형", required = false, schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "evlSttsCd", description = "평가 상태 : 전체/예정(1)/진행중(2)/완료(3)", required = false, schema = @Schema(type = "string", allowableValues = {"", "1", "2", "3"}, example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> stntEvalList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.findEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/start", method = {RequestMethod.POST})
    @Operation(summary = "평가 정보 수정(시작하기)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    //@Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"" +
                            "}"
                    )
            }
            )
    )
    public ResponseDTO<CustomBody> stntEvalStart(
            /*
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
             */
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.modifyEvalStart(paramData);

        if (resultData instanceof String) {
            return AidtCommonUtil.makeResultFail(paramData, null, resultData.toString());
        } else {
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 수정(시작하기)");
        }


    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/submit", method = {RequestMethod.POST})
    @Operation(summary = "평가 자료 제출하기", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    //@Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"textbookId\":1," +
                            "\"submAt\":\"Y\"" +
                            "}"
                    )
            }
            )
    )
    public ResponseDTO<CustomBody> stntEvalSubmit(
        /*
        @RequestParam(name = "evlId", defaultValue = "") String evlId,
        @RequestParam(name = "userId", defaultValue = "") String userId,
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
         */
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.modifyStntEvalSubmit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 자료 제출하기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/info", method = {RequestMethod.GET})
    @Operation(summary = "평가 정보 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    public ResponseDTO<CustomBody> stntEvalInfo(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.findEvalInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/exam", method = {RequestMethod.GET})
    @Operation(summary = "(공통) 평가 응시 (미사용-삭제예정)", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    public ResponseDTO<CustomBody> stntEvalExam(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.findStntEvalExam(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 평가 응시");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/save", method = {RequestMethod.POST})
    @Operation(summary = "(공통) 응시(article)자동저장", description = "")
    /*
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "userId", description = "평가대상 학생 ID", required = false, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "evlResultId", description = "평가결과ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "evlIemId", description = "평가항목ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "errata", description = "정오표 : 1: 정답, 2: 오답, 3: 부분정답, 4: 채점불가", required = true, schema = @Schema(type = "integer", allowableValues = {"1","2","3","4"}, defaultValue = "1" ))
    @Parameter(name = "subMitAnw", description = "제출답안", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subMitAnwUrl", description = "제출답안 이미지 URL", required = false, schema = @Schema(type = "string", example = ""))
    */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"evlResultId\":1," +
                            "\"evlIemId\":\"1\"," +
                            "\"subId\":0," +
                            "\"errata\":1," +
                            "\"subMitAnw\":1," +
                            "\"subMitAnwUrl\":\"\"," +
                            "\"evlTime\":\"\"," +
                            "\"hntUseAt\":\"Y\"" +
                            "}"
                    )
            })
    )
    public ResponseDTO<CustomBody> stntEvalSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.modifyStntEvalSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 응시(article)자동저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/result", method = {RequestMethod.GET})
    @Operation(summary = "평가 결과보기", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "userId", description = "평가대상 학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    public ResponseDTO<CustomBody> stntEvalResult(
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.findStntEvalResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 결과보기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/result-info", method = {RequestMethod.GET})
    @Operation(summary = "모듈 평가 결과 조회", description = "")
    @Parameter(name = "evlResultId", description = "평가결과 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "evlIemId", description = "평가항목(모듈) ID", required = true, schema = @Schema(type = "string", example = "6"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "string", example = "0"))
    public ResponseDTO<CustomBody> stntEvalResultinfo(
            @RequestParam(name = "evlResultId", defaultValue = "") String evlResultId,
            @RequestParam(name = "evlIemId", defaultValue = "") String evlIemId,
            @RequestParam(name = "subId", defaultValue = "0") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.findStntEvalResultinfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "모듈 평가 결과 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/recheck", method = {RequestMethod.POST})
    @Operation(summary = "(공통) 응시(article) 재확인 횟수저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"evlResultId\":\"\"," +
                            "\"evlIemId\":\"6\"," +
                            "\"subId\":0" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntEvalRecheck(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.modifyStntEvalRecheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 응시(article) 재확인 횟수저장");

    }

    /**
     * 응시답안 초기화
     * @param
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/stnt/eval/init", method = {RequestMethod.POST})
    @Operation(summary = "응시답안 초기화", description = "응시답안 초기화")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"evlResultId\":1," +
                            "\"evlIemId\":\"1\"," +
                            "\"subId\":0" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntEvalInit(
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData  = (Map<String, Object>)  stntEvalService.modifyStntEvalInit(paramData);
        String resultMessage = "응시 답안(article) 초기화 API";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    /**
     * 평가 ai tutor 대화 내용 저장
     * @param
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/stnt/evl/aitutor/submit/chat", method = {RequestMethod.POST})
    @Operation(summary = "과제 ai tutor 대화 내용 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":11," +
                            "\"userId\":\"student41\"," +
                            "\"evlResultId\":42," +
                            "\"evlIemId\":\"2580\"," +
                            "\"chatOrder\":1," +
                            "\"chatType\":\"auto\"," +
                            "\"aiCall\":\"연관된 개념을 알려줘\"," +
                            "\"aiReturn\":\"튜터러스 랩스 api return 값\"" +
                            "\"articleId\":0" +
                            "\"subId\":0" +
                            "}"
                    )
            })
    )
    public ResponseDTO<CustomBody> stntEvalAitutorSubmitChat(
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData  = (Map<String, Object>)  stntEvalService.modifyStntHomewkAitutorSumitChat(paramData);
        String resultMessage = "평가 ai tutor 대화 내용 저장";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/stnt/trgt/done", method = {RequestMethod.POST})
    @Operation(summary = "평가/과제 이탈 재진입", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"trgtSeCd\":3," +   /* 과제:2 , 평가:3 */
                            "\"trgtId\":1142044," +
                            "\"mamoymId\":\"engbook1400-s1\"," +
                            "\"doneYn\":\"Y\"" +   /* 진입:Y , 이탈:N */
                        "}"
                    )
            }
        )
    )
    public ResponseDTO<CustomBody> stntTrgtDone(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = stntEvalService.modifyStntTrgtDone(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가/과제 이탈 재진입");

    }

    @Loggable
    @RequestMapping(value = "/class/move/std-data/change", method = {RequestMethod.POST})
    @Operation(summary = "반 이동 데이터 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"s1\"," +
                            "\"oldClaId\":\"oldClaId\"," +
                            "\"newClaId\":\"newClaId\"" +
                        "}"
                    )
            }
        )
    )
    public ResponseDTO<CustomBody> classMoveStdDataChange (
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = stntEvalService.modifyClassMoveStdDataChange(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "반 이동 데이터 변경");

    }

    @Loggable
    @RequestMapping(value = "/class/std-data", method = {RequestMethod.POST})
    @Operation(summary = "반 데이터 생성", description = "api 사용하지 않고 서비스만 사용. test용 컨트롤러")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"claId\":\"oldClaId\"" +
                        "}"
                    )
            }
        )
    )
    public ResponseDTO<CustomBody> classStdData (
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = stntEvalService.saveClassStdData(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "반 데이터 생성");

    }

    @Loggable
    @GetMapping(value = "/stnt/eval/time/usage")
    @Operation(summary = "평가 누적사용 시간 조회", description = "")
    @Parameter(name = "studentId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook1644-s1"))
    @Parameter(name = "targetId", description = "평가 ID", required = true, schema = @Schema(type = "string", example = "MSTG291460"))
    public ResponseDTO<CustomBody> stntEvalTimeUsage(
            @RequestParam(name = "studentId", defaultValue = "") String studentId,
            @RequestParam(name = "targetId", defaultValue = "") String targetId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntEvalService.findStntEvalTimeUsage(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 누적사용 시간 조회");

    }

    @Loggable
    @PostMapping(value = "/stnt/eval/time/usage")
    @Operation(summary = "평가 누적사용 시간 저장", description = "")
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
    public ResponseDTO<CustomBody> stntEvalTimeSave(
        /*
        @RequestParam(name = "evlId", defaultValue = "") String evlId,
        @RequestParam(name = "userId", defaultValue = "") String userId,
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
         */
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntEvalService.saveStntEvalTimeUsage(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 누적사용 시간 저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/sets/check", method = {RequestMethod.GET})
    @Operation(summary = "(공통) 변경된 시트지 조회", description = "")
    @Parameter(name = "trgtSeCd", description = "대상 구분 (교과서 : 1 , 과제 : 2 , 평가 : 3 , 스스로학습 : 4)", required = true, schema = @Schema(type = "string", example = "1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000" ))
    @Parameter(name = "setsId", description = "세트지 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    @Parameter(name = "trgtId", description = "ID (평가 ID , 과제 ID)", required = true, schema = @Schema(type = "string", example = "1" ))
    @Parameter(name = "userId", description = "userID (평가 ID , 과제 ID)", required = true, schema = @Schema(type = "string", example = "1" ))
    public ResponseDTO<CustomBody> stntSetCheck(
            @RequestParam(name = "trgtSeCd", defaultValue = "") String trgtSeCd,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "setsId", defaultValue = "") String setsId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.findStntSetCheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 평가 SETS 변경 조회");

    }

}