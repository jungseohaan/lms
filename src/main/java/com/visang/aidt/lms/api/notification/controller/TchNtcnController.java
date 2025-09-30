package com.visang.aidt.lms.api.notification.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.notification.service.TchNtcnService;
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
 * (교사) 알림 처리 Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 알림 처리 API", description = "(교사) 알림 처리 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchNtcnController {
    private final TchNtcnService tchNtcnService;

    @Loggable
    @RequestMapping(value = "/tch/ntcn/list", method = {RequestMethod.GET})
    @Operation(summary = "(교사)알림보기", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "engbook229-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "49f37b12fe7f463785e38da824f212db" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "trgetCd", description = "대상코드 : P: 학부모, S: 학생, T: 교사", required = true, schema = @Schema(type = "string", allowableValues = {"P","S","T"}, defaultValue = "T"))
    @Parameter(name = "ntcnTyCd", description = "알림유형코드 : 1: AI집중관리, 2: 질문알림, 3: 학습관리알림", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3"}, defaultValue = "3"))
    //@Parameter(name = "trgetTyCd", description = "대상유형코드 : 1: 로그아웃, 2: 연결끊김, 3: 수업화면 이탈, 4: 진행 없음, 5: 수업 리포트, 6: 과제 리포트, 7: 평가 리포트, 8: 종합 리포트, 9: 과제, 10: 평가, 11: 댓글, 12: 피드백, 13: 오답노트 문항 추가, 14: 목표수정", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14"}, defaultValue = "1"))
    @Parameter(name = "page", description = "요청페이지", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "20"))
    public ResponseDTO<CustomBody> tchNtcnList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbookId", defaultValue = "") String textbookId,
            @RequestParam(name = "trgetCd", defaultValue = "") String trgetCd,
            @RequestParam(name = "ntcnTyCd", defaultValue = "") String ntcnTyCd,
            //@RequestParam(name = "trgetTyCd", defaultValue = "") String trgetTyCd,
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchNtcnService.findTchNtcnList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사)알림보기");

    }

    @Loggable
    @RequestMapping(value = "/tch/ntcn/readall", method = {RequestMethod.POST})
    @Operation(summary = "알림_전체 읽음 처리", description = "")
    //@Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    //@Parameter(name = "trgetCd", description = "대상코드 : P: 학부모, S: 학생, T: 교사", required = true, schema = @Schema(type = "string", allowableValues = {"P","S","T"}, defaultValue = "P"))
    //@Parameter(name = "ntcnTyCd", description = "알림유형코드 : 1: AI집중관리, 2: 질문알림, 3: 학습관리알림, 4: 수업 중 알림, 5: 학습관리알림", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5"}, defaultValue = "1"))
    //@Parameter(name = "trgetTyCd", description = "대상유형코드 : 1: 로그아웃, 2: 연결끊김, 3: 수업화면 이탈, 4: 진행 없음, 5: 수업 리포트, 6: 과제 리포트, 7: 평가 리포트, 8: 종합 리포트, 9: 과제, 10: 평가, 11: 댓글, 12: 피드백, 13: 오답노트 문항 추가, 14: 목표수정", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14"}, defaultValue = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"textbkId\":1," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"trgetCd\":\"P\"," +
                            "\"ntcnTyCd\":\"1\"" +
                            //"\"trgetTyCd\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchNtcnReadall(
            //@RequestParam(name = "userId", defaultValue = "") String userId,
            //@RequestParam(name = "trgetCd", defaultValue = "") String trgetCd,
            //@RequestParam(name = "ntcnTyCd", defaultValue = "") String ntcnTyCd,
            //@RequestParam(name = "trgetTyCd", defaultValue = "") String trgetTyCd,
            //@Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchNtcnService.modifyTchNtcnReadall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "알림_전체 읽음 처리");

    }

    @Loggable
    @RequestMapping(value = "/tch/ntcn/save", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 알림_전송", description = "")
    /*
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "trgetCd", description = "대상코드 : P: 학부모, S: 학생, T: 교사", required = true, schema = @Schema(type = "string", allowableValues = {"P","S","T"}, defaultValue = "P"))
    @Parameter(name = "ntcnTyCd", description = "알림유형코드 : 1: AI집중관리, 2: 질문알림, 3: 학습관리알림, 4: 수업 중 알림, 5: 학습관리알림", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5"}, defaultValue = "1"))
    @Parameter(name = "trgetTyCd", description = "대상유형코드 : 1: 로그아웃, 2: 연결끊김, 3: 수업화면 이탈, 4: 진행 없음, 5: 수업 리포트, 6: 과제 리포트, 7: 평가 리포트, 8: 종합 리포트, 9: 과제, 10: 평가, 11: 댓글, 12: 피드백, 13: 오답노트 문항 추가, 14: 목표수정", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14"}, defaultValue = "1"))
    @Parameter(name = "ntcnCn", description = "알림내용", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "linkUrl", description = "바로가기 URL", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "stntNm", description = "학생명", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "type", description = "타입구분 : T: 과제, E: 평가", required = false, schema = @Schema(type = "string", allowableValues = {"T","E"}, defaultValue = "T"))
    @Parameter(name = "setsId", description = "setsId", required = false, schema = @Schema(type = "string", example = "1"))
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"textbkId\":\"1\"," +
                            "\"trgetCd\":\"P\"," +
                            "\"ntcnTyCd\":\"1\"," +
                            "\"trgetTyCd\":\"1\"," +
                            "\"ntcnCn\":\"\"," +
                            "\"linkUrl\":\"\"," +
                            "\"stntNm\":\"\"," +
                            /*"\"type\":\"T\"," +
                            "\"setsId\":\"1\"," +*/
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchNtcnSave(
            /*
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "trgetCd", defaultValue = "") String trgetCd,
            @RequestParam(name = "ntcnTyCd", defaultValue = "") String ntcnTyCd,
            @RequestParam(name = "trgetTyCd", defaultValue = "") String trgetTyCd,
            @RequestParam(name = "ntcnCn", defaultValue = "") String ntcnCn,
            @RequestParam(name = "linkUrl", defaultValue = "") String linkUrl,
            @RequestParam(name = "stntNm", defaultValue = "") String stntNm,
            @RequestParam(name = "type", defaultValue = "") String type,
            @RequestParam(name = "setsId", defaultValue = "") String setsId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
             */
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchNtcnService.createTchNtcnSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 알림_전송");

    }

    @Loggable
    @RequestMapping(value = "/tch/ntcn/save/option", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 알림전송(옵션)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"sendTy\":\"A\"," +
                            "\"rcveId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"textbkId\":\"1\"," +
                            "\"trgetCd\":\"S\"," +
                            "\"ntcnTyCd\":\"1\"," +
                            "\"trgetTyCd\":\"1\"," +
                            "\"ntcnCn\":\"알림 내용\"," +
                            "\"linkUrl\":\"\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"setsId\":\"\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchNtcnSaveOption(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchNtcnService.createTchNtcnSaveOption(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 알림전송(옵션)");

    }

    /*
    //@ApiOperation(value = "읽지 않은 신규 알림 개수 조회", notes = "")
    @RequestMapping(value = "/tch/ntcn/count", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchNtcnCount() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchNtcnService.findNtcnUnreadCount(paramData);
        String resultMessage = "읽지 않은 신규 알림 개수 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "알림 유형별 알림 받기 설정", notes = "")
    @RequestMapping(value = "/tch/ntcn/read", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchNtcnRead() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchNtcnService.modifyNtcnRead(paramData);
        String resultMessage = "알림 유형별 알림 받기 설정";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "알림 읽기 처리", notes = "")
    @RequestMapping(value = "/tch/ntcn/setting", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchNtcnSetting() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchNtcnService.modifyNtcnSetting(paramData);
        String resultMessage = "알림 읽기 처리";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "알림 유형별 전체 읽음 처리", notes = "")
    @RequestMapping(value = "/tch/ntcn/read-all", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchNtcnReadAll() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchNtcnService.modifyNtcnToRead(paramData);
        String resultMessage = "알림 유형별 전체 읽음 처리";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
    */

    @Loggable
    @RequestMapping(value = {"/tch/ntcn/read", "/stnt/ntcn/read"}, method = {RequestMethod.POST})
    @Operation(summary = "알림 읽기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"ntcnId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchNtcnRead(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Pageable pageable = null;
        Object resultData = tchNtcnService.modifyTchNtcnRead(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "알림 읽기");

    }

    @Loggable
    @RequestMapping(value = {"/tch/ntcn/nt-check","/stnt/ntcn/nt-check"}, method = {RequestMethod.GET})
    @Operation(summary = "(교사/학생) 미확인 알림 유무 체크", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchNtcnNtcheck(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchNtcnService.findTchNtcheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 미확인 알림 유무 체크");

    }
}
