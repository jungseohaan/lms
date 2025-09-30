package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntMdulHdwrtMapper {
    int modifyStntEvalMdulHdwrtSave(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntEvalMdulHdwrtView(Map<String, Object> paramData) throws Exception;
    List<Map> findStntEvalMdulHdwrtShareList(Map<String, Object> paramData) throws Exception;

    int modifyStntHomewkMdulHdwrtSave(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntHomewkMdulHdwrtView(Map<String, Object> paramData) throws Exception;
    List<Map> findStntHomewkMdulHdwrtShareList(Map<String, Object> paramData) throws Exception;

}
