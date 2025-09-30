package com.visang.aidt.lms.api.openApi.admin.service;

import com.visang.aidt.lms.api.openApi.admin.mapper.TableSchemaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableSchemaService {

    private final TableSchemaMapper tableSchemaMapper;

    public List<Map<String, Object>> selectTableColumnGroupList(Map<String, Object> paramMap) throws Exception {
        String tableSchemas = MapUtils.getString(paramMap, "tableSchema", "");
        List<String> tableSchemaList = new ArrayList<>();
        for (String tableSchema : tableSchemas.split(",")) {
            tableSchemaList.add(tableSchema);
        }
        // 없을 경우 default 제일 중요한 lms DB 만 처리
        if (CollectionUtils.isEmpty(tableSchemaList)) {
            tableSchemaList.add("aidt_lms");
        }
        paramMap.put("tableSchemaList", tableSchemaList);
        return tableSchemaMapper.selectTableColumnGroupList(paramMap);
    }

    public List<Map<String, Object>> selectTableColumnInfoList(Map<String, Object> paramMap) throws Exception {
        return tableSchemaMapper.selectTableColumnInfoList(paramMap);
    }
}
