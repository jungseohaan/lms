package com.visang.aidt.lms.api.lecture.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface StntCrcuQuizMapper {
    List<Map> findStntToolQuizList(Map<String, Object> paramData) throws Exception;

    Map<String, Object>findStntToolQuizCall_spotQizInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findStntToolQuizCall_spotQizDistract(Map<String, Object> paramData) throws Exception;

    int createStntToolQuizSubmit(Map<String, Object> paramData) throws Exception;
    Optional<Map<String, Object>> findStntTollQuizHist(Map<String, Object> paramData) throws Exception;
}
