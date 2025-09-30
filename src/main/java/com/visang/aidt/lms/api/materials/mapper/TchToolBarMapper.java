package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchToolBarMapper {
    Map<String, Object> findToolBarCall(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findUserId(Map<String, Object> paramData) throws Exception;
    void insertTchToolBar(Map<String, Object> paramData) throws Exception;
    void updateTchToolBar(Map<String, Object> paramData) throws Exception;

    int insertTchTool(Object paramData) throws Exception;
    int updateTchTool(Object paramData) throws Exception;
    public Map<String, Object> selectTchToolExistCheck(Object paramData) throws Exception;

    int insertTchToolInfo(Object paramData) throws Exception;
    int deleteTchToolBar(Object paramData) throws Exception;

    Map<String, Object> selectTchBoard(Map<String, Object> paramData) throws Exception;
    int insertTchBoard(Object paramData) throws Exception;
    int updateTchBoard(Object paramData) throws Exception;

    // 화면 제어 설정 관련
    List<Map<String, Object>> selectScreenControlSettings(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectScreenControlSetting(Map<String, Object> paramData) throws Exception;
    int insertScreenControlSetting(Map<String, Object> paramData) throws Exception;
    int updateScreenControlSetting(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectScreenControlCodes() throws Exception;
}
