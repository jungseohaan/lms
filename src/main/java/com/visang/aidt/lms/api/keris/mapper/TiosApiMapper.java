package com.visang.aidt.lms.api.keris.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TiosApiMapper {

    Map<String, Object> getUserInfo(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getPtnInfo(Map<String, Object> paramMap) throws Exception;
    void insertUser(Map<String, Object> paramMap) throws Exception;
    void insertStdtRegInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcRegInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcClaInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcClaMbInfo(Map<String, Object> paramMap) throws Exception;
    void insertTiosUserInfo(Map<String, Object> paramMap) throws Exception;
    void insertVivasamUserInfo(Map<String, Object> paramMap) throws Exception;
    void insertVivasamUserInfoBulk(List<Map<String, Object>> paramMap) throws Exception;
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
    int insertStdUsdInfo(Map<String, Object> paramMap) throws Exception;
    int insertStdUsdUnitInfo(Map<String, Object> paramMap) throws Exception;
    int insertStdUsdDayHist(Map<String, Object> paramMap) throws Exception;

    void insertShopSkinHist(Map<String, Object>  paramMap) throws Exception;
    void insertShopGameHist(Map<String, Object>  paramMap) throws Exception;
    void insertShopProfileHist(Map<String, Object>  paramMap) throws Exception;
    void insertShopSkin(Map<String, Object>  paramMap) throws Exception;
    void insertShopGame(Map<String, Object>  paramMap) throws Exception;
    void insertShopProfile(Map<String, Object>  paramMap) throws Exception;
    void insertShopSkinHistBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopGameHistBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopProfileHistBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopSkinBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopGameBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopProfileBulk(List<Map<String, Object>>  paramMap) throws Exception;

    Map<String, Object> selectTeacherClassInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectTeacherClassStudentList(Map<String, Object> paramMap) throws Exception;
    String selectStudentResultExistsYn(List<Map<String, Object>> paramMap) throws Exception;
    List<Map<String, Object>> selectTcTextbookListForCurriTabSave(Map<String, Object> paramMap) throws Exception;

    void upsertTcClaUserInfo(Map<String, Object> paramMap) throws Exception;

}
