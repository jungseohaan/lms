package com.visang.aidt.lms.api.scenario.service;

import com.visang.aidt.lms.api.assessment.mapper.StntEvalMapper;
import com.visang.aidt.lms.api.assessment.mapper.TchEvalMapper;
import com.visang.aidt.lms.api.engvocal.service.StntMdulVocalScrService;
import com.visang.aidt.lms.api.materials.mapper.StntMdulQstnMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.scenario.mapper.ScenarioMapper;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnEngMapper;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnMapper;
import com.visang.aidt.lms.api.selflrn.service.StntSelfLrnEngService;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import com.visang.aidt.lms.api.wrongnote.service.StntWrongnoteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Slf4j
@AllArgsConstructor
public class ScenarioService {

    private final ScenarioMapper scenarioMapper;

    private final TchEvalMapper tchEvalMapper;
    private final StntEvalMapper stntEvalMapper;
    private final StntRewardService stntRewardService;
    private final StntMdulQstnMapper stntMdulQstnMapper;
    private final StntMdulVocalScrService stntMdulVocalScrService;
    private final StntWrongnoteMapper stntWrongnoteMapper;
    private final StntWrongnoteService stntWrongnoteService;
    private final StntSelfLrnMapper stntSelfLrnMapper;
    private final StntNtcnService stntNtcnService;
    private final StntSelfLrnEngMapper stntSelfLrnEngMapper;

    public Object modifyStntEvalSubmit(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> evalInfoItem = Arrays.asList("id", "setsId", "eakSttsCd", "eakSttsNm", "submAt", "eakStDt", "eakEdDt", "rwdSetAt", "rwdPoint", "edGidAt", "edGidDc");

        // 제출여부(submAt) 파라미터 디폴트값 셋팅
        String submAt = MapUtils.getString(paramData, "submAt");
        paramData.put("submAt", StringUtils.isEmpty(submAt) ? "Y" : submAt);
//        paramData.put("submAt", StringUtils.isEmpty(submAt) || submAt.equals("Y") ? "Y" : "N");

        var evlInfoMap = tchEvalMapper.findEvlInfo(paramData);

        int result3 = 0;
        int resultDetail = 0;

        // 2024-06-17. 타이머 마감 또는 기한 마감인 경우와 상관없이
        // 과제 응시 시작한 학생의 경우 무조건 제출하기 처리로 프로세스 변경 (최지연 CP님)
        /*
        boolean deadline = "Y".equals(MapUtils.getString(paramData, "submAt"))
                        && "Y".equals(MapUtils.getString(evlInfoMap, "pdSetAt"))
                        && MapUtils.getInteger(evlInfoMap, "deadline") == 1;
        */
        boolean deadline = false;

        if ("N".equals(paramData.get("submAt")) || deadline) {
            if (null == paramData.get("userId") || "".equals(paramData.get("userId"))) {
                paramData.put("userId", " ");
            }

            result3 = scenarioMapper.modifyEvalSubmAtERD(paramData);
            log.info("result3:{}", result3);
            int result4 = scenarioMapper.modifyEvalSubmAtERI(paramData);
            log.info("result4:{}", result4);
        } else {
            resultDetail = scenarioMapper.modifyStntEvalSubmitResultDetail(paramData);
            log.info("resultDetail:{}", resultDetail);

            Map<String, Object> resultDetailCntMap = stntEvalMapper.selectStntEvalSubmitResultDetailCnt(paramData);

            if (MapUtils.getInteger(resultDetailCntMap, "eakSttsCdCnt") == 0) {
                resultDetailCntMap.put("eakSttsCd", 5);
                resultDetailCntMap.put("mrkCpAt", "Y");

            } else {
                resultDetailCntMap.put("eakSttsCd", 3);
                resultDetailCntMap.put("mrkCpAt", "N");
            }
            resultDetailCntMap.put("evlId", paramData.get("evlId"));
            resultDetailCntMap.put("userId", paramData.get("userId"));
            resultDetailCntMap.put("submAt", paramData.get("submAt"));
            resultDetailCntMap.put("regDate", paramData.get("regDate"));
            int result1 = scenarioMapper.modifyStntEvalSubmitResultInfo(resultDetailCntMap);
            log.info("result1:{}", result1);

            //리워드
            //모든 문제를 푼 경우에는 resultDetail = 0 임.
            //if (resultDetail > 0) {
            Map<String, Object> rwdMap = new HashMap<>();

            rwdMap.put("userId", paramData.get("userId"));
            rwdMap.put("claId", evlInfoMap.get("claId"));
            rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
            rwdMap.put("menuSeCd", "3"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝
            rwdMap.put("sveSeCd", "4"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트
            rwdMap.put("trgtId", paramData.get("evlId")); //대상ID
            rwdMap.put("textbkId", paramData.get("textbookId"));
            rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
            rwdMap.put("rwdAmt", 10); //지급일때는 0
            rwdMap.put("rwdUseAmt", 0); //지급일때는 0

            Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);


            //}
        }

        // 평가 마스터 정보 상태 변경 (배치처리 X, 원복처리)
        // 2024-05-22: 평가 마스터 상태 처리 제외
        //int result2 = stntEvalMapper.modifyStntEvalSubmitEvlInfo(paramData);
        //log.info("result2:{}", result2);

        if  (deadline) {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "제출 기한이 마감되어 제출할 수 없습니다.");
        } else if (resultDetail > 0 || result3 > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        }

        returnMap.putAll(AidtCommonUtil.filterToMap(evalInfoItem, stntEvalMapper.findTchEvalSubmit(paramData)));

        List<String> StntResultInfoItem = Arrays.asList("evlStdrSet", "evlStdrSetNm", "evlResult", "evlResultScr", "evlTotalScr");

        returnMap.put("stntResultInfo", AidtCommonUtil.filterToMap(StntResultInfoItem, stntEvalMapper.findStntResultInfo(paramData)));

        scenarioMapper.updateEvalUptDt(paramData);

        return returnMap;
    }

    public Object modifyEvalEnd(Map<String, Object> paramData) throws Exception {
        List<String> evalInfoItem = Arrays.asList("id", "evlNm", "evlPrgDt", "evlCpDt", "evlSttsCd", "evlSttsNm");

        // 타임아웃여부(timeoutAt) 파라미터 디폴트값 셋팅
        String timeoutAt = MapUtils.getString(paramData, "timeoutAt");
        paramData.put("timeoutAt", StringUtils.isEmpty(timeoutAt) ? "N" : timeoutAt);

        //Optional<EvlInfoEntity> eEvlInfoEntity = evlInfoRepository.findById(Long.parseLong(String.valueOf(paramData.get("evlId"))));
        //paramData.put("wrterId", eEvlInfoEntity.get().getWrterId());

        //int result2 =  tchEvalMapper.modifyTchEvalEndEvalResultInfo(paramData);
        //log.info("result2:{}", result2);

        // 2024-04-24 (확인: 키인스 - 김명수 책임님)
        // 교사-[종료하기] 클릭시 학생-[제출하기]를 현재 처리하고 있다고 함.
        // 미제출자 처리로직 주석처리 (배치에서 미제출자 처리하기 때문에 약간의 시간차가 발생할 수 있음)
        /*
        if ("N".equals(paramData.get("timeoutAt"))) {
            int result3 = tchEvalMapper.modifyEvalSubmAtERD(paramData);
            log.info("result3:{}", result3);
            int result4 = tchEvalMapper.modifyEvalSubmAtERI(paramData);
            log.info("result4:{}", result4);
        }*/

        // 평가 마스터 정보 상태 수정
        int result1 =  tchEvalMapper.modifyTchEvalEndEvalInfo(paramData);
        log.info("result1:{}", result1);

        LinkedHashMap<Object, Object> evalInfoMap = AidtCommonUtil.filterToMap(evalInfoItem, tchEvalMapper.findTchEvalEndEvalInfo(paramData));
        evalInfoMap.put("evlPrgDt", AidtCommonUtil.stringToDateFormat((String) evalInfoMap.get("evlPrgDt"),"yyyy-MM-dd HH:mm:ss"));
        evalInfoMap.put("evlCpDt", AidtCommonUtil.stringToDateFormat((String) evalInfoMap.get("evlCpDt"),"yyyy-MM-dd HH:mm:ss"));

        scenarioMapper.updateEvalEndUptDt(paramData);

        return evalInfoMap;
    }

    // 평가 목록 조회
    @Transactional(readOnly = true)
    public Object findEvalList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> evalCheckItem = Arrays.asList("plnEvlCnt", "pgEvlCnt", "cpEvlCnt");
        List<String> evalInfoItem = Arrays.asList("no", "id", "evlNm", "evlPrgDt", "evlCpDt", "evlSttsCd", "evlSttsNm", "eakSttsCd", "eakSttsNm", "rptOthbcAt", "setsId", "submAt", "slfSubmAt", "perSubmAt", "slfPerSubmAt");

        LinkedHashMap<Object, Object> evalCheckMap = AidtCommonUtil.filterToMap(evalCheckItem, stntEvalMapper.findStntEvalListEvalCheck(paramData));

        long total = 0;

        List<Map> evalInfoList = (List<Map>) scenarioMapper.findStntEvalListEvalInfo(paramData);

        returnMap.put("evalCheck", evalCheckMap);
        returnMap.put("evalList", AidtCommonUtil.filterToList(evalInfoItem, evalInfoList));

        return returnMap;
    }


    public Object modifyStntMdulQstnSave(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        int result1 = scenarioMapper.modifyStntMdulQstnSave(paramData);
        log.info("result1:{}", result1);

        returnMap.put("resultDetailId", paramData.get("resultDetailId"));

        if(result1 > 0) {
            List<Map> stntMdulQstnList = stntMdulQstnMapper.findStntMdulQstnInfo(paramData);
            if(!ObjectUtils.isEmpty(stntMdulQstnList)) {
                for(Map<String,Object> map : stntMdulQstnList) {
                    int mrkTy  = MapUtils.getInteger(map, "mrkTy"); // 1:자동채점, 2:수동채점
                    int errata = MapUtils.getInteger(map, "errata");

                    // 2024-06-18
                    // [영어] 발음평가형 처리
                    String questionType = MapUtils.getString(map, "questionType");
                    // questionType - ptqz (발음평가형) 인 경우
                    if("ptqz".equals(questionType) || "wcom".equals(questionType)) {
                        String menuSeCd  = "1"; // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                        int trgtId       = MapUtils.getInteger(paramData,"resultDetailId");
                        String subMitAnw = MapUtils.getString(paramData, "subMitAnw");
                        // 발성평가 점수 등록 처리
                        stntMdulVocalScrService.saveVocalEvlScrInfo(menuSeCd, trgtId, subMitAnw);
                    }

                    // 수동채점유형 이거나 자동채점유형이면서 정답인 경우 리워드 발급대상임.
                    boolean isRewardTarget = (mrkTy == 2 || (mrkTy == 1 && errata == 1)) ? true : false;
                    if (isRewardTarget) {
                        Map<String, Object> rwdMap = new HashMap<>();

                        if(!"0".equals(map.get("srcDetailId").toString()) && "Y".equals(map.get("smExmAt"))){
                            // 다른문제풀기
//                                rwdMap.put("rwdAmt", 5); //지급    정책 테이블에서 넣어주고 있음
                            rwdMap.put("sveSeCd", "7");  //서비스구분 - 1:문제, 2:활동, 5:AI학습, 6:선택학습, 7:다른문제풀기
                        } else {
                            // 문제풀기
//                                rwdMap.put("rwdAmt", 1); //지급
                            rwdMap.put("sveSeCd", (mrkTy == 2) ? "2" : "1");  //서비스구분 - 1:문제, 2:활동, 5:AI학습, 6:선택학습, 7:다른문제풀기
                        }
                        rwdMap.put("userId", map.get("userId"));
                        rwdMap.put("claId", map.get("claId"));
                        rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                        rwdMap.put("menuSeCd", "1"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝
                        rwdMap.put("trgtId", map.get("trgtId")); //대상ID
                        rwdMap.put("textbkId", map.get("textbkId"));
                        rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                        rwdMap.put("rwdUseAmt", 0); //획득 0
                        Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);
                        log.error("결과 : " + rewardResult.get("resultOk").toString());
                    }
                }
            }

            int errata = MapUtils.getIntValue(paramData, "errata");

            if (errata == 2 || errata == 3) {
                Map<String, Object> findStdDtaResultDetailMap = stntMdulQstnMapper.findStdDtaResultDetail(paramData);

                findStdDtaResultDetailMap.put("wonAnwClsfCd", 1);

                // 등록된 오답노트 여부 조회
                int wonAswNoteCnt = stntWrongnoteMapper.findWonAswNoteCount(findStdDtaResultDetailMap);
                log.info("checkWonAswNoteCnt:{}", wonAswNoteCnt);

                if (wonAswNoteCnt == 0) {
                    String wonAnwNm = stntWrongnoteService.getWonAnwNm(findStdDtaResultDetailMap);
                    findStdDtaResultDetailMap.put("wonAnwNm", wonAnwNm);

                    int createWonAswNoteCnt = stntWrongnoteMapper.createWonAswNote(findStdDtaResultDetailMap);
                    log.info("createWonAswNoteCnt:{}", createWonAswNoteCnt);
                }
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

    public Map saveStntSelfLrnEnd(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int cnt = 0;

            if(paramData != null && paramData.get("edAt") != null && "Y".equals(paramData.get("edAt").toString())){
                cnt = stntSelfLrnMapper.saveStntSelfLrnEnd(paramData);
            }
            cnt = scenarioMapper.saveStntSelfLrnStdEnd(paramData);
            /*
            if(cnt <= 0) {
                resultMap.put("slfId", paramData.get("slfId"));
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "실패");
                resultMap.put("resultErr", "Not Found Modifiy Data: " + cnt);
                //throw new AidtException("Not Found Modifiy Data: " + cnt);

                return resultMap;
            }*/

            //자기주도학습 학습시간 조회
            Map<String, Object> stdTimeMap = stntSelfLrnMapper.getDeleteStntSelfLrnEnd(paramData);
            if("00:00:00".equals(stdTimeMap.get("stdTime").toString())) {
                int deleteResult1 = stntSelfLrnMapper.deleteStntSelfLrnEnd_info(paramData);
                int deleteResult2 = stntSelfLrnMapper.deleteStntSelfLrnEnd_result_info(paramData);
            } else {
                // 오답노트 등록
                int resultCnt = stntWrongnoteMapper.insertStntWrongnoteId(paramData);

                if(resultCnt > 0){
                    List<Map> wrongnoteTaskInfoInfo = stntWrongnoteMapper.findWrongnoteSelfLrnInfo(paramData);
                    for(Map temp : wrongnoteTaskInfoInfo) {
                        Map<String, Object> ntcnMap = new HashMap<>();
                        ntcnMap.put("userId", temp.get("wrterId"));
                        ntcnMap.put("rcveId", temp.get("stntId"));
                        ntcnMap.put("textbkId", temp.get("textbkId"));
                        ntcnMap.put("claId", temp.get("claId"));
                        ntcnMap.put("trgetCd", "S");
                        ntcnMap.put("linkUrl", "");
                        ntcnMap.put("stntNm", temp.get("flnm"));
                        ntcnMap.put("ntcnTyCd", "3");
                        ntcnMap.put("trgetTyCd", "13");
                        ntcnMap.put("ntcnCn", "[자기주도학습] 오답노트에 문제가 추가되었습니다.");
                        stntNtcnService.createStntNtcnSave(ntcnMap);
                    }
                }
            }


            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
            resultMap.put("resultErr", "");
        } catch (NullPointerException e) {
            log.error("saveStntSelfLrnEnd - NullPointerException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
            resultMap.put("resultErr", "NullPointerException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("saveStntSelfLrnEnd - IllegalArgumentException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
            resultMap.put("resultErr", "IllegalArgumentException: " + e.getMessage());
        } catch (DataAccessException e) {
            log.error("saveStntSelfLrnEnd - DataAccessException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
            resultMap.put("resultErr", "DataAccessException: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("saveStntSelfLrnEnd - RuntimeException:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
            resultMap.put("resultErr", "RuntimeException: " + e.getMessage());
        } catch (Exception e) {
            log.error("saveStntSelfLrnEnd - Exception:", e);
            CustomLokiLog.errorLog(e);
            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
            resultMap.put("resultErr", "Exception: " + e.getMessage());
        }

        scenarioMapper.updateSlfLrnUptDt(paramData);
        // Response
        return resultMap;
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
                requestCountMap.put("상", 2);
                requestCountMap.put("중", 3);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("중", 5);
            }
        } else if ("grammar".equals(stdNm)) {
            if ("ED01".equals(lvlId)) {
                requestCountMap.put("상", 3);
                requestCountMap.put("중", 2);
            } else if ("ED02".equals(lvlId)) {
                requestCountMap.put("중", 5);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("중", 2);
                requestCountMap.put("하", 3);
            }
        } else if ("reading".equals(stdNm)) {
            if ("ED01".equals(lvlId)) {
                requestCountMap.put("상", 3);
                requestCountMap.put("중", 2);
            } else if ("ED02".equals(lvlId)) {
                requestCountMap.put("중", 5);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("중", 2);
                requestCountMap.put("하", 3);
            }
        } else if ("listening".equals(stdNm)) {
            if ("ED01".equals(lvlId)) {
                requestCountMap.put("상", 3);
                requestCountMap.put("중", 2);
            } else if ("ED02".equals(lvlId)) {
                requestCountMap.put("중", 5);
            } else if ("ED03".equals(lvlId)) {
                requestCountMap.put("중", 2);
                requestCountMap.put("하", 3);
            }
        } else if ("pronunciation".equals(stdNm)) {
            articleSize = 1;
            String schNm = MapUtils.getString(stntSelfLrnEngMapper.selectCurriSchool(paramData), "val", "");

            if ("고등학교".equals(schNm)) {
                if ("W".equals(lvlId)) {
                    paramList = Arrays.asList("Vocabulary");
                } else if ("S".equals(lvlId)) {
                    paramList = Arrays.asList("Task 1-1", "Task 1-2");
                } else if ("D".equals(lvlId)) {
                    paramList = Arrays.asList("Task 1-1", "Task 2");
                }
            } else {
                if ("W".equals(lvlId)) {
                    paramList = Arrays.asList("Vocabulary");
                } else if ("S".equals(lvlId)) {
                    paramList = Arrays.asList("Everyday Communication 1", "Everyday Communication 2");
                } else if ("D".equals(lvlId)) {
                    paramList = Arrays.asList("Read");
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
                    articleList =  scenarioMapper.selectSelfLrnEngArticles(innerParam);
                    totalArticleList.addAll(articleList);
                }
            }
        }

        if(totalArticleList.isEmpty() || totalArticleList.size() < articleSize) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg",(totalArticleList.isEmpty()) ? "조건에 해당하는 모듈이 없습니다." : "조건에 맞는 "+articleSize+"개의 모듈이 없습니다.");
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
                        "id", "moduleId", "moduleNum"
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

    public Object saveStntSelfLrnReceiveEng(Map<String, Object> paramData) throws Exception {
        Map<Object, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","");
        Map<Object, Object> summaryInfo = new HashMap<>();

        Map<String, Object> beforeModuleMap = stntSelfLrnEngMapper.selectStntSelfLrnReceiveEng_beforeModuleMap(paramData);
        List<Map> list = scenarioMapper.selectStntSelfLrnReceiveEng(beforeModuleMap);

        if(list.isEmpty()) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg", "조건에 해당하는 모듈이 없습니다.");
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

    // 수학 자기주도학습 생성 (수학)
    public Object saveStntSelfLrnCreate(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","");
        Map<Object, Object> summaryInfo = new HashMap<>();

        // Response Parameters
        List<String> listItem = Arrays.asList(
                "userId", "id", "name"
        );

        // stdUsdId 찾아서 추가
        Map<String, Object> stdUsdInfo = scenarioMapper.findStdUsdId(paramData);
        int stdUsdId = MapUtils.getInteger(stdUsdInfo, "stdUsdId", 0);
        if (stdUsdId != 0) {
            paramData.put("stdUsdId", stdUsdId);
        }

        List<LinkedHashMap<Object,Object>> list = AidtCommonUtil.filterToList(listItem, scenarioMapper.selectStntSelfLrnRecModuleList(paramData));
        if(list.isEmpty() || list.size() < 5) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg",(list.isEmpty()) ? "조건에 해당하는 모듈이 없습니다." : "조건에 맞는 5개의 모듈이 없습니다.");
            return rtnMap;
        } else {
            int insertCnt = stntSelfLrnMapper.insertStntSelfLrnCreate(paramData);
            int id = MapUtils.getInteger(paramData, "id");
            log.info("자기주도학습 미존재 ID: "+id);

            int cnt = 0;
            if(insertCnt > 0){
                // Rule 기반으로 추출한 문항 목록 추가
                paramData.put("list", list);

                cnt = stntSelfLrnMapper.insertStntSelfLrnResultCreate(paramData);
            }

            if(insertCnt > 0  && cnt > 0) {
                List<String> infoItem = Arrays.asList(
                        "id", "stdCd", "stdCdNm", "stdNm", "stdUsdId"
                );
                List<String> nonelistItem = Arrays.asList(
                        "id", "moduleId", "moduleNum"
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

    // 자기주도학습 (오답시) 유사문항 받기 - 단답형 제외 로직 추가
    public Object saveStntSelfLrnReceive(Map<String, Object> paramData) throws Exception {
        log.info("결과정보 ID: "+paramData.get("slfResultId"));

        Map<Object, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","");
        Map<Object, Object> summaryInfo = new HashMap<>();

        // Response Parameters
        List<String> listItem = Arrays.asList(
                "gId", "articleId", "id", "stdId", "textbkId", "stdtId", "moduleNum"
        );

        // 2024-06-04
        // [기본조건]은 유형이 동일한 유사문항 받기
        // [추가조건]은 동일한 유형의 유사문항이 추출되지 않은 경우 유형이 다른 조건으로 한번 더 추출
        String[] compareValues = {"Y","N"};
        List<LinkedHashMap<Object, Object>> list = new ArrayList<>();

        for(String compareVal : compareValues) {
            paramData.put("studyMap2Equal", compareVal);
            // 유사문항 추출
            list = AidtCommonUtil.filterToList(listItem, scenarioMapper.selectStntSelfLrnRecvModuleList(paramData));
            if(!list.isEmpty()) {
                break;
            }
        }

        if(list.isEmpty()) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg", "조건에 해당하는 모듈이 없습니다.");
            return rtnMap;
        } else {
            // Rule 기반으로 추출한 문항 목록 추가
            paramData.put("list", list);

            int insertCnt = stntSelfLrnMapper.insertStntSelfLrnResultReceive(paramData);
            int id = MapUtils.getInteger(paramData, "id");
            log.info("자기주도학습 유사문항 미존재 ID: "+id);

            if(insertCnt > 0) {
                List<String> infoItem = Arrays.asList(
                        "id", "moduleId"
                );
                summaryInfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnMapper.findStntSelfLrnCreateReceiveInfo(paramData));

                summaryInfo.put("resultOk", true);
                summaryInfo.put("resultMsg", "성공");
                rtnMap.putAll(summaryInfo);
            }
        }

        // list 삭제
        paramData.remove("list");
        paramData.remove("studyMap2Equal");

        return rtnMap;
    }
}
