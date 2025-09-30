package com.visang.aidt.lms.api.mq.dto.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MediaInfoDetail {
    @JsonProperty("aitutor-recommended")
    private boolean aitutorRecommended; // AI튜터 추천여부
    private int duration;
    private boolean completion; // 학습완료여부
    private int attempt;
    @JsonProperty("mute-cnt")
    private int muteCnt;
    @JsonProperty("skip-cnt")
    private int skipCnt;
    @JsonProperty("pause-cnt")
    private int pauseCnt;
    @JsonIgnore
    private String userId;

}
