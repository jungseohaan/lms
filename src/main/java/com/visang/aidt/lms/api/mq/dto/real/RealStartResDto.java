package com.visang.aidt.lms.api.mq.dto.real;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RealStartResDto {
    private String partnerId;
    private MqAccessToken accessToken;
    private String userId;
    private String type;
    private String curriculum;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String reqTime;
    private List<String> curriculumStandardList;

    private String useTermsAgreeYn;          // 이용약관동의여부
}
