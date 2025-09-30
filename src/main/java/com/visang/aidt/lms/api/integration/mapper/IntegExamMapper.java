package com.visang.aidt.lms.api.integration.mapper;


import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IntegExamMapper {

    int getExamBoxNewVersion(Map<String, Object> paramData) throws Exception;

    int insertExamBox(Map<String, Object> paramData) throws Exception;

    int increaseModuleTcUseCnt(Map<String, Object> paramData) throws Exception;

    int deleteExamBox(Map<String, Object> paramData) throws Exception;

    Map getExamBoxInfo(Map<String, Object> paramData) throws Exception;

    List<Map> listExamBoxInfo(PagingParam<?> paramData) throws Exception;

    List<Map> listExamBoxHist(PagingParam<?> paramData) throws Exception;

    List<Map> listTextbkByExamHist(Map<String, Object> paramData) throws Exception;

    String getExamBoxDelAtStatus(Map<String, Object> paramData);

    String getExamBoxRegDt(Map<String, Object> paramData);

    int checkSetSummaryExists(Map<String, Object> paramData);

    List<Map> findLesnRscList_meta(Map<String, List<Object>> paramMap);

    List<Map> findLesnRscList_article(Map<String, List<Object>> paramMap);

    List<Map> findLesnRscList_articleType(Map<String, List<Object>> paramMap);

    List<Map> findLesnRscInfo_coment(Map<String, Object> paramData);

    List<Map> findLesnRscList_difficulty(Map<String, List<Object>> paramMap);
}
