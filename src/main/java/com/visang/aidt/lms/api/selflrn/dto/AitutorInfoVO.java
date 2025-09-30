package com.visang.aidt.lms.api.selflrn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AitutorInfoVO {

    private AitutorLrngInfoVO curLrngInfo;
    private AitutorLrngInfoVO nextLrngInfo;
    private AitutorQuestionVO questionInfo;
    private Boolean isFirst;
    private Boolean isAllEnd;

}
