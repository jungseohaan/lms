Feature: DB: S1, 과제 리포트 API 테스트 결과서

Background:
  * url 'http://localhost:15000'
  * configure headers = { 'Accept': 'application/json', 'Content-Type': 'application/json' }

Scenario: 교사) 과제 리포트 개요 조회
  Given path '/report/homewk/tch/summary'
  And param taskId = '3880'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 학생) 과제 리포트 개요 조회
  Given path '/report/homewk/stnt/summary'
  And param taskId = '3880'
  And param stntId = 'engbook229-s1'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 과제 미응시 학생 목록 조회
  Given path '/report/homewk/unsubmitted'
  And param taskId = '3880'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 리포트 과제 목록 조회
  Given path '/report/homewk/list'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbookId = '1150'
  And param loginUserId = 'engbook229-t'
  And param taskDivision = 'all'
  And param reportStatusType = 'end'
  And param page = '0'
  And param size = '6'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultData != null
  * print '응답데이터: ', response


Scenario: [오류 테스트] 리포트 과제 목록 조회 (잘못된 과제 구분값 전송)
  Given path '/report/homewk/list'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbookId = '1150'
  And param loginUserId = 'engbook229-t'
  And param taskDivision = 'wrongParam'
  And param reportStatusType = 'end'
  And param page = '0'
  And param size = '6'
  When method get
  Then status 200
  And match response.success == false
  And match response.resultData == null
  * print '응답데이터: ', response

Scenario: [오류 테스트] 리포트 과제 목록 조회 (잘못된 리포트 상태값 전송)
  Given path '/report/homewk/list'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbookId = '1150'
  And param loginUserId = 'engbook229-t'
  And param taskDivision = 'all'
  And param reportStatusType = 'wrongParam'
  And param page = '0'
  And param size = '6'
  When method get
  Then status 200
  And match response.success == false
  And match response.resultData == null
  * print '응답데이터: ', response


Scenario: 우리반 채점 결과표 조회 (학급 조회)
  Given path '/report/homewk/scoring/list'
  And param taskId = 3880
  And param page = 0
  And param size = 10
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '우리반 채점 결과표 조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 우리반 채점 결과표 조회 (학생 조회)
  Given path '/report/homewk/scoring/list'
  And param taskId = 3880
  And param submAt = 'Y'
  And param stntId = 'engbook229-s1'
  And param page = 0
  And param size = 10
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '우리반 채점 결과표 조회'
  And match response.resultData != null
  * print '응답데이터: ', response