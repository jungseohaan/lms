package com.visang.aidt.lms.api.dashboard.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.EtcService;
import com.visang.aidt.lms.api.user.service.UserService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.dashboard.controller
 * fileName : EtcController
 * USER : kimjh21
 * date : 2024-02-29
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-02-29         kimjh21          최초 생성
 */
@Slf4j
@RestController
@Tag(name = "대시보드 부가 기능 API", description = "META 자기조절학습, 오늘의 기분, 목표 설정")
//@AllArgsConstructor
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EtcController {

    private final EtcService etcService;

    @Loggable
    @RequestMapping(value = "/etc/tdymd/list", method = {RequestMethod.GET})
    @Operation(summary = "오늘의 기분 조회(기분, 에너지에 따른 목록들)", description = "")
    @Parameter(name = "cdtnSeCd", description = "1:나쁨, 2:좋음", required = false, schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "enrgSeCd", description = "1:에너지 낮음, 2:에너지 높음", required = false, schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> conditionList(
            @RequestParam(name = "cdtnSeCd", defaultValue = "0", required = true) int cdtnSeCd,
            @RequestParam(name = "enrgSeCd", defaultValue = "0", required = true) int enrgSeCd,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultList = etcService.getConditionList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/tdymd/init", method = {RequestMethod.POST})
    @Operation(summary = "학생 오늘의 기분 insert", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                    "\"tdyMdId\":1," +
                                    "\"tdyMdRsn\":\"그냥\"," +
                                    "\"stdtId\":\"re22mma15-s1\"," +
                                    "\"claId\":\"cc86a331d2824c52b1db68a0bf974dd5\"" +
                                    "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> conditionInit(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        if (MapUtils.getInteger(paramData, "tdyMdId", 0) > 54 || MapUtils.getInteger(paramData, "tdyMdId", 0) < 1) {
            return AidtCommonUtil.makeResultFail(paramData, null, "tdyMdId Error");
        }
        String tdyMdRsm = MapUtils.getString(paramData, "tdyMdRsm", "");
        if(tdyMdRsm.length() > 100) {
            return AidtCommonUtil.makeResultFail(paramData, null, "tdyMdRsm는 최대 100자까지만 가능합니다.");
        }
        int result = etcService.insertConditionDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, result, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/tdymd/info", method = {RequestMethod.GET})
    @Operation(summary = "학생 개인 오늘의 기분 목록(최근 검사한 1개만 return)", description = "")
    @Parameter(name = "stdtId", description = "학생ID", required = false, schema = @Schema(type = "String", example = "re22mma15-s1"))
    @Parameter(name = "claId", description = "클래스ID", required = false, schema = @Schema(type = "String", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    public ResponseDTO<CustomBody> conditionInfo(
            @RequestParam(name = "stdtId", defaultValue = "0", required = true) String stdtId,
            @RequestParam(name = "claId", defaultValue = "0", required = true) String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.conditionInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/tdymd/stCnt", method = {RequestMethod.GET})
    @Operation(summary = "클래스 기준으로 같은 날짜에 작성된 오늘의 기분 갯수 전달", description = "")
    @Parameter(name = "claId", description = "클래스ID", required = false, schema = @Schema(type = "String", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    public ResponseDTO<CustomBody> conditionUserListSize(
            @RequestParam(name = "claId", defaultValue = "2", required = false) String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.conditionUserListSize(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/tdymd/stinfo", method = {RequestMethod.GET})
    @Operation(summary = "클래스 기준으로 모든 학생들의 최근 기분 조회", description = "")
    @Parameter(name = "claId", description = "클래스ID", required = false, schema = @Schema(type = "String", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "curYn", description = "오늘날짜 기준 여부(Y: 오늘 날짜, N: 모든 날짜)", required = false, schema = @Schema(type = "String", example = "N"))
    public ResponseDTO<CustomBody> conditionUserList(
            @RequestParam(name = "claId", defaultValue = "2", required = false) String claId,
            @RequestParam(name = "curYn", defaultValue = "N", required = false) String curYn,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultMap = etcService.conditionUserList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/tdymd/dashboard", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 오늘의 기분 대시보드", description = "")
    @Parameter(name = "claId", description = "클래스ID", required = false, schema = @Schema(type = "String", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "date", description = "조회 날짜(디폴트 오늘날짜)", required = false, schema = @Schema(type = "String", example = "N"))
    @Parameter(name = "spot", description = "조회하려는 스팟(디폴트 모든스팟)", required = false, schema = @Schema(type = "String", example = ""))
    @Parameter(name = "num", description = "출석번호순(1:오름차순, 2:내림차순, default 오름차순)", required = false, schema = @Schema(type = "String", example = ""))
    @Parameter(name = "updt", description = "업데이트날짜순(1:오름차순, 2:내림차순, default null)", required = false, schema = @Schema(type = "String", example = ""))
    public ResponseDTO<CustomBody> conditionDashBoard(
            @RequestParam(name = "claId", defaultValue = "2", required = false) String claId,
            @RequestParam(name = "date", defaultValue = "2025-08-28", required = false) String date,
            @RequestParam(name = "spot", defaultValue = "1", required = false) String spot,
            @RequestParam(name = "num", defaultValue = "num", required = false) String num,
            @RequestParam(name = "updt", defaultValue = "updt", required = false) String updt,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.conditionDashBoardList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/tdymd/stdetail", method = {RequestMethod.GET})
    @Operation(summary = "학생 개인의 오늘의 기분 검사 목록", description = "")
    @Parameter(name = "stdtId", description = "학생ID", required = false, schema = @Schema(type = "String", example = "re22mma15-s1"))
    @Parameter(name = "claId", description = "클래스ID", required = false, schema = @Schema(type = "String", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "5"))
    public ResponseDTO<CustomBody> conditionUserDetail(
            @RequestParam(name = "stdtId", defaultValue = "0", required = true) String stdtId,
            @RequestParam(name = "claId", defaultValue = "0", required = true) String claId,
            @Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        if (StringUtils.isEmpty(stdtId) || StringUtils.isEmpty(claId)) {
            return AidtCommonUtil.makeResultFail(paramData, null, "stdtId or claId Error");
        }
        Map<String, Object> resultMap = etcService.conditionUserDetail(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/tdymd/reset", method = {RequestMethod.GET})
    @Operation(summary = "오늘의 기분 알람 제거", description = "")
    @Parameter(name = "claId", description = "클래스ID", required = false, schema = @Schema(type = "String", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    public ResponseDTO<CustomBody> conditionAlarmReset(
            @RequestParam(name = "claId", defaultValue = "cc86a331d2824c52b1db68a0bf974dd5", required = true) String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.conditionReset(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, "");
    }

    @Loggable
    @RequestMapping(value = "/etc/gl/save", method = {RequestMethod.GET})
    @Operation(summary = "목표 설정 저장", description = "")
    @Parameter(name = "crculIds", description = "커리큘럼 id 여러개(','로 묶어서 전달)", schema = @Schema(type = "string", example = "1,2,3,4"))
    @Parameter(name = "userId", description = "조회 요청한 id", schema = @Schema(type = "string", example = "eec7f221-f39d-5352-a3ae-c9bbf9b598ae"))
    @Parameter(name = "claId", description = "학급 id", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    public ResponseDTO<CustomBody> glSave(
            @RequestParam(name = "crculIds") String crculIds,
            @RequestParam(name = "claId") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        String resultMessage = "목표 설정 저장";
        if (StringUtils.isEmpty(crculIds) || StringUtils.isEmpty(claId)) {
            resultMessage = "필수 파라미터 누락";
            return AidtCommonUtil.makeResultFail(paramData,null,resultMessage);
        }
        Map<String, Object> resultData = etcService.saveGoal(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/gl/detail", method = {RequestMethod.GET})
    @Operation(summary = "목표 설정 리스트(학생조회만 사용)", description = "")
    @Parameter(name = "stdtId", description = "학생 id", schema = @Schema(type = "string", example = "re22mma15-s1"))
    public ResponseDTO<CustomBody> glInfo(
            @RequestParam(name = "stdtId", required = false) String stdtId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        String resultMessage = "학생 상세 목표설정 전달";
        if (StringUtils.isEmpty(stdtId)) {
            resultMessage = "필수 파라미터 누락";
            return AidtCommonUtil.makeResultFail(paramData,null,resultMessage);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultList", etcService.getMainGoalInfo(paramData));
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/gl/commongoal", method = {RequestMethod.GET})
    @Operation(summary = "공통 목표 리스트", description = "")
    @Parameter(name = "claId", description = "학급 id", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    public ResponseDTO<CustomBody> commonGoal(
            @RequestParam(name = "claId", required = false) String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        String resultMessage = "공통 목표 리스트";
        if (StringUtils.isEmpty(claId)) {
            resultMessage = "claId가 비어있습니다.";
            return AidtCommonUtil.makeResultFail(paramData,null,resultMessage);
        }
        Map<String, Object> resultData = etcService.getCommonGoal(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/gl/tch/list", method = {RequestMethod.GET})
    @Operation(summary = "목표 설정 리스트(선생님 조회만 사용)", description = "")
    @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> glInfoList(
            @RequestParam(name = "claId", required = false) String claId,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("claId", claId);
        Map<String, Object> resultMap = etcService.getTeacherGoalInfoList(paramData, pageable);
        String resultMessage = "클래스 단위 목표설정 리스트 전달";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/gl/updt", method = {RequestMethod.POST})
    @Operation(summary = "목표 설정 수정", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "[" +
                                    "{" +
                                    "\"goalDetailId\":15," +
                                    "\"claId\":\"4e6d95b341474bc9826dc372beefc6a5\"," +
                                    "\"goalNm\":\"선생님이 공통목표를 수정하는 경우\"," +
                                    "\"userType\":\"T\"," +
                                    "\"tcSetAt\":\"Y\"," +
                                    "\"crculId\":1," +
                                    "\"ordNo\":1" +
                                    "}," +
                                    "{" +
                                    "\"goalDetailId\":16," +
                                    "\"goalNm\":\"선생님이 학생의 개인 목표를 수정한 경우\"," +
                                    "\"userType\":\"S\"," +
                                    "\"stSetAt\":\"N\"" +
                                    "}," +
                                    "{" +
                                    "\"goalDetailId\":17," +
                                    "\"goalNm\":\"학생이 자신의 개인 목표를 수정한 경우\"," +
                                    "\"userType\":\"S\"," +
                                    "\"stSetAt\":\"Y\"" +
                                    "}" +
                                    "]"
                    )
            }
            ))
    public ResponseDTO<CustomBody> glDetailUpdt(
            @RequestBody List<Map<String, Object>> paramData
    ) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("param", paramData);
        Map<String, Object> resultData = etcService.updateGoalDetail(paramData);
        String resultMessage = "목표설정 수정";
        return AidtCommonUtil.makeResultSuccess(param, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/gl/stachv", method = {RequestMethod.POST})
    @Operation(summary = "(학생)목표 달성 여부 저장(대시보드)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                    "\"goalDetailId\":17," +
                                    "\"userId\":\"mathbe2-s1\"," +
                                    "\"claId\":\"claId\"," +
                                    "\"stChkAt\":\"Y\"" +
                                    "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> glSetAchv(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = etcService.updateGoalAchv(paramData);
        String resultMessage = "목표 달성 여부 수정";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/gl/read", method = {RequestMethod.GET})
    @Operation(summary = "(선생님) 목표설정 알람 확인", description = "")
    @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    public ResponseDTO<CustomBody> glTchRead(
            @RequestParam(name = "claId", required = false) String claId,
            @RequestParam(name = "tcId", required = false) String tcId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.updateGoalAlarm(paramData);
        String resultMessage = "(선생님)목표설정 알람 제거";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    /* META 자기조절학습 검사 */
    @Loggable
    @RequestMapping(value = "/etc/meta/tc/info", method = {RequestMethod.GET})
    @Operation(summary = "(선생님) 학습심리정서검사 목록 조회", description = "")
    @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "tcId", description = "선생님 ID", schema = @Schema(type = "string", example = "re22mma15-t"))
    @Parameter(name = "paperIdx", description = "시험지 정보", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> tchMetaInfo(
            @RequestParam(name = "claId", required = false) String claId,
            @RequestParam(name = "tcId", required = false) String tcId,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "0") int paperIdx,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.selectTcDgnssInfo(paramData);
        String resultMessage = "(선생님)학습심리정서검사 목록 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/start", method = {RequestMethod.GET})
    @Operation(summary = "(선생님) 학습심리정서검사 시작(시작시 데이터 삽입)", description = "")
    @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "tcId", description = "선생님 ID", schema = @Schema(type = "string", example = "re22mma15-t"))
    @Parameter(name = "ordNo", description = "심리검사 회차", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "grade", description = "학년 정보", schema = @Schema(type = "string", example = "el"))
    @Parameter(name = "paperIdx", description = "시험지 정보", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> tchMetaStart(
            @RequestParam(name = "claId", required = false) String claId,
            @RequestParam(name = "tcId", required = false) String tcId,
            @RequestParam(name = "ordNo", required = false) int ordNo,
            @RequestParam(name = "grade", required = false) String grade,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "2") int paperIdx,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.insertTcDgnssStart(paramData);
        String resultMessage = "(선생님) 학습심리정서검사 시작";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/end", method = {RequestMethod.GET})
    @Operation(summary = "(선생님) 학습심리정서검사 종료", description = "")
    @Parameter(name = "dgnssId", description = "심리검사 ID", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> tchMetaEnd(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            HttpServletRequest request
    ) throws Exception {
        Map<String, Object> resultMap = etcService.updateTcDgnssEnd(paramData, request);
        String resultMessage = "(선생님) 학습심리정서검사 종료";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/cancel", method = {RequestMethod.GET})
    @Operation(summary = "(선생님)학습심리정서검사 취소", description = "")
    @Parameter(name = "dgnssId", description = "심리검사 ID(데이터 삭제)", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "claId", description = "학급 ID", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    public ResponseDTO<CustomBody> tchMetaCancel(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        etcService.deleteTcDgnssCancel(paramData);
        String resultMessage = "(선생님)학습심리정서검사 취소";
        return AidtCommonUtil.makeResultSuccess(paramData, null, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/restart", method = {RequestMethod.GET})
    @Operation(summary = "(선생님)학습심리정서검사 재시작", description = "")
    @Parameter(name = "dgnssId", description = "심리검사 ID", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> tchMetaRestart(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @RequestParam(name = "claId", required = false) String claId,
            @RequestParam(name = "grade", required = false) String grade,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = etcService.tcDgnssRestart(paramData);
        String resultMessage = "(선생님)학습심리정서검사 재시작";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/text/save", method = {RequestMethod.GET})
    @Operation(summary = "(선생님)학습심리정서검사 텍스트 저장", description = "")
    @Parameter(name = "dgnssId", description = "심리검사 ID", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "dgnssText", description = "심리검사 텍스트", schema = @Schema(type = "string", example = "text"))
    public ResponseDTO<CustomBody> tchTextUpdt(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @RequestParam(name = "dgnssText", required = false) String dgnssText,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = etcService.tcDgnssTextSave(paramData);
        String resultMessage = "(선생님)학습심리정서검사 텍스트 저장";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/notsubm", method = {RequestMethod.GET})
    @Operation(summary = "(선생님)학습심리정서검사 미제출 인원 목록", description = "")
    @Parameter(name = "dgnssId", description = "심리검사 ID", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> tchMetaNotSubmSt(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultMap = etcService.selectTcDgnssNotSubmStList(paramData);
        String resultMessage = "학습심리정서검사 미제출 인원 목록";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/detail", method = {RequestMethod.GET})
    @Operation(summary = "(선생님)학습심리정서검사 상세 내용", description = "")
    @Parameter(name = "dgnssId", description = "심리검사 ID", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> tchMetaDetail(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.selectTcDgnssDetailInfo(paramData);
        String resultMessage = "학습심리정서검사 상세 내용";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/st/info", method = {RequestMethod.GET})
    @Operation(summary = "(학생)학습심리정서검사 목록 조회", description = "")
    @Parameter(name = "claId", description = "클래스 ID", schema = @Schema(type = "String", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "stdtId", description = "학생 ID", schema = @Schema(type = "String", example = "re22mma15-s1"))
    public ResponseDTO<CustomBody> stMetaStart(
            @RequestParam(name = "claId", required = false) String claId,
            @RequestParam(name = "stdtId", required = false) String stdtId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        List<Map<String, Object>> resultList = etcService.selectStDgnssList(paramData);
        String resultMessage = "(학생)학습심리정서검사 목록 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultList, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/st/start", method = {RequestMethod.GET})
    @Operation(summary = "(학생)META 자기조절학습 시작", description = "")
    @Parameter(name = "dgnssResultId", description = "심리검사 상세 ID", schema = @Schema(type = "int", example = "41"))
    @Parameter(name = "paperIdx", description = "심리검사 종류", schema = @Schema(type = "int", example = "2"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "20"))
    public ResponseDTO<CustomBody> stMetaStart(
            @RequestParam(name = "dgnssResultId", required = false) int dgnssResultId,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "2") int paperIdx,
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.selectStDgnssStart(paramData, pageable);
        String resultMessage = "(학생)META 자기조절학습 시작";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/st/new", method = {RequestMethod.GET})
    @Operation(summary = "(학생)심리검사 새로하기", description = "")
    @Parameter(name = "dgnssResultId", description = "심리검사 상세 ID", schema = @Schema(type = "int", example = "15"))
    @Parameter(name = "paperIdx", description = "심리검사 종류", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> stMetaNew(
            @RequestParam(name = "dgnssResultId", required = false) int dgnssResultId,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "0") int paperIdx,
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultMap = etcService.selectNewOmr(paramData, pageable);
        String resultMessage = "(학생)심리검사 새로하기";
        return AidtCommonUtil.makeResultSuccess(paramData, resultMap, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/st/answer", method = {RequestMethod.POST})
    @Operation(summary = "(학생)답 입력", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                    "\"omrIdx\":1," +
                                    "\"no\":1," +
                                    "\"answer\":1" +
                                    "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stMetaAnswer(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        int result = etcService.updateStAnswer(paramData);
        String resultMessage = "(학생)문제 풀이";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/st/submit", method = {RequestMethod.POST})
    @Operation(summary = "(학생)심리검사 제출", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                    "\"dgnssResultId\":1" +
                                    "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> stMetaSubmit(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = etcService.updateStSubmit(paramData);
        String resultMessage = "심리검사 제출";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/need", method = {RequestMethod.GET})
    @Operation(summary = "(교사)학습심리정서검사 상담 및 지도가 필요한 학생", description = "")
    @Parameter(name = "dgnssId", description = "진단평가 ID", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "paperIdx", description = "진단평가 ID", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> tcMetaNeed(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "2") int paperIdx,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        if (dgnssId == 0) {
            return AidtCommonUtil.makeResultFail(paramData,null, "필수 파라미터 누락");
        }
        Map<String, Object> result = etcService.selectTcNeedInfo(paramData);
        String resultMessage = "(교사)학습심리정서검사 상담 및 지도가 필요한 학생 전달";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/stinfolist", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 대시보드 - 학생 목록", description = "")
    @Parameter(name = "dgnssId", description = "심리검사 ID", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "paperIdx", description = "심리검사 종류", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "type", description = "타입(1:신뢰도, 2:동기전략, 3:인지전략, 4:행동전략)", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "testFlag", description = "테스트 플래그 YN", schema = @Schema(type = "string", example = "N"))
    public ResponseDTO<CustomBody> tcMetaStInfoList(
            @RequestParam(name = "dgnssId", required = false) int dgnssId,
            @RequestParam(name = "type", required = false) int type,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "2") int paperIdx,
            @RequestParam(name = "testFlag", required = false, defaultValue = "N") String testFlag,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = etcService.selectStInfoList(paramData);
        String resultMessage = "대시보드 - 학생 목록";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/tc/analysis", method = {RequestMethod.GET})
    @Operation(summary = "(교사) 대시보드 - 학습심리검사 종합 분석", description = "")
    @Parameter(name = "claId", description = "클래스 ID", schema = @Schema(type = "string", example = "cc86a331d2824c52b1db68a0bf974dd5"))
    @Parameter(name = "paperIdx", description = "심리검사 종류", schema = @Schema(type = "string", example = "2"))
    @Parameter(name = "ordNo", description = "현재 조회하는 회차", schema = @Schema(type = "string", example = "2"))
    public ResponseDTO<CustomBody> tcMetaStAnalysis(
            @RequestParam(name = "claId", required = false) String claId,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "2") String paperIdx,
            @RequestParam(name = "ordNo", required = false, defaultValue = "2") String ordNo,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = etcService.selectTcAnalysis(paramData);
        String resultMessage = "(교사) 대시보드 - 종합 분석";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/st/analysis", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학습심리정서검사 결과보기", description = "")
    @Parameter(name = "dgnssResultId", description = "심리검사 상세 ID", schema = @Schema(type = "int", example = "1"))
    @Parameter(name = "paperIdx", description = "심리검사 종류", schema = @Schema(type = "int", example = "2"))
    @Parameter(name = "ordNo", description = "현재 조회하는 회차", schema = @Schema(type = "string", example = "2"))
    @Parameter(name = "stdtId", description = "학생 ID", schema = @Schema(type = "string", example = "mathbe2-s1"))
    public ResponseDTO<CustomBody> stMetaAnalysis(
            @RequestParam(name = "dgnssResultId", required = false) String dgnssResultId,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "2") String paperIdx,
            @RequestParam(name = "ordNo", required = false, defaultValue = "1") String ordNo,
            @RequestParam(name = "stdtId", required = false, defaultValue = "1") String stdtId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = etcService.selectStAnalysis(paramData);
        String resultMessage = "(학생) 학습심리정서검사 결과보기";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/st/total/analysis", method = {RequestMethod.GET})
    @Operation(summary = "(학생) 학습심리정서검사 종합분석", description = "")
    @Parameter(name = "stdtId", description = "학생 ID", schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "paperIdx", description = "심리검사 종류", schema = @Schema(type = "int", example = "2"))
    @Parameter(name = "ordNo", description = "조회한 회차", schema = @Schema(type = "int", example = "1"))
    public ResponseDTO<CustomBody> stTotalAnalysis(
            @RequestParam(name = "stdtId", required = false) String stdtId,
            @RequestParam(name = "paperIdx", required = false, defaultValue = "2") String paperIdx,
            @RequestParam(name = "ordNo", required = false, defaultValue = "1") String ordNo,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = etcService.selectStTotalAnalysis(paramData);
        String resultMessage = "(학생) 학습심리정서검사 결과보기";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/pdf", method = {RequestMethod.POST})
    @Operation(summary = "자기조절학습 PDF 다운로드", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                    "\"userId\":\"mathbe2-s1\"," +
                                    "\"userType\":\"S\"," +
                                    "\"dgnssId\":184," +
                                    "\"answerIdx\":1161," +
                                    "\"ordNo\":1," +
                                    "\"token\":\"토큰정보\"," +
                                    "\"accessId\":\"접속정보\"," +
                                    "\"apiDomain\":\"KERIS API URL\"" +
                                    "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> metaPdfDownload(
            @RequestBody Map<String, Object> paramData,
            HttpServletRequest request
    ) throws Exception {
        Map<String, Object> result = etcService.pdfDownload(paramData, request);
        String resultMessage = "자기조절학습 PDF 다운로드";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/updateUserInfo", method = {RequestMethod.POST})
    @Operation(summary = "비바클래스 회원정보 업데이트", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                "\"stdtId\":\"mathbe2-s1\"," +
                                "\"nickNm\":\"학생이름\"," +
                                "\"gender\":\"M\"," +
                                "\"userNumber\":\"1\"," +
                                "\"gradeCd\":\"학년\"," +
                                "\"claCd\":\"반\"" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> updateUserInfo(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            result = etcService.updateUserInfo(paramData);
        } catch (Exception e) {
            result.put("success", "fail");
            result.put("message", "vivaClass update fail: " + e.getMessage());
            log.error("err:{}" , e.getMessage());
        }
        String resultMessage = "비바클래스용 학생 정보 업데이트";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/sync", method = {RequestMethod.POST})
    @Operation(summary = "비바클래스 회원 동기화", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "", value = """
                            {
                              "tcUserId": "exam10-t",
                              "claList": [
                                {
                                  "claId": "exam10class1",
                                  "schlCd": "1868",
                                  "schlNm": "연현초등학교",
                                  "year": "2024",
                                  "claNm": "2반",
                                  "gradeCd": "3",
                                  "stdtUserInfo": [
                                  { "stdtUserId": "exam10class1-s1", "nickNm": "홍길동", "userNumber" : 1 },
                                  { "stdtUserId": "exam10class1-s2", "nickNm": "김철수", "userNumber" : 2 }
                                  ]
                                },
                                {
                                  "claId": "exam10class2",
                                  "schlCd": "1868",
                                  "schlNm": "연현초등학교",
                                  "year": "2024",
                                  "claNm": "2반",
                                  "gradeCd": "3",
                                  "stdtUserInfo": [
                                  { "stdtUserId": "exam10class2-s3", "nickNm": "이영희", "userNumber" : 1},
                                  { "stdtUserId": "exam10class2-s4", "nickNm": "박민수", "userNumber" : 2}
                                  ]
                                }
                              ]
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> vivaSync(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            result = etcService.vivaSyncProc(paramData);
        } catch (Exception e) {
            result.put("success", "fail");
            result.put("message", "vivaClass Sync fail: " + e.getMessage());
            log.error("err:{}" , e.getMessage());
        }
        String resultMessage = "비바클래스 회원 동기화";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }

    @Loggable
    @RequestMapping(value = "/etc/meta/user", method = {RequestMethod.POST})
    @Operation(summary = "비바클래스 회원 등록(닉네임 발행)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value =
                            "{" +
                                    "\"claId\":\"학급ID\"," +
                                    "\"tcId\":\"mathbe2-t\"," +
                                    "\"stdtId\":\"mathbe2-s1\"," +
                                    "\"nickNm\":\"학생이름\"," +
                                    "\"userNumber\":\"1\"," +
                                    "\"gradeCd\":\"학년\"," +
                                    "\"claCd\":\"반\"" +
                                    "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> vivaInsertUser(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            result = etcService.insertVivaClassUser(paramData);
        } catch (Exception e) {
            result.put("success", "fail");
            result.put("message", "vivaClass NickName insert fail: " + e.getMessage());
            log.error("err:{}" , e.getMessage());
        }
        String resultMessage = "비바클래스 회원 등록(닉네임 발행)";
        return AidtCommonUtil.makeResultSuccess(paramData, result, resultMessage);
    }
}
