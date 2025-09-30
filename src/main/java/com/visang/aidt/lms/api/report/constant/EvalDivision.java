package com.visang.aidt.lms.api.report.constant;

import java.util.Set;

/** 평가구분
 * Description: 리포트 평가 목록 조회 시 평가 구분값(evlSeCd) 검증을 위한 유틸리티 클래스 입니다.
 * */
public class EvalDivision {

    private EvalDivision() {}

    public static final String DIAGNOSIS = "1"; // 진단평가
    public static final String FORMATION = "2"; // 형성평가
    public static final String GENERAL = "3"; // 총괄평가
    public static final String PERFORMANCE = "4"; // 수행평가

    public static final Set<String> VALID_VALUES = Set.of(DIAGNOSIS, FORMATION, GENERAL, PERFORMANCE);

    public static boolean isValidValue(String value) {
        return VALID_VALUES.contains(value);
    }

    public static String getAllowedValues() {
        return String.join(", ", VALID_VALUES);
    }
}