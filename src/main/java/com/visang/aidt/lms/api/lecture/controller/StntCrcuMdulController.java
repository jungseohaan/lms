package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.lecture.service.StntCrcuMdulService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
//@Api(tags = "(학생) 모듈")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntCrcuMdulController {

    private StntCrcuMdulService stntCrcuMdulService;

    //    @ApiOperation(value = "모듈 정보 조회", notes = "")
    @GetMapping(value = "/stnt/crcu/mdul/info")
    public ResponseDTO<CustomBody> stntCrcuMdulInfo() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntCrcuMdulService.findMdulInfo(paramData);
        String resultMessage = "모듈 정보 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "모듈 정보 수정 or 저장(PUT)", notes = "")
    @PutMapping(value = "/stnt/crcu/mdul")
    public ResponseDTO<CustomBody> stntCrcuMdul() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = stntCrcuMdulService.createMdulInfo(paramData);
        resultMessage = "모듈 정보 수정 or 저장(PUT)";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "상시툴 진행 결과 제출하기(POST)", notes = "")
    @PostMapping(value = "/stnt/crcu/mdul/tools")
    public ResponseDTO<CustomBody> stntCrcuMdulTools() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = stntCrcuMdulService.createMdulTools(paramData);
        resultMessage = "상시툴 진행 결과 제출하기";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "상시툴 진행 결과 조회하기(GET)", notes = "")
    @GetMapping(value = "/stnt/crcu/mdul/tools/info")
    public ResponseDTO<CustomBody> stntCrcuMdulToolsInfo() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntCrcuMdulService.findMdulToolsInfo(paramData);
        String resultMessage = "상시툴 진행 결과 조회하기";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "모듈 선택번호, 정/오답 표시 저장(POST)", notes = "")
    @PostMapping(value = "/stnt/crcu/mdul/answer")
    public ResponseDTO<CustomBody> stntCrcuMdulAnswer() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = stntCrcuMdulService.createMdulAnswer(paramData);
        resultMessage = "모듈 선택번호, 정/오답 표시 저장";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "모듈 정보 조회", notes = "")
//    @GetMapping(value = "/stnt/crcu/mdul/info")
//    public ResponseDTO stntCrcuMdul() {
//        Map<String, Object> paramData = new HashMap<>();
//        Map<String, Object> resultData = stntCrcuMdulService.createMdulAnswer(paramData);
//        String resultMessage = "모듈 정보 조회";
//
//        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
//    }


    //    @ApiOperation(value = "모듈 풀이과정 저장(POST), 조회(GET)", notes = "")
    @RequestMapping(value = "/stnt/crcu/mdul/soln-proc", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> stntCrcuMdulSolnProc(HttpServletRequest request) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = stntCrcuMdulService.findMdulSolnProc(paramData);
        resultMessage = "모듈 풀이과정 조회";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    @RequestMapping(value = "/stnt/crcu/mdul/soln-proc/create", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> stntCrcuMdulSolnProcCreate(HttpServletRequest request) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = null;
        String resultMessage = "";

        resultData = stntCrcuMdulService.createMdulSolnProc(paramData);
        resultMessage = "모듈 풀이과정 저장";

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //    @ApiOperation(value = "모듈 피드백 조회", notes = "")
    @GetMapping(value = "/stnt/crcu/mdul/feedback")
    public ResponseDTO<CustomBody> stntCrcuMdulFeedback() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntCrcuMdulService.findMdulFeedback(paramData);
        String resultMessage = "모듈 피드백 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


    //    @ApiOperation(value = "우수(학생) 답안 목록 조회", notes = "")
    @GetMapping(value = "/stnt/crcu/mdul/sharing/answer/list")
    public ResponseDTO<CustomBody> stntCrcuMdulAnswerList() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = stntCrcuMdulService.findOutstandAnswerList(paramData);
        String resultMessage = "우수(학생) 답안 목록 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

}
