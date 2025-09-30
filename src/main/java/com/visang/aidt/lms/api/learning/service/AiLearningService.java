package com.visang.aidt.lms.api.learning.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.contents.dto.*;
import com.visang.aidt.lms.api.contents.service.ContentsService;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.learning.vo.*;
import com.visang.aidt.lms.api.mq.service.AssignmentGaveService;
import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.global.AidtConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiLearningService {
    private final AiLearningMapper aiLearningMapper;
    private final AssignmentGaveService assignmentGaveService;
    private final ObjectMapper objectMapper;

    @Autowired
    private ContentsService contentsService;

    private static final String RESULT_OK = "resultOk";
    private static final String RESULT_MSG = "resultMsg";

    @Transactional(readOnly = true)
    public List<Map<String,Object>> findTargetEvlList(Map<String, Object> paramData) throws Exception {
        return aiLearningMapper.findTargetEvlList(paramData);
    }

    // AI 처방학습 대상 evl_id 조회
    @Transactional(readOnly = true)
    public Object findTargetEvlIdList(Map<String, Object> paramData) throws Exception {

        List<Map<String,Object>> evlList = this.findTargetEvlList(paramData);

        List<Integer> evlIdList = CollectionUtils.emptyIfNull(evlList)
                .stream()
                .map(a -> (Integer) a.get("id"))
                .collect(Collectors.toList());

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("evlIdList", evlIdList);
        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findAutoCreateAiLearningEvl(Map<String, Object> paramData) throws Exception {

        log.info("evlId:{} START==============================", paramData.get("evlId"));

        // temporary table 을 만들면 빠르고, 더 간단하게 로직을 만들 수 있겠지만, db계정에 create 권한이 없을 것 같아 자바로직으로 진행함.

        // 1. 어제 (00시 ~ 24시) 사이에 완료된 평가 중에서
        //    - 제출여부=Y, 채점여부=Y, errata=2(오답), mrk_ty=1(자동채점) 인 article 정보 조회
        // evl_id 는 이 앞에서 조회하여 evl_id 별로 호출 했으므로, 날짜 조건은 뺀다.
        List<AiArticleVO> targetArticleList =  aiLearningMapper.findAutoCreateAiLearningEvlStep1(paramData);

        // 2. 학생별로 loop 돌면서, 처방학습 article 목록을 반환
        // 대상학생 조회
        List<String> mamoymIdList = targetArticleList.stream()
                .map(a -> a.getMamoym_id())
                .distinct().toList();

        Map<String, Object> innerParam = new HashMap<>();

        // 모든 처리를 끝내고 반환할 AI처방 article 목록
        List<AiArticleListByStntVO> resultList = new ArrayList<>();

        for (String mamoymId : mamoymIdList) {
            log.debug("mamoymId:{} -------------------------------", mamoymId);

            List<AiArticleVO> innerResultList = new ArrayList<>(); // 학생별 추가할 AI처방 article목록

            // 학생별 오답 article 목록만 필터링
            List<AiArticleVO> userArticleList = targetArticleList
                    .stream().filter(a -> mamoymId.equals(a.getMamoym_id()))
                    .collect(Collectors.toList());

            // 추가조건 (wrter_id,cla_id,textbk_id) 추가
            innerParam.putAll(paramData);
            // 학생별 학습이력을 조회한다.
            innerParam.put("mamoymId", mamoymId);
            List<Map> hisArticleList =  aiLearningMapper.findAutoCreateAiLearningEvlStep2(innerParam); // 학습이력이 있는 article 정보

            // 학습완료 하여 제외시킬 articleId 목록
            List<String>  removeIdList = hisArticleList.stream()
                    .map(m -> (String)m.get("article_id") )
                    .distinct().toList();

            // 새로 추가되어 제외시켜야 할 articleId 목록
            List<String> addedIdList = new ArrayList<>();

            innerParam.clear();
            innerParam.put("removeIdList", removeIdList);
            innerParam.put("addedIdList", addedIdList);
            for (AiArticleVO faultVo : userArticleList) { // 오답 article 목록
//                innerParam.put("gubun", getMeanVo(faultVo)); // studyMap1 ~ difficulty 까지 "_" 로 합친 구분값
                innerParam.put("studyMap1", faultVo.getStudyMap1());
                innerParam.put("studyMap2", faultVo.getStudyMap2());
                innerParam.put("studyMap3", faultVo.getStudyMap3());
                innerParam.put("studyMap_1", faultVo.getStudyMap_1());
                innerParam.put("studyMap_2", faultVo.getStudyMap_2());
                innerParam.put("difficulty", faultVo.getDifficulty());
                AiArticleVO matchArticleVo = aiLearningMapper.findAutoCreateAiLearningEvlStep3(innerParam); // 매칭되는 article 1건만 조회

                if (matchArticleVo != null) {
                    matchArticleVo.setMamoym_id(mamoymId);
                    matchArticleVo.setEvl_id(Long.valueOf(String.valueOf(paramData.get("evlId"))));
                    innerResultList.add(matchArticleVo); // 학생별 추천 아티클 목록 add

                    addedIdList.add(matchArticleVo.getArticle_id()); // 결과에 추가되어 제외시켜야할 article
                    log.debug("matched {}: {}-{}", mamoymId, faultVo.getArticle_id(), matchArticleVo.getArticle_id());
                }
                else {
                    // 매칭되는 article 이 없을때.
                    log.warn("matchArticle is null!! : {}-{}", mamoymId, faultVo.getArticle_id());

                    // 학습 완료한 history 에서 가져와서 넣는다.(articleType=21 문항, articleCategory=61 비교과)
                    Optional<Map> foundMap = hisArticleList.stream()
                            .filter(map -> !addedIdList.contains(map.get("article_id")))
                            .filter(map -> map.get("articleType") != null && (Integer)map.get("articleType") == 21)
                            .filter(map -> map.get("articleCategory") != null && (Integer)map.get("articleCategory") == 61)
                            .findFirst();


                    if (foundMap.isPresent()) {
                        // history 에서 뒤져서 있으면
                        Map addMap = foundMap.get();

                        AiArticleVO _matchHistVo =  AiArticleVO.builder()
                                .mamoym_id(mamoymId)
                                .textbook_id(AidtCommonUtil.getLongValueFromObject(addMap.get("textbook_id")))
                                .evl_id(AidtCommonUtil.getLongValueFromObject(paramData.get("evlId")))
                                .article_id(addMap.get("article_id").toString())
                                .studyMap1(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap1")))
                                .studyMap2(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap2")))
                                .studyMap3(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap3")))
                                .studyMap_1(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap_1")))
                                .studyMap_2(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap_2")))
                                .difficulty(AidtCommonUtil.getLongValueFromObject(addMap.get("difficulty")))
                                .articleCategory(AidtCommonUtil.getLongValueFromObject(addMap.get("articleCategory")))
                                .gubun((String)addMap.get("gubun"))
                                .build();

                        innerResultList.add(_matchHistVo); // 학생별 추천 아티클 목록 add

                        addedIdList.add((String) addMap.get("article_id")); // 추가된 목록에 id add
                    } else {
                        // history 에도 없어서, 최종 추가 실패
                        log.warn("finally not found! : {}-{}", mamoymId, faultVo.getArticle_id());
                    }
                }
            }

            AiArticleListByStntVO aiArticleListByStntVO = AiArticleListByStntVO.builder()
                    .mamoymId(mamoymId)
                    .evlId(AidtCommonUtil.getLongValueFromObject(paramData.get("evlId")))
                    .articleList(innerResultList)
                    .build();

            resultList.add(aiArticleListByStntVO); // return list 에 add

        } // for (String mamoymId : mamoymIdList) END

        log.debug("resultList.size={}", resultList.size());
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("evlId", paramData.get("evlId"));
        returnMap.put("stntArticleList", resultList);
        return returnMap;
    }


    /**
     * null 인 항목은 제외하고, 의미있는 항목만 string으로 합친다.
     * @param faultVo
     * @return 대단원_중단원_소단원_지식요인_유형
     */
    private String getMeanVo(AiArticleVO faultVo) throws Exception {
        StringBuffer sb = new StringBuffer();

        if (faultVo.getStudyMap1() > 0) {
            sb.append(String.valueOf(faultVo.getStudyMap1())).append("_");
        }
        if (faultVo.getStudyMap2() > 0) {
            sb.append(String.valueOf(faultVo.getStudyMap2())).append("_");
        }
        if (faultVo.getStudyMap3() > 0) {
            sb.append(String.valueOf(faultVo.getStudyMap3())).append("_");
        }
        if (faultVo.getStudyMap_1() > 0) {
            sb.append(String.valueOf(faultVo.getStudyMap_1())).append("_");
        }
        if (faultVo.getStudyMap_2() > 0) {
            sb.append(String.valueOf(faultVo.getStudyMap_2())).append("_");
        }
        if (faultVo.getDifficulty() > 0) {
            sb.append(String.valueOf(faultVo.getDifficulty()));
        }

        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '_') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> findTargetTaskList(Map<String, Object> paramData) throws Exception {
        return aiLearningMapper.findTargetTaskList(paramData);
    }

    @Transactional(readOnly = true)
    public Object findTargetTaskIdList(Map<String, Object> paramData) throws Exception {

        List<Map<String,Object>> taskList = this.findTargetTaskList(paramData);

        List<Integer> taskIdList = CollectionUtils.emptyIfNull(taskList)
                .stream()
                .map(a -> (Integer) a.get("id"))
                .collect(Collectors.toList());

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("taskIdList", taskIdList);
        return returnMap;
    }

    // 자동문항생성-AI 처방 학습-과제
    @Transactional(readOnly = true)
    public Object findAutoCreateAiLearningTask(Map<String, Object> paramData) throws Exception {

        log.info("taskId:{} START==============================", paramData.get("taskId"));

        // temporary table 을 만들면 빠르고, 더 간단하게 로직을 만들 수 있겠지만, db계정에 create 권한이 없을 것 같아 자바로직으로 진행함.

        // 1. 어제 (00시 ~ 24시) 사이에 완료된 평가 중에서
        //    - 제출여부=Y, 채점여부=Y, errata=2(오답), mrk_ty=1(자동채점) 인 article 정보 조회
        // evl_id 는 이 앞에서 조회하여 evl_id 별로 호출 했으므로, 날짜 조건은 뺀다.
        List<AiArticleVO> targetArticleList =  aiLearningMapper.findAutoCreateAiLearningTaskStep1(paramData);

        // 2. 학생별로 loop 돌면서, 처방학습 article 목록을 반환
        // 대상학생 조회
        List<String> mamoymIdList = targetArticleList.stream()
                .map(a -> a.getMamoym_id())
                .distinct().toList();

        Map<String, Object> innerParam = new HashMap<>();

        // 모든 처리를 끝내고 반환할 AI처방 article 목록
        List<AiArticleListByStntVO> resultList = new ArrayList<>();

        for (String mamoymId : mamoymIdList) {
            log.debug("mamoymId:{} -------------------------------", mamoymId);

            List<AiArticleVO> innerResultList = new ArrayList<>(); // 학생별 추가할 AI처방 article목록

            // 학생별 오답 article 목록만 필터링
            List<AiArticleVO> userArticleList = targetArticleList
                    .stream().filter(a -> mamoymId.equals(a.getMamoym_id()))
                    .collect(Collectors.toList());

            // 추가조건 (wrter_id,cla_id,textbk_id) 추가
            innerParam.putAll(paramData);
            // 학생별 학습이력을 조회한다.
            innerParam.put("mamoymId", mamoymId);
            List<Map> hisArticleList =  aiLearningMapper.findAutoCreateAiLearningEvlStep2(innerParam); // 학습이력이 있는 article 정보 (evl 과 공통사용)

            // 학습완료 하여 제외시킬 articleId 목록
            List<String>  removeIdList = hisArticleList.stream()
                    .map(m -> (String)m.get("article_id") )
                    .distinct().toList();

            // 새로 추가되어 제외시켜야 할 articleId 목록
            List<String> addedIdList = new ArrayList<>();

            innerParam.clear();
            innerParam.put("removeIdList", removeIdList);
            innerParam.put("addedIdList", addedIdList);
            for (AiArticleVO faultVo : userArticleList) { // 오답 article 목록
//                innerParam.put("gubun", getMeanVo(faultVo)); // studyMap1 ~ difficulty 까지 "_" 로 합친 구분값
                innerParam.put("studyMap1", faultVo.getStudyMap1());
                innerParam.put("studyMap2", faultVo.getStudyMap2());
                innerParam.put("studyMap3", faultVo.getStudyMap3());
                innerParam.put("studyMap_1", faultVo.getStudyMap_1());
                innerParam.put("studyMap_2", faultVo.getStudyMap_2());
                innerParam.put("difficulty", faultVo.getDifficulty());
                AiArticleVO matchArticleVo = aiLearningMapper.findAutoCreateAiLearningEvlStep3(innerParam); // 매칭되는 article 1건만 조회 (evl 과 공통사용)

                if (matchArticleVo != null) {
                    matchArticleVo.setMamoym_id(mamoymId);
                    matchArticleVo.setTask_id(Long.valueOf(String.valueOf(paramData.get("taskId"))));
                    innerResultList.add(matchArticleVo); // 학생별 추천 아티클 목록 add

                    addedIdList.add(matchArticleVo.getArticle_id().toString()); // 결과에 추가되어 제외시켜야할 article
                    log.debug("matched {}: {}-{}", mamoymId, faultVo.getArticle_id(), matchArticleVo.getArticle_id());
                }
                else {
                    // 매칭되는 article 이 없을때.
                    log.warn("matchArticle is null!! : {}-{}", mamoymId, faultVo.getArticle_id());

                    // 학습 완료한 history 에서 가져와서 넣는다.(articleType=21 문항, articleCategory=61 비교과)
                    Optional<Map> foundMap = hisArticleList.stream()
                            .filter(map -> !addedIdList.contains(map.get("article_id")))
                            .filter(map -> map.get("articleType") != null && (Integer)map.get("articleType") == 21)
                            .filter(map -> map.get("articleCategory") != null && (Integer)map.get("articleCategory") == 61)
                            .findFirst();


                    if (foundMap.isPresent()) {
                        // history 에서 뒤져서 있으면
                        Map addMap = foundMap.get();

                        AiArticleVO _matchHistVo =  AiArticleVO.builder()
                                .mamoym_id(mamoymId)
                                .textbook_id(AidtCommonUtil.getLongValueFromObject(addMap.get("textbook_id")))
                                .task_id(AidtCommonUtil.getLongValueFromObject(paramData.get("taskId")))
                                .article_id(addMap.get("article_id").toString())
                                .studyMap1(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap1")))
                                .studyMap2(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap2")))
                                .studyMap3(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap3")))
                                .studyMap_1(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap_1")))
                                .studyMap_2(AidtCommonUtil.getLongValueFromObject(addMap.get("studyMap_2")))
                                .difficulty(AidtCommonUtil.getLongValueFromObject(addMap.get("difficulty")))
                                .articleCategory(AidtCommonUtil.getLongValueFromObject(addMap.get("articleCategory")))
                                .gubun((String)addMap.get("gubun"))
                                .build();

                        innerResultList.add(_matchHistVo); // 학생별 추천 아티클 목록 add

                        addedIdList.add((String) addMap.get("article_id")); // 추가된 목록에 id add
                    } else {
                        // history 에도 없어서, 최종 추가 실패
                        log.warn("finally not found! : {}-{}", mamoymId, faultVo.getArticle_id());
                    }
                }
            }

            AiArticleListByStntVO aiArticleListByStntVO = AiArticleListByStntVO.builder()
                    .mamoymId(mamoymId)
                    .taskId(AidtCommonUtil.getLongValueFromObject(paramData.get("taskId")))
                    .articleList(innerResultList)
                    .build();

            resultList.add(aiArticleListByStntVO); // return list 에 add

        } // for (String mamoymId : mamoymIdList) END

        log.debug("resultList.size={}", resultList.size());
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("taskId", paramData.get("taskId"));
        returnMap.put("stntArticleList", resultList);
        return returnMap;
    }

    public Map<String, Object> createAiLearningBatchEvl(Map<String, Object> paramData) throws Exception {
        try {
            long startTime = System.currentTimeMillis();
            log.debug("createAiLearningBatchEvl START-------------------------------");

            int setCount = 0; // 세트지 생성건수
            int idCount = 0; // 대상 평가id 건수
            int articleCount = 0; // 추출된 총 article 갯수
            int stntCnt = 0; // 대상 학생 수
            int successCnt = 0; // 성공하여 세트지 생성완료된 개수
            int failCnt = 0; // 실패하거나 오답이 없는등 대상 아티클이 없는 개수

            List<Map<String, Object>> evlList = this.findTargetEvlList(paramData);

            if (CollectionUtils.isNotEmpty(evlList)) {
                for (Map<String, Object> evlInfo : evlList) {
                    int evlId = MapUtils.getIntValue(evlInfo, "id");
                    int evlSetCount = 0; // 평가지당 생성된 세트지 건수

                    log.info("{}.evlId:{}", ++idCount, evlId);

                    Map<String, Object> oldSetsMap = getOldSetsMap("eval", evlId); // 참고할 세트지
                    String creator = String.valueOf(oldSetsMap.get("creator")); // 세트지와 join 한 evl_info 에서 가져온 작성자id

                    Map<String, Object> innerParam = new HashMap<>();
                    innerParam.put("evlId", evlId);

                    // 추가조건 (wrter_id,cla_id,textbk_id) 추가
                    innerParam.putAll(evlInfo);
                    innerParam.remove("id");

                    // 출제할 아티클을 추출
                    Map<String, Object> articleMap = (Map<String, Object>) this.findAutoCreateAiLearningEvl(innerParam);

                    // 출제할 학생별 아티클이 있음.
                    if (articleMap.get("stntArticleList") != null && !((List) articleMap.get("stntArticleList")).isEmpty()) {

                        // 20240411 로직추가 - setCategory 를 구하기 위해 textbk_id -> brand_id 를 구함.
                        int brandId = aiLearningMapper.findBrandIdByEvalId(evlId);

                        // evl_info 생성
                        int newEvlInfoCnt = aiLearningMapper.insertEvlInfo(innerParam);

                        long newId = AidtCommonUtil.getLongValueFromObject(innerParam.get("id")); // 생성된 evlId
                        log.info("newId:{}", newId);

                        List<AiArticleListByStntVO> stntArticleList = (List<AiArticleListByStntVO>) articleMap.get("stntArticleList");

                        // 학생별
                        for (AiArticleListByStntVO _stntArticleVo : stntArticleList) {

                            String mamoymId = _stntArticleVo.getMamoymId();
                            List<AiArticleVO> aiArticleVOList = _stntArticleVo.getArticleList();

                            // 학생이 가진 article 목록으로 세트지 생성
                            Map<String, Object> createResultMap = createSetsForAiLearning(aiArticleVOList, oldSetsMap, "eval", brandId);

                            boolean isSuccess = (boolean) createResultMap.get("isSuccess");
                            String newSetId = (String) createResultMap.getOrDefault("setsId", "");
                            String errMsg = String.valueOf(createResultMap.get("errMsg"));


                            log.info("isSucces:{}", isSuccess); // 생성시 오류가 없었다는 뜻. 실제 세트지가 생성된 것과는 무관
                            log.info("setsId:{}", newSetId); // 생성된 setId : null or 0 이 아니면 정상적으로 생성되었다는 뜻
                            log.info("errMsg:{}", errMsg);

                            // 세트지 생성에 성공했으면 count++
                            if (isSuccess && ObjectUtils.isNotEmpty(newSetId)) {
                                log.info("Sets Created:{}", newSetId);
                                setCount++;
                                evlSetCount++;

                                //세트지가 만들어 졌으므로, SetSummary, evl_result_info, evl_result_detail 정보를 생성한다.
                                log.info("SetSummary 생성-{}", evlId);
                                SetSummarySaveRequestVO setSummarySaveRequestVO = createSetSummary(newSetId, creator, aiArticleVOList);

                                log.info("evl_result_info 생성-{}", evlId);
                                Long newEvlResultId = null;
                                newEvlResultId = createEvlResultInfo(newSetId, newId, mamoymId, creator);
                                log.info("newEvlResultId:{}", newEvlResultId);

                                log.info("evl_result_detail 생성-{}", evlId);
                                int detailCnt = createEvlResultDetail(newSetId, newEvlResultId, creator);
                                log.info("evl_result_detail.cnt:{}", detailCnt);
                            } else {
                                log.warn("Fail Sets Creation:{}", evlId);
                            }
                            stntCnt++; // 세트지 성공여부에 관계없이 학생수 증가
                            articleCount = +_stntArticleVo.getArticleList().size(); // 세트지 성공여부에 관계없이 아티클수 증가
                        }
                    }

                    // 세트지 생성건수로 성공/실패 판단 - 실패하면 X 표시하여 다음에 대상이 안되게 한다.
                    String prscrStdCrtAt = "X"; // 실패등의 이유로 생성이 안됐을때
                    if (evlSetCount > 0) {
                        prscrStdCrtAt = "Y"; // 뭔가 생성 됐을때. 모든 학생 및 set지를 다 판단 할 수는 없음.
                        successCnt++;
                    } else {
                        failCnt++;
                    }

                    int updateCnt = updateAfterCreateEvlInfo(evlId, prscrStdCrtAt, creator);
                }
            }
            long endTime = System.currentTimeMillis();
            log.debug("createAiLearningBatchEvl END------------------------------- : {}", (endTime - startTime));

            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("duration", endTime - startTime);
            returnMap.put("setCount", setCount);
            returnMap.put("idCount", idCount);
            returnMap.put("articleCount", articleCount);
            returnMap.put("stntCnt", stntCnt);
            returnMap.put("successCnt", successCnt);
            returnMap.put("failCnt", failCnt);

            return returnMap;
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            throw e;
         /*   e.printStackTrace(); */
        }
    }

    /**
     * evl_info 에 생성 여부 저장 (Y/X) : X는 대상이 없거나 생성실패
     * @param evlId
     * @param prscrStdCrtAt
     * @param creator
     * @return
     */
    private int updateAfterCreateEvlInfo(int evlId, String prscrStdCrtAt, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();

        paramData.put("evlId", evlId);
        paramData.put("prscrStdCrtAt", prscrStdCrtAt);
        paramData.put("creator", creator);

        log.debug("{}-prscrStdCrtAt:{}", evlId, prscrStdCrtAt);

        return aiLearningMapper.updateAfterCreateEvlInfo(paramData);

    }

    /**
     * evl_result_info 생성
     * @param newSetId
     * @param mamoymId
     * @return 생성된 evl_result_info.id
     */
    private Long createEvlResultInfo(String newSetId, long evlId, String mamoymId, String creator) throws Exception {
        Map<String, Object> paramData = new HashMap<>();

        paramData.put("evlId", evlId);
        paramData.put("setsId", newSetId);
        paramData.put("mamoymId", mamoymId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createEvlResultInfo(paramData);

        return AidtCommonUtil.getLongValueFromObject(paramData.get("id"));
    }

    /**
     * evl_result_detail 생성
     * @param setsId
     * @param evlResultId
     * @param creator
     * @return 생성 개수
     */
    private int createEvlResultDetail(String setsId, Long evlResultId, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();
        paramData.put("evlResultId", evlResultId);
        paramData.put("setsId", setsId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createEvlResultDetail(paramData);
        log.info("createEvlResultDetail.cnt:{}",cnt);

        return cnt;
    }

    /**
     * SetSummary 정보 생성
     * @param newSetId
     * @param creator
     * @param aiArticleVOList
     * @return setSummarySaveRequestVO
     */
    private SetSummarySaveRequestVO createSetSummary(String newSetId, String creator, List<AiArticleVO> aiArticleVOList) throws Exception {

        SetSummarySaveRequestVO setSummarySaveRequestVO = new SetSummarySaveRequestVO();
        setSummarySaveRequestVO.setSet_id( newSetId);
        setSummarySaveRequestVO.setSaveType("insert");
        setSummarySaveRequestVO.setLoginUserId(creator);

        List<SetSummaryVO> setSummaryVOList = new ArrayList<>();
        for (AiArticleVO _vo :aiArticleVOList) {
            SetSummaryVO setSummaryVO = new SetSummaryVO();
            setSummaryVO.setSet_id((String) newSetId);
            setSummaryVO.setArticle_id(_vo.getArticle_id().toString());
            setSummaryVO.setSub_id(0L);
            setSummaryVO.setName(_vo.getName());
            setSummaryVO.setThumbnail(_vo.getThumbnail());
            setSummaryVO.setGradingMethod(59L);
            setSummaryVO.setCreator(creator);
            setSummaryVO.setUpdater(creator);
            setSummaryVOList.add(setSummaryVO);
        }

        setSummarySaveRequestVO.setSetSummary(setSummaryVOList);

        contentsService.saveSetSummary(setSummarySaveRequestVO);

        log.info("SetSummary Created.setId:{}", newSetId);

        return setSummarySaveRequestVO;
    }

    private Map<String, Object> createSetsForAiLearning(List<AiArticleVO> aiArticleVOList, Map<String, Object> oldSetsMap, String type, int brandId) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isSuccess", false);

        try {
            // 추가할 세트지 정보를 생성한다.
            if (oldSetsMap != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                SetsSaveVO newSetsVo = objectMapper.convertValue(oldSetsMap, SetsSaveVO.class);

                newSetsVo.clean();
                newSetsVo.setId(null);
                newSetsVo.setDescription("처방학습");
                newSetsVo.setIs_active(true);
                newSetsVo.setIs_publicOpen(true);
                newSetsVo.setPoints_type("auto");
                newSetsVo.setCreator_ty(4); // 4 공공존 수학 으로 고정

                // 20240411 추가 setCategory를 구해서 세팅함
                // 평가 아니면 과제라고 판단한다. 현재 ai처방학습은 평가/과제 밖에 없기 때문에. 나중에 늘어나면 switch 로 처리해야 함.
                newSetsVo.setSetCategory(findSetCategory("eval".equals(type)?"question-paper":"homework", brandId));

                // meta 정보 조회 및 세팅
                List<Map> oldMetaMapList =  aiLearningMapper.getMetaListBySetId((String)oldSetsMap.get("id"));

                //새로이 add할 메타맵정보 만들기
                List<MetaVO> newMetaList = new ArrayList<>();
                for (Map<String, Object> oldMetaMap: oldMetaMapList) {
                    MetaVO metaVO = objectMapper.convertValue(oldMetaMap, MetaVO.class);
                    newMetaList.add(metaVO);
                }
                newSetsVo.set_meta(newMetaList);
                newSetsVo.setArticles(makeArticleVoList(aiArticleVOList));

                SetsSaveRequestVO setsSaveRequestVO = new SetsSaveRequestVO();
                setsSaveRequestVO.setSaveType("insert");
                setsSaveRequestVO.setSets(newSetsVo);
                setsSaveRequestVO.setArticles(makeSetsArticleMapVoList(aiArticleVOList));
                // 세트지 생성
                String setsId = contentsService.saveSetInfo(setsSaveRequestVO);

                resultMap.put("isSuccess", true);
                resultMap.put("setsId", setsId); // 신규 생성된 세트지 id
                resultMap.put("errMsg", "");
            }
            else {
                resultMap.put("isSuccess", false);
                resultMap.put("setsId", null);
                resultMap.put("errMsg", "세트지 정보를 찾을 수 없습니다.");
            }
        }
        catch (Exception e) {
            throw e;
//            log.error(CustomLokiLog.errorLog(e));
//            resultMap.put("isSuccess", false);
//            resultMap.put("setsId", null);
//            resultMap.put("errMsg", "세트지 생성 에러");
        }

        return resultMap;
    }

    private List<SetsArticleMapVO> makeSetsArticleMapVoList(List<AiArticleVO> aiArticleVOList) throws Exception {
        if (aiArticleVOList.size() == 0) return null;

        List<SetsArticleMapVO> list = new ArrayList<>();

        for (AiArticleVO _vo: aiArticleVOList) {
            SetsArticleMapVO articleInfoVO = new SetsArticleMapVO();
            articleInfoVO.setId(0L);
            articleInfoVO.setArticle_id(_vo.getArticle_id());
            list.add(articleInfoVO);
        }
        return list;
    }


    public Map<String, Object> createAiLearningBatchTask(Map<String, Object> paramData) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchTask START-------------------------------");

        int setCount = 0; // 세트지 생성건수
        int idCount = 0; // 대상 평가id 건수
        int articleCount = 0; // 추출된 총 article 갯수
        int stntCnt = 0; // 대상 학생 수
        int successCnt = 0; // 성공하여 세트지 생성완료된 개수
        int failCnt = 0; // 실패하거나 오답이 없는등 대상 아티클이 없는 개수

        List<Map<String, Object>> taskList = this.findTargetTaskList(paramData);

        if (CollectionUtils.isNotEmpty(taskList)) {
                for(Map<String,Object> taskInfo : taskList) {
                    int taskId = MapUtils.getIntValue(taskInfo, "id");
                    int taskSetCount = 0; // 과제 당 생성된 세트지 건수

                    log.info("{}.taskId:{}", ++idCount, taskId);

                    Map<String, Object> oldSetsMap = getOldSetsMap("task", taskId); // 참고할 세트지
                    String creator = String.valueOf(oldSetsMap.get("creator")); // 세트지와 join 한 evl_info 에서 가져온 작성자id

                    Map<String, Object> innerParam = new HashMap<>();
                    innerParam.put("taskId", taskId);

                    // 추가조건 (wrter_id,cla_id,textbk_id) 추가
                    innerParam.putAll(taskInfo);
                    innerParam.remove("id");

                    // 출제할 아티클을 추출
                    Map<String, Object> articleMap = (Map<String, Object>) this.findAutoCreateAiLearningTask(innerParam);

                    // 출제할 학생별 아티클이 있음.
                    if (articleMap.get("stntArticleList") != null && !((List)articleMap.get("stntArticleList")).isEmpty()) {

                        // 20240411 setCategory 추가를 위해 brandId 를 미리 구하는 로직이 추가
                        int brandId = aiLearningMapper.findBrandIdByTaskId(taskId);

                        // task_info 생성
                        int newTaskInfoCnt = aiLearningMapper.insertTaskInfo(innerParam);

                        long  newId = AidtCommonUtil.getLongValueFromObject(innerParam.get("id")); // 생성된 evlId
                        log.info("newId:{}", newId);

                        List<AiArticleListByStntVO> stntArticleList = (List<AiArticleListByStntVO>) articleMap.get("stntArticleList");

                        // 학생별
                        for (AiArticleListByStntVO _stntArticleVo: stntArticleList) {

                            String mamoymId = _stntArticleVo.getMamoymId();
                            List<AiArticleVO> aiArticleVOList = _stntArticleVo.getArticleList();

                            // 학생이 가진 article 목록으로 세트지 생성
                            Map<String, Object> createResultMap = createSetsForAiLearning(aiArticleVOList, oldSetsMap, "task", brandId);

                            boolean isSuccess = (boolean) createResultMap.get("isSuccess");
                            String newSetId = (String)createResultMap.getOrDefault("setsId", "0") ;
                            String errMsg = String.valueOf(createResultMap.get("errMsg"));


                            log.info("isSucces:{}", isSuccess); // 생성시 오류가 없었다는 뜻. 실제 세트지가 생성된 것과는 무관
                            log.info("setsId:{}", newSetId); // 생성된 setId : null or 0 이 아니면 정상적으로 생성되었다는 뜻
                            log.info("errMsg:{}", errMsg);

                            // 세트지 생성에 성공했으면 count++
                            if (isSuccess && ObjectUtils.isNotEmpty(newSetId)) {
                                log.info("Sets Created:{}", newSetId);
                                setCount ++;
                                taskSetCount ++;

                                //세트지가 만들어 졌으므로, SetSummary, task_result_info, task_result_detail 정보를 생성한다.
                                log.info("SetSummary 생성-{}", taskId);
                                SetSummarySaveRequestVO setSummarySaveRequestVO = createSetSummary(newSetId, creator, aiArticleVOList);

                                log.info("task_result_info 생성-{}", taskId);
                                Long newTaskResultId = null;
                                newTaskResultId = createTaskResultInfo(newSetId, newId,  mamoymId, creator);
                                log.info("newTaskResultId:{}", newTaskResultId);

                                log.info("task_result_detail 생성-{}", taskId);
                                int detailCnt = createTaskResultDetail(newSetId, newTaskResultId, creator);
                                log.info("task_result_detail.cnt:{}", detailCnt);
                            }
                            else {
                                log.warn("Fail Sets Creation:{}", taskId);
                            }
                            stntCnt ++; // 세트지 성공여부에 관계없이 학생수 증가
                            articleCount =+ _stntArticleVo.getArticleList().size(); // 세트지 성공여부에 관계없이 아티클수 증가
                        }
                    }

                    // 세트지 생성건수로 성공/실패 판단 - 실패하면 X 표시하여 다음에 대상이 안되게 한다.
                    String prscrStdCrtAt = "X"; // 실패등의 이유로 생성이 안됐을때
                    if (taskSetCount > 0) {
                        prscrStdCrtAt = "Y"; // 뭔가 생성 됐을때. 모든 학생 및 set지를 다 판단 할 수는 없음.
                        successCnt ++;
                    } else {
                        failCnt ++;
                    }

                    int updateCnt = updateAfterCreateTaskInfo(taskId, prscrStdCrtAt, creator);
                }
        }
        long endTime = System.currentTimeMillis();
        log.debug("createAiLearningBatchTask END------------------------------- : {}", (endTime - startTime));

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("duration", endTime - startTime);
        returnMap.put("setCount", setCount);
        returnMap.put("idCount", idCount);
        returnMap.put("articleCount", articleCount);
        returnMap.put("stntCnt", stntCnt);
        returnMap.put("successCnt", successCnt);
        returnMap.put("failCnt", failCnt);

        return returnMap;

    }

    private int updateAfterCreateTaskInfo(int taskId, String prscrStdCrtAt, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();

        paramData.put("taskId", taskId);
        paramData.put("prscrStdCrtAt", prscrStdCrtAt);
        paramData.put("creator", creator);

        log.debug("{}-prscrStdCrtAt:{}", taskId, prscrStdCrtAt);

        return aiLearningMapper.updateAfterCreateTaskInfo(paramData);

    }

    private int createTaskResultDetail(String setsId, Long taskResultId, String creator) throws Exception {

        Map<String, Object> paramData = new HashMap<>();
        paramData.put("taskResultId", taskResultId);
        paramData.put("setsId", setsId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createTaskResultDetail(paramData);
        log.info("createTaskResultDetail.cnt:{}",cnt);

        return cnt;
    }

    private Long createTaskResultInfo(String newSetId, long taskId, String mamoymId, String creator) throws Exception {
        Map<String, Object> paramData = new HashMap<>();

        paramData.put("taskId", taskId);
        paramData.put("setsId", newSetId);
        paramData.put("mamoymId", mamoymId);
        paramData.put("creator", creator);

        int cnt = aiLearningMapper.createTaskResultInfo(paramData);

        return AidtCommonUtil.getLongValueFromObject(paramData.get("id"));
    }

    /**
     *
     * @param type
     * @param id evlId or taskId
     * @return oldSetsMap 기존 세트지 정보
     */
    private Map<String, Object> getOldSetsMap(String type, int id) throws Exception {
        Map<String, Object> oldSetsMap = null;
        if ("eval".equals(type)) {
            oldSetsMap = aiLearningMapper.getSetInfoByEvlId(id);
        }
        if ("task".equals(type)) {
            oldSetsMap = aiLearningMapper.getSetInfoByTaskId(id);
        }
        return oldSetsMap;
    }

    private List<SetsArticleInfoVO> makeArticleVoList(List<AiArticleVO> aiArticleVOList) {
        if (aiArticleVOList.isEmpty()) return Collections.emptyList();

        List<SetsArticleInfoVO> list = new ArrayList<>();

        for (AiArticleVO _vo: aiArticleVOList) {
            SetsArticleInfoVO articleInfoVO = new SetsArticleInfoVO();
            articleInfoVO.setId(0L);
            articleInfoVO.setArticle_id(_vo.getArticle_id());
            list.add(articleInfoVO);
        }
        return list;
    }


    /**
     * ai 맞춤 학습 과제/수업 만들기(공통문항)
     * @param paramData
     * @return
     * @throws Exception
     */
    public Map<String, Object> createAiCustomLeariningCommonPreview(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 난이도별 출제문항수에 따라 생성

        int eamExmNum = MapUtils.getInteger(paramData, "eamExmNum", 0);         // 총문항수
        int cntHigh = MapUtils.getInteger(paramData, "eamGdExmMun", 0);         // 상
        int cntMidHigh = MapUtils.getInteger(paramData, "eamAvUpExmMun", 0);    // 중상
        int cntMid = MapUtils.getInteger(paramData, "eamAvExmMun", 0);          // 중
        int cntMidLow = MapUtils.getInteger(paramData, "eamAvLwExmMun", 0);     // 중하
        int cntLow = MapUtils.getInteger(paramData, "eamBdExmMun", 0);          // 하

        // 출제문항수 검증
        if (eamExmNum != (cntHigh + cntMidHigh + cntMid + cntMidLow + cntLow)) {
            throw new AidtException("각 난이도별 출제문항수 합이 총문항수와 다릅니다.");
        }

        if (eamExmNum == 0) {
            throw new AidtException("출제문항수가 0건 입니다.");
        }

        // 난이도별 요청한 문제수
        LinkedHashMap<String, Integer> requestCountMap = new LinkedHashMap<>();
        requestCountMap.put("MD05", cntLow);
        requestCountMap.put("MD04", cntMidLow);
        requestCountMap.put("MD03", cntMid);
        requestCountMap.put("MD02", cntMidHigh);
        requestCountMap.put("MD01", cntHigh);

        // 생성한 문제수
        LinkedHashMap<String, Integer> resopnseCountMap = new LinkedHashMap<>();

        // 파라미터 복제
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        List<AiArticleVO> totalArticleList = new ArrayList<>();

        // 난이도별로 맞춤 아티클 추출
        for (Map.Entry<String,Integer> entry : requestCountMap.entrySet()) {
            String diffCode = entry.getKey();
            Integer cnt = entry.getValue();

            List<AiArticleVO> articleList = new ArrayList<>();
            innerParam.put("difficulty", diffCode);
            innerParam.put("cnt", cnt);

            if (cnt > 0) {
                articleList = selectAiCustomLearningArticles(innerParam);

                if (CollectionUtils.isNotEmpty(articleList)) {
                    totalArticleList.addAll(articleList);
                }
            }

            resopnseCountMap.put(diffCode, CollectionUtils.size(articleList));
        }

        int resultCnt = totalArticleList.size();

        if (eamExmNum > resultCnt) {
            StringBuilder sb = new StringBuilder();
            int md02Cnt = MapUtils.getInteger(requestCountMap, "MD02", 0) - MapUtils.getInteger(resopnseCountMap, "MD02", 0);
            int md03Cnt = MapUtils.getInteger(requestCountMap, "MD03", 0) - MapUtils.getInteger(resopnseCountMap, "MD03", 0);
            int md04Cnt = MapUtils.getInteger(requestCountMap, "MD04", 0) - MapUtils.getInteger(resopnseCountMap, "MD04", 0);
            int md05Cnt = MapUtils.getInteger(requestCountMap, "MD05", 0) - MapUtils.getInteger(resopnseCountMap, "MD05", 0);
            if (md02Cnt > 0) {
                sb.append("중상:").append(md02Cnt).append(",");
            }
            if (md03Cnt > 0) {
                sb.append("중:").append(md03Cnt).append(",");
            }
            if (md04Cnt > 0) {
                sb.append("중하:").append(md04Cnt).append(",");
            }
            if (md05Cnt > 0) {
                sb.append("하:").append(md05Cnt).append(",");
            }

            String cntString = sb.toString();
            if (cntString.endsWith(",")) {
                cntString = cntString.substring(0, cntString.length() - 1);
            }

            log.warn(cntString);

            resultMap.put(RESULT_OK, false);
            resultMap.put(RESULT_MSG, "입력하신 문항 수가 출제 가능한 범위를 초과하였습니다.<br> 다시 한 번 문항 수를 확인해 주세요.");
            resultMap.put("setsId", null);

            log.warn("출제수 부족 오류-요청:{}, 출제:{}", eamExmNum, resultCnt);

        } else {
            // 미리보기 정보 세팅
            Map<String, Object> previewMap = getPreviewInfo(totalArticleList);

            resultMap.put(RESULT_OK, true);
            resultMap.put(RESULT_MSG, "성공");
            resultMap.put("previewInfo", previewMap.get("previewInfo"));
            resultMap.put("articleList", previewMap.get("articleList"));
//            resultMap.put("totalArticleList", totalArticleList);

        }
        return resultMap;
    }


    /**
     * ai 맞춤 학습 과제/수업 만들기(공통문항)
     * @param paramData
     * @return
     * @throws Exception
     */
    public Map<String, Object> createAiCustomLeariningCommon(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);


        List<AiArticleVO> totalArticleList = aiLearningMapper.selectAiCustomLearningArticlesInfo(innerParam);

        int resultCnt = CollectionUtils.size(totalArticleList);

        // 세트지 생성
        String setsId = "";
        // 생성된 taskId
        Long taskId = null;
        // 생성된 stdDtaId
        int dtaId = 0;

        if (resultCnt > 0) {
            String name = "[AI 맞춤 학습] " + totalArticleList.get(0).getName();
            int metaId = totalArticleList.get(0).getMeta_id();
            String writerId = String.valueOf(innerParam.get("wrterId"));

            innerParam.put("setNm", name);
            innerParam.put("brandId", aiLearningMapper.findBrandId(innerParam)); // textbkId 를 이용해 brand id를 찾아옴

            setsId = createSetsForAiCustomLearning(totalArticleList, innerParam);
            log.info("created setsId={}",setsId);
            if (ObjectUtils.isNotEmpty(setsId)) {

                log.info("SetSummary 생성");
                createSetSummary(setsId, writerId, totalArticleList);
                innerParam.put("setsId", setsId);
                innerParam.put("eamScp", metaId);

                int tabUpdCnt = 0;
                // 과제, 수업 만들기
                switch (String.valueOf(paramData.get("lrnMethod"))) {
                    case "1":
                        // 수업중 풀기

                        innerParam.put("stdDatNm", name);

                        // tab 정보 수정 (ai_cstmzd_std_crt_at='Y', sets_id 세팅)
                        tabUpdCnt = aiLearningMapper.updateTabInfoForAiCustomLearning(innerParam);

                        if (tabUpdCnt == 0 ) {
                            resultMap.put(RESULT_OK, false);
                            resultMap.put(RESULT_MSG, "tab_id에 해당하는 Tab 정보가 없습니다.");

                            log.warn("출제 중복 오류-요청 tabId:{}", paramData.get("tabId"));
                            return resultMap;
                        }


                        /* Main 서버 DuplicateKeyException 발생
                         * std_dta_result_info 테이블의 ix_std_dta_result_info_02 제약조건 위반
                         * (이미 해당 학생, 탭의 데이터가 존재함)
                        */
                        try {
                            // std_dta_info 생성
                            dtaId = createStdDtaInfoForAiCustomLearning(innerParam);
                            innerParam.put("dtaId", dtaId);

                            // std_dta_result_info 생성
                            int sdriCnt = createStdDtaResultInfoForAiCustomLearning(innerParam);

                            // std_dta_result_detail 생성
                            int sdrdCnt = createStdDtaResultDetailForAiCustomLearning(innerParam);

                        } catch (DuplicateKeyException e) {
                            // 이미 존재하는 탭 호출 시 예외처리
                            resultMap.put(RESULT_OK, false);
                            resultMap.put(RESULT_MSG, "이미 출제된 tab_id 입니다.");

                            log.error("{} 탭의 정보가 이미 존재합니다.", innerParam.get("tabId"));

                            return resultMap;
                        }

                        // 설정정보 생성
                        innerParam.put("ai_cstmzd_std_mthd_se_cd", 1); // 수업중풀기 1, 과제 2
                        innerParam.put("eamScp", getEamScp(innerParam)); // 출제범위 세팅
                        int configUpdCnt =  aiLearningMapper.createConfigAiCustomLearning(innerParam);
                        log.info("configUpdCnt:{}", configUpdCnt);

                        resultMap.put("dtaId", dtaId);
//                        resultMap.put("tabId", tabId);

                        break;
                    case "2":
                        // 과제로 내기

                        // tab 정보 수정 (ai_cstmzd_std_crt_at='Y')
                        tabUpdCnt = aiLearningMapper.updateTabInfoForAiCustomLearning(innerParam);

                        if (tabUpdCnt == 0 ) {
                            resultMap.put(RESULT_OK, false);
                            resultMap.put(RESULT_MSG, "tab_id 에 해당하는 Tab 정보가 없습니다.");

                            log.warn("출제 중복 오류-요청 tabId:{}", paramData.get("tabId"));
                            return resultMap;
                        }

                        // 탭 이름 가져오기
                        String tabNm = aiLearningMapper.findTabNmForAiCustomLearning(innerParam);
                        name = "[" + tabNm + "] " + totalArticleList.get(0).getName();

                        innerParam.put("taskNm", name);
                        // task_info 생성
                        taskId = createTaskInfoForAiCustomLearning(innerParam);
                        innerParam.put("taskId", taskId);

                        // task_result_info 생성
                        int triCnt = createTaskResultInfoForAiCustomLearning(innerParam, totalArticleList);
                        log.info("created task_result_info cnt:{}", triCnt);

                        // task_result_detail 생성
                        int trdCnt = createTaskResultDetailForAiCustomLearning(innerParam);
                        log.info("created task_result_detail cnt:{}", trdCnt);

                        // 설정정보 생성
                        innerParam.put("ai_cstmzd_std_mthd_se_cd", 2); // 수업중풀기 1, 과제 2
                        innerParam.put("eamScp", getEamScp(innerParam)); // 출제범위 세팅
                        int configUpdCnt2 =  aiLearningMapper.createConfigAiCustomLearning(innerParam);
                        log.info("configUpdCnt:{}", configUpdCnt2);

                        resultMap.put("taskId", taskId);

                        // 과제 등록 시 MQ 발송
                        assignmentGaveService.insertBulkTaskMqTrnLog(resultMap);
                        break;
                    default:
                        log.warn("lrnMethod is not define!!!-{}", paramData.get("lrnMethod"));
                }
            } else {
                log.error("Sets not created.");
            }
        }




//        resultMap.put(RESULT_MSG, msg);
        resultMap.put("setsId", setsId);

//        aiLearningMapper.selectAiCustomLearningArticles(null); // 강제오류 발생 (rollback 테스트)


        return resultMap;
    }

    private Object getEamScp(Map<String, Object> innerParam) throws Exception {
        return aiLearningMapper.getEamScp(innerParam);
    }

    /**
     * ai 맞춤용 std_dta_result_detail 생성
     * @param innerParam
     * @return 생성된 개수
     */
    private int createStdDtaResultDetailForAiCustomLearning(Map<String, Object> innerParam) throws Exception {

        int cnt = aiLearningMapper.createStdDtaResultDetailForAiCustomLearning(innerParam);

        log.info("created std_dta_result_detail Cnt:{}", cnt);

        return cnt;

    }

    /**
     * ai 맞춤용 std_dta_result_info 생성
     * @param innerParam
     * @return 생성된 개수
     */
    private int createStdDtaResultInfoForAiCustomLearning(Map<String, Object> innerParam) throws Exception {

        int cnt = aiLearningMapper.createStdDtaResultInfoForAiCustomLearning(innerParam);

        log.info("created std_dta_result_info Cnt:{}", cnt);

        return cnt;
    }

    /**
     * ai 맞춤용 tab_info 생성 -> 탭 생성하지 않고, 받아오늘 방식으로 변경함에 따라 사용하지 않음. (참고용으로 남겨둠)
     * @param param
     * @return 생성된 tab_id
     */
    @Deprecated
    private int createTabInfoForAiCustomLearning(Map<String, Object> param) throws Exception {

        log.info("createTabInfoForAiCustomLearning START====");

        TabInfoVO tabInfoVO = TabInfoVO.builder()
                .wrter_id((String)param.get("wrterId"))
                .cla_id((String)param.get("claId"))
                .textbk_id(MapUtils.getInteger(param, "textbkId"))
                .crcul_id(MapUtils.getInteger(param, "crculId"))
                .tab_nm((String)param.get("stdDatNm"))
                .sets_id(MapUtils.getString(param, "setsId"))
                .expos_at("Y")
                .tab_add_at("Y")
                .rgtr(String.valueOf(param.get("wrterId")))
                .mdfr(String.valueOf(param.get("wrterId")))
                .build();

        int cnt = aiLearningMapper.createTabInfoForAiCustomLearning(tabInfoVO);

        int tabId = Integer.parseInt(String.valueOf(tabInfoVO.getId()));

        log.info("created tabId:{}" , tabId);

        return tabId;
    }

    /**
     * ai 맞춤용 std_dta_info 생성
     * @param param
     * @return
     */
    private int createStdDtaInfoForAiCustomLearning(Map<String, Object> param) throws Exception {

        StdDtaInfoVO stdDtaInfoVO = StdDtaInfoVO.builder()
                .wrter_id((String)param.get("wrterId"))
                .cla_id((String)param.get("claId"))
                .textbk_id(MapUtils.getInteger(param, "textbkId"))
                .crcul_id(MapUtils.getInteger(param, "crculId"))
                .std_dat_nm((String)param.get("stdDatNm"))
                .eam_mth(5) // ai 맞춤학습
                .eam_trget(MapUtils.getInteger(param, "eamTrget"))
                .eam_scp(MapUtils.getString(param, "eamScp"))
                .textbk_tab_id(MapUtils.getInteger(param, "tabId"))
                .textbk_tab_nm((String)param.get("stdDatNm"))
                .rgtr(MapUtils.getString(param, "wrterId", ""))
                .mdfr(MapUtils.getString(param, "wrterId", ""))
                .build();

        if (MapUtils.getInteger(param, "eamTrget", 0)  == 1) { // 공통문항 일때만 세팅
            stdDtaInfoVO.setSets_id(MapUtils.getString(param, "setsId") );
            stdDtaInfoVO.setEam_exm_num(MapUtils.getInteger(param, "eamExmNum", 0));
            stdDtaInfoVO.setEam_gd_exm_mun(MapUtils.getInteger(param, "eamGdExmMun", 0));
            stdDtaInfoVO.setEam_av_up_exm_mun(MapUtils.getInteger(param, "eamAvUpExmMun", 0));
            stdDtaInfoVO.setEam_av_exm_mun(MapUtils.getInteger(param, "eamAvExmMun", 0));
            stdDtaInfoVO.setEam_av_lw_exm_mun(MapUtils.getInteger(param, "eamAvLwExmMun", 0));
            stdDtaInfoVO.setEam_bd_exm_mun(MapUtils.getInteger(param, "eamBdExmMun", 0));
        } else {
            stdDtaInfoVO.setEam_exm_num(0);
        }


        int cnt = aiLearningMapper.createStdDtaInfoForAiCustomLearning(stdDtaInfoVO);

        return stdDtaInfoVO.getId();
    }



    /**
     * ai 맞춤용 task_result_info 생성
     * @param param
     * @return 생성된 task_result_info 개수
     */
    private int createTaskResultInfoForAiCustomLearning(Map<String, Object> param, List<AiArticleVO> articleList) throws Exception {

        int cnt = aiLearningMapper.createTaskResultInfoForAiCustomLearning(param);
        log.info("created task_result_info count:{}", cnt);

        return cnt;
    }

    /**
     * ai 맞춤용 task_result_detail 생성
     * @param param
     * @return
     */
    private int createTaskResultDetailForAiCustomLearning(Map<String, Object> param) throws Exception {

        int cnt = aiLearningMapper.createTaskResultDetailForAiCustomLearning(param);

        log.info("created task_result_detail count:{}", cnt);

        return cnt;
    }

    /**
     * ai 맞춤용 task_info 생성
     * @param param
     * @return taskId
     */
    private Long createTaskInfoForAiCustomLearning(Map<String, Object> param) throws Exception {
        String stDt = String.valueOf(param.getOrDefault("pdEvlStDt", ""));
        String edDt = String.valueOf(param.getOrDefault("pdEvlEdDt", ""));

        // 원래 파라미터에서 값 가져오기
        String origStDt = String.valueOf(param.getOrDefault("pdEvlStDt", ""));
        String origEdDt = String.valueOf(param.getOrDefault("pdEvlEdDt", ""));

        // 새로운 변수에 원하는 포맷으로 변환
        String formattedStDt = origStDt + ":00";
        String formattedEdDt = origEdDt + ":00";

        stDt = stDt.substring(0, stDt.lastIndexOf(" "));
        edDt = edDt.substring(0, edDt.lastIndexOf(" "));

        int taskSttsCd = 1; // 1 예정, 2 진행중

        Date pdEvlStDt = null;
        Date pdEvlEdDt = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(AidtConst.FORMAT_STRING_YMDHMS);
            pdEvlStDt = sdf.parse(formattedStDt);
            pdEvlEdDt = sdf.parse(formattedEdDt);

            if (pdEvlStDt.before(new Date())) {
                taskSttsCd = 2; // 시작일시가 지났으면 진행중
            }

        } catch (ParseException e) {
            log.error(CustomLokiLog.errorLog(e));
            //TODO Controller 에서 이미 validate 했음.
            log.error("err:", e);
        }

        TaskInfoVO taskInfoVO = TaskInfoVO.builder()
                .wrter_id(MapUtils.getString(param,"wrterId", "") )
                .cla_id(MapUtils.getString(param,"claId") )
                .textbk_id(MapUtils.getInteger(param, "textbkId") )
                .task_nm(MapUtils.getString(param,"taskNm") )
                .eam_mth(5) // AI 맞춤학습
                .eam_trget(MapUtils.getInteger(param, "eamTrget") )
                .task_stts_cd(taskSttsCd)
                .task_prg_dt(pdEvlStDt)
                .task_cp_dt(pdEvlEdDt)
                .pd_evl_st_dt(origStDt)
                .pd_evl_ed_dt(origEdDt)
                .eam_exm_num(MapUtils.getInteger(param, "eamExmNum", 0))
                .eam_gd_exm_mun(MapUtils.getInteger(param, "eamGdExmMun", 0))
                .eam_av_up_exm_mun(MapUtils.getInteger(param, "eamAvUpExmMun", 0))
                .eam_av_exm_mun(MapUtils.getInteger(param, "eamAvExmMun", 0))
                .eam_av_lw_exm_mun(MapUtils.getInteger(param, "eamAvLwExmMun", 0))
                .eam_bd_exm_mun(MapUtils.getInteger(param, "eamBdExmMun", 0))
                .rgtr(MapUtils.getString(param,"wrterId", "") )
                .mdfr(MapUtils.getString(param,"wrterId", "") )
                .ai_tut_set_at(MapUtils.getString(param,"aiTutSetAt", "N"))
                .build();

        if (MapUtils.getInteger(param, "eamTrget") == 1) { // 공통문항 일때만 setsId 세팅
            taskInfoVO.setSets_id(MapUtils.getString(param, "setsId") );
        }

        int cnt = aiLearningMapper.createTaskInfoForAiCustomLearning(taskInfoVO);

        Long taskId = taskInfoVO.getId();

        log.info("created taskId:{}", taskId);

        return taskId;
    }

    /**
     * AI 맞춤학습용 세트지 생성
     * @param totalArticleList
     * @param innerParam
     * @return 세트지id
     */
    private String createSetsForAiCustomLearning(List<AiArticleVO> totalArticleList, Map<String, Object> innerParam) throws Exception {

        SetsSaveVO setsVo = new SetsSaveVO();
        setsVo.setDescription(String.valueOf(innerParam.get("setNm")));
        setsVo.setIs_active(true);
        setsVo.setIs_publicOpen(true);
        setsVo.setPoints_type("auto");
        setsVo.setName(String.valueOf(innerParam.get("setNm")));
        setsVo.setCreator_ty(4); // 4 공공존수학 값으로 고정

        // brand id 세팅
        Object brandIdObj = innerParam.get("brandId");
        setsVo.setBrand_id( (brandIdObj instanceof Number) ? ((Number) brandIdObj).longValue() : 1L );

        setsVo.setCreator(String.valueOf(innerParam.get("wrterId")));
        setsVo.setSetCategory(findSetCategory(innerParam));

        List<SetsArticleInfoVO> setsArticleInfoVOList =  makeArticleVoList(totalArticleList);

        setsVo.setArticles(setsArticleInfoVOList);
        SetsSaveRequestVO setsSaveRequestVO = new SetsSaveRequestVO();
        setsSaveRequestVO.setSaveType("insert");
        setsSaveRequestVO.setSets(setsVo);
        setsSaveRequestVO.setArticles(makeSetsArticleMapVoList(totalArticleList));
        // 세트지 생성
        return contentsService.saveSetInfo(setsSaveRequestVO);
    }

    /**
     * setCatrgory 값을 찾아온다.
     * @param innerParam
     * @return
     */
    private Long findSetCategory(Map<String, Object> innerParam) throws Exception {

        switch (String.valueOf(innerParam.get("lrnMethod"))) {
            case "1":
                innerParam.put("code", "textbook");
                break;
            case "2":
                innerParam.put("code", "homework");
                break;
            default:
                log.error("lrnMethod is missing");
        }

        Integer setCategory = aiLearningMapper.findSetCategory(innerParam);

        if (setCategory == null) {
            throw new AidtException("setCategory 를 찾을 수 없습니다.");
        }
        return setCategory.longValue();

    }

    private Long findSetCategory(String code, int brandId ) throws Exception {

        Map<String, Object> innerParam = new HashMap<>();
        innerParam.put("code", code);
        innerParam.put("brandId", brandId);

        Integer setCategory = aiLearningMapper.findSetCategory(innerParam);

        if (setCategory == null) {
            throw new AidtException("setCategory 를 찾을 수 없습니다.");
        }
        return setCategory.longValue();
    }

    /**
     * ai 맞춤 학습용 맞춤 article 목록 조회
     * @param paramData
     * @return 맞춤 article 목록
     */
    private List<AiArticleVO> selectAiCustomLearningArticles(Map<String, Object> paramData) throws Exception {

        return aiLearningMapper.selectAiCustomLearningArticles(paramData);
    }

    public Object findAiCustomLeariningPersonalCountCheck(Map<String, Object> paramData) throws Exception {

        int cnt = aiLearningMapper.findAiCustomLeariningPersonalCountCheck(paramData);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(RESULT_OK, true);
        resultMap.put(RESULT_MSG, (cnt > 0) ? "성공" : "학생들의 학습 결과가 없습니다.<br> 수업을 더 진행하시거나, 모두 같은 문제를 선택하여 출제해 주세요.");

        resultMap.put("stdInfoExistYn", cnt>0? "Y":"N");

        return resultMap;
    }

    /**
     * ai맞춤학습 - 개별문항 출제
     * @param paramData
     * @return
     * @throws Exception
     */
    public Map<String, Object> createAiCustomLeariningPersonalPreview(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        if (ObjectUtils.isEmpty(paramData.get("stntIdList"))) {
            resultMap.put(RESULT_OK, false);
            resultMap.put(RESULT_MSG, "선택한 학생이 없습니다.");

            return resultMap;
        }

        // 학생 리스트 및 난이도를 화면에서 받아올 예정
        List<Map<String, Object>> stntLevelList = objectMapper.convertValue(paramData.get("stntIdList"), new TypeReference<>() {});

        if (stntLevelList.isEmpty()) {
            resultMap.put(RESULT_OK, false);
            resultMap.put(RESULT_MSG, "문항 출제할 대상 학생이 없습니다.");

            return resultMap;
        }

        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        // 문항출제를 위한 정보vo(article) 목록을 만든다.
        List<AiCustomLearningStntVO> aiCustomLearningStntVOList = makeAiCustomLearningPersonVo(innerParam, stntLevelList);

        // 출제가 안된 학생이 있는지 검증 - 문항수 부족으로 출제 안된 학생이 있으면 모두 출제 안함.
        List<String> emptyStudents = findEmptyStudents(stntLevelList, aiCustomLearningStntVOList);

        log.info("emptyStudents.size:{}", emptyStudents.size());

        if (!emptyStudents.isEmpty()) { // 한명이라도 출제가 안되면 모두 롤백
            resultMap.put(RESULT_OK, false);
            resultMap.put(RESULT_MSG, "출제할 문항이 부족합니다.");

            return resultMap;
        }


        // return article 정보 정리
        List<Map<String, Object>> stntArticleList = new ArrayList<>();
        List<Map<String, Object>> stntPreviewInfoList = new ArrayList<>();
        for (AiCustomLearningStntVO _vo : aiCustomLearningStntVOList) {
            List<AiArticleVO> totalArticleList = _vo.getCreateArticles();

            // 미리보기 정보 세팅
            Map<String, Object> previewMap = getPreviewInfo(totalArticleList);

            stntPreviewInfoList.add(Map.of(_vo.getStntId(), previewMap.get("previewInfo")));
            stntArticleList.add(Map.of(_vo.getStntId(), previewMap.get("articleList")));
        }

        resultMap.put("previewInfo", stntPreviewInfoList);
        resultMap.put("articleList", stntArticleList);
        resultMap.put(RESULT_OK, true);
        resultMap.put(RESULT_MSG, "성공");

        return resultMap;
    }

    /**
     * ai맞춤학습 - 개별문항 출제
     * @param paramData
     * @return
     * @throws Exception
     */
    public Map<String, Object> createAiCustomLeariningPersonal(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        List<AiCustomLearningStntVO> aiCustomLearningStntVOList = new ArrayList<>();

        List<Map<String, Object>> stntInfoList =  objectMapper.convertValue(innerParam.get("stntInfoList"), new TypeReference<>() {});

        for (Map<String, Object> stntInfo : stntInfoList) {
            AiCustomLearningStntVO aiCustomLearningStntVO = new AiCustomLearningStntVO();

            List<String> articleList = objectMapper.convertValue(stntInfo.get("articleList"), new TypeReference<>() {});

            List<AiArticleVO> aiArticleVOSet = aiLearningMapper.getSimilarArticlesInfo(articleList);

            if (CollectionUtils.isNotEmpty(aiArticleVOSet)) {
                aiCustomLearningStntVO.setStntId(MapUtils.getString(stntInfo, "mamoymId", ""));
                aiCustomLearningStntVO.setLevel(MapUtils.getString(stntInfo, "lev", ""));
                aiCustomLearningStntVO.setSimilarArticles(aiArticleVOSet);

                aiCustomLearningStntVOList.add(aiCustomLearningStntVO);
            }
        }

        String lrnMethod = MapUtils.getString(innerParam, "lrnMethod");

        // brand_id 세팅
        innerParam.put("brandId", aiLearningMapper.findBrandId(innerParam)); // textbkId 를 이용해 brand id를 찾아옴

        String currNm = aiLearningMapper.getStudyMapNm(innerParam);

        String name = "[AI 맞춤 학습] " + currNm;

        switch (lrnMethod) {
            case "1": // 수업중 풀기 생성
                innerParam.put("name", name);
                createAiCustomLeariningPersonalDta(resultMap, innerParam, aiCustomLearningStntVOList);
                break;
            case "2": // 과제 생성
                String tabNm = aiLearningMapper.findTabNmForAiCustomLearning(innerParam);

                name = "[" + tabNm + "] " + currNm;

                innerParam.put("name", name);

                createAiCustomLeariningPersonalTask(resultMap, innerParam, aiCustomLearningStntVOList);

                // 과제 등록 시 MQ 발송
                assignmentGaveService.insertBulkTaskMqTrnLog(resultMap);
                break;
            default:
                log.error("no lrnMethod");
                throw new AidtException("[오류] 수업자료/과제 구분이 없습니다.");
        }

//        addStntResultInfo(resultMap, innerParam);

//        resultMap.put("stntArticleList", aiCustomLearningStntVOList); //TODO 결과 확인 테스트용(데이터 많음)
        return resultMap;
    }

    /**
     * resultMap 에 수준별 학생 정보 세팅
     * @param resultMap
     * @param paramData
     */
    private void addStntResultInfo(Map<String, Object> resultMap, Map<String, Object> paramData) throws Exception {

        List<Map> resultStntList = aiLearningMapper.findCustomEamResultList(paramData);
        List<Map> stntGdList = new ArrayList<>();
        List<Map> stntAvList = new ArrayList<>();
        List<Map> stntLwList = new ArrayList<>();
        int stntCnt = 0;
        for (Map _map : resultStntList) {
            Map stntMap = new HashMap();
            stntCnt ++;
            switch (MapUtils.getInteger(_map, "lvl")) {
                case 1:
                    _map.remove("lvl");
                    stntGdList.add(_map);
                    break;
                case 2:
                    _map.remove("lvl");
                    stntAvList.add(_map);
                    break;
                case 3:
                    _map.remove("lvl");
                    stntLwList.add(_map);
                    break;
                default:
                    //no action
            }
        }

        resultMap.put("stntCnt", stntCnt);
        resultMap.put("stntGdList", stntGdList);
        resultMap.put("stntAvList", stntAvList);
        resultMap.put("stntLwList", stntLwList);
    }

    /**
     * ai맞춤학습 개별문항 추출을 기반으로 과제 데이터를 create 한다.
     * @param resultMap
     * @param innerParam
     * @param aiCustomLearningStntVOList
     */
    private void createAiCustomLeariningPersonalTask(Map<String, Object> resultMap
            , Map<String, Object> innerParam
            , List<AiCustomLearningStntVO> aiCustomLearningStntVOList) throws Exception {

        if (aiCustomLearningStntVOList.size() == 0 ) {
            throw new AidtException("생성할 학생 정보 및 문항 정보가 없습니다.");
        }

        // 1개씩 생성-------------------------------------------------------------------------

        // tab 정보 수정
        int tabUpdCnt = aiLearningMapper.updateTabInfoForAiCustomLearning(innerParam);
        if (tabUpdCnt == 0 ) {
            throw new AidtException("tab_id 에 해당하는 Tab 정보가 없습니다.");
        }

        // task_info 생성
        innerParam.put("taskNm", innerParam.get("name"));
        Long taskId = createTaskInfoForAiCustomLearning(innerParam);
        log.info("taskId:{}", taskId);

        if (taskId <= 0) {
            throw new AidtException("과제정보 생성시 오류가 발생했습니다.");
        }
        innerParam.put("taskId", taskId);

        // 설정정보 생성
        innerParam.put("ai_cstmzd_std_mthd_se_cd", 2); // 수업중풀기 1, 과제 2
        innerParam.put("eamScp", getEamScp(innerParam)); // 출제범위 세팅
        int configId = createConfigAiCustomLearning(innerParam);

        log.info("configId:{}", configId);

        // 설정 상세 정보 생성
        innerParam.put("configId", configId);
        int confingCnt = createConfigAiCustomResult(innerParam, aiCustomLearningStntVOList);

        // 1개씩 생성 END-------------------------------------------------------------------------


        // 학생수 만큼 생성-------------------------------------------------------------------------
        for (AiCustomLearningStntVO _vo : aiCustomLearningStntVOList) {
            List<AiArticleVO> totalArticleList = _vo.getCreateArticles();
            if (totalArticleList.isEmpty()) continue;

            // 세트지 생성
            innerParam.put("setNm", innerParam.get("name"));
            String setsId = createSetsForAiCustomLearning(totalArticleList, innerParam);

            if (ObjectUtils.isNotEmpty(setsId)) {
                innerParam.put("setsId", setsId);

                // setSummary 생성
                createSetSummary(String.valueOf(setsId), String.valueOf(innerParam.get("wrterId")), totalArticleList);

                // task_result_info 생성
                innerParam.put("stntId", _vo.getStntId());
                int resultId = createTaskResultInfoForAiCustomLearningPersonal(innerParam);
                log.info("resultId:{}", resultId);

                if (resultId <= 0) {
                    throw new AidtException("task_result_info 생성에 실패 했습니다.");
                }
                innerParam.put("taskResultId", resultId);

                // task_result_detail 생성
                innerParam.put("creator", innerParam.get("wrterId"));
                int detailCnt = aiLearningMapper.createTaskResultDetail(innerParam);
                log.info("detailCnt:{}", detailCnt);

            } else {
                throw new AidtException("세트지 생성에 실패 했습니다.");
            }
        }
        // 학생수 만큼 생성 END-------------------------------------------------------------------------

        // response 세팅
        resultMap.put(RESULT_OK, true);
        resultMap.put("taskId", taskId);
//        resultMap.put("pdEvlStDt", innerParam.get("pdEvlStDt"));
//        resultMap.put("pdEvlEdDt", innerParam.get("pdEvlEdDt"));
//        resultMap.put("aiTutSetAt", innerParam.get("aiTutSetAt"));
//        resultMap.put("eamTrget", innerParam.get("eamTrget"));
    }

    private int createConfigAiCustomResult(Map<String, Object> innerParam, List<AiCustomLearningStntVO> aiCustomLearningStntVOList) throws Exception {

        int totalCnt = 0;
        for (AiCustomLearningStntVO vo : aiCustomLearningStntVOList) {
            innerParam.put("stdt_id", vo.getStntId());
            innerParam.put("gd_av_bd_group_cd", vo.convertLevelToInt()); // 상중하 레벨정보
            int _cnt = aiLearningMapper.createConfigAiCustomResult(innerParam);
            totalCnt += _cnt;
        }

        return totalCnt;

    }

    private int createConfigAiCustomLearning(Map<String, Object> innerParam) throws Exception {

        innerParam.put("eamExmNum", MapUtils.getIntValue(innerParam, "eamExmNum", 0)); // db 컬럼 속성이 not null 이라서.. 세팅해야 함.
        innerParam.put("eamGdExmMun", MapUtils.getIntValue(innerParam, "eamGdExmMun", 0));
        innerParam.put("eamAvUpExmMun", MapUtils.getIntValue(innerParam, "eamAvUpExmMun", 0));
        innerParam.put("eamAvExmMun", MapUtils.getIntValue(innerParam, "eamAvExmMun", 0));
        innerParam.put("eamAvLwExmMun", MapUtils.getIntValue(innerParam, "eamAvLwExmMun", 0));
        innerParam.put("eamBdExmMun", MapUtils.getIntValue(innerParam, "eamBdExmMun", 0));
        innerParam.put("aiTutSetAt", MapUtils.getString(innerParam, "aiTutSetAt", "N"));
        int cnt =  aiLearningMapper.createConfigAiCustomLearning(innerParam);

        return MapUtils.getInteger(innerParam, "id", 0);

    }

    private int createTaskResultInfoForAiCustomLearningPersonal(Map<String, Object> innerParam) throws Exception {

        TaskResultInfoVO resultVo = TaskResultInfoVO.builder()
                .task_id(MapUtils.getInteger(innerParam, "taskId"))
                .mamoym_id(MapUtils.getString(innerParam, "stntId"))
                .sets_id(MapUtils.getString(innerParam, "setsId"))
                .eak_stts_cd(1)
                .mrk_cp_at("N")
                .rgtr(MapUtils.getString(innerParam, "wrterId", ""))
                .mdfr(MapUtils.getString(innerParam, "wrterId", ""))
                .build();

        int cnt = aiLearningMapper.createTaskResultInfoForAiCustomLearningPersonal(resultVo);

        return resultVo.getId();
    }

    /**
     * ai맞춤학습 개별문항 추출을 기반으로 수업중 풀기 데이터를 create 한다.
     * @param resultMap
     * @param innerParam
     * @param aiCustomLearningStntVOList
     */
    private void createAiCustomLeariningPersonalDta(Map<String, Object> resultMap
            , Map<String, Object> innerParam
            , List<AiCustomLearningStntVO> aiCustomLearningStntVOList) throws Exception {

        if (aiCustomLearningStntVOList.size() == 0 ) {
            throw new AidtException("생성할 학생 정보 및 문항 정보가 없습니다.");
        }

        // 1개씩 생성-------------------------------------------------------------------------

        // tab 정보 수정
        int tabUpdCnt = aiLearningMapper.updateTabInfoForAiCustomLearning(innerParam);
        if (tabUpdCnt == 0 ) {
            throw new AidtException("tab_id 에 해당하는 Tab 정보가 없습니다.");
        }

        // std_dta_info 생성
        innerParam.put("stdDatNm", innerParam.get("name"));

        // 출제범위 세팅
        innerParam.put("eamScp", getEamScp(innerParam));

        int dtaId = createStdDtaInfoForAiCustomLearning(innerParam);
        innerParam.put("dtaId", dtaId);

        if (dtaId <= 0) {
            throw new AidtException("수업중 풀기 생성시 오류가 발생했습니다.");
        }
        innerParam.put("dtaId", dtaId);

        // 설정정보 생성
        innerParam.put("ai_cstmzd_std_mthd_se_cd", 1); // 수업중풀기 1, 과제 2
        int configId =  createConfigAiCustomLearning(innerParam);
        log.info("configId:{}", configId);

        // 설정 상세 정보 생성
        innerParam.put("configId", configId);
        int confingCnt = createConfigAiCustomResult(innerParam, aiCustomLearningStntVOList);

        // 1개씩 생성 END-------------------------------------------------------------------------

        // 학생수 만큼 생성-------------------------------------------------------------------------
        for (AiCustomLearningStntVO _vo : aiCustomLearningStntVOList) {
            List<AiArticleVO> totalArticleList = _vo.getCreateArticles();
            if (totalArticleList.isEmpty()) continue;

            // 세트지 생성
            innerParam.put("setNm", innerParam.get("name"));
            String setsId = createSetsForAiCustomLearning(totalArticleList, innerParam);

            if (ObjectUtils.isNotEmpty(setsId)) {
                innerParam.put("setsId", setsId);

                // setSummary 생성
                createSetSummary(setsId, String.valueOf(innerParam.get("wrterId")), totalArticleList);

                /* Main 서버 DuplicateKeyException 발생
                 * std_dta_result_info 테이블의 ix_std_dta_result_info_02 제약조건 위반
                 * (이미 해당 학생, 탭의 데이터가 존재함)
                 */
                try {
                    // std_dta_result_info 생성
                    innerParam.put("stntId", _vo.getStntId());
                    int resultId = createStdDtaResultInfoForAiCustomLearningPersonal(innerParam);
                    log.info("resultId:{}", resultId);

                    if (resultId <= 0) {
                        throw new AidtException("std_dta_result_info 생성에 실패 했습니다.");
                    }
                    innerParam.put("stdDtaResultId", resultId);

                    // std_dta_result_detail 생성
                    int detailCnt = createStdDtaResultDetailForAiCustomLearning(innerParam);
                    log.info("detailCnt:{}", detailCnt);

                } catch (DuplicateKeyException e) {
                    // 이미 존재하는 탭 호출 시 예외처리
                    resultMap.put(RESULT_OK, false);
                    resultMap.put(RESULT_MSG, "이미 출제된 tab_id 입니다.");

                    log.error("{} 탭의 정보가 이미 존재합니다.", innerParam.get("tabId"));
                    log.error("error msg : {}", e.getMessage());
                    return;
                }
            } else {
                throw new AidtException("세트지 생성에 실패 했습니다.");
            }
        }
        // 학생수 만큼 생성 END-------------------------------------------------------------------------

        // response 세팅
        resultMap.put(RESULT_OK, true);
        resultMap.put("dtaId", dtaId);
//        resultMap.put("pdEvlStDt", innerParam.get("pdEvlStDt"));
//        resultMap.put("pdEvlEdDt", innerParam.get("pdEvlEdDt"));
//        resultMap.put("aiTutSetAt", innerParam.get("aiTutSetAt"));
//        resultMap.put("eamTrget", innerParam.get("eamTrget"));

    }

    private int createStdDtaResultInfoForAiCustomLearningPersonal(Map<String, Object> innerParam) throws Exception {
        int cnt = aiLearningMapper.createStdDtaResultInfoForAiCustomLearningPersonal(innerParam);
        log.info("create stdDtaResult cnt:{}", cnt);
        int stdDtaResultId = Integer.parseInt(String.valueOf(innerParam.get("stdDtaResultId")));
        return stdDtaResultId;

    }

    /**
     * ai맞춤학습 개별문항을 추출한다. (수업중풀기/과제 동일)
     *
     * @param param
     * @param stntLevelList
     * @return
     */
    private List<AiCustomLearningStntVO> makeAiCustomLearningPersonVo(Map<String, Object> param, List<Map<String, Object>> stntLevelList) throws Exception {
        // 학생id를 key 로 오답문제목록 등등의 정보를 가지고 있는 vo 리스트
        List<AiCustomLearningStntVO> aiCustomLearningStntVOList = new ArrayList<>();
        Map<String, Object> innerParam = ObjectUtils.clone(param);

        // 난이도 순서 정의 (상 -> 하)
        List<String> difficultyLevels = List.of(
                AidtConst.DIFFICULT_CODE.MD01.toString(),   // 상
                AidtConst.DIFFICULT_CODE.MD02.toString(),   // 중상
                AidtConst.DIFFICULT_CODE.MD03.toString(),   // 중
                AidtConst.DIFFICULT_CODE.MD04.toString(),   // 중하
                AidtConst.DIFFICULT_CODE.MD05.toString()    // 하
        );

        String stntId;
        String level;

        int stdIdx = 0;
        // 학생별로 문제 출제
        for (Map<String, Object> stntMap : stntLevelList) {
            stntId = MapUtils.getString(stntMap, "mamoymId");
            level = MapUtils.getString(stntMap, "lev");

            //TODO 테스트를 위해 강제로 high 세팅
            //level = AidtConst.STNT_LEVEL_HIGH;

            innerParam.put("stntId", stntId);
            innerParam.put("level", level);

            // 정오답 리스트 (원래는 오답문항만 추출했으나, 정/오답 모두 추출 하도록 쿼리 변경)
            List<Map<String, Object>> allList = aiLearningMapper.findStntIncorrentList(innerParam);

            // 출제에서 제외시킬 article id 리스트 (해당 학생의 오답문제 + 정답문제 + 생성될 출제문제)
            List<String> exceptArticleIds = new ArrayList<>();

            // 학생별 오답문제 추출
            List<Map<String, Object>> incorrectList = new ArrayList<>();
            List<Map<String, Object>> correctList = new ArrayList<>();

            allList.stream()
                    .peek(map -> exceptArticleIds.add(MapUtils.getString(map, "articleId")))
                    .forEach(map -> {
                        if (MapUtils.getInteger(map, "errata") == 1) {
                            // 정답 리스트
                            correctList.add(map);
                        } else {
                            // 오답 리스트
                            incorrectList.add(map);
                        }
                    });

            // 레벨-하 && 오답이 없으면 추가하지 않고 그냥 skip (중/상 은 오답이 없어도 추가문제를 출제해야 하므로 skip 안함)
            if (CollectionUtils.isEmpty(incorrectList) && StringUtils.equals(AidtConst.STNT_LEVEL_LOW, level)) {
                log.warn("no wrong answer:{}", stntId);
                continue;
            }

            innerParam.put("exceptArticleIds", exceptArticleIds);

            // 최종 출제할 유사 아티클 리스트
            List<AiArticleVO> similarArticles = new ArrayList<>();

            // 상/중 학생들을 위한 추가 출제 문항(2개) - 상/중 학생들은 오답이 없어도 무조건 2개 출제함
            Map<String, AiArticleVO> addArticleMap = new HashMap<>();

            ++stdIdx;
            int j = 0;
            // 오답 문제만큼 loop
            // stream 병렬처리 필요해 보임
            if (CollectionUtils.isNotEmpty(incorrectList)) {
                for (Map<String, Object> _map : incorrectList) {
                    log.info("[{}-{}] stntId:{}, incorrect_id:{}", stdIdx, ++j, stntId, _map.get("articleId"));

                    // 오답 문항과 유사한 문항 추출
                    innerParam.put("difficulty", _map.get("difficulty"));   // 난이도

                    // 2024-06-04
                    // [기본조건]은 유형이 동일한 유사문항 받기
                    // [추가조건]은 동일한 유형의 유사문항이 추출되지 않은 경우 유형이 다른 조건으로 한번 더 추출
                    String[] compareValues = {"Y", "N"};
                    AiArticleVO articleVO = null;

                    // 오답 문항의 난이도 코드 가져오기
                    String currentDifficultyCode = (String) _map.get("difficulty");

                    // 현재 난이도의 인덱스 찾기
                    int currentIndex = difficultyLevels.indexOf(currentDifficultyCode);

                    innerParam.put("articleId", _map.get("articleId")); // 오답문항

                    if (currentIndex == -1) {
                        // 현재 난이도가 없을 경우 기존 로직
                        // 오답 문항과 유사한 문항 추출

                        for (String compareVal : compareValues) {
                            innerParam.put("studyMap2Equal", compareVal);
                            // 유사문항 추출
                            articleVO = aiLearningMapper.getSimilarArticle(innerParam);
                            if (ObjectUtils.isNotEmpty(articleVO)) {
                                break;
                            }
                        }
                    } else {
                        // 현재 난이도부터 최하위 난이도까지 순차적으로 검색
                        for (int i = currentIndex; i < difficultyLevels.size() && articleVO == null; i++) {
                            String targetDifficultyCode = difficultyLevels.get(i);
                            innerParam.put("specificDifficultyCode", targetDifficultyCode);

                            // 각 난이도에서 동일 유형 -> 다른 유형 순으로 검색
                            for (String compareVal : compareValues) {
                                innerParam.put("studyMap2Equal", compareVal);

                                // 유사문항 추출
                                articleVO = aiLearningMapper.getSimilarArticleByDifficulty(innerParam);

                                if (ObjectUtils.isNotEmpty(articleVO)) {
                                    break;
                                }
                            }
                        }

                        // 파라미터 정리
                        innerParam.remove("specificDifficultyCode");
                    }

                    // studyMap2Equal 삭제
                    innerParam.remove("studyMap2Equal");

                    if (articleVO != null) {
                        log.info("find article_id:{}", articleVO.getArticle_id());
                        exceptArticleIds.add(articleVO.getArticle_id());
                        similarArticles.add(articleVO);
                    } else {
                        //TODO 찾을 수 없을때는 암것도 안함.
                        log.info("no result.");
                    }

                    //===============================================
                    // 추가문항을 출제한다
                    // 레벨이 중/상 인 학생들만 대상
                    // 중 레벨 : 난이도 2,3 문제 출제(각각 1개씩)
                    // 상 레벨 : 난이도 3,4 문제 출제(각각 1개씩)
                    //-----------------------------------------------
                    // 하 레벨 일때는 추가문제 X
                    String lev = (String) innerParam.get("level");
                    if (!AidtConst.STNT_LEVEL_LOW.equals(lev)) {
                        innerParam.put("correctList", correctList);

                        findLevelBasedAdditionalArticles(innerParam, addArticleMap, exceptArticleIds);
                    }
                }
            } else {
                String lev = (String) innerParam.get("level");
                if (!AidtConst.STNT_LEVEL_LOW.equals(lev)) {
                    innerParam.put("correctList", correctList);

                    findLevelBasedAdditionalArticles(innerParam, addArticleMap, exceptArticleIds);
                }
            }

            AiCustomLearningStntVO vo = AiCustomLearningStntVO.builder()
                    .stntId(stntId)
                    .level(level)
                    .incorrectList(incorrectList)           // 오답 목록
                    .correctList(correctList)               // 정답 목록
                    .exceptArticleIds(exceptArticleIds)
                    .similarArticles(similarArticles)
                    .additionalArticleMap(addArticleMap)
                    .build();

            aiCustomLearningStntVOList.add(vo);

        }
        return aiCustomLearningStntVOList;
    }

    /**
     * 정답 문항을 이용해 출제를 시도
     *
     * @param innerParam
     * @param addArticleMap
     * @param difyCodes
     */
    private void addArticlesForLevelByCollectArticles(Map<String, Object> innerParam, Map<String, AiArticleVO> addArticleMap, List<String> exceptArticleIds, String[] difyCodes) throws Exception {
        if (ObjectUtils.isNotEmpty(innerParam.get("correctList"))) {
            List<Map<String, Object>> correctList = (List) innerParam.get("correctList");

            for (String code : difyCodes) {
                if (addArticleMap.get(code) == null) {
                    for (Map correctMap : correctList) { // 정답만큼 loop
                        innerParam.put("diffcultCode", code);
                        innerParam.put("articleId", correctMap.get("articleId"));

                        // 2024-12-01
                        // [기본조건]은 유형이 다른 문항 받기
                        // [추가조건]은 다른 유형의 문항이 추출되지 않은 경우 유형이 동일한 조건으로 한번 더 추출
                        boolean isAdd = false;
                        String[] compareValues = {"N", "Y"};
                        AiArticleVO addArticleVO = new AiArticleVO();

                        for (String compareVal : compareValues) {
                            innerParam.put("studyMap2Equal", compareVal);
                            addArticleVO = aiLearningMapper.getAddArticle_first(innerParam);

                            // 첫번째 추가문항 출제 시도
                            if (addArticleVO != null) {
                                addArticleMap.put(code, addArticleVO);
                                exceptArticleIds.add(addArticleVO.getArticle_id());
                                innerParam.put("exceptArticleIds", exceptArticleIds);

                                isAdd = true;
                                break; // 세팅했으면 break
                            }
                            // 두번째 추가문항 출제 시도
                            else {
                                log.info("try Second:{}-{}", code, correctMap.get("articleId"));
                                addArticleVO = aiLearningMapper.getAddArticle_second(innerParam);
                                if (addArticleVO != null) {
                                    addArticleMap.put(code, addArticleVO);
                                    exceptArticleIds.add(addArticleVO.getArticle_id());
                                    innerParam.put("exceptArticleIds", exceptArticleIds);

                                    isAdd = true;
                                    break; // 세팅했으면 break
                                }
                            }
                        }

                        if (isAdd) {
                            break;
                        }
                    }
                }
            }
        }

        // studyMap2Equal 삭제
        innerParam.remove("studyMap2Equal");
    }

    /**
     * 레벨별 추가문항 출제 - 두 개의 독립적인 난이도 트랙으로 구현
     *
     * @param innerParam
     * @param addArticleMap
     * @param exceptArticleIds
     */
    private void findLevelBasedAdditionalArticles(Map<String, Object> innerParam,
                                                  Map<String, AiArticleVO> addArticleMap,
                                                  List<String> exceptArticleIds) throws Exception {
        String lev = (String) innerParam.get("level");
        innerParam.put("exceptArticleIds", exceptArticleIds);

        // 전체 난이도 순서 정의 (상 -> 하)
        List<String> allDifficultyLevels = List.of(
                AidtConst.DIFFICULT_CODE.MD01.toString(),  // 상
                AidtConst.DIFFICULT_CODE.MD02.toString(),  // 중상
                AidtConst.DIFFICULT_CODE.MD03.toString(),  // 중
                AidtConst.DIFFICULT_CODE.MD04.toString(),  // 중하
                AidtConst.DIFFICULT_CODE.MD05.toString()   // 하
        );

        // 레벨별 두 개의 트랙 시작 난이도 인덱스 결정
        int track1StartIndex;
        int track2StartIndex;

        if (StringUtils.equals(AidtConst.STNT_LEVEL_HIGH, lev)) {
            // 상 레벨 학생: 중상(MD02) + 중(MD03) 난이도 트랙
            track1StartIndex = 1; // MD02(중상) 인덱스
            track2StartIndex = 2; // MD03(중) 인덱스
        } else if (StringUtils.equals(AidtConst.STNT_LEVEL_MID, lev)) {
            // 중 레벨 학생: 중(MD03) + 중하(MD04) 난이도 트랙
            track1StartIndex = 2; // MD03(중) 인덱스
            track2StartIndex = 3; // MD04(중하) 인덱스
        } else {
            // 하 레벨 등 다른 레벨 학생은 추가 문항 출제하지 않음
            return;
        }

        log.debug("Student level: {}, Track1: {}, Track2: {}", lev,
                allDifficultyLevels.get(track1StartIndex),
                allDifficultyLevels.get(track2StartIndex));

        // 트랙 1에서 문제 찾기
        String track1Key = "track1";
        findArticleForTrack(innerParam, addArticleMap, exceptArticleIds, allDifficultyLevels, track1StartIndex, track1Key);

        // 트랙 2에서 문제 찾기
        String track2Key = "track2";
        findArticleForTrack(innerParam, addArticleMap, exceptArticleIds, allDifficultyLevels, track2StartIndex, track2Key);

        // 모든 트랙을 시도한 후 결과 로깅
        log.debug("Final result: {} articles found after trying all tracks and difficulty levels", addArticleMap.size());
    }

    /**
     * 하나의 난이도 트랙에서 문제 찾기
     * 시작 난이도에서 문제를 찾지 못하면 하위 난이도로 내려가며 시도
     */
    private void findArticleForTrack(Map<String, Object> innerParam,
                                     Map<String, AiArticleVO> addArticleMap,
                                     List<String> exceptArticleIds,
                                     List<String> difficultyLevels,
                                     int startIndex,
                                     String trackKey) throws Exception {
        // 시작 난이도부터 최하위 난이도까지 순차적으로 시도
        for (int i = startIndex; i < difficultyLevels.size(); i++) {
            String difficultyCode = difficultyLevels.get(i);

            log.debug("{}: Trying difficulty level {}", trackKey, difficultyCode);

            // 현재 난이도에서 문제 탐색
            AiArticleVO articleVO = findArticleWithAllStrategies(innerParam, exceptArticleIds, difficultyCode);

            // 문제를 찾았으면 저장하고 종료
            if (articleVO != null) {
                // 난이도와 트랙 정보를 결합한 고유 키 생성 (ex: MD02_track1)
                String uniqueKey = difficultyCode + "_" + trackKey;
                addArticleMap.put(uniqueKey, articleVO);
                exceptArticleIds.add(articleVO.getArticle_id());
                innerParam.put("exceptArticleIds", exceptArticleIds);
                log.debug("{}: Found article with ID {} at difficulty level {}",
                        trackKey, articleVO.getArticle_id(), difficultyCode);
                return;
            }

            log.debug("{}: No article found at difficulty level {}, trying next level", trackKey, difficultyCode);
        }

        log.debug("{}: No article found after trying all difficulty levels", trackKey);
    }

    /**
     * 주어진 난이도에서 모든 전략을 순차적으로 시도하여 문제 찾기
     */
    private AiArticleVO findArticleWithAllStrategies(Map<String, Object> innerParam,
                                                     List<String> exceptArticleIds,
                                                     String difficultyCode) throws Exception {
        innerParam.put("diffcultCode", difficultyCode);

        // 오답 문항 ID가 있는 경우 전략 1과 2 시도
        if (innerParam.containsKey("articleId") && MapUtils.getIntValue(innerParam, "articleId", 0) != 0) {
            // 전략 1: 오답문항과 지식요인이 동일하고 유형이 다른 문항 추출
            AiArticleVO articleVO = tryFirstStrategy(innerParam);
            if (articleVO != null) return articleVO;

            // 전략 2: 학습자료에서 오답문항과 지식요인은 동일하고 유형이 다른 문항 추출
            articleVO = trySecondStrategy(innerParam);
            if (articleVO != null) return articleVO;
        }

        // 전략 3: 정답 문항을 이용해 출제 시도 (최후의 수단)
        return tryThirdStrategy(innerParam);
    }

    /**
     * 전략 1: 오답문항과 지식요인이 동일하고 유형이 다른 문항 추출
     */
    private AiArticleVO tryFirstStrategy(Map<String, Object> innerParam) throws Exception {
        log.debug("Trying Strategy 1 with difficulty {}", innerParam.get("diffcultCode"));

        String[] compareValues = {"N", "Y"}; // 유형이 다른 문항 먼저 시도, 없으면 동일 유형으로 시도

        for (String compareVal : compareValues) {
            innerParam.put("studyMap2Equal", compareVal);
            AiArticleVO articleVO = aiLearningMapper.getAddArticle_first(innerParam);

            if (articleVO != null) {
                log.debug("Strategy 1 found article: {}", articleVO.getArticle_id());
                return articleVO;
            }
        }

        log.debug("Strategy 1 found no articles");
        innerParam.remove("studyMap2Equal");
        return null;
    }

    /**
     * 전략 2: 학습자료에서 오답문항과 지식요인은 동일하고 유형이 다른 문항 추출
     */
    private AiArticleVO trySecondStrategy(Map<String, Object> innerParam) throws Exception {
        log.debug("Trying Strategy 2 with difficulty {}", innerParam.get("diffcultCode"));

        String[] compareValues = {"N", "Y"}; // 유형이 다른 문항 먼저 시도, 없으면 동일 유형으로 시도

        for (String compareVal : compareValues) {
            innerParam.put("studyMap2Equal", compareVal);
            AiArticleVO articleVO = aiLearningMapper.getAddArticle_second(innerParam);

            if (articleVO != null) {
                log.debug("Strategy 2 found article: {}", articleVO.getArticle_id());
                return articleVO;
            }
        }

        log.debug("Strategy 2 found no articles");
        innerParam.remove("studyMap2Equal");
        return null;
    }

    /**
     * 전략 3: 정답 문항을 이용해 출제 시도 (최후의 수단)
     */
    private AiArticleVO tryThirdStrategy(Map<String, Object> innerParam) throws Exception {
        log.debug("Trying Strategy 3 with difficulty {}", innerParam.get("diffcultCode"));

        // 정답 리스트가 없으면 시도할 수 없음
        if (ObjectUtils.isEmpty(innerParam.get("correctList"))) {
            log.debug("Strategy 3 cannot be applied: no correct answers available");
            return null;
        }

        List<Map<String, Object>> correctList = (List) innerParam.get("correctList");

        // 각 정답 문항에 대해 시도
        for (Map correctMap : correctList) {
            String originalArticleId = MapUtils.getString(innerParam, "articleId");
            innerParam.put("articleId", correctMap.get("articleId"));

            String[] compareValues = {"N", "Y"}; // 유형이 다른 문항 먼저 시도, 없으면 동일 유형으로 시도

            // 첫번째 추출 시도
            for (String compareVal : compareValues) {
                innerParam.put("studyMap2Equal", compareVal);
                AiArticleVO articleVO = aiLearningMapper.getAddArticle_first(innerParam);

                if (articleVO != null) {
                    log.debug("Strategy 3 found article with first method: {}", articleVO.getArticle_id());
                    // 원래의 articleId 복원
                    if (StringUtils.isNotEmpty(originalArticleId)) {
                        innerParam.put("articleId", originalArticleId);
                    }
                    return articleVO;
                }
            }

            // 두번째 추출 시도
            for (String compareVal : compareValues) {
                innerParam.put("studyMap2Equal", compareVal);
                AiArticleVO articleVO = aiLearningMapper.getAddArticle_second(innerParam);

                if (articleVO != null) {
                    log.debug("Strategy 3 found article with second method: {}", articleVO.getArticle_id());
                    // 원래의 articleId 복원
                    if (StringUtils.isNotEmpty(originalArticleId)) {
                        innerParam.put("articleId", originalArticleId);
                    }
                    return articleVO;
                }
            }
        }

        log.debug("Strategy 3 found no articles");
        innerParam.remove("studyMap2Equal");
        return null;
    }

    @Transactional(readOnly = true)
    public Object findAiCustomLearningSetInfo(Map<String, Object> paramData) throws Exception {

        List<Map> resultMapList = aiLearningMapper.findAiCustomLearningSetInfo(paramData);

        // tabId 에 대해서 설정정보는 1개가 조회되어야 맞으나, DB 설계상 n개 일 수 있어 list 로 개발하여 첫번째 정보를 사용한다.
        // N개 조회되는 것이 정상이라면 리스트를 반환할것.

        if (resultMapList.size() == 0) return new HashMap<>();

        Map resultMap = resultMapList.get(0);

        // 수업중 풀기 + [개인별문항출제] 인 경우 셋팅
        if (MapUtils.getNumber(resultMap, "aiCstmzdStdMthdSeCd", 0).intValue() == 1
                && MapUtils.getNumber(resultMap, "eamTrget", 0).intValue() == 2 ) {
            List<Map> dtaResultInfoList = aiLearningMapper.findAiCustomLearningSetInfo_dtaResultInfo(paramData);
            resultMap.put("dtaResultInfoList", dtaResultInfoList);
        }
        else {
            resultMap.put("dtaResultInfoList", Collections.emptyList());
        }

        addStntResultInfo(resultMap, paramData);

        int tiId = MapUtils.getIntValue(paramData, "tabId", 0);
        String crtAt = aiLearningMapper.getTabInfoCrtAt(paramData);

        // 동일한 차시에 마지막 AI 맞춤 학습의 tabId 조회
        Map<String, Object> reTiMap =  aiLearningMapper.getReTiMap(paramData);
        int reTiId = MapUtils.getIntValue(reTiMap, "id", 0);

        // 입력된 tabId 와 마지막 tabId 의 값이 같은 경우 다시하기가 진행 되기 전 : N
        // 입력된 tabId 와 마지막 tabId 의 값이 다른 경우 다시하기가 진행 되어 신규 탭이 생김 : Y
        String restartAt = "N";
        if (tiId != reTiId) {
            restartAt = "Y";
        }

        resultMap.put("aiCstmzdStdCrtAt", crtAt);
        resultMap.put("aiCstmzdRestartAt", restartAt);

        return resultMap;

    }

    public Object deleteAiCustomLearning(Map<String, Object> paramData) throws Exception {

        Map resultMap = new HashMap();
        resultMap.put(RESULT_OK, true);
        resultMap.put(RESULT_MSG, "성공");

        Map configMap = (Map) findAiCustomLearningSetInfo(paramData);

        if (MapUtils.isEmpty(configMap)) throw new AidtException("탭 id 에 대한 ai맞춤문항 설정정보가 없습니다.");

        int lrnMethod = MapUtils.getNumber(configMap, "aiCstmzdStdMthdSeCd", 0).intValue(); // 1. 수업중풀기, 2. 과제로내기

        switch (lrnMethod) {
            case 1:
                removeDtaInfo(configMap, resultMap);
                break;
            case 2:
                removeTaskInfo(configMap, resultMap);
                break;
            default:
                log.error("lrnMethod invalid:{}", lrnMethod);
        }

        int delConfigCnt = aiLearningMapper.removeConfigInfoByTabId(paramData);
        log.info("delConfigCnt:{}", delConfigCnt);

        int updTabInfoCnt = aiLearningMapper.resetTabInfoForAiCustomLearning(configMap);
        log.info("updTabInfoCnt:{}", updTabInfoCnt);

        return resultMap;
    }

    /**
     * 수업자료 정보 삭제
     * @param configMap
     * @param resultMap
     */
    private void removeDtaInfo(Map configMap, Map resultMap) throws Exception {

//        int eamTrget = MapUtils.getNumber(configMap, "eamTrget", 0).intValue(); // 1. 공통문항, 2. 개별문항

        log.info("removeDtaInfo:{}", configMap.get("tabId"));
        int cnt = aiLearningMapper.removeDtaInfo(configMap);

//        switch (eamTrget) {
//            case 1 :
//
//                break;
//            case 2:
//                log.info("removeDtaInfo:{}", configMap.get("tabId"));
//                int cnt = aiLearningMapper.removeDtaInfo(configMap);
//                break;
//            default:
//                log.error("eamTrget invalid:{}", eamTrget);
//        }
    }

    /**
     * 과제 정보 삭제
     * @param configMap
     * @param resultMap
     */
    private void removeTaskInfo(Map configMap, Map resultMap) throws Exception {

        int taskId = MapUtils.getNumber(configMap, "taskId", 0).intValue();

        if (taskId == 0) {
            log.error("task_id is empty!!");
            resultMap.put(RESULT_OK, false);
            resultMap.put(RESULT_MSG, "실패");

            return ;
        }

        // task_result_detail 삭제
        int cnt = aiLearningMapper.removeTaskInfo(taskId);
        log.info("detail del cnt:{}", cnt);

    }

    public Map<String, Object> reCreateAiCustomLearining(Map<String, Object> paramData) throws Exception {
        aiLearningMapper.createTabInfo(paramData);
        int tabId = MapUtils.getIntValue(paramData, "id", 0);

        return Map.of("tabId", tabId);
    }

    public Map<String, Object> checkAiCustomLearningAvailability(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        // 1. 기본 날짜 체크
        validateDatePara(paramData);

        // 3. 학습 데이터 유무 조회
        /*
        조회 조건:
        - 해당 차시의 교과서 탭 자동채점문항 풀이 여부 OR
        - 해당 차시의 AI 맞춤학습 풀이 여부(공통/개별, 수업중/과제)
        */
        List<Map<String, Object>> studentDataList = aiLearningMapper.findStudentLearningData(paramData);

        /*if (studentDataList.isEmpty()) throw new AidtException("조회 가능한 학생이 없습니다.");
        if (studentDataList.stream()
                .noneMatch(student -> "Y".equals(student.get("hasLearningData")))) {
            throw new AidtException("출제 가능한 학습 데이터가 없습니다.");
        }*/

        // 4. 결과 맵 생성
        resultMap.put("availableStudents", studentDataList);

        return resultMap;
    }

    public Map<String, Object> createAiCustomPreview(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData;

        String eamTrget = MapUtils.getString(paramData, "eamTrget");
        // null 체크 추가
        if (eamTrget == null || eamTrget.isEmpty()) {
            throw new AidtException("eamTrget 값이 필수입니다.");
        }

        resultData = switch (eamTrget) {
            case "1" -> {
                log.info("1.공통문항 미리보기 START");
                yield createAiCustomLeariningCommonPreview(paramData); // 공통문항 출제
            }
            case "2" -> {
                log.info("2.개별문항 미리보기 START");
                yield createAiCustomLeariningPersonalPreview(paramData); // 개별문항 출제
            }
            default -> throw new AidtException("eamTrget 값이 정의되지 않았습니다.");
        };

        return resultData;

    }

    public Map<String, Object> createAiCustomLearining(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData;

        int tabId = MapUtils.getIntValue(paramData, "tabId", 0);

        paramData.put("tabId", tabId);

        resultData = switch (MapUtils.getString(paramData, "eamTrget")) {
            case "1" -> {
                log.info("1.공통문항 출제 START");
                yield createAiCustomLeariningCommon(paramData); // 공통문항 출제
            }
            case "2" -> {
                log.info("2.개별문항 출제 START");
                yield createAiCustomLeariningPersonal(paramData); // 개별문항 출제
            }
            default -> throw new AidtException("eamTrget 값이 정의되지 않았습니다.");
        };

        return resultData;

    }

    /**
     * 시작일자, 마감일자 검증
     * @param paramData
     * @throws Exception
     */
    private void validateDatePara(Map<String, Object> paramData) throws Exception {

        String stDt = String.valueOf(paramData.getOrDefault("pdEvlStDt", ""));
        String edDt = String.valueOf(paramData.getOrDefault("pdEvlEdDt", ""));

        if (StringUtils.equals("2", String.valueOf(paramData.get("lrnMethod")))) { // 과제
            if (StringUtils.isBlank(stDt)) {
                throw new AidtException("시작일자가 지정되어 있지 않습니다.");
            }

            if (!isValidDateFormat(AidtConst.FORMAT_STRING_YMDHM, stDt)) {
                throw new AidtException("올바른 날짜형식이 아닙니다. \n(ex. 2024-12-31 15:00 과 같은 형식으로 입력해 주세요.)");
            }

            // 종료일자가 있으면 정합성판단, 없으면 계산해서 세팅한다. (+1일 23:59)
            if (!StringUtils.isBlank(edDt)) {
                // 문자열이 "yyyy-MM-dd HH:mm" 형식인지 확인
                if (!isValidDateFormat(AidtConst.FORMAT_STRING_YMDHM, edDt)) {
                    throw new AidtException("올바른 날짜형식이 아닙니다. \n(ex. 2024-12-31 15:00 과 같은 형식으로 입력해 주세요.)");
                }
            }
            else {

                // 값이 없으므로, 시작일자 +1일 23:59 로 세팅
                SimpleDateFormat dateFormat = new SimpleDateFormat(AidtConst.FORMAT_STRING_YMDHM);
                try {
                    Date date = dateFormat.parse(stDt); // 시작일시

                    // 다음 날의 23:59 값을 설정
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // 다음 날로 이동
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);

                    // 설정한 값을 Date로 변환
                    Date dt2 = calendar.getTime();

                    // 계산한 마감 일시를 String 으로 다시 변환해서 param map 에 세팅한다.
                    paramData.put("pdEvlEdDt", dateFormat.format(dt2));
                } catch (ParseException e) {
                    log.error(CustomLokiLog.errorLog(e));
                    //TODO no action 이미 사전에 validate 했음.
                }
            }
        }

    }


    private boolean isValidDateFormat(String fmt, String dt) throws Exception {

        // "yyyy-MM-dd HH:mm"
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);

        try {
            Date date = dateFormat.parse(dt);
            return true;
        } catch (ParseException p) {
            log.error(CustomLokiLog.errorLog(p));
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Object findStntAiCustomLearningSetInfo(Map<String, Object> paramData) throws Exception {
        List<Map> resultMapList = aiLearningMapper.findStntAiCustomLearningSetInfo(paramData);

        // tabId 에 대해서 설정정보는 1개가 조회되어야 맞으나, DB 설계상 n개 일 수 있어 list 로 개발하여 첫번째 정보를 사용한다.
        // N개 조회되는 것이 정상이라면 리스트를 반환할것.

        if (resultMapList.size() == 0) return new HashMap<>();

        Map resultMap = resultMapList.get(0);

        // 수업중 풀기 + [개인별문항출제] 인 경우 셋팅
        if (MapUtils.getNumber(resultMap, "aiCstmzdStdMthdSeCd", 0).intValue() == 1
                && MapUtils.getNumber(resultMap, "eamTrget", 0).intValue() == 2 ) {
            List<Map> dtaResultInfoList = aiLearningMapper.findStntAiCustomLearningSetInfo_dtaResultInfo(paramData);
            resultMap.put("dtaResultInfoList", dtaResultInfoList);
        }
        else {
            resultMap.put("dtaResultInfoList", Collections.emptyList());
        }

        // 맞춤 출제 진행 된 tab일때 입력된 학생 ID 가 대상인 경우 Y
        Map<String, Object> trgtAtFindMap = new HashMap<>();
        trgtAtFindMap.putAll(paramData);
        trgtAtFindMap.putAll(resultMap);
        Map<String, Object> trgtAtMap =  aiLearningMapper.getTrgtAt(trgtAtFindMap);
        String trgtAt = MapUtils.getString(trgtAtMap, "trgtAt", "N'");
        resultMap.put("trgtAt", trgtAt);

        return resultMap;
    }



    private List<String> findEmptyStudents(List<Map<String, Object>> stntLevelList, List<AiCustomLearningStntVO> studentVOList) {
        // 출제대상 id 목록
        Set<String> targetStntIds = stntLevelList.stream()
                .map(map -> (String) map.get("mamoymId"))
                .collect(Collectors.toSet());

        // 출제문항 목록
//        Set<String> studentVOIds = studentVOList.stream()
//                .filter(studentVO -> (studentVO.getSimilarArticles() != null
//                        && !studentVO.getSimilarArticles().isEmpty()) // 오류 문항에 대한 유사문항 추출 목록 존재
//                        || (studentVO.getAdditionalArticleMap() != null
//                        && !studentVO.getAdditionalArticleMap().isEmpty()) ) // 유형이 다른 추가문항 2개 존재
//                .map(AiCustomLearningStntVO::getStntId)
//                .distinct() // 중복된 학생 ID 제거
//                .collect(Collectors.toSet());

        // studentVOList에서 조건에 맞는 학생 ID를 추출하고 중복 제거
        Set<String> studentVOIds = new HashSet<>();
        for (AiCustomLearningStntVO studentVO : studentVOList) {
            List<AiArticleVO> similarArticles = studentVO.getSimilarArticles();
            Map<String, AiArticleVO> additionalArticleMap = studentVO.getAdditionalArticleMap();

            if ((similarArticles != null && !similarArticles.isEmpty())
                    || (additionalArticleMap != null && !additionalArticleMap.isEmpty())) {
                studentVOIds.add(studentVO.getStntId());
            } else {
                log.info("{}:no article", studentVO.getStntId());
            }
        }

        // 출제대상 id 중 출제문항에 없는 학생id 체크
        targetStntIds.removeAll(studentVOIds);

        return new ArrayList<>(targetStntIds);
    }


    /**
     * @summary article의 미리보기 화면 구성을 위한 정보 세팅
     * @param articleVOList 세팅 articleList
     */
    private Map<String, Object> getPreviewInfo(List<AiArticleVO> articleVOList) {
        Map<String, Object> returnMap = new HashMap<>();

        // 미리보기 정보 세팅
        List<String> articleIdList = articleVOList.stream()
                .map(aiArticleVO -> String.valueOf(aiArticleVO.getArticle_id()))
                .toList();

        returnMap.put("previewInfo", aiLearningMapper.getArticlePreviewInfo(articleIdList));
        returnMap.put("articleList", articleIdList);

        return returnMap;
    }

    // =====================================================================
    // 아래 코드는 참조용 예시 코드입니다. 실제로 호출되지 않습니다.
    // 대량 학생 처리를 위한 커스텀 스레드 풀 구현 예시
    // AI 맞춤 학습의 속도 이슈가 있어 병렬처리 참조용으로 남겨둠
    // =====================================================================

    /**
     * [참조용 코드] 대량 학생 처리를 위한 병렬 처리 예시
     * 실제 호출되지 않는 참조용 메소드입니다.
     *
     * 학급 단위(30명) 학생들의 AI 맞춤형 학습 문제를 병렬로 생성하는 예시 코드
     *
     * @param paramData 학생 목록 및 기타 설정 정보를 담은 맵
     * @return 처리 결과를 담은 맵 (성공여부, 처리된 학생 수, 학생별 결과 등)
     * @throws Exception 처리 중 발생한 예외
     */
    private Map<String, Object> _referenceForBatchProcessing_generateBatchAiCustomLearning(Map<String, Object> paramData) throws Exception {
        // 학생 목록 추출 및 결과 맵 초기화
        List<Map<String, Object>> studentList = (List<Map<String, Object>>) paramData.get("studentList");
        int studentCount = studentList.size();
        Map<String, Object> resultMap = new HashMap<>();

        // ===============================================================
        // 1. 스레드 풀 구성 - 학생 처리용 메인 풀
        // ===============================================================
        // 시스템 코어 수와 학생 수를 고려하여 최적의 스레드 풀 크기 계산
        // 예: 4코어 시스템에서는 최대 8개 스레드, 학생이 5명이면 5개 스레드만 생성
        int mainPoolSize = Math.min(studentCount, Runtime.getRuntime().availableProcessors() * 2);
        ExecutorService mainThreadPool = Executors.newFixedThreadPool(mainPoolSize,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        // 스레드 이름에 고유 ID를 포함시켜 로그에서 추적 용이하게 함
                        t.setName("StudentBatch-Worker-" + t.getId());
                        return t;
                    }
                });

        // ===============================================================
        // 2. 각 학생의 트랙 처리용 서브 풀
        // ===============================================================
        // 각 학생 처리 내부에서 트랙 1,2를 처리하기 위한 별도 스레드 풀
        // 트랙 처리는 빠르게 완료되므로 고정 크기 4개로 설정
        ExecutorService trackThreadPool = Executors.newFixedThreadPool(4,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("Track-Worker-" + t.getId());
                        return t;
                    }
                });

        try {
            log.debug("Starting batch processing for {} students", studentCount);
            long batchStartTime = System.currentTimeMillis(); // 전체 작업 시작 시간 기록

            // ===============================================================
            // 3. 학생별 비동기 작업 생성 및 실행
            // ===============================================================
            List<CompletableFuture<Map<String, Object>>> studentFutures = new ArrayList<>();

            for (Map<String, Object> student : studentList) {
                // CompletableFuture.supplyAsync: 비동기적으로 값을 반환하는 작업 생성
                // mainThreadPool에서 실행되어 학생별로 병렬 처리
                CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        String studentId = (String) student.get("mamoymId");
                        String level = (String) student.get("level");

                        log.debug("Processing student: {}, level: {}", studentId, level);
                        long studentStartTime = System.currentTimeMillis();

                        // 학생별 문제 생성을 위한 파라미터 설정
                        // 원본 파라미터를 복사하여 학생별 처리에 사용
                        Map<String, Object> studentParam = new HashMap<>(paramData);
                        studentParam.put("studentId", studentId);
                        studentParam.put("level", level);

                        // ===============================================================
                        // 4. 중첩 병렬 처리: 각 학생별로 트랙 1,2를 동시에 처리
                        // ===============================================================
                        // ConcurrentHashMap: 여러 스레드가 동시에 접근해도 안전한 맵
                        Map<String, AiArticleVO> addArticleMap = new ConcurrentHashMap<>();
                        // synchronizedList: 여러 스레드가 동시에 접근해도 안전한 리스트
                        List<String> exceptArticleIds = Collections.synchronizedList(new ArrayList<>());

                        // 트랙 처리 로직 호출 (서브 스레드 풀 사용)
                        // 내부적으로 두 트랙이 병렬 처리됨
                        _referenceForBatchProcessing_processStudentTracks(
                                studentParam, addArticleMap, exceptArticleIds, trackThreadPool);

                        long studentEndTime = System.currentTimeMillis();
                        log.debug("Student {} completed in {} ms, found {} articles",
                                studentId, (studentEndTime - studentStartTime), addArticleMap.size());

                        // 학생별 처리 결과 구성
                        Map<String, Object> studentResult = new HashMap<>();
                        studentResult.put("studentId", studentId);
                        studentResult.put("articleMap", addArticleMap);
                        studentResult.put("processingTime", (studentEndTime - studentStartTime));
                        return studentResult;

                    } catch (Exception e) {
                        // 한 학생 처리 중 오류가 발생해도 다른 학생 처리는 계속 진행
                        log.error("Error processing student {}: {}", student.get("mamoymId"), e.getMessage());
                        Map<String, Object> errorResult = new HashMap<>();
                        errorResult.put("studentId", student.get("mamoymId"));
                        errorResult.put("error", e.getMessage());
                        return errorResult; // 오류 정보를 포함한 결과 반환
                    }
                }, mainThreadPool);

                studentFutures.add(future);
            }

            // ===============================================================
            // 5. 타임아웃 설정 및 모든 작업 완료 대기
            // ===============================================================
            long timeout = 5 * 60 * 1000; // 최대 5분 대기

            // 모든 CompletableFuture를 배열로 변환하고 allOf로 결합
            // allOf: 모든 CompletableFuture가 완료될 때 완료되는 새 CompletableFuture 반환
            CompletableFuture<Void> allStudents = CompletableFuture.allOf(
                    studentFutures.toArray(new CompletableFuture[0]));

            try {
                // 타임아웃 적용하여 대기 - 5분 이내에 모든 작업이 완료되지 않으면 TimeoutException 발생
                allStudents.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                // 타임아웃 발생 시 이미 완료된 작업만 결과에 포함시키고 진행
                log.warn("Batch processing timed out after {} ms", timeout);
            }

            // ===============================================================
            // 6. 결과 수집 및 반환
            // ===============================================================
            List<Map<String, Object>> studentResults = new ArrayList<>();
            for (CompletableFuture<Map<String, Object>> future : studentFutures) {
                // 정상 완료된 작업의 결과만 수집
                if (future.isDone() && !future.isCompletedExceptionally()) {
                    studentResults.add(future.get());
                }
            }

            long batchEndTime = System.currentTimeMillis();
            log.debug("Batch processing completed for {}/{} students in {} ms",
                    studentResults.size(), studentCount, (batchEndTime - batchStartTime));

            // 종합 결과 구성
            resultMap.put("success", true);
            resultMap.put("processedCount", studentResults.size());
            resultMap.put("totalCount", studentCount);
            resultMap.put("processingTime", (batchEndTime - batchStartTime));
            resultMap.put("studentResults", studentResults);

        } catch (Exception e) {
            // 전체 처리 중 예외 발생 시 오류 정보 반환
            log.error("Error in batch processing: ", e);
            resultMap.put("success", false);
            resultMap.put("error", e.getMessage());
        } finally {
            // ===============================================================
            // 7. 스레드 풀 종료 처리 - 리소스 누수 방지
            // ===============================================================
            _referenceForBatchProcessing_shutdownThreadPool(mainThreadPool, "Main");
            _referenceForBatchProcessing_shutdownThreadPool(trackThreadPool, "Track");
        }

        return resultMap;
    }

    /**
     * [참조용 코드] 각 학생의 트랙 처리 로직 (커스텀 스레드 풀 사용)
     *
     * 한 학생에 대해 두 개의 독립적인 난이도 트랙을 병렬로 처리하는 메소드
     * 각 트랙은 지정된 난이도부터 시작하여 문제를 찾지 못하면 하위 난이도로 이동
     *
     * @param studentParam 학생 정보와 처리 설정을 담은 맵
     * @param addArticleMap 찾은 문항을 저장할 결과 맵 (ConcurrentHashMap)
     * @param exceptArticleIds 제외할 문항 ID 목록 (동기화된 리스트)
     * @param trackThreadPool 트랙 처리에 사용할 스레드 풀
     * @throws Exception 처리 중 발생한 예외
     */
    private void _referenceForBatchProcessing_processStudentTracks(Map<String, Object> studentParam,
                                                                   Map<String, AiArticleVO> addArticleMap,
                                                                   List<String> exceptArticleIds,
                                                                   ExecutorService trackThreadPool) throws Exception {

        // 학생 레벨 추출 및 exceptArticleIds 설정
        String level = (String) studentParam.get("level");
        studentParam.put("exceptArticleIds", exceptArticleIds);

        // ===============================================================
        // 1. 난이도 정의 및 트랙 시작점 설정
        // ===============================================================
        // 전체 난이도 순서 정의 (상 -> 하)
        List<String> allDifficultyLevels = List.of(
                AidtConst.DIFFICULT_CODE.MD01.toString(),  // 상
                AidtConst.DIFFICULT_CODE.MD02.toString(),  // 중상
                AidtConst.DIFFICULT_CODE.MD03.toString(),  // 중
                AidtConst.DIFFICULT_CODE.MD04.toString(),  // 중하
                AidtConst.DIFFICULT_CODE.MD05.toString()   // 하
        );

        // 레벨별 두 개의 트랙 시작 난이도 인덱스 결정
        int track1StartIndex, track2StartIndex;

        if (StringUtils.equals(AidtConst.STNT_LEVEL_HIGH, level)) {
            // 상 레벨 학생: 중상(MD02) 트랙 + 중(MD03) 트랙
            track1StartIndex = 1; // MD02(중상) 인덱스
            track2StartIndex = 2; // MD03(중) 인덱스
        } else if (StringUtils.equals(AidtConst.STNT_LEVEL_MID, level)) {
            // 중 레벨 학생: 중(MD03) 트랙 + 중하(MD04) 트랙
            track1StartIndex = 2; // MD03(중) 인덱스
            track2StartIndex = 3; // MD04(중하) 인덱스
        } else {
            // 하 레벨 학생은 추가 문항 출제하지 않음
            return;
        }

        // ===============================================================
        // 2. 트랙 2를 위한 파라미터 복사본 생성 (동시 접근 문제 방지)
        // ===============================================================
        Map<String, Object> paramForTrack2 = new HashMap<>(studentParam);
        List<String> exceptIdsForTrack2 = new ArrayList<>(exceptArticleIds);

        // 트랙 키 정의
        String track1Key = "track1";
        String track2Key = "track2";

        // ===============================================================
        // 3. 두 트랙을 병렬로 실행 - CompletableFuture 사용
        // ===============================================================

        // 트랙 1 처리 (중상 또는 중 난이도부터 시작)
        CompletableFuture<Void> track1Future = CompletableFuture.runAsync(() -> {
            try {
                // findArticleForTrack 메소드 호출: 난이도 트랙을 따라 문제 찾기
                findArticleForTrack(studentParam, addArticleMap, exceptArticleIds,
                        allDifficultyLevels, track1StartIndex, track1Key);
            } catch (Exception e) {
                // 트랙 1 처리 중 예외 발생 시 로깅
                log.error("Error in track 1 for student {}: ", studentParam.get("studentId"), e);
            }
        }, trackThreadPool); // 지정된 스레드 풀에서 실행

        // 트랙 2 처리 (중 또는 중하 난이도부터 시작)
        CompletableFuture<Void> track2Future = CompletableFuture.runAsync(() -> {
            try {
                // findArticleForTrack 메소드 호출: 난이도 트랙을 따라 문제 찾기
                findArticleForTrack(paramForTrack2, addArticleMap, exceptIdsForTrack2,
                        allDifficultyLevels, track2StartIndex, track2Key);
            } catch (Exception e) {
                // 트랙 2 처리 중 예외 발생 시 로깅
                log.error("Error in track 2 for student {}: ", studentParam.get("studentId"), e);
            }
        }, trackThreadPool); // 지정된 스레드 풀에서 실행

        // ===============================================================
        // 4. 두 트랙 완료 대기 (타임아웃 30초 설정)
        // ===============================================================
        CompletableFuture.allOf(track1Future, track2Future)
                .orTimeout(30, TimeUnit.SECONDS) // 30초 후 타임아웃 발생
                .join(); // 완료될 때까지 현재 스레드 블로킹

        // ===============================================================
        // 5. 결과 병합 - 두 트랙에서 찾은 exceptArticleIds 통합
        // ===============================================================
        exceptArticleIds.addAll(exceptIdsForTrack2);

        // 중복 제거 (Stream API 사용)
        List<String> distinctExceptIds = exceptArticleIds.stream()
                .distinct() // 중복 제거
                .toList(); // 리스트로 변환

        // 원본 리스트 업데이트
        exceptArticleIds.clear();
        exceptArticleIds.addAll(distinctExceptIds);
        studentParam.put("exceptArticleIds", exceptArticleIds);
    }

    /**
     * [참조용 코드] 스레드 풀 종료 처리용 헬퍼 메소드
     *
     * 스레드 풀을 안전하게 종료하기 위한 방법을 구현한 유틸리티 메소드
     * 일반 종료(shutdown)를 시도하고, 시간 내에 종료되지 않으면 강제 종료(shutdownNow) 실행
     *
     * @param pool 종료할 ExecutorService 객체
     * @param poolName 로그 메시지에 포함될 풀 이름
     */
    private void _referenceForBatchProcessing_shutdownThreadPool(ExecutorService pool, String poolName) {
        // ===============================================================
        // 1. 일반 종료 시도 - 새 작업 접수 중단, 기존 작업은 완료 대기
        // ===============================================================
        pool.shutdown();

        try {
            // ===============================================================
            // 2. 지정된 시간(10초) 동안 모든 작업 완료 대기
            // ===============================================================
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                // 10초 내에 종료되지 않은 경우 경고 로그 출력
                log.warn("{} thread pool did not terminate in time, forcing shutdown", poolName);

                // ===============================================================
                // 3. 강제 종료 - 실행 중인 작업도 중단
                // ===============================================================
                List<Runnable> droppedTasks = pool.shutdownNow();
                // 처리되지 못한 작업 수 로깅
                log.warn("{} unfinished tasks were dropped from {}", droppedTasks.size(), poolName);
            } else {
                // 정상 종료된 경우
                log.debug("{} thread pool shut down successfully", poolName);
            }
        } catch (InterruptedException e) {
            // ===============================================================
            // 4. 대기 중 인터럽트 발생 시 강제 종료
            // ===============================================================
            log.error("Interrupted while waiting for {} thread pool to terminate", poolName);
            pool.shutdownNow();

            // 현재 스레드의 인터럽트 상태 복원
            // 이는 호출자가 인터럽트 상태를 확인할 수 있도록 하기 위함
            Thread.currentThread().interrupt();
        }
    }

    // =====================================================================
    // 참조용 예시 코드 끝
    // =====================================================================
}
