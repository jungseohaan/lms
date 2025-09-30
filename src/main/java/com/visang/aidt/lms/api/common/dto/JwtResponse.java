package com.visang.aidt.lms.api.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtResponse {

    @JsonProperty("accessToken")
    private final String accessToken;

    @JsonProperty("refreshToken")
    private final String refreshToken;

    @JsonProperty("hmac")
    private final String hmac;

}
