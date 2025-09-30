package com.visang.aidt.lms.api.selflrn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AitutorResultInfoVO {

    private Integer stdResultId;
    private Integer stdId;
    private String moduleNum;
    private String moduleId;
    private Integer libtextId;
    private String aiTutChtCn;
    private String smExmAt;
    private Integer errata;
    private Integer unitNum;
    private String userId;
    private String unitPrefixKey = "Lesson ";
    private String unitPrefixValue;
}
