package com.visang.aidt.lms.api.integration.mapper;


import com.visang.aidt.lms.api.integration.vo.WithdrawUserDto;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Mapper
public interface IntegPublishMapper {

    String selectClaId(Map<String, Object> param) throws Exception;

    void insertUserInfo(Map<String, Object> param) throws Exception;

    int selectUserExists(Map<String, Object> param) throws Exception;

    void insertTcClaInfo(Map<String, Object> param) throws Exception;

    void insertTcRegInfo(Map<String, Object> param) throws Exception;

    void insertStdtRegInfo(Map<String, Object> param) throws Exception;

    void insertTcClaMbInfo(Map<String, Object> param) throws Exception;

    void insertUserBulk(List<Map<String, Object>> paramMap) throws Exception;

    void insertStdtRegInfoBulk(List<Map<String, Object>> paramMap) throws Exception;

    void insertTcClaInfoBulk(List<Map<String, Object>> paramMap) throws Exception;

    void upsertTcClaUserInfo(List<Map<String, Object>> paramMap) throws Exception;

    void insertTcClaMbInfoBulk(List<Map<String, Object>> paramMap) throws Exception;

    void updateActvTnBulk(List<Map<String, Object>> paramMap) throws Exception;

    Map<String, Object> selectExamInfo(@Param("examId") String examId) throws Exception;

    int insertExamPublish(Map<String, Object> param) throws Exception;

    int createTchEvalCreateForTextbk_evlResultInfoCustom(Map<String, Object> param) throws Exception;

    int createTchTaskCreateForTextbk_taskResultInfoCustom(Map<String, Object> param) throws Exception;

    List<Map> listPublishGrpInfo(Map<String, Object> param);

    List<Map> selectPublishBoxList(PagingParam<?> pagingaPram);

    List<Map> selectPublishBoxClaStdtList(PagingParam<?> pagingParam);

    List<Map> selectPublishBoxClaStdtDetailList(PagingParam<?> pagingParam);

    List<Map> selectPublishBoxNicknameList(PagingParam<?> pagingParam);

    Map<String, Object> findPublishBoxDetail(Map<String, Object> paramData);

    Map<String, Object> getPublishTypeAndTrgtId(Map<String, Object> paramData);

//    List<Map> taskStntDetail(Map<String, Object> paramData);

    List<Map> selectEvlStntDetail(Map<String, Object> paramData);

    int updateUseHint(Map<String, Object> paramData);

    int deletePublishBox(Map<String, Object> paramData);

    List<Map> getPublishBoxEndingSoonCnt(Map<String, Object> paramData);

    List<Map> getPublishBoxNeedFeedBackCnt(Map<String, Object> paramData);

    List<Map> getPublishBoxNeedCheckCnt(Map<String, Object> paramData);

    List<Map> selectPublishBoxNeedCheckDetailList(Map<String, Object> paramData);

    List<Map> selectPublishBoxNeedFeedBackDetailList(Map<String, Object> paramData);

    List<Map> selectPublishBoxEndingSoonDetailList(Map<String, Object> paramData);

//    List<Map> findPublishBoxRecentOptionList(Map<String, Object> paramData);

//    List<Map> findPublishBoxRecentOptionClaInfoList(Map<String, Object> paramData);

    int insertPublishGrp(Map<String, Object> param);

    int checkUserExists(Map<String, Object> param);

    void createPublishNickNmUserAuth(Map<String, Object> paramData);

    boolean validateAuth(Map<String, Object> paramData);

    int updatePassword(Map<String, Object> paramData);

    int insertPodOption(Map<String, Object> podMap);

    List<Map<String, Object>> findPublishBoxRecentOptionList2(Map<String, Object> paramData);

    List<Map<String, Object>> findPublishBoxOptionPublishInfoList(Map<String, Object> paramData);

    List<Map<String, Object>> findPublishBoxOptionClaInfoList(Map<String, Object> paramData);

    List<Map<String, Object>> selectPublishDeleteInfoList(Map<String, Object> paramData);

    List<Map> findStdtByFlnm(Map<String, Object> paramData);

    List<Map<String, Object>> selectPublishClaStdtClaList(Map<String, Object> paramData);

    List<Map<String, Object>> selectPublishPodOptionByPublishId(Map<String, Object> paramData);

    boolean checkStdtInWrterCla(Map<String, Object> paramData);

    int updatePublishNickNmAuthInit(Map<String, Object> paramData);

    String checkUserInit(Map<String, Object> paramData);

    int createTchEvalCreateForTextbk_evlResultDetail(Map<String, Object> paramData);

    // 뚝딱
    void updateTimTime(Map<String, Object> paramData);
    List<Map> hasStdtTakenEvl(Map<String, Object> paramData);
    void updatePdSetAtN(Map<String, Object> paramData);
    void updatePdSet(Map<String, Object> paramData);
    Map<String, Object> getSttsCdAndPdSetAt(Map<String, Object> paramData);
    boolean isStdtInCla(Map<String, Object> paramData);

    int createTchEvalCreateForTextbk_evlInfo(Map<String, Object> createMap);
    int createTchEvalCreateForTextbk_evlIemInfo(Map<String, Object> createMap);

    int modifyEvalStatusToInProgress(Map<String, Object> createMap);

    Map<String, Object> findEvlInfo(Map<String, Object> paramData);

    void deleteTchEvalDeleteEvalResultDetail(Map<String, Object> paramData);

    void deleteTchEvalDeleteEvalResultInfo(Map<String, Object> paramData);

    void deleteTchEvalDeleteEvalIemInfo(Map<String, Object> paramData);

    void deleteTchEvalDeleteEvalTrnTrget(Map<String, Object> paramData);

    void deleteTchEvalDeleteEvalInfo(Map<String, Object> paramData);

    int selectPublishBoxExamLiveOnStatus(Map<String, Object> paramData);

    int insertVivaClassWithDrawUser(List<WithdrawUserDto> withdrawUserDto);

    List<Map<String, Object>> selectWithDrawTcList();
    List<Map<String, Object>> selectWithDrawStList();
    List<String> selectStdtListWithTcList(List<String> tcList);
    void deleteTcClaMbInfoWithTcIdList(List<String> tcList);
    void deleteTcClaInfoWithTcIdList(List<String> tcList);
    void deleteTcRegInfoWithTcIdList(List<String> tcList);
    void deleteStdtRegInfoWithTcIdList(List<String> stdtList);
    void deleteSpPrchsHist(List<String> userList);
    void deleteSpPrchsInfo(List<String> userList);
    void deleteUserWithUserId(List<String> userList);

    int selectPublishBoxExist(Map<String, Object> paramData);
}
