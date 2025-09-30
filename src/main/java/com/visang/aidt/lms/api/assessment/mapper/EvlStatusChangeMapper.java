package com.visang.aidt.lms.api.assessment.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface EvlStatusChangeMapper {

    // 평가 상태 진행중 변경
    int updateEvlStatusChangeToInProgress() throws Exception;

    // 평가 상태 완료로 바뀔 교사들에게 알람 등록
    List<Map> findEvlNtcnToTchList() throws Exception;

    // 평가 상태 완료로 변경
    int updateEvlStatusChangeToComplete() throws Exception;

    // 평가 결과 정보의 제출 여부가 미제출자 목록 조회
    List<Map> findEvlResultInfoNotSubmitted() throws Exception;

    String findCodeIfNull(String evlIemId) throws Exception;

    String findCodeIfNotNull(String evlIemId) throws Exception;

    void updateEvlResultDetailFinalProcess(String id) throws Exception;

    void updateEvlResultFinalProcesss(String id) throws Exception;

    void updateEvlInfoFinalProcess() throws Exception;

    String findUpdatedEvlIds()throws Exception;

    // 평가 완료된 평가 ID 조회
    List<String> findEvlIdsToBeCompleted() throws Exception;

    void bulkUpdateEvlResultDetailFinalProcess(List<String> evlResultIds) throws Exception;

    void bulkUpdateEvlResultFinalProcess(List<String> evlResultIds) throws Exception;

    List<Map<String, Object>> findEvlSubmAtN() throws Exception;
    void modifyStntEvalSubmitResultDetail(List<Map<String, Object>> list) throws Exception;
    void modifyStntEvalSubmitResultInfo(List<Map<String, Object>> list) throws Exception;
    void createRwdEarnHist(List<Map<String, Object>> list) throws Exception;
    List<Map<String, Object>> findRwdEarnInfo(List<Map<String, Object>> list) throws Exception;
    void createRwdEarnInfo(List<Integer> list) throws Exception;
    void modifyRwdEarnInfo(List<Integer> list) throws Exception;
    void createNtcnInfo(List<Map<String, Object>> list) throws Exception;

    List<Map<String, Object>> findEvlAutoRptList() throws Exception;
    int updateEvlResultStatusChangeToComplete() throws Exception;

    int updateEvlResultStatusChangeToComplete2() throws Exception;

    void modifyTchEvalReportChkAtOnCompleteList(List<String> params);

    List<Map<String, Object>> selectrCeateAiPrscrEvlToTaskTarget();
}
