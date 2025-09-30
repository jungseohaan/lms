package com.visang.aidt.lms.api.mq.dto.media;

import lombok.Builder;

/** mediaLog 테이블 조회결과 DTO*/

@Builder
public record MediaLogResultDto(
        String stdtId,
        String articleId,
        String medId,
        String medTy,
        Integer length,
        Integer difficulty,
        Integer difficultyMin,
        Integer difficultyMax,
        String curriculumStandardId,
        Boolean common,
        Boolean aitutorRecommended,
        Integer duration,
        Boolean completion,
        Integer attempt,
        Integer muteCnt,
        Integer skipCnt,
        Integer pauseCnt
) {}
