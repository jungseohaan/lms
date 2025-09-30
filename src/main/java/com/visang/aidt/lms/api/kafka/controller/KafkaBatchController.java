package com.visang.aidt.lms.api.kafka.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "KAFKA 배치 API", description = "KAFKA 배치 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class KafkaBatchController {
    private final KafkaBatchService kafkaBatchService;

    @PostMapping(value = {"/dashbord/batch"})
    @Operation(summary = "CDC 관련 계산로직 태우기 위한 API", description = "CDC 관련 계산로직 태우기 위한 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "claId": "308ad5afba8f11ee88c00242ac110002",
                                "textbookId": "10"
                            }
                            """
                    )
            }
            ))
    public void kafkaBatch(@RequestBody Map<String, Object> paramData) throws Exception {
        kafkaBatchService.processSelectOneCycle(paramData);
    }

    @PostMapping(value = {"/slf/slfPerDataSetting"})
    @Operation(summary = "페르소나 복제본 데이터 insert", description = "페르소나 복제본 데이터 insert")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "targetClassId": ["615bce1f7dbb4e23a549ca07effec9e5,615bce1f7dbb4e23a549ca07effec9e1"]
                            "copyClassId": ["ce792f2944074cf8a29c68d24bbc8df0", "ce792f2944074cf8a29c68d24bbc8df01"]
                        }
                        """
                    )
            }
            ))
    public void slfPerDataSetting(@RequestBody Map<String, Object> paramData) throws Exception {
        List<String> targetClassIds = (List<String>) paramData.get("targetClassId");
        List<String> copyClassIds = (List<String>) paramData.get("copyClassId");

        // targetClassId와 copyClassId의 길이가 같은지 확인
        if (targetClassIds.size() != copyClassIds.size()) {
            throw new Exception("targetClassId와 copyClassId의 개수가 일치하지 않습니다.");
        }

        // 각 쌍에 대해 kafkaBatchService.slfPerDataSetting 호출
        for (int i = 0; i < targetClassIds.size(); i++) {
            Map<String, Object> singlePairParam = new HashMap<>();
            singlePairParam.put("targetClassId", targetClassIds.get(i));
            singlePairParam.put("copyClassId", copyClassIds.get(i));

            // 각 쌍마다 kafkaBatchService.slfPerDataSetting 호출
            kafkaBatchService.slfPerDataSetting(singlePairParam);
        }
    }

    @PostMapping(value = {"/dashbord/historicalDataLoopBatch"})
    @Operation(summary = "CDC 관련 계산로직 태우기 위한 API", description = "CDC 관련 과거데이터 계산로직 태우기 위한 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "claId": "308ad5afba8f11ee88c00242ac110002",
                                "textbookId": "10"
                            }
                            """
                    )
            }
            ))
    public void kafkaBatchLoop(@RequestBody Map<String, Object> paramData) throws Exception {
        kafkaBatchService.kafkaBatchLoopSetting(paramData);
    }


    @PostMapping(value = {"/dashbord/historicalDataLoopBatchLoop"})
    @Operation(summary = "CDC 관련 계산로직 여러 학급에 대해 태우기 위한 API", description = "CDC 관련 과거데이터 계산로직을 여러 학급에 대해 태우기 위한 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                        {
                            "writerIds": ["tr-permath7211-t", "permath7211-t"]
                        }
                        """
                    )
            }
            ))
    public void historyKafkaBatchLoop(@RequestBody Map<String, Object> paramData) throws Exception {
        List<String> writerIds = (List<String>) paramData.get("writerIds");
            for (String writerId : writerIds) {
                Map<String, Object> singleClaParams = new HashMap<>();
                Map<String,Object> textbook = kafkaBatchService.getTextbook(writerId);
                singleClaParams.put("claId", textbook.get("claId"));
                singleClaParams.put("textbookId", textbook.get("textbkId"));
                log.debug("historyKafkaBatchLoop", singleClaParams);
                kafkaBatchService.kafkaBatchLoopSetting(singleClaParams);
            }
    }

    @Operation(summary = "평가 ID 및 시트지 및 아티클ID API", description = "평가 ID 및 시트지 및 아티클ID API")
    @Parameter(name = "targetId",
            description = "구분 (교과서(1), 과제(2), 평가(3), 스스로학습(4))\n" +
                    "- 1 선택 시: setsId, articleId 필수\n" +
                    "- 2 선택 시: setsId, articleId, taskId 필수\n" +
                    "- 3 선택 시: setsId, articleId, evlId 필수\n" +
                    "- 4 선택 시: articleId, slfId 필수",
            schema = @Schema(type = "String", example = "1", allowableValues = {"1", "2", "3", "4"}))
    @RequestMapping(value = "/article/check", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> conditionInfo(
            @Parameter(name = "setsId", description = "시트지ID", schema = @Schema(type = "String", example = ""))
            @RequestParam(name = "setsId", defaultValue = "", required = false) String setsId,

            @Parameter(name = "articleId", description = "아티클ID", schema = @Schema(type = "String", example = ""))
            @RequestParam(name = "articleId", defaultValue = "", required = false) String articleId,

            @Parameter(name = "evlId", description = "평가ID", schema = @Schema(type = "String", example = ""))
            @RequestParam(name = "evlId", defaultValue = "", required = false) String evlId,

            @Parameter(name = "taskId", description = "과제ID", schema = @Schema(type = "String", example = ""))
            @RequestParam(name = "taskId", defaultValue = "", required = false) String taskId,

            @Parameter(name = "slfId", description = "스스로학습ID", schema = @Schema(type = "String", example = ""))
            @RequestParam(name = "slfId", defaultValue = "", required = false) String slfId,

            @Parameter(name = "targetId",
                    description = "구분 (교과서(1), 과제(2), 평가(3), 스스로학습(4))",
                    schema = @Schema(type = "String",
                            example = "1",
                            allowableValues = {"1", "2", "3", "4"}))
            @RequestParam(name = "targetId", defaultValue = "", required = true) String targetId,

            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        // targetId 값에 따른 필수 파라미터 검증
        switch (targetId) {
            case "1": // 교과서
                validateAtLeastOneParam(new String[]{setsId, articleId}, new String[]{"setsId", "articleId"});
                break;
            case "2": // 과제
                validateAtLeastOneParam(new String[]{setsId, articleId, taskId}, new String[]{"setsId", "articleId", "taskId"});
                break;
            case "3": // 평가
                validateAtLeastOneParam(new String[]{setsId, articleId, evlId}, new String[]{"setsId", "articleId", "evlId"});
                break;
            case "4": // 스스로학습
                validateAtLeastOneParam(new String[]{articleId, slfId}, new String[]{"articleId", "slfId"});
                break;
            default:
                throw new IllegalArgumentException("유효하지 않은 targetId입니다. 1, 2, 3, 4 중 하나를 입력해주세요.");
        }

        List<Map> resultMap = new ArrayList<>();
        if(!paramData.isEmpty()){
            resultMap = kafkaBatchService.getAticleCheck(paramData);
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "아티클 검색결과");
    }

    /**
     * 나열된 파라미터 중 하나 이상이 값이 있는지 검사하는 메서드
     * @param params 검사할 파라미터 값들의 배열
     * @param paramNames 파라미터 이름들의 배열 (에러 메시지용)
     * @throws IllegalArgumentException 모든 파라미터가 비어있을 경우 발생
     */
    private void validateAtLeastOneParam(String[] params, String[] paramNames) {
        boolean hasValue = false;
        for (String param : params) {
            if (param != null && !param.trim().isEmpty()) {
                hasValue = true;
                break;
            }
        }
        if (!hasValue) {
            throw new IllegalArgumentException(
                    String.format("다음 파라미터 중 최소 하나는 값이 있어야 합니다: %s", String.join(", ", paramNames))
            );
        }
    }

}
