package com.visang.aidt.lms.api.openApi.admin.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.openApi.admin.service.TableSchemaService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(시스템공통) 테이블스키마 조회", description = "(시스템공통) 테이블스키마 조회")
@RequiredArgsConstructor
@RequestMapping(value = "/schema", produces = MediaType.APPLICATION_JSON_VALUE)
public class TableSchemaController {

	@Value("${spring.profiles.active}")
    private String serverEnv;

    private final TableSchemaService tableSchemaService;

    @Value("${key.salt.prefix}")
    private String keySaltPrefix;

    @Value("${key.salt.suffix:}")
    private String keySaltSuffix;

    @Loggable
	@CrossOrigin(origins = "https://t-vivamon.aidtclass.com")
    @RequestMapping(value = "/table-column-group-list", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "컬럼 그룹 정보가 포함된 테이블 목록 조회", description = "front-end 에서 user_check 테이블을 기반으로 한 write log 연동 데이터 조회")
    @Parameter(name = "tableSchema", description = "테이블스키마(콤마 구분자)", required = false, schema = @Schema(type = "string", example = "aidt_lcms,aidt_lms"))
    @Parameter(name = "tableName", description = "테이블 명(like 검색)", required = false, schema = @Schema(type = "string", example = ""))
	@Parameter(name = "token", description = "token", required = true, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> tableColumnGroupList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

		// 로컬이 아닐 때만 토큰 처리
		if (StringUtils.equals(serverEnv, "local") == false) {
			String token = MapUtils.getString(paramData, "token");
			if (StringUtils.isEmpty(token)) {
				return AidtCommonUtil.makeResultFail(paramData, null, "컬럼 그룹 정보가 포함된 테이블 목록 조회 - token empty");
			}

			String tableSchema = MapUtils.getString(paramData, "tableSchema", "");
			String tableName = MapUtils.getString(paramData, "tableName", "");

			// 현재 시간을 기준으로 시작
			LocalDateTime endTime = LocalDateTime.now();

			// 원하는 날짜 형식 (yyyyMMddHH)
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

			// 5분 간격으로 5분 전부터 현재까지 생성
			boolean isTokenCheck = false;
			for (int i = 0; i <= 5; i++) {  // 5번 (5분 이내 호출만 허용)
				String time = endTime.format(formatter);
				String tokenKey = "(" + tableSchema + "|" + tableName + time + ")";
				String checksum = CommonUtils.encryptString(tokenKey);
				if (StringUtils.equals(token, checksum)) {
					isTokenCheck = true;
					break;
				}
				endTime = endTime.minusMinutes(1);  // 1분 전으로 이동
			}

			if (isTokenCheck == false) {
				return AidtCommonUtil.makeResultFail(paramData, null, "컬럼 그룹 정보가 포함된 테이블 목록 조회 - token 인증 실패");
			}
		}

        Object resultData = tableSchemaService.selectTableColumnGroupList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "컬럼 그룹 정보가 포함된 테이블 목록 조회");

    }

    @Loggable
	@CrossOrigin(origins = "https://t-vivamon.aidtclass.com")
    @RequestMapping(value = "/table-column-info-list", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "테이블의 컬럼 정보 목록 조회", description = "front-end 에서 user_check 테이블을 기반으로 한 write log 연동 데이터 조회")
    @Parameter(name = "tableSchema", description = "테이블스키마", required = true, schema = @Schema(type = "string", example = "aidt_lms"))
    @Parameter(name = "tableName", description = "테이블 명", required = true, schema = @Schema(type = "string", example = ""))
	@Parameter(name = "token", description = "token", required = true, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> cloudLog(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

		// 로컬이 아닐 때만 토큰 처리
		if (StringUtils.equals(serverEnv, "local") == false) {
			String token = MapUtils.getString(paramData, "token");
			if (StringUtils.isEmpty(token)) {
				return AidtCommonUtil.makeResultFail(paramData, null, "테이블의 컬럼 정보 목록 조회 - token empty");
			}

			String tableSchema = MapUtils.getString(paramData, "tableSchema", "");
			String tableName = MapUtils.getString(paramData, "tableName", "");

			// 현재 시간을 기준으로 시작
			LocalDateTime endTime = LocalDateTime.now();

			// 원하는 날짜 형식 (yyyyMMddHH)
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

			// 5분 간격으로 5분 전부터 현재까지 생성
			boolean isTokenCheck = false;
			for (int i = 0; i <= 5; i++) {  // 5번 (5분 이내 호출만 허용)
				String time = endTime.format(formatter);
				String tokenKey = "(" + tableSchema + "|" + tableName + time + ")";
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

        Object resultData = tableSchemaService.selectTableColumnInfoList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "테이블의 컬럼 정보 목록 조회");

    }
}
