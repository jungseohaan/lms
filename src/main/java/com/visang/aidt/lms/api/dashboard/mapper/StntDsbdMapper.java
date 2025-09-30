package com.visang.aidt.lms.api.dashboard.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.dashboard.mapper
 * fileName : StntDsbdMapper
 * USER : lsm
 * date : 2024-03-08
 */
@Mapper
public interface StntDsbdMapper {

    Map<String, Object> selectStntDsbdReportTotStd(Map<String, Object> param) throws Exception;
    Map<String, Object> selectStntDsbdReportCrrRateForTch(Map<String, Object> param) throws Exception;
    Map<String, Object> selectStntDsbdReportCrrRate(Map<String, Object> param) throws Exception;
    List<Map> findStntDsbdReportStdTotList(Map<String, Object> param) throws Exception;
    List<Map> findStntDsbdReportEvlTotListForTch(Map<String, Object> paramData) throws Exception;
    List<Map> findStntDsbdReportEvlTotList(Map<String, Object> param) throws Exception;
    List<Map> findStntDsbdReportTaskTotList(Map<String, Object> param) throws Exception;
    Map<String, Object> findStntDsbdReportTotalUndstn(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> getMainGoolInfo(Map<String, Object> param) throws Exception;

    List<Map<String, Object>> getGoolDetailOne(int goolIdx) throws Exception;

    void insertGoolInfo(Map<String, Object> param) throws Exception;

    void insertGoolDetail(int goolIdx, int order) throws Exception;

    List<Map<String, Object>> getTeacherGoolInfoList(PagingParam<?> pagingParam) throws Exception;

    void updateGoolDetail(Map<String, Object> param) throws Exception;

    // 교사가 속해있는 클래스 목록 조회
    List<Map<String, Object>> selectTeacherClassList(Map<String,Object> param) throws Exception;

    // 학생) 컨디션, 에너지에 따른 목록 조회
    List<Map<String, Object>> getConditionList(Map<String, Object> param) throws Exception;

    // 학생) 오늘의 기분 학생 insert
    int insertConditionDetail(Map<String, Object> param) throws Exception;

    // 학생) 학생의 오늘의 기분 단건 조회
    Map<String, Object> conditionInfo(Map<String, Object> param) throws Exception;

    // 교사) 클래스에 속해있는 모든 학생들의 기분 조회
    List<Map<String, Object>> conditionUserList(Map<String, Object> param) throws Exception;

    // 교사) 학생 오늘의 기분 상세
    List<Map<String, Object>> conditionUserDetail(Map<String, Object> param) throws Exception;

    // (학생) 총 학습 정보
    Map findTotalStdCntInfo(Map<String, Object> paramData) throws Exception;

    // (학생) 월 학습 정보
    List<Map> findMnthInfo(Map<String, Object> paramData) throws Exception;

    // 일 평균 학습시간
    Map findAvgTime(Map<String, Object> paramData) throws Exception;

    // 주학습평균횟수
    Map findAvgWeekStdCnt(Map<String, Object> paramData) throws Exception;

    // 학습분석정보
    Map<String, Object> findStdAnalyInfo(Map<String, Object> paramData) throws Exception;

    // 학습분석정보 영어
    Map<String, Object> findStdAnalyInfoForEng(Map<String, Object> paramData) throws Exception;

    // 자기주도학습 상세
    List<Map> findStntDsbdStatusSelflrnChapterDetailList(PagingParam<?> pagingParam) throws Exception;

    List<Map> findStntDsbdStatusHomewkList(PagingParam<?> pagingParam) throws Exception;
    List<Map> findStntDsbdStatusEvalList(PagingParam<?> pagingParam) throws Exception;

    // Map<String, Object> findStntDsbdEvlTaskList(Map<String, Object> paramData) throws Exception;
    List<Map> findStntDsbdEvlTaskBbsList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectStdDsbdEval(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectStdDsbdTask(Map<String, Object> paramData) throws Exception;



    //영역별이해도, 상세
        List<Map> selectStntDsbdStatusAreausdContAreaList(Map<String, Object> paramData) throws Exception;
        List<Map> selectStntDsbdStatusAreausdAreaUsdList(Map<String, Object> paramData) throws Exception;
        List<Map> selectStntDsbdStatusAreausdDetail(PagingParam<?> pagingParam) throws Exception;
        Map selectAreaName(Map<String, Object> paramData) throws Exception;


    // 단원별 이해도
    List<Map> findStntDsbdStatusChapterunitList(Map<String, Object> paramData) throws Exception;

    // 단원별 이해도 상세
    Map<Object, Object> selectUnitName(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatusChapterunitDetail(PagingParam<?> pagingParam) throws Exception;


    // 과제 챗봇 채팅 횟수
    int selectTaskAitutorCount(Map<String, Object> paramData) throws Exception;
    // 자기주도학습 챗봇 채팅 횟수
    int selectSlfStdAitutorCount(Map<String, Object> paramData) throws Exception;
    // 평가 챗봇 채팅 횟수
    int selectEvlAitutorCount(Map<String, Object> paramData) throws Exception;
    // 과제 챗봇 채팅 내용 리스트
    List<Map<String, Object>> selectTaskAitutorList(Map<String, Object> paramData) throws Exception;
    // 자기주도학습 챗봇 채팅 내용 리스트
    List<Map<String, Object>> selectSlfStdAitutorList(Map<String, Object> paramData) throws Exception;
    // 평가 챗봇 채팅 내용 리스트
    List<Map<String, Object>> selectEvlAitutorList(Map<String, Object> paramData) throws Exception;
    // 영어 자기주도학습 AI학습 학습내용 리스트
    List<Map<String, Object>> selectSlfStdAitutorLrnList(Map<String,Object> paramData) throws Exception;

    List<Map> selectStntDsbdAreaAchievementList(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntDsbdAreaAchievementListAll(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatisticAchievementList1_Main(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntDsbdStatisticAchievementList1(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntDsbdStatisticAchievementList2(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementStudentList_Main(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectTchDsbdAreaAchievementStudentList(PagingParam<?> pagingParam) throws Exception;
    List<Map> selectTchDsbdAreaAchievementStudentList_daily(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectStntDsbdUnitInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStudyMapLanguageFormatList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatusStudyMapCommunicationList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatusStudyMapMaterialList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatusStudyAchievementStandardList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatusVocabularyList(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectTchDsbdVocabularyStudentList(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectStntDsbdStatusGrammarList(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectTchDsbdGrammarStudentList(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectStntDsbdStatusPronunciationList(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectTchDsbdPronunciationStudentList(PagingParam<?> pagingParam) throws Exception;

    Map<String, Object> selectStdDsbdSummaryEng(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectStdDsbdSummaryMath(Map<String, Object> paramData) throws Exception;

    int selectTchDsbdStdMapAchievementStandardCount(Map<String, Object> paramData);

    int selectTchDsbdStdMapMaterialCount(Map<String, Object> paramData);

    int selectTchDsbdStdMapCommunicationCount(Map<String, Object> paramData);

    int selectTchDsbdStdMapLanguageFormatCount(Map<String, Object> paramData);

    int selectTchDsbdAreaAchievementCount(Map<String, Object> paramData);
    int selectTchDsbdAreaAchievementCountAll(Map<String, Object> paramData);

    int selectStntDsbdStatusVocabularyCount(Map<String, Object> paramData);

    int selectStntDsbdStatusGrammarCount(Map<String, Object> paramData);

    int selectStntDsbdStatusPronunciationCount(Map<String, Object> paramData);

    // [학생] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준(수학)
    List<Map> selectStntDsbdStdMapMathAchievementStandardList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdAreaAchievementStudentDistributionSummary(Map<String, Object> paramData) throws Exception;

    // [학생] 학습이력이 있는 단원번호 조회
    List<Map> selectStntLearnedUnitList(Map<String, Object> paramData) throws Exception;

    // [학생] 단원의 성취도 정보 조회
    Map selectStntDsbdUnitAchievement(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdChapterUsdStudentDistributionSummary(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatusCommunicationDetail(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdStatusCommunicationList(Map<String, Object> paramData) throws Exception;

    List<Map> selectStntDsbdUnitAchievementList(Map<String, Object> paramData) throws Exception;
}
