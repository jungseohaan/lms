package com.visang.aidt.lms.api.openApi.user.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.openApi.common.dto.ResponseDto;
import com.visang.aidt.lms.api.openApi.user.service.UserService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 유저 API Controller
 */
@Slf4j
@RestController(value = "openApiUserController")
@Tag(name = "OpenAPI (유저) API", description = "OpenAPI 유저 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Value("${spring.profiles.active}")
    private String serverEnv;

    private final UserService userService;

    @Value("${key.salt.prefix}")
    private String keySaltPrefix;

    @Value("${key.salt.suffix:}")
    private String keySaltSuffix;

    @Loggable
    @GetMapping(value = "/v1/user/info")
    @Operation(summary = "유저 정보 조회", description = "")
    @Parameter(name = "userId", description = "유저 ID",  required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "userType", description = "유저 Type", required = true, schema = @Schema(type = "string", example = "T"))
    public ResponseDto userInfo(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "userType") String userType,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = userService.findUserInfo(paramData);

        boolean success = MapUtils.getBoolean(resultData, "success", true);
        if (success) {
            return ResponseDto.returnSuccess(resultData);
        } else {
            return ResponseDto.returnFail();
        }
    }

    @Loggable
    @GetMapping(value = "/v1/partner/info")
    @Operation(summary = "파트너 정보 조회", description = "")
    @Parameter(name = "curriSchool", description = "학교급",  required = true, schema = @Schema(type = "string", example = "elementary"))
    @Parameter(name = "curriSubject", description = "교과목", required = true, schema = @Schema(type = "string", example = "mathematics"))
    @Parameter(name = "curriGrade", description = "학년",  required = true, schema = @Schema(type = "string", example = "grade03"))
    @Parameter(name = "curriSemester", description = "학기",  required = true, schema = @Schema(type = "string", example = "semester01"))
    @Parameter(name = "curriBook", description = "커리큘럼",  required = true, schema = @Schema(type = "string", example = "0021"))
    public ResponseDto partnerInfo(
            @RequestParam(name = "curriSchool") String curriSchool,
            @RequestParam(name = "curriSubject") String curriSubject,
            @RequestParam(name = "curriGrade") String curriGrade,
            @RequestParam(name = "curriSemester") String curriSemester,
            @RequestParam(name = "curriBook") String curriBook,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

        Map<String, Object> resultData = userService.findPartnerInfo(paramData);

        boolean success = MapUtils.getBoolean(resultData, "success", true);
        if (success) {
            return ResponseDto.returnSuccess(resultData);
        } else {
            return ResponseDto.returnFail();
        }
    }

    @Loggable
    @GetMapping(value = "/v1/teacher/classInfo")
    @Operation(summary = "교사 반 정보 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID",  required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    public ResponseDto teacherClassInfo(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "claId") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = userService.findTeacherClassInfo(paramData);

        boolean success = MapUtils.getBoolean(resultData, "success", true);
        if (success) {
            return ResponseDto.returnSuccess(resultData);
        } else {
            return ResponseDto.returnFail();
        }
    }

    @Loggable
    @GetMapping(value = "/v1/teacher/classMemberInfo")
    @Operation(summary = "교사 학생 정보 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID",  required = true, schema = @Schema(type = "string", example = "532e8642-e29b-41d4-a746-446655441253"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    public ResponseDto teacherClassMemberInfo(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "claId") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

        Map<String, Object> userParam = new HashMap<>();
        userParam.put("userId", userId);
        userParam.put("claId", claId);

        Map<String, Object> resultData = userService.findTeacherClassMemberInfo(paramData);
        // user info 에서 try catch로 빠지지 않는 버그 상황에 대한 fail 처리 추가
        boolean success = MapUtils.getBoolean(resultData, "success", true);
        if (success) {
            return ResponseDto.returnSuccess(resultData);
        } else {
            return ResponseDto.returnFail();
        }
    }


    @Loggable
    @PostMapping(value = "/v1/user/add")
    @Operation(summary = "유저 정보 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "userInfo", value = """
                            {
                                "userList" : [
                                    {
                                        "userId" : "test1-t",
                                        "userType" : "T",
                                        "partnerId" : "0"
                                    },
                                    {
                                        "userId" : "test1-s1",
                                        "userType" : "S",
                                        "partnerId" : "0"
                                    }
                                ],
                                "tcRegInfo" : {
                                    "userId" : "test1-t",
                                    "userStatus" : "E",
                                    "schlCd" : "EEE00E",
                                    "schlNm" : "비상초등학교",
                                    "year" : "2024"
                                },
                                "tcClaInfo" : {
                                    "userId" : "test1-t",
                                    "claId" : "EEE00E",
                                    "year" : "2024",
                                    "smt" : 1,
                                    "schlNm" : "비상초등학교",
                                    "gradeCd" : 4,
                                    "claNm" : "1반"
                                },
                                "tcClaMbList" : [
                                    {
                                        "userId" : "test1-t",
                                        "claId" : "EEE00E",
                                        "stdtId" : "test1-s1",
                                        "year" : "2024",
                                        "smt" : 1,
                                        "schlNm" : "비상초등학교",
                                        "gradeCd" : 4,
                                        "claNm" : "1반"
                                    },
                                    {
                                        "userId" : "test1-t",
                                        "claId" : "EEE00E",
                                        "stdtId" : "test1-s2",
                                        "year" : "2024",
                                        "smt" : 1,
                                        "schlNm" : "비상초등학교",
                                        "gradeCd" : 4,
                                        "claNm" : "1반"
                                    }
                                ],
                                "stdtRegList" : [
                                    {
                                        "userId" : "test1-s1",
                                        "userStatus" : "E",
                                        "schlCd" : "EEE00E",
                                        "schlNm" : "비상초등학교",
                                        "year" : "2024",
                                        "gradeCd" : 4
                                    },
                                    {
                                        "userId" : "test1-s2",
                                        "userStatus" : "E",
                                        "schlCd" : "EEE00E",
                                        "schlNm" : "비상초등학교",
                                        "year" : "2024",
                                        "gradeCd" : 4
                                    }
                                ]
                            }
                            """
                    )
            }
            ))
    public ResponseDto userAdd(@RequestBody Map<String, Object> param)  {
        try {
            userService.addUserInfo(param);
            return ResponseDto.returnSuccess(null);
        } catch (Exception e) {
            log.error("userAdd error : {}", e.getMessage());
            return ResponseDto.returnFail();
        }
    }

    @Loggable
    @CrossOrigin(origins = "https://t-vivamon.aidtclass.com")
    @RequestMapping(value = "/updt-user-stts", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "유저 상태값 수동 업데이트", description = "소켓 배포시 유저 일괄 로그아웃 처리")
    @Parameter(name = "token", description = "token", required = true, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> updtUserStts(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        // 로컬이 아닐 때만 토큰 처리
        if (StringUtils.equals(serverEnv, "local") == false) {
            String token = MapUtils.getString(paramData, "token");
            if (StringUtils.isEmpty(token)) {
                return AidtCommonUtil.makeResultFail(paramData, null, "테이블의 컬럼 정보 목록 조회 - token empty");
            }

            // 현재 시간을 기준으로 시작
            LocalDateTime endTime = LocalDateTime.now();

            // 원하는 날짜 형식 (yyyyMMddHH)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

            // 5분 간격으로 5분 전부터 현재까지 생성
            boolean isTokenCheck = false;
            for (int i = 0; i <= 5; i++) {  // 5번 (5분 이내 호출만 허용)
                String time = endTime.format(formatter);
                String tokenKey = "(user_stts_proc" + time + ")jenkins";
                String checksum = CommonUtils.encryptSaltString(keySaltPrefix, keySaltSuffix, tokenKey);
                if (StringUtils.equals(token, checksum)) {
                    isTokenCheck = true;
                    break;
                }
                endTime = endTime.minusMinutes(1);  // 1분 전으로 이동
            }

            if (isTokenCheck == false) {
                return AidtCommonUtil.makeResultFail(paramData, null, "테이블의 컬럼 정보 목록 조회 - token 인증 실패");
            }
        }

        Object resultData = userService.updateUserLogOut();
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "테이블의 컬럼 정보 목록 조회");

    }
}
