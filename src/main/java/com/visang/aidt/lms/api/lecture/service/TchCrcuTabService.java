package com.visang.aidt.lms.api.lecture.service;

import com.visang.aidt.lms.api.assessment.mapper.TchEvalMapper;
import com.visang.aidt.lms.api.lecture.mapper.TchCrcuTabMapper;
import com.visang.aidt.lms.api.materials.mapper.TchStdMapper;
import com.visang.aidt.lms.api.repository.SetsRepository;
import com.visang.aidt.lms.api.repository.TabInfoRepository;
import com.visang.aidt.lms.api.repository.dto.*;
import com.visang.aidt.lms.api.repository.entity.SetsEntity;
import com.visang.aidt.lms.api.repository.entity.TabInfoEntity;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
//@AllArgsConstructor
@RequiredArgsConstructor
public class TchCrcuTabService {
    private final SetsRepository setsRepository;

    private final TabInfoRepository tabInfoRepository;

    private final TchCrcuTabMapper tchCrcuTabMapper;

    private final TchStdMapper tchStdMapper;
    private final TchEvalMapper tchEvalMapper;


    /**
     * (차시).탭 활성/비활성화 처리
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> modifyCrcuTabAvailable(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();

        int result = tchCrcuTabMapper.updateCrcuTabAvailable(paramData);
        Map<String, Object> resultMap = new HashMap<>();

        if(result > 0) {
            resultMap.putAll(paramData);
        } else {
            resultMap.put("result", "FAIL: 데이터가 존재하지 않습니다.");
        }

        rtnMap.put("tabInfo", resultMap);
        return rtnMap;
    }

    /**
     * (차시).탭의 모듈 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(transactionManager = "transactionManager")
    public Map<String, Object> findCrcuTabMdulList(Map<String, Object> paramData) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();

        Long tabId = (Long)paramData.get("tabId");

        TabInfoEntity tab = tabInfoRepository.findById(tabId).orElseThrow(() -> new IllegalArgumentException("tabInfo doesn't exist"));

        // 탭 기본정보 포함인 경우
        if(paramData.containsKey("isIncludeTabInfo")) {
            rtnMap.put("id", tab.getId());
            rtnMap.put("tabNm", tab.getTabNm());
            rtnMap.put("useAt", tab.getUseAt()); // 사용여부
            rtnMap.put("exposAt", tab.getExposAt()); // 노출여부
        }

        // 셋트지 정보 조회
        SetsEntity sets = setsRepository.findSetsEntityById(tab.getSet().getId()).orElseThrow(() -> new IllegalArgumentException("sets doesn't exist"));
        SetsDTO setsDTO = SetsDTO.toDTO(sets);
        // 셋트지 아티클(모듈) 목록 설정
        CollectionUtils.emptyIfNull(sets.getSetsArticleMapList())
                .stream()
                .map(setsArticleMapEntity -> setsArticleMapEntity.getArticle())
                .forEach(article -> {
                    ArticleDTO articleDTO = ArticleDTO.toDTO(article);
                    // Meta 정보 설정
                    articleDTO.setMetaList(
                            CollectionUtils.emptyIfNull(article.getArticleMetaMapList())
                                    .stream()
                                    .map(articleMetaMapEntity -> articleMetaMapEntity.getMeta())
                                    .map(meta -> MetaDTO.toDTO(meta))
                                    .collect(Collectors.toList())
                    );
                    // Library 정보 설정
                    articleDTO.setLibraryList(
                            CollectionUtils.emptyIfNull(article.getLibraryArticleMapList())
                                    .stream()
                                    .map(libraryArticleMapEntity -> libraryArticleMapEntity.getLibrary())
                                    .map(library -> LibraryDTO.toDTO(library))
                                    .collect(Collectors.toList())
                    );
                    // Libtext 정보 설정
                    articleDTO.setLibtextList(
                            CollectionUtils.emptyIfNull(article.getLibtextArticleMapList())
                                    .stream()
                                    .map(libtextArticleMapEntity -> libtextArticleMapEntity.getLibtext())
                                    .map(libtext -> LibtextDTO.toDTO(libtext))
                                    .collect(Collectors.toList())
                    );

                    setsDTO.addArticle(articleDTO);
                });

        rtnMap.put("set", setsDTO);

        return rtnMap;
    }

    /**
     * (차시).탭별 학생들의 모듈 진행 상황 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    public Map<String, Object> findCrcuTabMdulStatus(Map<String, Object> paramData) throws Exception {
        return new JSONObject("""
            {   "id": 1,
                "info": "dummy",
            }
        """).toMap();
    }

    /**
     * 탭 기본정보 조회
     *
     * @param paramData 입력 파라미터
     * @return
     */
    public Map<String, Object> findCrcuTabInfo(Map<String, Object> paramData) throws Exception {
        Map<String,Object> rtnMap = new HashMap<>();

        Map<String,Object> result = tchCrcuTabMapper.findCrcuTabInfo(paramData);

        rtnMap.put("tabInfo", result);
        return rtnMap;
    }

    public Object modifyTchCrcuTabSave(Map<String, Object> paramData) {
        var returnMap = new LinkedHashMap<>();

        List<Map<String, Object>> tabList = (List<Map<String, Object>>) paramData.get("TabList");
        int updateCrcuTabSaveInfo = 0;
        if(!ObjectUtils.isEmpty(tabList)) {
            for (Map<String, Object> tabMap : tabList){
                updateCrcuTabSaveInfo = tchCrcuTabMapper.modifyTchCrcuTabSave(tabMap);
            }
        }

        if (updateCrcuTabSaveInfo > 0 ) {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }
        return returnMap;
    }

    public Object modifyTchCrcuTabChginfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        returnMap.put("tabId", paramData.get("tabId"));
        try {
            returnMap.put("resultOK", true);
            returnMap.put("resultMsg", "성공");

            //탭의 세트지 변경에 따른 관련 테이블 정보 수정 처리할때 조건추가 및 수정
            //Map<String, Object> stdDtaInfoMap = tchCrcuTabMapper.findStdDtaInfoByTabId(paramData);
            //std_dta_info select (eam_mth: 직접출제(3))
            /*
            if (ObjectUtils.isEmpty(stdDtaInfoMap)) {
                returnMap.put("resultOK", false);
                returnMap.put("resultMsg", "직접출제된 학습자료정보가 없습니다.");
                return returnMap;
            }
            */

            // 수정 가능여부 조회
            // 학습이력 존재하지않고 직접출제(3)인 경우 탭 편집가능
            // 또는 학습이력 존재하지않고 문항자동생성(2)-공통문항출제(1)인 경우 탭 편집가능
            // /* 출제방법, null 이면 직접출제(3)로 처리 */, /* 출제대상, null 이면 공통문항출제(1)로 처리 */
            Map<String, Object> findTabEditableMap = tchCrcuTabMapper.findTabEditable(paramData);

            if ("N".equals(MapUtils.getString(findTabEditableMap, "editable", "N"))) {
                returnMap.put("resultOK", false);
                returnMap.put("resultMsg", "수정 가능한 탭이 존재하지 않습니다.");
                return returnMap;
            }

            //tab_info update
            int modifyCrcuTabSaveInfo = tchCrcuTabMapper.modifyTchCrcuTabChginfo(paramData);
            log.info("modifyCrcuTabSaveInfo:{}", modifyCrcuTabSaveInfo);
            if (modifyCrcuTabSaveInfo == 0) {
                returnMap.put("resultOK", false);
                returnMap.put("resultMsg", "탭 정보가 존재하지 않습니다.");
                return returnMap;
            }

            //std_dta_info update
            int modifyStdDtaInfoCnt = tchCrcuTabMapper.modifyTchCrcuTabChginfo_stdDtaInfo(paramData);
            log.info("modifyStdDtaInfoCnt:{}", modifyStdDtaInfoCnt);

            //tab_id 기준으로 처리
            //std_dta_result_info update
            int modifyStdDtaResultInfoCnt = tchCrcuTabMapper.modifyTchCrcuTabChginfo_stdDtaResultInfo(paramData);
            log.info("modifyStdDtaResultInfoCnt:{}", modifyStdDtaResultInfoCnt);

            //std_dta_result_detail delete
            int removeStdDtaResultDetailCnt = tchCrcuTabMapper.removeTchStdSaveSDRD(paramData);
            log.info("removeStdDtaResultDetailCnt:{}", removeStdDtaResultDetailCnt);

            //std_dta_result_detail insert
            int createStdDtaResultDetailCnt = tchCrcuTabMapper.createTchStdSaveSDRD(paramData);
            log.info("createStdDtaResultDetailCnt:{}", createStdDtaResultDetailCnt);

            paramData.put("wrterId", MapUtils.getString(paramData, "userId"));
            tchEvalMapper.increaseModuleUseCnt(paramData);
            paramData.remove("wrterId");

            //학습자료정보(std_dta_info) 의 bbs_sv_at = 'Y' 이면 세트지 등록 처리로직을 수행
            Map<String, Object> stdDtaInfoMap = tchCrcuTabMapper.findStdDtaInfoByTabId(paramData);
            if (ObjectUtils.isNotEmpty(stdDtaInfoMap)) {
                int stdDtaId = MapUtils.getInteger(stdDtaInfoMap, "id");
                stdDtaInfoMap.put("stdId", stdDtaId);
                stdDtaInfoMap.put("userId", MapUtils.getString(paramData, "userId"));
                Map<String, Object> setsInsertParamMap = new HashMap<>();
                //bbsSvAt is Y
                if ("Y".equals(stdDtaInfoMap.get("bbsSvAt"))) {
                    //delete sets tables
                    int removeSetsTablesCnt = tchCrcuTabMapper.removeTchCrcuTabChginfo_setsTables(stdDtaInfoMap);
                    log.info("removeSetsTablesCnt:{}", removeSetsTablesCnt);

                    int removeSetsCnt = tchCrcuTabMapper.removeTchCrcuTabChginfo_sets(stdDtaInfoMap);
                    log.info("removeSetsCnt:{}", removeSetsCnt);

                    //insert sets tables
                    int result0 = tchStdMapper.createTchStdSaveSets(stdDtaInfoMap);
                    log.info("result0:{}", result0);

                    setsInsertParamMap.put("newSetsid", MapUtils.getString(stdDtaInfoMap, "newSetsid"));
                    setsInsertParamMap.put("oldSetsId", stdDtaInfoMap.get("setsId"));

                    int result2 = tchEvalMapper.createTchEvalSaveSAM(setsInsertParamMap);
                    log.info("result2:{}", result2);

                    int result3 = tchEvalMapper.createTchEvalSaveSKM(setsInsertParamMap);
                    log.info("result3:{}", result3);

                    int result4 = tchEvalMapper.createTchEvalSaveSMM(setsInsertParamMap);
                    log.info("result4:{}", result4);

                    int result6 = tchEvalMapper.createTchEvalSaveSummary(setsInsertParamMap);
                    log.info("result6:{}", result6);

                    setsInsertParamMap.put("stdId", stdDtaId);
                    int result5 = tchStdMapper.modifyTchStdSaveBbsSetId(setsInsertParamMap);
                    log.info("result5:{}", result5);
                }

                //aidt_lms.std_dta_info.eam_exm_num
                int result11 =  tchStdMapper.modifyTchStdSaveEEN(stdDtaInfoMap);
            }
        } catch(Exception e) {
//            e.printStackTrace();
            CustomLokiLog.errorLog(e);
            returnMap.put("resultOK", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    /* e북 페이지에 해당하는 탭 정보 조회 */
    public Map<String, Object> findCrcuEbookTabInfo(Map<String, Object> paramData) throws Exception {
        return tchCrcuTabMapper.findCrcuEbookTabInfo(paramData);
    }
}
