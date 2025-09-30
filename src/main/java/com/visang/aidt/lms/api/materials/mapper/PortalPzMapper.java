package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PortalPzMapper {
    Map<String, Object> getClassInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getClassInfoByClassCode(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getClassInfoByLectureCode(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getPtnInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getTcTextbookInfo(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> findLcmsTextbookList(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getLcmsTextbookInfo(Map<String, Object> paramMap) throws Exception;

    int insertTcTextbook(Map<String, Object> paramMap) throws Exception;

    int insertTcCurriculum(Map<String, Object> paramMap) throws Exception;

    int insertTabInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getStdtRegInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getStTextbookInfo(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> findTcTextbookListByGroupKey(Map<String, Object> paramMap) throws Exception;

    /*[open] 교과서 version 업에 따른 교사 교과서 갱신 관련*/

    void insertTcTextbookVersion(Map<String, Object> data);
    int selectCurrentTextbookVersion(Map<String, Object> paramMap);
    Map<String, Object> selectTextbookIndex(Map<String, Object> paramMap);

    List<Map<String, Object>> selectTextbookCurriculum(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectTcCurriculum(Map<String, Object> paramMap) throws Exception;
    void insertTcCurriculumFromVersionCheck(Map<String, Object> data);
    int updateTcCurriculum(Map<String, Object> data);

    List<Map<String, Object>> selectTextbookTab(Map<String, Object> paramMap) throws Exception;
    List<Map<String, Object>> selectTcTabInfo(Map<String, Object> paramMap) throws Exception;
    void insertTcTabInfoFromVersionCheck(Map<String, Object> data);

    /*[close] 교과서 version 업에 따른 교사 교과서 갱신 관련*/

    Map<String, Object> getTcClaUserInfo(Map<String, Object> paramMap) throws Exception;
}
