package com.visang.aidt.lms.api.assessment.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchReportMapper {

    Map<String, Object> findTchReportTotStd(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchReportCrrRate(Map<String, Object> paramData) throws Exception;
    List<Map> findTchReportStdTotList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchReportEvlTotList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchReportTaskTotList(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchReportTotalUndstn(Map<String, Object> paramData) throws Exception;

    List<Map> findTchReportExamScopeList(Map<String, Object> paramData) throws Exception;

    // 해당 차시에 속한 article 갯수(교사/학생 공통)
    Map<String, Object> findStdReportForArticleCnt(Map<String, Object> paramData) throws Exception;

    // 마지막 수업일 및 탭이름(교사/학생)
    Map<String, Object> findStdReportForLastClaInfo(Map<String, Object> paramData) throws Exception;

    // 총수업시간(교사/학생)
    Map<String, Object> findStdReportForRecodeInfo(Map<String, Object> paramData) throws Exception;

    // 평균 정답률 (교사/학생)
    Map<String, Object> findStdReportForCrrctInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStdReportForCrrctInfoWithAI(Map<String, Object> paramData) throws Exception;

    // 평균 제출률 (교사)
    Map<String, Object> findStdReportForStdSubmInfo(Map<String, Object> paramData) throws Exception;

    // 평균 제출률 (학생)
    Map<String, Object> findStdReportForStntStdSubmInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findAllNoSubMitAnwAt(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchReportLastActivity(Map<String, Object> paramData);
}
