package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PortalMapper {

    List<Map<String, Object>> findSchoolList(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> findGradeList(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> findClassList(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getTcTextbookInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getTcClaInfo(Map<String, Object> paramMap) throws Exception;

    int insertTcTextbook(Map<String, Object> paramMap) throws Exception;

    List<Map<String, Object>> findLcmsTextbookList(Map<String, Object> paramMap) throws Exception;

    int insertTcCurriculum(Map<String, Object> paramMap) throws Exception;

    int insertTabInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getStdtRegInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getStTextbookInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getLcmsTextbookInfo(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getTcClaUserInfo(Map<String, Object> paramMap) throws Exception;

}
