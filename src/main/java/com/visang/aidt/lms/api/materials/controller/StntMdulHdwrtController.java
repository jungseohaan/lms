package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.StntMdulHdwrtService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(학생) 손글씨 (과제/평가)", description = "(학생) 손글씨 (과제/평가)")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntMdulHdwrtController {

    private final StntMdulHdwrtService stntMdulHdwrtService;

    @Loggable
    @RequestMapping(value = "/stnt/eval/mdul/hdwrt/save", method = {RequestMethod.POST})
    @Operation(summary = "(학생) 평가 손글씨 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":366," +
                            "\"userId\":\"vsstu476\"," +
                            "\"evlResultId\":963," +
                            "\"moduleId\":\"3010\"," +
                            "\"subId\":0," +
                            "\"hdwrtCn\":\"hdwrtCn1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntEvalMdulHdwrtSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulHdwrtService.createStntEvalMdulHdwrtSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 평가 손글씨 저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/mdul/hdwrt/view", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 평가 손글씨 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "366"))
    @Parameter(name = "userId", description = "평가대상 학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu476"))
    @Parameter(name = "evlResultId", description = "평가결과ID", required = true, schema = @Schema(type = "integer", example = "963"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "string", example = "3010"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> stntEvalMdulHdwrtView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulHdwrtService.findStntEvalMdulHdwrtView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 평가 손글씨 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/mdul/hdwrt/share-list", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 평가 손글씨(교사 필기) 학생 공유 조회", description = "")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> stntEvalMdulHdwrtShareList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulHdwrtService.findStntEvalMdulHdwrtShareList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 평가 손글씨(교사 필기) 학생 공유 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/mdul/hdwrt/save", method = {RequestMethod.POST})
    @Operation(summary = "(학생) 과제 손글씨 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":8," +
                            "\"userId\":\"student51\"," +
                            "\"taskResultId\":22," +
                            "\"moduleId\":\"2580\"," +
                            "\"subId\":0," +
                            "\"hdwrtCn\":\"hdwrtCn1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntHomewkMdulHdwrtSave(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        // null 체크 추가
        if (paramData == null || paramData.isEmpty()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("resultOk", false);
            errorResult.put("resultMsg", "요청 데이터가 없습니다.");

            return AidtCommonUtil.makeResultSuccess(null, errorResult, "(학생) 과제 손글씨 저장 Missing request body");
        }

        Object resultData = stntMdulHdwrtService.createStntHomewkMdulHdwrtSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 과제 손글씨 저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/mdul/hdwrt/view", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 과제 손글씨 조회", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "8"))
    @Parameter(name = "userId", description = "과제대상 학생 ID", required = true, schema = @Schema(type = "string", example = "student51"))
    @Parameter(name = "taskResultId", description = "과제결과ID", required = true, schema = @Schema(type = "integer", example = "22"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "string", example = "2580"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> stntHomewkMdulHdwrtView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulHdwrtService.findStntHomewkMdulHdwrtView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 과제 손글씨 조회");

    }

    @Loggable
    @RequestMapping(value = "/stnt/homewk/mdul/hdwrt/share-list", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 과제 손글씨(교사 필기) 학생 공유 조회", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "moduleId", description = "모듈 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> stntHomewkMdulHdwrtShareList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntMdulHdwrtService.findStntHomewkMdulHdwrtShareList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 과제 손글씨(교사 필기) 학생 공유 조회");

    }
}
