package com.visang.aidt.lms.api.act.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchActMapper {
    int createActToolInfo(Map<String, Object> paramData) throws Exception;

    int createActResultInfo(Map<String, Object> paramData) throws Exception;

    int modifyActToolInfoEnd(Map<String, Object> paramData) throws Exception;

    Map<String, Object> getActToolInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findActToolList(Map<String, Object> paramData) throws Exception;

    List<Map> findActMdulStatusList(Map<String, Object> paramData) throws Exception;

    List<Map> findActMdulStatusTabList(Map<String, Object> paramData) throws Exception;

    int modifyActToolInfo(Map<String, Object> paramData) throws Exception;

    int modifyActResultInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findActToolInfo(Map<String, Object> paramData) throws Exception;

    int modifyActToolInfoSave(Map<String, Object> paramData) throws Exception;

    List<Map> findActMdulMate(int mateActId) throws Exception;

    int modifyTchActMdulExchange(Map<String, Object> paramData) throws Exception;

    int createtchActMdulMate(Map<String, Object> paramData) throws Exception;

    int modifytchActMdulMate(Map<String, Object> paramData) throws Exception;

    int removeActMateInfo(Map<String,Object> param) throws Exception;

    int removeActMateFdb(Map<String,Object> param) throws Exception;

    // 초기화 : 동일 actId , read 여부 초기화
    int removeActMateRead(Map<String,Object> param) throws Exception;
    
    // 교사 status 화면에서 그룹리스트 먼저 받길 원하여 - 키인스 요청 사항
    List<Map> findGroupIdList(Map<String, Object> paramData) throws Exception;
}

