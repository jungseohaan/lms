package com.visang.aidt.lms.api.selflrn.service;

import com.visang.aidt.lms.api.engvocal.service.StntMdulVocalScrService;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnEngMapper;
import com.visang.aidt.lms.api.selflrn.mapper.StntSelfLrnMapper;
import com.visang.aidt.lms.api.user.mapper.UserMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class StntSelfLrnService {

    private final StntSelfLrnMapper stntSelfLrnMapper;
    private final StntSelfLrnEngMapper stntSelfLrnEngMapper;

    private final StntRewardService stntRewardService;

    private final StntWrongnoteMapper stntWrongnoteMapper;

    private final StntNtcnService stntNtcnService;

    private final StntMdulVocalScrService stntMdulVocalScrService;

    private final UserMapper userMapper;

    // 학습자료 존재유무 체크
    @Transactional(readOnly = true)
    public Boolean findStntSelfLrnStdInfoCheck(HashMap paramData) throws Exception {

        int cnt = this.stntSelfLrnMapper.selectStntSelfLrnExistCheck(paramData);
        AtomicReference<Boolean> isExist = new AtomicReference<>(Boolean.FALSE);

        if(cnt > 0) {
            isExist.set(Boolean.TRUE);
        }

        return isExist.get();
    }

    @Transactional(readOnly = true)
    public Object findStntSelfLrnChapterList2(Object paramData) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();

        List<String> resultItem = Arrays.asList("unitNum","metaId","parentId","code","unitNm","depth");
        resultMap.put("unitList", AidtCommonUtil.filterToList(resultItem, stntSelfLrnMapper.findStntSelfLrnChapterList2(paramData)));

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSelfLrnChapterList3(Object paramData) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();

        List<String> resultItem = Arrays.asList("unitNum","metaId","stdAt","metaVal","val","usdAchScr");
        resultMap.put("unitList", AidtCommonUtil.filterToList(resultItem, stntSelfLrnMapper.findStntSelfLrnChapterList3(paramData)));

        return resultMap;
    }

    // 자기주도학습 단원 목록 조회
    @Transactional(readOnly = true)
    public Object findStntSelfLrnChapterList(Object paramData) throws Exception {
        Map<Object, Object> respMap = new HashMap<>();

        // Response Parameters
        List<String> unitResultInfoItem = Arrays.asList(
                "metaId", "unitNum","stdAt", "usdScr", "unitNm", "allEdAt"
        );
        List<String> item = Arrays.asList(
                "lastLesnUnitId"
        );

        // 결과 정보
        Map<Object, Object> lastLesInfo = AidtCommonUtil.filterToMap(item, stntSelfLrnMapper.findStntSelfLrnLastLesson(paramData));

        respMap= lastLesInfo;

        if(lastLesInfo.isEmpty()){
            respMap.put("lastLesnUnitId",null);
        }
        List<LinkedHashMap<Object, Object>> unitList = AidtCommonUtil.filterToList(unitResultInfoItem, stntSelfLrnMapper.findStntSelfLrnChapterList(paramData));

        // 모든 단원 오픈
        String stdAt = "Y";
//        if (unitList != null && unitList.stream().allMatch(map -> "N".equals(map.get("stdAt")))) {
//            stdAt = "N";
//        }

        // 반올림
        for (Map map : unitList) {
            if (map.containsKey("usdScr")) {
                double usdScr = (double) map.get("usdScr");
                int roundedValue = (int) Math.round(usdScr);
                map.put("usdScr", roundedValue);
            }
        }

        respMap.put("stdAt",stdAt);
        respMap.put("unitList",unitList);

        return respMap;
    }

    // 자기주도학습 단원 개념 목록 조회
    @Transactional(readOnly = true)
    public Object findStntSelfLrnChapterConceptList(Object paramData, Pageable pageable) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "id", "stdAt", "kwgMainId","kwgNm", "usdScr"
        );
        List<String> mapItem = Arrays.asList(
                "metaId" ,"userId"
        );

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
            .param(paramData)
            .pageable(pageable)
            .build();

        List<Map> conceptListByPage = new ArrayList<>();
        conceptListByPage = stntSelfLrnMapper.findStntSelfLrnChapterConceptList(pagingParam);
        if (!conceptListByPage.isEmpty()) {
            total = (long) conceptListByPage.get(0).get("fullCount");
        }
        PagingInfo page = AidtCommonUtil.ofPageInfo(conceptListByPage, pageable, total);
        // 결과 정보
        List<LinkedHashMap<Object, Object>> conceptList = AidtCommonUtil.filterToList(infoItem, conceptListByPage);

        // 반올림
        for (Map map : conceptList) {
            if (map.containsKey("usdScr")) {
                double usdScr = (double) map.get("usdScr");
                int roundedValue = (int) Math.round(usdScr);
                map.put("usdScr", roundedValue);
            }
        }

        // Response
        Map<Object, Object> respMap = AidtCommonUtil.filterToMap(mapItem, paramData);
        respMap.put("conceptList",conceptList);

        // (수학) 난이도 추가
        List<LinkedHashMap<String, Object>> difficultyList = new ArrayList<>();
        LinkedHashMap<String, Object> difficulty = null;

        List codeList  = List.of("MD05","MD04","MD03","MD02");
        List valList   = List.of("하: 개념 이해","중하: 개념 및 원리의 적용","중: 문제의 해석 및 응용","중상: 개념의 확장 및 추론");
        List dispList  = List.of("Lv.1","Lv.2","Lv.3","Lv.4");

        for(int i=0; i<codeList.size(); i++) {
            difficulty = new LinkedHashMap<>();
            difficulty.put("code",codeList.get(i));
            difficulty.put("val",valList.get(i));
            difficulty.put("dispNm",dispList.get(i));

            difficultyList.add(difficulty);
        }

        respMap.put("difficultyList",difficultyList);

        respMap.put("page",page);

        return respMap;
    }

    // 자기주도학습 생성
    public Object saveStntSelfLrnCreate(Map<String, Object> paramData) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","");
        Map<Object, Object> summaryInfo = new HashMap<>();

        // Response Parameters
        List<String> listItem = Arrays.asList(
                "userId", "id", "name"
        );

        List<LinkedHashMap<Object,Object>> list = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.selectStntSelfLrnRecModuleList(paramData));
        if(list.isEmpty() || list.size() < 5) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg",(list.isEmpty()) ? "출제할 문항이 없습니다." : "출제할 문항이 부족합니다.");
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

    // 자기주도학습 (오답시) 유사문항 받기
    public Object saveStntSelfLrnReceive(Map<String, Object> paramData) throws Exception {
        log.info("결과정보 ID: "+paramData.get("slfResultId"));

        Map<Object, Object> rtnMap = new LinkedHashMap<>();
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
            list = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.selectStntSelfLrnRecvModuleList(paramData));
            if(!list.isEmpty()) {
                break;
            }
        }

        if(list.isEmpty()) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg", "출제할 문항이 없습니다.");
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

                Map<String,Object> mdulInfo =  stntSelfLrnMapper.findStntSelfLrnCreateReceiveSummary(paramData);

                if (!mdulInfo.isEmpty()) {
                    summaryInfo.put("mdulInfo", mdulInfo);
                }

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

    // 자기주도학습 답안 제출
    public Map saveStntSelfLrnSubmitAnswer(Map<String, Object> paramData) throws Exception {
        // 자기주도학습 답안 제출

        final int MAX_SIZE_BYTES = 10 * 1024 * 1024; // 16MB
        final int MAX_SIZE_MB = MAX_SIZE_BYTES / (1024 * 1024);

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("slfResultId", paramData.get("slfResultId"));
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");
        resultMap.put("resultErr", "");


        // 손글씨 내용 길이 검증
        String hdwrtCn = MapUtils.getString(paramData, "hdwrtCn");
        if (StringUtils.hasText(hdwrtCn)) {
            int contentSize = hdwrtCn.getBytes().length;

            if (contentSize > MAX_SIZE_BYTES) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "Data too large. Max " + MAX_SIZE_MB + "MB.");
                resultMap.put("size", contentSize);
                resultMap.put("limitSize", MAX_SIZE_MB);
                return resultMap;
            }
        }
            int cnt = stntSelfLrnMapper.saveStntSelfLrnSubmitAnswer(paramData);

            if (cnt > 0) {
                Map<String, Object> slfStdResultInfoMap = stntSelfLrnMapper.findSlfStdResultInfo(paramData);
                String questionType = MapUtils.getString(slfStdResultInfoMap, "questionType");
                // questionType - ptqz (발음평가형) 인 경우
                if ("ptqz".equals(questionType) || "wcom".equals(questionType)) {
                    String menuSeCd = "4"; // 1:교과서, 2:과제, 3:평가, 4:자기주도학습
                    int stdCd = MapUtils.getInteger(slfStdResultInfoMap, "stdCd", 0);
                    int trgtId = MapUtils.getInteger(paramData, "slfResultId");
                    String subMitAnw = MapUtils.getString(paramData, "subMitAnw");
                    // 발성평가 점수 등록 처리
                    stntMdulVocalScrService.saveVocalEvlScrInfo(menuSeCd, stdCd, trgtId, subMitAnw);

//2025-05-07 제출하기누를시 적립으로 변경
//                    try {
//                        Map<String, Object> claInfo = stntSelfLrnMapper.selectStntSelfLrnRewardClaid(paramData);
//
//                        Map<String, Object> paramInfo = new HashMap<>();
//                        paramInfo.put("userId"  ,claInfo.get("userId"));
//                        paramInfo.put("claId"   ,claInfo.get("claId"));
//                        paramInfo.put("seCd"    ,"1");
//                        paramInfo.put("menuSeCd","4");                    //1:교과서, 2:과제, 3:평가, 4:자기주도학습
//                        paramInfo.put("sveSeCd" ,claInfo.get("stdCd"));   //1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
//                        paramInfo.put("trgtId"  ,paramData.get("slfResultId"));
//                        paramInfo.put("textbkId"  ,claInfo.get("textbkId"));
//                        paramInfo.put("rwdSeCd" ,"1");  //1:하트, 2:스타
//                        paramInfo.put("rwdAmt"  ,"1");  //정답인 경우 1점 획득
//                        paramInfo.put("correctAnwNum",null);
//
//                        stntRewardService.createReward(paramInfo); //2025-05-07 삭제(제출하기시에 적립하기로)
//
//                    } catch (Exception e) {
//                        log.error(CustomLokiLog.errorLog(e));
//                    }
                }
            }

//2025-05-07 제출하기누를시 적립으로 변경
            //정답일 경우,리워드 획득.
//            if(cnt > 0 && "1".equals(paramData.get("errata").toString())){
//                try {
//                    Map<String, Object> claInfo = stntSelfLrnMapper.selectStntSelfLrnRewardClaid(paramData);
//
//                    Map<String, Object> paramInfo = new HashMap<>();
//                    paramInfo.put("userId"  ,claInfo.get("userId"));
//                    paramInfo.put("claId"   ,claInfo.get("claId"));
//                    paramInfo.put("seCd"    ,"1");
//                    paramInfo.put("menuSeCd","4");                    //1:교과서, 2:과제, 3:평가, 4:자기주도학습
//                    paramInfo.put("sveSeCd" ,claInfo.get("stdCd"));   //1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
//                    paramInfo.put("trgtId"  ,paramData.get("slfResultId"));
//                    paramInfo.put("textbkId"  ,claInfo.get("textbkId"));
//                    paramInfo.put("rwdSeCd" ,"1");  //1:하트, 2:스타
//                    paramInfo.put("rwdAmt"  ,"1");  //정답인 경우 1점 획득
//                    paramInfo.put("correctAnwNum",null);
//
//                    stntRewardService.createReward(paramInfo);
//
//                } catch (Exception e) {
//                    log.error(CustomLokiLog.errorLog(e));
//                }
//
//            }


            if(cnt <= 0 && 4 != (MapUtils.getIntValue(paramData, "errata", 0 ))) {
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "실패");
                resultMap.put("resultErr", "Not Found Modifiy Data: " + cnt);
                //throw new AidtException("Not Found Modifiy Data: " + cnt);
            }

            if (cnt > 0) {
                if (StringUtils.hasText(hdwrtCn)) {
                    resultMap.put("size", hdwrtCn.getBytes().length);
                }
            }
        // Response
        return resultMap;
    }

    // 자기주도학습 종료 하기
    public Map saveStntSelfLrnEnd(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            int cnt = 0;
            int deleteResult1 = 0;
            int deleteResult2 = 0;
            Map<String, Object> slfStdInfo = stntSelfLrnMapper.findSlfStdInfoById(paramData);
            int textbkId = MapUtils.getIntValue(slfStdInfo, "textbkId" ,0);

            // 종료
            if(paramData != null && paramData.get("edAt") != null && "Y".equals(paramData.get("edAt").toString())){
                cnt = stntSelfLrnMapper.saveStntSelfLrnEnd(paramData);
                cnt = stntSelfLrnMapper.saveStntSelfLrnStdEnd(paramData);

                //#3910 초등 5,6 영어 학습횟수, 과거 데이터 삭제 (민간존 초등 5영어 11790, 초등 6영어 11791
                if (textbkId == 6981 || textbkId == 6982 || textbkId == 11790 || textbkId == 11791) {
                    stntSelfLrnMapper.updateStntElementaryEngSelfLearningCount(paramData);
                    // 이전 완료 자료 불러오기
                    List<Map> selfLearningExceptList = stntSelfLrnEngMapper.selectSelfLearningExceptList(paramData);

                    for (Map selfLearningExceptInfo : selfLearningExceptList) {
                        // 오답노트 데이터 삭제
                        int dWonCnt = stntSelfLrnEngMapper.deleteWonAswNote(selfLearningExceptInfo);
                        log.info(" 오답노트 삭제: " + dWonCnt);

                        // slf_std_result_info 삭제
                        int dCnt = stntSelfLrnEngMapper.deleteStntSelfLrnResult(selfLearningExceptInfo);
                        log.info("slf_std_result_info 삭제: " + dCnt);

                        //aidt_lms.slf_std_info 삭제
                        int dInfoCnt = stntSelfLrnEngMapper.deleteStntSelfLrnInfo(selfLearningExceptInfo);
                        log.info("slf_std_info 삭제: " + dInfoCnt);
                    }
                }

                //자기주도학습 학습시간 조회
                Map<String, Object> stdTimeMap = stntSelfLrnMapper.getDeleteStntSelfLrnEnd(paramData);
                /* j1394
                 * 정답을 입력하지 않으면 오답으로 처리된다는 알림창이 뜨고 있기 때문에,
                 * 정답 입력하지 않은 문제들은 오답으로 처리되고 + 오답으로 기록이 남아야  함
                if("00:00:00".equals(stdTimeMap.get("stdTime").toString())) {
                    deleteResult1 = stntSelfLrnMapper.deleteStntSelfLrnEnd_info(paramData);
                    deleteResult2 = stntSelfLrnMapper.deleteStntSelfLrnEnd_result_info(paramData);
                } else {
                 */
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
                         ntcnMap.put("ntcnCn", "[스스로 학습] 오답노트에 문제가 추가되었습니다.");
                         stntNtcnService.createStntNtcnSave(ntcnMap);
                     }
                 }
                //}

                //맞힌갯수 리워드적립 2025-05-07 추가
                String correctCnt = "0";
                List<Map> errataMap = stntSelfLrnMapper.selectSaveStntSelfLrnEndCorrectCnt(paramData);
                if(ListUtils.emptyIfNull(errataMap) != null) {
                    if(errataMap.size() > 0) {
                        for (int i = 0; i < errataMap.size(); i++) {
                            Map<String, Object> claInfo = stntSelfLrnMapper.selectStntSelfLrnRewardClaid(errataMap.get(i));

                            Map<String, Object> paramInfo = new HashMap<>();
                            paramInfo.put("userId", claInfo.get("userId"));
                            paramInfo.put("claId", claInfo.get("claId"));
                            paramInfo.put("seCd", "1");
                            paramInfo.put("menuSeCd", "4");                    //1:교과서, 2:과제, 3:평가, 4:자기주도학습
                            paramInfo.put("sveSeCd", claInfo.get("stdCd"));   //1:문제, 2:활동, 3:과제, 4:평가, 5:AI학습, 6:선택학습, 7:다른문제풀기
                            paramInfo.put("textbkId", claInfo.get("textbkId"));
                            paramInfo.put("trgtId", errataMap.get(i).get("slfResultId"));
                            paramInfo.put("rwdSeCd", "1");  //1:하트, 2:스타
                            paramInfo.put("rwdAmt", "1");  //정답인 경우 1점 획득
                            paramInfo.put("correctAnwNum", null);

                            try {
                                stntRewardService.createReward(paramInfo);
                            } catch (Exception e) {
                                paramData.remove("slfResultId");
                                log.error(CustomLokiLog.errorLog(e));
                            }
                        }
                    }

                }

            }
            // 나가기
            else {
                //Map<String, Object> slfStdInfo = stntSelfLrnMapper.findSlfStdInfoById(paramData);

                //int textbkId = MapUtils.getIntValue(slfStdInfo, "textbkId" ,0);

                if (textbkId == 6981 || textbkId == 6982) {
                    //stntSelfLrnMapper.updateStntSelfLrnEnd_info(paramData);
                    //stntSelfLrnEngMapper.deleteStntSelfLrnResult(slfStdInfo);
                    stntSelfLrnMapper.updateStntSelfLrnEnd_result_info(paramData);
                } else {
                    deleteResult1 = stntSelfLrnMapper.deleteStntSelfLrnEnd_info(paramData);
                    deleteResult2 = stntSelfLrnMapper.deleteStntSelfLrnEnd_result_info(paramData);
                }
            }

            paramData.remove("slfResultId");
            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
            resultMap.put("resultErr", "");
        }
        catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            resultMap.put("slfId", paramData.get("slfId"));
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
            resultMap.put("resultErr", "err");
        }

        // Response
        return resultMap;
    }

    // 자기주도학습 결과 보기
    @Transactional(readOnly = true)
    public Object findStntSelfLrnResultSummary(Object paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "id", "stdCd","stdCdNm", "stdNm", "lvlId", "lvlNm", "beforeUsdScr", "afterUsdScr", "totMdulCnt", "stdMdulCnt", "anwNum", "wrngNum", "totalReward"
        );

        List<String> listItem = Arrays.asList(
                "id","moduleId", "moduleNum", "thumbnail", "questionStr", "hashTags", "url" , "image", "stdStDt", "stdEdDt", "subMitAnw","subMitAnwUrl", "errata", "smExmAt", "srcModuleId","srcSlfResultId", "aiTutUseAt", "hdwrtCn"
        );

        Map<Object, Object> summaryLinfo = null;

        summaryLinfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnMapper.findStntSelfLrnResultSummaryInfo(paramData));

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

        // Response
        //LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        //respMap.put("stdResultList",stdResultList);

        return summaryLinfo;
    }

    //학습내역 결과보기
    @Transactional(readOnly = true)
    public Object findStntSelfLrnResultList(Object paramData) throws Exception {
        // Response Parameters
        List<String> infoItem = Arrays.asList(
                "id", "stdCd","stdCdNm", "stdNm", "stdDt", "totMdulCnt", "stdMdulCnt", "stdTime"
        );

        List<String> listItem = Arrays.asList(
                "slfResultId","moduleId", "moduleNum", "thumbnail", "questionStr", "hashTags", "url" , "image", "stdAt", "mrkCpAt", "stdStDt", "stdEdDt", "subMitAnw","subMitAnwUrl", "errata", "smExmAt", "srcModuleId","srcSlfResultId", "aiTutUseAt", "hdwrtCn", "hntUseAt"
        );

        Map<Object, Object> summaryLinfo = null;
        List<LinkedHashMap<Object, Object>> stdImage = null;
        List<LinkedHashMap<Object, Object>> stdResultList = null;

        summaryLinfo = AidtCommonUtil.filterToMap(infoItem, stntSelfLrnMapper.findStntSelfLrnResultInfo(paramData));
        stdImage = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.findStntSelfLrnResultSummaryList(paramData));
        stdResultList = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.findStntSelfLrnResultSummaryList(paramData));

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

    /** 스스로 학습 그래프 조회 */
    @Transactional(readOnly = true)
    public Object findStntSelfLrnDashBoardGraphList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

        int brandId = getBrandIdByTextbkId(MapUtils.getIntValue(paramData,"textbkId"));
        paramData.put("brandId" , brandId);

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> lrnGraphList = new ArrayList<>();
        if (brandId == 1) { //수학 : AI학습 + 선택학습,
            lrnGraphList = stntSelfLrnMapper.stntSelfLrnDashBoardGraphListForMath(pagingParam);
        } else if (brandId == 3) { //영어 : AI학습 + 선택학습
            lrnGraphList = stntSelfLrnMapper.stntSelfLrnDashBoardGraphListForEngNew(pagingParam);
        }

        if (lrnGraphList.isEmpty()) {
            return returnMap;
        }

        long total = (long) lrnGraphList.get(0).get("fullCount");
        returnMap.put("lrnGraphList", lrnGraphList);
        returnMap.put("page", AidtCommonUtil.ofPageInfo(lrnGraphList, pageable, total));

        return returnMap;
    }

    /* 지금은 사용하지 않음(06/11) */
    @Transactional(readOnly = true)
    public Object findStntSelfLrnList_bak(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> SelfInfoItem = Arrays.asList("num", "id", "stdCd", "stdCdNm", "stdNm", "stdDt", "totMdulCnt", "stdMdulCnt", "stdTime", "aiTutAt");

        int brandId = getBrandIdByTextbkId(MapUtils.getIntValue(paramData,"textbkId"));
        paramData.put("brandId" , brandId);

        // 1. AI 튜터 정보용 전체 데이터 조회
        List<Map> allLrnInfoList = new ArrayList<>();
        if (brandId == 1) {
            allLrnInfoList = stntSelfLrnMapper.findStntSelfLrnListForMathAll(paramData);
        } else if (brandId == 3) {
            allLrnInfoList = stntSelfLrnMapper.findStntSelfLrnListForEngNewAll(paramData);
        }

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> lrnInfoList = new ArrayList<>();
        if (brandId == 1) { //수학 : AI학습 + 선택학습,
            lrnInfoList = stntSelfLrnMapper.findStntSelfLrnListForMath(pagingParam);
        } else if (brandId == 3) { //영어 : AI학습 + 선택학습
            lrnInfoList = stntSelfLrnMapper.findStntSelfLrnListForEngNew(pagingParam);
        }

        if (lrnInfoList.isEmpty()) {
            return returnMap;
        }

        long total = (long) lrnInfoList.get(0).get("fullCount");

        returnMap.put("aiTutorInfo", createAiTutorInfo(allLrnInfoList, paramData)); // AI 튜터 정보
        returnMap.put("stdList", AidtCommonUtil.filterToList(SelfInfoItem, lrnInfoList)); // 학습 리스트 정보
        returnMap.put("page", AidtCommonUtil.ofPageInfo(lrnInfoList, pageable, total)); // 페이징 정보

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntSelfLrnList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> SelfInfoItem = Arrays.asList("num", "id", "stdCd", "stdCdNm", "stdNm", "stdDt", "totMdulCnt", "stdMdulCnt", "stdTime", "aiTutAt");

        int brandId = getBrandIdByTextbkId(MapUtils.getIntValue(paramData,"textbkId"));
        paramData.put("brandId", brandId);

        // 전체 데이터 조회 (페이징 없이)
        List<Map> allLrnInfoList = new ArrayList<>();
        if (brandId == 1) {
            allLrnInfoList = stntSelfLrnMapper.findStntSelfLrnListForMathAll(paramData); // 전체 조회
        } else if (brandId == 3) {
            allLrnInfoList = stntSelfLrnMapper.findStntSelfLrnListForEngNewAll(paramData); // 전체 조회
        }

        if (allLrnInfoList.isEmpty()) {
            return returnMap;
        }

        // 자바에서 페이징 처리
        List<Map> pagedLrnInfoList = applyPaging(allLrnInfoList, pageable);
        long total = allLrnInfoList.size();

        // 전체 데이터로 AI 튜터 정보, 페이징된 데이터로 학습 리스트
        returnMap.put("aiTutorInfo", createAiTutorInfo(allLrnInfoList, paramData)); // 전체 데이터 사용
        returnMap.put("stdList", AidtCommonUtil.filterToList(SelfInfoItem, pagedLrnInfoList)); // 페이징 데이터 사용
        returnMap.put("page", createPageInfo(pageable, total)); // 직접 페이지 정보 생성

        return returnMap;
    }

    /** 자바에서 페이징 처리 */
    private List<Map> applyPaging(List<Map> allList, Pageable pageable) {
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), allList.size());

        if (start >= allList.size()) {
            return new ArrayList<>();
        }

        return allList.subList(start, end);
    }

    /** 페이지 정보 생성 */
    private Map<String, Object> createPageInfo(Pageable pageable, long total) {
        Map<String, Object> pageInfo = new LinkedHashMap<>();
        pageInfo.put("number", pageable.getPageNumber());
        pageInfo.put("size", pageable.getPageSize());
        pageInfo.put("totalElements", total);
        pageInfo.put("totalPages", (int) Math.ceil((double) total / pageable.getPageSize()));
       /* pageInfo.put("first", pageable.getPageNumber() == 0);
        pageInfo.put("last", pageable.getPageNumber() >= Math.ceil((double) total / pageable.getPageSize()) - 1); */
        return pageInfo;
    }

    /** AI 튜터 정보 생성 */
    private Map<String, Object> createAiTutorInfo(List<Map> lrnInfoList, Map<String, Object> paramData) throws Exception {
        // 1. 일별 학습 수 집계 및 정렬
        Map<String, Integer> dailyArticleCounts = new LinkedHashMap<>();
        for (Map item : lrnInfoList) {
            String stdDt = (String) item.get("stdDt");
            int stdMdulCnt = Optional.ofNullable(item.get("stdMdulCnt"))
                .map(value -> ((Number) value).intValue())
                .orElse(0);

            dailyArticleCounts.merge(stdDt, stdMdulCnt, Integer::sum);
        }

        List<Map.Entry<String, Integer>> sortedEntries = dailyArticleCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByKey().reversed())
                .limit(10)
                .toList();

        // 2. 학습자 정보 조회
        Map<String, Object> aiTutorInfo = new LinkedHashMap<>();
        Map<String, Object> stntInfo = userMapper.findUserInfoByUserId(paramData.get("userId").toString());

        aiTutorInfo.put("flnm", stntInfo.get("flnm").toString());
        aiTutorInfo.put("firstDate", sortedEntries.get(sortedEntries.size() - 1).getKey());
        aiTutorInfo.put("lastDate", sortedEntries.get(0).getKey());

        // 3. 최다 학습일 정보
        sortedEntries.stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(mostStudied -> {
                    Map<String, Object> mostStudiedInfo = new LinkedHashMap<>();
                    mostStudiedInfo.put("date", mostStudied.getKey());
                    mostStudiedInfo.put("studiedCnt", (int) lrnInfoList.stream()
                            .filter(item -> mostStudied.getKey().equals(item.get("stdDt")))
                            .count());
                    mostStudiedInfo.put("articleCnt", mostStudied.getValue());
                    aiTutorInfo.put("mostStudiedInfo", mostStudiedInfo);
                });

        // 4. 최소 학습일 정보
        sortedEntries.stream()
                .min(Map.Entry.comparingByValue())
                .ifPresent(leastStudied -> {
                    Map<String, Object> leastStudiedInfo = new LinkedHashMap<>();
                    leastStudiedInfo.put("date", leastStudied.getKey());
                    leastStudiedInfo.put("studiedCnt", (int) lrnInfoList.stream()
                            .filter(item -> leastStudied.getKey().equals(item.get("stdDt")))
                            .count());
                    leastStudiedInfo.put("articleCnt", leastStudied.getValue());
                    aiTutorInfo.put("leastStudiedInfo", leastStudiedInfo);
                });

        return aiTutorInfo;
    }

    @Transactional(readOnly = true)
    public Object stntSelflrnUsdlowKwgList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<Map> resultList = stntSelfLrnMapper.stntSelflrnUsdlowKwgList(paramData);
        List<Map<String, Object>> kwgInfoList = new ArrayList<>();
        if( resultList.size() > 0 ) {
            returnMap.put("unitNum", paramData.get("unitNum"));
            returnMap.put("metaId", resultList.get(0).get("metaId"));
            returnMap.put("metaNm", resultList.get(0).get("metaNm"));
            returnMap.put("stdtNm", resultList.get(0).get("stdtNm"));
            for(Map<String, Object> temp : resultList) {
                temp.remove("unitNum");
                temp.remove("metaId");
                temp.remove("metaNm");
                temp.remove("stdtNm");
                kwgInfoList.add(temp);
            }
        }
        returnMap.put("kwgInfoList", kwgInfoList);

        Map<String, Object> stdAtCnt = stntSelfLrnMapper.stntSelflrnUsdlowKwgList_stdAt(paramData);

        if (MapUtils.getIntValue(stdAtCnt, "cnt", 0) > 0) {
            returnMap.put("stdAt", "Y");
        } else {
            returnMap.put("stdAt", "N");
        }

        return returnMap;
    }

    // 자기주도학습-학습역량 표시
    @Transactional(readOnly = true)
    public Object selectStdCptList(Object paramData) throws Exception {
        // Response Parameters
        List<String> listItem = Arrays.asList(
                "stdCptCd", "stdCptNm"
        );

        // 결과 정보
        List<LinkedHashMap<Object, Object>> stdCptList = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.selectStdCptList(paramData));

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("stdCptList",stdCptList);

        return respMap;
    }

    // 자기주도학습-학습방법 및 내용조회
    @Transactional(readOnly = true)
    public Object selectStdMthList(Object paramData) throws Exception {
        // Response Parameters
        List<String> listItem = Arrays.asList(
                "stdMthCd", "stdMthNm"
        );

        // 결과 정보
        List<LinkedHashMap<Object, Object>> stdMthList = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.selectStdMthList(paramData));

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("stdMthList",stdMthList);

        return respMap;
    }

    // 자기주도학습 활동난이도조회
    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> findStntSelfLrnActLvlList(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> resultData = stntSelfLrnMapper.findStntSelfLrnActLvlList(paramData);
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("actLvList", resultData);
        return returnMap;
    }

    // 자기주도학습 답안입력방식조회
    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> findStntSelfLrnAnwIptTyList(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> resultData = stntSelfLrnMapper.findStntSelfLrnAnwIptTyList(paramData);
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("AnwIptList", resultData);
        return returnMap;
    }

    // 자기주도학습-활동유형 표시
    @Transactional(readOnly = true)
    public LinkedHashMap<String, Object> findStntSelfLrnActTyList(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> resultData = stntSelfLrnMapper.findStntSelfLrnActTyList(paramData);
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("ActTyList", resultData);
        return returnMap;
    }

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 목록 조회하기
    @Transactional(readOnly = true)
    public Object selectStntSelfLrnMyWordList(Map<String, Object> paramData) throws Exception {

        var returnMap = new LinkedHashMap<>();

        List<String> listItem = Arrays.asList(
                "libtextId", "type1", "name", "parts", "contentsEntry", "contentsMultilang", "example" //나중에 맞춰 다시 넣어야함(임시)
        );

        List<LinkedHashMap<Object, Object>> myWordList = AidtCommonUtil.filterToList(listItem, stntSelfLrnMapper.selectStntSelfLrnMyWordList(paramData));

        returnMap.put("MyWordList", myWordList);

        return returnMap;

    }

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 나의단어장 플래시 카드 start
    @Transactional(readOnly = true)
    public Object selectStntSelfLrnMyWordFlashStart(Map<String, Object> paramData, Pageable pageable) throws Exception {

        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem = Arrays.asList(
                "libtextId", "type1", "name", "parts", "contentsEntry", "contentsMultilang", "example" //나중에 맞춰 다시 넣어야함(임시)
        );

        List<Map> resultList = stntSelfLrnMapper.selectStntSelfLrnMyWordFlashStart(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> myWordFlashStart = AidtCommonUtil.filterToList(listItem, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("MyWordFlashStart", myWordFlashStart);
        returnMap.put("page",page);

        return returnMap;

    }

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 테스트 start
    @Transactional(readOnly = true)
    public Object selectStntSelfLrnMyWordExamStart(Map<String, Object> paramData, Pageable pageable) throws Exception {

        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem = Arrays.asList(
                "libtextId", "type1", "name", "parts", "contentsEntry", "contentsMultilang", "example" //나중에 맞춰 다시 넣어야함(임시)
        );

        List<Map> resultList = stntSelfLrnMapper.selectStntSelfLrnMyWordExamStart(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> myWordExamStart = AidtCommonUtil.filterToList(listItem, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("MyWordExamStart", myWordExamStart);
        returnMap.put("page",page);

        return returnMap;

    }



    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 발음연습하기
    @Transactional(readOnly = true)
    public Object selectStntSelfLrnMyWordArticulationStart(Map<String, Object> paramData, Pageable pageable) throws Exception {

        var returnMap = new LinkedHashMap<>();

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem = Arrays.asList(
                "libtextId", "type1", "name", "parts", "contentsEntry", "contentsMultilang", "example" //나중에 맞춰 다시 넣어야함(임시)
        );

        List<Map> resultList = stntSelfLrnMapper.selectStntSelfLrnMyWordArticulationStart(pagingParam);

        if(!resultList.isEmpty()) {
            total = Long.valueOf(resultList.get(0).get("fullCount").toString());
        }

        List<LinkedHashMap<Object, Object>> myWordArticulationStart = AidtCommonUtil.filterToList(listItem, resultList);

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        returnMap.put("MyWordArticulationStart", myWordArticulationStart);
        returnMap.put("page",page);

        return returnMap;

    }

    //모듈 학습내역 결과 보기
    @Transactional(readOnly = true)
    public Object findStntSelfLrnMudlResultSummaryList(Object paramData) throws Exception {
        List<String> listItem = Arrays.asList(
                "slfResultId","moduleId", "moduleNum", "thumbnail", "questionStr", "hashTags", "url" , "image", "stdAt", "mrkCpAt", "stdStDt", "stdEdDt", "subMitAnw","subMitAnwUrl", "errata", "smExmAt", "srcModuleId","srcSlfResultId", "aiTutUseAt", "hdwrtCn"
        );

        LinkedHashMap<Object, Object> stdImage = AidtCommonUtil.filterToMap(listItem, stntSelfLrnMapper.findStntSelfLrnMudlResultSummaryList(paramData));

        // 결과 정보
        LinkedHashMap<Object, Object> stdResultList = AidtCommonUtil.filterToMap(listItem, stntSelfLrnMapper.findStntSelfLrnMudlResultSummaryList(paramData));
        if(!stdResultList.isEmpty()){
            Map<Object, Object> imginfo = new HashMap<>();

            if(stdImage.get("moduleId").equals(stdResultList.get("moduleId"))){
                imginfo.put("url",stdImage.get("url"));
                imginfo.put("image",stdImage.get("image"));
                stdResultList.put("mdulImage",imginfo);
            }
        }

        return stdResultList;
    }

    //자기주도학습 응시 확인 저장
    public Map saveStntSelfLrnRecheck(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("slfResultId", paramData.get("slfResultId"));
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "저장완료");

        try {
            stntSelfLrnMapper.updateStntSelfLrnRecheck(paramData);
        } catch (Exception e) {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "저장실패");
        }

        return resultMap;
    }


    private int getBrandIdByTextbkId (int textbkId) throws Exception {
        return stntSelfLrnMapper.getBrandIdByTextbkId(textbkId);
    }

    //AI SW 학습선택
    @Transactional(readOnly = true)
    public Object selectAiEditInitEng(Object paramData) throws Exception {
        List<Map> StdInfo = stntSelfLrnMapper.selectAiEditInitEng();

        return StdInfo;
    }

    //AI SW 저장
    public Map saveAiEditSaveEng(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            stntSelfLrnMapper.insertAiEditSaveEng(paramData);
        } catch (Exception e) {
            resultMap.put("id", null);
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "저장실패");
            return resultMap;
        }

        resultMap.put("id", paramData.get("id"));
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "저장완료");
        return resultMap;
    }

    //AI SW 결과
    @Transactional(readOnly = true)
    public Object selectAiEditResultEng(Object paramData) throws Exception {

        return stntSelfLrnMapper.selectAiEditResultEng(paramData);
    }

    public Object saveStntSelfLrnCreateAll(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        Map<Object, Object> summaryInfo = new HashMap<>();
        List<String> listItem = Arrays.asList(
                "userId", "id", "name"
        );

        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","성공");

        // 초등 수학
        LinkedHashMap<String, Integer> requestCountMap = new LinkedHashMap<>();
        if ("high".equals(MapUtils.getString(paramData, "curriSchool", ""))) {
            // 중고등 수학
            requestCountMap.put("MD05", 3); //하
            requestCountMap.put("MD04", 3); //중하
            requestCountMap.put("MD03", 2); //중
            requestCountMap.put("MD02", 2); //중상
        } else {
            // 초등 수학
            requestCountMap.put("MD05", 3); //하
            requestCountMap.put("MD03", 5); //중
            requestCountMap.put("MD01", 2); //상
        }

        // 파라미터 복제
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        List<Map> totalArticleList = new ArrayList<>();

        // 난이도별로 맞춤 아티클 추출
        for (String key: requestCountMap.keySet()) {
            List<Map> articleList = new ArrayList<>();
            innerParam.put("difficulty", key);
            innerParam.put("cnt", requestCountMap.get(key));
            if (requestCountMap.get(key) > 0 ) {
                articleList =  stntSelfLrnMapper.selectStntSelfLrnRecModuleAllList(innerParam, totalArticleList);
                totalArticleList.addAll(articleList);
            }
        }

        // 단원 모아 풀기 클릭 시 열 문항
        if (CollectionUtils.isNotEmpty(totalArticleList) && totalArticleList.size() < 10) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg","더 이상 학습할 수 있는 문항이 없어요!");
        } else {
            // 정렬
            totalArticleList.sort(Comparator
                    .comparing((Map map) -> ((Integer) map.get("studymap1"))) // studyMap_1 오름차순
            );

            List<LinkedHashMap<Object,Object>> list = AidtCommonUtil.filterToList(listItem, totalArticleList);

            paramData.put("stdUsdId",0);
            paramData.put("lvlId",0);
            paramData.put("lvlNm","모아 풀기");
            int insertCnt = stntSelfLrnMapper.insertStntSelfLrnCreate(paramData);
            int id = MapUtils.getInteger(paramData, "id");
            log.info("자기주도학습 미존재 ID: "+id);

            int cnt = 0;
            if(insertCnt > 0){
                // Rule 기반으로 추출한 문항 목록 추가
                paramData.put("list", list);

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

    public Object saveStntSelfLrnCreateStudymap(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        Map<Object, Object> summaryInfo = new HashMap<>();
        List<String> listItem = Arrays.asList(
                "userId", "id", "name"
        );

        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("resultOk",true);
        rtnMap.put("resultMsg","성공");

        // 학습이해도 ID, 이해도 없을 경우 기본 값 설정 (학습이력이 없는 지식요인의 경우)
        int usdScr = MapUtils.getIntValue(paramData, "usdScr", 0);
        int stdUsdId = MapUtils.getIntValue(paramData, "stdUsdId", 0);
        paramData.put("stdUsdId",stdUsdId);

        // 이해도 그룹별 문항출제. 난이도별로 갯수 고정 (학교별로 상이)
        LinkedHashMap<String, Integer> requestCountMap = new LinkedHashMap<>();
        if ("high".equals(MapUtils.getString(paramData, "curriSchool", ""))) {
            if (usdScr == 0 && stdUsdId == 0) {
                paramData.put("lvlId","MD05");
                paramData.put("lvlNm","하");
                requestCountMap.put("MD05", 1); //하
                requestCountMap.put("MD04", 1); //중하
                requestCountMap.put("MD03", 2); //중
                requestCountMap.put("MD02", 1); //중상
            }
            // 중고등 수학
            else if (usdScr >= 80) { //70이상  //정책 변경(2025.03.25) 80 이상
                paramData.put("lvlId","MD01");
                paramData.put("lvlNm","상");
                requestCountMap.put("MD05", 0); //하
                requestCountMap.put("MD04", 1); //중하
                requestCountMap.put("MD03", 2); //중
                requestCountMap.put("MD02", 2); //중상
            } else if (usdScr >= 50) { //70미만 30이상  //정책 변경(2025.03.25) 80 미만 50 이상
                paramData.put("lvlId","MD03");
                paramData.put("lvlNm","중");
                requestCountMap.put("MD05", 1); //하
                requestCountMap.put("MD04", 1); //중하
                requestCountMap.put("MD03", 2); //중
                requestCountMap.put("MD02", 1); //중상
            } else {
                paramData.put("lvlId","MD05");
                paramData.put("lvlNm","하");
                requestCountMap.put("MD05", 2); //하
                requestCountMap.put("MD04", 2); //중하
                requestCountMap.put("MD03", 1); //중
                requestCountMap.put("MD02", 0); //중상
            }
        } else {
            if (usdScr == 0 && stdUsdId == 0) {
                paramData.put("lvlId","MD05");
                paramData.put("lvlNm","하");
                requestCountMap.put("MD05", 2); //하
                requestCountMap.put("MD03", 2); //중
                requestCountMap.put("MD01", 1); //상
            }
            // 초등 수학
            else if (usdScr >= 80) { //70이상  //정책 변경(2025.03.25) 80 이상
                paramData.put("lvlId","MD01");
                paramData.put("lvlNm","상");
                requestCountMap.put("MD05", 1); //하
                requestCountMap.put("MD03", 2); //중
                requestCountMap.put("MD01", 2); //상
            } else if (usdScr >= 50) { //70미만 30이상  //정책 변경(2025.03.25) 80 미만 50 이상
                paramData.put("lvlId","MD03");
                paramData.put("lvlNm","중");
                requestCountMap.put("MD05", 2); //하
                requestCountMap.put("MD03", 2); //중
                requestCountMap.put("MD01", 1); //상
            } else {
                paramData.put("lvlId","MD05");
                paramData.put("lvlNm","하");
                requestCountMap.put("MD05", 3); //하
                requestCountMap.put("MD03", 2); //중
                requestCountMap.put("MD01", 0); //상
            }
        }

        // 파라미터 복제
        Map<String, Object> innerParam = ObjectUtils.clone(paramData);

        List<Map> totalArticleList = new ArrayList<>();

        List<String> excludedArticleIdList = new ArrayList<>();

        // 난이도별로 맞춤 아티클 추출
        for (String key: requestCountMap.keySet()) {
            List<Map> articleList = new ArrayList<>();
            innerParam.put("difficulty", key);
            innerParam.put("cnt", requestCountMap.get(key));
            innerParam.put("excludedArticleIdList", excludedArticleIdList);

            if (requestCountMap.get(key) > 0 ) {
                articleList =  stntSelfLrnMapper.selectStntSelfLrnRecModuleStudymapList(innerParam);

                if (articleList.size() == Integer.parseInt(innerParam.get("cnt").toString())) {

                    totalArticleList.addAll(articleList);

                    if (!articleList.isEmpty()) {
                        for (Map articleListMap : articleList) {
                            excludedArticleIdList.add((String) articleListMap.get("id"));
                        }
                    }

                } else {
                    /*
                     * 문항 수 부족한 경우
                     * [상] ~ [중하]의 경우 : -1 난이도 문제 출제
                     * [하]의 경우 : +1 난이도 문제 출제
                     * */
                    // 부족 문항 수
                    int lackCnt = Integer.parseInt(innerParam.get("cnt").toString()) - articleList.size();

                    List<Map> lackArticleList = new ArrayList<>();

                    if("high".equals(MapUtils.getString(paramData, "curriSchool", ""))){

                        Map<String, String> addDifficultyMap = Map.of(
                            "MD01", "MD02",
                            "MD02", "MD03",
                            "MD03", "MD04",
                            "MD04", "MD05",
                            "MD05", "MD04"
                        );

                        innerParam.put("difficulty", addDifficultyMap.getOrDefault(key, key));

                    } else {

                        Map<String, String> addDifficultyMap = Map.of(
                            "MD01", "MD03",
                            "MD03", "MD05",
                            "MD05", "MD03"
                        );

                        innerParam.put("difficulty", addDifficultyMap.getOrDefault(key, key));
                    }

                    innerParam.put("cnt", lackCnt);
                    lackArticleList =  stntSelfLrnMapper.selectStntSelfLrnRecModuleStudymapList(innerParam);

                    totalArticleList.addAll(lackArticleList);

                    if (!lackArticleList.isEmpty()) {
                        for (Map articleListMap : lackArticleList) {
                            excludedArticleIdList.add((String) articleListMap.get("id"));
                        }
                    }
                }

            }
        }

        // 중복 제거 추가
        Set<Object> articleIdSet = new HashSet<>();
        List<Map> totalArticleListRemoveDuplicate = new ArrayList<>();

        for (Map totalArticleMap : totalArticleList) {
            Object id = totalArticleMap.get("id");
            if (articleIdSet.add(id)) {
                totalArticleListRemoveDuplicate.add(totalArticleMap);
            }
        }

        // 지식 요인 클릭 시 다섯 문항
        if (CollectionUtils.isEmpty(totalArticleListRemoveDuplicate) || (CollectionUtils.isNotEmpty(totalArticleListRemoveDuplicate) && totalArticleListRemoveDuplicate.size() < 5)) {
            rtnMap.put("resultOk",false);
            rtnMap.put("resultMsg","더 이상 학습할 수 있는 문항이 없어요!");
        } else {
            List<LinkedHashMap<Object,Object>> list = AidtCommonUtil.filterToList(listItem, totalArticleListRemoveDuplicate);

            int insertCnt = stntSelfLrnMapper.insertStntSelfLrnCreate(paramData);
            int id = MapUtils.getInteger(paramData, "id");
            log.info("자기주도학습 미존재 ID: "+id);

            int cnt = 0;
            if(insertCnt > 0){
                // Rule 기반으로 추출한 문항 목록 추가
                paramData.put("list", list);

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
}