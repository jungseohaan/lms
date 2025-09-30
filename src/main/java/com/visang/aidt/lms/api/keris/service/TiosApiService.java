package com.visang.aidt.lms.api.keris.service;

import com.visang.aidt.lms.api.keris.mapper.TiosApiMapper;
import com.visang.aidt.lms.api.learning.mapper.StdUsdCalculateMapper;
import com.visang.aidt.lms.api.materials.service.PortalPzService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TiosApiService {

    private final PortalPzService portalPzService;
    private final TiosApiMapper tiosApiMapper;
    private final StdUsdCalculateMapper stdUsdCalculateMapper;

    @Value("${app.lcmsapi.deployServerCodeMulti}")
    public String deployServerCodeMulti;/*민간존 운영(비바샘)의 경우에는 컨텐츠 배포를 여러 서버 다중 배포가 일어남*/

    @Transactional(readOnly = true)
    public Map<String, Object> getUserInfo(Map<String, Object> paramMap) throws Exception {
        return tiosApiMapper.getUserInfo(paramMap);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPtnInfo(Map<String, Object> paramMap) throws Exception {
        return tiosApiMapper.getPtnInfo(paramMap);
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
            tiosApiMapper.insertUser(userInsertMap);

            // tc_reg_info 적재
            tiosApiMapper.insertTcRegInfo(userInsertMap);

            // tc_cla_info 적재
            tiosApiMapper.insertTcClaInfo(userInsertMap);

            // tc_cla_user_info 적재
            tiosApiMapper.upsertTcClaUserInfo(userInsertMap);

            // tios_user_info 적재
            tiosApiMapper.insertTiosUserInfo(userInsertMap);

            // sp_prchs_hist 적재
            tiosApiMapper.insertShopSkinHist(userInsertMap);
            tiosApiMapper.insertShopGameHist(userInsertMap);
            tiosApiMapper.insertShopProfileHist(userInsertMap);

            // sp_prchs_info 적재
            tiosApiMapper.insertShopSkin(userInsertMap);
            tiosApiMapper.insertShopGame(userInsertMap);
            tiosApiMapper.insertShopProfile(userInsertMap);

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
                tiosApiMapper.insertUserBulk(userInsertMapList);
                // stdt_reg_info 적재
                tiosApiMapper.insertStdtRegInfoBulk(userInsertMapList);
                // tc_cla_mb_info 적재
                tiosApiMapper.insertTcClaMbInfoBulk(userInsertMapList);
                // tios_user_info 적재
                tiosApiMapper.insertTiosUserInfoBulk(userInsertMapList);

                // sp_prchs_hist 적재
                tiosApiMapper.insertShopSkinHistBulk(userInsertMapList);
                tiosApiMapper.insertShopGameHistBulk(userInsertMapList);
                tiosApiMapper.insertShopProfileHistBulk(userInsertMapList);

                // sp_prchs_info 적재
                tiosApiMapper.insertShopSkinBulk(userInsertMapList);
                tiosApiMapper.insertShopGameBulk(userInsertMapList);
                tiosApiMapper.insertShopProfileBulk(userInsertMapList);


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
            data.put("isPublish", "Y");

            /*한 서버에 여러 컨텐츠 배포하는 경우*/
            if (StringUtils.isNotEmpty(deployServerCodeMulti)) {
                List<String> deployServerCodeList = new ArrayList<>();
                for (String serverCode : deployServerCodeMulti.split(",")) {
                    serverCode = serverCode.replaceAll("\\s+", "");
                    if (StringUtils.isEmpty(serverCode)) {
                        continue;
                    }
                    deployServerCodeList.add(serverCode);
                }
                /*  비바샘 운영의 경우에는 하나의 서버에 다중으로 배포가 일어남 (VR - 비바샘운영, VW - 웹전시)
                    향후 민간존 쪽은 `웹전시` 같은 서버가 추가 될 때 마다 property에 추가 후 in 조건 처리 하도록 함 */
                if (CollectionUtils.isNotEmpty(deployServerCodeList)) {
                    data.put("deployServerCodeList", deployServerCodeList);
                }
            }

            List<Map<String, Object>> cmsTextbookList = portalPzService.findLcmsTextbookList(data);
            //CBS 배포된 교과서가 1개일 경우 LMS로 교과서 이관
            if (cmsTextbookList.size() == 1) {
                Map<String, Object> cmsTextbookInfo = cmsTextbookList.get(0);
                int textbkId = (int) cmsTextbookInfo.getOrDefault("textbkId",0);
                data.put("textbkId", textbkId);
                Map<String, Object> stdTransInfo = tiosApiMapper.getStdTransInfo(data);
                String srcGrp = MapUtils.getString(stdTransInfo, "srcGrp", "");
                String setsIds = MapUtils.getString(stdTransInfo, "setsIds", "");
                String transYn = MapUtils.getString(stdTransInfo, "transYn", "");

                if ("Y".equals(transYn)) {
                    //교과서 생성
                    portalPzService.createTextBook(cmsTextbookInfo, data);

                    endTime = Instant.now();
                    duration = Duration.between(startTime, endTime);
                    log.info("TiosService TEXTBOOK CREATE duration:{}",  duration.toMillis());

                    //학습데이터 이관
                    startTime = Instant.now();
                    for (int i =0; i<stdtIdList.size(); i++) {
                        String sourceUserId = srcGrp + "-s" + (i+1);
                        String targetUserId = stdtIdList.get(i);

                        //공통영어2인경우.
                        if (textbkId == 1189) {
                            if (i == 1 || i == 2 || i == 3) {
                                sourceUserId = srcGrp + "-s2";
                            }
                            if (i == 4) {
                                sourceUserId = srcGrp + "-s3";
                            }
                        } else {
                            //학생1(s2) / 학생2(s1) / 학생3(s3) / 학생4(s2) / 학생5(s2)
                            if (i==0 || i==3 || i==4) {
                                sourceUserId = srcGrp + "-s2";
                            }
                            if (i==1) {
                                sourceUserId = srcGrp + "-s1";
                            }
                            if (i==2) {
                                sourceUserId = srcGrp + "-s3";
                            }
                        }

                        Map<String, Object> param = new HashMap<>();
                        param.put("sourceUserId", sourceUserId);
                        param.put("targetUserId", targetUserId);
                        param.put("setsId", setsIds);
                        Map<String, Object> stdDtaResultInfo = tiosApiMapper.getStdDtaResultInfo(param);
                        param.put("sourceDtaResultId", MapUtils.getInteger(stdDtaResultInfo, "sourceDtaResultId", 0));
                        param.put("targetTextbkTabId", MapUtils.getInteger(stdDtaResultInfo, "targetTextbkTabId", 0));
                        tiosApiMapper.insertStdDtaResulInfo(param);
                        int dtaResultId = Integer.parseInt(String.valueOf(param.getOrDefault("dtaResultId", "0")));
                        param.put("dtaResultId", dtaResultId);
                        tiosApiMapper.insertStdDtaResulDetail(param);
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
                        int cnt21 = tiosApiMapper.insertEngUsdAchSrc2Info(s);
                        int cnt22 = tiosApiMapper.insertEngUsdAchSrc2Detail(s);
                        int cnt23 = tiosApiMapper.insertEngUsdAchSrc2Kwg(s);
                        int cnt25 = tiosApiMapper.insertAchCacSrcInfo(s);
                    }
                    //수학인경우
                    else {
                        int cnt21 = tiosApiMapper.insertUsdCacSrcInfo(s);
                        int cnt31 = tiosApiMapper.insertStdUsdDayHist(s);
                        int cnt33 = tiosApiMapper.insertStdUsdUnitDayHist(s);
                        int cnt34 = tiosApiMapper.insertStdUsdUnitKwgDayHist(s);
                        int cnt35 = tiosApiMapper.insertStdUsdStdtUnitKwgDayHist(s);
                        int cnt36 = tiosApiMapper.insertStdUsdInfo(s);
                        int cnt37 = tiosApiMapper.insertStdUsdUnitInfo(s);
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
        } catch (DataAccessException e) {
            log.error("Database access error in saveAccountProc: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in saveAccountProc: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in saveAccountProc: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in saveAccountProc: {}", e.getMessage());
            throw e;
        }
        return result;
    }


    public Map<String, Object> saveVivasamAccountProc(Map<String, Object> paramData, Map<String, Object> ptnInfo) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "00000");
        result.put("message", "성공");

        String encAuthCode = MapUtils.getString(paramData, "encAuthCode", "");
        String referUserId = MapUtils.getString(paramData, "referUserId", "");

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
            userInsertMap.put("rgtr", "vivasamAPI");

            Instant startTime = Instant.now();
            Duration duration = Duration.between(startTime, startTime);

            // user 테이블 적재
            tiosApiMapper.insertUser(userInsertMap);

            // tc_reg_info 적재
            tiosApiMapper.insertTcRegInfo(userInsertMap);

            // tc_cla_info 적재
            tiosApiMapper.insertTcClaInfo(userInsertMap);
            
            // tc_cla_user_info 적재
            tiosApiMapper.upsertTcClaUserInfo(userInsertMap);

            // vivasam_user_info 적재
            tiosApiMapper.insertVivasamUserInfo(userInsertMap);

            // sp_prchs_hist 적재
            tiosApiMapper.insertShopSkinHist(userInsertMap);
            tiosApiMapper.insertShopGameHist(userInsertMap);
            tiosApiMapper.insertShopProfileHist(userInsertMap);

            // sp_prchs_info 적재
            tiosApiMapper.insertShopSkin(userInsertMap);
            tiosApiMapper.insertShopGame(userInsertMap);
            tiosApiMapper.insertShopProfile(userInsertMap);

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
                userInsertMap.put("rgtr", "vivasamAPI");
                stdtIdList.add(stdtId);
                userInsertMapList.add(userInsertMap);
            }
            // user 테이블 적재
            tiosApiMapper.insertUserBulk(userInsertMapList);
            // stdt_reg_info 적재
            tiosApiMapper.insertStdtRegInfoBulk(userInsertMapList);
            // tc_cla_mb_info 적재
            tiosApiMapper.insertTcClaMbInfoBulk(userInsertMapList);
            // vivasam_user_info 적재
            tiosApiMapper.insertVivasamUserInfoBulk(userInsertMapList);

            // sp_prchs_hist 적재
            tiosApiMapper.insertShopSkinHistBulk(userInsertMapList);
            tiosApiMapper.insertShopGameHistBulk(userInsertMapList);
            tiosApiMapper.insertShopProfileHistBulk(userInsertMapList);

            // sp_prchs_info 적재
            tiosApiMapper.insertShopSkinBulk(userInsertMapList);
            tiosApiMapper.insertShopGameBulk(userInsertMapList);
            tiosApiMapper.insertShopProfileBulk(userInsertMapList);

            Instant endTime = Instant.now();
            duration = Duration.between(startTime, endTime);
            log.info("VivasamService USER CREATE duration:{}",  duration.toMillis());
        } catch (DataAccessException e) {
            log.error("Database access error in saveVivasamAccountProc: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument in saveVivasamAccountProc: {}", e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Null pointer exception in saveVivasamAccountProc: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in saveVivasamAccountProc: {}", e.getMessage());
            throw e;
        }
        return result;
    }

    public Map<String, Object> saveContentsAndResultForTeacher(Map<String, Object> ptnInfoAndParam) throws Exception {

        List<Map<String, Object>> teacherClassStudentList = null;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", false);

        String userId = MapUtils.getString(ptnInfoAndParam, "userId");
        String deployServerCode = MapUtils.getString(ptnInfoAndParam, "deployServerCode");
        String deployServerCodeMulti = MapUtils.getString(ptnInfoAndParam, "deployServerCodeMulti");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("wrterId", userId);

        Map<String, Object> userClassInfo = tiosApiMapper.selectTeacherClassInfo(paramMap);
        String claId = MapUtils.getString(userClassInfo, "claId");
        paramMap.put("claId", claId);
        teacherClassStudentList = tiosApiMapper.selectTeacherClassStudentList(paramMap);

        if (userClassInfo == null) {
            resultMap.put("resultMessage", "saveContentsAndResultProc - userClassInfo is null");
            return resultMap;
        }

        //교과서 세팅
        Instant startTime = Instant.now();
        Map<String, Object> data = new HashMap<>();
        data.put("claId", claId);
        data.put("wrterId", userId);
        data.put("userId", userId);
        data.put("curriSchool", MapUtils.getString(ptnInfoAndParam, "curriSchool", ""));
        data.put("curriGrade", MapUtils.getString(ptnInfoAndParam, "curriGrade", ""));
        data.put("curriSubject", MapUtils.getString(ptnInfoAndParam, "curriSubject", ""));
        data.put("curriSemester", MapUtils.getString(ptnInfoAndParam, "curriSemester", ""));
        data.put("deployServerCode", deployServerCode);
        data.put("isPublish", "Y");
        /*한 서버에 여러 컨텐츠 배포하는 경우*/
        boolean isMultiServer = false;
        if (StringUtils.isNotEmpty(deployServerCodeMulti)) {
            List<String> deployServerCodeList = new ArrayList<>();
            for (String serverCode : deployServerCodeMulti.split(",")) {
                serverCode = serverCode.replaceAll("\\s+", "");
                if (StringUtils.isEmpty(serverCode)) {
                    continue;
                }
                deployServerCodeList.add(serverCode);
            }
            /*  비바샘 운영의 경우에는 하나의 서버에 다중으로 배포가 일어남 (VR - 비바샘운영, VW - 웹전시)
                향후 민간존 쪽은 `웹전시` 같은 서버가 추가 될 때 마다 property에 추가 후 in 조건 처리 하도록 함 */
            if (CollectionUtils.isNotEmpty(deployServerCodeList)) {
                data.put("deployServerCodeList", deployServerCodeList);
                isMultiServer = true;
            }
        }
        List<Map<String, Object>> cmsTextbookList = portalPzService.findLcmsTextbookList(data);
        if (isMultiServer) {
            data.remove("deployServerCodeList");
        }

        if (CollectionUtils.isEmpty(cmsTextbookList)) {
            resultMap.put("resultMessage", "saveContentsAndResultProc - cmsTextbookList empty");
            return resultMap;
        }

        //CBS 배포된 교과서가 1개일 경우 LMS로 교과서 이관
        if (cmsTextbookList.size() != 1) {
            resultMap.put("resultMessage", "saveContentsAndResultProc - cmsTextbookList size -> " + cmsTextbookList.size());
            return resultMap;
        }

        //CBS 배포된 교과서가 1개일 경우 LMS로 교과서 이관
        Map<String, Object> cmsTextbookInfo = cmsTextbookList.get(0);
        int textbkId = MapUtils.getInteger(cmsTextbookInfo, "textbkId", 0);
        data.put("textbkId", textbkId);
        Map<String, Object> stdTransInfo = tiosApiMapper.getStdTransInfo(data);
        String srcGrp = MapUtils.getString(stdTransInfo, "srcGrp", "");
        String setsIds = MapUtils.getString(stdTransInfo, "setsIds", "");
        String transYn = MapUtils.getString(stdTransInfo, "transYn", "");

        if (StringUtils.equals(transYn, "Y") == false) {
            resultMap.put("resultMessage", "saveContentsAndResultProc [" + stdTransInfo + "] - transYn check -> " + transYn);
            return resultMap;
        }

        // 커리큘럼 정보가 세팅되지 않은 tc_textbook 정보를 조회한다
        List<Map<String, Object>> tcTextbookList = tiosApiMapper.selectTcTextbookListForCurriTabSave(paramMap);
        if (CollectionUtils.isEmpty(tcTextbookList)) {
            //교과서 생성
            portalPzService.createTextBook(cmsTextbookInfo, data);
        } else {
            // 커리큘럼이 세팅되지 않은 tc_textbook 세팅 이후 로직을 따로 호출
            for (Map<String, Object> map : tcTextbookList) {
                String existsCurriYn = MapUtils.getString(map, "existsCurriYn", "N");
                // tc_textbook 목록이 있는 상태에서 커리큘럼과 탭 까지 세팅이 되어 있다면 통과
                if (existsCurriYn.equals("Y")) {
                    continue;
                } else {
                    map.putAll(paramMap);
                    map.putAll(data);
                    portalPzService.createTextBookCurriTab(map);
                }
            }
        }

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("VivasamService TEXTBOOK CREATE duration:{}", duration.toMillis());

        if (CollectionUtils.isEmpty(teacherClassStudentList)) {
            resultMap.put("resultMessage", "saveContentsAndResultProc [" + stdTransInfo + "] - transYn check -> " + transYn);
            return resultMap;
        }

        String resultExistsYn = tiosApiMapper.selectStudentResultExistsYn(teacherClassStudentList);
        if (StringUtils.equals(resultExistsYn, "Y")) {
            resultMap.put("success", true);
            resultMap.put("resultMessage", "resultExistsYn == Y -> saveContentsAndResultProc 필요 없음");
            return resultMap;
        }

        //학습데이터 이관
        startTime = Instant.now();
        for (Map<String, Object> map : teacherClassStudentList) {
            String targetUserId = MapUtils.getString(map, "stdtId");
            if (StringUtils.isEmpty(targetUserId)) {
                continue;
            }
            int i = NumberUtils.toInt(StringUtils.substringAfterLast(targetUserId, "s")); // ✅ 마지막 s 기준
            if (i == 0) {
                continue;
            }
            i = i -1;
            String sourceUserId = srcGrp + "-s" + (i + 1);
            //공통영어2인경우.
            if (textbkId == 1189) {
                if (i == 1 || i == 2 || i == 3) {
                    sourceUserId = srcGrp + "-s2";
                }
                if (i == 4) {
                    sourceUserId = srcGrp + "-s3";
                }
            } else {
                //학생1(s2) / 학생2(s1) / 학생3(s3) / 학생4(s2) / 학생5(s2)
                if (i == 0 || i == 3 || i == 4) {
                    sourceUserId = srcGrp + "-s2";
                }
                if (i == 1) {
                    sourceUserId = srcGrp + "-s1";
                }
                if (i == 2) {
                    sourceUserId = srcGrp + "-s3";
                }
            }

            Map<String, Object> param = new HashMap<>();
            param.put("sourceUserId", sourceUserId);
            param.put("targetUserId", targetUserId);
            param.put("setsId", setsIds);
            Map<String, Object> stdDtaResultInfo = tiosApiMapper.getStdDtaResultInfo(param);
            if (MapUtils.isEmpty(stdDtaResultInfo)) {
                continue;
            }
            param.put("sourceDtaResultId", MapUtils.getInteger(stdDtaResultInfo, "sourceDtaResultId", 0));
            param.put("targetTextbkTabId", MapUtils.getInteger(stdDtaResultInfo, "targetTextbkTabId", 0));
            tiosApiMapper.insertStdDtaResulInfo(param);
            int dtaResultId = Integer.parseInt(String.valueOf(param.getOrDefault("dtaResultId", "0")));
            param.put("dtaResultId", dtaResultId);
            tiosApiMapper.insertStdDtaResulDetail(param);
        }

        endTime = Instant.now();
        duration = Duration.between(startTime, endTime);
        log.info("VivasamService STD TRANS duration:{}", duration.toMillis());

        //배치데이터 생성
        startTime = Instant.now();
        Map<String, Object> s = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
        String now = formatter.format(LocalDate.now());
        s.put("apiYn", "Y");
        s.put("calcDt", now);
        s.put("stdDt", now);
        s.put("textbkId", textbkId);
        s.put("wrterId", userId);
        s.put("claId", claId);

        //영어인경우.
        String curriSubject = MapUtils.getString(ptnInfoAndParam, "curriSubject", "");
        if (StringUtils.equals(curriSubject, "english")) {
            int cnt21 = tiosApiMapper.insertEngUsdAchSrc2Info(s);
            int cnt22 = tiosApiMapper.insertEngUsdAchSrc2Detail(s);
            int cnt23 = tiosApiMapper.insertEngUsdAchSrc2Kwg(s);
            int cnt25 = tiosApiMapper.insertAchCacSrcInfo(s);
        }
        //수학인경우
        else {
            int cnt21 = tiosApiMapper.insertUsdCacSrcInfo(s);
            int cnt31 = tiosApiMapper.insertStdUsdDayHist(s);
            int cnt33 = tiosApiMapper.insertStdUsdUnitDayHist(s);
            int cnt34 = tiosApiMapper.insertStdUsdUnitKwgDayHist(s);
            int cnt35 = tiosApiMapper.insertStdUsdStdtUnitKwgDayHist(s);
            int cnt36 = tiosApiMapper.insertStdUsdInfo(s);
            int cnt37 = tiosApiMapper.insertStdUsdUnitInfo(s);
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
        log.info("VivasamService BATCH CREATE duration:{}", duration.toMillis());

        resultMap.put("success", true);
        resultMap.put("resultMessage", "saveContentsAndResultProc - success");

        return resultMap;
    }
}
