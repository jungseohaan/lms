package com.visang.aidt.lms.api.homework.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.homework.service.TchHomewkService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
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
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.controller
 * fileName : TchHomewkController
 * USER : hs84
 * date : 2024-01-24
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-24         hs84          최초 생성
 */
@Slf4j
@RestController
@Tag(name = "(교사) 과제 API", description = "(교사) 과제 API")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchHomewkController {
    private final TchHomewkService tchHomewkService;

    @Loggable
    @RequestMapping(value = "/tch/homewk/list", method = {RequestMethod.GET})
    @Operation(summary = "과제 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색 유형", required = false, schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "tmprStrgAt", description = "공유완료/설정미완료 구분 : Y/N (임시저장일때: Y)", required = false, schema = @Schema(type = "string", allowableValues = {"", "Y", "N"}, defaultValue = ""))
    @Parameter(name = "taskSttsCd", description = "필터조건(과제상태) : 1: 예정, 2: 진행중, 3: 완료, 4: 채점중, 5: 채점완료", required = false, schema = @Schema(type = "string", allowableValues = {"1", "2", "3", "4", "5"}, defaultValue = ""))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchHomewkList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.findTchHomewkList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/info", method = {RequestMethod.GET})
    @Operation(summary = "과제 정보 조회", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> tchHomewkInfo(
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.findTchHomewkInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/preview", method = {RequestMethod.GET})
    @Operation(summary = "과제 정보 조회 (상세 미리보기)", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> tchHomewkPreview(
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.findTchHomewkPreview(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 정보 조회 (상세 미리보기)");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/result/status", method = {RequestMethod.GET})
    @Operation(summary = "과제 결과 조회 (응시중_응시완료)", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchHomewkResultStatus(
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.findTchHomewkResultStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 결과 조회 (응시중_응시완료)");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/delete", method = {RequestMethod.POST})
    @Operation(summary = "과제 정보 삭제", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{\"taskId\":0}")
            }
            ))
    public ResponseDTO<CustomBody> tchEvalDelete(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.removeTchHomewkDelete(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 삭제");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/read-info", method = {RequestMethod.GET})
    @Operation(summary = "과제 자료설정 수정(조회)", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> tchHomewkReadInfo(
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.findTchHomewkReadInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 자료설정 수정(조회)");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/save", method = {RequestMethod.POST})
    @Operation(summary = "과제 자료설정 수정(저장)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":1," +
                            "\"rptAutoOthbcAt\":\"Y\"," +
                            "\"taskNm\":\"단원과제수학\"," +
                            "\"pdEvlStDt\":\"2024-01-17 13:00\"," +
                            "\"pdEvlEdDt\":\"2024-01-17 14:00\"," +
                            "\"ntTrnAt\":\"Y\"," +
                            "\"bbsSvAt\":\"Y\"," +
                            "\"bbsNm\":\"자료실에 저장 테스트\"," +
                            "\"tag\":\"고등수학\"," +
                            "\"cocnrAt\":\"Y\"," +
                            "\"timStAt\":\"Y\"," +
                            "\"timTime\":\"60:00\"," +
                            /*"\"prscrStdSetAt\":\"N\"," +
                            "\"prscrStdStDt\":\"\"," +
                            "\"prscrStdEdDt\":\"\"," +
                            "\"prscrStdNtTrnAt\":\"Y\"," + */
                            "\"aiTutSetAt\":\"Y\"," +
                            "\"rwdSetAt\":\"Y\"," +
                            "\"edGidAt\":\"Y\"," +
                            "\"edGidDc\":\"자료설정 테스트\"," +
                            "\"stntList\":[{ \"stntId\": \"stntId1\"},{ \"stntId\": \"stntId2\"}]," +
                            "\"slfEvlInfo\":{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"gbCd\":\"2\"," +
                            "\"wrterId\":\"1\"," +
                            "\"wrtDt\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"1\"," +
                            "\"slfPerEvlNm\":\"테스트\"," +
                            "\"stExposAt\":\"Y\"," +
                            "\"textbkId\":\"1\"," +
                            "\"tabId\":\"1\"," +
                            "\"taskId\":\"1\"," +
                            "\"evlId\":\"1\"," +
                            "\"setsId\":\"1\"," +
                            "\"resultDtlId\":\"1\"," +
                            "\"tmpltId\":\"1\"," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
                            "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
                            "}," +
                            "\"perEvlInfo\":{" +
                            "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
                            "\"gbCd\":\"2\"," +
                            "\"wrterId\":\"1\"," +
                            "\"wrtDt\":\"1\"," +
                            "\"slfPerEvlClsfCd\":\"2\"," +
                            "\"slfPerEvlNm\":\"테스트\"," +
                            "\"stExposAt\":\"Y\"," +
                            "\"textbkId\":\"1\"," +
                            "\"tabId\":\"1\"," +
                            "\"taskId\":\"1\"," +
                            "\"evlId\":\"1\"," +
                            "\"setsId\":\"1\"," +
                            "\"resultDtlId\":\"1\"," +
                            "\"tmpltId\":\"1\"," +
                            "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
                            "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
                            "\"is_change\":\"Y\"," +
                            "}" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchHomewkSave(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchHomewkService.createTchHomewkSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 자료설정 수정(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/init", method = {RequestMethod.POST})
    @Operation(summary = "과제 정보 초기화(개발편의 임시성)", description = "")
    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{\"taskId\":1}")
            }
            ))
    public ResponseDTO<CustomBody> tchTaskInit(
            //@RequestParam(name = "evlId", defaultValue = "") String evlId,
            //@Parameter(hidden = true) @RequestParam Map<String, Object> paramData
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.removeTaskInit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 정보 초기화(개발편의 임시성)");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/create", method = {RequestMethod.POST})
    @Operation(summary = "과제 생성(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":\"aidt3\"," +
                            "\"claId\":\"0cc175b9c0f1b6a831c399e269772662\"," +
                            "\"textbookId\":1," +
                            "\"taskNm\":\"단원과제수학\"," +
                            "\"eamMth\":3," +
                            "\"eamTrget\":1," +
                            "\"eamExmNum\":0," +
                            "\"eamGdExmMun\":0," +
                            "\"eamAvUpExmMun\":0," +
                            "\"eamAvExmMun\":0," +
                            "\"eamAvLwExmMun\":0," +
                            "\"eamBdExmMun\":0," +
                            "\"eamScp\":\"4,5\"," +
                            "\"setsId\":\"240\"," +
                            "\"prscrStdSetAt\":\"N\"," +
                            "\"prscrStdStDt\":\"2024.01.26 09:00\"," +
                            "\"prscrStdEdDt\":\"2024.01.28 18:00\"," +
                            "\"prscrStdNtTrnAt\":\"N\"," +
                            "\"prscrStdPdSet\":0," +
                            "\"stntList\":[{ \"stntId\": \"stntId1\"},{ \"stntId\": \"stntId2\"}]" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchHomewkCreate(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Map<String, Object> resultData = tchHomewkService.createTchHomewkCreate(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 생성(저장)");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/copy", method = RequestMethod.POST)
    @Operation(summary = "과제 정보 복사")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "taskId" : 8
                                }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalCopy(
            @RequestBody Map<String, Object> paramData
    )throws Exception {


        Map<String, Object> resultData = tchHomewkService.copyHomewkInfo(paramData);
        String resultMessage = "과제 정보 복사";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);


    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/auto/qstn/extr", method = RequestMethod.GET)
    @Operation(summary = "(과제) 문항 자동생성 추천 모듈정보 조회", description = "문항 자동 생성에 필요한 모듈을 추출한다. ( [교사] 수업 자료 > 과제 > 만들기 )")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "vstea1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "1dfd618eb8fb11ee88c00242ac110002"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamExmNum", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "6"))
    @Parameter(name = "eamGdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "eamAvUpExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "3"))
    @Parameter(name = "eamAvExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamAvLwExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamBdExmMun", description = "출제문항수", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "eamScp", description = "출제범위", required = true, schema = @Schema(type = "string", example = "870,872,956"))
    public ResponseDTO<CustomBody> tchHomewkAutoQstnExtr(
            @RequestParam(name = "wrterId", defaultValue = "") String wrterId,
            @RequestParam(name = "claId", defaultValue = "") String claId,
            @RequestParam(name = "textbookId", defaultValue = "") int textbookId,
            @RequestParam(name = "eamExmNum", defaultValue = "0") int eamExmNum,
            @RequestParam(name = "eamGdExmMun", defaultValue = "0") int eamGdExmMun,
            @RequestParam(name = "eamAvUpExmMun", defaultValue = "0") int eamAvUpExmMun,
            @RequestParam(name = "eamAvExmMun", defaultValue = "0") int eamAvExmMun,
            @RequestParam(name = "eamAvLwExmMun", defaultValue = "0") int eamAvLwExmMun,
            @RequestParam(name = "eamBdExmMun", defaultValue = "0") int eamBdExmMun,
            @RequestParam(name = "eamScp", defaultValue = "") String eamScp,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        /// 파라미터 유효성 검증
        List<String> requiredParams = Arrays.asList("wrterId", "claId", "textbookId", "eamExmNum", "eamGdExmMun", "eamAvUpExmMun", "eamAvExmMun", "eamAvLwExmMun", "eamBdExmMun", "eamScp");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);

        paramData.put("eamScp", AidtCommonUtil.strToLongList((String) paramData.get("eamScp"))); // 출제 범위

        Map<String, Object> resultData = tchHomewkService.findHomewkAutoQstnExtr(paramData);
        if (Boolean.FALSE.equals(resultData.get("resultOk"))) {
            return AidtCommonUtil.makeResultFail(paramData, null, (String) resultData.get("resultMsg"));
        }
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(평가) 문항 자동생성 추천 모듈정보 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/m-save", method = {RequestMethod.POST})
    @Operation(summary = "과제 마법봉 수정(저장)", description = "과제에 대한 세트지 정보의 변경된 내용을 반영(수정)한다. ( [교사] 학급 자료 > 과제 > 공유 완료 목록/설정 미완료 목록 > 마법봉 )")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":1," +
                            "\"setsId\":\"1\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalSaveByMagicWand(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.modifyHomewkSaveByMagicWand(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 마법봉 수정(저장)");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/create/for/textbk", method = {RequestMethod.POST})
    @Operation(summary = "(교사) 교과서 과제 생성 메소드", description = "메소드 테스트를 위한 API 입니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                [
                                    {
                                        "wrterId" : "vstea22" ,
                                        "claId" : "308ad2eaba8f11ee88c00242ac110002",
                                        "textbookId" : 1,
                                        "taskNm" : "taskNm_1",
                                        "setsId" : "1003",
                                        "pdEvlStDt" : "2024.01.26 09:00",
                                        "pdEvlEdDt" : "2024.01.28 18:00",
                                        "timTime" : "01:30:00"
                                    },
                                    {
                                        "wrterId" : "vstea23" ,
                                        "claId" : "308ad304ba8f11ee88c00242ac110002",
                                        "textbookId" : 1,
                                        "taskNm" : "taskNm_2",
                                        "setsId" : "1006",
                                        "pdEvlStDt" : "2024.01.26 09:00",
                                        "pdEvlEdDt" : "2024.01.28 18:00",
                                        "timTime" : "01:30:00"
                                    }
                                ]
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchHomewkCreateForTextbk(
            @RequestBody List<Map<String, Object>> paramData
    )throws Exception {
        Object resultData = tchHomewkService.createTchHomewkCreateForTextbk(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(교사) 교과서 과제 생성 메소드");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/period/change", method = {RequestMethod.POST})
    @Operation(summary = "과제 기간 수정", description = "과제 기간 수정")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """
                                {
                                    "taskId" : 1563,
                                    "taskSttsCd" : 1,
                                    "pdEvlStDt" : "2024.07.26 09:00",
                                    "pdEvlEdDt" : "2024.07.28 18:00"
                                }
                            """
                    )
            }
    ))
    public ResponseDTO<CustomBody> tchHomewkPeriodChange(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        // 구분값에 따른 필수항목 체크
        List<String> requiredParams = new ArrayList<>();
        requiredParams.add("taskId");
        requiredParams.add("taskSttsCd");
        requiredParams.add("pdEvlEdDt");

        if (MapUtils.getIntValue(paramData, "taskSttsCd", 0) == 1) { // 예정
            requiredParams.add("pdEvlStDt");
        }

        AidtCommonUtil.checkRequiredParameter(paramData,requiredParams);

        Object resultData = tchHomewkService.modifyTchHomewkPeriodChange(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 기간 수정");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/status/list", method = {RequestMethod.GET})
    @Operation(summary = "진행중,채점이 필요한 과제 목록", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook732-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "44e701f580a34e17b2a030a02527dc4a"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1522"))
    public ResponseDTO<CustomBody> tchHomewkStatusList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchHomewkService.findTchHomewkStatusList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "진행중,채점이 필요한 과제 목록");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/subm/status", method = {RequestMethod.GET})
    @Operation(summary = "과제 제출 현황 조회", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "72249"))
    public ResponseDTO<CustomBody> tchHomewkSubmStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.findHomewkSubmStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 제출 현황 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/stnt/list", method = {RequestMethod.GET})
    @Operation(summary = "과제 출제 대상 조회", description = "")
    @Parameter(name = "taskId", description = "과제 ID", required = true, schema = @Schema(type = "integer", example = "15381"))
    public ResponseDTO<CustomBody> tchHomewkList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.findTchHomewkStntList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 출제 대상 조회");
    }

    @Loggable
    @RequestMapping(value = "/tch/homewk/end", method = {RequestMethod.POST})
    @Operation(summary = "과제 종료 하기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"taskId\":70125," +
                            "\"timeoutAt\":\"N\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchHomewkEnd(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchHomewkService.modifyHomewkEnd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 종료 하기");

    }
}

