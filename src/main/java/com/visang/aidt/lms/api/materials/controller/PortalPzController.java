package com.visang.aidt.lms.api.materials.controller;

import com.visang.aidt.lms.api.materials.service.PortalPzService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(포털)공공존 진입 페이지", description = "(포털)공공존 진입 페이지")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class PortalPzController {

    private final PortalPzService portalPzService;

    @RequestMapping(value = "/portal/pz/classInfo", method = {RequestMethod.GET})
    @Operation(summary = "(공공)교사 반정보조회", description = "(공공)교사 반정보조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "56a85ae2-529f-538b-9191-da884ded4535"))
    @Parameter(name = "openCourseCode", description = "개설 과목 코드", required = true, schema = @Schema(type = "string", example = "SBJCTM13100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016"))
    @Parameter(name = "lectureRoomCode", description = "강의실 코드", required = true, schema = @Schema(type = "string", example = "LCTRMM11000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000016"))
    public ResponseDTO<CustomBody> classInfo(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.getClassInfo(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공공)교사 반정보조회");
        } catch (IllegalArgumentException e) {
            log.error("classInfo - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("classInfo - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("classInfo - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("classInfo - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (Exception e) {
            log.error("classInfo - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/pz/classInfoByClassCode", method = {RequestMethod.GET})
    @Operation(summary = "(공공)교사 반정보조회", description = "(공공)교사 반정보조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "56a85ae2-529f-538b-9191-da884ded4535"))
    @Parameter(name = "classCode", description = "학급 코드", required = true, schema = @Schema(type = "string", example = "70d76d5e6ab2587839f4397fef5a3084"))
    public ResponseDTO<CustomBody> classInfoByClassCode(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.getClassInfoByClassCode(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공공)교사 반정보조회");
        } catch (IllegalArgumentException e) {
            log.error("classInfoByClassCode - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("classInfoByClassCode - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("classInfoByClassCode - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("classInfoByClassCode - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (Exception e) {
            log.error("classInfoByClassCode - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/pz/classInfoByLectureCode", method = {RequestMethod.GET})
    @Operation(summary = "(공공)교사 반정보조회", description = "(공공)교사 반정보조회")
    @Parameter(name = "userId", description = "교사 ID", required = true, schema = @Schema(type = "string", example = "c89b72ac-8c95-561a-828f-f38b656dace0"))
    @Parameter(name = "lectureCode", description = "강의 코드", required = true, schema = @Schema(type = "string", example = "LCTR160000223201"))
    public ResponseDTO<CustomBody> classInfoByLectureCode(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.getClassInfoByLectureCode(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공공)교사 반정보조회");
        } catch (IllegalArgumentException e) {
            log.error("classInfoByLectureCode - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("classInfoByLectureCode - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("classInfoByLectureCode - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("classInfoByLectureCode - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (Exception e) {
            log.error("classInfoByLectureCode - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/pz/tcTextbookList", method = {RequestMethod.POST})
    @Operation(summary = "(공공)교사 교과서목록조회", description = "(공공)교사 교과서목록조회")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "56a85ae2-529f-538b-9191-da884ded4535"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "70d76d5e6ab2587839f4397fef5a3084"))
    @Parameter(name = "partnerId", description = "파트너 ID", required = true, schema = @Schema(type = "string", example = "0f547ce0-4a01-558b-9b18-d35891e2176b"))
    public ResponseDTO<CustomBody> tcTextbookList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.tcTextbookList(paramData);
            boolean isExistsTask = MapUtils.getBoolean(paramData, "saveTcTaskEvl", false);
            // 중복 방지용으로 transction 바깥에서 처리
            if (isExistsTask) {
                String userId = MapUtils.getString(paramData, "userId");
                String claId = MapUtils.getString(paramData, "claId");
                String textbkId = MapUtils.getString(paramData, "textbkId");
                if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(claId) && StringUtils.isNotEmpty(textbkId)) {
                    Map<String, Object> saveTEParamMap = new HashMap<>();
                    saveTEParamMap.put("userId", userId);
                    saveTEParamMap.put("claId", claId);
                    saveTEParamMap.put("textbkId", textbkId);
                    portalPzService.saveTcTaskEvl(saveTEParamMap);
                }
            }
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공공)교사 교과서목록조회");
        } catch (IllegalArgumentException e) {
            log.error("tcTextbookList - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("tcTextbookList - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("tcTextbookList - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("tcTextbookList - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (RuntimeException e) {
            log.error("tcTextbookList - Runtime error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Runtime error occurred");
        } catch (Exception e) {
            log.error("tcTextbookList - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/pz/saveTcTextbook", method = {RequestMethod.POST})
    @Operation(summary = "(공공)교사 교과서선택", description = "(공공)교사 교과서선택")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "56a85ae2-529f-538b-9191-da884ded4535"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "70d76d5e6ab2587839f4397fef5a3084"))
    @Parameter(name = "textbkCrltnId", description = "교과서 배포 ID", required = true, schema = @Schema(type = "integer", example = "39"))
    public ResponseDTO<CustomBody> saveTcTextbook(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.saveTcTextbook(paramData);
            boolean isExistsTask = MapUtils.getBoolean(paramData, "saveTcTaskEvl", false);
            // 중복 방지용으로 transction 바깥에서 처리
            if (isExistsTask) {
                String userId = MapUtils.getString(paramData, "userId");
                String claId = MapUtils.getString(paramData, "claId");
                String textbkId = MapUtils.getString(paramData, "textbkId");
                if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(claId) && StringUtils.isNotEmpty(textbkId)) {
                    Map<String, Object> saveTEParamMap = new HashMap<>();
                    saveTEParamMap.put("userId", userId);
                    saveTEParamMap.put("claId", claId);
                    saveTEParamMap.put("textbkId", textbkId);
                    portalPzService.saveTcTaskEvl(saveTEParamMap);
                }
            }
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공공)교사 교과서선택");
        } catch (IllegalArgumentException e) {
            log.error("saveTcTextbook - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("saveTcTextbook - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("saveTcTextbook - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("saveTcTextbook - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (RuntimeException e) {
            log.error("saveTcTextbook - Runtime error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Runtime error occurred");
        } catch (Exception e) {
            log.error("saveTcTextbook - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/pz/stTextbookInfo", method = {RequestMethod.GET})
    @Operation(summary = "(공공)학생 교과서조회", description = "(공공)학생 교과서조회")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "1d4f48fb-55da-5d05-9244-d1eda9fd00d2"))
    public ResponseDTO<CustomBody> stTextbookList(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.stTextbookInfo(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(공공)학생 교과서조회");
        } catch (IllegalArgumentException e) {
            log.error("stTextbookList - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Invalid parameters provided");
        } catch (NullPointerException e) {
            log.error("stTextbookList - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Missing required data");
        } catch (DataAccessException e) {
            log.error("stTextbookList - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database operation failed");
        } catch (SQLException e) {
            log.error("stTextbookList - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Database query failed");
        } catch (Exception e) {
            log.error("stTextbookList - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "Error - Unexpected error occurred");
        }
    }

    @RequestMapping(value = "/portal/saveTcTaskEvl", method = {RequestMethod.POST})
    @Operation(summary = "교사 교과서 과제/평가 출제", description = "교사가 교과서 선택 시 교과서에 설정되어 있는 과제/평가 자동 출제")
    @Parameter(name = "userId", description = "사용자 ID", required = true, schema = @Schema(type = "string", example = "mathbook220-t"))
    @Parameter(name = "claId", description = "학급 ID", required = true, schema = @Schema(type = "string", example = "7c626ef19b1e45469290bf67af8b5206"))
    @Parameter(
            name = "textbkId",
            description = "교과서 ID",
            required = true,
            schema = @Schema(
                    type = "integer", example = "1192"
            ),
            in = ParameterIn.QUERY
    )
    public ResponseDTO<CustomBody> saveTcTaskEvl(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.saveTcTaskEvl(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 교과서 과제/평가 출제 성공");
        } catch (IllegalArgumentException e) {
            log.error("saveTcTaskEvl - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - 잘못된 파라미터");
        } catch (NullPointerException e) {
            log.error("saveTcTaskEvl - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - 필수 데이터 누락");
        } catch (DataAccessException e) {
            log.error("saveTcTaskEvl - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - 데이터베이스 오류");
        } catch (SQLException e) {
            log.error("saveTcTaskEvl - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - SQL 오류");
        } catch (Exception e) {
            log.error("saveTcTaskEvl - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패");
        }
    }

    @RequestMapping(value = "/portal/saveTcTaskEvlByUserGroup", method = {RequestMethod.POST})
    @Operation(summary = "교사 교과서 과제/평가 출제 (user group을 받아 처리)", description = "교사가 교과서 선택 시 교과서에 설정되어 있는 과제/평가 자동 출제 (user group을 받아 처리)")
    @Parameter(name = "groupKey", description = "사용자 그룹 키 (tc_textbook 테이블 wrter_id 컬럼 like 검색)", required = true, schema = @Schema(type = "string", example = "stress"))
    @Parameter(
            name = "textbkId",
            description = "교과서 ID",
            required = true,
            schema = @Schema(
                    type = "integer", example = "1191"
            ),
            in = ParameterIn.QUERY
    )
    public ResponseDTO<CustomBody> saveTcTaskEvlByUserGroup(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {
        try {
            Object resultData = portalPzService.saveTcTaskEvlByUserGroup(paramData);
            return AidtCommonUtil.makeResultSuccess(paramData, resultData, "교사 교과서 과제/평가 출제 성공");
        } catch (IllegalArgumentException e) {
            log.error("saveTcTaskEvlByUserGroup - Invalid argument error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - 잘못된 파라미터");
        } catch (NullPointerException e) {
            log.error("saveTcTaskEvlByUserGroup - Null pointer error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - 필수 데이터 누락");
        } catch (DataAccessException e) {
            log.error("saveTcTaskEvlByUserGroup - Database access error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - 데이터베이스 오류");
        } catch (SQLException e) {
            log.error("saveTcTaskEvlByUserGroup - SQL error: {}", e.getMessage());
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패 - SQL 오류");
        } catch (Exception e) {
            log.error("saveTcTaskEvlByUserGroup - Unexpected error: {}", CustomLokiLog.errorLog(e));
            return AidtCommonUtil.makeResultFail(paramData, null, "교사 교과서 과제/평가 출제 실패");
        }
    }
}