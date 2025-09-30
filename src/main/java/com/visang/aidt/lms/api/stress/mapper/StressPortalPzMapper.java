package com.visang.aidt.lms.api.stress.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StressPortalPzMapper {
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
}
