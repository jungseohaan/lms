package com.visang.aidt.lms.api.mq.dto.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Timestamp;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QueryAskInfo {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Timestamp timestamp;
    private boolean answer;
    @JsonIgnore
    private String trnAt;
    @JsonIgnore
    private String userId;
    @JsonIgnore
    private String answAt;

    private int duration;
    private float satisfaction;

    @JsonIgnore
    private String endTime;
    @JsonIgnore
    private String startTime;
    @JsonIgnore
    private Long queryAskedId;
}
