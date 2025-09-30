package com.visang.aidt.lms.api.report.constant;


import java.util.Set;

/** 리포트 상태
 * Description: 리포트 상태를 검증하기 위한 유틸리티 클래스입니다.
 * */
public class ReportStatusType {
    private ReportStatusType() {}

    public static final String ING = "ing"; // 진행중
    public static final String END = "end"; // 종료
    public static final String ALL = "all"; // 진행 + 종료

    public static final Set<String> VALID_VALUES = Set.of(ING,END,ALL);

    public static boolean isValid(final String value) {
        return VALID_VALUES.contains(value);
    }

    public static String getAllowedValues() {
        return String.join(", ", VALID_VALUES);
    }
}
