package com.visang.aidt.lms.api.integration.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IntegCommonMapper {

    List<Map> listPtnInfo(Map<String,Object> param) throws Exception;

    List<String> listClaId(Map<String,Object> param) throws Exception;

    Map getClaInfo(java.util.Map<String,Object> param) throws Exception;

}
