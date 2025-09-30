package com.visang.aidt.lms.api.mq.dto.assessment;

public record CompletedAssessments(
        Integer evlResultId,
        Integer evlId,
        String mamoymId
) {}

