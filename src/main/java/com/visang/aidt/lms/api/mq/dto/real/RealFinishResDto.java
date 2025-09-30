package com.visang.aidt.lms.api.mq.dto.real;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RealFinishResDto {
    private String partnerId;
    private MqAccessToken accessToken;
    private String userId;
    private String type;
    private String curriculum;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String reqTime;
    private String percent; // 학습 경과도(%)
    private String score;
    private String useTermsAgreeYn;
}
