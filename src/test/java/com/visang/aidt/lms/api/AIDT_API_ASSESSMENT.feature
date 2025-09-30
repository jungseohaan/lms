Feature: 비상교육 AIDT API - ASSESSMENT

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"
#    * url "https://t-class.aidtclass.com"


# ------ StntEvalController ------

  @StntEvalController
  Scenario Outline: 평가 목록 조회
    Given path '/stnt/eval/list'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '<textbookId>'
    And param condition = ''
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And match response.resultData.evalCheck.plnEvlCnt == <plnEvlCnt>
    And match response.resultData.evalCheck.pgEvlCnt == <pgEvlCnt>
    And match response.resultData.evalCheck.cpEvlCnt == <cpEvlCnt>
    And match response.resultData.page.size == <size>
    And match response.resultData.page.totalElements == <totalElements>
    And match response.resultData.page.totalPages == <totalPages>
    And match response.resultData.page.number == <number>
    Examples:
      | textbookId | size | totalElements | totalPages | number | plnEvlCnt | pgEvlCnt | cpEvlCnt |
      | 1          | 10    | 41             | 5          | 0      | 16        | 2          | 23         |
      | 2          | 0    | 0             | 0          | 0      | 16          | 2         | 23         |


  @StntEvalController
  Scenario: 평가 정보 수정(시작하기)
    Given path '/stnt/eval/start'
    And json jsonBody = "{ evlId: 2388, userId: 'vsstu386' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntEvalController
  Scenario: 평가 자료 제출하기
    Given path '/stnt/eval/submit'
    And json jsonBody = "{ evlId: 2388, userId: 'vsstu386', textbookId: 1, submAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntEvalController
  Scenario Outline: 평가 정보 조회
    Given path '/stnt/eval/info'
    And param evlId = '<evlId>'
    And param userId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And match response.resultCode == <status>
    And match response.paramData.evlId == '<evlId>'
    And match response.resultData.eakSttsCd == <eakSttsCd>
    And match response.resultData.eakSttsNm == '<eakSttsNm>'
    Examples:
      | status | evlId | eakSttsCd | eakSttsNm |
      | 200    | 2386     | 1         | 응시 전      |
      | 200    | 2388     | 5         | 채점완료     |


  @StntEvalController @키변경
  Scenario: (공통) 응시(article) 자동저장
    Given path '/stnt/eval/save'
    And json jsonBody = "{ evlId: 2388, userId: 'vsstu386', evlResultId: 1, evlIemId: 1, errata: 1, subMitAnw: 1, subMitAnwUrl: '', evlTime: '', hntUseAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntEvalController
  Scenario Outline: 평가 결과보기
    Given path '/stnt/eval/result'
    And param evlId = '<evlId>'
    And param userId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == <status>
    And match response.resultData.stntResultInfo.evlResultScr == <evlResultScr>
    And match response.resultData.stntResultInfo.evlTotalScr == <evlTotalScr>
    And match response.resultData.stntResultInfo.evlResult == <evlResult>
    Examples:
      | status | evlId | evlResultScr | evlTotalScr | evlResult |
      | 200    | 2386     | null         | 15.0        | '하'      |
      | 200    | 2388     | null         | 5.0       | null       |


  @StntEvalController @키변경
  Scenario Outline: 모듈 평가 결과 조회
    Given path '/stnt/eval/result-info'
    And param evlResultId = '<evlResultId>'
    And param evlIemId = '<evlIemId>'
    And param subId = '<subId>'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And match response.paramData.evlResultId == '<evlResultId>'
    And match response.paramData.evlIemId == '<evlIemId>'
    And match response.paramData.subId == '<subId>'
    And match response.resultData.errata == <errata>
    And match response.resultData.errataNm == <errataNm>
    And match response.resultData.eakAt == <eakAt>
    Examples:
      | evlResultId | evlIemId | subId | errata | errataNm | eakAt |
      | 151           | 2471        | 0     | null   | null     | 'N'   |
      | 463           | 2471        | 0     | 2   | '오답'     | 'Y'   |


  @StntEvalController @키변경
  Scenario: (공통) 응시(article) 재확인 횟수저장
    Given path '/stnt/eval/recheck'
    And json jsonBody = "{ evlId: 2388, userId: 'vsstu386', evlResultId: '', evlIemId: 6, subId: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntEvalController @키변경
  Scenario: 응시답안 초기화
    Given path '/stnt/eval/init'
    And json jsonBody = "{ evlId: 2388, userId: 'vsstu386', evlResultId: 1, evlIemId: 1, subId: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntEvalController @키변경
  Scenario: 과제 ai tutor 대화 내용 저장
    Given path '/stnt/evl/aitutor/submit/chat'
    And json jsonBody = "{ taskId: 11, userId: 'student41', evlResultId: 42, evlIemId: 2580, chatOrder: 1, chatType: 'auto', aiCall: '연관된 개념을 알려줘', aiReturn: '튜터러스 랩스 api return 값' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ StntReportEvalController ------

  @StntReportEvalController
  Scenario Outline: 평가 리포트 목록조회
    Given path '/stnt/report/eval/list'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param stntId = 'vsstu386'
    And param condition = ''
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And print response.resultData.evalList.length
    And match response.resultData.evalList[<idx>].no == <no>
    And match response.resultData.evalList[<idx>].id == <id>
    And match response.resultData.evalList[<idx>].eamMth == <eamMth>
    And match response.resultData.evalList[<idx>].evlSttsCd == <evlSttsCd>
    And match response.resultData.evalList[<idx>].evlSttsNm == <evlSttsNm>
    And match response.resultData.evalList[<idx>].eakSttsCd == <eakSttsCd>
    And match response.resultData.evalList[<idx>].eakSttsNm == <eakSttsNm>
    And match response.resultData.evalList[<idx>].submAt == <submAt>
    Examples:
      | idx | no | id | eamMth | evlSttsCd | evlSttsNm | eakSttsCd | eakSttsNm | submAt |
      | 0   | 9  | 4158  | 3      | 5         | '채점완료'    | 5         | '채점완료'    | 'Y'    |
      | 1   | 8  | 3923  | 3      | 5         | '채점완료'      | 5         | '채점완료'    | 'Y'    |
      | 5   | 4  | 2327  | 3      | 3         | '완료'    | 3         | '응시완료'    | 'Y'    |


  @StntReportEvalController @키변경
  Scenario: 평가 결과 조회(자세히 보기)
    Given path '/stnt/report/eval/result/detail'
    And param evlId = '2386'
    And param stntId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And match response.paramData.evlId == '2386'
    And match response.paramData.stntId == 'vsstu386'
    And match response.resultData.id == 2386
    And match response.resultData.setsId == "#number"
    And match response.resultData.mdulTotScr == 15.0


  @StntReportEvalController
  Scenario: 평가 결과 조회(결과보기) Header
    Given path '/stnt/report/eval/result/header'
    And param evlId = '2388'
    And param stntId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And match response.paramData.evlId == '2388'
    And match response.paramData.stntId == 'vsstu386'
    And match response.resultData.id == 2388
    And match response.resultData.evlNm == '[진단]선다형테스트'
    And match response.resultData.resultTypeNm == '평가'
    And match response.resultData.eamMth == '직접출제'
    And match response.resultData.evlIemScrTotal == "#number"
    And match response.resultData.evlStdrSet == "#number"
    And match response.resultData.evlCpDt == "#ignore"


  @StntReportEvalController
  Scenario: 평가 결과 조회(결과보기) Summary
    Given path '/stnt/report/eval/result/summary'
    And param evlId = '2388'
    And param stntId = 'vsstu386'
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And match response.paramData.evlId == '2388'
    And match response.paramData.stntId == 'vsstu386'
    And match response.resultData.evlItemResultList[0].num == 8804
    And match response.resultData.evlItemResultList[0].evlResultId == 25541
    And match response.resultData.evlItemResultList[0].questionType == "서술형"
    And match response.resultData.page.size == 1
    And match response.resultData.page.totalElements == "#number"
    And match response.resultData.page.totalPages == "#number"
    And match response.resultData.page.number == 0


  @StntReportEvalController @키변경
  Scenario: 평가 결과 조회(인사이트)
    Given path '/stnt/report/eval/result/insite'
    And param evlId = '2388'
    And param stntId = 'vsstu386'
    And param condition = '0'
    And param page = '0'
    And param size = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    And match response.paramData.evlId == '2388'
    And match response.paramData.stntId == 'vsstu386'
    And match response.paramData.condition == '0'
    And match response.resultData.list != null
    And match response.resultData.list[0].evlId == '2388'
    And match response.resultData.list[0].setsId == "#number"
    And match response.resultData.list[0].evlIemId == "#number"
    And match response.resultData.list[0].subId == "#number"


# ------ StntSlfperEvalController ------

  @StntSlfperEvalController
  Scenario: 자기동료평가세팅
    Given path '/stnt/slfper/evl/slf/set'
    And param stntId = 'vsstu386'
    And param gbCd = '3'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSlfperEvalController @키변경
  Scenario: 자기동료평가설정(모듈)
    Given path '/stnt/slfper/evl/set/save'
    And json jsonBody = "{ userId: 'student1', gbCd: 1, wrterId: 'student1', slfPerEvlNm: '평가명_20240513', textbkId: 1, tabId: 1, taskId: null, evlId: null, setsId: null, resultDtlId: null, moduleId: null, subId: 0, slfEvlInfoList: [{ tmpltItmSeq: 1, evlDmi: 'evlDmi1', evlIem: 'evlIem1', evlStdrCd: '1', evlstdrDc: 'evlstdrDc1' }, { tmpltItmSeq: 2, evlDmi: 'evlDmi2', evlIem: 'evlIem2', evlStdrCd: '2', evlstdrDc: 'evlstdrDc2' }], slfStExposAt: 'Y', perEvlInfoList: [{ tmpltItmSeq: 3, evlDmi: 'evlDmi3', evlIem: 'evlIem3', evlStdrCd: '3', evlstdrDc: 'evlstdrDc3' }, { tmpltItmSeq: 4, evlDmi: 'evlDmi4', evlIem: 'evlIem4', evlStdrCd: '4', evlstdrDc: 'evlstdrDc4' }], perStExposAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSlfperEvalController @키변경
  Scenario: 자기동료평가저장(통)
    Given path '/stnt/slfper/evl/slf/save'
    And json jsonBody = "{ slfEvlInfoList: [{ selInfoId: 7, slfPerEvlDetailId: 1, apraserId: 'student46', evlAsw: 'evlAsw1' }, { selInfoId: 7, slfPerEvlDetailId: 2, apraserId: 'student46', evlAsw: 'evlAsw2' }], perEvlInfoList: [{ perInfoId: 13, slfPerEvlDetailId: '3', perEvlIArrList: [{ apraserId: 'student51', perApraserId: 'student3', evlAsw: 'evlAsw3' }, { apraserId: 'student51', perApraserId: 'student4', evlAsw: 'evlAsw4' }] }, { perInfoId: 14, slfPerEvlDetailId: '4', perEvlIArrList: [{ apraserId: 'vsstu387', perApraserId: 'student5', evlAsw: 'evlAsw5' }, { apraserId: 'vsstu387', perApraserId: 'student6', evlAsw: 'evlAsw6' }] }], moduleSubmAt: 'N' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSlfperEvalController
  Scenario: 동료평가대상정보
    Given path '/stnt/slfper/evl/slf/perinfo'
    And param stntId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchEvalController ------

  @TchEvalController
  Scenario: 평가 목록 조회
    Given path '/tch/eval/list'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param tmprStrgAt = 'N'
    And param evlSttsCd = ''
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController @error
  Scenario: 평가 응시시간 추가하기 조회
    Given path '/tch/eval/time/info'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController @error
  Scenario: 평가 응시시간 추가하기 저장
    Given path '/tch/eval/time'
    And json jsonBody = "{ evlId: 2388, isSelAll: true, evlAdiSec: 60, studentList: ['vsstu386', 'vsstu387'] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 정보 조회
    Given path '/tch/eval/info'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 현황 조회
    Given path '/tch/eval/status'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 정보 조회 (상세 미리보기)
    Given path '/tch/eval/preview'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 결과 조회 (응시중_응시완료)
    Given path '/tch/eval/result/status'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 정보 수정(시작하기)
    Given path '/tch/eval/start'
    And json jsonBody = "{ evlId: 2388 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 종료 하기
    Given path '/tch/eval/end'
    And json jsonBody = "{ evlId: 2388, timeoutAt: 'N' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 정보 수정(다시시작)
    Given path '/tch/eval/reset'
    And json jsonBody = "{ evlId: 2388 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 정보 삭제
    Given path '/tch/eval/delete'
    And json jsonBody = "{ evlId: 2388 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 정보 초기화(개발편의 임시성)
    Given path '/tch/eval/init'
    And json jsonBody = "{ evlId: 2388 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 자료설정 정보 조회
    Given path '/tch/eval/read-info'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


#  @TchEvalController
#  Scenario: 평가 자료설정 저장(수정)
#    Given path '/tch/eval/save'
#    And json jsonBody = "{ evlId: 527, evlNm: '설정 미완료 목록 > 자료설정 테스트', bbsSvAt: 'Y', bbsNm: '자료실에 저장 테스트', tag: '테스트태그', cocnrAt: 'N', pdSetAt: 'N', pdEvlStDt: '', pdEvlEdDt: '', ntTrnAt: 'N', timStAt: 'Y', timTime: '11:00', aiTutSetAt: 'N', rwdSetAt: 'Y', scrSetAt: 'N', evlStdrSetAt: 'Y', evlStdrSet: '3', evlGdStdrScr: '', evlAvStdrScr: '', evlPsStdrScr: '', edGidAt: 'Y', edGidDc: '자료설정 테스트 - 자동출제테스트 - 평가_세트지_19_수정', stdSetAt: 'Y', studentInfoList: [{ id: 1, evlId: 527, trnTrgetId: 'vsstu386', trnTrgetNm: '이학생', isTrnTrget: true }, { id: 2, evlId: 527, trnTrgetId: 'vsstu387', trnTrgetNm: '김학생', isTrnTrget: false }], slfEvlInfo: { userId: 'vsstu386', gbCd: 3, wrterId: 1, wrtDt: 1, slfPerEvlClsfCd: 1, slfPerEvlNm: '테스트', stExposAt: 'Y', textbkId: 1, tabId: 1, taskId: 1, evlId: 2388, setsId:


  @TchEvalController @키변경
  Scenario: 평가 생성(저장)
    Given path '/tch/eval/create'
    And json jsonBody = "{ wrterId: 'aidt3', claId: '0cc175b9c0f1b6a831c399e269772661', textbookId: 1, evlNm: '테스트 평가 수학', evlSeCd: 1, eamMth: 3, eamExmNum: 0, eamGdExmMun: 0, eamAvUpExmMun: 0, eamAvExmMun: 0, eamAvLwExmMun: 0, eamBdExmMun: 0, eamScp: '4,5', setsId: 240, prscrStdSetAt: 'N', prscrStdStDt: '2024.01.26 09:00', prscrStdEdDt: '2024.01.28 18:00', prscrStdNtTrnAt: 'N', prscrStdPdSet: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 정보 복사
    Given path '/tch/eval/copy'
    And json jsonBody = "{ evlId: 2388 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchEvalController
  Scenario: 평가 문항 자동생성 추천 모듈정보 조회
    Given path '/tch/eval/auto/qstn/extr'
    And param wrterId = 'aidt3'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param eamExmNum = '6'
    And param eamGdExmMun = '0'
    And param eamAvUpExmMun = '3'
    And param eamAvExmMun = '1'
    And param eamAvLwExmMun = '1'
    And param eamBdExmMun = '1'
    And param eamScp = '870,872,956'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchReportController ------

  @TchReportController
  Scenario: 종합리포트
    Given path '/tch/report/total'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbkId = '1'
    And param smstr = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportController
  Scenario: 종합리포트(학생조회)
    Given path '/tch/report/search/stnt'
    And param userId = 'vsstu386'
    And param stntId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbkId = '1'
    And param smstr = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportController @키변경
  Scenario: 리포트 결과보기 (리포트 범위 보기)
    Given path '/tch/report/exam-scope'
    And param textbkId = '16'
    And param setsId = '690'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchReportEvalController ------

  @TchReportEvalController
  Scenario: 평가 리포트 목록조회
    Given path '/tch/report/eval/list'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param condition = ''
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(자세히 보기) > 목록
    Given path '/tch/report/eval/result/detail/list'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController @키변경
  Scenario: 평가 결과 조회(자세히 보기) > 모듈
    Given path '/tch/report/eval/result/detail/mdul'
    And param evlId = '2386'
    And param evlIemId = '1'
    And param subId = '0'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController @키변경
  Scenario: 평가 결과 조회(자세히 보기) > 학생
    Given path '/tch/report/eval/result/detail/stnt'
    And param evlId = '2386'
    And param evlIemId = '1'
    And param subId = '0'
    And param userId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController @키변경
  Scenario: 평가 결과 조회(자세히 보기) > 학생 > 피드백
    Given path '/tch/report/eval/result/detail/stnt/fdb/mod'
    And json jsonBody = "{ evlId: 2388, evlIemId: 1, subId: 0, userId: 'vsstu386', fdbDc: '테스트 피드백' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 학생조회 > 리포트화면 학생 검색
    Given path '/tch/stnt-srch/report/find/stnt'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param stntCondition = 'name'
    And param stntKeyword = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 학생조회 > 평가 리포트 목록조회
    Given path '/tch/stnt-srch/report/eval/list'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param stntId = 'vsstu386'
    And param condition = 'name'
    And param keyword = ''
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 학생조회 > 평가 결과 조회(자세히 보기)
    Given path '/tch/stnt-srch/report/eval/result/detail'
    And param evlId = '2386'
    And param stntId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(상단정보)
    Given path '/tch/report/eval/result/header'
    And param evlId = '2386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회-인사이트 본문
    Given path '/tch/report/eval/result/insite'
    And param evlId = '2386'
    And param condition = '0'
    And param page = '0'
    And param size = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(결과보기)
    Given path '/tch/report/eval/result/summary'
    And param evlId = '2386'
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController @키변경
  Scenario: 평가 결과 배점 수정
    Given path '/tch/report/eval/result/modi/score'
    And json jsonBody = "{ evlId: 2, userId: 'vsstu386', evlIemId: 1, subId: 0, evlIemScrResult: 44, tchId: '550e8400-e29b-41d4-a716-446655440000' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 학생조회 > 평가 결과 조회-상단정보(선택된 학생)
    Given path '/tch/stnt-srch/report/eval/result/header'
    And param evlId = '2386'
    And param stntId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 학생조회 > 평가 결과 조회-결과보기(선택된 학생)
    Given path '/tch/stnt-srch/report/eval/result/summary'
    And param evlId = '2386'
    And param stntId = 'vsstu386'
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 학생조회 > 평가 결과 조회-인사이트 본문
    Given path '/tch/stnt-srch/report/eval/result/insite'
    And param evlId = '4'
    And param stntId = 'vsstu386'
    And param condition = '0'
    And param page = '0'
    And param size = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 교사 평가리포트 > 공개처리
    Given path '/tch/report/eval/open'
    And json jsonBody = "{ evlId: 2388 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 교사 평가리포트 > 평가 공개후 배점 수정반영
    Given path '/tch/report/eval/result/appl/score'
    And json jsonBody = "{ evlId: 2 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 교사 평가리포트 > 평가 리포트 총평 저장
    Given path '/tch/report/eval/general-review/save'
    And json jsonBody = "{ evlId: 2388, userId: 'student47', genrvw: '1', stdtPrntRlsAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(개별문항출제) 자세히 보기 1
    Given path '/tch/report/eval/result/ind/list'
    And param evlId = '2'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(개별문항)결과_인사이트)-반별학생목록조회
    Given path '/tch/stnt-list'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(개별문항출제) 자세히 보기 2
    Given path '/tch/report/eval/result/ind/mdul'
    And param evlId = '2'
    And param stntId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(개별문항)결과_인사이트)-공통Header
    Given path '/tch/report/eval/result/ind/header'
    And param evlId = '2'
    And param stntId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(개별문항)결과_인사이트)-결과보기
    Given path '/tch/report/eval/result/ind/summary'
    And param evlId = '2'
    And param stntId = 'vsstu386'
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 결과 조회(개별문항)결과_인사이트)-인사이트
    Given path '/tch/report/eval/result/ind/insite'
    And param evlId = '2'
    And param stntId = 'vsstu386'
    And param condition = '0'
    And param page = '0'
    And param size = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 리포트 > 총평조회
    Given path '/tch/report/eval/general-review/info'
    And param evlId = '2386'
    And param userId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportEvalController
  Scenario: 평가 리포트 > 총평 AI 평어
    Given path '/tch/report/eval/general-review/ai-evl-word'
    And param evlId = '2386'
    And param userId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchSlfperEvalController ------

  @TchSlfperEvalController
  Scenario: 자기동료평가템플릿저장
    Given path '/tch/slfper/evl/tmplt/save'
    And json jsonBody = "{ userId: 'vsstu386', slfPerEvlClsfCd: 1, slfPerEvlNm: '테스트', stExposAt: 'N', slfPerEvlInfoList: [{ evlDmi: 'evlDmi1', evlIem: 'evlIem1', evlStdrCd: '1', evlStdrDc: 'evlStdrDc1' }, { evlDmi: 'evlDmi2', evlIem: 'evlIem2', evlStdrCd: '2', evlStdrDc: 'evlStdrDc2' }] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchSlfperEvalController
  Scenario: 자기동료평가템플릿조회
    Given path '/tch/slfper/evl/tmplt/list'
    And param userId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchSlfperEvalController
  Scenario: 자기동료평가템플릿조회상세
    Given path '/tch/slfper/evl/tmplt/detail'
    And param tmpltId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchSlfperEvalController @키변경
  Scenario: 자기동료평가설정
    Given path '/tch/slfper/evl/set/save'
    And json jsonBody = "{ userId : 'vsstu386', gbCd : 1, wrterId : 1, wrtDt : 1, slfPerEvlClsfCd : 1, slfPerEvlNm : '테스트', stExposAt : 'Y', textbkId : 1, tabId : 1, taskId : 1, evlId : 1, setsId : 1, resultDtlId : 1, tmpltId : 1, slfPerEvlInfoList : [{ evlDmi : 1, evlIem : 1, evlStdrCd : 1, evlStdrDc : 1 }, { evlDmi : 1, evlIem : 1, evlStdrCd : 1, evlStdrDc : 1 }] }"
    And request jsonBody
    When method POST
    Then status 200


  @TchSlfperEvalController @키변경
  Scenario: 자기동료평가결과보기
    Given path '/tch/slfper/evl/slf/view'
    And param stntId = 'vsstu386'
    And param gbCd = '1'
    And param textbkId = '1'
    And param tabId = '1'
    And param taskId = '1'
    And param evlId = '2386'
    And param setsId = '1'
    And param moduleId = '1'
    And param subId = '0'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchSlfperEvalController @키변경
  Scenario: 자기동료평가설정보기
    Given path '/tch/slfper/evl/slf/form'
    And param gbCd = '1'
    And param textbkId = '1'
    And param tabId = '1'
    And param taskId = '1'
    And param evlId = '2386'
    And param setsId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchSlfperEvalController
  Scenario: 자기동료평가결과보기(동료평가)
    Given path '/tch/slfper/evl/per/view'
    And param perInfoId = '5'
    And param stntId = 'vsstu386'
    And param perApraserId = 'vsstu386'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchSlfperEvalController @키변경
  Scenario: 자기동료평가제출현황
    Given path '/tch/slfper/evl/per/status'
    And param gbCd = '1'
    And param textbkId = '1'
    And param tabId = '1'
    And param taskId = '1'
    And param evlId = '2386'
    And param setsId = '1'
    And param moduleId = '1'
    And param subId = '0'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchSlfperEvalController
  Scenario: 동료평가대상정보
    Given path '/tch/slfper/evl/slf/perinfo'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200







