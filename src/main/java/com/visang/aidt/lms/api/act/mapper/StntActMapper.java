package com.visang.aidt.lms.api.act.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntActMapper {
    // 최근 활동진행코드
    List<Map> recntActProcList(Map<String, Object> paramData) throws Exception;

    // 개인
    List<Map> findStntActMdulList(Map<String, Object> paramData) throws Exception;

    // 짝꿍
    List<Map> findStntActMdulListForMate(Map<String, Object> paramData) throws Exception;

    // 짝꿍이 나에게 준 피드백
    List<Map> findStntActFeedback(Map<String, Object> paramData) throws Exception;

    // 짝꿍에게 준 나의 피드백
    List<Map> findStntActFeedbackForMate(Map<String, Object> paramData) throws Exception;

    int modifyStntActMdulSubmit(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntActMdul(Map<String, Object> paramData) throws Exception;

    // 짝꿍 답안에 피드백하기
    int createStntActMateFdb(Map<String, Object> paramData) throws Exception;

    // 짝꿍 답안 읽음 처리
    int createStntActMateChkReadSave(Map<String, Object> paramData) throws Exception;

}
