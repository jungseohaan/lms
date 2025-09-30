package com.visang.aidt.lms.api.dashboard.batch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import com.visang.aidt.lms.api.dashboard.model.PioPdf;
import com.visang.aidt.lms.api.dashboard.model.VivaClassApiDto;
import com.visang.aidt.lms.api.dashboard.model.VivaClassStDto;
import com.visang.aidt.lms.api.dashboard.model.VivaClassTcDto;
import com.visang.aidt.lms.api.dashboard.service.DrawPdfService;
import com.visang.aidt.lms.api.dashboard.service.EtcService;
import com.visang.aidt.lms.api.keris.utils.response.AidtMemberInfoVo;
import com.visang.aidt.lms.api.keris.utils.response.AidtScheduleInfoVo;
import com.visang.aidt.lms.api.keris.utils.response.AidtUserInfoResponse;
import com.visang.aidt.lms.api.library.service.FileService;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.utility.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"vs-math-develop-job", "vs-engl-develop-job", "vs-math-prod-job", "vs-engl-prod-job"})
public class EtcStatusBatchJob {

    private final EtcService etcService;
    private final EtcMapper etcMapper;
    private final BtchExcnLogService btchExcnLogService;
    private final DrawPdfService drawPdfService;
    private final FileService fileService;

    @Value("${cloud.aws.nas.path}")
    private String nasPath;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Scheduled(cron = "${batch-job.schedule.EtcBatchJob.executeDgnssEnd}")
    public void executeDgnssEnd() throws Exception {
        log.info("EtcBatchJob > executeDgnssEnd()");

        String btchNm = "EtcBatchJob.executeDgnssEnd";
        Map<String, Object> resultOfBatchInfoExist = btchExcnLogService.checkBatchInfoExist(btchNm);

        Boolean isBatchInfoExist = (Boolean) resultOfBatchInfoExist.get("resultOk");
        if (isBatchInfoExist) {
            // 배치 실행 로그 기록
            Map<String, Object> resultOfCreateBtchExcnLog = btchExcnLogService.createBtchExcnLog(resultOfBatchInfoExist.get("btchId").toString());

            // 배치 실행 로그 생성 성공 여부 확인
            Boolean isCreateBtchExcnLog = (Boolean) resultOfCreateBtchExcnLog.get("resultOk");
            if (isCreateBtchExcnLog) {
                Map<String, Object> batchparamData = new HashMap<>();
                batchparamData.put("btchDetId", resultOfBatchInfoExist.get("btchDetId"));

                //배치 서비스 호출
                Map<String, Object> resultOfDgnssEnd = new HashMap<>();
                try {
                    // 90일 마감 대상 심리검사 ID 추출
                    List<Map<String, Object>> dgnssInfoList = etcMapper.selectDgnssEndTargetList();

                    List<Map<String, Object>> dgnss3DaysLeftList = etcMapper.selectDgnssEnd3DaysLeftList();

                    int batchCnt = 0;
                    String token = "";
                    String vivaClassUrl = "";
                    if (CollectionUtils.isNotEmpty(dgnssInfoList) || CollectionUtils.isNotEmpty(dgnss3DaysLeftList)) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        if (StringUtils.equals(serverEnv, "vs-engl-develop-job") || StringUtils.equals(serverEnv, "vs-math-develop-job") || StringUtils.equals(serverEnv, "local")) {
                            vivaClassUrl = "https://dev-vivaclassapi.vivasam.com";
                        } else if (StringUtils.equals(serverEnv, "vs-engl-prod-job") || StringUtils.equals(serverEnv, "vs-math-prod-job")) {
                            vivaClassUrl = "https://vivaclassapi.vivasam.com";
                        }
                        if (StringUtils.isNotEmpty(vivaClassUrl)) {
                            RestTemplate restTemplate = new RestTemplate();
                            Map<String, Object> param = new HashMap<>();
                            String makeJwtTokenUrl = "/api/auth/login";
                            param.put("id", "metapsycho");
                            param.put("accessKey", "MDTMPmBACTNcYGsx+Pfk1lPDlBrCveACPzwtz1cPHBmv6KKhAZg+ikyD4/A/TCnKvFeD6GeLS5889Ic7HjwYv4TlpmpgNLUclPSoqkxe0ac=");

                            VivaClassApiDto vivaApiResponse = etcService.vivaClassApiCall(vivaClassUrl + makeJwtTokenUrl, param, headers);
                            if (StringUtils.equals(vivaApiResponse.getCode(), "-1")) {
                                batchparamData.put("failDc", "vivaclass token api fail : " + param.toString()); //실패사유
                                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                                return;
                            }
                            token = "Bearer " + vivaApiResponse.getResponse();
                        }
                    }
                    List<Map<String, Object>> alarmList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(dgnssInfoList)) {
                        for (Map<String, Object> map : dgnssInfoList) {
                            // 시험 종료 및 채점(다 풀었지만 제출하지 않은 학생도 채점 목록에 포함)
                            etcService.updateTcDgnssEnd(map, null);
                        }
                        String reportComplUrl = "/metapsycho/report";

                        for (Map<String, Object> map : dgnssInfoList) {
                            Map<String, Object> alarmTcMap = new HashMap<>();
                            // 시험종료가 완료된 채점지에 한해서 PDF 생성
                            map.put("userType", "T");
                            String dgnssId = MapUtils.getString(map, "dgnssId", "");
                            String userId = MapUtils.getString(map, "userId", "");
                            String claId = MapUtils.getString(map, "claId", "");
                            int claIdx = Integer.parseInt(claId.substring(claId.lastIndexOf("-") + 1));
                            String vivaUserId = userId.substring(userId.lastIndexOf("-") + 1);
                            int paperIdx = MapUtils.getInteger(map, "paperIdx", 0);
                            String paperName = paperIdx == 1 ? "META 학습종합검사" : "META 자기조절학습검사";
                            String summary = "[" + paperName + "] 리포트가 생성되었습니다. 리포트 결과를 확인해 보세요.";

                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.set("Authorization", token);
                            Map<String, Object> tcUserParam = new HashMap<>();

                            tcUserParam.put("teacherId", userId.substring(userId.lastIndexOf("-") + 1));
                            tcUserParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));

                            // 교사 학급 정보 호출
                            VivaClassApiDto vivaTcInfoApiResponse = etcService.vivaClassApiCall(vivaClassUrl + "/api/metapsycho/class/info", tcUserParam, headers);
                            if (StringUtils.equals(vivaTcInfoApiResponse.getCode(), "-1")) {
                                batchparamData.put("failDc", "vivaclass teacher token api fail : " + tcUserParam.toString()); //실패사유
                                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                                break;
                            }
                            ObjectMapper objectMapper = new ObjectMapper();
                            VivaClassTcDto vivaClassTcInfo = objectMapper.convertValue(vivaTcInfoApiResponse.getResponse(), VivaClassTcDto.class);

                            Map<String, Object> stUserParam = new HashMap<>();
                            stUserParam.put("classSeq", claId.substring(claId.lastIndexOf("-") + 1));

                            // 학급 구성원 호출
                            Map<String, Map<String, Object>> stVivaClassInfo = new HashMap<>();
                            VivaClassApiDto vivaStInfoApiResponse = etcService.vivaClassApiCall(vivaClassUrl + "/api/metapsycho/student/list", stUserParam, headers);
                            if (StringUtils.equals(vivaStInfoApiResponse.getCode(), "-1")) {
                                batchparamData.put("failDc", "vivaclass student token api fail : " + tcUserParam.toString()); //실패사유
                                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                                break;
                            }
                            // 학급 구성원을 List형태로 변환
                            List<VivaClassStDto> vivaClassStList = new ArrayList<>();
                            if (vivaStInfoApiResponse.getResponse() instanceof List<?>) {
                                vivaClassStList = ((List<?>) vivaStInfoApiResponse.getResponse()).stream()
                                        .map(item -> objectMapper.convertValue(item, VivaClassStDto.class))
                                        .collect(Collectors.toList());
                            } else {
                                batchparamData.put("failDc", "vivaclass student token api fail : no students"); //실패사유
                                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                                break;
                            }

                            for (VivaClassStDto stDto : vivaClassStList) {
                                if (stDto == null) continue;

                                Map<String, Object> stInfoMap = new HashMap<>();
                                // 학생용 PDF 생성시 필요
                                stInfoMap.put("MEM_NM", stDto.getName());
                                stInfoMap.put("SCH_NM", stDto.getSchName());
                                stInfoMap.put("CLASS_NO", stDto.getSchClassNo());
                                stInfoMap.put("MEM_GENDER", StringUtils.equals(stDto.getGender(), "M") ? "1" : "2");
                                stInfoMap.put("MEM_GRADE_NM", stDto.getSchYear().endsWith("학년") ? stDto.getSchYear() : stDto.getSchYear() + "학년");
                                stInfoMap.put("CLASS_NM", stDto.getSchClassNo() + "반");

                                // 교사용 PDF 생성시 필요
                                stInfoMap.put("userId", "vivaclass-s-" + stDto.getMemberId());
                                stInfoMap.put("userName", stDto.getName());
                                stInfoMap.put("schoolName", stDto.getSchName());
                                stInfoMap.put("userNumber", stDto.getSchClassNo());
                                stInfoMap.put("userGender", StringUtils.equals(stDto.getGender(), "M") ? "1" : "2");

                                stVivaClassInfo.put("vivaclass-s-" + stDto.getMemberId() + "-" + claId.substring(claId.lastIndexOf("-") + 1), stInfoMap);
                            }

                            // 교사 PDF 생성 및 파일 URL 할당
                            makeTcDgnssPdfForBatch(map, resultOfDgnssEnd, vivaClassTcInfo, stVivaClassInfo);
                            batchCnt++;

                            alarmTcMap.put("classSeq", claIdx);
                            alarmTcMap.put("category", "METAPSYCHO");
                            alarmTcMap.put("summary", summary);
                            alarmTcMap.put("url", "/metapsycho/report?userId=" + userId + "&classSeq=" + claIdx + "&isTeacher=true&dgnssId=" + dgnssId);
                            alarmTcMap.put("userType", "T");
                            alarmTcMap.put("targetId", vivaUserId);
                            alarmTcMap.put("regId", vivaUserId);

                            alarmList.add(alarmTcMap);
                            List<Map<String, Object>> dgnssMakePdfTargetAnswerIdxList = etcMapper.MakePdfTargetAnswerIdxList(map);

                            for (Map<String, Object> stMap : dgnssMakePdfTargetAnswerIdxList) {
                                Map<String, Object> alaramStMap = new HashMap<>();
                                int answerIdx = MapUtils.getInteger(stMap, "id", 0);
                                String stdtId = MapUtils.getString(stMap, "stdtId", "");
                                String vivaStUserId = stdtId.substring(
                                        stdtId.lastIndexOf("-", stdtId.lastIndexOf("-") - 1) + 1,
                                        stdtId.lastIndexOf("-")
                                );
                                String submAt = MapUtils.getString(stMap, "submAt", "");
                                String stSummary = "";

                                // 학생이 모든 문항을 응답했을때만 제출 진행(수동으로 제출하지 않아도)
                                if (StringUtils.equals(submAt, "Y")) {
                                    Map<String, Object> stUserMap = new HashMap<>();
                                    stUserMap.put("userType", "S");
                                    stUserMap.put("answerIdx", answerIdx);
                                    stUserMap.put("userId", stdtId);
                                    makeStDgnssPdfForBatch(stUserMap, resultOfDgnssEnd, stVivaClassInfo);

                                    stSummary = "[" + paperName + "] 가 끝났어요. 검사 결과를 확인해보세요.";
                                    alaramStMap.put("url", "/metapsycho/report?userId=" + stdtId + "&isTeacher=false&dgnssId=" + dgnssId);
                                    alaramStMap.put("dgnssId", MapUtils.getString(stMap, "dgnssId", ""));
                                } else {
                                    stSummary = "[" + paperName + "] 기간에 검사를 진행하지 않았어요.";
                                    alaramStMap.put("url", "/metapsycho/main?userId=" + stdtId + "&isTeacher=false&classSeq=" + claIdx);
                                }

                                alaramStMap.put("classSeq", claIdx);
                                alaramStMap.put("category", "METAPSYCHO");
                                alaramStMap.put("summary", stSummary);
                                alaramStMap.put("userType", "S");
                                alaramStMap.put("targetId", vivaStUserId);
                                alaramStMap.put("regId", vivaUserId);

                                alarmList.add(alaramStMap);
                            }
                        }

                        if ((Boolean) resultOfDgnssEnd.get("resultOk")) {
                            batchparamData.put("btchExcnRsltCnt", batchCnt);
                            resultOfDgnssEnd.put("resultOk", true);
                        } else {
                            batchparamData.put("failDc", resultOfDgnssEnd.get("failDc")); //실패사유
                            batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                            resultOfDgnssEnd.put("resultOk", false);
                        }
                    }
                    String daysLeftUrl = "/metapsycho/main";
                    if (CollectionUtils.isNotEmpty(dgnss3DaysLeftList)) {
                        for (Map<String, Object> map2 : dgnss3DaysLeftList) {
                            Map<String, Object> alarmStMap = new HashMap<>();
                            String userId = MapUtils.getString(map2, "userId", "");
                            String claId = MapUtils.getString(map2, "claId", "");
                            int claIdx = Integer.parseInt(claId.substring(claId.lastIndexOf("-") + 1));
                            String vivaTcId = userId.substring(userId.lastIndexOf("-") + 1);
                            int paperIdx = MapUtils.getInteger(map2, "paperIdx", 0);
                            String paperName = paperIdx == 1 ? "META 학습종합검사" : "META 자기조절학습검사";
                            String summary = "[" + paperName + "] 기한 마감 " + MapUtils.getInteger(map2, "remainDay", 0) + "일 전입니다. 서둘러 검사를 진행해 주세요.";

                            alarmStMap.put("classSeq", claIdx);
                            alarmStMap.put("category", "METAPSYCHO");
                            alarmStMap.put("summary", summary);
                            alarmStMap.put("userType", "S");
                            alarmStMap.put("regId", vivaTcId);

                            String notSubmStIdList = MapUtils.getString(map2, "notSubmStdtId", "");
                            if (StringUtils.isNotEmpty(notSubmStIdList)) {
                                String[] notSubmIdArr = notSubmStIdList.split(",");
                                for (String notSubmStdtId : notSubmIdArr) {
                                    alarmStMap.put("targetId", notSubmStdtId.substring(
                                            notSubmStdtId.lastIndexOf("-", notSubmStdtId.lastIndexOf("-") - 1) + 1,
                                            notSubmStdtId.lastIndexOf("-")
                                    ));
                                    alarmStMap.put("url", "/metapsycho/main?userId=" + notSubmStdtId + "&classSeq=" + claIdx + "&isTeacher=false");
                                    alarmList.add(alarmStMap);
                                }
                            }
                        }
                    }

                    // 알람 발송
                    if (CollectionUtils.isNotEmpty(alarmList)) {
                        String alarmUrl = "/api/alarm/insert";
                        RestTemplate restTemplate = new RestTemplate();

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("Authorization", token);

                        for (Map<String, Object> alarmMap : alarmList) {
                            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(alarmMap, headers);
                            ResponseEntity<VivaClassApiDto> response = restTemplate.exchange(
                                    vivaClassUrl + alarmUrl,
                                    HttpMethod.POST,
                                    requestEntity,
                                    VivaClassApiDto.class
                            );

                            if (!response.getBody().getCode().equals("00000")) {
                                log.error("vivaclass alarm insert fail");
                            }
                        }
                    }
                } catch (Exception e) {
                    batchparamData.put("failDc", resultOfDgnssEnd.get("failDc")); //실패사유
                    batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                } finally {
                    // 배치 결과 업데이트
                    btchExcnLogService.modifyBtchExcnLog(batchparamData);
                }
            }

        }

    }

    public void makeTcDgnssPdfForBatch(Map<String, Object> paramData,
                                     Map<String, Object> resultOfDgnssEnd,
                                     VivaClassTcDto vivaClassTcInfo,
                                     Map<String, Map<String, Object>> stVivaClassInfoMap) throws Exception {
        String userId = MapUtils.getString(paramData, "userId", "");

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTime = currentTime.format(formatter);

        String fileName = userId + "_" + formattedTime + ".pdf";

        Map<String, Object> tcVivaClassInfo = new HashMap<>();

        tcVivaClassInfo.put("className", vivaClassTcInfo.getSchClassName().endsWith("반") ? vivaClassTcInfo.getSchClassName() : vivaClassTcInfo.getSchClassName() + "반");
        tcVivaClassInfo.put("clsType", vivaClassTcInfo.getClsTypeCode());
        tcVivaClassInfo.put("nickNameClass", vivaClassTcInfo.getClsName());
        tcVivaClassInfo.put("userName", vivaClassTcInfo.getTeacherName());
        tcVivaClassInfo.put("schoolName", vivaClassTcInfo.getSchName());
        tcVivaClassInfo.put("userGrade", vivaClassTcInfo.getSchYear());

        Map<String, Object> tcUserInfo = etcMapper.selectTcUserInfo(paramData);
        makeTcPdf(paramData, tcUserInfo, fileName, resultOfDgnssEnd, tcVivaClassInfo, stVivaClassInfoMap);
    }


    public void makeStDgnssPdfForBatch(Map<String, Object> paramData,
                                       Map<String, Object> resultOfDgnssEnd,
                                       Map<String, Map<String, Object>> stVivaClassInfoMap) throws Exception {
        String userId = MapUtils.getString(paramData, "userId", "");

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTime = currentTime.format(formatter);

        String fileName = userId + "_" + formattedTime + ".pdf";

        Map<String, Object> stUserInfo = etcMapper.selectStUserInfo(paramData);
        Map<String, Object> targetStInfo = stVivaClassInfoMap.get(userId);
        stUserInfo.put("MEM_NM", MapUtils.getString(targetStInfo, "MEM_NM", ""));
        stUserInfo.put("SCH_NM", MapUtils.getString(targetStInfo, "SCH_NM", ""));
        stUserInfo.put("CLASS_NO", MapUtils.getString(targetStInfo, "CLASS_NO", ""));
        stUserInfo.put("MEM_GRADE_NM", MapUtils.getString(targetStInfo, "MEM_GRADE_NM", "").endsWith("학년") ? MapUtils.getString(targetStInfo, "MEM_GRADE_NM", "") : MapUtils.getString(targetStInfo, "MEM_GRADE_NM", "") + "학년");
        stUserInfo.put("CLASS_NM", MapUtils.getString(targetStInfo, "CLASS_NM", "") + "반");
        makeStPdf(paramData, stUserInfo, fileName, resultOfDgnssEnd);
    }

    void makeTcPdf(Map<String, Object> paramData,
                   Map<String, Object> tcUserInfo,
                   String fileName,
                   Map<String, Object> resultOfDgnssEnd,
                   Map<String, Object> tcVivaClassInfo,
                   Map<String, Map<String, Object>> stVivaClassInfoMap) throws Exception {
        Map<String, Object> dgnssData = new HashMap<String, Object>();
        Map<String, Object> param  = new HashMap<String, Object>();

        int nowOrd = MapUtils.getInteger(tcUserInfo, "TEST_ORD", 0);

        param.put("TEST_IDX", MapUtils.getString(tcUserInfo, "TEST_IDX"));
        param.put("TEST_ORD", nowOrd);
        param.put("DGNSS_ID", MapUtils.getString(tcUserInfo, "DGNSS_ID"));
        param.put("isVivaClass", true);
        param.put("claId", MapUtils.getString(tcUserInfo, "claId"));

        // 학습환경
        List<Map<String, Object>> dgnssReportLS = etcMapper.getDgnssReportLS(param);
        List<Map<String, Object>> dgnssReportSection = etcMapper.getDgnssReportSection(param);
        List<Map<String, Object>> dgnssReportValidity = etcMapper.getDgnssReportValidity(param);

        List<Map<String, Object>> dgnssReportMem = etcMapper.getDgnssReportMem(param);
        // 첫번째 검사를 본 id 추출
        param.put("FIRST_IDX", etcMapper.getDgnssFirstTest(param));

        // 종합분석 집계
        param.put("DEPTH", 3);
        param.put("notExists", "N");
        param.put("firstCancel", "N");
        List<Map<String, Object>> dgnssReportStat3 = etcMapper.getDgnssReportStatByTest(param);

        if (CollectionUtils.isEmpty(dgnssReportStat3)) {
            // 조회한 회차의 데이터가 신뢰도 지표 조건으로 인해 없는 경우 재조회
            param.put("notExists", "Y");
            dgnssReportStat3 = etcMapper.getDgnssReportStatByTest(param);

        }

        // 2회차까지의 데이터를 조회하는데 1회차가 신뢰도 조건으로 인해 0점인 경우 재조회
        if (nowOrd == 2) {
            boolean stat3First = dgnssReportStat3.stream()
                    .allMatch(map -> MapUtils.getInteger(map, "T_SCORE_FIRST", 0) == 0);

            if (stat3First) {
                param.put("firstCancel", "Y");
                dgnssReportStat3 = etcMapper.getDgnssReportStatByTest(param);
            }
        }

        param.put("notExists", "N");
        param.put("firstCancel", "N");
        param.put("DEPTH", 5);
        List<Map<String, Object>> dgnssReportStat5 = etcMapper.getDgnssReportStatByTest(param);

        if (CollectionUtils.isEmpty(dgnssReportStat5)) {
            param.put("notExists", "Y");
            dgnssReportStat5 = etcMapper.getDgnssReportStatByTest(param);
        }

        if (nowOrd == 2) {
            boolean stat5Mark = dgnssReportStat5.stream()
                    .allMatch(map -> MapUtils.getInteger(map, "T_SCORE_FIRST", 0) == 0);

            // 2회차까지의 데이터를 조회하는데 1회차가 신뢰도 조건으로 인해 0점인 경우 재조회
            if (stat5Mark) {
                param.put("firstCancel", "Y");
                dgnssReportStat5 = etcMapper.getDgnssReportStatByTest(param);
            }
        }

        if (MapUtils.isNotEmpty(tcVivaClassInfo) && MapUtils.isNotEmpty(stVivaClassInfoMap)) {
            etcService.convertDgnssData(tcVivaClassInfo, stVivaClassInfoMap, dgnssReportLS, dgnssReportSection, dgnssReportValidity, dgnssReportMem, tcUserInfo);
        }

        // 선생님 기본 정보(표지 데이터)
        dgnssData.put("testInfo", tcUserInfo);

        dgnssData.put("dgnssReportLS", dgnssReportLS);

        // 대분류, 중분류, 소분류 별 표준점수
        dgnssData.put("dgnssReportSection", dgnssReportSection);

        // 교사용 신뢰도(바람직성, 반응일관성, 무응답 수) 부족 학생
        dgnssData.put("dgnssReportValidity", dgnssReportValidity);

        // 중분류 별 상담 필요 학생
        dgnssData.put("dgnssReportMem", dgnssReportMem);

        dgnssData.put("dgnssReportStat3", dgnssReportStat3);

        dgnssData.put("dgnssReportStat5", dgnssReportStat5);

        try {
            String url = this.createDgnssReportCoch(new File(fileName), dgnssData);
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("fileUrl", url);
            updateMap.put("dgnssId", MapUtils.getString(param, "TEST_IDX", ""));
            etcMapper.updateFileUrlTch(updateMap);
            resultOfDgnssEnd.put("resultOk", true);
        } catch (Exception e) {
            resultOfDgnssEnd.put("failDc", "dgnssBatchFail TC - " + MapUtils.getString(param, "TEST_IDX", ""));
            resultOfDgnssEnd.put("resultOk", false);
        }

    }

    void makeStPdf(Map<String, Object> paramData, Map<String, Object> stUserInfo, String fileName, Map<String, Object> resultOfDgnssEnd) throws Exception {
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> dgnssData = new HashMap<>();
        param.put("ANSWER_IDX", MapUtils.getInteger(paramData, "answerIdx", 0));

        param.put("DEPTH", 3);
        dgnssData.put("dgnssReport3", etcMapper.getDgnssReport(param));

        param.put("DEPTH", 4);
        dgnssData.put("dgnssReport4", etcMapper.getDgnssReport(param));

        param.put("DEPTH", 5);
        dgnssData.put("dgnssReport5", etcMapper.getDgnssReport(param));
        dgnssData.put("dgnssReportStudy", etcMapper.getDgnssReportStudy(param));
        dgnssData.put("userInfo", stUserInfo);

        try {
            String url = this.createDgnssAnalysisByTemplate(new File(fileName), dgnssData);

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("fileUrl", url);
            updateMap.put("dgnssResultId", MapUtils.getString(stUserInfo, "dgnssResultId", ""));
            etcMapper.updateFileUrl(updateMap);
        } catch (Exception e) {
            resultOfDgnssEnd.put("failDc", "dgnssBatchFail ST - " + MapUtils.getString(stUserInfo, "dgnssResultId", ""));
            resultOfDgnssEnd.put("resultOk", false);
        }
    }

    // 교사용 시작
    public String createDgnssReportCoch(File file, Map<String, Object> dgnssData) throws Exception {

        String templateFileName = null;

        PioPdf pioPdf = new PioPdf();

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
        pioPdf.saveDoc(file);
//        byte[] pdfBytes;
//
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            pioPdf.saveDoc(outputStream);
//            pdfBytes = outputStream.toByteArray();
//        }
//
//        FileItem fileItem = new DiskFileItem("file", "application/pdf", true, file.getName(), pdfBytes.length, null);
//
//        try (OutputStream os = fileItem.getOutputStream()) {
//            os.write(pdfBytes);
//            os.flush();
//        }
//
//        MultipartFile mFile = new CommonsMultipartFile(fileItem);
//
//        return fileUpload(mFile);
        return "";
    }

    public String createDgnssAnalysisByTemplate(File file, Map<String, Object> dgnssData) throws Exception {

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
        pioPdf.saveDoc(file);
//        byte[] pdfBytes;
//
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            pioPdf.saveDoc(outputStream);
//            pdfBytes = outputStream.toByteArray();
//        }
//
//        FileItem fileItem = new DiskFileItem("file", "application/pdf", true, file.getName(), pdfBytes.length, null);
//
//        // 아래의 메서드가 파일 연결을 유지하고 있어 삭제할 수 없기에 try로 따로 관리
//        try (OutputStream os = fileItem.getOutputStream()) {
//            os.write(pdfBytes);
//            os.flush();
//        }
//
//        MultipartFile mFile = new CommonsMultipartFile(fileItem);
//
//        return fileUpload(mFile);
        return "";
    }

    public String fileUpload(MultipartFile file) {
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(file);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String filePath = nasPath + LocalDate.now().format(formatter);
        List<LinkedHashMap<String, Object>> url = fileService.uploadDgnssForBatch(fileList, filePath);

        return url.get(0).get("url").toString();
    }
}
