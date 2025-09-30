package com.visang.aidt.lms.api.keris.controller;

import com.visang.aidt.lms.api.common.mngrAction.aop.KerisActionLog;
import com.visang.aidt.lms.api.keris.service.KerisApiService;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@Tag(name = "공공포털 -> 비상AIDT", description = "공공포털 -> 비상AIDT<br>현재 버전 : 2.1")
@RequiredArgsConstructor
public class KerisApiController {

    private final KerisApiService kerisApiService;

    @KerisActionLog
    @PostMapping(value = "/keris/index/{textbkCd}", produces="application/json; charset=UTF8")
    @Operation(summary = "공공포털 -> 비상교육 시작메시지 호출 POST", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                                "access_token": {
                                    "token":"eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.qjuaIIMzIkye8vFBVxOKBRhxf5T7O2OvEhuiHQ9SxE9wROjpcF0pVKad2JBSFtRxvPEarFZlOjngv0TPkH0FJsxanLEwoGI0ltnRf0EyjF6lQ9s4mWQH5lT_2VYcnLUv_qG7_pyiRYS0VvhnHhFjd6Jrd2_7oAg4NoP0_x3GiwdAE3OFfmzLLeeU8V3wZNLHGgktDzFgNXKqP4bOHbKfO2s_2nXMWuLb4M84mObhz5uwpyQf5gmzJFF1WTc1oi4OuhWwpmLvoi_JpTFFlMGwbUg8wXLBXwLl7lob3MyFq3t_6fkEZ2LI09qgDsMHCslCi-TNwgvDIIHXVbB534Pz0g.BcyqwiQ4n98Nkofq.3IboQe0k5ul8ZLPhEWZXmGSd8z3OG663VYtAbeJegN_QX_mpu4snfpo71nfUXZQQ1JKsGT1AZebj6kYhRhjTwivSmRoBfm9NgeGv2HzRwbgcI6QT9-sQt1RhhkD4W7-GPD1n_DaNO_UIkfHyt6pUuUvaoOEJZAqFXHEUVAemu-isiI3RhL1jPhh_vil2d_HHkUfyxavZ0p7wwL5VikCYgemdPY2n2ZXB_GsSL1NKB3Li-TojrQWmaVoLa5e9TGg6TiOUm4RC9apmuexsx2L2N1-8nGBajaKnxL7kFrr6UGTBf9jDoZ7PfJJUf516dyYJx4lMrYeu_fPSVpQlmOe2FTRoAzwwD2pUlvD0L1S9xfVUXUl9AcA_ckXjaW9xMoC0g6zxEbIwK16WVO5Zo80lq0Px6dGoGP-Eatdy7n0NJxRpLVvRlsbALfxvBQ3k594NSY4FFwET4zKuevS0V1XBobxf37msRfFJkw1eB_LIku1VRT1Nxp-S6qtpeBg6yslaKyxGxrO2GaOz0v3l-E8gSvqs2naJULBWiGER9eH85DgWk98dZa7Q_LUxngV34xtT6AbQfVr5NKLHGTkRdIhlVy7NjVMA_a5XhGVyQIsDsDxXJ8vkDEIibjEFeeqkP-9TiMCBDUK64nVgx2-qG1PvqbPGRRb_5iqqXrTjOfhRLYZlvmuKFbHBVY21f5b8CxPmpAEe2ydvVClfugu0SHboYwmZOur3OpZ-GbQZhqUxJqLZmqBwKscxeNXxvGIB_Lo_gYql6jjqMqpz0vBDCh_rkWAxdbirTkqPsYcuKzGpUnghk26ZjiyRGC6h.rs8xtKlTVjKJxhq14holjw",
                                    "access_id":"6cf0dc7d390a8fef20174ca976a698f551fd8f39"
                                },
                                "api_domain" : "https://adv.aidtbook.kr/test",
                                "user_type" : "S",
                                "user_status" : "E",
                                "user_id" : "94b96beb-31d4-5725-a153-1e78f0e52d1b",
                                "lecture_code" : "4T100000157_20241",
                                "class_code" : "2B101622411_2024_00000000_4011",
                                "class_period" : "5",
                                "partner_id" : "e9e641df-3d48-5161-a0f0-c1305bef8b66"
                            }
                            """
                    )
            }
            ))
    @Parameter(name = "textbkCd", description = "AIDT 교과서 코드 ", required = true, schema = @Schema(type = "string", example = "mathel31"))
    public @ResponseBody Map<String, Object> index(@RequestBody Map<String, Object> paramData, @PathVariable("textbkCd") String textbkCd) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String userStatus = MapUtils.getString(paramData, "user_status", "");
        String userId = MapUtils.getString(paramData, "user_id", "");
        String userType = MapUtils.getString(paramData, "user_type", "");
        paramData.put("textbkCd", textbkCd);

        try {
            // 전입, 전출만 POST에서 대응하고 그 외의 경우는 개인정보제공 동의에서 진행
            if (StringUtils.equals(userStatus, "I")) {
                // 전입온 경우
                // 타 발행사에서의 학생이 학습한 이력 DB에 저장
                result = kerisApiService.saveTransferProc(paramData);
            } else if (StringUtils.equals(userStatus, "O")) {
                // 전출인 경우
                // 비상교육에서 학습한 이력 전달
                result = kerisApiService.getUserStudyInfo(paramData);
            } else {
                // 개인정보제공 동의를 수행하지 않은 초기 상태의 경우에는 바로 성공 리턴
                result.put("code", "00000");
                result.put("message", "성공");
            }
        } catch (Exception e) {
            log.error("keris err /keris/index:{}, param:{}", e.getMessage(), paramData.toString());
            result.put("code", "50001");
            result.put("message", "시스템 오류");
        }
        log.info("/keris/index Return: {}" , result.toString());
        return result;
    }

    /**
     * 공공포털 -> 비상교육 시작메시지 호출 GET (비상 AIDT 교과서 메인페이지로 redirect 처리 )
     * @param paramData
     * @param textbkCd
     * @return
     * @throws Exception
     */
    @KerisActionLog
    @GetMapping(value = "/keris/index/{textbkCd}")
    @Operation(summary = "공공포털 -> 비상교육 시작메시지 호출 GET", description = "")
    public RedirectView indexGet(@PathVariable("textbkCd") String textbkCd, @RequestParam Map<String, Object> paramData) throws Exception {
        String apiDomain = MapUtils.getString(paramData, "api_domain", "");
        String userId = MapUtils.getString(paramData, "user_id", "");
        String userStatus = MapUtils.getString(paramData, "user_status", "");
        String userType = MapUtils.getString(paramData, "user_type", "");
        String token = MapUtils.getString(paramData, "access_token.token", "");
        String accessId = MapUtils.getString(paramData, "access_token.access_id", "");
        String class_code = MapUtils.getString(paramData, "class_code", "");
        String lecture_code = MapUtils.getString(paramData, "lecture_code", "");
        String api_version = MapUtils.getString(paramData, "api_version", "");
        String age14_blw_lgrp_ci_no = MapUtils.getString(paramData, "entrusted_info.age14_blw_lgrp_ci_no", "");
        String age14_blw_lgrp_name = MapUtils.getString(paramData, "entrusted_info.age14_blw_lgrp_name", "");
        String use_terms_agree_dt = MapUtils.getString(paramData, "entrusted_info.use_terms_agree_dt", "");
        String use_terms_agree_yn = MapUtils.getString(paramData, "entrusted_info.use_terms_agree_yn", "");
        String classPeriod = MapUtils.getString(paramData, "class_period", "");

        paramData.put("textbkCd", textbkCd);

        Map<String, Object> ptnInfo = kerisApiService.getPtnInfo(paramData);
        if (MapUtils.isNotEmpty(ptnInfo)) {
            if (!StringUtils.equals((String) ptnInfo.getOrDefault("apiDomain", ""), apiDomain)) {
                kerisApiService.updatePtnInfo(paramData);
            }
        }
        String partnerId = MapUtils.getString(ptnInfo, "ptnId", "");
        String redirectUrl = (String) ptnInfo.getOrDefault("stdtMainUrl", "");
        if (StringUtils.equals(userType, "T")) {
            redirectUrl = (String) ptnInfo.getOrDefault("tcMainUrl", "");
        } else if (StringUtils.equals(userType, "P")) {
            redirectUrl = (String) ptnInfo.getOrDefault("paMainUrl", "");
        }
        redirectUrl = redirectUrl + "?class_code=" + class_code + "&lecture_code=" + lecture_code + "&userId=" + userId + "&accessToken=" + token + "&accessId=" + accessId + "&apiDomain=" + apiDomain
                + "&userStatus=" + userStatus + "&userType=" + userType + "&Partner-ID=" + partnerId + "&indvInfoAgreYn=Y" + "&textbkCd=" + textbkCd + "&api_version=" + api_version + "&class_period=" + classPeriod
                + "&age14_blw_lgrp_ci_no=" + age14_blw_lgrp_ci_no
                + "&age14_blw_lgrp_name=" + age14_blw_lgrp_name
                + "&use_terms_agree_dt=" + use_terms_agree_dt
                + "&use_terms_agree_yn=" + use_terms_agree_yn
        ;
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/index/proc", produces="application/json; charset=UTF8")
    @Operation(summary = "공공포털 통합인증 API 수행", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                                "access_token": {
                                    "token":"eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.0gPtAVu109dAmPlBa3DM0p9JwcPIgo6Nn_sHpU0OFvgCJK1GJhYLjTyLYJcOfgalhfzC71_coqXAbtJk7ix91BK5Ab1N_nslpch6cfI1jG7a4P-7aAJNJ9EOG47o7LAVnR7bGmmhYYV0CY6I0ns9y8xXmQl2kGc3r0A1TTnuwysigHRZmYPqiyQ9OTVAQVMCvcfOAkzLYOV3kR08Ps9jPRRL_1TLz6OH0hi7AJ4NuuiKtUCETwGUH_d4eBJPdN5VtB1zCe1AjO3uELfMFV03yO4GGC0eoNP3a980bIoSjqsWVRv3a-54YEb28OVetNceno9mgSj0i0cTb4R9Szv0ng.O5ChU1D9t0Myc3Mg.DDfNPrRI5DMYqBz-8tO1PgPcF3xY6cw7Wop0MvqFJG-_6fKY8O_O4Y9QmTXDF2M5R3-iQoQWtiN0xuZWYhaEyHZtU2GRlgTcuyTswJWNXGj-dVtngpvsCXwhosc7F4hBeij0XOsrHxScoukhAuDwg0ddJyVU3to3r5XQ26JzymIzzOZC2cgmh1ZlHfAaAmQBV2eqhYSX82YVgjVx7gY6FUMqBmt_q8Jc9s0dcOVqc38nhMzvs-txu4nIH8MHQu3UzETvIWDXXs-HgzqebgpzOiHJAsY2SaUUEFLE1sEXV91Fu65sD4uCtuGdGNAZ9bhl2epl8iyKD8NWOseTIq7LJFX26lYSepE7tJ80sv8IatM4UPlEYFyts-XZyOlf4vLDjOnCUXqxXGvZUXONbXpQwktACU4ZUQz0tN9JTdFhrG-immHUC69cFeE1uGZKMjEqkjy0nv6nXyz0XiJEjxYcSNPO4o4yYkwxunuo8a0kVrAmUYpWJv3VdzbtsymRHUljEARq2zUOm3wtpWmjtGXd24mPj1_EvfGE54V0-khRvmf3FQF7fLx4Tsbm2dTPEA13d0TEiaCHmRtXeagV6jtAwmN65DMCnjFbRL_y9myt0EFMhNlWtxKnhVcd7MemrQRRqBdxaT3D8u9BNNWP5copbB5EOH8b5ZdYYXZpMytbcz2uoBMsGZr3B7UUm7v3L6TKVY8oLEAXFZhSUe2L8n5ZuXRp1N5sBXqZI7rpMOTt-Hcu_vVp_T0I6FROSjrEYvsu-m84NWNqyYGUsZO7dVwZO9zWwVMbjOE9YZXuLOUwOUg3orOXfadnw1krE75BQJF7hXyNbsaSLW6Y8wy0cWJnpYeP5gXBHFj7aSd0Cj2v7qZTt1kM22y35bYiO3gnqoHBbM_i_tQYdu3PxlqPAAeKzlRbyeowl9p2ytiRWyzQUyw.H-ZetjWVWwqvms7IpnAKhw",
                                    "access_id":"7217715788e60a1f843740aa21e17cf8bb67704b"
                                },
                                "textbkCd" : "mathel42",
                                "api_domain" : "https://adv.aidtbook.kr/test",
                                "user_type" : "S",
                                "user_status" : "E",
                                "user_id" : "9b2e2573-5aa2-535b-a145-ed220446316a",
                                "lecture_code" : "2B101622421_20241_00011011",
                                "class_code" : "2B101622421_2024_00000000_4011",
                                "class_period" : "5",
                                "partner_id" : "ab155f10-a911-5d76-acf8-8357aae4b948",
                                "api_version" : "2.3"
                            }
                            """
                    )
            }
            ))
    public @ResponseBody Map<String, Object> userProc(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String userId = MapUtils.getString(paramData, "user_id", "");
        String userType = MapUtils.getString(paramData, "user_type", "");
        String userStatus = MapUtils.getString(paramData, "user_status", "");
        try {
            if ((StringUtils.equals(userStatus, "E") || StringUtils.equals(userStatus, "I") || StringUtils.equals(userStatus, "O"))) {
                if (StringUtils.equals(userType, "S")) {
                    //학생 저장 프로세스 수행
                    result = kerisApiService.saveStProc(paramData);
                } else if (StringUtils.equals(userType, "T")) {
                    //교사 저장 프로세스 수행
                    result = kerisApiService.saveTcProc(paramData);
                }
            } else {
                result.put("code", "40001");
                result.put("message", "파라메터오류:정의되지 않은 파라메터");
            }
        } catch (Exception e) {
            log.error("keris err /keris/index/proc:{}, param:{}", e.getMessage(), paramData.toString());
            String errorMessage = e.getMessage();
            String[] parts = errorMessage.split("###");
            if (parts.length > 1) {
                String[] errorParts = parts[1].split("###");
                if (errorParts.length > 0 ) {
                    String firstLine = errorParts[0].trim();
                    if (!firstLine.isEmpty()) {
                        errorMessage = firstLine;
                    }
                }
            }
            result.put("code", "50001");
            result.put("message", errorMessage);
        }
        log.info("/keris/index/proc Return: {}" , result.toString());
        return result;
    }

    @GetMapping(value = "/keris/user/indvInfoAgreYn", produces="application/json; charset=UTF8")
    @Operation(summary = "사용자 개인정보수집동의 여부 조회", description = "")
    @Parameter(name = "user_id", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "9b2e2573-5aa2-535b-a145-ed220446316a"))
    public ResponseDTO<CustomBody> indvInfoAgreYn(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception{
        // 개인정보수집동의 여부 조회
        // 최초 로그인시에는 resultData = null
        Object resultData = kerisApiService.getUserInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "사용자 개인정보수집동의 여부 조회");
    }

    @PostMapping(value = "/keris/test/in")
    @Operation(summary = "테스트", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                              "access_token": {
                                "token": "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.XauYPeLLzWlg_3_hwOZh1hGKeTlfvH6VVJdd69ihbnbKnKWXj2lVsNvR170XVnJDBBRKKdmaolxEN6X77j2ULJfmw3ZztPTJaoN1AyeCFzCcRQR90qUV4mlY4DzILrRa3xFYrRlfisAdgJPVsUXSzvCGWlggnhvYVrg_Zn83HxhU7zH_joL2L98jdsspe0b8juqvaNdNec9Yf6PAfRyEa9CCPfLaprqdcFUq2GK9xNjArnj9jXnXu9VaS1qGYNHSqfjMD6rFM7XoAJg6pgX2z7rzFFFxPNTS35wpIiJ4nggvaUyZRerd-8STjIM-WxDHmE5TUCRNQ62HJwIE6drXvA.ngP4FJPLPWy5yAYf.FzlW3KRAZoDS0foWfFIyCvYUwPsy97j7tHM3_1sXh4ju3sJwTH9ozfTLp0rGdbqHAsDyVZjvRjOFvN6JFENHfhWVexmMEbJwUC7z_8_J85SJutv0S68F6ZfmO-7_XW5bq9XpzvWwMMYjCtUn7vw_yte-ZzywGcdC2gEt1gI6pOySt6k5_dZs2jPJ-uPUkbb7066EoetjM1Kx4u-J47eVVSdJjqVpwR8qc1T6K8IEh14P1AqHOujQteB7fhha4ETOskyRYWpAM-gF8Ii8d35tnig9cbFtgHNheSA6QxBHSe6v4RESUGPQxIIwCFT6tKKM_LBenBsHMYOHNaStWg4SXJlXcR1ZtotX5jaBvXU8C5apMU8dQjQ1WwQ8O-vLhd8Xu2BH2zOFYCZrUaZd1eRoZLlioOrFu7HtxmXsBeSEStCKKkKyVuv-dVUFtewhm5Z2JO8_8-eFzJgiGmvRKwW7rYjw1nKJY-gBi4L2en9U5y9WYIzur1SZjhtR5qAYAgdBt1i8bm1xkJQs9gJtYUF8HYGV-6E2BadWmd6hYW9unAaR8hwTHwaoKRKZA0Wxynt2LoInL6VN9M8oVPkYONYGZYnUAoKSy7V54ojoJWZNFECQbUP0wuthypq1729AlpsTx-xUR8YpTX5oO5R3cOiktHgXD2Lqth5gUs5Y-ZyeOPZm2Ra5gRw_8NmUW_H17pFVz2hYM_Ybb5To3vJugWJj8OtIhAYuL7pcb3sBFAG96eZut0XiMGkBkgJyT8PCJSPg61KYlg-6cAwdtMxTO2E6JnmqvUYyAWB78OMCTVjp2Zprjk4aoa4h9Z5G.jdnVflBmckhCT5FXy2153Q",
                                "access_id": "1c0ca90324ebfe3776458976624a8bcb34e89e11"
                              },
                              "api_domain": "https://adv.aidtbook.kr/test",
                              "user_type": "S",
                              "user_status": "O",
                              "user_id": "e3726252-d19c-5ad7-ac74-f19e60fe2f46",
                              "count": "100",
                              "data": [
                                {
                                  "curriculum": "E4ENGA01B01",
                                  "achievement_level": "A",
                                  "percent": "100",
                                  "parter_curriculum": "E4ENGA01B04"
                                },
                                {
                                  "curriculum": "E4ENGA01B02",
                                  "achievement_level": "A",
                                  "percent": "100",
                                  "parter_curriculum": "E4ENGA01B05"
                                },
                                {
                                  "curriculum": "E4ENGA01B03",
                                  "achievement_level": "A",
                                  "percent": "100",
                                  "parter_curriculum": "E4ENGA01B06,E4ENGA01B07,E4ENGA01B08"
                                }
                              ]
                            }
                            """
                    )
            }
            ))
    public @ResponseBody Map<String, Object> test(@RequestBody Map<String, Object> paramData) throws Exception {
        paramData.put("textbkCd", "englmi1");
        return kerisApiService.getUserStudyInfo(paramData);
    }

    @KerisActionLog
    @PostMapping(value = "/keris/lecture/list", produces="application/json; charset=UTF8")
    @Operation(summary = "교사 강의 리스트", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                                "access_token": {
                                    "token":"eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.0gPtAVu109dAmPlBa3DM0p9JwcPIgo6Nn_sHpU0OFvgCJK1GJhYLjTyLYJcOfgalhfzC71_coqXAbtJk7ix91BK5Ab1N_nslpch6cfI1jG7a4P-7aAJNJ9EOG47o7LAVnR7bGmmhYYV0CY6I0ns9y8xXmQl2kGc3r0A1TTnuwysigHRZmYPqiyQ9OTVAQVMCvcfOAkzLYOV3kR08Ps9jPRRL_1TLz6OH0hi7AJ4NuuiKtUCETwGUH_d4eBJPdN5VtB1zCe1AjO3uELfMFV03yO4GGC0eoNP3a980bIoSjqsWVRv3a-54YEb28OVetNceno9mgSj0i0cTb4R9Szv0ng.O5ChU1D9t0Myc3Mg.DDfNPrRI5DMYqBz-8tO1PgPcF3xY6cw7Wop0MvqFJG-_6fKY8O_O4Y9QmTXDF2M5R3-iQoQWtiN0xuZWYhaEyHZtU2GRlgTcuyTswJWNXGj-dVtngpvsCXwhosc7F4hBeij0XOsrHxScoukhAuDwg0ddJyVU3to3r5XQ26JzymIzzOZC2cgmh1ZlHfAaAmQBV2eqhYSX82YVgjVx7gY6FUMqBmt_q8Jc9s0dcOVqc38nhMzvs-txu4nIH8MHQu3UzETvIWDXXs-HgzqebgpzOiHJAsY2SaUUEFLE1sEXV91Fu65sD4uCtuGdGNAZ9bhl2epl8iyKD8NWOseTIq7LJFX26lYSepE7tJ80sv8IatM4UPlEYFyts-XZyOlf4vLDjOnCUXqxXGvZUXONbXpQwktACU4ZUQz0tN9JTdFhrG-immHUC69cFeE1uGZKMjEqkjy0nv6nXyz0XiJEjxYcSNPO4o4yYkwxunuo8a0kVrAmUYpWJv3VdzbtsymRHUljEARq2zUOm3wtpWmjtGXd24mPj1_EvfGE54V0-khRvmf3FQF7fLx4Tsbm2dTPEA13d0TEiaCHmRtXeagV6jtAwmN65DMCnjFbRL_y9myt0EFMhNlWtxKnhVcd7MemrQRRqBdxaT3D8u9BNNWP5copbB5EOH8b5ZdYYXZpMytbcz2uoBMsGZr3B7UUm7v3L6TKVY8oLEAXFZhSUe2L8n5ZuXRp1N5sBXqZI7rpMOTt-Hcu_vVp_T0I6FROSjrEYvsu-m84NWNqyYGUsZO7dVwZO9zWwVMbjOE9YZXuLOUwOUg3orOXfadnw1krE75BQJF7hXyNbsaSLW6Y8wy0cWJnpYeP5gXBHFj7aSd0Cj2v7qZTt1kM22y35bYiO3gnqoHBbM_i_tQYdu3PxlqPAAeKzlRbyeowl9p2ytiRWyzQUyw.H-ZetjWVWwqvms7IpnAKhw",
                                    "access_id":"7217715788e60a1f843740aa21e17cf8bb67704b"
                                },
                                "api_domain" : "https://adv.aidtbook.kr/test",
                                "user_id" : "9b2e2573-5aa2-535b-a145-ed220446316a",
                                "user_type" : "T",
                                "lecture_code" : "4G100000214_20251_10442009",
                                "partner_id" : "ab155f10-a911-5d76-acf8-8357aae4b948",
                                "api_version" : "2.3"
                            }
                            """
                    )
            }
            ))
    public @ResponseBody Map<String, Object> lectureList(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = kerisApiService.lectureList(paramData);
        return resultData;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/index/proc/test", produces="application/json; charset=UTF8")
    @Operation(summary = "(테스트) 공공포털 통합인증 API 수행", description = "")
    @Parameter(
            name = "api_domain",
            description = "API 도메인 예시<br/>개발 서버 : https://t-vivamon.aidtclass.com/keris<br/>B2E 서버 : https://b-vivamon.aibookclass.com/keris<br/>운영 서버 : https://vivamon.aidtclass.com/keris",
            required = true,
            schema = @Schema(type = "string", defaultValue = ""))
    @Parameter(name = "user_id", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "3c7473ec-0f60-539e-86d6-4180b560f0a3"))
    @Parameter(name = "lecture_code", description = "강의 코드", required = false, schema = @Schema(type = "string", example = "3D100000854_20251_10139101"))
    @Parameter(name = "user_se_cd", description = "사용자 구분", required = false, schema = @Schema(type = "string", example = "T"))
    public @ResponseBody Map<String, Object> testUserProc(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userInfo = kerisApiService.getUserTypeAndStatus(paramData);
        if (userInfo == null || userInfo.isEmpty()) {
            result.put("message", "user_id로 조회되는 정보가 없습니다.");
            return result;
        }
        paramData.put("textbkCd", userInfo.getOrDefault("textbk_cd", ""));
        String userType = (String) userInfo.getOrDefault("user_se_cd", "");
        String userStatus = "E";
        try {
            if ((StringUtils.equals(userStatus, "E") || StringUtils.equals(userStatus, "I") || StringUtils.equals(userStatus, "O"))) {
                if (StringUtils.equals(userType, "S")) {
                    //학생 저장 프로세스 수행
                    result = kerisApiService.saveStProc(paramData);
                } else if (StringUtils.equals(userType, "T")) {
                    //교사 저장 프로세스 수행
                    result = kerisApiService.saveTcProc(paramData);
                }
            } else {
                result.put("code", "40001");
                result.put("message", "파라메터오류:정의되지 않은 파라메터");
            }
        } catch (Exception e) {
            log.error("keris err /keris/index/proc:{}, param:{}", e.getMessage(), paramData.toString());
            String errorMessage = e.getMessage();
            String[] parts = errorMessage.split("###");
            if (parts.length > 1) {
                String[] errorParts = parts[1].split("###");
                if (errorParts.length > 0 ) {
                    String firstLine = errorParts[0].trim();
                    if (!firstLine.isEmpty()) {
                        errorMessage = firstLine;
                    }
                }
            }
            result.put("code", "50001");
            result.put("message", errorMessage);
        }
        log.info("/keris/index/proc Return: {}" , result.toString());
        return result;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/prev/index/{textbkCd}", produces="application/json; charset=UTF8")
    @Operation(summary = "공공포털 -> 비상교육 시작메시지 호출 POST(AIDT 미리보기)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                                "access_token": {
                                    "token":"eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.qjuaIIMzIkye8vFBVxOKBRhxf5T7O2OvEhuiHQ9SxE9wROjpcF0pVKad2JBSFtRxvPEarFZlOjngv0TPkH0FJsxanLEwoGI0ltnRf0EyjF6lQ9s4mWQH5lT_2VYcnLUv_qG7_pyiRYS0VvhnHhFjd6Jrd2_7oAg4NoP0_x3GiwdAE3OFfmzLLeeU8V3wZNLHGgktDzFgNXKqP4bOHbKfO2s_2nXMWuLb4M84mObhz5uwpyQf5gmzJFF1WTc1oi4OuhWwpmLvoi_JpTFFlMGwbUg8wXLBXwLl7lob3MyFq3t_6fkEZ2LI09qgDsMHCslCi-TNwgvDIIHXVbB534Pz0g.BcyqwiQ4n98Nkofq.3IboQe0k5ul8ZLPhEWZXmGSd8z3OG663VYtAbeJegN_QX_mpu4snfpo71nfUXZQQ1JKsGT1AZebj6kYhRhjTwivSmRoBfm9NgeGv2HzRwbgcI6QT9-sQt1RhhkD4W7-GPD1n_DaNO_UIkfHyt6pUuUvaoOEJZAqFXHEUVAemu-isiI3RhL1jPhh_vil2d_HHkUfyxavZ0p7wwL5VikCYgemdPY2n2ZXB_GsSL1NKB3Li-TojrQWmaVoLa5e9TGg6TiOUm4RC9apmuexsx2L2N1-8nGBajaKnxL7kFrr6UGTBf9jDoZ7PfJJUf516dyYJx4lMrYeu_fPSVpQlmOe2FTRoAzwwD2pUlvD0L1S9xfVUXUl9AcA_ckXjaW9xMoC0g6zxEbIwK16WVO5Zo80lq0Px6dGoGP-Eatdy7n0NJxRpLVvRlsbALfxvBQ3k594NSY4FFwET4zKuevS0V1XBobxf37msRfFJkw1eB_LIku1VRT1Nxp-S6qtpeBg6yslaKyxGxrO2GaOz0v3l-E8gSvqs2naJULBWiGER9eH85DgWk98dZa7Q_LUxngV34xtT6AbQfVr5NKLHGTkRdIhlVy7NjVMA_a5XhGVyQIsDsDxXJ8vkDEIibjEFeeqkP-9TiMCBDUK64nVgx2-qG1PvqbPGRRb_5iqqXrTjOfhRLYZlvmuKFbHBVY21f5b8CxPmpAEe2ydvVClfugu0SHboYwmZOur3OpZ-GbQZhqUxJqLZmqBwKscxeNXxvGIB_Lo_gYql6jjqMqpz0vBDCh_rkWAxdbirTkqPsYcuKzGpUnghk26ZjiyRGC6h.rs8xtKlTVjKJxhq14holjw",
                                    "access_id":"6cf0dc7d390a8fef20174ca976a698f551fd8f39"
                                },
                                "api_domain" : "https://adv.aidtbook.kr/test",
                                "user_id" : "94b96beb-31d4-5725-a153-1e78f0e52d1b",
                                "schl_crs_se_cd" : "2",
                                "schl_cd" : "학교코드",
                                "scyr" : "학년도",
                                "smstr" : "학기",
                                "api_version" : "2.4"
                            }
                            """
                    )
            }
            ))
    @Parameter(name = "textbkCd", description = "AIDT 교과서 코드 ", required = true, schema = @Schema(type = "string", example = "mathel31"))
    public @ResponseBody Map<String, Object> indexPrev(@RequestBody Map<String, Object> paramData, @PathVariable("textbkCd") String textbkCd) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "00000");
        result.put("message", "성공");
        log.info("/keris/prev/index Return: {}" , result);
        return result;
    }

    @KerisActionLog
    @GetMapping(value = "/keris/prev/index/{textbkCd}")
    @Operation(summary = "공공포털 -> 비상교육 시작메시지 호출 GET", description = "")
    public RedirectView indexPrevGet(@PathVariable("textbkCd") String textbkCd, @RequestParam Map<String, Object> paramData) throws Exception {
        String apiDomain = MapUtils.getString(paramData, "api_domain", "");
        String userId = MapUtils.getString(paramData, "user_id", "");
        String token = MapUtils.getString(paramData, "access_token.token", "");
        String accessId = MapUtils.getString(paramData, "access_token.access_id", "");
        String api_version = MapUtils.getString(paramData, "api_version", "");
        String classPeriod = MapUtils.getString(paramData, "class_period", "");

        paramData.put("textbkCd", textbkCd);

        Map<String, Object> ptnInfo = kerisApiService.getPtnInfo(paramData);
        if (MapUtils.isNotEmpty(ptnInfo)) {
            if (!StringUtils.equals((String) ptnInfo.getOrDefault("apiDomain", ""), apiDomain)) {
                kerisApiService.updatePtnInfo(paramData);
            }
        }

        String partnerId = MapUtils.getString(ptnInfo, "ptnId", "");
        String redirectUrl = (String) ptnInfo.getOrDefault("tcMainUrl", "");
        redirectUrl = redirectUrl + "?userId=" + userId + "&accessToken=" + token + "&accessId=" + accessId + "&apiDomain=" + apiDomain
                + "&userType=T" + "&Partner-ID=" + partnerId + "&textbkCd=" + textbkCd + "&api_version=" + api_version + "&class_period" + classPeriod
                + "&prevYn=Y";
        ;
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/prev/index/proc", produces="application/json; charset=UTF8")
    @Operation(summary = "공공포털 미리보기 통합인증 API 수행", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                                "access_token": {
                                    "token":"eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.qjuaIIMzIkye8vFBVxOKBRhxf5T7O2OvEhuiHQ9SxE9wROjpcF0pVKad2JBSFtRxvPEarFZlOjngv0TPkH0FJsxanLEwoGI0ltnRf0EyjF6lQ9s4mWQH5lT_2VYcnLUv_qG7_pyiRYS0VvhnHhFjd6Jrd2_7oAg4NoP0_x3GiwdAE3OFfmzLLeeU8V3wZNLHGgktDzFgNXKqP4bOHbKfO2s_2nXMWuLb4M84mObhz5uwpyQf5gmzJFF1WTc1oi4OuhWwpmLvoi_JpTFFlMGwbUg8wXLBXwLl7lob3MyFq3t_6fkEZ2LI09qgDsMHCslCi-TNwgvDIIHXVbB534Pz0g.BcyqwiQ4n98Nkofq.3IboQe0k5ul8ZLPhEWZXmGSd8z3OG663VYtAbeJegN_QX_mpu4snfpo71nfUXZQQ1JKsGT1AZebj6kYhRhjTwivSmRoBfm9NgeGv2HzRwbgcI6QT9-sQt1RhhkD4W7-GPD1n_DaNO_UIkfHyt6pUuUvaoOEJZAqFXHEUVAemu-isiI3RhL1jPhh_vil2d_HHkUfyxavZ0p7wwL5VikCYgemdPY2n2ZXB_GsSL1NKB3Li-TojrQWmaVoLa5e9TGg6TiOUm4RC9apmuexsx2L2N1-8nGBajaKnxL7kFrr6UGTBf9jDoZ7PfJJUf516dyYJx4lMrYeu_fPSVpQlmOe2FTRoAzwwD2pUlvD0L1S9xfVUXUl9AcA_ckXjaW9xMoC0g6zxEbIwK16WVO5Zo80lq0Px6dGoGP-Eatdy7n0NJxRpLVvRlsbALfxvBQ3k594NSY4FFwET4zKuevS0V1XBobxf37msRfFJkw1eB_LIku1VRT1Nxp-S6qtpeBg6yslaKyxGxrO2GaOz0v3l-E8gSvqs2naJULBWiGER9eH85DgWk98dZa7Q_LUxngV34xtT6AbQfVr5NKLHGTkRdIhlVy7NjVMA_a5XhGVyQIsDsDxXJ8vkDEIibjEFeeqkP-9TiMCBDUK64nVgx2-qG1PvqbPGRRb_5iqqXrTjOfhRLYZlvmuKFbHBVY21f5b8CxPmpAEe2ydvVClfugu0SHboYwmZOur3OpZ-GbQZhqUxJqLZmqBwKscxeNXxvGIB_Lo_gYql6jjqMqpz0vBDCh_rkWAxdbirTkqPsYcuKzGpUnghk26ZjiyRGC6h.rs8xtKlTVjKJxhq14holjw",
                                    "access_id":"6cf0dc7d390a8fef20174ca976a698f551fd8f39"
                                },
                                "api_domain" : "https://adv.aidtbook.kr/test",
                                "user_id" : "94b96beb-31d4-5725-a153-1e78f0e52d1b",
                                "textbkCd" : "mathel42",
                                "api_version" : "2.4"
                            }
                            """
                    )
            }
            ))
    public @ResponseBody Map<String, Object> prevProc(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            result = kerisApiService.saveTcPrevProc(paramData);
        } catch (Exception e) {
            log.error("keris err /keris/prev/index/proc:{}, param:{}", e.getMessage(), paramData.toString());
            String errorMessage = e.getMessage();
            String[] parts = errorMessage.split("###");
            if (parts.length > 1) {
                String[] errorParts = parts[1].split("###");
                if (errorParts.length > 0 ) {
                    String firstLine = errorParts[0].trim();
                    if (!firstLine.isEmpty()) {
                        errorMessage = firstLine;
                    }
                }
            }
            result.put("code", "50001");
            result.put("message", errorMessage);
        }
        log.info("/keris/prev/index/proc Return: {}" , result.toString());
        return result;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/prev/paste", produces="application/json; charset=UTF8")
    @Operation(summary = "미리보기 계정 복사하기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                                "userId" : "mathbe2-t",
                                "trgtClaIdList" : [
                                    "pasteClaId1",
                                    "pasteClaId2",
                                    "pasteClaId3"
                                ]
                            }
                            """
                    )
            }
            ))
    public @ResponseBody Map<String, Object> pasteClaId(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            result = kerisApiService.pasteClaId(paramData);
        } catch (Exception e) {
            log.error("keris err /keris/prev/paste:{}", e.getMessage());
            result.put("code", "50001");
            result.put("message", "시스템 오류");
        }
        log.info("/keris/prev/paste Return: {}" , result.toString());
        return result;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/index/teacherSetting", produces = "application/json; charset=UTF8")
    @Operation(summary = "반정보 등록 API", description = "")
    @Parameter(
            name = "api_domain",
            description = "API 도메인 예시<br/>개발 서버 : https://t-vivamon.aidtclass.com/keris<br/>B2E 서버 : https://b-vivamon.aibookclass.com/keris<br/>운영 서버 : https://vivamon.aidtclass.com/keris",
            required = false,
            schema = @Schema(type = "string", defaultValue = ""))
    @Parameter(name = "user_id", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "3c7473ec-0f60-539e-86d6-4180b560f0a3"))
    @Parameter(name = "cla_id", description = "반 ID", required = true, schema = @Schema(type = "string", example = "3D100000854_20251_10139101"))
    @Parameter(name = "access_token", description = "token", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "api_version", description = "api version", required = false, schema = @Schema(type = "string", example = ""))
    public @ResponseBody Map<String, Object> teacherSetting(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();

        try {
            kerisApiService.saveTeacherInfo(paramData);

            result.put("resultOk", true);
            result.put("resultMsg", "성공");
            result.put("code", "00000");

        } catch (RuntimeException e) {
            log.error("시스템 오류: {}", e.getMessage());
            result.put("resultOk", false);
            result.put("resultMsg", "시스템 오류가 발생했습니다");
            result.put("code", "99999");
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            result.put("resultOk", false);
            result.put("resultMsg", "시스템 오류가 발생했습니다");
            result.put("code", "99999");
        }

        return result;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/index/teacherFlagInsert", produces = "application/json; charset=UTF8")
    @Operation(summary = "주 & 보조교사 등록 API", description = "")
    @Parameter(name = "user_id", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "3c7473ec-0f60-539e-86d6-4180b560f0a3"))
    @Parameter(name = "cla_id", description = "반 ID", required = true, schema = @Schema(type = "string", example = "3D100000854_20251_10139101"))
    public @ResponseBody Map<String, Object> teacherFlagInsert(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();

        try {
            kerisApiService.saveTeacherFlagInsert(paramData);

            result.put("resultOk", true);
            result.put("resultMsg", "성공");
            result.put("code", "00000");

        } catch (RuntimeException e) {
            log.error("시스템 오류: {}", e.getMessage());
            result.put("resultOk", false);
            result.put("resultMsg", "시스템 오류가 발생했습니다");
            result.put("code", "99999");
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            result.put("resultOk", false);
            result.put("resultMsg", "시스템 오류가 발생했습니다");
            result.put("code", "99999");
        }

        return result;
    }

    @KerisActionLog
    @PostMapping(value = "/keris/index/teacherFlagUpdate", produces = "application/json; charset=UTF8")
    @Operation(summary = "보조교사를 주교사로 권한 변경 API", description = "기존 주교사는 나간 상태로 처리하고, 보조교사를 주교사로 승격시킵니다")
    @Parameter(name = "user_id", description = "주교사로 승격할 보조교사 ID", required = true, schema = @Schema(type = "string", example = "3c7473ec-0f60-539e-86d6-4180b560f0a3"))
    @Parameter(name = "cla_id", description = "반 ID", required = true, schema = @Schema(type = "string", example = "3D100000854_20251_10139101"))
    public @ResponseBody Map<String, Object> teacherFlagUpdate(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();

        try {
            kerisApiService.saveTeacherFlagUpdate(paramData);

            result.put("resultOk", true);
            result.put("resultMsg", "성공");
            result.put("code", "00000");

        } catch (RuntimeException e) {
            if (e.getMessage().contains("데이터") || e.getMessage().contains("조회") || e.getMessage().contains("저장") || e.getMessage().contains("승격")) {
                log.error("데이터 처리 오류: {}", e.getMessage());
                result.put("resultOk", false);
                result.put("resultMsg", e.getMessage());
                result.put("code", "50002");
            } else {
                log.error("시스템 오류: {}", e.getMessage());
                result.put("resultOk", false);
                result.put("resultMsg", "시스템 오류가 발생했습니다");
                result.put("code", "99999");
            }
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            result.put("resultOk", false);
            result.put("resultMsg", "시스템 오류가 발생했습니다");
            result.put("code", "99999");
        }

        return result;
    }


    @KerisActionLog
    @PostMapping(value = "/keris/index/teacherLeave", produces = "application/json; charset=UTF8")
    @Operation(summary = "선생님 나간 처리 API", description = "특정 선생님을 나간 상태로 처리합니다")
    @Parameter(name = "user_id", description = "나간 처리할 선생님 ID", required = true, schema = @Schema(type = "string", example = "3c7473ec-0f60-539e-86d6-4180b560f0a3"))
    @Parameter(name = "cla_id", description = "반 ID", required = true, schema = @Schema(type = "string", example = "3D100000854_20251_10139101"))
    public @ResponseBody Map<String, Object> teacherLeave(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();

        try {
            kerisApiService.saveTeacherLeave(paramData);

            result.put("resultOk", true);
            result.put("resultMsg", "성공");
            result.put("code", "00000");

        } catch (RuntimeException e) {
            if (e.getMessage().contains("데이터") || e.getMessage().contains("조회") || e.getMessage().contains("나간")) {
                log.error("데이터 처리 오류: {}", e.getMessage());
                result.put("resultOk", false);
                result.put("resultMsg", e.getMessage());
                result.put("code", "50002");
            } else {
                log.error("시스템 오류: {}", e.getMessage());
                result.put("resultOk", false);
                result.put("resultMsg", "시스템 오류가 발생했습니다");
                result.put("code", "99999");
            }
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            result.put("resultOk", false);
            result.put("resultMsg", "시스템 오류가 발생했습니다");
            result.put("code", "99999");
        }

        return result;
    }

    @KerisActionLog
    @GetMapping(value = "/keris/lecture/mapping/info")
    @Operation(summary = "사용자 강의코드 매핑 정보 조회", description = "사용자 강의코드 매핑 정보 조회")
    @Parameter(name = "ptn_id", description = "파트너 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "cla_id", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "user_id", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "user_type", description = "사용자 유형", required = false, schema = @Schema(type = "string", example = "T"))
    public ResponseDTO<CustomBody> selectHomeNotice(
            @RequestParam(name = "user_type", defaultValue = "T") String userType,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("user_type", userType);
        Map<String, Object> resultData = kerisApiService.getUserLectureCodeMappingInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "사용자 강의코드 매핑 정보 조회");
    }

    @KerisActionLog
    @PostMapping(value = "/keris/lecture/mapping/setup", produces="application/json; charset=UTF8")
    @Operation(summary = "매핑 데이터 셋팅 API", description = "1학기와 2학기 강의 매핑 설정 및 학생 활성화 상태 변경")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "origin_cla_id": "4T100000157_20241",
                                "cla_id": "4T100000157_20242",
                                "user_id": "vstea50"
                            }
                            """)
            }))
    public ResponseDTO<CustomBody> setupLectureMapping(
            @RequestParam(name = "user_type", defaultValue = "T") String userType,
            @RequestBody Map<String, Object> paramData
    ) {
        paramData.put("user_type", userType);
        Map<String, Object> resultData = kerisApiService.setupLectureMapping(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "매핑 데이터 셋팅");
    }
}