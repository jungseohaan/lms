package com.visang.aidt.lms.api.mq.dto.real;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MqAccessToken {
    private String accessId;
    private String token;
}

