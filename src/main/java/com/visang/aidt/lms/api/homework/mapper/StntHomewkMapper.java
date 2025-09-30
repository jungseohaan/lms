package com.visang.aidt.lms.api.homework.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.homework.mapper
 * fileName : StntHomewkMapper
 * USER : hs84
 * date : 2024-01-25
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-25         hs84          최초 생성
 */
@Mapper
public interface StntHomewkMapper {
    // /stnt/homewk/list
    Map<String, Object> findStntHomewkListCheck(Map<String, Object> paramData) throws Exception;
    List<Map> findStntHomewkList(PagingParam<?> paramData) throws Exception;

    // /stnt/homewk/info
    Map<String, Object> findStntHomewkInfo(Map<String, Object> paramData) throws Exception;

    // /stnt/homewk
    int modifyStntHomewkResultInfo(Map<String, Object> paramData) throws Exception;
    int modifyStntHomewkResultDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntHomewk(Map<String, Object> paramData) throws Exception;

    // /stnt/homewk/exam
    int modifyStntHomewkExam(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntHomewkExam(Map<String, Object> paramData) throws Exception;
    List<Map> findStntHomewkExamResult(Map<String, Object> paramData) throws Exception;

    // /stnt/homewk/save
    int modifyStntHomewkSave(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTaskResultDetailCount(Map<String, Object> paramData) throws Exception;
    int modifyStntTaskResultInfo(Map<String, Object> paramData) throws Exception;
    int modifyStntTaskInfo(Map<String, Object> paramData) throws Exception;

    // /stnt/homewk/Submit
    int modifyStntHomewkSubmit(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntHomewkSubmit(Map<String, Object> paramData) throws Exception;
    ///
    int modifyStntHomewkSubmitResultDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntHomewkSubmitResultDetailCnt(Map<String, Object> paramData) throws Exception;
    int modifyStntHomewkSubmitResultInfo(Map<String, Object> paramData) throws Exception;
    int modifyStntHomewkSubmitTaskInfo(Map<String, Object> paramData) throws Exception;
    ///

    // /stnt/homewk/Result
    Map<String, Object> findStntHomewkResult(Map<String, Object> paramData) throws Exception;
    List<Map> findStntHomewkResultDetail(Map<String, Object> paramData) throws Exception;



    int modifyStntTaskInit(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalInitForTaskResultInfo(Map<String, Object> paramData) throws Exception;

    int modifyStntEvalInitForTaskInfo(Map<String, Object> paramData) throws Exception;

    int modifyStntHomewkRecheck(Map<String, Object> paramData) throws Exception;

    int findStntHomewkIdByArticle(Map<String, Object> paramData) throws Exception;
    String findStntHomewkAiTutCn(Map<String, Object> paramData) throws Exception;
    int modifyStntHomewkAiTutSave(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findStntHomewkResultinfo(Map<String, Object> paramData) throws Exception;

    int modifyStntTaskResultDetail(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findConceptCheck(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTaskResuldDetail(Map<String, Object> paramData) throws Exception;

    ///stnt/homewk/time/usage
    int findStntHomewkTimeUsage(Map<String, Object> paramData) throws Exception;
    int saveStntHomewkTimeUsage(Map<String, Object> paramData) throws Exception;
    int selectClaHomewkCheck(Map<String, Object> paramData) throws Exception;

}
