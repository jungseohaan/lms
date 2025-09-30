package com.visang.aidt.lms.api.act.controller;

import com.visang.aidt.lms.api.act.service.StntActService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@RestController
@Tag(name = "(학생) 활동 도구 API", description = "(학생) 활동 도구 API")
@AllArgsConstructor
@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntActController {
    private final StntActService stntActService;


    @Loggable
    @RequestMapping(value = "/stnt/act/mdul/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 활동결과 목록 조회하기", description = "학생 > 모듈별 기능 > 활동 도구 > 제출 후; 학생의 해당 모듈에 대한 활동도구에 대한 활동결과 목록을 조회한다.")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "aidt2"))
    @Parameter(name = "textbkTabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "actIemId", description = "활동모듈 ID", required = true, schema = @Schema(type = "string",  example = "10"))
    @Parameter(name = "subId", description = "서브 ID", required = true, schema = @Schema(type = "integer",  example = "0"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string",  example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string",  example = "claId"))
    public ResponseDTO<CustomBody> stntActMdulList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "textbkTabId", defaultValue = "") String textbkTabId,
            @RequestParam(name = "actIemId", defaultValue = "") String actIemId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntActService.findStntActMdulList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 활동결과 목록 조회하기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/act/mdul/submit", method = {RequestMethod.POST})
    @Operation(summary = "(학생) 활동결과 제출하기", description = "녹화/사진/녹음/그리기/키보드 선택한 활동방식에 대한 결과를 제출한다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "actId" : 6,
                    "userId" : "430e8400-e29b-41d4-a746-446655440000",
                    "thumbnail" : "upload/1/20231220/Jd2i7sypr.png",
                    "actSubmitUrl" : "upload/1/article/9/1.html",
                    "actSubmitDc" : "활동제출 내용"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> stntActMdulSubmit(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("actId","userId","actSubmitUrl");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Map resultData = stntActService.modifyStntActMdulSubmit(paramData);
        if(Boolean.FALSE.equals(resultData.get("resultOk"))) {
            return AidtCommonUtil.makeResultFail(paramData, null, resultData.get("resultErr").toString());
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData.get("actResultInfo"), "(학생) 활동결과 제출하기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/act/mdul/detail", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 활동결과상세 조회하기", description = "학생 > 모듈별 기능 > 활동 도구 > 제출 후; 학생의 해당 모듈에 대한 활동도구에 대한 활동결과 목록을 조회한다.")
    @Parameter(name = "actId", description = "활동 ID", required = true, schema = @Schema(type = "string",  example = "6"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    public ResponseDTO<CustomBody> stntActMdulDetail(
            @RequestParam(name = "actId", defaultValue = "") String actId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntActService.findStntActMdulDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 활동결과상세 조회하기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/act/mdul/fdb/save", method = {RequestMethod.POST})
    @Operation(summary = "(학생) 짝꿍 답안에 피드백하기", description = "학생 > 모듈별 기능 > 활동 도구 > 제출 후; 짝꿍 답안에 피드백하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                     "evaluatorId" : "engbook1400-s2",
                     "actId" : 1472,
                     "stntId" : "engbook1400-s1",
                     "groupId" : 1,
                     "fdbDc" : "(수정함) s2가 s1 에게 1472 활동아이디 ",
                     "fdbUrl" : "430e8400-e29b-41d4-a746-446655440000"
                }
            """)
            })
    )
  public ResponseDTO<CustomBody> stntActMateFdbSave( @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = stntActService.createStntActMateFdb(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 짝꿍 답안에 피드백하기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/act/mdul/status/read", method = {RequestMethod.POST})
    @Operation(summary = "(학생) 짝꿍 답안 읽음 처리", description = "학생 > 모듈별 기능 > 활동 도구 > 제출 후; 짝꿍 답안에 피드백하기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                     "evaluatorId" : "engbook1400-s2",
                     "actId" : 1472,
                     "stntId" : "engbook1400-s1",
                     "groupId" : 1,
                     "readYn" : "N",
                     
                }
            """)
            })
    )
  public ResponseDTO<CustomBody> stntActMateChkReadSave( @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = stntActService.createStntActMateChkReadSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 짝꿍 답안 읽음 처리");

    }

}
