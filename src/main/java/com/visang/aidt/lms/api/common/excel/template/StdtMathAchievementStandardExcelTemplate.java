package com.visang.aidt.lms.api.common.excel.template;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateCallback;
import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@SuppressWarnings("unchecked")
public class StdtMathAchievementStandardExcelTemplate implements ExcelTemplateCallback {

    @Override
    public ExcelTemplateType getTemplateType() {
        return ExcelTemplateType.STDT_MATH_ACHIEVEMENT_STANDARD;
    }

    @Override
    public void buildExcelWorkbook(Map<String, Object> model, Collection<?> data, Workbook workbook) {
        Sheet sheet = workbook.createSheet(ExcelTemplateType.STDT_MATH_ACHIEVEMENT_STANDARD.getDescription());

        // 스타일 설정
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);

        // 제목 생성
        createTitle(sheet, titleStyle);

        // 헤더 생성
        createHeaders(sheet, headerStyle);

        // 데이터 입력
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) data;
        if (!dataList.isEmpty()) {
            fillData(sheet, dataList, dataStyle, numberStyle);
        }

        // 컬럼 너비 설정
        sheet.setColumnWidth(0, 20 * 256);  // 성취기준코드
        sheet.setColumnWidth(1, 100 * 256); // 성취기준명
        sheet.setColumnWidth(2, 15 * 256);  // 평균
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


    private void createHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(2);  // 3행으로 변경
        int columnIndex = 0;
        
        // 헤더 (학생용은 3개 컬럼)
        createCell(headerRow, columnIndex++, "성취기준코드", headerStyle);
        createCell(headerRow, columnIndex++, "성취기준명", headerStyle);
        createCell(headerRow, columnIndex++, "평균", headerStyle);
    }


    private void fillData(Sheet sheet, List<Map<String, Object>> dataList, CellStyle dataStyle, CellStyle numberStyle) {
        int rowNum = 3;  // 4행부터 시작

        for (Map<String, Object> rowData : dataList) {
            Row row = sheet.createRow(rowNum++);
            int columnIndex = 0;
            
            // 성취기준코드
            Object code = rowData.get("성취기준코드");
            createCell(row, columnIndex++, code, dataStyle);
            
            // 성취기준명
            Object name = rowData.get("성취기준명");
            createCell(row, columnIndex++, name, dataStyle);
            
            // 평균
            Object avgValue = rowData.get("평균");
            CellStyle style = avgValue != null && avgValue.equals("-") ? dataStyle : numberStyle;
            createCell(row, columnIndex, avgValue, style);
        }
    }


    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            try {
                cell.setCellValue(value.toString());
            } catch (IllegalArgumentException e) {
                log.error("셀 값 설정 실패 - 잘못된 값 형식: {}", value, e);
                cell.setCellValue("");
            } catch (NullPointerException e) {
                log.error("셀 값 설정 실패 - null 참조 오류", e);
                cell.setCellValue("");
            } catch (UnsupportedOperationException e) {
                log.error("셀 값 설정 실패 - 지원되지 않는 작업: {}", value, e);
                cell.setCellValue("");
            } catch (OutOfMemoryError e) {
                log.error("셀 값 설정 실패 - 메모리 부족: {}", value, e);
                cell.setCellValue("");
            } catch (Exception e) {
                log.error("셀 값 설정 실패 - 예상치 못한 오류: {}", value, e);
                cell.setCellValue("");
            }
        }
        cell.setCellStyle(style);
    }
} 