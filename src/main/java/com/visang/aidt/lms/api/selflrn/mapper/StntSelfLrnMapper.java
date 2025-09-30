package com.visang.aidt.lms.api.selflrn.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntSelfLrnMapper {

    public int selectStntSelfLrnExistCheck(Object paramData) throws Exception;

    public List<Map> findStntSelfLrnChapterList(Object paramData) throws Exception;

    public List<Map> findStntSelfLrnChapterConceptList(PagingParam<?> paramData) throws Exception;

    int saveStntSelfLrnSubmitAnswer(Map<String, Object> paramData) throws Exception;

    int saveStntSelfLrnEnd(Map<String, Object> paramData) throws Exception;

    int saveStntSelfLrnStdEnd(Map<String, Object> paramData) throws Exception;

    public Map findStntSelfLrnResultSummaryInfo(Object paramData) throws Exception;

    public List<Map> findStntSelfLrnResultSummary(Object paramData) throws Exception;

    int insertStntSelfLrnCreate(Map<String, Object> paramData) throws Exception;

    // 자기주도학습(선택학습) 생성
    int insertStntSelfLrnResultCreate(Map<String, Object> paramData) throws Exception;

    public Map findStntSelfLrnCreateSummaryInfo(Object paramData) throws Exception;

    public List<Map> findStntSelfLrnCreateSummary(Object paramData) throws Exception;
    Map<String,Object> findStntSelfLrnCreateReceiveSummary(Object paramData) throws Exception;

    List<Map> findStntSelfLrnList(PagingParam<?> paramData) throws Exception;
    List<Map> findStntSelfLrnListForMathAI(PagingParam<?> paramData) throws Exception;

    List<Map> findStntSelfLrnListForMath(PagingParam<?> paramData) throws Exception;
    List<Map> findStntSelfLrnListForEng(PagingParam<?> paramData) throws Exception;
    List<Map> findStntSelfLrnListForEngNew(PagingParam<?> paramData) throws Exception;

    List<Map> findStntSelfLrnListForMathAll(Map<String, Object> paramData) throws Exception;
    List<Map> findStntSelfLrnListForEngNewAll(Map<String, Object> paramData) throws Exception;

    public Map findStntSelfLrnResultInfo(Object paramData) throws Exception;
    public Map findStntSelfLrnResultInfoForMathAI(Object paramData) throws Exception;

    int insertStntSelfLrnResultReceive(Map<String, Object> paramData) throws Exception;

    public Map findStntSelfLrnCreateReceiveInfo(Object paramData) throws Exception;

    public List<Map> findStntSelfLrnResultSummaryList(Object paramData) throws Exception;

    public List<Map> findStntSelfLrnResultSummaryListForMathAI(Object paramData) throws Exception;

    public List<Map> selectStntSelfLrnRecvModuleList(Object paramData) throws Exception;

    public Map<String, Object> selectStntSelfLrnRewardClaid(Object paramData) throws Exception;

    List<Map> stntSelflrnUsdlowKwgList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStdCptList(Object paramData) throws Exception;

    List<Map> selectStdMthList(Object paramData) throws Exception;

    List<Map<String, Object>> findStntSelfLrnActLvlList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findStntSelfLrnAnwIptTyList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findStntSelfLrnActTyList(Map<String, Object> paramData) throws Exception;

    // 자기주도학습(선택학습) 문항 추천 목록 조회 (생성 관련)
    List<Map> selectStntSelfLrnRecModuleList(Map<String, Object> paramData) throws Exception;

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 목록 조회하기
    List<Map> selectStntSelfLrnMyWordList(Map<String, Object> paramData) throws Exception;

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 나의단어장 플래시 카드 start
    List<Map> selectStntSelfLrnMyWordFlashStart(PagingParam<?> pagingParam) throws Exception;

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 나의단어장 테스트 start
    List<Map> selectStntSelfLrnMyWordExamStart(PagingParam<?> pagingParam) throws Exception;

    //[학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 나의단어장 발음연습하기
    List<Map> selectStntSelfLrnMyWordArticulationStart(PagingParam<?> pagingParam) throws Exception;

    public Map findStntSelfLrnMudlResultSummaryList(Object paramData) throws Exception;

    public Map findStntSelfLrnLastLesson(Object paramData) throws Exception;

    List<Map> findStntSelfLrnChapterList2(Object paramData) throws Exception;

    List<Map> findStntSelfLrnChapterList3(Object paramData) throws Exception;

    int updateStntSelfLrnRecheck(Map<String, Object> paramData) throws Exception;


    int getBrandIdByTextbkId(int textbkId) throws Exception;

    Map<String, Object> getDeleteStntSelfLrnEnd(Object paramData) throws Exception;
    int deleteStntSelfLrnEnd_info(Map<String, Object> paramData) throws Exception;

    int updateStntSelfLrnEnd_info(Map<String, Object> paramData) throws Exception;

    int updateStntSelfLrnEnd_result_info(Map<String, Object> paramData) throws Exception;

    int deleteStntSelfLrnEnd_result_info(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findSlfStdInfoById(Map<String, Object> paramData) throws Exception;

    List<Map> selectAiEditInitEng() throws Exception;
    int insertAiEditSaveEng(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectAiEditResultEng(Object paramData) throws Exception;

    Map<String, Object> findSlfStdResultInfo(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntSelfLrnRecModuleAllList(Map<String, Object> paramData, List<Map> articleList) throws Exception;
    List<Map> selectStntSelfLrnRecModuleStudymapList(Map<String, Object> paramData) throws Exception;

    List<Map> stntSelfLrnDashBoardGraphListForEngNew(PagingParam<?> pagingParam);

    List<Map> stntSelfLrnDashBoardGraphListForMath(PagingParam<?> pagingParam);

    Map<String, Object> stntSelflrnUsdlowKwgList_stdAt(Map<String, Object> paramData) throws Exception;
    List<Map> selectSaveStntSelfLrnEndCorrectCnt(Map<String, Object> paramData) throws Exception;

    void updateStntElementaryEngSelfLearningCount(Map<String, Object> paramData);
}