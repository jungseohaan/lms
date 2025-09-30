package com.visang.aidt.lms.api.selflrn.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface StntStdAiEngMapper {

    Map<String, Object> findStntStdAiInitEng(Map<String, Object> param) throws Exception;
    Map<String, Object> findUserById(Map<String, Object> param) throws Exception;
    Map<String, Object> findTcCurriculum(Map<String, Object> param) throws Exception;
}
