package com.visang.aidt.lms.api.mq.dto.real;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
public class RealClassFinishDto {
    private Integer classIdx;
    private Integer userIdx;
    private String userId;
    private Timestamp openDate;
    private Timestamp closeDate;
    private Integer connStatus;
}
