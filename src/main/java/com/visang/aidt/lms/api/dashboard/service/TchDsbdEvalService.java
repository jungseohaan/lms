package com.visang.aidt.lms.api.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.dashboard.mapper.TchDsbdEvalMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class TchDsbdEvalService {
    private final TchDsbdEvalMapper tchDsbdEvalMapper;

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> evalListItem = Arrays.asList("id", "evlNm", "evlPrgDt", "evlCpDt", "eamMth", "eamMthNm", "eamTrget", "targetCnt", "submitCnt", "mrkSttsCd", "mrkSttsNm");

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> evalList = tchDsbdEvalMapper.findTchDsbdStatusEvalList(pagingParam);

        if (!ObjectUtils.isEmpty(evalList)) {
            total = (long) evalList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(evalList, pageable, total);

        returnMap.put("evalList", AidtCommonUtil.filterToList(evalListItem, evalList));
        returnMap.put("page", page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusEvalDetail(Map<String, Object> paramData) throws Exception {
        List<String> evlInfoItem = Arrays.asList("evlId", "evlNm", "cpStntCnt", "incpStntCnt", "evalResultList");
        List<String> evlResultInfoItem = Arrays.asList("evlResultId", "mamoymId", "flnm", "claId", "eakSttsCd", "eakSttsNm", "submAt", "isEncouragement");

        var returnMap = AidtCommonUtil.filterToMap(evlInfoItem, tchDsbdEvalMapper.findTchDsbdStatusEvalDetail_evlInfo(paramData));

        List<LinkedHashMap<Object, Object>> evalResultInfoList = CollectionUtils.emptyIfNull(tchDsbdEvalMapper.findTchDsbdStatusEvalDetail_evlResultInfo(paramData)).stream().map(s -> {
            var tgtMap = new LinkedHashMap<>();
            if (Objects.isNull(s)) return tgtMap;
            var srcMap = new ObjectMapper().convertValue(s, Map.class);
            evlResultInfoItem.forEach(ss -> {
                if ("isEncouragement".equals(ss)) {
                    if (MapUtils.getInteger(srcMap, ss) == 1) {
                        tgtMap.put(ss, true);
                    } else {
                        tgtMap.put(ss, false);
                    }
                } else {
                    tgtMap.put(ss, srcMap.get(ss));
                }
            });
            return tgtMap;
        }).toList();

        returnMap.put("evalResultList", evalResultInfoList);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusEvalResult(Map<String, Object> paramData) throws Exception {
        List<String> evlInfoItem = Arrays.asList("evlId", "evlNm", "evlStdrSetAt", "evlStdrSet", "evlGdStdrScrCnt", "evlAvStdrScrCnt", "evlBdStdrScrCnt", "evlPsStdrScrCnt", "evlNotPsStdrScrCnt", "evlCpCnt", "evlNotCpCnt", "evalResultList");
        List<String> evlResultInfoItem = Arrays.asList("evlResultId", "mamoymId", "flnm", "claId", "eakSttsCd", "eakSttsNm", "evlResultScr", "evlResultAnctNm");

        var evlInfo = AidtCommonUtil.filterToMap(evlInfoItem, tchDsbdEvalMapper.findTchDsbdStatusEvalResult_evlInfo(paramData));

        var evlInfoCnt = tchDsbdEvalMapper.findTchDsbdStatusEvalResult_evlInfoCnt(paramData);

        String evlResult = "";
        for (Map<Object, Object> s : evlInfoCnt) {
            evlResult = MapUtils.getString(s, "evlResult");

         /*   if ("gd".equals(evlResult)) {
                evlInfo.put("evlGdStdrScrCnt", MapUtils.getInteger(s, "cnt"));
            } else if ("av".equals(evlResult)) {
                evlInfo.put("evlAvStdrScrCnt", MapUtils.getInteger(s, "cnt"));
            } else if ("bd".equals(evlResult)) {
                evlInfo.put("evlBdStdrScrCnt", MapUtils.getInteger(s, "cnt"));
            } else */
            if ("ps".equals(evlResult)) {
                evlInfo.put("evlPsStdrScrCnt", MapUtils.getInteger(s, "cnt"));
            } else if ("notPs".equals(evlResult)) {
                evlInfo.put("evlNotPsStdrScrCnt", MapUtils.getInteger(s, "cnt"));
            } else if ("cp".equals(evlResult)) {
                evlInfo.put("evlCpCnt", MapUtils.getInteger(s, "cnt"));
            } else if ("notCp".equals(evlResult)) {
                evlInfo.put("evlNotCpCnt", MapUtils.getInteger(s, "cnt"));
            }
        }

        evlInfo.put("evalResultList", AidtCommonUtil.filterToList(evlResultInfoItem, tchDsbdEvalMapper.findTchDsbdStatusEvalResult_evlResultInfo(paramData)));
        return evlInfo;
    }
}
