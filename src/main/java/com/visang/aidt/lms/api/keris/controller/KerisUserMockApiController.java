package com.visang.aidt.lms.api.keris.controller;

import com.visang.aidt.lms.api.keris.service.KerisUserMockApiService;
import com.visang.aidt.lms.api.keris.service.KerisApiService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;

import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.function.Function;

import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "테스트 정보 조회", description = "테스트 정보 조회")
public class KerisUserMockApiController {

    private final KerisUserMockApiService service;
    private final KerisApiService kerisApiService;
    
    // 학생 성명 검색
    @GetMapping({"/test/aidt_userinfo/{userType}/{searchType}", "/aidt_userinfo/{userType}/{searchType}"})
    @Operation(summary = "학생 성명 검색", description = "")
    public ResponseEntity<Object> getInfo(@RequestHeader("Partner-ID") String partnerID,
                                          @PathVariable("userType") String userType,
                                          @PathVariable("searchType") String searchType,
                                          @RequestParam(value="user_id", required = false) String userId,
                                          @RequestParam(value="user_ids", required = false) List<String> userIds) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        // user_id와 user_ids를 모두 쓰는 searchType들
        List<String> userIdsUseList = Arrays.asList("name", "school_name", "school_id", "division", "grade", "class", "number", "gender");

        if (userIdsUseList.contains(searchType) && (StringUtils.isEmpty(userId) && CollectionUtils.isEmpty(userIds))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);

        } else if (!userIdsUseList.contains(searchType) && (StringUtils.isEmpty(userId))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }


        if("all".equals(searchType)) {
            Map<String, Object> paramData = new HashMap<>();
            paramData.put("user_id", userId);
            return infoAll(partnerID, userType, searchType, paramData);
        }

        return info(partnerID, userType, searchType, userId, userIds);
    }

    // 학생 성명 검색
    @PostMapping({"/test/aidt_userinfo/{userType}/{searchType}", "/aidt_userinfo/{userType}/{searchType}"})
    @Operation(summary = "학생 성명 검색", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터1", value = """
                            {
                                "access_token": {
                                    "token": "0a55d4909026e959c403eb7b01eb7065cdb7175f",
                                    "access_id": "adf"
                                },
                                "user_id" : "21868e4c-b42e-5469-ac65-5ccd8e2fc4e2"
                            }
                            """
                    ),
                    @ExampleObject(name = "파라미터2", value = """
                            {
                                "access_token": {
                                    "token": "0a55d4909026e959c403eb7b01eb7065cdb7175f",
                                    "access_id": "adf"
                                },
                                "user_ids" : [
                                    "430e8400-e29b-41d4-a746-446655440000",
                                    "550e8400-e29b-41d4-a716-446655440000"
                                ]
                            }
                            """
                    )
            }
    ))
    public ResponseEntity<Object> postInfo(@RequestHeader("Partner-ID") String partnerID,
                                           @PathVariable("userType") String userType,
                                           @PathVariable("searchType") String searchType,
                                           @RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        // user_id와 user_ids를 모두 쓰는 searchType들
        List<String> userIdsUseList = Arrays.asList("name", "school_name", "school_id", "division", "grade", "class", "number", "gender");

        if (userIdsUseList.contains(searchType) && (StringUtils.isEmpty((String) paramData.get("user_id")) && CollectionUtils.isEmpty((List<String>) paramData.get("user_ids")))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);

        } else if (!userIdsUseList.contains(searchType) && (StringUtils.isEmpty((String) paramData.get("user_id")))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        if("all".equals(searchType)) {
            return infoAll(partnerID, userType, searchType, paramData);
        }

        @SuppressWarnings("unchecked")
        List<String> userIds = (List<String>) paramData.get("user_ids");
        String userId = (String) paramData.get("user_id");
        return info(partnerID, userType, searchType, userId, userIds);
    }

    private ResponseEntity<Object> info(String partnerID,
                                        String userType,
                                        String searchType,
                                        String userId,
                                        List<String> userIds) throws Exception {
        String key = searchType;
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = new HashMap<>();

        boolean unique = (userId == null || userId.isEmpty() ? false : true);
        if(userIds == null) {
            userIds = new ArrayList<>();
            userIds.add("");
        }
        if(unique) {
            userIds.clear();
            userIds.add(userId);
        }

        paramData.put("user_ids", userIds);
        List<Map<String, Object>> value = null;
        try{
            switch (userType) {
                case "student":
                    switch (searchType) {
                        case "name":
                            key = "user_name";
                            value = service.getStdtName(paramData);
                            break;
                        case "school_name":
                            value = service.getStdtSchoolName(paramData);
                            break;
                        case "school_id":
                            value = service.getStdtSchoolId(paramData);
                            break;
                    }
                    break;
                case "teacher":
                    switch (searchType) {
                        case "name":
                            key = "user_name";
                            value = service.getTcName(paramData);
                            break;
                        case "school_name":
                            value = service.getTcSchoolName(paramData);
                            break;
                        case "school_id":
                            value = service.getTcSchoolId(paramData);
                            break;
                    }
                    break;
            }
            if(unique == false) key = "user_list";
            if(value == null || value.isEmpty()) {
                resultData.put("code", "40401");
                resultData.put("message", "존재하지 않은 데이터");
                resultData.put(key, (unique ? "" : new ArrayList<>()));
            }
            else {
                resultData.put("code", "00000");
                resultData.put("message", "성공");
                resultData.put(key, (unique ? value.get(0).get(key) : value));
            }
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));

            resultData.put("code", "50001");
            resultData.put("message", "시스템 오류");
        }

        return ResponseEntity.status(HttpStatus.OK).body(resultData);
    }

    private ResponseEntity<Object> infoAll(String partnerID,
                                        String userType,
                                        String searchType,
                                        Map<String, Object> paramData) throws Exception {
        
        Map<String, Object> resultData = new HashMap<>();
        try{
            Map<String, Object> map = ("student".equals(userType) ? service.getStdtAll(paramData) : service.getTcAll(paramData));
            if(map == null || map.isEmpty()) {
                resultData.put("code", "40401");
                resultData.put("message", "존재하지 않은 데이터");
                resultData.put("schedule_info", new ArrayList<>());
            }
            else {
                resultData.put("code", "00000");
                resultData.put("message", "성공");

                resultData.putAll(map);
                resultData.put("schedule_info", service.getScheduleList(paramData));

                if("teacher".equals(userType)) {
                    List<Map<String, Object>> list = service.getClassList(paramData);
                    resultData.put("class_info", (list == null ? new ArrayList<>() : list));

                    List<Map<String, Object>> lectureInfo = service.getLectureInfo(paramData);
                    resultData.put("lecture_info", lectureInfo);
                } else if ("student".equals(userType)) {
                    resultData.put("lecture_info", new ArrayList<>());
                }
            }
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));

            resultData.put("code", "50001");
            resultData.put("message", "시스템 오류");
        }

        return ResponseEntity.status(HttpStatus.OK).body(resultData);
    }

    /**
     * 학생 학교 구분 조회
     * @param userId 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "학생 학교 구분 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/division", "/aidt_userinfo/student/division"}, method = {RequestMethod.GET})
    @Parameter(name = "user_id", description = "사용자 ID(개인 식별코드)", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "user_ids", description = "조회 대상 사용자 리스트", required = false, array = @ArraySchema(schema = @Schema(type = "string", example = "2")))
    public ResponseEntity<Object> sdDivisionGet(
            @RequestParam(value="user_id", required = false) String user_id,
            @RequestParam(value="user_ids", required = false) String[] user_ids,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(user_id) && user_ids == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        paramData.put("user_ids", user_ids);

        return this.getSdDivision(paramData);
    }

    /**
     * 학생 학교 구분 조회
     * @param userId 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "학생 학교 구분 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/division", "/aidt_userinfo/student/division"}, method = {RequestMethod.POST})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "access_token" : "F58D9956AB92798F51B7F9D833D0C42B",
                                "user_id": "1",
                                "user_ids": ["1", "2"]
                            }
                            """
                    )
            }
            ))
    public ResponseEntity<Object> sdDivisionPost(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", "")) && paramData.get("user_ids") == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        return this.getSdDivision(paramData);
    }

    /**
     * 학생 학교 구분 조회 서비스 콜
     * @param paramData
     * @return
     * @throws Exception
     */
    private ResponseEntity<Object> getSdDivision(Map<String, Object> paramData) throws Exception{
        Map<String, Object> resultDate = new HashMap<String, Object>();
        String code = "40401";
        String message = "존재하지 않은 데이터";
        String userDivision = "";

        try {
            List<Map<String, Object>> infoData = service.getSdDivision(paramData);

            if(infoData != null && !infoData.isEmpty()){
                code = "00000";
                message = "성공";

                if(paramData.get("user_id") != null && !"".equals(StringUtils.defaultIfEmpty(paramData.get("user_id").toString(), ""))){
                    userDivision = MapUtils.getString((Map<String, Object>)infoData.get(0), "user_division", "");
                }else{
                    resultDate.put("user_list", infoData);
                }
            }
        }catch(Exception e){
            code = "50001";
            message = "시스템 오류";
            userDivision = "";
        }

        resultDate.put("code", code);
        resultDate.put("message", message);
        if(resultDate.get("user_list") == null) {
            resultDate.put("user_division", userDivision);
        }

        return ResponseEntity.ok(resultDate);
    }

    /**
     * 학생 학년 조회
     * @param userId 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "학생 학년 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/grade", "/aidt_userinfo/student/grade"}, method = {RequestMethod.GET})
    @Parameter(name = "user_id", description = "사용자 ID(개인 식별코드)", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "user_ids", description = "조회 대상 사용자 리스트", required = false, array = @ArraySchema(schema = @Schema(type = "string", example = "2")))
    public ResponseEntity<Object> sdGradeGet(
            @RequestParam(value="user_id", required = false) String user_id,
            @RequestParam(value="user_ids", required = false) String[] user_ids,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(user_id) && user_ids == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        paramData.put("user_ids", user_ids);

        return this.sdGrade(paramData);
    }

    @Operation(summary = "학생 학년 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/grade", "/aidt_userinfo/student/grade"}, method = {RequestMethod.POST})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "access_token" : "F58D9956AB92798F51B7F9D833D0C42B",
                                "user_id": "1",
                                "user_ids": ["1", "2"]
                            }
                            """
                    )
            }
            ))
    public ResponseEntity<Object> sdGradePost(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", "")) && paramData.get("user_ids") == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        return this.sdGrade(paramData);
    }

    /**
     * 학생 학년 조회 서비스 콜
     * @param paramData
     * @return
     * @throws Exception
     */
    private ResponseEntity<Object> sdGrade(Map<String, Object> paramData) throws Exception{
        Map<String, Object> resultDate = new HashMap<String, Object>();
        String code = "40401";
        String message = "존재하지 않은 데이터";
        String userGrade = "";

        try {
            List<Map<String, Object>> infoData = service.getSdGrade(paramData);

            if(infoData != null && !infoData.isEmpty()){
                code = "00000";
                message = "성공";


                if(paramData.get("user_id") != null && !"".equals(StringUtils.defaultIfEmpty(paramData.get("user_id").toString(), ""))){
                    userGrade = MapUtils.getString((Map<String, Object>)infoData.get(0), "user_grade", "");
                }else{
                    resultDate.put("user_list", infoData);
                }
            }
        }catch(Exception e){
            code = "50001";
            message = "시스템 오류";
            userGrade = "";
        }

        resultDate.put("code", code);
        resultDate.put("message", message);
        if(resultDate.get("user_list") == null) {
            resultDate.put("user_grade", userGrade);
        }

        return ResponseEntity.ok(resultDate);
    }

    /**
     * 학생 반 조회
     * @param userId 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "학생 반 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/class", "/aidt_userinfo/student/class"}, method = {RequestMethod.GET})
    @Parameter(name = "user_id", description = "사용자 ID(개인 식별코드)", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "user_ids", description = "조회 대상 사용자 리스트", required = false, array = @ArraySchema(schema = @Schema(type = "string", example = "2")))
    public ResponseEntity<Object> sdClassGet(
            @RequestParam(value="user_id", required = false) String user_id,
            @RequestParam(value="user_ids", required = false) String[] user_ids,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(user_id) && user_ids == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        paramData.put("user_ids", user_ids);

        return this.sdClass(paramData);
    }

    /**
     * 학생 반 조회
     * @param userId 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "학생 반 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/class", "/aidt_userinfo/student/class"}, method = {RequestMethod.POST})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "access_token" : "F58D9956AB92798F51B7F9D833D0C42B",
                                "user_id": "1",
                                "user_ids": ["1", "2"]
                            }
                            """
                    )
            }
            ))
    public ResponseEntity<Object> sdClassPost(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", "")) && paramData.get("user_ids") == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        return this.sdClass(paramData);
    }

    /**
     * 학생 반 조회 서비스 콜
     * @param paramData
     * @return
     * @throws Exception
     */
    private ResponseEntity<Object> sdClass(Map<String, Object> paramData) throws Exception{
        Map<String, Object> resultDate = new HashMap<String, Object>();
        String code = "40401";
        String message = "존재하지 않은 데이터";
        String userClass = "";

        try {
            List<Map<String, Object>> infoData = service.getStClass(paramData);

            if(infoData != null && !infoData.isEmpty()){
                code = "00000";
                message = "성공";

                if(paramData.get("user_id") != null && !"".equals(StringUtils.defaultIfEmpty(paramData.get("user_id").toString(), ""))){
                    userClass = MapUtils.getString((Map<String, Object>)infoData.get(0), "user_class", "");
                }else{
                    resultDate.put("user_list", infoData);
                }
            }
        }catch(Exception e){
            code = "50001";
            message = "시스템 오류";
            userClass = "";
        }

        resultDate.put("code", code);
        resultDate.put("message", message);
        if(resultDate.get("user_list") == null) {
            resultDate.put("user_class", userClass);
        }

        return ResponseEntity.ok(resultDate);
    }

    /**
     * 학생 번호 조회
     * @param userId 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "학생 번호 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/number", "/aidt_userinfo/student/number"}, method = {RequestMethod.GET})
    @Parameter(name = "user_id", description = "사용자 ID(개인 식별코드)", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "user_ids", description = "조회 대상 사용자 리스트", required = false, array = @ArraySchema(schema = @Schema(type = "string", example = "2")))
    public ResponseEntity<Object> sdNumberGet(
            @RequestParam(value="user_id", required = false) String user_id,
            @RequestParam(value="user_ids", required = false) String[] user_ids,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(user_id) && user_ids == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        paramData.put("user_ids", user_ids);

        return this.sdNumber(paramData);
    }

    @Operation(summary = "학생 번호 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/number", "/aidt_userinfo/student/number"}, method = {RequestMethod.POST})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "access_token" : "F58D9956AB92798F51B7F9D833D0C42B",
                                "user_id": "1",
                                "user_ids": ["1", "2"]
                            }
                            """
                    )
            }
            ))
    public ResponseEntity<Object> sdNumberPost(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", "")) && paramData.get("user_ids") == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        return this.sdNumber(paramData);
    }

    /**
     * 학생 성별 조회
     * @param userId 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "학생 성별 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/gender", "/aidt_userinfo/student/gender"}, method = {RequestMethod.GET})
    @Parameter(name = "user_id", description = "사용자 ID(개인 식별코드)", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "user_ids", description = "조회 대상 사용자 리스트", required = false, array = @ArraySchema(schema = @Schema(type = "string", example = "2")))
    public ResponseEntity<Object> sdGenderGet(
            @RequestParam(value="user_id", required = false) String user_id,
            @RequestParam(value="user_ids", required = false) String[] user_ids,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(user_id) && user_ids == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        paramData.put("user_ids", user_ids);

        return this.sdGender(paramData);
    }

    @Operation(summary = "학생 성별 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/student/gender", "/aidt_userinfo/student/gender"}, method = {RequestMethod.POST})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "access_token" : "F58D9956AB92798F51B7F9D833D0C42B",
                                "user_id": "1",
                                "user_ids": ["1", "2"]
                            }
                            """
                    )
            }
            ))
    public ResponseEntity<Object> sdGenderPost(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", "")) && paramData.get("user_ids") == null) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 user_ids: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        return this.sdGender(paramData);
    }

    /**
     * 학생 성별 조회 서비스 콜
     * @param paramData
     * @return
     * @throws Exception
     */
    private ResponseEntity<Object> sdGender(Map<String, Object> paramData) throws Exception{
        Map<String, Object> resultDate = new HashMap<String, Object>();
        String code = "40401";
        String message = "존재하지 않은 데이터";
        String userGender = "";

        try {
            List<Map<String, Object>> infoData = service.getStGender(paramData);

            if(infoData != null && !infoData.isEmpty()){
                code = "00000";
                message = "성공";

                if(paramData.get("user_id") != null && !"".equals(StringUtils.defaultIfEmpty(paramData.get("user_id").toString(), ""))){
                    userGender = MapUtils.getString((Map<String, Object>)infoData.get(0), "user_gender", "");
                }else{
                    resultDate.put("user_list", infoData);
                }
            }
        }catch(Exception e){
            code = "50001";
            message = "시스템 오류";
            userGender = "";
        }

        resultDate.put("code", code);
        resultDate.put("message", message);
        if(resultDate.get("user_list") == null) {
            resultDate.put("user_gender", userGender);
        }

        return ResponseEntity.ok(resultDate);
    }

    /**
     * 학생 번호 조회 서비스 콜
     * @param paramData
     * @return
     * @throws Exception
     */
    private ResponseEntity<Object> sdNumber(Map<String, Object> paramData) throws Exception{
        Map<String, Object> resultDate = new HashMap<String, Object>();
        String code = "40401";
        String message = "존재하지 않은 데이터";
        String userNumber = "";

        try {
            List<Map<String, Object>> infoData = service.getStNumber(paramData);

            if(infoData != null && !infoData.isEmpty()){
                code = "00000";
                message = "성공";

                if(paramData.get("user_id") != null && !"".equals(StringUtils.defaultIfEmpty(paramData.get("user_id").toString(), ""))){
                    userNumber = MapUtils.getString((Map<String, Object>)infoData.get(0), "user_number", "");
                }else{
                    resultDate.put("user_list", infoData);
                }
            }
        }catch(Exception e){
            code = "50001";
            message = "시스템 오류";
            userNumber = "";
        }

        resultDate.put("code", code);
        resultDate.put("message", message);
        if(resultDate.get("user_list") == null) {
            resultDate.put("user_number", userNumber);
        }

        return ResponseEntity.ok(resultDate);
    }

    // 학생 성명 검색
    @GetMapping({"/test/aidt_userinfo/teacher/class_list", "/aidt_userinfo/teacher/class_list"})
    @Operation(summary = "교사 학급 목록 조회", description = "")
    @Parameter(name = "access_token")
    @Parameter(name = "user_id")
    public ResponseEntity<Object> getClassList(@RequestHeader("Partner-ID") String partnerID,
                                               @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", ""))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }
        return classList(partnerID, paramData);
    }

    // 학생 성명 검색
    @PostMapping({"/test/aidt_userinfo/teacher/class_list", "/aidt_userinfo/teacher/class_list"})
    @Operation(summary = "교사 학급 목록 조회", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(examples = {
            @ExampleObject(name = "파라미터", value = """
                {
                    "access_token": "0a55d4909026e959c403eb7b01eb7065cdb7175f",
                    "user_id" : "21868e4c-b42e-5469-ac65-5ccd8e2fc4e2"
                }
                """
            )
        }
    ))
    public ResponseEntity<Object> postClassList(@RequestHeader("Partner-ID") String partnerID,
                                                @RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", ""))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        return classList(partnerID, paramData);
    }
    
    private ResponseEntity<Object> classList(String partnerID, Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultDate = new HashMap<String, Object>();
        String code = "40401";
        String message = "존재하지 않은 데이터";
        List<Map<String, Object>> classInfo = null;

        try {
            classInfo = service.getClassList(paramData);
            if(classInfo == null || classInfo.isEmpty()){
                classInfo = new ArrayList<>();
            }
            else {
                code = "00000";
                message = "성공";
            }
        }catch(Exception e){
            code = "50001";
            message = "시스템 오류";
            classInfo = new ArrayList<>();
        }

        resultDate.put("code", code);
        resultDate.put("message", message);
        resultDate.put("class_info", classInfo);

        return ResponseEntity.ok(resultDate);
    }

    /**
     * 학생 번호 조회
     * @param user_id 사용자 ID(개인 식별코드)
     * @return
     * @throws Exception
     */
    @Operation(summary = "사용자 식별 ID를 통한 교사 시간표 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/teacher/schedule", "/aidt_userinfo/teacher/schedule"}, method = {RequestMethod.GET})
    @Parameter(name = "user_id", description = "조회 대상사용자 ID", required = true, schema = @Schema(type = "string", example = "c89b72ac-8c95-561a-828f-f38b656dace0"))
    public ResponseEntity<Object> scheduleGet(
            @RequestParam(value="user_id", required = true) String user_id,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(user_id)) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        paramData.put("user_id", user_id);

        return this.schedule(paramData);
    }

    @Operation(summary = "사용자 식별 ID를 통한 교사 시간표 조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/teacher/schedule", "/aidt_userinfo/teacher/schedule"}, method = {RequestMethod.POST})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "access_token" :{
                                    "token" : "F58D9956AB92798F51B7F9D833D0C42B",
                                    "access_id": "1"
                                },
                                "user_id": "c89b72ac-8c95-561a-828f-f38b656dace0"
                            }
                            """
                    )
            }
            ))
    public ResponseEntity<Object> schedulePost(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", ""))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        Map<String, Object> access = (Map<String, Object>)paramData.get("access_token");
        paramData.put("access_id", access.get("access_id"));
        return this.schedule(paramData);
    }

    private ResponseEntity<Object> schedule(Map<String, Object> paramData) throws Exception{
        return ResponseEntity.ok(service.scheduleList(paramData));
    }

    @RequestMapping(value = "/test/stnt/class/change", method = {RequestMethod.GET})
    @Operation(summary = "(테스트) 학생 반이동", description = "")
    @Parameter(name = "stdtId", description = "대상 학생", required = false, schema = @Schema(type = "string", example = "mathcctest1-t"))
    @Parameter(name = "tcId", description = "원본 학급의 교사 ID", required = false, schema = @Schema(type = "string", example = "mathcctest1-s1"))
    @Parameter(name = "trgtTcId", description = "대상 학급의 교사 ID", required = false, schema = @Schema(type = "string", example = "mathcctest1-s1"))
    public ResponseDTO<CustomBody> classChange(
            @RequestParam(name = "stdtId", defaultValue = "0", required = true) String stdtId,
            @RequestParam(name = "tcId", defaultValue = "0", required = true) String tcId,
            @RequestParam(name = "trgtTcId", defaultValue = "0", required = true) String trgtTcId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Object resultData = service.updateClassChange(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(테스트) 학생 반이동");
    }

    @RequestMapping(value = "/test/stnt/class/add", method = {RequestMethod.GET})
    @Operation(summary = "(테스트) 학생 추가", description = "")
    @Parameter(name = "tcId", description = "추가하고자 하는 반 교사 ID", required = false, schema = @Schema(type = "string", example = "mathcctest1-t"))
    @Parameter(name = "stdtId", description = "대상 학생", required = false, schema = @Schema(type = "string", example = "mathcctest1-s1"))
    public ResponseDTO<CustomBody> addClass(
            @RequestParam(name = "tcId", defaultValue = "0", required = true) String tcId,
            @RequestParam(name = "stdtId", defaultValue = "0", required = true) String stdtId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData)throws Exception {
        Object resultData = service.addClass(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(테스트) 학생 추가");
    }


    @RequestMapping(value = "/test/tch/class/overwrite", method = {RequestMethod.GET})
    @Operation(summary = "(테스트) 정담임/부담임", description = "")
    @Parameter(name = "srcTcId", description = "옮기려는 선생님", required = false, schema = @Schema(type = "string", example = "mathcctest1-t"))
    @Parameter(name = "trgTcId", description = "대상 선생님", required = false, schema = @Schema(type = "string", example = "mathcctest2-t"))
    @Parameter(name = "trgClaId", description = "대상 학급ID", required = false, schema = @Schema(type = "string", example = "800896b9dfcc4b3cb862f73652c572d4"))
    public ResponseDTO<CustomBody> overwriteClass(
            @RequestParam(name = "srcTcId", defaultValue = "0", required = true) String srcTcId,
            @RequestParam(name = "trgTcId", defaultValue = "0", required = true) String trgTcId,
            @RequestParam(name = "trgClaId", defaultValue = "0", required = true) String trgClaId,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    )throws Exception {
        Object resultData = service.overwriteClass(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "(테스트) 학생 추가");
    }

    @PostMapping(value = "/test/keris/prev/index/proc", produces="application/json; charset=UTF8")
    @Operation(summary = "(테스트) 내부계정 미리보기 접속", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                            {
                                "user_id" : "mathbe2-t",
                                "partnerId" : "d79366e1-d506-51c4-a758-601f2945a7a4"
                            }
                            """
                    )
            }
            ))
    public @ResponseBody  Map<String, Object> testPrevKerisIndexProc(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            result = service.testKerisPrevProc(paramData);
        } catch (Exception e) {
            log.error("err: {}", e.getMessage());
            String errorMessage = e.getMessage();
            String[] parts = errorMessage.split("###");
            if (parts.length > 1) {
                String[] errorParts = parts[1].split("###");
                if (errorParts.length > 0 ) {
                    String firstLine = errorParts[0].trim();
                    if (!firstLine.isEmpty()) {
                        errorMessage = firstLine;
                    }
                }
            }
            result.put("code", "50001");
            result.put("message", errorMessage);
        }
        return result;
    }


    @PostMapping(value = "/test/keris/index/proc", produces="application/json; charset=UTF8")
    @Operation(summary = "(테스트) 내부계정 본강의 접속", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                             {
                                "user_id" : "mathbe2-t",
                                "partnerId" : "d79366e1-d506-51c4-a758-601f2945a7a4"
                            }
                            """
                    )
            }
            ))
    public @ResponseBody Map<String, Object> testKerisProc(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
//            Map<String, Object> serviceResult = kerisApiService.updateClaMemberActivation(paramData);
//            result.putAll(serviceResult);
            result.put("code", "00000");
            result.put("message", "성공");
        } catch (Exception e) {
            result.put("code", "50001");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/test/keris/lecture/list", produces="application/json; charset=UTF8")
    @Operation(summary = "(테스트) 교사 강의 리스트", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터(주기적으로 바뀌는 값이어서 신뢰할 수 없음)", value = """
                             {
                                "user_id" : "mathbe2-t",
                                "partnerId" : "d79366e1-d506-51c4-a758-601f2945a7a4"
                            }
                            """
                    )
            }
            ))
    public @ResponseBody Map<String, Object> lectureList(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = service.lectureList(paramData);
        return resultData;
    }


    @Operation(summary = "강의코드를 이용한 개설과목 정보조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/teacher/open_subject_info", "/aidt_userinfo/teacher/open_subject_info"}, method = {RequestMethod.GET})
    @Parameter(name = "user_id", description = "사용자 ID(개인 식별코드)", required = false, schema = @Schema(type = "string", example = "1"))
    @Parameter(name = "lecture_code", description = "강의코드", required = false, schema = @Schema(type = "string", example = "1"))
    public ResponseEntity<Object> openSubjectInfoGet(
            @RequestParam(value="user_id", required = false) String user_id,
            @RequestParam(value="lecture_code", required = false) String lecture_code,
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(user_id) || StringUtils.isEmpty(lecture_code)) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 lecture_code: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        Map<String, Object> data = service.getSubjectInfo(paramData);
        if (MapUtils.isNotEmpty(data)) {
            resultData.put("code", "00000");
            resultData.put("message", "성공");
            resultData.putAll(service.getSubjectInfo(paramData));
        } else {
            resultData.put("code", "40401");
            resultData.put("message", "존재하지 않은 데이터");
        }
        return ResponseEntity.ok(resultData);
    }

    @Operation(summary = "강의코드를 이용한 개설과목 정보조회", description = "")
    @RequestMapping(value = {"/test/aidt_userinfo/teacher/open_subject_info", "/aidt_userinfo/teacher/open_subject_info"}, method = {RequestMethod.POST})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = """ 
                            {
                                "access_token" : "F58D9956AB92798F51B7F9D833D0C42B",
                                "user_id": "1",
                                "lecture_code": "2"
                            }
                            """
                    )
            }
            ))
    public ResponseEntity<Object> openSubjectInfoPost(@RequestBody Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        if (StringUtils.isEmpty(MapUtils.getString(paramData, "user_id", ""))
                || StringUtils.isEmpty(MapUtils.getString(paramData, "lecture_code", ""))) {
            resultData.put("code", "40001");
            resultData.put("message", "파라메터오류:[user_id 또는 lecture_code: 필수체크 오류]");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultData);
        }

        Map<String, Object> data = service.getSubjectInfo(paramData);
        if (MapUtils.isNotEmpty(data)) {
            resultData.put("code", "00000");
            resultData.put("message", "성공");
            resultData.putAll(service.getSubjectInfo(paramData));
        } else {
            resultData.put("code", "40401");
            resultData.put("message", "존재하지 않은 데이터");
        }

        log.info("resultData:{}", resultData);
        return ResponseEntity.ok(resultData);
    }
}
