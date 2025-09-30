package com.visang.aidt.lms.api.common.excel.template;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateCallback;
import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import com.visang.aidt.lms.api.common.excel.resource.ExcelSheet;
import com.visang.aidt.lms.api.common.excel.resource.Unit;
import com.visang.aidt.lms.api.common.excel.resource.Lesson;
import com.visang.aidt.lms.api.common.excel.resource.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 홍보람 (qhfka2854@codebplat.co.kr)
 */
@Slf4j
public class TchMetaNameAiGenrvwExcelTemplate implements ExcelTemplateCallback {

    @Override
    public ExcelTemplateType getTemplateType() {
        return ExcelTemplateType.TCH_META_NAME_AI_GENRVW;
    }

    @Override
    public void buildExcelWorkbook(Map<String, Object> model, Collection<?> data, Workbook workbook) {
        // 데이터 캐스팅
        @SuppressWarnings("unchecked")
        List<ExcelSheet> sheetDataList = (List<ExcelSheet>) data;

        // 스타일 생성
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle unitCellStyle = createUnitCellStyle(workbook);
        CellStyle lessonCellStyle = createLessonCellStyle(workbook);
        CellStyle achievementStyle = createAchievementStyle(workbook);

        // 각 시트별로 처리
        for (ExcelSheet sheetData : sheetDataList) {
            Sheet sheet = workbook.createSheet(sheetData.getSheetName());

            // 첫 번째 행에 안내 문구 추가
            Row noticeRow = sheet.createRow(0);
            Cell noticeCell = noticeRow.createCell(0);
            noticeCell.setCellValue("※ 총평을 미작성한 단원 및 학습도 노출됩니다.");
            noticeCell.setCellStyle(dataStyle);

            // Unit 데이터 검증
            if (sheetData.getUnits() == null || sheetData.getUnits().isEmpty()) {
                // Units가 null이거나 비어있을 때 기본 헤더만 생성
                createSheetWithHeaderOnly(sheet, sheetData, headerStyle);
                continue;
            }

            List<String> allStudentNames = getAllStudentNames(sheetData);

            int maxStudentCount = allStudentNames.size();

            // 열 너비 설정
            sheet.setColumnWidth(0, 10000); // 단원별
            sheet.setColumnWidth(1, 15000); // 차시명
            for (int i = 0; i < maxStudentCount; i++) {
                sheet.setColumnWidth(i + 2, 10000); // 학생 컬럼
            }

            // 헤더 생성
            Row headerRow = sheet.createRow(1);
            createHeaderCell(headerRow, 0, sheetData.getFirstHeaderName(), headerStyle);
            createHeaderCell(headerRow, 1, sheetData.getSecondHeaderName(), headerStyle);

            // 학생 헤더 생성
            for (int i = 0; i < maxStudentCount; i++) {
                createHeaderCell(headerRow, i + 2, allStudentNames.get(i), headerStyle);
            }

            // 데이터 행 생성
            int currentRow = 2;

            for (Unit unit : sheetData.getUnits()) {
                int unitStartRow = currentRow;

                // 레슨 행들 생성
                for (Lesson lesson : unit.getLessons()) {
                    Row row = sheet.createRow(currentRow);

                    // 단원별 셀 (단원의 첫 행에만 생성)
                    if (currentRow == unitStartRow) {
                        Cell unitCell = row.createCell(0);
                        unitCell.setCellValue(unit.getUnitName());
                        unitCell.setCellStyle(unitCellStyle);
                    }

                    // 차시명 셀
                    Cell lessonCell = row.createCell(1);
                    lessonCell.setCellValue(lesson.getLessonName());
                    lessonCell.setCellStyle(lessonCellStyle);

                    // 학생별 리뷰 데이터 (이름으로 매칭)
                    Map<String, String> studentReviewMap = lesson.getStudents().stream()
                            .collect(Collectors.toMap(Student::getName, Student::getReview, (a, b) -> a));

                    for (int i = 0; i < maxStudentCount; i++) {
                        Cell dataCell = row.createCell(i + 2);
                        dataCell.setCellStyle(dataStyle);

                        String studentName = allStudentNames.get(i);
                        String review = studentReviewMap.getOrDefault(studentName, "-");
                        dataCell.setCellValue(review != null ? review : "-");
                    }

                    currentRow++;
                }

                // 성취도 행 추가
                Row achievementRow = sheet.createRow(currentRow);

                // 성취도 라벨 셀 (차시명 컬럼에 위치)
                Cell achievementLabelCell = achievementRow.createCell(1);
                achievementLabelCell.setCellValue("성취도");
                achievementLabelCell.setCellStyle(achievementStyle);

                // 각 학생별 성취도 데이터
                for (int i = 0; i < maxStudentCount; i++) {
                    Cell achievementCell = achievementRow.createCell(i + 2);
                    achievementCell.setCellStyle(achievementStyle);

                    String studentName = allStudentNames.get(i);
                    Map<String, Double> achievements = unit.getStudentAchievement();
                    double achievement = (achievements != null) ? achievements.getOrDefault(studentName, 0d) : 0;
                    achievementCell.setCellValue(achievement);
                }

                currentRow++;

                // 단원별 병합 (레슨 + 성취도 행 포함)
                int totalRows = unit.getLessons().size() + 1; // +1은 성취도 행
                if (totalRows > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(
                            unitStartRow, unitStartRow + totalRows - 1, 0, 0
                    ));
                    applyBorderToMergedRegion(sheet, unitStartRow, unitStartRow + totalRows - 1, 0, 0);
                }
            }
        }
    }

    // 헤더만 있는 시트 생성 메서드 (데이터가 없는 경우)
    private void createSheetWithHeaderOnly(Sheet sheet, ExcelSheet sheetData, CellStyle headerStyle) {
        // 기본 열 너비 설정
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 15000);

        // 헤더 행만 생성
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0,
                sheetData.getFirstHeaderName() != null ? sheetData.getFirstHeaderName() : "단원별",
                headerStyle);
        createHeaderCell(headerRow, 1,
                sheetData.getSecondHeaderName() != null ? sheetData.getSecondHeaderName() : "차시명",
                headerStyle);
    }

    // 전체 학생 이름 목록 추출
    private List<String> getAllStudentNames(ExcelSheet sheetData) {
        Set<String> allStudentNames = new HashSet<>();

        // 모든 단원의 모든 레슨을 순회하며 학생 이름 수집
        for (Unit unit : sheetData.getUnits()) {
            for (Lesson lesson : unit.getLessons()) {
                for (Student student : lesson.getStudents()) {
                    allStudentNames.add(student.getName());
                }
            }
        }

        // Set을 List로 변환하여 반환 (정렬된 순서로)
        return allStudentNames.stream()
                .sorted()  // 이름순으로 정렬
                .collect(Collectors.toList());
    }

    // 성취도 셀 스타일 생성
    private CellStyle createAchievementStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void applyBorderToMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
    }

    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createUnitCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createLessonCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}