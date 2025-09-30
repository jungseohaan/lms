package com.visang.aidt.lms.api.common.excel.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import com.visang.aidt.lms.api.common.excel.resource.ExcelSheet;
import com.visang.aidt.lms.api.common.excel.resource.Lesson;
import com.visang.aidt.lms.api.common.excel.resource.Student;
import com.visang.aidt.lms.api.common.excel.resource.Unit;
import com.visang.aidt.lms.api.common.excel.service.ExcelDownloadService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/excel/download")
@RequiredArgsConstructor
@Tag(name = "Excel 다운로드", description = "Excel 다운로드 API")
public class ExcelDownloadController {

    private final ExcelDownloadService excelDownloadService;

    @Loggable
    @GetMapping(value = "/achievement")
    @Operation(summary = "성취기준별 학습 현황 Excel 다운로드", description = "성취기준별 학습 현황을 Excel 파일로 다운로드합니다.")
    @Parameter(name = "templateType", description = "템플릿 유형 코드", required = true, schema = @Schema(type = "string", example = "TCH_MATH_ACHIEVEMENT_STANDARD"))
    public ModelAndView downloadAchievementExcel(
            @Parameter(hidden = true) @RequestParam Map<String, Object> param
    ) throws Exception {

        String templateTypeStr = MapUtils.getString(param, "templateType");
        ExcelTemplateType templateType = ExcelTemplateType.fromCode(templateTypeStr);

        List<LinkedHashMap<Object, Object>> data  = excelDownloadService.getAchievementData(templateType, param);
        AbstractXlsxView view = excelDownloadService.getExcelTemplate(templateType);

        String filename = String.format("%s.xlsx", templateType.getDescription());
        ModelAndView mav = new ModelAndView();
        mav.setView(view);
        mav.addObject("filename", filename);
        mav.addObject("data", data);

        return mav;
    }


    @Loggable
    @GetMapping(value = "/unit-summary")
    @Operation(summary = "단원별 총평 Excel 다운로드", description = "단원별 총평 데이터를 Excel 파일로 다운로드합니다.")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "860936a514f04169afa154459758f944"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1201"))
    @Parameter(name = "metaIds", description = "단원 ID", required = true, schema = @Schema(type = "list", example = "[2264, 2270]"))
    @Parameter(name = "downloadType", description = "다운로드 유형", required = true, schema = @Schema(type = "list", example = "subject"))
    public ModelAndView downloadUnitSummaryExcel(
            @Parameter(hidden = true) @RequestParam Map<String, Object> param,
            @Parameter(hidden = true) @RequestParam List<String> metaIds,
            @Parameter(hidden = true) @RequestParam List<String> downloadType
    ) throws Exception {
        String templateTypeStr = "TCH_META_NAME_AI_GENRVW";
        ExcelTemplateType templateType = ExcelTemplateType.fromCode(templateTypeStr);

        param.put("metaIds", metaIds);
        param.put("downloadType", downloadType);


        List<ExcelSheet> data = excelDownloadService.getExcelData(param);

        AbstractXlsxView view = excelDownloadService.getExcelTemplate(templateType);

        ModelAndView mav = new ModelAndView();
        String filename = String.format("%s.xlsx", templateType.getDescription());
        mav.setView(view);
        mav.addObject("filename", filename);
        mav.addObject("data", data);

        return mav;
    }

    @GetMapping(value = "/unit-summary/data/exist")
    @Operation(summary = "단원별 총평 Excel 다운로드 확인", description = "단원별 총평 데이터가 존재하는지 확인합니다.")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "860936a514f04169afa154459758f944"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1201"))
    @Parameter(name = "metaIds", description = "단원 ID", required = true, schema = @Schema(type = "list", example = "[2264, 2270]"))
    @Parameter(name = "downloadType", description = "다운로드 유형", required = true, schema = @Schema(type = "list", example = "subject"))
    public Object existUnitSummary(
            @Parameter(hidden = true) @RequestParam Map<String, Object> param,
            @Parameter(hidden = true) @RequestParam List<String> metaIds,
            @Parameter(hidden = true) @RequestParam List<String> downloadType
    ) throws Exception {
        param.put("metaIds", metaIds);
        param.put("downloadType", downloadType);


        List<ExcelSheet> data = excelDownloadService.getExcelData(param);
        Map<String, Object> result = new HashMap<>();
        if (data.stream().allMatch(excelSheet -> excelSheet.getUnits() == null || excelSheet.getUnits().isEmpty())) {
            // Map으로 리턴 (ResponseDTO가 아니므로 AOP에서 건드리지 않음)
            result.put("downloadDataExists", false);
            result.put("resultMessage", "NO_DATA");
        } else {
            result.put("downloadDataExists", true);
            result.put("resultMessage", "EXIST_DATA");
        }

        return AidtCommonUtil.makeResultSuccess(param, result, "단원별 총평 Excel 다운로드 확인");
    }

    @Loggable
    @GetMapping(value = "/units")
    @Operation(summary = "단원 정보 조회", description = "단원 정보를 조회합니다.")
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1201"))
    public ResponseDTO<CustomBody> getUnits(
            @Parameter(hidden = true) @RequestParam Map<String, Object> param
    ) {
        Integer textbookId = MapUtils.getInteger(param, "textbookId");
        // 테스트 데이터 생성
        Object resultData = excelDownloadService.getUnits(textbookId);

        return AidtCommonUtil.makeResultSuccess(param, resultData, "단원 목록 조회");
    }
}