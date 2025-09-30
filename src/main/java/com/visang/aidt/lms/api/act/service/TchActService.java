package com.visang.aidt.lms.api.act.service;

import com.visang.aidt.lms.api.act.mapper.TchActMapper;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class TchActService {
    private final TchActMapper tchActMapper;

    public Object createActToolInfo(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        log.debug("param:{}", paramData.toString());

        //actProcCd defaultValue = 1
        paramData.put("actProcCd", MapUtils.getIntValue(paramData, "actProcCd" , 1));

        // 프론트쪽 작업 완료할 때까지 임시코드 null일 경우 0으로 처리
        if (paramData.get("subId") == null || ("").equals(paramData.get("subId").toString())) {
            paramData.put("subId", 0);
        }

        List<Map<String, Object>> groupList = (List<Map<String, Object>>) paramData.get("groupList");
        if (MapUtils.getIntValue(paramData, "actProcCd") == 2) {
            if (ObjectUtils.isEmpty(groupList)) {
                resultMap.put("id", "");
                resultMap.put("actSttsCd", "");
                resultMap.put("actSttsNm", "");
                resultMap.put("actStDt", "");
                resultMap.put("resultOk", false);
                resultMap.put("resultMsg", "실패");

                return resultMap;
            }
        }

        Map<String, Object> actMap = tchActMapper.findActToolInfo(paramData);
        paramData.put("id", MapUtils.getInteger(actMap,"id"));

        int result = 0;
        result = tchActMapper.modifyActToolInfo(paramData);

        if (result == 0) {
            result = tchActMapper.createActToolInfo(paramData);
        }

        int result2 = 0;
        result2 = tchActMapper.modifyActResultInfo(paramData);

        if (result2 == 0) {
            result2 = tchActMapper.createActResultInfo(paramData);
        }

        int result3 = tchActMapper.removeActMateInfo(paramData);
        log.debug("result3:{}", result3);

        int result4 = tchActMapper.removeActMateFdb(paramData);
        log.debug("result4:{}", result4);

        int result5 = tchActMapper.removeActMateRead(paramData);
        log.debug("result5:{}", result5);

        if (result > 0 && result2 > 0) {
            var id = paramData.get("id");

            if (MapUtils.getIntValue(paramData, "actProcCd") == 2) {
                paramData.put("actId", id);
                this.createtchActMdulMate(paramData);
            }

            resultMap.put("id", id);
            resultMap.put("actSttsCd", 1);
            resultMap.put("actSttsNm", "활동중");
            resultMap.put("actStDt", AidtCommonUtil.formatCurrentDtToString("yyyy-MM-dd HH:mm:ss"));
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } else {
            resultMap.put("id", "");
            resultMap.put("actSttsCd", "");
            resultMap.put("actSttsNm", "");
            resultMap.put("actStDt", "");
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }
        return resultMap;
    }

    public Object modifyActToolInfoEnd(Map<String, Object> paramData) throws Exception {
        int result = tchActMapper.modifyActToolInfoEnd(paramData);

        Map<String, Object> resultMap = tchActMapper.getActToolInfo(paramData);

        if (resultMap == null) {
            resultMap = new HashMap<>();
        }

        if (result > 0 ) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } else {
            resultMap.put("id", "");
            resultMap.put("actSttsCd", "");
            resultMap.put("actSttsNm", "");
            resultMap.put("actWy", "");
            resultMap.put("actWyNm", "");
            resultMap.put("actStDt", "");
            resultMap.put("actEdDt", "");
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }
        return resultMap;


    }

    @Transactional(readOnly = true)
    public Object findActToolList(Map<String, Object> paramData) throws Exception {

        // 프론트쪽 작업 완료할 때까지 임시코드 null일 경우 0으로 처리
        if (paramData.get("subId") == null || ("").equals(paramData.get("subId").toString())) {
            paramData.put("subId", 0);
        }

        List<Map> list = tchActMapper.findActToolList(paramData);

        var actWyItem = Arrays.asList("actWy", "actWyNm");
        var actInfoItem = Arrays.asList("id","wrterId","claId","textbkTabId","textbkId","textbkNm","actIemId","subId","actSttsCd","actSttsNm","actWy","actWyNm","actStDt","actEdDt");

        List<Map> actWyList = new ArrayList<>();

        for (Map _map : list) {

            Map actWyMap = AidtCommonUtil.filterToMap(actWyItem, _map);
            Map actInfoMap = AidtCommonUtil.filterToMap(actInfoItem, _map);

            actWyMap.put("actInfo", actInfoMap);

            actWyList.add(actWyMap);
        }

        Map resultMap = new HashMap();
        resultMap.put("actWyList", actWyList);

        return resultMap;
    }

    @Transactional(readOnly = true)
    public Object findActMdulActiveList(Map<String, Object> paramData) throws Exception {

        // 프론트쪽 작업 완료할 때까지 임시코드 null일 경우 0으로 처리
        if (paramData.get("subId") == null || ("").equals(paramData.get("subId").toString())) {
            paramData.put("subId", 0);
        }

        List<Map> list = tchActMapper.findActToolList(paramData);

        Map resultMap = new HashMap();

        if (list == null || list.isEmpty()) {
            resultMap.put("isExist", false);
            resultMap.put("actInfoList", CollectionUtils.emptyCollection());
        } else {
            var actInfoItem = Arrays.asList("id","actSttsCd","actSttsNm","actWy","actWyNm","actStDt");

            List<Map> actInfoList = new ArrayList<>();

            for (Map _map : list) {
                Map actInfoMap = AidtCommonUtil.filterToMap(actInfoItem, _map);
                actInfoList.add(actInfoMap);
            }
            resultMap.put("isExist", true);
            resultMap.put("actInfoList", actInfoList);
        }

        return resultMap;

    }

    @Transactional(readOnly = true)
    public Object findActMdulStatusList(Map<String, Object> paramData) throws Exception {

        var actResultListItem = Arrays.asList("id"  ,"actId"  ,"mamoymId"  ,"flnm"  , "pfUiImg", "thumbnail"  ,"actSubmitUrl" , "delYn"  ,"actSubmitDc"  ,"actStDt"  ,"actEdDt" , "fdbDc", "fdbUrl");
        var stntListItem = Arrays.asList("userIdx","userId","flnm","pfUiImg","thumbnail");

        var actResultMateListItem = Arrays.asList("id"  ,"actId"  ,"mamoymId"  ,"flnm"  , "pfUiImg", "thumbnail"  ,"actSubmitUrl", "delYn"  ,"actSubmitDc"  ,"actStDt"  ,"actEdDt" , "fdbDc", "fdbUrl"
                                                  , "groupId","exchngYn","actMateFdbList","othersReadYn","myReadYn");
        var stntListMateItem = Arrays.asList("userIdx","userId","flnm","pfUiImg","thumbnail","groupId");
        var actMateFdbItem = Arrays.asList("id","evaluatorId","mateFdbDc","mateFdbUrl");

        var groupItem = Arrays.asList("groupId");

        //actProcCd defaultValue = 1
        //paramData.put("actProcCd", MapUtils.getIntValue(paramData, "actProcCd" , 1));

        // 프론트쪽 작업 완료할 때까지 임시코드 null일 경우 0으로 처리
        if (paramData.get("subId") == null || ("").equals(paramData.get("subId").toString())) {
            paramData.put("subId", 0);
        }

        List<Map> list = tchActMapper.findActMdulStatusList(paramData);

        List<Map> actResultList = new ArrayList<>();
        List<Map> stntList = new ArrayList<>();

        List<Map> actResultMateList = new ArrayList<>();
        List<Map> stntMateList = new ArrayList<>();

        int mateActId = 0;
        int recntActProcCd = 0;

        if (ObjectUtils.isNotEmpty(list)) {
            recntActProcCd = MapUtils.getIntValue(list.get(0), "actProcCd", 0);
        }

        for(Map _map : list) {
            Map map = new HashMap<>();

            if (MapUtils.getIntValue(_map, "actProcCd", 1) == 1) {
                if (StringUtils.equals((String)_map.get("submAt"), "Y")) {
                    map = AidtCommonUtil.filterToMap(actResultListItem, _map);
                    actResultList.add(map);
                } else {
                    map = AidtCommonUtil.filterToMap(stntListItem, _map);
                    stntList.add(map);
                }
            } else {
                if (StringUtils.equals((String)_map.get("submAt"), "Y")) {
                    map = AidtCommonUtil.filterToMap(actResultMateListItem, _map);
                    actResultMateList.add(map);
                    mateActId = MapUtils.getIntValue(map, "actId", 0);
                } else {
                    map = AidtCommonUtil.filterToMap(stntListMateItem, _map);
                    stntMateList.add(map);
                }
            }
        }

        if (mateActId != 0) {
            List<Map> mateList = tchActMapper.findActMdulMate(mateActId);

            actResultMateList.forEach(m -> {
                List<Map> filteredMateList = mateList.stream()
                    .filter(mate -> m.get("mamoymId").equals(mate.get("mamoymId")))
                    .toList();

                m.put("actMateFdbList", AidtCommonUtil.filterToList(actMateFdbItem, filteredMateList));
            });
        }

        Map resultMap = new HashMap<>();
        Map indProc = new HashMap<>();
        Map mateProc = new HashMap<>();

        indProc.put("actResultList", actResultList);
        indProc.put("stntList",stntList);

        // SANGHEUM ADD : FIND MATE GROUP LIST - kiins requests
        List<LinkedHashMap<Object, Object>>  groupList = AidtCommonUtil.filterToList(groupItem, tchActMapper.findGroupIdList(paramData));
        mateProc.put("actResultList", actResultMateList);
        mateProc.put("stntList",stntMateList);
        mateProc.put("groupList",groupList);

        resultMap.put("recntActProcCd",recntActProcCd);
        resultMap.put("indProc",indProc);
        resultMap.put("mateProc",mateProc);

        return resultMap;

    }

    @Transactional(readOnly = true)
    public Object findActMdulStatusTabList(Map<String, Object> paramData) throws Exception {

        var actResultListItem = Arrays.asList("tabId"  ,"tabNm"  ,"actId" , "actProcCd" ,"dtaIemId", "subId", "tabSeq", "actWy", "actSttsCd", "actSttsNm");

        // 프론트쪽 작업 완료할 때까지 임시코드 null일 경우 0으로 처리
        if (paramData.get("subId") == null || ("").equals(paramData.get("subId").toString())) {
            paramData.put("subId", 0);
        }

        List<Map> list = tchActMapper.findActMdulStatusTabList(paramData);

        boolean isStdDtaResult = false;
        boolean isActiveAct = false;

        for(Map _map : list) {
            if ((Long)_map.get("typeSeq") == 0) {
                isStdDtaResult = true;
            }
            if ((Long)_map.get("typeSeq") == 1) {
                isActiveAct = true;
            }
        }


        List<LinkedHashMap<Object, Object>> statusTabList = AidtCommonUtil.filterToList(actResultListItem, list);

        Map resultMap = new HashMap<>();
        resultMap.put("isStdDtaResult", isStdDtaResult);
        resultMap.put("isActiveAct",isActiveAct);
        resultMap.put("statusTabList",statusTabList);

        return resultMap;

    }

    public Object modifyActToolInfoSave(Map<String, Object> paramData) throws Exception {

        log.debug("param:{}", paramData.toString());

        Map resultMap = new HashMap<>();

        int result = tchActMapper.modifyActToolInfoSave(paramData);

        if (result > 0) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }
        return resultMap;
    }

    public Object modifyTchActMdulExchange(Map<String, Object> paramData) throws Exception {
        int result = tchActMapper.modifyTchActMdulExchange(paramData);

        Map<String, Object> resultMap = new HashMap<>();

        if (result > 0 ) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");

        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }
        return resultMap;
    }

    public Object createtchActMdulMate(Map<String, Object> paramData) throws Exception {
        int actId = MapUtils.getIntValue(paramData, "actId", 0);
        List<Map<String, Object>> groupList = (List<Map<String, Object>>) paramData.get("groupList");
        int result = 0;
        if (actId != 0) {
            for (Map<String, Object> group : groupList) {
                group.put("actId", actId);
                result = result + tchActMapper.createtchActMdulMate(group);
                tchActMapper.modifytchActMdulMate(group);
            }
        }

        log.info("result:{}", result);
        Map<String, Object> resultMap = new HashMap<>();

        if (result > 0 ) {
            resultMap.put("resultOk", true);
            resultMap.put("resultMsg", "성공");
        } else {
            resultMap.put("resultOk", false);
            resultMap.put("resultMsg", "실패");
        }

        return resultMap;
    }
}
