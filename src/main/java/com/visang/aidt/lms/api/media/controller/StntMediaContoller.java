package com.visang.aidt.lms.api.media.controller;

import com.visang.aidt.lms.api.common.annotation.Loggable;
import com.visang.aidt.lms.api.media.dto.MediaLogRequestDTO;
import com.visang.aidt.lms.api.media.service.StntMediaService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@Tag(name = "(학생) 미디어 API", description = "학생 미디어 API")
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StntMediaContoller {

    private final StntMediaService stntMediaService;

    @Loggable
    @PostMapping(value = "/stnt/media-std/log/save")
    @Operation(summary = "학생의 미디어 학습로그 결과 저장", description = "")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {
                    @ExampleObject(name = "파라미터", value = "{" +
                            "\"userId\":\"vsstu1\"," +
                            "\"claId\":\"1111\"," +
                            "\"textbkId\":1," +
                            "\"menuSeCd\":\"4\"," +
                            "\"trgtId\":12345," +
                            "\"stdCd\":1," +
                            "\"articleId\":\"art123\"," +
                            "\"medTy\":1," +
                            "\"medId\":\"http://media.url\"," +
                            "\"medLt\":360," +
                            "\"aiTutRecmdAt\":\"Y\"," +
                            "\"medPlyTime\":300," +
                            "\"medStdCpAt\":\"Y\"," +
                            "\"medPlyCnt\":5," +
                            "\"medPlyMuteCnt\":1," +
                            "\"medPlyJumpCnt\":2," +
                            "\"medPlyStopCnt\":3" +
                            "\"crculId\":2" +
                            "}"
                    )
            })
    )
    public ResponseDTO<CustomBody> saveStudentMediaLearningLogResults(
            @Valid @RequestBody MediaLogRequestDTO paramData
    )throws Exception {
        Object resultData = stntMediaService.saveStudentMediaLearningLogResults(paramData);
        return AidtCommonUtil.makeResultSuccess(paramData, resultData, "학생의 미디어 학습로그 결과 저장");
    }
}
