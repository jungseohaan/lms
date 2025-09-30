package com.visang.aidt.lms.api.report.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface WrongNoteReportMapper {

    /*************************
    ** 교사 : 학급 전체 리포트  **
    *************************/
    //선택한 날짜의 우리반 총 오답 문항 수
    Map<String, Object> getClaWrongNoteCnt(Map<String, Object> paramData) throws Exception;

    //지정 날짜 구간 복습률
    Map<String, Object> getClaWrongNoteRetryRate(Map<String, Object> paramData) throws Exception;

    //지정 날짜 대비 복습률 : 전일, 전주, 전월
    Map<String, Object> getClaWrongNoteRetryRateComparedData(Map<String, Object> paramData) throws Exception;

    //오답이 많은 순
    List<Map> getClaWrongNoteStntList(Map<String, Object> paramData) throws Exception;

    //전체 복습률이 낮은 학생 순
    List<Map> getClaWrongNoteRetryRateStntList(Map<String, Object> paramData) throws Exception;

    /*************************
     ** 교사 : 학급 전체 리포트 **
     ** 학생별 통계 데이터      **
     *************************/

    //클래스 소속 학생 목록 가져오기
    List<Map> getClaStntList(Map<String, Object> paramData) throws Exception;

    //오답 노트 수 , 전체 복습률 LOOP
    Map<String, Object> getWrongNoteStntData(Map<String, Object> paramData) throws Exception;

    //가장 많이 틀린 이유 탑3 LOOP
    List<Map> getStntWrongReasonTop3List(Map<String, Object> paramData) throws Exception;

    /*************************
     ** 교사/학생 : 학생별 리포트**
     *************************/
    //선택한 날짜의 총 오답 문항 수
    Map<String, Object> getStntWrongNoteCnt(Map<String, Object> paramData) throws Exception;

    //단원별 오답 수 : 탑 3
    List<Map> getStntUnitWrongCntTop3List(Map<String, Object> paramData) throws Exception;

    //복습률 : 지정날짜 복습률 */
    Map<String, Object> getStntWrongRetryRate(Map<String, Object> paramData) throws Exception;

    //복습률 : 지정날짜 복습률 대비 지난주/일/월 통계데이터 */
    Map<String, Object> getStntWrongRetryRateComparedData(Map<String, Object> paramData) throws Exception;

    //오답 노트 미완료 : 모두 노출
    List<Map> getStntWrongNoteIncompleteList(Map<String, Object> paramData) throws Exception;

    //오답 틀린 이유 전체 : 오답 이유 분석
    List<Map> getStntWrongReasonList(Map<String, Object> paramData) throws Exception;
    List<Map> getStntWrongReasonListForEng(Map<String, Object> paramData) throws Exception;

    /*************************
     ** 교사/학생 : 학생별 리포트**
     ** 특정학생 오답노트결과보기 **
     ** 기프 전체, 완료, 미완료 **
     *************************/

    /*************************
     ** 교사 : 한마디  **
     *************************/
    //교사한마디 호출 */
    Map<String, Object> getTchComment(Map<String, Object> paramData) throws Exception;

    //교사한마디 수정 개별*/
    int modTchComment(Map<String, Object> paramData) throws Exception;

    //교사한마디 저장 개별*/
    int regTchComment(Map<String, Object> paramData) throws Exception;

    //교사한마디 저장 일괄을 위한 초기화
    int delAllTchCommentForInit(Map<String, Object> paramData) throws Exception;

    //교사한마디 저장 일괄
    int regAllTchComment(Map<String, Object> paramData) throws Exception;

    Map<String, Object> getTchRptChkAt(Map<String, Object> paramData) throws Exception;

    //리포트 > 교사 읽음 처리
    int modReadY(Map<String, Object> paramData) throws Exception;

}
