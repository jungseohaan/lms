package com.visang.aidt.lms.api.scenario.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScenarioMapper {

    // 평가 시나리오
    int modifyEvalSubmAtERD(Map<String, Object> paramData) throws Exception;
    int modifyEvalSubmAtERI(Map<String, Object> paramData) throws Exception;
    int modifyStntEvalSubmitResultDetail(Map<String, Object> paramData) throws Exception;
    int modifyStntEvalSubmitResultInfo(Map<String, Object> paramData) throws Exception;
    void updateEvalUptDt(Map<String, Object> paramData) throws Exception;
    void updateEvalEndUptDt(Map<String, Object> paramData) throws Exception;

    // 과제 시나리오
    int modifyStntMdulQstnSave(Map<String, Object> paramData) throws Exception;

    // 자기주도학습(수학, 영어)
    int saveStntSelfLrnStdEnd(Map<String, Object> paramData) throws Exception;
    void updateSlfLrnUptDt(Map<String, Object> paramData) throws Exception;

    List<Map> selectSelfLrnEngArticles(Map<String, Object> paramData) throws Exception;
    List<Map> selectStntSelfLrnReceiveEng(Map<String, Object> paramData) throws Exception;

    // 자기주도학습(선택학습) 문항 추천 목록 조회 (생성 관련) - 단답형 제외 조건 추가
    List<Map> selectStntSelfLrnRecModuleList(Map<String, Object> paramData) throws Exception;
    // 자기주도학습(선택학습) 유사문항 받기 - 단답형 제외 조건 추가
    List<Map> selectStntSelfLrnRecvModuleList(Object paramData) throws Exception;
    // 평가목록조회
    List<Map> findStntEvalListEvalInfo(Map<String, Object> paramData) throws Exception;
    // 자기주도학습 생성을 위한 stdUsdId
    Map<String, Object> findStdUsdId(Map<String, Object> paramData) throws Exception;
}
