package com.visang.aidt.lms.api.shop.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.shop.service.ShopService;
import com.visang.aidt.lms.api.utility.exception.AidtException;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * 샵 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(샵) 유저 정보 API", description = "(샵) 유저 정보 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ShopController {
    private final ShopService shopService;

    @Loggable
    @RequestMapping(value = "/shop/userinfo", method = {RequestMethod.GET})
    @Operation(summary = "유저 정보 조회", description = "유저 정보 조회")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    public ResponseDTO<CustomBody> getUserInfoList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = shopService.findShopUserInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "샵 유저 정보 조회");


    }

    @Loggable
    @RequestMapping(value = "/shop/item-list", method = {RequestMethod.GET})
    @Operation(summary = "상점 정보 조회", description = "상점 정보 조회")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "prchsGdsSeCd", description = "상점 구분 : 프로필(P)/스킨(S)/게임(G)", required = true, schema = @Schema(type = "string", allowableValues = {"P","S","G"}, example = "P"))
    public ResponseDTO<CustomBody> getShopInfoList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = shopService.findShopItemList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "샵 상점 정보 조회");


    }

    @Loggable
    @RequestMapping(value = "/shop/item-detail", method = {RequestMethod.GET})
    @Operation(summary = "상점 상세 요청", description = "상점 상세 요청")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "prchsGdsSeCd", description = "상점 구분", required = true, schema = @Schema(type = "string", example = "S"))
    @Parameter(name = "itemId", description = "샵 이미지 정보 ID(아이템)", schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getShopItemDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = shopService.findShopItemDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "샵 상점 정보 조회");


    }

    @Loggable
    @RequestMapping(value = "/shop/buy-item", method = {RequestMethod.POST})
    @Operation(summary = "상점 구매 요청")
//    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
//    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "[1,2,3]"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"prchsGdsSeCd\":\"P\"," +
                            "\"itemId\":1," +
                            "\"rwdSeCd\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> getShopbuyItem(
//        @RequestParam(name = "userId", defaultValue = "") String userId,
//        @RequestParam(name = "bkmkId", defaultValue = "") String bkmkId,
//        @RequestParam(name = "tabId", defaultValue = "") String tabId,
//        @RequestParam(name = "tagNm", defaultValue = "") String tagNm,
//        @RequestParam(name = "bassTagAt", defaultValue = "") String bassTagAt,
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Map<String, Object> resultData = shopService.getShopbuyItem(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "상점 구매 요청");
    }


    @Loggable
    @RequestMapping(value = "/shop/use-item", method = {RequestMethod.POST})
    @Operation(summary = "아이템 사용 요청")
//    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
//    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "[1,2,3]"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"prchsGdsSeCd\":\"P\"," +
                            "\"itemId\":2" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> getShopUseItem(
//        @RequestParam(name = "userId", defaultValue = "") String userId,
//        @RequestParam(name = "bkmkId", defaultValue = "") String bkmkId,
//        @RequestParam(name = "tabId", defaultValue = "") String tabId,
//        @RequestParam(name = "tagNm", defaultValue = "") String tagNm,
//        @RequestParam(name = "bassTagAt", defaultValue = "") String bassTagAt,
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = shopService.getShopUseItem(paramData);
        String resultMessage = "아이템 사용 요청";
        if(resultData == null){
            resultMessage = "아이템 미존재";
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);

    }

    @Loggable
    @RequestMapping(value = "/shop/myroom", method = {RequestMethod.GET})
    @Operation(summary = "마이룸", description = "마이룸")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "vstea1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002"))
    public ResponseDTO<CustomBody> getShopMyroom(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = shopService.getShopMyroom(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마이룸");


    }

    @Loggable
    @RequestMapping(value = "/shop/change-item-inv", method = {RequestMethod.POST})
    @Operation(summary = "상품 위치 변경")
//    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
//    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "[1,2,3]"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"prchsGdsSeCd\":\"P\"," +
                            "\"prchsId\":1," +
                            "\"invSeCd\":2," +
                            "\"prchsId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> getShopChangeItemInv(
//        @RequestParam(name = "userId", defaultValue = "") String userId,
//        @RequestParam(name = "bkmkId", defaultValue = "") String bkmkId,
//        @RequestParam(name = "tabId", defaultValue = "") String tabId,
//        @RequestParam(name = "tagNm", defaultValue = "") String tagNm,
//        @RequestParam(name = "bassTagAt", defaultValue = "") String bassTagAt,
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = shopService.getShopChangeItemInv(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "상품 위치 변경");

    }

    @Loggable
    @RequestMapping(value = "/shop/reward", method = {RequestMethod.POST})
    @Operation(summary = "보상지급", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claID\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"tabId\":49860," +
                            "\"rewardList\":[{\"userId\":\"430e8400-e29b-41d4-a746-446655440000\",\"claId\":\"0cc175b9c0f1b6a831c399e269772661\",\"rwdAmt\":100}," +
                            "{\"userId\":\"vstea1\",\"claId\":\"1dfd618eb8fb11ee88c00242ac110002\",\"rwdAmt\":100}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveReward(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = shopService.saveReward(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "보상지급");

    }

    @Loggable
    @RequestMapping(value = "/shop/mdul-info", method = {RequestMethod.GET})
    @Operation(summary = "문제정보", description = "문제정보")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "tabId", description = "게임탭 ID", required = true, schema = @Schema(type = "string", example = "2"))
    public ResponseDTO<CustomBody> getShopMdulInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = shopService.getShopMdulInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "문제정보");


    }

    @Loggable
    @RequestMapping(value = "/shop/user-message", method = {RequestMethod.POST})
    @Operation(summary = "유저상태메시지 변경", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"rprsGdsAnct\":\"테스트에요1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveUserMessage(
            @RequestBody Map<String, Object> paramData,
            HttpServletRequest request
    )throws Exception {
        log.info("saveUserMessage paramData : {}, userId: {}", paramData, request.getAttribute("auth.userId"));
        if (Objects.equals(request.getAttribute("auth.userId"), paramData.get("userId"))) {
            Map<String, Object> resultData = shopService.saveUserMessage(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "유저상태메시지");
        } else {
            throw new AidtException("userId 값이 유효하지 않습니다.");
        }
    }

}
