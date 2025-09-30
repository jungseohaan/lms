package com.visang.aidt.lms.api.kafka.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.keris.mapper
 * fileName : KerisApiMapper
 * USER : user
 * date : 2024-05-16
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-05-16         user          최초 생성
 */
@Mapper
public interface KafkaEngMapper {

    String getBrandId(Map<String, Object> data) throws Exception;

    // 배치 전 데이터 삭제
    int deleteEngStdUsdTarget_1(Map<String, Object> paramData);
    int deleteEngStdUsdTarget_2(Map<String, Object> paramData);
    int deleteEngStdUsdTarget_3(Map<String, Object> paramData);
    int deleteEngStdUsdTarget_4(Map<String, Object> paramData);

    int deleteMvEngStdUsdTarget_1(Map<String, Object> paramData);
    int deleteMvEngStdUsdTarget_2(Map<String, Object> paramData);
    int deleteMvEngStdUsdTarget_3(Map<String, Object> paramData);
    int deleteMvEngStdUsdTarget_4(Map<String, Object> paramData);

    List<Map<String, Object>> findStdUsdTargetList(Map<String, Object> paramData);

    int insertEngUsdAchSrc2Info(Map<String, Object> paramData);
    int insertEngUsdAchSrc2Detail(Map<String, Object> paramData);
    int insertEngUsdAchSrc2Kwg(Map<String, Object> paramData);
    int updateEngUsdAchSrc2Info(Map<String, Object> paramData);
    int insertAchCacSrcInfo(Map<String, Object> paramData);

    int finalTableChk(Map<String, Object> paramData);

    int finalTableEngChk(Map<String, Object> paramData);

    void finalTableExistsYnUpdate(Map<String, Object> paramData);
    void finalTableExistsYnUpdate_1(Map<String, Object> paramData);
    void finalTableExistsYnUpdate_2(Map<String, Object> paramData);
    void finalTableExistsYnUpdate_3(Map<String, Object> paramData);
    void finalTableExistsYnUpdate_4(Map<String, Object> paramData);

    String getLastLearningDate(Map<String, Object> paramData);

    List<Map> selectTchMdulQstnResetSDRHist(Map<String, Object> paramData) throws Exception;
    List<Map> selectEvaluationHistoryRecord(Map<String, Object> paramData) throws Exception;

    List<Map> selectMvLmsUsdAchSrc2Info(Map<String, Object> paramData) throws Exception;
    List<Map> selectMvLmsUsdCacSrcInfo(Map<String, Object> paramData) throws Exception;

    List<Map> selectLoopMvLmsUsdAchSrc2Info(Map<String, Object> paramData) throws Exception;
    List<Map> selectLoopMvLmsUsdCacSrcInfo(Map<String, Object> paramData) throws Exception;

    List<Map> getAticleCheck(Map<String, Object> paramData) throws Exception;

    Map getTextbook(String paramData) throws Exception;
    void slfPerDataSetting(Map<String,Object> paramData) throws Exception;


}