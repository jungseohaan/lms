package com.visang.aidt.lms.api.vclc.service;

import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import com.visang.aidt.lms.api.vclc.mapper.VclcEvlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VclcEvlService {

    private final VclcEvlMapper vclcEvlMapper;

    public Map<String, Object> getLastEvalReportSummary(Map<String, Object> paramData) throws Exception {
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("result", false);

        if (StringUtils.isEmpty(MapUtils.getString(paramData, "claId", ""))) {
            resultData.put("message", "claId가 없습니다.");

            return resultData;
        }

        Map<String, Object> innerParam = new HashMap<>(paramData);

        Map<String, Object> lastEvalInfo = vclcEvlMapper.findVclcLastEval(innerParam);

        if (ObjectUtils.isEmpty(lastEvalInfo)) {
            resultData.put("message", "최근 진행한 평가가 없습니다.");

            return resultData;
        }

        innerParam.put("evlId", MapUtils.getIntValue(lastEvalInfo, "evlId", 0));

        // 평가 리포트 요약
        Map<String, Object> evlReportSummary = vclcEvlMapper.findVclcEvlReportSummary(innerParam);

        if (ObjectUtils.isEmpty(evlReportSummary)) {
            resultData.put("message", "최근 평가 현황 정보가 없습니다.");

            return resultData;
        }

        resultData.put("result", true);
        resultData.put("evlReportSummary", evlReportSummary);

        return resultData;
    }


    @Transactional(readOnly = true)
    public Object findEvalList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();

        if(!paramData.containsKey("page")) {
            paramData.put("page", "0");
        }
        if(!paramData.containsKey("size")) {
            paramData.put("size", "10");
        }
        String userIdStr = paramData.get("userId").toString();
        String[] userIds = userIdStr.split(",");
        paramData.put("userIds", userIds);

        if(paramData.containsKey("evlSttsCd")) {
            String evlSttsCdStr = paramData.get("evlSttsCd").toString();
            String[] evlSttsCds = evlSttsCdStr.split(",");
            paramData.put("evlSttsCds", evlSttsCds);
        }
        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map<String, Object>> evalCheckListMap = vclcEvlMapper.findStntEvalListEvalCheck(paramData);
        List<Map<String, Object>> publishList = vclcEvlMapper.findStntEvalListEvalInfo(pagingParam);

        PagingInfo pagingInfo = null;
        if(!publishList.isEmpty()) {
            pagingInfo = ofPageInfo(publishList, pageable,
                    ((BigInteger) publishList.get(0).get("fullCount")).longValueExact());
        }

        // regDt -> String, PagingInfo 최대값 확인
        for (String userId : userIds) {
            List<Map<String, Object>> list = publishList.stream().filter(map -> userId.equals(map.get("mamoymId"))).toList();
            if(!list.isEmpty()) {
                list.forEach(map -> {
                    String regDt = map.get("regDt").toString();
                    map.put("regDt", regDt);
                });
            }
        }

        returnMap.put("page", pagingInfo);
        returnMap.put("evalCheck", evalCheckListMap);

        HashMap<Object, Object> evalCheckAll = new HashMap<>();
        evalCheckAll.put("plnEvlCnt", 0);
        evalCheckAll.put("pgEvlCnt", 0);
        evalCheckAll.put("cpEvlCnt", 0);
        evalCheckAll.put("submCnt", 0);
        evalCheckListMap.forEach(map -> map.keySet().forEach(key -> {
            if(!"mamoymId".equals(key)) {
                evalCheckAll.put(key, (int) evalCheckAll.get(key) + ((BigDecimal) map.get(key)).intValueExact());
            }
        }));
        returnMap.put("evalCheckAll", evalCheckAll);

        // evalList -> publishList 키 값 변경
        returnMap.put("publishList", publishList);

        return returnMap;
    }

    private PagingInfo ofPageInfo(List<Map<String, Object>> mapList, Pageable pageable, long total) {
        var pageInfo = new PageImpl<>(mapList, pageable, total);

        return PagingInfo.builder()
                .size(pageInfo.getNumberOfElements())
                .totalElements(total)
                .totalPages(pageable.getPageSize() == 0 ? 1 : (int) Math.ceil((double) total / pageable.getPageSize()))
                .number(pageInfo.getNumber())
                .build();
    }
}



