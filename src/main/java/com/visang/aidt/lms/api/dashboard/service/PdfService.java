package com.visang.aidt.lms.api.dashboard.service;

import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import com.visang.aidt.lms.api.dashboard.model.PioPdf;
import com.visang.aidt.lms.api.library.service.FileHandleService;
import com.visang.aidt.lms.api.library.service.FileService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final DrawPdfService drawPdfService;

    private final FileService fileService;

    private final FileHandleService fileHandleService;

    @Value("${cloud.aws.nas.path}")
    private String nasPath;

    public String createDgnssAnalysisByTemplate(File file, Map<String, Object> dgnssData, HttpServletRequest request) throws Exception {

        String templateFileName = null;

        PioPdf pioPdf = new PioPdf();

        int currentPage= 1;

        Map<String, Object> userInfo = (HashMap)dgnssData.get("userInfo");

        // 종합평가일 경우
        if(userInfo.get("DGNSS_ID").toString().equals("DGNSS_10")) {

            templateFileName = "./assets/imgs/dgnss/template/template_10.pdf";

            // 종합평가의 경우 대분류 통계값 없음
            List<Map<String, Object>> dgnssReport4 = (List<Map<String, Object>>)dgnssData.get("dgnssReport4");
            List<Map<String, Object>> dgnssReport5 = (List<Map<String, Object>>)dgnssData.get("dgnssReport5");

            List<Map<String, Object>> dgnssReportStudy = (List<Map<String, Object>>)dgnssData.get("dgnssReportStudy");

            try {
                pioPdf.loadDoc(templateFileName);

                currentPage = 1;
                for (int i = 1; i < 24; i++) {
                    log.debug("now page : {}" , i);
                    pioPdf.movePage(currentPage);

                    drawPdfService.addDgnssPage_DGNSS10(pioPdf, pioPdf.pdDoc, pioPdf.pdStream,  i, userInfo, null, dgnssReport4, dgnssReport5,dgnssReportStudy);

                    currentPage++;

                }
            } catch(IOException e){
                throw new RuntimeException(e);
            }

        }
        // 자기조절일 경우
        else if(userInfo.get("DGNSS_ID").toString().equals("DGNSS_20")){

            if(userInfo.get("DGNSS_ORD").toString().equals("1"))
                templateFileName = "./assets/imgs/dgnss/template/template_20_1st.pdf";
            else
                templateFileName = "./assets/imgs/dgnss/template/template_20_nst.pdf";

            List<Map<String, Object>> dgnssReport3 = (List<Map<String, Object>>) dgnssData.get("dgnssReport3");
            List<Map<String, Object>> dgnssReport4 = (List<Map<String, Object>>)dgnssData.get("dgnssReport4");
            List<Map<String, Object>> dgnssReport5 = (List<Map<String, Object>>)dgnssData.get("dgnssReport5");

            try {
                pioPdf.loadDoc(templateFileName);

                currentPage = 1;
                for (int i = 1; i < 19; i++) {

                    pioPdf.movePage(currentPage);

                    drawPdfService.addDgnssPage_DGNSS20(pioPdf, pioPdf.pdDoc, pioPdf.pdStream,  i, userInfo, dgnssReport3, dgnssReport4, dgnssReport5);

                    currentPage++;

                }
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }
        // 로컬로 파일 저장할때 사용
        //pioPdf.saveDoc(file);
        byte[] pdfBytes;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            pioPdf.saveDoc(outputStream);
            pdfBytes = outputStream.toByteArray();
        }

        FileItem fileItem = new DiskFileItem("file", "application/pdf", true, file.getName(), pdfBytes.length, null);

        // 아래의 메서드가 파일 연결을 유지하고 있어 삭제할 수 없기에 try로 따로 관리
        try (OutputStream os = fileItem.getOutputStream()) {
            os.write(pdfBytes);
            os.flush();
        }

        MultipartFile mFile = new CommonsMultipartFile(fileItem);

        return fileUpload(mFile, request);
//        return "";
    }

    // 교사용 시작
    public String createDgnssReportCoch(File file, Map<String, Object> dgnssData, HttpServletRequest request) throws Exception {

        String templateFileName = null;

        PioPdf  pioPdf = new PioPdf();

        int currentPage= 1;

        Map<String, Object> testInfo = (HashMap)dgnssData.get("testInfo");

        // 종합평가일 경우
        if(testInfo.get("DGNSS_ID").toString().equals("DGNSS_10")) {

            templateFileName = "./assets/imgs/dgnss/template/template_10_coch_30.pdf";

            List<Map<String, Object>> dgnssReportLS = (List<Map<String, Object>>)dgnssData.get("dgnssReportLS");
            List<Map<String, Object>> dgnssReportSection = (List<Map<String, Object>>)dgnssData.get("dgnssReportSection");
            List<Map<String, Object>> dgnssReportValidity = (List<Map<String, Object>>)dgnssData.get("dgnssReportValidity");
            List<Map<String, Object>> dgnssReportMem = (List<Map<String, Object>>)dgnssData.get("dgnssReportMem");
            List<Map<String, Object>> dgnssReportStat3 = (List<Map<String, Object>>)dgnssData.get("dgnssReportStat3");
            List<Map<String, Object>> dgnssReportStat5 = (List<Map<String, Object>>)dgnssData.get("dgnssReportStat5");

            try {
                pioPdf.loadDoc(templateFileName);

                currentPage = 1;
                for (int i = 1; i < 19; i++) {
                    log.debug("now page : {}" , i);
                    pioPdf.movePage(currentPage);

                    drawPdfService.addDgnssPage_DGNSS10_COCH(pioPdf, pioPdf.pdDoc, pioPdf.pdStream,  i, testInfo,  dgnssReportLS, dgnssReportSection, dgnssReportValidity,dgnssReportMem, dgnssReportStat3, dgnssReportStat5);

                    currentPage++;

                }
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }
        // 자기조절일 경우
        else if(testInfo.get("DGNSS_ID").toString().equals("DGNSS_20")){

            templateFileName = "./assets/imgs/dgnss/template/template_20_coch_30.pdf";

            List<Map<String, Object>> dgnssReportLS = (List<Map<String, Object>>)dgnssData.get("dgnssReportLS");
            List<Map<String, Object>> dgnssReportSection = (List<Map<String, Object>>)dgnssData.get("dgnssReportSection");
            List<Map<String, Object>> dgnssReportValidity = (List<Map<String, Object>>)dgnssData.get("dgnssReportValidity");
            List<Map<String, Object>> dgnssReportMem = (List<Map<String, Object>>)dgnssData.get("dgnssReportMem");
            List<Map<String, Object>> dgnssReportStat3 = (List<Map<String, Object>>)dgnssData.get("dgnssReportStat3");
            List<Map<String, Object>> dgnssReportStat5 = (List<Map<String, Object>>)dgnssData.get("dgnssReportStat5");

            try {
                pioPdf.loadDoc(templateFileName);

                currentPage = 1;
                for (int i = 1; i < 12; i++) {
                    //log.debug("now page : {}" , i);
                    pioPdf.movePage(currentPage);

                    drawPdfService.addDgnssPage_DGNSS20_COCH(pioPdf, pioPdf.pdDoc, pioPdf.pdStream,  i, testInfo,  dgnssReportLS, dgnssReportSection, dgnssReportValidity,dgnssReportMem, dgnssReportStat3, dgnssReportStat5);

                    currentPage++;

                }
            } catch(IOException e){
                throw new RuntimeException(e);
            }

        }
        // 로컬로 파일 저장할때 사용
        //pioPdf.saveDoc(file);
        byte[] pdfBytes;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            pioPdf.saveDoc(outputStream);
            pdfBytes = outputStream.toByteArray();
        }

        FileItem fileItem = new DiskFileItem("file", "application/pdf", true, file.getName(), pdfBytes.length, null);

        try (OutputStream os = fileItem.getOutputStream()) {
            os.write(pdfBytes);
            os.flush();
        }

        MultipartFile mFile = new CommonsMultipartFile(fileItem);

        return fileUpload(mFile, request);
        //return "";
    }

    public String fileUpload(MultipartFile file, HttpServletRequest request) {
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String filePath = nasPath + LocalDate.now().format(formatter);
        List<LinkedHashMap<String, Object>> url = fileService.uploadFile(fileList, filePath, "Y", request);

        return url.get(0).get("url").toString();
    }
}


