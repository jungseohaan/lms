package com.visang.aidt.lms.api.common.excel.converter;

import java.util.List;
import java.util.LinkedHashMap;

/** 성취도 데이터 변환을 위한 인터페이스 */
public interface AchievementStandardDataConverter {
    List<LinkedHashMap<Object, Object>> convert(LinkedHashMap<String, Object> rowData);
}