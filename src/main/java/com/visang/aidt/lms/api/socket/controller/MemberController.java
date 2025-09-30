package com.visang.aidt.lms.api.socket.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.socket.service.MemberService;
import com.visang.aidt.lms.api.socket.vo.UserDiv;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.ApiAuthUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import com.visang.aidt.lms.global.vo.socket.SocketExceptionBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(Socket) Member API", description = "(Socket) 회원 공통 API")
@RequiredArgsConstructor
@RequestMapping(value = "/member", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {

    private final MemberService memberService;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Value("${key.salt.main}")
    private String keySaltMain;

    @GetMapping(value="/_login.json")
    @Operation(summary = "학생 클래스 입장", description = "<h2>학생 클래스 입장 API</h2>" +
            "<h3><font color='red'>※주의!</font>" +
            "<br><font color='red'> 1. uuid와 userDiv값만 정확히 넣어 주세요</font>" +
            "<br><font color='red'> 2. user 테이블을 기준으로 uuid 세팅하시면 됩니다.</font>" +
            "<br><font color='red'> 3. 학생인데 stdt_reg_info 매핑 정보가 없을 경우 오류 메세지 return 합니다.</font>" +
            "<br><font color='red'> 4. 선생은 현재 스쿨 매핑 테이블이 없습니다. (선생님 스쿨은 `XX초등학교`로 고정 값)</font>" +
            "<br><font color='red'> 5. user 테이블에 user_div 컬럼이 없어서 user 조회 조건은 uuid 단일 조건 입니다.</font>" +
            "</h3>" +
            "<h3>참고URL : https://vcloudapi.edutdc.com/member/_login.json?ip=0.0.0.0&macAddr=12%3A34%3A56%3A78%3A90%3AAB&pwd=q12345&userDiv=T&username=Smith%20Aile&userphone=1111111111&uuid=F.vtest</h3>", parameters = {
            @Parameter(name = "userDiv", description = "유저 구분값(T-선생, S-학생, P-부모)", example = "S", content = @Content(schema = @Schema(implementation = UserDiv.class)), required = true),
            @Parameter(name = "pwd", description = "패스워드", required = true, schema = @Schema(type = "string", example = "q12345")),
            @Parameter(name = "uuid", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000")),
            @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "bcaf4c85c58c4734872e9d925001376b")),
            @Parameter(
                    name = "semester",
                    description = "학기",
                    required = false,
                    schema = @Schema(
                            type = "string",
                            allowableValues = {"semester01", "semester02"}
                    ),
                    in = ParameterIn.QUERY
            ),
            @Parameter(name = "username", description = "유저 이름", schema = @Schema(type = "string", example = "Smith Aile")),
            @Parameter(name = "userphone", description = "휴대전화 번호", schema = @Schema(type = "string", example = "1111111111")),
            @Parameter(name = "ip", description = "아이피",  schema = @Schema(type = "string", example = "0.0.0.0")),
            @Parameter(name = "macAddr", description = "MAC 주소", schema = @Schema(type = "string", example = "12:34:56:78:90:AB")),
            @Parameter(name = "device", description = "기기명", schema = @Schema(type = "string", example = "PC")),
            @Parameter(name = "os", description = "OS", schema = @Schema(type = "string", example = "Win10")),
            @Parameter(name = "browser", description = "브라우저", schema = @Schema(type = "string", example = "Chrome"))
    }, responses = {
        @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(examples = @ExampleObject(
                        """
                  {"result": 0,"birthday": "","thumbnail": "","frIdx": 1299,"SSOToken": "","gender": "M","schIdx": 0,"brcIdx": 0,
                  "nickName": "Smith Aile","defaultThumbnail": "","uuid": "F.vtest","token": "","profileThumbnail": "",
                  "sckId": 0,"name": "Smith Aile","id": 2111,"enc": "","pwd": "","userDiv": "T","resultType": "Success","key": ""}"""
                ))),
        @ApiResponse(responseCode = "400", description = "실패",
                content = @Content(
                        examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                        schema = @Schema(implementation = SocketExceptionBody.class))
        ),
        @ApiResponse(responseCode = "500", description = "실패",
                content = @Content(
                        examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                        schema = @Schema(implementation = SocketExceptionBody.class))
        )
    })
    public Map<String, Object> _login (
            @RequestParam final UserDiv userDiv,
            @RequestParam final String pwd,
            @RequestParam final String uuid,
            @RequestParam(value="claId", required = false) final String claId,
            @RequestParam(value="semester", required = false) final String semester,
            @RequestParam(value="username", required = false) final String username,
            @RequestParam(value="userphone", required = false) final String userphone,
            @RequestParam(value="ip", required = false) final String ip,
            @RequestParam(value="macAddr", required = false) final String macAddr,
            @RequestParam(value="device", required = false) final String device,
            @RequestParam(value="os", required = false) final String os,
            @RequestParam(value="browser", required = false) final String browser,
            HttpServletRequest request
    ) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("userDiv", userDiv.toString());
        paramData.put("uuid", uuid);
        paramData.put("pwd", pwd);
        paramData.put("username", username);
        paramData.put("claId", claId);
        paramData.put("semester", semester);
        paramData.put("ip", ip);
        paramData.put("macAddr", macAddr);
        paramData.put("device", device);
        paramData.put("os", os);
        paramData.put("browser", browser);
        paramData.put("lgnSttsAt", 1);

        Map<String,Object> resultData = memberService.getUserInfo(paramData, true);

        // 비바샘에서는 auth-login 아직 적용 전 (auth-login 적용하고 이거 지워야 함)
        if (StringUtils.equals(serverEnv, "vs-math-prod") || StringUtils.equals(serverEnv, "vs-math-develop")) {
            // 기존에 getUserInfo 내에 있던 것 밖으로 뺐음 (user 정보 호출 시 마다 중복 저장 방지)
            memberService.insertAccessLog(paramData, request); // 학생 접속로그 기록
        }

        String resultMessage = "(Socket) 회원 공통 API";
        resultData.put("resultMessage", resultMessage);

        //형식 변경 금지
        return resultData;
    }

    @PostMapping(value="/_login.json")
    @Operation(summary = "학생 클래스 입장", description = "<h2>학생 클래스 입장 API(POST 방식으로 변경)</h2>" +
            "<h3><font color='red'>※주의!</font>" +
            "<br><font color='red'> 1. uuid와 userDiv값만 정확히 넣어 주세요</font>" +
            "<br><font color='red'> 2. user 테이블을 기준으로 uuid 세팅하시면 됩니다.</font>" +
            "<br><font color='red'> 3. 학생인데 stdt_reg_info 매핑 정보가 없을 경우 오류 메세지 return 합니다.</font>" +
            "<br><font color='red'> 4. 선생은 현재 스쿨 매핑 테이블이 없습니다. (선생님 스쿨은 `XX초등학교`로 고정 값)</font>" +
            "<br><font color='red'> 5. user 테이블에 user_div 컬럼이 없어서 user 조회 조건은 uuid 단일 조건 입니다.</font>" +
            "</h3>" +
            "<h3>참고URL : https://vcloudapi.edutdc.com/member/_login.json?ip=0.0.0.0&macAddr=12%3A34%3A56%3A78%3A90%3AAB&pwd=q12345&userDiv=T&username=Smith%20Aile&userphone=1111111111&uuid=F.vtest</h3>", parameters = {
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject(
                            """
                      {"result": 0,"birthday": "","thumbnail": "","frIdx": 1299,"SSOToken": "","gender": "M","schIdx": 0,"brcIdx": 0,
                      "nickName": "Smith Aile","defaultThumbnail": "","uuid": "F.vtest","token": "","profileThumbnail": "",
                      "sckId": 0,"name": "Smith Aile","id": 2111,"enc": "","pwd": "","userDiv": "T","resultType": "Success","key": ""}"""
                    ))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
            @ApiResponse(responseCode = "500", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                              "ip": "210.221.232.133",
                              "macAddr": "",
                              "uuid": "mathbe1-t",
                              "pwd": "tempTest1234",
                              "userDiv": "T",
                              "claId": "7d6f25631001495b903997f9e06da609",
                              "semester": "",
                              "device": "PC",
                              "os": "Windows",
                              "browser": "Chrome"
                            }
                            """
                    )
            }
            ))
    public Map<String, Object> _loginPost (
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String,Object> resultData = memberService.getUserInfo(paramData,true);
        String resultMessage = "(Socket) 회원 공통 API";
        resultData.put("resultMessage", resultMessage);

        //형식 변경 금지
        return resultData;
    }

    @GetMapping(value="/_logout.json")
    @Operation(summary = "학생 클래스 퇴장", description = "<h2>학생 클래스 퇴장(로그아웃) API</h2>",
            parameters = {
            @Parameter(name = "uuid", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
           //,@Parameter(name = "lgnSttsAt", description = "로그인 여부 (1:로그인/2:로그아웃)", schema = @Schema(type = "int", example = "2"))
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject(
                            """
                      {"result": 0,"resultMessage": "(Socket) logout"}"""
                    ))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
            @ApiResponse(responseCode = "500", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    public Map<String, Object> _logout(
            @RequestParam final String uuid
//            , @RequestParam final int lgnSttsAt
    ) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("uuid", uuid);
        paramData.put("lgnSttsAt", 2);

        Map<String,Object> resultData = memberService.updateLoginStatus(paramData);
        String resultMessage = "(Socket) logout";
        resultData.put("resultMessage", resultMessage);

        //형식 변경 금지
        return resultData;
    }
/*
    @GetMapping(value="/_logoutAll.json")
    @Operation(summary = "모든 유저 클래스 퇴장", description = "<h2>모든 유저 클래스 퇴장(로그아웃) API</h2>", responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject(
                            """
                      {"result": 0,"resultMessage": "(Socket) logout"}"""
                    ))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
            @ApiResponse(responseCode = "500", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    public Map<String, Object> _logoutAll() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("lgnSttsAt", 2);

        Map<String,Object> resultData = memberService.updateLoginStatusAll(paramData);
        String resultMessage = "(Socket) logout";
        resultData.put("resultMessage", resultMessage);

        //형식 변경 금지
        return resultData;
    }
 */

    @PostMapping(value="/auth-login.json")
    @Operation(summary = "학생 클래스 입장", description = "<h2>학생 클래스 입장 API(POST 방식으로 변경) - token 값 추가 전달</h2>" +
            "<h3><font color='red'>※주의!</font>" +
            "<br><font color='red'> 1. uuid와 userDiv값만 정확히 넣어 주세요</font>" +
            "<br><font color='red'> 2. user 테이블을 기준으로 uuid 세팅하시면 됩니다.</font>" +
            "<br><font color='red'> 3. 학생인데 stdt_reg_info 매핑 정보가 없을 경우 오류 메세지 return 합니다.</font>" +
            "<br><font color='red'> 4. 선생은 현재 스쿨 매핑 테이블이 없습니다. (선생님 스쿨은 `XX초등학교`로 고정 값)</font>" +
            "<br><font color='red'> 5. user 테이블에 user_div 컬럼이 없어서 user 조회 조건은 uuid 단일 조건 입니다.</font>" +
            "</h3>" +
            "<h3>참고URL : https://vcloudapi.edutdc.com/member/_login.json?ip=0.0.0.0&macAddr=12%3A34%3A56%3A78%3A90%3AAB&pwd=q12345&userDiv=T&username=Smith%20Aile&userphone=1111111111&uuid=F.vtest</h3>", parameters = {
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject(
                            """
                      {"result": 0,"birthday": "","thumbnail": "","frIdx": 1299,"SSOToken": "","gender": "M","schIdx": 0,"brcIdx": 0,
                      "nickName": "Smith Aile","defaultThumbnail": "","uuid": "F.vtest","token": "","profileThumbnail": "",
                      "sckId": 0,"name": "Smith Aile","id": 2111,"enc": "","pwd": "","userDiv": "T","resultType": "Success","key": ""}"""
                    ))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
            @ApiResponse(responseCode = "500", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                              "ip": "210.221.232.133",
                              "macAddr": "",
                              "uuid": "mathbe1-t",
                              "pwd": "tempTest1234",
                              "userDiv": "T",
                              "claId": "7d6f25631001495b903997f9e06da609",
                              "semester": "",
                              "device": "PC",
                              "os": "Windows",
                              "browser": "Chrome"
                            }
                            """
                    )
            }
            ))
    public Map<String, Object> authLoginPost (
            @RequestBody Map<String, Object> paramData, HttpServletRequest request
    ) throws Exception {


        Map<String, Object> authFailMap = null;

        // controller snippet inside /member/auth-login.json handler
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.startsWith("Bearer ") == false) {
            authFailMap = Map.of("result", 401, "resultType", "Fail", "returnType", "Missing Authorization");
        } else {
            String token = authHeader.substring(7).trim();
            String uuid = MapUtils.getString(paramData, "uuid");
            String apiUrl = request.getRequestURI();
            String matchedSalt2 = ApiAuthUtil.verifyAuthTokenByTimeWindow(token, keySaltMain, uuid, apiUrl);
            if (matchedSalt2 == null) {
                authFailMap = Map.of("result", 401, "resultType", "Fail", "returnType", "Invalid or expired token");
            }
        }

        // 테스트 완료 이후 주석 해제
        /*if (authFailMap != null) {
            return authFailMap;
        }*/

        Map<String,Object> resultData = memberService.getUserInfo(paramData, false);
        // 기존에 getUserInfo 내에 있던 것 밖으로 뺐음 (user 정보 호출 시 마다 중복 저장 방지)
        memberService.insertAccessLog(paramData, request); // 학생 접속로그 기록

        // accessToken, refreshToken, hmac 정보 return
        Map<String, Object> tokenMap = memberService.getCurrentTokenInfo(paramData);
        // 해당 3개 정보 세팅
        if (resultData != null && tokenMap != null) {
            resultData.putAll(tokenMap);
        }

        String resultMessage = "(Socket) 회원 공통 API - front 호출용 login 토큰추가 API";
        resultData.put("resultMessage", resultMessage);

        if (authFailMap != null) {
            resultData.put("returnType", authFailMap.get("returnType"));
        }

        //형식 변경 금지
        return resultData;
    }

    @PostMapping(value="/socket-login.json")
    @Operation(summary = "학생 클래스 입장", description = "<h2>학생 클래스 입장 API(POST 방식으로 변경)</h2>" +
            "<h3>소켓에서 호출하는 경량화 API</h3>", parameters = {
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject(
                            """
                              {
                                  "uuid": "codebsample001-t",
                                  "result": 0,
                                  "classid": 24707,
                                  "claId": "76c9781644f846c4af6bdecf6af56e82",
                                  "name": "codebsample001-t",
                                  "id": 402415,
                                  "userDiv": "T"
                              }
                            """
                    ))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
            @ApiResponse(responseCode = "500", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "uuid": "codebsample001-t",
                                "pwd": "Vip12345!",
                                "userDiv": "T",
                                "claId": "76c9781644f846c4af6bdecf6af56e82",
                                "semester": ""
                            }
                            """
                    )
            }
            ))
    public Map<String, Object> socketLoginPost (
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String,Object> resultData = memberService.getUserInfoForSocket(paramData);

        String resultMessage = "(Socket) 회원 공통 API - 소켓 경량화 API";
        resultData.put("resultMessage", resultMessage);

        //형식 변경 금지
        return resultData;
    }

    @PostMapping(value="/connection-check.json")
    @Operation(summary = "로그인 시 접속 정보 관련 내용 검증 API", description = "<h2>로그인 시 접속 정보 관련 내용 검증 API</h2>" +
            "<h3>여기에 return 되는 값으로 cntnLog 데이터 저장</h3>", parameters = {
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject(
                            """
                              {
                                  "uuid": "codebsample001-t",
                                  "result": 0,
                                  "classid": 24707,
                                  "claId": "76c9781644f846c4af6bdecf6af56e82",
                                  "name": "codebsample001-t",
                                  "id": 402415,
                                  "userDiv": "T"
                              }
                            """
                    ))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
            @ApiResponse(responseCode = "500", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"result\": \"999\",\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    public Map<String, Object> connectionCheck (HttpServletRequest request
    ) throws Exception {

        Map<String,Object> resultData = AidtCommonUtil.getUserConnectionInfo(request);

        String resultMessage = "(Socket) 회원 공통 API - 사용자 접속 정보";
        resultData.put("resultMessage", resultMessage);

        return resultData;
    }

    /**
     * 비밀번호 등록/초기화 + 만료일 설정
     * - path: /api/internal/user-auth-init.json
     * - params:
     *   - userId (required)
     *   - pwd (optional, 미입력 시 userId + "123!@#")
     *   - accountExpireDt (optional, 예: "2025-12-31 23:59:59" 또는 ISO-8601. 미입력 시 올해 말 23:59:59)
     */
    @Loggable
    @PostMapping(value = "/user-auth-init", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDTO<CustomBody> userAuthInit(
            @RequestParam("userId") String userId,
            @RequestParam(value = "pwd", required = false) String pwd,
            @RequestParam(value = "accountExpireDt", required = false) String accountExpireDt
    ) {
        if (StringUtils.isEmpty(userId)) {
            return AidtCommonUtil.makeResultFail(null, null, "userId required");
        }

        String rawPassword = StringUtils.defaultIfEmpty(pwd, userId + "123!@#");

        Map<String, Object> result = new HashMap<>();
        try {
            result = memberService.upsertPassword(userId, rawPassword, accountExpireDt);
        } catch (NoSuchAlgorithmException e) {
            log.error("[{}] {} hash parsing error - {}", userId, pwd, e.getMessage());
        } catch (Exception e) {
            log.error("[{}] {} etc error - {}", userId, pwd, e.getMessage());
        }
        if (MapUtils.getInteger(result, "result") > 0) {
            return AidtCommonUtil.makeResultFail(null, result, "user auth initialized/updated");
        } else {
            return AidtCommonUtil.makeResultSuccess(null, result, "user auth initialized/updated");
        }
    }

    private LocalDateTime parseOrDefaultEndOfYear(String accountExpireDt, ZoneId zone) {
        if (StringUtils.isEmpty(accountExpireDt)) {
            LocalDate last = LocalDate.now(zone).with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
            return last.atTime(23, 59, 59);
        }
        // 허용 포맷: ISO-8601 / "yyyy-MM-dd HH:mm:ss" / "yyyy-MM-dd" / "yyyyMMddHHmmss" / "yyyyMMdd"
        List<DateTimeFormatter> fmts = Arrays.asList(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
                DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        for (DateTimeFormatter f : fmts) {
            try {
                if (f == DateTimeFormatter.ISO_LOCAL_DATE) {
                    return LocalDate.parse(accountExpireDt, f).atTime(23, 59, 59);
                }
                LocalDateTime ldt = LocalDateTime.parse(accountExpireDt.replace("T", " "), f);
                return ldt;
            }
             catch (DateTimeParseException ex) {
                log.error("{} parse error - {}", accountExpireDt, ex.getMessage());
            } catch (RuntimeException ex) {
                log.error("{} runtime error - {}", accountExpireDt, ex.getMessage());
            }
            catch (Exception ignore) {
                log.error("{} ignore error - {}", accountExpireDt, ignore.getMessage());
            }
        }
        // 파싱 실패 시 올해 말 23:59:59
        LocalDate last = LocalDate.now(zone).with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
        return last.atTime(23, 59, 59);
    }

}
