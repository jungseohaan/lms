package com.visang.aidt.lms.api.mq.dto.assignment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskGbCd {
    REGISTRATION(1),        // 등록
    SUBMISSION(2);          // 제출

    private final int code;
}