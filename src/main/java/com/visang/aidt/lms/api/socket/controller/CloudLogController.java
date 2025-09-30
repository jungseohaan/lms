package com.visang.aidt.lms.api.socket.controller;

import com.visang.aidt.lms.api.repository.entity.CloudLog;
import com.visang.aidt.lms.api.socket.service.CloudLogService;
import com.visang.aidt.lms.global.vo.socket.SocketExceptionBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/")
@RequiredArgsConstructor
@Tag(name = "(Socket) CloudLog API", description = "소켓 필수 API (공통 log API)")
public class CloudLogController {
	private final Environment environment;
	private final CloudLogService cloudLogService;

	@GetMapping(value="/writelog.json")
    @Operation(summary = "로그 기록", description = "로그 기록 API", parameters = {
            @Parameter(name = "message", description = "에러 메시지", required = true, schema = @Schema(type = "string", example = "테스트")),
            @Parameter(name = "code", description = "에러 코드 값", required = true, schema = @Schema(type = "integer", example = "999")),
            @Parameter(name = "logDiv", description = "APP, SERVER", required = true, schema = @Schema(type = "string", example = "APP")),
            @Parameter(name = "level", description = "DEBUG, INFO, WARN, ERROR", required = true, schema = @Schema(type = "string", example = "ERROR")),
            @Parameter(name = "userDiv", description = "유저 구분값(T-선생, S-학생)", required = true, schema = @Schema(type = "string", example = "T")),
            @Parameter(name = "user_idx", description = "회원 IDX", required = true, schema = @Schema(type = "integer", example = "1111")),
			@Parameter(name = "serverName", description = "서버 이름", required = true, schema = @Schema(type = "string", example = "AIDT")),
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    examples = @ExampleObject("""
                            {"result": 0,"access_key_id": "123456789","settings": [{"groupNm": "Display Mode",
                            "code": "CC0101","classSettingsIdx": "242","useYn": "Y","groupCd": "CC01"},{"groupNm": "Background Image","code": "CC0501",
                            "files": [{"filePath": "","classSettingsIdx": "274","orderNum": "11","selected": "Y"},{"filePath": "","classSettingsIdx": "334",
                            "orderNum": "12","selected": "N"}],"groupCd": "CC05"}],"classid": "644","secret_access_key": "12345"}""")
            )),
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
	@ResponseBody
	public Map<String, Object> writeLog(
			@RequestParam final String message,
			@RequestParam final Integer code,
			@RequestParam final String logDiv,
			@RequestParam final String level,
			@RequestParam(value="userDiv", required = false) final String userDiv,
			@RequestParam(value="user_idx", required = false) final Integer user_idx,
			@RequestParam(value="serverName", required = false) final String serverName
	) throws Exception {

		Map<String, Object> resultMap = new HashMap<>();

		CloudLog cloudLog = new CloudLog();
		cloudLog.setUserIdx(ObjectUtils.defaultIfNull(user_idx, 0));
		cloudLog.setUserDiv(StringUtils.upperCase(userDiv));
		cloudLog.setLogCode(ObjectUtils.defaultIfNull(code, 0));
		cloudLog.setMessage(HtmlUtils.htmlUnescape(message));
		cloudLog.setServerName(serverName);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = sdf.format(new Date());
		cloudLog.setRegDate(currentDate);

		// 로그레벨 정리
		if (StringUtils.isNotEmpty(level)) {
			cloudLog.setLogLevel(NumberUtils.toInt(environment.getProperty("logs.level." + level.toLowerCase())));
		}
		// 로그 형태 정리
		if (StringUtils.isNotEmpty(logDiv)) {
			cloudLog.setLogDiv(NumberUtils.toInt(environment.getProperty("logs.div." + logDiv.toLowerCase())));
		}
		// 에러면 튕김
		if(resultMap.containsKey("result") && resultMap.get("result").equals(999)) {
			return resultMap;
		} else { // 에러가 아니면 저장
			cloudLogService.insertWriteLog(cloudLog); // 로그 저장
		}

		resultMap.put("result", 0);

		return resultMap;
	}

	@GetMapping(value="/updatelog.json")
    @Operation(summary = "로그 기록", description = "로그 기록 API", parameters = {
			@Parameter(name = "idx", description = "log pk", required = true, schema = @Schema(type = "integer", example = "1")),
            @Parameter(name = "message", description = "에러 메시지", required = true, schema = @Schema(type = "string", example = "테스트")),
            @Parameter(name = "code", description = "에러 코드 값", required = true, schema = @Schema(type = "integer", example = "999")),
            @Parameter(name = "logDiv", description = "APP, SERVER", required = true, schema = @Schema(type = "string", example = "APP")),
            @Parameter(name = "level", description = "DEBUG, INFO, WARN, ERROR", required = true, schema = @Schema(type = "string", example = "ERROR")),
            @Parameter(name = "userDiv", description = "유저 구분값(T-선생, S-학생)", required = true, schema = @Schema(type = "string", example = "T")),
            @Parameter(name = "user_idx", description = "회원 IDX", required = true, schema = @Schema(type = "integer", example = "1111")),
			@Parameter(name = "serverName", description = "서버 이름", required = true, schema = @Schema(type = "string", example = "AIDT")),
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    examples = @ExampleObject("""
                            {"result": 0,"access_key_id": "123456789","settings": [{"groupNm": "Display Mode",
                            "code": "CC0101","classSettingsIdx": "242","useYn": "Y","groupCd": "CC01"},{"groupNm": "Background Image","code": "CC0501",
                            "files": [{"filePath": "","classSettingsIdx": "274","orderNum": "11","selected": "Y"},{"filePath": "","classSettingsIdx": "334",
                            "orderNum": "12","selected": "N"}],"groupCd": "CC05"}],"classid": "644","secret_access_key": "12345"}""")
            )),
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
	@ResponseBody
	public Map<String, Object> updateLog(
			@RequestParam final Integer idx,
			@RequestParam final String message,
			@RequestParam final Integer code,
			@RequestParam final String logDiv,
			@RequestParam final String level,
			@RequestParam(value="userDiv", required = false) final String userDiv,
			@RequestParam(value="user_idx", required = false) final Integer user_idx,
			@RequestParam(value="serverName", required = false) final String serverName
	) throws Exception {

		Map<String, Object> resultMap = new HashMap<>();

		CloudLog cloudLog = new CloudLog();
		cloudLog.setUserIdx(ObjectUtils.defaultIfNull(user_idx, 0));
		cloudLog.setUserDiv(StringUtils.upperCase(userDiv));
		cloudLog.setLogCode(ObjectUtils.defaultIfNull(code, 0));
		cloudLog.setMessage(HtmlUtils.htmlUnescape(message));
		cloudLog.setServerName(serverName);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = sdf.format(new Date());
		cloudLog.setRegDate(currentDate);

		// 로그레벨 정리
		if (StringUtils.isNotEmpty(level)) {
			cloudLog.setLogLevel(NumberUtils.toInt(environment.getProperty("logs.level." + level.toLowerCase())));
		}
		// 로그 형태 정리
		if (StringUtils.isNotEmpty(logDiv)) {
			cloudLog.setLogDiv(NumberUtils.toInt(environment.getProperty("logs.div." + logDiv.toLowerCase())));
		}
		// 에러면 튕김
		if(resultMap.containsKey("result") && resultMap.get("result").equals(999)) {
			return resultMap;
		} else { // 에러가 아니면 저장
			cloudLogService.updateWriteLog(cloudLog, idx); // 로그 저장
		}

		resultMap.put("result", 0);

		return resultMap;
	}

	@GetMapping(value="/readlog.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "로그 기록", description = "로그 기록 API", parameters = {
			@Parameter(name = "search_sdate", description = "검색 시작 일자", schema = @Schema(type = "string", example = "2020-12-01")),
            @Parameter(name = "search_edate", description = "검색 종료 일자", schema = @Schema(type = "string", example = "2020-12-31")),
			@Parameter(name = "search_str", description = "검색문자열", schema = @Schema(type = "string", example = "API")),
            @Parameter(name = "logDiv", description = "APP, SERVER", schema = @Schema(type = "string", example = "APP")),
            @Parameter(name = "level", description = "DEBUG, INFO, WARN, ERROR", schema = @Schema(type = "string", example = "ERROR")),
            @Parameter(name = "userDiv", description = "유저 구분값(T-선생, S-학생)", schema = @Schema(type = "string", example = "T")),
            @Parameter(name = "user_id", description = "회원 ID", schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000")),
			@Parameter(name = "serverName", description = "서버 이름", schema = @Schema(type = "string", example = "AIDT")),
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(
                    examples = @ExampleObject("""
                            {"result": 0,"access_key_id": "123456789","settings": [{"groupNm": "Display Mode",
                            "code": "CC0101","classSettingsIdx": "242","useYn": "Y","groupCd": "CC01"},{"groupNm": "Background Image","code": "CC0501",
                            "files": [{"filePath": "","classSettingsIdx": "274","orderNum": "11","selected": "Y"},{"filePath": "","classSettingsIdx": "334",
                            "orderNum": "12","selected": "N"}],"groupCd": "CC05"}],"classid": "644","secret_access_key": "12345"}""")
            )),
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
	@ResponseBody
	public Map<String, Object> readLog(@RequestParam Map<String, Object> requestMap) throws Exception {

		Map<String, Object> resultMap = new HashMap<>();

		// 로그레벨 정리
		String level = MapUtils.getString(requestMap, "level");
		if (StringUtils.isNotEmpty(level)) {
			requestMap.put("level", NumberUtils.toInt(environment.getProperty("logs.level." + level.toLowerCase())));
		}
		// 로그 형태 정리
		String logDiv = MapUtils.getString(requestMap, "logDiv");
		if (StringUtils.isNotEmpty(logDiv)) {
			requestMap.put("logDiv", NumberUtils.toInt(environment.getProperty("logs.div." + logDiv.toLowerCase())));
		}

		List<CloudLog> list = cloudLogService.getLogList(requestMap); // 로그 조회

		resultMap.put("result", 0);
		resultMap.put("list", list);

		return resultMap;
	}
}
