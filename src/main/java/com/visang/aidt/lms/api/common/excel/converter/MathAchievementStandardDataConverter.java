package com.visang.aidt.lms.api.common.excel.converter;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;

/** 수학 성취기준별 데이터 컨버터 */
@Component
@SuppressWarnings("unchecked")
public class MathAchievementStandardDataConverter implements AchievementStandardDataConverter {
    @Override
    public List<LinkedHashMap<Object, Object>> convert(LinkedHashMap<String, Object> rowData) {
        String templateType = rowData.get("templateType").toString();
        List<Map<String, Object>> studentScores = (List<Map<String, Object>>) rowData.get("studentScores");

        // 교사 템플릿의 경우 학생 별 동적 컬럼 추가를 위해 분기 처리
        if (ExcelTemplateType.TCH_MATH_ACHIEVEMENT_STANDARD.getCode().equals(templateType)) {
            if (studentScores == null || studentScores.isEmpty()) {
                return studentDataConverter(rowData);
            }
            return teacherDataConverter(rowData);
        } else if (ExcelTemplateType.STDT_MATH_ACHIEVEMENT_STANDARD.getCode().equals(templateType)) {
            return studentDataConverter(rowData);
        }
        
        return new ArrayList<>();
    }

    private List<LinkedHashMap<Object, Object>> teacherDataConverter(LinkedHashMap<String,Object> rowData) {
        List<LinkedHashMap<Object, Object>> teacherExcelData = new ArrayList<>();
        List<Map<String, Object>> achStdList = (List<Map<String, Object>>) rowData.get("achStdList");
        List<Map<String, Object>> studentScores = (List<Map<String, Object>>) rowData.get("studentScores");

        // 학생 목록 추출 (중복 제거 및 정렬)
        List<String> studentNames = studentScores.stream()
                .map(score -> (String) score.get("flnm"))
                .distinct()
                .sorted()
                .toList();

        // 성취기준별 학생 점수를 맵으로 구성 (복합 키 사용)
        Map<String, Map<String, Object>> scoresByAchStd = new HashMap<>();
        for (Map<String, Object> score : studentScores) {
            String achStdCd = (String) score.get("achStdCd");
            BigInteger metaId = (BigInteger) score.get("metaId");
            String studentName = (String) score.get("flnm");
            Object avgScore = roundScore(score.get("avgUsdScr")); // 반올림 처리

            // 복합 키 생성 (achStdCd + metaId)
            String compositeKey = achStdCd + "_" + metaId;

            scoresByAchStd.computeIfAbsent(compositeKey, k -> new HashMap<>())
                    .put(studentName, avgScore);
        }

        // 엑셀 데이터 생성
        for (Map<String, Object> achStd : achStdList) {
            LinkedHashMap<Object, Object> row = new LinkedHashMap<>();
            String achStdCd = (String) achStd.get("achStdCd");
            BigInteger metaId = (BigInteger) achStd.get("metaId"); // achStdList에도 metaId가 있다고 가정

            // 복합 키 생성
            String compositeKey = achStdCd + "_" + metaId;

            // 성취기준 정보
            row.put("성취기준코드", achStdCd);
            row.put("성취기준명", achStd.get("achStdNm"));

            // 각 학생별 점수
            Map<String, Object> studentScoreMap = scoresByAchStd.getOrDefault(compositeKey, new HashMap<>());
            for (String studentName : studentNames) {
                row.put(studentName, studentScoreMap.getOrDefault(studentName, "-"));
            }

            // 평균
            row.put("평균", achStd.get("avgUsdScr"));

            teacherExcelData.add(row);
        }

        return teacherExcelData;
    }

    private List<LinkedHashMap<Object, Object>> studentDataConverter(LinkedHashMap<String,Object> rowData) {
        List<LinkedHashMap<Object, Object>> stdtExcelData = new ArrayList<>();
        List<Map<String, Object>> achStdList = (List<Map<String, Object>>) rowData.get("achStdList");
        if (achStdList.isEmpty()){return stdtExcelData;}

        // 엑셀 데이터 생성
        for (Map<String, Object> achStd : achStdList) {
            LinkedHashMap<Object, Object> row = new LinkedHashMap<>();
            String achStdCd = (String) achStd.get("achStdCd");

            row.put("성취기준코드", achStdCd);
            row.put("성취기준명", achStd.get("achStdNm"));
            row.put("평균", achStd.get("avgUsdScr"));

            stdtExcelData.add(row);
        }

        return stdtExcelData;
    }

    private Object roundScore(Object score) {
        if (score.equals("-")) return score;
        if (score instanceof String) {
            double value = Double.parseDouble((String) score);
            return String.valueOf(Math.round(value));
        } else if (score instanceof Number) {
            return String.valueOf(Math.round(((Number) score).doubleValue()));
        }
        return score;
    }
}
