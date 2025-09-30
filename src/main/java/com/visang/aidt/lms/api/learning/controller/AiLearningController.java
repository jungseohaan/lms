package com.visang.aidt.lms.api.learning.controller;

import com.visang.aidt.lms.api.learning.service.AiLearningService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "AI학습 API", description = "AI학습 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AiLearningController {

    private final AiLearningService aiLearningService;

//    private final AiLearningBatchJob aiLearningBatchJob;

    @GetMapping("/batch/ai/remedy-lrn/create/evl/get-target")
    @Operation(summary = "자동문항생성-AI 처방 학습-평가 : 대상 평가id 조회", description = "백앤드 배치 테스트를 위한 API 입니다.")
    @Parameter(name = "fromDt", description = "from Date(yyyymmdd)", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> findTargetEvlId(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.findTargetEvlIdList(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "어제 날짜 종료된 평가id 목록 조회");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @GetMapping("/batch/ai/remedy-lrn/create/evl/article")
    @Operation(summary = "자동문항생성-AI 처방 학습-평가", description = "백앤드 배치 테스트를 위한 API 입니다. 입력된 평가id의 오답 article 을 가져옴")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "5709")) // 2226(민간 개발)
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathreal91-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "5cc108d7cf12484b8b530852a1bd70d3"))
    @Parameter(name = "textbkId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "373"))
    public ResponseDTO<CustomBody> autoCreateAiLearningEvl(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.findAutoCreateAiLearningEvl(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문항생성-AI 처방 학습 - 생성할 article 목록 조회");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @GetMapping("/batch/ai/remedy-lrn/create/task/get-target")
    @Operation(summary = "자동문항생성-AI 처방 학습-과제 : 대상 과제id 조회", description = "백앤드 배치 테스트를 위한 API 입니다. ")
    @Parameter(name = "fromDt", description = "from Date(yyyymmdd)", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> findTargetTaskId(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.findTargetTaskIdList(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "어제 날짜 종료된 과제id 목록 조회");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @GetMapping("/batch/ai/remedy-lrn/create/task/article")
    @Operation(summary = "자동문항생성-AI 처방 학습-평가", description = "백앤드 배치 테스트를 위한 API 입니다. 입력된 과제id의 오답 article 을 가져옴")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "1469"))
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "re22eng5-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "4763f6ca9d9c4fd2904aa71a124952b7"))
    @Parameter(name = "textbkId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "241"))
    public ResponseDTO<CustomBody> autoCreateAiLearningTask(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.findAutoCreateAiLearningTask(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문항생성-AI 처방 학습 - 생성할 article 목록 조회");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @GetMapping("/batch/ai/remedy-lrn/create/forced-execute")
    @Operation(summary = "자동문항생성-AI 처방 학습-강제실행", description = "백앤드 배치 테스트를 위한 API 입니다. 배치 job을 강제로 실행시킴")
    @Parameter(name = "fromDt", description = "from DT(yyyymmdd)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "id", description = "평가 또는 과제 ID", required = false, schema = @Schema(type = "int", example = ""))
    public ResponseDTO<CustomBody> autoCreateAiLearningForcedExecute(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
//            Object resultData = this.aiLearningService.findAutoCreateAiLearningTask(paramData);

            Map<String, Object> evlResult = new HashMap<>();
            Map<String, Object> taskResult = new HashMap<>();

            evlResult = aiLearningService.createAiLearningBatchEvl(paramData);
          //  taskResult = aiLearningService.createAiLearningBatchTask(paramData);

            LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();

            long totalDur = 0;
            long evlTime = 0;
            long taskTime = 0;

//            evlTime = (long) evlResult.get("duration");
//            taskTime = (long) taskResult.get("duration");

            totalDur = evlTime + taskTime;


            resultMap.put("대상 평가id 건수", evlResult.get("idCount"));
            resultMap.put("대상 학생수", evlResult.get("stntCnt"));
            resultMap.put("평가 아티클 추출 건수", evlResult.get("articleCount"));
            resultMap.put("생성 성공 건수", evlResult.get("successCnt"));
            resultMap.put("생성 스킵 or 실패 건수", evlResult.get("failCnt"));
            resultMap.put("대상 과제id 건수", taskResult.get("idCount"));
            resultMap.put("과제 아티클 추출 건수", taskResult.get("articleCount"));
            resultMap.put("과제 세트지 생성 건수", taskResult.get("setCount"));
            resultMap.put("총 소요시간(ms)", totalDur);

            return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "executeCreateAiLearningSets 배치 실행됨");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @GetMapping(value = "/tch/ai/custom-lrn/available")
    @Operation(summary = "(교사) AI 맞춤 학습 생성 가능 여부 확인", description = "AI 맞춤 학습 전 학습 데이터 유무 조회")
    @Parameter(name = "wrterId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "emaone1-t"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "aa28cf360fe34b8d80cf5146229c811a"))
    @Parameter(name = "textbkId", description = "교과서ID", required = true, schema = @Schema(type = "integer", example = "16"))
    @Parameter(name = "tabId", description = "탭ID", required = true, schema = @Schema(type = "integer", example = "1181260"))
    @Parameter(name = "crculId", description = "커리큘럼ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> checkAiCustomLearningAvailability(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Map<String, Object> resultData = aiLearningService.checkAiCustomLearningAvailability(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) AI 맞춤 학습 생성 가능 여부 확인");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @PostMapping(value = "/tch/ai/custom-lrn/preview")
    @Operation(summary = "(교사) AI 맞춤 학습 생성-공통/개별 문항 미리보기", description = "AI 맞춤 학습 미리보기. ( [교사] > AI 맞춤 학습 > 수학 전용 )")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                  {
                    "wrterId": "mathbook2190-t",
                    "claId": "dcf23d8f01c747a3924dd9148695a8f1",
                    "textbkId": 1152,
                    "crculId": 38,
                    "eamTrget": 2,
                    "eamExmNum": 0,
                    "eamGdExmMun": 0,
                    "eamAvUpExmMun": 0,
                    "eamAvExmMun": 0,
                    "eamAvLwExmMun": 0,
                    "eamBdExmMun": 0,
                    "stntIdList": [
                      {
                        "mamoymId": "mathbook2190-s1",
                        "lev": "gd"
                      },
                      {
                        "mamoymId": "mathbook2190-s2",
                        "lev": "av"
                      }
                    ]
                  }
            """)
            })
    )
    public ResponseDTO<CustomBody> tchAiCustomLearnPreview(
            @RequestBody Map<String, Object> paramData
    ) {
        try {

            Map<String, Object> resultData = aiLearningService.createAiCustomPreview(paramData);

            if (MapUtils.getBoolean(resultData, "resultOk")) {
                return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) AI 맞춤 학습 미리보기");
            } else {
                return AidtCommonUtil.makeResultFail(paramData, resultData, MapUtils.getString(resultData, "resultMsg", "실패"));
            }

        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }
    
    @PostMapping("/tch/ai/custom-lrn/create")
    @Operation(summary = "(교사) AI 맞춤 학습 생성-공통/개별 문항출제", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "wrterId": "mathbook2190-t",
                    "claId": "dcf23d8f01c747a3924dd9148695a8f1",
                    "textbkId": 1152,
                    "tabId": 4493255,
                    "crculId": 38,
                    "lrnMethod": 2,
                    "pdEvlStDt": "2025-06-25 17:35",
                    "pdEvlEdDt": "2025-06-27 17:35",
                    "aiTutSetAt": "N",
                    "eamTrget": 2,
                    "articleList": [
                      {
                        "mathbook2190-s1": [
                          "26566",
                          "26595",
                          "26598",
                          "128249",
                          "26876"
                        ]
                      },
                      {
                        "mathbook2190-s2": [
                          "322959",
                          "26790",
                          "26855"
                        ]
                      }
                    ]
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> tchAiCustomLearnCreate(
            @RequestBody Map<String, Object> paramData
    ) {

        paramData.put("eamExmNum", MapUtils.getInteger(paramData, "eamExmNum", 0));
        paramData.put("eamGdExmMun", MapUtils.getInteger(paramData, "eamGdExmMun", 0));
        paramData.put("eamAvUpExmMun", MapUtils.getInteger(paramData, "eamAvUpExmMun", 0));
        paramData.put("eamAvExmMun", MapUtils.getInteger(paramData, "eamAvExmMun", 0));
        paramData.put("eamAvLwExmMun", MapUtils.getInteger(paramData, "eamAvLwExmMun", 0));
        paramData.put("eamBdExmMun", MapUtils.getInteger(paramData, "eamBdExmMun", 0));

        try {

            Map<String, Object> resultData = aiLearningService.createAiCustomLearining(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) AI 맞춤 학습 생성");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }


    @RequestMapping(value = "/tch/ai/custom-lrn/restart", method = {RequestMethod.POST})
    @Operation(summary = "(교사) AI 맞춤 학습 다시하기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "wrterId": "emaone1-t",
                    "claId": "aa28cf360fe34b8d80cf5146229c811a",
                    "textbkId": "16",
                    "crculId": "1",
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> tchAiCustomLearnReCreate(
            @RequestBody Map<String, Object> paramData
    ) {
        try {

            Map<String, Object> resultData = aiLearningService.reCreateAiCustomLearining(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) AI 맞춤 학습 다시하기");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }


    @GetMapping("/tch/ai/custom-lrn/std-info/check")
    @Operation(summary = "AI 맞춤 학습 생성-학습자료 존재여부를 체크", description = "AI 맞춤 학습 생성-학습자료 존재여부를 체크")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "emaone1-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "aa28cf360fe34b8d80cf5146229c811a"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "16"))
    @Parameter(name = "crculId", description = "수업중인 커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "2"))
    public ResponseDTO<CustomBody> tchAiCustomLearningPersonalCountCheck(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.findAiCustomLeariningPersonalCountCheck(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI 맞춤 학습 생성-학습자료 존재여부를 체크");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }


    @GetMapping({"/tch/ai/custom-lrn/set-info", "/tch/ai/custom-lrn/set-info/eng"})
    @Operation(summary = "AI 맞춤 학습 설정 정보 조회(교사)", description = "AI 맞춤 학습 설정 정보 조회(교사)")
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "5360"))
    public ResponseDTO<CustomBody> tchAiCustomLearningSetInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.findAiCustomLearningSetInfo(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI 맞춤 학습 설정 정보 조회(교사)");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }


    @RequestMapping(value = "/tch/ai/custom-lrn/init", method = {RequestMethod.POST})
    @Operation(summary = "AI 맞춤 학습 초기화", description = "AI 맞춤 학습 초기화")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "tabId": "5360"
                }
            """)
            })
    )
//    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "5360"))
    public ResponseDTO<CustomBody> tchAiCustomLearningInit(
            @RequestBody Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.deleteAiCustomLearning(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI 맞춤 학습 초기화");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }

    @GetMapping({"/stnt/ai/custom-lrn/set-info", "/stnt/ai/custom-lrn/set-info/eng"})
    @Operation(summary = "AI 맞춤 학습 설정 정보 조회(학생)", description = "")
    @Parameter(name = "tabId", description = "탭 ID", required = true, schema = @Schema(type = "integer", example = "5360"))
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathreal21-s3"))
    public ResponseDTO<CustomBody> stntAiCustomLearningSetInfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) {
        try {
            Object resultData = this.aiLearningService.findStntAiCustomLearningSetInfo(paramData);

            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI 맞춤 학습 설정 정보 조회(학생)");
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, e.getMessage());
        }
    }
}
