package com.visang.aidt.lms.api.homework.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskNtcnSendMapper {

    // 교사에게 보낼 알림 목록(종료일)
    List<Map> findTaskSendNtcnUnsubEndListToTch(Map<String, Object> param) throws Exception;

    // 과제 미제출 학생 목록(종료일)
    List<Map> findTaskSendNtcnUnsubEndListToStnt(Map<String, Object> param) throws Exception;

    // 교사에게 보낼 알림 목록(시작일)
    List<Map> findTaskSendNtcnUnsubStListToTch(Map<String, Object> param) throws Exception;

    // 과제 미제출 학생 목록(시작일)
    List<Map> findTaskSendNtcnUnsubStListToStnt(Map<String, Object> param) throws Exception;

    int insertTaskCreateReportListSendNtcnToTch(Map<String, Object> param) throws Exception;
}
