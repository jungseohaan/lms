package com.visang.aidt.lms.api.mq.dto.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class AssignmentFinishedInfoDto {
    private String stntId; // 학생ID
    private Integer taskId; // 과제ID
    private String standardIds; // 표준체계ID
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String taskRegDt; // 교사가 과제를 등록한 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String submDt; // 학생이 과제를 제출한 시간
}
