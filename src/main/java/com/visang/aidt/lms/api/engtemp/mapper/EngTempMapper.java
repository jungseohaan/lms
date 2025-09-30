package com.visang.aidt.lms.api.engtemp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

@Mapper
public interface EngTempMapper {

    void updateLesnRsc(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> selectStdDtaEngTempResultInfoDate(int engTempResultId) throws Exception;
    int selectDtaEngTempResultId(Map<String, Object> paramMap) throws Exception;
    void deleteDtaQuestion(@Param("dtaEngTempResultId") int dtaEngTempResultId) throws Exception;
    List<Map<String, Object>> selectLesnRscQuestion(@Param("dtaEngTempResultId") int dtaEngTempResultId) throws Exception;
    void insertLesnRscQuestion(List<Map<String, Object>> list) throws Exception;
    void updateLesnRscEnd(Map<String, Object> paramMap) throws Exception;
    void updateLesnRscResultDetail(Map<String, Object> paramMap) throws Exception;
    int updateLesnRscDdln(Map<String, Object> paramMap) throws Exception;
    int updateLesnRscRsltRlsAt(Map<String, Object> paramMap) throws Exception;
    int getLesnRscUserTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getLesnRscNotUdstdCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getLesnRscUserAnswer(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getLesnRscSubmitCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getLesnRscUserQuesInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getLesnRscAnswerList(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectLesnRscIsStudy(Map<String, Object> paramMap) throws Exception;

    void insertAssessment(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> selectEvlEngTempResultInfoDate(Map<String, Object> paramMap) throws Exception;
    void updateAssessment(Map<String, Object> paramMap) throws Exception;
    int selectAssessmentExists(Map<String, Object> paramMap) throws Exception;
    void insertAssessmentResultDetail(Map<String, Object> paramMap) throws Exception;
    int getAssessmentUserTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getAssessmentNotUdstdCnt(Map<String, Object> paramMap) throws Exception;
    int updateAssessmentRsltRlsAt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getAssessmentUserAnswer(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getAssessmentSubmitCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getAssessmentUserQuesInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getAssessmentAnswerList(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectAssessmentIsStudy(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectAssessmentStdtList(@Param("resultDetailId") Integer resultDetailId) throws Exception;

    void insertHomework(Map<String, Object> paramMap) throws Exception;
    void updateHomeworkStart(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> selectTaskEngTempResultInfoDate(Map<String, Object> paramMap) throws Exception;
    void updateHomework(Map<String, Object> paramMap) throws Exception;
    void updateHomeworkResultDetail(Map<String, Object> paramMap) throws Exception;
    int selectHomeworkExists(Map<String, Object> paramMap) throws Exception;
    void deleteHomeworkQuestion(@Param("taskEngTempResultId") int taskEngTempResultId) throws Exception;
    void insertHomeworkResultDetail(List<Map<String, Object>> list) throws Exception;
    List<Map<String, Object>> selectHomeworkQuestion(@Param("taskEngTempResultId") int taskEngTempResultId) throws Exception;
    int updateHomeworkDdln(Map<String, Object> paramMap) throws Exception;
    int updateHomeworkRsltRlsAt(Map<String, Object> paramMap) throws Exception;
    int getHomeworkUserTotalCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getHomeworkNotUdstdCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getHomeworkUserAnswer(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getHomeworkSubmitCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getHomeworkUserQuesInfo(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getHomeworkAnswerList(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectHomeworkIsStudy(Map<String, Object> paramMap) throws Exception;

    int insertSelfLrn(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectHomeworkStdtList(int resultDetailId) throws Exception;
    Map<String, Object> selectSlfStdEngTempResultInfoDate(Map<String, Object> paramMap) throws Exception;
    void updateSelfLrn(Map<String, Object> paramMap) throws Exception;
    void insertSelfLrnResultDetail(Map<String, Object> paramMap) throws Exception;
    int updateSelfLrnDdln(Map<String, Object> paramMap) throws Exception;
    int updateSelfLrnRsltRlsAt(Map<String, Object> paramMap) throws Exception;
    int updateSelfLrnResultDetail(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getSelfLrnNotUdstdCnt(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> getSelfLrnUserAnswer(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectSelfLrnIsStudy(Map<String, Object> paramMap) throws Exception;

    void insertLesnRsc(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectEngtempInfoByArticleId(@Param("articleId") String articleId, @Param("subId") Integer subId) throws Exception;
    String selectEngtempExistsYn(@Param("resultDetailId") Integer resultDetailId, @Param("engTempId") Integer engTempId) throws Exception;
    List<Map<String, Object>> selectEngtempAtivityList(@Param("resultDetailId") Integer resultDetailId, @Param("engTempId") Integer engTempId) throws Exception;
    List<Map<String, Object>> selectStdtList(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> selectStartEndDate(Map<String, Object> paramMap) throws Exception;
}
