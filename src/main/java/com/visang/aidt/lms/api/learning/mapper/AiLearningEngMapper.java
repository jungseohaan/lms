package com.visang.aidt.lms.api.learning.mapper;

import com.visang.aidt.lms.api.learning.vo.AiArticleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AiLearningEngMapper {
    List<AiArticleVO> selectAiCustomLearningCommonArticles(Map<String, Object> paramData) throws Exception;
    List<AiArticleVO> selectAiCustomEachLearningArticles(Map<String, Object> paramData, List<Map<String, Object>> paramCountList, List<AiArticleVO> totalArticleList) throws Exception;
    int findUsdAchScrCnt(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> findStntList(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> findCurriUnit2List(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> findUsdAchScrList(Map<String, Object> paramData, String strStdtId, List<String> curriUnit2Val) throws Exception;

    List<Map<String, Object>> findUsdAchScrListGrammarOrVocabulary(Map<String, Object> paramData, String strStdtId, List<String> curriUnit2Val) throws Exception;

    List<Map<String, Object>> findUsdAchScrListAll(Map<String, Object> paramData, List<String> curriUnit2Val) throws Exception;
    int createStdDtaResultInfoForAiCustomLearning(Map<String, Object> innerParam) throws Exception;
    int createStdDtaResultInfoForAiCustomLearningEach(Map<String, Object> innerParam) throws Exception;
    int createStdDtaResultDetailForAiCustomLearning(Map<String, Object> innerParam) throws Exception;
    int createTaskResultInfoForAiCustomLearning(Map<String, Object> param) throws Exception;
    int createTaskResultInfoForAiCustomLearningEach(Map<String, Object> param) throws Exception;
    int createTaskResultDetailForAiCustomLearning(Map<String, Object> param) throws Exception;
    Map<String, Object> findCurriculumMap(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTargetWrongEvlList(Map<String, Object> paramData);
    Map<String, Object> findTargetWrongEvlListTest(Map<String, Object> paramData);
    List<Map<String,Object>> findTargetEvlList(Map<String, Object> paramData) throws Exception;
    List<Map<String,Object>> findTargetTaskList(Map<String, Object> paramData) throws Exception;
    List<AiArticleVO> findAutoCreateAiLearningEvlStep1(Map<String, Object> paramData) throws Exception;
    List<AiArticleVO> findAutoCreateAiLearningTaskStep1(Map<String, Object> paramData) throws Exception;
    List<Map> findAutoCreateAiLearningEvlStep2(Map<String, Object> paramData) throws Exception;
    AiArticleVO findAutoCreateAiLearningEvlStep3(Map<String, Object> innerParam) throws Exception;

    String getEamScp(Map<String, Object> innerParam) throws Exception;

    List<Map<String, Object>> findUsdAchScrListAll2(Map<String, Object> paramData, List<String> curriUnit2Val) throws Exception;

    //AI맞춤학습-대상학생목록조회
    List<Map<String, Object>> findAiCustomLrnCreateEngTargetList(Map<String, Object> paramData) throws Exception;

    String findTabNmForAiCustomLearning(Map<String, Object> innerParam) throws Exception;

    //tab info 생성
    int createTabInfo(Map<String, Object> innerParam) throws Exception;

    List<String> selectUsdAchId(Map<String, Object> paramData);

    List<AiArticleVO> selectAiCustomLearningCommonArticlesInfo(Map<String, Object> paramData);
}
