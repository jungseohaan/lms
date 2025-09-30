package com.visang.aidt.lms.api.lecture.mapper;


import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchLectureReportMapper {

    List<Map> findTchLectureReportTabInfoList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportTabInfoListForElementMath(Map<String, Object> paramData) throws Exception;

    Map getTchLectureReportStdDtaInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportMdulDtaInfoList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportStntDtaErrataInfoList(Map<String, Object> paramData) throws Exception;

    List<Map> findReportLectureResultDetailMdulList(Map<String, Object> paramData) throws Exception;

    Map findTchLectureReportMdulDtaInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findArticleCommentInfo(Map<String, List<String>> idParam) throws Exception;

    Map<String, Object> getTchLectureReportStntInfo(Map<String, Object> paramData) throws Exception;

    List<Map> getTchLectureReportStntInfoOther(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportErrataInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportActList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportActResultList(Map<String, Object> idParam) throws Exception;

    int modifyTchReportLectureResultFdbMod(Map<String, Object> paramData) throws Exception;

    Map getModifyTchReportLectureResultErrataMod(Map<String, Object> paramData) throws Exception;

    int modifyTchReportLectureResultErrataMod(Map<String, Object> paramData) throws Exception;

    int getTchReportLectureResultMrkTy(Map<String, Object> paramData) throws Exception;

    int updateTchReportLectureReviewSave(Map<String, Object> paramData) throws Exception;

    List<Map> findSitesetDashreportExposList(Map<String, Object> paramData) throws Exception;

    Map findSitesetDashreportExposYN(Map<String, Object> paramData) throws Exception;

    int saveSitesetDashreportExpos(Map<String, Object> paramData) throws Exception;

    int modifySitesetDashreportExpos(Map<String, Object> paramData) throws Exception;

    List<String> findTchLectureReportMdulDtaInfo_answers(Map<String, Object> paramData) throws Exception;

    Map findTchReportStdDtaGeneralReviewInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchReportLectureGeneralReviewAiEvlWord(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchReportLectureResultDetail(Map<String, Object> paramData) throws Exception;

    List<Map> findTchLectureReportMdulDtaInfoList_setsIdNull(Map<String, Object> paramData) throws Exception;
    List<Map> findTchLectureReportStntDtaErrataInfoList_setsIdNull(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findLectureDetailInfo(Map<String, Object> paramData);

    int getWrongNoteCount(Map<String, Object> paramData);

    int createWonAswNote(Map<String, Object> innerParam);

    Map<String, Object> tcCurriculumTextTabId(Map<String, Object> paramData) throws Exception;
    Map<String, Object> tcCurriculumTextElse(Map<String, Object> paramData) throws Exception;

    List<Map> findTchReportLectureResultMdul_setsIdNull(PagingParam<?> paramData) throws Exception;

    List<Map> findTchReportLectureResultMdul(PagingParam<?> paramData) throws Exception;
    int modifyTchReportLectureActTchRptChkAt(Map<String, Object> paramData) throws Exception;
    int modifyTchReportLectureStdDtaTchRptChkAt(Map<String, Object> paramData) throws Exception;
    List<Map> findTchReportLectureActWyList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchReportLectureResultAct(Map<String, Object> paramData) throws Exception;
    List<Map> findStntReportLectureResultAct(Map<String, Object> paramData) throws Exception;
}
