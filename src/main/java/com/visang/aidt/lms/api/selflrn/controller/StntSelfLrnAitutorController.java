package com.visang.aidt.lms.api.selflrn.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.selflrn.service.StntSelfLrnAitutorService;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "(학생) 자기주도AI학습 API", description = "자기주도AI학습 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntSelfLrnAitutorController {

    private final StntSelfLrnAitutorService stntSelfLrnAitutorService;

    // 자기주도AI학습 생성
    @Loggable
    @RequestMapping(value = "/stnt/self-lrn/aitutor/create" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도AI학습 생성", description = "자기주도AI학습 생성")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "qa10-s1",
                    "textbkId" : 20,
                    "claId" : "22e38ef4c33049bcacf7716e302e28b4",
                    "stdNm" : "Lesson 1 > pronunciation,Grammar,Reading",
                    "enLrngDivIds" : "2157,2150,2151",
                    "unitNum" : 1
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnAitutorCreate(@RequestBody Map<String, Object> paramData) throws Exception {
        int unitNum = MapUtils.getInteger(paramData, "unitNum", 0);
        // 이후 하드코딩 수정 필요
        String enLrngDivIds = null;
        String stdNm = null;
        switch (unitNum) {
            case 1: // 1일 경우는 넘어온 값으로 처리
                /*enLrngDivIds = "2149,2150,2151";//듣기,문법,읽기
                stdNm = "Lesson 1 > Listening,Grammar,Reading";*/
                enLrngDivIds = MapUtils.getString(paramData, "enLrngDivIds");
                stdNm = MapUtils.getString(paramData, "stdNm");
                break;
            case 2:
                enLrngDivIds = "2149,2150,2151";//듣기,문법,읽기
                stdNm = "Lesson 2 > Listening,Grammar,Reading";
                break;
            case 3:
                enLrngDivIds = "2150,2149,2151";//문법,듣기,읽기
                stdNm = "Lesson 3 > Grammar,Listening,Reading";
                break;
            case 4:
                // lesson4에 발음활동 나오도록 수정 요청으로 잠시 변경(김새미시피님)
                // enLrngDivIds = "2151,2150,2149";//읽기,문법,듣기
                enLrngDivIds = "2151,2150,2157";//읽기,문법,발음
                stdNm = "Lesson 4 > Reading,Grammar,Pronunciation";
                break;
            case 5:
                enLrngDivIds = "2153,2154,2156";//말하기,쓰기,어휘
                stdNm = "Lesson 5 > Speaking,Writing,Vocabulary";
                break;
            case 6:
                enLrngDivIds = "2154,2153,2156";//쓰기,말하기,어휘
                stdNm = "Lesson 6 > Writing,Speaking,Vocabulary";
                break;
            case 7:
                enLrngDivIds = "2156,2154,2153";//어휘,쓰기,말하기
                stdNm = "Lesson 7 > Vocabulary,Writing,Speaking";
                break;
            case 8:
                enLrngDivIds = "2157,2156,2149";//발음,어휘,듣기
                stdNm = "Lesson 8 > Pronunciation,Vocabulary,Listening";
                break;
            case 9:
                enLrngDivIds = "2151,2150,2149";//읽기,문법,듣기
                stdNm = "Lesson 9 > Reading,Grammar,Listening";
                break;
            case 10:
                enLrngDivIds = "2153,2154,2156";//말하기,쓰기,어휘
                stdNm = "Lesson 10 > Speaking,Writing,Vocabulary";
                break;
            default:
                enLrngDivIds = "2149,2150,2151";//듣기,문법,읽기
                stdNm = "Lesson 1 > Listening,Grammar,Reading";
                break;
        }
        // 하드코딩 수정필요 stdNm 없으면 학습종료(/stnt/self-lrn/end) 안되므로 임시 추가
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "stdNm"))) {
            paramData.put("stdNm", stdNm);
        }
        // 테스트용 임시 하드코딩, 수정필요-e
        paramData.put("enLrngDivIds", enLrngDivIds);

        Map<String, Object> resultData = stntSelfLrnAitutorService.startAitutor(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도AI학습 생성");
    }

    // 자기주도AI학습 다음문제 받기
    @Loggable
    @RequestMapping( value = "/stnt/self-lrn/aitutor/next-question/receive" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도AI학습 다음문제 받기", description = "자기주도AI학습 다음문제 받기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "qa10-s1",
                    "stdId" : 403,
                    "libtextType1" : "어휘"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnAitutorNextQuestionReceive(@RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = stntSelfLrnAitutorService.findAitutorQuestion(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도AI학습 다음문제 받기");
    }

    // 자기주도AI학습 답안 제출
    @Loggable
    @RequestMapping( value = "/stnt/self-lrn/aitutor/submit/answer" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도AI학습 답안 제출", description = "자기주도AI학습 답안 제출")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "qa10-s1",
                    "stdResultId" : "758",
                    "subMitAnw" : "2,3",
                    "subMitAnwUrl" : "www.naver.com",
                    "errata" : "1"
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnAitutorSubmitAnswer(@RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = stntSelfLrnAitutorService.saveAitutorSubmitAnswer(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도AI학습 답안 제출");
    }

    // 자기주도AI학습 채팅저장
    @Loggable
    @RequestMapping(value =  "/stnt/self-lrn/aitutor/submit/chat" , method = {RequestMethod.POST})
    @Operation(summary = "자기주도AI학습 채팅저장", description = "자기주도AI학습 채팅저장")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                {
                    "userId" : "qa10-s1",
                    "stdResultId" : 758,
                    "chatType" : "aitutor",
                    "aiCall" : "aitutor",
                    "aiReturn" : "문제를 풀어보자",
                    "articleId" : 123456
                    "subId" : 0
                }
            """)
            })
    )
    public ResponseDTO<CustomBody> saveStntSelfLrnAitutorSubmitChat( @RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = stntSelfLrnAitutorService.saveAitutorSubmitChat(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도AI학습 채팅저장");
    }

    // 자기주도AI학습이 존재하는 날짜 목록 보기
    @Loggable
    @GetMapping("/stnt/self-lrn/aitutor/date")
    @Operation(summary = "자기주도AI학습이 존재하는 날짜 목록 보기", description = "자기주도AI학습이 존재하는 날짜 목록 보기")
    @Parameter(name = "userId", description = "학생 ID", required = true)
    @Parameter(name = "learningType", description = "학습 유형 / 1 : 전체, 2 : 평가, 3 : 과제, 4 : 자기주도 학습", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> findStntSelfLrnAitutorResultDate(
            @RequestParam(name = "userId", defaultValue = "qa10-s1") String userId,
            @RequestParam(name = "learningType", defaultValue = "1") String learningType
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("userId", userId);
        paramData.put("learningType", learningType);

        Map<String, Object> resultData = stntSelfLrnAitutorService.findAitutorExistDate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도AI학습이 존재하는 날짜 목록 보기");
    }

    // 자기주도AI학습 초기화 (테스트를 위한 임시 초기화)
    @Loggable
    @GetMapping("/stnt/self-lrn/aitutor/question-init")
    @Operation(summary = "자기주도AI학습 초기화 (임시로 푼 문항 삭제)", description = "자기주도AI학습 초기화")
    @Parameter(name = "stdId", description = "학습 ID", required = true)
    public ResponseDTO<CustomBody> aitutorQuestionInit(
            @RequestParam(name = "stdId", defaultValue = "262") String stdId
    ) throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("stdId", stdId);

        Map<String, Object> resultData = stntSelfLrnAitutorService.aitutrQuestionInit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기주도AI학습 초기화");
    }
}
