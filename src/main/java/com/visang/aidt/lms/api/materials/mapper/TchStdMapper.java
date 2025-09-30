package com.visang.aidt.lms.api.materials.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchStdMapper {
    List<Map> findTchStdList(PagingParam<?> paramData) throws Exception;

    int removeTchStdDel_stdDtaResultDetail(Map<String, Object> paramData) throws Exception;
    int removeTchStdDel_stdDtaResultInfo(Map<String, Object> paramData) throws Exception;
    int removeTchStdDel_stdDtaInfo(Map<String, Object> paramData) throws Exception;

    int createTchStd(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchStdReadInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStdDtaInfoById(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findstdDtaResultDetailByStdId(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findstdDtaResultDetailByTabId(Map<String, Object> paramData) throws Exception;

    int modifyTchStdSave_stdDtaInfo(Map<String, Object> paramData) throws Exception;
    int modifyTchStdSave_stdDtaInfo_tabId(Map<String, Object> paramData) throws Exception;

    int createTchStdSave_tabInfo(Map<String, Object> paramData) throws Exception;

    int createTchStdSaveSets(Map<String, Object> paramData) throws Exception;
    int modifyTchStdSaveBbsSetId(Map<String, Object> studentInfo) throws Exception;

    int removeTchStdSaveSDRD(Map<String, Object> studentInfo) throws Exception;
    int removeTchStdSaveSDRI(Map<String, Object> studentInfo) throws Exception;
    int createTchStdSaveSDRI(Map<String, Object> studentInfo) throws Exception;
    int createTchStdSaveSDRD(Map<String, Object> studentInfo) throws Exception;
    int modifyTchStdSaveEEN(Map<String, Object> paramData) throws Exception;

    Map findTchStdLastPagYN(Map<String, Object> paramData) throws Exception;

    int createTchStdLastPageSave(Map<String, Object> paramData) throws Exception;

    int modifyTchStdLastPageSave(Map<String, Object> paramData) throws Exception;

    List<Map> findTchStdLastPageCall(Map<String, Object> paramData) throws Exception;
    int modifyTchStdSaveCrcul(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findEakAtCnt(Map<String, Object> paramData) throws Exception;

    int removeTchStdDel_tabInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTabInfo(Map<String, Object> paramData) throws Exception;

    int modifyTcCurriculum(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findSubMitCnt(Map<String, Object> paramData) throws Exception;

    int modifyStdDtaInfoSetsId(Map<String, Object> paramData) throws Exception;

    int modifyTabInfoSetsId(Map<String, Object> paramData) throws Exception;

    List<Map> findSlfPerEvlSetInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTabInfoById(Map<String, Object> paramData) throws Exception;

    int removeTchTabStdSaveSDRD(Map<String, Object> studentInfo) throws Exception;

    int removeTchTabStdSaveSDRI(Map<String, Object> studentInfo) throws Exception;

    int createTchTabStdSaveSDRI(Map<String, Object> studentInfo) throws Exception;

    int createTchTabStdSaveSDRD(Map<String, Object> studentInfo) throws Exception;

    int modifyTchStdSave_tabInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStdDtaInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findSetsName(Map<String, Object> paramData) throws Exception;

    int createTchStdSaveSets2(Map<String, Object> paramData) throws Exception;

    //수업 당시 해당 클래스 전체 학생 목록, 기존 데이터 삭제
    int deleteStdEnrollment(Map<String, Object> paramData) throws Exception;

    //수업 당시 해당 클래스 전체 학생 목록 저장
    int createStdEnrollment(Map<String, Object> paramData) throws Exception;

    // 수업시작시간 기록 전 , 비정상 데이터 삭제
    int deleteNoDataStdRecodeInfo(Map<String, Object> paramData) throws Exception;

    // 수업시작시간 기록
    int createStdRecodeInfo(Map<String, Object> paramData) throws Exception;

    // 수업종료시간 기록
    int modifyStdRecodeInfoForEndDt(Map<String, Object> paramData) throws Exception;

    int modifyTabSeqExposAt(Map<String, Object> studentInfo) throws Exception;

    int createExtLearnCnts(Map<String, Object> paramData) throws Exception;

    int removeTchExtLearnCnts(Map<String, Object> paramData) throws Exception;

    int createTchStdCntsMap(Map<String, Object> paramData) throws Exception;

    int removeTchStdCntsMap(Map<String, Object> paramData) throws Exception;

    int modifyTchStdTabNm(Map<String, Object> studentInfo) throws Exception;

    int createStdRecodeInfo_end(Map<String, Object> paramData) throws Exception;
    int modifyStdRecodeInfoForEndDt_end(Map<String, Object> paramData) throws Exception;
}

