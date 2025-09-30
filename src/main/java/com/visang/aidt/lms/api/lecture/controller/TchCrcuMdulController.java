package com.visang.aidt.lms.api.lecture.controller;

import com.visang.aidt.lms.api.lecture.service.TchCrcuMdulService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 커리큘럼 모듈 API Controller
 */
@RestController
//@Api(tags = "(교사) 커리큘럼 모듈 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchCrcuMdulController {
    private final TchCrcuMdulService tchCrcuMdulService;

    //@ApiOperation(value = "모듈 정보 조회", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/info", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuMdulInfo() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.findMdulInfo(paramData);
        String resultMessage = "모듈 정보 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "모듈 정답보기(조회)", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/answer", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuMdulAnswer() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.findMdulAnswer(paramData);
        String resultMessage = "모듈 정답보기(조회)";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "제출현황 및 정답(률) 리셋", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/answers/reset", method = {RequestMethod.DELETE})
    public ResponseDTO<CustomBody> tchCrcuMdulAnswerReset() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.createSubmitStatusAndReset(paramData);
        String resultMessage = "제출현황 및 정답(률) 리셋";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "제출현황 조회", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/submit/status", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuMdulSubmitStatus() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.findSubmitStatus(paramData);
        String resultMessage = "제출현황 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "모듈 상시툴 답안 제출 방식 내려주기", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/tools", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuMdulTools() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.modifyMdulSubmitTools(paramData);
        String resultMessage = "모듈 상시툴 답안 제출 방식 내려주기";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "상시툴 활동 종료", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/tools/end", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchCrcuMdulToolsEnd() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.createAlwaysOnToolsActEnd(paramData);
        String resultMessage = "상시툴 활동 종료";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "상시툴 제출 현황 조회", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/tools/status", method = {RequestMethod.GET})
    public ResponseDTO<CustomBody> tchCrcuMdulToolsStatus() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.findAlwaysOnToolsStatus(paramData);
        String resultMessage = "상시툴 제출 현황 조회";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "우수학생 답안 공유하기", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/share/answer", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuMdulShareAnswer() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.createShareOutstandAnswer(paramData);
        String resultMessage = "우수학생 답안 공유하기";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "모듈(콘텐츠) 정렬순서를 변경", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/sort", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchCrcuMdulSort() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.modifyTabMdulSort(paramData);
        String resultMessage = "모듈(콘텐츠) 정렬순서를 변경";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "모듈(콘텐츠) 노출여부 설정", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/show", method = {RequestMethod.PUT})
    public ResponseDTO<CustomBody> tchCrcuMdulShow() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.modifyMdulShow(paramData);
        String resultMessage = "모듈(콘텐츠) 노출여부 설정";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "모듈(콘텐츠) 복사", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul/copy", method = {RequestMethod.POST})
    public ResponseDTO<CustomBody> tchCrcuMdulCopy() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.createMdulCopy(paramData);
        String resultMessage = "모듈(콘텐츠) 복사";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }

    //@ApiOperation(value = "모듈(콘텐츠) 삭제_일괄삭제", notes = "")
    @RequestMapping(value = "/tch/crcu/mdul", method = {RequestMethod.DELETE})
    public ResponseDTO<CustomBody> tchCrcuMdul() throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        Map<String, Object> resultData = tchCrcuMdulService.removeMdul(paramData);
        String resultMessage = "모듈(콘텐츠) 삭제_일괄삭제";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
}
