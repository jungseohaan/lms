package com.visang.aidt.lms.api.integration.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.integration.service.IntegPublishService;
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

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(연동) 발행함 API", description = "(연동) 발행함 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class IntegPublishController {

    private final IntegPublishService integPublishService;

    @RequestMapping(value = "/integ/pub/user", method = {RequestMethod.POST})
    @Operation(summary = "회원 정보 등록", description = "")
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
    public ResponseDTO<CustomBody> intePubUserProc(@RequestBody Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.saveUserProc(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "회원 정보 등록");
    }

    @RequestMapping(value = "/integ/pub/exam", method = {RequestMethod.POST})
    @Operation(summary = "발행하기", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "", value = """
                            {
                                "examIdList" : ["1", "2"],
                                "tcUserId" : "exam10-t",
                                "publishType" : "2",
                                "evlSeCd" : "1",
                                "timStAt" : "Y",
                                "timTime" : "00:15:00",
                                "pdSetAt" : "Y",
                                "pdEvlStDt" : "2024-12-27",
                                "pdEvlEdDt" : "2024-12-27",
                                "claList": [
                                    {
                                      "claId": "exam10class1",
                                      "stdtUserId": [ "exam10class1-s1", "exam10class1-s2"]
                                    },
                                    {
                                      "claId": "exam10class2",
                                      "stdtUserId": ["exam10class2-s3", "exam10class2-s4"]
                                    }
                                ],
                                "podOptionList": [
                                    {
                                      "code": "pod_template",
                                      "val": "pod_template_01"
                                    },
                                    {
                                      "code": "pod_answer",
                                      "val": "Y"
                                    },
                                    {
                                      "code": "pod_description",
                                      "val": "Y"
                                    },
                                    {
                                      "code": "pod_eng_description",
                                      "val": "Y"
                                    },
                                    {
                                      "code": "pod_qr",
                                      "val": "pod_qr_all"
                                    },
                                    {
                                      "code": "pod_letter",
                                      "val": "pod_letter_A4"
                                    }
                                ]
                            }
                            """
                    )
            }
            ))
    public ResponseDTO<CustomBody> intePubExamPublish(@RequestBody Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.examPublishProc(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "발행하기");
    }

    @GetMapping(value = "/integ/publish/grp/list")
    @Operation(summary = "그룹별 발행 리스트 조회", description = "")
    @Parameter(name = "publishGrpId", description = "발행 그룹 ID", required = true, schema = @Schema(type = "integer", example = "25"))
    public ResponseDTO<CustomBody> findPublishGrpList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.listPublishGrpInfo(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "그룹별 발행 리스트 조회");
    }


    @Loggable
    @GetMapping(value = "/integ/publish/list")
    @Operation(summary = "내 발행함 - 발행 리스트별 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    @Parameter(name = "claIds", description = "학급 ID", required = false, schema = @Schema(type = "array", example = "[\"1f28a66305a341c79e95f8a1f4fbdb97\"]"))
    @Parameter(name = "publishTypes", description = "발행 구분 : 1: 비바클래스 발행, 2: URL 응시 발행, 3: 시험지 출력", required = false, schema = @Schema(type = "array", example = "[1, 2]"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "sttsCds", description = "필터조건(평가상태) : 1: 예정, 2: 진행중, 3: 종료", required = false, schema = @Schema(type = "array", example = "[1]"))
    @Parameter(name = "dateFrom", description = "시작 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-01-01"))
    @Parameter(name = "dateTo", description = "종료 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-12-31"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    @Parameter(name = "orderBy", description = "정렬 순서", required = false, schema = @Schema(type = "integer", example = "1"))
    public ResponseDTO<CustomBody> selectPublishBoxList(
            @RequestParam(required = false) List<String> claIds,
            @RequestParam(required = false) List<Integer> publishTypes,
            @RequestParam(required = false) List<Integer> sttsCds,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("claIds", claIds);
        paramData.put("publishTypes", publishTypes);
        paramData.put("sttsCds", sttsCds);
        Object resultData = integPublishService.selectPublishBoxList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 발행 리스트별 조회");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/detail")
    @Operation(summary = "내 발행함 - 발행 상세 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "exam10-t"))
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "2"))
    public ResponseDTO<CustomBody> findPublishBoxDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {

        Object resultData = integPublishService.findPublishBoxDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 발행 상세 조회");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/stdt-detail")
    @Operation(summary = "내 발행함 - 발행 상세 조회 - 학생 제출 정보", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "2"))
    @Parameter(name = "orderBy", description = "정렬 방식 : 1: 제출 시간, 2: 점수, 3: 가나다", required = false, schema = @Schema(type = "integer", allowableValues = {"1", "2", "3"}, defaultValue = ""))
    public ResponseDTO<CustomBody> selectPublishBoxStdtDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Object resultData = integPublishService.selectPublishBoxStdtDetail(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 발행 상세 조회 - 학생 제출 정보");
    }

    @RequestMapping(value = "/integ/publish/addStdt", method = {RequestMethod.POST})
    @Operation(summary = "내 발행함 - 발행 상세 - 학생 추가 ", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "stdtUserIds", description = "학생 ID (복수 가능)", required = true, schema = @Schema(type = "array", example = "[\"mathbe101-s1\", \"mathbe101-s2\"]"))
    public ResponseDTO<CustomBody> addStdt(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData,
            @RequestParam List<String> stdtUserIds) throws Exception {
        paramData.put("stdtUserIds", stdtUserIds);
        Object resultData = integPublishService.savePublishStdt(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 발행 상세 - 학생 추가");
    }

    @RequestMapping(value = "/integ/publish/pdSet", method = {RequestMethod.POST})
    @Operation(summary = "내 발행함 - 발행 상세 - 응시 기간 수정", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "pdSetAt", description = "응시 시간 설정", required = true, schema = @Schema(type = "string", example = "Y"))
    @Parameter(name = "pdEvlStDt", description = "응시 시작 기간", required = false, schema = @Schema(type = "string", example = "2025-02-25"))
    @Parameter(name = "pdEvlEdDt", description = "응시 종료 기간", required = false, schema = @Schema(type = "string", example = "2025-02-28"))
    public ResponseDTO<CustomBody> updatePdSet(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.updatePdSet(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 발행 상세 - 응시 기간 수정");
    }

    @RequestMapping(value = "/integ/publish/timTime", method = {RequestMethod.POST})
    @Operation(summary = "내 발행함 - 발행 상세 - 응시 시간 수정", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "timStAt", description = "응시 시간 설정", required = true, schema = @Schema(type = "string", example = "Y"))
    @Parameter(name = "timTime", description = "응시 시간", required = false, schema = @Schema(type = "string", example = "00:59:00"))
    public ResponseDTO<CustomBody> updateTimTime(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.updateTimTime(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 발행 상세 - 응시 시간 수정");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/claStdt-list")
    @Operation(summary = "내 발행함 - 클래스 학생별 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathqa45-t"))
    @Parameter(name = "stdtUserIds", description = "학생 ID", required = false, schema = @Schema(type = "array", example = "[\"mathqa45-s1\"]"))
    @Parameter(name = "claIds", description = "학급 ID", required = true, schema = @Schema(type = "array", example = "[\"1f28a66305a341c79e95f8a1f4fbdb97\"]"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectPublishBoxClaStdtList(
            @RequestParam(required = false) List<String> stdtUserIds,
            @RequestParam(required = true) List<String> claIds,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("stdtUserIds", stdtUserIds);
        paramData.put("claIds", claIds);
        Object resultData = integPublishService.selectPublishClaStdtList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 클래스 학생별 조회");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/claStdt-detail")
    @Operation(summary = "내 발행함 - 클래스 학생별 상세 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    @Parameter(name = "stdtUserIds", description = "학생 ID", required = true, schema = @Schema(type = "array", example = "[\"gcym2530102-6248-106393\", \"gcym2530102-6248-106394\"]"))
    @Parameter(name = "keyword", description = "키워드", required = false, schema = @Schema(type = "string"))
    @Parameter(name = "dateFrom", description = "시작 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-01-01"))
    @Parameter(name = "dateTo", description = "종료 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-12-31"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectPublishBoxClaStdtDetailList(
            @RequestParam(required = false) List<String> stdtUserIds,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("stdtUserIds", stdtUserIds);
        Object resultData = integPublishService.selectPublishBoxClaStdtDetailList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 클래스 학생별 상세 조회");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/nickname-list")
    @Operation(summary = "내 발행함 - 닉네임별 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    @Parameter(name = "nickNm", description = "학생 이름", required = false, schema = @Schema(type = "string", example = "카리"))
    @Parameter(name = "sttsCds", description = "필터조건(평가상태) : 2: 진행중, 3: 종료", required = false, schema = @Schema(type = "array", example = "[2]"))
    @Parameter(name = "dateFrom", description = "시작 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-01-01"))
    @Parameter(name = "dateTo", description = "종료 날짜 (yyyy-MM-dd)", required = false, schema = @Schema(type = "string", format = "date", example = "2024-12-31"))
    @Parameter(name = "page", description = "페이지번호", required = false, schema = @Schema(type = "integer", example = "0"))
    @Parameter(name = "size", description = "페이지크기", required = false, schema = @Schema(type = "integer", example = "10"))
    public ResponseDTO<CustomBody> selectPublishBoxNicknameList(
            @RequestParam(required = false) List<Integer> sttsCds,
            @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        paramData.put("sttsCds", sttsCds);
        Object resultData = integPublishService.selectPublishBoxNicknameList(paramData, pageable);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 닉네임별 조회");
    }

    @RequestMapping(value = "/integ/publish/update-hint", method = {RequestMethod.POST})
    @Operation(summary = "내 발행함 - 힌트 노출 여부 수정", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "hintOnOff", description = "힌트 노출 여부 (Y, N)", required = true)
    public ResponseDTO<CustomBody> updateUseHint(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.updateUseHint(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 힌트 노출 여부 수정");
    }

    @RequestMapping(value = "/integ/publish/delete", method = {RequestMethod.POST})
    @Operation(summary = "내 발행함 - 발행 삭제", description = "")
    @Parameter(name = "publishIds", description = "발행 ID", required = true, schema = @Schema(type = "array", example = "[1, 2]"))
    public ResponseDTO<CustomBody> deleteExamBox(
            @RequestParam(required = false) List<Integer> publishIds,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        paramData.put("publishIds", publishIds);
        Object resultData = integPublishService.updateDeletePublishBox(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 발행 삭제");
    }


    @Loggable
    @GetMapping(value = "/integ/publish/after-login/cnt")
    @Operation(summary = "내 발행함 - 메인 화면(로그인 후) - 개수", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    @Parameter(name = "intervalDt", description = "~ 일 전 마감")
    public ResponseDTO<CustomBody> getPublishBoxCntAfterLogin(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.getPublishBoxCntAfterLogin(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 메인 화면(로그인 후) - 개수");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/after-login/detail")
    @Operation(summary = "내 발행함 - 메인 화면(로그인 후) - 상세 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    @Parameter(name = "caseBy", description = "케이스 : 1: 마감 예정, 2: 피드백, 3: 채점필요", required = true, schema = @Schema(type = "integer", allowableValues = {"1", "2", "3"}, defaultValue = ""))
    @Parameter(name = "intervalDt", description = "~ 일 전 마감")
    public ResponseDTO<CustomBody> selectPublishBoxCntAfterLoginDetail(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.selectPublishBoxDetailAfterLogin(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 메인 화면(로그인 후) - 상세 조회");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/recent-option")
    @Operation(summary = "발행 옵션 설정 - 최근 발행물 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    public ResponseDTO<CustomBody> findPublishBoxRecentOptionList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        Object resultData = integPublishService.findPublishBoxRecentOptionList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "발행 옵션 설정 - 최근 발행물 조회");
    }

    @PostMapping(value = "/integ/publish/nickname-auth")
    @Operation(summary = "내 발행함 - 닉네임 발행 인증", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "stdtUserId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "pwd", description = "암호", required = true, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> publishNickNmAuth(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.savePublishNickNmAuth(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 닉네임 발행 인증");
    }

    @PostMapping(value = "/integ/publish/nickname-auth/init")
    @Operation(summary = "내 발행함 - 닉네임 발행 인증 - 비밀번호 초기화", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "stdtUserId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "initAt", description = "초기화 여부 : Y, N", required = true, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> publishNickNmAuthInit(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.updatePublishNickNmAuthInit(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 닉네임 발행 인증 - 비밀번호 초기화");
    }

    @PostMapping(value = "/integ/publish/nickname-auth/new-pwd")
    @Operation(summary = "내 발행함 - 닉네임 발행 인증 - 비밀번호 변경", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "stdtUserId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "newPwd", description = "새로운 암호", required = true, schema = @Schema(type = "string", example = ""))
    @Parameter(name = "checkNewPwd", description = "새로운 암호 확인", required = true, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> publishNickNmAuthNewPwd(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.updatePublishNickNmAuthNewPwd(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 닉네임 발행 인증 - 비밀번호 변경");
    }

    @GetMapping(value = "/integ/publish/stdt-by-nickname")
    @Operation(summary = "내 발행함 - 학생 ID 조회 By 닉네임", description = "")
    @Parameter(name = "publishId", description = "발행 ID", required = true, schema = @Schema(type = "integer", example = "1"))
    @Parameter(name = "nickNm", description = "닉네임", required = true, schema = @Schema(type = "string", example = ""))
    public ResponseDTO<CustomBody> findStdtByFlnm(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = integPublishService.findStdtByFlnm(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 학생 ID 조회 By 닉네임");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/claStdt-cla-list")
    @Operation(summary = "내 발행함 - 클래스 학생별 - 학급 목록 조회", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    public ResponseDTO<CustomBody> selectPublishBoxClaStdtClaList(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        Object resultData = integPublishService.selectPublishClaStdtClaList(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "내 발행함 - 클래스 학생별 - 학급 목록 조회");
    }

    @Loggable
    @GetMapping(value = "/integ/publish/exam/live-on-status")
    @Operation(summary = "평가중 상태 확인", description = "")
    @Parameter(name = "wrterId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "mathbe101-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "1f28a66305a341c79e95f8a1f4fbdb97"))
    public ResponseDTO<CustomBody> selectPublishBoxExamLiveOnStatus(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        Object resultData = integPublishService.selectPublishBoxExamLiveOnStatus(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "평가중 상태 확인");
    }
}
