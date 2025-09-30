package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionMapper {
    List<Map> findQuestionList(Map<String, Object> paramData) throws Exception;

    // 다른문제풀기 - frequency 적용
    List<Map> findQuestionList2(Map<String, Object> paramData) throws Exception;

    // 다른문제풀기
    // 아티클(유사,쌍둥이) 맵(article_article_map) 사용
    List<Map> findQuestionList3(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTabInfo(Map<String, Object> paramData) throws Exception;
}
