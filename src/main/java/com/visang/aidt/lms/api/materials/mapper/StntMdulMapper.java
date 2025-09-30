package com.visang.aidt.lms.api.materials.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntMdulMapper {

    int updateStntNoteInfo(Map<String, Object> paramData) throws Exception;
    List<Map> selectTcNoteConts(Map<String, Object> paramData) throws Exception;
    List<Map> selectStdDtaResult(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectStntMdulHdwrntCn(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectExltCnt(Map<String, Object> paramData) throws Exception;

    List<Map> getStntMdulFdbShare_selectStdDtaResult(Map<String, Object> paramData) throws Exception;
}
