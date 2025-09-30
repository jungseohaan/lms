package com.visang.aidt.lms.api.apm.controller;

import com.visang.aidt.lms.api.apm.service.CsInquiryService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apm/cs/inquiry")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CS 문의", description = "고객 문의 관련 API")
public class CsInquiryController {

    private final CsInquiryService csInquiryService;


    @PostMapping(consumes = {"multipart/form-data"}, produces = {"application/json"})
    @Operation(summary = "문의 등록 및 재접수", description = "새로운 문의를 등록합니다")
    @Parameter(name = "inquiryId", description = "문의 ID", required = false, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "15debbc4e3ab4896ad37c87095171ab7"))
    @Parameter(name = "textbookId", description = "교재 ID", required = true, schema = @Schema(type = "string", example = "281"))
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "mathreal71-s5"))
    @Parameter(name = "userSeCd", description = "사용자 구분 코드", required = true, schema = @Schema(type = "string", example = "S"))
    @Parameter(name = "submitter", description = "제출자", required = true, schema = @Schema(type = "string", example = "문승민"))
    @Parameter(name = "phoneNumber", description = "전화번호", required = true, schema = @Schema(type = "string", example = "01000000000"))
    @Parameter(name = "email", description = "이메일", required = true, schema = @Schema(type = "string", example = "mathreal71-s5@visang.com"))
    @Parameter(name = "schoolName", description = "학교명", required = true, schema = @Schema(type = "string", example = "비상중학교"))
    @Parameter(name = "className", description = "반명", required = true, schema = @Schema(type = "string", example = "1반"))
    @Parameter(name = "subjectName", description = "과목명", required = true, schema = @Schema(type = "string", example = "수학"))
    @Parameter(name = "subjectCd", description = "과목 코드", required = true, schema = @Schema(type = "string", example = "mathcurri12"))
    @Parameter(name = "inquiryType", description = "문의 유형", required = true, schema = @Schema(type = "string", example = "M10"))
    @Parameter(name = "feedbackMethod", description = "피드백 방법", required = true, schema = @Schema(type = "string", example = "10"))
    @Parameter(name = "inquiryTitle", description = "문의 제목", required = true, schema = @Schema(type = "string", example = "문의제목"))
    @Parameter(name = "inquiryContent", description = "문의 내용", required = true, schema = @Schema(type = "string", example = "문의내용"))
    @Parameter(name = "systemResolution", description = "시스템 해상도", required = true, schema = @Schema(type = "string", example = "1900*800"))
    @Parameter(name = "privacyAgreementYn", description = "개인정보 동의 여부", required = true, schema = @Schema(type = "string", example = "Y"))
    public ResponseDTO<CustomBody> createInquiry(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramMap,
            HttpServletRequest request
    ) throws Exception {
        Map<String, Object> resultData = csInquiryService.insertInquiry(paramMap, files, request);

        boolean result = MapUtils.getBoolean(resultData, "success", false);
        String resultMsg = MapUtils.getString(resultData, "resultMsg", "");

        if (result) {
            return AidtCommonUtil.makeResultSuccess(paramMap, resultData, resultMsg);
        } else {
            return AidtCommonUtil.makeResultFail(paramMap, resultData, resultMsg);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "문의 목록 조회", description = "문의 목록을 조회합니다 (페이징 및 검색 지원)")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "mathreal71-s5"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "15debbc4e3ab4896ad37c87095171ab7"))
    @Parameter(name = "page", description = "페이지 번호", schema = @Schema(type = "integer", example = "1", defaultValue = "1"))
    @Parameter(name = "size", description = "페이지 크기", schema = @Schema(type = "integer", example = "10", defaultValue = "10"))
    @Parameter(name = "searchType", description = "검색 타입 (all, title, content, type)", schema = @Schema(type = "string", example = "all", defaultValue = "all"))
    @Parameter(name = "searchKeyword", description = "검색 키워드", schema = @Schema(type = "string", example = "문의"))
    public ResponseDTO<CustomBody> getInquiryList(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "claId") String claId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "searchType", defaultValue = "all") String searchType,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword
    ) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("claId", claId);
        paramMap.put("page", page);
        paramMap.put("size", size);
        paramMap.put("searchType", searchType);
        paramMap.put("searchKeyword", searchKeyword);
        
        Map<String, Object> resultData = new HashMap<>();
        Map<String, Object> resultMap = csInquiryService.getInquiryList(paramMap);

        if (MapUtils.getBoolean(resultMap, "success", false)) {
            resultData.put("inquiries", resultMap.get("inquiries"));
            resultData.put("pagination", resultMap.get("pagination"));

            return AidtCommonUtil.makeResultSuccess(paramMap, resultData, "문의 목록 조회 성공");
        } else {
            return AidtCommonUtil.makeResultFail(paramMap, resultData, MapUtils.getString(resultMap, "resultMsg", ""));
        }
    }

    @GetMapping("/detail")
    @Operation(summary = "문의 상세 조회 및 재문의", description = "특정 문의의 상세 정보를 조회합니다")
    @Parameter(name = "inquiryId", description = "접수 ID", required = true, schema = @Schema(type = "string", example = "21" ))
    @Parameter(name = "reopenedYn", description = "재문의 여부 (Y/N)", required = false, schema = @Schema(type = "string", example = "N" ))
    public ResponseDTO<CustomBody> getInquiryDetail(
            @RequestParam(name = "inquiryId", defaultValue = "") String inquiryId,
            @RequestParam(name = "reopenedYn", defaultValue = "N") String reopenedYn
    ) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("inquiryId", inquiryId);
        paramMap.put("reopenedYn", reopenedYn);

        Map<String, Object> inquiry = csInquiryService.getInquiryDetail(paramMap);

        // 서비스에서 오류 응답이 온 경우
        if (MapUtils.getBoolean(inquiry, "success", true) == false) {
            String errorMsg = MapUtils.getString(inquiry, "resultMsg", "문의 상세 조회 실패");
            return AidtCommonUtil.makeResultFail(paramMap, inquiry, errorMsg);
        }

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("inquiry", inquiry);

        return AidtCommonUtil.makeResultSuccess(paramMap, resultData, "문의 상세 조회 성공");
    }

    @PostMapping(value = "/update",consumes = "multipart/form-data",produces = "application/json")
    @Operation(summary = "문의 수정", description = "기존 문의를 수정합니다")
    @Parameter(name = "inquiryId", description = "문의 ID", required = true, schema = @Schema(type = "string", example = "21"))
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "mathreal71-s5"))
    @Parameter(name = "submitter", description = "제출자", required = true, schema = @Schema(type = "string", example = "문승민"))
    @Parameter(name = "phoneNumber", description = "전화번호", required = true, schema = @Schema(type = "string", example = "01000000000"))
    @Parameter(name = "email", description = "이메일", required = true, schema = @Schema(type = "string", example = "mathreal71-s5@visang.com"))
    @Parameter(name = "schoolName", description = "학교명", required = true, schema = @Schema(type = "string", example = "비상중학교"))
    @Parameter(name = "className", description = "반명", required = true, schema = @Schema(type = "string", example = "1반"))
    @Parameter(name = "subjectName", description = "과목명", required = true, schema = @Schema(type = "string", example = "수학"))
    @Parameter(name = "subjectCd", description = "과목 코드", required = true, schema = @Schema(type = "string", example = "mathcurri12"))
    @Parameter(name = "inquiryType", description = "문의 유형", required = true, schema = @Schema(type = "string", example = "M10"))
    @Parameter(name = "inquiryTitle", description = "문의 제목", required = true, schema = @Schema(type = "string", example = "문의제목"))
    @Parameter(name = "inquiryContent", description = "문의 내용", required = true, schema = @Schema(type = "string", example = "문의내용"))
    public ResponseDTO<CustomBody> updateInquiry(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramMap,
            HttpServletRequest request
    ) {
        Map<String, Object> resultData = csInquiryService.updateInquiry(paramMap, files, request);

        boolean result = MapUtils.getBoolean(resultData, "success", false);
        String resultMsg = MapUtils.getString(resultData, "resultMsg", "");

        if (result) {
            return AidtCommonUtil.makeResultSuccess(paramMap, resultData, resultMsg);
        } else {
            return AidtCommonUtil.makeResultFail(paramMap, resultData, resultMsg);
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "문의 삭제", description = "문의를 논리적으로 삭제합니다 (del_yn = 'Y')")
    @Parameter(name = "inquiryId", description = "문의 ID", required = true, schema = @Schema(type = "string", example = "21"))
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "mathreal71-s5"))
    public ResponseDTO<CustomBody> deleteInquiry(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramMap,
            HttpServletRequest request
    ) {
        Map<String, Object> resultData = csInquiryService.deleteInquiry(paramMap, request);

        boolean result = MapUtils.getBoolean(resultData, "success", false);
        String resultMsg = MapUtils.getString(resultData, "resultMsg", "");

        if (result) {
            return AidtCommonUtil.makeResultSuccess(paramMap, resultData, resultMsg);
        } else {
            return AidtCommonUtil.makeResultFail(paramMap, resultData, resultMsg);
        }
    }

    @GetMapping("/detail/codes")
    @Operation(summary = "공통 코드 및 서버 정보 조회", description = "문의 관련 공통 코드를 조회합니다")
    public ResponseDTO<CustomBody> getCommonCodes(HttpServletRequest request) {
        log.info("공통 코드 조회 요청");

        Map<String, Object> resultData = csInquiryService.getCommonCodes(request);

        boolean result = MapUtils.getBoolean(resultData, "success", false);
        String resultMsg = MapUtils.getString(resultData, "resultMsg", "");

        if (result) {
            return AidtCommonUtil.makeResultSuccess(new HashMap<>(), resultData, resultMsg);
        } else {
            return AidtCommonUtil.makeResultFail(new HashMap<>(), resultData, resultMsg);
        }
    }

}
