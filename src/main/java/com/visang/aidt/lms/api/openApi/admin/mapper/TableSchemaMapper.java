package com.visang.aidt.lms.api.openApi.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TableSchemaMapper {

    List<Map<String, Object>> selectTableColumnGroupList(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectTableColumnInfoList(Map<String, Object> paramMap) throws Exception;
}
