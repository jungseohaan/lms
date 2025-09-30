package com.visang.aidt.lms.api.lecture.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visang.aidt.lms.api.act.service.TchActService;
import com.visang.aidt.lms.api.lecture.mapper.TchLectureReportMapper;
import com.visang.aidt.lms.api.materials.service.TchMdulQstnService;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.wrongnote.service.StntWrongnoteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@Service
@AllArgsConstructor
public class TchLectureReportService {
    private final TchLectureReportMapper tchLectureReportMapper;

    private final StntWrongnoteService stntWrongnoteService;
    private final TchMdulQstnService tchMdulQstnService;
    private final TchActService tchActService;

    @Transactional(readOnly = true)
    public Object findReportLectureResultList(Map<String, Object> paramData) throws Exception {

        List<String> resultItem = Arrays.asList("id", "stdDtaNm", "crcuNm", "setsId"); // main 정보
        List<String> tabInfoItem = Arrays.asList("tabId", "tabNm", "tabSeq", "categoryCd", "categoryNm", "setsId", "aiCstmzdStdMthdSeCd", "eamTrget"); // tab 정보

        int selectedTabId = 0; // 선택된 tabId
        Map resultMap = new HashMap();

        String textbId = paramData.get("textbkId") != null ? paramData.get("textbkId").toString() : "";

        // 초등 수학 교과서 목록 정의
        List<String> specialTextbIds = Arrays.asList("1175", "1197", "1198", "1199");

        // textbId가 특정 목록에 포함되어 있는지 확인
        boolean isSpecialTextbId = !textbId.isEmpty() && specialTextbIds.contains(textbId);

        List<Map> _tabInfoList;

        // 초등 수학일 경우, 탭 목록에서 '수학 마을' 탭 제거 요청(Jira 이슈 #1133) - 20250331
        if (isSpecialTextbId) {
            _tabInfoList = tchLectureReportMapper.findTchLectureReportTabInfoListForElementMath(paramData);
        } else {
            _tabInfoList = tchLectureReportMapper.findTchLectureReportTabInfoList(paramData);
        }

        if (paramData.get("tabId") == null
                || StringUtils.isBlank((String) paramData.get("tabId"))
                || StringUtils.equals((String) paramData.get("tabId"), "0") ) { // 선택된(받아온) 탭이 없을때
            log.debug("선택된 탭 없음:{}", paramData.get("tabId"));

            if (!_tabInfoList.isEmpty()) {
                selectedTabId = (int) _tabInfoList.get(0).get("tabId");
                paramData.put("tabId", selectedTabId); //
            }

        } else { // 선택된 탭이 있을때
            log.debug("선택된 탭 있음:{}", paramData.get("tabId"));
            selectedTabId = Integer.valueOf((String)paramData.get("tabId"));
        }


        // 유효한 탭 정보가 있을때
        if (selectedTabId > 0 ) {

            Map tmpMap = tchLectureReportMapper.getTchLectureReportStdDtaInfo(paramData);
            if (tmpMap != null) {
                resultMap= AidtCommonUtil.filterToMap(resultItem, tmpMap);

                if (StringUtils.isEmpty(MapUtils.getString(resultMap,"setsId"))) {
                    List<Map> mdulDtaInfoList = tchLectureReportMapper.findTchLectureReportMdulDtaInfoList_setsIdNull(paramData);
                    resultMap.put("mdulDtaInfoList", mdulDtaInfoList);


                    // 학생 학습자료 정오표 정보
                    List<Map> stntDtaErrataInfoList = tchLectureReportMapper.findTchLectureReportStntDtaErrataInfoList_setsIdNull(paramData);
                    resultMap.put("stntDtaErrataInfoList", stntDtaErrataInfoList);
                } else {
                    // 모듈 학습자료 정보 목록
                    List<Map> mdulDtaInfoList = tchLectureReportMapper.findTchLectureReportMdulDtaInfoList(paramData);
                    resultMap.put("mdulDtaInfoList", mdulDtaInfoList);


                    // 학생 학습자료 정오표 정보
                    List<Map> stntDtaErrataInfoList = tchLectureReportMapper.findTchLectureReportStntDtaErrataInfoList(paramData);
                    resultMap.put("stntDtaErrataInfoList", stntDtaErrataInfoList);
                }
            } else {
                resultMap.put("id", null);
                resultMap.put("stdDtaNm", null);
                resultMap.put("crcuNm", null);
                resultMap.put("mdulDtaInfoList", Collections.EMPTY_LIST);
                resultMap.put("stntDtaErrataInfoList", Collections.EMPTY_LIST);
            }



        } else { // 탭정보가 없을때
            resultMap.put("id", null);
            resultMap.put("stdDtaNm", null);
            resultMap.put("crcuNm", null);
            resultMap.put("mdulDtaInfoList", Collections.EMPTY_LIST);
            resultMap.put("stntDtaErrataInfoList", Collections.EMPTY_LIST);
        }

        Map<String, Object> crcuNmMap = new HashMap<>();
        String crcuNm = null;
        if (selectedTabId > 0 ) {
            crcuNmMap = tchLectureReportMapper.tcCurriculumTextTabId(paramData);
        } else {
            crcuNmMap = tchLectureReportMapper.tcCurriculumTextElse(paramData);
        }

        if (!ObjectUtils.isEmpty(crcuNmMap)){
            crcuNm = MapUtils.getString(crcuNmMap, "idPathNm", null);
            resultMap.put("crcuNm", crcuNm);
        }

        List<LinkedHashMap<Object, Object>> tabInfoList = AidtCommonUtil.filterToList(tabInfoItem, _tabInfoList);

        resultMap.put("tabInfoList", tabInfoList);

        return resultMap;

    }

    @Transactional(readOnly = true)
    public Object findReportLectureResultDetailMdulList(Map<String, Object> paramData) throws Exception {

        List<String> mainItem = Arrays.asList("id","stdDtaNm","crcuNm");
        List<String> mdulDtaInfoListItem = Arrays.asList("setsId", "dtaIemId", "subId", "thumbnail","targetCnt","submitCnt","avgCorrectRate");
        List<String> mdulImageInfoItem = Arrays.asList("url", "image"); // image 정보
        List<String> mdulInfoItem = Arrays.asList("curriYear", "curriSchool", "curriSubject", "curriGrade");
        List<String> classAnalysisInfoItem = Arrays.asList("correctRate", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "answerRateStr");

        // 학습자료 정보
        Map tmpMap = tchLectureReportMapper.getTchLectureReportStdDtaInfo(paramData);

        // 필요한 item만 필터링
        Map resultMap = AidtCommonUtil.filterToMap(mainItem, tmpMap);

        // 모듈 학습자료 정보 목록
        List<Map> mdulDtaInfoList = new ArrayList<Map>();

        //List<Map> list = tchLectureReportMapper.findReportLectureResultDetailMdulList(paramData);

        Map<String,Object> mdulDtaInfo = tchLectureReportMapper.findTchLectureReportMdulDtaInfo(paramData);

        // null 체크 및 빈 맵 초기화
        if (mdulDtaInfo == null) {
            mdulDtaInfo = new HashMap<>(); // 빈 맵으로 초기화
        }

        // 지문응답률 세팅
        List<String> answers = tchLectureReportMapper.findTchLectureReportMdulDtaInfo_answers(paramData);
        if (CollectionUtils.isNotEmpty(answers)) {
            mdulDtaInfo.put("answerRateStr", AidtCommonUtil.getAnswerCountString(answers));
        }

        List<Map> list = ObjectUtils.isEmpty(mdulDtaInfo) ? List.of() : List.of(mdulDtaInfo);

        List<String> articleIdList = CollectionUtils.emptyIfNull(list).stream()
                .map(m -> ((String) m.get("dtaIemId")))
                .toList();

        // 값이 있으면
        if (!articleIdList.isEmpty()) {
            Map<String, List<String>> idParam = Map.of("articleIdList", articleIdList);

            List<Map> commentList = tchLectureReportMapper.findArticleCommentInfo(idParam);

            // 코멘터리 정보를 put 한다.
            list.forEach(_map -> {
                Map mdulMap = AidtCommonUtil.filterToMap(mdulDtaInfoListItem, _map);
                mdulMap.put("mdulImageInfo", AidtCommonUtil.filterToMap(mdulImageInfoItem, _map));
                mdulMap.put("mdulInfo", AidtCommonUtil.filterToMap(mdulInfoItem, _map));
                mdulMap.put("classAnalysisInfo", AidtCommonUtil.filterToMap(classAnalysisInfoItem, _map));
                mdulMap.put("mdulImageInfo", AidtCommonUtil.filterToMap(mdulImageInfoItem, _map));

                commentList.stream()
                        .filter(_comment -> _map.get("dtaIemId").equals(_comment.get("articleId")))
                        .findFirst()
                        .ifPresentOrElse(
                                comment -> mdulMap.put("commentary", comment),
                                () -> mdulMap.put("commentary", null)
                        );

                mdulDtaInfoList.add(mdulMap);
            });
        }

        Map<String, Object> crcuNmMap = new HashMap<>();
        String crcuNm = null;

        crcuNmMap = tchLectureReportMapper.tcCurriculumTextTabId(paramData);
        if (!ObjectUtils.isEmpty(crcuNmMap)){
            crcuNm = MapUtils.getString(crcuNmMap, "idPathNm", null);
            resultMap.put("crcuNm", crcuNm);
        }

        resultMap.put("mdulDtaInfo", mdulDtaInfoList);

        return resultMap;

    }

    @Transactional(readOnly = true)
    public Object findReportLectureResultDetailStnt(Map<String, Object> paramData) throws Exception {

        /* 원복 - 미제출자 정오표 수정을 위한 데이터 생성
        Map<String, Object> stdDtaMap = new HashMap<>();
        stdDtaMap.put("textbkTabId", MapUtils.getIntValue(paramData, "tabId", 0));
        stdDtaMap.put("userId", MapUtils.getString(paramData, "userId", null));

        Map<String, Object> setsMap = stntMdulQstnMapper.findStntMdulQstnResetSetsId(stdDtaMap);
        stdDtaMap.put("setsId", MapUtils.getString(setsMap, "setsId","0"));

        if (MapUtils.getIntValue(stdDtaMap, "textbkTabId") != 0
                && !"0".equals(MapUtils.getString(stdDtaMap, "setsId"))) {

            Map<String, Object> resultSDRI = stntMdulQstnMapper.findStntMdulQstnResetSDRI(stdDtaMap);

            if (ObjectUtils.isEmpty(resultSDRI)) {
                int createSDRIcnt = stntMdulQstnMapper.createStntMdulQstnResetSDRI(stdDtaMap);
                log.info("createSDRIcnt:{}", createSDRIcnt);

                int createSDRDcnt = stntMdulQstnMapper.createStntMdulQstnResetSDRDsetsId(stdDtaMap);
                log.info("createSDRDcnt:{}", createSDRDcnt);
            }

        }
         */

        List<String> mainItem = Arrays.asList("id","stdDtaNm","crcuNm");
        List<String> mdulDtaInfoItem = Arrays.asList("setsId", "dtaIemId", "subId", "thumbnail","avgCorrectRate");
        List<String> stntDtaInfoItem = Arrays.asList("userId", "flnm");
        List<String> stntDtaResultItem = Arrays.asList("setsId", "id"  ,"dtaResultId"  ,"dtaIemId" , "subId", "errata"  ,"submAt"  ,"subMitAnw"  ,"subMitAnwUrl"  ,"exltAnwAt"  ,"reExmCnt"  ,"rubric"  ,"fdbDc"  ,"fdbExpAt"  ,"actTolInfo"  ,"peerReview"  ,"selfEvl", "delYn");
        List<String> errataInfoItem = Arrays.asList("dtaIemId", "subId", "mrkTy", "eakSttsCd", "eakAt", "errata", "articleType", "articleTypeNm","submAt");
        //        List<String> actListItem = Arrays.asList("id","actIemId","actWy","actWyNm","actSttsCd","actSttsNm","actStDt","actEdDt","actResult");
//        List<String> actResultItem = Arrays.asList("id","actId","thumbnail","actSubmitUrl","actSubmitDc","actStDt","actEdDt");

        // 학습자료 정보(main)
        Map tmpMap = tchLectureReportMapper.getTchLectureReportStdDtaInfo(paramData);

        // 필요한 item만 필터링
        Map resultMap = AidtCommonUtil.filterToMap(mainItem, tmpMap);

        // 모듈 정보
        Map<String,Object> _mdulDtaInfo = tchLectureReportMapper.findTchLectureReportMdulDtaInfo(paramData);
        LinkedHashMap<Object, Object> mdulDtaInfo = AidtCommonUtil.filterToMap(mdulDtaInfoItem, _mdulDtaInfo);

        // 학생정보
        Map<String,Object> _stntMainInfo = tchLectureReportMapper.getTchLectureReportStntInfo(paramData);
        LinkedHashMap<Object, Object> stntDtaInfo = AidtCommonUtil.filterToMap(stntDtaInfoItem, _stntMainInfo);



        List<LinkedHashMap<Object, Object>> errataInfoList = AidtCommonUtil.filterToList(errataInfoItem, tchLectureReportMapper.findTchLectureReportErrataInfo(paramData));

        // 학생 학습자료 결과 정보
        LinkedHashMap<Object, Object> stntDtaResult = AidtCommonUtil.filterToMap(stntDtaResultItem, _stntMainInfo);
        if (!ObjectUtils.isEmpty(stntDtaResult.get("rubric"))) {
            String rubricJsonString = (String) stntDtaResult.get("rubric");

            JsonObject jsonObject = JsonParser.parseString(rubricJsonString).getAsJsonObject();
            Map<String, Object> rubricMap = new Gson().fromJson(jsonObject.toString(), Map.class);
            stntDtaResult.put("rubric", rubricMap);
        } else {
            stntDtaResult.put("rubric", new HashMap<>());
        }
        stntDtaInfo.put("stntDtaResult", stntDtaResult);
        stntDtaInfo.put("errataInfoList", errataInfoList);

        // 다른 문제 풀기
        if (_stntMainInfo != null ) {
            Map<String, Object> innerParam = new HashMap<>();
            innerParam.put("id", MapUtils.getInteger(_stntMainInfo, "id", -1));
            List<Map> otherInfo = tchLectureReportMapper.getTchLectureReportStntInfoOther(innerParam);

            stntDtaInfo.put("stntOtherResultList", otherInfo);
        }
        else {
            stntDtaInfo.put("stntOtherResultList", null);
        }

        List<Map> actList = tchLectureReportMapper.findTchLectureReportActList(paramData);

        List<Long> actIdList = CollectionUtils.emptyIfNull(actList).stream()
                .map(m -> MapUtils.getLong(m, "id"))
                .toList();

        // 값이 있으면 ActResultInfo 를 구해서 actInfo 에 put 한다.
        if (!actIdList.isEmpty()) {
            Map<String, Object> idParam = new HashMap();
            idParam.put("actIdList", actIdList);
            idParam.put("userId", paramData.get("userId"));

            List<Map> actResultList = tchLectureReportMapper.findTchLectureReportActResultList(idParam);

            // ActResult 정보를 put 한다.
            actList.forEach(_map -> {
                actResultList.stream()
                        .filter(_actResult -> _map.get("id").equals(_actResult.get("actId")))
                        .findFirst()
                        .ifPresentOrElse(
                                actResult -> _map.put("actResult", actResult),
                                () -> _map.put("actResult", null)
                        );
            });
        }

        Map<String, Object> crcuNmMap = new HashMap<>();
        String crcuNm = null;

        crcuNmMap = tchLectureReportMapper.tcCurriculumTextTabId(paramData);
        if (!ObjectUtils.isEmpty(crcuNmMap)){
            crcuNm = MapUtils.getString(crcuNmMap, "idPathNm", null);
            resultMap.put("crcuNm", crcuNm);
        }

        stntDtaResult.put("actList", actList);

        resultMap.put("mdulDtaInfo", mdulDtaInfo);
        resultMap.put("stntDtaInfo", stntDtaInfo);

        return resultMap;

    }

    public Map modifyTchReportLectureResultErrataMod(Map<String, Object> paramData) throws Exception {

        int cnt = tchLectureReportMapper.modifyTchReportLectureResultErrataMod(paramData);

        if (cnt <= 0) {
            throw new AidtException("ID 에 해당하는 학습자료결과상세 값이 없습니다.");
        }

        try {
            // 활동 유형(= mrkTy 값이 2에 해당)의 경우 오답노트가 등록 되면 안되어서
            int mrkTy = tchLectureReportMapper.getTchReportLectureResultMrkTy(paramData);
            // 오답노트 등록
            String errata = MapUtils.getString(paramData, "errata");
            if (mrkTy != 2 && (StringUtils.equals("2", errata) || StringUtils.equals("3", errata))) {
                log.debug("insert wrong note:id-{},errata-{}", MapUtils.getString(paramData, "dtaResultDetailId"), errata);

                // 수업 데이터 조회
                Map<String, Object> map = tchLectureReportMapper.findLectureDetailInfo(paramData);

                if (map != null) {
                    Map<String, Object> innerParam = new HashMap<>();
                    innerParam.put("wrterId"        , MapUtils.getString(map        , "wrterId")            );
                    innerParam.put("wrtYmd"         , MapUtils.getString(map        , "wrtYmd")             );
                    innerParam.put("wonAnwClsfCd"   , 1);
                    innerParam.put("tabId"          , MapUtils.getString(map        , "tabId")              );
                    innerParam.put("trgtId"         , MapUtils.getString(map        , "tabId")              );
                    innerParam.put("textbkId"       , MapUtils.getString(map        , "textbkId")           );
                    innerParam.put("moduleId"       , MapUtils.getString(map        , "moduleId")           );
                    innerParam.put("subId"          , MapUtils.getString(map        , "subId")              );
                    innerParam.put("wonAnwTgId"     , MapUtils.getString(paramData  , "dtaResultDetailId")  );

                    innerParam.put("errata"         , MapUtils.getString(paramData  , "errata")             );
                    innerParam.put("subMitAnw"      , MapUtils.getString(paramData  , "subMitAnw")          );
                    innerParam.put("subMitAnwUrl"   , MapUtils.getString(paramData  , "subMitAnwUrl")       );

                    // 등록된 오답노트 여부 조회
                    int regCnt = tchLectureReportMapper.getWrongNoteCount(innerParam);
                    if (regCnt == 0) {
                        // wonAnwNm 구하기 위한 파라미터
                        innerParam.put("textbookId" , MapUtils.getString(map, "textbkId"));
                        innerParam.put("userId"     , MapUtils.getString(map, "userId")  );
                        innerParam.put("claId"      , MapUtils.getString(map, "claId")   );
                        innerParam.put("crculId"    , MapUtils.getString(map, "crculId") );

                        String wonAnwNm = stntWrongnoteService.getWonAnwNm(innerParam); // wonAnwNm 를 별도로 구함

                        innerParam.put("wonAnwNm", wonAnwNm);
                        int wonCnt = tchLectureReportMapper.createWonAswNote(innerParam);
                    } else {
                        log.info("wrong note already Exists:{}-{}-{}", MapUtils.getString(map, "wrterId"), MapUtils.getString(map, "wrtYmd"), MapUtils.getString(paramData, "dtaResultDetailId"));
                    }
                } else {
                    log.error("can't find dta info:{}", MapUtils.getString(paramData, "dtaResultDetailId"));
                }
            }
        } catch(Exception e) {
//            e.printStackTrace();
            CustomLokiLog.errorLog(e);
        }

        Map resultMap = new HashMap();
        Map stntDtaErrataInfo = tchLectureReportMapper.getModifyTchReportLectureResultErrataMod(paramData);

        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");
        resultMap.put("stntDtaErrataInfo", stntDtaErrataInfo);
        return resultMap;
    }

    public Map modifyTchReportLectureResultFdbMod(Map<String, Object> paramData) throws Exception {

        int cnt = tchLectureReportMapper.modifyTchReportLectureResultFdbMod(paramData);

        if (cnt <= 0) {
            throw new AidtException("ID 에 해당하는 학습자료결과상세 값이 없습니다.");
        }

        Map resultMap = new HashMap();
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "성공");

        return resultMap;
    }

    public Map<String, Object> createTchReportLectureGeneralReviewSave(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        //TODO:: mdfr = 교사ID session
        int result = tchLectureReportMapper.updateTchReportLectureReviewSave(paramData);

        if(result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }


        return returnMap;
    }

    public Object createTchReportLectureGeneralReviewSaveAll(Map<String, Object> paramData) throws Exception {
        List<Map<String, Object>> stntList = (List<Map<String, Object>>) paramData.get("stntList");

        int result =  0;
        Map<String, Object> updateMap = new HashMap<>();
        for (Map<String, Object> m : stntList) {
            updateMap = new HashMap<>();
            updateMap.putAll(m);
            updateMap.putAll(paramData);

            result = result + tchLectureReportMapper.updateTchReportLectureReviewSave(updateMap);
        }
        
        Map<String, Object> returnMap = new HashMap<>();

        if(result > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "저장완료");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "저장실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findSitesetDashreportExposList(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // 대시보드_리포트 노출 설정 조회
        List<Map> resultList = tchLectureReportMapper.findSitesetDashreportExposList(paramData);

        for(int i=0; i<resultList.size(); i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Object tempObj = objectMapper.readValue(resultList.get(i).get("exposTrgetList").toString(), Object.class);
                resultList.get(i).put("exposTrgetList", tempObj);
            } catch(Exception e) {
                log.error(CustomLokiLog.errorLog(e));
//                e.printStackTrace();
                CustomLokiLog.errorLog(e);
            }
        }
        returnMap.put("exposList", resultList);
        return returnMap;
    }


    public Object saveSitesetDashreportExpos(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("resultOk", false);
        int result = 0;
        String flag = "";

        if (paramData.get("wrterId") == null || ("").equals(paramData.get("wrterId"))) {
            returnMap.put("resultMsg", "wrterId를 입력해주세요");
            return returnMap;
        }

        // 1. 저장여부 확인
        Map info  = tchLectureReportMapper.findSitesetDashreportExposYN(paramData);

        List<Map> exposList = (List)paramData.get("exposList");
        for(int i=0; i<exposList.size(); i++) {
            List<Map> exposTrgetList = (List)exposList.get(i).get("exposTrgetList");

            for(int j=0; j<exposTrgetList.size(); j++) {

                Map<String, Object> temp = new HashMap<>();
                temp.put("wrterId",     paramData.get("wrterId"));
                temp.put("claId",       paramData.get("claId"));
                temp.put("yr",          paramData.get("yr"));
                temp.put("smt",         paramData.get("smt"));
                temp.put("textbkId",    paramData.get("textbkId"));
                temp.put("exposCd",     paramData.get("exposCd"));
                temp.put("exposTrgetCd",exposTrgetList.get(j).get("exposTrgetCd"));
                temp.put("exposAt",     exposTrgetList.get(j).get("exposAt"));
                if(MapUtils.isEmpty(info)) {
                    result = tchLectureReportMapper.saveSitesetDashreportExpos(temp);
                    flag = "저장";
                } else {
                    result = tchLectureReportMapper.modifySitesetDashreportExpos(temp);
                    flag = "수정";
                }
            }

        }


        if(result > 0) {
            returnMap.put("resultOk", true);
            //returnMap.put("exposTrgetCd", paramData.get("exposTrgetCd"));
            returnMap.put("resultMsg", flag + "완료");
        } else {
            returnMap.put("resultOk", false);
            //returnMap.put("exposTrgetCd", paramData.get("exposTrgetCd"));
            returnMap.put("resultMsg", flag + "실패");
        }

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchReportStdDtaGeneralReviewInfo(Map<String, Object> paramData) throws Exception {
        Map resultMap = tchLectureReportMapper.findTchReportStdDtaGeneralReviewInfo(paramData);

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findTchReportLectureGeneralReviewAiEvlWord(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> metaIdInfoItem = Arrays.asList("metaId");

        // 학습맵 대단원 목록정보
        List<LinkedHashMap<Object, Object>> metaIdList = AidtCommonUtil.filterToList(metaIdInfoItem, tchLectureReportMapper.findTchReportLectureGeneralReviewAiEvlWord(paramData));

        // 수준
        Map<String, Object> resultMap = tchLectureReportMapper.findTchReportLectureResultDetail(paramData);
        Integer level = MapUtils.getInteger(resultMap,"level",null);

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("metaIdList",metaIdList);
        respMap.put("level",level);
        return respMap;
    }


    @Transactional(readOnly = true)
    public Object findTchReportLectureResultMdul(Map<String, Object> paramData, Pageable pageable) throws Exception {
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<String> listItem = Arrays.asList("no", "setRowNo", "tabId", "setsId", "dtaIemId", "subId" , "thumbnail", "subMitAt", "actAt", "newAt", "taskId", "selfEvlAt", "slfPerEvlAt");

        Map resultMap = new HashMap();
        List<Map> mdulList = new ArrayList<>(); // 여기에서 mdulList 선언 및 초기화

        // pageable 없이 호출하도록 수정
        Map<String, Object> tempParam = new HashMap<>(paramData);
        // pageable 관련 파라미터는 이 메서드에서 사용하지 않음
        Map tmpMap = tchLectureReportMapper.getTchLectureReportStdDtaInfo(tempParam);
        if (tmpMap != null) {
            resultMap = AidtCommonUtil.filterToMap(listItem, tmpMap);

            if (MapUtils.getInteger(resultMap,"setsId", 0) == 0){
                mdulList = tchLectureReportMapper.findTchReportLectureResultMdul_setsIdNull(pagingParam);
            } else {
                mdulList = tchLectureReportMapper.findTchReportLectureResultMdul(pagingParam);
            }
            resultMap.put("mdulList", mdulList); // 이 코드를 if 블록 밖으로 이동
        }

        long total = 0;
        if (mdulList != null && !mdulList.isEmpty()) {
            total = ((Number) mdulList.get(0).get("fullCount")).longValue(); // 타입 안전 변환 추가
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(mdulList, pageable, total);
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("mdulList", AidtCommonUtil.filterToList(listItem, mdulList));
        returnMap.put("page", page);

        return returnMap;
    }

    public Object findTchReportLectureResultMdulDetail(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();

        // act_result_info update (tch_rpt_chk_at)
        tchLectureReportMapper.modifyTchReportLectureActTchRptChkAt(paramData);

        // std_dta_result_detail update (tch_rpt_chk_at)
        tchLectureReportMapper.modifyTchReportLectureStdDtaTchRptChkAt(paramData);

        // 제출 현황 조회
        returnMap.put("qstnStatus", tchMdulQstnService.modifyTchMdulQstnStatus(paramData));

        // 활동 방식 조회
        List<Map> actWyList = tchLectureReportMapper.findTchReportLectureActWyList(paramData);

        // 활동 방식 별 활동 조회
        List<Map> actList = new ArrayList();
        Map<String, Object> actSearchMap = new HashMap<>();
        if (!(ObjectUtils.isEmpty(actWyList))) {
            actList = actWyList.stream()
                    .map(s -> {
                        try {
                            actSearchMap.putAll(s);
                            actSearchMap.putAll(paramData);
                            actSearchMap.put("textbkTabId", MapUtils.getIntValue(paramData, "tabId", 0));
                            actSearchMap.put("actIemId", MapUtils.getIntValue(paramData, "dtaIemId", 0));

                            if (MapUtils.getIntValue(s, "actId", 0) == 0) {
                                s.put("actWyStatus", null);
                            } else {
                                s.put("actWyStatus", tchActService.findActMdulStatusList(actSearchMap));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return s;
                    }).toList();
        }
        returnMap.put("actList", actList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchReportLectureResultAct(Map<String, Object> paramData) throws Exception {
        List<String> listItem = Arrays.asList("stdtId","actWyCd","actWyNm","actWyCnt");

        // 활동 결과 - 학생별 활동 횟수
        List<Map> actList = tchLectureReportMapper.findTchReportLectureResultAct(paramData);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("actList", AidtCommonUtil.filterToList(listItem, actList));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findStntReportLectureResultAct(Map<String, Object> paramData) throws Exception {
        List<String> listItem = Arrays.asList("actWyCd","actWyNm","actProcCd","actIemId","articleThumbnail","actThumbnail","actSubmitUrl","delYn","actSubmitDc");

        // 활동 결과 - 학생의 활동
        List<Map> actList = tchLectureReportMapper.findStntReportLectureResultAct(paramData);

        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("actList", AidtCommonUtil.filterToList(listItem, actList));

        return returnMap;
    }
}
