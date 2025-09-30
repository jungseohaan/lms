package com.visang.aidt.lms.api.materials.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.materials.mapper.TchMaterialsMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TchMaterialsServcie {
    private final TchMaterialsMapper tchMaterialsMapper;

    @Transactional
    public Map<String, Object> createMaterialsCreate(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<String, Object>();

        /* 이미 등록된 세트지 id는 등록되지 않도록 처리 */
        int count = tchMaterialsMapper.selecTcMaterialsInfoCount(paramData);

        if (count > 0) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "이미 등록된 내 자료 입니다.");
            return returnMap;
        }

        /* 교과서 > 수업재구성 > 내 자료 를 내 자료에 등록할 시 */
        int extLearnCntsId = MapUtils.getIntValue(paramData, "extLearnCntsId", 0);
        if (extLearnCntsId > 0) {
            paramData.put("extLearnCntsId", extLearnCntsId);

            int result2 = tchMaterialsMapper.createMaterialsSaveExtLearnCnts(paramData);
            log.info("result2:{}", result2);

            if (result2 > 0) {
                int tcMaterialsId = MapUtils.getIntValue(paramData, "id", 0);
                paramData.put("tcMaterialsId", tcMaterialsId);
                paramData.remove("id"); /* 임시 id 제거 */

                int materialsDetailSaveExtLearnCntsResult = tchMaterialsMapper.createMaterialsDetailSaveExtLearnCnts(paramData);
                log.info("materialsDetailSaveExtLearnCntsResult:{}", materialsDetailSaveExtLearnCntsResult);

                if (materialsDetailSaveExtLearnCntsResult > 0) {
                    returnMap.put("tcMaterialsId", tcMaterialsId);
                    returnMap.put("resultOk", true);
                    returnMap.put("resultMsg", "성공");
                } else {
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "ExtLearnCnts Detail 저장 실패");
                }
            } else {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "ExtLearnCnts 저장 실패");
            }
        } else {
            String paramName = paramData.get("name") != null ? (String) paramData.get("name") : "";
            int duplicateCount = tchMaterialsMapper.checkDuplicateName(paramData);

            int result;

            if (duplicateCount == 0) {
                /* 내 자료 등록 시 */
                result = tchMaterialsMapper.createMaterialsSaveSets(paramData);
                log.info("result:{}", result);
            } else {
                /* 내 자료 등록 시 - 이름이 같을 때 (중복) */
                paramData.put("name", "[중복] " + paramName);
                result = tchMaterialsMapper.createMaterialsSaveSets(paramData);
                log.info("result:{}", result);
            }

            int tcMaterialsInfoId = 0;
            if (result > 0) {
                tcMaterialsInfoId = MapUtils.getIntValue(paramData, "id", 0);
                paramData.put("tcMaterialsInfoId", tcMaterialsInfoId);
                paramData.remove("id"); /* 임시 id 제거 */

                int materialsDetailResult = tchMaterialsMapper.createMaterialsDetailSaveSetsummary(paramData);
                log.info("materialsDetailResult:{}", materialsDetailResult);

                if (materialsDetailResult > 0) {
                    returnMap.put("tcMaterialsInfoId", tcMaterialsInfoId);
                    returnMap.put("resultOk", true);
                    returnMap.put("resultMsg", "성공");
                } else {
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "Materials Detail 저장 실패");
                }
            } else {
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "tc_materials_info 저장 실패");
                return returnMap;
            }
        }

        return returnMap;
    }

    public Object findMaterialsList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        long setsTotalCount = 0;

        PagingParam<?> pagingParam  = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> tcMaterialsInfoList = tchMaterialsMapper.findMaterialsList(pagingParam);

        if (!tcMaterialsInfoList.isEmpty()) {
            setsTotalCount = (long) tcMaterialsInfoList.get(0).get("setsTotalCount");

            for (Map<String, Object> item : tcMaterialsInfoList) {
                String articleTypeJson = (String) item.get("articleTypeJson");
                Map<String, Object> articleTypeMap = new HashMap<>();

                if (articleTypeJson != null && !articleTypeJson.isEmpty()) {
                    try {
                        articleTypeMap = objectMapper.readValue(
                                articleTypeJson,
                                new TypeReference<Map<String, Object>>() {}
                        );
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        // CSAP 보안 취약점 수정 - 구체적인 예외 처리
                        log.error("JSON parsing error in findMaterialsList: {}", CustomLokiLog.errorLog(e));
                        // 파싱 실패 시 기본값 설정
                        articleTypeMap.put("conceptCnt", 0);
                        articleTypeMap.put("questionCnt", 0);
                        articleTypeMap.put("movementCnt", 0);
                    } catch (IllegalArgumentException e) {
                        log.error("Invalid argument for articleTypeJson: {}", CustomLokiLog.errorLog(e));
                        articleTypeMap.put("conceptCnt", 0);
                        articleTypeMap.put("questionCnt", 0);
                        articleTypeMap.put("movementCnt", 0);
                    }
                } else {
                    // null이거나 빈 문자열일 때 기본값 설정
                    articleTypeMap.put("conceptCnt", 0);
                    articleTypeMap.put("questionCnt", 0);
                    articleTypeMap.put("movementCnt", 0);
                }

                item.putAll(articleTypeMap);
                item.remove("articleTypeJson");

                String classTasksListJson = (String) item.get("classTasksList");
                List<Map<String, Object>> tcMaterialsClassTasksList = new ArrayList<>();

                if (classTasksListJson != null && !classTasksListJson.isEmpty()) {
                    try {
                        List<String> claIdList = objectMapper.readValue(
                                classTasksListJson,
                                new TypeReference<List<String>>() {}
                        );

                        for (String claId : claIdList) {
                            Map<String, Object> classTaskItem = new HashMap<>();
                            classTaskItem.put("claId", claId);
                            tcMaterialsClassTasksList.add(classTaskItem);
                        }

                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        // CSAP 보안 취약점 수정 - 구체적인 예외 처리
                        log.error("JSON parsing error for classTasksList: {}", CustomLokiLog.errorLog(e));
                        tcMaterialsClassTasksList = new ArrayList<>();
                    } catch (IllegalArgumentException e) {
                        log.error("Invalid argument for classTasksList: {}", CustomLokiLog.errorLog(e));
                        tcMaterialsClassTasksList = new ArrayList<>();
                    }
                }

                item.put("tcMaterialsClassTasksList", tcMaterialsClassTasksList);
                item.remove("classTasksList");
            }
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(tcMaterialsInfoList, pageable, setsTotalCount);

        Map<String, Object> result = new HashMap<>();
        result.put("setsTotalCount", setsTotalCount);
        result.put("tcMaterialsInfoList", tcMaterialsInfoList);
        result.put("page", page);

        return result;
    }

    public Map<String, Object> findMaterialsDetail(Map<String, Object> paramData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> errorResult = new HashMap<>();

        List<Map<String, Object>> queryResults = tchMaterialsMapper.findMaterialsDetail(paramData);

        if (queryResults == null || queryResults.isEmpty()) {
            errorResult.put("resultOk", false);
            errorResult.put("resultMsg", "해당하는 자료가 없습니다.");

            return errorResult;
        }

        Map<String, Object> firstRow = queryResults.get(0);

        Map<String, Object> result = new LinkedHashMap<>(firstRow);

        Arrays.asList("articleId", "subId", "thumbnail", "hashTags", "questionType",
                        "articleType", "articleDifficulty", "score", "articleUseCount", "hint",
                        "explanation", "qnum", "textbkId", "cntsType", "cntsExt", "url")
                .forEach(result::remove);

        /* 콘텐츠 난이도별 개수 JSON 파싱 */
        String difficultyCountJson = (String) result.get("articleDifficultyCountJson");
        if (difficultyCountJson != null && !difficultyCountJson.isEmpty()) {
            try {
                Map<String, Object> difficultyCount = objectMapper.readValue(difficultyCountJson, Map.class);
                result.put("difyHighMdulCnt", difficultyCount.get("difyHighMdulCnt"));
                result.put("difyMiddleHighMdulCnt", difficultyCount.get("difyMiddleHighMdulCnt"));
                result.put("difyMiddleMdulCnt", difficultyCount.get("difyMiddleMdulCnt"));
                result.put("difyMiddleLowMdulCnt", difficultyCount.get("difyMiddleLowMdulCnt"));
                result.put("difyLowMdulCnt", difficultyCount.get("difyLowMdulCnt"));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // CSAP 보안 취약점 수정 - 구체적인 예외 처리
                log.error("JSON parsing error for difficultyCountJson: {}", CustomLokiLog.errorLog(e));
                result.put("difyHighMdulCnt", 0);
                result.put("difyMiddleHighMdulCnt", 0);
                result.put("difyMiddleMdulCnt", 0);
                result.put("difyMiddleLowMdulCnt", 0);
                result.put("difyLowMdulCnt", 0);
            } catch (IllegalArgumentException e) {
                log.error("Invalid argument for difficultyCountJson: {}", CustomLokiLog.errorLog(e));
                result.put("difyHighMdulCnt", 0);
                result.put("difyMiddleHighMdulCnt", 0);
                result.put("difyMiddleMdulCnt", 0);
                result.put("difyMiddleLowMdulCnt", 0);
                result.put("difyLowMdulCnt", 0);
            }
        } else {
            result.put("difyHighMdulCnt", 0);
            result.put("difyMiddleHighMdulCnt", 0);
            result.put("difyMiddleMdulCnt", 0);
            result.put("difyMiddleLowMdulCnt", 0);
            result.put("difyLowMdulCnt", 0);
        }
        result.remove("articleDifficultyCountJson");

        /* 문제 유형별 개수 JSON 파싱 */
        String articleTypeJson = (String) result.get("articleTypeJson");
        if (articleTypeJson != null && !articleTypeJson.isEmpty()) {
            try {
                Map<String, Object> articleTypeCount = objectMapper.readValue(articleTypeJson, Map.class);
                result.put("conceptCnt", articleTypeCount.get("conceptCnt"));
                result.put("questionCnt", articleTypeCount.get("questionCnt"));
                result.put("movementCnt", articleTypeCount.get("movementCnt"));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // CSAP 보안 취약점 수정 - 구체적인 예외 처리
                log.error("JSON parsing error for articleTypeJson in detail: {}", CustomLokiLog.errorLog(e));
                result.put("conceptCnt", 0);
                result.put("questionCnt", 0);
                result.put("movementCnt", 0);
            } catch (IllegalArgumentException e) {
                log.error("Invalid argument for articleTypeJson in detail: {}", CustomLokiLog.errorLog(e));
                result.put("conceptCnt", 0);
                result.put("questionCnt", 0);
                result.put("movementCnt", 0);
            }
        } else {
            result.put("conceptCnt", 0);
            result.put("questionCnt", 0);
            result.put("movementCnt", 0);
        }
        result.remove("articleTypeJson");

        result.put("thumbnail", result.get("tcMaterialsInfoThumbnail"));
        result.remove("tcMaterialsInfoThumbnail");

        List<Map<String, Object>> tcMaterialsDetailList = new ArrayList<>();
        for (Map<String, Object> row : queryResults) {
            Map<String, Object> detailItem = new LinkedHashMap<>();

            detailItem.put("articleId", row.get("articleId"));
            detailItem.put("subId", row.get("subId"));
            detailItem.put("name", row.get("name"));
            detailItem.put("thumbnail", row.get("thumbnail"));
            detailItem.put("hashTags", row.get("hashTags"));
            detailItem.put("questionType", row.get("questionType"));
            detailItem.put("articleType", row.get("articleType"));
            detailItem.put("articleDifficulty", row.get("articleDifficulty"));
            detailItem.put("score", row.get("score"));
            detailItem.put("articleUseCount", row.get("articleUseCount"));
            detailItem.put("hint", row.get("hint"));
            detailItem.put("explanation", row.get("explanation"));
            detailItem.put("qnum", row.get("qnum"));
            detailItem.put("textbkId", row.get("textbkId"));
            detailItem.put("cntsType", row.get("cntsType"));
            detailItem.put("cntsExt", row.get("cntsExt"));
            detailItem.put("url", row.get("url"));

            tcMaterialsDetailList.add(detailItem);
        }

        result.put("tcMaterialsDetailList", tcMaterialsDetailList);

        String setsId = (String) firstRow.get("setsId");

        // setSummary 검증: setsId가 존재하고 'null'이 아닌 경우에만 검증
        if (StringUtils.isNotBlank(setsId) && !"null".equals(setsId)) {
            Map<String, Object> setSummaryCheckMap = new HashMap<>();

            setSummaryCheckMap.put("setsId", setsId);

            int isSetSummaryExist = tchMaterialsMapper.findSetSummaryForMaterials(setSummaryCheckMap);

            if (isSetSummaryExist == 0) {
                errorResult.putAll(result);
                errorResult.put("resultOk", false);
                errorResult.put("resultMsg", "setSummary 데이터가 없어 내 자료 조회 불가");
                errorResult.put("errCode", "setIdErr");

                log.error("setId 이상으로 인한 내 자료 조회 불가 - errCode: setIdErr, setsId: {}", setsId);

                return errorResult;
            }
        }

        result.put("resultOk", true);
        result.put("resultMsg", "성공");

        return result;
    }

    @Transactional
    public Map<String, Object> createMaterialsClassTaks(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<String, Object>();

        /* 중복 방지 */
        int count = tchMaterialsMapper.selecMaterialsClassTasksCount(paramData);

        if (count > 0) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "이미 해당 반에 출제된 이력이 있습니다.");
            return returnMap;
        }

        int result1 = tchMaterialsMapper.createMaterialsClassTaks(paramData);
        log.info("result1:{}", result1);

        returnMap.put("tcMaterialsInfoId", paramData.get("tcMaterialsInfoId"));

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }
        return returnMap;
    }

    public Object findMaterialsClassTasksList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        List<Map> materialsClassTasksList = new ArrayList<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        materialsClassTasksList = tchMaterialsMapper.findMaterialsClassTasksList(pagingParam);

        if (!materialsClassTasksList.isEmpty()) {
            total = (long) materialsClassTasksList.get(0).get("totalCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(materialsClassTasksList, pageable, total);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        returnMap.put("tcMaterialsInfoId", paramData.get("tcMaterialsInfoId"));
        returnMap.put("tcMaterialsInClassTasksList", materialsClassTasksList);
        returnMap.put("page", page);

        return returnMap;
    }

    @Transactional
    public Object updateAndDeleteMaterials(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        try {
            Integer tcMaterialsInfoId = (Integer) paramData.get("tcMaterialsInfoId");
            Integer updateType = (Integer) paramData.get("updateType");
            String userId = (String) paramData.get("userId");
            String name = (String) paramData.get("name");

            if (tcMaterialsInfoId == null || updateType == null || userId == null) {
                returnMap.put("tcMaterialsInfoId", tcMaterialsInfoId);
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "필수 파라미터가 누락되었습니다. 파라미터 다시 확인해주세요.");
                return returnMap;
            }

            /* 내자료 존재 여부 확인 */
            int result1 = tchMaterialsMapper.findIsTchMetrialsYn(paramData);
            log.info("result1:{}", result1);
            if (result1 == 0) {
                returnMap.put("tcMaterialsInfoId", tcMaterialsInfoId);
                returnMap.put("resultOk", false);
                returnMap.put("resultMsg", "존재하지 않는 내 자료입니다.");
                return returnMap;
            }

            Map<String, Object> result = new HashMap<>();
            String message = "";

            /* 수정구분 값에 따른 분기 처리 */
            switch (updateType) {
                case 1: /* 내 자료명 수정 */
                    result = updateMaterialsTitle(tcMaterialsInfoId, name, userId);
                    break;

                case 2: /* 내자료 삭제 */
                    result = deleteMaterials(tcMaterialsInfoId, userId);
                    break;

                default:
                    returnMap.put("tcMaterialsInfoId", tcMaterialsInfoId);
                    returnMap.put("resultOk", false);
                    returnMap.put("resultMsg", "올바르지 않은 수정구분 값입니다. (1:타이틀수정, 2:삭제)");
                    return returnMap;
            }

            message = (String) result.get("resultMsg");

            returnMap.put("tcMaterialsInfoId", tcMaterialsInfoId);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", message);

        } catch (DataAccessException e) {
            // CSAP 보안 취약점 수정 - 구체적인 예외 처리
            log.error("Database error in updateAndDeleteMaterials: {}", CustomLokiLog.errorLog(e));
            returnMap.put("tcMaterialsInfoId", paramData.get("tcMaterialsInfoId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "내 자료 수정 및 삭제 중 데이터베이스 오류가 발생했습니다.");
        } catch (NullPointerException e) {
            log.error("Null pointer in updateAndDeleteMaterials: {}", CustomLokiLog.errorLog(e));
            returnMap.put("tcMaterialsInfoId", paramData.get("tcMaterialsInfoId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "내 자료 수정 및 삭제 중 필수 데이터가 누락되었습니다.");
        } catch (RuntimeException e) {
            log.error("Runtime error in updateAndDeleteMaterials: {}", CustomLokiLog.errorLog(e));
            returnMap.put("tcMaterialsInfoId", paramData.get("tcMaterialsInfoId"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "내 자료 수정 및 삭제 중 오류가 발생했습니다.");
        }

        return returnMap;
    }

    /**
     * 내자료 타이틀 수정
     */
    @Transactional
    public Map<String, Object> updateMaterialsTitle(Integer tcMaterialsInfoId, String name, String userId) throws Exception {
        Map<String, Object> result = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            result.put("resultOk", false);
            result.put("resultMsg", "내 자료명을 입력해주세요.");
            return result;
        }

        try {
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("tcMaterialsInfoId", tcMaterialsInfoId);
            updateParams.put("name", name.trim());
            updateParams.put("userId", userId);

            int updateCount = tchMaterialsMapper.updateMaterialsTitle(updateParams);
            log.info("updateCount:{}", updateCount);

            if (updateCount == 0) {
                result.put("resultOk", false);
                result.put("resultMsg", "내 자료명 수정에 실패했습니다.");
                return result;
            }

            result.put("tcMaterialsInfoId", tcMaterialsInfoId);
            result.put("resultOk", true);
            result.put("resultMsg", "내 자료명이 수정되었습니다.");

        } catch (DataAccessException e) {
            // CSAP 보안 취약점 수정 - 구체적인 예외 처리
            log.error("Database error in updateMaterialsTitle: {}", CustomLokiLog.errorLog(e));
            result.put("resultOk", false);
            result.put("resultMsg", "내 자료명 수정 중 데이터베이스 오류가 발생했습니다.");
        } catch (RuntimeException e) {
            log.error("Runtime error in updateMaterialsTitle: {}", CustomLokiLog.errorLog(e));
            result.put("resultOk", false);
            result.put("resultMsg", "내 자료명 수정 중 오류가 발생했습니다.");
        }

        return result;
    }

    /**
     * 내자료 삭제
     */
    @Transactional
    public Map<String, Object> deleteMaterials(Integer tcMaterialsInfoId, String userId) throws Exception {
        Map<String, Object> result = new HashMap<>();

        try {
            /* 내자료 정보 삭제 */
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("tcMaterialsInfoId", tcMaterialsInfoId);
            deleteParams.put("userId", userId);

            /* 관련 문항들에 대한 상세 정보 먼저 삭제 */
            tchMaterialsMapper.deleteMaterialsDetailByMaterialsInfoId(deleteParams);

            tchMaterialsMapper.deleteMaterialsClassTasksByMaterialsInfoId(deleteParams);

            int deleteCount = tchMaterialsMapper.deleteMaterialsInfo(deleteParams);
            log.info("deleteCount:{}", deleteCount);

            String orgSetsId = tchMaterialsMapper.selectSetsIdByTcMaterialsInfoId(deleteParams);
            deleteParams.put("orgSetsId", orgSetsId);

            /* 수업 자료실에 스크랩한 자료를 내 자료에서 삭제 시, 스크랩 해제하기 위함 (2025.09.24 작업)*/
            int deletedScrapCount = tchMaterialsMapper.deleteScrapInfoBySetsId(deleteParams);
            log.info("deletedScrapCount:{}", deletedScrapCount);

            if (deleteCount == 0) {
                result.put("resultOk", false);
                result.put("resultMsg", "내자료 삭제에 실패했습니다.");
                return result;
            }

            result.put("tcMaterialsInfoId", tcMaterialsInfoId);
            result.put("resultOk", true);
            result.put("resultMsg", "내자료가 삭제되었습니다.");
        } catch (DataAccessException e) {
            // CSAP 보안 취약점 수정 - 구체적인 예외 처리
            log.error("Database error in deleteMaterials: {}", CustomLokiLog.errorLog(e));
            result.put("resultOk", false);
            result.put("resultMsg", "내자료 삭제 중 데이터베이스 오류가 발생했습니다.");
        } catch (RuntimeException e) {
            log.error("Runtime error in deleteMaterials: {}", CustomLokiLog.errorLog(e));
            result.put("resultOk", false);
            result.put("resultMsg", "내자료 삭제 중 오류가 발생했습니다.");
        }

        return result;
    }
}
