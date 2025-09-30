package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.assessment.service.StntEvalService;
import com.visang.aidt.lms.api.assessment.service.TchEvalService;
import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.dashboard.service.StntDsbdService;
import com.visang.aidt.lms.api.dashboard.service.TchDsbdService;
import com.visang.aidt.lms.api.homework.service.StntHomewkService;
import com.visang.aidt.lms.api.homework.service.TchHomewkService;
import com.visang.aidt.lms.api.lecture.service.TchCrcuService;
import com.visang.aidt.lms.api.materials.service.TchMdulQstnService;
import com.visang.aidt.lms.api.socket.vo.UserDiv;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import com.visang.aidt.lms.global.vo.socket.SocketExceptionBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/stress")
public class StressController {

    private final StressService stressService;
    private final TchEvalService tchEvalService;
    private final TchCrcuService tchCrcuService;
    private final TchMdulQstnService tchMdulQstnService;
    private final TchHomewkService tchHomewkService;
    private final StntHomewkService stntHomewkService;
    private final StntEvalService stntEvalService;
    private final TchDsbdService tchDsbdService;
    private final StntDsbdService stntDsbdService;

    @RequestMapping(value = "/stnt/lecture/mdul/qstn/view", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> stntMdulQstnView(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = stressService.findStntMdulQstnView(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답있는모듈상세조회");
    }

    @RequestMapping(value = "/shop/userinfo", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> getUserInfoList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stressService.findShopUserInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "샵 유저 정보 조회");

    }

    @RequestMapping(value = "/portal/pz/stTextbookInfo", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> stTextbookList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = stressService.stTextbookInfo(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공공)학생 교과서조회");
        } catch (NullPointerException e) {
            log.error("stTextbookInfo - NullPointerException: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "NullPointerException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("stTextbookInfo - IllegalArgumentException: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "IllegalArgumentException: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error("stTextbookInfo - DataAccessException: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "DataAccessException: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("stTextbookInfo - RuntimeException: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "RuntimeException: " + e.getMessage());
        } catch (Exception e) {
            log.error("stTextbookInfo - Exception: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Exception: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/stnt/slfper/evl/slf/set", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> stntSlfperEvlSlfSet(
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @RequestParam(name = "gbCd", defaultValue = "") String gbCd,
            @RequestParam(name = "textbkId", defaultValue = "") String textbkId,
            @RequestParam(name = "tabId", defaultValue = "") String tabId,
            @RequestParam(name = "taskId", defaultValue = "") String taskId,
            @RequestParam(name = "evlId", defaultValue = "") String evlId,
            @RequestParam(name = "setsId", defaultValue = "") String setsId,
            @RequestParam(name = "moduleId", defaultValue = "") String moduleId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stressService.findStntSlfperEvlSlfSet(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "자기동료평가세팅");

    }

    @RequestMapping(value = "/stnt/lecture/mdul/qstn/recheck", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> stntMdulQstnRecheck(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stressService.createStntMdulQstnRecheck(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "재확인횟수저장");

    }

    @RequestMapping(value = "/stnt/slfper/evl/slf/perinfo", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> findStntSlfperEvlSlfPerinfo(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stressService.findStntSlfperEvlSlfPerinfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "동료평가대상정보");


    }

    @RequestMapping(value = "/stnt/act/mdul/list", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> stntActMdulList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            @RequestParam(name = "textbkTabId", defaultValue = "") String textbkTabId,
            @RequestParam(name = "actIemId", defaultValue = "") String actIemId,
            @RequestParam(name = "subId", defaultValue = "") String subId,
            @RequestParam(name = "stntId", defaultValue = "") String stntId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stressService.findStntActMdulList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(학생) 활동결과 목록 조회하기");

    }

    @RequestMapping(value = "/tch/crcu/list", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuList(
            @RequestParam(name = "userId",   defaultValue = "550e8400-e29b-41d4-a716-446655440000") String userId,
            @RequestParam(name = "textbkId",   defaultValue = "1") long textbkId,
            @RequestParam(name = "textbkIdxId",   defaultValue = "1") long textbkIdxId,
            @RequestParam(name = "claId",   defaultValue = "0cc175b9c0f1b6a831c399e269772661") String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {


        Map<String, Object> resultData = stressService.getCurriculumList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "커리큘럼 목록 조회");
    }

    @GetMapping("/stnt/crcu/last-position")
    public ResponseDTO<CustomBody> findStntCrcuLastposition(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stressService.findStntCrcuLastposition(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막수업기록 조회");

    }

     //마지막수업기록 저장
    @RequestMapping(value = "/stnt/crcu/last-position", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> saveStntCrcuLastposition(@RequestBody Map<String, Object> paramData
    ) throws Exception {
        List<String> requiredParams = Arrays.asList("userId", "textbkId", "claId", "crculId");
        AidtCommonUtil.checkRequiredParameter(paramData, requiredParams);
        Map resultData = stressService.saveStntCrcuLastposition(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "마지막수업기록 저장");

    }

//    @RequestMapping(value = "/tch/crcu/info", method = {RequestMethod.GET})
//    public ResponseDTO<CustomBody> tchCrcuInfo(
//            @RequestParam(name = "userId",   defaultValue = "mathreal103-t") String userId,
//            @RequestParam(name = "textbkId",   defaultValue = "373") long textbkId,
//            @RequestParam(name = "textbkIdxId",   defaultValue = "81") long textbkIdxId,
//            @RequestParam(name = "claId",   defaultValue = "3e1d0bfae54f43468be13471e79b3452") String claId,
//            @RequestParam(name = "crculId",   defaultValue = "7") long crculId,
//            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {
//        Map<String, Object> resultData = stressService.findCrcuInfo(paramData);
//        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "차시 정보 조회");
//    }

    @GetMapping(value = "/user/info")
    public ResponseDTO<CustomBody> userInfo(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "claId", required = false) String claId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception {

        Map<String, Object> resultData = stressService.findUserInfo(paramData);
        // user info 에서 try catch로 빠지지 않는 버그 상황에 대한 fail 처리 추가
        boolean success = MapUtils.getBoolean(resultData, "success", true);
        if (success) {
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "");
        } else {
            return AidtCommonUtil.makeResultFail(paramData, null, MapUtils.getString(resultData, "resultMessage"));
        }
    }

    @GetMapping(value="/_login.json")
    public Map<String, Object> _login (
            @RequestParam final UserDiv userDiv,
            @RequestParam final String pwd,
            @RequestParam final String uuid,
            @RequestParam(value="claId", required = false) final String claId,
            @RequestParam(value="semester", required = false) final String semester,
            @RequestParam(value="username", required = false) final String username,
            @RequestParam(value="userphone", required = false) final String userphone,
            @RequestParam(value="ip", required = false) final String ip,
            @RequestParam(value="macAddr", required = false) final String macAddr,
            @RequestParam(value="device", required = false) final String device,
            @RequestParam(value="os", required = false) final String os,
            @RequestParam(value="browser", required = false) final String browser
    ) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("userDiv", userDiv.toString());
        paramData.put("uuid", uuid);
        paramData.put("pwd", pwd);
        paramData.put("username", username);
        paramData.put("claId", claId);
        paramData.put("semester", semester);
        paramData.put("ip", ip);
        paramData.put("macAddr", macAddr);
        paramData.put("device", device);
        paramData.put("os", os);
        paramData.put("browser", browser);
        paramData.put("lgnSttsAt", 1);

        Map<String,Object> resultData = stressService.getUserInfo(paramData);
        String resultMessage = "(Socket) 회원 공통 API";
        resultData.put("resultMessage", resultMessage);

        //형식 변경 금지
        return resultData;
    }

    @RequestMapping(value = "/tch/tool/edit/bar/call", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> selectTchTool(
            @RequestParam(name = "userId", defaultValue = "vstea1") String userId,
            @RequestParam(name = "claId", defaultValue = "1") String claId,
            @RequestParam(name = "textbkId", defaultValue = "1") String textbkId,
            @RequestParam(name = "userSeCd", defaultValue = "T") String userSeCd,
            @RequestParam(name = "sbjctCd", defaultValue = "1") String sbjctCd
    )throws Exception {
        HashMap<String, Object> paramData = new HashMap<>();



        paramData.put("userId", userId);
        paramData.put("claId", claId);
        paramData.put("textbkId", textbkId);
        paramData.put("userSeCd", userSeCd);
        paramData.put("sbjctCd", sbjctCd);

        Object resultData = stressService.selectTchTool(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "툴편집(호출)");

    }

    @RequestMapping(value = "/tch/lecture/mdul/qstn/answ", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchMdulQstnAnsw(
            @RequestBody Map<String, Object> paramData
    )throws Exception {

        Object resultData = stressService.findTchMdulQstnAnsw(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "정답보기");

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

        Object resultData = stressService.selectTchDsbdAreaAchievementClassdDstribution(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (영어)");

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

        Object resultData = stressService.selectTchDsbdUnitAchievementListAll(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사] 학급관리 > 홈 대시보드 > 단원별 그래프 ALL");

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

        Object resultData = stressService.selectStntDsbdAreaAchievementStudentDstribution(paramData);
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

        Object resultData = stressService.selectStntDsbdUnitAchievementListAll(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[학생] 학급관리 > 홈 대시보드 > 단원별 그래프 (영어)");

    }

    /**
     * 교과서 커리큘럼 목록 조회
     *
     * @param textbookIndexId
     * @param paramData
     * @return
     */
    @Loggable
    @RequestMapping(value = "/textbook/crcu/list", method = {RequestMethod.GET})
    @Operation(summary = "교과서 커리큘럼 목록 조회", description = "")
    @Parameter(name = "textbookIndexId", description = "교과서 목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "userId", description = "유저 ID", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> textbookCrcuList(
            @RequestParam(name = "textbookIndexId",   defaultValue = "1") long textbookIndexId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {

        List<Map<String, Object>> resultData = stressService.getTextbookCrcuList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교과서 커리큘럼 목록 조회");
    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/save", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(summary = "(공통) 응시(article)자/stnt/eval/save동저장", description = "")
    public ResponseDTO<CustomBody> stntEvalSave(
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, Object> bodyData,
            @RequestParam Map<String, Object> queryData
    ) throws Exception {

        Map<String, Object> paramData = new HashMap<>();

        // POST인 경우 body 데이터 우선, GET인 경우 query 데이터 사용
        if ("POST".equalsIgnoreCase(request.getMethod()) && bodyData != null && !bodyData.isEmpty()) {
            paramData = bodyData;
        } else {
            paramData = queryData;
        }

        Object resultData = stressService.modifyStntEvalSave(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공통) 응시(article)자동저장");
    }

    @Loggable
    @RequestMapping(value = "/tch/eval/status/list", method = {RequestMethod.GET})
    @Operation(summary = "진행중,채점이 필요한 평가 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbook732-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "44e701f580a34e17b2a030a02527dc4a"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1522"))
    public ResponseDTO<CustomBody> tchEvalStatusList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = tchEvalService.findTchEvalStatusList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "진행중,채점이 필요한 평가 목록 조회");
    }

    @Loggable
    @PostMapping(value = "/tch/eval/create")
    @Operation(summary = "평가 생성(저장)", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"wrterId\":\"mathtest110-t\"," +
                            "\"claId\":\"821bf76183e943b3adf2a8e0b2064b46\"," +
                            "\"textbookId\":1199," +
                            "\"evlNm\":\"테스트 평가 수학\"," +
                            "\"evlSeCd\":1," + // /tch/eval/save api 에서 처리 하는 것으로 변경 됨 // 화면 수정 완료 후 삭제 예정
                            "\"eamMth\":3," +
                            "\"eamExmNum\":0," +
                            "\"eamGdExmMun\":0," +
                            "\"eamAvUpExmMun\":0," +
                            "\"eamAvExmMun\":0," +
                            "\"eamAvLwExmMun\":0," +
                            "\"eamBdExmMun\":0," +
                            "\"eamScp\":\"4,5\"," +
                            "\"setsId\":\"MSTG88922\"," +
                            "\"prscrStdSetAt\":\"N\"," +
                            "\"prscrStdStDt\":\"2024.09.30 09:00\"," +
                            "\"prscrStdEdDt\":\"2024.09.30 18:00\"," +
                            "\"prscrStdNtTrnAt\":\"N\"," +
                            "\"prscrStdPdSet\":0" +
                            "}"
                    )
            }
            ))
    public ResponseDTO<CustomBody> tchEvalCreate(
            @RequestBody Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchEvalService.createTchEvalCreate(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 생성(저장)");
    }

    @Loggable
    @RequestMapping(value = "/tch/crcu/info", method = {RequestMethod.GET})
    @Operation(summary = "차시 정보 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "550e8400-e29b-41d4-a716-446655440000"))
    @Parameter(name = "textbkId", description = "교과서 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "textbkIdxId", description = "목차 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772661"))
    @Parameter(name = "crculId", description = "커리큘럼 ID", required = true, schema = @Schema(type = "integer", example = "7"))
    public ResponseDTO<CustomBody> tchCrcuInfo(
            @RequestParam(name = "userId",   defaultValue = "mathreal103-t") String userId,
            @RequestParam(name = "textbkId",   defaultValue = "373") long textbkId,
            @RequestParam(name = "textbkIdxId",   defaultValue = "81") long textbkIdxId,
            @RequestParam(name = "claId",   defaultValue = "3e1d0bfae54f43468be13471e79b3452") String claId,
            @RequestParam(name = "crculId",   defaultValue = "7") long crculId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception  {


        Map<String, Object> resultData = tchCrcuService.findCrcuInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "차시 정보 조회");
    }

    @Loggable
    @RequestMapping(value = "/stnt/eval/list", method = {RequestMethod.GET})
    @Operation(summary = "평가 목록 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "mathbook732-s1" ))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "44e701f580a34e17b2a030a02527dc4a" ))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1522"))
    @Parameter(name = "condition", description = "검색 유형", required = false, schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "evlSttsCd", description = "평가 상태 : 전체/예정(1)/진행중(2)/완료(3)", required = false, schema = @Schema(type = "string", allowableValues = {"", "1", "2", "3"}, example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> stntEvalList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = stntEvalService.findEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 목록 조회");

    }

    @Loggable
    @RequestMapping(value = "/tch/lecture/mdul/qstn/status", method = {RequestMethod.GET})
    @Operation(summary = "제출현황 조회", description = "")
    //@Parameter(name = "dtaResultId", description = "자료(학습)결과ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "dtaIemId", description = "자료평가항목ID", required = true, schema = @Schema(type = "string", example = "827"))
    @Parameter(name = "subId", description = "연쇄형 서브 문항 idx", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "tabId", description = "탭 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "textbkId", description = "교과서 ID", required = false, schema = @Schema(type = "integer", example = ""))
    @Parameter(name = "claId", description = "클래스 ID", required = false, schema = @Schema(type = "string", example = "1dfd6267b8fb11ee88c00242ac110002"))
    @Parameter(name = "setsId", description = "세트지 ID", required = false, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "selfEvlYn", description = "자기평가 Y/N", required = false, schema = @Schema(type = "string", example = "N"))
    public ResponseDTO<CustomBody> tchMdulQstnStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {

        Object resultData = tchMdulQstnService.modifyTchMdulQstnStatus(paramData);

        if (resultData instanceof Map) {
            Map<String, Object> resultMap = (Map<String, Object>) resultData;
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("resultList");

            if (resultList == null || resultList.isEmpty()) {
                resultList = new ArrayList<>();

                List<Map<String, Object>> stntList = (List<Map<String, Object>>) resultMap.get("stntList");
                String mamoymId = "stressmath3221-s1";
                String flnm = "stressmath3221-s1";

                if (stntList != null && !stntList.isEmpty()) {
                    Map<String, Object> firstStudent = stntList.get(0);
                    mamoymId = (String) firstStudent.get("userId");
                    flnm = (String) firstStudent.get("flnm");
                }


                Map<String, Object> defaultItem = new HashMap<>();
                defaultItem.put("id", 139265);
                defaultItem.put("detailId", 1210604);
                defaultItem.put("mamoymId", mamoymId);
                defaultItem.put("flnm", flnm);
                defaultItem.put("profileImg", "profile_myroom_studying_cha01(196).png");
                defaultItem.put("thumbnail", "upload/1/20250414/Pfvq3sqwb.png");
                defaultItem.put("stdFdbDc", null);
                defaultItem.put("stdFdbUrl", null);
                defaultItem.put("exltAnwAt", "N");
                defaultItem.put("fdbExpAt", "N");
                defaultItem.put("oldExltAnwAt", "N");
                defaultItem.put("subMitAnw", "[\"X\"]");
                defaultItem.put("subMitAnwUrl", "upload/1/20250414/Pfvq3sqwb.png");
                defaultItem.put("hdwrtCn", "{\"json\":\"\",\"width\":0,\"height\":0,\"padding\":0}");
                defaultItem.put("delYn", "N");

                resultList.add(defaultItem);

                resultMap.put("resultList", resultList);
                resultMap.put("resultCnt", 1);
            }
        }

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "제출현황 조회");

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
    @RequestMapping(value = "/stnt/homewk/list", method = {RequestMethod.GET})
    @Operation(summary = "과제 목록 조회", description = "")
    @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "student41"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "0cc175b9c0f1b6a831c399e269772669"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "condition", description = "검색 유형", required = false, schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "taskSttsCd", description = "과제 상태 : 전체/예정(1)/진행중(2)/완료(3)", required = false, schema = @Schema(type = "string", allowableValues = {"", "1", "2", "3"}, example = ""))
    @Parameter(name = "page", description = "page", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "size", required = false, schema = @Schema(type = "integer", example = "10"))

    public ResponseDTO<CustomBody> stntHomewkList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = stntHomewkService.findStntHomewkList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "과제 목록 조회");

    }

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
    @GetMapping(value = "/tch/eval/list")
    @Operation(summary = "평가 목록 조회", description = "")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathtest104-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "60263afa38fe4cdf9fe775c2865a6062"))
    @Parameter(name = "textbookId", description = "교과서(과목) ID", required = true, schema = @Schema(type = "integer", example = "1152"))
    @Parameter(name = "condition", description = "검색 유형", required = false, schema = @Schema(type = "string", allowableValues = {"name"}, defaultValue = "name"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string", example = "테스트"))
    @Parameter(name = "tmprStrgAt", description = "공유완료/설정미완료 구분", required = false, schema = @Schema(type = "string", allowableValues = {"","Y","N"}, defaultValue = "" ))
    @Parameter(name = "evlSttsCd", description = "필터조건(평가상태) : 1: 예정, 2: 진행중, 3: 완료, 4: 채점중, 5: 채점완료", required = false, schema = @Schema(type = "string", allowableValues = {"1","2","3","4","5"}, defaultValue = "" ))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> tchEvalList(
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = tchEvalService.findEvalList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가 목록 조회");
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

}
