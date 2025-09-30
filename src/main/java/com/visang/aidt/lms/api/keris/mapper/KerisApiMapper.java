package com.visang.aidt.lms.api.keris.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * packageName : com.visang.aidt.lms.api.keris.mapper
 * fileName : KerisApiMapper
 * USER : user
 * date : 2024-05-16
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-05-16         user          최초 생성
 */
@Mapper
public interface KerisApiMapper {

    Map<String, Object> getUserInfo(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getPtnInfo(Map<String, Object> paramMap) throws Exception;
    void updatePtnInfo(Map<String, Object> paramMap) throws Exception;
    void insertUser(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getSchlInfo(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getTcClaMbInfoSt(Map<String, Object> paramMap) throws Exception;
    void insertSchool(Map<String, Object> paramMap) throws Exception;
    void insertStdtRegInfo(Map<String, Object> paramMap) throws Exception;
    void insertSchedule(Map<String, Object> paramMap) throws Exception;
    void insertTcRegInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcClaInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcClaMbInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getUserStudyInfo(Map<String, Object> paraMap) throws Exception;
    Map<String, Object> getUserTcInfo(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getClaId(String claSubId) throws Exception;
    List<Map<String, Object>> getUserMathStudyInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getUserEnglStudyInfo(Map<String, Object> paramMap) throws Exception;
    void insertTransferTcClaMbInfo(Map<String, Object> paramMap) throws Exception;
    void insertTransferStdtRegInfo(Map<String, Object> paramMap) throws Exception;
    void insertAidtNwStdtInfo(Map<String, Object> paramMap) throws Exception;
    void deleteAidtNwStdtInfo(String userId) throws Exception;
    void updateStdtRegInfoOut(String userId) throws Exception;
    void updateTcClaMbInfoOut(String userId) throws Exception;

    String selectExistsClaId(Map<String, Object> paramMap) throws Exception;

    String getClaIdFromTcClaMbInfo(Map<String, Object> paramMap) throws Exception;

    int getTextbkIdFromTcTextbook(Map<String, Object> paramMap) throws Exception;

    int updatePersonaClaIdUpdate(Map<String, Object> paramMap) throws Exception;

    int updatePersonaUserUpdate(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getTcClaInfo(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> listTcClaStdtInfo(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> listStdtRegInfo(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> listOtherTcClaMbStdtInfo(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> listTcClaMbInfo(Map<String, Object> paramMap) throws Exception;

    int deleteSchdul(Map<String, Object> paramMap) throws Exception;

    int updateStdtRegInfo(Map<String, Object> paramMap) throws Exception;

    int updateTcRegInfo(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> listSchdulInfo(Map<String, Object> paramMap) throws Exception;

    int updateUserindvInfoAgreYn(Map<String, Object> paramMap) throws Exception;

    int updateTcClaMbInfoActv(Set<String> semesterList) throws Exception;

    void insertUserBulk(List<Map<String, Object>> paramMap) throws Exception;
    void insertStdtRegInfoBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertTcRegInfoBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertTcClaInfoBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void updateTcClaInfoBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertTcClaMbInfoBulk(List<Map<String, Object>> paramMap) throws Exception;
    int updateTcClaMbInfoOutBulk(Map<String, Object> paramMap) throws Exception;

    void insertShopSkinHist(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopGameHist(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopProfileHist(List<Map<String, Object>>  paramMap) throws Exception;

    void insertShopSkin(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopGame(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopProfile(List<Map<String, Object>>  paramMap) throws Exception;

    List<Map<String, Object>> listClaInfo(Map<String, Object> paramMap) throws Exception;

    void updateOverWriteClaId(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getUserTypeAndStatus(Map<String, Object> paramData);

    String selectPrevClaId(Map<String, Object> paramData) throws Exception;
    void pasteTcTextbook(Map<String, Object> paramData) throws Exception;
    void pasteTcCurriculum(Map<String, Object> paramData) throws Exception;
    void pasteTabInfo(Map<String, Object> paramData) throws Exception;
    List<String> selectStdtList(Map<String, Object> paramData) throws Exception;
    void pasteEvlInfo(Map<String, Object> paramData) throws Exception;
    void pasteTaskInfo(Map<String, Object> paramData) throws Exception;
    List<Integer> selectEvlIdList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectEvlIemInfo(Map<String, Object> paramData) throws Exception;
    void insertEvlIemInfoBulk(Map<String, Object> paramData) throws Exception;
    String getPrevClaId(Map<String, Object> paramData) throws Exception;
    String getRegularClaExistsYn(Map<String, Object> paramData) throws Exception;


    int selectTcClaUserInfoCheck(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> selectTcClaUserInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> selectTcClaMainInfo(Map<String, Object> paramMap) throws Exception;

    void insertTcClaUserInfo(Map<String, Object> paramData);

    int selectTcClaUserHistCount(Map<String, Object> paramMap) throws Exception;

    void insertTcClaUserHist(Map<String, Object> paramData);

    void updateTcClaUserHistMainTeacherToLeave(Map<String, Object> paramData);

    void updateTcClaUserInfo(Map<String, Object> paramData);

    void updateTcClaUserInfoMainTeacherToEmpty(Map<String, Object> paramData);

    /**
     * 특정 사용자의 히스토리를 나간 상태로 업데이트
     * @param paramData user_id, cla_id 포함
     */
    void updateTcClaUserHistToLeave(Map<String, Object> paramData);

    /**
     * 현재 선생님의 역할 조회 (주교사: Y, 보조교사: N)
     * @param paramData user_id, cla_id 포함
     * @return main_sub_flag 값 ("Y" 또는 "N")
     */
    String selectCurrentTeacherRole(Map<String, Object> paramData);

    List<Map<String, Object>> getSelectClaInfo(Map<String, Object> paramMap) throws Exception;

    int selectTcClaUserTeachHistCount(Map<String, Object> paramMap) throws Exception;

    int upsertTcClaUserInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getTextbookInfo(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> getGroupMappingInfo(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> getFirstSemesterLectureList(Map<String, Object> paramData) throws Exception;

    // 매핑 데이터 셋팅 관련 메소드들
    Map<String,Object> getClaIdFromLectureCode(Map<String, Object> paramData) throws Exception;
    
    void insertTcClaGroupInfo(Map<String, Object> paramData) throws Exception;
    
    Map<String, Object> getGroupClaInfo(Map<String, Object> paramData) throws Exception;
    
    void updateTcClaMbInfoActivation(Map<String, Object> paramData) throws Exception;

    Map<String,Object> getTcClaGroupInfo(Map<String, Object> paramData) throws Exception;
}
