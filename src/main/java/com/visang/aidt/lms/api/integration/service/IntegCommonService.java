package com.visang.aidt.lms.api.integration.service;

import com.visang.aidt.lms.api.integration.mapper.IntegCommonMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class IntegCommonService {

    private final IntegCommonMapper integCommonMapper;

    public Object listPtnInfo(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        List<Map> textbkList = integCommonMapper.listPtnInfo(paramData);
        returnMap.put("ptnInfoList",textbkList);
        return returnMap;
    }

    public Object listClaId(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        List<String> claIdList = integCommonMapper.listClaId(paramData);
        returnMap.put("claIdList",claIdList);
        return returnMap;
    }
    public Object getClaInfo(Map<String, Object> paramData) throws Exception {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        Map claInfo = integCommonMapper.getClaInfo(paramData);
        returnMap.put("claInfo",claInfo);
        return returnMap;
    }

}
