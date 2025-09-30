package com.visang.aidt.lms.api.lecture.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchCrcuQuizMapper {
    int createTchToolQuizForm_spotQizInfo(Map<String, Object> paramData) throws Exception;
    int insertTchToolQuizForm_spotQizInfo(Map<String, Object> paramData) throws Exception;
    int updateTchToolQuizForm_spotQizInfo(Map<String, Object> paramData) throws Exception;
    int createTchToolQuizForm_spotQizDistract(Map<String, Object> paramData) throws Exception;

    List<Map> findTchToolQuizView_spotQizInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchToolQuizView_spotQizDistractList(Map<String, Object> paramData) throws Exception;

    int modifyTchToolQuizStart(Map<String, Object> paramData) throws Exception;
    int modifyTchToolQuizEnd(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchToolQuizResult(Map<String, Object> paramData) throws Exception;
    List<Map> findTchToolQuizResult_qizInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchToolQuizResult_qizStntInfoList(Map<String, Object> paramData) throws Exception;

    int removeTchToolQuizDel_spotQizDistract(Map<String, Object> paramData) throws Exception;
    int removeTchToolQuizDel_spotQizResult(Map<String, Object> paramData) throws Exception;
    int removeTchToolQuizDel_spotQizInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchToolQuizNav(Map<String, Object> paramData) throws Exception;

    int removeTchToolQuizInit_spotQizResult(Map<String, Object> paramData) throws Exception;
    int removeTchToolQuizForm_spotQizDistract(List<Map<String, Object>> paramData) throws Exception;
}
