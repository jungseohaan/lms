Feature: 비상교육 AIDT API - LECTURE

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ StntCrcuController ------

  @StntCrcuController
  Scenario: 커리큘럼 목록 조회
    Given path '/stnt/crcu/list'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuController
  Scenario: 차시 정보 조회
    Given path '/stnt/crcu/info'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuController
  Scenario: 즉석퀴즈 답안 제출
    Given path '/stnt/crcu/quiz/answer'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuController
  Scenario: 모드 정보 조회
    Given path '/stnt/mode'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuController
  Scenario: 모드 설정
    Given path '/stnt/mode/create'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntCrcuMdulController ------

  @StntCrcuMdulController
  Scenario: 모듈 정보 조회
    Given path '/stnt/crcu/mdul/info'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 모듈 정보 수정 or 저장(PUT)
    Given path '/stnt/crcu/mdul'
    When method PUT
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 상시툴 진행 결과 제출하기(POST)
    Given path '/stnt/crcu/mdul/tools'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 상시툴 진행 결과 조회하기(GET)
    Given path '/stnt/crcu/mdul/tools/info'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 모듈 선택번호, 정/오답 표시 저장(POST)
    Given path '/stnt/crcu/mdul/answer'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 모듈 풀이과정 조회(GET)
    Given path '/stnt/crcu/mdul/soln-proc'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 모듈 풀이과정 저장(POST)
    Given path '/stnt/crcu/mdul/soln-proc/create'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 모듈 피드백 조회
    Given path '/stnt/crcu/mdul/feedback'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuMdulController
  Scenario: 우수(학생) 답안 목록 조회
    Given path '/stnt/crcu/mdul/sharing/answer/list'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntCrcuQuizController ------

  @StntCrcuQuizController
  Scenario: 학생 퀴즈풀기(목록출력)
    Given path '/stnt/tool/quiz/list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbkId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuQuizController
  Scenario: 학생 퀴즈풀기(호출)
    Given path '/stnt/tool/quiz/call'
    And param qizId = 3
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntCrcuQuizController
  Scenario: 학생 퀴즈풀기(제출하기)
    Given path '/stnt/tool/quiz/submit'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', qizId: 3, submdistrNum: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntReportLectureController ------

  @StntReportLectureController
  Scenario: (학생) 수업리포트 결과 조회 (자세히보기)
    Given path '/stnt/report/lecture/detail'
    And param userId = 'vstea38'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbkId = 1
    And param crculId = 7
    And param tabId = ''
    And param stntId = 'vsstu388'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchCrcuBoardController ------

  @TchCrcuBoardController
  Scenario: 의견보드(저장)
    Given path '/tch/tool/board/save'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1, brdCd: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuBoardController
  Scenario: 의견보드(호출)
    Given path '/tch/tool/board/call'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbkId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuBoardController
  Scenario: 화이트보드(저장)
    Given path '/tch/tool/whiteboard/save'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1, brdSeq: 1, brdCn: 'brdCn1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuBoardController
  Scenario: 화이트보드(리스트)
    Given path '/tch/tool/whiteboard/list'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbkId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuBoardController
  Scenario: 화이트보드(호출)
    Given path '/tch/tool/whiteboard/call'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbkId = 1
    And param brdSeq = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuBoardController
  Scenario: 화이트보드(갱신)
    Given path '/tch/tool/whiteboard/modify'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1, brdSeq: 1, brdCn: 'brdCn1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuBoardController
  Scenario: 화이트보드(삭제)
    Given path '/tch/tool/whiteboard/del'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchCrcuCateController ------

  @TchCrcuCateController
  Scenario: 분류 생성
    Given path '/tch/crcu/cate/create'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuCateController
  Scenario: 분류 수정
    Given path '/tch/crcu/cate/modify'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuCateController
  Scenario: 분류 삭제
    Given path '/tch/crcu/cate/remove'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuCateController
  Scenario: 분류 복제
    Given path '/tch/crcu/cate/copy'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuCateController
  Scenario: 동일 Depth 이동
    Given path '/tch/crcu/cate/move'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchCrcuController ------

  @TchCrcuController
  Scenario: 커리큘럼 목록 조회
    Given path '/tch/crcu/list'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param textbkId = 1
    And param textbkIdxId = 1
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuController
  Scenario: 커리큘럼 목록 조회
    Given path '/tch/crcu/list2'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param textbkId = 1
    And param textbkIdxId = 1
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuController
  Scenario: 차시 정보 조회
    Given path '/tch/crcu/info'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param textbkId = 1
    And param textbkIdxId = 1
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param crculId = 7
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuController
  Scenario: 마지막 수업한 차시정보 조회
    Given path '/tch/crcu/last-position'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbkId = 1
    And param textbkIdxId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuController
  Scenario: 마지막 수업한 차시정보 기록(수정)
    Given path '/tch/crcu/last-position'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1, textbkIdxId: 1, crculId: 5 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuController
  Scenario: 대중소분류 등록
    Given path '/tch/crcu/classify/reg'
    And json jsonBody = "{ textbkId: 1, wrterId: 'vstea50', claId: '308ad5afba8f11ee88c00242ac110002', parentKey: 4, crculNm: '테스트 대분류' }"
    And request jsonBody
    When method POST
    Then status 200


  @TchCrcuController
  Scenario: 대중소분류 수정
    Given path '/tch/crcu/classify/mod'
    And json jsonBody = "{ textbkId: 1, wrterId: 'vstea50', claId: '308ad5afba8f11ee88c00242ac110002', crculKey: 18, parentKey: 4, crculNm: '테스트 대분류 수정' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuController
  Scenario: 대중소분류 삭제
    Given path '/tch/crcu/classify/del'
    And json jsonBody = "{ textbkId: 1, wrterId: 'vstea50', claId: '308ad5afba8f11ee88c00242ac110002', crculKey: 18, parentKey: 4, crculNm: '테스트 대분류 수정' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchCrcuMdulController ------

  @TchCrcuMdulController
  Scenario: 모듈 정보 조회
    Given path '/tch/crcu/mdul/info'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 모듈 정답보기(조회)
    Given path '/tch/crcu/mdul/answer'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 제출현황 및 정답(률) 리셋
    Given path '/tch/crcu/mdul/answers/reset'
    When method DELETE
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 제출현황 조회
    Given path '/tch/crcu/mdul/submit/status'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 모듈 상시툴 답안 제출 방식 내려주기
    Given path '/tch/crcu/mdul/tools'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 상시툴 활동 종료
    Given path '/tch/crcu/mdul/tools/end'
    When method PUT
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 상시툴 제출 현황 조회
    Given path '/tch/crcu/mdul/tools/status'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 우수학생 답안 공유하기
    Given path '/tch/crcu/mdul/share/answer'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 모듈(콘텐츠) 정렬순서를 변경
    Given path '/tch/crcu/mdul/sort'
    When method PUT
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 모듈(콘텐츠) 노출여부 설정
    Given path '/tch/crcu/mdul/show'
    When method PUT
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 모듈(콘텐츠) 복사
    Given path '/tch/crcu/mdul/copy'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuMdulController
  Scenario: 모듈(콘텐츠) 삭제_일괄삭제
    Given path '/tch/crcu/mdul'
    When method DELETE
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchCrcuQuizController ------

  @TchCrcuQuizController
  Scenario: 즉석퀴즈생성
    Given path '/tch/tool/quiz/form'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', schlNm: 'schlNm1', textbkId: 1, qizNum: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuQuizController
  Scenario: 즉석퀴즈보기
    Given path '/tch/tool/quiz/view'
    And param qizId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuQuizController
  Scenario: 즉석퀴즈시작
    Given path '/tch/tool/quiz/start'
    And json jsonBody = "{ qizList: [ { qizId: 1, distrNm: '평가항목1', textbkNm: '교과서명1', qizPosScript: '퀴즈발문1', resultDispAt: 'Y', anonyAt: 'N', qizSttsCd: 2, qizInfoList: [ { distrNum: 1, distrNm: '평가항목이름1' }, { distrNum: 2, distrNm: '평가항목이름2' } ] }, { qizId: 1, distrNm: '평가항목1', textbkNm: '교과서명1', qizPosScript: '퀴즈발문1', resultDispAt: 'Y', anonyAt: 'N', qizSttsCd: 2, qizInfoList: [ { distrNum: 1, distrNm: '평가항목이름1' }, { distrNum: 2, distrNm: '평가항목이름2' } ] } ] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuQuizController
  Scenario: 즉석퀴즈종료
    Given path '/tch/tool/quiz/end'
    And json jsonBody = "{ qizId: [1,2] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuQuizController
  Scenario: 즉석퀴즈결과
    Given path '/tch/tool/quiz/result'
    And param qizId = 1
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuQuizController
  Scenario: 즉석퀴즈삭제
    Given path '/tch/tool/quiz/del'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuQuizController
  Scenario: 즉석퀴즈정보(네비)
    Given path '/tch/tool/quiz/nav'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuQuizController
  Scenario: 즉석퀴즈(초기화)
    Given path '/tch/tool/quiz/init'
    And json jsonBody = "{ qizId: [1,2,3] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchCrcuTabController ------

  @TchCrcuTabController
  Scenario: 차시 탭 활성/비활성화 처리
    Given path '/tch/crcu/tab'
    And json jsonBody = "{ tabId: 1, exposAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuTabController
  Scenario: 차시 탭 정보 조회
    Given path '/tch/crcu/tab/info'
    And param tabId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuTabController
  Scenario: 탭의 모듈 목록 조회 (미사용)
    Given path '/tch/crcu/tab/list'
    And param tabId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuTabController
  Scenario: 수업자료탭 변경
    Given path '/tch/crcu/tab/save'
    And json jsonBody = "{ TabList : [{ tabId : 1, tabSeq : 2 }, { tabId : 2, tabSeq : 2 }] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchCrcuTabController
  Scenario: 탭 세트지 변경
    Given path '/tch/crcu/tab/chg_info'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', tabId : 1, setsId : 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchLectureReportController ------

  @TchLectureReportController
  Scenario: (교사) 수업리포트 결과 조회 (자세히보기)
    Given path '/tch/report/lecture/result/list'
    And param userId = 'vstea1'
    And param claId = '1dfd618eb8fb11ee88c00242ac110002'
    And param textbkId = 1
    And param crculId = 7
    And param tabId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 수업리포트 결과 조회 (자세히보기2-콘텐츠 정보)
    Given path '/tch/report/lecture/result/detail/mdul'
    And param crculId = 7
    And param tabId = 127
    And param dtaIemId = 1
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 수업리포트 결과 조회 (자세히보기3-학생답안)
    Given path '/tch/report/lecture/result/detail/stnt'
    And param crculId = 7
    And param tabId = 127
    And param dtaIemId = 1
    And param subId = 0
    And param userId = 'vsstu1'
    And param reExmNum = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 수업리포트 결과 조회 (자세히 보기 : 정오표 수정)
    Given path '/tch/report/lecture/result/errata/mod'
    And json jsonBody = "{ dtaResultDetailId: 8, errata: 3 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 수업리포트 결과 조회 (자세히 보기 : 피드백 저장)
    Given path '/tch/report/lecture/result/fdb/mod'
    And json jsonBody = "{ dtaResultDetailId: 8, fdbDc: '테스트 피드백' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 수업리포트 결과 조회 (자세히보기)
    Given path '/tch/stnt-srch/report/lecture/detail'
    And param userId = 'vstea38'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbkId = 1
    And param crculId = 7
    And param tabId = ''
    And param stntId = 'vsstu388'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: 수업 리포트 총평 저장
    Given path '/tch/report/lecture/general-review/save'
    And json jsonBody = "{ textbkTabId: '1941', userId: 'vsstu615', genrvw: 'N', stdtPrntRlsAt: 'N' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 사이트 설정 > 학부모 설정 > 대시보드/리포트 노출 설정 (수학/영어) 조회
    Given path '/tch/site-set/dash-report/expos/list'
    And param wrterId = 'vsstu1'
    And param claId = '308ad21cba8f11ee88c00242ac110002'
    And param yr = '2024'
    And param smt = 1
    And param textbkId = 1
    And param exposCd = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 사이트 설정 > 학부모 설정 > 대시보드/리포트 노출 설정 (수학/영어) (수정)등록
    Given path '/tch/site-set/dash-report/expos/save'
    And json jsonBody = "{ wrterId: 'vsstu1', claId: '308ad21cba8f11ee88c00242ac110002', yr: '2024', smt: 1, textbkId: 1, exposCd: 1, exposTrgetCd: 1, exposAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: (교사) 홈 대시보드 > 수업 리포트 > 자세히보기 > 총평조회
    Given path '/tch/report/lecture/general-review/info'
    And param textbkTabId = 2272
    And param userId = 'qa5-s20'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportController
  Scenario: 수업 리포트 총평 AI 평어
    Given path '/tch/report/lecture/general-review/ai-evl-word'
    And param textbkTabId = 2
    And param userId = 'vsstu615'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchLectureReportEngController ------

  @TchLectureReportEngController
  Scenario: (교사) 수업리포트 결과 조회 (자세히보기)
    Given path '/tch/report/lecture/result/list/eng'
    And param userId = 'vstea1'
    And param claId = '1dfd618eb8fb
    And param textbkId = 1
    And param crculId = 7
    And param tabId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLectureReportEngController
  Scenario: (교사) 수업리포트 결과 조회 (자세히보기2-콘텐츠 정보)
    Given path '/tch/report/lecture/result/detail/mdul/eng'
    And param crculId = 7
    And param tabId = 127
    And param dtaIemId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


