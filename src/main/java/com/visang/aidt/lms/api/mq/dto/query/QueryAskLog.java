package com.visang.aidt.lms.api.mq.dto.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryAskLog {
    private String userId;
    private Integer id; // 프론트에 반환 할 자동 생성 키
}
