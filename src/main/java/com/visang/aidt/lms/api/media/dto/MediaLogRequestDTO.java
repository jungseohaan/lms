package com.visang.aidt.lms.api.media.dto;


import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class MediaLogRequestDTO {

    @NotBlank(message = "User ID 필수값입니다.")
    private String userId;

    @NotBlank(message = "Class ID 필수값입니다.")
    private String claId;

    @NotNull(message = "Textbook ID 필수값입니다.")
    private Integer textbkId;

    @NotNull(message = "Menu Code 필수값입니다.")
    private String menuSeCd;

    @NotNull(message = "Target ID 필수값입니다.")
    private Integer trgtId;

    @NotNull(message = "Study Code 필수값입니다.")
    private Integer stdCd;

    @NotBlank(message = "Article ID 필수값입니다.")
    private String articleId;

    @NotBlank(message = "Media ID 필수값입니다.")
    private String medId;

    @NotNull(message = "Media Type 필수값입니다.")
    private Integer medTy;

    private Integer medLt;
    private String aiTutRecmdAt;
    private Integer medPlyTime;
    private String medStdCpAt;
    private Integer medPlyCnt;
    private Integer medPlyMuteCnt;
    private Integer medPlyJumpCnt;
    private Integer medPlyStopCnt;
    private String crculId;
}

