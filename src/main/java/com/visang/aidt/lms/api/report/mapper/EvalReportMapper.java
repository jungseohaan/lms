package com.visang.aidt.lms.api.report.mapper;

import com.visang.aidt.lms.api.report.dto.EvalReportListReqDto;
import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface EvalReportMapper {

    List<Map> findEvlReportSummary(Map<String, Object> paramData) throws Exception;

    List<Map> findStntEvlReportSummary(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findAvgCorrectRateInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findUnsubmittedStudentsByEvlId(Map<String, Object> paramData) throws Exception;

    List<Map> findReportEvalResultDetailList_stnt(Map<String, Object> paramData) throws Exception;

    List<Map> findEvalList(PagingParam<?> paramData) throws Exception;

    Map<String, Object> findLatestEval(EvalReportListReqDto paramData) throws Exception;

    List<Map> findEvalProgressCount(EvalReportListReqDto paramData) throws Exception;

    List<Map> findScoringClassResultInfo(PagingParam<?> paramData) throws Exception;

    int modifyTeacherEvalReportChkAt(Map<String, Object> paramData);

    List<Map> selectEvalEncouragementTargets(Map<String, Object> paramData) throws Exception;

    int insertStntEncouragementNotification(Map<String, Object> paramData) throws Exception;

    void insertTchEncouragementNotification(Map<String, Object> paramData) throws Exception;

    int updateTchReportEvlReviewSave(Map<String, Object> paramData) throws Exception;

    int selectWrongNoteCreatedAt(Map<String, Object> paramData) throws Exception;

    List<Map> findRecommendedQuestions(PagingParam<?> paramData) throws Exception;

    Integer selectTargetTaskId(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findAutoCreateAiLearningEvlWithDiagnosticAnotherQuestion(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findAutoCreateAiLearningEvlWithDiagnosticAnotherQuestionIfNull(Map<String, Object> paramData) throws Exception;

    int modifyTchEvalReportChkAtOnSubmit(Map<String, Object> paramData) throws Exception;

    Integer selectLastLessonCrculId(Map<String, Object> paramData) throws Exception;
    String selectCrculId(Map<String, Object> paramData) throws Exception;

    int selectTextbookId(Map<String, Object> paramData) throws Exception;

    String selectClaId(Map<String, Object> paramData) throws Exception;
}