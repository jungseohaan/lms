package com.visang.aidt.lms.api.dashboard.model;

import com.visang.aidt.lms.api.common.excel.ExcelTemplateType;
import com.visang.aidt.lms.api.common.excel.ExcelView;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined.GREY_25_PERCENT;

@Slf4j
public class eventExcel implements ExcelView.Callback {

    @Override
    public ExcelTemplateType getTemplateType() {
        return null;
    }

    @Override
    public void buildExcelWorkbook(Map<String, Object> model, Collection<?> data, Workbook workbook) {
        // 시트를 작성한다
        Sheet sheet = workbook.createSheet("test");

        // 스타일
        Font thfont = workbook.createFont();
        thfont.setBold(true);

        Font hdFont = workbook.createFont();
        hdFont.setBold(true);
        hdFont.setFontHeightInPoints((short) 14);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFont(hdFont);

        CellStyle tableHeaderStyle = workbook.createCellStyle();
        tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderStyle.setBorderRight(BorderStyle.THIN);
        tableHeaderStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderStyle.setFillForegroundColor(GREY_25_PERCENT.getIndex());
        tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        tableHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderStyle.setFont(thfont);

        CellStyle rowStyle = workbook.createCellStyle();
        rowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rowStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle tableStyle = workbook.createCellStyle();
        tableStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableStyle.setAlignment(HorizontalAlignment.CENTER);

        //표 그리기
        List<Map<String,Object>> testScheList = (List<Map<String,Object>>) data;

        if( testScheList.isEmpty() ) {
            sheet.setDefaultColumnWidth(30);

            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("기말고사 시험시간표");
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 4));
            titleRow.getCell(0).setCellStyle(titleStyle);

            Row tableHaederRow = sheet.createRow(3);
            tableHaederRow.createCell(0).setCellValue("지정시간대");
            tableHaederRow.createCell(1).setCellValue("과목코드");
            tableHaederRow.createCell(2).setCellValue("과목명");
            tableHaederRow.createCell(3).setCellValue("교수명");
            tableHaederRow.createCell(4).setCellValue("제한시간");

            sheet.setColumnWidth(0, 5500);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 5500);
            sheet.setColumnWidth(3, 2900);
            sheet.setColumnWidth(4, 2250);

            for( int i = 0; i <= 4; i++ ) {
                sheet.getRow(3).getCell(i).setCellStyle(tableHeaderStyle);
            }

            Row userRow = sheet.createRow(4);
            userRow.createCell(0).setCellValue("등록된 시간표가 없습니다.");
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 4));

            sheet.getRow(3).setHeight((short) 600);
            titleRow.getCell(0).setCellStyle(titleStyle);
            userRow.getCell(0).setCellStyle(tableStyle);

            return;
        }

        int rowIndex = 4;
        int columnIndex = 1;
        int compareColumnIndex = 1;
        int listSize = testScheList.size();

        Row tableHaederRow = sheet.createRow(3);     //테이블 헤더 생성
        Row userRow = sheet.createRow(rowIndex);        //시간표가 출력될 행

        List<Integer> dateRowList = new ArrayList<>();

        for( int i = 0; i < listSize; i++ ) {
            if( i == 0 ) {
                sheet.setDefaultColumnWidth(30);
                sheet.setColumnWidth(0, 5500);

                tableHaederRow.createCell(0).setCellValue("지정시간대");

                dateRowList.add(rowIndex);

                rowIndex++;
                userRow = sheet.createRow(rowIndex);
            }

            if( columnIndex <= compareColumnIndex ) {
                tableHaederRow.createCell(compareColumnIndex).setCellValue("과목코드");
                tableHaederRow.createCell(compareColumnIndex+1).setCellValue("과목명");
                tableHaederRow.createCell(compareColumnIndex+2).setCellValue("교수명");
                tableHaederRow.createCell(compareColumnIndex+3).setCellValue("제한시간");

                sheet.setColumnWidth(compareColumnIndex, 4000);
                sheet.setColumnWidth(compareColumnIndex+1, 5500);
                sheet.setColumnWidth(compareColumnIndex+2, 2900);
                sheet.setColumnWidth(compareColumnIndex+3, 2250);
            }

            userRow.createCell(0).setCellValue(1);
            userRow.createCell(compareColumnIndex).setCellValue(2);
            userRow.createCell(compareColumnIndex+1).setCellValue(3);
            userRow.createCell(compareColumnIndex+2).setCellValue(4);
            userRow.createCell(compareColumnIndex+3).setCellValue(5);

            userRow.getCell(0).setCellStyle(tableStyle);
            userRow.getCell(compareColumnIndex).setCellStyle(tableStyle);
            userRow.getCell(compareColumnIndex+1).setCellStyle(tableStyle);
            userRow.getCell(compareColumnIndex+2).setCellStyle(tableStyle);
            userRow.getCell(compareColumnIndex+3).setCellStyle(tableStyle);

            if( i == (listSize-1) ) {
                columnIndex += 4;
                break;
            }
        }

        // 타이틀
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("기말고사 시험시간표");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, columnIndex-1));

        // 스타일 적용
        sheet.getRow(3).setHeight((short) 600);
        titleRow.getCell(0).setCellStyle(titleStyle);

        for( int i = 0; i < columnIndex; i++ ) {
            sheet.getRow(3).getCell(i).setCellStyle(tableHeaderStyle);
        }

        for( int i = 0; i < dateRowList.size(); i++ ) {
            sheet.addMergedRegion(new CellRangeAddress(dateRowList.get(i), dateRowList.get(i), 0, columnIndex-1));
            sheet.getRow(dateRowList.get(i)).getCell(0).setCellStyle(rowStyle);
        }
    }
}
