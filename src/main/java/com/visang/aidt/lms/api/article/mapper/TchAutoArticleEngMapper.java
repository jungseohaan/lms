package com.visang.aidt.lms.api.article.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchAutoArticleEngMapper {
    List<Map> findTchHomewkAutoQstnExtrEng(Map<Object, Object> procParamData) throws Exception;
}
