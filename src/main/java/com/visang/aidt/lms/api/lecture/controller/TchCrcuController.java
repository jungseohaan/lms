package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * (교사) 커리큘럼 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 커리큘럼 API", description = "(교사) 커리큘럼 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchCrcuController {
    private final TchCrcuService tchCrcuService;

    /**
     * 커리큘럼 목록 조회 변경 후
     * @param userId
     * @param textbkId
     * @param textbkIdxId
     * @param claId
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/tch/crcu/list", method = {RequestMethod.GET})
    @Operation(summary = "커리큘럼 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathreal103-t"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "373"))
    @Parameter(name = "textbkIdxId", description = "목차 ID", required = true, schema = @Schema(type = "integer", example = "81"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "3e1d0bfae54f43468be13471e79b3452"))
    public ResponseDTO<CustomBody> tchCrcuList(
            @RequestParam(name = "userId",   defaultValue = "550e8400-e29b-41d4-a716-446655440000") String userId,
            @RequestParam(name = "textbkId",   defaultValue = "1") long textbkId,
            @RequestParam(name = "textbkIdxId",   defaultValue = "1") long textbkIdxId,
            @RequestParam(name = "claId",   defaultValue = "0cc175b9c0f1b6a831c399e269772661") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {


        Map<String, Object> resultData = tchCrcuService.getCurriculumList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "커리큘럼 목록 조회");

    }

    /**
     * 커리큘럼 목록 조회 변경 전
     * @param userId
     * @param textbkId
     * @param textbkIdxId
     * @param claId
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/tch/crcu/list2", method = {RequestMethod.GET})
    @Operation(summary = "커리큘럼 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "textbkIdxId", description = "목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    public ResponseDTO<CustomBody> tchCrcuList2(
            @RequestParam(name = "userId",   defaultValue = "550e8400-e29b-41d4-a716-446655440000") String userId,
            @RequestParam(name = "textbkId",   defaultValue = "1") long textbkId,
            @RequestParam(name = "textbkIdxId",   defaultValue = "1") long textbkIdxId,
            @RequestParam(name = "claId",   defaultValue = "0cc175b9c0f1b6a831c399e269772661") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {


        Map<String, Object> resultData = tchCrcuService.findCrcuList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "커리큘럼 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/info", method = {RequestMethod.GET})
    @Operation(summary = "차시 정보 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "textbkIdxId", description = "목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7"))
    public ResponseDTO<CustomBody> tchCrcuInfo(
            @RequestParam(name = "userId",   defaultValue = "mathreal103-t") String userId,
            @RequestParam(name = "textbkId",   defaultValue = "373") long textbkId,
            @RequestParam(name = "textbkIdxId",   defaultValue = "81") long textbkIdxId,
            @RequestParam(name = "claId",   defaultValue = "3e1d0bfae54f43468be13471e79b3452") String claId,
            @RequestParam(name = "crculId",   defaultValue = "7") long crculId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {


        Map<String, Object> resultData = tchCrcuService.findCrcuInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "차시 정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/last-position", method = {RequestMethod.GET})
    @Operation(summary = "마지막 수업한 차시정보 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "textbkIdxId", description = "목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchCrcuLastPosition(
            @RequestParam(name = "userId",   defaultValue = "550e8400-e29b-41d4-a716-446655440000") String userId,
            @RequestParam(name = "claId",   defaultValue = "0cc175b9c0f1b6a831c399e269772661") String claId,
            @RequestParam(name = "textbkId",   defaultValue = "1") long textbkId,
            @RequestParam(name = "textbkIdxId",   defaultValue = "1") long textbkIdxId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {


        Map<String, Object> resultData = tchCrcuService.findCrcuLastPosition(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막 수업한 차시정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/last-position", method = {RequestMethod.POST})
    @Operation(summary = "마지막 수업한 차시정보 기록(수정)", description = "")
    /*
        {
        \"userId\":\"550e8400-e29b-41d4-a716-446655440000\",
        \"claId\":\"0cc175b9c0f1b6a831c399e269772661\",
        \"textbkId\":1,
        \"textbkIdxId\":1,
        \"crculId\":5
        }
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"textbkId\":1," +
                            "\"textbkIdxId\":1," +
                            "\"tabId\":1," +
                            "\"crculId\":5" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchCrcuLastPosition(@RequestBody Map<String, Object> paramData)throws Exception  {

        Map<String, Object> resultData =  tchCrcuService.modifyCrcuLastPosition(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막 수업한 차시정보 기록(수정)");

    }

    /*
    //@ApiOperation(value = "차시별 질문 개수 및 질문 목록 정보 조회 (Empty)", notes = "")
    @RequestMapping(value = "/tch/crcu/quest/list", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuQuestList() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuService.findCrcuQuestList(paramData);
        String resultMessage = "차시별 질문 개수 및 질문 목록 정보 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
    */

    /*
    //@ApiOperation(value = "모드 정보 조회_설정 (Empty)", notes = "")
    @RequestMapping(value = "/tch/crcu/mode", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuMode() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuService.findCrcuMode(paramData);
        String resultMessage = "모드 정보 조회_설정";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "모드 정보 조회_설정 (Empty)", notes = "")
    @RequestMapping(value = "/tch/crcu/mode/modify", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuModeModify() {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuService.modityCrcuMode(paramData);
        String resultMessage = "모드 정보 조회_설정";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
    */

    @Loggable
    @RequestMapping(value = "/tch/crcu/classify/reg", method = {RequestMethod.POST})
    @Operation(summary = "대중소분류 등록", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"textbkId\":1," +
                            "\"wrterId\":\"vstea50\"," +
                            "\"claId\":\"308ad5afba8f11ee88c00242ac110002\"," +
                            "\"parentKey\":4," +
                            "\"crculNm\":\"테스트 대분류\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchCrcuClassifyReg(@RequestBody Map<String, Object> paramData)throws Exception  {

        Object resultData =  tchCrcuService.createTchCrcuClassifyReg(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "대중소분류 등록");

    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/classify/mod", method = {RequestMethod.POST})
    @Operation(summary = "대중소분류 수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"textbkId\":1," +
                            "\"wrterId\":\"vstea50\"," +
                            "\"claId\":\"308ad5afba8f11ee88c00242ac110002\"," +
                            "\"crculKey\":18," +
                            "\"parentKey\":4," +
                            "\"crculNm\":\"테스트 대분류 수정\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchCrcuClassifyMod(@RequestBody Map<String, Object> paramData)throws Exception  {

        Object resultData =  tchCrcuService.modifyTchCrcuClassifyMod(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "대중소분류 수정");

    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/classify/del", method = {RequestMethod.POST})
    @Operation(summary = "대중소분류 삭제", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"textbkId\":1," +
                            "\"wrterId\":\"vstea50\"," +
                            "\"claId\":\"308ad5afba8f11ee88c00242ac110002\"," +
                            "\"crculKey\":18," +
                            "\"parentKey\":4," +
                            "\"crculNm\":\"테스트 대분류 수정\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchCrcuClassifyDel(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchCrcuService.deleteTchCrcuClassifyDel(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "대중소분류 삭제");

    }

    /**
     * 교과과정 커리큘럼 ID에 대한 교사 커리큘럼 ID 조회
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Loggable
    @RequestMapping(value = "/tch/crcu/redirect/curri-info", method = {RequestMethod.GET})
    @Operation(summary = "교과과정 커리큘럼 ID에 대한 교사 커리큘럼 ID 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engreal51-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "1c4379432acc4a37ad0b608fd3a16a5c"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "335"))
    @Parameter(name = "curriUnitId", description = "교과과정 커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "28312"))
    public ResponseDTO<CustomBody> tchRedirectCrcuInfo(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {
        Object resultData = tchCrcuService.getTchRedirectCrcuInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과과정 커리큘럼 ID에 대한 교사 커리큘럼 ID 조회");
    }
}
