package com.visang.aidt.lms.api.mq.dto.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MediaInfo {
    private String id;
    private String mediaType;
    private int length;
    private int difficulty;
    @JsonProperty("difficulty-min")
    private int difficultyMin;
    @JsonProperty("difficulty-max")
    private int difficultyMax;
    @JsonProperty("curriculum-standard-id")
    private List<String> curriculumStandardId;
    private boolean common;
    private MediaInfoDetail mediaDetail;

    @JsonIgnore
    private String standardId;
    @JsonIgnore
    private String commonType;
    @JsonIgnore
    private String userId;
}
