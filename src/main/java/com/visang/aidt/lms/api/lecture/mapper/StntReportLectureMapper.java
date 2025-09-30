package com.visang.aidt.lms.api.lecture.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntReportLectureMapper {

    List<Map> findStntReportLectureDetail_tab(Map<String, Object> paramData) throws Exception;

    Map<String,Object> findStntReportLectureDetail_errata(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_image(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_mdul(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_analysis(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_coment(Map<String, Object> paramData) throws Exception;

    Map<String,Object> findStntReportLectureDetail_info(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_item(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_tabMdul(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_stntDta(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_stntDtaResult(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_act(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_actResult(Map<String, Object> paramData) throws Exception;

    List<Map> findStntReportLectureDetail_stntDtaResultOther(Map<String, Object> innerParam);

    String findStntReportLectureTabName(Map<String, Object> paramData) throws Exception;
}
