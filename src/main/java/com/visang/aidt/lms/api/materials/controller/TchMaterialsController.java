package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.materials.service.TchMaterialsServcie;
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

import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(교사) 내 자료 API", description = "(교사) 내 자료 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchMaterialsController {
    private final TchMaterialsServcie tchMaterialsServcie;

    @Loggable
    @RequestMapping(value = "/tch/materials/create", method = {RequestMethod.POST})
    @Operation(summary = "내 자료 등록", description = "내 자료를 등록한다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"setsId\":\"MSTG313026\"," +
                            "\"extLearnCntsId\":\"303\"," +
                            "\"userId\":\"newmath666-t\"," +
                            "\"saveType\":\"1\"," +
                            "\"eamMth\":\"2\"," +
                            "\"name\":\"[형성] 2025-08-05 14:02:07 저장됨111\"" +
                            "}"
                    )}
            ))
    public ResponseDTO<CustomBody> tchMaterialsCreate(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchMaterialsServcie.createMaterialsCreate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 자료 등록");
    }

    @Loggable
    @RequestMapping(value = "/tch/materials/list", method = {RequestMethod.GET})
    @Operation(summary = "내 자료 목록 조회", description = "내 자료의 목록을 조회한다.")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook3105-t"))
    @Parameter(name = "saveType", description = "저장구분", required = true, schema = @Schema(type = "string", allowableValues = {"1", "2", "3"}))
    @Parameter(name = "sortCode", description = "정렬조건", required = true, schema = @Schema(type = "string", allowableValues = {"1", "2", "3"}))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "페이지 번호", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지 크기", required = true, schema = @Schema(type = "integer", example = "12"))
    public ResponseDTO<CustomBody> tchMaterialsList(
            @Parameter(hidden = true) @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchMaterialsServcie.findMaterialsList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 자료 목록 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/materials/detail", method = {RequestMethod.GET})
    @Operation(summary = "내 자료 상세 조회", description = "내 자료의 상세 정보를 조회한다.")
    @Parameter(name = "tcMaterialsInfoId", description = "내 자료 ID", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchMaterialsDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchMaterialsServcie.findMaterialsDetail(paramData);

        // setSummary 검증 실패 시 실패 응답 반환
        if (!MapUtils.getBoolean(resultData, "resultOk", false)) {
            return AidtCommonUtil.makeResultFail(paramData, resultData, resultData.get("resultMsg").toString());
        }

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 자료 상세 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/materials/classtasks/create", method = {RequestMethod.POST})
    @Operation(summary = "출제한 반 이력 등록", description = "내 자료에서 출제 시, 반 이력 등록한다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"tcMaterialsInfoId\":\"1\"," +
                            "\"userId\":\"newmath666-t\"," +
                            "\"claId\":\"2ea0e6b81d244c1db6d529d468eadd22\"," +
                            "\"setCategory\":\"31\"," +
                            "\"trgetId\":\"2228565\"" +
                            "}"
                    )}
            ))
    public ResponseDTO<CustomBody> tchMaterialsClassTaksCreate (
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchMaterialsServcie.createMaterialsClassTaks(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제한 반 이력 등록");
    }

    @Loggable
    @RequestMapping(value = "/tch/materials/classtasks/list", method = {RequestMethod.GET})
    @Operation(summary = "출제한 반 이력 목록", description = "출제한 반의 이력에 대한 목록 조회한다.")
    @Parameter(name = "tcMaterialsInfoId", description = "내 자료 ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "page", description = "페이지 번호", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지 크기", required = true, schema = @Schema(type = "integer", example = "5"))
    public ResponseDTO<CustomBody> tchMaterialsClassTasksList(
            @Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchMaterialsServcie.findMaterialsClassTasksList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "출제한 반 이력 목록");
    }

    @Loggable
    @RequestMapping(value = "/tch/materials/update", method = {RequestMethod.POST})
    @Operation(summary = "내 자료의 타이틀 수정 및 내 자료 삭제", description = "내 자료의 타이틀 수정하고, 내 자료 삭제한다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"tcMaterialsInfoId\":11," +
                            "\"updateType\":1," +
                            "\"userId\":\"mathbook666-t\"," +
                            "\"name\":\"내 자료 테스트\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchMaterialsUpdate(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchMaterialsServcie.updateAndDeleteMaterials(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 자료의 타이틀 수정 및 내 자료 삭제");
    }
}
