package com.visang.aidt.lms.api.mq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.configuration.NatsTopicConfig;
import com.visang.aidt.lms.api.mq.dto.assessment.AssessmentSubmittedMqDto;
import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentFinishedMqDto;
import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentGaveMqDto;
import com.visang.aidt.lms.api.mq.dto.media.MediaDto;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskInfo;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskMqDto;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import com.visang.aidt.lms.api.mq.dto.teaching.ReorganizedInfo;
import com.visang.aidt.lms.api.mq.dto.teaching.TeachingReorganizedMqDto;
import com.visang.aidt.lms.api.mq.service.*;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "(NATS) 메세지큐 API", description = "(NATS) 메세지큐 API")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class NatsSendController {

    private final NatsTopicConfig natsTopicConfig;
    private final NatsSendService natsSendService;
    private final AssignmentFinishedService assignmentFinishedService;
    private final AssessmentSubmittedService assessmentSubmittedService;
    private final AssignmentGaveService assignmentGaveService;
    private final QueryAskService queryAskService;
    private final TeachingReorganizedService teachingReorganizedService;
    private final MediaPlayedService mediaPlayedService;
    private final ObjectMapper objectMapper;

    private final String RESULT = "result";
    private final String SUCCESS = "success";

    @Loggable
    @PostMapping("/mq/pushRealFinishMQ.json")
    @Operation(summary = "교사) 수업 종료 실시간 메세지큐 전송")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "partnerId": "9d3959d5-5a4c-5311-8ce2-5c8e41ba6604",
                                "accessToken": {
                                    "accessId": "cf5c6febff5e7433e319fd88403b5a62f12efe03",
                                    "token": "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU"
                                },
                                "userId": "mathbook3065-t",
                                "claIdx": 21078,
                                "textbkId": 1150
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushRealFinishMQ(@RequestBody RealMqReqDto paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        natsSendService.sendClassFinishInfo(paramData); // real 종료 정보 전송
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "교사) 수업 종료 실시간 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/pushRealStartMQ.json")
    @Operation(summary = "교사) 수업 시작 실시간 메세지큐 전송")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "partnerId": "9d3959d5-5a4c-5311-8ce2-5c8e41ba6604",
                                "accessToken": {
                                    "accessId": "cf5c6febff5e7433e319fd88403b5a62f12efe03",
                                    "token": "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU"
                                },
                                "userId": "engbook255-t",
                                "claIdx": 1787,
                                "textbkId": 1150
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushRealStartMQ(@RequestBody RealMqReqDto paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        natsSendService.sendClassStartInfo(paramData); // real 시작 정보 전송
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "교사) 수업 시작 실시간 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/sendAssessmentSubmittedBulkMQ")
    @Operation(summary = "학생) 평가결과 이력 메세지큐 전송")
    public ResponseDTO<CustomBody> sendAssessmentSubmittedBulkMQ() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        List<AssessmentSubmittedMqDto> resultList = assessmentSubmittedService.createAssessmentResultRequest(RealMqReqDto.builder().build());
        sendToNats(resultList);
        assessmentSubmittedService.modifyEvlMqTrnAt();
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(Map.of(), resultList, "학생) 평가결과 이력 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/sendAssessmentSubmittedJson")
    @Operation(summary = "학생) 평가결과 이력 메세지큐 JSON 데이터 확인")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "학생 ID", value = """
                            {
                                "userId": "mathbook1404-s3"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> sendAssessmentSubmittedBulkJson(@RequestBody RealMqReqDto paramData) throws Exception {
        List<AssessmentSubmittedMqDto> resultData = assessmentSubmittedService.createAssessmentResultRequest(
                RealMqReqDto.builder()
                        .userId(paramData.getUserId())
                        .build());

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생) 평가결과 이력 메세지큐 JSON 데이터 확인");
    }

    @Loggable
    @PostMapping("/mq/pushAssignmentGaveBulkMQ")
    @Operation(summary = "교사) 과제등록 메세지큐 전송")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "testStartTime": "2024-02-23 05:09:22",
                                "testEndTime": "2024-02-24 05:09:22"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushAssignmentGaveBulkMQ(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String[] dateTimes = extractDateTimeParams(paramData);

        RealMqReqDto realMqReqDto = RealMqReqDto.builder()
                .startTime(dateTimes[0])
                .endTime(dateTimes[1])
                .build();

        List<AssignmentGaveMqDto> resultList = assignmentGaveService.createAssignmentGaveMq(realMqReqDto);
        sendToNats(resultList);
        assignmentGaveService.updateBulkTaskMqTrnLog();
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "교사) 과제등록 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/pushAssignmentFinishedBulkMQ")
    @Operation(summary = "학생) 과제제출 메세지큐 전송")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "testStartTime": "2024-05-28 05:09:22",
                                "testEndTime": "2025-03-02 05:09:22"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushAssignmentFinishedBulkMQ(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String[] dateTimes = extractDateTimeParams(paramData);

        RealMqReqDto realMqReqDto = RealMqReqDto.builder()
                .startTime(dateTimes[0])
                .endTime(dateTimes[1])
                .build();

        List<AssignmentFinishedMqDto> resultData = assignmentFinishedService.createAssignmentFinishedMq(realMqReqDto);
        sendToNats(resultData);
        assignmentFinishedService.updateAssignmentFinishedSendAt();
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생) 과제제출 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/pushAssignmentGaveJson")
    @Operation(summary = "교사) 과제등록 메세지큐 JSON 데이터 확인")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "userId": "mathbook227-s2"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushAssignmentGaveJson(@RequestBody RealMqReqDto paramData) throws Exception {
        List<AssignmentGaveMqDto> resultData = assignmentGaveService.createAssignmentGaveMq(
                RealMqReqDto.builder()
                        .userId(paramData.getUserId())
                        .build());


        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사) 과제등록 메세지큐 JSON 데이터 확인");
    }

    @Loggable
    @PostMapping("/mq/pushAssignmentFinishedJson")
    @Operation(summary = "학생) 과제제출 메세지큐 JSON 데이터 확인")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "학생 ID", value = """
                            {
                                "userId": "mathbook2194-s1"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushAssignmentFinishedJson(@RequestBody RealMqReqDto paramData) throws Exception {
        List<AssignmentFinishedMqDto> resultData = assignmentFinishedService.createAssignmentFinishedMq(
                RealMqReqDto.builder()
                        .userId(paramData.getUserId())
                        .build());

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생) 과제제출 메세지큐 JSON 데이터 확인");
    }



    @Loggable
    @PostMapping("/mq/stnt/ai-chatbot/logs")
    @Operation(summary = "학생) AiTutor 질의 데이터 저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "학생 ID", value = """
                            {
                                "userId": "engreal109-s1"
                            }
                            """
                    )
            }
            ))
    public Object insertLogTableQueryAsk(@RequestBody Map<String, Object> paramData) throws Exception{
        Object resultData =  queryAskService.insertQueryAskLog(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) AI튜터(챗봇) 질의 데이터 적재 성공");
    }

    @Loggable
    @PostMapping("/mq/stnt/ai-chatbot/logs/modify")
    @Operation(summary = "학생) AiTutor 응답 시간 데이터 수정")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "queryAskedId": 57
                            }
                            """
                    )
            }
            ))
    public Object modifyLogTableQueryAsk(@RequestBody Map<String, Object> paramData) throws Exception {
        Object resultData = queryAskService.modifyQueryAskLog(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) AI튜터(챗봇) 질의 데이터 응답시간 수정 성공");
    }

    @Loggable
    @PostMapping("/mq/pushQueryAskBulkMQ")
    @Operation(summary = "학생) 질의 메세지큐 전송")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "testStartTime": "2024-06-22 13:51:16",
                                "testEndTime": "2024-06-25 10:22:47"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushQueryAskBulkMQ(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String[] dateTimes = extractDateTimeParams(paramData);

        QueryAskInfo queryAskInfo =
                QueryAskInfo.builder()
                        .startTime(dateTimes[0])
                        .endTime(dateTimes[1])
                        .trnAt("N")
                        .build();

        List<QueryAskMqDto> resultList = queryAskService.createQueryAskMq(queryAskInfo);
        sendToNats(resultList);
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "학생) 질의 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/sendQueryAskJson")
    @Operation(summary = "학생) 질의 JSON 데이터 확인")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "학생 ID", value = """
                            {
                                "userId": "mathbook1404-s3"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> sendQueryAskBulkJson(@RequestBody RealMqReqDto paramData) throws Exception {
        List<QueryAskMqDto> resultData = queryAskService.createQueryAskMq(
                QueryAskInfo.builder()
                        .userId(paramData.getUserId())
                        .build());

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생) 질의 JSON 데이터 확인");
    }

    @Loggable
    @PostMapping("/mq/pushTeachingReorganizedBulkMQ")
    @Operation(summary = "교사) 교수 활동 이력 메세지큐 전송")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "testStartTime": "2024-06-22 13:51:16",
                                "testEndTime": "2024-06-25 10:22:47"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushTeachingReorganizedBulkMQ(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String[] dateTimes = extractDateTimeParams(paramData);

        ReorganizedInfo reorganizedInfo = ReorganizedInfo.builder()
                .startTime(dateTimes[0])
                .endTime(dateTimes[1])
                .build();

        List<TeachingReorganizedMqDto> resultList = teachingReorganizedService.createTeachingReorganized(reorganizedInfo);
        sendToNats(resultList);
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "교사) 교수 활동 이력 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/pushMediaPlayedBulkMQ")
    @Operation(summary = "학생) 미디어 콘텐츠 사용 이력 메세지큐 전송")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                            {
                                "testStartTime": "2024-07-17 09:48:18",
                                "testEndTime": "2024-07-24 10:35:19"
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushMediaPlayedBulkMQ(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        String[] dateTimes = extractDateTimeParams(paramData);
        String testStartTime = dateTimes[0];
        String testEndTime = dateTimes[1];

        List<MediaDto> mediaMqList = mediaPlayedService.createMediaPlayedMq(testStartTime, testEndTime, "");
        sendToNats(mediaMqList);
        mediaPlayedService.modifyMediaPlayedUpdate(testStartTime, testEndTime);
        resultMap.put(RESULT, SUCCESS);

        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "학생) 미디어 콘텐츠 사용 이력 메세지큐 전송");
    }

    @Loggable
    @PostMapping("/mq/pushMediaPlayedJson")
    @Operation(summary = "학생) 미디어 JSON 데이터 확인")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "학생 ID", value = """
                            {
                                "testStartTime": "2024-07-17 09:48:18",
                                "testEndTime": "2024-07-24 10:35:19",
                                "userId": ""
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> pushMediaPlayedJson(@RequestBody Map<String, Object> paramData) throws Exception {
        String[] dateTimes = extractDateTimeParams(paramData);
        String testStartTime = dateTimes[0];
        String testEndTime = dateTimes[1];
        List<MediaDto> resultData = mediaPlayedService.createMediaPlayedMq(testStartTime, testEndTime, MapUtils.getString(paramData, "userId", ""));

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생) 미디어 JSON 데이터 확인");
    }

    /**
     * 공통 메서드: JSON 변환 및 메세지큐 전송
     */
    private <T> void sendToNats(List<T> resultList) throws Exception {
        if (resultList == null || resultList.isEmpty()) {
            return;
        }
        for (T resultData : resultList) {
            String jsonData = objectMapper.writeValueAsString(resultData);
            natsSendService.pushNatsMQ(natsTopicConfig.getBulkSendName(), jsonData);
        }
    }

    /**
     * 공통 메서드: 시작 및 종료 시간 추출
     */
    private String[] extractDateTimeParams(Map<String, Object> paramData) {
        String testStartTime = Optional.ofNullable(paramData.get("testStartTime"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("testStartTime 확인 필요"));

        String testEndTime = Optional.ofNullable(paramData.get("testEndTime"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("testEndTime 확인 필요"));

        return new String[]{testStartTime, testEndTime};
    }
}
