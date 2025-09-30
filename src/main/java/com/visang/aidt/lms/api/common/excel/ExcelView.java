package com.visang.aidt.lms.api.common.excel;


import com.visang.aidt.lms.api.utility.utils.EncodeUtils;
import lombok.Builder;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

public class ExcelView extends AbstractXlsxView {
    protected String fileName;
    protected Collection<?> data;
    protected Callback callback;

    public ExcelView() {
        setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8;");
    }

    @Builder
    public ExcelView(Callback callback, Collection<?> data, String fileName) {
        this();
        this.callback = callback;
        this.data = data;
        this.fileName = fileName;
    }

    @Override
    protected void buildExcelDocument(
            Map<String, Object> model
            , Workbook workbook
            , HttpServletRequest request
            , HttpServletResponse response
    ) throws Exception {
        // 현재 설정 저장
        String originalHeadless = System.getProperty("java.awt.headless");
        
        try {
            // 헤드리스 모드 설정
            System.setProperty("java.awt.headless", "true");
            
            // 파일명에 한국어를 포함해도 문자 깨짐이 발생하지 않도록 UTF-8로 인코딩한다
            String encodedFilename = EncodeUtils.encodeUtf8(fileName);
            String contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFilename);
            response.setHeader(CONTENT_DISPOSITION, contentDisposition);
            
            // Excel 통합 문서를 구축한다
            callback.buildExcelWorkbook(model, this.data, workbook);
        } finally {
            // 원래 설정으로 복구
            if (originalHeadless != null) {
                System.setProperty("java.awt.headless", originalHeadless);
            } else {
                System.clearProperty("java.awt.headless");
            }
        }
    }

    public interface Callback {
        ExcelTemplateType getTemplateType();

        void buildExcelWorkbook(Map<String, Object> model, Collection<?> data, Workbook workbook);
    }
}