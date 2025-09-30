package com.visang.aidt.lms.api.dashboard.service;

import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import com.visang.aidt.lms.api.dashboard.mapper.TchDsbdMapper;
import com.visang.aidt.lms.api.dashboard.vo.QuadrantAlertEnum;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.user.mapper.UserMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CamelHashMap;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TchDsbdService {
    /**
     *
     * (대시보드).이해도 낮은
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    private final TchDsbdMapper tchDsbdMapper;

    private final AiLearningMapper aiLearningMapper;
    private final EtcMapper etcMapper;
    private final UserMapper userMapper;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    private AtomicBoolean updating = new AtomicBoolean(false);


    public Map<String, Object> findTchDsbdUnderstand(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).정답률 낮은순
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findTchDsbdAccuracy(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).목표-이해도 차이 큰 순
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findTchDsbdGap(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).단원별 이해도
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findTchDsbdUnderstandChapter(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).개념별 이해도
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findTchDsbdUnderstandConcept(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).지식맵
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findTchDsbdKnowledgeMap(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).영역별 이해도
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> findTchDsbdUnderstandDomain(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).영역별 이해도 상세
     *
     * @param paramData
     * @return Map
     */

    public Map<String, Object> findTchDsbdUnderstandDomainDetail(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).과제 평가 현황
     *
     * @param paramData
     * @return Map
     */

    public Map<String, Object> findTchDsbdStatusEval(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).과제 평가 현황 상세
     *
     * @param paramData
     * @return Map
     */

    public Map<String, Object> findTchDsbdStatusEvalDetail(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * (대시보드).자기주도 학습 현황
     *
     * @param paramData
     * @return Map
     */

    public Map<String, Object> findTchDsbdSelflearning(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusSelflrnChapterList(Map<String, Object> paramData) throws Exception {

        // 단원학습 정보를 불러온다
//        List<Map> UnitInfo = tchDsbdMapper.findUnitInfo(paramData);
        List<Map> SubmInfo = null;

        Map stntCntInfo = tchDsbdMapper.findStntCnt(paramData);
        SubmInfo = tchDsbdMapper.findSubmInfo(paramData);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("submList",SubmInfo);
        returnMap.put("stntCnt",stntCntInfo.get("stntCnt"));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object tchDsbdStatusSelflrnChapterDetail(Map<String, Object> paramData) throws Exception {

        // 학습 인원수를 구한다
        LinkedHashMap<Object, Object> chapterDetail = tchDsbdMapper.findTchDsbdStatusSelflrnChapterDetail(paramData);
        // 학생 정보를 구한다
        List<Map> StntInfo = tchDsbdMapper.findChapterDetailStntInfo(paramData);

        chapterDetail.put("StntList", StntInfo);

        return chapterDetail;
    }

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusChapterUnitList(Map<String, Object> paramData) throws Exception {

        LinkedList<Map> list = tchDsbdMapper.findTchDsbdStatusChapterUnitList(paramData);

        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("chptUnitList", list);

        return resultMap;
    }

    public Object findTchDsbdStatusChapterUnitInfo(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        List<Map> list = tchDsbdMapper.findTchDsbdStatusChapterUnitInfo(paramData);
        List<Map> unitLevel = tchDsbdMapper.findTchDsbdStudentsByUnitLevel(paramData);

        // 전단원 클릭 시
        if(Objects.isNull(paramData.get("unitNum"))) {
            // avgUsdScr이 가장 높은/낮은 단원 찾기
            Map highestUnit = list.stream()
                .max(Comparator.comparingDouble(m -> Double.parseDouble(m.get("avgUsdScr").toString())))
                .orElse(null);

            Map lowestUnit = list.stream()
                .min(Comparator.comparingDouble(m -> Double.parseDouble(m.get("avgUsdScr").toString())))
                .orElse(null);

            resultMap.put("highestUnitName", highestUnit != null ? highestUnit.get("unitNm") : null);
            resultMap.put("lowestUnitName", lowestUnit != null ? lowestUnit.get("unitNm") : null);
        }

        // 상,중,하 학생 리스트
        list.forEach(unit -> {
            String unitNum = String.valueOf(unit.get("unitNum"));

            Map<String, List<Map>> studentLists = unitLevel.stream()
                    .filter(student -> unitNum.equals(String.valueOf(student.get("unitNum"))))
                    .collect(Collectors.groupingBy(student -> {
                        String level = (String) student.get("level");
                        return "1".equals(level) ? "high" : "2".equals(level) ? "middle" : "low";
                    }));

            // 빈 리스트 처리
            studentLists.putIfAbsent("high", Collections.emptyList());
            studentLists.putIfAbsent("middle", Collections.emptyList());
            studentLists.putIfAbsent("low", Collections.emptyList());

            unit.put("studentLists", studentLists);
        });

        // 단원 클릭 시 그대로 반환하면됨.
        resultMap.put("chptUnitList", list);

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusChapterUnitDetail(Map<String, Object> paramData) throws Exception {

        Map resultMap = tchDsbdMapper.findTchDsbdStatusChapterUnitDetail(paramData);

        List<Map> chptStdtList = tchDsbdMapper.findTchDsbdStatusChapterUnitDetail_chptStdtList(paramData);

        // 성적 반올림 처리
        for (Map studentMap : chptStdtList) {
            if (studentMap.containsKey("usdScr")) {
                Object usdScrObj = studentMap.get("usdScr");
                if (usdScrObj instanceof Number) {
                    double usdScr = ((Number) usdScrObj).doubleValue();
                    int roundedValue = (int) Math.round(usdScr);
                    studentMap.put("usdScr", roundedValue);

                    // resultGradeNm 재계산 (쿼리의  case when 구문에서 계산된 것이 정확하지 않을 수 있음)
                    String resultGradeNm;
                    if (roundedValue >= 80) {
                        resultGradeNm = "상";
                    } else if (roundedValue > 50) {
                        resultGradeNm = "중";
                    } else {
                        resultGradeNm = "하";
                    }
                    studentMap.put("resultGradeNm", resultGradeNm);
                }
            }
        }

        resultMap.put("chptStdtList", chptStdtList);

        return resultMap;

    }

    // 가장 최근 수업 정보 및 요약 현황
    @Transactional(readOnly = true)
    public Object selectTchDsbdSummary(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "rcntClsDt", "rcntClsCurri", "rcntClsCurriKey", "pgTaskCnt", "pgEvalCnt", "cdtnUpdCnt", "btchUpdDt"
        );

        Map<Object, Object> summaryInfo = new HashMap<>();
        Map<Object, Object> rtnMap = new HashMap<>();

        summaryInfo = AidtCommonUtil.filterToMap(infoItem, tchDsbdMapper.selectTchDsbdSummary(paramData));
        int cdtnCnt = etcMapper.conditionUserListSize(paramData);
        summaryInfo.put("cdtnCnt", cdtnCnt);
        rtnMap.put("dsbdCheck",summaryInfo);

        return rtnMap;
    }

    // 가장 최근 수업 정보 및 요약 현황
    @Transactional(readOnly = true)
    public Object selectTchDsbdSummaryNew(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "rcntClsDt", "rcntClsCurri", "crculId"
        );

        List<String> evlTaskInfo = Arrays.asList(
                "gb", "id","name", "rcntDt", "endDt", "attendStntCnt", "totalStntCnt"
        );

        Map<Object, Object> rtnMap = new LinkedHashMap<>();

        int brandId = tchDsbdMapper.findBrandId(paramData);
        paramData.put("brandId", brandId);

        //수업 현황 정보
        Map<Object, Object> summaryInfo = AidtCommonUtil.filterToMap(infoItem, tchDsbdMapper.selectTchDsbdSummaryNew(paramData));

        if (1 == brandId) {
            // 참여 인원 수
            Integer result = tchDsbdMapper.selectTchDsbdParticipantCnt(paramData);
            int participantCnt = (result != null && result > 0) ? result : 0;

            // 전체 학생 수
            int stdtCnt = tchDsbdMapper.selectTchDsbdStdtCnt(paramData);
            // 이해도 [하]인 학생 수
            int lowerLevelStdtCnt = tchDsbdMapper.selectTchDsbdLowerLevelStdtCnt(paramData);

            summaryInfo.put("participantCnt", participantCnt);
            summaryInfo.put("stdtCnt", stdtCnt);
            summaryInfo.put("lowerLevelStdtCnt", lowerLevelStdtCnt);
        } else if (3 == brandId) {
            // 추가 지도가 필요한 학생수
            int cdtnLvCnt = etcMapper.conditionLvUserListSize(paramData);

            summaryInfo.put("lowLvlCnt", cdtnLvCnt);
        }

        //평가, 과제 현황 통합 정보
        List<LinkedHashMap<Object, Object>> dsbdEvlTaskList = AidtCommonUtil.filterToList(evlTaskInfo,  tchDsbdMapper.findTchDsbdEvlTaskList(paramData));

        // 학생 오늘의 기분 업데이트 (비상측에서 개발)
        int cdtnUpdCnt = tchDsbdMapper.selectTchDsbdCdtnUpdCnt(paramData);
        // 최근 기분이 좋지 않은 학생 수 (비상측에서 개발)
        int cdtnCnt = etcMapper.conditionUserListSize(paramData);
        // 최근 목표 설정 입력한 학생 수 (비상측에서 개발)
        int goalCnt = etcMapper.selectUserGoalCnt(paramData);
        // 자기조절학습 검사 상태 (비상측에서 개발)
        String metaStartYn = etcMapper.selectMetaStartYn(paramData);

        String btchUpdDt = tchDsbdMapper.selectBtchUpDt(paramData);

        summaryInfo.put("cdtnUpdCnt", cdtnUpdCnt);
        summaryInfo.put("lowConditionCnt", cdtnCnt);
        summaryInfo.put("goalCnt", goalCnt);
        summaryInfo.put("metaStartYn", metaStartYn);
        summaryInfo.put("btchUpdDt", btchUpdDt);

        rtnMap.put("dsbdCheck",summaryInfo);
        rtnMap.put("dsbdEvlTaskList",dsbdEvlTaskList);

        return rtnMap;
    }

    // 개념별 이해도
    @Transactional(readOnly = true)
    public Object selectTchDsbdConceptUsdList(Map<String, Object> paramData) throws Exception {
        List<String> listItem1 = Arrays.asList(
                "metaId", "unitNum", "kwgMainId", "stdDt", "stdDtLabel", "usdScr"
        );
        List<String> listItem2 = Arrays.asList(
                "metaId", "unitNum","unitNm", "kwgMainId", "kwgNm", "stdAt", "usdScr" ,"unitLastLesnAt", "kwgLastLesnAt"
        );
        Map<Object, Object> rtnMap = new HashMap<>();

        List<LinkedHashMap<Object, Object>> chptUnitKwgCombo; // 단원별 지식요인 정보(콤보박스)
        List<LinkedHashMap<Object, Object>> cncptUsdList; // 단원별 학생 분포 정보

        String metaId = (String) paramData.get("metaId");
        String kwgMainId = (String) paramData.get("kwgMainId");
        String allSrhYn = (String) paramData.get("allSrhYn");

        /* 전체 단원
        단원Id, 지식요인Id 없을 때 */
        if(metaId.isEmpty() && kwgMainId.isEmpty()) {
            // 전체단원 학생 분포 정보
            if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                    || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
                cncptUsdList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdCncptUsdAllUnitList_Main(paramData));
            } else {
                cncptUsdList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdCncptUsdAllUnitList(paramData));
            }

            for (Map cncptUsd : cncptUsdList) {
                // 프론트에서 요청한 정수 처리부분
                cncptUsd.put("usdScr", (int) Math.floor((Double) cncptUsd.get("usdScr")));
            }

            List<Map> kwgComboList = tchDsbdMapper.selectTchDsbdChptUnitKwgCombo(paramData);

            int average = (int) Math.round(kwgComboList.stream()
                    .filter(map -> "Y".equals(map.get("stdAt")))
                    .mapToDouble(map -> (double) map.get("usdScr"))
                    .average()
                    .orElse(0));

            // 전체 콤보 박스 추가
            Map<String, Object> allCombo = Map.of(
                    "kwgMainId", 0,
                    "kwgNm", "전체",
                    "stdAt", "Y",
                    "usdScr", average,
                    "unitLastLesnAt", "N",
                    "kwgLastLesnAt","N"
            );

            List<Map<String, Object>> comboList = List.of(allCombo);

            rtnMap.put("cncptUsdList",cncptUsdList);
            rtnMap.put("chptUnitKwgCombo", comboList);

            return rtnMap;
        }

        /* 단원 선택
         단원Id 있고, 전체 조회 여부가 'Y'가 아니고 지식요인Id가 없을 때
         단원의 지식요인 콤보박스 조회 */
        if(!metaId.isEmpty() && !"Y".equals(allSrhYn) && kwgMainId.isEmpty()) {
            // 단원별 지식요인 정보(콤보박스)
            List<Map> kwgComboList = tchDsbdMapper.selectTchDsbdChptUnitKwgCombo(paramData);

            for (Map kwgCombo : kwgComboList) {
                // 프론트에서 요청한 정수 처리부분
                kwgCombo.put("usdScr", (int) Math.floor((Double) kwgCombo.get("usdScr")));
            }

            // (전체) 추가
            if(CollectionUtils.isNotEmpty(kwgComboList)) {
                CamelHashMap allCombo = new CamelHashMap();
                allCombo.putAll(kwgComboList.get(0));

                int average = (int) Math.round(kwgComboList.stream()
                        .filter(map -> "Y".equals(map.get("stdAt")))
                        .mapToInt(map -> (int) map.get("usdScr"))
                        .average()
                        .orElse(0));

                allCombo.put("kwgMainId", 0);
                allCombo.put("kwgNm", "전체");
                allCombo.put("stdAt", "Y");
                allCombo.put("usdScr", average);
                allCombo.put("unitLastLesnAt", "N");
                allCombo.put("kwgLastLesnAt", "N");

                kwgComboList.add(0, allCombo);
            }
            chptUnitKwgCombo = AidtCommonUtil.filterToList(listItem2, kwgComboList);
            rtnMap.put("chptUnitKwgCombo", chptUnitKwgCombo);

            allSrhYn = "Y";
            rtnMap.put("allSrhYn", allSrhYn);
        }

        List<Map> cncptUsdListSet = new ArrayList<>();

        if ("Y".equals(allSrhYn)) {
            if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                    || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
                cncptUsdListSet = tchDsbdMapper.selectTchDsbdCncptUsdUnitAllKwgList_Main(paramData);
            } else {
                cncptUsdListSet = tchDsbdMapper.selectTchDsbdCncptUsdUnitAllKwgList(paramData);
            }
        } else {
            if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                    || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
                cncptUsdListSet = tchDsbdMapper.selectTchDsbdCncptUsdList_Main(paramData);
            } else {
                cncptUsdListSet = tchDsbdMapper.selectTchDsbdCncptUsdList(paramData);
            }
        }

        for (Map cncptUsd : cncptUsdListSet) {
            // 값의 타입 확인 및 안전한 변환
            if (cncptUsd.containsKey("usdScr")) {
                Object usdScrObj = cncptUsd.get("usdScr");
                double usdScr = 0.0;

                if (usdScrObj instanceof Number) {
                    usdScr = ((Number) usdScrObj).doubleValue();
                }

                // Math.round로 반올림 처리
                int roundedValue = (int) Math.round(usdScr);
                cncptUsd.put("usdScr", roundedValue);
            }
        }

        // 단원별 학생 분포 정보 조회: metaId와 kwgMainId가 있을 경우와 그 외 경우로 나눔
        cncptUsdList = AidtCommonUtil.filterToList(listItem1, cncptUsdListSet);

        // metaId와 kwgMainId를 사용하여 이름 조회
        Set<String> idSet = new HashSet<>();

        // 유효한 ID만 추가
        if (metaId != null && !metaId.isEmpty()) {
            idSet.add(metaId);
        }
        if (kwgMainId != null && !kwgMainId.isEmpty()) {
            idSet.add(kwgMainId);
        }

        // ID가 있는 경우에만 조회
        if (!idSet.isEmpty()) {
            // 한 번의 쿼리로 모든 ID에 대한 정보 조회
            Map<String, Object> param = new HashMap<>();
//            param.put("ids", String.join("','", idSet));
            param.put("idsList", new ArrayList<>(idSet)); //CSAP 25.08.12.lhr
            List<Map> metaInfoList = tchDsbdMapper.findMetaInfoList(param);

            // ID를 키로 하는 맵 생성
            Map<String, String> idNameMap = new HashMap<>();
            for (Map metaInfo : metaInfoList) {
                idNameMap.put(metaInfo.get("id").toString(), metaInfo.get("val").toString());
            }

            // 조회된 이름 정보를 응답에 추가
            if (metaId != null && !metaId.isEmpty() && idNameMap.containsKey(metaId)) {
                rtnMap.put("unitName", idNameMap.get(metaId));
            }

            if (kwgMainId != null && !kwgMainId.isEmpty() && idNameMap.containsKey(kwgMainId)) {
                rtnMap.put("kwgName", idNameMap.get(kwgMainId));
            }
        }



        rtnMap.put("cncptUsdList",cncptUsdList);

        return rtnMap;
    }

    // 개념별 이해도 상세
    @Transactional(readOnly = true)
    public Object selectTchDsbdConceptUsdDetail(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> listItem = Arrays.asList(
                "stdDt", "gdUsdScrCnt","avUsdScrCnt", "bdUsdScrCnt"
        );
        List<String> listItem1 = Arrays.asList(
                "stdtId", "flnm", "claId", "usdScr", "resultGradeNm"
        );

        Map<Object, Object> rtnMap;
        List<LinkedHashMap<Object, Object>> cncptStdtList;

        String metaId = (String) paramData.get("metaId");
        String kwgMainId = (String) paramData.get("kwgMainId");

        // 전체단원 선택 시
        if (StringUtils.isEmpty(metaId) && StringUtils.isEmpty(kwgMainId)) {
            rtnMap = AidtCommonUtil.filterToMap(listItem, tchDsbdMapper.selectTchDsbdCncptUsdAllUnitGradeCnt(paramData));
            cncptStdtList = processConceptStudentList(tchDsbdMapper.selectTchDsbdConceptUsdAllUnitDetail(paramData));
        }
        // 단원 및 지식요인 선택 시
        else if (!StringUtils.isEmpty(metaId) && !StringUtils.isEmpty(kwgMainId)) {
            rtnMap = AidtCommonUtil.filterToMap(listItem, tchDsbdMapper.selectTchDsbdConceptUsdCnt(paramData));
            List<Map> rawCncptStdtList = tchDsbdMapper.selectTchDsbdConceptUsdDetail(paramData);
            cncptStdtList = processConceptStudentList(rawCncptStdtList);
        }
        // 단원 선택 or 지식요인 전체 선택 시
        else {
            rtnMap = AidtCommonUtil.filterToMap(listItem, tchDsbdMapper.selectTchDsbdCncptUsdUnitAllKwgGradeCnt(paramData));
            cncptStdtList = processConceptStudentList(tchDsbdMapper.selectTchDsbdConceptUsdAllKwgDetail(paramData));
        }

        rtnMap.put("cncptStdtList", cncptStdtList);

        return rtnMap;
    }

    private List<LinkedHashMap<Object, Object>> processConceptStudentList(List<Map> rawList) {
        // Process and round the scores
        for (Map student : rawList) {
            if (student.containsKey("usdScr")) {
                Object usdScrObj = student.get("usdScr");
                double usdScr = 0.0;

                if (usdScrObj instanceof Number) {
                    usdScr = ((Number) usdScrObj).doubleValue();
                }

                // Math.round로 반올림 처리
                int roundedValue = (int) Math.round(usdScr);
                student.put("usdScr", roundedValue);

                // 등급 재계산
                String resultGradeNm;
                if (roundedValue >= 80) {
                    resultGradeNm = "상";
                } else if (roundedValue >= 50) {
                    resultGradeNm = "중";
                } else {
                    resultGradeNm = "하";
                }
                student.put("resultGradeNm", resultGradeNm);
            }
        }

        return AidtCommonUtil.filterToList(
                Arrays.asList("stdtId", "flnm", "claId", "usdScr", "resultGradeNm"),
                rawList
        );
    }

    /**
     * 영역별 이해도
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getTchDsbdStatusAreausdList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "areaId", "areaNm"
        );
        List<String> listItem2 = Arrays.asList(
                "areaId", "areaNm", "usdScr"
        );

        List<LinkedHashMap<Object, Object>> contAreaList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStatusAreausdContAreaList(paramData));
        List<LinkedHashMap<Object, Object>> areaUsdList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdStatusAreausdAreaUsdList(paramData));

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
    public Object getTchDsbdStatusAreausdDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        int gdUsdScrCnt = 0;
        int avUsdScrCnt = 0;
        int bdUsdScrCnt = 0;

        Map<Object, Object> areaNm = tchDsbdMapper.selectAreaName(paramData);
        if(MapUtils.isNotEmpty(areaNm)) {
           returnMap.put("areaNm", MapUtils.getString(areaNm, "areaNm"));
        } else {
           returnMap.put("areaNm", "");
        }

        List<Map> resultList = tchDsbdMapper.selectTchDsbdStatusAreausdDetail(paramData);

        if(resultList.size() > 0) {
            for(Map<String, Object> temp:resultList) {
                switch(MapUtils.getString(temp, "scoreRange")) {
                    case "상" :
                        gdUsdScrCnt = MapUtils.getInteger(temp, "count");
                        break;
                    case "중" :
                        avUsdScrCnt = MapUtils.getInteger(temp, "count");
                        break;
                    case "하" :
                        bdUsdScrCnt = MapUtils.getInteger(temp, "count");
                        break;
                }
            }
        }

        returnMap.put("gdUsdScrCnt", gdUsdScrCnt);
        returnMap.put("avUsdScrCnt", avUsdScrCnt);
        returnMap.put("bdUsdScrCnt", bdUsdScrCnt);


        //영역별 학생 이해도 정보
        List<String> listItem2 = Arrays.asList(
                        "stdtId", "flnm", "claId", "usdScr", "resultGradeNm"
                );
        List<LinkedHashMap<Object, Object>> areaStdtList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdStatusAreausdDetailAreaStdtList(paramData));
        returnMap.put("areaStdtList", areaStdtList);
        return returnMap;
    }

    /**
     * 학습맵 이해도
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdChptUnitInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "metaId", "unitNum", "kwgMainId", "kwgNm", "usdScr",
                "prevMetaId", "prevUnitNum", "prevKwgMainId", "prevKwgNm", "prevUsdScr"
        );

        //Map<Object, Object> chptUnitInfo = AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdChptUnitInfo(paramData));
        List<LinkedHashMap<Object, Object>> stdMapUsdList = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdStdMapUsdList(paramData));

        returnMap.put("stdMapUsdList", stdMapUsdList);

        return returnMap;
    }



    /**
     * 학습맵 이해도 (개념)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStdCncptUsdInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "kwgNm", "kwgUsdScr", "cncptCurri", "kwgMainId"
        );

        List<String> listItem = Arrays.asList(
                "kwgMainId", "kwgNm", "kwgUsdScr"
        );

        Map<Object, Object> cncptUsdInfo = AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdStdCncptUsdInfo(paramData));
        List<LinkedHashMap<Object, Object>> kwgUsdList = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdStdMapKwgList(paramData));

        cncptUsdInfo.put("kwgUsdList",kwgUsdList);
        returnMap.put("cncptUsdInfo", cncptUsdInfo);

        return returnMap;
    }


    /**
     * 학습맵 이해도 상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStdMapUsdInfo(Map<String, Object> paramData) throws Exception {
        Map<Object, Object> returnMap = new HashMap<>();

        List<String> listItem1 = Arrays.asList(
               "kwgNm", "gdUsdScrCnt", "avUsdScrCnt", "bdUsdScrCnt"
        );
        List<String> listItem2 = Arrays.asList(
               "stdtId", "flnm", "claId", "usdScr", "resultGradeNm"
        );

        Map<Object, Object> chptUnitInfo = AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdStdMapUsdInfo(paramData));
        List<LinkedHashMap<Object, Object>> cncptStdtList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdStdMapCncptStdtList(paramData));

        returnMap =  chptUnitInfo;
        returnMap.put("cncptStdtList", cncptStdtList);

        return returnMap;
    }

    /**
     * 자주쓰는문장(등록)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> tchDsbdOftensentsSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int insertResult = tchDsbdMapper.createTchOftensents(paramData);

        if(insertResult>0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * 자주쓰는문장(수정)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> tchDsbdOftensentsMod(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int insertResult = tchDsbdMapper.modifyTchOftensents(paramData);

        if(insertResult>0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * 자주쓰는문장(삭제)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> tchDsbdOftensentsDel(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int insertResult = tchDsbdMapper.deleteTchOftensents(paramData);

        if(insertResult>0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }


    /**
     * (교사).자주쓰는문장(목록)
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Object
     */
    @Transactional(readOnly = true)
    public Object tchDsbdOftensentsList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        List<String> itemList = Arrays.asList("no", "sentsId", "sents", "wrtYmd");

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        //select
        List<Map> resultList = tchDsbdMapper.selectTchOftensents(pagingParam);
        List<LinkedHashMap<Object, Object>> oftensentsList = new ArrayList<>();

        if(resultList.size() > 0) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
            oftensentsList = AidtCommonUtil.filterToList(itemList, resultList);
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("resultList", oftensentsList);
        returnMap.put("page",page);
        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 그래프
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
            "unitNum", "metaId", "unitNm"
        );

        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materAreaUnitInfo = AidtCommonUtil.filterToList(listItem, areaAchievementUnitInfo);

        List<String> listItem1 = Arrays.asList(
            "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy", "articleList", "articleCnt"
        );

        List<Map> tempTchDsbdAreaAchievementList = tchDsbdMapper.selectTchDsbdAreaAchievementList(paramData);
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

        int areaAchievementCount = tchDsbdMapper.selectTchDsbdAreaAchievementCount(paramData);

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

        for (Map map : tempTchDsbdAreaAchievementList) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }
//        List<LinkedHashMap<Object, Object>> areaAchievementList =  AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementList(paramData));
        List<LinkedHashMap<Object, Object>> areaAchievementList = AidtCommonUtil.filterToList(listItem1, tempTchDsbdAreaAchievementList);

        int areaAchievementCount = tchDsbdMapper.selectTchDsbdAreaAchievementCountAll(paramData);

        // 가장 높은 usdAchScrPercent를 가진 영역 찾기 (rflt_actv_cnt > 0인 경우만)
        Map highestAchievedArea = areaAchievementList.stream()
                .filter(m -> MapUtils.getDoubleValue(m, "rfltActvCnt", 0) > 0)
                .max(Comparator.comparingDouble(m -> MapUtils.getDoubleValue(m, "usdAchScrPercent", 0)))
                .orElse(null);

        // 가장 낮은 usdAchScrPercent를 가진 영역 찾기 (rflt_actv_cnt > 0 > 0인 경우만)
        Map lowestAchievedArea = areaAchievementList.stream()
                .filter(m -> MapUtils.getDoubleValue(m, "rfltActvCnt", 0) > 0)
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
     * [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
            "upper", "middle", "lower"
        );

        List<String> listItem2 = Arrays.asList(
            "stdtId", "flnm", "usdAchScr", "dfcltLvlTy", "studyYn"
        );

        /*
        if (ObjectUtils.isEmpty(MapUtils.getString(paramData, "stdDtYmd"))) {
            Map<Object, Object> areaAchievementCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementCountDetail(paramData));
            List<LinkedHashMap<Object, Object>> areaAchievementStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdAreaAchievementStudentList(paramData));
            returnMap.put("AreaAchievementCountDetail", areaAchievementCountDetail);
            returnMap.put("AreaAchievementStudentList", areaAchievementStudentList);
        } else {
            Map<Object, Object> areaAchievementCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementCountDetail_daily(paramData));
            List<LinkedHashMap<Object, Object>> areaAchievementStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdAreaAchievementStudentList_daily(paramData));
            returnMap.put("AreaAchievementCountDetail", areaAchievementCountDetail);
            returnMap.put("AreaAchievementStudentList", areaAchievementStudentList);
        }
         */

        Map<Object, Object> areaAchievementCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementCountDetail(paramData));
        List<LinkedHashMap<Object, Object>> areaAchievementStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdAreaAchievementStudentList(paramData));
        returnMap.put("AreaAchievementCountDetail", areaAchievementCountDetail);
        returnMap.put("AreaAchievementStudentList", areaAchievementStudentList);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 All
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementDetailAll(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<Map> EvalDetail = new ArrayList<>();

        Set<String> validEvaluationAreas = Set.of("listening", "reading", "viewing", "speaking", "writing", "presenting");
        String evaluationAreaCd = (String) paramData.get("evaluationAreaCd");

        List<String> listItem1 = Arrays.asList(
                "upper", "middle", "lower"
        );

        List<String> listItem2 = Arrays.asList(
                "stdtId", "flnm", "usdAchScr", "dfcltLvlTy", "studyYn"
        );

        // "listening", "reading", "viewing", "speaking", "writing", "presenting"
        if (validEvaluationAreas.contains(evaluationAreaCd)) {
            // 영역별 그래프 상세 (상/중/하)
            Map<Object, Object> areaAchievementCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementCountDetailAll(paramData));

            // 영역별 학생 목록
            List<LinkedHashMap<Object, Object>> areaAchievementStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdAreaAchievementStudentListAll(paramData));

            for (Map map : areaAchievementStudentList) {
                // usdAchScrPercent 반올림
                if (map.containsKey("usdAchScr")) {
                    double usdAchScrPercent = (double) map.get("usdAchScr");
                    int roundedValue = (int) Math.round(usdAchScrPercent);
                    map.put("usdAchScr", roundedValue);
                }
            }

            returnMap.put("AreaAchievementCountDetail", areaAchievementCountDetail);
            returnMap.put("AreaAchievementStudentList", areaAchievementStudentList);

            // 학습맵 > 성취 기준 상세
            Map result = findTchDsbdStatusAreaAchievementDetailInfoAll(paramData);
            EvalDetail.add(result);

            returnMap.put("EvalDetail", EvalDetail);
        } else {
            // 단어/문법/발음 > vocabulary, grammar, pronunciation 상세(상/중/하)
            //Map<Object, Object> vocabularyCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdVocabularyCountDetailAll(paramData));
            Map<Object, Object> vocabularyCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdCountDetailAll(paramData));

            // 단어/문법/발음 > vocabulary, grammar, pronunciation 상세(학생 목록)
            //List<LinkedHashMap<Object, Object>> vocabularyStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdVocabularyStudentListAll(paramData));
            List<LinkedHashMap<Object, Object>> vocabularyStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdStudentListAll(paramData));

            for (Map map : vocabularyStudentList) {
                // usdAchScrPercent 반올림
                if (map.containsKey("usdAchScr")) {
                    double usdAchScrPercent = (double) map.get("usdAchScr");
                    int roundedValue = (int) Math.round(usdAchScrPercent);
                    map.put("usdAchScr", roundedValue);
                }
            }

            returnMap.put("AreaAchievementCountDetail", vocabularyCountDetail);
            returnMap.put("AreaAchievementStudentList", vocabularyStudentList);

            Map result = selectTchDsbdStatusVocabularyListAll(paramData);
            List<Map<String, Object>> vocabularyList = (List<Map<String, Object>>) result.get("VocabularyList");

            if (vocabularyList != null && !vocabularyList.isEmpty()) {
                for (Map<String, Object> vocabMap : vocabularyList) {
                    if (vocabMap.containsKey("totalUsdSrc")) {
                        double usdAchScr = (double) vocabMap.get("totalUsdSrc");
                        int roundedValue = (int) Math.round(usdAchScr);
                        vocabMap.put("totalUsdSrc", roundedValue);
                    }
                }
            }

            EvalDetail.add(result);

            // 단어/문법/발음 성취 상세
            returnMap.put("EvalDetail", EvalDetail);
            }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object selectTchDsbdStatisticAchievementList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Set<String> validEvaluationAreas = Set.of("listening", "reading", "viewing", "speaking", "writing", "presenting");
        String evaluationAreaCd = (String) paramData.get("evaluationAreaCd");

        List<String> listItem1 = Arrays.asList(
                "stdDt","stdDtYmd","usdAchScr"
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

        // "listening", "reading", "viewing", "speaking", "writing", "presenting"
        if (validEvaluationAreas.contains(evaluationAreaCd)) {

            // 성취도 추이
            List<LinkedHashMap<Object, Object>> statisticAchList = new ArrayList<>();
            if (StringUtils.equals(serverEnv, "math-release") || StringUtils.equals(serverEnv, "engl-release")
                    || StringUtils.equals(serverEnv, "math-prod")|| StringUtils.equals(serverEnv, "engl-prod")){
                statisticAchList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStatisticAchievementList1_main(paramData));
            }else {
                statisticAchList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStatisticAchievementList1(paramData));
            }

            returnMap.put("statisticAchList", statisticAchList);

        } else {
            // Vocabulary, Grammar, Pronunciation

            List<LinkedHashMap<Object, Object>> statisticAchList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStatisticAchievementList2(paramData));

            returnMap.put("statisticAchList", statisticAchList);
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusVocabularyList(Map<String, Object> paramData, Pageable pageable) throws Exception {

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
        List<Map> vocabularyUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> vocabularyUnitInfoMap = new LinkedHashMap<>();
        vocabularyUnitInfoMap.put("unitNum", 0);
        vocabularyUnitInfoMap.put("metaId", 0);
        vocabularyUnitInfoMap.put("unitNm", "전 단원");
        vocabularyUnitInfo.add(0, vocabularyUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materVocabularyUnitInfo = AidtCommonUtil.filterToList(listItem, vocabularyUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "rowNum", "usdAchId", "totalUsdSrc", "totalLvlTy", "iemId", "iemCd", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy"
        );

        List<Map> resultList = tchDsbdMapper.selectTchDsbdStatusVocabularyList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> vocabularyList = AidtCommonUtil.filterToList(listItem1, resultList);

        int vocabularyCount = tchDsbdMapper.selectTchDsbdStatusVocabularyCount(paramData);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("vocabularyUnitInfo", materVocabularyUnitInfo);
        returnMap.put("VocabularyCount", vocabularyCount);
        returnMap.put("VocabularyList", vocabularyList);
        returnMap.put("page",page);

        return returnMap;

    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map selectTchDsbdStatusVocabularyListAll(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm","unit"
        );

        paramData.put("isProject", 0);
        List<Map> vocabularyUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        LinkedHashMap<Object, Object> vocabularyUnitInfoMap = new LinkedHashMap<>();
        vocabularyUnitInfoMap.put("unitNum", 0);
        vocabularyUnitInfoMap.put("metaId", 0);
        vocabularyUnitInfoMap.put("unitNm", "전 단원");
        vocabularyUnitInfo.add(0, vocabularyUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materVocabularyUnitInfo = AidtCommonUtil.filterToList(listItem, vocabularyUnitInfo);

        List<String> listItem1 = Arrays.asList(
                "rowNum", "metaId", "usdAchId", "totalUsdSrc", "totalLvlTy", "iemId", "iemCd", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy"
        );

        List<Map> resultList = tchDsbdMapper.selectTchDsbdStatusVocabularyListAll(paramData);

        List<LinkedHashMap<Object, Object>> vocabularyList = AidtCommonUtil.filterToList(listItem1, resultList);

        int vocabularyCount = tchDsbdMapper.selectTchDsbdStatusVocabularyCountAll(paramData);

        returnMap.put("vocabularyUnitInfo", materVocabularyUnitInfo);
        returnMap.put("VocabularyCount", vocabularyCount);
        returnMap.put("VocabularyList", vocabularyList);

        return returnMap;

    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusVocabularyDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "iemCd", "upper", "middle", "lower"
        );

        Map<Object, Object> vocabularyCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdVocabularyCountDetail(paramData));

        List<String> listItem2 = Arrays.asList(
                "stdtId", "flnm", "usdAchScr", "dfcltLvlTy", "studyYn"
        );

        List<LinkedHashMap<Object, Object>> vocabularyStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdVocabularyStudentList(paramData));

        returnMap.put("vocabularyCountDetail", vocabularyCountDetail);
        returnMap.put("vocabularyStudentList", vocabularyStudentList);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusGrammarList(Map<String, Object> paramData, Pageable pageable) throws Exception {
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
        List<Map> grammarUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

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

        List<Map> resultList = tchDsbdMapper.selectTchDsbdStatusGrammarList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> grammarList = AidtCommonUtil.filterToList(listItem1, resultList);

        int grammarCount = tchDsbdMapper.selectTchDsbdStatusGrammarCount(paramData);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("GrammarUnitInfo", headGrammarUnitInfo);
        returnMap.put("GrammarCount", grammarCount);
        returnMap.put("GrammarList", grammarList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusGrammarDetail(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "iemCd", "upper", "middle", "lower"
        );

        Map<Object, Object> GrammarCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdGrammarCountDetail(paramData));

        List<String> listItem2 = Arrays.asList(
                "stdtId", "flnm", "usdAchScr", "dfcltLvlTy", "studyYn"
        );

        List<LinkedHashMap<Object, Object>> GrammarStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdGrammarStudentList(paramData));

        returnMap.put("GrammarCountDetail", GrammarCountDetail);
        returnMap.put("GrammarStudentList", GrammarStudentList);

        return returnMap;

    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusPronunciationList(Map<String, Object> paramData, Pageable pageable) throws Exception {

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
        List<Map> pronunciationUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

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

        List<Map> resultList = tchDsbdMapper.selectTchDsbdStatusPronunciationList(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> pronunciationList = AidtCommonUtil.filterToList(listItem1, resultList);

        int pronunciationCount = tchDsbdMapper.selectTchDsbdStatusPronunciationCount(paramData);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("PronunciationUnitInfo", headPronunciationUnitInfo);
        returnMap.put("PronunciationCount", pronunciationCount);
        returnMap.put("PronunciationList", pronunciationList);
        returnMap.put("page",page);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusPronunciationDetail(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "iemCd", "upper", "middle", "lower"
        );

        Map<Object, Object> PronunciationCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdPronunciationCountDetail(paramData));

        List<String> listItem2 = Arrays.asList(
                "stdtId", "flnm", "usdAchScr", "dfcltLvlTy", "studyYn"
        );

        List<LinkedHashMap<Object, Object>> PronunciationStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdPronunciationStudentList(paramData));

        returnMap.put("PronunciationCountDetail", PronunciationCountDetail);
        returnMap.put("PronunciationStudentList", PronunciationStudentList);

        return returnMap;

    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusStudyMapAchievementStandardList(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectTchDsbdUnitInfoList = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();
        selectTchDsbdUnitInfoMap.put("unitNum", 0);
        selectTchDsbdUnitInfoMap.put("metaId", 0);
        selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectTchDsbdUnitInfoList.add(0, selectTchDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> achievementStandardUnitInfo = AidtCommonUtil.filterToList(listItem, selectTchDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "parentAcNm", "acCd","acNm", "fullAcNm", "depth", "usdScr", "studyMapCd", "kwgTotCount", "val"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> achievementStandardList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStdMapAchievementStandardList(paramData));

        // usdScr 값 처리
        for (Map map : achievementStandardList) {
            if (map.containsKey("usdScr")) {
                Object value = map.get("usdScr");
                if (value instanceof String && "-".equals(value)) {
                    continue;
                }
                double usdScr = value instanceof String ? Double.parseDouble((String) value) : (Double) value;
                map.put("usdScr", (int) Math.round(usdScr));
            }
        }

        int achievementStandardCount = tchDsbdMapper.selectTchDsbdStdMapAchievementStandardCount(paramData);

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
     * [교사] 학급관리 > 홈 대시보드 > 학습맵 > 소재
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusStudyMapMaterialList(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectTchDsbdUnitInfoList = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();
        selectTchDsbdUnitInfoMap.put("unitNum", 0);
        selectTchDsbdUnitInfoMap.put("metaId", 0);
        selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectTchDsbdUnitInfoList.add(0, selectTchDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> materialUnitInfo = AidtCommonUtil.filterToList(listItem, selectTchDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "maNm", "fullMaNm", "depth", "usdScr", "studyMapCd", "kwgTotCount"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> materialList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStdMapMaterialList(paramData));

        for (Map map : materialList) {
            if (map.containsKey("usdScr")) {
                Object value = map.get("usdScr");

                // "-" 문자열인 경우는 건너뛰기
                if (value instanceof String) {
                    if ("-".equals(value)) {
                        continue;
                    }
                    // 문자열을 double로 변환
                    double usdScr = Double.parseDouble((String) value);
                    int roundedValue = (int) Math.round(usdScr);
                    map.put("usdScr", roundedValue);
                } else if (value instanceof Double) {
                    // 이미 Double인 경우
                    double usdScr = (Double) value;
                    int roundedValue = (int) Math.round(usdScr);
                    map.put("usdScr", roundedValue);
                }
            }
        }

        int materialCount = tchDsbdMapper.selectTchDsbdStdMapMaterialCount(paramData);

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
     * [교사] 학급관리 > 홈 대시보드 > 학습맵 > 의사소통 기능
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusStudyMapCommunicationList(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectTchDsbdUnitInfoList = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();
        selectTchDsbdUnitInfoMap.put("unitNum", 0);
        selectTchDsbdUnitInfoMap.put("metaId", 0);
        selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectTchDsbdUnitInfoList.add(0, selectTchDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> communicationUnitInfo = AidtCommonUtil.filterToList(listItem, selectTchDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "coNm", "fullCoNm", "depth", "usdScr", "studyMapCd", "kwgTotCount"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> communicationList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStdMapCommunicationList(paramData));

        for (Map map : communicationList) {
            if (map.containsKey("usdScr")) {
                Object value = map.get("usdScr");

                // "-" 문자열인 경우는 건너뛰기
                if (value instanceof String) {
                    if ("-".equals(value)) {
                        continue;
                    }
                    // 문자열을 double로 변환
                    double usdScr = Double.parseDouble((String) value);
                    int roundedValue = (int) Math.round(usdScr);
                    map.put("usdScr", roundedValue);
                } else if (value instanceof Double) {
                    // 이미 Double인 경우
                    double usdScr = (Double) value;
                    int roundedValue = (int) Math.round(usdScr);
                    map.put("usdScr", roundedValue);
                }
            }
        }

        int communicationCount = tchDsbdMapper.selectTchDsbdStdMapCommunicationCount(paramData);

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
     * [교사] 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusStudyMapLanguageFormatList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        paramData.put("isProject", 0);
        List<Map> selectTchDsbdUnitInfoList = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();
        selectTchDsbdUnitInfoMap.put("unitNum", 0);
        selectTchDsbdUnitInfoMap.put("metaId", 0);
        selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectTchDsbdUnitInfoList.add(0, selectTchDsbdUnitInfoMap);

        List<LinkedHashMap<Object, Object>> languageFormatUnitInfo = AidtCommonUtil.filterToList(listItem, selectTchDsbdUnitInfoList);

        List<String> listItem1 = Arrays.asList(
                "metaId", "parentId", "code", "laNm", "fullLaNm", "depth", "usdScr", "studyMapCd", "kwgTotCount"
        );

        String unitCode = tchDsbdMapper.selectTchDsbdUnitCode(paramData);
        paramData.put("unitCode", unitCode);

        List<LinkedHashMap<Object, Object>> languageFormatList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStdMapLanguageFormatList(paramData));

        for (Map map : languageFormatList) {
            if (map.containsKey("usdScr")) {
                Object value = map.get("usdScr");

                // "-" 문자열인 경우는 건너뛰기
                if (value instanceof String) {
                    if ("-".equals(value)) {
                        continue;
                    }
                    // 문자열을 double로 변환
                    double usdScr = Double.parseDouble((String) value);
                    int roundedValue = (int) Math.round(usdScr);
                    map.put("usdScr", roundedValue);
                } else if (value instanceof Double) {
                    // 이미 Double인 경우
                    double usdScr = (Double) value;
                    int roundedValue = (int) Math.round(usdScr);
                    map.put("usdScr", roundedValue);
                }
            }
        }

        int languageFormatCount = tchDsbdMapper.selectTchDsbdStdMapLanguageFormatCount(paramData);


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

    // [교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 상세
    @Transactional(readOnly = true)
    public Object findTchDsbdStatusAreaAchievementDetailInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<Map> studyMapDetailmap = tchDsbdMapper.findTchDsbdTargetArticle(paramData);

        if (null != studyMapDetailmap) {
            /*articleList - articleId 오름차순 정렬*/
            for (Map<String, Object> detailMap : studyMapDetailmap) {
                if (detailMap.containsKey("articleList") && detailMap.containsKey("usdClsfCd")) {
                    String articleListStr = (String) detailMap.get("articleList");
                    Integer usdClsfCd = (Integer) detailMap.get("usdClsfCd");

                    List<String> articleList = Arrays.asList(articleListStr.split(","));

                    // usdClsfCd 값에 따른 정렬
                    List<String> sortedArticleList = articleList;
                    if (usdClsfCd == 1 || usdClsfCd == 2 || usdClsfCd == 3 || usdClsfCd == 4) {
                        sortedArticleList = articleList.stream()
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList());
                    }

                    String sortedArticleListStr = String.join(",", sortedArticleList);

                    detailMap.put("articleList", sortedArticleListStr);
                }
            }
        }
        // 정렬된 리스트를 문자열로 변환
        String articleList = studyMapDetailmap.stream()
                .map(map -> (String) map.get("articleList"))
                .collect(Collectors.joining(","));

        String articles[] = articleList.split(",");
        paramData.put("articles",articles);

        List<Map> detailInfolist = new ArrayList<Map>();

        Set<String> seenParameters = new HashSet<>();
        if(articles.length > 0 ){
            for (int ii = 0 ;  ii < articles.length; ii++) {
                if (articles[ii].contains("-")) {
                    String[] item = articles[ii].split("-");
                    String articleId = item[0];
                    String subId = item[1];

                    String key = articleId + "-" + subId; // 중복체크를 위한 키 생성
                    if (!seenParameters.contains(key)) {
                        seenParameters.add(key);
                        paramData.put("articleId", articleId);
                        paramData.put("subId", subId);

                        Map<Object, Object> tempMap = tchDsbdMapper.findTchDsbdStatusAreaAchievementDetailInfo(paramData);
                        detailInfolist.add(tempMap);
                    }
                }
            }
        }
        /*교과서, 비교과서 정렬*/
        Comparator<Map> comparator = Comparator
                .comparing((Map m) -> m.get("metaId").toString());

        detailInfolist.sort(comparator);

        Integer textbookId = MapUtils.getInteger(paramData, "textbookId");
        if (ObjectUtils.isEmpty(textbookId)) {
            paramData.put("textbookId", MapUtils.getInteger(paramData, "textbkId"));
        }

        returnMap.put("evalutionAreaNm", MapUtils.getString(paramData, "evaluationAreaCd"));

        if (ObjectUtils.isNotEmpty(detailInfolist)) {
            returnMap.put("stdAreaDetailList", detailInfolist);
            returnMap.put("rfltTotCnt", detailInfolist.size());
        } else {
            List<String> listItem1 = Arrays.asList(
                    "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy", "articleCnt"
            );

            returnMap.put("stdAreaDetailList", null);
            returnMap.put("rfltTotCnt", 0);

            List<LinkedHashMap<Object, Object>> areaAchievementList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementList(paramData));
            int areaAchievementCount = tchDsbdMapper.selectTchDsbdAreaAchievementCount(paramData);

            returnMap.put("AreaAchievementCount", areaAchievementCount);
            returnMap.put("AreaAchievementList", areaAchievementList);
        }

        return returnMap;
    }

    // [교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 상세
    @Transactional(readOnly = true)
    public Map findTchDsbdStatusAreaAchievementDetailInfoAll(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<Map> studyMapDetailmap = tchDsbdMapper.findTchDsbdTargetArticle(paramData);

        if (null != studyMapDetailmap) {
            /*articleList - articleId 오름차순 정렬*/
            for (Map<String, Object> detailMap : studyMapDetailmap) {
                if (detailMap.containsKey("articleList") && detailMap.containsKey("usdClsfCd")) {
                    String articleListStr = (String) detailMap.get("articleList");
                    Integer usdClsfCd = (Integer) detailMap.get("usdClsfCd");

                    List<String> articleList = Arrays.asList(articleListStr.split(","));

                    // usdClsfCd 값에 따른 정렬
                    List<String> sortedArticleList = articleList;
                    if (usdClsfCd == 1 || usdClsfCd == 2 || usdClsfCd == 3 || usdClsfCd == 4) {
                        sortedArticleList = articleList.stream()
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList());
                    }

                    String sortedArticleListStr = String.join(",", sortedArticleList);

                    detailMap.put("articleList", sortedArticleListStr);
                }
            }
        }
        // 정렬된 리스트를 문자열로 변환
        String articleList = studyMapDetailmap.stream()
                .map(map -> (String) map.get("articleList"))
                .collect(Collectors.joining(","));

        String articles[] = articleList.split(",");
        paramData.put("articles",articles);

        List<Map> detailInfolist = new ArrayList<Map>();

        Set<String> seenParameters = new HashSet<>();
        if(articles.length > 0 ){
            for (int ii = 0 ;  ii < articles.length; ii++) {
                if (articles[ii].contains("-")) {
                    String[] item = articles[ii].split("-");
                    String articleId = item[0];
                    String subId = item[1];

                    String key = articleId + "-" + subId; // 중복체크를 위한 키 생성
                    if (!seenParameters.contains(key)) {
                        seenParameters.add(key);
                        paramData.put("articleId", articleId);
                        paramData.put("subId", subId);

                        Map<Object, Object> tempMap = tchDsbdMapper.findTchDsbdStatusAreaAchievementDetailInfo(paramData);
                        detailInfolist.add(tempMap);
                    }
                }
            }
        }
        /*교과서, 비교과서 정렬*/
        Comparator<Map> comparator = Comparator
                .comparing((Map m) -> m.get("metaId").toString());

        detailInfolist.sort(comparator);

        Integer textbookId = MapUtils.getInteger(paramData, "textbookId");
        if (ObjectUtils.isEmpty(textbookId)) {
            paramData.put("textbookId", MapUtils.getInteger(paramData, "textbkId"));
        }

        returnMap.put("evalutionAreaNm", MapUtils.getString(paramData, "evaluationAreaCd"));

        if (ObjectUtils.isNotEmpty(detailInfolist)) {
            returnMap.put("stdAreaDetailList", detailInfolist);
            returnMap.put("rfltTotCnt", detailInfolist.size());
        } else {
            List<String> listItem1 = Arrays.asList(
                    "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "dfcltLvlTy"
            );

            returnMap.put("stdAreaDetailList", null);
            returnMap.put("rfltTotCnt", 0);

//            List<LinkedHashMap<Object, Object>> areaAchievementList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdAreaAchievementListAll(paramData));
//            int areaAchievementCount = tchDsbdMapper.selectTchDsbdAreaAchievementCount(paramData);
//
//            returnMap.put("AreaAchievementCount", areaAchievementCount);
//            returnMap.put("AreaAchievementList", areaAchievementList);
        }

        return returnMap;
    }

    // [교사] 학급관리 > 홈 대시보드 > 학습맵 > 소재 상세
    @Transactional(readOnly = true)
    public Object findTchDsbdStatusStudyMapDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 입력된 paramData의 unitNum 존재 하지 않거나 0인 경우 전체로 처리
        int unitNum = MapUtils.getIntValue(paramData, "unitNum", 0);
        if (unitNum == 0) {
            returnMap.put("unitNm", "전 단원");
            returnMap.put("kwgAchNum", 0);
        } else {
            Map<Object, Object> studyMapDetailmap = tchDsbdMapper.findTchDsbdStatusStudyMapDetail(paramData);
            returnMap.put("unitNm", MapUtils.getString(studyMapDetailmap, "unitNm"));
            returnMap.put("kwgAchNum", MapUtils.getInteger(studyMapDetailmap, "kwgAchNum"));
        }

        List<Map> studyMapDetaillist = tchDsbdMapper.findTchDsbdStatusStudyMapDetail_list(paramData);
        returnMap.put("kwgTotCount", studyMapDetaillist.size());
        returnMap.put("stdMapDetailList", studyMapDetaillist);


        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusMathaitutor(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItemResult = Arrays.asList(
                 "flnm", "usdAchScr", "dfcltLvlTy", "stdtId"
        );

        List<String> listItem = Arrays.asList(
                "unitNm"
        );

        paramData.put("isProject", 0);

        int unitNum = MapUtils.getInteger(paramData, "unitNum", 0 );

        // 단원명 조회
        Map<Object, Object> selectTchDsbdAiTutorUnitInfo = tchDsbdMapper.selectTchDsbdAiTutorUnitInfo(paramData);

        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();


        if (ObjectUtils.isEmpty(selectTchDsbdAiTutorUnitInfo)) {
            selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");

            returnMap.put("unitNm", selectTchDsbdUnitInfoMap);
        } else {
            Map<Object, Object> achievementStandardUnitInfo = AidtCommonUtil.filterToMap(listItem, selectTchDsbdAiTutorUnitInfo);

            returnMap.put("unitNm", achievementStandardUnitInfo);
        }

        // 성취도 '상', '중', '하' 학생 총 카운트
        int totalStntCount = tchDsbdMapper.selectTchDsbdStatusMathStnCount(paramData);
        returnMap.put("totalStntCount", totalStntCount);

        // 이해도 '상' 학생 3명
        List<Map> bestStntList = tchDsbdMapper.selectTchDsbdStatusMathBestStntList(paramData);
        for (Map bestStnt : bestStntList) {
            if (bestStnt.containsKey("usdAchScr")) {
                Object usdAchScrObj = bestStnt.get("usdAchScr");
                double usdAchScr = 0.0;

                if (usdAchScrObj instanceof Number) {
                    usdAchScr = ((Number) usdAchScrObj).doubleValue();
                }

                // Math.round로 반올림 처리
                int roundedValue = (int) Math.round(usdAchScr);
                bestStnt.put("usdAchScr", roundedValue);
            }
        }

        List<LinkedHashMap<Object, Object>> rstBestStntList = AidtCommonUtil.filterToList(listItemResult, bestStntList);
        returnMap.put("bestStnt", rstBestStntList);
        returnMap.put("bestStntCnt", rstBestStntList.size());

        // 이해도 '중' 학생 3명
        List<Map> mddStntList = tchDsbdMapper.selectTchDsbdStatusMathMddStntList(paramData);
        for (Map mddStnt : mddStntList) {
            if (mddStnt.containsKey("usdAchScr")) {
                Object usdAchScrObj = mddStnt.get("usdAchScr");
                double usdAchScr = 0.0;

                if (usdAchScrObj instanceof Number) {
                    usdAchScr = ((Number) usdAchScrObj).doubleValue();
                }

                // Math.round로 반올림 처리
                int roundedValue = (int) Math.round(usdAchScr);
                mddStnt.put("usdAchScr", roundedValue);
            }
        }

        List<LinkedHashMap<Object, Object>> rstMddStntList = AidtCommonUtil.filterToList(listItemResult, mddStntList);
        returnMap.put("mddStnt", rstMddStntList);
        returnMap.put("mddStntCnt", rstMddStntList.size());

        // 이해도 '하' 학생 3명
        List<Map> worstStntList = tchDsbdMapper.selectTchDsbdStatusMathWorstStntList(paramData);
        for (Map worstStnt : worstStntList) {
            if (worstStnt.containsKey("usdAchScr")) {
                Object usdAchScrObj = worstStnt.get("usdAchScr");
                double usdAchScr = 0.0;

                if (usdAchScrObj instanceof Number) {
                    usdAchScr = ((Number) usdAchScrObj).doubleValue();
                }

                // Math.round로 반올림 처리
                int roundedValue = (int) Math.round(usdAchScr);
                worstStnt.put("usdAchScr", roundedValue);
            }
        }

        List<LinkedHashMap<Object, Object>> rstWorstStntList = AidtCommonUtil.filterToList(listItemResult, worstStntList);
        returnMap.put("worstStnt", rstWorstStntList);
        returnMap.put("worstStntCnt", rstWorstStntList.size());

        return returnMap;
    }


    /**
     * [교사] 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusAreaAchievementaitutor(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "usdAchId", "flnm", "stdtId", "usdAchScr", "dfcltLvlTy","bestStntCnt"
        );
        List<String> listItem2 = Arrays.asList(
                "usdAchId", "flnm", "stdtId", "usdAchScr", "dfcltLvlTy","worstStntCnt"
        );

        List<String> listItem = Arrays.asList(
                "unitNm","unit"
        );

        paramData.put("isProject", 0);
        Map<Object, Object> selectTchDsbdAiTutorUnitInfo = tchDsbdMapper.selectTchDsbdAiTutorUnitInfo(paramData);

        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();

        if (ObjectUtils.isEmpty(selectTchDsbdAiTutorUnitInfo)) {
            selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");
            selectTchDsbdUnitInfoMap.put("unit", "");

            returnMap.put("unitNm", selectTchDsbdUnitInfoMap);
        } else {
            Map<Object, Object> achievementStandardUnitInfo = AidtCommonUtil.filterToMap(listItem, selectTchDsbdAiTutorUnitInfo);

            returnMap.put("unitNm", achievementStandardUnitInfo);
        }
        long bestStntCnt = 0;
        long worstStntCnt = 0;

        // 성취도 '상', '중', '하' 학생 총 카운트
        int totalStntCount = tchDsbdMapper.selectTchDsbdStatusAreaAchievementCount(paramData);
        returnMap.put("totalStntCount", totalStntCount);

        // 성취도 '상' 학생 3명
        List<LinkedHashMap<Object, Object>> bestStntList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStatusAreaAchievementBestStntList(paramData)); //3
        // 성취도 '상' 총 학생
//        int bestStntCount = tchDsbdMapper.selectTchDsbdStatusAreaAchievementBestStntCount(paramData);

        returnMap.put("bestStnt", bestStntList);

        if (!ObjectUtils.isEmpty(bestStntList)) {
            bestStntCnt = (long) bestStntList.get(0).get("bestStntCnt");
        }
        returnMap.put("bestStntCnt", bestStntCnt);

        // 성취도 '하' 학생 3명
        List<LinkedHashMap<Object, Object>> worstStntList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdStatusAreaAchievementWorstStntList(paramData)); //3
        // 성취도 '하' 총 학생
//        int worstStntCount = tchDsbdMapper.selectTchDsbdStatusAreaAchievementWorstStntCount(paramData);

        returnMap.put("worstStnt", worstStntList);

        if (!ObjectUtils.isEmpty(worstStntList)) {
            worstStntCnt = (long) worstStntList.get(0).get("worstStntCnt");
        }
        returnMap.put("worstStntCnt", worstStntCnt);

        return returnMap;

    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 우리반 학습 분석(AI튜터_학습 요약_단원의 성취도 정보 조회)
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusAreaAchievementAitutorUnitInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<String, Object>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        List<String> listItem1 = Arrays.asList(
                "avgUsdAchScrPercent"
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

        // 파라미터로 들어온 unitNum에 해당하는 UnitNm 찾기
        String currentUnitName = "전 단원";
        Object unitNumParam = paramData.get("unitNum");

        if (unitNumParam != null) {
            int requestedUnitNum = unitNumParam instanceof Number ?
                    ((Number)unitNumParam).intValue() :
                    Integer.parseInt(unitNumParam.toString());

            // unitNum이 0인 경우 "전 단원"으로 이미 설정되어 있으므로 0보다 큰 경우만 처리
            if (requestedUnitNum > 0) {
                for (LinkedHashMap<Object, Object> unitInfo : materAreaUnitInfo) {
                    int unitNum = ((Number)unitInfo.get("unitNum")).intValue();
                    if (unitNum == requestedUnitNum) {
                        currentUnitName = (String) unitInfo.get("unitNm");
                        break;
                    }
                }
            }
        }

        // 단원의 학생별 정보 조회
        List<Map> studentData = tchDsbdMapper.selectTchDsbdStatusAreaAchievementStntInfo(paramData);

        // 난이도별 학생 분류
        Map<Integer, List<String>> groupedStudents = studentData.stream()
                .collect(Collectors.groupingBy(
                        student -> ((Number) student.get("dfcltLvlTy")).intValue(), // 난이도 기준으로 그룹화
                        Collectors.mapping(student -> (String) student.get("stdtId"), Collectors.toList()) // 학생 ID 리스트로 매핑
                ));

        // 단원 성취도 정보 조회
        List<Map> selectTchDsbdUnitAchievementList = tchDsbdMapper.selectTchDsbdUnitAchievementList(paramData);

        if (selectTchDsbdUnitAchievementList != null && !selectTchDsbdUnitAchievementList.isEmpty()) {
            List<LinkedHashMap<Object, Object>> unitAchievement = AidtCommonUtil.filterToList(listItem1, selectTchDsbdUnitAchievementList);

            Double totalAvgUsdAchScr = Double.parseDouble(
                    unitAchievement.get(0).get("avgUsdAchScrPercent").toString()
            );

            // 반올림 (정수로 반올림)
            int roundedTotalAvgUsdAchScr = (int) Math.round(totalAvgUsdAchScr);
            returnMap.put("avgUsdAchScr", roundedTotalAvgUsdAchScr); // 단원의 평균 성취도
        }else{
            returnMap.put("avgUsdAchScr", "-");
        }

        returnMap.put("unitNm", currentUnitName); // 현재 선택된 단원명
        returnMap.put("levelHighStudents", groupedStudents.getOrDefault(1, Collections.emptyList()));
        returnMap.put("levelMiddleStudents", groupedStudents.getOrDefault(2, Collections.emptyList()));
        returnMap.put("levelLowStudents", groupedStudents.getOrDefault(3, Collections.emptyList()));

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 공지사항 등록
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> saveNotice(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // 공지사항 등록
        int insertResult = tchDsbdMapper.insertNotice(paramData);

        if(insertResult > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            // 등록된 정보 조회하여 리턴
            List<Map<String,Object>> selectNoticeList = tchDsbdMapper.selectPopupNoticeList(paramData);
            returnMap.put("noticeList", selectNoticeList);

        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 공지사항 고정여부 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> updateNoticePin(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int updateResult = tchDsbdMapper.updateNoticePin(paramData);

        if(updateResult > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 공지사항 삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> deleteNotice(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int deleteResult = tchDsbdMapper.deleteNotice(paramData);

        if(deleteResult > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 홈 공지사항 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> selectHomeNotice(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 홈 공지사항 목록
        List<Map<String, Object>> noticeList = tchDsbdMapper.selectHomeNoticeList(paramData);

        resultMap.put("noticeList", noticeList);

        return resultMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 팝업 공지사항 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> selectPopupNotice(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 팝업 공지사항 목록
        List<Map<String, Object>> noticeList = tchDsbdMapper.selectPopupNoticeList(paramData);

        resultMap.put("noticeList", noticeList);

        return resultMap;
    }


    /**
     * [교사] 학급관리 > 홈 대시보드 > 접속학생통계 등록
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional
    public Map<String, Object> saveStatisticParticipant(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        boolean isSuccess = true;

        List<String> studentIds = (List<String>) paramData.get("stntId");

        try {
            // 학생 ID 리스트 검증
            if (studentIds == null || studentIds.isEmpty()) {
                log.warn("학생 ID 리스트가 비어있습니다");
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "학생 ID가 없습니다");
                return returnMap;
            }

            log.info("배치 처리 시작: {} 명의 학생", studentIds.size());

            // 학생 ID 정렬로 락 순서 일관성 확보 (데드락 방지)
            studentIds.sort(String::compareTo);

            // 파라미터에 studentIds 추가
            paramData.put("studentIds", studentIds);

            // ON DUPLICATE KEY UPDATE로 단일 쿼리 처리
            tchDsbdMapper.upsertStatisticParticipant(paramData);

//            // 1. INSERT IGNORE로 모든 학생 데이터 삽입 시도 (중복 무시)
//            tchDsbdMapper.insertStatisticParticipantBatch(paramData);
//
//            // 2. 모든 학생 데이터 업데이트 (기존 데이터 갱신)
//            tchDsbdMapper.updateStatisticParticipantBatch(paramData);

            log.info("배치 처리 완료: {} 명의 학생", studentIds.size());

        } catch (Exception e) {
            isSuccess = false;
            log.error("saveStatisticParticipant 배치 처리 실패 - 학생 수: {}", studentIds != null ? studentIds.size() : 0, e);
        }

        returnMap.put("resultOk", isSuccess);
        returnMap.put("resultMsg", isSuccess ? "성공" : "실패");
        return returnMap;
    }


    /**
     * [교사] 학급관리 > 홈 대시보드 > 접속학생통계 초기화
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 30)
    public Map<String, Object> deleteStatisticParticipant(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        boolean isOk = false;
        int paramCnt = 0;

        if (ObjectUtils.isNotEmpty(paramData.get("userId"))){
            paramCnt++;
        };

        if (ObjectUtils.isNotEmpty(paramData.get("claId"))){
            paramCnt++;
        };
        if (ObjectUtils.isNotEmpty(paramData.get("textbookId"))){
            paramCnt++;
        };

        if (paramCnt ==3) isOk = true;

        System.out.println("paramCnt " + paramCnt);
        System.out.println("isOk " + isOk);
        if (isOk) {
            int deleteResult = tchDsbdMapper.deleteStatisticParticipant(paramData);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }
        else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }


        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 접속학생통계 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> selectStatisticParticipant(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        List<String> listItem = Arrays.asList(
                "stntId"
        );

        // 학습대상학생수
        int targetStntCnt = tchDsbdMapper.targetStntCnt(paramData);

        // 접속학생수
        List<LinkedHashMap<Object, Object>> participantList = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectStatisticParticipant(paramData));

        returnMap.put("targetStntCnt"   , targetStntCnt         );
        returnMap.put("participantList" , participantList       );
        returnMap.put("participantCnt"  , participantList.size());

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 학습맵 > 단원 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusStudyMapUnitList(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm"
        );

        paramData.put("isProject", 0);
        List<Map> selectTchDsbdUnitInfoList = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        /*전체 추가*/
        HashMap<Object, Object> selectTchDsbdUnitInfoMap = new HashMap<>();
        selectTchDsbdUnitInfoMap.put("unitNum", 0);
        selectTchDsbdUnitInfoMap.put("metaId", 0);
        selectTchDsbdUnitInfoMap.put("unitNm", "전 단원");
        selectTchDsbdUnitInfoList.add(0, selectTchDsbdUnitInfoMap);

        returnMap.put("unitList", AidtCommonUtil.filterToList(listItem, selectTchDsbdUnitInfoList));
        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준(수학)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdStatusStudyMapMathAchievementStandardList(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "contentAreaNm", "achStdCd", "achStdNm", "metaId", "kwgMainInfo", "avgUsdScr"
        );

        if(!paramData.containsKey("metaId")) {
            paramData.put("metaId", 0); // 전 단원 검색으로 처리
        }

        // 성취기준 목록 조회
        List<LinkedHashMap<Object, Object>> achStdList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectTchDsbdStdMapMathAchievementStandardList(paramData));

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

        // 학생별 성취기준 점수 조회
        List<Map<String, Object>> studentScores = tchDsbdMapper.selectTchDsbdStdMapMathStudentScores(paramData);

        returnMap.put("achStdList", achStdList);
        returnMap.put("studentScores", studentScores);

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
     * [교사] 학급관리 > 홈 대시보드 > 메모 등록
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> saveMemo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // 메모 등록
        int insertResult = tchDsbdMapper.insertMemo(paramData);

        if(insertResult > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            // 등록된 정보 조회하여 리턴
            List<Map<String,Object>> selectMomoList = tchDsbdMapper.selectMemoList(paramData);
            returnMap.put("momoList", selectMomoList);

        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 메모 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> selectMemo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 메모 목록
        List<Map<String, Object>> memoList = tchDsbdMapper.selectMemoList(paramData);

        resultMap.put("memoList", memoList);

        return resultMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 메모 삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> deleteMemo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        int deleteResult = tchDsbdMapper.deleteMemo(paramData);

        if(deleteResult > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 메모 수정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> updateMemo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // 메모 수정 처리
        int updateResult = tchDsbdMapper.updateMemo(paramData);

        if (updateResult > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            // 수정 후 목록 반환 (선택적)
            Map<String,Object> selectMemo = tchDsbdMapper.selectMemo(paramData);
            returnMap.put("memoInfo", selectMemo);

        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 우리반 학습 현황 캘린더 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findTchDsbdCalendarEventsList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        paramData.put("trgtSeCd", AidtCommonUtil.strToLongList((String)paramData.get("trgtSeCd")));

        List<Map> selectTchDsbdCalendarEventsList = tchDsbdMapper.selectTchDsbdCalendarEventsList(paramData);

        returnMap.put("CalendarList", selectTchDsbdCalendarEventsList);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 학습 요약 > 수학/영어 > 교사"
     * 학습요약 학급 학생 리스트
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectOfClassInStudentsList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "name", "stdtId"
        );

        // 학급전체를 담을 LinkedHashMap 생성
        LinkedHashMap<Object, Object> allClassMap = new LinkedHashMap<>();
        allClassMap.put("name", "학급전체");
        allClassMap.put("stdtId", "all");

        // 기존 학생 목록 조회
        List<LinkedHashMap<Object, Object>> studentsList = AidtCommonUtil.filterToList(listItem1, tchDsbdMapper.selectOfClassInStudentsList(paramData));

        // 새로운 리스트를 생성하여 학급전체를 첫 번째 요소로 추가
        List<LinkedHashMap<Object, Object>> finalStudentsList = new ArrayList<>();
        finalStudentsList.add(allClassMap);
        finalStudentsList.addAll(studentsList);

        returnMap.put("studentsList", finalStudentsList);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 학습 요약
     * 학습요약 통계
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectLeaningSummaryStatisticsEng(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "stdtId", "avgStudyTime", "avgStudyTimePre", "totalStudyDays", "totalStudyDaysPre",
                "totalSolvedProblems", "totalSolvedProblemsPre", "questionCount", "activityCount", "correctRate", "correctRatePre"
        );

        List<String> trgtSeCdList = Arrays.stream(Objects.toString(paramData.get("trgtSeCd"), "")
                        .split(","))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        paramData.put("trgtSeCdList", trgtSeCdList);
        Map<Object, Object> statistics =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectLeaningSummaryStatisticsEng(paramData));

        // 1. 파라미터에서 시작일, 종료일, 비교 단위 추출
        String endDate = (String) paramData.get("endDate");
        String startDate = (String) paramData.get("startDate");

        // 2. 날짜 계산
        Map<String, String> dateParams = calculateDateRanges(startDate, endDate);

        // 3. 계산된 날짜 정보를 원래 파라미터 맵에 추가
        paramData.putAll(dateParams);

        Map<Object, Object> statisticsPre =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectLeaningSummaryStatisticsEng(paramData));

        // 4. 현재 통계와 이전 기간 통계 비교하여 증감 계산 (증가(1) 또는 감소(-1) 또는 변화없음(0)으로 표시)
        // 현재 기간 통계가 있는지 확인
        if (statistics != null && !statistics.isEmpty()) {
            // avgStudyTime 필드 비교
            if (statistics.get("avgStudyTime") != null && statisticsPre.get("avgStudyTime") != null) {
                String currentTime = statistics.get("avgStudyTime").toString();
                String previousTime = statisticsPre.get("avgStudyTime").toString();
                if ("00:00".equals(currentTime)) {
                    statistics.put("avgStudyTimePre", 0); // 문구 미노출
                } else if (previousTime.equals(currentTime)) {
                    statistics.put("avgStudyTimePre", 0); // 문구 미노출 (동일)
                } else if ("00:00".equals(previousTime)) {
                    statistics.put("avgStudyTimePre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                } else {
                    String[] currentParts = currentTime.split(":");
                    String[] previousParts = previousTime.split(":");

                    int currentMinutes = Integer.parseInt(currentParts[0]) * 60 + Integer.parseInt(currentParts[1]);
                    int previousMinutes = Integer.parseInt(previousParts[0]) * 60 + Integer.parseInt(previousParts[1]);

                    int comparisonResult = Integer.compare(currentMinutes, previousMinutes);
                    statistics.put("avgStudyTimePre", comparisonResult > 0 ? 1 : (comparisonResult < 0 ? -1 : 0));
                }
            } else if (statistics.get("avgStudyTime") != null && statisticsPre.get("avgStudyTime") == null) {
                // 현재 데이터는 있고 이전 데이터가 없는 경우
                String currentTime = statistics.get("avgStudyTime").toString();
                if ("00:00".equals(currentTime)) {
                    statistics.put("avgStudyTimePre", 0); // 문구 미노출
                } else {
                    statistics.put("avgStudyTimePre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                }
            } else {
                statistics.put("avgStudyTimePre", 0); // 문구 미노출
            }

            // 나머지 필드 비교 (totalStudyDays, totalSolvedProblems, correctRate)
            String[] fields = {"totalStudyDays", "totalSolvedProblems", "correctRate"};
            for (String field : fields) {
                if (statistics.get(field) != null && statisticsPre.get(field) != null) {
                    Double currentValue = Double.parseDouble(statistics.get(field).toString());
                    Double previousValue = Double.parseDouble(statisticsPre.get(field).toString());

                    // 0이면 변화 없음으로 처리
                    if(Math.abs(currentValue) < 0.001) {
                        statistics.put(field + "Pre", 0); // 문구 미노출
                    } else if(currentValue.equals(previousValue)){
                        statistics.put(field + "Pre", 0); // 문구 미노출 (동일)
                    } else if (Math.abs(previousValue) < 0.001) {
                        statistics.put(field + "Pre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                    } else {
                        int comparisonResult = Double.compare(currentValue, previousValue);
                        statistics.put(field + "Pre", comparisonResult > 0 ? 1 : (comparisonResult < 0 ? -1 : 0));
                    }
                } else if (statistics.get(field) != null && statisticsPre.get(field) == null) {
                    // 현재 데이터는 있고 이전 데이터가 없는 경우
                    Double currentValue = Double.parseDouble(statistics.get(field).toString());
                    if(Math.abs(currentValue) < 0.001) {
                        statistics.put(field + "Pre", 0); // 문구 미노출
                    } else {
                        statistics.put(field + "Pre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                    }
                } else {
                    statistics.put(field + "Pre", 0); // 문구 미노출
                }
            }
        } else {
            // 현재 통계 데이터 자체가 없는 경우 - 빈 결과 반환하거나 기본값 설정
            statistics = new HashMap<>();
            statistics.put("avgStudyTimePre", 0); // 문구 미노출
            statistics.put("totalStudyDaysPre", 0); // 문구 미노출
            statistics.put("totalSolvedProblemsPre", 0); // 문구 미노출
            statistics.put("correctRatePre", 0); // 문구 미노출
        }

        // 현재 기간 데이터 반올림 처리
        String[] fieldsToRound = {"totalStudyDays", "totalSolvedProblems", "correctRate"};
        for (String field : fieldsToRound) {
            if (statistics.containsKey(field)) {
                double value = Double.parseDouble(statistics.get(field).toString());
                // 반올림
                double roundedValue = Math.round(value);
                statistics.put(field, roundedValue);
            }
        }

        if (paramData.get("userId") == null || paramData.get("userId") == "") {
            statistics.put("stdtId", "학급 전체");
        }

        returnMap.put("resultData", statistics);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 학습 요약
     * 학습요약 통계
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectLeaningSummaryStatisticsMath(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem1 = Arrays.asList(
                "stdtId", "avgStudyTime", "avgStudyTimePre", "correctRate", "correctRatePre", "topRanks", "topRanksCount", "bottomRanks", "bottomRanksCount"
        );

        List<String> listItem2 = Arrays.asList(
                "stdtId", "participationRatePercent", "avgParticipationPercent"
        );

        List<String> trgtSeCdList = Arrays.stream(Objects.toString(paramData.get("trgtSeCd"), "")
                        .split(","))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        paramData.put("trgtSeCdList", trgtSeCdList);

        Map<Object, Object> statistics =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectLeaningSummaryStatisticsMath(paramData));
        List<LinkedHashMap<Object, Object>> statisticsParticipation = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectLeaningSummaryStatisticsParticipationMath(paramData));

        // 1. 파라미터에서 시작일, 종료일, 비교 단위 추출
        String endDate = (String) paramData.get("endDate");
        String startDate = (String) paramData.get("startDate");

        // 2. 날짜 계산
        Map<String, String> dateParams = calculateDateRanges(startDate, endDate);

        // 3. 계산된 날짜 정보를 원래 파라미터 맵에 추가
        paramData.putAll(dateParams);

        Map<Object, Object> statisticsPre =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectLeaningSummaryStatisticsMath(paramData));

        // 4. 현재 통계와 이전 기간 통계 비교하여 증감 계산 (증가(1) 또는 감소(-1) 또는 변화없음(0)으로 표시)
        // 현재 기간 통계가 있는지 확인
        if (statistics != null && !statistics.isEmpty()) {
            // 이전 기간 통계가 있으면 비교하여 증감 계산
            if (statistics.get("avgStudyTime") != null && statisticsPre.get("avgStudyTime") != null) {
                String currentTime = statistics.get("avgStudyTime").toString();
                String previousTime = statisticsPre.get("avgStudyTime").toString();
                if ("00:00".equals(currentTime)) {
                    statistics.put("avgStudyTimePre", 0); // 문구 미노출
                } else if (previousTime.equals(currentTime)) {
                    statistics.put("avgStudyTimePre", 0); // 문구 미노출 (동일)
                } else if ("00:00".equals(previousTime)) {
                    statistics.put("avgStudyTimePre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                } else {
                    String[] currentParts = currentTime.split(":");
                    String[] previousParts = previousTime.split(":");

                    int currentMinutes = Integer.parseInt(currentParts[0]) * 60 + Integer.parseInt(currentParts[1]);
                    int previousMinutes = Integer.parseInt(previousParts[0]) * 60 + Integer.parseInt(previousParts[1]);

                    int comparisonResult = Integer.compare(currentMinutes, previousMinutes);
                    statistics.put("avgStudyTimePre", comparisonResult > 0 ? 1 : (comparisonResult < 0 ? -1 : 0));
                }
            } else if (statistics.get("avgStudyTime") != null && statisticsPre.get("avgStudyTime") == null) {
                // 현재 데이터는 있고 이전 데이터가 없는 경우
                String currentTime = statistics.get("avgStudyTime").toString();
                if ("00:00".equals(currentTime)) {
                    statistics.put("avgStudyTimePre", 0); // 문구 미노출
                } else {
                    statistics.put("avgStudyTimePre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                }
            } else {
                statistics.put("avgStudyTimePre", 0); // 문구 미노출
            }

            // correctRate 필드 비교
            if (statistics.get("correctRate") != null && statisticsPre.get("correctRate") != null) {
                Double currentValue = Double.parseDouble(statistics.get("correctRate").toString());
                Double previousValue = Double.parseDouble(statisticsPre.get("correctRate").toString());

                // 0이면 변화 없음으로 처리
                if(Math.abs(currentValue) < 0.001) {
                    statistics.put("correctRatePre", 0); // 문구 미노출
                } else if(statistics.get("correctRate").toString().equals(statisticsPre.get("correctRate").toString())){
                    statistics.put("correctRatePre", 0); // 문구 미노출 (동일)
                } else if (Math.abs(previousValue) < 0.001) {
                    statistics.put("correctRatePre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                } else {
                    int comparisonResult = Double.compare(currentValue, previousValue);
                    statistics.put("correctRatePre", comparisonResult > 0 ? 1 : (comparisonResult < 0 ? -1 : 0));
                }
            } else if (statistics.get("correctRate") != null && statisticsPre.get("correctRate") == null) {
                // 현재 데이터는 있고 이전 데이터가 없는 경우
                Double currentValue = Double.parseDouble(statistics.get("correctRate").toString());
                if(Math.abs(currentValue) < 0.001) {
                    statistics.put("correctRatePre", 0); // 문구 미노출
                } else {
                    statistics.put("correctRatePre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
                }
            } else {
                statistics.put("correctRatePre", 0); // 문구 미노출
            }
        } else {
            // 현재 통계 데이터 자체가 없는 경우 - 빈 결과 반환하거나 기본값 설정
            statistics = new HashMap<>();
            statistics.put("avgStudyTimePre", 0); // 문구 미노출
            statistics.put("correctRatePre", 0); // 문구 미노출
        }

        List<LinkedHashMap<Object, Object>> statisticsParticipationPre = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectLeaningSummaryStatisticsParticipationMath(paramData));

        if (statisticsParticipation != null && !statisticsParticipation.isEmpty()) {
            // userId 유무에 따라 참여도 필드명 결정 (개인 또는 평균)
            String participationField = paramData.get("userId") != null ?
                    "participationRatePercent" : "avgParticipationPercent";

            // 현재 참여도 값 설정
            statistics.put("participationRatePercent", statisticsParticipation.get(0).get(participationField));
            if(statisticsParticipation.get(0).get(participationField) != null && (statisticsParticipationPre == null || statisticsParticipationPre.isEmpty())){
                statistics.put("participationRatePercentPre", 2); // 문구 미노출 + 상승 아이콘만 (0 기준 상승)
            }else{
                // 지난 기간 대비 계산 (1: 증가, -1: 감소, 0: 변화없음)
                if (statisticsParticipationPre != null && !statisticsParticipationPre.isEmpty()) {
                    BigDecimal currentValue = (BigDecimal) statisticsParticipation.get(0).get(participationField);
                    BigDecimal previousValue = (BigDecimal) statisticsParticipationPre.get(0).get(participationField);

                    int comparisonResult = currentValue.compareTo(previousValue);
                    statistics.put("participationRatePercentPre",
                            comparisonResult > 0 ? 1 : (comparisonResult < 0 ? -1 : 0));
                } else {
                    statistics.put("participationRatePercentPre", 0);
                }
            }
        } else {
            statistics.put("participationRatePercent", 0);
            statistics.put("participationRatePercentPre", 0); // 문구 미노출
        }

        if (paramData.get("userId") == null || paramData.get("userId") == "") {
            statistics.put("stdtId", "학급 전체");
        }


        // final 키워드를 추가하여 명시적으로 불변임을 선언
        final Map<Object, Object> statisticsFinal = statistics;

        // 반올림이 필요한 키 목록
        List<String> keysToRound = Arrays.asList("correctRate", "participationRatePercent");

        keysToRound.forEach(key -> {
            if (statisticsFinal.containsKey(key)) {
                Object value = statisticsFinal.get(key);
                if (value instanceof BigDecimal) {
                    double doubleValue = ((BigDecimal) value).doubleValue();
                    statisticsFinal.put(key, (int) Math.round(doubleValue));
                }
            }
        });

        returnMap.put("resultData", statistics);

        return returnMap;
    }

    /**
     * 현재 기간과 비교 기간의 날짜 범위를 계산
     */
    private Map<String, String> calculateDateRanges(String startDate, String endDate) {
        Map<String, String> result = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
        LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

        // 월 단위 비교인지 확인 (월의 첫날부터 마지막날까지인 경우)
        boolean isMonthlyComparison =
                startLocalDate.getDayOfMonth() == 1 &&
                        endLocalDate.getDayOfMonth() == endLocalDate.lengthOfMonth();

        LocalDate previousStartDate;
        LocalDate previousEndDate;

        if (isMonthlyComparison) {
            // 월 단위 비교 - 이전 월로 계산
            previousEndDate = startLocalDate.minusDays(1); // 이전 월의 마지막 날
            previousStartDate = previousEndDate.withDayOfMonth(1); // 이전 월의 첫날
        } else {
            // 일반 비교 - 동일한 일수로 계산
            long daysBetween = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
            previousEndDate = startLocalDate.minusDays(1);
            previousStartDate = previousEndDate.minusDays(daysBetween);
        }

        result.put("currentStartDate", startDate);
        result.put("currentEndDate", endDate);
        result.put("previousStartDate", previousStartDate.format(formatter));
        result.put("previousEndDate", previousEndDate.format(formatter));

        return result;
    }
    @Transactional(readOnly = true)
    public Object selectTchDsbdRecSets(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> recSetsInfo = Arrays.asList(
                "setId", "setNm", "setIdx"
        );

        Map<Object, Object> rstRecSetsInfo = AidtCommonUtil.filterToMap(recSetsInfo, tchDsbdMapper.selectTchDsbdRecSets(paramData));

        returnMap.put("setId", MapUtils.getString(rstRecSetsInfo,"setId",null));
        returnMap.put("setNm", MapUtils.getString(rstRecSetsInfo,"setNm",null));
        returnMap.put("setIdx", MapUtils.getString(rstRecSetsInfo,"setIdx",null));


        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object selectTchDsbdRecArticle(Map<String, Object> paramData) throws Exception {
        // var returnMap = new LinkedHashMap<>();
        Map<String, Object> returnMap = new HashMap<>();
        List<String> recArticleInfo = Arrays.asList(
               "articleId", "articleNm"
        );

        List<LinkedHashMap<Object, Object>> rstRecArticleInfo = AidtCommonUtil.filterToList(recArticleInfo, tchDsbdMapper.selectTchDsbdRecArticle(paramData));
        returnMap.put("recArticleList", rstRecArticleInfo);

        return returnMap;
    }

    public Object modifyTchDsbdRecChk(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        int chgCnt = tchDsbdMapper.modifyTchDsbdRecChk(paramData);

        if (chgCnt > 0) {
            returnMap.put("resultOk" , true);
            returnMap.put("resultMsg", "성공");
        }
        else {
            returnMap.put("resultOk" , false);
            returnMap.put("resultMsg", "실패");
        }
        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 우리반 학습 현황 캘린더 상세 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findTchDsbdCalendarEventsDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        String trgtSeCd = (String) paramData.get("trgtSeCd");

        switch(trgtSeCd) {
            case "1" -> {
                List<String> trgtIdList = Arrays.stream(Objects.toString(paramData.get("trgtId"), "")
                                .split(","))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                paramData.put("trgtIdList", trgtIdList);

                // 차시
                List<Map<String, Object>> lessonInfoList = tchDsbdMapper.selectLessonCalendarCrcuInfo(paramData);
                // 우리반 학습 현황
                Map<String, Object> lessoninfo = tchDsbdMapper.selectLessonCalendarInfo(paramData);
                // 정답률
                Map<String, Object> lessonCrrctCorrectRate = tchDsbdMapper.selectLessonCrrctCorrectRate(paramData);

                // 반올림
                if (lessonCrrctCorrectRate.containsKey("correctRate")) {
                    BigDecimal bdValue = (BigDecimal) lessonCrrctCorrectRate.get("correctRate");
                    double usdAchScr = bdValue.doubleValue();
                    int roundedValue = (int) Math.round(usdAchScr);
                    lessonCrrctCorrectRate.put("correctRate", roundedValue);
                }

                returnMap.put("lessoninfo", lessoninfo);
                returnMap.put("lessonCrcuInfo", lessonInfoList);
                returnMap.put("correctRate", lessonCrrctCorrectRate);

            }
            case "2" -> {
                Map<String, Object> taskCrcuInfo = tchDsbdMapper.selectTaskCalendarCrcuInfo(paramData);
                Map<String, Object> taskInfo = tchDsbdMapper.selectTaskCalendarInfo(paramData);
                Map<String, Object> taskCorrectRate = tchDsbdMapper.selectTaskCorrectRate(paramData);

                // 반올림
                if (taskCorrectRate.containsKey("correctRate")) {
                    BigDecimal bdValue = (BigDecimal) taskCorrectRate.get("correctRate");
                    double usdAchScr = bdValue.doubleValue();
                    int roundedValue = (int) Math.round(usdAchScr);
                    taskCorrectRate.put("correctRate", roundedValue);
                }

                returnMap.put("taskInfo", taskInfo);
                returnMap.put("taskCrcuInfo", taskCrcuInfo);
                returnMap.put("correctRate", taskCorrectRate);

            }
            case "4" -> {
                Map<String, Object> bbsInfo = tchDsbdMapper.selectBbsCalendarInfo(paramData);

                returnMap.put("bbsInfo", bbsInfo);
            }
            default -> {
                Map<String, Object> evlCrcuInfo = tchDsbdMapper.selectEvlCalendarCrcuInfo(paramData);
                Map<String, Object> evlInfo = tchDsbdMapper.selectEvlCalendarInfo(paramData);
                Map<String, Object> evlCorrectRate = tchDsbdMapper.selectEvlCorrectRate(paramData);

                // 반올림
                if (evlCorrectRate.containsKey("correctRate")) {
                    BigDecimal bdValue = (BigDecimal) evlCorrectRate.get("correctRate");
                    double usdAchScr = bdValue.doubleValue();
                    int roundedValue = (int) Math.round(usdAchScr);
                    evlCorrectRate.put("correctRate", roundedValue);
                }

                returnMap.put("evlCrcuInfo", evlCrcuInfo);
                returnMap.put("evlInfo", evlInfo);
                returnMap.put("correctRate", evlCorrectRate);
            }
        }

        return returnMap;
    }

    private Map<String, Object> getAchievementInfo(int brandId, Map<String, Object> paramData) throws Exception {
        return brandId == 3
                ? tchDsbdMapper.selectTchDsbdCalendarAchievement(paramData)
                : tchDsbdMapper.selectTchDsbdCalendarusdSrc(paramData);
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (영어)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementClassdDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> listItem = Arrays.asList(
                "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "stdAt"
        );
        List<String> listItem2 = Arrays.asList(
                "code", "codeNm", "usdAchScrPercent", "diffType"
        );

        // 초등 5,6 학년 영어일 시 "grammar" 제외
        List<String> languageCodes;
        if(paramData.get("textbookId").equals("6981") || paramData.get("textbookId").equals("6982")){
            languageCodes = List.of("pronunciation", "vocabulary");
        } else {
            languageCodes = List.of("pronunciation", "grammar", "vocabulary");
        }
        paramData.put("languageCodes", languageCodes);

        // 학급 평균
        List<LinkedHashMap<Object, Object>> classDistribution = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdAreaAchievementDistribution(paramData));

        for (Map map : classDistribution) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("classDistribution", classDistribution);

        // 학급 요약
        List<LinkedHashMap<Object, Object>> achievementSummary = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdAreaAchievementDistributionSummary(paramData));

        for (Map map : achievementSummary) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("achievementSummary", achievementSummary);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementStudentDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> listItem = Arrays.asList(
                "code", "codeNm", "usdAchScr", "rfltActvCnt", "usdAchScrPercent", "stdAt"
        );
        List<String> listItem2 = Arrays.asList(
                "code", "codeNm", "usdAchScrPercent", "diffType"
        );


        // 초등 5,6 학년 영어일 시 "grammar" 제외
        List<String> languageCodes;
        if(paramData.get("textbookId").equals("6981") || paramData.get("textbookId").equals("6982")){
            languageCodes = List.of("pronunciation", "vocabulary");
        } else {
            languageCodes = List.of("pronunciation", "grammar", "vocabulary");
        }
        paramData.put("languageCodes", languageCodes);

        // 학급 평균
        List<LinkedHashMap<Object, Object>> classDistribution = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdAreaAchievementDistribution(paramData));

        for (Map map : classDistribution) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }


        // 학생 개인
        List<LinkedHashMap<Object, Object>> studentDistribution = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdAreaAchievementStudentDstribution(paramData));

        for (Map map : studentDistribution) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("studentDistribution", studentDistribution);


        String paramEvaluationAreaCd = (String) paramData.get("evaluationAreaCd");

        // 개인 요약 (AI 튜터정보)
        List<LinkedHashMap<Object, Object>> achievementSummary = new ArrayList<>();
        for (Map<Object, Object> student : studentDistribution) {
            String code = (String) student.get("code");
            Integer studentScore = (Integer) student.get("usdAchScrPercent");
            String stdAt = (String) student.get("stdAt");

            if (stdAt == null || !stdAt.equals("Y")) continue;  // 학생의 학습 내역이 없는 경우 건너뛰기

            // 해당 영역의 학급 평균 점수 찾기
            Integer classAverage = 0;
            for (Map<Object, Object> classData : classDistribution) {
                if (code.equals(classData.get("code")) && classData.get("stdAt").equals("Y")) {
                    classAverage = (Integer) classData.get("usdAchScrPercent");
                    break;
                }
            }

            // 점수 차이 계산
            int difference = studentScore - classAverage;

            // 차이가 있는 경우에만 추가
            if (Math.abs(difference) >= 0 && Objects.equals(code, paramEvaluationAreaCd)) {
                LinkedHashMap<Object, Object> summary = new LinkedHashMap<>();
                summary.put("code", student.get("code"));
                summary.put("codeNm", student.get("codeNm"));
                summary.put("usdAchScrPercent", student.get("usdAchScrPercent"));
                summary.put("diffType", difference > 0 ? "high" : difference < 0 ? "low" : "equals");
                achievementSummary.add(summary);
            }
        }

        returnMap.put("achievementSummary", achievementSummary);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (영어)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdDistributionAreaAchievementStudentList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Set<String> validEvaluationAreas = Set.of("understanding", "expression");
        String evaluationAreaCd = (String) paramData.get("evaluationAreaCd");

        // 조건에 따라 적절한 리스트 조회
        List<Map> achievementStudentList = Collections.emptyList();

        if (evaluationAreaCd == null || evaluationAreaCd.isEmpty()) {
            // 전체 영역
            achievementStudentList = tchDsbdMapper.selectTchDsbdDistributionAreaAchievementStudentList(paramData);
        } else if (validEvaluationAreas.contains(evaluationAreaCd)) {
            // 학생 성취도(이해,표현) 목록
            achievementStudentList = tchDsbdMapper.selectTchDsbdDistributionAreaAchievementStudentList1(paramData);
        } else {
            // 학생 성취도(단어/문법/발음) 목록
            achievementStudentList = tchDsbdMapper.selectTchDsbdDistributionAreaAchievementStudentList2(paramData);
        }

        // 반올림 처리
        for (Map map : achievementStudentList) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("achievementStudentList", achievementStudentList);

        return returnMap;
    }

    /** [교사][영어] 학급관리 > 홈 대시보드 > 영역분석 > 단원 콤보박스 */
    public List<Map> findUnitList(Map<String, Object> paramData) throws Exception {

        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = tchDsbdMapper.selectUnitList(paramData);

        // 콤보박스에 전 단원 추가
        LinkedHashMap<Object, Object> areaAchievementUnitInfoMap = new LinkedHashMap<>();
        areaAchievementUnitInfoMap.put("unitNum", 0);
        areaAchievementUnitInfoMap.put("metaId", 0);
        areaAchievementUnitInfoMap.put("unitNm", "전 단원");
        areaAchievementUnitInfo.add(0, areaAchievementUnitInfoMap);

        return areaAchievementUnitInfo;
    }

    /** [교사][영어] 학급관리 > 홈 대시보드 > 영역분석 > 영역 콤보박스 */
    public List<Map> findEngAreaList() throws Exception {
        return tchDsbdMapper.selectEngAreaList();
    }

    @Transactional(readOnly = true)
    public Object selectTchDsbdUnitAchievementListAll(Map<String, Object> paramData) throws Exception {
        // 반환할 결과 맵 초기화
        var returnMap = new LinkedHashMap<>();

        // 필터링에 사용할 항목 리스트 정의
        List<String> listItem = Arrays.asList(
                "unitNum", "metaId", "unitNm", "unit"
        );

        // 모든 기본 단원 정보 조회
        paramData.put("isProject", 0);
        List<Map> areaAchievementUnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

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



        // 성취도 정보 조회
        List<Map> unitAchievement = tchDsbdMapper.selectTchDsbdUnitAchievementList(paramData);

        // 전체 단원 평균 성취도 계산
        double totalAchievement = 0.0;
        int unitCount = 0;

        for (Map unit : unitAchievement) {
            if (unit.containsKey("avgUsdAchScrPercent")) {
                totalAchievement += Double.parseDouble(unit.get("avgUsdAchScrPercent").toString());
                unitCount++;
            }
        }

        double averageAchievement = (unitCount > 0) ? (totalAchievement / unitCount) : 0;

        // 전체 평균을 나타내는 Map 생성 (JSON 형식에 맞춤)
        Map<String, Object> totalAchievementMap = new LinkedHashMap<>();
        totalAchievementMap.put("unitNum", 0);  // 단원 번호는 0으로 설정
        totalAchievementMap.put("avgUsdAchScrPercent", averageAchievement);

        // UnitAchievementList에 전체 단원 데이터 추가 (맨 앞에 추가)
        unitAchievement.add(0, totalAchievementMap);

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
     * [교사] 학급관리 > 홈 대시보드 > 영역별 학급 평균 분포 (수학)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdChapterUsdClassdDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> listItem = Arrays.asList(
                "contentAreaId", "contentAreaNm", "usdScr", "stdAt"
        );

        List<LinkedHashMap<Object, Object>> classDistribution = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdChapterUsdClassdDstribution(paramData));


        for (Map map : classDistribution) {
            // usdScr 반올림
            if (map.containsKey("usdScr")) {
                double usdAchScrPercent = (double) map.get("usdScr");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdScr", roundedValue);
            }
        }

        returnMap.put("classDistribution", classDistribution);

        // 학급 요약 계산 및 추가
        returnMap.put("areaUsdSummary", calculateAreaUsdSummary(classDistribution));

        return returnMap;
    }

    /**
     * [AI튜터 정보] 영역별 최고값과 최저값을 추출하여 요약 정보 생성
     */
    private List<LinkedHashMap<Object, Object>> calculateAreaUsdSummary(
            List<LinkedHashMap<Object, Object>> classDistribution) {

        List<LinkedHashMap<Object, Object>> chapterUsdSummary = new ArrayList<>();

        // stdAt가 'Y'인 항목들만 필터링
        List<LinkedHashMap<Object, Object>> validItems = classDistribution.stream()
                .filter(map -> "Y".equals(map.get("stdAt")))
                .collect(Collectors.toList());

        if (!validItems.isEmpty()) {
            // 최고값 찾기
            LinkedHashMap<Object, Object> highestItem = validItems.stream()
                    .max(Comparator.comparingInt(map -> (Integer) map.get("usdScr")))
                    .orElse(null);

            // 최저값 찾기
            LinkedHashMap<Object, Object> lowestItem = validItems.stream()
                    .min(Comparator.comparingInt(map -> (Integer) map.get("usdScr")))
                    .orElse(null);

            // 최고값 요약 항목 추가
            if (highestItem != null) {
                LinkedHashMap<Object, Object> highSummary = new LinkedHashMap<>();
                highSummary.put("contentAreaId", highestItem.get("contentAreaId"));
                highSummary.put("contentAreaNm", highestItem.get("contentAreaNm"));
                highSummary.put("usdScr", highestItem.get("usdScr"));
                highSummary.put("diffType", "high");
                chapterUsdSummary.add(highSummary);
            }

            // 최저값 요약 항목 추가
            if (lowestItem != null && !lowestItem.equals(highestItem)) {
                LinkedHashMap<Object, Object> lowSummary = new LinkedHashMap<>();
                lowSummary.put("contentAreaId", lowestItem.get("contentAreaId"));
                lowSummary.put("contentAreaNm", lowestItem.get("contentAreaNm"));
                lowSummary.put("usdScr", lowestItem.get("usdScr"));
                lowSummary.put("diffType", "low");
                chapterUsdSummary.add(lowSummary);
            }
        }

        return chapterUsdSummary;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 학생 개인 분포 (수학)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdChapterUsdStudentDstribution(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> studentListItem = Arrays.asList(
                "contentAreaNm", "contentAreaId", "flnm", "stdtId", "stdAt", "usdScr"
        );

        List<LinkedHashMap<Object, Object>> studentDistribution = AidtCommonUtil.filterToList(studentListItem, tchDsbdMapper.selectTchDsbdChapterUsdStudentDistribution(paramData));

        roundScores(studentDistribution, "usdScr");
        returnMap.put("studentDistribution", studentDistribution);

        List<String> classListItem = Arrays.asList(
                "contentAreaId", "contentAreaNm", "usdScr", "stdAt"
        );

        // 학급 평균
        List<LinkedHashMap<Object, Object>> classDistribution = AidtCommonUtil.filterToList(classListItem, tchDsbdMapper.selectTchDsbdChapterUsdClassdDstribution(paramData));



        roundScores(classDistribution, "usdScr");

        Integer paramContentAreaId = Integer.valueOf((String) paramData.get("contentAreaId"));

        // 개인 요약 (AI 튜터정보)
        List<LinkedHashMap<Object, Object>> chapterUsdSummary = new ArrayList<>();
        for (Map<Object, Object> student : studentDistribution) {
            Integer contentAreaId = (Integer) student.get("contentAreaId");
            Integer studentScore = (Integer) student.get("usdScr");
            String stdAt = (String) student.get("stdAt");

            if (stdAt == null || !stdAt.equals("Y")) continue;  // 학생의 학습 내역이 없는 경우 건너뛰기

            // 해당 영역의 학급 평균 점수 찾기
            Integer classAverage = 0;
            for (Map<Object, Object> classData : classDistribution) {
                if (contentAreaId.equals(classData.get("contentAreaId")) && classData.get("stdAt").equals("Y")) {
                    classAverage = (Integer) classData.get("usdScr");
                    break;
                }
            }

            // 점수 차이 계산
            int difference = studentScore - classAverage;

            // 차이가 있는 경우에만 추가
            if (Math.abs(difference) >= 0 && Objects.equals(contentAreaId, paramContentAreaId)) {
                LinkedHashMap<Object, Object> summary = new LinkedHashMap<>();
                summary.put("stntId", student.get("stdtId"));
                summary.put("flnm", student.get("flnm"));
                summary.put("contentAreaId", contentAreaId);
                summary.put("contentAreaNm", student.get("contentAreaNm"));
                summary.put("usdScr", studentScore);
                summary.put("differentType", difference > 0 ? "high" : difference < 0 ? "low" : "equals");
                chapterUsdSummary.add(summary);
            }
        }

        returnMap.put("chapterUsdSummary", chapterUsdSummary);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 학생 이해도 목록 (수학)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdDistributionChapterUsdStudentList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "contentAreaNm", "contentAreaId", "flnm", "stdtId", "stdAt", "usdScr"
        );

        // 영역별 학생 이해도 목록
        List<LinkedHashMap<Object, Object>> chapterUsdStudentList = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdDistributionChapterUsdStudentList(paramData));


        for (Map map : chapterUsdStudentList) {
            // usdScr 반올림
            if (map.containsKey("usdScr")) {
                double usdAchScrPercent = (double) map.get("usdScr");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdScr", roundedValue);
            }
        }

        returnMap.put("chapterUsdStudentList", chapterUsdStudentList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStudentSelfLearningQuestionCount(Map<String, Object> paramData) throws Exception {
        // 학생별 자기주도학습 풀이 문항 수 정보 조회
        List<Map> studentQuestionCountList = tchDsbdMapper.selectStudentSelfLearningQuestionCount(paramData);

        Map<String, Object> selfLearningCntInfo = new LinkedHashMap<>();

        //최대/최소 문항 풀이수를 가진 학생 찾기
        if (!studentQuestionCountList.isEmpty()) {
            Map maxStudent = studentQuestionCountList.stream()
                    .max(Comparator.comparingInt(m -> ((Number)m.get("totalStdMdulCnt")).intValue()))
                    .orElse(null);

            Map minStudent = studentQuestionCountList.stream()
                    .min(Comparator.comparingInt(m -> ((Number)m.get("totalStdMdulCnt")).intValue()))
                    .orElse(null);

            if (maxStudent != null) {
                selfLearningCntInfo.put("highestStudentName", maxStudent.get("flnm"));
            }

            if (minStudent != null) {
                selfLearningCntInfo.put("lowestStudentName", minStudent.get("flnm"));
            }
        }

        selfLearningCntInfo.put("studentList", studentQuestionCountList);

        return selfLearningCntInfo;
    }


    @Transactional(readOnly = true)
    public Object findTchDsbdConceptUsdTree(Map<String, Object> paramData) throws Exception {
        List<String> listItem = Arrays.asList("metaId", "unitNum","unitNm", "kwgMainId", "kwgNm", "stdAt", "usdScr" ,"depth", "code");

        Map rtnMap = new LinkedHashMap();

        // 단원 정보
        paramData.put("isProject", 0);
        List<Map> UnitInfo = tchDsbdMapper.selectTchDsbdUnitInfo(paramData);

        rtnMap.put("unitList",UnitInfo);

        // 결과 정보
        Map<Object, Object> lastLesInfo = tchDsbdMapper.selectTchDsbdLastLesson(paramData);

        // 사용자가 단원을 선택했는지 확인
        if (paramData.get("metaId") != null) {
            // 사용자가 선택한 단원의 metaId 사용
            paramData.put("metaId", paramData.get("metaId"));

            // 첫 화면 진입
        } else {
            // 마지막 수업 정보가 없을 시 첫 번째 단원 조회
            if (lastLesInfo == null || lastLesInfo.isEmpty()) {
                Map firstUnit = UnitInfo.get(0); // 첫 번째 단원 가져오기
                if (firstUnit.containsKey("metaId")) {
                    paramData.put("metaId", firstUnit.get("metaId"));
                }
            } else {
                // 마지막 수업 정보가 있을 시 조회
                paramData.put("metaId", lastLesInfo.get("metaId"));
            }
        }

        rtnMap.put("lastLesInfo",lastLesInfo);


        List<Map> conceptUsdTreeList = tchDsbdMapper.findTchDsbdConceptUsdTree(paramData);
        rtnMap.put("conceptUsdTreeList", AidtCommonUtil.filterToList(listItem, conceptUsdTreeList));

        if (conceptUsdTreeList != null) {

            List<Map> sortedList = conceptUsdTreeList.stream()
                    .filter(map -> {
                        String textbookId = (String) paramData.get("textbookId");

                        // 초등수학 교과서 ID 목록
                        List<String> specialTextbookIds = Arrays.asList(
                                "1175", "1197", "1198", "1199",
                                "7036", "7040", "7041", "7042"
                        );

                        // 초등수학 교과서 ID 목록에 포함되는지 확인
                        if (specialTextbookIds.contains(textbookId)) {
                            // 특정 교과서의 경우 depth 2 적용
                            return ((Number) map.get("depth")).intValue() == 2;
                        } else {
                            // 그 외의 경우 depth 4 적용
                            return ((Number) map.get("depth")).intValue() == 4;
                        }
                    })
                    .filter(map -> "Y".equals(map.get("stdAt")))
                    .collect(Collectors.toList());

            // 정렬
            sortedList.sort(Comparator
                    .comparing((Map map) -> ((Number) map.get("usdScr")).doubleValue()) // usdScr 오름차순
                    .thenComparing((Map map) -> (Integer) map.get("kwgMainId")) // kwgMainId 오름차순
            );

            if (sortedList.size() > 0) {
                rtnMap.put("usdScrMin", sortedList.get(0));
            }

            // 정렬
            sortedList.sort(Comparator
                    .comparing((Map map) -> ((Number) map.get("usdScr")).doubleValue(), Comparator.reverseOrder()) // usdScr 내림차순
                    .thenComparing((Map map) -> (Integer) map.get("kwgMainId")) // kwgMainId 오름차순
            );

            if (sortedList.size() > 0) {
                rtnMap.put("usdScrMax", sortedList.get(0));
            }

        }

        if(paramData.get("stdtId") != null){
            String stdtId = paramData.get("stdtId").toString();
            Map<String,Object> userInfo = userMapper.findUserInfoByUserId(stdtId);
            rtnMap.put("flnm", userInfo.get("flnm"));
        }

        return rtnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 (수학)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdUsdParticipationQuadrant(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> listItem = Arrays.asList(
                "stdtId", "flnm", "claId", "textbkId", "participationRatePercent", "avgParticipationPercent",
                "understandingScore", "avgUnderstandingScore", "quadrant"
        );

        // 반올림할 필드 목록
        List<String> roundFields = Arrays.asList(
                "participationRatePercent", "avgParticipationPercent",
                "understandingScore", "avgUnderstandingScore"
        );

        List<LinkedHashMap<Object, Object>> usdParticipationQuadrant = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdUsdParticipationQuadrant(paramData));

        for (Map map : usdParticipationQuadrant) {
            for (String field : roundFields) {
                if (map.containsKey(field)) {
                    Object value = map.get(field);
                    double doubleValue = 0;

                    // BigDecimal인 경우 처리
                    if (value instanceof BigDecimal) {
                        doubleValue = ((BigDecimal) value).doubleValue();
                    }

                    // Double인 경우 처리
                    else if (value instanceof Double) {
                        doubleValue = (Double) value;
                    }

                    int roundedValue = (int) Math.round(doubleValue);
                    map.put(field, roundedValue);
                }
            }
        }

        returnMap.put("usdParticipationQuadrant", usdParticipationQuadrant);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 사분면 그래프 학생 분포 그룹(수학)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdUsdParticipationStudentQuadrant(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "stdtId", "flnm", "claId", "textbkId", "participationRatePercent", "avgParticipationPercent",
                "understandingScore", "avgUnderstandingScore", "quadrant", "quadrantDescription"
        );

        // 반올림할 필드 목록
        List<String> roundFields = Arrays.asList(
                "participationRatePercent", "avgParticipationPercent",
                "understandingScore", "avgUnderstandingScore"
        );

        List<LinkedHashMap<Object, Object>> usdParticipationStudentQuadrant = AidtCommonUtil.filterToList(listItem, tchDsbdMapper.selectTchDsbdUsdParticipationStudentQuadrant(paramData));

        for (Map map : usdParticipationStudentQuadrant) {
            for (String field : roundFields) {
                if (map.containsKey(field)) {
                    Object value = map.get(field);
                    double doubleValue = 0;

                    // BigDecimal인 경우 처리
                    if (value instanceof BigDecimal) {
                        doubleValue = ((BigDecimal) value).doubleValue();
                    }

                    // Double인 경우 처리
                    else if (value instanceof Double) {
                        doubleValue = (Double) value;
                    }

                    int roundedValue = (int) Math.round(doubleValue);
                    map.put(field, roundedValue);
                }
            }
        }

        returnMap.put("usdParticipationStudentQuadrant", usdParticipationStudentQuadrant);

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 사분면 그룹 독려 알림 전송 팝업
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object sendQuadrantEncouragementNotificationPop(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 사분면 그룹 조회
        List<Map> usdParticipationStudentQuadrant = tchDsbdMapper.selectTchDsbdUsdParticipationStudentQuadrant(paramData);

        if (usdParticipationStudentQuadrant != null && !usdParticipationStudentQuadrant.isEmpty()) {
            // String 타입의 객체를 Integer 타입으로 직접 형변환할 때, 에러가 발생하여 아래와 같이 수정(05/14)
            Integer quadrant = Integer.valueOf(String.valueOf(usdParticipationStudentQuadrant.get(0).get("quadrant")));

            String quadrantDescription = (String) usdParticipationStudentQuadrant.get(0).get("quadrantDescription");

            // 해당 학생의 사분면에 맞는 랜덤 알림 메시지 가져오기
            String encouragementMessage = QuadrantAlertEnum.getRandomAlertForQuadrant(quadrant);

            Map<String, Object> quadrantInfo = new LinkedHashMap<>();

            quadrantInfo.put("startDate", paramData.get("startDate"));
            quadrantInfo.put("endDate", paramData.get("endDate"));
            quadrantInfo.put("quadrant", quadrant);
            quadrantInfo.put("quadrantDescription", quadrantDescription);
            quadrantInfo.put("encouragementMessage", encouragementMessage);

            // 전체 결과 Map에 사분면 정보 Map 추가
            returnMap.put("quadrantInfo", quadrantInfo);

        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 사분면 그룹 독려 알림 전송
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> sendQuadrantEncouragementNotification(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // 사분면 그룹 조회
        List<Map> usdParticipationStudentQuadrant = tchDsbdMapper.selectTchDsbdUsdParticipationStudentQuadrant(paramData);

        if (usdParticipationStudentQuadrant.isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "독려 알림 대상 학생이 없습니다.");
            return returnMap;
        }
        boolean isSuccess = true;

        paramData.put("trgetTyCd", 17);
        paramData.put("tchNtcnCn", "[대시보드]에서 선생님이 격려 메시지를 보냈습니다.");

        // 교사 알림
        int teacherResult = tchDsbdMapper.insertTchEncouragementNotification(paramData);
        if (teacherResult <= 0) {
            isSuccess = false;
        }

        for (Map student : usdParticipationStudentQuadrant) {
            String studentId = (String) student.get("stdtId");
            String studentName = (String) student.get("flnm");

            paramData.put("stdtId", studentId);
            paramData.put("flnm", studentName);

            // 학생 알림
            int studentResult = tchDsbdMapper.insertStntEncouragementNotification(paramData);

            if (studentResult <= 0) {
                isSuccess = false;
            }
        }

        if(isSuccess) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 AI튜터
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementAitutor(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Set<String> validEvaluationAreas = Set.of("listening", "reading", "viewing", "speaking", "writing", "presenting");
        String evaluationAreaCd = (String) paramData.get("evaluationAreaCd");

        List<String> listItem1 = Arrays.asList(
                "upper", "middle", "lower"
        );

        // "listening", "reading", "viewing", "speaking", "writing", "presenting"
        if (validEvaluationAreas.contains(evaluationAreaCd)) {
            // 영역별 성취도 및 (상/중/하)
            List<Map> areaAchievementAitutor = tchDsbdMapper.selectTchDsbdAreaAchievementAitutor(paramData);
            List<Map> usdAchLevel = tchDsbdMapper.findTchDsbdStudentsByAreaAchievementLevel(paramData);

            // 두 리스트 합쳐서 한번에 반올림 처리
            List<Map> allLists = new ArrayList<>();
            allLists.addAll(areaAchievementAitutor);
            allLists.addAll(usdAchLevel);

            for (Map map : allLists) {
                if (map.containsKey("usdAchScrPercent")) {
                    double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                    int roundedValue = (int) Math.round(usdAchScrPercent);
                    map.put("usdAchScrPercent", roundedValue);
                }
            }

            // 상,중,하 학생 리스트
            areaAchievementAitutor.forEach(usdAch -> {
                String usdAchId = String.valueOf(usdAch.get("usdAchId"));

                Map<String, List<Map>> studentLists = usdAchLevel.stream()
                        .filter(student -> usdAchId.equals(String.valueOf(student.get("usdAchId"))))
                        .collect(Collectors.groupingBy(student -> {
                            String level = (String) student.get("level");
                            return "1".equals(level) ? "high" : "2".equals(level) ? "middle" : "low";
                        }));

                // 빈 리스트 처리
                studentLists.putIfAbsent("high", Collections.emptyList());
                studentLists.putIfAbsent("middle", Collections.emptyList());
                studentLists.putIfAbsent("low", Collections.emptyList());

                usdAch.put("studentLists", studentLists);
            });


            returnMap.put("areaAchievementAitutor", areaAchievementAitutor);

        } else {
            // 단어/문법/발음 > 성취도 및 (상/중/하)
            List<Map> areaAchievementAitutor = tchDsbdMapper.selectTchDsbdAreaAchievementAitutor2(paramData);
            List<Map> areaAchievementAitutorCnt = tchDsbdMapper.areaAchievementAitutorCnt(paramData);
            List<Map> usdAchLevel = tchDsbdMapper.findTchDsbdStudentsByAreaAchievementLevel2(paramData);

            if (!areaAchievementAitutor.isEmpty()){
                Map cntData = areaAchievementAitutorCnt.get(0); // 첫 번째 결과
                areaAchievementAitutor.get(0).put("gdUsdAchScrCnt", cntData.get("gdUsdAchScrCnt"));
                areaAchievementAitutor.get(0).put("avUsdAchScrCnt", cntData.get("avUsdAchScrCnt"));
                areaAchievementAitutor.get(0).put("bdUsdAchScrCnt", cntData.get("bdUsdAchScrCnt"));
            }


            // 두 리스트 합쳐서 한번에 반올림 처리
            List<Map> allLists = new ArrayList<>();
            allLists.addAll(areaAchievementAitutor);
            allLists.addAll(usdAchLevel);

            for (Map map : allLists) {
                if (map.containsKey("usdAchScrPercent")) {
                    double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                    int roundedValue = (int) Math.round(usdAchScrPercent);
                    map.put("usdAchScrPercent", roundedValue);
                }
            }

            // 상,중,하 학생 리스트
            areaAchievementAitutor.forEach(usdAch -> {
                String usdAchId = String.valueOf(usdAch.get("usdAchId"));

                Map<String, List<Map>> studentLists = usdAchLevel.stream()
                        .filter(student -> usdAchId.equals(String.valueOf(student.get("usdAchId"))))
                        .collect(Collectors.groupingBy(student -> {
                            String level = (String) student.get("level");
                            return "1".equals(level) ? "high" : "2".equals(level) ? "middle" : "low";
                        }));

                // 빈 리스트 처리
                studentLists.putIfAbsent("high", Collections.emptyList());
                studentLists.putIfAbsent("middle", Collections.emptyList());
                studentLists.putIfAbsent("low", Collections.emptyList());

                usdAch.put("studentLists", studentLists);
            });

            returnMap.put("areaAchievementAitutor", areaAchievementAitutor);
        }

        return returnMap;
    }

    /**
     * [교사] 학급관리 > 홈 대시보드 > 영역별 그래프 상세 All
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object selectTchDsbdAreaAchievementCommunicationDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<Map> CommunicationDetail = new ArrayList<>();

        List<String> listItem1 = Arrays.asList(
                "upper", "middle", "lower"
        );

        List<String> listItem2 = Arrays.asList(
                "stdtId", "flnm", "usdAchScr", "dfcltLvlTy", "studyYn"
        );

        Integer textbookId = Integer.parseInt((String) paramData.get("textbookId"));

        // 초등 5
        if (textbookId == 6981) {
            paramData.put("evaluationAreaCd", "evalCommu15");
        // 초등 6
        } else if (textbookId == 6982) {
            paramData.put("evaluationAreaCd", "evalCommu16");
        }

        // 의사소통 기능 상세(상/중/하)
        Map<Object, Object> vocabularyCountDetail =  AidtCommonUtil.filterToMap(listItem1, tchDsbdMapper.selectTchDsbdCountDetailAll(paramData));

        // 의사소통 기능 상세(학생 목록)
        List<LinkedHashMap<Object, Object>> vocabularyStudentList = AidtCommonUtil.filterToList(listItem2, tchDsbdMapper.selectTchDsbdStudentListAll(paramData));

        for (Map map : vocabularyStudentList) {
            // usdAchScrPercent 반올림
            if (map.containsKey("usdAchScr")) {
                double usdAchScrPercent = (double) map.get("usdAchScr");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScr", roundedValue);
            }

            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }
        }

        returnMap.put("AreaAchievementCountDetail", vocabularyCountDetail);
        returnMap.put("AreaAchievementStudentList", vocabularyStudentList);

        Map result = selectTchDsbdStatusVocabularyListAll(paramData);
        List<Map<String, Object>> vocabularyList = (List<Map<String, Object>>) result.get("VocabularyList");
        List<Map<String, Object>> vocabularyUnitInfo = (List<Map<String, Object>>) result.get("vocabularyUnitInfo");

        if (vocabularyList != null && !vocabularyList.isEmpty()) {
            for (Map<String, Object> vocabMap : vocabularyList) {
                if (vocabMap.containsKey("totalUsdSrc")) {
                    double usdAchScr = (double) vocabMap.get("totalUsdSrc");
                    int roundedValue = (int) Math.round(usdAchScr);
                    vocabMap.put("totalUsdSrc", roundedValue);
                }

                if (vocabMap.containsKey("usdAchScrPercent")) {
                    double usdAchScr = (double) vocabMap.get("usdAchScrPercent");
                    int roundedValue = (int) Math.round(usdAchScr);
                    vocabMap.put("usdAchScrPercent", roundedValue);
                }
            }
        }

        // 각 List 항목에 대해 매칭되는 unit 추가
        vocabularyList.forEach(vocab -> {
            Integer vocabMetaId = (Integer) vocab.get("metaId");

            vocabularyUnitInfo.stream()
                    .filter(unit -> vocabMetaId.equals(unit.get("metaId")))
                    .findFirst()
                    .ifPresent(unit -> {
                        vocab.put("unit", unit.get("unit"));
                        vocab.put("unitNum", unit.get("unitNum")); // unitNum 추가
                    });
        });

        CommunicationDetail.add(result);

        // 최고 점수와 최저 점수를 가진 항목 찾기
        Map<String, Object> highestScore = vocabularyList.stream()
                .max(Comparator.comparingDouble(item -> ((Number) item.get("usdAchScrPercent")).doubleValue()))
                .orElse(null);

        Map<String, Object> lowestScore = vocabularyList.stream()
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

        CommunicationDetail.add(detailMap);

        returnMap.put("CommunicationDetail", CommunicationDetail);

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
