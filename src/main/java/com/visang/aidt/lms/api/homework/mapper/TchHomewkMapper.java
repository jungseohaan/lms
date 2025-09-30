package com.visang.aidt.lms.api.homework.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.mapper
 * fileName : TchHomewkMapper
 * USER : hs84
 * date : 2024-01-24
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-24         hs84          최초 생성
 */
@Mapper
public interface TchHomewkMapper {
    // /tch/homewk
    Map<String, Object> findTaskInfo(Map<String, Object> paramData) throws Exception;

    // /tch/homewk/list
    List<Map> findTchHomewkListTaskList(PagingParam<?> paramData) throws Exception;
    List<Map> findTchHomewkListTaskListTmpr(PagingParam<?> paramData) throws Exception;

    // /tch/homewk/info
    Map<String, Object> findTchHomewkListTaskInfo(Map<String, Object> paramData) throws Exception;

    // /tch/homewk/preview
    Map<String, Object> findTchHomewkPreviewTaskInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchHomewkPreviewStudentList(Map<String, Object> paramData) throws Exception;

    // /tch/homewk/result/status
    Map<String, Object> findTchHomewkResultStatusTaskInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchHomewkResultStatusTaskIemInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchHomewkResultStatusTaskResultDet(Map<String, Object> paramData) throws Exception;
    List<Map> findTchHomewkResultStatusSubMitAnwStnt(Map<String, Object> paramData) throws Exception;
    List<Map> findTchHomewkResultStatusAnwStudent(Map<String, Object> paramData) throws Exception;


    // /tch/homewk/preview
    int removeTchTaskResultDetail(Map<String, Object> paramData) throws Exception;
    int removeTchTaskResultInfo(Map<String, Object> paramData) throws Exception;
    int removeTchTaskTrnTrget(Map<String, Object> paramData) throws Exception;
    int removeTchTaskInfo(Map<String, Object> paramData) throws Exception;

    // /tch/homewk/read-info
    Map<String, Object> findTchHomewkReadInfo(Map<String, Object> paramData) throws Exception;

    // /tch/homewk/save
    int modifyTchTaskSave(Map<String, Object> paramData) throws Exception;
    int createTchTaskSaveSets(Map<String, Object> paramData) throws Exception;
    int removeTchTaskSaveTRD(Map<String, Object> studentInfo) throws Exception;
    int removeTchTaskSaveTRI(Map<String, Object> studentInfo) throws Exception;
    List<Map<String, Object>> findExistingTaskResultInfo(Map<String, Object> paramData) throws Exception;
    int createTchTaskSaveTRI(Map<String, Object> studentInfo) throws Exception;
    int createTchTaskSaveTRD(Map<String, Object> studentInfo) throws Exception;
    int modifyTchTaskSaveBbsSetId(Map<String, Object> studentInfo) throws Exception;



    int modifyTchTaskInitTaskInfoY(Map<String, Object> paramData) throws Exception;
    int modifyTchTaskInitTaskInfoN(Map<String, Object> paramData) throws Exception;

    int modifyTchTaskInitTaskResultInfo(Map<String, Object> paramData) throws Exception;
    int modifyTchTaskInitTaskResultDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchTaskInit(Map<String, Object> paramData) throws Exception;

    int createTchTaskCreate(Map<String, Object> paramData) throws Exception;

    int modifyHomewkSubmAtTRD(Map<String, Object> paramData) throws Exception;
    int modifyHomewkSubmAtTRI(Map<String, Object> paramData) throws Exception;

    int modifyTchTaskSaveEEN(Map<String, Object> paramData) throws Exception;

    LinkedHashMap getTaskInfoById(Map<String, Object> paramData) throws Exception;

    List<LinkedHashMap> findTaskResultInfoListByTaskId(Map<String, Object> paramData) throws Exception;

    int cloneTaskInfo(Map<String, Object> paramData) throws Exception;
    int cloneTaskResultInfo(Map<String, Object> paramData) throws Exception;


    int copyTaskResultDetailByTaskId(Map tmpParamMap) throws Exception;

    List<Map> findHomewkAutoQstnExtr(Map<Object, Object> procParamData) throws Exception;

    int modifyTaskStatusToInProgress(Map<String, Object> paramData) throws Exception;
    int modifyTchTaskSaveSetsId(Map<String, Object> paramData) throws Exception;

    int createTchTaskCreateForTextbk_taskInfo(Map<String, Object> paramData) throws Exception;
    int createTchTaskCreateForTextbk_taskResultInfo(Map<String, Object> paramData) throws Exception;
    int createTchTaskCreateForTextbk_taskResultDetail(Map<String, Object> paramData) throws Exception;

    int modifyTchHomewkPeriodChange(Map<String, Object> paramData) throws Exception;
    int modifyTchHomewkResultInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findTchHomewkStatusList_currentTaskList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchHomewkStatusList_reqGradeTaskListItem(Map<String, Object> paramData) throws Exception;

    void updateTimTime(Map<String, Object> paramData);

    int selectSttsCd(Map<String, Object> paramData);

    List<Map> hasStdtTakenTask(Map<String, Object> paramData);

    // 모둠 출제 시 학생 목록 조회
    List<Map> findTchHomewkReadInfoStntList(Map<String, Object> paramData);

    int modifyTchAiCustomTaskInfo(Map<String, Object> paramData);

    Map<String, Object> findTaskSubmStatus_ti(Map<String, Object> paramData) throws Exception;
    List<Map> findTaskSubmStatus_tri(Map<String, Object> paramData) throws Exception;
    List<Map> findTchHomewkStntList(Map<String, Object> paramData) throws Exception;

    int modifyTchHomewkEnd(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTchHomewkEndHomewkInfo(Map<String, Object> paramData) throws Exception;

    void updateTaskAt(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> finTaskResultInfo(Map<String, Object> paramData) throws Exception;

    int findTchHomewkEamTrget(Map<String, Object> paramData);

    int createTchTaskSaveTriForEamtrget2(Map<String, Object> paramData);

    int updateTaskInfo(Map<String, Object> paramData);
}
