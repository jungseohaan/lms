package com.visang.aidt.lms.api.report.constant;

import java.util.Set;

/** 과제 구분
 * Description: 리포트 과제 목록 조회 시 과제 구분값 검증을 위한 유틸리티 클래스 입니다.
 * */
public class TaskDivision {
    private TaskDivision() {}

    public static final String ALL = "all"; // 전체
    public static final String AI_CUSTOM = "aiCustom"; // AI 맞춤학습
    public static final String GENERAL = "general"; // 일반
    public static final String AI_PRESCRIPTION = "aiPrescription"; // AI 처방 과제
    public static final String GROUPTASK = "groupTask"; // 모둠 출제

    private static final Set<String> VALID_VALUES = Set.of(ALL, AI_CUSTOM, GENERAL, AI_PRESCRIPTION, GROUPTASK);

    public static boolean isValidValue(String value) {
        return VALID_VALUES.contains(value);
    }

    public static String getAllowedValues() {
        return String.join(", ", VALID_VALUES);
    }
}
