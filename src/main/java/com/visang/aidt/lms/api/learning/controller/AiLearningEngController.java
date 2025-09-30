package com.visang.aidt.lms.api.learning.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.learning.service.AiLearningEngService;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.AidtConst;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
@Tag(name = "[영어] AI학습 API", description = "[영어] AI학습 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AiLearningEngController {
    private final AiLearningEngService aiLearningEngService;

    @Loggable
    @PostMapping(value = "/tch/ai/custom-lrn/preview/eng")
    @Operation(summary = "AI맞춤학습-과제,수업(동일, 개별)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "wrterId": "engreal21-t",
                                    "claId": "8cdd444954404cfba4666767db51e967",
                                    "textbkId": 308,
                                    "tabId": 6406,
                                    "crculId": 10,
                                    "lrnMethod": 2,
                                    "aiTutSetAt": "N",
                                    "eamTrget": 2,
                                    "eamExmNum": 0,
                                    "eamGdExmMun": 0,
                                    "eamAvExmMun": 0,
                                    "eamBdExmMun": 0,
                                    "pdEvlStDt":"2024-04-29 15:00",
                                    "pdEvlEdDt":"2024-04-29 15:00",
                                    "targetStntList": [
                                         {"stntId": "student1"},
                                         {"stntId": "student2"},
                                         {"stntId": "student3"},
                                         {"stntId": "student4"},
                                         {"stntId": "student5"},
                                         {"stntId": "student6"},
                                         {"stntId": "student7"},
                                         {"stntId": "student8"},
                                         {"stntId": "student9"}
                                     ]
                                }
                            """)
            })
    )
//    @Parameter(name = "wrterId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "emaone1-t" ))
//    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "aa28cf360fe34b8d80cf5146229c811a" ))
//    @Parameter(name = "textbkId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "16" ))
//    @Parameter(name = "crculId", description = "수업중인 커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "4" ))
//    @Parameter(name = "lrnMethod", description = "학습 방법(1.수업중 2.과제)", required = true, schema = @Schema(type = "integer", example = "2" ))
//    @Parameter(name = "pdEvlStDt", description = "시작일자", required = false, schema = @Schema(type = "string", example = "2024-04-01 09:00" ))
//    @Parameter(name = "pdEvlEdDt", description = "마감일자", required = false, schema = @Schema(type = "string", example = "2024-04-02 23:59" ))
//    @Parameter(name = "aiTutSetAt", description = "AI 튜터 사용 여부", required = true, schema = @Schema(type = "string", example = "Y" ))
//    @Parameter(name = "eamTrget", description = "출제 대상(1.공통 2.개별)", required = true, schema = @Schema(type = "integer", example = "1" ))
//    @Parameter(name = "eamExmNum", description = "출제 문항수", required = true, schema = @Schema(type = "integer", example = "20" ))
//    @Parameter(name = "eamGdExmMun", description = "출제 문항수(상)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamAvUpExmMun", description = "출제 문항수(중상)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamAvExmMun", description = "출제 문항수(중)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamAvLwExmMun", description = "출제 문항수(중하)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamBdExmMun", description = "출제 문항수(하)", required = true, schema = @Schema(type = "integer", example = "5" ))
    public ResponseDTO<CustomBody> tchAiCustomLearnCreateEngPreview(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = aiLearningEngService.createAiCustomLeariningPreview(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI맞춤학습-과제,수업(동일, 개별)");

    }

    @Loggable
    @PostMapping(value = "/tch/ai/custom-lrn/create/eng")
    @Operation(summary = "AI맞춤학습-과제,수업(동일, 개별)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "wrterId": "engreal21-t",
                                    "claId": "8cdd444954404cfba4666767db51e967",
                                    "textbkId": 308,
                                    "tabId": 6406,
                                    "crculId": 10,
                                    "lrnMethod": 2,
                                    "aiTutSetAt": "N",
                                    "eamTrget": 2,
                                    "eamExmNum": 0,
                                    "eamGdExmMun": 0,
                                    "eamAvExmMun": 0,
                                    "eamBdExmMun": 0,
                                    "pdEvlStDt":"2024-04-29 15:00",
                                    "pdEvlEdDt":"2024-04-29 15:00",
                                    "targetStntList": [
                                         {"stntId": "student1"},
                                         {"stntId": "student2"},
                                         {"stntId": "student3"},
                                         {"stntId": "student4"},
                                         {"stntId": "student5"},
                                         {"stntId": "student6"},
                                         {"stntId": "student7"},
                                         {"stntId": "student8"},
                                         {"stntId": "student9"}
                                     ]
                                }
                            """)
            })
    )
//    @Parameter(name = "wrterId", description = "교사ID", required = true, schema = @Schema(type = "string", example = "emaone1-t" ))
//    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "aa28cf360fe34b8d80cf5146229c811a" ))
//    @Parameter(name = "textbkId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "16" ))
//    @Parameter(name = "crculId", description = "수업중인 커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "4" ))
//    @Parameter(name = "lrnMethod", description = "학습 방법(1.수업중 2.과제)", required = true, schema = @Schema(type = "integer", example = "2" ))
//    @Parameter(name = "pdEvlStDt", description = "시작일자", required = false, schema = @Schema(type = "string", example = "2024-04-01 09:00" ))
//    @Parameter(name = "pdEvlEdDt", description = "마감일자", required = false, schema = @Schema(type = "string", example = "2024-04-02 23:59" ))
//    @Parameter(name = "aiTutSetAt", description = "AI 튜터 사용 여부", required = true, schema = @Schema(type = "string", example = "Y" ))
//    @Parameter(name = "eamTrget", description = "출제 대상(1.공통 2.개별)", required = true, schema = @Schema(type = "integer", example = "1" ))
//    @Parameter(name = "eamExmNum", description = "출제 문항수", required = true, schema = @Schema(type = "integer", example = "20" ))
//    @Parameter(name = "eamGdExmMun", description = "출제 문항수(상)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamAvUpExmMun", description = "출제 문항수(중상)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamAvExmMun", description = "출제 문항수(중)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamAvLwExmMun", description = "출제 문항수(중하)", required = true, schema = @Schema(type = "integer", example = "5" ))
//    @Parameter(name = "eamBdExmMun", description = "출제 문항수(하)", required = true, schema = @Schema(type = "integer", example = "5" ))
    public ResponseDTO<CustomBody> tchAiCustomLearnCreateEng(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {

        Map<String, Object> resultData = aiLearningEngService.createAiCustomLearining(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI맞춤학습-과제,수업(동일, 개별)");

    }

    @Loggable
    @GetMapping("/batch/ai/remedy-lrn/create/evl/get-target/eng")
    @Operation(summary = "대상 평가id 조회", description = "백앤드 배치 테스트를 위한 API 입니다.")
    @Parameter(name = "fromDt", description = "from Date(yyyymmdd)", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> findTargetEvlIdEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.aiLearningEngService.findTargetEvlList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "어제 날짜 종료된 평가id 목록 조회");

    }

    @Loggable
    @GetMapping("/batch/ai/remedy-lrn/create/evl/article/eng")
    @Operation(summary = "자동문항생성-AI 처방 학습-평가", description = "백앤드 배치 테스트를 위한 API 입니다. 입력된 평가id의 오답 article 을 가져옴")
    @Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "514"))
    // 2226(민간 개발)
    public ResponseDTO<CustomBody> autoCreateAiLearningEvlEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.aiLearningEngService.findAutoCreateAiLearningEvl(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문항생성-AI 처방 학습 - 생성할 article 목록 조회");

    }

    @Loggable
    @GetMapping("/batch/ai/remedy-lrn/create/task/get-target/eng")
    @Operation(summary = "대상 과제id 조회", description = "백앤드 배치 테스트를 위한 API 입니다. ")
    @Parameter(name = "fromDt", description = "from Date(yyyymmdd)", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> findTargetTaskIdEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.aiLearningEngService.findTargetTaskList(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "어제 날짜 종료된 과제id 목록 조회");

    }

    @Loggable
    @GetMapping("/batch/ai/remedy-lrn/create/task/article/eng")
    @Operation(summary = "자동문항생성-AI 처방 학습-평가", description = "백앤드 배치 테스트를 위한 API 입니다. 입력된 과제id의 오답 article 을 가져옴")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "11"))
    public ResponseDTO<CustomBody> autoCreateAiLearningTaskEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = this.aiLearningEngService.findAutoCreateAiLearningTask(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자동문항생성-AI 처방 학습 - 생성할 article 목록 조회");

    }

    @Loggable
    @GetMapping("/batch/ai/remedy-lrn/create/forced-execute/eng")
    @Operation(summary = "자동문항생성-AI 처방 학습-강제실행", description = "백앤드 배치 테스트를 위한 API 입니다. 배치 job을 강제로 실행시킴")
    @Parameter(name = "fromDt", description = "from DT(yyyymmdd)", required = false, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> autoCreateAiLearningForcedExecuteEng(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

//            Object resultData = this.aiLearningService.findAutoCreateAiLearningTask(paramData);

        Map<String, Object> evlResult = new HashMap<>();
        Map<String, Object> taskResult = new HashMap<>();

        evlResult = aiLearningEngService.createAiLearningBatchEvlEng(paramData);
        taskResult = aiLearningEngService.createAiLearningBatchTaskEng(paramData);

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
        resultMap.put("과제 스킵 or 실패 건수", taskResult.get("failCnt"));
        resultMap.put("총 소요시간(ms)", totalDur);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "executeCreateAiLearningSets 배치 실행됨");

    }

    @Loggable
    @RequestMapping(value = "/tch/ai/custom-lrn/create/eng/targetList", method = {RequestMethod.GET})
    @Operation(summary = "AI맞춤학습-대상학생목록", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "engbook1315-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "ba0856357bb549f9bc2316ab692c03d0"))
    @Parameter(name = "textbkId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1150"))
    @Parameter(name = "crculId", description = "수업중인 커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "120"))
    public ResponseDTO<CustomBody> tchAiCustomLrnCreateEngTargetList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Map<String, Object> resultData = aiLearningEngService.tchAiCustomLrnCreateEngTargetList(paramData);

        if (MapUtils.getBoolean(resultData, "resultOk", false)) {
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "AI맞춤학습-대상학생목록");
        } else {
            return AidtCommonUtil.makeResultFail(
                    paramData, resultData,
                    MapUtils.getString(resultData, "resultMsg", "학생 목록을 조회하는데 실패 했습니다."));
        }
    }
}
