package com.visang.aidt.lms.api.mq.dto.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public class AssignmentRegistInfoDto {
    private Integer textbkId;
    private String claId;
    private Integer taskId;
    private String stntId;
    private String standardId;
    private String standardIds;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String taskRegDt;// 교사) 과제 등록 시간
    private String setsId;
    private String wrterId;
}
