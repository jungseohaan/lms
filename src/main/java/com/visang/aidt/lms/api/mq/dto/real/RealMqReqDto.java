package com.visang.aidt.lms.api.mq.dto.real;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
* 학습종료 시 받는 RequestDto
*/
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealMqReqDto {
    private String partnerId;
    private MqAccessToken accessToken;
    private String userId; /*학생id*/
    private Integer claIdx;
    private Integer textbkId;
    private String claId;
    private String startTime;
    private String endTime;
    @JsonIgnore
    private String stntId;
    @JsonIgnore
    private String tchId;
    @JsonIgnore
    private String curriculum;
    private String wrterId;

}
