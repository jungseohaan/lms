package com.visang.aidt.lms.api.lecture.service;

import com.visang.aidt.lms.api.lecture.mapper.TchLectureReportEngMapper;
import com.visang.aidt.lms.api.lecture.mapper.TchLectureReportMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
@Service
@AllArgsConstructor
public class TchLectureReportEngService {
    private final TchLectureReportMapper tchLectureReportMapper; // 공통 서비스
    private final TchLectureReportEngMapper tchLectureReportEngMapper; // 영어전용 서비스

    @Transactional(readOnly = true)
    public Object findReportLectureResultList(Map<String, Object> paramData) throws Exception {

        List<String> resultItem = Arrays.asList("id", "stdDtaNm", "crcuNm"); // main 정보
        List<String> tabInfoItem = Arrays.asList("tabId", "tabNm", "tabSeq", "categoryCd", "categoryNm", "setsId", "aiCstmzdStdMthdSeCd", "eamTrget"); // tab 정보

        int selectedTabId = 0; // 선택된 tabId

        Map resultMap = new HashMap();

        List<Map> _tabInfoList = tchLectureReportMapper.findTchLectureReportTabInfoList(paramData);

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

                // 모듈 학습자료 정보 목록
                List<Map> mdulDtaInfoList = tchLectureReportMapper.findTchLectureReportMdulDtaInfoList(paramData);
                resultMap.put("mdulDtaInfoList", mdulDtaInfoList);

                /**
                 * -------------------------------
                 * 영어
                 * -------------------------------
                 */
                // 학생 학습자료 정오표 정보
                List<Map> stntDtaErrataInfoList = tchLectureReportEngMapper.findTchLectureReportStntDtaErrataInfoList(paramData);
                resultMap.put("stntDtaErrataInfoList", stntDtaErrataInfoList);


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


        List<LinkedHashMap<Object, Object>> tabInfoList = AidtCommonUtil.filterToList(tabInfoItem, _tabInfoList);

        resultMap.put("tabInfoList", tabInfoList);

        return resultMap;

    }

//    @Transactional(readOnly = true)
//    public Object findReportLectureResultDetailMdulList(Map<String, Object> paramData) throws Exception {
//        //TODO 데이터가 없고, api 명세서가 명확하지 않아서 일단 임의로 개발함. 데이터 입력후 전체적으로 점검 필요!
//
//        List<String> mainItem = Arrays.asList("id","stdDtaNm","crcuNm");
//        List<String> mdulDtaInfoItem = Arrays.asList("dta_iem_id","thumbnail","targetCnt", "submitCnt");
//        List<String> subMdulInfoItem = Arrays.asList("seq", "engTempId", "articleId", "tempType", "url", "image", "classAnalysisInfo", "commentary");
//        List<String> mdulImageInfoItem = Arrays.asList("tempId", "tempType", "url", "image"); // image 정보
////        List<String> classAnalysisInfoItem = Arrays.asList("correctRate", "solvSecAvr", "reIdfCntAvg", "anwChgCntAvg", "answerRateStr");
//
//        // 학습자료 정보
//        Map tmpMap = tchLectureReportMapper.getTchLectureReportStdDtaInfo(paramData);
//        // 필요한 item만 필터링
//        Map resultMap = AidtCommonUtil.filterToMap(mainItem, tmpMap);
//
//        if (tmpMap != null) {
//            String setsId = MapUtils.getString(tmpMap, "setsId");
//
//            // 모듈Dta 정보
//            Map<String,Object> mdulDtaInfo = tchLectureReportEngMapper.getTchLectureReportStdDtaInfo_mdulDtaInfo(paramData);
//            resultMap.put("mdulDtaInfo", AidtCommonUtil.filterToMap(mdulDtaInfoItem, mdulDtaInfo));
//
//            // 모듈 정보 (하단 정보탭)
//            Map<String,Object> mdulInfo = tchLectureReportEngMapper.getTchLectureReportStdDtaInfo_mdulInfo(paramData);
//            resultMap.put("mdulInfo", mdulInfo);
//
//            Map<String, Object> innerParam = ObjectUtils.clone(paramData);
//            innerParam.put("setsId", setsId);
//
//            if (mdulDtaInfo != null) {
//
//                innerParam.put("dtaResultId", mdulDtaInfo.get("dtaResultId"));
//
//                // 서브모듈 정보 목록
//                List<Map<String,Object>> subMdulList = tchLectureReportEngMapper.getTchLectureReportStdDtaInfo_subMdulInfo(paramData);
//
//                List<String> articleIdList = CollectionUtils.emptyIfNull(subMdulList).stream()
//                        .map(m -> ((String) m.get("articleId")))
//                        .toList();
//
//
//                // 값이 있을때,
//                if (!articleIdList.isEmpty()) {
//                    Map<String, List<String>> idParam = Map.of("articleIdList", articleIdList);
//
//                    // 우리반 분석 (sub 모듈 기준으로)
//                    List<Map<String, Object>> classAnalysysList = tchLectureReportEngMapper.getTchLectureReportStdDtaInfo_classAnalysys(innerParam);
//
//                    // 지문별응답률 세팅
//                    for (Map<String, Object> _anlyMap: classAnalysysList) {
//                        innerParam.put("articleId", _anlyMap.get("articleId"));
//                        List<String> answers = tchLectureReportEngMapper.findTchLectureReportMdulDtaInfo_answers(innerParam);
//
//                        _anlyMap.put("answerRateStr", AidtCommonUtil.getAnswerCountString(answers));
//                    }
//
//                    // 우리반 분석 정보를 put 한다.
//                    subMdulList.forEach(_map -> {
//                        classAnalysysList.stream()
//                                .filter(_anlyMap -> _map.get("articleId").equals(_anlyMap.get("articleId")))
//                                .findFirst()
//                                .ifPresentOrElse(
//                                        _anlyMap -> _map.put("classAnalysisInfo", _anlyMap),
//                                        () -> _map.put("classAnalysisInfo", null)
//                                );
//                    });
//
//                    // 해설(comment) comment 맵을 articleIdList 에 세팅
//                    List<Map> commentList = tchLectureReportMapper.findArticleCommentInfo(idParam);
//
//                    // 코멘터리 정보를 put 한다.
//                    subMdulList.forEach(_map -> {
//                        commentList.stream()
//                                .filter(_comment -> _map.get("articleId").equals(_comment.get("articleId")))
//                                .findFirst()
//                                .ifPresentOrElse(
//                                        comment -> _map.put("commentary", comment),
//                                        () -> _map.put("commentary", null)
//                                );
//                    });
//                }
//
//                resultMap.put("subMdulList", AidtCommonUtil.filterToMap(subMdulInfoItem, subMdulList));
//
//            }
//
//            // 학생 현황 한눈에 보기
//            List<Map> stntInfoList = tchLectureReportEngMapper.findTchLectureReportMdulDtaInfo_stntInfos(innerParam);
//
//            for (Map _map : stntInfoList) {
//                innerParam.put("stntId", _map.get("mamoymId"));
//                // 정오답, 답안 정보 입력
//                List<Map> errataInfoList = tchLectureReportEngMapper.findTchLectureReportMdulDtaInfo_errataInfos(innerParam);
//
//                _map.put("errataInfoList", errataInfoList);
//            }
//            resultMap.put("stntInfoList", stntInfoList);
//
//
//        }
//
//
//        return resultMap;
//    }
}
