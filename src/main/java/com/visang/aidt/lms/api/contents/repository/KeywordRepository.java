package com.visang.aidt.lms.api.contents.repository;

import java.util.List;

import com.visang.aidt.lms.api.contents.dto.KeywordMapVO;
import com.visang.aidt.lms.api.contents.dto.KeywordVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KeywordRepository {
    public Long getKeywordId(String value) throws Exception;

    public Long insertKeyword(String value) throws Exception;


    //article
    public void addArticleKeywordMap(List<KeywordMapVO> keywordId) throws Exception;

    public void deleteArticleKeywordMap(String targetId) throws Exception;

    //sets
    public void addSetsKeywordMap(List<KeywordMapVO> keywordId) throws Exception;

    public void deleteSetsKeywordMap(String targetId) throws Exception;

    //template
    public void addTemplateKeywordMap(List<KeywordMapVO> keywordId) throws Exception;

    public void deleteTemplateKeywordMap(Long targetId) throws Exception;

    public void insertKeywordCms(KeywordVO keywordVO) throws Exception;
}
