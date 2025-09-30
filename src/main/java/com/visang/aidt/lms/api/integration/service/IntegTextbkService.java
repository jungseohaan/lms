package com.visang.aidt.lms.api.integration.service;

import com.visang.aidt.lms.api.integration.mapper.IntegTextbkMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import com.visang.aidt.lms.api.utility.utils.PagingInfo;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class IntegTextbkService {

    private final IntegTextbkMapper integTextbkMapper;

    public Object listTextbkInfo(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        List<Map> textbkList = integTextbkMapper.listTextbkInfo(paramData);
        returnMap.put("textbkList",textbkList);
        return returnMap;
    }

    public List<Map<String, Object>> listTextbkCrcuListByMeta(Map<String, Object> paramData) throws Exception {
        Map textbkInfo = integTextbkMapper.getTextbkInfo(paramData);
        String curriSubject = MapUtils.getString(textbkInfo, "curriSubject", "");
        paramData.put("curriSubject", curriSubject);
        return integTextbkMapper.listTextbkCrcuListByMeta(paramData);
    }

}
