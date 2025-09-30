package com.visang.aidt.lms.api.contents.repository;

import com.visang.aidt.lms.api.contents.dto.*;
import com.visang.aidt.lms.api.library.dto.*;
import org.apache.ibatis.annotations.Mapper;

import com.visang.aidt.lms.api.system.dto.MetaVO;

import java.util.List;


@Mapper
public interface ArticleRepositoryX {

    public List<ArticleVO> getArticleList(ArticleSearchRequestVO vo) throws Exception;

    public List<ArticleVO> getArticleSearch(ArticleSearchRequestVO vo) throws Exception;

    public ArticleVO addArticle(ArticleVO vo) throws Exception;

    public ArticleExVO getArticleById(ArticleInfoVO vo) throws Exception;

    public ArticleExVO getArticleExById(ArticleInfoVO vo) throws Exception;


    public Long updateArticle(ArticleExVO article) throws Exception;

    public Long versionUpArticle(ArticleExVO article) throws Exception;

    public Long tempUpdateArticle(ArticleExVO article) throws Exception;


    public List<SetsArticleInfoVO> getArticleBySetsId(String sets_id) throws Exception;


    public void addArticleMetaMap(ArticleMetaMapVO metamap) throws Exception;

    public void addLibraryArticleMap(LibraryArticleMapVO map) throws Exception;

    public void addLibtextArticleMap(LibtextArticleMapVO map) throws Exception;


    public void deleteArticleMetaMap(String targetId) throws Exception;

    public void deleteLibraryArticleMap(String targetId) throws Exception;

    //public void deleteLibtextArticleMap(Long targetId) throws Exception;
    public void deleteLibtextArticleMapByLibtextId(Long targetId) throws Exception;


    public List<MetaVO> getArticleMetaList(String article_id) throws Exception;

    public List<LibraryVO> getLibraryArticleList(String article_id) throws Exception;

    public List<LibtextVO> getLibtextArticleList(String article_id) throws Exception;

    public List<ArticleVO> getArticleListByLibtextId(String libtext_id) throws Exception;

}
