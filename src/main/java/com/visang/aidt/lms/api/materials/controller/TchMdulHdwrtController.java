package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.TchMdulHdwrtService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(교사) 손글씨 (과제/평가)", description = "(교사) 손글씨 (과제/평가)")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchMdulHdwrtController {
    private final TchMdulHdwrtService tchMdulHdwrtService;

    @Loggable
    @RequestMapping(value = "/tch/eval/mdul/hdwrt/save", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 평가 손글씨 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"moduleId\":\"1\"," +
                            "\"subId\":0," +
                            "\"hdwrtCn\":\"2\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalMdulHdwrtSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulHdwrtService.createTchEvalMdulHdwrtSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 평가 손글씨 저장");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/mdul/hdwrt/view", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 평가 손글씨 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchEvalMdulHdwrtView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulHdwrtService.findTchEvalMdulHdwrtView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 평가 손글씨 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/eval/mdul/hdwrt/share", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 평가 손글씨(교사 필기) 학생 공유", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"hdwrtId\":1," +
                            "\"hdwrtImgUrl\":\"0\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalMdulHdwrtShare(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulHdwrtService.createTchEvalMdulHdwrtShare(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 평가 손글씨(교사 필기) 학생 공유");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/mdul/hdwrt/save", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 과제 손글씨 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":1," +
                            "\"moduleId\":1," +
                            "\"subId\":0," +
                            "\"hdwrtCn\":\"2\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchHomewkMdulHdwrtSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulHdwrtService.createTchHomewkMdulHdwrtSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 손글씨 저장");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/mdul/hdwrt/view", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 평가 손글씨 조회", description = "")
    @Parameter(name = "taskId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> tchHomewkMdulHdwrtView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulHdwrtService.findTchHomewkMdulHdwrtView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 손글씨 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/mdul/hdwrt/share", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 평가 손글씨(교사 필기) 학생 공유", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":1," +
                            "\"hdwrtId\":1," +
                            "\"hdwrtImgUrl\":\"0\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchHomewkMdulHdwrtShare(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulHdwrtService.createTchHomewkMdulHdwrtShare(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 과제 손글씨(교사 필기) 학생 공유");

    }
}
