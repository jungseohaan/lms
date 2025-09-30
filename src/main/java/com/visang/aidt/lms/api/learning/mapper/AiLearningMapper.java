package com.visang.aidt.lms.api.learning.mapper;

import com.visang.aidt.lms.api.learning.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AiLearningMapper {

    List<Map<String,Object>> findTargetEvlList(Map<String, Object> paramData) throws Exception;

    List<Map> findAutoCreateAiLearningEvlStep2(Map<String, Object> paramData) throws Exception;

    AiArticleVO findAutoCreateAiLearningEvlStep3(Map<String, Object> innerParam) throws Exception;

    List<Map<String,Object>> findTargetTaskList(Map<String, Object> paramData) throws Exception;

    List<AiArticleVO> findAutoCreateAiLearningTaskStep1(Map<String, Object> paramData) throws Exception;

    Map<String, Object> getSetInfoByEvlId(int id) throws Exception;

    Map<String, Object> getSetInfoByTaskId(int id) throws Exception;

    List<Map> getMetaListBySetId(String id) throws Exception;

    List<AiArticleVO> findAutoCreateAiLearningEvlStep1(Map<String, Object> paramData) throws Exception;



    int insertEvlToTaskInfo(Map<String, Object> innerParam);

    int insertEvlInfo(Map<String, Object> paramData) throws Exception;

    int createEvlResultInfo(Map<String, Object> paramData) throws Exception;

    int createEvlResultDetail(Map<String, Object> paramData) throws Exception;

    int updateAfterCreateEvlInfo(Map<String, Object> paramData) throws Exception;

    int insertTaskInfo(Map<String, Object> innerParam) throws Exception;

    int createTaskResultInfo(Map<String, Object> paramData) throws Exception;

    int createTaskResultDetail(Map<String, Object> paramData) throws Exception;

    int updateAfterCreateTaskInfo(Map<String, Object> paramData) throws Exception;

    int createTaskInfoForAiCustomLearning(TaskInfoVO taskInfoVO) throws Exception;

    Long selectTaskInfoForAiCustomLearning(TaskInfoVO taskInfoVO) throws Exception;

    int createTaskResultInfoForAiCustomLearning(Map<String, Object> param) throws Exception;

    List<String> selectStntList(Map<String, Object> innerParam) throws Exception;

    int createTaskResultDetailForAiCustomLearning(Map<String, Object> param) throws Exception;

    int createTabInfoForAiCustomLearning(TabInfoVO tabInfoVO) throws Exception;

    int createStdDtaInfoForAiCustomLearning(StdDtaInfoVO stdDtaInfoVO) throws Exception;

    int createStdDtaResultInfoForAiCustomLearning(Map<String, Object> innerParam) throws Exception;

    int createStdDtaResultDetailForAiCustomLearning(Map<String, Object> innerParam) throws Exception;

    int updateTabInfoForAiCustomLearning(Map<String, Object> innerParam) throws Exception;

    String findTabNmForAiCustomLearning(Map<String, Object> innerParam) throws Exception;

    int findSetCategory(Map<String, Object> innerParam) throws Exception;

    int findBrandId(Map<String, Object> innerParam) throws Exception;

    int findBrandIdByEvalId(int evlId) throws Exception;

    int findBrandIdByTaskId(int taskId) throws Exception;

    int findAiCustomLeariningPersonalCountCheck(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findStntLevelList(Map<String, Object> paramData) throws Exception;

    int createConfigAiCustomLearning(Map<String, Object> innerParam) throws Exception;

    int createTaskResultInfoForAiCustomLearningPersonal(TaskResultInfoVO resultVo) throws Exception;

    String getStudyMapNm(Map<String, Object> innerParam) throws Exception;

    int createStdDtaResultInfoForAiCustomLearningPersonal(Map<String, Object> innerParam) throws Exception;

    String getEamScp(Map<String, Object> innerParam) throws Exception;

    List<Map> findAiCustomLearningSetInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findAiCustomLearningSetInfo_dtaResultInfo(Map<String, Object> paramData) throws Exception;

    int removeTaskInfo(int taskId) throws Exception;

    int removeDtaInfo(Map configMap) throws Exception;

    int removeConfigInfoByTabId(Map<String, Object> paramData) throws Exception;

    int resetTabInfoForAiCustomLearning(Map<String, Object> paramData) throws Exception;

    String getTabInfoCrtAt(Map<String, Object> paramData) throws Exception;

    List<Map> findStntAiCustomLearningSetInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findStntAiCustomLearningSetInfo_dtaResultInfo(Map<String, Object> paramData) throws Exception;

    int createConfigAiCustomResult(Map<String, Object> innerParam) throws Exception;

    List<Map> findCustomEamResultList(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>> findStudentLearningData(Map<String, Object> paramData);

    //tab info 생성
    int createTabInfo(Map<String, Object> innerParam) throws Exception;

    Map<String, Object> getReTiMap(Map<String, Object> paramData) throws Exception;

    Map<String, Object> getTrgtAt(Map<String, Object> paramData) throws Exception;

    // ai 맞춤 학습 미리보기
    // 공통 사용
    List<Map<String, Object>> getArticlePreviewInfo(List<String> articleIdList);

    // 모두 같은 문제
    List<AiArticleVO> selectAiCustomLearningArticles(Map<String, Object> paramData) throws Exception;

    // 개인별 맞춤 학습
    List<Map<String, Object>> findStntIncorrentList(Map innerParam) throws Exception;

    AiArticleVO getSimilarArticle(Map innerParam) throws Exception;

    AiArticleVO getSimilarArticleByDifficulty(Map innerParam) throws Exception;

    AiArticleVO getAddArticle_first(Map innerParam) throws Exception;

    AiArticleVO getAddArticle_second(Map innerParam) throws Exception;


    // ai 맞춤 학습 출제
    // 모두 같은 문제
    List<AiArticleVO> selectAiCustomLearningArticlesInfo(Map<String, Object> innerParam) throws Exception;

    // 개인별 맞춤 학습
    List<AiArticleVO> getSimilarArticlesInfo(List<String> articleList) throws Exception;

    List<String> selectBulkTaskMqTarget();
}
