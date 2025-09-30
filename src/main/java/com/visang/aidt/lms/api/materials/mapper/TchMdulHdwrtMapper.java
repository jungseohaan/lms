package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface TchMdulHdwrtMapper {
    int createTchEvalMdulHdwrtSave(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchEvalMdulHdwrtView(Map<String, Object> paramData) throws Exception;
    int createTchEvalMdulHdwrtShare(Map<String, Object> paramData) throws Exception;

    int createTchHomewkMdulHdwrtSave(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchHomewkMdulHdwrtView(Map<String, Object> paramData) throws Exception;
    int createTchHomewkMdulHdwrtShare(Map<String, Object> paramData) throws Exception;
}
