package com.visang.aidt.lms.api.common.excel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.*;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum ExcelTemplateType {
    TCH_MATH_ACHIEVEMENT_STANDARD("TCH_MATH_ACHIEVEMENT_STANDARD", "(교사)수학 성취기준별 학습 현황"),
    STDT_MATH_ACHIEVEMENT_STANDARD("STDT_MATH_ACHIEVEMENT_STANDARD", "(학생)수학 성취기준별 학습 현황"),
    TCH_ENGLISH_ACHIEVEMENT_STANDARD("TCH_ENGLISH_ACHIEVEMENT_STANDARD", "(교사)영어 성취기준별 학습 현황"),
    STDT_ENGLISH_ACHIEVEMENT_STANDARD("STDT_ENGLISH_ACHIEVEMENT_STANDARD", "(학생)영어 성취기준별 학습 현황"),
    TCH_META_NAME_AI_GENRVW("TCH_META_NAME_AI_GENRVW", "(교사)단원별 AI 총평");


    private final String code;
    private final String description;

    private static final Map<String, ExcelTemplateType> TEMPLATE_TYPE_BY_CODE = new HashMap<>();

    static {
        for (ExcelTemplateType type : values()) {
            TEMPLATE_TYPE_BY_CODE.put(type.getCode(), type);
        }
    }

    public static ExcelTemplateType fromCode(String code) {
        return Optional.ofNullable(TEMPLATE_TYPE_BY_CODE.get(code))
            .orElseThrow(() -> new IllegalArgumentException("템플릿 타입을 확인해주세요: " + code));
    }
}