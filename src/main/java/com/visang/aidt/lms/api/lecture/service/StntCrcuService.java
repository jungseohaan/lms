package com.visang.aidt.lms.api.lecture.service;

import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.visang.aidt.lms.api.lecture.mapper.StntCrcuMapper;
import com.visang.aidt.lms.api.lecture.mapper.StntCrcuQuizMapper;
import com.visang.aidt.lms.api.utility.utils.AidtCommonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class StntCrcuService {

    private final StntCrcuMapper stntCrcuMapper;

    public Map<String, Object> findCrcuList(Map<String, Object> paramData)throws Exception {
        return null;
    }

    
    public Map<String, Object> findCrcuInfo(Map<String, Object> paramData)throws Exception {
        return null;
    }

    
    public Map<String, Object> createCrcuQuizAnswer(Map<String, Object> paramData)throws Exception {
        return null;
    }

    
    public Map<String, Object> findCrcuMode(Map<String, Object> paramData)throws Exception {
        return null;
    }

    
    public Map<String, Object> createCrcuMode(Map<String, Object> paramData)throws Exception {
        return null;
    }

    @Transactional(readOnly = true)
    public Object findStntCrcuLastposition(Map<String, Object> paramData)throws Exception {
        List<String> listItem = Arrays.asList(
                "id","userId", "textbkId", "claId", "crculId"
        );
        LinkedHashMap<Object, Object> resultMap = AidtCommonUtil.filterToMap(listItem, stntCrcuMapper.selectStntCrcuLastposition(paramData));
        return resultMap;
    }

    public Map saveStntCrcuLastposition(Map<String, Object> paramData)throws Exception {
        LinkedHashMap<Object, Object> resultMap = new LinkedHashMap<>();
        List<String> listItem = Arrays.asList(
               "id","userId", "textbkId", "claId", "crculId"
       );
        LinkedHashMap<Object, Object> selectMap = AidtCommonUtil.filterToMap(listItem, stntCrcuMapper.selectStntCrcuLastposition(paramData));
        int cnt = 0;
        if(MapUtils.isEmpty(selectMap)) {
            cnt = stntCrcuMapper.createStntCrcuLastposition(paramData);
        } else {
            paramData.put("id", selectMap.get("id"));
            cnt = stntCrcuMapper.updateStntCrcuLastposition(paramData);
        }

        if(cnt>0) {
            resultMap = AidtCommonUtil.filterToMap(listItem, stntCrcuMapper.selectStntCrcuLastposition(paramData));
        } else {
            resultMap.put("id", null);
            resultMap.put("userId", paramData.get("userId"));
            resultMap.put("textbkId", paramData.get("textbkId"));
            resultMap.put("claId", paramData.get("claId"));
            resultMap.put("crculId", paramData.get("crculId"));
        }

        paramData.remove("id");
        return resultMap;
    }
}
