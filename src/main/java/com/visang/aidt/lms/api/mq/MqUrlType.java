package com.visang.aidt.lms.api.mq;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MqUrlType {
    ASSESSMENT("https://govcon.aidtclass.com/assessment_"), // 평가
    ASSIGNMENT("https://govcon.aidtclass.com/assignment_"), // 과제
    MEDIA("https://govcon.aidtclass.com/media_"); //미디아
    private final String url;
}
