package com.visang.aidt.lms.api.dashboard.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.StntDsbdInfoService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * (학생) 대시보드 API Controller
 */
@Slf4j
@RestController
@Tag(name = "(학생) 대시보드", description = "(학생) 대시보드")
@AllArgsConstructor
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class StntDsbdInfoController {

    private final StntDsbdInfoService stntDsbdInfoService;

    // 개념별 이해도
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/concept-usd/list" , method = {RequestMethod.GET})
    @Operation(summary = "개념별 이해도", description = "개념별 이해도")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "claId", description = "학급 ID", required = true)
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true)
    @Parameter(name = "metaId", description = "단원 ID", required = true)
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = false)
    @Parameter(name = "allSrhYn", description = "전체 조회 여부", required = false)
    public ResponseDTO<CustomBody> selectTchDsbdConceptUsdList(
            @RequestParam(name = "userId", defaultValue = "vsstu467") String userId,
            @RequestParam(name = "claId", defaultValue = "1dfd6267b8fb11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "16") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "") String kwgMainId,
            @RequestParam(name = "allSrhYn", defaultValue = "N") String allSrhYn
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId.isEmpty()?"":metaId);
        paramData.put("kwgMainId", kwgMainId.isEmpty()?"":kwgMainId);
        paramData.put("allSrhYn", allSrhYn);

        Object resultData = stntDsbdInfoService.selectStntDsbdConceptUsdList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "개념별 이해도");

    }

    // 개념별 이해도 상세
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/concept-usd/detail" , method = {RequestMethod.GET})
    @Operation(summary = "개념별 이해도 상세", description = "개념별 이해도 상세")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "claId", description = "학급 ID", required = true)
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true)
    @Parameter(name = "metaId", description = "단원 ID", required = true)
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = false)
    @Parameter(name = "allSrhYn", description = "전체 조회 여부", required = false)
    @Parameter(name = "stdDt", description = "학습날짜", required = true)
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "5"))
    public ResponseDTO<CustomBody> selectTchDsbdConceptUsdDetail(
            @RequestParam(name = "userId", defaultValue = "vstea46") String userId,
            @RequestParam(name = "claId", defaultValue = "308ad54bba8f11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "1559") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "") String kwgMainId,
            @RequestParam(name = "stdDt", defaultValue = "20240313") String stdDt,
            @RequestParam(name = "allSrhYn", defaultValue = "N") String allSrhYn,
            @Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId);
        paramData.put("kwgMainId", kwgMainId);
        paramData.put("stdDt", stdDt);
        paramData.put("allSrhYn", allSrhYn);

        Object resultData = stntDsbdInfoService.selectStntDsbdConceptUsdDetail(paramData,pageable);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "개념별 이해도 상세");

    }

    //학습맵 이해도
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/study-map/list", method = {RequestMethod.GET})
    @Operation(summary = "학습맵 이해도", description = "학습맵 이해도")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu467"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad483ba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "metaId", description = "단원 ID", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> selectTchDsbdChptUnitInfo(
            @RequestParam(name = "userId", defaultValue = "vsstu467") String userId,
            @RequestParam(name = "claId", defaultValue = "308ad483ba8f11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "") String kwgMainId
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();



        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId);
        paramData.put("kwgMainId", kwgMainId);

        Object resultData = stntDsbdInfoService.selectStntDsbdChptUnitInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 이해도");

    }

    //학습맵 이해도 (개념)
    //사용안함
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/study-map/concept", method = {RequestMethod.GET})
    @Operation(summary = "학습맵 이해도 (개념)", description = "학습맵 이해도 (개념)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu467"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad483ba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "string", example = "870"))
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = true, schema = @Schema(type = "string", example = "915"))
    public ResponseDTO<CustomBody> selectTchDsbdStdCncptUsdInfo(
            @RequestParam(name = "userId", defaultValue = "vsstu467") String userId,
            @RequestParam(name = "claId", defaultValue = "308ad483ba8f11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "870") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "915") String kwgMainId
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();



        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId);
        paramData.put("kwgMainId", kwgMainId);

        Object resultData = stntDsbdInfoService.selectStntDsbdStdCncptUsdInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 이해도(개념)");

    }

    //학습맵 이해도 (상세)
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/study-map/detail", method = {RequestMethod.GET})
    @Operation(summary = "학습맵 이해도 (상세)", description = "학습맵 이해도 (상세)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu467"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "string", example = "1559"))
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = true, schema = @Schema(type = "string", example = "1565"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "5"))
    public ResponseDTO<CustomBody> selectTchDsbdStdMapUsdInfo(
            @RequestParam(name = "userId", defaultValue = "vsstu467") String userId,
            @RequestParam(name = "claId", defaultValue = "308ad54bba8f11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "1559") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "1565") String kwgMainId,
            @Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();



        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId);
        paramData.put("kwgMainId", kwgMainId);

        Object resultData = stntDsbdInfoService.selectStntDsbdStdMapUsdInfo(paramData,pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 이해도 (상세)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/study-map/ind", method = {RequestMethod.GET})
    @Operation(summary = "성취기준 상세 조회_학생", description = "성취기준 상세 조회_학생")
    @Parameter(name = "userId", description = "userId", required = true, schema = @Schema(type = "string", example = "engreal21-s1"))
    @Parameter(name = "textbkId", description = "textbkId", required = true, schema = @Schema(type = "integer", example = "308"))
    @Parameter(name = "claId", description = "claId", required = true, schema = @Schema(type = "string", example = "8cdd444954404cfba4666767db51e967"))
    @Parameter(name = "unitNum", description = "unitNum", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "metaId", description = "metaId", required = true, schema = @Schema(type = "integer", example = "26908"))
    @Parameter(name = "studyMapCd", description = "studyMapCd", required = false, schema = @Schema(type = "string", example = "communication"))
    public ResponseDTO<CustomBody> findStntDsbdStatusStudyMapInd(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntDsbdInfoService.findStntDsbdStatusStudyMapInd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "성취기준 상세 조회_학생");

    }
}
