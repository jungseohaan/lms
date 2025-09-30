package com.visang.aidt.lms.api.mathvillage.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MathVillageMapper {
    List<Map<String, Object>> selectTchCompletedList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStdByStdtId(Map<String, Object> paramData) throws Exception;

    int insertStep(Map<String, Object> paramData) throws Exception;

    int updateStep(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectActvImage(Map<String, Object> paramData) throws Exception;

    int insertReportImage(Map<String, Object> paramData) throws Exception;

    Object selectResultList(Map<String, Object> paramData) throws Exception;

    int insertReportQitem(Map<String, Object> paramData) throws Exception;
}
