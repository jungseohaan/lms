package com.visang.aidt.lms.api.mq.mapper.bulk;

import com.visang.aidt.lms.api.mq.dto.assessment.*;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AssessmentSubmittedMapper {

    void insertEvlMqTrnLog(@Param("evlId") int evlId);
    void modifyAssessmentScore(@Param("evlId") int evlId);

    /**평가 완료한 학생 목록 조회*/
    List<CompletedAssessments> findCompletedAssessmentsStudents(RealMqReqDto paramData);

    /**학생이 진행한 평가지 목록 조회*/
    List<AssessmentInfo> findAssessmentSheets(Map<String, String> paramData);

    /**형성평가 커리큘럼Id 목록 조회*/
    List<Map<String, Object>> findCurriculumFormativeAssessment(@Param("mamoymId") String mamoymId);

    List<AssessmentDetail> findStdtEvalResultsDetail(@Param("evlResultId") int evlResultId, @Param("evlId") int evlId);
    Map<String, String> getUserInfo(String userId);
    int updateEvlMqTrnAt();

    /**평가지에 해당하는 학교 조회 (초,중,고)*/
    String findcurriSchoolByEvlId(int evlId);

}
