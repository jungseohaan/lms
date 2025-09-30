package com.visang.aidt.lms.api.integration.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.integration.service.IntegExamService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(연동) 출제함 API", description = "(연동) 출제함 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class IntegExamController {

    private final IntegExamService integExamService;

    @RequestMapping(value = "/integ/exam/save", method = {RequestMethod.POST})
    @Operation(summary = "출제함 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "", value = """
                            {
                                "wrterId" : "mathbe101-t",
                                "textbkId" : 1152,
                                "setsId" : "912",
                                "examNm" : "출제테스트1"
                            }
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> insertExamBox(@RequestBody Map<String, Object> paramData)throws Exception {
        /// 파라미터 유효성 검증
        List<String> requiredParams = Arrays.asList("wrterId", "textbkId", "setsId", "examNm");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = integExamService.insertExamBox(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제함 저장");
    }

    @RequestMapping(value = "/integ/exam/delete", method = {RequestMethod.POST})
    @Operation(summary = "출제함 삭제", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "", value = """
                           {
                               "examIdList" : ["1", "2"]
                           }
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> deleteExamBox(@RequestBody Map<String, Object> paramData)throws Exception {
        Object resultData = integExamService.deleteExamBox(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제함 삭제");
    }

    @RequestMapping(value = "/integ/exam/update", method = {RequestMethod.POST})
    @Operation(summary = "출제함 수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "", value = """
                            {
                                "wrterId" : "mathbe101-t",
                                "textbkId" : 1152,
                                "setsId" : "912",
                                "examNm" : "출제테스트1",
                                "examId" : 2
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> updatetExamBox(@RequestBody Map<String, Object> paramData)throws Exception {
        Object resultData = integExamService.updateExamBox(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제함 수정");
    }

    @RequestMapping(value = "/integ/exam/list", method = {RequestMethod.GET})
    @Operation(summary = "출제함 목록 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    @Parameter(name = "textbkIds", description = "교과서ID", required = false, schema = @Schema(type = "array", example = "[1152, 1154]"))
    @Parameter(name = "keyword", description = "검색키워드", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "curriIdList", description = "교과정보", required = false, schema = @Schema(type = "string", example = "915,917,921"))
    @Parameter(name = "sortGbCd", description = "정렬조건", required = false, schema = @Schema(type = "integer", allowableValues = {"1", "2", "3"}, example = "1"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> listExamBoxInfo(@RequestParam(required = false) List<Integer> textbkIds, @Parameter(hidden = true) @RequestParam Map<String, Object> paramData, @PageableDefault(size = 10) Pageable pageable) throws Exception {
        String curriIdList = MapUtils.getString(paramData, "curriIdList", "");
        paramData.put("curriIdList", AidtCommonUtil.strToLongList(curriIdList));
        paramData.put("textbkIds", textbkIds);
        Object resultData = integExamService.listExamBoxInfo(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제함 목록 조회");
    }

    @Loggable
    @RequestMapping(value = "/integ/exam/info", method = {RequestMethod.GET})
    @Operation(summary = "출제함 상세조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "examId", description = "출제함 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getExamInfo(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        List<String> requiredParams = Arrays.asList("examId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        Object resultData = integExamService.getExamInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제함 상세조회");
    }

    @RequestMapping(value = "/integ/exam/hist", method = {RequestMethod.GET})
    @Operation(summary = "출제함 발행 이력 조회", description = "")
    @Parameter(name = "examId", description = "출제함 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "dateFrom", description = "시작 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-01-01"))
    @Parameter(name = "dateTo", description = "종료 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-12-31"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "pageable", hidden = true)
    public ResponseDTO<CustomBody> listExamBoxHistInfo( @Parameter(hidden = true) @RequestParam Map<String, Object> paramData, @PageableDefault(size = 10) Pageable pageable) throws Exception {
        Object resultData = integExamService.listExamBoxHist(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제함 발행 이력 조회");
    }


    @RequestMapping(value = "/integ/exam/textbk/list", method = {RequestMethod.GET})
    @Operation(summary = "출제이력 교과서 목록 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    public ResponseDTO<CustomBody> listTextbkInfo(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integExamService.listTextbkByExamHist(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제이력 교과서 목록 조회");
    }
}
