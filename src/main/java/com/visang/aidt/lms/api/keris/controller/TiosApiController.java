package com.visang.aidt.lms.api.keris.controller;

import com.visang.aidt.lms.api.common.mngrAction.constant.MngrActionType;
import com.visang.aidt.lms.api.keris.service.TiosApiService;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@Tag(name = "한국교과서연구재단 -> 비상AIDT")
@RequiredArgsConstructor
public class TiosApiController {

    @Value("${app.lms.errorPageUrl}")
    private String errorPageUrl;

    @Value("${app.lcmsapi.deployServerCode}")
    public String deployServerCode;

    @Value("${app.lcmsapi.deployServerCodeMulti}")
    public String deployServerCodeMulti;/*민간존 운영(비바샘)의 경우에는 컨텐츠 배포를 여러 서버 다중 배포가 일어남*/

    @Value("${spring.profiles.active}")
    private String serverEnv;

    private final TiosApiService tiosApiService;

    private final JwtUtil jwtUtil;

    @RequestMapping(value = "/tios/index/{textbkCd}", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "한국교과서연구재단 -> 비상교육 시작메시지 호출 POST", description = "")
    @Parameter(name = "textbkCd", description = "AIDT 교과서 코드 ", required = true, schema = @Schema(type = "string", example = "mathel31"))
    public RedirectView index(@PathVariable("textbkCd") String textbkCd, @RequestParam Map<String, Object> paramData) throws Exception {
        Instant startTime = Instant.now();
        Duration duration = Duration.between(startTime, startTime);
        RedirectView redirectView = new RedirectView();
        log.info("param: {}" ,  paramData);
        String authCode = MapUtils.getString(paramData, "authCode", "");
        String authType = MapUtils.getString(paramData, "authType", "");
        paramData.put("deployServerCode", deployServerCode);

        //파라미터 빈값여부 체크
        if (StringUtils.isEmpty(authCode) || StringUtils.isEmpty(authType)) {
            return this.createErrorRedirectView( "파라미터 오류");
        }

        //authType 값 체크
        authType = authType.toLowerCase();
        if (!authType.equals("t") && !authType.equals("s")) {
            return this.createErrorRedirectView( "파라미터 오류");
        }

        //userId 변환
        paramData.put("referUserId", authCode);
        String encAuthCode = "tios-"+ textbkCd + "-" + CommonUtils.encryptString(authCode).substring(0,15);
        String userId = encAuthCode;
        if (authType.equals("t")) {
            userId = userId + '-'+ authType;
        } else {
            userId = userId + '-'+ authType + "1";
        }

        //사용자 접근 커스텀 로그
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            attributes.setAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
            attributes.setAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_SE_CD, authType.toUpperCase(), RequestAttributes.SCOPE_REQUEST);
        }

        // 파트너 ID 조회
        paramData.put("textbkCd", textbkCd);
        Map<String, Object> ptnInfo = tiosApiService.getPtnInfo(paramData);
        if (MapUtils.isEmpty(ptnInfo)) {
            return this.createErrorRedirectView( "파트너 아이디 조회 실패 하였습니다.");
        }

        //사용자 정보 조회
        paramData.put("userId", userId);
        paramData.put("encAuthCode", encAuthCode);
        Map<String, Object> userInfo = tiosApiService.getUserInfo(paramData);
        if (MapUtils.isEmpty(userInfo)) {
            // 등록된 사용자가 없는 경우. 교사1, 학생5 계정 세팅
            try {
                tiosApiService.saveAccountProc(paramData, ptnInfo);
            } catch (IllegalArgumentException e) {
                log.error("신규 사용자 등록 - 잘못된 파라미터: {}", e.getMessage());
                return this.createErrorRedirectView("잘못된 파라미터로 인한 신규 사용자 등록 실패");
            } catch (DataAccessException e) {
                log.error("신규 사용자 등록 - 데이터베이스 접근 오류: {}", e.getMessage());
                return this.createErrorRedirectView("데이터베이스 오류로 인한 신규 사용자 등록 실패");
            } catch (Exception e) {
                log.error("신규 사용자 등록 - 예상치 못한 오류: {}", e.getMessage());
                return this.createErrorRedirectView("신규 사용자 등록에 실패 하였습니다.");
            }
        }

        //redirectUrl 세팅
        String partnerId = MapUtils.getString(ptnInfo, "ptnId", "");
        String redirectUrl = (String) ptnInfo.getOrDefault("tiosMainUrl", "");
        /*
        if (StringUtils.equals(authType, "t")) {
            redirectUrl = (String) ptnInfo.getOrDefault("tcMainUrl", "");
        }
        */

        //String idHash = CommonUtils.encryptString(userId);
        //redirectUrl = redirectUrl + "?userId="+userId + "&isKeris=N&privacyAgreement=N&Partner-ID=" + partnerId + "&idHash=" + idHash;
        redirectUrl = redirectUrl + "?userId="+userId + "&Partner-ID=" + partnerId;
        redirectView.setUrl(redirectUrl);
        log.info("redirectUrl: {}" , redirectUrl);
        //redirectView.setUrl("https://t-class.aidtclass.com/launcher/launcher/aidt-el-math3-1.html?userId=mathbe1-t&isKeris=N&Partner-ID=f9e5056b-dcc7-5874-bea8-9969c6d45795&idHash=8f3d8defc3b5463bd945ae6c41446e875edbcdf108574164895b7f18bb3e97ee&privacyAgreement=N");
        //return redirectView;

        Instant endTime = Instant.now();
        duration = Duration.between(startTime, endTime);
        log.info("TiosService total duration:{}", duration.toMillis());
        return redirectView;
    }

    @GetMapping(value = "/vivasam/index/{textbkCd}")
    @Operation(summary = "비바샘 -> 비상교육 시작메시지 호출 POST", description = "")
    @Parameter(name = "textbkCd", description = "AIDT 교과서 코드 ", required = true, schema = @Schema(type = "string", example = "mathel31"))
    public RedirectView vivasamIndex(@PathVariable("textbkCd") String textbkCd, @RequestParam Map<String, Object> paramData) throws Exception {
        RedirectView redirectView = new RedirectView();
        log.info("param: {}" ,  paramData);
        String userId = MapUtils.getString(paramData, "userId", "");
        String userSeCd = MapUtils.getString(paramData, "userSeCd", "");
        String jwtToken = MapUtils.getString(paramData, "jwtToken", "");
        //파라미터 빈값여부 체크
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(userSeCd)) {
            return this.createErrorRedirectView( "파라미터 오류");
        }
        //userSeCd 값 체크
        if (!userSeCd.toLowerCase().equals("t") && !userSeCd.toLowerCase().equals("s")) {
            return this.createErrorRedirectView( "파라미터 오류");
        }
        //jwt토큰 체크

        // 로컬에서는 패스
        if (StringUtils.equals(serverEnv, "local") == false) {
            try {
                Claims claims = jwtUtil.getAllClaimsFromToken(jwtToken);
                String jwtUserId = claims.get("id", String.class);
                log.info("jwtUserId: {}", jwtUserId);
                if (jwtUserId == null || !jwtUserId.equals(userId)) {
                    return this.createErrorRedirectView("인증 오류");
                }
            } catch (IllegalArgumentException e) {
                log.error("JWT 토큰 검증 - 잘못된 파라미터: {}", e.getMessage());
                return this.createErrorRedirectView("인증 오류");
            } catch (DataAccessException e) {
                log.error("JWT 토큰 검증 - 데이터베이스 접근 오류: {}", e.getMessage());
                return this.createErrorRedirectView("인증 오류");
            } catch (Exception e) {
                log.error("JWT 토큰 검증 - 예상치 못한 오류: {}", e.getMessage());
                return this.createErrorRedirectView("인증 오류");
            }
        }

        paramData.put("referUserId", userId);
        paramData.put("deployServerCode", deployServerCode);

        userSeCd = userSeCd.toLowerCase();
        String encAuthCode = "vivasam-"+ textbkCd + "-" + CommonUtils.encryptString(userId).substring(0,15);
        userId = encAuthCode;
        if (userSeCd.equals("t")) {
            userId = userId + '-'+ userSeCd;
        } else {
            userId = userId + '-'+ userSeCd + "1";
        }

        //사용자 접근 커스텀 로그
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            attributes.setAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
            attributes.setAttribute(MngrActionType.MNGRACTION_CUSTOM_USER_SE_CD, userSeCd.toUpperCase(), RequestAttributes.SCOPE_REQUEST);
        }

        // 파트너 ID 조회
        paramData.put("textbkCd", textbkCd);
        Map<String, Object> ptnInfo = tiosApiService.getPtnInfo(paramData);
        if (MapUtils.isEmpty(ptnInfo)) {
            return this.createErrorRedirectView( "파트너 아이디 조회 실패 하였습니다.");
        }

        //사용자 정보 조회
        paramData.put("userId", userId);
        paramData.put("encAuthCode", encAuthCode);
        Map<String, Object> userInfo = tiosApiService.getUserInfo(paramData);
        if (MapUtils.isEmpty(userInfo)) {
            // 등록된 사용자가 없는 경우. 교사1, 학생5 계정 세팅
            try {
                tiosApiService.saveVivasamAccountProc(paramData, ptnInfo);
            } catch (IllegalArgumentException e) {
                log.error("신규 사용자 등록 - 잘못된 파라미터: {}", e.getMessage());
                return this.createErrorRedirectView("잘못된 파라미터로 인한 신규 사용자 등록 실패");
            } catch (DataAccessException e) {
                log.error("신규 사용자 등록 - 데이터베이스 접근 오류: {}", e.getMessage());
                return this.createErrorRedirectView("데이터베이스 오류로 인한 신규 사용자 등록 실패");
            } catch (Exception e) {
                log.error("신규 사용자 등록 - 예상치 못한 오류: {}", e.getMessage());
                return this.createErrorRedirectView("신규 사용자 등록에 실패 하였습니다.");
            }
        }

        //redirectUrl 세팅
        String partnerId = MapUtils.getString(ptnInfo, "ptnId", "");
        String redirectUrl = (String) ptnInfo.getOrDefault("tiosMainUrl", "");
        redirectUrl = redirectUrl + "?userId="+userId + "&Partner-ID=" + partnerId;
        redirectView.setUrl(redirectUrl);
        /*
        String redirectUrl = (String) ptnInfo.getOrDefault("stdtMainUrl", "");
        if (StringUtils.equals(userSeCd.toLowerCase(), "t")) {
            redirectUrl = (String) ptnInfo.getOrDefault("tcMainUrl", "");
        }
        String idHash = CommonUtils.encryptString(userId);
        redirectUrl = redirectUrl + "?userId="+userId + "&isKeris=N&privacyAgreement=N&Partner-ID=" + partnerId + "&idHash=" + idHash;
        redirectView.setUrl(redirectUrl);
        */
        log.info("redirectUrl: {}" , redirectUrl);
        return redirectView;
    }


    public RedirectView createErrorRedirectView(String errMsg) {
        String url = UriComponentsBuilder.fromHttpUrl(errorPageUrl)
                .queryParam("msg", errMsg)
                .queryParam("linkShow", "gone")
                .toUriString();
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(url);
        return redirectView;
    }

}
