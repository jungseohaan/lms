package com.visang.aidt.lms.api.common.excel.service;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateCallback;
import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import com.visang.aidt.lms.api.common.excel.converter.AchievementStandardDataConverter;
import com.visang.aidt.lms.api.common.excel.mapper.ExcelDownloadMapper;
import com.visang.aidt.lms.api.common.excel.resource.ExcelSheet;
import com.visang.aidt.lms.api.common.excel.resource.Lesson;
import com.visang.aidt.lms.api.common.excel.resource.Student;
import com.visang.aidt.lms.api.common.excel.resource.Unit;
import com.visang.aidt.lms.api.common.excel.template.*;
import com.visang.aidt.lms.api.dashboard.service.StntDsbdService;
import com.visang.aidt.lms.api.dashboard.service.TchDsbdService;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.utility.utils.EncodeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelDownloadService {

    private final TchDsbdService tchDsbdService;
    private final StntDsbdService stntDsbdService;
    private final AiLearningMapper aiLearningMapper;
    private final ExcelDownloadMapper excelDownloadMapper;
    private final Map<ExcelTemplateType, AchievementStandardDataConverter> converters;


    public List<LinkedHashMap<Object, Object>> getAchievementData(ExcelTemplateType templateType, Map<String, Object> searchParams) throws Exception {
        LinkedHashMap<String, Object> rawData = fetchRawData(templateType, searchParams);
        AchievementStandardDataConverter converter = converters.get(templateType);
        if (converter == null) {
            throw new IllegalArgumentException("지원하지 않는 템플릿 타입입니다: " + templateType);
        }
        rawData.put("templateType", templateType);

        return converter.convert(rawData);
    }


    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, Object> fetchRawData(ExcelTemplateType templateType, Map<String, Object> searchParams) throws Exception {
        log.info("Excel 템플릿에 넣을 데이터 조회 - templateType: {}  searchParams: {}", templateType, searchParams);

        return switch (templateType) {
            case TCH_MATH_ACHIEVEMENT_STANDARD ->
                    (LinkedHashMap<String, Object>) tchDsbdService.selectTchDsbdStatusStudyMapMathAchievementStandardList(searchParams);
            case STDT_MATH_ACHIEVEMENT_STANDARD ->
                    (LinkedHashMap<String, Object>) stntDsbdService.selectStntDsbdStatusStudyMapMathAchievementStandardList(searchParams);
            case TCH_ENGLISH_ACHIEVEMENT_STANDARD ->
                    (LinkedHashMap<String, Object>) tchDsbdService.selectTchDsbdStatusStudyMapAchievementStandardList(searchParams);
            case STDT_ENGLISH_ACHIEVEMENT_STANDARD ->
                    (LinkedHashMap<String, Object>) stntDsbdService.selectStntDsbdStatusStudyAchievementStandardList(searchParams);
//          data 단원별 총평 조회 코드 작성
            default -> null;
        };
    }

    public List<Map<String, Object>> getUnits(Integer textbookId) {
        return excelDownloadMapper.findUnits(textbookId);
    }

    public AbstractXlsxView getExcelTemplate(ExcelTemplateType templateType) {
        log.info("Excel 템플릿 생성 - templateType: {}", templateType);

        ExcelTemplateCallback template = switch (templateType) {
            case TCH_MATH_ACHIEVEMENT_STANDARD -> new TchMathAchievementStandardExcelTemplate();
            case STDT_MATH_ACHIEVEMENT_STANDARD -> new StdtMathAchievementStandardExcelTemplate();
            case TCH_ENGLISH_ACHIEVEMENT_STANDARD -> new TchEnglishAchievementStandardExcelTemplate();
            case STDT_ENGLISH_ACHIEVEMENT_STANDARD -> new StdtEnglishAchievementStandardExcelTemplate();
            case TCH_META_NAME_AI_GENRVW -> new TchMetaNameAiGenrvwExcelTemplate();
        };

        return new AbstractXlsxView() {
            @Override
            protected void buildExcelDocument(
                    Map<String, Object> model,
                    Workbook workbook,
                    HttpServletRequest request,
                    HttpServletResponse response
            ) throws Exception {
                String filename = (String) model.get("filename");
                Collection<?> data;
                if (templateType == ExcelTemplateType.TCH_META_NAME_AI_GENRVW) {
                    // 새로운 구조의 데이터
                    @SuppressWarnings("unchecked")
                    List<ExcelSheet> sheetData = (List<ExcelSheet>) model.get("data");
                    data = sheetData;
                } else {
                    // 기존 구조의 데이터
                    @SuppressWarnings("unchecked")
                    List<LinkedHashMap<Object, Object>> mapData = (List<LinkedHashMap<Object, Object>>) model.get("data");
                    data = mapData;
                }
                template.buildExcelWorkbook(model, data, workbook);

                // 파일명 설정
                String encodedFilename = EncodeUtils.encodeUtf8(filename);
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
            }
        };
    }

    public List<ExcelSheet> getExcelData(Map<String, Object> searchParams) throws Exception {

        int subjectType = aiLearningMapper.findBrandId(searchParams);

        List<Map<String, Object>> studentAchievementByUnit = new ArrayList<>();
        // 수학
        if (subjectType == 1) {
            studentAchievementByUnit = this.excelDownloadMapper.findMathAchievementByTextbkIdAndClaId(searchParams);
        }
        // 영어
        else if (subjectType == 3) {
            studentAchievementByUnit = this.excelDownloadMapper.findEnglishAchievementByTextbkIdAndClaId(searchParams);
        }


        return this.getReviews(searchParams, subjectType, studentAchievementByUnit);
    }

    public List<ExcelSheet> getReviews(Map<String, Object> searchParams, int subject, List<Map<String, Object>> studentAchievementByUnit) throws Exception {

        List<ExcelSheet> excelSheets = new ArrayList<>();
        List<String> downloadTypes = (List<String>) MapUtils.getObject(searchParams, "downloadType");

        for(String downloadType : downloadTypes) {
            if ("subject".equals(downloadType)) {
                excelSheets.add(this.getSubjectReviews(searchParams, subject, studentAchievementByUnit));
            } else if ("task".equals(downloadType)) {
                excelSheets.add(this.getTaskReviews(searchParams, studentAchievementByUnit));
            } else if ("evl".equals(downloadType)) {
                excelSheets.add(this.getEvlReviews(searchParams, studentAchievementByUnit));
            }
        }

        return excelSheets;
    }

    public ExcelSheet getTaskReviews(Map<String, Object> searchParams, List<Map<String, Object>> studentAchievementByUnit) throws Exception {
        List<Map<String, Object>> taskReviews = this.excelDownloadMapper.findTaskReviewsByClaIdAndMetaIds(searchParams);
        return this.convertToExcelSheet(taskReviews, "task", studentAchievementByUnit);
    }

    public ExcelSheet getEvlReviews(Map<String, Object> searchParams, List<Map<String, Object>> studentAchievementByUnit) throws Exception {
        List<Map<String, Object>> evlReviews = this.excelDownloadMapper.findEvlReviewsByClaIdAndMetaIds(searchParams);
        return this.convertToExcelSheet(evlReviews, "evl", studentAchievementByUnit);
    }

    public ExcelSheet getSubjectReviews(Map<String, Object> searchParams, int subjectType, List<Map<String, Object>> studentAchievementByUnit) throws Exception {

        List<Map<String, Object>> subjectReviews = new ArrayList<>();
        // 수학
        if (subjectType == 1) {
            subjectReviews = this.excelDownloadMapper.findMathReviewsByClaIdAndMetaIds(searchParams);
        }
        // 영어
        else if (subjectType == 3) {
            subjectReviews = this.excelDownloadMapper.findEnglishReviewsByClaIdAndMetaIds(searchParams);
        }
        return this.convertToExcelSheet(subjectReviews, "subject", studentAchievementByUnit);
    }

    public ExcelSheet convertToExcelSheet(List<Map<String, Object>> reviews, String reviewType, List<Map<String, Object>> studentAchievementByUnit) {

        // ExcelSheet 생성
        String sheetName = "";
        String firstHeaderName = "단원명";
        String secondHeaderName = "";

        if ("evl".equals(reviewType)) {
            sheetName = "평가 리포트 총평";
            secondHeaderName = "평가명";
        } else if ("task".equals(reviewType)) {
            sheetName = "과제 리포트 총평";
            secondHeaderName = "과제명";
        } else if ("subject".equals(reviewType)) {
            sheetName = "수업 리포트 총평";
            secondHeaderName = "차시명";
        }

        ExcelSheet excelSheet = new ExcelSheet(sheetName, firstHeaderName, secondHeaderName);

        if (reviews.isEmpty()) {
            return excelSheet;
        }

        // 성취도 데이터를 Map으로 변환
        Map<String, Map<String, Double>> achievementMap = convertAchievementToMap(studentAchievementByUnit);

        // 단원별, 차시별로 그룹핑
        Map<String, Map<String, List<Map<String, Object>>>> groupedData = groupByUnitAndLesson(reviews);

        // Unit과 Lesson 생성
        for (Map.Entry<String, Map<String, List<Map<String, Object>>>> unitEntry : groupedData.entrySet()) {
            String unitName = unitEntry.getKey();
            Unit unit = new Unit(unitName);

            for (Map.Entry<String, List<Map<String, Object>>> lessonEntry : unitEntry.getValue().entrySet()) {
                String lessonName = lessonEntry.getKey();
                Lesson lesson = new Lesson(lessonName);

                // Student 생성
                for (Map<String, Object> studentData : lessonEntry.getValue()) {
                    String studentName = (String) studentData.get("studentName");
                    String review = studentData.get("review") == null ? "-" : (String) studentData.get("review");

                    Student student = new Student(studentName, review);
                    lesson.getStudents().add(student);
                }

                unit.getLessons().add(lesson);
            }

            // Unit에 성취도 데이터 추가
            Map<String, Double> unitAchievements = achievementMap.getOrDefault(unitName, new HashMap<>());

            // 리뷰에 있는 모든 학생에 대해 성취도 설정 (없으면 0)
            Map<String, Double> completeAchievements = new HashMap<>();

            // 첫 번째 레슨의 학생들을 기준으로 전체 학생 목록 확보
            if (!unit.getLessons().isEmpty() && !unit.getLessons().get(0).getStudents().isEmpty()) {
                for (Student student : unit.getLessons().get(0).getStudents()) {
                    String studentName = student.getName();
                    completeAchievements.put(studentName, unitAchievements.getOrDefault(studentName, 0d));
                }
            }

            unit.setStudentAchievement(completeAchievements);
            excelSheet.getUnits().add(unit);
        }

        return excelSheet;
    }

    // 성취도 데이터를 Map으로 변환하는 헬퍼 메서드
    private Map<String, Map<String, Double>> convertAchievementToMap(List<Map<String, Object>> studentAchievementByUnit) {
        Map<String, Map<String, Double>> achievementMap = new HashMap<>();

        for (Map<String, Object> achievement : studentAchievementByUnit) {
            String unitKey = (String) achievement.get("unitName"); // 단원명
            String studentName = (String) achievement.get("studentName"); // 학생명
            Object scoreObj = achievement.get("usdScr"); // 성취도 점수
            Double score = scoreObj != null ? (Double) scoreObj : 0;

            achievementMap
                    .computeIfAbsent(unitKey, k -> new HashMap<>())
                    .put(studentName, score);
        }

        return achievementMap;
    }

    private Map<String, Map<String, List<Map<String, Object>>>> groupByUnitAndLesson(List<Map<String, Object>> reviews) {
        Map<String, Map<String, List<Map<String, Object>>>> groupedData = new LinkedHashMap<>();

        for (Map<String, Object> row : reviews) {
            String unitName = (String) row.get("unitName");
            String lessonName = (String) row.get("idPathNm");

            groupedData
                    .computeIfAbsent(unitName, k -> new LinkedHashMap<>())
                    .computeIfAbsent(lessonName, k -> new ArrayList<>())
                    .add(row);
        }

        return groupedData;
    }
} 