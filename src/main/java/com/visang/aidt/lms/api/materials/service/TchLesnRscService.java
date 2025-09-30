package com.visang.aidt.lms.api.materials.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.assessment.mapper.TchSlfperEvalMapper;
import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.materials.mapper.TchLesnRscMapper;
import com.visang.aidt.lms.api.textbook.mapper.CrcuMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@Service
@AllArgsConstructor
public class TchLesnRscService {

    private final TchLesnRscMapper tchLesnRscMapper;

    private final TchSlfperEvalMapper tchSlfperEvalMapper;

    private final CrcuMapper crcuMapper;

    private final AiLearningMapper aiLearningMapper;

    /**
     * (수업자료).수업 자료 목록 조회
     *
     * @param paramData 입력 파라메터
     * @param pageable 페이징 정보
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findLesnRscList(Map<String, Object> paramData, Pageable pageable) throws Exception {

//        long startTime = System.nanoTime();

        // Response Parameters
        List<String> setsItem = Arrays.asList(
            "id", "name", "category", "setCategoryCd",
            "setCategoryNm", "hashTags", "mdulCnt", "examScopeExistYn",
            "difyNm", "thumbnail", "slfPerEvlAt", "scrapAt", "scrapCnt", "creatorName",
            "creatorId", "creator", "regdate", "updDate", "articleTypeList"
        );
        /*List<String> metaItem = Arrays.asList("id", "name", "code", "val", "isActive");
        List<String> articleItem = Arrays.asList(
            "id", "name", "articleType", "url","thumbnail",
            "hashTags","questionStr","review","isActive","isPublicOpen","isEditable"
        );*/
        List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");

        List<Map> setsList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        // 세트지 정보
        List<Map> setsEntityList = tchLesnRscMapper.findLesnRscList(pagingParam);
        if(!setsEntityList.isEmpty()) {
            List<Object> setsIdList = CollectionUtils.emptyIfNull(setsEntityList).stream().map(s -> s.get("id")).toList();
            Map<String, List<Object>> paramMap = Map.of("setsIdList", setsIdList);
            // 메터정보
            /*List<Map> metaEntityList = tchLesnRscMapper.findLesnRscList_meta(paramMap);*/
            // 모듈정보
            /*List<Map> articleEntityList = tchLesnRscMapper.findLesnRscList_article(paramMap);*/
            // 모듈유형정보
            List<Map> articleTypeList = tchLesnRscMapper.findLesnRscList_articleType(paramMap);

            boolean isFirst = true;
            for (Map entity : setsEntityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(setsItem, entity);
                tmap.put("category", paramData.get("category")); // 파라미터 검색 구분

                // 메타정보
                /*tmap.put("metaList", CollectionUtils.emptyIfNull(metaEntityList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(tmap,"id"),MapUtils.getString(r,"setsId")))
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(metaItem, r);
                    }).toList());*/

                // 모듈정보
                /*tmap.put("articleList", CollectionUtils.emptyIfNull(articleEntityList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(tmap,"id"),MapUtils.getString(r,"setsId")))
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(articleItem, r);
                    }).toList());*/

                // 모듈유형정보
                tmap.put("articleTypeList", CollectionUtils.emptyIfNull(articleTypeList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(tmap,"id"),MapUtils.getString(r,"setsId")))
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(articleTypeItem, r);
                    }).toList());

                setsList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(setsList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("setsList",setsList);
        returnMap.put("page",page);

//        long endTime = System.nanoTime();
//        long duration = endTime - startTime;
//        log.error("함수 소요 시간: " + duration + " 나노초");
//        log.error("함수 소요 시간: " + (duration / 1000000) + " 밀리초");

        return returnMap;
    }

    /**
     * (수업자료).수업 자료 상세 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object findLesnRscInfo(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> setsInfoItem = Arrays.asList(
            "id", "name", "setCategoryCd", "setCategoryNm", "hashTags","difyNm",
            "description", "mdulCnt","difyHighMdulCnt", "difyMiddleMdulCnt", "difyLowMdulCnt",
            "scrapAt", "scrapCnt", "setTotalScr",
            "slfEvlYn", "slfEvlInfo", "perEvlYn", "perEvlInfo",
            "creatorName", "creatorId", "creator", "regdate", "udpDate", "examScopeExistYn"
        );
        /*List<String> metaItem = Arrays.asList("id", "name", "code", "val", "isActive");*/
        List<String> articleItem = Arrays.asList(
            "id", "subId", "name", "thumbnail","hashTags",
            "articleType","questionType","slfPerEvlYn","slfPerEvlInfo",
            "hint", "explanation", "mdulUseCnt", "mdulDifyNm","mdulScr"
        );
        List<String> comentItem = Arrays.asList("hint", "explanation");

        // 세트지정보
        Map<Object, Object> setsInfo = new LinkedHashMap<>();
        Map lesnRscInfo = tchLesnRscMapper.findLesnRscInfo(paramData);
        if(lesnRscInfo != null) {
            setsInfo.putAll(lesnRscInfo);
            String difyMdulCnt = MapUtils.getString(setsInfo, "difyMdulCnt", "");
            if (StringUtils.isNotEmpty(difyMdulCnt)) {
                setsInfo.putAll(new Gson().fromJson(difyMdulCnt, Map.class));
            }
            setsInfo = AidtCommonUtil.filterToMap(setsInfoItem, setsInfo);

            // 자기동료평가 설정정보
            Map<String,Object> srhInfo = new HashMap<>();
            srhInfo.put("setsId", setsInfo.get("id"));

            String[] slfPerEvlYnNms = {"slfEvlYn","perEvlYn"};
            String[] slfPerEvlNms   = {"slfEvlInfo","perEvlInfo"};
            int[] slfPerEvlClsfCds  = {1,2}; // 1: 자기평가, 2: 동료평가

            for(int i=0; i<slfPerEvlClsfCds.length; i++) {
                srhInfo.put("slfPerEvlClsfCd", slfPerEvlClsfCds[i]);

                Object slfPerSetInfo = tchSlfperEvalMapper.findTchSlfperEvlSetInfo(srhInfo);
                setsInfo.put(slfPerEvlYnNms[i], ObjectUtils.isNotEmpty(slfPerSetInfo) ? "Y" : "N");
                setsInfo.put(slfPerEvlNms[i], slfPerSetInfo);
            }

            // 메타정보
            Map<String, List<Object>> paramMap = Map.of("setsIdList", List.of(setsInfo.get("id")));
            /*List<LinkedHashMap<Object, Object>> metaList = AidtCommonUtil.filterToList(metaItem, tchLesnRscMapper.findLesnRscList_meta(paramMap));
            setsInfo.put("metaList", metaList);*/

            // 모듈해설
            List<Map> comentList = tchLesnRscMapper.findLesnRscInfo_coment(paramData);

            // 모듈정보
            /*List<MetaEntity> metaEntityList = new ArrayList<>();
            List<LibraryEntity> libraryEntityList = new ArrayList<>();
            List<LibtextEntity> libtextEntityList = new ArrayList<>();*/
            List<LinkedHashMap<Object, Object>> articleList = AidtCommonUtil.filterToList(articleItem, tchLesnRscMapper.findLesnRscList_article(paramMap));
            List<Long> articleIdList = articleList.stream().map(s -> NumberUtils.toLong(s.get("id").toString())).toList();
            /*if (!articleIdList.isEmpty()) {
                ModelMapper modelMapper = new ModelMapper();
                Map<String, List<Long>> mdulParamMap = Map.of("articleIdList", articleIdList);
                metaEntityList.addAll(CollectionUtils.emptyIfNull(tchLesnRscMapper.findMdulList_meta(mdulParamMap)).stream()
                    .map(entity -> modelMapper.map(entity, MetaEntity.class)).toList());
                libraryEntityList.addAll(CollectionUtils.emptyIfNull(tchLesnRscMapper.findMdulList_library(mdulParamMap)).stream()
                    .map(entity -> modelMapper.map(entity, LibraryEntity.class)).toList());
                libtextEntityList.addAll(CollectionUtils.emptyIfNull(tchLesnRscMapper.findMdulList_libtext(mdulParamMap)).stream()
                    .map(entity -> modelMapper.map(entity, LibtextEntity.class)).toList());
            }*/
            setsInfo.put("articleList", CollectionUtils.emptyIfNull(articleList).stream()
                .map(s -> {
                    // 2024-04-12
                    // 모듈(아티클)에 설정되어 있는 자기동료평가 설정정보 처리
                    Object slfPerEvlInfo = s.get("slfPerEvlInfo");
                    if (!ObjectUtils.isEmpty(slfPerEvlInfo)) {
                        String evaluationJsonString = (String) slfPerEvlInfo;

                        JsonObject jsonObject = JsonParser.parseString(evaluationJsonString).getAsJsonObject();
                        Map<String, Object> evaluationMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                        s.put("slfPerEvlInfo", evaluationMap);
                    } else {
                        s.put("slfPerEvlInfo", new HashMap<>());
                    }

                    /*s.put("metaList",  CollectionUtils.emptyIfNull(metaEntityList).stream()
                        .filter(m -> Objects.equals(articleId,m.getArticleId()))
                        .map(MetaDTO::toDTO).toList());
                    s.put("libraryList", CollectionUtils.emptyIfNull(libraryEntityList).stream()
                        .filter(m -> Objects.equals(articleId,m.getArticleId()))
                        .map(LibraryDTO::toDTO).toList());
                    s.put("libtextList", CollectionUtils.emptyIfNull(libtextEntityList).stream()
                        .filter(m -> Objects.equals(articleId,m.getArticleId()))
                        .map(LibtextDTO::toDTO).toList());*/
                    s.putAll(AidtCommonUtil.filterToMap(comentItem,CollectionUtils.emptyIfNull(comentList).stream()
                        .filter(m -> StringUtils.equals(MapUtils.getString(s,"id"),MapUtils.getString(m,"articleId")))
                        .filter(m -> StringUtils.equals(MapUtils.getString(s,"subId"),MapUtils.getString(m,"subId")))
                        .findFirst().orElse(null)));
                    return s;
                }).toList());
        }

        // Response
        return setsInfo;
    }


    /**
     * (모듈).모듈(콘텐츠) 목록 조회
     *
     * @param paramData 입력 파라메터
     * @param pageable
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findMdulList(Map<String, Object> paramData, Pageable pageable) throws Exception {

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        long total = 0l;
        List<LinkedHashMap> articleList = new ArrayList<>();
        String autoEamAt = MapUtils.getString(paramData, "autoEamAt", "N");
        if ("Y".equals(autoEamAt)) {
            LinkedHashMap<String, Integer> requestCountMap = new LinkedHashMap<>();
            int cntHigh = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamGdExmNum", "0"))); // 상
            int cntAvUp = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamAvUpExmNum", "0"))); // 중상
            int cntMid = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamAvExmNum", "0"))); // 중
            int cntAvLw = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamAvLwExmNum", "0"))); // 중하
            int cntLow = Integer.parseInt(String.valueOf(paramData.getOrDefault("eamBdExmNum", "0")));  // 하
            if (cntLow > 0) requestCountMap.put("하", cntLow);
            if (cntAvLw > 0) requestCountMap.put("중하", cntAvLw);
            if (cntMid > 0) requestCountMap.put("중", cntMid);
            if (cntAvUp > 0) requestCountMap.put("중상", cntAvUp);
            if (cntHigh > 0) requestCountMap.put("상", cntHigh);

            // 난이도별로 맞춤 아티클 추출
            Map<String, Object> innerParam = ObjectUtils.clone(paramData);
            for (String key: requestCountMap.keySet()) {
                innerParam.put("difficultyNm", key);
                innerParam.put("difficultyCnt", requestCountMap.get(key));
                pagingParam = PagingParam.builder()
                        .param(innerParam)
                        .pageable(pageable)
                        .build();
                List<LinkedHashMap> list = tchLesnRscMapper.findMdulList(pagingParam);
                articleList.addAll(list);
                total += list.isEmpty() ? 0 : (long) list.get(0).get("fullCount");
            }
        } else {
            if (Arrays.asList("1175", "1197", "1198", "1199").contains(paramData.get("textbkId").toString())) {
                // 초등 수학인 교과서에 대한 처리
                articleList = tchLesnRscMapper.findElementTextbkIdsMdulList(pagingParam);
            } else {
                // 그 외 textbkId에 대한 기본 처리
                articleList = tchLesnRscMapper.findMdulList(pagingParam);
            }
            total = articleList.isEmpty() ? 0 : (long) articleList.get(0).get("fullCount");
        }

        // 모듈 id 리스트 - map, library, libText 목록을 조회하기 위함
//        List<String> articleIdList = CollectionUtils.emptyIfNull(articleList).stream()
//                .map(map -> (MapUtils.getString(map, "id")))
//                .toList();

        // total count
        PageImpl<LinkedHashMap> pageInfo = new PageImpl<>(articleList, pageable, total);
        PagingInfo page = PagingInfo.builder()
                .size(pageInfo.getNumberOfElements())
                .totalElements(pageInfo.getTotalElements())
                .totalPages(pageInfo.getTotalPages())
                .number(pageInfo.getNumber())
                .build();

        return new JSONObject()
                .put("moduleCount",total)
                .put("moduleList",articleList)
                .put("page",page)
                .toMap();
    }

    /**
     * (모듈).모듈(콘텐츠) 상세 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findMdulInfo(Map<String, Object> paramData) throws Exception {
        ModelMapper modelMapper = new ModelMapper();

        Map<String, Object> resultMap = new HashMap<>();

        Map modulInfo = tchLesnRscMapper.findMdulInfo(paramData);

        if (modulInfo != null) {

            // 2024-04-04
            // 모듈(아티클)에 설정되어 있는 자기동료평가 설정정보 처리
            Object slfPerEvlInfo = modulInfo.get("slfPerEvlInfo");
            if (!ObjectUtils.isEmpty(slfPerEvlInfo)) {
                String evaluationJsonString = (String) slfPerEvlInfo;

                JsonObject jsonObject = JsonParser.parseString(evaluationJsonString).getAsJsonObject();
                Map<String, Object> evaluationMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                modulInfo.put("slfPerEvlInfo", evaluationMap);
            } else {
                modulInfo.put("slfPerEvlInfo", new HashMap<>());
            }

            /*List<MetaEntity> metaEntityList;
            List<LibraryEntity> libraryEntityList;
            List<LibtextEntity> libtextEntityList;

            List<Map> mapList;
            if (paramMap != null && !paramMap.isEmpty()) {
                mapList = tchLesnRscMapper.findMdulList_meta(paramMap);
                metaEntityList = mapList.stream()
                        .map(entity -> modelMapper.map(entity, MetaEntity.class))
                        .toList();

                mapList = tchLesnRscMapper.findMdulList_library(paramMap);
                libraryEntityList = mapList.stream()
                        .map(entity -> modelMapper.map(entity, LibraryEntity.class))
                        .toList();

                mapList= tchLesnRscMapper.findMdulList_libtext(paramMap);
                libtextEntityList = mapList.stream()
                        .map(entity -> modelMapper.map(entity, LibtextEntity.class))
                        .toList();
            } else {
                libtextEntityList = new ArrayList<>();
                libraryEntityList = new ArrayList<>();
                metaEntityList = new ArrayList<>();
            }

            modulInfo.put("metaList", metaEntityList);
            modulInfo.put("libraryList", libraryEntityList);
            modulInfo.put("libtextList", libtextEntityList);*/
        }
        resultMap.put("moduleInfo", modulInfo);
        return resultMap;

    }

    public Map<String, Object> saveTchLesnrscScrp(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("resultOk", false);

        if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
            returnMap.put("resultMsg", "userId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("dtaCd") == null || ("").equals(paramData.get("dtaCd"))) {
            returnMap.put("resultMsg", "dtaCd를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("dtaId") == null || ("").equals(paramData.get("dtaId"))) {
            returnMap.put("resultMsg", "dtaId를 입력해주세요");
            return returnMap;
        }

        int result = tchLesnRscMapper.saveTchLesnrscScrp(paramData);

        if(result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchLesnrscScrpList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        returnMap.put("resultOk", false);

        if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
            returnMap.put("resultMsg", "userId를 입력해주세요");
            return returnMap;
        }

        List<String> scrapInfoItem = Arrays.asList("dtaCd","dtaNm", "dtaId");
        List<LinkedHashMap<Object, Object>> resultList = AidtCommonUtil.filterToList(scrapInfoItem, tchLesnRscMapper.selectScrapInfoList(paramData));

        returnMap.put("scrpRgtrId", paramData.get("userId"));
        returnMap.put("scrpList", resultList);


        return returnMap;
    }

    //수업 자료실 세트지 (삭제)
    public Object updateTchLesnrscSet(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);

        if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
            returnMap.put("resultMsg", "userId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("setsId") == null || ("").equals(paramData.get("setsId"))) {
            returnMap.put("resultMsg", "setsId를 입력해주세요");
            return returnMap;
        }

        int resultCnt = tchLesnRscMapper.deleteTchLesnrscSet(paramData);

        Map<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("dtaCd","1"); // 1:셋트, 2:아티클
        paramMap.put("dtaId",paramData.get("setsId"));

        tchLesnRscMapper.deleteTchLesnrscScript(paramMap);

        if( resultCnt > 0 ) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");

        }else{
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패 - 나의 수업자료(콘텐츠)만 삭제할 수 있습니다.");
        }

        returnMap.put("setsId", paramData.get("setsId"));

        return returnMap;
    }

    // 수업 자료실 모듈(콘텐츠) (삭제)
    public Object updateTchLesnrscMdul(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);

        if (paramData.get("userId") == null || ("").equals(paramData.get("userId"))) {
            returnMap.put("resultMsg", "userId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("articleId") == null || ("").equals(paramData.get("articleId"))) {
            returnMap.put("resultMsg", "articleId를 입력해주세요");
            return returnMap;
        }

        int resultCnt = tchLesnRscMapper.deleteTchLesnrscMdul(paramData);

        Map<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("dtaCd","2"); // 1:셋트, 2:아티클
        paramMap.put("dtaId",paramData.get("articleId"));

        tchLesnRscMapper.deleteTchLesnrscScript(paramMap);

        if( resultCnt > 0 ) {

            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        }else{
            //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패 - 나의 수업자료(콘텐츠)만 삭제할 수 있습니다.");
        }

        returnMap.put("articleId", paramData.get("articleId"));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchLesnrscExamscopeList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "조회한 데이터가 없습니다.");

        // 브랜드 ID 조회(교과서 구분용)
        Map<String,Object> srhMap = new HashMap<>();
        srhMap.put("textbookId", paramData.get("textbkId"));

        int brandId = crcuMapper.findTextbookBrandId(srhMap);
        paramData.put("brandId", brandId);

        List<Map> tchLesnrscExamscopeList = tchLesnRscMapper.findTchLesnrscExamscopeList(paramData);
        if(tchLesnrscExamscopeList != null && ! tchLesnrscExamscopeList.isEmpty()) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        List<String> examscopeInfoItem = Arrays.asList("id","parentId", "val", "depth", "mdulCnt");
        List<LinkedHashMap<Object, Object>> resultList = AidtCommonUtil.filterToList(examscopeInfoItem, tchLesnrscExamscopeList);

        returnMap.put("examScopeList", resultList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchLesnrscMdulExamscopeList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "조회한 데이터가 없습니다.");

        // 브랜드 ID 조회(교과서 구분용)
        Map<String,Object> srhMap = new HashMap<>();
        srhMap.put("textbookId", paramData.get("textbkId"));

        int brandId = crcuMapper.findTextbookBrandId(srhMap);
        paramData.put("brandId", brandId);

        List<Map> tchLesnrscMdulExamscopeList = tchLesnRscMapper.findTchLesnrscMdulExamscopeList(paramData);
        if(tchLesnrscMdulExamscopeList != null && ! tchLesnrscMdulExamscopeList.isEmpty()) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");

            // 2024-07-18, mdulCnt 추가
            // 맨 마지막 depth에 mdulCnt 값 1로 설정
            // 2024-09-10, mdulCnt 제외 (박채린 CP님과 협의완료)
            /*
            int size = tchLesnrscMdulExamscopeList.size();
            for(int i=0; i<size; i++) {
                if((i+1) == size) {
                    tchLesnrscMdulExamscopeList.get(i).put("mdulCnt", 1);
                } else {
                    tchLesnrscMdulExamscopeList.get(i).put("mdulCnt", 0);
                }
            }*/
        }

        List<String> examscopeInfoItem = Arrays.asList("id","parentId", "val", "depth");
        List<LinkedHashMap<Object, Object>> resultList = AidtCommonUtil.filterToList(examscopeInfoItem, tchLesnrscMdulExamscopeList);

        returnMap.put("mdulExamScopeList", resultList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchLesnrscMdulSearchFilterInfo(Map<String, Object> paramData) throws Exception {

        // 검색필터 구분 : 난이도 / 교과역량 / 평가영역
        String[] filterStr = {"difficulty", "subjectAbility", "evaluationArea"};

        Map<String, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> innerParam = ObjectUtils.clone(paramData);
        innerParam.put("kind", "mdul");

        String textbId = paramData.get("textbkId") != null ? paramData.get("textbkId").toString() : "";

        // 초등 수학 교과서 목록 정의
        List<String> specialTextbIds = Arrays.asList("1175", "1197", "1198", "1199", "7036", "7040", "7041", "7042");

        // textbId가 특정 목록에 포함되어 있는지 확인
        boolean isSpecialTextbId = !textbId.isEmpty() && specialTextbIds.contains(textbId);

        for (String name : filterStr) {
            innerParam.put("name", name);

            if (isSpecialTextbId) {
                List<Map<String, Object>> filterList = tchLesnRscMapper.findTchLesnrscMdulSearchFilterInfoForElementMath(innerParam);
                returnMap.put(name+"List", filterList);
            } else {
                List<Map<String, Object>> filterList = tchLesnRscMapper.findTchLesnrscMdulSearchFilterInfo(innerParam);
                returnMap.put(name+"List", filterList);
            }
        }

        int brandId = aiLearningMapper.findBrandId(paramData);

        if (brandId == 1) {
            // 수학
            List<Map<String, Object>> questionTypeFilterList = tchLesnRscMapper.findTchLesnrscMdulSearchQuestionTypeFilterInfoForMath(innerParam);
            returnMap.put("questionTypeList", questionTypeFilterList);
        } else {
            // 영어
            List<Map<String, Object>> questionTypeFilterList = tchLesnRscMapper.findTchLesnrscMdulSearchQuestionTypeFilterInfo(innerParam);
            returnMap.put("questionTypeList", questionTypeFilterList);
        }


        List<Map<String, Object>> creatorList = new LinkedList<>();
        // 작성자로 검색 기능 제거 - 프론트 오류방지를 위해 항목은 살려둠
//        creatorList.add(new LinkedHashMap<>() {{
//            put("creator","visang");
//            put("creatorName","저작자");
//            put("bmkYn", "N");
//        }});
//        creatorList.addAll(tchLesnRscMapper.findTchLesnrscMdulSearchFilterInfoCreator(innerParam));
        returnMap.put("creatorList", creatorList);

        return returnMap;
    }


    public Object createTchLesnrscMdulSearchFilterBookmark(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);

        if (paramData.get("textbkId") == null || ("").equals(paramData.get("textbkId"))) {
            returnMap.put("resultMsg", "textbkId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("wrterId") == null || ("").equals(paramData.get("wrterId"))) {
            returnMap.put("resultMsg", "wrterId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("creatorId") == null || ("").equals(paramData.get("creatorId"))) {
            returnMap.put("resultMsg", "creatorId를 입력해주세요");
            return returnMap;
        }

        int resultCnt = tchLesnRscMapper.createTchLesnrscMdulSearchFilterBookmark(paramData);

        if (resultCnt == 0) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패(이미 등록된 출처)");
        } else {
            returnMap.put("bkmkId", paramData.get("id"));
            returnMap.put("resultMsg", "성공");

            paramData.remove("id"); // generated key 는 파라미터에서 제거
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchLesnrscSearchFilterInfo(Map<String, Object> paramData) throws Exception {

        // 검색필터 구분 : 난이도
        String[] filterStr = {"difficulty"};

        Map<String, Object> returnMap = new LinkedHashMap<>();

        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        String textbId = paramData.get("textbkId") != null ? paramData.get("textbkId").toString() : "";

        // 초등 수학 교과서 목록 정의
        List<String> specialTextbIds = Arrays.asList("1175", "1197", "1198", "1199");

        // textbId가 특정 목록에 포함되어 있는지 확인
        boolean isSpecialTextbId = !textbId.isEmpty() && specialTextbIds.contains(textbId);

        for (String name : filterStr) {
            innerParam.put("name", name);
            if (isSpecialTextbId) {
                List<Map<String, Object>> filterList = tchLesnRscMapper.findTchLesnrscMdulSearchFilterInfoForElementMath(innerParam);
                returnMap.put(name+"List", filterList);
            } else {
                List<Map<String, Object>> filterList = tchLesnRscMapper.findTchLesnrscMdulSearchFilterInfo(innerParam);
                returnMap.put(name+"List", filterList);
            }
        }

        List<Map<String, Object>> creatorList = new ArrayList<>();
        // 작성자로 조회 기능 제거 - 프론트 오류 방지를 위해 항목은 살려둠
//        creatorList.add(new LinkedHashMap<>() {{
//            put("creator","visang");
//            put("creatorName","저작자");
//            put("bmkYn", "N");
//        }});
//        creatorList.addAll(tchLesnRscMapper.findTchLesnrscSearchFilterInfoCreator(innerParam));

        returnMap.put("creatorList", creatorList);

        return returnMap;
    }

    public Object deleteTchLesnrscMdulSearchFilterBookmark(Map<String, Object> paramData) throws Exception {

        Map<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("resultOk", true);

        if (paramData.get("textbkId") == null || ("").equals(paramData.get("textbkId"))) {
            returnMap.put("resultMsg", "textbkId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("wrterId") == null || ("").equals(paramData.get("wrterId"))) {
            returnMap.put("resultMsg", "wrterId를 입력해주세요");
            return returnMap;
        }
        if (paramData.get("creatorId") == null || ("").equals(paramData.get("creatorId"))) {
            returnMap.put("resultMsg", "creatorId를 입력해주세요");
            return returnMap;
        }

        int resultCnt = tchLesnRscMapper.deleteTchLesnrscMdulSearchFilterBookmark(paramData);

        if (resultCnt == 0) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패(삭제할 북마크가 없습니다.)");
        } else {
            returnMap.put("resultMsg", "성공");
        }

        return returnMap;
    }


    /**
     * (교사).스크랩(해제)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> deleteTchLesnRscScrp(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        try {
            int cnt = tchLesnRscMapper.deleteTchLesnRscScrp(paramData);

            if (cnt == 0) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "내 스크랩 정보가 아님");
            } else {
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "성공");
            }
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }
    @Transactional(readOnly = true)
    public Object findTchLesnrscUnitRcmdList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        List<String> setsListItem = Arrays.asList("id","name","category","setCategoryCd","setCategoryNm","hashTags","mdulCnt","difyNm","thumbnail","creatorName","creatorId","scrapAt","scrapCnt","regdate");
        List<String> articleTypeItem = Arrays.asList("articleType","articleTypeCnt");

        List<LinkedHashMap<Object, Object>> setsList = AidtCommonUtil.filterToList(setsListItem, tchLesnRscMapper.findTchLesnrscUnitRcmdList(paramData));

        if (CollectionUtils.isNotEmpty(setsList)) {
            List<Object> setsIdList = CollectionUtils.emptyIfNull(setsList).stream().map(s -> s.get("id")).collect(Collectors.toList());

            Map innerParam = Map.of("setsIdList", setsIdList);

            List<Map> articleTypeList = tchLesnRscMapper.findLesnRscList_articleType(innerParam);

            // 아티클 타입 세팅
            for (Map sets : setsList) {
                sets.put("articleTypeList", CollectionUtils.emptyIfNull(articleTypeList).stream()
                        .filter(a -> StringUtils.equals(MapUtils.getString(sets,"id"), MapUtils.getString(a,"setsId")))
                        .map(a -> {
                            return AidtCommonUtil.filterToMap(articleTypeItem, a);
                        }).toList());

            }
        }
        returnMap.put("setsList", setsList);

        return returnMap;
    }

    /* 셋트지 ID에 대한 아티클 목록조회 */
    public Object findArticleListBySetId(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        List<String> articleItem = Arrays.asList("articleId","subId","evlIemScr","articleType","articleTypeNm","questionType","questionTypeNm","difficulty","difficultyNm");

        List<LinkedHashMap<Object, Object>> articleList = AidtCommonUtil.filterToList(articleItem, tchLesnRscMapper.findArticleListBySetId(paramData));
        returnMap.put("articleList", articleList);

        return returnMap;
    }

    // 커리큘럼 목차에서 선택한 차시에 포함된 학습맵을 갖고있는 셋트지 목록을 조회 (추천 목록)
    @Transactional(readOnly = true)
    public Object findLesnRscRecList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> setsItem = Arrays.asList(
                "id", "name", "setCategoryCd",
                "setCategoryNm", "hashTags", "mdulCnt", "examScopeExistYn",
                "difyNm", "thumbnail", "scrapAt", "scrapCnt", "creatorName",
                "creatorId", "creator", "regdate", "updDate", "articleTypeList"
        );

        List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");

        List<Map> setsList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // 브랜드 아이디 추출
        int brandId = aiLearningMapper.findBrandId(paramData);

        List<Map> setsEntityList = new ArrayList<>();
        // 세트지 정보
        if (brandId == 1) {  // 수학
            setsEntityList = tchLesnRscMapper.findLesnRscRecListForMath(pagingParam);
        } else if (brandId == 3) {  // 영어
            setsEntityList = tchLesnRscMapper.findLesnRscRecListForEng(pagingParam);
        }
        if(!setsEntityList.isEmpty()) {
            List<Object> setsIdList = CollectionUtils.emptyIfNull(setsEntityList).stream().map(s -> s.get("id")).toList();
            Map<String, List<Object>> paramMap = Map.of("setsIdList", setsIdList);

            // 모듈유형정보
            List<Map> articleTypeList = tchLesnRscMapper.findLesnRscList_articleType(paramMap);

            boolean isFirst = true;
            for (Map entity : setsEntityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(setsItem, entity);
                // 모듈유형정보
                tmap.put("articleTypeList", CollectionUtils.emptyIfNull(articleTypeList).stream()
                        .filter(r -> StringUtils.equals(MapUtils.getString(tmap,"id"),MapUtils.getString(r,"setsId")))
                        .map(r -> {
                            return AidtCommonUtil.filterToMap(articleTypeItem, r);
                        }).toList());

                setsList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(setsList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("setsList",setsList);
        returnMap.put("page",page);

        return returnMap;
    }

    // 교사가 스크랩한 셋트지 목록을 조회
    @Transactional(readOnly = true)
    public Object findLesnRscMyScrapList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> setsItem = Arrays.asList(
                "id", "name", "setCategoryCd",
                "setCategoryNm", "hashTags", "mdulCnt", "examScopeExistYn",
                "difyNm", "thumbnail", "scrapAt", "scrapCnt", "creatorName",
                "creatorId", "creator", "regdate", "updDate", "articleTypeList"
        );

        List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");

        List<Map> setsList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // 세트지 정보
        List<Map> setsEntityList = tchLesnRscMapper.findLesnRscMyScrapList(pagingParam);
        if(!setsEntityList.isEmpty()) {
            List<Object> setsIdList = CollectionUtils.emptyIfNull(setsEntityList).stream().map(s -> s.get("id")).toList();
            Map<String, List<Object>> paramMap = Map.of("setsIdList", setsIdList);

            // 모듈유형정보
            List<Map> articleTypeList = tchLesnRscMapper.findLesnRscList_articleType(paramMap);

            boolean isFirst = true;
            for (Map entity : setsEntityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(setsItem, entity);
                // 모듈유형정보
                tmap.put("articleTypeList", CollectionUtils.emptyIfNull(articleTypeList).stream()
                        .filter(r -> StringUtils.equals(MapUtils.getString(tmap,"id"),MapUtils.getString(r,"setsId")))
                        .map(r -> {
                            return AidtCommonUtil.filterToMap(articleTypeItem, r);
                        }).toList());

                setsList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(setsList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("setsList",setsList);
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findMyLesnRscList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> setsItem = Arrays.asList(
                "stdId", "setsId", "setsNm", "textbkTabNm", "tmprStrgAt", "eamMth",
                "setCategoryCd", "setCategoryNm", "hashTags", "mdulCnt", "examScopeExistYn",
                "difyNm", "thumbnail", "regdate", "updDate", "articleTypeList"
                ,"extLearnCntsId", "cntsType", "cntsNm", "cntsExt", "url"
        );

        List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");

        List<Map> setsList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // 세트지 정보
        List<Map> setsEntityList = tchLesnRscMapper.findMyLesnRscList(pagingParam);
        if(!setsEntityList.isEmpty()) {
            List<Object> setsIdList = CollectionUtils.emptyIfNull(setsEntityList).stream().map(s -> s.get("setsId")).toList();
            Map<String, List<Object>> paramMap = Map.of("setsIdList", setsIdList);

            // 모듈유형정보
            List<Map> articleTypeList = tchLesnRscMapper.findLesnRscList_articleType(paramMap);

            boolean isFirst = true;
            for (Map entity : setsEntityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(setsItem, entity);
                // 모듈유형정보
                tmap.put("articleTypeList", CollectionUtils.emptyIfNull(articleTypeList).stream()
                        .filter(r -> StringUtils.equals(MapUtils.getString(tmap,"setsId"),MapUtils.getString(r,"setsId")))
                        .map(r -> {
                            return AidtCommonUtil.filterToMap(articleTypeItem, r);
                        }).toList());

                setsList.add(tmap);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(setsList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("setsList",setsList);
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findLesnRscMdulRecList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> mdulItem = Arrays.asList(
            "id", "name", "thumbnail", "hashTags", "articleType", "questionType", "difficulty",
            "scrapAt", "scrapCnt", "mdulUseCnt", "slfPerEvlYn", "examScopeExistYn", "fullCount"
        );

        long total = 0;
        List<Map> moduleList = new ArrayList<>();

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<LinkedHashMap<Object, Object>> list = AidtCommonUtil.filterToList(mdulItem, tchLesnRscMapper.findLesnRscMdulRecList(pagingParam));
        if(list != null && !list.isEmpty()) {
            total = (Long) ((Map)list.get(0)).get("fullCount");

            moduleList.addAll(list);
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(moduleList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("moduleList",list);
        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findLesnRscMdulRecListForEng(Map<String, Object> paramData, Pageable pageable) throws Exception {
        // null 체크 로직 추가
        if (paramData == null) {
            throw new IllegalArgumentException("paramData는 null일 수 없습니다.");
        }

        if (pageable == null) {
            throw new IllegalArgumentException("pageable은 null일 수 없습니다.");
        }

        // Response Parameters
        List<String> mdulItem = Arrays.asList(
            "priority","matchCnt","id", "name", "thumbnail", "hashTags", "articleType", "questionType", "difficulty",
            "scrapAt", "scrapCnt", "mdulUseCnt", "slfPerEvlYn", "examScopeExistYn", "fullCount"
        );

        long total = 0;
        List<Map> moduleList = new ArrayList<>();

        int pageNo = MapUtils.getIntValue(paramData, "page", 0) + 1;
        int sizeNo = pageable.getPageSize();

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        // articleIdList null 체크 추가
        Object articleIdListObj = paramData.get("articleIdList");
        if (articleIdListObj == null) {
            throw new IllegalArgumentException("articleIdList는 null일 수 없습니다.");
        }

        String articleIds[] = articleIdListObj.toString().split(",");
        List<LinkedHashMap<Object, Object>> list = new ArrayList<>();

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        for (int ii = 0; ii < articleIds.length; ii++) {
            if (articleIds[ii] != null && !articleIds[ii].trim().isEmpty()) {
                paramData.put("articleId", articleIds[ii]);
                List<LinkedHashMap<Object, Object>> tempList = AidtCommonUtil.filterToList(mdulItem, tchLesnRscMapper.findLesnRscMdulRecListForEng(pagingParam));

                if (tempList != null) {
                    list.addAll(tempList);
                }
            }
        }


        if (list != null) {
            list = list.stream().distinct().collect(Collectors.toList());
        } else {
            list = new ArrayList<>();
        }

        if (list != null && !list.isEmpty()) {
            list.sort(
                    Comparator.<LinkedHashMap<Object, Object>, Float>
                                    comparing(map -> MapUtils.getFloat(map, "priority"))
                            .thenComparing(map -> MapUtils.getFloat(map, "matchCnt"), Comparator.reverseOrder())
            );
        }

        if(list != null && !list.isEmpty()) {
            total = list.size();
            moduleList.addAll(list);
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(moduleList, pageable, total);


        // 페이지 처리
        int startIndex = (pageNo - 1) * sizeNo;
        int endIndex = Math.min(startIndex + sizeNo, list.size());

        if (list != null && !list.isEmpty()) {
            // 유효한 범위인지 확인
            if (startIndex < list.size() && startIndex >= 0) {
                int validEndIndex = Math.min(endIndex, list.size());

                List<LinkedHashMap<Object, Object>> pageContent = list.subList(startIndex, validEndIndex);

                if (pageContent != null && !pageContent.isEmpty()) {
                    System.out.println("m번째 페이지의 내용:");
                    pageContent.forEach(System.out::println);
                    returnMap.put("moduleList", pageContent);
                } else {
                    System.out.println("페이지 내용이 비어있습니다.");
                    returnMap.put("moduleList", new ArrayList<>());
                }
            } else {
                returnMap.put("moduleList", new ArrayList<>());
            }
        }else{
            System.out.println("리스트가 null이거나 비어있습니다.");
            returnMap.put("moduleList", new ArrayList<>());
        }

        returnMap.put("page",page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findLesnRscRcmdList(Map<String, Object> paramData) throws Exception {

        // Response Parameters
        List<String> setsItem = Arrays.asList("id", "name", "setCategoryCd", "setCategoryNm", "hashTags", "mudlCnt", "difyNm", "thumbnail", "order", "slfPerEvlAt", "creatorName", "creatorId", "regdate", "articleTypeList", "scrapAt", "scrapCnt");
        List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");

        List<Map> setsList = new ArrayList<>();
        long total = 0;

        // 세트지 정보
        List<Map> setsEntityList = tchLesnRscMapper.findLesnRscRcmdList(paramData);

        // 2025-02-26 수업이 없을 경우, 1차시로 조회하는 로직 삭제 (기획자 김새미CP님 요청 사항)
//        if(setsEntityList.isEmpty()) {
//            setsEntityList = tchLesnRscMapper.findLesnRscRcmdListCrculId1(paramData);
//        }

        if(!setsEntityList.isEmpty()) {
            List<Object> setsIdList = CollectionUtils.emptyIfNull(setsEntityList).stream().map(s -> s.get("id")).toList();
            Map<String, List<Object>> paramMap = Map.of("setsIdList", setsIdList);
            List<Map> articleTypeList = tchLesnRscMapper.findLesnRscList_articleType(paramMap);

            boolean isFirst = true;
            for (Map entity : setsEntityList) {
                if(isFirst) {
                    total = (Long)entity.get("fullCount");
                    isFirst = false;
                }

                var tmap = AidtCommonUtil.filterToMap(setsItem, entity);

                // 모듈유형정보
                tmap.put("articleTypeList", CollectionUtils.emptyIfNull(articleTypeList).stream()
                    .filter(r -> StringUtils.equals(MapUtils.getString(tmap,"id"),MapUtils.getString(r,"setsId")))
                    .map(r -> {
                        return AidtCommonUtil.filterToMap(articleTypeItem, r);
                    }).toList());

                setsList.add(tmap);
            }
        }

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("setsList",setsList);

        return returnMap;
    }
}
