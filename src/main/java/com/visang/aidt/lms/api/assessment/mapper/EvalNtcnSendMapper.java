package com.visang.aidt.lms.api.assessment.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface EvalNtcnSendMapper {

    // 교사에게 보낼 알림 목록(마감일)
    List<Map> findEvalSendNtcnUnsubEndListToTch(Map<String, Object> param) throws Exception;

    // 과제 미제출 학생 목록(마감일)
    List<Map> findEvalSendNtcnUnsubEndListToStnt(Map<String, Object> param) throws Exception;

    // 교사에게 보낼 알림 목록(시작일)
    List<Map> findEvalSendNtcnUnsubStListToTch(Map<String, Object> param) throws Exception;

    // 과제 미제출 학생 목록(시작일)
    List<Map> findEvalSendNtcnUnsubStListToStnt(Map<String, Object> param) throws Exception;

    // 교사에게 평가 리포트 생성 알림 전송
    int insertEvalCreateReportListSendNtcnToTch(Map<String, Object> param) throws Exception;
}
