package com.visang.aidt.lms.api.dashboard.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.dashboard.mapper
 * fileName : EtcMapper
 * USER : kimjh21
 * date : 2024-02-29
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-02-29         kimjh21          최초 생성
 */
@Mapper
public interface EtcMapper {

    List<Map<String, Object>> getMainGoalInfo(Map<String, Object> param);

    List<Map<String, Object>> selectTcClaMbInfo(Map<String, Object> param);

    List<Map<String, Object>> getGoalDetailOne(int goalId);

    List<Map<String, Object>> getCommonGoalList(Map<String, Object> paramMap);

    List<Integer> getInsertMainGoalId(Map<String, Object> paramMap);

    List<Integer> getCommonGoalIdxList(Map<String, Object> param);

    void insertGoalInfo(Map<String, Object> param);

    int selectGoalExists(Map<String, Object> param);

    void insertGoalDetail(@Param("goalId") int goalId, @Param("order") int order, @Param("rectAt") String rectAt);

    List<Integer> selectGoalCrculIds(Map<String, Object> param);

    List<String> selectGoalStdtList(Map<String, Object> param);

    List<Map> getTeacherGoalInfoList(PagingParam<?> pagingParam);

    void updateGoalTchChkAt(Map<String, Object> param);

    void updateCommonGoalReset(Map<String, Object> param);

    void updateGoalReset(Map<String, Object> param);

    Map<String, Object> selectGoalResetTarget(Map<String, Object> param);

    void updateCommonGoalNm(Map<String, Object> param);

    void updateGoalTchSet(Map<String, Object> param);

    void updateGoalDetail(Map<String, Object> param);

    int updateGoalStSet(Map<String, Object> param);

    int updateGoalAlarm(Map<String, Object> param);

    // 교사가 속해있는 클래스 목록 조회
    List<Map<String, Object>> selectTeacherClassList(Map<String,Object> param);

    // 학생) 컨디션, 에너지에 따른 목록 조회
    List<Map<String, Object>> getConditionList(Map<String, Object> param);

    // 학생) 오늘의 기분 학생 insert
    int insertConditionDetail(Map<String, Object> param);

    // 학생) 학생의 오늘의 기분 단건 조회
    Map<String, Object> conditionInfo(Map<String, Object> param);

    // 교사) 클래스에 속해있는 모든 학생들의 기분 조회
    List<Map<String, Object>> conditionUserList(Map<String, Object> param);

    // 교사) 클래스에 속해있는 모든 학생들의 기분 조회(대시보드용)
    List<Map<String, Object>> conditionDashBoardUserList(Map<String, Object> param);

    Map<String, Object> getTdyMdInfoDashBoard(Map<String, Object> param);

    // 오늘의기분 id별 색상, 코드명 가져오기
    List<Map<String, Object>> getTodayInfo();

    // 교사) 오늘 날짜 기준으로 업데이트 된 오늘의 기분 개수 조회
    int conditionUserListSize(Map<String, Object> param);
    int conditionLvUserListSize(Map<String, Object> param);

    // 교사) 목표설정 수정된 개수 조회(교사가 대시보드 확인 시 초기화)
    int selectUserGoalCnt(Map<String, Object> param);

    // 교사) 학생 오늘의 기분 상세
    List<Map> conditionUserDetail(PagingParam<?> pagingParam);

    void conditionReset(Map<String, Object> param);

    /* META 자기조절학습 관련 */
    // 교사) 최근 META 자기조절학습 시작 여부(대시보드)
    String selectMetaStartYn(Map<String, Object> param);

    // 교사) META 자기조절학습 목록 전달
    List<Map<String, Object>> selectTcDgnssInfo(Map<String, Object> param);

    void deleteTargetStListResultInfo(List<String> stList);

    void deleteTargetStListAnswer(List<String> stList);

    // 교사) META 자기조절학습 마스터 INSERT
    void insertDgnssInfo(Map<String, Object> param);

    // 교사) META 자기조절학습 마스터 진단 상태 변경
    void updateDgnssInfo(Map<String, Object> param);

    int selectActvStdtCnt(Map<String, Object> param);

    // 비바클래스 동일 학년/반의 정보가 있는지 확인
    int selectExistsSameClaId(Map<String, Object> param);

    // 교사) UPDATE 할 META 자기조절학습 Info 테이블 검색
    Map<String, Object> selectTcDgnssInfoOne(Map<String, Object> param);

    Map<String, Object> selectTcDgnssInfoOneFirst(Map<String, Object> param);

    // 교사) UPDATE 할 META 자기조절학습 Info 테이블 검색(dgnssId로 탐색)
    Map<String, Object> selectTcDgnssInfoOneWithDgnssId(Map<String, Object> param);

    // 교사) 학생들의 OMR 카드 정보 가져오기
    List<LinkedHashMap<String, Object>> selectStOmrInfo(Map<String, Object> param);

    // 교사) 제출한 모든 학생의 이름 가져오기
    List<String> selectSubmitStList(Map<String, Object> param);

    List<String> selectDgnssStdtList(Map<String, Object> param);

    // 교사) META 자기조절학습 result_info에 insert할 학생 ID 탐색
    List<String> selectTargetStList(Map<String, Object> param);

    String selectTcId(Map<String, Object> param);

    // 교사) META 자기조절학습 학생 개인 답안(OMR) insert
    int insertDgnssOmr(Map<String, Object> param);

    // 교사) META 자기조절학습 result_info에 데이터 insert
    void insertDgnssResult(Map<String, Object> param);

    // dgnss_result_info에 새로운 omrIdx 할당
    void updateDgnssResult(Map<String, Object> param);

    // 과거 omrIdx 제거(Map으로)
    void deleteDgnssOmr(Map<String, Object> param);

    // 과거 omrIdx 제거(idx로)
    void deleteDgnssOmrIdx(int omrIdx);

    // 교사) META 자기조절학습 answer에 데이터 insert
    void insertDgnssAnswer(Map<String, Object> param);

    // 교사) META 자기조절학습 취소(마스터테이블 삭제)
    void deleteTcDgnssInfo(Map<String, Object> param);

    // 교사) META 자기조절학습 취소(상세테이블 삭제)
    void deleteTcDgnssResultInfo(Map<String, Object> param);

    void deleteTcDgnssAnswer(Map<String, Object> param);

    List<Integer> selectOmrIdxList(Map<String, Object> param);

    // 교사) META 자기조절학습 미제출 학생 전달
    List<Map<String, Object>> selectTcDgnssNotSubmStList(Map<String, Object> param);

    // 학생) META 자기조절학습 목록 조회
    List<Map<String, Object>> selectStDgnssList(Map<String, Object> param);

    // 학생) META 자기조절학습 문제 조회
    List<Map> selectStQuesList(PagingParam<?> pagingParam);

    List<Map<String, Object>> selectStQuesListOrigin(Map<String, Object> param);

    List<String> selectAllStdtList(Map<String, Object> param);

    void saveDgnssTextSave(Map<String, Object> param);

    // 학생) OMR 조회(resultId로 조회)
    LinkedHashMap<String, Object> selectStDgnssOmr(Map<String, Object> param);

    String selectPaperIdxFromResultId(Map<String, Object> param);

    // 학생) META 자기조절학습 응시 시작
    void updateStStart(Map<String, Object> param);

    // 학생) 답 입력
    int updateStAnswer(Map<String, Object> param);

    // 학생) META 새로 시작하기(omrIdx값 가져오기)
    Map<String, Object> selectPastOmrInfo(Map<String, Object> param);

    // 학생) 자기진단 제출
    void updateStSubmit(int dgnssResultId);

    // 선생, 학생) TB_DGNSS_ANSWER 테이블에 ANSWER 컬럼 업데이트
    void updateDgnssAnswerJson(@Param("dgnssResultId") int dgnssResultId, @Param("sameAnswerCheck")  boolean sameAnswerCheck);

    void updateDgnssAnswerJsonLearn(@Param("dgnssResultId") int dgnssResultId, @Param("sameAnswerCheck")  boolean sameAnswerCheck);

    // 선생, 학생) 프로시저 실행을 위한 ANSWER_IDX 값 가져오기
    int selectAnswerIdx(int dgnssResultId);

    // 선생, 학생) 프로시저 실행
    void callProcMark(int answerIdx);

    // 선생) 상담 및 지도가 필요한 학생(신뢰도)
    Map<String, Object> selectTcTrustInfo(Map<String, Object> param);

    Map<String, Object> selectLernEtcInfo(Map<String, Object> param);

    // 선생) 기타 전략 상담 및 지도가 필요한 학생
    Map<String, Object> selectTcEtcInfo(Map<String, Object> param);

    // 선생) 대시보드 - 학생 목록 - 신뢰도 및 학습현황
    List<Map<String, Object>> selectDgnssAnswerReliability(Map<String, Object> param);

    // 선생) 대시보드 - 학생 목록 - 동기 전략
    List<Map<String, Object>> selectDgnssAnswerReportMotivate(Map<String, Object> param);

    // 선생) 대시보드 - 학생 목록 - 인지 전략
    List<Map<String, Object>> selectDgnssAnswerReportRecognition(Map<String, Object> param);

    // 선생) 대시보드 - 학생 목록 - 행동 전략
    List<Map<String, Object>> selectDgnssAnswerReportBehavior(Map<String, Object> param);

    // 선생) dgnssId 탐색
    List<Integer> selectDgnssIdxList(Map<String, Object> param);

    // 선생) 회차별 데이터 도출
    List<Map<String, Object>> selectClassTotalReport(Map<String, Object> param);

    // 학생) 대시보드 학생의 정보
    Map<String, Object> selectStInfo(Map<String, Object> param);

    List<Map<String, Object>> selectStLernAnalysis(Map<String, Object> param);

    String selectFirstDgnssResultId(Map<String, Object> param);

    // 학생) 학생 분석
    String selectStAnalysis(Map<String, Object> param);

    // 학생) 강점 요인
    List<String> selectStrFactor(Map<String, Object> param);

    // 학생) 약점 요인
    List<String> selectWeakFactor(Map<String, Object> param);

    Map<String, Object> selectTcUserInfo(Map<String, Object> param);

    Map<String, Object> selectStUserInfo(Map<String, Object> param);

    List<Map<String, Object>> getDgnssReport(Map<String, Object> param);

    List<Map<String, Object>> getDgnssReportStudy(Map<String, Object> param);

    void updateFileUrl(Map<String, Object> param);

    // 교사용 분석표 용(자기조절)
    String getDgnssFirstTest(Map<String, Object> param);
    List<Map<String, Object>> getDgnssReportLS(Map<String, Object> param);
    List<Map<String, Object>> getDgnssReportSection(Map<String, Object> param);
    List<Map<String, Object>> getDgnssReportValidity(Map<String, Object> param);
    List<Map<String, Object>> getDgnssReportMem(Map<String, Object> param);
    List<Map<String, Object>> getDgnssReportStatByTest(Map<String, Object> param);

    void updateFileUrlTch(Map<String, Object> param);

    List<Map<String, Object>> selectClassLernReport(Map<String, Object> param);
    List<Map<String, Object>> selectLernType2(Map<String, Object> param);
    List<Map<String, Object>> selectLernType3(Map<String, Object> param);
    List<Map<String, Object>> selectLernType4(Map<String, Object> param);
    List<Map<String, Object>> selectLernType5(Map<String, Object> param);
    List<Map<String, Object>> selectLernType6(Map<String, Object> param);

    List<String> selectDgnssStdtListFromDgnssId(Map<String, Object> param);
    void updateDgnssStatus(Map<String, Object> param);
    Map<String, Object> selectTcDgnssDetailInfo(Map<String, Object> param);
    List<Map<String, Object>> selectStTotalReport(Map<String, Object> param);
    void updateMonitFile(Map<String, Object> param);
    void updateNullMonitFile(Map<String, Object> param);

    void updateUserInfo(Map<String, Object> param);
    void updateUserGender(Map<String, Object> param);
    void insertVivaClassUser(Map<String, Object> param);
    void insertVivaClassStdtRegInfo(Map<String, Object> param);
    void insertVivaClassTcClaMbInfo(Map<String, Object> param);

    List<Map<String, Object>> selectDgnssEndTargetList();
    List<Map<String, Object>> selectDgnssEnd3DaysLeftList();
    List<Map<String, Object>> MakePdfTargetAnswerIdxList(Map<String, Object> param);

    int tcDgnssFileReset();
    int stDgnssFileReset();
    String selectStdtIdFromDgnssResultId(@Param("dgnssResultId") int dgnssResultId);
    List<Integer> selectDgnssIdxListFromTcIdList(List<String> tcUserList);
    void deleteTcDgnssAnswerReport(List<Integer> dgnssIdList);
    void deleteTcDgnssAnswerWithDgnssIdList(List<Integer> dgnssIdList);
    void deleteTcDgnssOmrWithDgnssIfList(List<Integer> dgnssIdList);
    void deleteTcDgnssResultInfoWithDgnssIdList(List<Integer> dgnssIdList);
    void deleteTcDgnssInfoWithDgnssIdList(List<Integer> dgnssIdList);

    String selectClaId(Map<String, Object> param) throws Exception;

    List<String> selectTcClaStList(Map<String, Object> param) throws Exception;

    void insertUserInfo(Map<String, Object> param) throws Exception;

    int selectUserExists(Map<String, Object> param) throws Exception;

    String selectUserActiveYn(Map<String, Object> param) throws Exception;

    void insertTcClaInfo(Map<String, Object> param) throws Exception;

    void insertTcRegInfo(Map<String, Object> param) throws Exception;

    void insertStdtRegInfo(Map<String, Object> param) throws Exception;

    void insertTcClaMbInfo(Map<String, Object> param) throws Exception;

    void insertUserBulk(List<Map<String, Object>> paramMap) throws Exception;

    void insertStdtRegInfoBulk(List<Map<String, Object>> paramMap) throws Exception;

    void insertTcClaInfoBulk(List<Map<String, Object>> paramMap) throws Exception;

    void upsertTcClaUserInfo(List<Map<String, Object>> paramMap) throws Exception;

    void insertTcClaMbInfoBulk(List<Map<String, Object>> paramMap) throws Exception;

    void updateActvTnBulk(List<Map<String, Object>> paramMap) throws Exception;
}
