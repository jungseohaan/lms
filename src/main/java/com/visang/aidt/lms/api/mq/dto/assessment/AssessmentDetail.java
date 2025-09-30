package com.visang.aidt.lms.api.mq.dto.assessment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AssessmentDetail {
    private String id;
    @JsonIgnore
    private String setsId;
    private String type;
    private String difficulty;
    @JsonProperty("difficulty-min")
    private Integer difficultyMin;
    @JsonProperty("difficulty-max")
    private Integer difficultyMax;
    @JsonProperty("curriculum-standard-id")
    private List<String> curriculumStandardId;
    @JsonIgnore
    private String curriculumStandardIdString;
    private Boolean common;
    @JsonProperty("aitutor-recommended")
    private Boolean aitutorRecommended;
    private Boolean completion;
    private Double success;
    private Integer duration;
    private Integer attempt;
}

