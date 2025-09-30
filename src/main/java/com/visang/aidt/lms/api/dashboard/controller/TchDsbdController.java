package com.visang.aidt.lms.api.dashboard.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.TchDsbdService;
import com.visang.aidt.lms.api.kafka.service.KafkaBatchService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 대시보드 API Controller
 */
@Slf4j
@RestController
//@Api(tags = "(교사) 대시보드 API")
@Tag(name = "(교사) 대시보드 API", description = "(교사) 대시보드 API")
@AllArgsConstructor
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class TchDsbdController {

    private final TchDsbdService tchDsbdService;
    private final KafkaBatchService kafkaBatchService;

    //@ApiOperation(value = "이해도 낮은", notes = "")
//    @RequestMapping(value = "/tch/dsbd/understand/low", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdUnderstandLow() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdUnderstand(paramData);
        String resultMessage = "이해도 낮은";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "정답률 낮은순", notes = "")
//    @RequestMapping(value = "/tch/dsbd/accuracy/low", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdAccuracyLow() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdAccuracy(paramData);
        String resultMessage = "정답률 낮은순";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "목표-이해도 차이 큰 순", notes = "")
//    @RequestMapping(value = "/tch/dsbd/gap/high", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdGapHigh() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdGap(paramData);
        String resultMessage = "목표-이해도 차이 큰 순";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "단원별 이해도", notes = "")
//    @RequestMapping(value = "/tch/dsbd/understand/chapter", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdUnderstandChapter() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdUnderstandChapter(paramData);
        String resultMessage = "단원별 이해도";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "개념별 이해도", notes = "")
//    @RequestMapping(value = "/tch/dsbd/understand/concept", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdUnderstandConcept() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdUnderstandConcept(paramData);
        String resultMessage = "개념별 이해도";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "지식맵", notes = "")
//    @RequestMapping(value = "/tch/dsbd/knowledgemap", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdKnowledgeMap() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdKnowledgeMap(paramData);
        String resultMessage = "지식맵";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "영역별 이해도", notes = "")
//    @RequestMapping(value = "/tch/dsbd/understand/domain", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdUnderstandDomain() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdUnderstandDomain(paramData);
        String resultMessage = "영역별 이해도";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "영역별 이해도 상세", notes = "")
//    @RequestMapping(value = "/tch/dsbd/understand/domain/detail", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdUnderstandDomainDetail() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdUnderstandDomainDetail(paramData);
        String resultMessage = "영역별 이해도 상세";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "과제 평가 현황", notes = "")
//    @RequestMapping(value = "/tch/dsbd/status/eval", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusEval() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdStatusEval(paramData);
        String resultMessage = "과제 평가 현황";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "과제 평가 현황 상세", notes = "")
//    @RequestMapping(value = "/tch/dsbd/status/eval/detail", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusEvalDetail() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdStatusEvalDetail(paramData);
        String resultMessage = "과제 평가 현황 상세";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "자기주도 학습 현황", notes = "")
//    @RequestMapping(value = "/tch/dsbd/status/selflearning", method = {RequestMethod.GET})
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdSelflearning() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchDsbdService.findTchDsbdSelflearning(paramData);
        String resultMessage = "자기주도 학습 현황";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @RequestMapping(value = "/tch/dsbd/status/self-lrn/chapter/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 스스로학습 현황", description = "")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
//    @Parameter(name = "unitNum", description = "단원번호", schema = @Schema(type = "integer", example = "1" ))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusSelflrnChapterList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchDsbdService.findTchDsbdStatusSelflrnChapterList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 스스로학습 현황");

    }

    @RequestMapping(value = "/tch/dsbd/status/self-lrn/chapter/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 스스로학습 현황 상세", description = "")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "submDt", description = "제출날짜", required = true, schema = @Schema(type = "string", example = "20240226" ))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusSelflrnChapterDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchDsbdService.tchDsbdStatusSelflrnChapterDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 스스로학습 현황 상세");

    }

    @RequestMapping(value = "/tch/dsbd/status/self-lrn/chapter/detail/ai-tutor", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 스스로학습 현황(AiTutor) > 문항 풀이 현황 조회", description = "")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "49f37b12fe7f463785e38da824f212db" ))
    @Parameter(name = "stdDt", description = "학습 일자", required = true, schema = @Schema(type = "string", example = "2025-02-25" ))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    @Parameter(name = "brandId", description = "과목Id", required = true, schema = @Schema(type = "integer", example = "3" ))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusSelflrnChapterDetailAitutor(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchDsbdService.findStudentSelfLearningQuestionCount(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 스스로학습 현황 자세히 보기(AiTutor) > 문항풀이 현황 조회");

    }



    @RequestMapping(value = "/tch/dsbd/status/chapter-unit/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단원별 학생분포", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea46" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusChapterUnitList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception  {

        //kafkaBatchService.processSelectOneCycle(paramData);

        Object resultData = tchDsbdService.findTchDsbdStatusChapterUnitList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 단원별 학생분포");

    }

    @RequestMapping(value = "/tch/dsbd/status/chapter-unit/info", method = {RequestMethod.GET})
    @Operation(summary = "[교사][수학] 학급관리 > 홈 대시보드 > 단원별 분석", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook1644-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "3af91dd7bde84083bc4c415fc7052daa" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1342" ))
    @Parameter(name = "unitNum", description = "단원 번호", required = false, schema = @Schema(type = "integer", example = "1" ))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusChapterUnitInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception  {

        Object resultData = tchDsbdService.findTchDsbdStatusChapterUnitInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사][수학] 학급관리 > 홈 대시보드 > 단원별 분석");

    }


    @RequestMapping(value = "/tch/dsbd/status/chapter-unit/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단원별 학생분포 상세", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea46" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "metaId", description = "단원 ID", required = false, schema = @Schema(type = "integer", example = "1560" ))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdStatusChapterUnitDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.findTchDsbdStatusChapterUnitDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 단원별 학생분포 상세");

    }

    // 가장 최근 수업 정보 및 요약 현황 (사용 안함)
    @Loggable
    @RequestMapping(value = "/tch/dsbd/summary_old" , method = {RequestMethod.GET})
    @Operation(summary = "가장 최근 수업 정보 및 요약 현황 (추후 삭제 예정)", description = "가장 최근 수업 정보 및 요약 현황")
    @Parameter(name = "userId", description = "교사 ID", required = true)
    @Parameter(name = "claId", description = "학급 ID", required = true)
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true)
    public ResponseDTO<CustomBody> selectTchDsbdSummary(
            @RequestParam(name = "userId", defaultValue = "vstea6") String userId,
            @RequestParam(name = "claId", defaultValue = "1dfd6267b8fb11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId
    )throws Exception  {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);

        Object resultData = tchDsbdService.selectTchDsbdSummary(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "가장 최근 수업 정보 및 요약 현황");

    }

    // 가장 최근 수업 정보 및 요약 현황(개선사항)
    @Loggable
    @RequestMapping(value = "/tch/dsbd/summary" , method = {RequestMethod.GET})
    @Operation(summary = "가장 최근 수업 정보 및 요약 현황", description = "가장 최근 수업 정보 및 요약 현황")
    @Parameter(name = "userId", description = "교사 ID", required = true)
    @Parameter(name = "claId", description = "학급 ID", required = true)
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true)
    public ResponseDTO<CustomBody> selectTchDsbdSummaryNew(
            @RequestParam(name = "userId", defaultValue = "vstea6") String userId,
            @RequestParam(name = "claId", defaultValue = "1dfd6267b8fb11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId
    )throws Exception  {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);

        Object resultData = tchDsbdService.selectTchDsbdSummaryNew(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "가장 최근 수업 정보 및 요약 현황");

    }


    // 개념별 이해도
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/concept-usd/list" , method = {RequestMethod.GET})
    @Operation(summary = "개념별 이해도", description = "개념별 이해도")
    @Parameter(name = "userId", description = "교사 ID", required = true)
    @Parameter(name = "claId", description = "학급 ID", required = true)
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true)
    @Parameter(name = "metaId", description = "단원 ID", required = false)
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = false)
    @Parameter(name = "allSrhYn", description = "N", required = false)
    public ResponseDTO<CustomBody> selectTchDsbdConceptUsdList(
            @RequestParam(name = "userId", defaultValue = "vstea16") String userId,
            @RequestParam(name = "claId", defaultValue = "1dfd6267b8fb11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "16") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "") String kwgMainId,
            @RequestParam(name = "allSrhYn", defaultValue = "N") String allSrhYn
    )throws Exception  {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId.isEmpty()?"":metaId);
        paramData.put("kwgMainId", kwgMainId.isEmpty()?"":kwgMainId);
        paramData.put("allSrhYn", allSrhYn.isEmpty()?"":allSrhYn);

        Object resultData = tchDsbdService.selectTchDsbdConceptUsdList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "개념별 이해도");

    }

    // 개념별 이해도 상세
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/concept-usd/detail" , method = {RequestMethod.GET})
    @Operation(summary = "개념별 이해도 상세", description = "개념별 이해도 상세")
    @Parameter(name = "userId", description = "교사 ID", required = true)
    @Parameter(name = "claId", description = "학급 ID", required = true)
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true)
    @Parameter(name = "metaId", description = "단원 ID", required = false)
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = false)
    @Parameter(name = "stdDt", description = "학습날짜", required = true)
    public ResponseDTO<CustomBody> selectTchDsbdConceptUsdDetail(
            @RequestParam(name = "userId", defaultValue = "vstea16") String userId,
            @RequestParam(name = "claId", defaultValue = "1dfd6267b8fb11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "") String kwgMainId,
            @RequestParam(name = "stdDt", defaultValue = "20240313") String stdDt
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();


        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId);
        paramData.put("kwgMainId", kwgMainId);
        paramData.put("stdDt", stdDt);

        Object resultData = tchDsbdService.selectTchDsbdConceptUsdDetail(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "개념별 이해도 상세");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-usd/list", method = {RequestMethod.GET})
    @Operation(summary = "영역별 이해도", description = "교사 대시보드 화면의 각 학생의 영역별 이해도(단원별)를 기반으로 이해도 정보를 표시한다.")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea46"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "13"))
    public ResponseDTO<CustomBody> getTchDsbdStatusAreausdList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.getTchDsbdStatusAreausdList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "영역별 이해도");


    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-usd/detail", method = {RequestMethod.GET})
    @Operation(summary = "영역별 이해도 상세", description = "교사 대시보드 화면의 각 학생의 영역별 이해도(단원별)를 기반으로 이해도 정보를 표시한다.")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vsstu467"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "13"))
    @Parameter(name = "areaId", description = "영역 ID", required = true, schema = @Schema(type = "string", example = "1559"))
    public ResponseDTO<CustomBody> getTchDsbdStatusAreausdDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.getTchDsbdStatusAreausdDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "영역별 이해도 상세");


    }
    //학습맵 이해도
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/study-map/list", method = {RequestMethod.GET})
    @Operation(summary = "학습맵 이해도", description = "학습맵 이해도")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea38"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad483ba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "metaId", description = "단원 ID", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = false, schema = @Schema(type = "string", example = "915"))
    public ResponseDTO<CustomBody> selectTchDsbdChptUnitInfo(
            @RequestParam(name = "userId", defaultValue = "vstea38") String userId,
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

        Object resultData = tchDsbdService.selectTchDsbdChptUnitInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 이해도");

    }

    //학습맵 이해도 (개념)
    //사용안함
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/study-map/concept", method = {RequestMethod.GET})
    @Operation(summary = "학습맵 이해도 (개념)", description = "학습맵 이해도 (개념)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea38"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad483ba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "string", example = "870"))
    @Parameter(name = "kwgMainId", description = "지식요인 ID", required = true, schema = @Schema(type = "string", example = "915"))
    public ResponseDTO<CustomBody> selectTchDsbdStdCncptUsdInfo(
            @RequestParam(name = "userId", defaultValue = "vstea38") String userId,
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

        Object resultData = tchDsbdService.selectTchDsbdStdCncptUsdInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 이해도(개념)");

    }

    //학습맵 이해도 (상세)
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/study-map/detail", method = {RequestMethod.GET})
    @Operation(summary = "학습맵 이해도 (상세)", description = "학습맵 이해도 (상세)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea46"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad54bba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "string", example = "1559"))
    @Parameter(name = "kwgMainId", description = "단원 ID", required = true, schema = @Schema(type = "string", example = "1565"))
    public ResponseDTO<CustomBody> selectTchDsbdStdMapUsdInfo(
            @RequestParam(name = "userId", defaultValue = "vstea46") String userId,
            @RequestParam(name = "claId", defaultValue = "308ad54bba8f11ee88c00242ac110002") String claId,
            @RequestParam(name = "textbookId", defaultValue = "1") String textbookId,
            @RequestParam(name = "metaId", defaultValue = "1559") String metaId,
            @RequestParam(name = "kwgMainId", defaultValue = "1565") String kwgMainId
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();



        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbookId", textbookId);
        paramData.put("metaId", metaId);
        paramData.put("kwgMainId", kwgMainId);

        Object resultData = tchDsbdService.selectTchDsbdStdMapUsdInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 이해도 (상세)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/oftensent/save", method = {RequestMethod.POST})
    @Operation(summary = "자주쓰는문장(등록)", description = "자주쓰는 문장 등록")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"sents\":\"의견을 잘 표현함\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveOftensent(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchDsbdService.tchDsbdOftensentsSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자주쓰는문장(등록)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/oftensent/mod", method = {RequestMethod.POST})
    @Operation(summary = "자주쓰는문장(수정)", description = "자주쓰는 문장 수정")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"sentsId\":1," +
                            "\"sents\":\"자세하게 설명함\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> modOftensent(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchDsbdService.tchDsbdOftensentsMod(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자주쓰는문장(수정)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/oftensent/del", method = {RequestMethod.POST})
    @Operation(summary = "자주쓰는문장(삭제)", description = "자주쓰는 문장 삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"sentsId\":1" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> delOftensent(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchDsbdService.tchDsbdOftensentsDel(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자주쓰는문장(삭제)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/oftensents/list", method = {RequestMethod.GET})
    @Operation(summary = "자주쓰는문장(목록)", description = "자주쓰는 문장 목록")
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "vstea1"))
    @Parameter(name = "claId", description = "클래스 ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002"))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> getOftensentList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.tchDsbdOftensentsList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자주쓰는문장(목록)");

    }

    // [교사] 학급관리 > 홈 대시보드 > 영역별 그래프
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프");

    }

    // [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 All
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/listall", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 All (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 All (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementListAll(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        //kafkaBatchService.processSelectOneCycle(paramData);

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementListAll(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 ALL");

    }

    // [교사][영어] 학급관리 > 홈 대시보드 > 단원별 그래프 All
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/unit-achievement/listall", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단원별 그래프 All (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 단원별 그래프 All (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook229-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "49f37b12fe7f463785e38da824f212db"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    public ResponseDTO<CustomBody> selectTchDsbdUnitAchievementListAll(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdUnitAchievementListAll(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 단원별 그래프 ALL");

    }


    // [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "code", description = "지표명", required = true, schema = @Schema(type = "string", example = "listening"))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 (영어)");

    }

    // [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 ALL
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/detailAll", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "evaluationAreaCd", description = "지표명", required = true, schema = @Schema(type = "string", example = "listening"))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementDetailAll(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementDetailAll(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 (영어)");

    }


    // [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세(학생의 성취도)
    @Loggable
    @RequestMapping(value = "/tch/dsbd/statistic/achievement", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세(학생의 성취도) (영어)", description = "'학습 학생의 성취도 추이를 확인한다")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "unitNum", description = "단원 번호", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "evaluationAreaCd", description = "지표명", required = true, schema = @Schema(type = "string", example = "listening"))
    public ResponseDTO<CustomBody> selectTchDsbdStatisticAchievement(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatisticAchievementList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세(학생의 성취도) (영어)");

    }



    //[교사] 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식
    @Loggable
    @GetMapping(value = "/tch/dsbd/status/study-map/languageFormat/list")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식 (영어)", description = "학습맵 언어 형식 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook972-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a84c86cf944f158e302875f8d32c48" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1189"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusStudyMapLanguageFormatList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchDsbdService.selectTchDsbdStatusStudyMapLanguageFormatList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 언어 형식 (영어)");
    }

    //[교사] 학급관리 > 홈 대시보드 > 학습맵 > 의사소통 기능
    @Loggable
    @GetMapping(value = "/tch/dsbd/status/study-map/communication/list")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습맵 > 의사소통 (영어)", description = "학습맵 의사소통 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook972-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "75a84c86cf944f158e302875f8d32c48" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1189"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusStudyMapCommunicationList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchDsbdService.selectTchDsbdStatusStudyMapCommunicationList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 의사소통");
    }

    //[교사] 학급관리 > 홈 대시보드 > 학습맵 > 소재
    @Loggable
    @GetMapping(value = "/tch/dsbd/status/study-map/material/list")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습맵 > 소재 (영어)", description = "학습맵 소재 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook717-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "f5f305b5e468442dabb137da394545ff" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusStudyMapMaterialList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchDsbdService.selectTchDsbdStatusStudyMapMaterialList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 소재 (영어)");
    }

    //[교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준
    @Loggable
    @GetMapping(value = "/tch/dsbd/status/study-map/achievementStandard/list")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 (영어)", description = "학습맵 성취 기준 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook229-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "49f37b12fe7f463785e38da824f212db" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    @Parameter(name = "unitNum", description = "단원 번호", required = true, schema = @Schema(type = "integer", example = "1" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusStudyMapAchievementStandardList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchDsbdService.selectTchDsbdStatusStudyMapAchievementStandardList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 성취 기준 (영어)");

    }

    //[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/vocabulary/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 (영어)", description = "Vocabulary 그래프 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "sortGbCd", description = "정렬조건(A1:단어 오름차순, A2:단어 내림차순, B1:성취도 오름차순, B2:성취도 내림차순)", required = true, schema = @Schema(type = "string", example = "A1"))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectTchDsbdStatusVocabularyList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatusVocabularyList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Vocabulary 그래프 (영어)");

    }

    //[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 상세
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/vocabulary/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 상세 (영어)", description = "Vocabulary 그래프 상세 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "iemId", description = "항목 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusVocabularyDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatusVocabularyDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Vocabulary 그래프 상세 (영어)");

    }

    //[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/grammar/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 (영어)", description = "Grammar 그래프 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "sortGbCd", description = "정렬조건(A1:발음 오름차순, A2:발음 내림차순, B1:성취도 오름차순, B2:성취도 내림차순)", required = true, schema = @Schema(type = "string", example = "A1"))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectTchDsbdStatusGrammarList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatusGrammarList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Grammar 그래프 (영어)");

    }

    //[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 상세
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/grammar/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 상세 (영어)", description = "Grammar 그래프 상세 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "iemId", description = "항목 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusGrammarDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatusGrammarDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Grammar 그래프 상세 (영어)");

    }

    //[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/pronunciation/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 (영어)", description = "Pronunciation 그래프 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "2" ))
    @Parameter(name = "sortGbCd", description = "정렬조건(A1:발음 오름차순, A2:발음 내림차순, B1:성취도 오름차순, B2:성취도 내림차순)", required = true, schema = @Schema(type = "string", example = "A1"))
    @Parameter(name = "page", description = "page", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = true, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectTchDsbdStatusPronunciationList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatusPronunciationList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Pronunciation 그래프 (영어)");

    }

    //[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 상세
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/pronunciation/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 상세 (영어)", description = "Pronunciation 그래프 상세 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "2" ))
    @Parameter(name = "iemId", description = "항목 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusPronunciationDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        Object resultData = tchDsbdService.selectTchDsbdStatusPronunciationDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "Pronunciation 그래프 상세 (영어)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/detail/info", method = {RequestMethod.POST})
    @Operation(summary = "영역별 상세 조회", description = "영역별 상세 조회")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"engreal30-t\"," +
                            "\"textbkId\":308," +
                            "\"claId\":\"b4d7ae53e473488c921f35beb5ba98c6\"," +
                            "\"unitNum\":\"1\"," +
                            "\"evaluationAreaCd\":\"Material\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> findTchDsbdStatusAreaAchievementDetailInfo(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.findTchDsbdStatusAreaAchievementDetailInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "영역별 상세 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/study-map/total", method = {RequestMethod.POST})
    @Operation(summary = "성취기준 상세 조회", description = "성취기준 상세 조회")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbkId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"," +
                            "\"unitNum\":9," +
                            "\"metaId\":26353," +
                            "\"studyMapCd\":\"Material\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> findTchDsbdStatusStudyMapDetail(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.findTchDsbdStatusStudyMapDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "성취기준 상세 조회");

    }

    //[교사] 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약)
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/math/aitutor", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약)", description = "우리반 학습 분석(AI튜터_학습 요약)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook1644-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "3af91dd7bde84083bc4c415fc7052daa" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1342" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "0" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusAreaAchievementaitutorMath(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        Object resultData = tchDsbdService.selectTchDsbdStatusMathaitutor(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 학습 분석(AI튜터_학습 요약)");

    }

    //[교사] 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약)
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/aitutor", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약)", description = "우리반 학습 분석(AI튜터_학습 요약)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "2" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusAreaAchievementaitutor(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        Object resultData = tchDsbdService.selectTchDsbdStatusAreaAchievementaitutor(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 학습 분석(AI튜터_학습 요약)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/aitutor/unit-info", method = {RequestMethod.GET})
    @Operation(summary = "[교사](영어) 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약_단원 성취도 조회)", description = "우리반 학습 분석(AI튜터_학습 요약_단원 성취도 조회)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook229-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "49f37b12fe7f463785e38da824f212db" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "2" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusAreaAchievement(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        Object resultData = tchDsbdService.selectTchDsbdStatusAreaAchievementAitutorUnitInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "우리반 학습 분석(AI튜터_학습 요약_단원 성취도 조회)");

    }



    @Loggable
    @RequestMapping(value = "/tch/dsbd/notice/reg", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 공지사항 등록", description = "공지사항 등록")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbookId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"," +
                            "\"ntConts\":\"중간고사\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveNotice(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.saveNotice(paramData);
        String resultMessage = "공지사항 저장 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/notice/updatePin", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 공지사항 고정여부 수정", description = "공지사항 고정여부 수정")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"noticeId\":\"1\"," +
                            "\"noticeYn\":\"Y\"," +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbookId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> updateNoticePin(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.updateNoticePin(paramData);
        String resultMessage = "공지사항 고정여부 수정 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/notice/del", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 공지사항 삭제", description = "공지사항 삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"noticeId\":\"1\"," +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbookId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteNotice(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.deleteNotice(paramData);
        String resultMessage = "공지사항 삭제 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @GetMapping(value = {"/tch/dsbd/notice/call", "/stnt/dsbd/notice/call"})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 홈 공지사항 조회", description = "홈 공지사항 조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    public ResponseDTO<CustomBody> selectHomeNotice(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.selectHomeNotice(paramData);
        String resultMessage = "공지사항 조회 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @GetMapping(value = {"/tch/dsbd/notice/pop/call", "/stnt/dsbd/notice/pop/call"})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 팝업 공지사항 조회", description = "팝업 공지사항 조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    public ResponseDTO<CustomBody> selectPopupNotice(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.selectPopupNotice(paramData);
        String resultMessage = "공지사항 조회 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/statistic/participant/reg", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 접속학생통계 등록", description = "접속학생통계 등록")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbookId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"," +
                            "\"stntId\":[\"engreal79-s1\", \"engreal79-s2\", \"engreal79-s3\"]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveStatisticParticipant(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.saveStatisticParticipant(paramData);
        String resultMessage = "접속학생통계 등록 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/statistic/participant/init", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 접속학생통계 초기화", description = "접속학생통계 초기화")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbookId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteStatisticParticipant(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.deleteStatisticParticipant(paramData);
        String resultMessage = "접속학생통계 초기화 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @GetMapping(value = "/tch/dsbd/statistic/participant/call")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 접속학생통계 조회", description = "접속학생통계 조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engreal79-t" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "a6f63a21e3f84f28a1cfd7ee177925d4" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "335" ))
    public ResponseDTO<CustomBody> selectStatisticParticipant(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.selectStatisticParticipant(paramData);
        String resultMessage = "접속학생통계 조회 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    /**
     * (교과서) 학습맵 단원 목록 조회
     * - 수학/영어 공통
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Loggable
    @RequestMapping(value = {"/tch/dsbd/status/study-map/unit/list","/stnt/dsbd/status/study-map/unit/list"}, method = {RequestMethod.GET})
    @Operation(summary = "(교과서) 학습맵 단원 목록 조회", description = "")
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1152" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusStudyMapUnitList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatusStudyMapUnitList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교과서) 학습맵 단원 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/study-map/math/ach-std/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 (수학)", description = "학습맵 성취 기준 (수학)")
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1152" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "6f0039ec1ac94787846348d5ed478969" ))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "870" ))
    public ResponseDTO<CustomBody> selectTchDsbdStatusStudyMapMathAchievementStandardList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdStatusStudyMapMathAchievementStandardList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습맵 성취 기준 (수학)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/leaningSummary/studentList", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습 요약 > 수학/영어 > 교사", description = "학습요약 학급 학생 리스트")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "73e000a04fc64449a17bb9dfa42f5b23" ))
    public ResponseDTO<CustomBody> selectOfClassInStudentsList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectOfClassInStudentsList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습요약 학급 학생 리스트");

    }

    @Loggable
    @RequestMapping(value = {"/tch/dsbd/status/leaningSummary/statistics/eng", "/stnt/dsbd/status/leaningSummary/statistics/eng"}, method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습 요약(영어)", description = "학습요약 통계(영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1118-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "cc5addad543944429aca826a8a75ad93" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1150" ))
    @Parameter(name = "trgtSeCd", description = "학습 구분", required = true, schema = @Schema(type = "string", example = "1,2,3,4" )) // 1.수업 2.과제 3.평가 4.스스로학습
    @Parameter(name = "startDate", description = "학습 시작 일시", required = true, schema = @Schema(type = "string", example = "20250330" ))
    @Parameter(name = "endDate", description = "학습 종료 일시", required = true, schema = @Schema(type = "string", example = "20250405" ))
    public ResponseDTO<CustomBody> selectLeaningSummaryStatisticsEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectLeaningSummaryStatisticsEng(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습요약 통계");

    }

    @Loggable
    @RequestMapping(value = {"/tch/dsbd/status/leaningSummary/statistics/math", "/stnt/dsbd/status/leaningSummary/statistics/math"}, method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 학습 요약(수학)", description = "학습요약 통계(수학)")
    @Parameter(name = "userId", description = "학생 ID", required = false, schema = @Schema(type = "string", example = "mathbook3111-s1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5e208b05c1c842c1a940c34676b346dd" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "string", example = "1201" ))
    @Parameter(name = "trgtSeCd", description = "학습 구분", required = true, schema = @Schema(type = "string", example = "1,2,3,4" )) // 1.수업 2.과제 3.평가 4.스스로학습
    @Parameter(name = "startDate", description = "학습 시작 일시", required = true, schema = @Schema(type = "string", example = "20250118" ))
    @Parameter(name = "endDate", description = "학습 종료 일시", required = true, schema = @Schema(type = "string", example = "20250228" ))
    public ResponseDTO<CustomBody> selectLeaningSummaryStatisticsMath(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectLeaningSummaryStatisticsMath(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학습요약 통계");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/rec/sets", method = {RequestMethod.GET})
    @Operation(summary = "대시보드 > 그룹별맞춤과제출제 : 추천 세트지 조회", description = "그룹별맞춤과제출제(수학)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "373" ))
    @Parameter(name = "unitNum", description = "학급 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "difficulty", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "userId", description = "단원 ID", required = true, schema = @Schema(type = "string", example = "mathreal151-t" ))
    public ResponseDTO<CustomBody> selectTchDsbdRecSets(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdRecSets(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "그룹별맞춤과제출제(수학)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/rec/article", method = {RequestMethod.GET})
    @Operation(summary = "대시보드 > 그룹별맞춤과제출제 : 추천 아티클 조회", description = "그룹별맞춤과제출제(수학)")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "373" ))
    @Parameter(name = "unitNum", description = "학급 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "difficulty", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1" ))
    @Parameter(name = "setId", description = "세트지 ID", required = true, schema = @Schema(type = "string", example = "1" ))
    public ResponseDTO<CustomBody> selectTchDsbdRecArticle(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdRecArticle(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "그룹별맞춤과제출제(수학)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/rec/sets/chk", method = {RequestMethod.POST})
    @Operation(summary = "대시보드 > 그룹별맞춤과제출제 : 추천받은 세트지 체크", description = "그룹별맞춤과제출제(수학)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"textbkId\":\"550e8400-e29b-41d4-a716-446655440000\"," +
                            "\"unitNum\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"difficulty\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"setId\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"setIdx\":\"0cc175b9c0f1b6a831c399e269772661\"," +
                            "\"userId\":\"mathreal151-t\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> modifyTchDsbdRecChk(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.modifyTchDsbdRecChk(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "그룹별맞춤과제출제(수학)");

    }


    @Loggable
    @RequestMapping(value = "/tch/dsbd/memo/reg", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 메모 등록", description = "메모 등록")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbookId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"," +
                            "\"memoConts\":\"중간고사\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveMemo(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.saveMemo(paramData);
        String resultMessage = "메모 저장 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @GetMapping(value = "/tch/dsbd/memo/list")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 메모 목록 조회", description = "메모 목록 조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea50" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "308ad5afba8f11ee88c00242ac110002" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "10" ))
    public ResponseDTO<CustomBody> selectMemo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.selectMemo(paramData);
        String resultMessage = "메모 목록 조회 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/memo/del", method = {RequestMethod.POST})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 메모 삭제", description = "메모 삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"memoId\":\"1\"," +
                            "\"userId\":\"engreal79-t\"," +
                            "\"textbookId\":335," +
                            "\"claId\":\"a6f63a21e3f84f28a1cfd7ee177925d4\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> deleteMemo(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = tchDsbdService.deleteMemo(paramData);
        String resultMessage = "메모 삭제 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @PostMapping("/tch/dsbd/memo/modi")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 메모 수정", description = "메모 수정")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"memoId\":518," +
                            "\"userId\":\"neweng2161-t\"," +
                            "\"textbookId\":6982," +
                            "\"claId\":\"31e775aec33442539e6e69567bb084c7\"," +
                            "\"memoConts\":\"중간고사\"" +
                            "}")
            })
    )
    public ResponseDTO<CustomBody> updateMemo(
               @RequestBody Map<String, Object> paramData) throws Exception {
               Map<String, Object> resultData = tchDsbdService.updateMemo(paramData);
        String resultMessage = "메모 수정 성공";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }



    @RequestMapping(value = {"/tch/dsbd/calendar/list" ,"/stnt/dsbd/calendar/list"}, method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 우리반 학습 현황 캘린더 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook3111-t" ))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook3111-s1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5e208b05c1c842c1a940c34676b346dd" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1201" ))
    @Parameter(name = "trgtSeCd", description = "대상구분(1: 학습, 2: 과제, 3: 평가)", required = true, schema = @Schema(type = "string", example = "1,2,3"))
    @Parameter(name = "year", description = "연도", required = true, schema = @Schema(type = "string", example = "2025"))
    @Parameter(name = "month", description = "월", required = true, schema = @Schema(type = "string", example = "02"))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdCalendarEventsList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception  {

        Object resultData = tchDsbdService.findTchDsbdCalendarEventsList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 우리반 학습 현황 캘린더 조회");

    }

    @RequestMapping(value = {"/tch/dsbd/calendar/detail" , "/stnt/dsbd/calendar/detail"}, method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 우리반 학습 현황 캘린더 상세 조회", description = "학습(1)의 경우 trgtId 복수 값(콤마로 구분)이 가능하며, 과제(2)/평가(3)는 단일 값만 가능합니다.")
    @Parameter(name = "tchId", description = "교사 ID", required = false, schema = @Schema(type = "string", example = "mathbook3111-t" ))
    @Parameter(name = "stntId", description = "학생 ID", required = false, schema = @Schema(type = "string", example = "mathbook3111-s1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5e208b05c1c842c1a940c34676b346dd" ))
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1201" ))
    @Parameter(name = "trgtId", description = "타겟 ID(학습: 복수, 과제/평가: 단일)", required = true, schema = @Schema(type = "string", example = "121"))
    @Parameter(name = "trgtSeCd", description = "대상구분(1: 학습, 2: 과제, 3: 평가)", required = true, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "startDate", description = "학습 일시", required = true, schema = @Schema(type = "string", example = "20250317"))
    @Loggable
    public ResponseDTO<CustomBody> tchDsbdCalendarEventsDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception  {

        Object resultData = tchDsbdService.findTchDsbdCalendarEventsDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 우리반 학습 현황 캘린더 상세 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/class/distribution", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook1192-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "a607f8e867844c3c86340256e6c12570"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementClassdDstribution(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementClassdDstribution(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (영어)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/student/distribution", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "engbook1192-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "a607f8e867844c3c86340256e6c12570"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "evaluationAreaCd", description = "영역 코드", required = true, schema = @Schema(type = "string", example = "understanding"))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementStudentDstribution(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementStudentDstribution(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/student/list", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 성취도 목록 (영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 성취도 목록 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook1118-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "cc5addad543944429aca826a8a75ad93"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "evaluationAreaCd", description = "영역 코드", required = false, schema = @Schema(type = "string", example = "understanding"))
    public ResponseDTO<CustomBody> selectTchDsbdDistributionAreaAchievementStudentList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdDistributionAreaAchievementStudentList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 성취도 목록 (영어)");

    }

    @Loggable
    @GetMapping(value = "/tch/dsbd/status/chapter-usd/class/distribution")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (수학)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (수학)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "applemath53-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "2aa8ad6aa5b64a938af130a2e3e61ce1"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    public ResponseDTO<CustomBody> selectTchDsbdChapterUsdClassdDistribution(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchDsbdService.selectTchDsbdChapterUsdClassdDstribution(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (수학)");
    }

    @Loggable
    @GetMapping(value = "/tch/dsbd/status/chapter-usd/student/distribution")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook497-t"))
    @Parameter(name = "stntId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook497-s1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "6f0039ec1ac94787846348d5ed478969"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    @Parameter(name = "contentAreaId", description = "영역 ID", required = true, schema = @Schema(type = "integer", example = "568"))
    public ResponseDTO<CustomBody> selectTchDsbdChapterUsdStudentDistribution(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchDsbdService.selectTchDsbdChapterUsdStudentDstribution(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)");
    }

    @Loggable
    @GetMapping(value = "/tch/dsbd/status/chapter-usd/student/list")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 이해도 목록 (수학)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 이해도 목록 (수학)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook497-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "6f0039ec1ac94787846348d5ed478969"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    @Parameter(name = "contentAreaId", description = "영역 ID", required = false, schema = @Schema(type = "integer", example = "568"))
    public ResponseDTO<CustomBody> selectTchDsbdDistributionChapterUsdStudentList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchDsbdService.selectTchDsbdDistributionChapterUsdStudentList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 학생 이해도 목록 (수학)");
    }

    @Loggable
    @GetMapping(value = "/tch/dsbd/combo-box/unit-list")
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 단원 목록 조회", description = "단원 목록 조회")
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1150" ))
    public ResponseDTO<CustomBody> selectComboBoxUnitList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchDsbdService.findUnitList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "단원 목록 조회");
    }

    @Loggable
    @GetMapping(value = "/tch/dsbd/combo-box/eng-area")
    @Operation(summary = "[교사][영어] 학급관리 > 홈 대시보드 > 영역 목록 조회", description = "영역 목록 조회")
    public ResponseDTO<CustomBody> selectComboBoxEngAreaList(
    ) throws Exception {
        Object resultData = tchDsbdService.findEngAreaList();
        return AidtCommonUtil.makeResultSuccess(null, resultData, "영역 목록 조회");
    }

    @Loggable
    @RequestMapping(value = {"/tch/dsbd/status/concept-usd/tree", "/stnt/dsbd/status/concept-usd/tree"}, method = {RequestMethod.GET})
    @Operation(summary = "개념별 이해도 - 트리 맵", description = "영역 목록 조회")
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5e208b05c1c842c1a940c34676b346dd") )
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "metaId", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "870"))
    @Parameter(name = "tchId", description = "선생 ID", required = false, schema = @Schema(type = "string", example = "mathbook3111-t"))
    @Parameter(name = "stdtId", description = "학생 ID", required = false, schema = @Schema(type = "string", example = "mathbook3111-s1"))
    public ResponseDTO<CustomBody> findTchDsbdConceptUsdTree(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchDsbdService.findTchDsbdConceptUsdTree(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "개념별 이해도 - 트리 맵");
    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/usd-participation/quadrant", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 (수학)", description = "[교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 (수학)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook3111-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5e208b05c1c842c1a940c34676b346dd"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "startDate", description = "학습 시작 일시", required = true, schema = @Schema(type = "string", example = "20241105" ))
    @Parameter(name = "endDate", description = "학습 종료 일시", required = true, schema = @Schema(type = "string", example = "20250312" ))
    public ResponseDTO<CustomBody> selectTchDsbdUsdParticipationQuadrant(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdUsdParticipationQuadrant(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 (수학)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/usd-participation/student/quadrant", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 그룹 (수학)", description = "[교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 그룹(수학)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook3111-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5e208b05c1c842c1a940c34676b346dd"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "quadrant", description = "사분면 그룹 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "startDate", description = "학습 시작 일시", required = true, schema = @Schema(type = "string", example = "20241105" ))
    @Parameter(name = "endDate", description = "학습 종료 일시", required = true, schema = @Schema(type = "string", example = "20250312" ))
    public ResponseDTO<CustomBody> selectTchDsbdUsdParticipationStudentQuadrant(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdUsdParticipationStudentQuadrant(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 그룹(수학)");

    }

    @Loggable
    @RequestMapping(value = "/tch/dsbd/quadrant/encouragement/notification/pop", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 사분면 그룹 독려 알림 전송 팝업", description = "[교사] 학급관리 > 홈 대시보드 > 사분면 그룹 독려 알림 전송 팝업")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook3111-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5e208b05c1c842c1a940c34676b346dd"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1201"))
    @Parameter(name = "quadrant", description = "사분면 그룹 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "startDate", description = "학습 시작 일시", required = true, schema = @Schema(type = "string", example = "20241105" ))
    @Parameter(name = "endDate", description = "학습 종료 일시", required = true, schema = @Schema(type = "string", example = "20250312" ))
    public ResponseDTO<CustomBody> sendQuadrantEncouragementNotificationPop(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.sendQuadrantEncouragementNotificationPop(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 사분면 그룹 독려 알림 전송 팝업");

    }


    @Loggable
    @PostMapping(value = "/tch/dsbd/quadrant/encouragement/notification")
    @Operation(summary = "사분면 그룹 독려 알림 전송", description = "사분면 그룹 독려 알림 전송.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"mathbook3111-t\"," +
                            "\"claId\":\"5e208b05c1c842c1a940c34676b346dd\"," +
                            "\"textbookId\":\"1201\"," +
                            "\"stntNtcnCn\":\"더 큰 목표를 설정하고 도전해 볼 준비가 되었나요? 선생님은 늘 응원합니다!\"," +
                            "\"quadrant\":1," +
                            "\"startDate\":\"20241105\"," +
                            "\"endDate\":\"20250312\"" +
                            "}"
                    )
                }
            ))
    public ResponseDTO<CustomBody> sendQuadrantEncouragementNotification(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Object resultData = tchDsbdService.sendQuadrantEncouragementNotification(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "사분면 그룹 독려 알림 전송");
    }

    // [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 AI튜터
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/detail/aitutor", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 AI튜터(영어)", description = "[교사] 학급관리 > 홈 대시보드 > 영역별 그래프 AI튜터 (영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook1118-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "cc5addad543944429aca826a8a75ad93"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "unitNum", description = "단원 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "evaluationAreaCd", description = "지표명", required = true, schema = @Schema(type = "string", example = "listening"))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementAitutor(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementAitutor(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 AI튜터");

    }

    // [교사] 학급관리 > 홈 대시보드 > 의사소통 기능 상세(초등 영어)
    @Loggable
    @RequestMapping(value = "/tch/dsbd/status/area-achievement/communication/detail", method = {RequestMethod.GET})
    @Operation(summary = "[교사] 학급관리 > 홈 대시보드 > 의사소통 기능 상세(초등 영어)", description = "[교사] 학급관리 > 홈 대시보드 > 의사소통 기능 상세(초등 영어)")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook1118-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "cc5addad543944429aca826a8a75ad93"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "iemId", description = "항목 ID", required = false, schema = @Schema(type = "integer", example = "1" ))
    public ResponseDTO<CustomBody> selectTchDsbdAreaAchievementCommunicationDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchDsbdService.selectTchDsbdAreaAchievementCommunicationDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 의사소통 기능 상세(초등 영어)");

    }


}