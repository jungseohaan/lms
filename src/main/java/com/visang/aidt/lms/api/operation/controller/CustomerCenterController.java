package com.visang.aidt.lms.api.operation.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.operation.dto.NoticeInfoDto;
import com.visang.aidt.lms.api.operation.service.CustomerCenterService;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.operation.controller
 * fileName : CustomerCenterController
 * USER : shinhc1
 * date : 2025-07-03
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2025-07-03      shinhc1          최초 생성
 */
@Slf4j
@RestController
@Tag(name = "vivamon 관리자 페이지 연동 - 공지사항 / 자주 하는 질문", description = "vivamon 관리자 페이지 연동 - 공지사항 / 자주 하는 질문")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerCenterController {

    private final CustomerCenterService customerCenterService;

    @Loggable
    @RequestMapping(value = "/vivamon/customer-notice", method = {RequestMethod.GET})
    @Operation(summary = "Vivamon AIDT Admin 에서 관리하고 있는 공지사항 정보 조회", description = "")
    @Parameter(name = "exposTrgtCd", description = "교사 학생 구분 값 (T|S)", required = false, schema = @Schema(type = "string", example = "T"))
    @Parameter(name = "schoolLevelCd", description = "학교급 값 (A: 전체, E: 초등, M: 중등, H : 고등)", required = false, schema = @Schema(type = "string", example = "E"))
    @Parameter(name = "brandId", description = "브랜드ID (A: 전체, E: 영어, M: 수학)", required = false, schema = @Schema(type = "string", example = "E"))
    @Parameter(name = "search", description = "검색 (제목 컨텐츠 기중)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "페이징", required = false, schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "pageSize", description = "페이지 값 (10, 20, 30)", required = false, schema = @Schema(type = "int", example = "10"))
    public ResponseDTO<CustomBody> customerNotice(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = customerCenterService.getCustomerNotice(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "공지사항 정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/vivamon/customer-notice/{noticeId}", method = {RequestMethod.GET})
    @Operation(summary = "Vivamon AIDT Admin 에서 관리하고 있는 공지사항 정보 조회", description = "")
    @Parameter(name = "noticeId", description = "공지사항아이디", required = false, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> customerNoticeDtl(
            @PathVariable(name = "noticeId") String noticeId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("noticeId",noticeId);
        Map<String, Object> resultData = customerCenterService.getCustomerNoticeDtl(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "공지사항 상세정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/vivamon/customer-faq", method = {RequestMethod.GET})
    @Operation(summary = "Vivamon AIDT Admin 에서 관리하고 있는 자주하는질문 정보 조회", description = "")
    @Parameter(name = "exposTrgtCd", description = "교사 학생 구분 값 (T|S)", required = false, schema = @Schema(type = "string", example = "T"))
    @Parameter(name = "schoolLevelCd", description = "학교급 값 (A: 전체, E: 초등, M: 중등, H : 고등)", required = false, schema = @Schema(type = "string", example = "E"))
    @Parameter(name = "brandId", description = "브랜드ID (A: 전체, E: 영어, M: 수학)", required = false, schema = @Schema(type = "string", example = "E"))
    @Parameter(name = "category", description = "분류 (common: 공통 , assignment: 과제/평가, home: 홈)", required = false, schema = @Schema(type = "string", example = "common"))
    @Parameter(name = "search", description = "검색 (제목 컨텐츠 기중)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "페이징", required = false, schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "pageSize", description = "페이지 값 (10, 20, 30)", required = false, schema = @Schema(type = "int", example = "10"))
    public ResponseDTO<CustomBody> customerFaq(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = customerCenterService.getCustomerFaq(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자주하는질문 정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/vivamon/customer-faq/{faqId}", method = {RequestMethod.GET})
    @Operation(summary = "Vivamon AIDT Admin 에서 관리하고 있는 자주하는질문 정보 조회", description = "")
    @Parameter(name = "faqId", description = "faqId", required = false, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> customerFaqDtl(
            @PathVariable(name = "faqId") String faqId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("faqId",faqId);
        Map<String, Object> resultData = customerCenterService.getCustomerFaqDtl(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자주하는질문 상세정보 조회");

    }


    @Loggable
    @RequestMapping(value = "/vivamon/customer-category", method = {RequestMethod.GET})
    @Operation(summary = "자주하는질문 메뉴 정보 조회", description = "")
    public ResponseDTO<CustomBody> getCustomerCodeList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = customerCenterService.getCustomerCodeList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자주하는질문 상세정보 조회");

    }

}
