package com.visang.aidt.lms.api.notification.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
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
@Tag(name = "(학생) 알림 처리 API", description = "(학생) 알림 처리 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntNtcnController {
    private final StntNtcnService stntNtcnService;

    @Loggable
    @RequestMapping(value = "/stnt/ntcn/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생)알림보기", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5a0a89a258bd48968a4eedcc229e2b04" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "373"))
    @Parameter(name = "trgetCd", description = "대상코드 : P: 학부모, S: 학생, T: 교사", required = true, schema = @Schema(type = "string", allowableValues = {"P","S","T"}, defaultValue = "S"))
    //@Parameter(name = "ntcnTyCd", description = "알림유형코드 : 1: AI집중관리, 2: 질문알림, 3: 학습관리알림, 4: 수업 중 알림, 5: 학습관리알림", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5"}, defaultValue = "1"))
    //@Parameter(name = "trgetTyCd", description = "대상유형코드 : 1: 로그아웃, 2: 연결끊김, 3: 수업화면 이탈, 4: 진행 없음, 5: 수업 리포트, 6: 과제 리포트, 7: 평가 리포트, 8: 종합 리포트, 9: 과제, 10: 평가, 11: 댓글, 12: 피드백, 13: 오답노트 문항 추가, 14: 목표수정", required = true, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14"}, defaultValue = "1"))
    @Parameter(name = "page", description = "요청페이지", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "20"))
    public ResponseDTO<CustomBody> stntNtcnList(
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = stntNtcnService.findStntNtcnList(paramData, pageable);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)알림보기");
    }

    @Loggable
    @RequestMapping(value = "/stnt/ntcn/save", method = {RequestMethod.POST})
    @Operation(summary = "(학생) 알림_전송", description = "")
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
                            "\"rcveId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"textbkId\":\"1\"," +
                            "\"trgetCd\":\"P\"," +
                            "\"ntcnTyCd\":\"1\"," +
                            "\"trgetTyCd\":\"1\"," +
                            "\"ntcnCn\":\"\"," +
                            "\"linkUrl\":\"\"," +
                            "\"stntNm\":\"\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"" +
                            //"\"type\":\"T\"," +
                            //"\"setsId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntNtcnSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
            Object resultData = stntNtcnService.createStntNtcnSave(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 알림_전송");
    }

    @Loggable
    @RequestMapping(value = "/stnt/ntcn/readall", method = {RequestMethod.POST})
    @Operation(summary = "(학생) 알림 전체 읽음 처리", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = {
                        @ExampleObject(name = "파라미터", value = "{" +
                                "\"textbkId\":1," +
                                "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                                "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"" +
                                "}"
                        )
                }
    ))
    public ResponseDTO<CustomBody> stntNtcnReadall(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
            Object resultData = stntNtcnService.modifyStntNtcnReadall(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 알림 전체 읽음 처리");
    }

    // 학생 ID(userId)만으로 모든 알림 조회
    @Loggable
    @RequestMapping(value = "/stnt/ntcn/list/optional", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 모든 알림 보기", description = "")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "trgetCd", description = "대상코드 : P: 학부모, S: 학생, T: 교사", required = true, schema = @Schema(type = "string", allowableValues = {"P","S","T"}, defaultValue = "S"))

    // 필수값 X
    @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "5a0a89a258bd48968a4eedcc229e2b04" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", schema = @Schema(type = "integer", example = "373"))
    @Parameter(name = "page", description = "요청페이지", required = false, schema = @Schema(type = "integer", example = "0", defaultValue = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "20", defaultValue = "20"))
    public ResponseDTO<CustomBody> stntNtcnListOptional(
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = stntNtcnService.findStntNtcnListNoticeListOptional(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 모든 알림 보기");
    }
}

