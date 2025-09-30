package com.visang.aidt.lms.api.materials.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface TchMdulQstnMapper {
    Map<String, Object> findTchMdulQstnSDRI(Map<String, Object> paramData) throws Exception;
    // /tch/mdul/qstn/answ
    List<Map> findTchMdulQstnAnswResultDetailInfo(PagingParam<?> paramData) throws Exception;
    List<Map> findTchMdulQstnAnswStntRate(Map<String, Object> paramData) throws Exception;

    List<Map> findTchMdulQstnAnswResultInfo(List<LinkedHashMap<Object, Object>> paramData, Map<String, Object> paramMap) throws Exception;
    List<Map> findTchMdulQstnAnswSelfStd(List<LinkedHashMap<Object, Object>> paramData, Map<String, Object> paramMap) throws Exception;

    // /tch/mdul/qstn/reset
    int createTchMdulQstnResetSDRHist(Map<String, Object> paramData) throws Exception;
    int modifyTchMdulQstnResetSDRD(Map<String, Object> paramData) throws Exception;
    int modifyTchMdulQstnResetSDRI(Map<String, Object> paramData) throws Exception;
    int modifyTchMdulQstnResetSDI(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/qstn/exclnt
    int modifyTchMdulQstnExclnt(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/qstn/exclnt/status
    int modifyTchMdulQstnExclntCancel(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/qstn/fdb
    int modifyTchMdulQstnFdb(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/qstn/fdb/share
    int modifyTchMdulQstnFdbShare(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/qstn/status
    List<Map> findTchMdulQstnStatusResultInfoList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchMdulQstnStatusStntInfoList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchMdulQstnStatusStntErrataList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchMdulQstnIndi(Map<String, Object> paramData) throws Exception;

    List<Map> findLectureAutoQstnExtr(Map<Object, Object> procParamData) throws Exception;

    List<Map> findRwdInParam(Map<String, Object> paramData) throws Exception;

    int modifyTchMdulQstnResetSrcDetailId(Map<String, Object> paramData) throws Exception;
    
    // 기존 문항지에서 파생된(다른문제 풀기) 삭제 (std_dta_result_detail.src_detail_id)
    int removeTchMdulQstnResetSrcDetailId(Map<String, Object> paramData) throws Exception;

    String getBrandId(Map<String, Object> data) throws Exception;

    int modifyTchMvResetSDRIINFO(Map<String, Object> paramData) throws Exception;
    int modifyTchMvResetSDRIKWG(Map<String, Object> paramData) throws Exception;
    int modifyTchMvResetSDRIDEATIL(Map<String, Object> paramData) throws Exception;
    int modifyTchMvResetSDRISRCINFO(Map<String, Object> paramData) throws Exception;

    int modifyTchMvResetSDRISRCINFOMath(Map<String, Object> paramData) throws Exception;

    // 오답노트 초기화
    int initWanCnt (Map<String, Object> paramData) throws Exception;

    /**
     * 개인별 맞춤 학습 문제 목록 조회 (fullCount 포함)
     */
    List<Map> findCustomLearningProblemsWithCount(PagingParam<?> pagingParam);

    /**
     * 개인별 맞춤 학습 전체 문제 수 계산
     */
    long countCustomLearningProblems(Map<String, Object> paramData);

    /**
     * 개인별 맞춤 학습 답안 정보 조회
     */
    List<Map> findCustomLearningAnswerInfo(@Param("paramData") List<LinkedHashMap<Object, Object>> problemList, @Param("paramMap") Map<String, Object> paramData);

    /**
     * 개인별 맞춤 학습 자습 정보 조회
     */
    List<Map> findCustomLearningSelfStd(@Param("paramData") List<LinkedHashMap<Object, Object>> problemList, @Param("paramMap") Map<String, Object> paramData);

    /**
     * 개인별 맞춤 학습 학생별 정답률/제출률 조회
     */
    Map<String, Object> findCustomLearningStudentRate(Map<String, Object> paramData);
}
