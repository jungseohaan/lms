package com.visang.aidt.lms.api.assessment.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.assessment.mapper
 * fileName : StntEvalMapper
 * USER : hs84
 * date : 2024-01-18
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-18         hs84          최초 생성
 */
@Mapper
public interface StntEvalMapper {
    // /stnt/eval/list
    Map<String, Object> findStntEvalListEvalCheck(Map<String, Object> paramData) throws Exception;

    List<Map> findStntEvalListEvalInfo(PagingParam<?> paramData) throws Exception;

    // /stnt/eval/start
    int modifyEvalStartResultInfo(Map<String, Object> paramData) throws Exception;

    int modifyEvalStartResultDetail(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntEvalStart(Map<String, Object> paramData) throws Exception;

    // /stnt/eval/submit
    int modifyStntEvalSubmitResultDetail(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectStntEvalSubmitResultDetailCnt(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalSubmitResultInfo(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalSubmitEvlInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchEvalSubmit(Map<String, Object> paramData) throws Exception;

    // /stnt/eval/exam
    int modifyStntEvalExamResultDetail(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntEvalExamEvalInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findStntEvalExamEvalIemInfo(Map<String, Object> paramData) throws Exception;

    // /stnt/eval/save
    Map<String, Object> findStntEvalSaveIemScr(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalSaveResultDetail(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalSaveResultInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findEvlResultDetailCount(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalResultInfo(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalInfo(Map<String, Object> paramData) throws Exception;


    // /stnt/eval/mark
    Map<String, Object> findStntEvalResultEvalInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findStntEvalResultEvalIemInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntResultInfo(Map<String, Object> paramData) throws Exception;


    // /stnt/eval/info
    Map<String, Object> findStntEvalInfoEvalInfo(Map<String, Object> paramData) throws Exception;

    // /stnt/eval/recheck
    int modifyStntEvalRecheck(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalInit(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalInitForEvlResultInfo(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalInitForEvlInfo(Map<String, Object> paramData) throws Exception;

    int findStntEvalIdByArticle(Map<String, Object> paramData) throws Exception;

    String findStntEvalAiTutCn(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalAiTutSave(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntEvalResultDetailInfo(Map<String, Object> paramData) throws Exception;

    int modifyStntEvlResultDetail(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findConceptCheck(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findEvalResuldDetail(Map<String, Object> paramData) throws Exception;

    int modifyStntTaskDone(Map<String, Object> paramData) throws Exception;

    int modifyStntEvlDone(Map<String, Object> paramData) throws Exception;

    int saveClassMoveHistory(Map<String, Object> paramData) throws Exception;

    int saveClassAddHistory(Map<String, Object> paramData) throws Exception;

    int saveClassMoveStdDataChange_evlResultInfo(Map<String, Object> paramData) throws Exception;

    int saveClassMoveStdDataChange_evlResultDetail(Map<String, Object> paramData) throws Exception;

    int saveClassMoveStdDataChange_taskResultInfo(Map<String, Object> paramData) throws Exception;

    int saveClassMoveStdDataChange_taskResultDetail(Map<String, Object> paramData) throws Exception;


    int removeClassMoveStdDataChange_evlResultDetail(Map<String, Object> paramData) throws Exception;

    int removeClassMoveStdDataChange_evlResultInfo(Map<String, Object> paramData) throws Exception;

    int removeClassMoveStdDataChange_taskResultDetail(Map<String, Object> paramData) throws Exception;

    int removeClassMoveStdDataChange_taskResultInfo(Map<String, Object> paramData) throws Exception;


    int modifyClassMoveStdDataChange_rwdEarnInfo(Map<String, Object> paramData) throws Exception;

    int modifyClassMoveStdDataChange_rwdEarnHist(Map<String, Object> paramData) throws Exception;

    int modifyClassMoveStdDataChange_spPrchsInfo(Map<String, Object> paramData) throws Exception;

    int modifyClassMoveStdDataChange_spPrchsHist(Map<String, Object> paramData) throws Exception;

    int saveClassStdData_evlResultInfo(Map<String, Object> paramData) throws Exception;

    int saveClassStdData_evlResultDetail(Map<String, Object> paramData) throws Exception;

    int saveClassStdData_taskResultInfo(Map<String, Object> paramData) throws Exception;

    int saveClassStdData_taskResultDetail(Map<String, Object> paramData) throws Exception;

    /// stnt/eval/time/usage
    int findStntEvalTimeUsage(Map<String, Object> paramData) throws Exception;

    int saveStntEvalTimeUsage(Map<String, Object> paramData) throws Exception;

    Map<String,Object> findStntSetCheck(Map<String, Object> paramData) throws Exception;

    int selectClaEvalCheck(Map<String, Object> paramData) throws Exception;

    void updateEvalAt(Map<String, Object> paramData) throws Exception;
    void updateTaskAt(Map<String, Object> paramData) throws Exception;
}