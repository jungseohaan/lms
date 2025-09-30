package com.visang.aidt.lms.api.report.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.report.service.WrongNoteReportService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(리포트) 스스로학습 > 오답노트", description = "(리포트) 스스로학습 > 오답노트")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class WrongNoteReportController {
///report/wrong-note/statis
///report/comment
///report/wrong-note/list

    private final WrongNoteReportService src;

    @Loggable
    @RequestMapping(value = "/tch/report/wrong-note/statis", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습리포트 > 오답노트탭", description = "")
    @Parameter(name = "textbkId", description = "교과서 아이디", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "claId", description = "클래스 아이디", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "dateType", description = "날짜 타입(해당 값 없으면, 전체에 해당)", required = false, schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "dayDate", description = "dateType d : 날짜 타입 : yyyymmdd "  ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "startDate", description = "dateType w : 날짜 타입  : yyyymmdd",  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "endDate", description = "dateType w : 날짜 타입 : yyyymmdd"   ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "monthDate", description = "dateType m : 날짜 타입 : yyyymm"   ,  schema = @Schema(type = "string", example = "d"))
    public ResponseDTO<CustomBody> getClaWrongNoteStaticInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = src.getClaWrongNoteStaticInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 오답노트 정보 조회");
    }



    @Loggable
    @RequestMapping(value = "/tch/report/wrong-note/list", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습리포트 > 오답노트탭", description = "")
    @Parameter(name = "textbkId", description = "교과서 아이디", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "claId", description = "클래스 아이디", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "dateType", description = "날짜 타입(해당 값 없으면, 전체에 해당)", required = false, schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "dayDate", description = "dateType d : 날짜 타입 : yyyymmdd "  ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "startDate", description = "dateType w : 날짜 타입  : yyyymmdd",  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "endDate", description = "dateType w : 날짜 타입 : yyyymmdd"   ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "monthDate", description = "dateType m : 날짜 타입 : yyyymm"   ,  schema = @Schema(type = "string", example = "d"))
    public ResponseDTO<CustomBody> getClaStntWrongNoteStaticInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = src.getClaStntWrongNoteStaticInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 오답노트 학생별 정보 조회");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/wrong-note/statis", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습리포트 > 오답노트탭", description = "")
    @Parameter(name = "textbkId", description = "교과서 아이디", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "claId", description = "클래스 아이디", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 아이디 ", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "dateType", description = "날짜 타입(해당 값 비어있으면 전체 해당)", required = false, schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "dayDate", description = "dateType d : 날짜 타입 : yyyymmdd "  ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "startDate", description = "dateType w : 날짜 타입  : yyyymmdd",  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "endDate", description = "dateType w : 날짜 타입 : yyyymmdd"   ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "monthDate", description = "dateType m : 날짜 타입 : yyyymm"   ,  schema = @Schema(type = "string", example = "d"))
    public ResponseDTO<CustomBody> getStntWrongNoteStaticInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = src.getStntWrongNoteStaticInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 오답노트 정보 조회");
    }



    @Loggable
    @RequestMapping(value = "/stnt/report/wrong-note/list_back", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습리포트 > 오답노트탭", description = "")
    @Parameter(name = "textbkId", description = "교과서 아이디", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "claId", description = "클래스 아이디", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 아이디 ", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "dateType", description = "날짜 타입 ", required = true, schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "dayDate", description = "dateType d : 날짜 타입 : yyyymmdd "  ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "startDate", description = "dateType w : 날짜 타입  : yyyymmdd",  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "endDate", description = "dateType w : 날짜 타입 : yyyymmdd"   ,  schema = @Schema(type = "string", example = "d"))
    @Parameter(name = "monthDate", description = "dateType m : 날짜 타입 : yyyymm"   ,  schema = @Schema(type = "string", example = "d"))
    public ResponseDTO<CustomBody> getStntWrongNoteListInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        //사용 안함
        ///stnt/wrong-note/list 기존 프로그램 활용

        Object resultData = src.getStntWrongNoteListInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 오답노트 학생별 정보 조회");
    }


    @Loggable
    @RequestMapping(value = "/report/comment", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습리포트 (공통) 교사 한마디 호출", description = "")
    @Parameter(name = "textbkId", description = "교과서 아이디", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "claId", description = "클래스 아이디", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 아이디", required = true, schema = @Schema(type = "string", example = "engbook1400-s1"))
    @Parameter(name = "rptCmmntCd", description = "1 스스로학습 , 2 오답노트", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getTchComment(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = src.getTchComment(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로학습리포트 (공통) 교사 한마디 호출");
    }

    @Loggable
    @RequestMapping(value = "/report/comment", method = {RequestMethod.POST})
    @Operation(summary = "스스로학습리포트 (공통) 교사 한마디 수정(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                              "processType": "ind",   
                              "claId": "a498e4c7d7634773b147b5de262ba762",  
                              "textbkId": "1150",   
                              "tchId": "engbook1400-t",
                              "stntId": "engbook1400-s1",
                              "rptCmmnt": "잘했어요!3333 일괄4444", 
                              "rptCmmntCd": "1"  
                            }
                            """
                    )
            })
    )
    public ResponseDTO<CustomBody> modTchComment(
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = src.modTchComment(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로학습리포트 (공통) 교사 한마디 수정(저장)");
    }

      @Loggable
    @RequestMapping(value = "/tch/report/wrong-note/newat", method = {RequestMethod.POST})
    @Operation(summary = "오답노트리포트 new 표시 수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                              "stntId": "engbook1400-s1",  
                              "textbkId": "1150",
                              "dateType": "",
                              "dayDate": "",
                              "startDate": "",
                              "endDate": "",
                              "monthDate": "" 
                            }
                            """
                    )
            })
    )
    public ResponseDTO<CustomBody> modReadY(
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = src.modReadY(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 읽음 처리");
    }





}
