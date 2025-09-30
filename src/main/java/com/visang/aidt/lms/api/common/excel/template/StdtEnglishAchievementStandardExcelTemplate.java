package com.visang.aidt.lms.api.common.excel.template;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateCallback;
import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class StdtEnglishAchievementStandardExcelTemplate implements ExcelTemplateCallback {

    @Override
    public ExcelTemplateType getTemplateType() {
        return ExcelTemplateType.STDT_ENGLISH_ACHIEVEMENT_STANDARD;
    }

    @Override
    public void buildExcelWorkbook(Map<String, Object> model, Collection<?> data, Workbook workbook) {
        log.info("(학생) 중학교 영어 성취기준 Excel 템플릿 생성 시작");

        Sheet sheet = workbook.createSheet(ExcelTemplateType.STDT_ENGLISH_ACHIEVEMENT_STANDARD.getDescription());

        // 헤더 스타일 생성
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle headerStyleForAchievement = createHeaderStyleForAchievement(workbook);

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"내용 체계 영역", "성취기준 코드", "1단계 내용 요소", "2단계 내용 요소", "나의 성취도"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            if (i == 4) {
                cell.setCellStyle(headerStyleForAchievement);
            } else {
                cell.setCellStyle(headerStyle);
            }
        }

        // 데이터 스타일 생성
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        // 데이터 입력 (2행부터)
        int rowNum = 1;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> typedData = (List<Map<String, Object>>) data;

        for (Map<String, Object> rowData : typedData) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(String.valueOf(rowData.get("내용 체계 영역")));
            cell0.setCellStyle(dataStyle);

            Cell cell4 = row.createCell(1);
            cell4.setCellValue(String.valueOf(rowData.get("성취기준 코드")));
            cell4.setCellStyle(dataStyle);

            // 1단계 내용 요소 (depth 3)
            Cell cell1 = row.createCell(2);
            cell1.setCellValue(String.valueOf(rowData.get("1단계 내용 요소")));
            cell1.setCellStyle(dataStyle);

            // 2단계 내용 요소 (depth 4)
            Cell cell2 = row.createCell(3);
            cell2.setCellValue(String.valueOf(rowData.get("2단계 내용 요소")));
            cell2.setCellStyle(dataStyle);

            // 나의 성취도
            Cell cell3 = row.createCell(4);
            Object avgValue = rowData.get("성취도");
            if (avgValue != null) {
                if (avgValue.equals("-")) {
                    cell3.setCellValue("-");
                    cell3.setCellStyle(dataStyle);
                    cell3.setCellStyle(createDataStyleForAchievement(workbook));
                } else {
                    cell3.setCellValue(Double.parseDouble(String.valueOf(avgValue)) / 100.0);
                    cell3.setCellStyle(numberStyle);
                }
            }
        }

        // 컬럼 너비 설정
        sheet.setColumnWidth(0, 15 * 256); // 내용 체계 영역
        sheet.setColumnWidth(1, 20 * 256); // 성취기준 코드
        sheet.setColumnWidth(2, 80 * 256); // 1단계 내용 요소
        sheet.setColumnWidth(3, 80 * 256); // 2단계 내용 요소
        sheet.setColumnWidth(4, 20 * 256); // 나의 성취도

        log.info("중학교 영어 성취기준 Excel 템플릿 생성 완료");
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        
        // #DAE3F3 색상 설정
        XSSFColor color = new XSSFColor(new java.awt.Color(0xDA, 0xE3, 0xF3), null);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createHeaderStyleForAchievement(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        
        // #104861 색상 설정
        XSSFColor color = new XSSFColor(new java.awt.Color(0x10, 0x48, 0x61), null);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0%"));
        return style;
    }

    private CellStyle createDataStyleForAchievement(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }
}
