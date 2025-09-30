//package com.visang.aidt.lms.api.common.excel.converter;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class MathAchievementDataConverterTest {
//
//    private MathAchievementDataConverter converter;
//    private LinkedHashMap<String, Object> rowData;
//
//    @BeforeEach
//    void setUp() {
//        converter = new MathAchievementDataConverter();
//        rowData = new LinkedHashMap<>();
//    }
//
//    @Test
//    @DisplayName("achStdList가 없는 경우 빈 리스트를 반환해야 함")
//    void whenAchStdListIsNull_shouldReturnEmptyList() {
//        // given
//        rowData.put("achStdList", null);
//
//        // when
//        List<LinkedHashMap<Object, Object>> result = converter.convert(rowData);
//
//        // then
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("[교사] studentScores 가 없는 경우 achStdList 데이터만 반환해야 함")
//    void whenStudentScoresIsNull_shouldReturnBasicData() {
//        // given
//        List<Map<String, Object>> achStdList = new ArrayList<>();
//        Map<String, Object> achStd = new HashMap<>();
//        achStd.put("achStdCd", "TEST001");
//        achStd.put("achStdNm", "테스트 성취기준");
//        achStd.put("avgUsdScr", 85.5);
//        achStdList.add(achStd);
//        rowData.put("achStdList", achStdList);
//        rowData.put("templateType", "TCH_MATH");
//
//        // when
//        List<LinkedHashMap<Object, Object>> result = converter.convert(rowData);
//
//        // then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        LinkedHashMap<Object, Object> row = result.get(0);
//        assertEquals("TEST001", row.get("성취기준코드"));
//        assertEquals("테스트 성취기준", row.get("성취기준명"));
//        assertEquals(85.5, row.get("평균"));
//    }
//
//    @Test
//    @DisplayName("[교사] achStdList와 studentScores가 모두 있는 경우 모든 데이터가 포함되어야 함")
//    void whenBothDataExist_shouldReturnCompleteData() {
//        // given
//        List<Map<String, Object>> achStdList = new ArrayList<>();
//        Map<String, Object> achStd = new HashMap<>();
//        achStd.put("achStdCd", "TEST001");
//        achStd.put("achStdNm", "test 성취기준명");
//        achStd.put("avgUsdScr", 95);
//        achStdList.add(achStd);
//        rowData.put("achStdList", achStdList);
//        rowData.put("templateType", "TCH_MATH");
//
//        List<Map<String, Object>> studentScores = new ArrayList<>();
//        Map<String, Object> student1Score = new HashMap<>();
//        student1Score.put("achStdCd", "TEST001");
//        student1Score.put("flnm", "홍길동");
//        student1Score.put("avgUsdScr", 90.0);
//
//        Map<String, Object> student2Score = new HashMap<>();
//        student2Score.put("achStdCd", "TEST001");
//        student2Score.put("flnm", "가나다");
//        student2Score.put("avgUsdScr", 100.0);
//
//        studentScores.add(student1Score);
//        studentScores.add(student2Score);
//        rowData.put("studentScores", studentScores);
//
//        // when
//        List<LinkedHashMap<Object, Object>> result = converter.convert(rowData);
//
//        // then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        LinkedHashMap<Object, Object> row = result.get(0);
//        assertEquals("TEST001", row.get("성취기준코드"));
//        assertEquals("test 성취기준명", row.get("성취기준명"));
//        assertEquals(95, row.get("평균"));
//        assertEquals("90", row.get("홍길동"));
//        assertEquals("100", row.get("가나다"));
//    }
//
//    //
//
//
//
//
//}