package com.visang.aidt.lms.api.user.controller;

import com.visang.aidt.lms.api.user.service.StntUserService;
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

@RestController
//@Tag(name = "(학생) 유저 API", description = "(학생) 유저 API")
@AllArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntUserController {

    private StntUserService stntUserService;

    @PostMapping(value = "/stnt/login")
    //@Operation(summary = "로그인", description = "")
    //@Parameter(name = "stntId", description = "학생 ID",  schema = @Schema(type = "string", example = "user1"))
    public ResponseDTO<CustomBody> stntLogin(
            @RequestParam(name = "stntId") String stntId
    ) throws Exception {
        Map<String, Object> paramData = new HashMap<>();
        paramData.put("stntId", stntId);

        Map<String, Object> resultData = stntUserService.findStntInfo(paramData);
        String resultMessage = "로그인 처리";
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, resultMessage);
    }
}
