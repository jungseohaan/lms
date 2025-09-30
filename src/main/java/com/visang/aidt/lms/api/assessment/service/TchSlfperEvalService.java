package com.visang.aidt.lms.api.assessment.service;

import com.visang.aidt.lms.api.assessment.mapper.TchSlfperEvalMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
//@AllArgsConstructor
public class TchSlfperEvalService {

    private final TchSlfperEvalMapper tchSlfperEvalMapper;

    /**
     * (평가).자기동료평가템플릿저장
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public Object createTchSlfperEvlTmplt(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Integer tmpltId = MapUtils.getInteger(paramData, "tmpltId");

        List<Map> slfPerEvlInfoList = (List <Map>) paramData.get("slfPerEvlInfoList");

        int resultTmplt = 0;

        if (tmpltId != null && tmpltId > 0) {
            resultTmplt = tchSlfperEvalMapper.modifyTchSlfperEvlTmplt(paramData);
            paramData.put("id", tmpltId);
        } else {
            resultTmplt = tchSlfperEvalMapper.createTchSlfperEvlTmplt(paramData);
        }

        int resultTmpltDetailDelete = tchSlfperEvalMapper.removeTchSlfperEvlTmpltDetail(paramData);
        log.info("resultTmpltDetailDelete:{}", resultTmpltDetailDelete);

        int resultTmpltDetail = 0;
        for (Map map : slfPerEvlInfoList) {
            map.put("tmpltId", MapUtils.getInteger(paramData, "id"));
            map.put("tmpltItmSeq", (resultTmpltDetail + 1));
            resultTmpltDetail = resultTmpltDetail + tchSlfperEvalMapper.createTchSlfperEvlTmpltDetail(map);

            map.remove("tmpltId");
            map.remove("tmpltItmSeq");
        }
        log.info("resultTmpltDetail:{}", resultTmpltDetail);

        paramData.remove("id");
        if (resultTmplt > 0) {
            returnMap.put("resultOk", true);
            returnMap.put("resultMsg", "성공");
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
        }

        return returnMap;
    }

    public  Map<String, Object> saveTchSlfperEvlTmplt2(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("resultOk", false);
        returnMap.put("resultMsg", "실패");

        List<String> selectSlfPerEvlTmpltMapForm = Arrays.asList("id");
        LinkedHashMap<Object, Object> selectSlfPerEvlTmpltMap = AidtCommonUtil.filterToMap(selectSlfPerEvlTmpltMapForm, tchSlfperEvalMapper.selectSlfPerEvlTmpltMap(paramData));

        String slfPerEvlInfoListStr = paramData.get("slfPerEvlInfoList").toString();
        List<Map<String, Object>> slfPerEvlInfoList = AidtCommonUtil.objectStringToListMap(slfPerEvlInfoListStr);

        //신규
        if(selectSlfPerEvlTmpltMap.size() == 0) {
            int insertResult = tchSlfperEvalMapper.insertSlfPerEvlTmplt(paramData);

            if(insertResult > 0) { //Detail insert
                for(int i=0; i< slfPerEvlInfoList.size(); i++) {
                    Map<String, Object> inputParam = slfPerEvlInfoList.get(i);
                    inputParam.put("tmpltId", paramData.get("tmpltId"));
                    inputParam.put("tmpltItmSeq", (i+1));
                    inputParam.put("userId", paramData.get("userId"));
                    tchSlfperEvalMapper.insertSlfPerEvlTmpltDetail(inputParam);
                }
            } else {
                return returnMap;
            }

        //수정
        } else {
            int updateResult = tchSlfperEvalMapper.updateSlfPerEvlTmplt(paramData);

            if(updateResult > 0) { //Detail update
                for(int i=0; i< slfPerEvlInfoList.size(); i++) {
                    Map<String, Object> inputParam = slfPerEvlInfoList.get(i);
                    inputParam.put("tmpltId", paramData.get("tmpltId"));
                    inputParam.put("tmpltItmSeq", (i+1));
                    inputParam.put("userId", paramData.get("userId"));
                    tchSlfperEvalMapper.updateSlfPerEvlTmpltDetail(inputParam);
                }
            } else {
                return returnMap;
            }
        }

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");
        return returnMap;
    }

    /**
     * (평가).자기동료평가템플릿조회
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getTchSlfperEvlTmpltList(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> selectTchSlfperEvlTmpltListForm = Arrays.asList("userId","tmpltId", "slfPerEvlClsfCd", "slfPerEvlNm", "stExposAt", "regDt");
        List<LinkedHashMap<Object, Object>> selectTchSlfperEvlTmpltList = AidtCommonUtil.filterToList(selectTchSlfperEvlTmpltListForm, tchSlfperEvalMapper.selectSlfPerEvlTmplt(paramData));

        returnMap.put("templtList", selectTchSlfperEvlTmpltList);

        return returnMap;
    }

    /**
     * (평가).자기동료평가템플릿조회상세
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getTchSlfperEvlTmpltDetail(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        Map<String, Object> tmpltMap = new LinkedHashMap<>();
        List<LinkedHashMap<Object, Object>> tmpltDetail = new ArrayList<>();        //신규
        if(paramData.get("tmpltId") == null || ("").equals(paramData.get("tmpltId").toString())) {
            return returnMap;

        } else {
            //List<String> selectTchSlfperEvlTmpltMapForm = Arrays.asList("userId","tmpltId", "slfPerEvlClsfCd", "slfPerEvlNm", "stExposAt");
            List<String> selectTchSlfperEvlTmpltListDetailForm = Arrays.asList("evlDmi", "evlIem", "evlStdrCd", "evlStdrDc");
            tmpltMap = tchSlfperEvalMapper.selectSlfPerEvlTmpltMap(paramData);
            tmpltDetail = AidtCommonUtil.filterToList(selectTchSlfperEvlTmpltListDetailForm, tchSlfperEvalMapper.selectSlfPerEvlTmpltDetail(paramData));

            returnMap.put("userId", tmpltMap.get("wrterId"));
            returnMap.put("tmpltId", tmpltMap.get("id"));
            returnMap.put("slfPerEvlClsfCd", tmpltMap.get("slfPerEvlClsfCd"));
            returnMap.put("stExposeAt", tmpltMap.get("stExposAt"));
            returnMap.put("slfPerEvlInfoList", tmpltDetail);

        }

        return returnMap;
    }

    /**
     * (평가). 자기동료평가설정
     *
     * @param paramData 입력 파라메터
     * @return Map
     */

    public  Object saveTchSlfperEvlSet(Map<String, Object> paramData) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        String slfPerEvlNm = MapUtils.getString(paramData, "slfPerEvlNm");
        if (ObjectUtils.isEmpty(slfPerEvlNm)) {
            paramData.put("slfPerEvlNm", "-");
        }

        //slf_per_evl_tmplt_detail list
        //String slfPerEvlInfoListStr = paramData.get("slfPerEvlInfoList").toString();
        //List<Map<String, Object>> slfPerEvlInfoList = AidtCommonUtil.objectStringToListMap(slfPerEvlInfoListStr);
        List<Map<String, Object>> slfPerEvlInfoList = (List<Map<String, Object>>) paramData.get("slfPerEvlInfoList");

        //insert slf_per_evl_set_info
        int insertResult = tchSlfperEvalMapper.insertSlfPerEvlSetInfo(paramData);

        if(insertResult > 0) { //slf_per_evl_tmplt_detail insert
            for(int i=0; i< slfPerEvlInfoList.size(); i++) {
                Map<String, Object> inputParam = slfPerEvlInfoList.get(i);
                inputParam.put("slfPerEvlSetId", paramData.get("id"));
                inputParam.put("tmpltItmSeq", (i+1));
                inputParam.put("userId", paramData.get("userId"));
                tchSlfperEvalMapper.insertSlfPerEvlSetDetailInfo(inputParam);
            }
        } else {
            returnMap.put("resultOk", false);
            returnMap.put("resultMsg", "실패");
            return returnMap;
        }

        returnMap.put("resultOk", true);
        returnMap.put("resultMsg", "성공");
        return returnMap;
    }

    /**
     * (평가).자기동료평가결과보기
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getTchSlfperEvlSlfView(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        paramData.put("slfPerEvlSetInfo", 1);
        var selInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("slfPerEvlSetInfo", 2);
        var perInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("selInfoId", MapUtils.getInteger(selInfoIdMap, "id"));
        paramData.put("perInfoId", MapUtils.getInteger(perInfoIdMap, "id"));

        List<Map> slList = new ArrayList<>();
        List<Map> perInfoList = new ArrayList<>();
        List<Map> perInfoTempList = new ArrayList<>();
        List<Map> templtList = new ArrayList<>();

        int selInfoIdMapId = 0;
        if (selInfoIdMap != null && !selInfoIdMap.isEmpty()) {
            slList = tchSlfperEvalMapper.findTchSlfperEvlSlfViewSl(paramData);
            selInfoIdMapId = MapUtils.getInteger(selInfoIdMap, "id");
        }

        int perInfoIdMapId = 0;
        if (perInfoIdMap != null && !perInfoIdMap.isEmpty()) {
            perInfoTempList = tchSlfperEvalMapper.findTchSlfperEvlSlfViewPerInfo(paramData);
            List<Map> perResultInfoList = tchSlfperEvalMapper.findTchSlfperEvlSlfView_perResultInfoList(paramData);

            perInfoList = CollectionUtils.emptyIfNull(perInfoTempList).stream()
            .map(s->{
                List<Map> perResultList = CollectionUtils.emptyIfNull(perResultInfoList).stream()
                    .filter(t -> StringUtils.equals(MapUtils.getString(s,"perApraserId"), MapUtils.getString(t,"perApraserId")))
                    .map(t -> {
                        t.remove("perApraserId");
                        return t;
                    }).toList();
                s.put("PerResult", perResultList);
                return s;
            }).toList();

            templtList = tchSlfperEvalMapper.findSTchSlfperEvlSlfViewTemplt(paramData);
            perInfoIdMapId = MapUtils.getInteger(perInfoIdMap, "id");
        }

        paramData.remove("selInfoId");
        paramData.remove("perInfoId");

        returnMap.put("stntId", paramData.get("stntId"));
        returnMap.put("selInfoId", selInfoIdMapId);
        returnMap.put("perInfoId", perInfoIdMapId);

        returnMap.put("slResult", slList);
        returnMap.put("perInfoList", perInfoList);
        returnMap.put("templtList", templtList);

        returnMap.put("slfNum", "");
        returnMap.put("slfTotNum", slList.size());
        returnMap.put("perNum", "");
        returnMap.put("perTotNum", perInfoList.size());

        returnMap.put("slfStExposAt", MapUtils.getString(selInfoIdMap, "stExposAt"));
        returnMap.put("perStExposAt", MapUtils.getString(perInfoIdMap, "stExposAt"));

        var evlAtMap = tchSlfperEvalMapper.findMdulSlfPerEvlAt(paramData);

        returnMap.put("mdulSlfPerEvlAt", MapUtils.getString(evlAtMap, "mdulSlfPerEvlAt"));



        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchSlfperEvlSlfForm(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        paramData.put("slfPerEvlSetInfo", 1);
        var selInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("slfPerEvlSetInfo", 2);
        var perInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("selInfoId", MapUtils.getInteger(selInfoIdMap, "id"));
        paramData.put("perInfoId", MapUtils.getInteger(perInfoIdMap, "id"));

        List<Map> slList = new ArrayList<>();
        List<Map> templtList = new ArrayList<>();

        if (selInfoIdMap != null && !selInfoIdMap.isEmpty()) {
            slList = tchSlfperEvalMapper.findTchSlfperEvlSlfFormSl(paramData);
        }

        String slExport = tchSlfperEvalMapper.findTchSlfperEvlSlfFormSlExport(paramData);

        if (perInfoIdMap != null && !perInfoIdMap.isEmpty()) {
            templtList = tchSlfperEvalMapper.findSTchSlfperEvlSlfViewTemplt(paramData);
        }

        String templtExport = tchSlfperEvalMapper.findSTchSlfperEvlSlfViewTempltExport(paramData);

        paramData.remove("selInfoId");
        paramData.remove("perInfoId");
        paramData.remove("slfPerEvlSetInfo");

        returnMap.put("slResult", slList);
        returnMap.put("slExport", slExport);
        returnMap.put("templtList", templtList);
        returnMap.put("templtExport", templtExport);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object getTchSlfperEvlSlfView2(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> selectSlfPerEvlSetInfoForm = Arrays.asList("id", "slfPerEvlClsfCd", "slfPerEvlNm");
        List<LinkedHashMap<Object, Object>> selectSlfPerEvlSetInfoList = AidtCommonUtil.filterToList(selectSlfPerEvlSetInfoForm, tchSlfperEvalMapper.selectSlfPerEvlSetInfo(paramData));

        String selInfoId = "";
        String perInfoId = "";
        List<String> slfResultInfoForm = Arrays.asList("tmpltItmSeq", "evlDmi", "evlIem", "evlStdrCd", "evlStdrDc", "evlResult", "evlAsw");
        List<LinkedHashMap<Object, Object>> slfResultInfoList = new ArrayList<>();
        for(Map<Object, Object> temp : selectSlfPerEvlSetInfoList) {
            if("1".equals(temp.get("slfPerEvlClsfCd").toString())){
                Map<String, Object> param = new HashMap<>();
                param.put("stntId", paramData.get("stntId"));
                param.put("slfPerEvlSetDetailInfoId", temp.get("id"));
                selInfoId = temp.get("id").toString();
                List<LinkedHashMap<Object, Object>> tempList = AidtCommonUtil.filterToList(slfResultInfoForm, tchSlfperEvalMapper.selectTchSlfperEvlSlfView(param));
                for(LinkedHashMap<Object, Object> tempMap : tempList) {
                    slfResultInfoList.add(tempMap);
                }
            }
        }

        List<String> perInfoForm = Arrays.asList("perApraserId","flnm");
        List<LinkedHashMap<Object, Object>> perInfoList = new ArrayList<>();
        for(Map<Object, Object> temp : selectSlfPerEvlSetInfoList) {
            Map<String, Object> param = new HashMap<>();
            param.put("stntId", paramData.get("stntId"));
            param.put("slfPerEvlSetDetailInfoId", temp.get("id"));
            List<LinkedHashMap<Object, Object>> tempList = AidtCommonUtil.filterToList(perInfoForm, tchSlfperEvalMapper.selectPerAprsrIdNm(param));
            for(LinkedHashMap<Object, Object> tempMap : tempList) {
                perInfoList.add(tempMap);
            }
        }


        List<String> tmpltForm = Arrays.asList("tmpltItmSeq", "evlDmi", "evlIem", "evlStdrCd", "evlStdrDc");
        List<LinkedHashMap<Object, Object>> tmpltInfoList = new ArrayList<>();
        for(Map<Object, Object> temp : selectSlfPerEvlSetInfoList) {
            if("2".equals(temp.get("slfPerEvlClsfCd").toString())){
                Map<String, Object> param = new HashMap<>();
                param.put("slfPerEvlSetDetailInfoId", temp.get("id"));
                perInfoId = temp.get("id").toString();
                List<LinkedHashMap<Object, Object>> tempList = AidtCommonUtil.filterToList(tmpltForm, tchSlfperEvalMapper.selectTmpltInfoList(param));
                for(LinkedHashMap<Object, Object> tempMap : tempList) {
                    tmpltInfoList.add(tempMap);
                }
            }
        }
        returnMap.put("stntId", paramData.get("stntId"));
        returnMap.put("selInfoId", selInfoId);
        returnMap.put("perInfoId", perInfoId);
        returnMap.put("slResult", slfResultInfoList);
        returnMap.put("perInfoList",perInfoList);
        returnMap.put("templtList", tmpltInfoList);
        returnMap.put("slfNum", ""); //?
        returnMap.put("slfTotNum", slfResultInfoList.size()); //?
        returnMap.put("perNum", ""); //?
        returnMap.put("perTotNum", perInfoList.size()); //?

        return returnMap;
    }

    /**
     * (평가).자기동료평가결과보기(동료평가)
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getTchSlfperEvlperView(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> listForm = Arrays.asList("tmpltItmSeq", "evlDmi", "evlIem", "evlStdrCd", "evlStdrDc", "evlResult", "evlAsw");
        List<LinkedHashMap<Object, Object>> resultList = AidtCommonUtil.filterToList(listForm, tchSlfperEvalMapper.selectTchSlfperEvlPerView(paramData));

        returnMap.put("slfResultInfo", resultList);
        return returnMap;
    }

    //자기동료평가제출현황(자기평가)
    @Transactional(readOnly = true)
    public Object getTchSlfperEvlPerStatus(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        paramData.put("slfPerEvlSetInfo", 1);
        var selInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("slfPerEvlSetInfo", 2);
        var perInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("selInfoId", MapUtils.getInteger(selInfoIdMap, "id"));
        paramData.put("perInfoId", MapUtils.getInteger(perInfoIdMap, "id"));

        List<Map> submStntInfoList = tchSlfperEvalMapper.findTchSlfperEvlPerStatus_submStntInfoList(paramData);
        List<Map> nonSubStntInfoList = tchSlfperEvalMapper.findTchSlfperEvlPerStatus_nonSubStntInfoList(paramData);

        paramData.remove("selInfoId");
        paramData.remove("perInfoId");

        returnMap.put("gbCd", paramData.get("gbCd"));
        returnMap.put("tabId", paramData.get("tabId"));
        returnMap.put("submStntInfoList", submStntInfoList);
        returnMap.put("resultCnt", submStntInfoList.size());
        returnMap.put("nonSubStntInfoList", nonSubStntInfoList);
        returnMap.put("stntInfoCnt", nonSubStntInfoList.size());

        return returnMap;
    }

    //자기동료평가제출현황(동료평가)
    @Transactional(readOnly = true)
    public Object getTchSlfperEvlPerStatusPer(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        paramData.put("slfPerEvlSetInfo", 1);
        var selInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("slfPerEvlSetInfo", 2);
        var perInfoIdMap = tchSlfperEvalMapper.findTchSlfperEvlSlfSet(paramData);

        paramData.put("selInfoId", MapUtils.getInteger(perInfoIdMap, "id"));
        paramData.put("perInfoId", MapUtils.getInteger(perInfoIdMap, "id"));

        List<String> itemList = Arrays.asList("apraserId", "flnm", "perApraserCnt");
        List<LinkedHashMap<Object, Object>> submStntInfoList = AidtCommonUtil.filterToList(itemList, tchSlfperEvalMapper.findTchSlfperEvlPerStatus_submStntInfoListPer(paramData));

        List<Map> nonSubStntInfoList = tchSlfperEvalMapper.findTchSlfperEvlPerStatus_nonSubStntInfoListPer(paramData);

        paramData.remove("selInfoId");
        paramData.remove("perInfoId");

        returnMap.put("gbCd", paramData.get("gbCd"));
        returnMap.put("tabId", paramData.get("tabId"));
        returnMap.put("submStntInfoList", submStntInfoList);
        returnMap.put("resultCnt", submStntInfoList.size());
        returnMap.put("nonSubStntInfoList", nonSubStntInfoList);
        returnMap.put("stntInfoCnt", nonSubStntInfoList.size());

        return returnMap;
    }
    /**
     * (평가).자기동료평가제출현황
     *
     * @param paramData 입력 파라메터
     * @return Map
     */
    @Transactional(readOnly = true)
    public Object getTchSlfperEvlPerStatus2(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> selectSlfPerEvlSetInfoForm = Arrays.asList("id", "slfPerEvlClsfCd", "slfPerEvlNm", "apraserId");
        List<LinkedHashMap<Object, Object>> selectSlfPerEvlSetInfoList = AidtCommonUtil.filterToList(selectSlfPerEvlSetInfoForm, tchSlfperEvalMapper.selectSlfPerEvlSetInfoSubm(paramData));

        returnMap.put("gbCd", paramData.get("gbCd"));   //구분코드
        returnMap.put("tabId", paramData.get("tabId")); //탭ID

        Map<String, Object> param = new HashMap<>();
        param.put("setsId", paramData.get("setsId"));

        for(LinkedHashMap<Object, Object> temp : selectSlfPerEvlSetInfoList) {
            if("1".equals(temp.get("slfPerEvlClsfCd").toString())) {
                param.put("slfPerEvlSetDetailInfoIdSlf", temp.get("id"));
            } else if("2".equals(temp.get("slfPerEvlClsfCd").toString())){
                param.put("apraserId", temp.get("apraserId"));
                param.put("slfPerEvlSetDetailInfoIdClg", temp.get("id"));
            }
        }

        //제출자 정보 List(제출자 활동결과 정보) - SubmStntInfoList
        List<String> selectSubmStntInfoListForm = Arrays.asList("apraserId", "flnm","perApraserCnt");
        List<LinkedHashMap<Object, Object>> selectSubmStntInfoList = AidtCommonUtil.filterToList(selectSubmStntInfoListForm, tchSlfperEvalMapper.selectSubmStntInfoList(param));
        returnMap.put("submStntInfoList", selectSubmStntInfoList);  //제출자정보

        //제출자 수 Map - resultCnt
        List<String> selectSubmStntInfoListCntForm = Arrays.asList("cnt");
        LinkedHashMap<Object, Object> selectSubmStntInfoListCnt = AidtCommonUtil.filterToMap(selectSubmStntInfoListCntForm, tchSlfperEvalMapper.selectSubmStntInfoListCnt(param));
        returnMap.put("resultCnt", selectSubmStntInfoListCnt.get("cnt"));  //제출자수

        //미제출자 정보 List(미제출자 학생 정보) - NonSubmStntInfoList
        List<String> selectNonSubmStntInfoListForm = Arrays.asList("apraserId", "flnm");
        List<LinkedHashMap<Object, Object>> selectNonSubmStntInfoList = AidtCommonUtil.filterToList(selectNonSubmStntInfoListForm, tchSlfperEvalMapper.selectNonSubmStntInfoList(param));
        returnMap.put("nonSubStntInfoList", selectNonSubmStntInfoList);  //미제출자정보

        //미제출자 수 Map - stntInfoCnt
        List<String> selectNonSubmStntInfoListCntForm = Arrays.asList("cnt");
        param.put("tabId", paramData.get("tabId"));
        LinkedHashMap<Object, Object> selectNonSubmStntInfoListCnt = AidtCommonUtil.filterToMap(selectNonSubmStntInfoListCntForm, tchSlfperEvalMapper.selectNonSubmStntInfoListCnt(param));
        returnMap.put("stntInfoCnt", selectNonSubmStntInfoListCnt.get("cnt"));  //미제출자수


        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchSlfperEvlSlfPerinfo(Map<String, Object> paramData) throws Exception {
        return tchSlfperEvalMapper.findTchSlfperEvlSlfPerinfo(paramData);
    }

    @Transactional(readOnly = true)
    public Object findTchSlfperEvlSetInfo(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> slfPerEvlInfoItem = Arrays.asList("evlSeq", "evlDmi", "evlIem", "evlStdrCd", "evlStdrNm", "evlStdrDc");
        Map<String, Object> slfPerEvlClsfNm = tchSlfperEvalMapper.findSlfPerEvlClsfNm(paramData);

        returnMap.put("slfPerEvlClsfCd", MapUtils.getInteger(paramData, "slfPerEvlClsfCd"));
        returnMap.put("slfPerEvlClsfNm", MapUtils.getString(slfPerEvlClsfNm, "slfPerEvlClsfNm"));

        List<Map> slfPerEvlInfoList = tchSlfperEvalMapper.findTchSlfperEvlSetInfoList(paramData);
        returnMap.put("slfPerEvlList", AidtCommonUtil.filterToList(slfPerEvlInfoItem, slfPerEvlInfoList));
        String stExposAt = "";
        if (ObjectUtils.isNotEmpty(slfPerEvlInfoList)) {
            stExposAt = MapUtils.getString(slfPerEvlInfoList.get(0), "stExposAt", "");
        }
        returnMap.put("stExposAt", stExposAt);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchSlfperEvlResult(Map<String, Object> paramData) throws Exception {
        var returnMap = new LinkedHashMap<>();

        List<String> slfPerEvlInfoItem = Arrays.asList("evlSeq", "evlDmi", "evlIem", "evlStdrCd", "evlStdrNm", "evlStdrDc","evlResult","evlAsw");
        List<String> slfPerResultInfoItem = Arrays.asList("stntId", "flnm");

        Map<String, Object> slfPerEvlClsfNm = tchSlfperEvalMapper.findSlfPerEvlClsfNm(paramData);

        returnMap.put("slfPerEvlClsfCd", MapUtils.getInteger(paramData, "slfPerEvlClsfCd"));
        returnMap.put("slfPerEvlClsfNm", MapUtils.getString(slfPerEvlClsfNm, "slfPerEvlClsfNm"));

        List<Map> slfperEvlResultList = tchSlfperEvalMapper.findTchSlfperEvlResultList(paramData);
        List<LinkedHashMap<Object, Object>> slfPerResultListStnt = AidtCommonUtil.filterToList(slfPerResultInfoItem, slfperEvlResultList).stream().distinct().collect(Collectors.toList());

        List<LinkedHashMap<Object, Object>> slfPerResultList = CollectionUtils.emptyIfNull(slfPerResultListStnt).stream()
            .map(s -> {
                List<Map> slfPerEvlList = CollectionUtils.emptyIfNull(slfperEvlResultList).stream()
                    .filter(t -> {
                        return StringUtils.equals(MapUtils.getString(s,"stntId"), MapUtils.getString(t,"stntId"));
                    }).toList();
                s.put("slfPerEvlList", AidtCommonUtil.filterToList(slfPerEvlInfoItem, slfPerEvlList));
                return s;
            }).toList();

        returnMap.put("slfPerResultList", slfPerResultList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchSlfperEvlResultDetailList(Map<String, Object> paramData) throws Exception {
        // Response Parameters
        List<String> slfPerResultInfoItem = Arrays.asList("stntId", "flnm", "evlAswAt");

        // 평가정보
        List<Map> slfperEvlResultDetailList = tchSlfperEvalMapper.findTchSlfperEvlResultDetailList(paramData);

        // 자기평가정보
        List<LinkedHashMap<Object, Object>> slfResultList = CollectionUtils.emptyIfNull(slfperEvlResultDetailList).stream()
            .filter(r -> StringUtils.equals(MapUtils.getString(r,"slfPerEvlClsfCd"), "1"))
            .map(r -> {
                return AidtCommonUtil.filterToMap(slfPerResultInfoItem, r);
            }).toList();
        String slfEvlSetAt = slfResultList.isEmpty() ? "N" : "Y";

        // 동료평가정보
        List<LinkedHashMap<Object, Object>> perResultList = CollectionUtils.emptyIfNull(slfperEvlResultDetailList).stream()
            .filter(r -> StringUtils.equals(MapUtils.getString(r,"slfPerEvlClsfCd"), "2"))
            .map(r -> {
                return AidtCommonUtil.filterToMap(slfPerResultInfoItem, r);
            }).toList();
        String perEvlSetAt = perResultList.isEmpty() ? "N" : "Y";

        // Response
        LinkedHashMap<Object, Object> respMap = new LinkedHashMap<>();
        respMap.put("slfEvlSetAt",slfEvlSetAt);
        respMap.put("perEvlSetAt",perEvlSetAt);
        respMap.put("slfResultList",slfResultList);
        respMap.put("perResultList",perResultList);
        return respMap;
    }
}
