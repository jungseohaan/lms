package com.visang.aidt.lms.api.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.dashboard.mapper.TchDsbdHomewkMapper;
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
public class TchDsbdHomewkService {
    private final TchDsbdHomewkMapper tchDsbdHomewkMapper;

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusHomewkList(Map<String, Object> paramData, Pageable pageable) throws Exception {
        var returnMap = new LinkedHashMap<>();
        List<String> taskListItem = Arrays.asList("id", "taskNm", "taskSttsCd", "taskSttsNm","taskPrgDt", "taskCpDt", "eamMth", "eamMthNm", "eamTrget", "targetCnt", "submitCnt", "mrkSttsCd", "mrkSttsNm", "taskExpiredYn");

        long total = 0;

        PagingParam<?> pagingParam = PagingParam.builder()
                .param(paramData)
                .pageable(pageable)
                .build();

        List<Map> taskList = tchDsbdHomewkMapper.findTchDsbdStatusHomewkList(pagingParam);

        if (!ObjectUtils.isEmpty(taskList)) {
            total = (long) taskList.get(0).get("fullCount");
        }

        PagingInfo page = AidtCommonUtil.ofPageInfo(taskList, pageable, total);

        returnMap.put("taskList", AidtCommonUtil.filterToList(taskListItem, taskList));
        returnMap.put("page", page);

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusHomewkDetail(Map<String, Object> paramData) throws Exception {
        List<String> taskInfoItem = Arrays.asList("taskId", "taskNm", "cpStntCnt", "incpStntCnt", "taskResultList");
        List<String> taskResultInfoItem = Arrays.asList("taskResultId", "mamoymId", "flnm", "claId", "eakSttsCd", "eakSttsNm", "submAt");

        var returnMap = AidtCommonUtil.filterToMap(taskInfoItem, tchDsbdHomewkMapper.findTchDsbdStatusHomewkDetail_taskInfo(paramData));
        returnMap.put("taskResultList", AidtCommonUtil.filterToList(taskResultInfoItem, tchDsbdHomewkMapper.findTchDsbdStatusHomewkDetail_taskResultInfo(paramData)));

        return returnMap;
    }

    @Transactional(readOnly = true)
    public Object findTchDsbdStatusHomewkResult(Map<String, Object> paramData) throws Exception {
        List<String> taskInfoItem = Arrays.asList("taskId", "taskNm", "cpStntCnt", "incpStntCnt", "taskResultList");
        List<String> taskResultInfoItem = Arrays.asList("taskResultId", "mamoymId", "flnm", "claId", "eakSttsCd", "eakSttsNm", "mrkNeedAt", "anwNum", "triNum", "wrngNum");

        var returnMap = AidtCommonUtil.filterToMap(taskInfoItem, tchDsbdHomewkMapper.findTchDsbdStatusHomewkResult_taskInfo(paramData));
        returnMap.put("taskResultList", AidtCommonUtil.filterToList(taskResultInfoItem, tchDsbdHomewkMapper.findTchDsbdStatusHomewkResult_taskResultInfo(paramData)));

        return returnMap;
    }
}
