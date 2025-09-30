package com.visang.aidt.lms.api.wrongnote.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.wrongnote.service.StntWrongnoteService;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * (학생)오답노트 API Controller
 */

@Slf4j
@RestController
@Tag(name = "(학생) 오답노트 API", description = "(학생) 오답노트 API")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntWrongnoteController {
    private final StntWrongnoteService stntWrongnoteService;

    @Loggable
    @RequestMapping(value = "/stnt/wrong-note/list", method = {RequestMethod.GET})
    @Operation(summary = "오답노트 목록 조회하기", description = "오답노트 목록 조회하기")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "학급ID", required = false, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "430e8400-e29b-41d4-a746-446655440000"))
    @Parameter(name = "condition", description = "검색 유형", required = true, schema = @Schema(type = "string", allowableValues = {"name", "date", "curri"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "검색어(키워드)", required = false, schema = @Schema(type = "string", example = "수플러_샘플_초4_1_240119"))
    @Parameter(name = "clsfCdgubun", description = "교과구분", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "tab", description = "검색 탭", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "dateType", description = "날짜조건(해당 값 비어있으면 전체 해당)", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "dayDate", description = "일날짜", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "startDate", description = "시작날짜", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "endDate", description = "종료날짜", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "monthDate", description = "월날짜", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> getWrongnoteList(
            @RequestParam(name = "textbkId", defaultValue = "") long textbkId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "condition", defaultValue = "") String condition,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "clsfCdgubun", defaultValue = "") String clsfCdgubun,
            @RequestParam(name = "tab", defaultValue = "") String tab,
            @RequestParam(name = "dateType", defaultValue = "") String dateType,
            @RequestParam(name = "dayDate", defaultValue = "") String dayDate,
            @RequestParam(name = "startDate", defaultValue = "") String startDate,
            @RequestParam(name = "endDate", defaultValue = "") String endDate,
            @RequestParam(name = "monthDate", defaultValue = "") String monthDate,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        // 검색유형이 날짜(date) 인 경우
        if ("date".equals(String.valueOf(paramData.get("condition")))) {
            String[] keywords = StringUtils.split((String) paramData.get("keyword"), "~");
            paramData.put("st_dt", keywords[0]);
            paramData.put("ed_dt", keywords[1]);
        }
        Object resultData = stntWrongnoteService.getWrongnoteList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 목록 조회하기");
    }

    @Loggable
    @RequestMapping(value = "/stnt/wrong-note/won-asw/list", method = {RequestMethod.GET})
    @Operation(summary = "오답노트 오답 목록 조회", description = "오답노트 오답 목록 조회")
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "vsstu1"))
    @Parameter(name = "trgtId", description = "타겟 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "wrtYmd", description = "작성날짜", required = true, schema = @Schema(type = "string", example = "20240123"))
    public ResponseDTO<CustomBody> getWrongnoteWonaswList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntWrongnoteService.getWrongnoteWonaswList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 오답 목록 조회");


    }

    @Loggable
    @RequestMapping(value = "/stnt/wrong-note/won-asw/tag/save", method = {RequestMethod.POST})
    @Operation(summary = "오답노트 오답 모듈 태그정보 저장(수정)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wonAswId\":1," +
                            "\"wonTag\":\"1\"," +
                            "\"gubun\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveWrongnoteWonaswTag(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntWrongnoteService.saveWrongnoteWonaswTag(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 오답 모듈 태그정보 저장(수정)");

    }

    @Loggable
    @Deprecated
    /** 테스트 용으로 작성한 api임 사용하지 말것. */
    // @RequestMapping(value = "/stnt/wrong-note/test1", method = {RequestMethod.POST})
    //@Operation(summary = "메소드 테스트용", description = "오답노트 생성")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":1," +
                            "\"wrtYmd\":1," +
                            "\"wonAnwClsfCd\":1," +
                            "\"wonAnwNm\":1," +
                            "\"tabId\":1," +
                            "\"moduleId\":1," +
                            "\"subId\":1," +
                            "\"wonAnwTgId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveWrongnoteTest(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntWrongnoteService.createWonAswNote(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 테스트");

    }

    @Loggable
    // @RequestMapping(value = "/stnt/wrong-note/test2", method = {RequestMethod.POST})
    // @Operation(summary = "메소드 테스트용22", description = "오답노트 삭제")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":1," +
                            "\"wonAnwClsfCd\":1," +
                            "\"tabId\":1," +
                            "\"moduleId\":1," +
                            "\"subId\":1," +
                            "\"wonAnwTgId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> saveWrongnoteTest2(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntWrongnoteService.deleteWonAswNote(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 테스트2");

    }

    @Loggable
    // @RequestMapping(value = "/stnt/wrong-note/test3", method = {RequestMethod.POST})
    // @Operation(summary = "메소드 테스트용3", description = "평가 메소드 테스트")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":10" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> createStntWrongnoteEvlId(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntWrongnoteService.createStntWrongnoteEvlId(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 테스트3");

    }

    @Loggable
    // @RequestMapping(value = "/stnt/wrong-note/test4", method = {RequestMethod.POST})
    // @Operation(summary = "메소드 테스트용4", description = "과제 메소드 테스트")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":11" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> createStntWrongnoteTaskId(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntWrongnoteService.createStntWrongnoteTaskId(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 테스트4");

    }

    @Loggable
    // @RequestMapping(value = "/stnt/wrong-note/test5", method = {RequestMethod.POST})
    // @Operation(summary = "메소드 테스트용", description = "오답노트 생성")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "wrterId" : "1",
                                    "wonAnwClsfCd" : "1",
                                    "wonAnwTgId" : 53
                                }
                            """)
            }
            ))
    public ResponseDTO<CustomBody> createStntWrongnote(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = stntWrongnoteService.createWonAswNote(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 테스트5");

    }

    @Loggable
    /* [임시] 테스트 용 추가 */
    @RequestMapping(value = "/stnt/wrong-note/won-asw-name", method = {RequestMethod.GET})
    @Operation(summary = "오답노트 이름 조회 (임시)", description = "오답노트 이름 조회 (임시)")
    @Parameter(name = "textbookId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "281"))
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathreal79-t"))
    @Parameter(name = "claId", description = "학급ID", required = true, schema = @Schema(type = "string", example = "45450d6e56f3485f823fe768ac333e21"))
    @Parameter(name = "crculId", description = "커리큘럼 ID(key)", required = true, schema = @Schema(type = "integer", example = "14"))
    public ResponseDTO<CustomBody> getWrongnoteWonAswNm(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        Object resultData = stntWrongnoteService.getWonAnwNm(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 이름 조회 (임시)");

    }



    @Loggable
    @RequestMapping(value = "/stnt/wrong-note/retry", method = {RequestMethod.POST})
    @Operation(summary = "오답노트 다시풀기", description = "오답노트 다시풀기")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            """
                                    {
                                            "noteNm": "오답노트 테스트",
                                            "stntId": "engbook1400-s1",
                                            "wonAnwInfo": [
                                                {       
                                                    "textbkid": "1150",
                                                    "wrterid": "engbook1400-s1",
                                                    "wrtymd": "20250311",
                                                    "trgtid": "3639"
                                                },
                                                {       
                                                    "textbkid": "1150",
                                                    "wrterid":  "engbook1400-s1",
                                                    "wrtymd": "20250203",
                                                    "trgtid": "1405459"
                                                }
                                            ]
                                        }
                                    """
                    )
            }
            ))
    public ResponseDTO<CustomBody> createRetry(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
        Map<String, Object> resultData = stntWrongnoteService.createRetry(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 다시풀기");

    }

    @Loggable
    @RequestMapping(value = "/stnt/wrong-note/retry/save", method = {RequestMethod.POST})
    @Operation(summary = "(공통) 응시(article)자동저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"claId\":\"4G100000214_2025_10440003\"," +
                            "\"evlResultId\":1," +
                            "\"evlIemId\":\"1\"," +
                            "\"subId\":0," +
                            "\"errata\":1," +
                            "\"subMitAnw\":1," +
                            "\"subMitAnwUrl\":\"\"," +
                            "\"evlTime\":\"\"," +
                            "\"hntUseAt\":\"Y\"" +
                            "}"
                    )
            })
    )
    public ResponseDTO<CustomBody> saveWrongNoteRetry(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
            //필수 데이터
            //stdWonAnwId
            //subMitAnw
            //subMitAnwUrl
            //errata
            //articleId
            //subId
            //wrterId
            //textbkId

        Object resultData = stntWrongnoteService.saveWrongNoteRetry(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 응시(article)자동저장");

    }

    @Loggable
    @RequestMapping(value = "/stnt/wrong-note/retry/submit", method = {RequestMethod.POST})
    @Operation(summary = "(공통) 최종 제출", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"evlId\":1," +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"evlResultId\":1," +
                            "\"evlIemId\":\"1\"," +
                            "\"subId\":0," +
                            "\"errata\":1," +
                            "\"subMitAnw\":1," +
                            "\"subMitAnwUrl\":\"\"," +
                            "\"evlTime\":\"\"," +
                            "\"hntUseAt\":\"Y\"" +
                            "}"
                    )
            })
    )
    public ResponseDTO<CustomBody> submitWrongNoteRetry(
            @RequestBody Map<String, Object> paramData
    )throws Exception {
            //필수 데이터
            //stdWonAnwId
            //subMitAnw
            //subMitAnwUrl
            //errata
            //articleId
            //subId
            //wrterId
            //textbkId

        Object resultData = stntWrongnoteService.submitWrongNoteRetry(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 응시(article)자동저장");

    }


    @Loggable
    @RequestMapping(value = "/stnt/wrong-note/statis", method = {RequestMethod.GET})
    @Operation(summary = "오답노트 통계", description = "오답노트 통계")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathreal79-t"))
    public ResponseDTO<CustomBody> getWrongnoteStatis(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception{

        Object resultData = stntWrongnoteService.getWrongnoteStatis(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "오답노트 통계");

    }
}



