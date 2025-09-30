package com.visang.aidt.lms.api.keris.utils;

import org.apache.commons.lang3.StringUtils;

public enum SchoolType {
    ELEMENTARY("초등학교", 2),
    MIDDLE("중학교", 3),
    HIGH("고등학교", 4);

    private final String suffix;
    private final int division;

    SchoolType(String suffix, int division) {
        this.suffix = suffix;
        this.division = division;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getDivision() {
        return division;
    }

    public static int getDivisionBySuffix(String schoolName) {
        for (SchoolType type : values()) {
            if (StringUtils.endsWith(schoolName, type.getSuffix())) {
                return type.getDivision();
            }
        }
        return 0;
    }
}