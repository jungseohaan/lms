package com.visang.aidt.lms.api.integration.controller;

import com.visang.aidt.lms.api.integration.service.IntegTextbkService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(연동) 교과서 API", description = "(연동) 교과서 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class IntegTextbkController {

    private final IntegTextbkService integTextbkService;

    @RequestMapping(value = "/integ/textbk/list", method = {RequestMethod.GET})
    @Operation(summary = "교과서 목록 조회", description = "")
    @Parameter(name = "brandId", description = "브랜드 ID", required = false, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "curriSchool", description = "학교급", required = false, schema = @Schema(type = "string", example = "elementary"))
    @Parameter(name = "curriSubject", description = "과목", required = false, schema = @Schema(type = "string", example = "mathematics"))
    @Parameter(name = "curriYear", description = "년도", required = false, schema = @Schema(type = "string", example = "2022"))
    @Parameter(name = "curriGrade", description = "학년", required = false, schema = @Schema(type = "string", example = "grade04"))
    @Parameter(name = "curriSemester", description = "학기", required = false, schema = @Schema(type = "string", example = "semester01"))
    @Parameter(name = "curriBook", description = "교과과정", required = false, schema = @Schema(type = "string", example = "VS106414"))
    public ResponseDTO<CustomBody> listTextbkInfo(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integTextbkService.listTextbkInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서 목록 조회");
    }

    @RequestMapping(value = "/integ/textbk/meta/crcu/list", method = {RequestMethod.GET})
    @Operation(summary = "교과서 학습맵 커리큘럼 목록 조회", description = "")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    public ResponseDTO<CustomBody> listTextbkCrcuListByMeta( @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> resultData = integTextbkService.listTextbkCrcuListByMeta(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서 학습맵 커리큘럼 목록 조회");
    }

}
