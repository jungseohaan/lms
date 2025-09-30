package com.visang.aidt.lms.api.selflrn.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.selflrn.service.SelfLrnReportService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@Slf4j
@Tag(name = "리포트 스스로학습 API", description = "리포트 스스로학습 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SelfLrnReportController {

    private final SelfLrnReportService selfLrnReportService;

    @Loggable
    @RequestMapping(value = "/tch/report/self-lrn/statist", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습 현황", description = "스스로학습 현황")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값이 비어있으면, 전체에 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    public ResponseDTO<CustomBody> findReportSelfLrnStatis(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findReportSelfLrnStatis(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로학습 현황");
    }

    @Loggable
    @RequestMapping(value = "/tch/report/self-lrn/list", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습 학생 목록 (수학)", description = "스스로학습 학생 목록 (수학)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = false, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값이 비어있으면, 전체에 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    public ResponseDTO<CustomBody> fidnReportSelfLrnList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("brandId",1);
        Object resultData = selfLrnReportService.fidnReportSelfLrnList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로학습 학생 목록 (수학)");
    }

    @Loggable
    @RequestMapping(value = "/tch/report/self-lrn/list/high/eng", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습 학생 목록 (중고등-영어)", description = "스스로학습 학생 목록 (중고등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = false, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값이 비어있으면, 전체에 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    public ResponseDTO<CustomBody> fidnReportSelfLrnListHighEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("brandId",3);
        paramData.put("curriSchool","high");
        Object resultData = selfLrnReportService.fidnReportSelfLrnList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로학습 학생 목록 (중고등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/tch/report/self-lrn/list/elementary/eng", method = {RequestMethod.GET})
    @Operation(summary = "스스로학습 학생 목록 (초등-영어)", description = "스스로학습 학생 목록 (초등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = false, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값이 비어있으면, 전체에 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    public ResponseDTO<CustomBody> fidnReportSelfLrnListElementaryEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("brandId",3);
        paramData.put("curriSchool","elementary");
        Object resultData = selfLrnReportService.fidnReportSelfLrnList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "스스로학습 학생 목록 (초등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/statist", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 현황 (수학)", description = "(학생) 스스로학습 현황 (수학)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    public ResponseDTO<CustomBody> findStntReportSelfLrnStatis(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("brandId",1);
        Object resultData = selfLrnReportService.findStntReportSelfLrnStatis(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 현황 (수학)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/statist/high/eng", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 현황 (중고등-영어)", description = "(학생) 스스로학습 현황 (중고등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면, 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    public ResponseDTO<CustomBody> findStntReportSelfLrnStatishighEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("brandId",3);
        paramData.put("curriSchool","high");
        Object resultData = selfLrnReportService.findStntReportSelfLrnStatis(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 현황 (중고등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/statist/elementary/eng", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 현황 (초등-영어)", description = "(학생) 스스로학습 현황 (초등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면, 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    public ResponseDTO<CustomBody> findStntReportSelfLrnStatisElementaryEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("brandId",3);
        paramData.put("curriSchool","elementary");
        Object resultData = selfLrnReportService.findStntReportSelfLrnStatis(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 현황 (초등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/unit/statist", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 단원별 현황 (수학)", description = "(학생) 스스로학습 단원별 현황 (수학)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "unitNum", description = "unitNum", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면, 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    @Parameter(name = "status", description = "상태", required = false, schema = @Schema(type = "integer", example = ""))
    public ResponseDTO<CustomBody> findStntReportSelfLrnUnitStatis(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findStntReportSelfLrnUnitStatis(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 단원별 현황 (수학)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/unit/statist/high/eng", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 단원별 현황 (중고등-영어)", description = "(학생) 스스로학습 단원별 현황 (중고등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "unitNum", description = "unitNum", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면, 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    @Parameter(name = "status", description = "상태", required = false, schema = @Schema(type = "integer", example = ""))
    public ResponseDTO<CustomBody> findStntReportSelfLrnUnitStatisHighEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findStntReportSelfLrnUnitStatisHighEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 단원별 현황 (중고등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/unit/statist/elementary/eng", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 단원별 현황 (초등-영어)", description = "(학생) 스스로학습 단원별 현황 (초등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "unitNum", description = "unitNum", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면, 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    @Parameter(name = "status", description = "상태", required = false, schema = @Schema(type = "integer", example = ""))
    public ResponseDTO<CustomBody> findStntReportSelfLrnUnitStatisElementaryEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findStntReportSelfLrnUnitStatisElementaryEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 단원별 현황 (초등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/list", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 목록 (수학)", description = "(학생) 스스로학습 목록 (수학)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "slfAt", description = "slfAt", required = true, schema = @Schema(type = "string", example = "Y"))
    @Parameter(name = "unitNum", description = "단원 별 조회 조건, 기본 값 -1 (전체), 각 단원의 1이상의 숫자 ", required = false, schema = @Schema(type = "integer", example = "-1", defaultValue = "-1"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "12"))
    public ResponseDTO<CustomBody> findStntReportSelfLrnList(
            @Parameter(hidden = true) @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findStntReportSelfLrnList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 목록 (수학)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/list/high/eng", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 목록 (중고등-영어)", description = "(학생) 스스로학습 목록 (중고등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "slfAt", description = "slfAt", required = true, schema = @Schema(type = "string", example = "Y"))
    @Parameter(name = "unitNum", description = "단원 별 조회 조건, 기본 값 -1 (전체), 각 단원의 1이상의 숫자 ", required = false, schema = @Schema(type = "integer", example = "-1", defaultValue = "-1"))
    @Parameter(name = "aiStdCd", description = "스스로 학습 AI Speaking (1), AI Writing (2) 구분 코드 값, 기본 단원은 -1", required = false, schema = @Schema(type = "integer", example = "-1"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면, 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "12"))
    public ResponseDTO<CustomBody> findStntReportSelfLrnListHighEng(
            @Parameter(hidden = true) @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findStntReportSelfLrnListHighEng(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 목록 (중고등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/list/elementary/eng", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 스스로학습 목록 (초등-영어)", description = "(학생) 스스로학습 목록 (초등-영어)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "a498e4c7d7634773b147b5de262ba762"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1400-s2"))
    @Parameter(name = "slfAt", description = "slfAt", required = true, schema = @Schema(type = "string", example = "Y"))
    @Parameter(name = "unitNum", description = "단원 별 조회 조건, 기본 값 -1 (전체), 각 단원의 1이상의 숫자 ", required = false, schema = @Schema(type = "integer", example = "-1", defaultValue = "-1"))
    @Parameter(name = "aiStdCd", description = "스스로 학습 AI Speaking (1), AI Writing (2) 구분 코드 값, 기본 단원은 -1", required = false, schema = @Schema(type = "integer", example = "-1"))
    @Parameter(name = "dwm", description = "검색 날짜 유형(해당 값 비어있으면, 전체 해당)", required = false, schema = @Schema(type = "string", example = "w"))
    @Parameter(name = "dDate", description = "일yyyymmdd", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "wDateSt", description = "주yyyymmdd시작일", required = false, schema = @Schema(type = "string", example = "20250330"))
    @Parameter(name = "wDateEd", description = "주yyyymmdd종료일", required = false, schema = @Schema(type = "string", example = "20250405"))
    @Parameter(name = "mDate", description = "월yyyymm", required = false, schema = @Schema(type = "string", example = "202504"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "12"))
    public ResponseDTO<CustomBody> findStntReportSelfLrnListElementaryEng(
            @Parameter(hidden = true) @PageableDefault(size = 12) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findStntReportSelfLrnListElementaryEng(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 스스로학습 목록 (초등-영어)");
    }

    @Loggable
    @RequestMapping(value = "/tch/report/self-lrn/newat", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 스스로학습 현황 (읽음 처리)", description = "(교사) 스스로학습 현황 (읽음 처리)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "textbkId": 1150,
                    "claId": "a498e4c7d7634773b147b5de262ba762",
                    "stntId": "engbook1400-s2",
                    "dwm": "w",
                    "dDate": "20250405",
                    "wDateSt": "20250327",
                    "wDateEd": "20250330",
                    "mDate": "202504"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> modifyTchReportSelfLrnNewAt(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.modifyTchReportSelfLrnNewAt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 스스로학습 현황 (읽음 처리)");
    }

    @Loggable
    @RequestMapping(value = "/stnt/report/self-lrn/chapter/list", method = {RequestMethod.GET})
    @Operation(summary = "리포트 스스로 학습 단원 및 AI 목록", description = "수학/영어 스스로 학습 단원 목록과 영어 AI Speaking, AI Writing 목록 조회")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150"))
    public ResponseDTO<CustomBody> findStntReportSelfLrnChapterList(
        @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = selfLrnReportService.findStntReportSelfLrnChapterList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "리포트 스스로 학습 단원 목록");
    }
}
