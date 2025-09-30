package com.visang.aidt.lms.api.mq.dto.assessment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**메세지큐 전송용 클래스*/
@Getter
@Setter
@Builder
public class AssessmentInfo {
    @JsonProperty("id")
    private String setsId;
    private String type; // 평가 유형
    @JsonProperty("aitutor-recommended")
    private Boolean aitutorRecommended; // AI 튜터의 평가 추천 여부
    private Integer score; // 평가 채점 점수
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String timestamp; // 평가 제출 시간
    private List<AssessmentDetail> assessmentDetailList; // 평가 상세 정보 목록

    @JsonIgnore
    private String userId;
    @JsonIgnore
    private Integer evlId;
    @JsonIgnore
    private Integer evlResultId;
}


