package com.visang.aidt.lms.api.stress.controller;

import com.visang.aidt.lms.api.common.mngrAction.constant.MngrActionType;
import com.visang.aidt.lms.api.keris.service.TiosApiService;
import com.visang.aidt.lms.api.stress.service.StressTiosApiService;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Controller
@Tag(name = "한국교과서연구재단 -> 비상AIDT")
@RequiredArgsConstructor
public class StressTiosApiController {

    @Value("${app.lms.errorPageUrl}")
    private String errorPageUrl;

    @Value("${app.lcmsapi.deployServerCode}")
    public String deployServerCode;

    private final StressTiosApiService stressTiosApiService;

    @GetMapping(value = "/stress/tios/index/{textbkCd}")
    @Operation(summary = "한국교과서연구재단 -> 비상교육 시작메시지 호출 POST", description = "")
    @Parameter(name = "textbkCd", description = "AIDT 교과서 코드 ", required = true, schema = @Schema(type = "string", example = "mathel31"))
    public ResponseEntity<Map<String, Object>> index(@PathVariable("textbkCd") String textbkCd, @RequestParam Map<String, Object> paramData) throws Exception {

        log.info("param: {}", paramData);
        String authCode = generateRandomAuthCode();
        String authType = MapUtils.getString(paramData, "authType", "");
        paramData.put("deployServerCode", deployServerCode);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "00000");
        response.put("message", "성공");

        //userId 변환
        paramData.put("referUserId", authCode);
        String encAuthCode = "tios-" + textbkCd + "-" + CommonUtils.encryptString(authCode).substring(0, 15);
        String userId = encAuthCode;
        if (authType.equals("t")) {
            userId = userId + '-' + authType;
        } else {
            userId = userId + '-' + authType + "1";
        }

        // 파트너 ID 조회
        paramData.put("textbkCd", textbkCd);
        Map<String, Object> ptnInfo = stressTiosApiService.getPtnInfo(paramData);
        if (MapUtils.isNotEmpty(ptnInfo)) {
            //사용자 정보 조회
            paramData.put("userId", userId);
            paramData.put("encAuthCode", encAuthCode);

            log.info("paramData:{}", paramData.toString());
            Map<String, Object> userInfo = stressTiosApiService.getUserInfo(paramData);
            if (MapUtils.isEmpty(userInfo)) {
                // 등록된 사용자가 없는 경우. 교사1, 학생5 계정 세팅
                try {
                    stressTiosApiService.saveAccountProc(paramData, ptnInfo);
                } catch (NullPointerException e) {
                    log.error("saveAccountProc - NullPointerException: {}", e.getMessage());
                } catch (IllegalArgumentException e) {
                    log.error("saveAccountProc - IllegalArgumentException: {}", e.getMessage());
                } catch (RuntimeException e) {
                    log.error("saveAccountProc - RuntimeException: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("saveAccountProc - Exception: {}", e.getMessage());
                }
            }
        }
        return ResponseEntity.ok(response);
    }


    public String generateRandomAuthCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }
}
