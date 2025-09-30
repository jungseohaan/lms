package com.visang.aidt.lms.api.materials.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchMdulMapper {
    Map<String, Object> selectTcNoteInfo(Map<String, Object> paramData) throws Exception;
    int insertTcNoteInfo(Map<String, Object> paramData) throws Exception;
    int updateTcNoteInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchModulCheck(Map<String, Object> paramData) throws Exception;

    int insertTcNoteConts(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectTchMdulHdwrntCn(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectTchNoteInfoById(Map<String, Object> paramData) throws Exception;



}
