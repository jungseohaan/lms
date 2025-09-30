package com.visang.aidt.lms.api.operation.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.operation.service.PopUpService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.operation.controller
 * fileName : PopUpController
 * USER : leejh16
 * date : 2025-02-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2025-02-25      leejh16          최초 생성
 */
@Slf4j
@RestController
@Tag(name = "Operation 관리자 페이지 연동 - 공지사항 > 팝업 API", description = "Operation 관리자 페이지 연동 - 공지사항 > 팝업 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PopUpController {

    private final PopUpService popUpService;

    @Loggable
    @RequestMapping(value = "/operation/customer-support/popup-summary", method = {RequestMethod.GET})
    @Operation(summary = "Operation AIDT Admin 에서 관리하고 있는 팝업 정보 조회", description = "")
    @Parameter(name = "exposTrgtCd", description = "교사 학생 구분 값 (T|S)", required = false, schema = @Schema(type = "string", example = "T"))
    @Parameter(name = "brandId", description = "브랜드 구분(1|3)", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "exposPstnCd", description = "노출위치\n파라미터를 보내주지 않으면 1을 기본 값으로 사용합니다.\n" +
            "    id: 1, value: 홈\n" +
            "    id: 2, value: 교과서\n" +
            "    id: 3, value: 과제\n" +
            "    id: 4, value: 평가\n" +
            "    id: 5, value: 수업자료실\n" +
            "    id: 6, value: 스스로학습", required = false, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> stntHomewkInfo(
            @RequestParam(name = "exposTrgtCd", defaultValue = "") String exposTrgtCd,
            @RequestParam(name = "exposPstnCd", defaultValue = "1") Integer exposPstnCd,
            @RequestParam(name = "brandId", defaultValue = "1") Integer brandId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = popUpService.getPopUpSummary(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 정보 조회");

    }
}
