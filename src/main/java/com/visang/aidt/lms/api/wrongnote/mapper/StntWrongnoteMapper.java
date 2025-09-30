package com.visang.aidt.lms.api.wrongnote.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.wrongnote.mapper
 * fileName : StntWrongnoteMapper
 * USER : lsm
 * date : 2024-02-27
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-02-27         lsm          최초 생성
 */
@Mapper
public interface StntWrongnoteMapper {
    // /stnt/wrong-note/list
    List<Map> selectWonAswNoteList(PagingParam<?> paramData) throws Exception;
    Map<String, Object> selectWonAnwClsfCdNm(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectStntWrongnoteTaskDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectStntWrongnoteEvlDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectStntWrongnoteStdNm(Map<String, Object> paramData) throws Exception;
    // /stnt/wrong-note/won-asw/list
    List<Map> selectStntResultInfo(Map<String, Object> paramData) throws Exception;
    List<Map> selectWonTagInfo(Map<String, Object> paramData) throws Exception;

    // /stnt/wrong-note/won-asw/tag/save
    int updateWonAwsNote(Map<String, Object> paramData) throws Exception;
    int insertWonAwsNote(Map<String, Object> paramData) throws Exception;
    int deleteWonAwsNote(Map<String, Object> paramData) throws Exception;

    Map<String, Object> selectWonAwsChk(Map<String, Object> paramData) throws Exception;

    int insertStntWrongnoteTaskId(Map<String, Object> paramData) throws Exception;

    int deleteStntWrongnoteEvlId(Map<String, Object> paramData) throws Exception;

    int insertStntWrongnoteEvlId(Map<String, Object> paramData) throws Exception;

    List<Map> findWrongnoteTaskInfo(Map<String, Object> paramData) throws Exception;

    List<Map>  findWrongnoteEvlInfo(Map<String, Object> paramData) throws Exception;

    /**
     * 평가 공개후 배점 수정반영시 기존 오답노트 모두 삭제
     * @param paramData
     * @return 삭제건수
     */
    int deleteReportEvalWrongnote(Map<String, Object> paramData) throws Exception;

    /**
     * 평가 공개후 배점 수정반영시 재등록되는 오답노트
     * @param paramData
     * @return 등록건수
     */
    int insertReportEvalWrongnote(Map<String, Object> paramData) throws Exception;

    int deleteReportHomewkWrongnote(Map<String, Object> paramData) throws Exception;

    int insertReportHomewkWrongnote(Map<String, Object> paramData) throws Exception;

    int insertStntWrongnoteId(Map<String, Object> paramData) throws Exception;

    List<Map>  findWrongnoteSelfLrnInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findWrongnoteWonAnsNm1(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findWrongnoteWonAnsNm2(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findWrongnoteWonAnsNm3(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findWrongnoteWonAnsNm4(Map<String, Object> paramData) throws Exception;

    String selectWonAnwNm(Map<String, Object> paramData) throws Exception;

    int findWonAswNoteCount(Map<String, Object> paramData) throws Exception;

    int createWonAswNote(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기 : info 테이블 생성 (학습지 정보 생성)
    int createStdWonAswInfo(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기 : detail 테이블 생성 (문항 생성)
    int createStdWonAswDetail(@Param("stdWonAnwId") String stdWonAnwId, @Param("wonAnwInfo") List<Map<String, Object>> paramDataList) throws Exception;

    // 오답노트 다시풀기 : 시작하기
    List<Map>  selectStdWonAswRetry(@Param("stdWonAnwId") String stdWonAnwId) throws Exception;

    // 오답노트 다시풀기 : 제출여부 확인
    Map<String, Object> checkSubmitYn(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기 : 아티클 저장
    int saveWrongNoteRetry(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기 : 전체 문항 제출 여부 확인, 젠체 문항을 풀었을 경우  info 테이블에 최종 제출로 변경
    int saveWrongNoteRetryFinalData(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기 : 제출하기 Detail 완료처리
    int submitWrongNoteRetryDetail(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기 : 제출하기 info 완료처리
    int submitWrongNoteRetrylInfo(Map<String, Object> paramData) throws Exception;

    // 문항(아티클+서브)별 정오표
    List<Map> getHistErra(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기의 오답노트 만들기
    int createWonAswForRetry(Map<String, Object> paramData) throws Exception;

    int findBrandId(int textbkId) throws Exception;

    int getTextbkId(Map<String, Object> paramData) throws Exception;

    // 오답노트 진입시 수행
    // 오답노트 다시풀기 초기화 : 오답노트 진입시 , 다시풀기 submit N 인 데이터 초기화 (detail)
    int deleteWrongNoteRetryDetail(Map<String, Object> paramData) throws Exception;

    // 오답노트 다시풀기 초기화 : 오답노트 진입시 , 다시풀기 submit N 인 데이터 초기화 (info)
    int deleteWrongNoteRetryInfo(Map<String, Object> paramData) throws Exception;

    // 오답노트 통계 데이터
    Map<String, Object> getWrongNoteStatis(Map<String, Object> paramData) throws Exception;

    // 자주틀린 개념 목록 탑 3
    List<Map> frequentlyMisunderstoodTConcept(Map<String, Object> paramData) throws Exception;

    List<Map> frequentlyMisunderstoodTConceptForEng(Map<String, Object> paramData) throws Exception;

    // 자주틀린 사유 목록 탑 3
    List<Map> frequentlyErrorCauses(Map<String, Object> paramData) throws Exception;

    List<Map> frequentlyErrorCausesForEng(Map<String, Object> paramData) throws Exception;

    // 리워드 적립을 위한 errata조회
    Map<String, Object> getWrongNoteRetryRwd(Map<String, Object> paramData) throws Exception;

    int getCurrentWonAnwClsfCdOfWrongNote(Map<String, Object> paramData) throws Exception;

    String getCurrentWrongNoteName(Map<String, Object> paramData) throws Exception;

    Integer getMaxRetryNumber(Map<String, Object> paramData) throws Exception;

    String getCurrentTabId(Map<String, Object> paramData) throws Exception;

    int getTextbkId2(Map<String, Object> paramData);
}
