package com.visang.aidt.lms.api.integration.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.integration.mapper.IntegExamMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class IntegExamService {

    private final IntegExamMapper integExamMapper;

    public Object insertExamBox(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        try {
            int cnt = integExamMapper.checkSetSummaryExists(paramData);
            if (cnt < 1) {
                throw new IllegalArgumentException("해당 세트지에 대한 출제 정보가 존재하지 않습니다.");
            }
            int version = integExamMapper.getExamBoxNewVersion(paramData);
            paramData.put("version", version);

            integExamMapper.insertExamBox(paramData);
            String insertExamId = MapUtils.getString(paramData, "examId", "");
            integExamMapper.increaseModuleTcUseCnt(paramData);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            returnMap.put("examId", Integer.parseInt(insertExamId));
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", e.getMessage());
        }
        return returnMap;
    }

    public Object updateExamBox(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        String examId = MapUtils.getString(paramData, "examId", "");

        try {
            String delAt = integExamMapper.getExamBoxDelAtStatus(paramData);
            if ("Y".equals(delAt)) {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "이미 삭제된 출제 ID : " + paramData.get("examId"));
                return returnMap;
            }

            int cnt = integExamMapper.checkSetSummaryExists(paramData);
            if (cnt < 1) {
                throw new IllegalArgumentException("해당 setsId에 대한 출제 정보가 존재하지 않습니다.");
            }

            String regDt = integExamMapper.getExamBoxRegDt(paramData);
            paramData.put("regDt", regDt);

            List<String> examIdList = new ArrayList<>();
            examIdList.add(examId);
            paramData.put("examIdList", examIdList);
            this.deleteExamBox(paramData);

            paramData.put("parentId", examId);
            this.insertExamBox(paramData);
            String insertExamId = MapUtils.getString(paramData, "examId", "");
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            returnMap.put("examId", Integer.parseInt(insertExamId));
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
        return returnMap;
    }

    public Object deleteExamBox(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        try {
            List<String> examIdList = (List<String>) paramData.get("examIdList");
            for (String examId : examIdList) {
                Map<String, Object> param = new HashMap<>();
                param.put("examId", examId);
                integExamMapper.deleteExamBox(param);
            }
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } catch (Exception e) {
            log.error("err:", e.getMessage());
            throw e;
        }
        return returnMap;
    }

    public Object listExamBoxInfo(Map<String, Object> paramData, Pageable pageable) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

        String wrterId = (String) paramData.get("wrterId");
        if (wrterId == null || wrterId.trim().isEmpty()) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "wrterId는 필수 값입니다");
            return returnMap;
        }

        // Response Parameters
        List<String> setsItem = Arrays.asList(
                "examId", "examParentId", "textbkId", "version",
                "curriBook", "curriSchoolVal", "curriSubjectId", "curriSubject", "curriSubjectVal", "curriGradeVal", "curriSemesterVal",
                "curriBookCd", "curriBookNm",
                "setsId", "originalSetsId", "name", "category", "setCategoryCd",
                "setCategoryNm", "hashTags", "mdulCnt", "examScopeExistYn",
                "difyNm", "thumbnail", "slfPerEvlAt", "scrapAt", "scrapCnt", "creatorName",
                "creatorId", "creator", "rdate", "udate", "articleTypeList", "publishCnt"
        );
        List<String> articleItem = Arrays.asList(
                "id", "subId", "name", "thumbnail", "hashTags",
                "articleType", "questionType", "slfPerEvlYn", "slfPerEvlInfo",
                "hint", "explanation", "mdulUseCnt", "mdulDifyNm", "mdulScr"
        );
        List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");
        List<Map> setsList = new ArrayList<>();
        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();
        List<Map> setsEntityList = integExamMapper.listExamBoxInfo(pagingParam);
        if (!setsEntityList.isEmpty()) {
            List<Object> setsIdList = CollectionUtils.emptyIfNull(setsEntityList).stream().map(s -> s.get("setsId")).toList();
            Map<String, List<Object>> paramMap = Map.of("setsIdList", setsIdList);
            // 메터정보
//            List<Map> metaEntityList = integExamMapper.findLesnRscList_meta(paramMap);
            // 모듈정보
//            List<Map> articleEntityList = integExamMapper.findLesnRscList_article(paramMap);
            // 모듈유형정보
            List<Map> articleTypeList = integExamMapper.findLesnRscList_articleType(paramMap);

            List<Map> articleEntityList = integExamMapper.findLesnRscList_article(paramMap);

            boolean isFirst = true;
            for (Map entity : setsEntityList) {
                if (isFirst) {
                    total = (Long) entity.get("fullCount");
                    isFirst = false;
                }
                List<String> comentItem = Arrays.asList("hint", "explanation");

                var tmap = AidtCommonUtil.filterToMap(setsItem, entity);
                tmap.put("category", paramData.get("category")); // 파라미터 검색 구분

                paramData.put("setsId", entity.get("setsId"));
                List<Map> comentList = integExamMapper.findLesnRscInfo_coment(paramData);
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
                        .filter(r -> StringUtils.equals(MapUtils.getString(tmap, "setsId"), MapUtils.getString(r, "setsId")))
                        .map(r -> {
                            return AidtCommonUtil.filterToMap(articleTypeItem, r);
                        }).toList());

                List<LinkedHashMap<Object, Object>> filteredArticleList = CollectionUtils.emptyIfNull(articleEntityList).stream()
                        .filter(r -> StringUtils.equals(MapUtils.getString(tmap, "setsId"), MapUtils.getString(r, "setsId")))
                        .map(r -> {
                            var articleMap = AidtCommonUtil.filterToMap(articleItem, r);

                            Object slfPerEvlInfo = articleMap.get("slfPerEvlInfo");
                            if (!ObjectUtils.isEmpty(slfPerEvlInfo)) {
                                String evaluationJsonString = (String) slfPerEvlInfo;
                                JsonObject jsonObject = JsonParser.parseString(evaluationJsonString).getAsJsonObject();
                                Map<String, Object> evaluationMap = new Gson().fromJson(jsonObject.toString(), Map.class);
                                articleMap.put("slfPerEvlInfo", evaluationMap);
                            } else {
                                articleMap.put("slfPerEvlInfo", new HashMap<>());
                            }

                            Map<String, Object> matchedComent = CollectionUtils.emptyIfNull(comentList).stream()
                                    .filter(m -> StringUtils.equals(MapUtils.getString(articleMap, "id"), MapUtils.getString(m, "articleId")))
                                    .filter(m -> StringUtils.equals(MapUtils.getString(articleMap, "subId"), MapUtils.getString(m, "subId")))
                                    .findFirst()
                                    .orElse(null);

                            if (matchedComent != null) {
                                articleMap.putAll(AidtCommonUtil.filterToMap(comentItem, matchedComent));
                            }

                            return articleMap;
                        })
                        .toList();
                tmap.put("articleList", filteredArticleList);
                setsList.add(tmap);
                paramData.remove("setsId");

            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(setsList, pageable, total);

        returnMap.put("examList", setsList);
        returnMap.put("page", page);
        return returnMap;
    }

    public Object getExamInfo(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        // Response Parameters
        List<String> setsInfoItem = Arrays.asList(
                "examId", "examParentId", "textbkId", "version", "delAt", "wrterId",
                "curriBook", "curriSchoolVal", "curriSubjectId", "curriSubject", "curriSubjectVal", "curriGradeVal", "curriSemesterVal",
                "curriBookCd", "curriBookNm",
                "setsId", "originalSetsId", "name", "setCategoryCd", "setCategoryNm", "hashTags", "difyNm",
                "description", "mdulCnt", "difyHighMdulCnt", "difyMiddleHighMdulCnt", "difyMiddleMdulCnt", "difyMiddleLowMdulCnt", "difyLowMdulCnt",
                "scrapAt", "scrapCnt", "setTotalScr",
                "slfEvlYn", "slfEvlInfo", "perEvlYn", "perEvlInfo",
                "creatorName", "creatorId", "creator", "rdate", "udate", "examScopeExistYn"
        );
        /*List<String> metaItem = Arrays.asList("id", "name", "code", "val", "isActive");*/
        List<String> articleItem = Arrays.asList(
                "id", "subId", "name", "thumbnail", "hashTags", "source",
                "articleType", "questionType", "slfPerEvlYn", "slfPerEvlInfo",
                "hint", "explanation", "mdulUseCnt", "mdulDifyNm", "mdulScr"
        );
        List<String> articleTypeItem = Arrays.asList("articleType", "articleTypeCnt");
        List<String> difficultyItem = Arrays.asList("difficulty", "difficultyCnt");
        List<String> comentItem = Arrays.asList("hint", "explanation");

        // 세트지정보
        Map<Object, Object> setsInfo = new LinkedHashMap<>();
        Map lesnRscInfo = integExamMapper.getExamBoxInfo(paramData);
        if (!lesnRscInfo.get("wrterId").equals(paramData.get("wrterId"))) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "출제 작성자와 조회 계정이 다릅니다");
            return returnMap;
        }
        if (lesnRscInfo != null) {
            setsInfo.putAll(lesnRscInfo);
            String difyMdulCnt = MapUtils.getString(setsInfo, "difyMdulCnt", "");
            if (StringUtils.isNotEmpty(difyMdulCnt)) {
                setsInfo.putAll(new Gson().fromJson(difyMdulCnt, Map.class));
            }
            setsInfo = AidtCommonUtil.filterToMap(setsInfoItem, setsInfo);

            // 자기동료평가 설정정보
            Map<String, Object> srhInfo = new HashMap<>();
            srhInfo.put("setsId", setsInfo.get("setsId"));

            // 메타정보
            Map<String, List<Object>> paramMap = Map.of("setsIdList", List.of(setsInfo.get("setsId")));
            /*List<LinkedHashMap<Object, Object>> metaList = AidtCommonUtil.filterToList(metaItem, tchLesnRscMapper.findLesnRscList_meta(paramMap));
            setsInfo.put("metaList", metaList);*/

            // 모듈유형정보
            List<LinkedHashMap<Object, Object>> articleTypeList = AidtCommonUtil.filterToList(articleTypeItem, integExamMapper.findLesnRscList_articleType(paramMap));
            setsInfo.put("articleTypeList", articleTypeList);

            // 난이도정보
            List<LinkedHashMap<Object, Object>> difficultyList = AidtCommonUtil.filterToList(difficultyItem, integExamMapper.findLesnRscList_difficulty(paramMap));
            setsInfo.put("difficultyList", difficultyList);

            // 모듈해설
            paramData.put("setsId", setsInfo.get("setsId"));
            List<Map> comentList = integExamMapper.findLesnRscInfo_coment(paramData);

            // 모듈정보
            /*List<MetaEntity> metaEntityList = new ArrayList<>();
            List<LibraryEntity> libraryEntityList = new ArrayList<>();
            List<LibtextEntity> libtextEntityList = new ArrayList<>();*/
            List<LinkedHashMap<Object, Object>> articleList = AidtCommonUtil.filterToList(articleItem, integExamMapper.findLesnRscList_article(paramMap));
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
                        s.putAll(AidtCommonUtil.filterToMap(comentItem, CollectionUtils.emptyIfNull(comentList).stream()
                                .filter(m -> StringUtils.equals(MapUtils.getString(s, "id"), MapUtils.getString(m, "articleId")))
                                .filter(m -> StringUtils.equals(MapUtils.getString(s, "subId"), MapUtils.getString(m, "subId")))
                                .findFirst().orElse(null)));
                        return s;
                    }).toList());
        }
        // Response
        return setsInfo;
    }

    public Object listExamBoxHist(Map<String, Object> paramData, Pageable pageable) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

        List<String> item = Arrays.asList(
                "publishId", "version", "examRegDt", "publishUse", "publishNm", "claId", "claNm", "publishRegDt"
        );

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();
        List<Map> examBoxHist = integExamMapper.listExamBoxHist(pagingParam);
        long total = examBoxHist.isEmpty() ? 0 : (long) examBoxHist.get(0).get("fullCount");

        PagingInfo page = AidtCommonUtil.ofPageInfo(examBoxHist, pageable, total);
        returnMap.put("examHist", AidtCommonUtil.filterToList(item, examBoxHist));
        returnMap.put("page", page);
        return returnMap;
    }

    public Object listTextbkByExamHist(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        List<Map> textbkList = integExamMapper.listTextbkByExamHist(paramData);
        returnMap.put("textbkList", textbkList);
        return returnMap;
    }

}
