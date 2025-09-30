package com.visang.aidt.lms.api.user.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.user.service.TchRewardService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 리워드 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교사) 리워드 API", description = "(교사) 리워드 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchRewardController {
    private final TchRewardService tchRewardService;

    @Loggable
//    @RequestMapping(value = "/tch/reward/status", method = {RequestMethod.GET})
    @Operation(summary = "학생리워드 현황 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661" ))
    @Parameter(name = "menuSeCd", description = "메뉴 구분", schema = @Schema(type = "string", example = "1" ))    // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
    @Parameter(name = "sveSeCd", description = "서비스 구분", schema = @Schema(type = "string", example = "1" ))    // 1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
    public ResponseDTO<CustomBody> tchRewardStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Map<String, Object> resultData = tchRewardService.findStntRewardStatus(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생리워드 현황 조회");
    }

    @RequestMapping(value = "/tch/reward/adjust", method = {RequestMethod.POST})
    @Operation(summary = "학생 리워드 조정 (지급/차감)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                        {
                            "userId": "mathbook1644-s1",
                            "claId": "3af91dd7bde84083bc4c415fc7052daa",
                            "rwdAdjType": 1
                        }
                        """
                    )
            })
    )
    public ResponseDTO<CustomBody> tchRewardAdjust(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        // rwdAdjType)  1: 지급  2: 차감
        Map<String, Object> resultData = tchRewardService.adjustReward(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리워드 조정");
    }

    @Loggable
    @RequestMapping(value = {"/tch/stnt-srch/reward/list", "/tch/dsbd/status/reward/list"}, method = {RequestMethod.GET})
    @Operation(summary = "학생리워드 현황 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661" ))
    @Parameter(name = "menuSeCd", description = "메뉴 구분", schema = @Schema(type = "string", example = "1" ))    // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
    @Parameter(name = "sveSeCd", description = "서비스 구분", schema = @Schema(type = "string", example = "1" ))    // 1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
    @Parameter(name = "smt", description = "학기", schema = @Schema(type = "integer", example = "1" ))    // 1:1학기, 2:2학기
    public ResponseDTO<CustomBody> tchStntsrchRewardList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
            Object resultData = tchRewardService.findStntsrchRewardList(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생리워드 현황 목록 조회");
    }

}
