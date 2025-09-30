package com.visang.aidt.lms.api.assessment.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.assessment.mapper
 * fileName : tchEvalMapper
 * USER : hs84
 * date : 2024-01-17
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-17         hs84          최초 생성
 */
@Mapper
public interface TchEvalMapper {
    // /tch/eval/preview
    Map<String, Object> findTchEvalPreviewEvalInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalPreviewEvalIemInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalPreviewStudentList(Map<String, Object> paramData) throws Exception;

    // /tch/eval/status
    List<Map> findTchEvalStatusStudentInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalStatusEvalResultDet(Map<String, Object> paramData) throws Exception;

    // /tch/eval/result/status
    Map<String, Object> findTchEvalResultStatusEvalInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalResultStatusEvalIemInfo(PagingParam<?> paramData) throws Exception;
    List<Map> findTchEvalResultStatusEvalResultDet(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalResultStatusSubMitAnwStnt(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalResultStatusAnwStudent(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalResultStatusEvalAdiSecStnt(Map<String, Object> paramData) throws Exception;

    Map<String, Object> checkEvlDataByEvlId(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findSetsIdByEvlId(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> findSetSummaryBySetsId(Map<String, Object> paramData) throws Exception;
    // /tch/eval/start
    int modifyTchEvalStartEvalInfo(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalStartEvalResultInfo(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalStartEvalResultDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchEvalStartEvalInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchEvalStartPrgEvalInfo(Map<String, Object> paramData) throws Exception;

    // /tch/eval/end
    int modifyTchEvalEndEvalInfo(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalEndEvalResultInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchEvalEndEvalInfo(Map<String, Object> paramData) throws Exception;

    // /tch/eval/reset
    int modifyTchEvalResetEvalInfoY(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalResetEvalInfoN(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalResetEvalResultInfo(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalResetEvalResultDetail(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchEvalInfoEvalInfo(Map<String, Object> paramData) throws Exception;

    // /tch/eval/list
    List<Map> findTchEvalListEvalList(PagingParam<?> paramData) throws Exception;
    List<Map> findTchEvalListEvalListTmpr(PagingParam<?> paramData) throws Exception;

    // /tch/eval/delete
    int deleteTchEvalDeleteEvalResultDetail(Map<String, Object> paramData) throws Exception;
    int deleteTchEvalDeleteEvalResultInfo(Map<String, Object> paramData) throws Exception;
    int deleteTchEvalDeleteEvalIemInfo(Map<String, Object> paramData) throws Exception;
    int deleteTchEvalDeleteEvalTrnTrget(Map<String, Object> paramData) throws Exception;
    int deleteTchEvalDeleteEvalInfo(Map<String, Object> paramData) throws Exception;

    // /tch/eval/init
    int modifyTchEvalInitEvalInfoY(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalInitEvalInfoN(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalInitEvalResultInfo(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalInitEvalResultDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchEvalInit(Map<String, Object> paramData) throws Exception;

    // /tch/eval/read-info
    Map<String, Object> findTchEvalReadInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalReadInfoStudentInfo(LinkedHashMap<Object, Object> studentInfoMap) throws Exception;

    // /tch/eval/save
    int modifyTchEvalSave(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalSaveEEN(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findEvlInfo(Map<String, Object> paramData) throws Exception;
    int findEvlResultInfoCount(Map<String, Object> paramData) throws Exception;
    int createTchEvalSaveSets(Map<String, Object> paramData) throws Exception;
    int createTchEvalSaveSAM(Map<String, Object> setsMap) throws Exception;
    int createTchEvalSaveSKM(Map<String, Object> setsMap) throws Exception;
    int createTchEvalSaveSMM(Map<String, Object> setsMap) throws Exception;
    int removeTchEvalSaveETT(Map<String, Object> studentInfo) throws Exception;
    int removeTchEvalSaveERD(Map<String, Object> studentInfo) throws Exception;
    int removeTchEvalSaveERI(Map<String, Object> studentInfo) throws Exception;
    int createTchEvalSaveETT(Map<String, Object> studentInfo) throws Exception;
    int createTchEvalSaveERI(Map<String, Object> studentInfo) throws Exception;
    int createTchEvalSaveERD(Map<String, Object> studentInfo) throws Exception;

    int modifyTchEvalSaveBbsSetId(Map<String, Object> studentInfo) throws Exception;

    // /tch/eval/time
    int modifyEvalTimeAdd(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectEvlInfoDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectEvlResultStat(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectEvlArticleList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectEvlResultUserInfoList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectEvlResultDetailList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectEvlResultDetailList2(Map<String, Object> paramData) throws Exception;

    // /tch/eval/create
    int createTchEvalCreate(Map<String, Object> paramData) throws Exception;
    int createTchEvalIemCreate(Map<String, Object> paramData) throws Exception;
    int deleteTchEvalItems(Map<String, Object> paramData) throws Exception;

    int modifyEvalSubmAtERD(Map<String, Object> paramData) throws Exception;
    int modifyEvalSubmAtERI(Map<String, Object> paramData) throws Exception;

    LinkedHashMap getEvlInfoById(Map<String, Object> paramData) throws Exception;

    int cloneEvlInfo(Map<String, Object> paramData) throws Exception;
    int cloneEvlResultInfo(Map<String, Object> paramData) throws Exception;

    int copyEvlIemInfoByEvlId(Map tmpParamMap) throws Exception;

    int copyEvlResultDetailByEvlId(Map tmpParamMap) throws Exception;

    List<LinkedHashMap> findEvlResultInfoListByEvlId(Map<String, Object> paramData) throws Exception;

    List<Map> findEvalAutoQstnExtr(Map<Object, Object> procParamData) throws Exception;

    int increaseModuleUseCnt(Map<String, Object> paramData) throws Exception;

    int modifyEvalStatusToInProgress(Map<String, Object> paramData) throws Exception;

    int createTchEvalSaveSummary(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalSaveSetsId(Map<String, Object> paramData) throws Exception;

    int removeTchEvalSaveSets(Map<String, Object> paramData) throws Exception;
    int removeTchEvalSaveSAM(Map<String, Object> paramData) throws Exception;
    int removeTchEvalSaveSKM(Map<String, Object> paramData) throws Exception;
    int removeTchEvalSaveSMM(Map<String, Object> paramData) throws Exception;
    int removeTchEvalSaveSummary(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchEvalTaskInfo(Map<String, Object> paramData) throws Exception;

    int findSetSummaryForEval(Map<String, Object> paramData) throws Exception;

    int createTchEvalCreateForTextbk_evlInfo(Map<String, Object> paramData) throws Exception;
    int createTchEvalCreateForTextbk_evlIemInfo(Map<String, Object> paramData) throws Exception;
    int createTchEvalCreateForTextbk_evlResultInfo(Map<String, Object> paramData) throws Exception;
    int createTchEvalCreateForTextbk_evlResultDetail(Map<String, Object> paramData) throws Exception;

    int modifyTchEvalPeriodChange(Map<String, Object> paramData) throws Exception;
    int modifyTchEvalResultInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchEvalInfoBySet(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchTaskInfoBySet(Map<String, Object> paramData) throws Exception;

    List<Map> findTchEvalStatusList_currentEvalList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchEvalStatusList_reqGradeEvalListItem(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findEvalSubmStatus_ei(Map<String, Object> paramData) throws Exception;
    List<Map> findEvalSubmStatus_eri(Map<String, Object> paramData) throws Exception;

    List<Map> findTchEvlStntRateList(Map<String, Object> paramData) throws Exception;

    int createEvaluationHistoryRecord(Map<String, Object> paramData) throws Exception;

    int deleteOriginalAiPrscrEvlToTaskInfo(Map<String, Object> paramData) throws Exception;

    int deleteOriginalAiPrscrEvlToTRI(Map<String, Object> paramData) throws Exception;

    int deleteOriginalAiPrscrEvlToTRD(Map<String, Object> paramData) throws Exception;

    int insertSetHist(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findSetsHist(Map<String, Object> paramData) throws Exception;

    void updateEvalAt(Map<String, Object> paramData) throws Exception;

    int updateEvalInfo(Map<String,Object> paramData) throws Exception;

    List<Map<String, Object>> findExistingEvalResultInfo(Map<String, Object> paramData) throws Exception;

    List<Long> findStudyMap1MetaIds(List<Long> metaIds) throws Exception;
}
