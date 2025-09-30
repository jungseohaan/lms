package com.visang.aidt.lms.api.kafka.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface KafkaMathMapper {

    int deleteStdUsdTarget_1(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_2(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_3(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_4(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_5(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_6(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_7(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdTarget_8(Map<String, Object> paramData) throws Exception;

    int deleteMvStdUsdTarget_1(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdUnitTarget_2(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdInfoTarget_3(Map<String, Object> paramData) throws Exception;
    int deleteStdUsdUnitDayHistTarget_4(Map<String, Object> paramData) throws Exception;

    int insertUsdCacSrcInfo(Map<String, Object> paramData) throws Exception;
    int insertStdUsdDayHist(Map<String, Object> paramData) throws Exception;
    int insertStdUsdNotStdtUnitDayHist(Map<String, Object> paramData) throws Exception;
    int insertStdUsdUnitDayHist(Map<String, Object> paramData) throws Exception;
    int insertStdUsdUnitKwgDayHist(Map<String, Object> paramData) throws Exception;
    int insertStdUsdStdtUnitKwgDayHist(Map<String, Object> paramData) throws Exception;
    int insertStdUsdInfo(Map<String, Object> paramData) throws Exception;
    int insertStdUsdUnitInfo(Map<String, Object> paramData) throws Exception;
    int insertStdUsdTotalHist(Map<String, Object> paramData) throws Exception;
    int insertStdUsdContentAreaHist(Map<String, Object> paramData) throws Exception;
}
