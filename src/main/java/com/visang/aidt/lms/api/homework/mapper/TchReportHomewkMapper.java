package com.visang.aidt.lms.api.homework.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.mapper
 * fileName : TchReportHomewkMapper
 * USER : 조승현
 * date : 2024-01-29
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-29         조승현          최초 생성
 */
@Mapper
public interface TchReportHomewkMapper {
    public List<Map> findTchReportHomewkTaskList(PagingParam<?> pagingParam) throws Exception;

    Object findReportHomewkResultList_main(Map<String, Object> paramData) throws Exception;

    Object findReportHomewkResultListStnt_main(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findReportHomewkResultList_mdulList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findReportHomewkResultList_stntMdulList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findReportHomewkResultList_stntList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findReportHomewkResultList_allStntList(Map<String, Object> paramData) throws Exception;

    Object tchReportHomewkResultDetailMdul_taskInfo(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> tchReportHomewkResultDetailMdul_image(Map<String, Object> paramData) throws Exception;

    Object tchReportHomewkResultDetailMdul_mdulInfo(Map<String, Object> paramData) throws Exception;

    Object tchReportHomewkResultDetailMdul_classAnalysisInfo(Map<String, Object> paramData) throws Exception;

    Object tchReportHomewkResultDetailMdul_commentary(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findHomewkResultDetailStnt_stntInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findHomewkResultDetailStnt_stntResultInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findHomewkReportErrataInfoList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchStntSrchReportTaskSummary_main(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findTchStntSrchReportTaskSummary_itemList(Map<String, Object> paramData) throws Exception;


    Map<String, Object>  findTchReportHomewkResultErrataMod_0(Map<String, Object> paramData) throws Exception;
    int insertTchReportHomewkResultErrataMod_0(Map<String, Object> paramData) throws Exception;
    int updateTchReportHomewkResultErrataMod_0(Map<String, Object> paramData) throws Exception;
    int modifyTchReportHomewkResultErrataMod_1(Map<String, Object> paramData) throws Exception;
    int modifyTchReportHomewkResultErrataMod_2(Map<String, Object> paramData) throws Exception;
    int modifyTchReportHomewkResultErrataMod_3(Map<String, Object> paramData) throws Exception;

    int modifyTchReportHomewkResultErrataAppl(Map<String, Object> paramData) throws Exception;

    int modifyTchReportHomewkResultFdbMod(Map<String, Object> paramData) throws Exception;

    int modifyTchReportHomewkResultMod(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findReportHomewkResultSummary(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndResult_errata(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultSummary_stnt(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndResult_stnt(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndResult_result(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findReportHomewkResultIndResult(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findReportHomewkResultList_stntTaskErrataInfList(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndMdul_errata(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndMdul_image(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndMdul_task(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndMdul_mdul(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndMdul_analysis(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndMdul_coment(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndMdul_item(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findReportHomewkResultIndMdul_info(Map<String, Object> paramData) throws Exception;

    List<Map> findReportHomewkResultIndSummary_mdul(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findReportHomewkResultIndSummary(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskList(PagingParam<?> pagingParam) throws Exception;

    Map<String, Object> findStntSrchReportTaskDetail_errata(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_image(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_mdul(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_analysis(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetail_coment(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetailMdul_info(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSrchReportTaskDetailMdul_task(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntSrchReportTaskDetail(Map<String, Object> paramData) throws Exception;

    Map<String, Object> modifyTchReportHomewkResultErrataMod_errata(Map<String, Object> paramData) throws Exception;

    int modifyReportTaskOpen(Map<String, Object> paramData) throws Exception;

    Object findReportTaskOpenData(Map<String, Object> paramData) throws Exception;

    int updateTchReportHomewkReviewSave(Map<String, Object> paramData) throws Exception;

    List<Map> fedInfoList(Map<String, Object> paramData) throws Exception;

    List<String> tchReportHomewkResultDetailMdul_classAnalysisInfo_answers(Map<String, Object> paramData) throws Exception;

    List<Map> findSendNtcnTaskList(Map<String, Object> paramData) throws Exception;

    List<Map> findSendNtcnTaskListPassivity(Map<String, Object> paramData) throws Exception;

    List<Map> findSendNtcnTaskListAuto(Map<String, Object> paramData) throws Exception;

    Map findTchReportTaskGeneralReviewInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchReportHomewkGeneralReviewAiEvlWord(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchReportHomewkResultDetail(Map<String, Object> paramData) throws Exception;

    List<Map> findStntTopFiveList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntGuideNeededList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findAvgCorrectRateInfo(Map<String, Object> paramData) throws Exception;

    int modifyTchReportHomewkResultErrataMod_submAt(Map<String, Object> paramData) throws Exception;
    int modifyTchReportHomewkResultErrataMod_1_rpt_y(Map<String, Object> paramData) throws Exception;

    int findEamMthInTaskInfo(String taskId);

    String findClaIdInTaskInfo(String taskId) throws Exception;
}
