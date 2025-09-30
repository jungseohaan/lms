package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionEngMapper {
    List<Map<String,Object>> findQuestionListEng(Map<String, Object> paramData) throws Exception;
}
