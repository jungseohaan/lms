package com.visang.aidt.lms.api.learning.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StdUsdCalculateMapper {
    int deleteStdUsdTarget_1(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_2(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_3(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_4(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_5(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_6(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findStdUsdTextbkTargetList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> findStdUsdTargetList(Map<String, Object> paramData) throws Exception;

    int insertUsdCacSrcInfo(Map<String, Object> paramData) throws Exception;

    int insertStdUsdDayHist(Map<String, Object> paramData) throws Exception;

    int insertStdUsdNotStdtUnitDayHist(Map<String, Object> paramData) throws Exception;

    int insertStdUsdUnitDayHist(Map<String, Object> paramData) throws Exception;

    int insertStdUsdUnitKwgDayHist(Map<String, Object> paramData) throws Exception;

    int insertStdUsdStdtUnitKwgDayHist(Map<String, Object> paramData) throws Exception;

    int insertStdUsdInfo(Map<String, Object> paramData) throws Exception;

    int insertStdUsdUnitInfo(Map<String, Object> paramData) throws Exception;
    
    
    /**
     * 영어 성취도
     */
    
    List<Map<String, Object>> findEngStdUsdTargetList(Map<String, Object> paramData) throws Exception;
    
    int insertEngUsdAchSrc2Info(Map<String, Object> s) throws Exception;
    int insertEngUsdAchSrc2Detail(Map<String, Object> s) throws Exception;
    int insertEngUsdAchSrc2Kwg(Map<String, Object> s) throws Exception;
    int updateEngUsdAchSrc2Info(Map<String, Object> s) throws Exception;

    int insertAchCacSrcInfo(Map<String, Object> s) throws Exception;


    int insertEngUsdAchSrc2InfoDaily(Map<String, Object> s) throws Exception;
    int insertEngUsdAchSrc2DetailDaily(Map<String, Object> s) throws Exception;
    int insertEngUsdAchSrc2KwgDaily(Map<String, Object> s) throws Exception;
    int updateEngUsdAchSrc2InfoDaily(Map<String, Object> s) throws Exception;



    
    int deleteEngStdUsdTarget_1(Map<String, Object> paramData) throws Exception;
    int deleteEngStdUsdTarget_2(Map<String, Object> paramData) throws Exception;
    int deleteEngStdUsdTarget_3(Map<String, Object> paramData) throws Exception;
    int deleteEngStdUsdTarget_4(Map<String, Object> paramData) throws Exception;



}
