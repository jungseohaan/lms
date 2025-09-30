package com.visang.aidt.lms.api.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvalReportListReqDto {

    @NotNull(message = "교사 ID는 필수값입니다.")
    @Schema(description = "교사 ID", example = "engbook229-t")
    private String userId;

    @Schema(description = "학생 ID", example = "engbook229-s1")
    private String stntId;

    @NotNull(message = "학급 ID는 필수값입니다.")
    @Schema(description = "학급 ID", example = "49f37b12fe7f463785e38da824f212db")
    private String claId;

    @NotNull(message = "교과서 ID는 필수값입니다.")
    @Schema(description = "교과서 ID", example = "1150")
    private Integer textbookId;

    @Schema(description = "평가 구분",
            example = "2",
            allowableValues = {"1", "2", "3", "4"},
            defaultValue = "2")
    private String evlSeCd;

    @NotNull(message = "리포트 상태는 필수값입니다.")
    @Schema(description = "리포트 상태",
            example = "end",
            allowableValues = {"ing", "end"},
            defaultValue = "end")
    private String reportStatusType;

    @NotNull(message = "로그인 유저 ID는 필수값입니다.")
    @Schema(description = "로그인 유저 ID", example = "engbook229-t")
    private String loginUserId;

    private String loginUserSeCd;
}