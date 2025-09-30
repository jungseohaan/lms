package com.visang.aidt.lms.api.common.excel.template;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateCallback;
import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class TchMathAchievementStandardExcelTemplate implements ExcelTemplateCallback {

    @Override
    public ExcelTemplateType getTemplateType() {
        return ExcelTemplateType.TCH_MATH_ACHIEVEMENT_STANDARD;
    }

    @Override
    public void buildExcelWorkbook(Map<String, Object> model, Collection<?> data, Workbook workbook) {
        log.info("수학 성취기준별 학습 현황 Excel 생성 시작");
        
        Sheet sheet = workbook.createSheet(ExcelTemplateType.TCH_MATH_ACHIEVEMENT_STANDARD.getDescription());
        
        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        
        // 제목 생성
        createTitle(sheet, titleStyle);
        
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
        if (!dataList.isEmpty()) {
            // 헤더 생성
            createHeaders(sheet, headerStyle, dataList.get(0));
            // 데이터 입력
            fillData(sheet, dataList, dataStyle, numberStyle);
        }

        // 컬럼 너비 설정
        sheet.setColumnWidth(0, 20 * 256);  // 성취기준코드
        sheet.setColumnWidth(1, 100 * 256); // 성취기준명
        
        // 학생 이름 컬럼들과 평균 컬럼의 너비 설정
        if (!dataList.isEmpty()) {
            Map<String, Object> firstRow = dataList.get(0);
            int studentCount = firstRow.size() - 3; // 성취기준코드, 성취기준명, 평균 제외
            for (int i = 2; i < studentCount + 2; i++) {
                sheet.setColumnWidth(i, 15 * 256);
            }
            sheet.setColumnWidth(studentCount + 2, 15 * 256); // 평균
        }

        log.info("수학 성취기준별 학습 현황 Excel 생성 완료");
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setDataFormat(workbook.createDataFormat().getFormat("0"));
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private void createTitle(Sheet sheet, CellStyle titleStyle) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("성취기준별 성취율(단위: %)");
        titleCell.setCellStyle(titleStyle);
    }
    
    private void createHeaders(Sheet sheet, CellStyle headerStyle, Map<String, Object> firstRow) {
        Row headerRow = sheet.createRow(2);  // 3번째 행에 헤더 생성
        int columnIndex = 0;
        
        // 고정 컬럼
        createCell(headerRow, columnIndex++, "성취기준코드", headerStyle);
        createCell(headerRow, columnIndex++, "성취기준명", headerStyle);
        
        // 학생 이름 컬럼들
        for (String key : firstRow.keySet()) {
            if (!key.equals("성취기준코드") && !key.equals("성취기준명") && !key.equals("평균")) {
                createCell(headerRow, columnIndex++, key, headerStyle);
            }
        }
        
        // 평균 컬럼
        createCell(headerRow, columnIndex, "평균", headerStyle);
    }
    
    private void fillData(Sheet sheet, List<Map<String, Object>> dataList, CellStyle dataStyle, CellStyle numberStyle) {
        int rowNum = 3;  // 4번째 행부터 데이터 시작
        
        for (Map<String, Object> rowData : dataList) {
            Row row = sheet.createRow(rowNum++);
            int columnIndex = 0;
            
            // 성취기준코드
            createCell(row, columnIndex++, rowData.get("성취기준코드"), dataStyle);
            // 성취기준명
            createCell(row, columnIndex++, rowData.get("성취기준명"), dataStyle);
            
            // 학생별 점수
            for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                if (!entry.getKey().equals("성취기준코드") && !entry.getKey().equals("성취기준명") && !entry.getKey().equals("평균")) {
                    Object value = entry.getValue();
                    CellStyle style = value.equals("-") ? dataStyle : numberStyle;
                    createCell(row, columnIndex++, value, style);
                }
            }
            
            // 평균
            Object avgValue = rowData.get("평균");
            CellStyle style = avgValue.equals("-") ? dataStyle : numberStyle;
            createCell(row, columnIndex, avgValue, style);
        }
    }
    
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }
} 
