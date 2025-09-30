package com.visang.aidt.lms.api.dashboard.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * packageName : com.visang.aidt.lms.api.dashboard.mapper
 * fileName : StntDsbdMapper
 * USER : lsm
 * date : 2024-03-08
 */
@Mapper
public interface TchDsbdMapper {

    List<Map> findUnitInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findSubmInfo(Map<String, Object> paramData) throws Exception;

    LinkedHashMap<Object, Object>  findTchDsbdStatusSelflrnChapterDetail(Map<String, Object> paramData) throws Exception;

    List<Map> findChapterDetailStntInfo(Map<String, Object> paramData) throws Exception;

    LinkedList<Map> findTchDsbdStatusChapterUnitList(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStatusChapterUnitInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStudentsByUnitLevel(Map<String, Object> paramData) throws Exception;

    Map findTchDsbdStatusChapterUnitDetail(Map<String, Object> paramData) throws Exception;

    List<Map> findTchDsbdStatusChapterUnitDetail_chptStdtList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectTchDsbdSummary(Map param) throws Exception;
    Map<String, Object> selectTchDsbdSummaryNew(Map param) throws Exception;

    int findBrandId(Map<String, Object> innerParam) throws Exception;

    Integer selectTchDsbdParticipantCnt(Map<String, Object> param);
    int selectTchDsbdStdtCnt(Map<String, Object> param);
    int selectTchDsbdLowerLevelStdtCnt(Map<String, Object> param);
    int selectTchDsbdCdtnUpdCnt(Map<String, Object> param);
    String selectBtchUpDt(Map<String, Object> param);

    List<Map> selectTchDsbdEvalList(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdTaskList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchDsbdEvlTaskList(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdChptUnitKwgCombo(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdCncptUsdList_Main(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdCncptUsdList(Map<String, Object> paramData) throws Exception;
    Map selectTchDsbdConceptUsdCnt(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdConceptUsdDetail(Map<String, Object> paramData) throws Exception;

    /* 전체 단원 */
    List<Map> selectTchDsbdCncptUsdAllUnitList_Main(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdCncptUsdAllUnitList(Map<String, Object> paramData) throws Exception;
    Map selectTchDsbdCncptUsdAllUnitGradeCnt(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdConceptUsdAllUnitDetail(Map<String, Object> paramData) throws Exception;

    /* 단원 전체 */
    List<Map> selectTchDsbdCncptUsdUnitAllKwgList_Main(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdCncptUsdUnitAllKwgList(Map<String, Object> paramData) throws Exception;
    Map selectTchDsbdCncptUsdUnitAllKwgGradeCnt(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdConceptUsdAllKwgDetail(Map<String, Object> paramData) throws Exception;


    List<Map> selectTchDsbdStatusAreausdContAreaList(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdStatusAreausdAreaUsdList(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdStatusAreausdDetail(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdStatusAreausdDetailAreaStdtList(Map<String, Object> paramData) throws Exception;
    Map selectAreaName(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectTchDsbdChptUnitInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStdMapUsdList(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectTchDsbdStdCncptUsdInfo(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectTchDsbdStdMapUsdInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStdMapCncptStdtList(Map<String, Object> paramData) throws Exception;

    int createTchOftensents(Map<String, Object> paramData) throws Exception;
    int modifyTchOftensents(Map<String, Object> paramData) throws Exception;
    int deleteTchOftensents(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchOftensents(PagingParam<?> pagingParam) throws Exception;

    List<Map> selectTchDsbdStdMapLanguageFormatList(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStdMapCommunicationList(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStdMapMaterialList(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStdMapAchievementStandardList(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatusVocabularyList(PagingParam<?> pagingParam) throws Exception;
    List<Map> selectTchDsbdStatusVocabularyListAll(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdUnitInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementList(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdAreaAchievementListAll(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectTchDsbdAreaAchievementCountDetail(Map<String, Object> paramData) throws Exception;
    Map<Object, Object> selectTchDsbdAreaAchievementCountDetail_daily(Map<String, Object> paramData) throws Exception;
    Map<Object, Object> selectTchDsbdAreaAchievementCountDetailAll(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdAreaAchievementAitutor(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStudentsByAreaAchievementLevel(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStudentsByAreaAchievementLevel2(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdAreaAchievementAitutor2(Map<String, Object> paramData) throws Exception;
    List<Map> areaAchievementAitutorCnt(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementStudentList(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdAreaAchievementStudentList_daily(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdAreaAchievementStudentListAll(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdVocabularyStudentList(Map<String, Object> paramData) throws Exception;

    //추후 삭제
    List<Map> selectTchDsbdVocabularyStudentListAll(Map<String, Object> paramData) throws Exception;

    //메소드명 변경
    List<Map> selectTchDsbdStudentListAll(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatisticAchievementList1(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdStatisticAchievementList1_main(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatisticAchievementList2(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectTchDsbdVocabularyCountDetail(Map<String, Object> paramData) throws Exception;

    //추후 삭제
    Map<Object, Object> selectTchDsbdVocabularyCountDetailAll(Map<String, Object> paramData) throws Exception;

    //메소드명 변경
    Map<Object, Object> selectTchDsbdCountDetailAll(Map<String, Object> paramData) throws Exception;


    List<Map> selectTchDsbdStatusGrammarList(PagingParam<?> pagingParam) throws Exception;

    Map<Object, Object> selectTchDsbdGrammarCountDetail(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdGrammarStudentList(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatusPronunciationList(PagingParam<?> pagingParam) throws Exception;

    Map<Object, Object> selectTchDsbdPronunciationCountDetail(Map<String, Object> paramData) throws Exception;


    List<Map> selectTchDsbdPronunciationStudentList(Map<String, Object> paramData) throws Exception;

    Map findStntCnt(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStdMapKwgList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchDsbdStatusAreaAchievementDetailInfo_Back(Map<String, Object> paramData) throws Exception;

    Map<Object, Object>  findTchDsbdStatusAreaAchievementDetailInfo(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> findTchDsbdStatusStudyMapDetail(Map<String, Object> paramData) throws Exception;
    List<Map> findTchDsbdStatusStudyMapDetail_list(Map<String, Object> paramData) throws Exception;
    
    int selectTchDsbdStdMapAchievementStandardCount(Map<String, Object> paramData);
    
    int selectTchDsbdStdMapMaterialCount(Map<String, Object> paramData);
    
    int selectTchDsbdStdMapCommunicationCount(Map<String, Object> paramData);
    
    int selectTchDsbdStdMapLanguageFormatCount(Map<String, Object> paramData);
    
    int selectTchDsbdAreaAchievementCount(Map<String, Object> paramData);
    int selectTchDsbdAreaAchievementCountAll(Map<String, Object> paramData);

    int selectTchDsbdStatusVocabularyCount(Map<String, Object> paramData);
    int selectTchDsbdStatusVocabularyCountAll(Map<String, Object> paramData);

    int selectTchDsbdStatusGrammarCount(Map<String, Object> paramData);
    
    int selectTchDsbdStatusPronunciationCount(Map<String, Object> paramData);
    
    String selectTchDsbdUnitCode(Map<String, Object> paramData);

    List<Map> findTchDsbdTargetArticle(Map<String, Object> paramData) throws Exception;

    int selectTchDsbdStatusMathStnCount(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatusMathBestStntList(Map<String, Object> paramData) throws Exception;

    int selectTchDsbdStatusMathBestStntCount(Map<String, Object> paramData) throws Exception;

    // 중간등급 학생 목록
    List<Map> selectTchDsbdStatusMathMddStntList(Map<String, Object> paramData) throws Exception;

    // 중간등급 학생 수
    int selectTchDsbdStatusMathMddSStntCount(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatusMathWorstStntList(Map<String, Object> paramData) throws Exception;
    int selectTchDsbdStatusMathWorstStntCount(Map<String, Object> paramData) throws Exception;

    int selectTchDsbdStatusAreaAchievementCount(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatusAreaAchievementBestStntList(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdStatusAreaAchievementStntInfo(Map<String, Object> paramData) throws Exception;
    int selectTchDsbdStatusAreaAchievementBestStntCount(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdStatusAreaAchievementWorstStntList(Map<String, Object> paramData) throws Exception;
    int selectTchDsbdStatusAreaAchievementWorstStntCount(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectTchDsbdAiTutorUnitInfo(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectHomeNoticeList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> selectPopupNoticeList(Map<String, Object> paramData) throws Exception;

    int updateNoticePin(Map<String, Object> paramData) throws Exception;

    int insertNotice(Map<String, Object> paramData) throws Exception;

    int deleteNotice(Map<String, Object> paramData) throws Exception;

    List<Map> selectStatisticParticipant(Map<String, Object> paramData) throws Exception;

    int targetStntCnt(Map<String, Object> paramData) throws Exception;

    int updateStatisticParticipant(Map<String, Object> paramData) throws Exception;

    int insertStatisticParticipant(Map<String, Object> paramData) throws Exception;

    int insertStatisticParticipantBatch(Map<String, Object> paramData) throws Exception;

    int updateStatisticParticipantBatch(Map<String, Object> paramData) throws Exception;

    int upsertStatisticParticipant(Map<String, Object> paramData) throws Exception;

    int deleteStatisticParticipant(Map<String, Object> paramData) throws Exception;

    // [교사] 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준(수학)

    List<Map> selectTchDsbdStdMapMathAchievementStandardList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectMemoList(Map<String, Object> paramData) throws Exception;

    int insertMemo(Map<String, Object> paramData) throws Exception;

    int deleteMemo(Map<String, Object> paramData) throws Exception;

    // [교사] 학급관리 > 홈 대시보드 > 메모 수정
    int updateMemo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectMemo(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdCalendarEventsList(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectLeaningSummaryStatisticsEng(Map<String, Object> paramData) throws Exception;

    Map<Object, Object> selectLeaningSummaryStatisticsMath(Map<String, Object> paramData) throws Exception;

    List<Map> selectLeaningSummaryStatisticsParticipationMath(Map<String, Object> paramData) throws Exception;

    List<Map> selectOfClassInStudentsList(Map<String, Object> paramData) throws Exception;

    // 대시보드 > 그룹별추천과제 : 추천세트지
    Map<Object, Object> selectTchDsbdRecSets(Map<String, Object> paramData) throws Exception;

    // 대시보드 > 그룹별추천과제 : 추천아티클
    List<Map> selectTchDsbdRecArticle(Map<String, Object> paramData) throws Exception;

    // 대시보드 > 그룹별추천과제 : 현재 추천받은 세트지
    int modifyTchDsbdRecChk(Map<String, Object> paramData) throws Exception;


    Map<String, Object> selectTchDsbdCalendarAchievement(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> selectLessonCalendarCrcuInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectLessonCalendarInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectLessonCrrctCorrectRate(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectTaskCalendarCrcuInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectTaskCalendarInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectTaskCorrectRate(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectEvlCalendarCrcuInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectEvlCalendarInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectEvlCorrectRate(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectTchDsbdCalendarusdSrc(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementDistribution(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementDistributionSummary(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementStudentDstribution(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdAreaAchievementStudentDistributionSummary(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdDistributionAreaAchievementStudentList(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdDistributionAreaAchievementStudentList1(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdDistributionAreaAchievementStudentList2(Map<String, Object> paramData) throws Exception;

    List<Map> selectUnitList(Map<String, Object> paramData) throws Exception;
    List<Map> selectEngAreaList() throws Exception;
    List<Map> selectLearnedUnitList(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdUnitAchievementList(Map<String, Object> paramData) throws Exception;
    List<Map> selectStudentSelfLearningQuestionCount(Map<String, Object> paramData) throws Exception;


    List<Map> selectTchDsbdChapterUsdClassdDstribution(Map<String, Object> paramData) throws Exception;
    List<Map> selectTchDsbdChapterUsdClassdDstribution2(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdChapterUsdDistributionSummary(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdChapterUsdStudentDistribution(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdDistributionChapterUsdStudentList(Map<String, Object> paramData) throws Exception;

    List<Map> findTchDsbdConceptUsdTree(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdUsdParticipationQuadrant(Map<String, Object> paramData) throws Exception;

    List<Map> selectTchDsbdUsdParticipationStudentQuadrant(Map<String, Object> paramData) throws Exception;

    int insertTchEncouragementNotification(Map<String, Object> paramData) throws Exception;

    int insertStntEncouragementNotification(Map<String, Object> paramData) throws Exception;

    List<Map> findMetaInfoList(Map<String, Object> paramData);

    List<Map<String, Object>> selectTchDsbdStdMapMathStudentScores(Map<String, Object> paramData);

    Map<Object, Object> selectTchDsbdLastLesson(Map<String, Object> paramData) throws Exception;


    Map<String, Object> selectBbsCalendarInfo(Map<String, Object> paramData) throws Exception;
}
