package com.visang.aidt.lms.api.textbook.controller;


import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.textbook.service.TextbookService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * (교과서) 커리큘럼 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(교과서) 커리큘럼 API", description = "(교과서) 커리큘럼 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TextbookController {

	private final TextbookService textbookService;

	/**
	 * 교과서 커리큘럼 목록 조회
	 *
	 * @param textbookIndexId
	 * @param paramData
	 * @return
	 */
	@Loggable
	@RequestMapping(value = "/textbook/crcu/list", method = {RequestMethod.GET})
	@Operation(summary = "교과서 커리큘럼 목록 조회", description = "")
	@Parameter(name = "textbookIndexId", description = "교과서 목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
	@Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "1"))
	public ResponseDTO<CustomBody> textbookCrcuList(
			@RequestParam(name = "textbookIndexId",   defaultValue = "1") long textbookIndexId,
			@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

		List<Map<String, Object>> resultData = textbookService.getTextbookCrcuList(paramData);
		return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서 커리큘럼 목록 조회");
	}

	/**
	 * 메타(aidt_lcms.meta) 테이블에 저장되어 있는 교과과정 커리큘럼 목록 조회
	 *
	 * @param textbookIndexId
	 * @param paramData
	 * @return
	 */
	@Loggable
	@RequestMapping(value = "/textbook/meta/crcu/list", method = {RequestMethod.GET})
	@Operation(summary = "교과서 학습맵 커리큘럼 목록 조회", description = "")
	@Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
	public ResponseDTO<CustomBody> textbookCrcuListByMeta(
			@RequestParam(name = "textbookId",   defaultValue = "1") long textbookIndexId,
			@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

		List<Map<String, Object>> resultData = textbookService.getTextbookCrcuListByMeta(paramData);
		return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서 교과과정 목록 조회");
	}

	/**
	 * 메타(aidt_lcms.meta) 테이블에 저장되어 있는 교과과정 커리큘럼 목록 조회
	 *
	 * @param textbookIndexId
	 * @param paramData
	 * @return
	 */
	@Loggable
	@RequestMapping(value = "/textbook/meta/crcu/list/eng", method = {RequestMethod.GET})
	@Operation(summary = "교과서 학습맵 커리큘럼 목록 조회 (영어)", description = "")
	@Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1189"))
	public ResponseDTO<CustomBody> textbookCrcuListByMetaEng(
			@RequestParam(name = "textbookId",   defaultValue = "1") long textbookIndexId,
			@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

			paramData.put("displayEngYn", "Y");
		List<Map<String, Object>> resultData = textbookService.getTextbookCrcuListByMeta(paramData);
		return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서 교과과정 목록 조회 (영어)");
	}

	/**
	 * 메타(aidt_lcms.meta) 테이블에 저장되어 있는 교과과정 커리큘럼 목록 조회
	 * - [영어]의 경우 특정 차시만 노출되도록 처리
	 *
	 * @param textbookIndexId
	 * @param paramData
	 * @return
	 */
	@Loggable
	@RequestMapping(value = "/textbook/meta/crcu/spc-list", method = {RequestMethod.GET})
	@Operation(summary = "교과서 학습맵 커리큘럼 목록 조회 (특정 차시만 노출)", description = "")
	@Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
	public ResponseDTO<CustomBody> textbookCrcuSpcListByMeta(
			@RequestParam(name = "textbookId",   defaultValue = "1") long textbookIndexId,
			@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

		paramData.put("displayYn", "Y"); // 노출여부가 Y인것만 조회

		List<Map<String, Object>> resultData = textbookService.getTextbookCrcuListByMeta(paramData);
		return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서 교과과정 목록 조회 (특정 차시만 노출)");
	}

}
