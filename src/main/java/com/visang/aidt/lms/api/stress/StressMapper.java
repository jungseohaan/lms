package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface StressMapper {

    List<Map> findStntMdulQstnView(Map<String, Object> paramData) throws Exception;
    List<Map> findStntMdulQstnViewHist(List<LinkedHashMap<Object, Object>> paramData) throws Exception;
    List<Map> findStntMdulQstnViewOtherList(List<LinkedHashMap<Object, Object>> paramData) throws Exception;

     Map<String, Object> getStdtRegInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> findTchSlfperEvlSlfSet(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlSlfViewSl(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlSlfViewPerInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchSlfperEvlSlfView_perResultInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findSTchSlfperEvlSlfViewTemplt(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findMdulSlfPerEvlAt(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findShopUserInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemProfileInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemGameInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemSkinInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemProfileInfoDefault(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemGameInfoDefault(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemSkinInfoDefault(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntSlfperEvlSlfSet(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSlfperEvlSlfSetSlList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSlfperEvlSlfSetTempltList(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntSlfperEvlSlfSetSlfperYn(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSlfperEvlSlfPerinfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntSlfperEvlSlfSetTempltYn(Map<String, Object> paramData) throws Exception;

    int modifyStntMdulQstnResetSDRI(Map<String, Object> paramData) throws Exception;
    int createStntMdulQstnResetSDRI(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnResetSetsId(Map<String, Object> paramData) throws Exception;
    int modifyStntMdulQstnResetSDRD(Map<String, Object> paramData) throws Exception;
    int createStntMdulQstnResetSDRD(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnRecheck(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnResetSDRI(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulQstnResetSDRD(Map<String, Object> paramData) throws Exception;

    void insertLesnRsc(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectEngtempInfoByArticleId(@Param("articleId") String articleId, @Param("subId") Integer subId) throws Exception;
    String selectEngtempExistsYn(@Param("resultDetailId") Integer resultDetailId, @Param("engTempId") Integer engTempId) throws Exception;
    List<Map<String, Object>> selectEngtempAtivityList(@Param("resultDetailId") Integer resultDetailId, @Param("engTempId") Integer engTempId) throws Exception;

    List<Map> findStntActMdulList(Map<String, Object> paramData) throws Exception;

    List<Map<String,Object>> selectCurriculumList(Map<String,Object> param) throws Exception;

    Map<String,Object> selectStntCrcuLastposition(Map<String,Object> param) throws Exception;
    int createStntCrcuLastposition(Map<String, Object> paramData) throws Exception;
    int updateStntCrcuLastposition(Map<String, Object> paramData) throws Exception;

    List<Map<String,Object>> findCrcuTabList(Map<String,Object> param) throws Exception;

    Map<String,Object> findByUserId(Map<String,Object> param) throws Exception;
    List<Map<String,Object>> findStdtListByClass(Map<String,Object> param) throws Exception;
    Map<String,Object> findStdtInfo(Map<String,Object> param) throws Exception;
    Map<String,Object> findClassInfo(Map<String,Object> param) throws Exception;

    Map<String, Object> getTcTextbookInfo(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getStTextbookInfo(Map<String, Object> paramMap) throws Exception;

    int updateLgnSttsAt(Map<String, Object> param) throws Exception;

    Map<String, Object> selectTchToolExistCheck(Object paramData) throws Exception;
    int insertTchToolInfo(Object paramData) throws Exception;
    List<Map> findTchMdulQstnAnswResultDetailInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchMdulQstnAnswResultInfo(List<LinkedHashMap<Object, Object>> paramData, Map<String, Object> paramMap) throws Exception;
    List<Map> findTchMdulQstnAnswSelfStd(List<LinkedHashMap<Object, Object>> paramData, Map<String, Object> paramMap) throws Exception;

    //신규 추가
    List<Map> findTchSlfperEvlSlfSetList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> findTchSlfperEvlPerSetList(Map<String, Object> paramData) throws Exception;
    List<Map> findMdulSlfPerEvlAt(List<LinkedHashMap<Object, Object>> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementDistribution(Map<String, Object> paramData);

    List<Map> selectTchDsbdAreaAchievementDistributionSummary(Map<String, Object> paramData);

    List<Map> selectTchDsbdUnitInfo(Map<String, Object> paramData);

    List<Map> selectTchDsbdUnitAchievementList(Map<String, Object> paramData);

    List<Map> selectTchDsbdAreaAchievementStudentDstribution(Map<String, Object> paramData);

    List<Map> selectStntDsbdAreaAchievementStudentDistributionSummary(Map<String, Object> paramData);

    List<Map> selectStntDsbdUnitInfo(Map<String, Object> paramData);

    Map<String, Object> findUserInfoByUserId(String stntId);

    List<Map> selectStntDsbdUnitAchievementList(Map<String, Object> paramData);

    List<Map<String, Object>> findTextbookCrcuList(Map<String, Object> paramData);

    Map<String, Object> findStntEvalStart(Map<String, Object> paramData);

    Map<String, Object> findConceptCheck(Map<String, Object> paramData);

    Map<String, Object> findStntEvalSaveIemScr(Map<String, Object> paramData);

    int modifyStntEvalSaveResultDetail(Map<String, Object> paramData);

    Map<String, Object> findEvalResuldDetail(Map<String, Object> paramData);

    int removeVocalEvlScrInfo(Map<String, Object> param);

    int createVocalEvlScrInfo(Map<String, Object> param);

    void createVocalEvlScrDetail(Map<String, Object> itemDetail);

    void createVocalEvlScrColor(Map<String, Object> vocalColorList);

    void createVocalEvlPhoneLevel(Map<String, Object> vocalPhoneList);

    Map<String, Object> findEvlResultDetailCount(Map<String, Object> paramData);

    int modifyStntEvalResultInfo(Map<String, Object> evlResultMap);

    int modifyStntEvalSaveResultInfo(Map<String, Object> paramData);
}
