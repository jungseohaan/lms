package com.visang.aidt.lms.api.lecture.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchLectureReportEngMapper {

    Map<String, Object> findReportLectureResultDetailMdulList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportStntDtaErrataInfoList(Map<String, Object> paramData) throws Exception;

//    Map<String, Object> getTchLectureReportStdDtaInfo_mdulDtaInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> getTchLectureReportStdDtaInfo_mdulInfo(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> getTchLectureReportStdDtaInfo_subMdulInfo(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> getTchLectureReportStdDtaInfo_classAnalysys(Map<String, Object> paramData) throws Exception;

    List<String> findTchLectureReportMdulDtaInfo_answers(Map<String, Object> innerParam) throws Exception;

    List<Map> findTchLectureReportMdulDtaInfo_stntInfos(Map<String, Object> innerParam) throws Exception;

    List<Map> findTchLectureReportMdulDtaInfo_errataInfos(Map<String, Object> innerParam) throws Exception;
}
