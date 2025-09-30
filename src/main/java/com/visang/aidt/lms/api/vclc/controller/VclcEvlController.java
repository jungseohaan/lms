package com.visang.aidt.lms.api.vclc.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.vclc.service.VclcEvlService;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vclc/report/eval", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "[비바샘](런처) 평가 API", description = "(런처) 평가 API")
public class VclcEvlController {

    private final VclcEvlService vclcEvlService;

    @Loggable
    @GetMapping("/tch/summary")
    @Operation(summary = "초등 교사 런처 홈 최근 평가 현황 Summary", description = "비바샘 런처 최근 평가 Summary 호출")
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "class456"))
    public ResponseDTO<CustomBody> getLatsEvalReportSummary(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = vclcEvlService.getLastEvalReportSummary(paramData);

        if (MapUtils.getBoolean(resultData, "result", false).equals(false)) {
            return AidtCommonUtil.makeResultFail(paramData, resultData, resultData.get("message").toString());
        }

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, " 최근 평가 현황 Summary");
    }


    // 학생 ID(userId)만으로 모든 알림 조회
    @Loggable
    @RequestMapping(value = "/stnt/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 목록 조회 확인용", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook732-s1" ))
    
    //필수값 X
    @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "44e701f580a34e17b2a030a02527dc4a" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID",schema = @Schema(type = "integer", example = "1522"))
    @Parameter(name = "condition", description = "검색 유형", schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "evlSttsCd", description = "평가 상태 : 전체/예정(1)/진행중(2)/완료(3)", schema = @Schema(type = "string", allowableValues = {"", "1", "2", "3"}, example = ""))
    @Parameter(name = "page", description = "page", schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "submAt", description = "학생의 평가 제출 여부 : Y, N")
    public ResponseDTO<CustomBody> stntEvalList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = vclcEvlService.findEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 목록 조회");
    }
}
