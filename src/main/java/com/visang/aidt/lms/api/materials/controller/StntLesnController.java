package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.StntLesnService;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import com.visang.aidt.lms.api.mq.service.NatsSendService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * (학생) 학습 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(학생) 학습 API", description = "(학생) 학습 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntLesnController {

    private final StntLesnService stntLesnService;
    private final NatsSendService natsSendService;


    @Loggable
    @RequestMapping(value = "/stnt/lesn/start", method = {RequestMethod.POST})
    @Operation(summary = "(학생)학습시작", description = "(학생)학습시작")
    public ResponseDTO<CustomBody> getStntLesnStart(
            @RequestBody RealMqReqDto paramData
    )throws Exception {

        Object resultData = natsSendService.sendClassStartInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)학습시작");
    }

    @Loggable
    @PostMapping( "/stnt/lesn/end")
    @Operation(summary = "(학생)학습종료", description = "(학생)학습종료")
    public ResponseDTO<CustomBody> getStntLesnEnd(
            @RequestBody RealMqReqDto paramData
    )throws Exception {
        Object resultData =  natsSendService.sendClassFinishInfo(paramData); // real 종료 정보 전송
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)학습종료");
    }

    @Loggable
    @RequestMapping(value = "/stnt/lesn/prog/rate", method = {RequestMethod.POST})
    @Operation(summary = "(학생)학습진도율정보전송", description = "(학생)학습진도율정보전송")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getStntLesnProgRate(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntLesnService.getStntLesnProgRate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)학습진도율정보전송");

    }

    @Loggable
    @RequestMapping(value = "/stnt/lesn/prog/std/check", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학생의 학습 과정 기준 수행여부 정보 전송", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu586"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad67aba8f11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> stntLesnProgStdCheck(
            @RequestParam(name = "userId", required = true, defaultValue = "") String userId,
            @RequestParam(name = "claId", required = true, defaultValue = "") String claId,
            @RequestParam(name = "textbkId", required = true, defaultValue = "") String textbkId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        //TODO: 세션 아이디 가져오기 - userId, claId

//            paramData.put("userId", userId);
//            paramData.put("claId", userId);
//            paramData.put("textbkId", userId);

        Object resultData = stntLesnService.checkLesnProgStd(paramData);
        String resultMessage = "(학생) 학생의 학습 과정 기준 수행여부 정보 전송";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);


    }

    @Loggable
    @RequestMapping(value = "/stnt/lesn/prog/std/rate", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학생의 학습 과정 기준 수행결과 정보 전송", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu586"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad67aba8f11ee88c00242ac110002"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> stntLesnProgStdRate(
            @RequestParam(name = "userId", required = true, defaultValue = "") String userId,
            @RequestParam(name = "claId", required = true, defaultValue = "") String claId,
            @RequestParam(name = "textbkId", required = true, defaultValue = "") String textbkId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntLesnService.findLesnProgStdRate(paramData);
        String resultMessage = "(학생) 학생의 학습 과정 기준 수행결과 정보 전송";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);


    }
}
