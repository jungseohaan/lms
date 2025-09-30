package com.visang.aidt.lms.api.socket.controller;

import com.visang.aidt.lms.api.socket.service.TeacherService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import com.visang.aidt.lms.global.vo.socket.SocketExceptionBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/teacher", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "(Socket) Teacher API", description = "(Socket) 선생님 API")
public class TeacherController {

    private final TeacherService teacherService;


    @GetMapping(value = "/_openclass.json")
    @Operation(summary = "선생님 클래스 오픈", description = "선생님 클래스 오픈 API", parameters = {
            @Parameter(name = "classid", description = "클래스 IDX", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "tch_idx", description = "선생님 IDX", required = true, schema = @Schema(type = "string", example = "1")),
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject("""
                            {"classlogid": 128272,"result": 0,"classid": 364,"teacherName": "Smith Aile","students": [{"birthday": "19900101","displayMode": 0,"thumbnail": "","gender": "",
                            "profileThumbnail": "","nickName": "","learn_idx": 541377,"name": "Fanny","defaultThumbnail": "","id": 50709},{"birthday": "19900101","displayMode": 0,"thumbnail": "",
                            "gender": "M","profileThumbnail": "","nickName": "","learn_idx": 541363,"name": "Farrell","defaultThumbnail": "","id": 50710}], "className": "Starter Class 1","prodIdx": 1119,"resultType": "Success"}"""))
            ),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
    })
    public Map<String, Object> openClass(
            @RequestParam(name = "classid") final String classId,
            @RequestParam(name = "tch_idx") final String tchidx
    ) throws Exception {
        Map<String, Object> result = teacherService
                .getOpenClass(Integer.parseInt(classId), Integer.parseInt(tchidx));
        return result;
    }

    @GetMapping(value = "/_closeclass.json")
    @Operation(summary = "선생님 클래스 종료", description = "선생님 클래스 종료 API", parameters = {
            @Parameter(name = "classlogid", description = "클래스 로그 IDX", required = true, example = "1"),
            @Parameter(name = "classid", description = "클래스 IDX", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "tch_idx", description = "선생님 IDX", required = true, schema = @Schema(type = "string", example = "1"))
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(examples = @ExampleObject("{\"result\": 0}"))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    public Map<String, Object> closeClass(
            @RequestParam(name = "classlogid") final String classLogId,
            @RequestParam(name = "classid") final String classId,
            @RequestParam(name = "tch_idx") final String tchIdx
    ) throws Exception {

        if(StringUtils.isEmpty(classLogId)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 120);
            error.put("returnType", "Error - classlogid required");
            return error;
        }

        if(StringUtils.isEmpty(classId)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 121);
            error.put("returnType", "Error - classid required");
            return error;
        }

        if(StringUtils.isEmpty(tchIdx)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 122);
            error.put("returnType", "Error - tch_idx required");
            return error;
        }

        return teacherService
                .updateCloseClass(Long.parseLong(classLogId), Integer.parseInt(classId), Integer.parseInt(tchIdx));
    }

    @GetMapping(value = "/_getclassstudents.json")
    @Operation(summary = "학생 리스트", description = "학생 리스트 API", parameters = {
            @Parameter(name = "classid", description = "클래스 IDX", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "tch_idx", description = "선생님 IDX", required = true, schema = @Schema(type = "string", example = "1"))
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject("""
                            {"result": 0,"students": [{"birthday": "19900101","displayMode": 0,"thumbnail": "","gender": "","profileThumbnail": "","nickName": "","learn_idx": 541377,"name": "Fanny",
                            "defaultThumbnail": "","id": 50709},{"birthday": "19900101","displayMode": 0,"thumbnail": "","gender": "M","profileThumbnail": "","nickName": "","learn_idx": 541363,"name": "Farrell",
                            "defaultThumbnail": "","id": 50710}],"resultType": "Success"}"""))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
    })
    @ResponseBody
    public Map<String, Object> getClassStudents(
            @RequestParam final String classid,
            @RequestParam final String tch_idx
    ) throws Exception {
        Map<String, Object> result = teacherService
                .getClassStudents(Integer.parseInt(classid), Integer.parseInt(tch_idx));
        return result;
    }

    @GetMapping(value = "/getTeacher/List")
    @Operation(summary = "선생님 목록 조회 API", description = "클래스의 선생님 목록 조회")
    @Parameter(name = "cla_id", description = "반 ID", required = true, schema = @Schema(type = "string", example = "3D100000854_20251_10139101"))
    public ResponseDTO<CustomBody> getClassTeachers(
            @Parameter(hidden = true) @RequestParam Map<String, Object> paramData
    ) throws Exception {
        Map<String, Object> resultData = teacherService.getClassTeacher(paramData);

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "[교사]교사 목록 및 주교사 & 보조교사 상태구분");
    }


}
