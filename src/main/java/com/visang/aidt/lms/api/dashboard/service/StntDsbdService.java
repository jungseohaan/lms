package com.visang.aidt.lms.api.dashboard.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.dashboard.mapper.StntDsbdMapper;
import com.visang.aidt.lms.api.dashboard.mapper.TchDsbdMapper;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnAitutorMapper;
import com.visang.aidt.lms.api.user.mapper.UserMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.user.service.TchRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntDsbdService {
    private final StntDsbdMapper stntDsbdMapper;
    private final AiLearningMapper aiLearningMapper;
    private final UserMapper userMapper;


    private final StntRewardService stntRewardService;
    private final TchDsbdMapper tchDsbdMapper;
    private final TchRewardService tchRewardService;

    private final StntSelfLrnAitutorMapper stntSelfLrnAitutorMapper;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    /**
     * (학생).종합리포트
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getStntDsbdReportTotal(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //풀어본 총 문항
        returnMap.put("totStd", stntDsbdMapper.selectStntDsbdReportTotStd(paramData).get("cnt"));
        //한 학기 최종 평균 이해도
        returnMap.put("undstn", stntDsbdMapper.findStntDsbdReportTotalUndstn(paramData).get("undstn"));
        //한 학기 최종 평균 정답률
        if("tch".equals(paramData.get("svc_call_type"))) {
            returnMap.put("crrRate",stntDsbdMapper.selectStntDsbdReportCrrRateForTch(paramData).get("crrRate"));
        } else {
            returnMap.put("crrRate",stntDsbdMapper.selectStntDsbdReportCrrRate(paramData).get("crrRate"));
        }
        //한 학기 총 리워드
        Map<String, Object> inputParam = new HashMap<>();

        inputParam.put("userId", paramData.get("stntId"));
        inputParam.put("claId", paramData.get("claId"));
        Map<String, Object> resultMap = tchRewardService.findStntRewardStatus(inputParam);
        if(MapUtils.isEmpty(resultMap)) {
            returnMap.put("earnReward", 0);
        } else {
            returnMap.put("earnReward", resultMap.get("smtHtEarnGramt"));
        }

        //학습종합정보 list
        List<String> stdTotItem = Arrays.asList("unitName", "slfStdSmry", "currUndstn");
        List<LinkedHashMap<Object, Object>> stdTotList = AidtCommonUtil.filterToList(stdTotItem, stntDsbdMapper.findStntDsbdReportStdTotList(paramData));
        returnMap.put("stdTotList", stdTotList);
        //평가종합정보 list
        List<String> evlTotItem = Arrays.asList("evlId", "eamMth", "eamMthNm", "evlNm", "avgTime", "score", "evlResult", "submAt", "genrvw");
        List<LinkedHashMap<Object, Object>> evlTotList = null;
        if("tch".equals(paramData.get("svc_call_type"))) {
            evlTotList = AidtCommonUtil.filterToList(evlTotItem, stntDsbdMapper.findStntDsbdReportEvlTotListForTch(paramData));
        } else {
            evlTotList = AidtCommonUtil.filterToList(evlTotItem, stntDsbdMapper.findStntDsbdReportEvlTotList(paramData));
        }
        returnMap.put("evlTotList", evlTotList);
        //과제정합정보 list
        List<String> taskTotItem = Arrays.asList("taskId", "eamMth", "eamMthNm", "taskNm", "avgTime", "submAt", "evlResultCd", "genrvw");
        List<LinkedHashMap<Object, Object>> taskTotList = AidtCommonUtil.filterToList(taskTotItem, stntDsbdMapper.findStntDsbdReportTaskTotList(paramData));
        returnMap.put("taskTotList", taskTotList);

        return returnMap;
    }

    public Map<String, Object> findStntDsbdClassinfo(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdWeekinfo(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdNtcnClass(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdSchedule(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdUnderstandChapter(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdUnderstandConcept(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdKnowledgemap(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdUnderstandDomain(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdStatusEval(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdSelflearning(Map<String, Object> paramData) throws Exception {
        return null;
    }


    public Map<String, Object> findStntDsbdSelflearningDetail(Map<String, Object> paramData) throws Exception {
        return null;
    }

    // 자기주도학습현황
    @Transactional(readOnly = true)
    public Object findStntDsbdStatusSelflrnChapterList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 학기 총 리워드, 당월 총 리워드
        LinkedHashMap<Object, Object> rewardStatus = (LinkedHashMap<Object, Object>) stntRewardService.findRewardStatus(paramData);

        // 총학습 정보
        Map totalStdInfo = stntDsbdMapper.findTotalStdCntInfo(paramData);

        // 월학습 정보
        List<Map> mnthInfo = stntDsbdMapper.findMnthInfo(paramData);

        // 학습 횟수
        returnMap.put("totalStdCnt", totalStdInfo.get("totalStdCnt"));
        returnMap.put("mnthStdCnt", mnthInfo.size() > 0 ? mnthInfo.size() : 0);
        paramData.put("mnthStdCnt", returnMap.get("mnthStdCnt"));
        // 리워드 획득(학기)
        returnMap.put("earnSmstRwd", rewardStatus.get("smtHtEarnGramt"));
        // 리워드 획득(월)
        returnMap.put("earnMnthRwd", rewardStatus.get("monHtEarnGramt"));
        // 리워드 획득(총)
        returnMap.put("htBlnc", rewardStatus.get("htBlnc"));
        // 학생명
        returnMap.put("flnm", MapUtils.getString(rewardStatus, "flnm"));
        // 학습시간
        String avgTime = MapUtils.getString(stntDsbdMapper.findAvgTime(paramData), "avgTime");
        if (StringUtils.isNotBlank(avgTime) && !StringUtils.equals(avgTime, "0")) {
            String hms[] = avgTime.split(":");
            avgTime = (!StringUtils.equals(hms[0], "00")) ? Integer.valueOf(hms[0]) + "시간 " : "";
            avgTime += (!StringUtils.equals(hms[1], "00")) ? Integer.valueOf(hms[1]) + "분 " : "";
            avgTime += (!StringUtils.equals(hms[2], "00")) ? Integer.valueOf(hms[2]) + "초" : "";
        } else {
            avgTime = "";
        }

        returnMap.put("avgTime", avgTime);
        // 주학습평균횟수
        returnMap.put("avgWeekStdCnt", stntDsbdMapper.findAvgWeekStdCnt(paramData).get("avgWeekStdCnt"));
        paramData.remove("mnthStdCnt");
        // 월학습 정보
        returnMap.put("mnthList", mnthInfo);
        // 단원정보
        // 단원학습 정보를 불러온다
        List<Map> unitInfo = tchDsbdMapper.findUnitInfo(paramData);

        // 브랜드 아이디 추출
        int brandId = aiLearningMapper.findBrandId(paramData);

        // 학습분석정보
        if (!unitInfo.isEmpty()) {
            for (Map entity : unitInfo) {
                paramData.put("metaId", entity.get("metaId"));

                Map<String, Object> data = new LinkedHashMap<>();
                if (brandId == 1) {
                     data = stntDsbdMapper.findStdAnalyInfo(paramData);
                } else if (brandId == 3) {
                    paramData.put("unitNum", entity.get("unitNum"));
                    data = stntDsbdMapper.findStdAnalyInfoForEng(paramData);
                } else {
                    entity.put("stdAnalyList", Map.of("worstStd", "-", "bsetStd", "-"));
                }

                if (MapUtils.isEmpty(data)) data = Map.of("worstStd", "-", "bsetStd", "-");

                entity.put("stdAnalyList", data);

                paramData.remove("unitNum");
            }
        }
        returnMap.put("unitList", unitInfo);

        return returnMap;
    }

    // 자기주도학습 현황(상세)
    @Transactional(readOnly = true)
    public Object findStntDsbdStatusSelflrnChapterDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {

        long total = 0;
        List<String> stdDetItem = Arrays.asList("unitNum", "stdNm", "stdTime");

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> stdInfoList = stntDsbdMapper.findStntDsbdStatusSelflrnChapterDetailList(pagingParam);

        if (!stdInfoList.isEmpty()) {
            total = (long) stdInfoList.get(0).get("fullCount");
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(stdInfoList, pageable, total);

        stdInfoList.forEach(s -> {
            s.remove("fullCount");
        });

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        returnMap.put("stdList", AidtCommonUtil.filterToList(stdDetItem, stdInfoList));
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * 영역별 이해도
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getStntDsbdStatusAreausdList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "areaId", "areaNm"
        );
        List<String> listItem2 = Arrays.asList(
                "areaId", "areaNm", "usdScr"
        );

        List<LinkedHashMap<Object, Object>> contAreaList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStatusAreausdContAreaList(paramData));
        List<LinkedHashMap<Object, Object>> areaUsdList = AidtCommonUtil.filterToList(listItem2, stntDsbdMapper.selectStntDsbdStatusAreausdAreaUsdList(paramData));

        returnMap.put("contAreaList", contAreaList);
        returnMap.put("areaUsdList", areaUsdList);
        return returnMap;
    }

    /**
     * 영역별 이해도 상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getStntDsbdStatusAreausdDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        long total = 0;
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        //영역 명
        Map<Object, Object> areaNm = stntDsbdMapper.selectAreaName(paramData);
        if(MapUtils.isNotEmpty(areaNm)) {
            returnMap.put("areaNm", MapUtils.getString(areaNm, "areaNm"));
        } else {
            returnMap.put("areaNm", "");
        }

        List<String> itemList = Arrays.asList("rowNo","stdDt", "trgtSeCd","trgtNm", "rpOthbcAt");
        List<Map> resultList = stntDsbdMapper.selectStntDsbdStatusAreausdDetail(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> usdSrcList = AidtCommonUtil.filterToList(itemList, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("usdSrcList", usdSrcList);
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntDsbdStatusHomewkList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        List<String> taskItem = Arrays.asList("id", "taskNm", "taskPrgDt", "taskCpDt", "taskSttsCd", "taskSttsNm", "submAt", "rptOthbcAt");

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> taskList = stntDsbdMapper.findStntDsbdStatusHomewkList(pagingParam);

        if (!taskList.isEmpty()) {
            total = (long) taskList.get(0).get("fullCount");
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(taskList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        returnMap.put("taskList", AidtCommonUtil.filterToList(taskItem, taskList));
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntDsbdStatusEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        List<String> evalItem = Arrays.asList("id", "evlNm", "evlPrgDt", "evlCpDt", "evlSttsCd", "evlSttsNm", "submAt", "rptOthbcAt");

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> evalList = stntDsbdMapper.findStntDsbdStatusEvalList(pagingParam);

        if (!evalList.isEmpty()) {
            total = (long) evalList.get(0).get("fullCount");
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(evalList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        returnMap.put("evalList", AidtCommonUtil.filterToList(evalItem, evalList));
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntDsbdStatusChapterunitList(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        // 학습여부 (실제 이해도 점수로 체크)
        AtomicBoolean isStudy = new AtomicBoolean(false);

        List<Map> chptUnitList = stntDsbdMapper.findStntDsbdStatusChapterunitList(paramData);
        if(!chptUnitList.isEmpty()) {
            CollectionUtils.emptyIfNull(chptUnitList)
                    .stream()
                    .filter(m -> (MapUtils.getIntValue(m,"isStudy") > 0))
                    .forEach(m -> {
                        isStudy.set(true);
                    });

            // 가장 높은 이해도 단원과 가장 낮은 이해도 단원 찾기
            Optional<Map> highestUnitOpt = chptUnitList.stream()
                    .filter(m -> MapUtils.getIntValue(m, "isStudy", 0) > 0)
                    .max(Comparator.comparingDouble(m -> MapUtils.getDoubleValue(m, "currUsdScr", 0)));

            Optional<Map> lowestUnitOpt = chptUnitList.stream()
                    .filter(m -> MapUtils.getIntValue(m, "isStudy", 0) > 0)
                    .min(Comparator.comparingDouble(m -> MapUtils.getDoubleValue(m, "currUsdScr", 0)));

            // 학생명(flNm)과 최고/최저 단원명만 추출하여 추가
            returnMap.put("flNm", chptUnitList.get(0).get("stntNm"));
            returnMap.put("highestUnitNm", highestUnitOpt.map(m -> m.get("unitNm")).orElse(null));

            if(!highestUnitOpt.equals(lowestUnitOpt)) {
                returnMap.put("lowestUnitNm", lowestUnitOpt.map(m -> m.get("unitNm")).orElse(null));
            }

            for (Map map : chptUnitList) {
                // currUsdScr 반올림
                if (map.containsKey("currUsdScr")) {
                    double currUsdScr = (double) map.get("currUsdScr");
                    int roundedValue = (int) Math.round(currUsdScr);
                    map.put("currUsdScr", roundedValue);
                }

                // diffUsdScr 반올림
                if (map.containsKey("diffUsdScr")) {
                    double diffUsdScr = (double) map.get("diffUsdScr");
                    int roundedValue = (int) Math.round(diffUsdScr);
                    map.put("diffUsdScr", roundedValue);
                }
            }

        }

        returnMap.put("isStudy", isStudy);
        returnMap.put("chptUnitList", chptUnitList);

        return returnMap;
    }

    // 단원별 이해도 상세
    @Transactional(readOnly = true)
    public Object findStntDsbdStatusChapterunitDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {

        String stdDt = (String) paramData.get("stdDt");
        if (stdDt != null && stdDt.length() == 8) {
            LocalDate date = LocalDate.parse(stdDt, DateTimeFormatter.ofPattern("yyyyMMdd"));
            paramData.put("stdDt", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        var returnMap = new LinkedHashMap<>();

        long total = 0;
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        //단원 명
        Map<Object, Object> unitNm = stntDsbdMapper.selectUnitName(paramData);
        returnMap.put("unitNm", MapUtils.getString(unitNm, "unitNm"));

        List<String> usdSrcInfoItem = Arrays.asList("rowNo", "stdDt", "trgtSeCd", "trgtSeNm", "tabId", "trgtNm", "rpOthbcAt");
        List<Map> resultList = stntDsbdMapper.selectStntDsbdStatusChapterunitDetail(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> usdSrcList = AidtCommonUtil.filterToList(usdSrcInfoItem, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("usdSrcList", usdSrcList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [학생] 학습관리 > AI튜터 - AI튜터 챗봇 내용 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findAitutorResult(Map<String, Object> paramData) throws Exception {

        Map<String, Object> rtnMap = new LinkedHashMap<>();

        int learningType = MapUtils.getInteger(paramData, "learningType", 1);

        List<List<Map<String, Object>>> chatInfoList = new ArrayList<>();

        int aitutorUseCnt = 0;
        // getChatInfoList 메소드 내부에서 aitutorUseCnt를 계산하여 paramData에 할당
        // learningType > 1 : 전체, 2 : 평가, 3 : 과제, 4 : 자기주도 학습
        if (learningType == 1) {
            // 숙제
            List<Map<String, Object>> reList1 = getChatInfoList(3, paramData);
            if (CollectionUtils.isNotEmpty(reList1)) {
                aitutorUseCnt += MapUtils.getInteger(paramData, "aitutorUseCnt", 0);
                chatInfoList.add(reList1);
            }
            // 자기주도학습
            List<Map<String, Object>> reList2 = getChatInfoList(4, paramData);
            if (CollectionUtils.isNotEmpty(reList2)) {
                aitutorUseCnt += MapUtils.getInteger(paramData, "aitutorUseCnt", 0);
                chatInfoList.add(reList2);
            }
            // 평가
            List<Map<String, Object>> reList3 = getChatInfoList(2, paramData);
            if (CollectionUtils.isNotEmpty(reList3)) {
                aitutorUseCnt += MapUtils.getInteger(paramData, "aitutorUseCnt", 0);
                chatInfoList.add(reList3);
            }

        } else {
            List<Map<String, Object>> reList = getChatInfoList(learningType, paramData);
            if (CollectionUtils.isNotEmpty(reList)) {
                aitutorUseCnt += MapUtils.getInteger(paramData, "aitutorUseCnt", 0);
                chatInfoList.add(reList);
            }
        }

        rtnMap.put("aitutorUseCnt", aitutorUseCnt);
        rtnMap.put("chatInfoList", chatInfoList);

        return rtnMap;
    }

//    @Transactional(readOnly = true)
    private List<Map<String, Object>> getChatInfoList(int learningType, Map<String, Object> paramData) throws Exception {

        Integer aitutorUseCnt = null;
        List<Map<String, Object>> list = null;
        if (learningType == 2) {
            aitutorUseCnt = stntDsbdMapper.selectEvlAitutorCount(paramData);
            list = stntDsbdMapper.selectEvlAitutorList(paramData);
        } else if (learningType  == 3) {
            aitutorUseCnt = stntDsbdMapper.selectTaskAitutorCount(paramData);
            list = stntDsbdMapper.selectTaskAitutorList(paramData);
        } else if (learningType  == 4) {
            aitutorUseCnt = stntDsbdMapper.selectSlfStdAitutorCount(paramData);
            list = stntDsbdMapper.selectSlfStdAitutorList(paramData);
        } else {
            return null;
        }

        List<Map<String, Object>> reList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Map<String, Object> aiTutChtCnByDate = new LinkedHashMap<>();
            aiTutChtCnByDate.put("id", map.get("resultId"));
            aiTutChtCnByDate.put("stdDt", map.get("stdDt"));
            aiTutChtCnByDate.put("stdNm", map.get("stdNm"));
            aiTutChtCnByDate.put("articleId",map.get("articleId"));
            aiTutChtCnByDate.put("subId",map.get("subId"));

            List<Map<String, Object>> aiTutChtCn = null;
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = MapUtils.getString(map, "aiTutChtCn");
            if (StringUtils.isNotEmpty(jsonData)) {
                try {
                    aiTutChtCn = mapper.readValue(jsonData, List.class);
                } catch (JsonParseException | JsonMappingException e) {
                    log.error(CustomLokiLog.errorLog(e));
                }
            }
            if (aiTutChtCn == null) {
                aiTutChtCn = new LinkedList<>();
            }

            aiTutChtCnByDate.put("aiTutChtCn", aiTutChtCn);
            reList.add(aiTutChtCnByDate);
        }

        paramData.put("aitutorUseCnt", aitutorUseCnt);

        return reList;
    }

    /**
     * [학생] 학습관리 > 나의 학습공간 > 학습 내역 - 영어 자기주도학습 AI튜터학습 내용 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findAitutorLrnResult(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new LinkedHashMap<>();

        // enLrngDivMap
        List<Map<String, Object>> divList = stntSelfLrnAitutorMapper.selectEnLrngDivList(paramData);
        Map<Integer, String> divMap = new HashMap<>();
        for (Map<String, Object> map : divList) {
            int enLrngDivId = MapUtils.getInteger(map, "enLrngDivId", 0);
            String code = MapUtils.getString(map, "code");
            if (enLrngDivId == 0 || StringUtils.isEmpty(code)) {
                continue;
            }
            divMap.put(enLrngDivId, code);
        }

        // ai학습내용 조회
        // stdNm - 학습내역 제목 (slf_std_info.std_nm)
        // 채팅 리스트 내 stdNm은 [AI튜터학습 > Lesson 2 > reading] 순서로
        List<Map<String, Object>> chatInfoList = new ArrayList<>();
        List<Map<String, Object>> list = stntDsbdMapper.selectSlfStdAitutorLrnList(paramData);

        for (Map<String, Object> map : list ) {
            Map<String, Object> aiTutChtCnByDate = new LinkedHashMap<>();
            aiTutChtCnByDate.put("id", map.get("resultId"));
            aiTutChtCnByDate.put("stdDt", map.get("stdDt"));

            // 문항별 소제목
            String enLrngDivIds = MapUtils.getString(map, "enLrngDivIds");
            String articleStdNm = "AI튜터학습";
            if (StringUtils.isNotEmpty(MapUtils.getString(map, "lessonNm"))) {
                articleStdNm += " > "+MapUtils.getString(map, "lessonNm");  // AI튜터학습 > Lesson 1
            }
            for (String enLrngDivId : enLrngDivIds.split(",")) {
                String code = MapUtils.getString(divMap, NumberUtils.toInt(enLrngDivId));   // speaking
                if (StringUtils.isEmpty(code))  break;
                articleStdNm += " > "+code;    // Lesson 1 > speaking
                break;
            }
            aiTutChtCnByDate.put("stdNm", articleStdNm);

            List<Map<String, Object>> aiTutChtCn = null;
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = MapUtils.getString(map, "aiTutResult");
            if (StringUtils.isNotEmpty(jsonData)) {
                try {
                    aiTutChtCn = mapper.readValue(jsonData, List.class);
                } catch (JsonParseException | JsonMappingException e) {
                    log.error(CustomLokiLog.errorLog(e));
                }
            }

            aiTutChtCnByDate.put("aiTutChtCn", aiTutChtCn);
            chatInfoList.add(aiTutChtCnByDate);

            // AI학습 제목
            rtnMap.put("stdNm", MapUtils.getString(map, "stdNm"));
        }

        rtnMap.put("chatInfoList", chatInfoList);

        return rtnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 영역별 그래프
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
	public Object selectStntDsbdAreaAchievementList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm"
        );

        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materAreaUnitInfo =
                AidtCommonUtil.filterToList(listItem, areaAchievementUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy", "prevUsdAchScrPercent"
        );

        List<LinkedHashMap<Object, Object>> areaAchievementList =
                AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdAreaAchievementList(paramData));

        int areaAchievementCount = stntDsbdMapper.selectTchDsbdAreaAchievementCount(paramData);

        returnMap.put("AreaAchievementUnitInfo", materAreaUnitInfo);
        returnMap.put("AreaAchievementCount", areaAchievementCount);
        returnMap.put("AreaAchievementList", areaAchievementList);

        return returnMap;
	}

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 ALL
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementListAll(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfoMap.put("unit", "");
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materAreaUnitInfo = AidtCommonUtil.filterToList(listItem, areaAchievementUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy", "articleList", "articleCnt", "stdAt"
        );

        List<Map> tempTchDsbdAreaAchievementList = tchDsbdMapper.selectTchDsbdAreaAchievementListAll(paramData);
        for (Map item : tempTchDsbdAreaAchievementList) {
            String articleList = (String) item.get("articleList");

            if (articleList != null && !articleList.isEmpty()) {
                // 1. Stream을 사용하여 분리, 중복 제거, 연결까지 한 번에 처리
                String newArticleList = Arrays.stream(articleList.split("[,|]"))
                        .distinct()
                        .collect(Collectors.joining(","));

                // 2. 원래 맵에 수정된 문자열 저장
                item.put("articleList", newArticleList);
                item.put("articleCnt", newArticleList.split(",").length);
            }
        }

//        List<LinkedHashMap<Object, Object>> areaAchievementList =  AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementList(paramData));
        List<LinkedHashMap<Object, Object>> areaAchievementList = AidtCommonUtil.filterToList(listItem1, tempTchDsbdAreaAchievementList);

        int areaAchievementCount = tchDsbdMapper.selectTchDsbdAreaAchievementCountAll(paramData);

        // 가장 높은 usdAchScrPercent를 가진 영역 찾기
        Map highestAchievedArea = areaAchievementList.stream()
                .max(Comparator.comparingDouble(m -> MapUtils.getDoubleValue(m, "usdAchScrPercent", 0)))
                .orElse(null);

        // 가장 낮은 usdAchScrPercent를 가진 영역 찾기
        Map lowestAchievedArea = areaAchievementList.stream()
                .filter(m -> MapUtils.getDoubleValue(m, "usdAchScrPercent", 0) > 0) // 0 이상인 값만 고려
                .min(Comparator.comparingDouble(m -> MapUtils.getDoubleValue(m, "usdAchScrPercent", 0)))
                .orElse(null);

        // 최고 영역과 최저 영역의 이름과 수치 추가
        if (highestAchievedArea != null) {
            returnMap.put("highestAchievedAreaName", highestAchievedArea.get("code"));
            returnMap.put("highestAchievedAreaPercent", highestAchievedArea.get("usdAchScrPercent"));
        }

        if (lowestAchievedArea != null) {
            returnMap.put("lowestAchievedAreaName", lowestAchievedArea.get("code"));
            returnMap.put("lowestAchievedAreaPercent", lowestAchievedArea.get("usdAchScrPercent"));
        }


        returnMap.put("AreaAchievementUnitInfo", materAreaUnitInfo);
        returnMap.put("AreaAchievementCount", areaAchievementCount);
        returnMap.put("AreaAchievementList", areaAchievementList);

        return returnMap;
    }


    /**
     * [학생] 학급관리 > 홈 대시보드 > 영역별 그래프(성취 기준 별)
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdAreaAchievementListall(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfoMap.put("unit", "");
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materAreaUnitInfo =
                AidtCommonUtil.filterToList(listItem, areaAchievementUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy", "prevUsdAchScrPercent", "stdAt"
        );

        List<LinkedHashMap<Object, Object>> areaAchievementList =
                AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdAreaAchievementListAll(paramData));

        for (Map map : areaAchievementList) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        int areaAchievementCount = stntDsbdMapper.selectTchDsbdAreaAchievementCountAll(paramData);

        // usdAchScrPercent 평균값 계산
        double avgUsdAchScrPercent = areaAchievementList.stream()
                .mapToDouble(m -> MapUtils.getDoubleValue(m, "usdAchScrPercent", 0))
                .average()
                .orElse(0);

        // unitNum을 기반으로 unitNm 조회
        Integer unitNum = MapUtils.getInteger(paramData, "unitNum", 0);
        String unitNm = "전 단원"; // 기본값 (unitNum이 0이면 '전 단원'으로 설정)
        if (unitNum > 0) {
            Optional<Map> matchedUnit = areaAchievementUnitInfo.stream()
                    .filter(unit -> unitNum.equals(MapUtils.getInteger(unit, "unitNum", 0)))
                    .findFirst();

            if (matchedUnit.isPresent()) {
                unitNm = (String) matchedUnit.get().get("unitNm");
            }
        }

        List<Map> validAreas = areaAchievementList.stream()
                .filter(m -> MapUtils.getDoubleValue(m, "rfltActvCnt", 0) > 0)
                .collect(Collectors.toList());

        List<Map> highestAchievedAreas = new ArrayList<>();
        List<Map> lowestAchievedAreas = new ArrayList<>();

        if (!validAreas.isEmpty()) {
            // 최고 점수 찾기
            int maxScore = validAreas.stream()
                    .mapToInt(m -> MapUtils.getIntValue(m, "usdAchScrPercent", 0))
                    .max()
                    .orElse(0);

            // 최저 점수 찾기
            int minScore = validAreas.stream()
                    .mapToInt(m -> MapUtils.getIntValue(m, "usdAchScrPercent", 0))
                    .min()
                    .orElse(0);

            // 최고 점수와 동일한 모든 영역 찾기 + 조건 체크
            if (maxScore > 0 && maxScore >= 50) {
                highestAchievedAreas = validAreas.stream()
                        .filter(m -> MapUtils.getIntValue(m, "usdAchScrPercent", 0) == maxScore)
                        .collect(Collectors.toList());
            }

            // 최저 점수와 동일한 모든 영역 찾기 + 조건 체크
            if (minScore < 100 && minScore < 50) {
                lowestAchievedAreas = validAreas.stream()
                        .filter(m -> MapUtils.getIntValue(m, "usdAchScrPercent", 0) == minScore)
                        .collect(Collectors.toList());
            }
        }

        // 최고 영역들의 이름과 점수 추가
        if (!highestAchievedAreas.isEmpty()) {
            List<String> highestAreaNames = highestAchievedAreas.stream()
                    .map(m -> (String) m.get("codeNm"))
                    .collect(Collectors.toList());

            returnMap.put("highestAchievedAreaNames", highestAreaNames);
            returnMap.put("highestAchievedAreaPercent", highestAchievedAreas.get(0).get("usdAchScrPercent"));
        }

        if (!lowestAchievedAreas.isEmpty()) {
            List<String> lowestAreaNames = lowestAchievedAreas.stream()
                    .map(m -> (String) m.get("codeNm"))
                    .collect(Collectors.toList());

            returnMap.put("lowestAchievedAreaNames", lowestAreaNames);
            returnMap.put("lowestAchievedAreaPercent", lowestAchievedAreas.get(0).get("usdAchScrPercent"));
        }

        returnMap.put("AreaAchievementUnitInfo", materAreaUnitInfo);
        returnMap.put("AreaAchievementCount", areaAchievementCount);
        returnMap.put("AreaAchievementList", areaAchievementList);
        returnMap.put("avgUsdAchScrPercent", (int) Math.round(avgUsdAchScrPercent)); // 정수 반올림
        returnMap.put("unitNm", unitNm);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 영역별 단원별 목록
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdAreaAchievementDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem2 = Arrays.asList(
                "rowNo", "stdDt", "trgtSeNm", "trgtNm", "rpOthbcAt", "trgtId"
        );

        List<Map> resultList = new ArrayList<>();

        if (paramData.get("unitNum") != null && Integer.parseInt((String) paramData.get("unitNum")) > 0) {
            String unitNum = (String) paramData.get("unitNum");
            paramData.put("isProject", 0);
            List<Map> areaAchievementUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

            Integer metaId = areaAchievementUnitInfo.stream()
                    .filter(unitInfo -> unitNum.equals(String.valueOf(unitInfo.get("unitNum"))))
                    .map(unitInfo -> (Integer) unitInfo.get("metaId"))
                    .findFirst()
                    .orElse(null);

            paramData.put("metaId", metaId);
        }

        if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
            resultList = stntDsbdMapper.selectTchDsbdAreaAchievementStudentList_Main(pagingParam);
        } else {
            resultList = stntDsbdMapper.selectTchDsbdAreaAchievementStudentList(pagingParam);
        }

        /*
        학생의 경우
        성취도 학습내역 팝업의
        학습내역의 데이터는 누적된 데이터를 조회 하여 보여준다.
        usd_ach_src2_info (o)
        usd_ach_src2_info_daily (x)
         */
        /*
        if (ObjectUtils.isEmpty(MapUtils.getString(paramData, "stdDtYmd"))) {
            resultList = stntDsbdMapper.selectTchDsbdAreaAchievementStudentList(pagingParam);
        } else {
            resultList = stntDsbdMapper.selectTchDsbdAreaAchievementStudentList_daily(pagingParam);
        }
        */

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> areaAchievementStudentList = AidtCommonUtil.filterToList(listItem2, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("areaAchievementStudentList", areaAchievementStudentList);
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object selectStntDsbdStatisticAchievementList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Set<String> validEvaluationAreas = Set.of("listening", "reading", "viewing", "speaking", "writing", "presenting");
        String evaluationAreaCd = (String) paramData.get("evaluationAreaCd");

        List<String> listItem1 = Arrays.asList(
                "stdDt","stdDtLabel","usdAchScr","stdDtYmd"
        );

        if (paramData.get("unitNum") != null && Integer.parseInt((String) paramData.get("unitNum")) > 0) {
            String unitNum = (String) paramData.get("unitNum");
            paramData.put("isProject", 0);
            List<Map> areaAchievementUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

            Integer metaId = areaAchievementUnitInfo.stream()
                    .filter(unitInfo -> unitNum.equals(String.valueOf(unitInfo.get("unitNum"))))
                    .map(unitInfo -> (Integer) unitInfo.get("metaId"))
                    .findFirst()
                    .orElse(null);

            paramData.put("metaId", metaId);
        }

        List<LinkedHashMap<Object, Object>> statisticAchList = List.of();
        // "listening", "reading", "viewing", "speaking", "writing", "presenting"
        if (validEvaluationAreas.contains(evaluationAreaCd)) {

            if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                    || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
                // 성취도 추이
                statisticAchList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStatisticAchievementList1_Main(paramData));
            } else {
                statisticAchList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStatisticAchievementList1(paramData));
            }

            returnMap.put("statisticAchList", statisticAchList);

        } else {

            statisticAchList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStatisticAchievementList2(paramData));

            returnMap.put("statisticAchList", statisticAchList);
        }

        for (Map map : statisticAchList) {
            if (map.containsKey("usdAchScr")) {
                double usdAchScrPercent = (double) map.get("usdAchScr");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScr", roundedValue);
            }
        }

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusVocabularyList(Map<String, Object> paramData, Pageable pageable) throws Exception {//stntDsbdMapper.selectStntDsbdStatusVocabularyList(pagingParam);
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm"
        );

        paramData.put("isProject", 0);
        List<Map> vocabularyUnitInfo = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);


        LinkedHashMap<Object, Object> vocabularyUnitInfoMap = new LinkedHashMap<>();
        vocabularyUnitInfoMap.put("unitNum", 0);
        vocabularyUnitInfoMap.put("metaId", 0);
        vocabularyUnitInfoMap.put("unitNm", "전 단원");
        vocabularyUnitInfo.add(0, vocabularyUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materVocabularyUnitInfo = AidtCommonUtil.filterToList(listItem, vocabularyUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "rowNum", "usdAchId", "totalUsdSrc", "totalLvlTy", "iemId", "iemCd", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy"
        );

        List<Map> resultList = stntDsbdMapper.selectStntDsbdStatusVocabularyList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> vocabularyList = AidtCommonUtil.filterToList(listItem1, resultList);

        int vocabularyCount = stntDsbdMapper.selectStntDsbdStatusVocabularyCount(paramData);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("vocabularyUnitInfo", materVocabularyUnitInfo);
        returnMap.put("VocabularyCount", vocabularyCount);
        returnMap.put("VocabularyList", vocabularyList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 단원별 상세
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusVocabularyDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem2 = Arrays.asList(
                "rowNo", "stdDt", "trgtSeNm", "trgtNm", "rpOthbcAt", "trgtId"
        );

        List<Map> resultList = stntDsbdMapper.selectTchDsbdVocabularyStudentList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> VocabularyStudentList = AidtCommonUtil.filterToList(listItem2, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("VocabularyStudentList", VocabularyStudentList);
        returnMap.put("page",page);

        return returnMap;

    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusGrammarList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm"
        );

        paramData.put("isProject", 0);
        List<Map> grammarUnitInfo = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> grammarUnitInfoMap = new LinkedHashMap<>();
        grammarUnitInfoMap.put("unitNum", 0);
        grammarUnitInfoMap.put("metaId", 0);
        grammarUnitInfoMap.put("unitNm", "전 단원");
        grammarUnitInfo.add(0, grammarUnitInfoMap);

        List<LinkedHashMap<Object, Object>> headGrammarUnitInfo =
                AidtCommonUtil.filterToList(listItem, grammarUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "rowNum", "usdAchId", "totalUsdSrc", "totalLvlTy", "iemId", "iemCd", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy"
        );

        List<Map> resultList = stntDsbdMapper.selectStntDsbdStatusGrammarList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> grammarList = AidtCommonUtil.filterToList(listItem1, resultList);

        int grammarCount = stntDsbdMapper.selectStntDsbdStatusGrammarCount(paramData);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("GrammarUnitInfo", headGrammarUnitInfo);
        returnMap.put("GrammarCount", grammarCount);
        returnMap.put("GrammarList", grammarList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 상세
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusGrammarDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem2 = Arrays.asList(
                "rowNo", "stdDt", "trgtSeNm", "trgtNm", "rpOthbcAt", "trgtId"
        );

        List<Map> resultList = stntDsbdMapper.selectTchDsbdGrammarStudentList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> GrammarStudentList = AidtCommonUtil.filterToList(listItem2, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("GrammarStudentList", GrammarStudentList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 >  Pronunciation 그래프
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusPronunciationList(Map<String, Object> paramData, Pageable pageable) throws Exception {//
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm"
        );

        paramData.put("isProject", 0);
        List<Map> pronunciationUnitInfo = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> pronunciationUnitInfoMap = new LinkedHashMap<>();
        pronunciationUnitInfoMap.put("unitNum", 0);
        pronunciationUnitInfoMap.put("metaId", 0);
        pronunciationUnitInfoMap.put("unitNm", "전 단원");
        pronunciationUnitInfo.add(0, pronunciationUnitInfoMap);

        List<LinkedHashMap<Object, Object>> headPronunciationUnitInfo =
                AidtCommonUtil.filterToList(listItem, pronunciationUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "rowNum", "usdAchId", "totalUsdSrc", "totalLvlTy", "iemId", "iemCd", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy"
        );

        List<Map> resultList = stntDsbdMapper.selectStntDsbdStatusPronunciationList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> pronunciationList = AidtCommonUtil.filterToList(listItem1, resultList);

        int pronunciationCount = stntDsbdMapper.selectStntDsbdStatusPronunciationCount(paramData);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("PronunciationUnitInfo", headPronunciationUnitInfo);
        returnMap.put("PronunciationCount", pronunciationCount);
        returnMap.put("PronunciationList", pronunciationList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 단어/문법/발음 >  Pronunciation 그래프 상세
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusPronunciationDetail(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem2 = Arrays.asList(
                "rowNo", "stdDt", "trgtSeNm", "trgtNm", "rpOthbcAt", "trgtId"
        );

        List<Map> resultList = stntDsbdMapper.selectTchDsbdPronunciationStudentList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> PronunciationStudentList = AidtCommonUtil.filterToList(listItem2, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("PronunciationStudentList", PronunciationStudentList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusStudyAchievementStandardList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectStntDsbdUnitInfoList = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectStntDsbdUnitInfoMap = new HashMap<>();
        selectStntDsbdUnitInfoMap.put("unitNum", 0);
        selectStntDsbdUnitInfoMap.put("metaId", 0);
        selectStntDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectStntDsbdUnitInfoList.add(0, selectStntDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> achievementStandardUnitInfo = AidtCommonUtil.filterToList(listItem, selectStntDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "parentAcNm", "acCd", "acNm", "fullAcNm", "depth", "usdScr", "studyMapCd", "kwgTotCount", "val"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> achievementStandardList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStatusStudyAchievementStandardList(paramData));

        int achievementStandardCount = stntDsbdMapper.selectTchDsbdStdMapAchievementStandardCount(paramData);


        // AI 튜터 정보 생성
        Map<String, Object> aiTutorInfo = new LinkedHashMap<>();

        // 1. 단원 이름 설정
        String unitNm = achievementStandardUnitInfo.stream()
                .filter(unit -> {
                    return String.valueOf(unit.get("unitNum"))
                            .equals(String.valueOf(paramData.get("unitNum"))
                            );
                })
                .map(unit -> {
                    Object value = unit.get("unitNm");
                    return value instanceof String ? (String) value : "";
                })
                .findFirst()
                .orElse("");
        aiTutorInfo.put("unitNm", unitNm);

        // 2. 가장 높은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> highestDepth4 = achievementStandardList.stream()
                .filter(item -> Integer.valueOf(4).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .max(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 가장 높은 학습맵의 상위 항목 찾기
        if (highestDepth4.isPresent()) {
            LinkedHashMap<Object, Object> highest = highestDepth4.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth3 = achievementStandardList.stream()
                    .filter(item -> item.get("metaId").equals(highest.get("parentId")))
                    .findFirst();

            Map<String, Object> highestAchievementMap = new LinkedHashMap<>();
            parentDepth3.ifPresent(parent -> highestAchievementMap.put("depth3", parent.get("acNm")));
            highestAchievementMap.put("depth4", highest.get("acNm"));

            aiTutorInfo.put("highestAchievementStudyMap", highestAchievementMap);
            aiTutorInfo.put("highestAchievementScore", highest.get("usdScr"));
        } else {
            Map<String, Object> emptyMap = new LinkedHashMap<>();
            emptyMap.put("depth3", "");
            emptyMap.put("depth4", "");
            aiTutorInfo.put("highestAchievementStudyMap", emptyMap);
            aiTutorInfo.put("highestAchievementScore", "-");
        }

        // 3. 가장 낮은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> lowestDepth4 = achievementStandardList.stream()
                .filter(item -> Integer.valueOf(4).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .min(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 가장 낮은 학습맵의 상위 항목 찾기
        if (lowestDepth4.isPresent() && highestDepth4.isPresent() &&
                !lowestDepth4.get().get("metaId").equals(highestDepth4.get().get("metaId"))) {

            LinkedHashMap<Object, Object> lowest = lowestDepth4.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth3 = achievementStandardList.stream()
                    .filter(item -> item.get("metaId").equals(lowest.get("parentId")))
                    .findFirst();

            Map<String, Object> lowestAchievementMap = new LinkedHashMap<>();
            parentDepth3.ifPresent(parent -> lowestAchievementMap.put("depth3", parent.get("acNm")));
            lowestAchievementMap.put("depth4", lowest.get("acNm"));

            aiTutorInfo.put("lowestAchievementStudyMap", lowestAchievementMap);
            aiTutorInfo.put("lowestAchievementScore", lowest.get("usdScr"));
        }

        // 4. 응답 데이터 구성
        returnMap.put("AchievementStandardAitutorInfo", aiTutorInfo);

        returnMap.put("AchievementStandardUnitInfo", achievementStandardUnitInfo);
        returnMap.put("AchievementStandardCount", achievementStandardCount);
        returnMap.put("AchievementStandardList", achievementStandardList);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 학습맵 > 소재
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusStudyMapMaterialList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectStntDsbdUnitInfoList = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectStntDsbdUnitInfoMap = new HashMap<>();
        selectStntDsbdUnitInfoMap.put("unitNum", 0);
        selectStntDsbdUnitInfoMap.put("metaId", 0);
        selectStntDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectStntDsbdUnitInfoList.add(0, selectStntDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materialUnitInfo = AidtCommonUtil.filterToList(listItem, selectStntDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "maNm", "fullMaNm", "depth", "usdScr", "studyMapCd", "kwgTotCount"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> materialList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStatusStudyMapMaterialList(paramData));

        int materialCount = stntDsbdMapper.selectTchDsbdStdMapMaterialCount(paramData);

        // AI 튜터 정보 생성
        Map<String, Object> aiTutorInfo = new LinkedHashMap<>();

        // 1. 단원 이름 설정
        String unitNm = materialUnitInfo.stream()
                .filter(unit -> {
                    return String.valueOf(unit.get("unitNum"))
                            .equals(String.valueOf(paramData.get("unitNum"))
                            );
                })
                .map(unit -> {
                    Object value = unit.get("unitNm");
                    return value instanceof String ? (String) value : "";
                })
                .findFirst()
                .orElse("");
        aiTutorInfo.put("unitNm", unitNm);

        // 2. 가장 높은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> highestDepth3 = materialList.stream()
                .filter(item -> Integer.valueOf(3).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .max(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 가장 높은 학습맵의 상위 항목 찾기
        if (highestDepth3.isPresent()) {
            LinkedHashMap<Object, Object> highest = highestDepth3.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth2 = materialList.stream()
                    .filter(item -> item.get("metaId").equals(highest.get("parentId")))
                    .findFirst();

            Map<String, Object> highestMaterialMap = new LinkedHashMap<>();
            parentDepth2.ifPresent(parent -> highestMaterialMap.put("depth2", parent.get("maNm")));
            highestMaterialMap.put("depth3", highest.get("maNm"));

            aiTutorInfo.put("highestMaterialStudyMap", highestMaterialMap);
            aiTutorInfo.put("highestMaterialScore", highest.get("usdScr"));
        } else {
            Map<String, Object> emptyMap = new LinkedHashMap<>();
            emptyMap.put("depth2", "");
            emptyMap.put("depth3", "");
            aiTutorInfo.put("highestMaterialStudyMap", emptyMap);
            aiTutorInfo.put("highestMaterialScore", "-");
        }

        // 3. 가장 낮은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> lowestDepth3 = materialList.stream()
                .filter(item -> Integer.valueOf(3).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .min(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 수치가 두개 이상 있을때만 낮은값 조회
        if (lowestDepth3.isPresent() && highestDepth3.isPresent() &&
                !lowestDepth3.get().get("metaId").equals(highestDepth3.get().get("metaId"))) {
            // 가장 낮은 학습맵의 상위 항목 찾기
            LinkedHashMap<Object, Object> lowest = lowestDepth3.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth2 = materialList.stream()
                    .filter(item -> item.get("metaId").equals(lowest.get("parentId")))
                    .findFirst();

            Map<String, Object> lowestMaterialMap = new LinkedHashMap<>();
            parentDepth2.ifPresent(parent -> lowestMaterialMap.put("depth2", parent.get("maNm")));
            lowestMaterialMap.put("depth3", lowest.get("maNm"));

            aiTutorInfo.put("lowestMaterialStudyMap", lowestMaterialMap);
            aiTutorInfo.put("lowestMaterialScore", lowest.get("usdScr"));
        }

        returnMap.put("aiTutorInfo", aiTutorInfo);

        returnMap.put("MaterialUnitInfo", materialUnitInfo);
        returnMap.put("MaterialCount", materialCount);
        returnMap.put("MaterialList", materialList);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 학습맵 > 의사소통
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusStudyMapCommunicationList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectStntDsbdUnitInfoList = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectStntDsbdUnitInfoMap = new HashMap<>();
        selectStntDsbdUnitInfoMap.put("unitNum", 0);
        selectStntDsbdUnitInfoMap.put("metaId", 0);
        selectStntDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectStntDsbdUnitInfoList.add(0, selectStntDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> communicationUnitInfo = AidtCommonUtil.filterToList(listItem, selectStntDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "coNm", "fullCoNm", "depth", "usdScr", "studyMapCd", "kwgTotCount"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> communicationList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStatusStudyMapCommunicationList(paramData));

        int communicationCount = stntDsbdMapper.selectTchDsbdStdMapCommunicationCount(paramData);

        // AI 튜터 정보 생성
        Map<String, Object> aiTutorInfo = new LinkedHashMap<>();

        // 1. 단원 이름 설정
        String unitNm = communicationUnitInfo.stream()
                .filter(unit -> {
                    return String.valueOf(unit.get("unitNum"))
                            .equals(String.valueOf(paramData.get("unitNum"))
                            );
                })
                .map(unit -> {
                    Object value = unit.get("unitNm");
                    return value instanceof String ? (String) value : "";
                })
                .findFirst()
                .orElse("");
        aiTutorInfo.put("unitNm", unitNm);

        // 2. 가장 높은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> highestDepth3 = communicationList.stream()
                .filter(item -> Integer.valueOf(3).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .max(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 가장 높은 학습맵의 상위 항목 찾기
        if (highestDepth3.isPresent()) {
            LinkedHashMap<Object, Object> highest = highestDepth3.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth2 = communicationList.stream()
                    .filter(item -> item.get("metaId").equals(highest.get("parentId")))
                    .findFirst();

            Map<String, Object> highestCommunicationMap = new LinkedHashMap<>();
            parentDepth2.ifPresent(parent -> highestCommunicationMap.put("depth2", parent.get("coNm")));
            highestCommunicationMap.put("depth3", highest.get("coNm"));

            aiTutorInfo.put("highestCommunicationStudyMap", highestCommunicationMap);
            aiTutorInfo.put("highestCommunicationScore", highest.get("usdScr"));
        } else {
            Map<String, Object> emptyMap = new LinkedHashMap<>();
            emptyMap.put("depth2", "");
            emptyMap.put("depth3", "");
            aiTutorInfo.put("highestCommunicationStudyMap", emptyMap);
            aiTutorInfo.put("highestCommunicationScore", "-");
        }

        // 3. 가장 낮은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> lowestDepth3 = communicationList.stream()
                .filter(item -> Integer.valueOf(3).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .min(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 수치가 두개 이상 있을때만 낮은값 조회
        if (lowestDepth3.isPresent() && highestDepth3.isPresent() &&
                !lowestDepth3.get().get("metaId").equals(highestDepth3.get().get("metaId"))) {
            // 가장 낮은 학습맵의 상위 항목 찾기
            LinkedHashMap<Object, Object> lowest = lowestDepth3.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth2 = communicationList.stream()
                    .filter(item -> item.get("metaId").equals(lowest.get("parentId")))
                    .findFirst();

            Map<String, Object> lowestCommunicationMap = new LinkedHashMap<>();
            parentDepth2.ifPresent(parent -> lowestCommunicationMap.put("depth2", parent.get("coNm")));
            lowestCommunicationMap.put("depth3", lowest.get("coNm"));

            aiTutorInfo.put("lowestCommunicationStudyMap", lowestCommunicationMap);
            aiTutorInfo.put("lowestCommunicationScore", lowest.get("usdScr"));
        }

        returnMap.put("aiTutorInfo", aiTutorInfo);

        returnMap.put("CommunicationUnitInfo", communicationUnitInfo);
        returnMap.put("CommunicationCount", communicationCount);
        returnMap.put("CommunicationList", communicationList);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStudyMapLanguageFormatList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectStntDsbdUnitInfoList = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectStntDsbdUnitInfoMap = new HashMap<>();
        selectStntDsbdUnitInfoMap.put("unitNum", 0);
        selectStntDsbdUnitInfoMap.put("metaId", 0);
        selectStntDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectStntDsbdUnitInfoList.add(0, selectStntDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> languageFormatUnitInfo = AidtCommonUtil.filterToList(listItem, selectStntDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "laNm", "fullLaNm", "depth", "usdScr", "studyMapCd", "kwgTotCount"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> languageFormatList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStudyMapLanguageFormatList(paramData));

        int languageFormatCount = stntDsbdMapper.selectTchDsbdStdMapLanguageFormatCount(paramData);

        // AI 튜터 정보 생성
        Map<String, Object> aiTutorInfo = new LinkedHashMap<>();

        // 1. 단원 이름 설정
        String unitNm = languageFormatUnitInfo.stream()
                .filter(unit -> {
                    return String.valueOf(unit.get("unitNum"))
                            .equals(String.valueOf(paramData.get("unitNum"))
                            );
                })
                .map(unit -> {
                    Object value = unit.get("unitNm");
                    return value instanceof String ? (String) value : "";
                })
                .findFirst()
                .orElse("");
        aiTutorInfo.put("unitNm", unitNm);

        // 2. 가장 높은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> highestDepth3 = languageFormatList.stream()
                .filter(item -> Integer.valueOf(3).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .max(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 가장 높은 학습맵의 상위 항목 찾기
        if (highestDepth3.isPresent()) {
            LinkedHashMap<Object, Object> highest = highestDepth3.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth2 = languageFormatList.stream()
                    .filter(item -> item.get("metaId").equals(highest.get("parentId")))
                    .findFirst();

            Map<String, Object> highestLanguageFormatMap = new LinkedHashMap<>();
            parentDepth2.ifPresent(parent -> highestLanguageFormatMap.put("depth2", parent.get("laNm")));
            highestLanguageFormatMap.put("depth3", highest.get("laNm"));

            aiTutorInfo.put("highestLanguageFormatStudyMap", highestLanguageFormatMap);
            aiTutorInfo.put("highestLanguageFormatScore", highest.get("usdScr"));
        } else {
            Map<String, Object> emptyMap = new LinkedHashMap<>();
            emptyMap.put("depth2", "");
            emptyMap.put("depth3", "");
            aiTutorInfo.put("highestLanguageFormatStudyMap", emptyMap);
            aiTutorInfo.put("highestLanguageFormatScore", "-");
        }

        // 3. 가장 낮은 성취도 정보 조회
        Optional<LinkedHashMap<Object, Object>> lowestDepth3 = languageFormatList.stream()
                .filter(item -> Integer.valueOf(3).equals(item.get("depth")))
                .filter(item -> !"-".equals(item.get("usdScr")))
                .min(Comparator.comparingDouble(item -> Double.parseDouble(item.get("usdScr").toString())));

        // 수치가 두개 이상 있을때만 낮은값 조회
        if (lowestDepth3.isPresent() && highestDepth3.isPresent() &&
                !lowestDepth3.get().get("metaId").equals(highestDepth3.get().get("metaId"))) {
            // 가장 낮은 학습맵의 상위 항목 찾기
            LinkedHashMap<Object, Object> lowest = lowestDepth3.get();
            Optional<LinkedHashMap<Object, Object>> parentDepth2 = languageFormatList.stream()
                    .filter(item -> item.get("metaId").equals(lowest.get("parentId")))
                    .findFirst();

            Map<String, Object> lowestLanguageFormatMap = new LinkedHashMap<>();
            parentDepth2.ifPresent(parent -> lowestLanguageFormatMap.put("depth2", parent.get("laNm")));
            lowestLanguageFormatMap.put("depth3", lowest.get("laNm"));

            aiTutorInfo.put("lowestLanguageFormatStudyMap", lowestLanguageFormatMap);
            aiTutorInfo.put("lowestLanguageFormatScore", lowest.get("usdScr"));
        }
        returnMap.put("aiTutorInfo", aiTutorInfo);

        returnMap.put("LanguageFormatUnitInfo", languageFormatUnitInfo);
        returnMap.put("LanguageFormatCount", languageFormatCount);
        returnMap.put("LanguageFormatList", languageFormatList);

        return returnMap;
    }

    // 가장 최근 수업 정보 및 요약 현황
    @Transactional(readOnly = true)
    public Object selectTchDsbdSummary(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "rcntClsDt","rcntClsCurri", "usdAchScr"
        );

        List<String> evlTaskInfo = Arrays.asList(
                "gb", "id","name", "rcntDt", "endDt"
        );

        Map<Object, Object> rtnMap = new LinkedHashMap<>();

        // textbookId 를 이용해 brand id 조회
        paramData.put("textbkId", paramData.get("textbookId"));
        int brandId = aiLearningMapper.findBrandId(paramData);
        paramData.put("brandId", brandId);

        Map<Object, Object> summaryInfo = new HashMap<>();
        if (1 == brandId) {// 수학
            // 최근 수업 정보, 최근 수업 이해도
            summaryInfo = AidtCommonUtil.filterToMap(infoItem, stntDsbdMapper.selectStdDsbdSummaryMath(paramData));

        } else if (3 == brandId) {// 영어
            // 최근 수업 정보, 최근 수업 성취도
         summaryInfo = AidtCommonUtil.filterToMap(infoItem, stntDsbdMapper.selectStdDsbdSummaryEng(paramData));
        }

        String btchUpdDt = tchDsbdMapper.selectBtchUpDt(paramData); /* 쿼리 확인해보니 claId와 textbookId 만 요청 파라미터로 받고 있어서 교사쪽 쿼리 그대로 사용 가능 */

        summaryInfo.put("btchUpdDt", btchUpdDt);

        //평가, 과제, 과제 게시판 현황정보 통합
        List<Map> dsbdEvlTaskBbsList = stntDsbdMapper.findStntDsbdEvlTaskBbsList(paramData);

        //평가 현황 정보
        //Map<Object, Object> dsbdEval = AidtCommonUtil.filterToMap(listItem, stntDsbdMapper.findStntDsbdEvlTaskList(paramData));

        //과제 현황 정보
        //Map<Object, Object> dsbdTask = AidtCommonUtil.filterToMap(listItem1, stntDsbdMapper.selectStdDsbdTask(paramData));

        rtnMap.put("dsbdCheck",summaryInfo);
        rtnMap.put("dsbdEvlTaskBbsList",dsbdEvlTaskBbsList);

        return rtnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 (수학)
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusStudyMapMathAchievementStandardList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "contentAreaNm", "achStdCd", "achStdNm", "metaId", "kwgMainInfo", "avgUsdScr"
        );

        if(!paramData.containsKey("metaId")) {
            paramData.put("metaId", 0); // 전 단원 검색으로 처리
        }

        List<LinkedHashMap<Object, Object>> achStdList = AidtCommonUtil.filterToList(listItem1, stntDsbdMapper.selectStntDsbdStdMapMathAchievementStandardList(paramData));

        CollectionUtils.emptyIfNull(achStdList)
                .stream()
                .forEach(achStdInfo -> {
                    List<Map<String,Object>> kwgMainList = new ArrayList<>();

                    String[] kwgMainInfos = MapUtils.getString(achStdInfo, "kwgMainInfo").split("\\|");
                    if(kwgMainInfos != null) {
                        for (String kwgMainInfo : kwgMainInfos) {
                            kwgMainList.add(AidtCommonUtil.jsonToMap(kwgMainInfo));
                        }
                    }

                    achStdInfo.put("kwgMainInfo", kwgMainList);
                });

        returnMap.put("achStdList", achStdList);

        // [AI 튜터 정보 생성]
        Map<String, Object> aiTutorInfo = new LinkedHashMap<>();

        paramData.put("isProject", 0);
        List<Map> selectTchDsbdUnitInfoList = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        /*단원명(전 단원) 추가*/
        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();
        selectTchDsbdUnitInfoMap.put("unitNum", 0);
        selectTchDsbdUnitInfoMap.put("metaId", 0);
        selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectTchDsbdUnitInfoList.add(0, selectTchDsbdUnitInfoMap);

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm"
        );
        List<LinkedHashMap<Object, Object>> achievementStandardUnitInfo = AidtCommonUtil.filterToList(listItem, selectTchDsbdUnitInfoList);

        // 1. 단원 이름 설정
        String unitNm = achievementStandardUnitInfo.stream()
                .filter(unit -> String.valueOf(unit.get("metaId")).equals(String.valueOf(paramData.get("metaId"))))
                .map(unit -> {
                    Object value = unit.get("unitNm");
                    return value instanceof String ? (String) value : "";
                })
                .findFirst()
                .orElse("");
        aiTutorInfo.put("unitNm", unitNm);

        // 가장 높은 성취도와 가장 낮은 성취도 찾기
        List<String> lowestAchievementList = new ArrayList<>();

        // 유효한 점수를 가진 항목들만 필터링
        List<LinkedHashMap<Object, Object>> validScoreItems = achStdList.stream()
                .filter(item -> {
                    String avgUsdScr = (String) item.get("avgUsdScr");
                    return avgUsdScr != null && !avgUsdScr.equals("-");
                })
                .toList();

        if (!validScoreItems.isEmpty()) {
            // 가장 높은 성취도 찾기
            Optional<LinkedHashMap<Object, Object>> highestItem = validScoreItems.stream()
                    .max(Comparator.comparing(item -> Double.parseDouble((String) item.get("avgUsdScr"))));

            highestItem.ifPresent(item -> {
                aiTutorInfo.put("highestUnderstandingCriteria", item.get("achStdNm"));
                aiTutorInfo.put("highestUnderstandingScore", item.get("avgUsdScr"));
            });

            // 가장 낮은 성취도 찾기
            Optional<LinkedHashMap<Object, Object>> lowestItem = validScoreItems.stream()
                    .min(Comparator.comparingDouble(item -> Double.parseDouble(item.get("avgUsdScr").toString())));

            lowestItem.ifPresent(item -> {
                lowestAchievementList.add((String) item.get("achStdNm"));
                aiTutorInfo.put("lowestAchievementScore", item.get("avgUsdScr"));
            });
        }

        aiTutorInfo.put("lowestAchievementStudyMapList", lowestAchievementList);
        returnMap.put("AchievementStandardAiTutorInfo", aiTutorInfo);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 ALL(단원별)
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdUnitAchievementListAll(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 필터링에 사용할 항목 리스트 정의
        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        int unitNumChk = 0;
        if (paramData.get("unitNum") != null && !paramData.get("unitNum").equals("")) {
            unitNumChk = Integer.parseInt(paramData.get("unitNum").toString());
        }


        // 모든 기본 단원 정보 조회
        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        // "전 단원" 정보를 담을 맵 생성
        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfoMap.put("unit", "");
        // "전 단원" 정보를 단원 목록의 첫번째 위치에 추가
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);
        // 필요한 항목만 필터링하여 새로운 리스트 생성
        List<LinkedHashMap<Object, Object>> materAreaUnitInfo = AidtCommonUtil.filterToList(listItem, areaAchievementUnitInfo);



        // 학생 이름 조회
        String stntId = paramData.get("userId").toString();
        Map<String, Object> loginUserInfo = userMapper.findUserInfoByUserId(stntId);
        returnMap.put("flnm", loginUserInfo.get("flnm"));



        // 성취도 정보 조회
        List<Map> unitAchievement = stntDsbdMapper.selectStntDsbdUnitAchievementList(paramData);

        // 전체 단원의 평균 성취도 계산을 위한 변수 초기화
        double totalAchievementPercent = 0.0;
        int unitCount = 0;

        if (unitNumChk <= 0) {
            for (Map unit : unitAchievement) {
                if (unit.containsKey("avgUsdAchScrPercent")) {
                    totalAchievementPercent += Double.parseDouble(unit.get("avgUsdAchScrPercent").toString());
                    unitCount++;
                }
            }

            double averageAchievement = (unitCount > 0) ? (totalAchievementPercent / unitCount) : 0;

            // 전체 평균을 나타내는 Map 생성 (JSON 형식에 맞춤)
            Map<String, Object> totalAchievementMap = new LinkedHashMap<>();
            totalAchievementMap.put("unitNum", 0);  // 단원 번호는 0으로 설정
            totalAchievementMap.put("avgUsdAchScrPercent", averageAchievement);

            // UnitAchievementList에 전체 단원 데이터 추가 (맨 앞에 추가)
            unitAchievement.add(0, totalAchievementMap);
        }

        for (Map map : unitAchievement) {
            // avgUsdAchScrPercent 반올림
            if (map.containsKey("avgUsdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("avgUsdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("avgUsdAchScrPercent", roundedValue);
            }
        }

        // 최고/최저 성취도 단원 찾기
        String highestUnitName = unitAchievement.stream()
                .filter(unit -> unit.containsKey("avgUsdAchScrPercent") && unit.containsKey("unitNum") &&
                        !unit.get("unitNum").toString().equals("0")) // 전체 단원(unitNum=0) 제외
                .max(Comparator.comparingDouble(unit ->
                        Double.parseDouble(unit.get("avgUsdAchScrPercent").toString())))
                .map(unit -> {
                    int unitNum = Integer.parseInt(unit.get("unitNum").toString());
                    return materAreaUnitInfo.stream()
                            .filter(info -> info.containsKey("unitNum") &&
                                    Integer.parseInt(info.get("unitNum").toString()) == unitNum)
                            .map(info -> (String) info.get("unitNm"))
                            .findFirst()
                            .orElse("");
                })
                .orElse("");

        String lowestUnitName = unitAchievement.stream()
                .filter(unit -> unit.containsKey("avgUsdAchScrPercent") && unit.containsKey("unitNum") &&
                        !unit.get("unitNum").toString().equals("0")) // 전체 단원(unitNum=0) 제외
                .min(Comparator.comparingDouble(unit ->
                        Double.parseDouble(unit.get("avgUsdAchScrPercent").toString())))
                .map(unit -> {
                    int unitNum = Integer.parseInt(unit.get("unitNum").toString());
                    return materAreaUnitInfo.stream()
                            .filter(info -> info.containsKey("unitNum") &&
                                    Integer.parseInt(info.get("unitNum").toString()) == unitNum)
                            .map(info -> (String) info.get("unitNm"))
                            .findFirst()
                            .orElse("");
                })
                .orElse("");

        // 최종 결과 맵에 데이터 추가
        returnMap.put("AreaAchievementUnitInfo", materAreaUnitInfo);
        returnMap.put("UnitAchievementList", unitAchievement);
        returnMap.put("highestUnitName", highestUnitName);
        returnMap.put("lowestUnitName", lowestUnitName);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdAreaAchievementStudentDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 학생 개인
        List<Map> studentDistribution = tchDsbdMapper.selectTchDsbdAreaAchievementStudentDstribution(paramData);

        // 반올림 처리
        for (Map map : studentDistribution) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("studentDistribution", studentDistribution);

        // 학생의 최고/최저 성취도 영역 조회
        List<Map> achievementSummary = stntDsbdMapper.selectStntDsbdAreaAchievementStudentDistributionSummary(paramData);

        returnMap.put("achievementSummary", achievementSummary);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdChapterUsdStudentDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 학생 개인의 영역별 이해도 정보_그래프 데이터
        List<String> studentListItem = Arrays.asList(
                "contentAreaNm", "contentAreaId", "flnm", "stdtId", "stdAt", "usdScr"
        );

        List<Map> studentDistributionRare = tchDsbdMapper.selectTchDsbdChapterUsdStudentDistribution(paramData);

        List<LinkedHashMap<Object, Object>> studentDistribution = AidtCommonUtil.filterToList(studentListItem, studentDistributionRare);
        roundScores(studentDistribution, "usdScr");
        returnMap.put("studentDistribution", studentDistribution);

        // 학생의 최고/최저 이해도 영역 조회
        List<String> studentScoreItem = Arrays.asList(
                "contentAreaNm", "contentAreaId", "flnm", "stdtId", "stdAt", "usdScr", "scoreRank"
        );

        List<Map> chapterUsdSummaryRare = stntDsbdMapper.selectStntDsbdChapterUsdStudentDistributionSummary(paramData);

        List<LinkedHashMap<Object, Object>> chapterUsdSummary = AidtCommonUtil.filterToList(studentScoreItem, chapterUsdSummaryRare);
        returnMap.put("chapterUsdSummary", chapterUsdSummary);

        return returnMap;
    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 의사소통 기능 > communication 단원별 상세 (초등 영어)
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusCommunicationDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem2 = Arrays.asList(
                "rowNo", "stdDt", "trgtSeNm", "trgtNm", "rpOthbcAt", "trgtId"
        );

        Integer textbookId = Integer.parseInt((String) paramData.get("textbookId"));

        // 초등 5
        if (textbookId == 6981) {
            paramData.put("evaluationAreaCd", "evalCommu15");
        // 초등 6
        } else if (textbookId == 6982) {
            paramData.put("evaluationAreaCd", "evalCommu16");
        }

        List<Map> resultList = stntDsbdMapper.selectStntDsbdStatusCommunicationDetail(paramData);

        List<LinkedHashMap<Object, Object>> communicationStudentList = AidtCommonUtil.filterToList(listItem2, resultList);

        returnMap.put("communicationStudentList", communicationStudentList);

        return returnMap;

    }

    /**
     * [학생] 학급관리 > 홈 대시보드 > 의사소통 기능 > communication 단원별 영역
     * @param paramData 입력 파라메터
     * @return map
     */
    @Transactional(readOnly = true)
    public Object selectStntDsbdStatusCommunicationList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer textbookId = Integer.parseInt((String) paramData.get("textbookId"));

        // 초등 5
        if (textbookId == 6981) {
            paramData.put("evaluationAreaCd", "evalCommu15");
        // 초등 6
        } else if (textbookId == 6982) {
            paramData.put("evaluationAreaCd", "evalCommu16");
        }


        paramData.put("isProject", 0);
        List<Map> communicationUnitInfo = stntDsbdMapper.selectStntDsbdUnitInfo(paramData);

        List<String> listItem1 = Arrays.asList(
                "rowNum", "usdAchId", "totalUsdSrc", "totalLvlTy", "iemId", "iemCd", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy", "metaId"
        );

        List<Map> resultList = stntDsbdMapper.selectStntDsbdStatusCommunicationList(paramData);

        List<LinkedHashMap<Object, Object>> communicationList = AidtCommonUtil.filterToList(listItem1, resultList);

        if (communicationList != null && !communicationList.isEmpty()) {
            for (LinkedHashMap<Object, Object> vocabMap : communicationList) {
                if (vocabMap.containsKey("totalUsdSrc")) {
                    double usdAchScr = (double) vocabMap.get("totalUsdSrc");
                    int roundedValue = (int) Math.round(usdAchScr);
                    vocabMap.put("totalUsdSrc", roundedValue);
                }

                if (vocabMap.containsKey("usdAchScrPercent")) {
                    double usdAchScrPercent = (double) vocabMap.get("usdAchScrPercent");
                    int roundedPercentValue = (int) Math.round(usdAchScrPercent);
                    vocabMap.put("usdAchScrPercent", roundedPercentValue);
                }
            }
        }

        communicationList.forEach(vocab -> {
            Integer vocabMetaId = (Integer) vocab.get("metaId");

            // metaId가 같은 항목 찾기
            communicationUnitInfo.stream()
                    .filter(unit -> vocabMetaId.equals(unit.get("metaId")))
                    .findFirst()
                    .ifPresent(unit -> {
                        vocab.put("unit", unit.get("unit"));
                        vocab.put("unitNum", unit.get("unitNum")); // unitNum 추가
                    });
        });

        // 최고 점수와 최저 점수를 가진 항목 찾기
        LinkedHashMap<Object, Object> highestScore = communicationList.stream()
                .max(Comparator.comparingDouble(item -> ((Number) item.get("usdAchScrPercent")).doubleValue()))
                .orElse(null);

        LinkedHashMap<Object, Object> lowestScore = communicationList.stream()
                .min(Comparator.comparingDouble(item -> ((Number) item.get("usdAchScrPercent")).doubleValue()))
                .orElse(null);

        Map<String, Object> detailMap = new LinkedHashMap<>();

        if (highestScore != null) {
            Map<String, Object> highestArea = new LinkedHashMap<>();
            highestArea.put("unit", highestScore.get("unit"));
            highestArea.put("iemCd", highestScore.get("iemCd"));
            highestArea.put("usdAchScrPercent", highestScore.get("usdAchScrPercent"));
            detailMap.put("highArea", highestArea);
        }

        if (lowestScore != null) {
            Map<String, Object> lowestArea = new LinkedHashMap<>();
            lowestArea.put("unit", lowestScore.get("unit"));
            lowestArea.put("iemCd", lowestScore.get("iemCd"));
            lowestArea.put("usdAchScrPercent", lowestScore.get("usdAchScrPercent"));
            detailMap.put("lowArea", lowestArea);
        }

        returnMap.put("communicationList", communicationList);
        returnMap.put("communicationAitutor", detailMap);

        return returnMap;
    }

    private void roundScores(List<LinkedHashMap<Object, Object>> list, String fieldName) {
        for (Map<Object, Object> map : list) {
            if (map.containsKey(fieldName)) {
                double value = (double) map.get(fieldName);
                int roundedValue = (int) Math.round(value);
                map.put(fieldName, roundedValue);
            }
        }
    }
}
