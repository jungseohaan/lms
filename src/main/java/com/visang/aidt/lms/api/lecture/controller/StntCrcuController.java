package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.lecture.service.StntCrcuService;
import com.visang.aidt.lms.api.lecture.service.TchCrcuService;
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
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(학생) 커리큘럼", description = "(학생) 커리큘럼")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntCrcuController {

    private final StntCrcuService stntCrcuService;
    private final TchCrcuService tchCrcuService;

    @RequestMapping(value = "/stnt/crcu/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 커리큘럼 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "textbkIdxId", description = "목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Loggable
    public ResponseDTO<CustomBody> stntCrcuList(
            @RequestParam(name = "userId", defaultValue = "550e8400-e29b-41d4-a716-446655440000") String userId,
            @RequestParam(name = "stntId", defaultValue = "430e8400-e29b-41d4-a746-446655440000") String stntId,
            @RequestParam(name = "textbkId", defaultValue = "1") long textbkId,
            @RequestParam(name = "textbkIdxId", defaultValue = "1") long textbkIdxId,
            @RequestParam(name = "claId", defaultValue = "0cc175b9c0f1b6a831c399e269772661") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

        // (학생)의 마지막 위치정보 조회
        Map<String, Object> srhMap = new HashMap<>();
        srhMap.putAll(paramData);
        srhMap.put("userId", stntId);

        Map<String, Object> lastPosition = (Map<String, Object>) stntCrcuService.findStntCrcuLastposition(srhMap);
        if (MapUtils.isNotEmpty(lastPosition)) {
            Long crculId = MapUtils.getLong(lastPosition, "crculId");
            paramData.put("lastPosition", crculId);
        }
        Map<String, Object> resultData = tchCrcuService.getCurriculumList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 커리큘럼 목록 조회");

    }

    //    @ApiOperation(value = "차시 정보 조회", notes = "")
    //@GetMapping(value = "/stnt/crcu/info")
    @Loggable
    public ResponseDTO<CustomBody> stntCrcuInfo() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntCrcuService.findCrcuInfo(paramData);
        String resultMessage = "차시 정보 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "즉석퀴즈 답안 제출", notes = "")
    //@PostMapping(value = "/stnt/crcu/quiz/answer")
    @Loggable
    public ResponseDTO<CustomBody> stntCrcuQuizAnswer() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntCrcuService.createCrcuQuizAnswer(paramData);
        String resultMessage = "즉석퀴즈 답안 제출";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "모드정보 조회(GET), 모드 설정(PUT)", notes = "")
    //@RequestMapping(value = "/stnt/mode", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> stntCrcuMode(HttpServletRequest request) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = stntCrcuService.findCrcuMode(paramData);
        resultMessage = "모드 정보 조회";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@RequestMapping(value = "/stnt/mode/create", method = {RequestMethod.POST})
    @Loggable
    public ResponseDTO<CustomBody> stntCrcuModeCreate(HttpServletRequest request) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = stntCrcuService.createCrcuMode(paramData);
        resultMessage = "모드 설정";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //마지막수업기록 조회
    @Loggable
    @GetMapping("/stnt/crcu/last-position")
    @Operation(summary = "마지막수업기록 조회", description = "학생의 마지막 수업을 조회한다.")
    @Parameter(name = "userId", description = "유저(학생) ID", required = true)
    @Parameter(name = "textbkId", description = "교과서ID", required = true)
    @Parameter(name = "claId", description = "학급ID", required = true)
    public ResponseDTO<CustomBody> findStntCrcuLastposition(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntCrcuService.findStntCrcuLastposition(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막수업기록 조회");

    }

    //마지막수업기록 저장
    @RequestMapping(value = "/stnt/crcu/last-position", method = {RequestMethod.POST})
    @Operation(summary = "마지막수업기록 저장", description = "학생의 마지막 수업을 기록한다")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "userId" : "vstea6",
                                    "textbkId" : 1,
                                    "claId" : "1dfd6267b8fb11ee88c00242ac110002",
                                    "crculId" : 8
                                }
                            """)
            })
    )
    @Loggable
    public ResponseDTO<CustomBody> saveStntCrcuLastposition(@RequestBody Map<String, Object> paramData
    ) throws Exception {
        List<String> requiredParams = Arrays.asList("userId", "textbkId", "claId", "crculId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);
        Map resultData = stntCrcuService.saveStntCrcuLastposition(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막수업기록 저장");

    }
}
