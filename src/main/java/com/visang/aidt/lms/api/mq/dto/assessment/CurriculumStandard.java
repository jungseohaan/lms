package com.visang.aidt.lms.api.mq.dto.assessment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CurriculumStandard {
    private String id;
    @JsonProperty("achievement-level")
    private String achievementLevel;
}
