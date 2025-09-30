package com.visang.aidt.lms.api.selflrn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AitutorLrngInfoVO {
    private Integer aitutorLrngInfoId;
    private Integer lrngSttsCd;
    private String lowRankUdstdRateAt;
    private String curPrgrsLrngAt;
    private Integer enLrngDivId;
    private String enLrngDivCode;
    private Integer sort;
}
