package com.visang.aidt.lms.api.homework.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskStatusChangeMapper {

    // 과제 상태 진행중 변경
    int updateTaskStatusChangeToInProgress(List<String> taskIds) throws Exception;

    // 과제 상태 완료로 바뀔 교사들에게 알람 등록
    List<Map> findTaskNtcnToTchList() throws Exception;

    // 과제 상태 완료로 변경
    int updateTaskStatusChangeToComplete() throws Exception;

    // 과제 결과 정보의 제출 여부가 미제출자 목록 조회
    List<Map> findTaskResultInfoNotSubmitted() throws Exception;

    String findCodeIfNotNull(String evlIemId) throws Exception;

    void updateTaskResultDetailFinalProcess(String id) throws Exception;

    void updateTaskResultFinalProcesss(String id) throws Exception;

    void updateTaskInfoFinalProcess() throws Exception;

    void bulkUpdateTaskResultDetailFinalProcess(List<String> taskIds) throws Exception;

    void bulkUpdateTaskResultFinalProcess(List<String> taskResultIds) throws Exception;

    List<Map<String, Object>> findTaskSubmAtN() throws Exception;
    void modifyStntTaskSubmitResultDetail(List<Map<String, Object>> list) throws Exception;
    void modifyStntTaskSubmitResultInfo(List<Map<String, Object>> list) throws Exception;
    void createRwdEarnHist(List<Map<String, Object>> list) throws Exception;
    List<Map<String, Object>> findRwdEarnInfo(List<Map<String, Object>> list) throws Exception;
    void createRwdEarnInfo(List<Integer> list) throws Exception;
    void modifyRwdEarnInfo(List<Integer> list) throws Exception;
    void createNtcnInfo(List<Map<String, Object>> list) throws Exception;

    List<Map<String, Object>> findTaskAutoRptList() throws Exception;
    int updateTaskResultStatusChangeToComplete() throws Exception;

    List<String> selectBulkTaskMqTarget();
}
