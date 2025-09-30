package com.visang.aidt.lms.api.stress.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StressTiosApiMapper {

    Map<String, Object> getUserInfo(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getPtnInfo(Map<String, Object> paramMap) throws Exception;
    void insertUser(Map<String, Object> paramMap) throws Exception;
    void insertTcRegInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcClaInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcClaMbInfo(Map<String, Object> paramMap) throws Exception;
    void insertTiosUserInfo(Map<String, Object> paramMap) throws Exception;
    void insertVivasamUserInfo(Map<String, Object> paramMap) throws Exception;
    void insertUserBulk(List<Map<String, Object>> paramMap) throws Exception;
    void insertStdtRegInfoBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertTcClaMbInfoBulk(List<Map<String, Object>> paramMap) throws Exception;
    void insertTiosUserInfoBulk(List<Map<String, Object>> paramMap) throws Exception;
    Map<String, Object> getStdTransInfo(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getStdDtaResultInfo(Map<String, Object> paramMap) throws Exception;
    int insertStdDtaResulInfo(Map<String, Object> paramMap) throws Exception;
    int insertStdDtaResulDetail(Map<String, Object> paramMap) throws Exception;
    int insertEngUsdAchSrc2Info(Map<String, Object> paramMap) throws Exception;
    int insertEngUsdAchSrc2Detail(Map<String, Object> paramMap) throws Exception;
    int insertEngUsdAchSrc2Kwg(Map<String, Object> paramMap) throws Exception;
    int insertAchCacSrcInfo(Map<String, Object> paramMap) throws Exception;
    int insertUsdCacSrcInfo(Map<String, Object> paramMap) throws Exception;
    int insertStdUsdUnitDayHist(Map<String, Object> paramMap) throws Exception;
    int insertStdUsdUnitKwgDayHist(Map<String, Object> paramMap) throws Exception;
    int insertStdUsdStdtUnitKwgDayHist(Map<String, Object> paramMap) throws Exception;
    int insertStdUsdUnitInfo(Map<String, Object> paramMap) throws Exception;


}
