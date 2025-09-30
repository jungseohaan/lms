package com.visang.aidt.lms.api.wrongnote.service;

import com.visang.aidt.lms.api.learning.mapper.AiLearningMapper;
import com.visang.aidt.lms.api.notification.service.StntNtcnService;
import com.visang.aidt.lms.api.textbook.service.TextbookService;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import com.visang.aidt.lms.api.user.service.TchRewardService;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.wrongnote.mapper.StntWrongnoteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class StntWrongnoteService {
    private final StntWrongnoteMapper stntWrongnoteMapper;
    private final AiLearningMapper aiLearningMapper;
    private final StntNtcnService stntNtcnService;
    private final TextbookService textbookService;
    private final StntRewardService stntRewardService;
    private final TchRewardService tchRewardService;

    /**
     * (학생).오답노트 목록 조회
     *
     * @param paramData 입력 파라메터
     * @param pageable  페이징 정보
     * @return Object
     */

    //@Transactional(readOnly = true)
    public Object getWrongnoteList(Map<String, Object> paramData, Pageable pageable) throws Exception {

        // 오답노트 다시풀기 : 제출여부 N 일시 초기화
        stntWrongnoteMapper.deleteWrongNoteRetryDetail(paramData);
        stntWrongnoteMapper.deleteWrongNoteRetryInfo(paramData);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        List<String> wrongnoteListForm = Arrays.asList("noteId", "rowNo", "wrtYmd", "dispWrtYmd", "wonAnwClsfCd", "wonAnwClsfNm", "wonAnwNm", "trgtId", "wonAnwCnt", "noteCmplteYn", "submAgainYn");
        List<LinkedHashMap<Object, Object>> wrongnoteList = new ArrayList<>();


        long total = 0;

        if ("curri".equals(paramData.get("condition").toString())) {
            String[] keyword = paramData.get("keyword").toString().split(",");
            paramData.put("keyword2", keyword);
        }

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        //select

        List<Map> entityList = stntWrongnoteMapper.selectWonAswNoteList(pagingParam);
        List<Map> resultList = new ArrayList<>();

        if (!entityList.isEmpty()) {
            total = Long.valueOf(entityList.get(0).get("fullCount").toString());
            wrongnoteList = AidtCommonUtil.filterToList(wrongnoteListForm, entityList);
            for (LinkedHashMap temp : wrongnoteList) {
                resultList.add(temp);
            }
        }

        // 페이징 정보
        PagingInfo page = AidtCommonUtil.ofPageInfo(resultList, pageable, total);
        paramData.remove("keyword2");
        returnMap.put("wrongNoteList", wrongnoteList);
        returnMap.put("page", page);
        return returnMap;
    }


    /**
     * (학생).오답노트 오답 목록 조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    @Transactional(readOnly = true)
    public Object getWrongnoteWonaswList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        //select 코드명
        Map<String, Object> wonAnwClfCdNm = stntWrongnoteMapper.selectWonAnwClsfCdNm(paramData);

        returnMap.put("wonAnwClsfCd", wonAnwClfCdNm.get("wonAnwClsfCd"));
        returnMap.put("wonAnwClsfNm", wonAnwClfCdNm.get("wonAnwClsfNm"));
        returnMap.put("wonAnwNm", wonAnwClfCdNm.get("wonAnwNm"));

        //brandId 추출
        Map<String, Object> srhMap = new HashMap<>();
        srhMap.put("textbookId", paramData.get("textbkId"));

        int brandId = textbookService.getTextbookBrandId(srhMap);
        paramData.put("brandId", brandId);

        //WonTagInfo List
        List<String> wontagInfoForm = Arrays.asList("wonTag", "wonTagNm");
        List<LinkedHashMap<Object, Object>> wonTagInfoList = AidtCommonUtil.filterToList(wontagInfoForm, stntWrongnoteMapper.selectWonTagInfo(paramData));
        returnMap.put("wonTagInfo", wonTagInfoList);

        //StntResultInfo List
        List<String> selectStntResultInfoForm = Arrays.asList("id", "tabId", "moduleId", "subId", "thumbnail", "targetId", "wonAnwTgId", "subMitAnw", "subMitAnwUrl", "studyMap", "wonTag");
        List<LinkedHashMap<Object, Object>> selectStntResultInfo = AidtCommonUtil.filterToList(selectStntResultInfoForm, stntWrongnoteMapper.selectStntResultInfo(paramData));
        returnMap.put("stntResultList", selectStntResultInfo);

        return returnMap;
    }

    /**
     * (학생) 오답노트 오답 모듈 태그정보 저장(수정)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> saveWrongnoteWonaswTag(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        paramData.put("chk", "OK");
        Map<String, Object> wonAwsNoteMap = stntWrongnoteMapper.selectWonAwsChk(paramData);
        String wonTagVal = MapUtils.getString(wonAwsNoteMap, "wonTag", "");
        String wonTag = "";
        int tempListSize = 0; //태그삭제용

        if (MapUtils.isEmpty(wonAwsNoteMap)) {
            returnMap.put("wonAswId", paramData.get("wonAswId"));
            returnMap.put("wonTag", paramData.get("wonTag"));
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "해당하는 오답노트 ID가 없거나 삭제할 태그정보가 없습니다.");
        } else {
            if ("1".equals(paramData.get("gubun").toString())) { //태그추가
                if ("".equals(wonTagVal)) { //태그 value가 없을때
                    paramData.put("wonTagAdd", paramData.get("wonTag"));

                    //리워드 1지급
                    Map<String, Object> rwdMap = new HashMap<>();

                    rwdMap.put("userId", wonAwsNoteMap.get("wrterId"));
                    rwdMap.put("claId", wonAwsNoteMap.get("claId"));
                    rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                    rwdMap.put("menuSeCd", "4"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝, 5:다시풀기
                    rwdMap.put("sveSeCd", "12"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트, 8:노트정리, 9:다시풀기
                    rwdMap.put("trgtId", wonAwsNoteMap.get("trgtId")); //대상ID
                    rwdMap.put("textbkId", wonAwsNoteMap.get("textbkId"));
                    rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                    rwdMap.put("rwdAmt", 1); //지급일때는 0
                    rwdMap.put("rwdUseAmt", 0); //지급일때는 0

                    log.error(rwdMap.toString());

                    Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);
                    log.error("결과 : " + rewardResult.get("resultOk").toString());

                } else { //태그 value가 있을때
                    // String[] array = wonAwsNoteMap.get("wonTag").toString().split(",");
                    // String[] arrays = new String[array.length+1];
                    paramData.put("wonTagAdd", (StringUtils.isEmpty(wonTagVal) ? "" : wonTagVal + ",") + paramData.get("wonTag"));
                    //리워드 지급하지 않음(따로 로직 필요없음)
                }

                int updateWonAwsNote = stntWrongnoteMapper.updateWonAwsNote(paramData);

            } else if ("2".equals(paramData.get("gubun").toString())) { //태그삭제
                String[] wonTagArray = wonTagVal.split(",");

                List<String> tempList = new ArrayList<>(Arrays.asList(wonTagArray));
                tempListSize = tempList.size(); //삭제할 태그갯수

                for (int i = 0; i < tempListSize; i++) {
                    if (wonTagArray[i].toString().equals(paramData.get("wonTag").toString())) {
                        tempList.remove(i);
                    }
                }

                for (int j = 0; j < tempList.size(); j++) {
                    if (j == tempList.size() - 1) {
                        wonTag += tempList.get(j);
                    } else {
                        wonTag += tempList.get(j) + ",";
                    }
                }

                paramData.put("wonTagAdd", wonTag);
                int updateWonAwsNote = stntWrongnoteMapper.updateWonAwsNote(paramData);
            }

        }

        paramData.put("chk", "");
        wonAwsNoteMap = stntWrongnoteMapper.selectWonAwsChk(paramData);

        if ("2".equals(paramData.get("gubun").toString())) { //삭제일때
            //삭제할 태그갯수가 0이상이고 결과값이 없으면 리워드 차감(-1)
            if (tempListSize > 0 && MapUtils.getString(wonAwsNoteMap, "wonTag").isEmpty()) {
                Map<String, Object> rwdMap = new HashMap<>();

                rwdMap.put("userId", wonAwsNoteMap.get("wrterId"));
                rwdMap.put("claId", wonAwsNoteMap.get("claId"));
                rwdMap.put("seCd", "2"); //구분 1:획득, 2:사용
                rwdMap.put("menuSeCd", "4"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝, 5:다시풀기
                rwdMap.put("sveSeCd", "12"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트, 8:노트정리, 9:다시풀기
                rwdMap.put("trgtId", wonAwsNoteMap.get("trgtId")); //대상ID
                rwdMap.put("textbkId", wonAwsNoteMap.get("textbkId"));
                rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                rwdMap.put("rwdAmt", 0);
                rwdMap.put("rwdUseAmt", 1); //차감 1

                log.error(rwdMap.toString());

                Map<String, Object> rewardResult = stntRewardService.useReward(rwdMap);
            }
        }
        paramData.remove("wonTagAdd");
        paramData.remove("chk");
        returnMap.put("wonAswId", paramData.get("wonAswId"));
        returnMap.put("wonTag", wonAwsNoteMap.get("wonTag"));
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "저장완료");

        return returnMap;
    }

    @Deprecated
    /** 테스트 용으로 작성한 api임 사용하지 말것. */
    /**
     * (학생) 오답노트 생성 - 테스트 용으로 작성한 api임 사용하지 말것.
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> createWonAswNote(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("resultOk", false);

        //작성자ID
        if (paramData.get("wrterId") == null || ("").equals(paramData.get("wrterId"))) {
            returnMap.put("resultMsg", "wrterId를 입력해주세요");
            return returnMap;
        }
        //작성일자
        /*
        if (paramData.get("wrtYmd") == null || ("").equals(paramData.get("wrtYmd"))) {
            returnMap.put("resultMsg", "wrtYmd를 입력해주세요");
            return returnMap;
        }*/
        //오답노트 분류코드
        if (paramData.get("wonAnwClsfCd") == null || ("").equals(paramData.get("wonAnwClsfCd"))) {
            returnMap.put("resultMsg", "wonAnwClsfCd를 입력해주세요");
            return returnMap;
        }
        //오답노트명
//        if (paramData.get("wonAnwNm") == null || ("").equals(paramData.get("wonAnwNm"))) {
//            returnMap.put("resultMsg", "wonAnwNm를 입력해주세요");
//            return returnMap;
//        }
        //탭ID
//        if (paramData.get("tabId") == null || ("").equals(paramData.get("tabId"))) {
//            returnMap.put("resultMsg", "tabId를 입력해주세요");
//            return returnMap;
//        }
        //모듈ID
//        if (paramData.get("moduleId") == null || ("").equals(paramData.get("moduleId"))) {
//            returnMap.put("resultMsg", "moduleId를 입력해주세요");
//            return returnMap;
//        }
        //subId
        //module_id조건이 들어가있는 건은 sub_id가 쌍으로 움직인다.
        //조회/등록/수정시 module_id 파라미터를 받는다면 sub_id 가 쌍으로 움직여야 한다.
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }
        //오답결과ID
        if (paramData.get("wonAnwTgId") == null || ("").equals(paramData.get("wonAnwTgId"))) {
            returnMap.put("resultMsg", "wonAnwTgId를 입력해주세요");
            return returnMap;
        }


        Map<String, Object> wonAnwNm = new HashMap<>();
        if ("1".equals(paramData.get("wonAnwClsfCd").toString())) { //수업
            wonAnwNm = stntWrongnoteMapper.findWrongnoteWonAnsNm1(paramData);
            if (MapUtils.isNotEmpty(wonAnwNm)) {
                paramData.put("wonAnwNm", wonAnwNm.get("stdDatNm"));
            } else {
                paramData.put("wonAnwNm", "-");
            }
        } else if ("2".equals(paramData.get("wonAnwClsfCd").toString())) { //자기주도학습
            paramData.put("tabId", 0);

            wonAnwNm = stntWrongnoteMapper.findWrongnoteWonAnsNm2(paramData);
            if (MapUtils.isNotEmpty(wonAnwNm)) {
                paramData.put("wonAnwNm", wonAnwNm.get("stdNm"));
            } else {
                paramData.put("wonAnwNm", "-");
            }

        } else if ("3".equals(paramData.get("wonAnwClsfCd").toString())) { //과제
            Map<String, Object> taskDetail = stntWrongnoteMapper.selectStntWrongnoteTaskDetail(paramData);
            if (!("1".equals(taskDetail.get("mrkTy").toString()) && "Y".equals(taskDetail.get("eakAt").toString())
                    && "2".equals(taskDetail.get("errata").toString()))) {
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "오답노트 대상이 아님");
                return returnMap;
            }
            paramData.put("tabId", 0);

            wonAnwNm = stntWrongnoteMapper.findWrongnoteWonAnsNm3(paramData);
            if (MapUtils.isNotEmpty(wonAnwNm)) {
                paramData.put("wonAnwNm", wonAnwNm.get("taskNm"));
            } else {
                paramData.put("wonAnwNm", "-");
            }


        } else if ("4".equals(paramData.get("wonAnwClsfCd").toString())) { //평가
            Map<String, Object> evlDetail = stntWrongnoteMapper.selectStntWrongnoteEvlDetail(paramData);
            if (!("1".equals(evlDetail.get("mrkTy").toString()) && "Y".equals(evlDetail.get("eakAt").toString())
                    && "2".equals(evlDetail.get("errata").toString()))) {
                returnMap.put("resultOk", true);
                returnMap.put("resultMsg", "오답노트 대상이 아님");
                return returnMap;
            }
            paramData.put("tabId", 0);

            wonAnwNm = stntWrongnoteMapper.findWrongnoteWonAnsNm4(paramData);
            if (MapUtils.isNotEmpty(wonAnwNm)) {
                paramData.put("wonAnwNm", wonAnwNm.get("evlNm"));
            } else {
                paramData.put("wonAnwNm", "-");
            }
        }

        int insertWonAswNote = stntWrongnoteMapper.insertWonAwsNote(paramData);

        if (insertWonAswNote > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }

    /**
     * (학생) 오답노트 삭제
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> deleteWonAswNote(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("resultOk", false);

        //작성자ID
        if (paramData.get("wrterId") == null || ("").equals(paramData.get("wrterId"))) {
            returnMap.put("resultMsg", "wrterId를 입력해주세요");
            return returnMap;
        }
        //오답노트 분류코드
        if (paramData.get("wonAnwClsfCd") == null || ("").equals(paramData.get("wonAnwClsfCd"))) {
            returnMap.put("resultMsg", "wonAnwClsfCd를 입력해주세요");
            return returnMap;
        }
        //탭ID
        if (paramData.get("tabId") == null || ("").equals(paramData.get("tabId"))) {
            returnMap.put("resultMsg", "tabId를 입력해주세요");
            return returnMap;
        }
        //모듈ID
        if (paramData.get("moduleId") == null || ("").equals(paramData.get("moduleId"))) {
            returnMap.put("resultMsg", "moduleId를 입력해주세요");
            return returnMap;
        }
        //subId
        //module_id조건이 들어가있는 건은 sub_id가 쌍으로 움직인다.
        //조회/등록/수정시 module_id 파라미터를 받는다면 sub_id 가 쌍으로 움직여야 한다.
        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }
        //오답결과ID
        if (paramData.get("wonAnwTgId") == null || ("").equals(paramData.get("wonAnwTgId"))) {
            returnMap.put("resultMsg", "wonAnwTgId를 입력해주세요");
            return returnMap;
        }


        if (!"1".equals(paramData.get("wonAnwClsfCd").toString())) {
            paramData.put("tabId", 0);
        }

        int deleteWonAwsNote = stntWrongnoteMapper.deleteWonAwsNote(paramData);

        if (deleteWonAwsNote > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "삭제완료");
        } else {
            returnMap.put("resultMsg", "삭제실패");
        }


        return returnMap;
    }


    /**
     * (학생) 과제 조건부 오답노트 생성 - 과제(won_anw_clsf_cd:3)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> createStntWrongnoteTaskId(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //과제ID
        if (paramData.get("taskId") == null || ("").equals(paramData.get("taskId"))) {
            returnMap.put("resultMsg", "taskId를 입력해주세요");
            return returnMap;
        }


        //채점유형(mrk_ty):1.자동, 정오표(errata):2.오답 인 대상만
        int insertWonAwsNote = stntWrongnoteMapper.insertStntWrongnoteTaskId(paramData);

        if (insertWonAwsNote > 0) {

            List<Map> wrongnoteTaskInfoInfo = stntWrongnoteMapper.findWrongnoteTaskInfo(paramData);
            for (Map temp : wrongnoteTaskInfoInfo) {
                Map<String, Object> ntcnMap = new HashMap<>();
                ntcnMap.put("userId", temp.get("wrterId"));
                ntcnMap.put("rcveId", temp.get("stntId"));
                ntcnMap.put("textbkId", temp.get("textbkId"));
                ntcnMap.put("claId", temp.get("claId"));
                ntcnMap.put("trgetCd", "S");
                ntcnMap.put("linkUrl", paramData.get("taskId"));
                ntcnMap.put("stntNm", temp.get("flnm"));
                ntcnMap.put("ntcnTyCd", "3");
                ntcnMap.put("trgetTyCd", "13");
                ntcnMap.put("ntcnCn", "오답노트에 문제가 추가되었습니다.");
                stntNtcnService.createStntNtcnSave(ntcnMap);
            }

            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }

    /**
     * (학생) 평가 조건부 오답노트 생성 - 평가(won_anw_clsf_cd:4)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Map<String, Object> createStntWrongnoteEvlId(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //평가ID
        if (paramData.get("evlId") == null || ("").equals(paramData.get("evlId"))) {
            returnMap.put("resultMsg", "evlId를 입력해주세요");
            return returnMap;
        }

        //오답노트 초기화
        int deleteWonAnsNote = stntWrongnoteMapper.deleteStntWrongnoteEvlId(paramData);

        //채점유형(mrk_ty):1.자동, 정오표(errata):2.오답 인 대상만
        int insertWonAwsNote = stntWrongnoteMapper.insertStntWrongnoteEvlId(paramData);

        if (insertWonAwsNote > 0) {
            List<Map> wrongnoteEvlInfo = stntWrongnoteMapper.findWrongnoteEvlInfo(paramData);
            if (!wrongnoteEvlInfo.isEmpty()) {
                for (Map temp : wrongnoteEvlInfo) {
                    Map<String, Object> ntcnMap = new HashMap<>();
                    ntcnMap.put("userId", temp.get("wrterId"));
                    ntcnMap.put("rcveId", temp.get("stntId"));
                    ntcnMap.put("textbkId", temp.get("textbkId"));
                    ntcnMap.put("claId", temp.get("claId"));
                    ntcnMap.put("trgetCd", "S");
                    ntcnMap.put("linkUrl", paramData.get("evlId"));
                    ntcnMap.put("stntNm", temp.get("flnm"));
                    ntcnMap.put("ntcnTyCd", "3");
                    ntcnMap.put("trgetTyCd", "13");
                    ntcnMap.put("ntcnCn", "오답노트에 문제가 추가되었습니다.");
                    stntNtcnService.createStntNtcnSave(ntcnMap);
                }
            }
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public String getWonAnwNm(Map<String, Object> paramData) throws Exception {
        return stntWrongnoteMapper.selectWonAnwNm(paramData);
    }


    public Map<String, Object> createRetry(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        List<String> stdWonAswRetryItem = Arrays.asList("mamoymId", "id", "textbkId", "wrterId", "wrtYmd", "wonAnwClsfCd", "wonAnwNm", "stdWonAnwId", "srcTextbkId"
                , "srcWrterId", "srcWrtYmd", "srcTrgtId", "articleId", "subId", "subMitAnw", "subMitAnwUrl", "errata", "eakSttsCd", "eakAt", "eakStDt", "eakEdDt");

        List<String> stdHistErra = Arrays.asList("errata", "stdNm", "stdType", "stdTypeNm", "stdDate");

        int cntStdWonAswInfo = stntWrongnoteMapper.createStdWonAswInfo(paramData);
        log.error("[0]paramData .... " + paramData.toString());
        log.error("[1]paramData .... " + paramData.toString());

        List<Map<String, Object>> wonAnwInfo = (List<Map<String, Object>>) paramData.get("wonAnwInfo");

        String stdWonAnwId = "";
        if (null != MapUtils.getString(paramData, "stdWonAnwId") && !"".equals(MapUtils.getString(paramData, "stdWonAnwId"))) {
            stdWonAnwId = MapUtils.getString(paramData, "stdWonAnwId");
            int cntStdWonAswDetail = stntWrongnoteMapper.createStdWonAswDetail(stdWonAnwId, wonAnwInfo);
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");

            return returnMap;
        }

        List<LinkedHashMap<Object, Object>> stdWonAswRetryList = AidtCommonUtil.filterToList(stdWonAswRetryItem, stntWrongnoteMapper.selectStdWonAswRetry(stdWonAnwId));

        LinkedHashMap<Object, Object> stdWonAswRetryArticle = new LinkedHashMap<>();

        // 아티클 별 {학생ID, 아티클ID, 서브ID} 정오이력
        for (int ii = 0; ii < stdWonAswRetryList.size(); ii++) {
            LinkedHashMap<Object, Object> temp = new LinkedHashMap<>();
            temp = stdWonAswRetryList.get(ii);

            Map<String, Object> pHistErraMap = new HashMap<>();
            pHistErraMap.put("mamoymId", MapUtils.getString(temp, "mamoymId", ""));
            pHistErraMap.put("articleId", MapUtils.getString(temp, "articleId", ""));
            pHistErraMap.put("subId", MapUtils.getString(temp, "subId", "0"));

            List<LinkedHashMap<Object, Object>> histErraMap = AidtCommonUtil.filterToList(stdHistErra, stntWrongnoteMapper.getHistErra(pHistErraMap));

            temp.put("histErra", histErraMap);
        }

        if (stdWonAswRetryList.size() > 0) {
            returnMap.put("stdWonAswRetryList", stdWonAswRetryList);
            returnMap.put("stdWonAnwId", stdWonAnwId);
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "시스템 점검 바랍니다.");
        }

        return returnMap;
    }

    public Map<String, Object> saveWrongNoteRetry(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        Map<String, Object> checkSubmitYn = stntWrongnoteMapper.checkSubmitYn(paramData);
        if ("Y".equals(MapUtils.getString(checkSubmitYn, "submAt", ""))) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
            return returnMap;
        }

        Integer subId = MapUtils.getInteger(paramData, "subId");
        if (ObjectUtils.isEmpty(subId)) {
            paramData.put("subId", 0);
        }

        // 문항 제출 데이터 반영
        int savedWrongNoteRetryCnt = stntWrongnoteMapper.saveWrongNoteRetry(paramData);

        // detail  테이블의 eak_stts_cd < 5 카운트해서 5보다 작은게 없으면 info 테이블에 완료로 처리
        if (savedWrongNoteRetryCnt > 0) {
            int savedWrongNoteRetryFinalDataCnt = stntWrongnoteMapper.saveWrongNoteRetryFinalData(paramData);
            log.info("checking Final data : " + savedWrongNoteRetryFinalDataCnt);
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
            return returnMap;
        }

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        return returnMap;
    }

    public Map<String, Object> submitWrongNoteRetry_bak(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // detail 완료처리
        int submitWrongNoteRetryDetailCnt = stntWrongnoteMapper.submitWrongNoteRetryDetail(paramData);

        // info 완료처리
        int submitWrongNoteRetrylInfoCnt = stntWrongnoteMapper.submitWrongNoteRetrylInfo(paramData);

        // 리워드 지급
        Map<String, Object> getWrongNoteRetryRwd = stntWrongnoteMapper.getWrongNoteRetryRwd(paramData);

        int rwdCount = 0;
        if (MapUtils.isNotEmpty(getWrongNoteRetryRwd)) {

            rwdCount = MapUtils.getIntValue(getWrongNoteRetryRwd, "totalWrd", 0);

            if (rwdCount > 0) {
                //리워드
                //모든 문제를 푼 경우에는 resultDetail = 0 임.
                //if (resultDetail > 0) {
                Map<String, Object> rwdMap = new HashMap<>();

                rwdMap.put("userId", getWrongNoteRetryRwd.get("srcWrterId"));
                rwdMap.put("claId", getWrongNoteRetryRwd.get("claId"));
                rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                rwdMap.put("menuSeCd", "4"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝, 5:다시풀기
                rwdMap.put("sveSeCd", "13"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트, 8:노트정리, 9:다시풀기
                rwdMap.put("trgtId", getWrongNoteRetryRwd.get("srcTrgtId")); //대상ID
                rwdMap.put("textbkId", getWrongNoteRetryRwd.get("srcTextbkId"));
                rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                rwdMap.put("rwdAmt", rwdCount); //지급일때는 0
                rwdMap.put("rwdUseAmt", 0); //지급일때는 0

                log.error(rwdMap.toString());

                Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);
                log.error("결과 : " + rewardResult.get("resultOk").toString());
            }
        }

        // 오답노트 생성
        int createWonAswForRetryInfoCnt = stntWrongnoteMapper.createWonAswForRetry(paramData);

        returnMap.put("rwdAmt", rwdCount); //적립된 리워드양
        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");

        return returnMap;
    }


    public Map<String, Object> submitWrongNoteRetry(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // failedArticleInfo List<Map<String, Object>> 조회하여 articleId 추출
        Object failedArticleInfoObj = paramData.get("failedArticleInfoList");
        List<String> articleIds = new ArrayList<>();
        List<Map<String, Object>> failedArticleInfoList = new ArrayList<>();

        if (failedArticleInfoObj != null && failedArticleInfoObj instanceof List) {
            failedArticleInfoList = (List<Map<String, Object>>) failedArticleInfoObj;
            for (Map<String, Object> itemMap : failedArticleInfoList) {
                String articleId = MapUtils.getString(itemMap, "articleId");
                if (articleId != null) {
                    articleIds.add(articleId);
                }
            }
        }

        // articleIds를 paramData에 추가 (빈 리스트여도 상관없음)
        paramData.put("excludedArticleIds", articleIds);

        // detail 완료처리
        int submitWrongNoteRetryDetailCnt = stntWrongnoteMapper.submitWrongNoteRetryDetail(paramData);

        // info 완료처리
        int submitWrongNoteRetrylInfoCnt  = stntWrongnoteMapper.submitWrongNoteRetrylInfo(paramData);

        // 리워드 지급
        Map<String, Object> getWrongNoteRetryRwd = stntWrongnoteMapper.getWrongNoteRetryRwd(paramData);

        int rwdCount = 0;
        if(MapUtils.isNotEmpty(getWrongNoteRetryRwd)) {

            rwdCount = MapUtils.getIntValue(getWrongNoteRetryRwd, "totalWrd", 0);

            if (rwdCount > 0) {
                //리워드
                //모든 문제를 푼 경우에는 resultDetail = 0 임.
                //if (resultDetail > 0) {
                Map<String, Object> rwdMap = new HashMap<>();

                rwdMap.put("userId", getWrongNoteRetryRwd.get("srcWrterId"));
                rwdMap.put("claId", getWrongNoteRetryRwd.get("claId"));
                rwdMap.put("seCd", "1"); //구분 1:획득, 2:사용
                rwdMap.put("menuSeCd", "4"); //메뉴구분 1:교과서, 2:과제, 3:평가, 4:셀프러닝, 5:다시풀기
                rwdMap.put("sveSeCd", "13"); //서비스구분 1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트, 8:노트정리, 9:다시풀기
                rwdMap.put("trgtId", getWrongNoteRetryRwd.get("srcTrgtId")); //대상ID
                rwdMap.put("textbkId", getWrongNoteRetryRwd.get("srcTextbkId"));
                rwdMap.put("rwdSeCd", "1"); //리워드구분 1:하트, 2:스타
                rwdMap.put("rwdAmt", rwdCount); //지급일때는 0
                rwdMap.put("rwdUseAmt", 0); //지급일때는 0

                log.error(rwdMap.toString());

                Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);
                log.error("결과 : " + rewardResult.get("resultOk").toString());
            }
        }

        // failedArticleInfo가 있을 때 saveWrongNoteRetry 호출
        if (!failedArticleInfoList.isEmpty()) {
            for (Map<String, Object> item : failedArticleInfoList) {
                try {
                    Integer subId = MapUtils.getInteger(item, "subId");
                    if (ObjectUtils.isEmpty(subId)) {
                        item.put("subId", 0);
                    }

                    // 문항 제출 데이터 반영
                    int failedIemResult = stntWrongnoteMapper.saveWrongNoteRetry(item);
                    log.info("failedIemResult:{}", failedIemResult);

                    if (failedIemResult > 0) {
                        // detail 테이블의 eak_stts_cd < 5 카운트해서 5보다 작은게 없으면 info 테이블에 완료로 처리
                        int savedWrongNoteRetryFinalDataCnt = stntWrongnoteMapper.saveWrongNoteRetryFinalData(item);
                        log.info("savedWrongNoteRetryFinalDataCnt:{}", savedWrongNoteRetryFinalDataCnt);
                    }

                } catch (Exception e) {
                    log.error("saveWrongNoteRetry 호출 중 오류 발생: {}", item, e);
                    throw e;
                }
            }
        }

        String IsCurrenttabId = stntWrongnoteMapper.getCurrentTabId(paramData);

        int wonAnwClsfCd = 0;
        // 현재 오답노트의 won_anw_clsf_cd 값 확인
        if ("N".equals(IsCurrenttabId)){
            wonAnwClsfCd =  stntWrongnoteMapper.getCurrentWonAnwClsfCdOfWrongNote(paramData);
        }

        // won_anw_clsf_cd = 5인 경우만 계층적 오답노트명 생성
        if (wonAnwClsfCd == 5) {
            String hierarchicalWrongNoteName = generateHierarchicalWrongNoteName(paramData);
            if (hierarchicalWrongNoteName != null) {
                paramData.put("hierarchicalWrongNoteName", hierarchicalWrongNoteName);
            }
        }

        // 오답노트 생성
        int createWonAswForRetryInfoCnt  = stntWrongnoteMapper.createWonAswForRetry(paramData);

        returnMap.put("rwdAmt", rwdCount); //적립된 리워드양
        returnMap.put("resultOk" , true);
        returnMap.put("resultMsg", "성공");

        return returnMap;
    }


    /**
     * 계층적 오답노트명 생성 (이미 won_anw_clsf_cd = 5인 것 확인됨)
     */
    private String generateHierarchicalWrongNoteName(Map<String, Object> paramData) throws Exception {
        // 현재 오답노트명 조회
        String currentNoteName = stntWrongnoteMapper.getCurrentWrongNoteName(paramData);

        if (currentNoteName == null || currentNoteName.trim().isEmpty()) {
            log.error("현재 오답노트명을 찾을 수 없습니다. stdWonAnwId: " + paramData.get("stdWonAnwId"));
            throw new IllegalArgumentException("현재 오답노트명을 찾을 수 없습니다");
        }

        // 예외가 발생하면 자연스럽게 상위로 전파
        return generateNextHierarchicalName(currentNoteName, paramData);
    }

    /**
     * 계층적 다음 단계 오답노트명 생성
     */
    private String generateNextHierarchicalName(String currentNoteName, Map<String, Object> paramData) throws Exception {

            Pattern pattern = Pattern.compile("\\[다시풀기\\(([0-9-]+)\\)\\]\\s*(.*)");
            Matcher matcher = pattern.matcher(currentNoteName);

            if (!matcher.find()) {
                log.error("=== 디버깅: 패턴 매칭 실패");
                return null;
            }

            String numberPart = matcher.group(1);
            String originalName = matcher.group(2).trim();

            String[] levels = numberPart.split("-");

            if (levels.length >= 3) {
                String parentPath = String.join("-", Arrays.copyOf(levels, levels.length - 1));
                String nextNumber = getNextRetryNumber(paramData, parentPath, originalName);
                return "[다시풀기(" + parentPath + "-" + nextNumber + ")] " + originalName;
            } else {
                String nextNumber = getNextRetryNumber(paramData, numberPart, originalName);
                return "[다시풀기(" + numberPart + "-" + nextNumber + ")] " + originalName;
            }
    }

    /**
     * 같은 부모를 가진 형제 노트들 중 다음 번호 조회
     */
    private String getNextRetryNumber(Map<String, Object> paramData, String parentPath, String originalName) throws Exception {

            Map<String, Object> queryParam = new HashMap<>();
            queryParam.put("stdWonAnwId", paramData.get("stdWonAnwId"));
            queryParam.put("parentPath", parentPath);
            queryParam.put("originalName", originalName);

            Integer maxNumber = stntWrongnoteMapper.getMaxRetryNumber(queryParam);
            return String.valueOf(maxNumber == null ? 1 : maxNumber + 1);

    }

    @Transactional(readOnly = true)
    public Object getWrongnoteStatis(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        // 통계 데이터
        Map<String, Object> getWrongNoteStatisInfo = stntWrongnoteMapper.getWrongNoteStatis(paramData);

        int textbkId;

        textbkId = stntWrongnoteMapper.getTextbkId(paramData);

        if (textbkId == 0) {
            // 교과서 단위에서 체크
            textbkId = stntWrongnoteMapper.getTextbkId2(paramData);
        }

        if (textbkId > 0) {
            int brandId = stntWrongnoteMapper.findBrandId(textbkId);

            List<Map> frequentlyMisunderstoodTConceptInfo;

            if (brandId == 1) {
                frequentlyMisunderstoodTConceptInfo = stntWrongnoteMapper.frequentlyMisunderstoodTConcept(paramData);
            } else {
                frequentlyMisunderstoodTConceptInfo  = stntWrongnoteMapper.frequentlyMisunderstoodTConceptForEng(paramData);
                convertKoreanEvaluationAreaToEnglish(frequentlyMisunderstoodTConceptInfo);
            }

            // 자주틀리는이유 탑 3
            List<Map> frequentlyErrorCausesInfo;

            if (brandId == 1) {
                frequentlyErrorCausesInfo = stntWrongnoteMapper.frequentlyErrorCauses(paramData);
            } else {
                frequentlyErrorCausesInfo = stntWrongnoteMapper.frequentlyErrorCausesForEng(paramData);
            }

            returnMap.put("frequentlyMisunderstoodTConceptInfo", frequentlyMisunderstoodTConceptInfo);
            returnMap.put("frequentlyErrorCausesInfo", frequentlyErrorCausesInfo);
        } else {
            returnMap.put("frequentlyMisunderstoodTConceptInfo", null);
            returnMap.put("frequentlyErrorCausesInfo", null);
        }

        returnMap.put("getWrongNoteStatisInfo", getWrongNoteStatisInfo);

        return returnMap;
    }

    private void convertKoreanEvaluationAreaToEnglish(List<Map> resultList) {
        // value 변환 매핑
        Map<String, String> valueMapping = new HashMap<>();
        valueMapping.put("문법", "Grammar");
        valueMapping.put("어휘", "Vocabulary");
        valueMapping.put("발음", "Pronunciation");
        valueMapping.put("듣기", "Listening");
        valueMapping.put("읽기", "Reading");
        valueMapping.put("쓰기", "Writing");
        valueMapping.put("말하기", "Speaking");

        // 각 Map의 모든 value를 확인하여 변환
        for (Map<String, Object> map : resultList) {
            Object wrongConceptValue = map.get("wrongConcept");
            if (wrongConceptValue != null) {
                String currentValue = wrongConceptValue.toString();
                if (valueMapping.containsKey(currentValue)) {
                    map.put("wrongConcept", valueMapping.get(currentValue));
                }
            }
        }
    }

}
