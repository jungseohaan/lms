package com.visang.aidt.lms.api.mq.dto.real;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 진도율 조회 파라미터 VO */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressVO {
    private String partnerId;
    private MqAccessToken accessToken;
    private String stntId;
    private String tchId;
    private Integer claIdx;
    private Integer textbkId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String tchOpenDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String tchCloseDate;
    private String claId;
}