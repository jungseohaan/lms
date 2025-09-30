package com.visang.aidt.lms.api.selflrn.service;

import com.visang.aidt.lms.api.selflrn.mapper.StntStdAiMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class StntStdAiService {
    private final StntStdAiMapper stntStdAiMapper;
    private final StntRewardService stntRewardService;
    private final StntWrongnoteMapper stntWrongnoteMapper;


    public Object findStntStdAiInit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        String resultMsg = "";
        boolean bStdAt = false;
        List<String> aiStdResultInfoItem = Arrays.asList("id", "stdNm", "stdAiId", "moduleId", "stdUsdId");

        //재 진입 시 새로운 ai 학습 출제
        //Map<String, Object> slfAiStdInfoMap = stntStdAiMapper.findStntStdAi_slfAiStdInfo(paramData);

        //if (ObjectUtils.isNotEmpty(slfAiStdInfoMap)) {
        //bStdAt = true;
        //} else {

        int result1 = stntStdAiMapper.modifyStntStdAiInit_edAt(paramData);
        log.info("result1:{}", result1);

        List<Map<String, Object>> stdUsdInfoList = stntStdAiMapper.findStntStdAi_stdUsdInfo(paramData);

        if (ObjectUtils.isEmpty(stdUsdInfoList)) {
            bStdAt = true;
            resultMsg = "학습이해도정보가 없습니다.";
        } else {
            Map<String, Object> stdUsdInfoMap = stdUsdInfoList.get(0);
            stdUsdInfoMap.putAll(paramData);

            Map<String, Object> newArticleIdMap = stntStdAiMapper.findStntStdAi_newArticleId(stdUsdInfoMap);

            if (ObjectUtils.isEmpty(newArticleIdMap)) {
                bStdAt = true;
                resultMsg = "출제 가능한 문항 이 없습니다.";
            } else {
                stdUsdInfoMap.putAll(newArticleIdMap);
                stdUsdInfoMap.put("smExmAt", "N");

                stntStdAiMapper.createStntStdAi_slfAiStdInfo(stdUsdInfoMap);
                stntStdAiMapper.createStntStdAi_aiStdResultInfo(stdUsdInfoMap);

                Map<String, Object> aiStdResultInfoMap = stntStdAiMapper.findStntStdAi_aiStdResultInfo(stdUsdInfoMap);

                returnMap = AidtCommonUtil.filterToMap(aiStdResultInfoItem, aiStdResultInfoMap);
            }
        }
        //}

        if (bStdAt) {
            returnMap.put("id", "");
            returnMap.put("stdAiId", "");
            returnMap.put("moduleId", "");
            returnMap.put("stdUsdId", "");
            returnMap.put("resultMsg", resultMsg);
        }

        return returnMap;
    }


    public Object findStntStdAiSubmit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> aiStdResultInfoItem = Arrays.asList("id", "stdNm", "stdAiId", "moduleId", "stdUsdId");
        Map<String, Object> aiStdResultInfoReturnMap = null;
        Map<String, Object> rewardResult = null;

        int result1 = stntStdAiMapper.modifyStntStdAi_aiStdResultInfo(paramData);

        if (result1 > 0 && MapUtils.getInteger(paramData, "errata") == 1) {
            Map<String, Object> rwdMap = new HashMap<>();

            rwdMap.put("userId", paramData.get("userId"));
            rwdMap.put("claId", paramData.get("claId"));
            rwdMap.put("seCd", "1");
            rwdMap.put("menuSeCd", "4");
            rwdMap.put("sveSeCd", "5");
            rwdMap.put("trgtId", paramData.get("id"));
            rwdMap.put("textbkId", paramData.get("textbookId"));
            rwdMap.put("rwdSeCd", "1");
            rwdMap.put("rwdAmt", 100);
            rwdMap.put("rwdUseAmt", 0);

            rewardResult = stntRewardService.createReward(rwdMap);
        }

        List<Map<String, Object>> stdUsdInfoList = stntStdAiMapper.findStntStdAi_stdUsdInfo(paramData);
        Map<String, Object> slfAiStdInfoMap = stntStdAiMapper.findStntStdAi_slfAiStdInfo(paramData);

        boolean bStdAt = false;
        if (ObjectUtils.isEmpty(slfAiStdInfoMap) || ObjectUtils.isEmpty(stdUsdInfoList)) {
            bStdAt = true;
        } else {
            for (Map<String, Object> stdUsdInfoMap : stdUsdInfoList) {
                Integer srcResultInfoId = 0;
                Integer beLvNum = 0; // 이전문항 난이도
                Integer beErrata = 0; //이전문항 정요표
                Integer errtaCount = 0; // 정오표가 1이 아닐때 1씩증가 (틀횟수)
                Integer newArticle = 0; // 새로운 문항의 난이도를 정할때 사용 " beLvNum + newArticle = 새로운문항의 난이도를 정할대 사용

                boolean lvDown = false; // 난이도를 내려야 되는지 여부
                int lvDownCount = 0;  // 2개이상이면 lvDdown = endSign 이 true가 되면서 다음 성취도로 .....
                int topLvCount = 0;

                boolean endSign = false;  // false : 현재 성취도의 문항을 출제, ture : 다음성취도
                int stdUsdId =  MapUtils.getIntValue(stdUsdInfoMap, "stdUsdId", 0);

                stdUsdInfoMap.putAll(paramData);
                stdUsdInfoMap.put("stdUsdId", stdUsdId);
                stdUsdInfoMap.put("smExmAt", "N");
                stdUsdInfoMap.put("srcResultInfoId", null);
                List<Map<String, Object>> aiStdResultInfoList = stntStdAiMapper.findStntStdAi_slfAiStdInfo_aiStdResultInfo(stdUsdInfoMap);

                for (int i = 0; i < aiStdResultInfoList.size(); i++) {
                    var aiStdResultInfoMap = aiStdResultInfoList.get(i);
                    Integer lvNum = MapUtils.getInteger(aiStdResultInfoMap, "lvNum");
                    Integer errata = MapUtils.getInteger(aiStdResultInfoMap, "errata");
                    srcResultInfoId = MapUtils.getInteger(aiStdResultInfoMap, "id");

                    if (lvDown) {
                        lvDownCount = lvDownCount + 1;
                    }

                    if (lvNum == 5) {
                        topLvCount = topLvCount + 1;
                    }

                    if (beLvNum != lvNum) {
                        errtaCount = 0;
                        newArticle = 0;
                        beErrata = 0;
                    }

                    if (lvDownCount == 2 || topLvCount == 2) {
                        endSign = true;
                    } else {
                        if (beLvNum == lvNum) {
                            if (errata == 1) {
                                if (beErrata == 1) {
                                    newArticle = 1;
                                }
                            } else {
                                errtaCount = errtaCount + 1;
                                if (errtaCount == 3 || beErrata != 1) {
                                    newArticle = -1;
                                    lvDown = true;
                                }
                            }
                        }

                        if (errata != 1 && newArticle == 0) {
                            stdUsdInfoMap.put("smExmAt", "Y");
                            stdUsdInfoMap.put("srcResultInfoId", srcResultInfoId);

                            Map<String, Object> studyMapTwoMap = stntStdAiMapper.findStntStdAi_studyMapTwo(aiStdResultInfoMap);
                            stdUsdInfoMap.put("studyMapTwoId", MapUtils.getInteger(studyMapTwoMap, "studyMapTwoId"));
                        } else {
                            stdUsdInfoMap.put("smExmAt", "N");
                            stdUsdInfoMap.put("srcResultInfoId", null);
                            stdUsdInfoMap.put("studyMapTwoId", null);
                        }
                    }

                    beLvNum = lvNum;
                    beErrata = errata;
                }

                if (!endSign) {
                    Integer newLv = beLvNum + newArticle;

                    if (ObjectUtils.isEmpty(aiStdResultInfoList)) {
                        newLv = Integer.parseInt(MapUtils.getString(stdUsdInfoMap, "lvlId").substring(2,3));
                    }

                    if (newLv == 1 || newLv == 0) {
                        //stdUsdInfoMap.put("lvlNm", "Lv1. 이해");
                        stdUsdInfoMap.put("lvlNm", "하");
                        stdUsdInfoMap.put("lvlId", "lv1");
                    } else if (newLv == 2) {
                        //stdUsdInfoMap.put("lvlNm", "Lv2. 적용");
                        stdUsdInfoMap.put("lvlNm", "중하");
                        stdUsdInfoMap.put("lvlId", "lv2");
                    } else if (newLv == 3) {
                        //stdUsdInfoMap.put("lvlNm", "Lv3. 응용");
                        stdUsdInfoMap.put("lvlNm", "중");
                        stdUsdInfoMap.put("lvlId", "lv3");
                    } else if (newLv == 4) {
                        //stdUsdInfoMap.put("lvlNm", "Lv4. 발전");
                        stdUsdInfoMap.put("lvlNm", "중상");
                        stdUsdInfoMap.put("lvlId", "lv4");
                    } else if (newLv == 5) {
                        //stdUsdInfoMap.put("lvlNm", "Lv5. 심화");
                        stdUsdInfoMap.put("lvlNm", "상");
                        stdUsdInfoMap.put("lvlId", "lv5");
                    }
                    Map<String, Object> newArticleIdMap = stntStdAiMapper.findStntStdAi_newArticleId(stdUsdInfoMap);

                    if (ObjectUtils.isEmpty(newArticleIdMap)) {
                        endSign = true;
                    } else {
                        stdUsdInfoMap.putAll(newArticleIdMap);
                        stdUsdInfoMap.put("id", MapUtils.getInteger(paramData, "stdAiId"));
                        stntStdAiMapper.createStntStdAi_aiStdResultInfo(stdUsdInfoMap);

                        aiStdResultInfoReturnMap = stntStdAiMapper.findStntStdAi_aiStdResultInfo(stdUsdInfoMap);
                    }
                }

                if (!endSign) {
                    break;
                }
            }
        }

        if (ObjectUtils.isEmpty(aiStdResultInfoReturnMap)) {
            returnMap.put("stdAt", "N");
            returnMap.put("newAiStd", null);
            stntStdAiMapper.modifyStntStdAi_aiStdInfo_edAt(paramData);
        } else {
            returnMap.put("stdAt", "Y");
            returnMap.put("newAiStd", AidtCommonUtil.filterToMap(aiStdResultInfoItem, aiStdResultInfoReturnMap));
        }

        if (MapUtils.isEmpty(rewardResult)) {
            returnMap.put("rwd" , 0);
        } else {
            returnMap.put("rwd", MapUtils.getInteger(stntStdAiMapper.findStntStdAi_rwdEarnHist(rewardResult), "rwdAmt"));
        }

        returnMap.put("totalRwd", MapUtils.getInteger(stntStdAiMapper.findStntStdAi_rwdEarnHist_total(paramData), "rwdAmtTotal"));

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public Object findStntStdAiEnd(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = stntStdAiMapper.modifyStntStdAi_aiStdInfo_edAt(paramData);

        List<Map<String, Object>> slfAiStdResultInfoList = stntStdAiMapper.findSlfAiStdResultInfo(paramData);
        log.info("slfAiStdResultInfoList.size:{}", slfAiStdResultInfoList.size());

        for (Map<String, Object> map : slfAiStdResultInfoList) {
            map.put("wrterId", MapUtils.getString(paramData, "userId"));
            map.put("wonAnwClsfCd", 2);
            map.put("tabId", null);
            map.put("wonTag", null);

            // 등록된 오답노트 여부 조회
            int wonAswNoteCnt = stntWrongnoteMapper.findWonAswNoteCount(map);
            log.info("checkWonAswNoteCnt:{}", wonAswNoteCnt);

            if (wonAswNoteCnt == 0) {
                int createWonAswNoteCnt = stntWrongnoteMapper.createWonAswNote(map);
                log.info("createWonAswNoteCnt:{}", createWonAswNoteCnt);
            }
        }

        if (result1 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSelflrnAiResult(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        returnMap.put("claId", paramData.get("claId"));
        returnMap.put("textbookId", paramData.get("textbookId"));
        returnMap.put("unitNum", paramData.get("unitNum"));
        returnMap.put("stdList", stntStdAiMapper.findStntSelfStdInfoList(paramData));
        return returnMap;
    }
}
