package com.visang.aidt.lms.api.user.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.user.service.StntRewardService;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(학생) 리워드 API", description = "(학생) 리워드 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntRewardController {

    private StntRewardService stntRewardService;

    @Loggable
    //    @GetMapping(value = "/stnt/reward")
    @Operation(summary = "리워드 지급", description = "")
    public ResponseDTO<CustomBody> stntReward() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntRewardService.createReward(paramData);
        String resultMessage = "리워드 지급";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    //    @RequestMapping(value = "/stnt/create/reward", method = {RequestMethod.POST})
    @Operation(summary = "리워드 생성(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"seCd\":1," +         // 1:획득
                            "\"menuSeCd\":1," +     // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                            "\"sveSeCd\":1," +      // 1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
                            "\"trgtId\":3," +       // 대상 ID (구매: 아이템ID)
                            "\"rwdSeCd\":\"1\"," +  // 1:하트, 2:스타
                            "\"rwdAmt\":1000," +    // 획득 리워드
                            "\"rwdUseAmt\":0" +     // 사용 리워드
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntCreateReward(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntRewardService.createReward(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리워드 생성(저장)");

    }

    @Loggable
    //    @RequestMapping(value = "/stnt/use/reward", method = {RequestMethod.POST})
    @Operation(summary = "리워드 사용(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"seCd\":2," +         //  2:사용
                            "\"menuSeCd\":1," +     // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                            "\"sveSeCd\":1," +      // 1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
                            "\"trgtId\":3," +       // 대상 ID (구매: 아이템ID)
                            "\"rwdSeCd\":\"1\"," +  // 1:하트, 2:스타
                            "\"rwdAmt\":1000," +    // 획득 리워드
                            "\"rwdUseAmt\":0" +     // 사용 리워드
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stntUseReward(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntRewardService.useReward(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리워드 사용(저장)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/reward/status", method = {RequestMethod.GET})
    @Operation(summary = "리워드 현황 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "menuSeCd", description = "메뉴 구분", schema = @Schema(type = "string", example = "1"))
    // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
    @Parameter(name = "sveSeCd", description = "서비스 구분", schema = @Schema(type = "string", example = "1"))
    // 1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
    public ResponseDTO<CustomBody> stntRewardStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntRewardService.findRewardStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리워드 현황 조회");

    }

    @Loggable
    @RequestMapping(value = {"/stnt/reward/list", "/stnt/dsbd/status/reward/list"}, method = {RequestMethod.GET})
    @Operation(summary = "리워드 내역 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook229-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "49f37b12fe7f463785e38da824f212db"))
    @Parameter(name = "menuSeCd", description = "메뉴 구분", schema = @Schema(type = "string", example = "3")) // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
    @Parameter(name = "sveSeCd", description = "서비스 구분", schema = @Schema(type = "string", example = "4")) // 1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
    @Parameter(name = "sortGbCd", description = "정렬 구분: 1(리워드)", schema = @Schema(type = "string", allowableValues = {"1"}, example = "1")) // 1:리워드
    @Parameter(name = "sortOrder", description = "정렬 순서: ASC, DESC", schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}, example = "DESC")) // ASC: 오름차순, DESC: 내림차순
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> stntRewardList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "menuSeCd", defaultValue = "") String menuSeCd,
            @RequestParam(name = "sveSeCd", defaultValue = "") String sveSeCd,
            @RequestParam(name = "sortGbCd", defaultValue = "") String sortGbCd,
            @RequestParam(name = "sortOrder", defaultValue = "") String sortOrder,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception {

        Object resultData = stntRewardService.findRewardList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리워드 내역 조회");

    }


}
