package com.visang.aidt.lms.api.mq.dto.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**질의(학생이 질문)*/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class QueryAskMqDto {
    private String partnerId;
    private String userId;
    private String type;
    private String verb;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private String reqTime;
    private List<QueryAskInfo> askInfoList;
    private String useTermsAgreeYn;

}
