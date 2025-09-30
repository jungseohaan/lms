package com.visang.aidt.lms.api.common.excel.config;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import com.visang.aidt.lms.api.common.excel.converter.AchievementStandardDataConverter;
import com.visang.aidt.lms.api.common.excel.converter.EnglishAchievementStandardDataConverter;
import com.visang.aidt.lms.api.common.excel.converter.MathAchievementStandardDataConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Excel 템플릿 타입별 데이터 변환기(Converter) 설정
 */
@Configuration
public class ConverterConfig {
    
    /**
     * Excel 템플릿 타입별 데이터 변환기를 생성하고 매핑합니다.(성취 기준 용)
     * @param mathConverter 수학 과목 데이터 변환기
     * @param englishConverter 영어 과목 데이터 변환기
     */
    @Bean
    public Map<ExcelTemplateType, AchievementStandardDataConverter> converters(
        MathAchievementStandardDataConverter mathConverter,
        EnglishAchievementStandardDataConverter englishConverter
    ) {
        Map<ExcelTemplateType, AchievementStandardDataConverter> converters = new EnumMap<>(ExcelTemplateType.class);
        // 수학
        converters.put(ExcelTemplateType.TCH_MATH_ACHIEVEMENT_STANDARD, mathConverter);
        converters.put(ExcelTemplateType.STDT_MATH_ACHIEVEMENT_STANDARD, mathConverter);

        // 영어
        converters.put(ExcelTemplateType.TCH_ENGLISH_ACHIEVEMENT_STANDARD, englishConverter);
        converters.put(ExcelTemplateType.STDT_ENGLISH_ACHIEVEMENT_STANDARD, englishConverter);
        return converters;
    }
}