package com.visang.aidt.lms.api.learning.service;

import com.visang.aidt.lms.api.learning.mapper.StdUsdCalculateMapper;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class StdUsdCalculateService {
    private final StdUsdCalculateMapper stdUsdCalculateMapper;

    @Transactional(readOnly = true)
    // 교과서Id 조회
    public List<Long> findStdUsdTextbkTargetList(Map<String, Object> paramData) throws Exception {
        return CollectionUtils.emptyIfNull(
                stdUsdCalculateMapper.findStdUsdTextbkTargetList(paramData)
            ).stream().map(s -> MapUtils.getLong(s, "textbkId")).toList();
    }

    public Map<String, Object> createStdUsdCalculate(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("btchExcnRsltCnt", 0);
        returnMap.put("resultOk", true);
        int count = 0;

        try {
            String serverInfo = this.getServerInfo();
            log.info("0-createStdUsdCalculate={}", serverInfo);

            /*---------------------------------------------------------------*/
            // 1. 데이터가 많을 수 있어서 일단 교과서/교사/학급 단위로 처리
            /*---------------------------------------------------------------*/
            // 교과서 / 교사 / 학급별 정보 조회
            List<Map<String, Object>> targetList = stdUsdCalculateMapper.findStdUsdTargetList(paramData);
            log.info("1-findStdUsdTargetList={}", targetList.size());

            /*---------------------------------------------------------------*/
            // 2. 1에서 구한 목록만큼 오늘날짜의 이해도 계산용 원천정보(usd_cac_src_info) 수집
            /*---------------------------------------------------------------*/
//            targetList.forEach(s -> {
//                // [1].2024-03-28_이해도 계산용 원천 정보(usd_cac_src_info)_교과서_교사_학급기준_등록 쿼리 정리.txt 실행
//                s.put("stdDt", paramData.get("stdDt"));
//                int cnt = 0;
//                try {
//                    cnt = stdUsdCalculateMapper.insertUsdCacSrcInfo(s);
//                } catch (Exception e) {
//                    log.error(CustomLokiLog.errorLog(e));
//                }
//                log.info("2-insertUsdCacSrcInfo={}", cnt);
//            });

            //cnt를 위해서 새로 만듬 20240527+
            int cnt = 0;
            for(Map<String, Object> temp : targetList) {
                // [1].2024-03-28_이해도 계산용 원천 정보(usd_cac_src_info)_교과서_교사_학급기준_등록 쿼리 정리.txt 실행
                temp.put("stdDt", paramData.get("stdDt"));
                temp.put("serverInfo", serverInfo);

                /* 20240803 추가 - api 호출 형태일 경우 parameter로 받은 날짜 까지 누적 값으로 처리 (테스트시나리오용) */
                if (MapUtils.getString(paramData, "apiYn", "N").equals("Y")) {
                    temp.put("apiYn", paramData.get("apiYn"));
                    temp.put("calcDt", paramData.get("calcDt"));
                }

                cnt = stdUsdCalculateMapper.insertUsdCacSrcInfo(temp);
                count += cnt;
                log.info("2-insertUsdCacSrcInfo={}", cnt);
            }

            /*---------------------------------------------------------------*/
            // 3. 학습 이해도 관련 테이블 등록 및 수정
            //    - 2에서 모든 계산용 원천정보를 수집한 이후 아래의 순서대로 처리
            //    - 3.1~3.4의 where 조건의 학습일자(std_dt)값은 [공통]에서 구해놓은 날짜로 처리
            // (30분 간격으로 처리하다 보니 혹시라도 날짜가 바뀔 가능성이 있어서 미리 구해놓고 처리)
            /*---------------------------------------------------------------*/
            // 3.1 [2].2024-03-21_학습이해도 일별 이력(std_usd_day_hist) 등록 쿼리 정리.txt
            int cnt31 = stdUsdCalculateMapper.insertStdUsdDayHist(paramData);
            log.info("3.1-insertStdUsdDayHist={}", cnt31);

            // 3.2 [3].2024-10-30_학습이해도 단원별 일별 이력(std_usd_notstdt_unit_day_hist) 등록 쿼리 정리.txt
            int cnt32 = stdUsdCalculateMapper.insertStdUsdNotStdtUnitDayHist(paramData);
            log.info("3.2-insertStdUsdNotStdtUnitDayHist={}", cnt32);

            // 3.3 [4].2024-03-21_학습이해도 단원별 일별 학생 이력(std_usd_unit_day_hist) 등록 쿼리 정리.txt
            int cnt33 = stdUsdCalculateMapper.insertStdUsdUnitDayHist(paramData);
            log.info("3.3-insertStdUsdUnitDayHist={}", cnt33);

            // 3.4 [5].2024-03-21_학습이해도단원별 지식요인이력(std_usd_unit_kwg_day_hist) 등록 쿼리 정리.txt
            int cnt34 = stdUsdCalculateMapper.insertStdUsdUnitKwgDayHist(paramData);
            log.info("3.4-insertStdUsdUnitKwgDayHist={}", cnt34);

            // 3.5 [6].2024-03-21_학습이해도 단원별 지식요인별 일별 이력(std_usd_stdt_unit_kwg_day_hist) 등록 쿼리 정리.txt
            int cnt35 = stdUsdCalculateMapper.insertStdUsdStdtUnitKwgDayHist(paramData);
            log.info("3.5-insertStdUsdStdtUnitKwgDayHist={}", cnt35);

            // 3.6 [7].2024-03-21_학습이해도정보(std_usd_info) 등록 쿼리 정리.txt
            int cnt36 = stdUsdCalculateMapper.insertStdUsdInfo(paramData);
            log.info("3.6-insertStdUsdInfo={}", cnt36);

            // 3.7 [8].2024-03-21_학습이해도 단원별 정보(std_usd_unit_info) 등록 쿼리 정리.txt
            int cnt37 = stdUsdCalculateMapper.insertStdUsdUnitInfo(paramData);
            log.info("3.7-insertStdUsdUnitInfo={}", cnt37);

            returnMap.put("btchExcnRsltCnt", count+cnt31+cnt32+cnt33+cnt34+cnt35+cnt36+cnt37);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));
            throw e;
        }

        log.info("StdUsdCalculateBatchJob > createStdUsdCalculate() > END");
        return returnMap;

    }

    public void deleteStdUsdTarget(Map<String, Object> paramData) throws Exception {
        int cnt1 = stdUsdCalculateMapper.deleteStdUsdTarget_1(paramData);
        log.debug("deleteStdUsdTarget_1 cnt={}", cnt1);
        int cnt2 = stdUsdCalculateMapper.deleteStdUsdTarget_2(paramData);
        log.debug("deleteStdUsdTarget_2 cnt={}", cnt2);
        int cnt3 = stdUsdCalculateMapper.deleteStdUsdTarget_3(paramData);
        log.debug("deleteStdUsdTarget_3 cnt={}", cnt3);
        int cnt4 = stdUsdCalculateMapper.deleteStdUsdTarget_4(paramData);
        log.debug("deleteStdUsdTarget_4 cnt={}", cnt4);
        int cnt5 = stdUsdCalculateMapper.deleteStdUsdTarget_5(paramData);
        log.debug("deleteStdUsdTarget_5 cnt={}", cnt5);
        int cnt6 = stdUsdCalculateMapper.deleteStdUsdTarget_6(paramData);
        log.debug("deleteStdUsdTarget_6 cnt={}", cnt6);
    }

    /**
     * 영어 성취도
     */
    public Map<String, Object> createEngStdUsdCalculate(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("btchExcnRsltCnt", 0);
        returnMap.put("resultOk", true);
        int count = 0;

        try {
            String serverInfo = this.getServerInfo();
            log.info("0-createEngStdUsdCalculate={}", serverInfo);

            /*---------------------------------------------------------------*/
            // 1. 데이터가 많을 수 있어서 일단 교과서/교사/학급 단위로 처리
            /*---------------------------------------------------------------*/
            // 교과서 / 교사 / 학급별 정보 조회
            List<Map<String, Object>> targetList = stdUsdCalculateMapper.findStdUsdTargetList(paramData);
            log.info("1-findStdUsdTargetList={}", targetList.size());

            /*---------------------------------------------------------------*/
            // 2. 1에서 구한 목록만큼 오늘날짜의 성취도 계산용 원천정보(usd_ach_src2_info / usd_ach_src2_kwg) 수집
            /*---------------------------------------------------------------*/
            for (Map<String, Object> s : targetList) {
                s.put("stdDt", paramData.get("stdDt"));
                s.put("serverInfo", serverInfo);

                // [1].2024-04-18_성취도도 계산용 원천 정보(usd_ach_src2_info)_교과서_교사_학급기준_등록 쿼리 정리.txt 실행
                int cnt21 = stdUsdCalculateMapper.insertEngUsdAchSrc2Info(s);
                log.info("2.1-insertEngUsdAchSrc2Info={}", cnt21);
                count += cnt21;

                // [2].2024-04-18_성취도도 계산용 원천 정보(usd_ach_src2_detail)_교과서_교사_학급기준_등록 쿼리 정리.txt 실행
                int cnt22 = stdUsdCalculateMapper.insertEngUsdAchSrc2Detail(s);
                log.info("2.2-insertEngUsdAchSrc2Detail={}", cnt22);
                count += cnt22;

                // [3].2024-04-18_성취도도 계산용 원천 정보(usd_ach_src2_kwg)_교과서_교사_학급기준_등록 쿼리 정리.txt 실행
                int cnt23 = stdUsdCalculateMapper.insertEngUsdAchSrc2Kwg(s);
                log.info("2.3-insertEngUsdAchSrc2Kwg={}", cnt23);
                count += cnt23;

                // [4].2024-04-18_성취도도 계산용 원천 정보(usd_ach_src2_info)_교과서_교사_학급기준_등록 쿼리 정리.txt 실행
                int cnt24 = stdUsdCalculateMapper.updateEngUsdAchSrc2Info(s);
                log.info("2.4-updateEngUsdAchSrc2Info={}", cnt24);
                count += cnt24;

                // [5]
                int cnt25 = stdUsdCalculateMapper.insertAchCacSrcInfo(s);
                log.info("2.4-ach_cac_src_info={}", cnt25);
                count += cnt25;

                // [6].데일리 성취도 수행
                int cnt26 = stdUsdCalculateMapper.insertEngUsdAchSrc2InfoDaily(s);
                log.info("2.1-insertEngUsdAchSrc2InfoDaily={}", cnt26);
                count += cnt26;

                // [7].데일리 성취도 수행 detail
                int cnt27 = stdUsdCalculateMapper.insertEngUsdAchSrc2DetailDaily(s);
                log.info("2.2-insertEngUsdAchSrc2DetailDaily={}", cnt27);
                count += cnt27;

                // [8].데일리 성취도 수행 kwg
                int cnt28 = stdUsdCalculateMapper.insertEngUsdAchSrc2KwgDaily(s);
                log.info("2.3-insertEngUsdAchSrc2KwgDaily={}", cnt28);
                count += cnt28;

                // [9].데일리 성취도 업데이트 info
                int cnt29 = stdUsdCalculateMapper.updateEngUsdAchSrc2InfoDaily(s);
                log.info("2.4-updateEngUsdAchSrc2InfoDaily={}", cnt29);
                count += cnt29;

            }

            returnMap.put("btchExcnRsltCnt", count);
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("failDc", CustomLokiLog.shortErrorLog(e, 0, 100));
            throw e;
        }

        return returnMap;
    }
    
    public void deleteEngStdUsdTarget(Map<String, Object> paramData) throws Exception {
        // 삭제 method 호출 순서 변경(1 <-> 2) - detail 테이블에서 info 테이블의 데이터 조건으로 삭제하기 위함 (by 이정훈)
        int cnt2 = stdUsdCalculateMapper.deleteEngStdUsdTarget_2(paramData);
        log.debug("deleteEngStdUsdTarget_2 cnt={}", cnt2);
        int cnt1 = stdUsdCalculateMapper.deleteEngStdUsdTarget_1(paramData);
        log.debug("deleteEngStdUsdTarget_1 cnt={}", cnt1);
        int cnt3 = stdUsdCalculateMapper.deleteEngStdUsdTarget_3(paramData);
        log.debug("deleteEngStdUsdTarget_3 cnt={}", cnt3);
        int cnt4 = stdUsdCalculateMapper.deleteEngStdUsdTarget_4(paramData);
        log.debug("deleteEngStdUsdTarget_4 cnt={}", cnt4);
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
}
