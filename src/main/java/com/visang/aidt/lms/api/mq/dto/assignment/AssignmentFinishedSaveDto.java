package com.visang.aidt.lms.api.mq.dto.assignment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignmentFinishedSaveDto {
    private Integer taskId; // 과제ID
    private Integer taskGbCd; // 과제구분코드 1: 등록, 2: 제출
    private String  stntId; // 학생ID
}
