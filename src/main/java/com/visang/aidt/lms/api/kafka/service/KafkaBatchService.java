package com.visang.aidt.lms.api.kafka.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.kafka.mapper.KafkaEngMapper;
import com.visang.aidt.lms.api.kafka.mapper.KafkaMathMapper;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import org.springframework.dao.DataAccessException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaBatchService {
    private static final String MATH_BRAND_ID = "1";
    private static final String ENG_BRAND_ID = "3";

    private static final List<String> MATH_TEXTBK_ID_LIST = List.of("1198","1152","1201","1199","1175","1197","1342","7036","7040","7041","6993","7042");
    private static final List<String> ENG_TEXTBK_ID_LIST = List.of("1150","1184","1189","6979","6981","6982");

    @Value("${lms.api.cdc.skip}")
    private boolean isCdcSkip;

    private final KafkaEngMapper kafkaMapper;
    private final KafkaMathMapper kafkaMapperMath;


    public int processFullCheck(Map<String, Object> paramData) {
        return kafkaMapper.finalTableChk(paramData);
    }

    @Synchronized
    public void finalTableExistsYnUpdate(Map<String, Object> paramData) {
        kafkaMapper.finalTableExistsYnUpdate(paramData);
    }

    public int processFullEngCheck(Map<String, Object> paramData) {
        int total_count = kafkaMapper.finalTableEngChk(paramData);
        return total_count;
    }

    @Synchronized
    public void finalTableExistsYnUpEngdate(Map<String, Object> paramData) {
        kafkaMapper.finalTableExistsYnUpdate_1(paramData);
        kafkaMapper.finalTableExistsYnUpdate_2(paramData);
        kafkaMapper.finalTableExistsYnUpdate_3(paramData);
        kafkaMapper.finalTableExistsYnUpdate_4(paramData);
    }

    public void slfPerDataSetting(Map<String,Object> paramData) throws Exception {
        kafkaMapper.slfPerDataSetting(paramData);
    }



    public Map getTextbook(String paramData) throws Exception {
        Map<String,Object> textbook = kafkaMapper.getTextbook(paramData);
        return textbook;
    }

    public List<Map> getAticleCheck(Map<String, Object> paramData) throws Exception {
        List<Map> articleCheck = kafkaMapper.getAticleCheck(paramData);
        return articleCheck;
    }
    // 계산로직
    public void processFullInsertCycle(Map<String, Object> paramData) throws Exception {
        if(!isCdcSkip) {
            if (paramData.get("textbk_id") != null) {
                // 수학에서 사용하는 교과서 아이디 체크
                if (MATH_TEXTBK_ID_LIST.contains(paramData.get("textbk_id").toString())) {
                    if (MATH_BRAND_ID.equals(paramData.get("brandId").toString())) {
                        processMathInsertCycle(paramData);
                    }
                } else if (ENG_TEXTBK_ID_LIST.contains(paramData.get("textbk_id").toString())) {
                    if (ENG_BRAND_ID.equals(paramData.get("brandId").toString())) {
                        processEngInsertCycle(paramData);
                    }
                } else {
                    log.warn("Unsupported textbkId: {}", paramData.get("brand_id").toString());
                }
            }
        }
    }

    // 계산 로직 단건
    @Transactional
    public void processSelectOneCycle(Map<String, Object> paramData) throws Exception {
        paramData.put("textbk_id", paramData.get("textbookId"));

        String lastDate = kafkaMapper.getLastLearningDate(paramData);
        String brandId = kafkaMapper.getBrandId(paramData);
        paramData.put("stdDt", lastDate);       // 현재 날짜 대신 마지막 학습 일자
        paramData.put("brandId", brandId);

        if (MATH_TEXTBK_ID_LIST.contains(paramData.get("textbk_id").toString())) {
            if (MATH_BRAND_ID.equals(paramData.get("brandId").toString())) {
                int chk = processFullCheck(paramData);
                if (chk > 0) {
                    finalTableExistsYnUpdate(paramData);
                    processFullInsertCycle(paramData);
                }
            }
        } else if (ENG_TEXTBK_ID_LIST.contains(paramData.get("textbk_id").toString())) {
            if (ENG_BRAND_ID.equals(paramData.get("brandId").toString())) {
                int chk = processFullEngCheck(paramData);
                if (chk > 0) {
                    finalTableExistsYnUpEngdate(paramData);
                    processFullInsertCycle(paramData);
                }
            }
        } else {
            log.warn("Unsupported textbkId: {}", paramData.get("brandId").toString());
        }
    }

    //과거데이터 루프
    public void kafkaBatchLoopSetting(Map<String, Object> paramData) throws Exception {
        paramData.put("textbk_id", paramData.get("textbookId"));
        paramData.put("cla_id", paramData.get("claId"));
        String brandId = kafkaMapper.getBrandId(paramData);

        List<Map> selectMvInfo = List.of();
        if (brandId.equals("1")) {
            selectMvInfo = kafkaMapper.selectLoopMvLmsUsdCacSrcInfo(paramData);
            deleteStdUsdTarget(paramData);
        }else{
            selectMvInfo = kafkaMapper.selectLoopMvLmsUsdAchSrc2Info(paramData);
            deleteEngStdUsdTarget(paramData);
        }

        if (selectMvInfo != null && !selectMvInfo.isEmpty()) {
            for (Map param: selectMvInfo) {
                paramData.put("brandId", brandId);
                paramData.put("stdDt", param.get("regDt")); // 각 날짜별로 처리할 작업 수행

                processFullInsertCycle(paramData);
            }
        }
    }


    // 다시풀기
    @Transactional
    public void processContentReset(Map<String, Object> paramData) throws Exception {
        List<Map> tchMdulQstnResetSDRHist = List.of();

        Map<String, Object> inParam = new LinkedHashMap<>(paramData);

        if (paramData.get("trgtSeCd").equals("1")) {
            tchMdulQstnResetSDRHist = kafkaMapper.selectTchMdulQstnResetSDRHist(inParam);
        } else {
            tchMdulQstnResetSDRHist = kafkaMapper.selectEvaluationHistoryRecord(inParam);
        }


        if (!ObjectUtils.isEmpty(tchMdulQstnResetSDRHist)) {
            Map<String, Object> mdulMap = new LinkedHashMap<>();
            for (Map mdul : tchMdulQstnResetSDRHist) {
                if (inParam.get("trgtSeCd").equals("1")) {
                    mdulMap.put("stdtId", paramData.get("stdtId"));
                }
                mdulMap.put("trgtSeCd", inParam.get("trgtSeCd"));
                mdulMap.put("subId", inParam.get("subId"));
                mdulMap.put("claId", mdul.get("claId"));
                mdulMap.put("textbookId", mdul.get("textbookId"));
                mdulMap.put("wrterId", mdul.get("wrterId"));
                mdulMap.put("tabId", mdul.get("tabId"));
                mdulMap.put("trgtId", mdul.get("trgtId"));
                mdulMap.put("articleId", mdul.get("articleId"));
                inParam.put("textbk_id", mdul.get("textbookId"));

                log.debug("processContentReset (다시풀기): {}",inParam);

                String brandId = kafkaMapper.getBrandId(inParam);
                List<Map> selectMvInfo = List.of();

                if (brandId.equals("1")) {
                    mdulMap.put("stdDt", mdul.get("stdDt"));
                    selectMvInfo = kafkaMapper.selectMvLmsUsdCacSrcInfo(mdulMap);

                    if (selectMvInfo.size() > 0) {
                        int result1 = kafkaMapperMath.deleteMvStdUsdTarget_1(mdulMap);
                        if (result1 > 0) log.info("1.deleteMvEngStdUsdTarget_1 완료");

                        int result2 = kafkaMapperMath.deleteStdUsdUnitTarget_2(mdulMap);
                        if (result2 > 0) log.info("2.deleteStdUsdUnitTarget_2 완료");
                        for (Map kwgMain : selectMvInfo) {
                            mdulMap.put("kwgMainId", kwgMain.get("kwgMainId"));

                            int result3 = kafkaMapperMath.deleteStdUsdInfoTarget_3(mdulMap);
                            if (result3 > 0) log.info("3.deleteStdUsdInfoTarget_3 완료");
                        }
                        int result4 = kafkaMapperMath.deleteStdUsdUnitDayHistTarget_4(mdulMap);
                    }

                } else if (brandId.equals("3")) {
                    // 영어는 mv_lms_usd_ach_src2_info 테이블을 기준으로 함
                    selectMvInfo = kafkaMapper.selectMvLmsUsdAchSrc2Info(mdulMap);

                    if (selectMvInfo.size() > 0) {
                        int result1 = kafkaMapper.deleteMvEngStdUsdTarget_1(mdulMap);
                        if (result1 > 0) log.info("1.deleteMvEngStdUsdTarget_1 완료");

                        int result2 = kafkaMapper.deleteMvEngStdUsdTarget_2(mdulMap);
                        if (result2 > 0) log.info("2.deleteMvEngStdUsdTarget_2 완료");

                        int result3 = kafkaMapper.deleteMvEngStdUsdTarget_3(mdulMap);
                        if (result3 > 0) log.info("2.deleteMvEngStdUsdTarget_3 완료");

                        int result4 = kafkaMapper.deleteMvEngStdUsdTarget_4(mdulMap);
                        if (result4 > 0) log.info("3.deleteMvEngStdUsdTarget_4 완료");
                    }
                }

                // 공통 메서드 호출
                generateDateListFromMinDate(selectMvInfo, mdulMap);

                List<String> regDtList = (List<String>) mdulMap.get("regDtList");

                if (regDtList != null && !regDtList.isEmpty()) {
                    for (String stdDt : regDtList) {
                        mdulMap.put("textbk_id", inParam.get("textbk_id"));
                        mdulMap.put("brandId", brandId);
                        mdulMap.put("stdDt", stdDt); // 각 날짜별로 처리할 작업 수행

                        processFullInsertCycle(mdulMap);
                    }
                }
            }
        }
    }

    @Transactional
    public void processEngInsertCycle(Map<String, Object> paramData) {
        String serverInfo = this.getServerInfo();
        paramData.put("serverInfo", serverInfo);
        paramData.put("cla_id", paramData.get("claId"));

        // 이전 배치 삭제
        if(paramData.get("stdDt") != null && !paramData.get("stdDt").equals("")) {
            deleteEngStdUsdTarget(paramData);
        }

        // 영어(3) INSERT 사이클
        try {
            int cnt1 = kafkaMapper.insertEngUsdAchSrc2Info(paramData);
            if (cnt1 > 0) log.info("1.insertEngUsdAchSrc2Info 완료");

            int cnt2 = kafkaMapper.insertEngUsdAchSrc2Detail(paramData);
            if (cnt2 > 0) log.info("2.insertEngUsdAchSrc2Detail 완료");

            int cnt3 = kafkaMapper.insertEngUsdAchSrc2Kwg(paramData);
            if (cnt3 > 0) log.info("3.insertEngUsdAchSrc2Kwg 완료");

            int cnt4 = kafkaMapper.updateEngUsdAchSrc2Info(paramData);
            if (cnt4 > 0) log.info("4.updateEngUsdAchSrc2Info 완료");

            int cnt5 = kafkaMapper.insertAchCacSrcInfo(paramData);
            if (cnt5 > 0) log.info("5.insertAchCacSrcInfo 완료");

        } catch (IllegalArgumentException e) {
            log.error("processEngInsertCycle - 잘못된 파라미터: {}", e.getMessage());
            throw new RuntimeException("잘못된 파라미터로 인한 처리 실패", e);
        } catch (DataAccessException e) {
            log.error("processEngInsertCycle - 데이터베이스 접근 오류: {}", e.getMessage());
            throw new RuntimeException("데이터베이스 접근 오류로 인한 처리 실패", e);
        } catch (Exception e) {
            log.error("processEngInsertCycle - 예상치 못한 오류: {}", e.getMessage());
            throw new RuntimeException("예상치 못한 오류로 인한 처리 실패", e);
        }

    }

    /** 영어 이전 배치 삭제*/
    @Transactional
    public void deleteEngStdUsdTarget(Map<String, Object> paramData) {
        try {
            // 삭제 method 호출 순서 변경(1 <-> 2) - detail 테이블에서 info 테이블의 데이터 조건으로 삭제하기 위함
            int cnt2 = kafkaMapper.deleteEngStdUsdTarget_2(paramData);
            log.debug("deleteEngStdUsdTarget_2 cnt={}", cnt2);
            int cnt1 = kafkaMapper.deleteEngStdUsdTarget_1(paramData);
            log.debug("deleteEngStdUsdTarget_1 cnt={}", cnt1);
            int cnt3 = kafkaMapper.deleteEngStdUsdTarget_3(paramData);
            log.debug("deleteEngStdUsdTarget_3 cnt={}", cnt3);
            int cnt4 = kafkaMapper.deleteEngStdUsdTarget_4(paramData);
            log.debug("deleteEngStdUsdTarget_4 cnt={}", cnt4);
        } catch (IllegalArgumentException e) {
            log.error("processEngInsertCycle - 잘못된 파라미터: {}", e.getMessage());
            throw new RuntimeException("잘못된 파라미터로 인한 처리 실패", e);
        } catch (DataAccessException e) {
            log.error("processEngInsertCycle - 데이터베이스 접근 오류: {}", e.getMessage());
            throw new RuntimeException("데이터베이스 접근 오류로 인한 처리 실패", e);
        } catch (Exception e) {
            log.error("processEngInsertCycle - 예상치 못한 오류: {}", e.getMessage());
            throw new RuntimeException("예상치 못한 오류로 인한 처리 실패", e);
        }
    }

    /** 수학 이전 배치 삭제*/
    @Transactional
    public void deleteStdUsdTarget(Map<String, Object> paramData) throws Exception {

        int cnt1 = kafkaMapperMath.deleteStdUsdTarget_1(paramData);
        log.debug("deleteStdUsdTarget_1 cnt={}", cnt1);
        int cnt2 = kafkaMapperMath.deleteStdUsdTarget_2(paramData);
        log.debug("deleteStdUsdTarget_2 cnt={}", cnt2);
        int cnt3 = kafkaMapperMath.deleteStdUsdTarget_3(paramData);
        log.debug("deleteStdUsdTarget_3 cnt={}", cnt3);
        int cnt4 = kafkaMapperMath.deleteStdUsdTarget_4(paramData);
        log.debug("deleteStdUsdTarget_4 cnt={}", cnt4);
        int cnt5 = kafkaMapperMath.deleteStdUsdTarget_5(paramData);
        log.debug("deleteStdUsdTarget_5 cnt={}", cnt5);
        if(paramData.get("stdDt") != null && !paramData.get("stdDt").equals("")) {
            int cnt6 = kafkaMapperMath.deleteStdUsdTarget_6(paramData);
            log.debug("deleteStdUsdTarget_6 cnt={}", cnt6);
        }
        int cnt7 = kafkaMapperMath.deleteStdUsdTarget_7(paramData);
        log.debug("deleteStdUsdTarget_7 cnt={}", cnt7);
        int cnt8 = kafkaMapperMath.deleteStdUsdTarget_8(paramData);
        log.debug("deleteStdUsdTarget_7 cnt={}", cnt8);
    }


    @Transactional
    public void processMathInsertCycle(Map<String, Object> paramData) {
        String serverInfo = this.getServerInfo();
        paramData.put("cla_id", paramData.get("claId"));
        paramData.put("serverInfo", serverInfo);
        log.info("수학 INSERT 사이클 시작.");
        try {
            // 이전 배치 삭제
            if(paramData.get("stdDt") != null && !paramData.get("stdDt").equals("")) {
                deleteStdUsdTarget(paramData);
            }

            kafkaMapperMath.insertUsdCacSrcInfo(paramData);
            kafkaMapperMath.insertStdUsdDayHist(paramData);
            kafkaMapperMath.insertStdUsdNotStdtUnitDayHist(paramData);
            kafkaMapperMath.insertStdUsdUnitDayHist(paramData);
            kafkaMapperMath.insertStdUsdUnitKwgDayHist(paramData);
            kafkaMapperMath.insertStdUsdStdtUnitKwgDayHist(paramData);
            kafkaMapperMath.insertStdUsdInfo(paramData);
            kafkaMapperMath.insertStdUsdUnitInfo(paramData);
            kafkaMapperMath.insertStdUsdTotalHist(paramData);
            kafkaMapperMath.insertStdUsdContentAreaHist(paramData);
            log.info("수학 INSERT 사이클 완료.");
        } catch (IllegalArgumentException e) {
            log.error("processMathInsertCycle - 잘못된 파라미터: {}", e.getMessage());
            throw new RuntimeException("잘못된 파라미터로 인한 처리 실패", e);
        } catch (DataAccessException e) {
            log.error("processMathInsertCycle - 데이터베이스 접근 오류: {}", e.getMessage());
            throw new RuntimeException("데이터베이스 접근 오류로 인한 처리 실패", e);
        } catch (Exception e) {
            log.error("processMathInsertCycle - 예상치 못한 오류: {}", e.getMessage());
            throw new RuntimeException("예상치 못한 오류로 인한 처리 실패", e);
        }
    }

    private Map<String, Object> convertJsonToMap(JsonNode data) {
        return new ObjectMapper().convertValue(data, Map.class);
    }

    private String getServerInfo() {
        String hostName ="Empty";
        String hostAddress = "00";

        try {
            InetAddress localHost = InetAddress.getLocalHost();

            // 호스트 이름 가져오기
            hostName = localHost.getHostName();

            // IP 주소 가져오기
            hostAddress = localHost.getHostAddress();
        } catch (UnknownHostException ue) {
            log.info("UnknownHostException ERR");
        }
        log.warn("Server Name: {}", hostName);
        log.warn("Server IP: {}", hostAddress);

        // 서버명이 길어서 36자를 초과할 수 있을것 같아서 일단 IP만 처리
        StringBuffer serverInfo = new StringBuffer();
        //serverInfo.append(hostName).append("(").append(hostAddress).append(")");
        serverInfo.append(hostAddress);

        return serverInfo.toString();
    }

    /**
     * 최소 날짜부터 현재까지의 날짜 목록을 생성하는 공통 메서드
     * @param selectMvInfo 쿼리 결과 목록
     * @param mdulMap 파라미터 맵
     */
    private void generateDateListFromMinDate(List<Map> selectMvInfo, Map<String, Object> mdulMap) {
        if (selectMvInfo != null && !selectMvInfo.isEmpty()) {
            // 최소 날짜 찾기
            Date minDate = null;
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd"); // DB 날짜 형식에 맞게 조정

            for (Map row : selectMvInfo) {
                if (row.containsKey("regDt")) {
                    String regDtStr = String.valueOf(row.get("regDt"));
                    try {
                        Date regDt = inputFormat.parse(regDtStr);
                        if (minDate == null || regDt.before(minDate)) {
                            minDate = regDt;
                        }
                    } catch (ParseException e) {
                        // 날짜 파싱 오류 처리
                        log.error("날짜 형식 오류: " + regDtStr, e);
                    }
                }
            }

            // 최소 날짜부터 현재까지의 날짜 목록 생성
            if (minDate != null) {
                List<String> regDtList = new ArrayList<>();
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd"); // 원하는 출력 형식

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(minDate);

                Date currentDate = new Date(); // 현재 날짜

                while (!calendar.getTime().after(currentDate)) {
                    regDtList.add(outputFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, 1); // 하루씩 증가
                }

                mdulMap.put("regDtList", regDtList);
            }
        }
    }
}