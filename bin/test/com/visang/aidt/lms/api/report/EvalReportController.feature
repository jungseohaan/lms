Feature: DB: S1, 평가 리포트 API 테스트 결과서

Background:
  * url 'http://localhost:15000'
  * configure headers = { 'Accept': 'application/json', 'Content-Type': 'application/json' }

Scenario: [교사] 평가 리포트 개요 조회
  Given path '/report/eval/tch/summary'
  And param evlId = 122096
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '평가 리포트 개요 조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: [학생] 평가 리포트 개요 조회
  Given path '/report/eval/stnt/summary'
  And param evlId = 122096
  And param stntId = 'engbook229-s1'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '학생) 평가 리포트 개요 조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 평가 미응시 학생 목록 조회
  Given path '/report/eval/unsubmitted'
  And param evlId = 122096
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '평가 미응시 학생 목록 조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 평가 목록 조회
  Given path '/report/eval/list'
  And param userId = 'engbook229-t'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbookId = 1150
  And param loginUserId = 'engbook229-t'
  And param reportStatusType = 'end'
  And param evlSeCd = 2
  And param page = 0
  And param size = 4
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '평가 목록 조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: [실패 테스트] 평가 목록 조회 (없는 loginUserId 호출)
  Given path '/report/eval/list'
  And param userId = 'engbook229-t'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbookId = 1150
  And param loginUserId = 'werwrqwerqwer'
  And param reportStatusType = 'end'
  And param page = 0
  And param size = 4
  When method get
  Then status 200
  And match response.success == false
  And match response.resultMessage == '로그인 유저 정보를 확인해 주세요.'
  And match response.resultData == null
  * print '응답데이터: ', response

Scenario: [실패 테스트] 평가 목록 조회 (유효하지 않은 평가 구분값 전송)
  Given path '/report/eval/list'
  And param userId = 'engbook229-t'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbookId = 1150
  And param loginUserId = 'engbook229-t'
  And param reportStatusType = 'end'
  And param evlSeCd = 123
  And param page = 0
  And param size = 4
  When method get
  Then status 200
  And match response.success == false
  And match response.resultMessage contains '잘못된 평가 구분값입니다.'
  And match response.resultData == null
  * print '응답데이터: ', response

Scenario: [실패 테스트] 평가 목록 조회 (유효하지 않은 리포트 상태값 전송)
  Given path '/report/eval/list'
  And param userId = 'engbook229-t'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbookId = 1150
  And param loginUserId = 'engbook229-t'
  And param reportStatusType = 'wrongStatusCode'
  And param evlSeCd = 2
  And param page = 0
  And param size = 4
  When method get
  Then status 200
  And match response.success == false
  And match response.resultMessage contains '잘못된 리포트 상태 값입니다.'
  And match response.resultData == null
  * print '응답데이터: ', response

Scenario: 우리반 채점 결과표 조회
  Given path '/report/eval/scoring/list'
  And param evlId = 122096
  And param page = 0
  And param size = 10
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '우리반 채점 결과표 조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: AI 처방학습 학생 조회
  Given path '/ai/prscr/check'
  And param evlId = 69791
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == 'AI 처방학습 학생 조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 개념영상문항 목록 조회
  Given path '/report/eval/recommended-questions'
  And param evlId = 87479
  And param page = 0
  And param size = 3
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '개념영상문항 목록 조회'
  And match response.resultData != null
  * print '응답데이터: ', response