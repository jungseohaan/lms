package com.visang.aidt.lms.api.common.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "(공통) 서버정보 API", description = "(공통) 서버정보 API")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DateController {

    @Loggable
    @GetMapping(value = "/common/dateTime")
    @Operation(summary = "(공통) 서버시간 호출", description = "(공통) 서버시간 호출")
    public ResponseDTO<CustomBody> getDateTime(@Parameter(hidden = true) @RequestParam Map<String, Object> paramData) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String resultData = sdf.format(new Date());

        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "서버시간 호출");
    }
}
