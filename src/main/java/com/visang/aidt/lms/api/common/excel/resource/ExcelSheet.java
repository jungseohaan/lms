package com.visang.aidt.lms.api.common.excel.resource;


import java.util.ArrayList;
import java.util.List;

/**
 * @author 홍보람 (qhfka2854@codebplat.co.kr)
 */
public class ExcelSheet {
    private String sheetName;
    private String firstHeaderName;
    private String SecondHeaderName;
    private List<Unit> units;

    public ExcelSheet(String sheetName, String firstHeaderName, String SecondHeaderName) {
        this.sheetName = sheetName;
        this.firstHeaderName = firstHeaderName;
        this.SecondHeaderName = SecondHeaderName;
        this.units = new ArrayList<>();
    }

    // getter, setter
    public String getSheetName() { return sheetName; }
    public String getFirstHeaderName() { return firstHeaderName; }
    public String getSecondHeaderName() { return SecondHeaderName; }
    public List<Unit> getUnits() { return units; }
}
