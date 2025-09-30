package com.visang.aidt.lms.api.dashboard.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.StntDsbdService;
import com.visang.aidt.lms.api.kafka.service.KafkaBatchService;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
//@Api(tags = "(학생) 대시보드")
@Tag(name = "(학생) 대시보드", description = "(학생) 대시보드")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntDsbdController {

    private StntDsbdService stntDsbdService;
    private KafkaBatchService kafkaBatchService;

    @Loggable
    @RequestMapping(value = "/stnt/dashboard/report/total", method = {RequestMethod.GET})
    @Operation(summary = "(학생)종합리포트", description = "(학생)종합리포트")
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "smstr", description = "학기", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> getStntDsbdReportTotal(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        Object resultData = stntDsbdService.getStntDsbdReportTotal(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생)종합리포트");
    }

    //    @ApiOperation(value = "클래스룸 정보", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/classinfo")
    public ResponseDTO<CustomBody> stntDsbdClassinfo() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdClassinfo(paramData);
        String resultMessage = "클래스룸 정보";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "달력(주간)", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/weekinfo")
    public ResponseDTO<CustomBody> stntDsbdWeekinfo() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdWeekinfo(paramData);
        String resultMessage = "달력(주간)";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "실시간 우리반 알림", notes = "")
    @GetMapping(value = "/stnt/dsbd/ntcn/class")
    public ResponseDTO<CustomBody> stntDsbdNtcnClass() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdNtcnClass(paramData);
        String resultMessage = "실시간 우리반 알림";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "시간표", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/schedule")
    public ResponseDTO<CustomBody> stntDsbdSchedule() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdSchedule(paramData);
        String resultMessage = "시간표";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "단원별 이해도", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/understand/chapter")
    public ResponseDTO<CustomBody> stntDsbdUnderstandChapter() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdUnderstandChapter(paramData);
        String resultMessage = "단원별 이해도";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "개념별 이해도", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/understand/concept")
    public ResponseDTO<CustomBody> stntDsbdUnderstandConcept() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdUnderstandConcept(paramData);
        String resultMessage = "개념별 이해도";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }



    //    @ApiOperation(value = "지식맵", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/knowledgemap")
    public ResponseDTO<CustomBody> stntDsbdKnowledgemap() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdKnowledgemap(paramData);
        String resultMessage = "지식맵";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }



    //    @ApiOperation(value = "영역별 이해도", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/understand/domain")
    public ResponseDTO<CustomBody> stntDsbdUnderstandDomain() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdUnderstandDomain(paramData);
        String resultMessage = "영역별 이해도";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "과제 평가 현황", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/status/eval")
    public ResponseDTO<CustomBody> stntDsbdStatusEval() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdStatusEval(paramData);
        String resultMessage = "과제 평가 현황";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "자기주도 학습 현황", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/status/selflearning")
    public ResponseDTO<CustomBody> stntDsbdStatusSelflearning() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdSelflearning(paramData);
        String resultMessage = "자기주도 학습 현황";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }



    //    @ApiOperation(value = "자기주도 학습 현황 상세", notes = "")
    @Loggable
    @GetMapping(value = "/stnt/dsbd/status/selflearning/detail")
    public ResponseDTO<CustomBody> stntDsbdStatusSelflearningDetail() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntDsbdService.findStntDsbdSelflearningDetail(paramData);
        String resultMessage = "자기주도 학습 현황 상세";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/self-lrn/chapter/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 자기주도학습 현황", description = "(학생)종합리포트")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "searchDt", description = "조회년월", required = true, schema = @Schema(type = "string", example = "202403"))
    public ResponseDTO<CustomBody> stntDsbdStatusSelflrnChapterList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        Object resultData = stntDsbdService.findStntDsbdStatusSelflrnChapterList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 자기주도학습 현황");


    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/chapter-unit/list", method = {RequestMethod.GET})
    @Operation(summary = "단원별 이해도", description = "'학생 대시보드 화면의 각 단원별 학생 이해도의 증가/감소 정보를 표시한다.")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook1644-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "3af91dd7bde84083bc4c415fc7052daa"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1342"))
    public ResponseDTO<CustomBody> stntDsbdStatusChapterunitList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        //kafkaBatchService.processSelectOneCycle(paramData);

        Object resultData = stntDsbdService.findStntDsbdStatusChapterunitList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "단원별 이해도");


    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/chapter-unit/detail", method = {RequestMethod.GET})
    @Operation(summary = "단원별 이해도 상세", description = "'학생 대시보드 화면의 각 단원별 학생 이해도 계산에 사용된 수업, 과제, 평가, 자기주도학습의 학습 정보를 표시한다.")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathqa1-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0ff1492c808a4659ba204562393cad6e"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1198"))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "string", example = "1560"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "stdDt", description = "stdDt", required = false, schema = @Schema(type = "string", example = "20240730"))
    public ResponseDTO<CustomBody> stntDsbdStatusAreausdDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.findStntDsbdStatusChapterunitDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "단원별 이해도 상세");


    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/area-usd/list", method = {RequestMethod.GET})
    @Operation(summary = "영역별 이해도", description = "학생 대시보드 화면의 각 학생의 영역별 이해도(단원별)를 기반으로 이해도 정보를 표시한다.")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu467"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "13"))
    public ResponseDTO<CustomBody> getStntDsbdStatusAreausdList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        Object resultData = stntDsbdService.getStntDsbdStatusAreausdList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "영역별 이해도");


    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/area-usd/detail", method = {RequestMethod.GET})
    @Operation(summary = "영역별 이해도 상세", description = "학생 대시보드 화면의 각 학생의 영역별 이해도(단원별)를 기반으로 이해도 정보를 표시한다.")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu467"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "13"))
    @Parameter(name = "areaId", description = "영역 ID", required = true, schema = @Schema(type = "string", example = "1559"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> getStntDsbdStatusAreausdDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.getStntDsbdStatusAreausdDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "영역별 이해도 상세");


    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/self-lrn/chapter/detail", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 자기주도학습 현황(상세)", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "searchDt", description = "조회년월일", required = true, schema = @Schema(type = "string", example = "20240305"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> stntDsbdStatusSelflrnChapterDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.findStntDsbdStatusSelflrnChapterDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 자기주도학습 현황(상세)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/homewk/list", method = {RequestMethod.GET})
    @Operation(summary = "과제_평가 현황 (과제)", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student51"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772671"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "5"))
    public ResponseDTO<CustomBody> stntDsbdStatusHomewkList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.findStntDsbdStatusHomewkList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제_평가 현황 (과제)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "과제_평가 현황 (평가)", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student51"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772671"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "5"))
    public ResponseDTO<CustomBody> stntDsbdStatusEvalList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.findStntDsbdStatusEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제_평가 현황 (평가)");

    }

    // 자기주도AI학습 결과 보기 (챗봇)
    // 자기주도학습 -> 스스로 학습으로 용어 변경(11/27)
    @Loggable
    @GetMapping("/stnt/dsbd/status/aitutor/result")
    @Operation(summary = "스스로학습 AI 결과 보기(챗봇)", description = "스스로학습 AI 결과 보기(챗봇)")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "textBkId", description = "교과서 ID", required = true)
    @Parameter(name = "claId", description = "클래스 ID", required = true)
    @Parameter(name = "learningType", description = "학습 유형 / 1 : 전체, 2 : 평가, 3 : 과제, 4 : 스스로 학습", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "learningDate", description = "학습일", required = false, schema = @Schema(type = "string", example = "20240319"))
    @Parameter(name = "brandId", description = "브랜드 ID / 1 : aidt수학, 3 : aidt영어", required = true)
    @Parameter(name = "stdId", description = "스스로학습 ID", required = false)
    @Parameter(name = "searchChtCn", description = "스스로학습 AI 학습 채팅 내용 검색 조건", required = false)
    public ResponseDTO<CustomBody> findStntSelfLrnAitutorResultSummary(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = stntDsbdService.findAitutorResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI튜터 결과 보기(챗봇)");
    }

    // 자기주도AI학습 결과 보기 (AI학습내용)
    @Loggable
    @GetMapping("/stnt/dsbd/status/aitutor/lrn-result")
    @Operation(summary = "자기주도AI학습 결과 보기(AI학습내용)", description = "자기주도AI학습 결과 보기(AI학습내용)")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "textBkId", description = "교과서 ID", required = true)
    @Parameter(name = "claId", description = "클래스 ID", required = true)
    @Parameter(name = "learningDate", description = "학습일", required = true, schema = @Schema(type = "string", example = "20240530"))
    public ResponseDTO<CustomBody> findAitutorLrnResult(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = stntDsbdService.findAitutorLrnResult(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도AI학습 결과 보기(AI학습내용)");
    }

    // [학생] 학급관리 > 홈 대시보드 > 영역별 그래프
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/area-achievement/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)", description = "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectStntDsbdAreaAchievementList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdAreaAchievementList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)");

    }

    // [학생] 학급관리 > 홈 대시보드 > 영역별 그래프
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/area-achievement/listall", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)", description = "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectStntDsbdAreaAchievementListall(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        //kafkaBatchService.processSelectOneCycle(paramData);

        Object resultData = stntDsbdService.selectStntDsbdAreaAchievementListall(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)");

    }

    // [학생] 학급관리 > 홈 대시보드 > 영역별 단원별 목록
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/area-achievement/detail", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 영역별 단원별 목록 (영어)", description = "[학생] 학급관리 > 홈 대시보드 > 영역별 단원별 목록 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "code", description = "지표명", required = true, schema = @Schema(type = "string", example = "listening"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectStntDsbdAreaAchievementDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdAreaAchievementDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 영역별 단원별 목록 (영어)");

    }

    // [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세(학생의 성취도)
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/statistic/achievement", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 상세(학생의 성취도) (영어)", description = "'학습 학생의 성취도 추이를 확인한다")
    @Parameter(name = "tchId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vstea50-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "evaluationAreaCd", description = "지표명", required = true, schema = @Schema(type = "string", example = "listening"))
    public ResponseDTO<CustomBody> selectTchDsbdStatisticAchievement(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntDsbdService.selectStntDsbdStatisticAchievementList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 영역별 그래프 상세(학생의 성취도) (영어)");

    }

    // [학생] 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식
    @Loggable
    @GetMapping("/stnt/dsbd/status/study-map/languageFormat/list")
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식 (영어)", description = "학습맵 언어 형식 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook972-s4"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a84c86cf944f158e302875f8d32c48"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1189"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusStudyMapLanguageFormatList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        Object resultData = stntDsbdService.selectStntDsbdStudyMapLanguageFormatList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 - 언어 형식 (영어)");
    }

    // [학생] 학급관리 > 홈 대시보드 > 학습맵 > 의사소통
    @Loggable
    @GetMapping("/stnt/dsbd/status/study-map/communication/list")
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 학습맵 > 의사소통 (영어)", description = "학습맵 의사소통 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook972-s4"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a84c86cf944f158e302875f8d32c48"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1189"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusStudyMapCommunicationList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        Object resultData = stntDsbdService.selectStntDsbdStatusStudyMapCommunicationList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 - 의사소통 (영어)");
    }

    // [학생] 학급관리 > 홈 대시보드 > 학습맵 > 소재
    @Loggable
    @GetMapping("/stnt/dsbd/status/study-map/material/list")
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 학습맵 > 소재 (영어)", description = "학습맵 소재 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook972-s4"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a84c86cf944f158e302875f8d32c48"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1189"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusStudyMapMaterialList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        Object resultData = stntDsbdService.selectStntDsbdStatusStudyMapMaterialList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 - 소재 (영어)");
    }

    // [학생] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준
    @Loggable
    @GetMapping("/stnt/dsbd/status/study-map/achievementStandard/list")
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 (영어)", description = "학습맵 성취 기준 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook972-s4"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a84c86cf944f158e302875f8d32c48"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1189"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusStudyAchievementStandardList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {
        Object resultData = stntDsbdService.selectStntDsbdStatusStudyAchievementStandardList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 - 성취 기준 (영어)");
    }

    // [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/vocabulary/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 (영어)", description = "Vocabulary 그래프 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "sortGbCd", description = "정렬조건(A1:단어 오름차순, A2:단어 내림차순, B1:성취도 오름차순, B2:성취도 내림차순)", required = true, schema = @Schema(type = "string", example = "A1"))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusVocabularyList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdStatusVocabularyList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Vocabulary 그래프 (영어)");

    }

    // [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 단원별 상세
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/vocabulary/detail", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 단원별 상세 (영어)", description = "Vocabulary 단원별 상세 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "iemId", description = "항목 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusVocabularyDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable

    ) throws Exception {

        Object resultData = stntDsbdService.selectStntDsbdStatusVocabularyDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Vocabulary 단원별 상세 (영어)");

    }

    //[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/grammar/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 (영어)", description = "Grammar 그래프 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "sortGbCd", description = "정렬조건(A1:단어 오름차순, A2:단어 내림차순, B1:성취도 오름차순, B2:성취도 내림차순)", required = true, schema = @Schema(type = "string", example = "A1"))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusGrammarList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdStatusGrammarList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Grammar 그래프 (영어)");

    }

    //[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 단원별 상세
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/grammar/detail", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 단원별 상세 (영어)", description = "Grammar 단원별 상세 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vstea50"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "iemId", description = "항목 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusGrammarDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable

    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdStatusGrammarDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Grammar 단원별 상세 (영어)");

    }

    //[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/pronunciation/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 (영어)", description = "Pronunciation 그래프 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "sortGbCd", description = "정렬조건(A1:단어 오름차순, A2:단어 내림차순, B1:성취도 오름차순, B2:성취도 내림차순)", required = true, schema = @Schema(type = "string", example = "A1"))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusPronunciationList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception {

        Object resultData = stntDsbdService.selectStntDsbdStatusPronunciationList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Pronunciation 그래프 (영어)");

    }

    //[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 단원별 상세
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/pronunciation/detail", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 단원별 상세 (영어)", description = "Pronunciation 단원별 상세 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "vsstu506"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "iemId", description = "항목 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusPronunciationDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    ) throws Exception {

        Object resultData = stntDsbdService.selectStntDsbdStatusPronunciationDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Pronunciation 단원별 상세 (영어)");

    }

    // 가장 최근 수업 정보 및 요약 현황
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/summary" , method = {RequestMethod.GET})
    @Operation(summary = "가장 최근 수업 정보 및 요약 현황", description = "가장 최근 수업 정보 및 요약 현황")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "claId", description = "학급 ID", required = true)
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true)
    public ResponseDTO<CustomBody> selectTchDsbdSummary(
            @RequestParam(name = "userId", defaultValue = "mathbook2876-s1") String userId,
            @RequestParam(name = "claId", defaultValue = "d154cc1443c94d6e90d197439d1230cf") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1152") String textbookId
    )throws Exception  {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);

        Object resultData = stntDsbdService.selectTchDsbdSummary(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "가장 최근 수업 정보 및 요약 현황");

    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/study-map/math/ach-std/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 (수학)", description = "학습맵 성취 기준 (수학)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook497-s1"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1152" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "6f0039ec1ac94787846348d5ed478969" ))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "870" ))
    public ResponseDTO<CustomBody> selectStntDsbdStatusStudyMapMathAchievementStandardList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdStatusStudyMapMathAchievementStandardList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 - 성취 기준 (수학)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/area-achievement/student/distribution", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)", description = "[학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1192-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "a607f8e867844c3c86340256e6c12570"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    public ResponseDTO<CustomBody> selectStntDsbdAreaAchievementStudentDstribution(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntDsbdService.selectStntDsbdAreaAchievementStudentDstribution(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)");

    }

    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/unit-achievement/listall", method = {RequestMethod.GET})
    @Operation(summary = "[학생][영어] 학급관리 > 홈 대시보드 > 단원별 그래프", description = "[학생] 학급관리 > 홈 대시보드 > 단원별 그래프 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook229-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "49f37b12fe7f463785e38da824f212db"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "unitNum", description = "단원 ID", required = false, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectStntDsbdUnitAchievementListAll(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdUnitAchievementListAll(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 단원별 그래프 (영어)");

    }

    @Loggable
    @GetMapping(value = "/stnt/dsbd/status/chapter-usd/student/distribution")
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)", description = "[학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)")
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook497-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "6f0039ec1ac94787846348d5ed478969"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    public ResponseDTO<CustomBody> selectStntDsbdChapterUsdStudentDstribution(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntDsbdService.selectStntDsbdChapterUsdStudentDstribution(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)");
    }

    // [학생] 학급관리 > 홈 대시보드 > 의사소통 기능 > communication 단원별 상세 (초등 영어)
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/communication/detail", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 의사소통 기능 > communication 단원별 상세 (초등 영어)", description = "communication 단원별 상세 (초등 영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1118-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "cc5addad543944429aca826a8a75ad93"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "iemId", description = "항목 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    public ResponseDTO<CustomBody> selectStntDsbdStatusCommunicationDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntDsbdService.selectStntDsbdStatusCommunicationDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "communication 단원별 상세 (초등 영어)");

    }

    //[학생] 학급관리 > 홈 대시보드 > 의사소통 기능 > communication 단원별 영역 (초등 영어)
    @Loggable
    @RequestMapping(value = "/stnt/dsbd/status/communication/list", method = {RequestMethod.GET})
    @Operation(summary = "[학생] 학급관리 > 홈 대시보드 > 의사소통 기능 > communication 단원별 영역(초등 영어)", description = "communication 단원별 영역(초등 영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1118-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "cc5addad543944429aca826a8a75ad93"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    public ResponseDTO<CustomBody> selectStntDsbdStatusCommunicationList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception  {

        Object resultData = stntDsbdService.selectStntDsbdStatusCommunicationList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, " communication 단원별 영역 (초등 영어)");

    }

}
