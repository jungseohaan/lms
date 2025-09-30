package com.visang.aidt.lms.api.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomewkReportListReqDto {

    @Schema(description = "학생 ID", example = "engbook229-s1")
    private String stntId;

    @NotNull(message = "학급 ID는 필수값입니다.")
    @Schema(description = "학급 ID", example = "49f37b12fe7f463785e38da824f212db")
    private String claId;

    @NotNull(message = "교과서 ID는 필수값입니다.")
    @Schema(description = "교과서 ID", example = "1150")
    private Integer textbookId;

    @NotNull(message = "과제 구분은 필수값입니다.")
    @Schema(description = "과제 구분",
            example = "general",
            allowableValues = {"all", "aiCustom", "general", "aiPrescription", "groupTask"},
            defaultValue = "general")
    private String taskDivision;

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
