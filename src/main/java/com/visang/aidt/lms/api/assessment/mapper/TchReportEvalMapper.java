package com.visang.aidt.lms.api.assessment.mapper;


import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchReportEvalMapper {
    List<Map> findReportEvalList(PagingParam<?> pagingParam) throws Exception;

    List<Map> findReportEvalResultDetailList_result(Map<String, Object> paramData) throws Exception;
    List<Map> findReportEvalResultDetailList_mdul(Map<String, Object> paramData) throws Exception;
    List<Map> findReportEvalResultDetailList_stnt(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportEvalResultDetailList_eval(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportEvalResultDetailMdul_info(Map<String, Object> paramData) throws Exception;
    List<Map> findReportEvalResultDetailMdul_image(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportEvalResultDetailMdul_mdul(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportEvalResultDetailMdul_class(Map<String, Object> paramData) throws Exception;
    List<String> findReportEvalResultDetailMdul_class_answers(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findReportEvalResultDetailMdul_coment(Map<String, Object> paramData) throws Exception;
    List<Map> findReportErrataInfoList(Map<String, Object> paramData) throws Exception;

    List<Map> findReportEvalResultDetailStnt_result(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findReportEvalResultDetailStnt_stnt(Map<String, Object> paramData) throws Exception;
    int modifyReportEvalResultDetailStntFdbMod(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportFindStnt(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportEvalList(PagingParam<?> pagingParam) throws Exception;
    Map<String, Object> findStntSrchReportEvalResultDetail_errata(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSrchReportEvalResultDetail_image(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSrchReportEvalResultDetail_mdul(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSrchReportEvalResultDetail_analysis(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSrchReportEvalResultDetail_coment(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSrchReportEvalResultDetailMdul_info(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSrchReportEvalResultDetailMdul_eval(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntSrchReportEvalResultDetail(Map<String, Object> paramData) throws Exception;



    Map<String, Object> findReportEvalResultForStudent(Map<String, Object> paramData) throws Exception;

    int modifyReportEvalResultScore(Map<String, Object> modiMap) throws Exception;

    Object findReportEvalResultScoreSum(int evlResultId) throws Exception;

    int modifyReportEvalResultScoreSum_resultInfo(Map<String, Object> modiMap) throws Exception;

    int modifyReportEvalResultScoreSum_info(Map<String, Object> modiMap) throws Exception;

    Object findReportEvalResultHeader(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findReportEvalResultInsiteCalculate(Map<String, Object> paramData) throws Exception;

    List<Map> findReportEvalResultInsite(PagingParam<?> paramData) throws Exception;


    List<Map> findReportEvalResultSummary(Map<String, Object> paramData) throws Exception;

    float findReportMdulTotScr(Map<String, Object> paramData) throws Exception;

    Object findStntSrchReportEvalResultHeader(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportEvalResultSummary(PagingParam<?> pagingParam) throws Exception;

    List<Map> findStntSrchReportEvalResultInsite(PagingParam<?> pagingParam) throws Exception;

    int modifyReportEvalOpen(Map<String, Object> paramData) throws Exception;


    Map<String, Object> findReportEvalOpenData(Map<String, Object> paramData) throws Exception;


    void modifyReportEvalSubmAt(int evlResultId);

    Map findEvlScrMdInfo(Map<String, Object> modiMap) throws Exception;

    int insertEvlScrMdInfo(Map<String, Object> modiMap) throws Exception;

    int updateEvlScrMdInfo(Map<String, Object> modiMap) throws Exception;


    int modifyReportEvalResultApplScore(Map<String, Object> idParam) throws Exception;

    Map<String, String> findEvlResultIdForApplScore(Map<String, Object> paramData) throws Exception;

    int modifyReportEvalResultScoreSumByResultIds(Map<String, Object> idParam) throws Exception;

    int updateTchReportEvlReviewSave(Map<String, Object> paramData) throws Exception;

    List<Map> findSendNtcnEvlList(Map<String, Object> paramData) throws Exception;

    List<Map> findSendNtcnEvlListAuto(Map<String, Object> paramData) throws Exception;

    List<Map> findSendNtcnEvlListPassivity(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findReportEvalResultIndList_eval(Map<String, Object> paramData) throws Exception;

    List<Map> findReportEvalResultIndList_stnt(Map<String, Object> paramData) throws Exception;

    List<Map> findStntList(Map<String, Object> paramData) throws Exception;

    Map findTchReportEvalGeneralReviewInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchReportEvalGeneralReviewAiEvlWord(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchReportEvalResultDetail(Map<String, Object> paramData) throws Exception;

    int modifyReportEvalApplScore(Map<String, Object> paramData);

    List<Map> findStntTopFiveList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntGuideNeededList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findAvgCorrectRateInfo(Map<String, Object> paramData) throws Exception;

    int modifyReportEvalResultScore_rpt_y(Map<String, Object> modiMap) throws Exception;
    int modifyReportEvalResultScoreSum_resultInfo_rpt_y(Map<String, Object> modiMap) throws Exception;

    int modifyReportEvalResultScore_submAt(Map<String, Object> modiMap) throws Exception;

    String findClaIdInEvalInfo(String evlId) throws Exception;
}
