package com.visang.aidt.lms.api.selflrn.mapper;

import com.visang.aidt.lms.api.learning.vo.AiArticleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntSelfLrnEngMapper {
    List<Map> findStntSelfLrnChapterConceptListEng(Object paramData) throws Exception;

    Map<String, Object> findStntSelfLrnChapterConceptListEngMin(Object paramData) throws Exception;

    List<Map> selectSelfLrnEngArticles(Map<String, Object> paramData) throws Exception;
    List<Map> selectDiagnosticEngArticles(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectCurriSchool(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectCurriSchool2(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectCurriSchool3(Map<String, Object> paramData) throws Exception;

    List<Map> selectSelfLrnEngArticles_pronunciation(Map<String, Object> paramData, List<String> paramList) throws Exception;

    List<Map> selectSelfLrnEngArticles_test(Map<String, Object> paramData, List<String> paramList) throws Exception;

    int insertStntSelfLrnCreateEng(Map<String, Object> paramData) throws Exception;

    /* 진단하기 */
    int insertStntSelfLrnDiagEng(Map<String, Object> paramData) throws Exception;

    Map findStntSelfLrnResultSummaryInfoEng(Object paramData) throws Exception;

    Map selectStntSelfLrnReceiveEng_beforeModuleMap(Object paramData) throws Exception;

    List<Map> selectStntSelfLrnReceiveEng(Map<String, Object> paramData) throws Exception;

    List<Map> selectSelfLrnElementaryEngArticles(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSelfLrnUnitEng(Object paramData) throws Exception;

    int insertStntSelfLrnCreateElementaryEng(Map<String, Object> paramData) throws Exception;

    List<Map> findStntSelfLrnSetsElementaryEng(Object paramData) throws Exception;

    List<Map> findSlfStdInfo(Map<String, Object> paramData) throws Exception;
    int updateStntSelfLrn(Map<String, Object> paramData) throws Exception;
    int updateStntSelfLrnResult(Map<String, Object> paramData) throws Exception;
    int deleteStntSelfLrnResult(Map<String, Object> paramData) throws Exception;
    int deleteStntSelfLrnInfo(Map<String, Object> paramData) throws Exception;
    int deleteWonAswNote(Map<String, Object> paramData) throws Exception;

    List<Map> selectSelfLearningExceptList(Map<String, Object> paramData);
}
