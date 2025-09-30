package com.visang.aidt.lms.api.integration.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IntegTextbkMapper {

    List<Map> listTextbkInfo(Map<String, Object> paramData) throws Exception;

    Map getTextbkInfo(Map<String, Object> paramData) throws Exception;

    List<Map<String,Object>> listTextbkCrcuListByMeta(Map<String,Object> param) throws Exception;

}
