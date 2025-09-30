package com.visang.aidt.lms.global.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Global Response Body")
public record CustomBody(@Schema(description = "성공 여부", example = "true") boolean success,
                         @Schema(description = "응답 메세지", example = "Ok") String resultMessage,
                         @Schema(description = "응답 코드(HttpStatus 를 통해 판단)", example = "200") int resultCode,
                         @Schema(description = "요청 데이터") Object paramData,
                         @Schema(description = "결과 데이터") Object resultData,
                         @Schema(description = "요청 시간", example = "2024-07-29 11:08:10") String sTime,
                         @Schema(description = "응답 시간", example = "2024-07-29 11:08:11") String eTime,
                         @Schema(description = "응답 해시", example = "abcdef123456...") String hash,
                         @Schema(description = "요청 시간", example = "abcdef123456...") String currentTime
) { }
