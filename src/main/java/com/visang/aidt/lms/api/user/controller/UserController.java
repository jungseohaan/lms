package com.visang.aidt.lms.api.user.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.log.service.SystemLogService;
import com.visang.aidt.lms.api.user.service.UserService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (교사) 유저 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(유저) API", description = "유저 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;
    private final SystemLogService systemLogService;

    @Loggable
    @GetMapping(value = "/user/info")
    @Operation(summary = "유저 정보 조회", description = "")
    @Parameter(name = "userId", description = "유저 ID",  required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(
            name = "semester",
            description = "학기",
            required = false,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"semester01", "semester02"}
            ),
            in = ParameterIn.QUERY
    )
    public ResponseDTO<CustomBody> userInfo(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "claId") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception {

        Map<String, Object> resultData = userService.findUserInfo(paramData);
        // user info 에서 try catch로 빠지지 않는 버그 상황에 대한 fail 처리 추가
        boolean success = MapUtils.getBoolean(resultData, "success", true);
        if (success) {
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "");
        } else {
            return AidtCommonUtil.makeResultFail(paramData, null, MapUtils.getString(resultData, "resultMessage"));
        }
    }

    @Loggable
    @RequestMapping(value = "/user/clause-agre/save", method = {RequestMethod.POST})
    @Operation(summary = "콘텐츠 오류 신고 등록", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"dclrId\":\"vsstu1\"," +
                            "\"schlCd\":\"1111\"," +
                            "\"textbkId\":1," +
                            "\"dclrTyCd\":3," +
                            "\"dclrSeCd\":2," +
                            "\"dclrCn\":\"신고내용테 테스트\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveUserClauseagre(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = userService.saveUserClauseagre(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "콘텐츠 오류 신고 등록");

    }

    @Loggable
    @RequestMapping(value="/user/clause-agre/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사/학생] 약관동의 팝업 정보 조회", description = "교사와 학생의 약관동의 정보를 조회한다.")
    @Parameter(name = "userId", description = "유저 아이디", required = false, schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> userClauseAgreList(
            @RequestParam(name = "userId", defaultValue = "0", required = true) int userId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = userService.findUserClauseAgreList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사/학생] 약관동의 팝업 정보 조회");

    }

    @Loggable
    @RequestMapping(value="/user/conts-err-dclr/save", method = {RequestMethod.GET})
    @Operation(summary = "[교사/학생] 약관동의 팝업 update", description = "교사와 학생의 약관동의 정보를 수정한다.")
    @Parameter(name = "userId", description = "유저 아이디", required = false, schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "IndvdlinfoColctUseeAgreAt", description = "개인정보수집/이용동의 여부", required = false, schema = @Schema(type = "String", example = "Y"))
    @Parameter(name = "rcptn_agre_ymd", description = "동의일자", required = false, schema = @Schema(type = "String", example = "20240307"))
    public ResponseDTO<CustomBody> userClauseAgreList(
            @RequestParam(name = "userId", defaultValue = "0", required = true) int userId,
            @RequestParam(name = "IndvdlinfoColctUseeAgreAt", required = false) String IndvdlinfoColctUseeAgreAt,
            @RequestParam(name = "rcptn_agre_ymd", required = false) String rcptn_agre_ymd,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = userService.updateUserContsErrDclr(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사/학생] 약관동의 정보 수정");

    }

    @Loggable
    @RequestMapping(value = "/user/accesslog/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 접속로그 목록 조회", description = "학생의 접속로그 정보를 조회한다<br>- 학생ID, 접속날짜, 기기, OS, 브라우저, IP 정보를 조회한다")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathreal79-s1"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> userAccesslogList(
            @RequestParam(name = "userId",   defaultValue = "") String userId,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("userId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);
        Object resultData = userService.findUserAccesslogList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 접속로그 목록 조회");
    }

    /**
     * 학생이 속한 모든 학급 목록 조회 (동일 교과서 기준)
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Loggable
    @RequestMapping(value = "/user/class/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 모든 학급 목록 조회", description = "동일 교과서 기준의 학생의 모든 학급 목록을 조회한다.")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    @Parameter(name = "stdtId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook2190-s1"))
    public ResponseDTO<CustomBody> findStudentClassList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        List<String> requiredParams = Arrays.asList("textbkId","stdtId");
        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = userService.findStudentClassList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 모든 학급 목록 조회");
    }

        /**
     * // 학생이 속한 모든 학급 목록 조회 (학생 기준)
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Loggable
    @RequestMapping(value = "/stnt/class/info", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학생이 속한 모든 학급 목록 조회 (학생 기준)", description = "// 학생이 속한 모든 학급 목록 조회 (학생 기준)")
    @Parameter(name = "stdtId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook2190-s1"))
    public ResponseDTO<CustomBody> findStudentClassInfoList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = userService.findStudentClassInfoList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 학생이 속한 모든 학급 목록 조회");
    }


        /**
     * // 교사이 속한 모든 학급 목록 조회 (교사 기준)
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Loggable
    @RequestMapping(value = "/tch/class/info", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 교사가 속한 모든 학급 목록 조회 (교사 기준)", description = "// 교사 속한 모든 학급 목록 조회 (교사 기준)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook2190-t"))
    public ResponseDTO<CustomBody> findTchClassInfoList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = userService.findTchClassInfoList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 교사이 속한 모든 학급 목록 조회");
    }


    /**
     * (공통) 사용자 계정으로 로그 등록을 할 지 여부 정보를 전달 받는다
     * @param paramData - { "userId" : "mathbook2190-t" }
     * @return
     * @throws Exception
     */
    @Loggable
    @RequestMapping(value = "/user/check/info", method = {RequestMethod.GET})
    @Operation(summary = "(공통) 사용자 계정으로 로그 등록을 할 지 여부 정보를 전달 받는다", description = "// 계정의 로그 등록 여부 호출")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook2190-t"))
    @Parameter(
            name = "inspSrvc",
            description = "<h3>서비스 목록</h3><h5>런처 : visang-aidt-launcher</h5><h5>vcloudapi : vlmsapi</h5>",
            required = false,
            schema = @Schema(
                    type = "string",
                    allowableValues = {"visang-aidt-launcher", "vlmsapi"}
            ),
            in = ParameterIn.QUERY
    )
    @Parameter(
            name = "inspAreaKey",
            description = "<h3>서비스 상세 영역</h3><h5>같은 git 내에서 영역 구분이 필요할 경우 (초기값 : default)</h5>",
            required = false,
            schema = @Schema(
                    type = "string",
                    allowableValues = { "default" }
            ),
            in = ParameterIn.QUERY
    )
    public ResponseDTO<CustomBody> getUserCheckInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        // 케리스 접근 후 현재 UUID 계정에서 세부 로깅을 할 지 여부를 조회하여 전달함
        Map<String, Object> userCheckInfo = null;
        try {// 테이블 추가되어 있어야 하는 신규 로직이기 때문에 사이드 이펙트 방지를 위해 try catch
            userCheckInfo = systemLogService.getUserCheckInfo(paramData);
        } catch (NullPointerException e) {
            // null 체크 관련 오류
            log.error("[get] /user/check/info - NullPointerException: {}, param:{}", e.getMessage(), paramData.toString());
            userCheckInfo = new HashMap<>();
            userCheckInfo.put("userId", MapUtils.getString(paramData, "userId"));
            userCheckInfo.put("userIdx", 0);
            userCheckInfo.put("logRegAt", "N");
            userCheckInfo.put("resultMessage", "NullPointerException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 잘못된 파라미터 오류
            log.error("[get] /user/check/info - IllegalArgumentException: {}, param:{}", e.getMessage(), paramData.toString());
            userCheckInfo = new HashMap<>();
            userCheckInfo.put("userId", MapUtils.getString(paramData, "userId"));
            userCheckInfo.put("userIdx", 0);
            userCheckInfo.put("logRegAt", "N");
            userCheckInfo.put("resultMessage", "IllegalArgumentException: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("[get] /user/check/info - DataAccessException: {}, param:{}", e.getMessage(), paramData.toString());
            userCheckInfo = new HashMap<>();
            userCheckInfo.put("userId", MapUtils.getString(paramData, "userId"));
            userCheckInfo.put("userIdx", 0);
            userCheckInfo.put("logRegAt", "N");
            userCheckInfo.put("resultMessage", "DataAccessException: " + e.getMessage());
        } catch (RuntimeException e) {
            // 런타임 오류
            log.error("[get] /user/check/info - RuntimeException: {}, param:{}", e.getMessage(), paramData.toString());
            userCheckInfo = new HashMap<>();
            userCheckInfo.put("userId", MapUtils.getString(paramData, "userId"));
            userCheckInfo.put("userIdx", 0);
            userCheckInfo.put("logRegAt", "N");
            userCheckInfo.put("resultMessage", "RuntimeException: " + e.getMessage());
        } catch (Exception e) {
            // 기타 모든 오류
            log.error("[get] /user/check/info - Exception: {}, param:{}", e.getMessage(), paramData.toString());
            userCheckInfo = new HashMap<>();
            userCheckInfo.put("userId", MapUtils.getString(paramData, "userId"));
            userCheckInfo.put("userIdx", 0);
            userCheckInfo.put("logRegAt", "N");
            userCheckInfo.put("resultMessage", "Exception: " + e.getMessage());
        }
        // 무조건 로깅 처리가 필요할 경우 아래 주석 해제
        /*userCheckInfo.put("logRegAt", "Y");*/
        String resultMessage = MapUtils.getString(userCheckInfo, "resultMessage");
        if (StringUtils.isEmpty(resultMessage)) {
            return AidtCommonUtil.makeResultSuccess(paramData, userCheckInfo, "(공통) 계정의 로그 등록 여부 호출");
        } else {
            userCheckInfo.remove("resultMessage");
            log.error("[get] /user/check/info:{}, param:{}", resultMessage, paramData.toString());
            return AidtCommonUtil.makeResultFail(paramData, userCheckInfo, resultMessage);
        }
    }

}
