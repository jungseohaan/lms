package com.visang.aidt.lms.api.mq.dto.teaching;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TeachingReorganizedMqDto {
    private String partnerId;
    private String userId;
    private String type;
    private String verb;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String reqTime;
    private List<ReorganizedInfo> reorganizedInfoList;
    private String useTermsAgreeYn;
}
