package com.visang.aidt.lms.api.socket.controller;

import com.visang.aidt.lms.api.socket.service.StudentService;
import com.visang.aidt.lms.global.vo.socket.SocketExceptionBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/student", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "(Socket) Student API", description = "(Socket) 학생 API")
public class StudentController {

    private final StudentService studentService;

    @GetMapping(value = "/_joinclass.json")
    @Operation(summary = "학생 클래스 입장", description = "학생 클래스 입장 API", parameters = {
            @Parameter(name = "classid", description = "클래스 IDX", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "user_idx", description = "학생 IDX", required = true, schema = @Schema(type = "string", example = "1"))
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(examples = @ExampleObject("{\"classlogid\": 128265,\"result\": 0,\"classid\": 364,\"prodIdx\": 1119,\"resultType\": \"Success\",\"tch_idx\": 2111}"))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            )
    })
    public Map<String, Object> joinClass(
            @RequestParam(name = "classid" ) final String classId,
            @RequestParam(name = "user_idx") final String userIdx
    ) throws Exception {
        if(StringUtils.isEmpty(classId)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 130);
            error.put("returnType", "Error - classid required");
            return error;
        }

        if(StringUtils.isEmpty(userIdx)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 131);
            error.put("returnType", "Error - user_idx required");
            return error;
        }

        return studentService.getJoinClass(Integer.parseInt(userIdx), Integer.parseInt(classId));
    }

    @GetMapping(value = "/_exitclass.json")
    @Operation(summary = "학생 클래스 퇴장", description = "학생 클래스 퇴장 API", parameters = {
            @Parameter(name = "classlogid", description = "클래스 로그 IDX", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "classid", description = "클래스 IDX", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "user_idx", description = "학생 IDX", required = true, schema = @Schema(type = "string", example = "1"))
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(examples = @ExampleObject("{\"result\": 0}"))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
    })
    public Map<String, Object> exitClass(
            @RequestParam(name = "classlogid") final String classLogId,
            @RequestParam(name = "classid") final String classId,
            @RequestParam(name = "user_idx") final String userIdx
    ) throws Exception {
        if(StringUtils.isEmpty(classLogId)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 140);
            error.put("returnType", "Error - classlogid required");
            return error;
        }

        if(StringUtils.isEmpty(classId)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 141);
            error.put("returnType", "Error - classid required");
            return error;
        }

        if(StringUtils.isEmpty(userIdx)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 142);
            error.put("returnType", "Error - user_idx required");
            return error;
        }

        return studentService.getExitClass(Integer.parseInt(classLogId), Integer.parseInt(classId), Integer.parseInt(userIdx));
    }

    @Operation(summary = "학생 클래스 리스트", description = "학생 클래스 리스트 API", parameters = {
            @Parameter(name = "user_idx", description = "학생 IDX", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "service", description = "서비스 구분자", required = true, schema = @Schema(type = "string", example = "mathalive")),
    })
    @GetMapping(value = "/_studentclasslist.json")
    @ApiResponse(responseCode="200", description = "성공")
    public Map<String, Object> studentClassList(
            @RequestParam(value = "user_idx")String userIdx,
            @RequestParam(value="service", required = false, defaultValue = "" ) String service
    ) throws Exception {

        if(StringUtils.isEmpty(userIdx)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 190);
            error.put("returnType", "Error - userIdx required");
            return error;
        }


        return studentService.getStudentClassList(Integer.parseInt(userIdx), service);
    }

    @GetMapping(value = "/_addStudent.json")
    @Operation(summary = "학생 신규 추가(고도화 소켓 모니터링)", description = "학생이 신규 가입으로 들어온 경우", parameters = {
            @Parameter(name = "userId", description = "학생 ID", required = true, schema = @Schema(type = "string", example = "1")),
            @Parameter(name = "claId", description = "클래스 IDX", required = true, schema = @Schema(type = "string", example = "1"))
    }, responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(examples = @ExampleObject("{\"result\": 0}"))),
            @ApiResponse(responseCode = "400", description = "실패",
                    content = @Content(
                            examples = @ExampleObject("{\"returnType\": \"fail message\"}"),
                            schema = @Schema(implementation = SocketExceptionBody.class))
            ),
    })
    public Map<String, Object> addStudentMonitoring(
            @RequestParam(name = "userId") final String userId,
            @RequestParam(name = "claId") final String claId
    ) throws Exception {
        if(StringUtils.isEmpty(userId)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 140);
            error.put("returnType", "Error - userId required");
            return error;
        }

        if(StringUtils.isEmpty(claId)) {
            Map<String, Object> error =  new HashMap<>();
            error.put("result", 141);
            error.put("returnType", "Error - claId required");
            return error;
        }

        return studentService.getSocketMonitoringUrl(userId, claId);
    }


}
