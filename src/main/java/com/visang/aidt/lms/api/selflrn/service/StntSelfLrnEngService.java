package com.visang.aidt.lms.api.selflrn.service;

import com.visang.aidt.lms.api.dashboard.mapper.StntDsbdMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnEngMapper;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntSelfLrnEngService {
    private final StntSelfLrnEngMapper stntSelfLrnEngMapper;
    private final StntSelfLrnMapper stntSelfLrnMapper;
    private final StntRewardService stntRewardService;
    private final StntWrongnoteMapper stntWrongnoteMapper;
    private final StntNtcnService stntNtcnService;
    private final StntDsbdMapper stntDsbdMapper;

    @Transactional(readOnly = true)
    public Object findStntSelfLrnChapterConceptListEng(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        List<String> conceptInfoItem = Arrays.asList("achId", "stdAt", "stdMetaId", "stdNm", "usdAchScrPercent", "difficultyList");

        // 결과 정보
        List<LinkedHashMap<Object, Object>> conceptList = AidtCommonUtil.filterToList(conceptInfoItem, stntSelfLrnEngMapper.findStntSelfLrnChapterConceptListEng(paramData));
        Map<String, Object> conceptMinMap = stntSelfLrnEngMapper.findStntSelfLrnChapterConceptListEngMin(paramData);

        // 난이도 추가
        List<LinkedHashMap<String, Object>> difficultyList = new ArrayList<>();
        List<LinkedHashMap<String, Object>> difficultyList2 = new ArrayList<>();
        LinkedHashMap<String, Object> difficulty1 = null;
        LinkedHashMap<String, Object> difficulty2 = null;

        List codeList1  = List.of("ED01","ED02","ED03");
        List valList1   = List.of("상","중","하");
        List dispList1  = List.of("Level 3","Level 2","Level 1");

        //Pronunciation
        List codeList2  = List.of("D","S","W");
        List valList2   = List.of("지문","스크립트","어휘");
        List dispList2   = List.of("지문","스크립트","어휘");

        for(int i=0; i<codeList1.size(); i++) {
            difficulty1 = new LinkedHashMap<>();
            difficulty1.put("code",codeList1.get(i));
            difficulty1.put("val",valList1.get(i));
            difficulty1.put("dispNm",dispList1.get(i));

            difficultyList.add(difficulty1);

            //Pronunciation
            difficulty2 = new LinkedHashMap<>();
            difficulty2.put("code",codeList2.get(i));
            difficulty2.put("val",valList2.get(i));
            difficulty2.put("dispNm",dispList2.get(i));

            difficultyList2.add(difficulty2);
        }

        for (LinkedHashMap<Object, Object> conceptMap : conceptList) {
            if ("pronunciation".equals(MapUtils.getString(conceptMap,"stdNm"))) {
                conceptMap.put("difficultyList", difficultyList2);
            } else {
                conceptMap.put("difficultyList", difficultyList);
            }
        }

        for (Map map : conceptList) {
            // currUsdScr 반올림
            if (map.containsKey("usdAchScrPercent")) {
                double usdAchScrPercent = (double) map.get("usdAchScrPercent");
                int roundedValue = (int) Math.round(usdAchScrPercent);
                map.put("usdAchScrPercent", roundedValue);
            }

        }

        // Response
        returnMap.put("metaId", MapUtils.getIntValue(paramData, "metaId"));
        returnMap.put("recmndStdNm", MapUtils.getString(conceptMinMap, "stdNm"));
        returnMap.put("recmndStdLevel", MapUtils.getString(conceptMinMap, "recmndStdLevel"));
        returnMap.put("conceptList", conceptList);

        return returnMap;
    }

    public Object saveStntSelfLrnCreateEng(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","");
        Map<Object, Object> summaryInfo = new HashMap<>();

        // 영역, 난이도별 문제수
        int articleSize = 5;
        LinkedHashMap<String, Integer> requestCountMap = new LinkedHashMap<>();
        List<String> paramList = new ArrayList<>();
        String stdNm = MapUtils.getString(paramData, "stdNm").toLowerCase();
        paramData.put("stdNm", stdNm);

        String lvlId = MapUtils.getString(paramData, "lvlId");
        if ("vocabulary".equals(stdNm)) {
            if ("ED01".equals(lvlId)) {
                requestCountMap.put("상", 5);
            } else if ("ED02".equals(lvlId)) {
                requestCountMap.put("중", 3);
                requestCountMap.put("상", 2);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("중", 5);
            }
        } else if ("grammar".equals(stdNm)) {
            if ("ED01".equals(lvlId)) {
                requestCountMap.put("중", 2);
                requestCountMap.put("상", 3);
            } else if ("ED02".equals(lvlId)) {
                requestCountMap.put("중", 5);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("하", 3);
                requestCountMap.put("중", 2);
            }
        } else if ("reading".equals(stdNm)) {
            if ("ED01".equals(lvlId)) {
                requestCountMap.put("중", 2);
                requestCountMap.put("상", 3);
            } else if ("ED02".equals(lvlId)) {
                requestCountMap.put("중", 5);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("하", 3);
                requestCountMap.put("중", 2);
            }
        } else if ("listening".equals(stdNm)) {
            if ("ED01".equals(lvlId)) {
                requestCountMap.put("중", 2);
                requestCountMap.put("상", 3);
            } else if ("ED02".equals(lvlId)) {
                requestCountMap.put("중", 5);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("하", 3);
                requestCountMap.put("중", 2);
            }
        } else if ("pronunciation".equals(stdNm)) {
            articleSize = 1;
            //String schNm = MapUtils.getString(stntSelfLrnEngMapper.selectCurriSchool(paramData), "val", "");
            String schNm = MapUtils.getString(stntSelfLrnEngMapper.selectCurriSchool2(paramData), "val", "");
            if ("중학교".equals(schNm)) {
                if ("W".equals(lvlId)) {
                    paramList = Arrays.asList("Vocabulary");
                } else if ("S".equals(lvlId)) {
                    paramList = Arrays.asList("Everyday Communication 1", "Everyday Communication 2");
                } else if ("D".equals(lvlId)) {
                    paramList = Arrays.asList("Read");
                }
            } else {
                if ("W".equals(lvlId)) {
                    paramList = Arrays.asList("Vocabulary");
                } else if ("S".equals(lvlId)) {
                    paramList = Arrays.asList("Task 1-1", "Task 1-2");
                } else if ("D".equals(lvlId)) {
                    paramList = Arrays.asList("Task 1-1", "Task 2");
                }
            }
        }

        // Response Parameters
        List<String> listItem = Arrays.asList(
                "userId", "id", "name"
        );

        // 파라미터 복제
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);
        List<Map> totalArticleList = new ArrayList<>();

        if ("pronunciation".equals(stdNm)) {
            totalArticleList = stntSelfLrnEngMapper.selectSelfLrnEngArticles_pronunciation(innerParam, paramList);
        } else {
            // 난이도별로 맞춤 아티클 추출
            for (String key: requestCountMap.keySet()) {
                List<Map> articleList = new ArrayList<>();
                innerParam.put("difficulty", key);
                innerParam.put("cnt", requestCountMap.get(key));
                if (requestCountMap.get(key) > 0 ) {
                    articleList =  stntSelfLrnEngMapper.selectSelfLrnEngArticles(innerParam);
                    totalArticleList.addAll(articleList);
                }
            }
        }

        if(totalArticleList.isEmpty() || totalArticleList.size() < articleSize) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg",(totalArticleList.isEmpty()) ? "출제할 문항이 없습니다." : "출제할 문항이 부족합니다.");
            return rtnMap;
        } else {
            if ("pronunciation".equals(stdNm)) {
                paramData.put("lvlNum", MapUtils.getString(paramData,"lvlNm"));
            } else {
                if ("ED01".equals(lvlId)) {
                    paramData.put("lvlNum", "Level 3");
                } else if ("ED02".equals(lvlId)) {
                    paramData.put("lvlNum", "Level 2");
                } else if ("ED03".equals(lvlId)) {
                    paramData.put("lvlNum", "Level 1");
                }
            }

            int insertCnt = stntSelfLrnEngMapper.insertStntSelfLrnCreateEng(paramData);
            int id = MapUtils.getInteger(paramData, "id");
            log.info("자기주도학습 미존재 ID: "+id);
            paramData.remove("lvlNum");

            int cnt = 0;
            if(insertCnt > 0){
                // Rule 기반으로 추출한 문항 목록 추가
                paramData.put("list", totalArticleList);

                cnt = stntSelfLrnMapper.insertStntSelfLrnResultCreate(paramData);
            }

            if(insertCnt > 0  && cnt > 0) {
                List<String> infoItem = Arrays.asList(
                        "id", "stdCd", "stdCdNm", "stdNm", "stdUsdId"
                );
                List<String> nonelistItem = Arrays.asList(
                        "id", "moduleId", "moduleNum", "difficultyMetaId", "difficultyMetaCode", "difficultyMetaVal"
                         ,"hint","sbsChatting","sbsSolution","hintYN","sbsChattingYN","sbsSolutionYN"
                );
                summaryInfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnMapper.findStntSelfLrnCreateSummaryInfo(paramData));

                List<LinkedHashMap<Object, Object>> mdulInfoList = AidtCommonUtil.filterToList(nonelistItem, stntSelfLrnMapper.findStntSelfLrnCreateSummary(paramData));

                if (!mdulInfoList.isEmpty()) {
                    summaryInfo.put("mdulInfoList", mdulInfoList);
                }
            }
        }

        // list 삭제
        paramData.remove("list");

        rtnMap.put("slfStdInfo", summaryInfo);

        return rtnMap;
    }

    public Object saveStntSelfLrnCreateTestEng(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","");
        Map<Object, Object> summaryInfo = new HashMap<>();

        // 영역별 2문제씩 총 8문제 출제
        int articleSize = 8;
        List<String> engAreaList = Arrays.asList("vocabulary", "grammar", "reading", "listening");


        List<Map> totalArticleList = new ArrayList<>();

        // 각 영역별로 문제 추출
        for (String engArea : engAreaList) {
            // 파라미터 복제
            Map<String, Object> innerParam = ObjectUtils.clone(paramData);
            innerParam.put("engArea", engArea);

            // vocabulary는 '하' 난이도 문제가 없으므로 '중' 문제 출제
            if ("vocabulary".equals(engArea)) {
                innerParam.put("difficulty", "중");
            } else {
                innerParam.put("difficulty", "하");
            }

            innerParam.put("cnt", 2);

            List<Map> articleList = stntSelfLrnEngMapper.selectDiagnosticEngArticles(innerParam);

            totalArticleList.addAll(articleList);
        }
        if (totalArticleList.isEmpty()) {
            rtnMap.put("resultOk", false);
            rtnMap.put("resultMsg", "출제할 문항이 없습니다.");
            return rtnMap;
        } else {
            paramData.put("lvlNum", "Level 1");
            paramData.put("lvlId","ED03");
            paramData.put("lvlNm","하");

            int insertCnt = stntSelfLrnEngMapper.insertStntSelfLrnDiagEng(paramData);
            int id = MapUtils.getInteger(paramData, "id");
            log.info("자기주도학습 미존재 ID: " + id);
            paramData.remove("lvlNum");

            int cnt = 0;
            if (insertCnt > 0) {
                // Rule 기반으로 추출한 문항 목록 추가
                paramData.put("list", totalArticleList);
                cnt = stntSelfLrnMapper.insertStntSelfLrnResultCreate(paramData);
            }

            if (insertCnt > 0 && cnt > 0) {
                List<String> infoItem = Arrays.asList(
                        "id", "stdCd", "stdCdNm", "stdNm", "stdUsdId"
                );
                List<String> nonelistItem = Arrays.asList(
                        "id", "moduleId", "moduleNum", "difficultyMetaId", "difficultyMetaCode", "difficultyMetaVal"
                        ,"hint","sbsChatting","sbsSolution","hintYN","sbsChattingYN","sbsSolutionYN"
                );
                summaryInfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnMapper.findStntSelfLrnCreateSummaryInfo(paramData));

                List<LinkedHashMap<Object, Object>> mdulInfoList = AidtCommonUtil.filterToList(nonelistItem, stntSelfLrnMapper.findStntSelfLrnCreateSummary(paramData));

                if (!mdulInfoList.isEmpty()) {
                    summaryInfo.put("mdulInfoList", mdulInfoList);
                }
            }
        }

        // list 삭제
        paramData.remove("list");

        rtnMap.put("slfStdInfo", summaryInfo);

        return rtnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSelfLrnResultSummaryEng(Object paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "id", "stdCd","stdCdNm", "stdNm", "lvlId", "lvlNm", "beforeUsdScr", "afterUsdScr", "totMdulCnt", "stdMdulCnt", "anwNum", "wrngNum", "totalReward"
        );

        List<String> listItem = Arrays.asList(
                "id","moduleId", "moduleNum", "thumbnail", "questionStr", "hashTags", "url" , "image", "stdStDt", "stdEdDt", "subMitAnw","subMitAnwUrl", "errata", "smExmAt", "srcModuleId","srcSlfResultId", "aiTutUseAt", "hdwrtCn"
        );

        Map<Object, Object> summaryLinfo = null;

        summaryLinfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnEngMapper.findStntSelfLrnResultSummaryInfoEng(paramData));

        List<LinkedHashMap<Object, Object>> stdImage = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.findStntSelfLrnResultSummary(paramData));

        // 결과 정보
        List<LinkedHashMap<Object, Object>> stdResultList = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.findStntSelfLrnResultSummary(paramData));
        if(!stdResultList.isEmpty()){
            for(int i=0; i < stdResultList.size(); i++){
                Map<Object, Object> imginfo = new HashMap<>();
                if(stdImage.get(i).get("moduleId").equals(stdResultList.get(i).get("moduleId"))){
                    imginfo.put("url",stdImage.get(i).get("url"));
                    imginfo.put("image",stdImage.get(i).get("image"));
                    stdResultList.get(i).put("mdulImage",imginfo);
                }
            }
            summaryLinfo.put("stdResultList",stdResultList);
        }

        return summaryLinfo;
    }

    public Object saveStntSelfLrnReceiveEng(Map<String, Object> paramData) throws Exception {
        Map<Object, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","");
        Map<Object, Object> summaryInfo = new HashMap<>();

        Map<String, Object> beforeModuleMap = stntSelfLrnEngMapper.selectStntSelfLrnReceiveEng_beforeModuleMap(paramData);
        List<Map> list = stntSelfLrnEngMapper.selectStntSelfLrnReceiveEng(beforeModuleMap);

        if(list.isEmpty()) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg", "출제할 문항이 없습니다.");
            return rtnMap;
        } else {
            // Rule 기반으로 추출한 문항 목록 추가
            beforeModuleMap.put("list", list);

            int insertCnt = stntSelfLrnMapper.insertStntSelfLrnResultReceive(beforeModuleMap);
            int id = MapUtils.getInteger(beforeModuleMap, "id");
            log.info("자기주도학습 유사문항 미존재 ID: "+id);

            if(insertCnt > 0) {
                List<String> infoItem = Arrays.asList("id", "moduleId");
                summaryInfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnMapper.findStntSelfLrnCreateReceiveInfo(beforeModuleMap));

                summaryInfo.put("resultOk", true);
                summaryInfo.put("resultMsg", "성공");
                rtnMap.putAll(summaryInfo);
            }
        }

        return rtnMap;
    }

    public Object saveStntSelfLrnCreateElementaryEng(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","성공");
        Map<Object, Object> summaryInfo = new HashMap<>();

        // Response Parameters
        List<String> listItem = Arrays.asList(
                "userId", "id", "name"
        );

        // 파라미터 복제
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        // 문항 조회 (초등영어 매핑 테이블의 set id 기준)
        List<Map> articleList = stntSelfLrnEngMapper.selectSelfLrnElementaryEngArticles(paramData);
        if(articleList.isEmpty()) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg","출제할 문항이 없습니다.");
            return rtnMap;
        } else {
            int insertCnt = 0;
            int cnt = 0;

            // 학습 횟수가 0 보다 큰 경우 (스스로 학습을 다시 한 경우)
            List<Map> slfStdInfoList = stntSelfLrnEngMapper.findSlfStdInfo(paramData);
            for (Map slfStdInfo : slfStdInfoList) {
                slfStdInfo.putAll(paramData);

                // 오답노트 데이터 삭제
                int dWonCnt = stntSelfLrnEngMapper.deleteWonAswNote(slfStdInfo);
                log.info(" 오답노트 삭제: " + dWonCnt);

                // slf_std_result_info 삭제
                int dCnt = stntSelfLrnEngMapper.deleteStntSelfLrnResult(slfStdInfo);
                log.info("slf_std_result_info 삭제: " + dCnt);

                //aidt_lms.slf_std_info 삭제
                int dInfoCnt = stntSelfLrnEngMapper.deleteStntSelfLrnInfo(slfStdInfo);
                log.info("slf_std_info 삭제: " + dInfoCnt);
            }

            insertCnt = stntSelfLrnEngMapper.insertStntSelfLrnCreateElementaryEng(paramData);
            int id = MapUtils.getInteger(paramData, "id");
            log.info("자기주도학습 미존재 ID: " + id);

            cnt = 0;
            if (insertCnt > 0) {
                // Rule 기반으로 추출한 문항 목록 추가
                paramData.put("list", articleList);

                cnt = stntSelfLrnMapper.insertStntSelfLrnResultCreate(paramData);
            }

            // 학습 정보 조회
            if(insertCnt > 0  && cnt > 0) {
                List<String> infoItem = Arrays.asList(
                        "id", "stdCd", "stdCdNm", "stdNm", "stdUsdId"
                );
                List<String> nonelistItem = Arrays.asList(
                        "id", "moduleId", "moduleNum", "difficultyMetaId", "difficultyMetaCode", "difficultyMetaVal"
                        ,"hint","sbsChatting","sbsSolution","hintYN","sbsChattingYN","sbsSolutionYN"
                );
                summaryInfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnMapper.findStntSelfLrnCreateSummaryInfo(paramData));

                List<LinkedHashMap<Object, Object>> mdulInfoList = AidtCommonUtil.filterToList(nonelistItem, stntSelfLrnMapper.findStntSelfLrnCreateSummary(paramData));

                if (!mdulInfoList.isEmpty()) {
                    summaryInfo.put("mdulInfoList", mdulInfoList);
                }
            }
        }

        // list 삭제
        paramData.remove("list");

        rtnMap.put("slfStdInfo", summaryInfo);

        return rtnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSelfLrnUnitEng(Object paramData) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();

        // 단원 별 평균 성취도 조회 (평균 성취도 오름차순)
        List<String> resultItem = Arrays.asList("unitNum","metaId","metaVal","unitScrPercent");
        //textbkId
                //paramData.
        Map<String, Object> pData = (Map<String, Object>)paramData;
        pData.put("textbookId", pData.get("textbkId"));

        // resultMap.put("unitScrList", AidtCommonUtil.filterToList(resultItem, stntSelfLrnEngMapper.findStntSelfLrnUnitEng(paramData)));
        resultMap.put("unitScrList", AidtCommonUtil.filterToList(resultItem, stntDsbdMapper.selectStntDsbdUnitAchievementList((Map<String, Object>) paramData)));

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSelfLrnSetsElementaryEng(Object paramData) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();

        // 초등영어 매핑 테이블 조회
        List<String> resultItem = Arrays.asList("setId","setName","setThumbnail","evaluationAreaIdx","evaluationAreaNm","difficulty","difficultyNm","difficultyStar","evaluationArea","stdCnt", "edAt");

        List<Map> evaluationSetsList = stntSelfLrnEngMapper.findStntSelfLrnSetsElementaryEng(paramData);


        /*
        * partitioningBy 사용
        * 초등 영어 스스로 학습 목록 기본 목록과 진단하기 값 분리
        * 진단하기 구분: evaluationAreaIdx == 13
        * */
        Map<Boolean, List<Map>> partitionedMap = evaluationSetsList.stream()
                                                                .collect(Collectors.partitioningBy( item -> {
                                                                    Object evaluationAreaIdx = item.get("evaluationAreaIdx");
                                                                    return evaluationAreaIdx != null && 13 == (Integer) evaluationAreaIdx;
                                                                }));
        // true: evaluationAreaIdx가 13인 리스트, false: 나머지 리스트
        List<Map> evaluationDiagnosticSetsList = partitionedMap.get(true);
        List<Map> otherEvaluationSetsList = partitionedMap.get(false);



        Map<Object, Object> evaluationDiagnosticSetsMap = new HashMap<>();

        if (!evaluationDiagnosticSetsList.isEmpty()) {
            resultMap.put("evaluationDiagnosticSetsMap", evaluationDiagnosticSetsList.get(0));
        } else {
            resultMap.put("evaluationDiagnosticSetsMap", evaluationDiagnosticSetsMap);
        }

        resultMap.put("evaluationSetsList", AidtCommonUtil.filterToList(resultItem, otherEvaluationSetsList));

        return resultMap;
    }
}
