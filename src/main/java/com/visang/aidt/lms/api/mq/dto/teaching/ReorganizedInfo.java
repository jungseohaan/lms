package com.visang.aidt.lms.api.mq.dto.teaching;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReorganizedInfo {
    @JsonIgnore
    private String userId;
    @JsonProperty("curriculum-standard-id")
    private List<String> curriculumStandardId;
    @JsonIgnore
    private String standardId;
    @JsonIgnore
    private Timestamp timestamp;
    @JsonIgnore
    private String endTime;
    @JsonIgnore
    private String startTime;
}
