package com.visang.aidt.lms.api.selflrn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AitutorQuestionVO {

    private Integer aitutorLrngDetailId;
    private Integer aitutorLrngInfoId;
    private String articleId = "0";
    private Integer libtextId = 0;
    private String libraryUrl;
    private String libraryName;
    private Double libraryStartTime;
    private Double libraryEndTime;
    private String contentsAudioAnalysis;
    private String userId;
    private Boolean isEnd = false;

}
