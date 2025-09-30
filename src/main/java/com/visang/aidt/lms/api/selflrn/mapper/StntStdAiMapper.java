package com.visang.aidt.lms.api.selflrn.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntStdAiMapper {

    Map<String, Object> findStntStdAi_slfAiStdInfo(Map<String, Object> param) throws Exception;
    List<Map<String, Object>> findStntStdAi_stdUsdInfo(Map<String, Object> param) throws Exception;
    Map<String, Object> findStntStdAi_studyMapTwo(Map<String, Object> param) throws Exception;
    Map<String, Object> findStntStdAi_newArticleId(Map<String, Object> param) throws Exception;
    int createStntStdAi_slfAiStdInfo(Map<String, Object> param) throws Exception;
    int createStntStdAi_aiStdResultInfo(Map<String, Object> param) throws Exception;
    Map<String, Object> findStntStdAi_aiStdResultInfo(Map<String, Object> param) throws Exception;
    int modifyStntStdAi_aiStdResultInfo(Map<String, Object> param) throws Exception;

    List<Map<String, Object>> findStntStdAi_slfAiStdInfo_aiStdResultInfo(Map<String, Object> param) throws Exception;

    int modifyStntStdAi_aiStdInfo_edAt(Map<String, Object> param) throws Exception;
    Map<String, Object> findStntStdAi_rwdEarnHist(Map<String, Object> param) throws Exception;
    Map<String, Object> findStntStdAi_rwdEarnHist_total(Map<String, Object> param) throws Exception;

    List<Map<String, Object>> findStntSelfStdInfoList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findSlfAiStdResultInfo(Map<String, Object> paramData) throws Exception;

    int modifyStntStdAiInit_edAt(Map<String, Object> param) throws Exception;
}
