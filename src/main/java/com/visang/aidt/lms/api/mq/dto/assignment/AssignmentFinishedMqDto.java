package com.visang.aidt.lms.api.mq.dto.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
public class AssignmentFinishedMqDto {
    private String partnerId;
    private String userId;
    private String type;
    private String verb;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String reqTime;
    private List<AssignmentInfo> assignmentInfoList;
    private String useTermsAgreeYn;
}
