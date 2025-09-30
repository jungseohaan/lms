package com.visang.aidt.lms.api.stress.service;

import com.visang.aidt.lms.api.learning.mapper.StdUsdCalculateMapper;
import com.visang.aidt.lms.api.materials.service.PortalPzService;
import com.visang.aidt.lms.api.stress.mapper.StressTiosApiMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class StressTiosApiService {

    private final StressPortalPzService stressPortalPzService;
    private final StressTiosApiMapper stressTiosApiMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> getUserInfo(Map<String, Object> paramMap) throws Exception {
        return stressTiosApiMapper.getUserInfo(paramMap);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPtnInfo(Map<String, Object> paramMap) throws Exception {
        return stressTiosApiMapper.getPtnInfo(paramMap);
    }

    public Map<String, Object> saveAccountProc(Map<String, Object> paramData, Map<String, Object> ptnInfo) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "00000");
        result.put("message", "성공");

        String encAuthCode = MapUtils.getString(paramData, "encAuthCode", "");
        String referUserId = MapUtils.getString(paramData, "referUserId", "");
        String deployServerCode = MapUtils.getString(paramData, "deployServerCode", "");

        try {
            String curriSchool = MapUtils.getString(ptnInfo, "curriSchool", "");
            String schlNm = "비상";
            if (curriSchool.equals("elementary")) {
                schlNm += "초등학교";
            } else if (curriSchool.equals("middle")) {
                schlNm += "중학교";
            } else {
                schlNm += "고등학교";
            }
            String curriGrade = MapUtils.getString(ptnInfo, "curriGrade", "");
            String grade = curriGrade.replaceAll("grade0", "");
            if (grade.equals("nograde")) {
                grade = "1";
            }
            String claNm = "1";
            String claId = UUID.randomUUID().toString().replaceAll("-", "");

            Map<String, Object> userInsertMap = new HashMap<>();

            String tcId = encAuthCode + "-t";
            userInsertMap.put("userId", tcId);
            userInsertMap.put("flnm", "김비상");
            userInsertMap.put("userType", "T");
            userInsertMap.put("schlNm", schlNm);
            userInsertMap.put("grade", grade);
            userInsertMap.put("claNm", claNm);
            userInsertMap.put("claId", claId);
            userInsertMap.put("referUserId", referUserId);
            userInsertMap.put("rgtr", "tiosAPI");

            Instant startTime = Instant.now();
            Duration duration = Duration.between(startTime, startTime);

            // user 테이블 적재
            stressTiosApiMapper.insertUser(userInsertMap);

            // tc_reg_info 적재
            stressTiosApiMapper.insertTcRegInfo(userInsertMap);

            // tc_cla_info 적재
            stressTiosApiMapper.insertTcClaInfo(userInsertMap);

            // tios_user_info 적재
            stressTiosApiMapper.insertTiosUserInfo(userInsertMap);

            List<String> stdtIdList = new ArrayList<>();
            List<Map<String, Object>> userInsertMapList = new ArrayList<>();
            for (int i=1; i<=5; i++) {
                userInsertMap = new HashMap<>();
                String stdtId = encAuthCode + "-s" + i;
                userInsertMap.put("userId", stdtId);
                userInsertMap.put("tcId", tcId);
                userInsertMap.put("flnm", "학생"+i);
                userInsertMap.put("userType", "S");
                userInsertMap.put("schlNm", schlNm);
                userInsertMap.put("grade", grade);
                userInsertMap.put("claNm", claNm);
                userInsertMap.put("claId", claId);
                userInsertMap.put("num", i);
                userInsertMap.put("referUserId", referUserId);
                userInsertMap.put("rgtr", "tiosAPI");
                stdtIdList.add(stdtId);
                userInsertMapList.add(userInsertMap);
            }
            // user 테이블 적재
            stressTiosApiMapper.insertUserBulk(userInsertMapList);
            // stdt_reg_info 적재
            stressTiosApiMapper.insertStdtRegInfoBulk(userInsertMapList);
            // tc_cla_mb_info 적재
            stressTiosApiMapper.insertTcClaMbInfoBulk(userInsertMapList);
            // tios_user_info 적재
            stressTiosApiMapper.insertTiosUserInfoBulk(userInsertMapList);


            Instant endTime = Instant.now();
            duration = Duration.between(startTime, endTime);
            log.info("TiosService USER CREATE duration:{}",  duration.toMillis());

            //교과서 세팅
            startTime = Instant.now();
            Map<String, Object> data = new HashMap<>();
            data.put("wrterId", tcId);
            data.put("claId", claId);
            data.put("curriSchool", ptnInfo.getOrDefault("curriSchool", ""));
            data.put("curriGrade", ptnInfo.getOrDefault("curriGrade", ""));
            data.put("curriSubject", ptnInfo.getOrDefault("curriSubject", ""));
            data.put("curriSemester", ptnInfo.getOrDefault("curriSemester", ""));
            data.put("deployServerCode", deployServerCode);
            List<Map<String, Object>> cmsTextbookList = stressPortalPzService.findLcmsTextbookList(data);
            //CBS 배포된 교과서가 1개일 경우 LMS로 교과서 이관
            if (cmsTextbookList.size() == 1) {
                Map<String, Object> cmsTextbookInfo = cmsTextbookList.get(0);
                int textbkId = (int) cmsTextbookInfo.getOrDefault("textbkId",0);
                data.put("textbkId", textbkId);
                Map<String, Object> stdTransInfo = stressTiosApiMapper.getStdTransInfo(data);
                String srcGrp = MapUtils.getString(stdTransInfo, "srcGrp", "");
                String setsIds = MapUtils.getString(stdTransInfo, "setsIds", "");
                String transYn = MapUtils.getString(stdTransInfo, "transYn", "");

                if ("Y".equals(transYn)) {
                    //교과서 생성
                    stressPortalPzService.createTextBook(cmsTextbookInfo, data);

                    endTime = Instant.now();
                    duration = Duration.between(startTime, endTime);
                    log.info("TiosService TEXTBOOK CREATE duration:{}",  duration.toMillis());

                    //학습데이터 이관
                    startTime = Instant.now();
                    List<Map<String, Object>> params = new ArrayList<>();
                    for (int i =0; i<stdtIdList.size(); i++) {
                        String sourceUserId = srcGrp + "-s" + (i+1);
                        String targetUserId = stdtIdList.get(i);
                        if (i == 1 || i == 2 || i == 3) {
                            sourceUserId = srcGrp + "-s2";
                        }
                        if (i == 4) {
                            sourceUserId = srcGrp + "-s3";
                        }
                        Map<String, Object> param = new HashMap<>();
                        param.put("sourceUserId", sourceUserId);
                        param.put("targetUserId", targetUserId);
                        param.put("setsId", setsIds);
                        Map<String, Object> stdDtaResultInfo = stressTiosApiMapper.getStdDtaResultInfo(param);
                        param.put("sourceDtaResultId", MapUtils.getInteger(stdDtaResultInfo, "sourceDtaResultId", 0));
                        param.put("targetTextbkTabId", MapUtils.getInteger(stdDtaResultInfo, "targetTextbkTabId", 0));
                        stressTiosApiMapper.insertStdDtaResulInfo(param);
                        int dtaResultId = Integer.parseInt(String.valueOf(param.getOrDefault("dtaResultId", "0")));
                        param.put("dtaResultId", dtaResultId);
                        stressTiosApiMapper.insertStdDtaResulDetail(param);
                    }

                    endTime = Instant.now();
                    duration = Duration.between(startTime, endTime);
                    log.info("TiosService STD TRANS duration:{}",  duration.toMillis());

                    //배치데이터 생성
                    startTime = Instant.now();
                    Map<String, Object> s = new HashMap<>();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
                    String now = formatter.format(LocalDate.now());
                    s.put("apiYn", "Y");
                    s.put("calcDt", now);
                    s.put("stdDt", now);
                    s.put("textbkId", textbkId);
                    s.put("wrterId", tcId);
                    s.put("claId", claId);
                    
                    //영어인경우.
                    if (StringUtils.equals((String) ptnInfo.getOrDefault("curriSubject", ""), "english")) {
                        int cnt21 = stressTiosApiMapper.insertEngUsdAchSrc2Info(s);
                        int cnt22 = stressTiosApiMapper.insertEngUsdAchSrc2Detail(s);
                        int cnt23 = stressTiosApiMapper.insertEngUsdAchSrc2Kwg(s);
                        //int cnt25 = tiosApiMapper.insertAchCacSrcInfo(s);
                    }
                    //수학인경우
                    else {
                        int cnt21 = stressTiosApiMapper.insertUsdCacSrcInfo(s);
                        int cnt33 = stressTiosApiMapper.insertStdUsdUnitDayHist(s);
                        int cnt34 = stressTiosApiMapper.insertStdUsdUnitKwgDayHist(s);
                        int cnt35 = stressTiosApiMapper.insertStdUsdStdtUnitKwgDayHist(s);
                        int cnt37 = stressTiosApiMapper.insertStdUsdUnitInfo(s);
                       /*
                        int cnt31 = stdUsdCalculateMapper.insertStdUsdDayHist(s);
                        int cnt32 = stdUsdCalculateMapper.insertStdUsdNotStdtUnitDayHist(s);
                        int cnt33 = stdUsdCalculateMapper.insertStdUsdUnitDayHist(s);
                        int cnt34 = stdUsdCalculateMapper.insertStdUsdUnitKwgDayHist(s);
                        int cnt35 = stdUsdCalculateMapper.insertStdUsdStdtUnitKwgDayHist(s);
                        int cnt36 = stdUsdCalculateMapper.insertStdUsdInfo(s);
                        int cnt37 = stdUsdCalculateMapper.insertStdUsdUnitInfo(s);
                        */
                    }
                    endTime = Instant.now();
                    duration = Duration.between(startTime, endTime);
                    log.info("TiosService BATCH CREATE duration:{}",  duration.toMillis());
                }
            }
        } catch (NullPointerException e) {
            log.error("saveAccountProc - NullPointerException: {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("saveAccountProc - IllegalArgumentException: {}", e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("saveAccountProc - RuntimeException: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("saveAccountProc - Exception: {}", e.getMessage(), e);
            throw e;
        }
        return result;
    }

}
