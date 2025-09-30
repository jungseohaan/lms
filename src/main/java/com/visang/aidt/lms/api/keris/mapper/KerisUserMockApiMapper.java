package com.visang.aidt.lms.api.keris.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface KerisUserMockApiMapper {

    /**
     * 학생 성명 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getStdtName(Map<String, Object> paramData) throws Exception;

    /**
     * 학생 학교 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getStdtSchoolName(Map<String, Object> paramData) throws Exception;

    /**
     * 학생 학교 ID 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getStdtSchoolId(Map<String, Object> paramData) throws Exception;

    /**
     * 교사 성명 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getTcName(Map<String, Object> paramData) throws Exception;

    /**
     * 교사 학교 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getTcSchoolName(Map<String, Object> paramData) throws Exception;

    /**
     * 교사 학교 ID 조회
     * @param paramData
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getTcSchoolId(Map<String, Object> paramData) throws Exception;

    /**
     * 학생 학교 구분 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getStduentDivision(Map<String, Object> paramMap) throws Exception;

    /**
     * 학생 학년 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getStduentGrade(Map<String, Object> paramMap) throws Exception;

    /**
     * 학생 반 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getStduentClass(Map<String, Object> paramMap) throws Exception;

    /**
     * 학생 번호 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getStduentNumber(Map<String, Object> paramMap) throws Exception;

    /**
     * 학생 번호 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> getClassList(Map<String, Object> paramMap);


    /**
     * 학생 전체정보 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    Map<String, Object> getStdtAll(Map<String, Object> param);

    /**
     * 교사 전체정보 조회
     * @param paramMap
     * @return
     * @throws Exception
     */
    Map<String, Object> getTcAll(Map<String, Object> param);

    /**
     * 사용자 식별 ID를 통한 교사 시간표 조회
     * @param param
     * @return
     */
    List<Map<String, Object>> scheduleList(Map<String, Object> param);

    int updateUserindvInfoAgreYn(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getClassInfo(Map<String, Object> param);

    String getClaIdFromTcId(@Param("tcId") String tcId) throws Exception;

    int updateClassActvtnAt(Map<String, Object> paramMap) throws Exception;

    int insertTcClaMbInfo(Map<String, Object> paramMap) throws Exception;

    void updateTcClaInfo(Map<String, Object> param) throws Exception;

    void updateTcClaMbInfo(Map<String, Object> param) throws Exception;

    void updateTcTextbook(Map<String, Object> param) throws Exception;

    void updateTcCurriculum(Map<String, Object> param) throws Exception;

    void updateTabInfo(Map<String, Object> param) throws Exception;

    void updateTcLastLesson(Map<String, Object> param) throws Exception;

    void updateTbDgnssInfo(Map<String, Object> param) throws Exception;

    void updateGlSetInfo(Map<String, Object> param) throws Exception;

    void updateEvlInfo(Map<String, Object> param) throws Exception;

    void updateTaskInfo(Map<String, Object> param) throws Exception;

    int insertTcLecture(Map<String, Object> param) throws Exception;

    int insertTcLectureBefore(Map<String, Object> param) throws Exception;

    Map<String, Object> getTcInfo(Map<String, Object> param);

    int insertUser(Map<String, Object> paramMap) throws Exception;
    int insertStdtRegInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getPtnInfoById(Map<String, Object> param);

    void insertTcClaInfo(Map<String, Object> paramMap) throws Exception;
    void upsertTcClaUserInfo(Map<String, Object> paramMap) throws Exception;
    void insertUserBulk(List<Map<String, Object>> paramMap) throws Exception;
    void insertStdtRegInfoBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertTcClaMbInfoBulk(List<Map<String, Object>> paramMap) throws Exception;
    void insertShopSkinHistBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopGameHistBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopProfileHistBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopSkinBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopGameBulk(List<Map<String, Object>>  paramMap) throws Exception;
    void insertShopProfileBulk(List<Map<String, Object>>  paramMap) throws Exception;

    List<Map<String, Object>> lectureList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> getUserInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getTcClaInfo(Map<String, Object> paramMap) throws Exception;

    String getPrevClaId(Map<String, Object> paramData) throws Exception;

    String getRegularClaExistsYn(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> getStduentGender(Map<String, Object> paramData);

    Map<String, Object> getSubjectInfo(Map<String, Object> paramData);

}
