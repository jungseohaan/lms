package com.visang.aidt.lms.api.user.controller;

import com.visang.aidt.lms.api.user.service.TchUserService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * (교사) 유저 API Controller
 */
@RestController
//@Tag(name = "(교사) 유저 API", description = "(교사) 유저 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TchUserController {
    private final TchUserService tchUserService;

    @PostMapping(value = "/tch/login")
    //@Operation(summary = "AIDT 로그인", description = "")
    //@Parameter(name = "tchId", description = "교사 ID",  required = true, schema = @Schema(type = "string", example = "tch0001"))
    //@Parameter(name = "subjId", description = "과목 ID", required = true, schema = @Schema(type = "string", example = "M1MAT"))
    //@Parameter(name = "clsNum", description = "반 번호", required = true, schema = @Schema(type = "string", example = "1"))
    public ResponseDTO<CustomBody> tchLogin(
            @RequestParam(name = "tchId") String tchId,
            @RequestParam(name = "subjId") String subjId,
            @RequestParam(name = "clsNum") int clsNum
    ) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("tchId", tchId);
        paramData.put("subjId", subjId);
        paramData.put("clsNum", clsNum);

        Map<String, Object> resultData = tchUserService.findTchInfo(paramData);
        String resultMessage = "";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }


}
