package com.visang.aidt.lms.api.report.mapper;

import com.visang.aidt.lms.api.report.dto.HomewkReportListReqDto;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface HomewkReportMapper {

    int modifyTeacherTaskReportChkAt(Map<String, Object> paramData);

    List<Map> selectTaskEncouragementTargets (Map<String, Object> paramData) throws Exception;

    int updateTchReportHomewkReviewSave(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findReportHomewkResultList_stntTaskErrataInfList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findReportHomewkResultList_mdulList(PagingParam<?> paramData) throws Exception;

    List<Map> findTaskReportSummary(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findAvgCorrectRateInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findStntTaskReportSummary(Map<String, Object> paramData) throws Exception;

    List<Map> findUnsubmittedStudentsByTaskId (Map<String, Object> paramData) throws Exception;

    List<Map> findHomewkListForTeacher(PagingParam<?> paramData) throws Exception;
    List<Map> findHomewkListForStudent(PagingParam<?> paramData) throws Exception;

    Map<String, Object> findLatestTaskForTeacher(HomewkReportListReqDto paramData) throws Exception;
    Map<String, Object> findLatestTaskForStudent(HomewkReportListReqDto paramData) throws Exception;

    List<Map> findTaskkProgressCountForTeacher(HomewkReportListReqDto paramData) throws Exception;
    List<Map> findTaskkProgressCountForStudent(HomewkReportListReqDto paramData) throws Exception;

    List<Map<String, Object>> findScoringClassResultIemInfoHeader(PagingParam<?> paramData) throws Exception;
}