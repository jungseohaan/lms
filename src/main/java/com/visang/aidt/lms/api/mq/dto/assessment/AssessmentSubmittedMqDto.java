package com.visang.aidt.lms.api.mq.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentSubmittedMqDto {
    private String partnerId;
    private String userId;
    private String type;
    private String verb;
    private String reqTime;
    private List<AssessmentInfo> assessmentInfoList;
    private List<CurriculumStandard> curriculumStandardList;


}
