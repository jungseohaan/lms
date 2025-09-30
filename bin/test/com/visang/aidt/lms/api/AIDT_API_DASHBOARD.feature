Feature: 비상교육 AIDT API - DASHBOARD

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ EtcController ------

  @EtcController
  Scenario: 오늘의 기분 조회(기분, 에너지에 따른 목록들)
    Given path '/etc/tdymd/list'
    And param cdtnSeCd = 1
    And param enrgSeCd = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 학생 오늘의 기분 insert
    Given path '/etc/tdymd/init'
    And json jsonBody = { "tdyMdId":1, "tdyMdRsn":"그냥", "stdtId":"re22mma15-s1", "claId":"cc86a331d2824c52b1db68a0bf974dd5" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 학생 개인 오늘의 기분 목록(최근 검사한 1개만 return)
    Given path '/etc/tdymd/info'
    And param stdtId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 클래스 기준으로 모든 학생들의 최근 기분 조회
    Given path '/etc/tdymd/stinfo'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 학생 개인의 오늘의 기분 검사 목록
    Given path '/etc/tdymd/stdetail'
    And param stdtId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 목표 설정 리스트(모든 대단원 고유값 전달 필요)
    Given path '/etc/gl/detail'
    And param crculIds = '1,2,3,4'
    And param stdtId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 공통 목표 리스트
    Given path '/etc/gl/commongoal'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 선생님 목표설정 리스트
    Given path '/etc/gl/tch/list'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param crculId = '1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 목표 설정 수정
    Given path '/etc/gl/updt'
    And json jsonBody = [ { "claId":"4e6d95b341474bc9826dc372beefc6a5", "crculId":1, "ordNo":1, "goalNm":"선생님이 수정한것입니다", "tchSetAt":"Y" }, { "goalDetailId":16, "goalNm":"비상교육" }, { "goalDetailId":17, "goalNm":"핫둘셋넷" } ]
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: 목표 달성 여부 저장
    Given path '/etc/gl/stachv'
    And json jsonBody = { "goalDetailId":17, "stChkAt":"Y" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (선생님)META 목록 조회
    Given path '/etc/meta/tc/info'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param tcId = 're22mma15-t'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (선생님)META 자기조절학습 시작(시작시 데이터 삽입)
    Given path '/etc/meta/tc/start'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param tcId = 're22mma15-t'
    And param ordNo = 1
    And param grade = 'el'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (선생님)META 자기조절학습 종료
    Given path '/etc/meta/tc/end'
    And param dgnssId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (선생님)META 자기조절학습 취소
    Given path '/etc/meta/tc/cancel'
    And param dgnssId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (선생님)META 자기조절학습 미제출한 학생 목록
    Given path '/etc/meta/tc/notsubm'
    And param dgnssId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (학생)META 자기조절학습 목록 조회
    Given path '/etc/meta/st/info'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param stdtId = 're22mma15-s1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (학생)META 자기조절학습 시작
    Given path '/etc/meta/st/start'
    And param dgnssResultId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (학생)META 자기조절학습 새로하기
    Given path '/etc/meta/st/new'
    And param dgnssResultId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (학생)META 자기조절학습 답 입력
    Given path '/etc/meta/st/answer'
    And json jsonBody = { "omrIdx":1, "no":1, "answer":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (학생)META 자기조절학습 제출
    Given path '/etc/meta/st/submit'
    And json jsonBody = { "dgnssResultId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (교사)META 자기조절학습 상담 및 지도가 필요한 학생
    Given path '/etc/meta/tc/need'
    And param dgnssId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (교사)META 자기조절학습 학생 목록
    Given path '/etc/meta/tc/stinfolist'
    And param dgnssId = 1
    And param type = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (교사) 대시보드 - 종합 분석
    Given path '/etc/meta/tc/analysis'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (학생) META 자기조절학습 결과보기
    Given path '/etc/meta/st/analysis'
    And param dgnssResultId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @EtcController
  Scenario: (학생) META 자기조절학습 정답 랜덤입력(테스트 편의용)
    Given path '/etc/meta/st/subrandom'
    And param omrIdx = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ StntDsbdController ------

  @StntDsbdController
  Scenario: (학생)종합리포트
    Given path '/stnt/dashboard/report/total'
    And param stntId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbkId = '16'
    And param smstr = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 클래스 정보
    Given path '/stnt/dsbd/classinfo'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 주간 정보
    Given path '/stnt/dsbd/weekinfo'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 실시간 우리반 알림
    Given path '/stnt/dsbd/ntcn/class'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 시간표
    Given path '/stnt/dsbd/schedule'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 단원별 이해도
    Given path '/stnt/dsbd/understand/chapter'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 개념별 이해도
    Given path '/stnt/dsbd/understand/concept'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 영역별 이해도
    Given path '/stnt/dsbd/understand/domain'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 과제 평가 현황
    Given path '/stnt/dsbd/status/eval'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 자기주도 학습 현황
    Given path '/stnt/dsbd/status/selflearning'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 자기주도 학습 현황 상세
    Given path '/stnt/dsbd/status/selflearning/detail'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 자기주도 학습 현황(단원별)
    Given path '/stnt/dsbd/status/self-lrn/chapter/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbkId = '16'
    And param searchDt = '202403'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 단원별 이해도
    Given path '/stnt/dsbd/status/chapter-unit/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 단원별 이해도 상세
    Given path '/stnt/dsbd/status/chapter-unit/detail'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param metaId = '870'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 영역별 이해도
    Given path '/stnt/dsbd/status/area-usd/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbkId = '16'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 영역별 이해도 상세
    Given path '/stnt/dsbd/status/area-usd/detail'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbkId = '16'
    And param areaId = '870'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 자기주도 학습 현황(단원별 상세)
    Given path '/stnt/dsbd/status/self-lrn/chapter/detail'
    And param userId = 're22mma15-s1'
    And param textbkId = '16'
    And param searchDt = '20240305'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 과제_평가 현황 (과제)
    Given path '/stnt/dsbd/status/homewk/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 과제_평가 현황 (평가)
    Given path '/stnt/dsbd/status/eval/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 자기주도AI학습 결과 보기
    Given path '/stnt/dsbd/status/aitutor/result'
    And param userId = 're22mma15-s1'
    And param stdId = '262'
    And param textbkId = '16'
    And param learningType = '1'
    And param learningDate = '20240319'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 영역별 그래프
    Given path '/stnt/dsbd/status/area-achievement/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 영역별 단원별 목록
    Given path '/stnt/dsbd/status/area-achievement/detail'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    And param code = 'listening'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 학습맵 언어 형식
    Given path '/stnt/dsbd/status/study-map/languageFormat/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 학습맵 의사소통
    Given path '/stnt/dsbd/status/study-map/communication/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 학습맵 소재
    Given path '/stnt/dsbd/status/study-map/material/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - 학습맵 성취 기준
    Given path '/stnt/dsbd/status/study-map/achievementStandard/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - Vocabulary 그래프
    Given path '/stnt/dsbd/status/vocabulary/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController @키변경2
  Scenario: 학생 대시보드 - Vocabulary 단원별 상세
    Given path '/stnt/dsbd/status/vocabulary/detail'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    And param iemId = '1'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - Grammar 그래프
    Given path '/stnt/dsbd/status/grammar/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController @키변경2
  Scenario: 학생 대시보드 - Grammar 단원별 상세
    Given path '/stnt/dsbd/status/grammar/detail'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    And param iemId = '1'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController
  Scenario: 학생 대시보드 - Pronunciation 그래프
    Given path '/stnt/dsbd/status/pronunciation/list'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdController @키변경2
  Scenario: 학생 대시보드 - Pronunciation 단원별 상세
    Given path '/stnt/dsbd/status/pronunciation/detail'
    And param userId = 're22mma15-s1'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbookId = '16'
    And param unitNum = '1'
    And param iemId = '1'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ StntDsbdInfoController ------

  @StntDsbdInfoController
  Scenario: 개념별 이해도
    Given path '/stnt/dsbd/status/concept-usd/list'
    And param userId = 'vsstu467'
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = '16'
    And param metaId = ''
    And param kwgMainId = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdInfoController
  Scenario: 개념별 이해도 상세
    Given path '/stnt/dsbd/status/concept-usd/detail'
    And param userId = 'vsstu467'
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = '16'
    And param metaId = ''
    And param kwgMainId = ''
    And param stdDt = '20240313'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdInfoController
  Scenario: 학습맵 이해도
    Given path '/stnt/dsbd/status/study-map/list'
    And param userId = 'vsstu467'
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = '16'
    And param metaId = ''
    And param kwgMainId = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdInfoController
  Scenario: 학습맵 이해도 (개념)
    Given path '/stnt/dsbd/status/study-map/concept'
    And param userId = 'vsstu467'
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = '16'
    And param metaId = '870'
    And param kwgMainId = '915'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntDsbdInfoController
  Scenario: 학습맵 이해도 (상세)
    Given path '/stnt/dsbd/status/study-map/detail'
    And param userId = 'vsstu467'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param metaId = '1559'
    And param kwgMainId = '1565'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchDsbdController ------

  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 자기주도학습 현황
    Given path '/tch/dsbd/status/self-lrn/chapter/list'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbkId = '16'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 자기주도학습 현황 상세
    Given path '/tch/dsbd/status/self-lrn/chapter/detail'
    And param claId = 'cc86a331d2824c52b1db68a0bf974dd5'
    And param textbkId = '16'
    And param unitNum = '1'
    And param submDt = '20240226'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단원별 학생분포
    Given path '/tch/dsbd/status/chapter-unit/list'
    And param userId = 'vstea46'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param textbookId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단원별 학생분포 상세
    Given path '/tch/dsbd/status/chapter-unit/detail'
    And param userId = 'vstea46'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param metaId = '1560'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 가장 최근 수업 정보 및 요약 현황
    Given path '/tch/dsbd/summary'
    And param userId = 'vstea6'
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 개념별 이해도
    Given path '/tch/dsbd/status/concept-usd/list'
    And param userId = 'vstea16'
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = '16'
    And param metaId = ''
    And param kwgMainId = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 개념별 이해도 상세
    Given path '/tch/dsbd/status/concept-usd/detail'
    And param userId = 'vstea16'
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = '1'
    And param metaId = '870'
    And param kwgMainId = '0'
    And param stdDt = '20240313'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 영역별 이해도
    Given path '/tch/dsbd/status/area-usd/list'
    And param userId = 'vstea46'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param textbkId = '13'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 영역별 이해도 상세
    Given path '/tch/dsbd/status/area-usd/detail'
    And param userId = 'vsstu467'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param textbkId = '13'
    And param areaId = '1559'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학습맵 이해도
    Given path '/tch/dsbd/status/study-map/list'
    And param userId = 'vstea38'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param metaId = ''
    And param kwgMainId = '915'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학습맵 이해도 (개념)
    Given path '/tch/dsbd/status/study-map/concept'
    And param userId = 'vstea38'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param metaId = '870'
    And param kwgMainId = '915'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학습맵 이해도 (상세)
    Given path '/tch/dsbd/status/study-map/detail'
    And param userId = 'vstea46'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param metaId = '1559'
    And param kwgMainId = '1565'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 자주쓰는문장(등록)
    Given path '/tch/dsbd/oftensent/save'
    And request { "userId": "550e8400-e29b-41d4-a716-446655440000", "sents": "의견을 잘 표현함" }
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 자주쓰는문장(수정)
    Given path '/tch/dsbd/oftensent/mod'
    And request { "userId": "550e8400-e29b-41d4-a716-446655440000", "sentsId": 1, "sents": "자세하게 설명함" }
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 자주쓰는문장(삭제)
    Given path '/tch/dsbd/oftensent/del'
    And request { "userId": "550e8400-e29b-41d4-a716-446655440000", "sentsId": 1 }
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 자주쓰는문장(목록)
    Given path '/tch/dsbd/oftensents/list'
    And param userId = 'vstea1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 영역별 그래프
    Given path '/tch/dsbd/status/area-achievement/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 영역별 그래프 상세
    Given path '/tch/dsbd/status/area-achievement/detail'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    And param code = 'listening'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식
    Given path '/tch/dsbd/status/study-map/languageFormat/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 학습맵 > 의사소통
    Given path '/tch/dsbd/status/study-map/communication/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 학습맵 > 소재
    Given path '/tch/dsbd/status/study-map/material/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준
    Given path '/tch/dsbd/status/study-map/achievementStandard/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프
    Given path '/tch/dsbd/status/vocabulary/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 상세
    Given path '/tch/dsbd/status/vocabulary/detail'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    And param iemCd = 'track'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프
    Given path '/tch/dsbd/status/grammar/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 상세
    Given path '/tch/dsbd/status/grammar/detail'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '1'
    And param iemCd = 'be 동사'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프
    Given path '/tch/dsbd/status/pronunciation/list'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '2'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdController
  Scenario: (교사) 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 상세
    Given path '/tch/dsbd/status/pronunciation/detail'
    And param userId = 'vsstu386'
    And param claId = '308ad5afba8f11ee88c00242ac110002'
    And param textbookId = '10'
    And param unitNum = '2'
    And param iemCd = 'i can do it anything!'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchDsbdEvalController ------

  @TchDsbdEvalController @error @MediaType.APPLICATION_JSON_VALUE
  Scenario: (교사) 학급관리 > 홈 대시보드 > 과제_평가 현황 (평가)
    Given path '/tch/dsbd/status/eval/list'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdEvalController @error @MediaType.APPLICATION_JSON_VALUE
  Scenario: (교사) 학급관리 > 홈 대시보드 > 과제_평가 현황 상세 (평가)
    Given path '/tch/dsbd/status/eval/detail'
    And param evlId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchDsbdHomewkController ------

  @TchDsbdHomewkController @error @MediaType.APPLICATION_JSON_VALUE
  Scenario: (교사) 학급관리 > 홈 대시보드 > 과제_평가 현황 (과제)
    Given path '/tch/dsbd/status/homewk/list'
    And param userId = 'vsstu386'
    And param claId = '308ad483ba8f11ee88c00242ac110002'
    And param textbookId = '1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdHomewkController @error @MediaType.APPLICATION_JSON_VALUE
  Scenario: (교사) 학급관리 > 홈 대시보드 > 과제_평가 현황 상세 (과제)
    Given path '/tch/dsbd/status/homewk/detail'
    And param taskId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchDsbdHomewkController @error @MediaType.APPLICATION_JSON_VALUE
  Scenario: (교사) 학급관리 > 홈 대시보드 > 과제_평가 현황 결과 (과제)
    Given path '/tch/dsbd/status/homewk/result'
    And param taskId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

