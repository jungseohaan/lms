package com.visang.aidt.lms.api.mq.dto.real;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressDto {
    private String stntId;
    private String curriculum;
    private Integer moduleCnt;
    private Integer moduleAnwCnt;
    private Integer percent;
    private Integer score;
}
