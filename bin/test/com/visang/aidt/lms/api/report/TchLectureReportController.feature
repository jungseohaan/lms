Feature: DB: S1, 수업 리포트 API 테스트 결과서

Background:
  * url 'http://localhost:15000'
  * configure headers = { 'Accept': 'application/json', 'Content-Type': 'application/json' }

Scenario: [교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기) 1
  Given path '/tch/report/lecture/result/list'
  And param userId = 'engbook229-t'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbkId = 1150
  And param crculId = 18
  And param tabId = 0
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기) 1'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: [교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기2-콘텐츠 정보)
  Given path '/tch/report/lecture/result/detail/mdul'
  And param crculId = 18
  And param tabId = 79885
  And param dtaIemId = '20355'
  And param subId = 0
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기2-콘텐츠 정보)'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: [교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기3-학생답안)
  Given path '/tch/report/lecture/result/detail/stnt'
  And param crculId = 18
  And param tabId = 632925
  And param dtaIemId = '11914'
  And param subId = 0
  And param userId = 'mathbook1261-s1'
  And param reExmNum = 0
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '[교사] 학급관리 > 홈 대시보드 > 수업리포트(자세히보기3-학생답안)'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: [교사] 학급관리 > 홈 대시보드(학생조회) > 수업리포트 (자세히보기)
  Given path '/tch/stnt-srch/report/lecture/detail'
  And param userId = 'engbook229-t'
  And param claId = '49f37b12fe7f463785e38da824f212db'
  And param textbkId = 1150
  And param crculId = 18
  And param stntId = 'engbook229-s1'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '[교사] 학급관리 > 홈 대시보드(학생조회) > 수업리포트 (자세히보기-콘텐츠정보)'
  And match response.resultData != null
  * print '응답데이터: ', response


Scenario: [교사] 홈 대시보드 > 수업 리포트 > 자세히보기 > 총평조회
  Given path '/tch/report/lecture/general-review/info'
  And param textbkTabId = 1004382
  And param userId = 'engbook1241-s2'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '[교사] 홈 대시보드 > 수업 리포트 > 자세히보기 > 총평조회'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 수업 리포트 총평 AI 평어
  Given path '/tch/report/lecture/general-review/ai-evl-word'
  And param textbkTabId = 79885
  And param userId = 'engbook229-s1'
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '수업 리포트 총평 AI 평어'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 답안보기 - 해당 목차의 모든 콘텐츠
  Given path '/tch/report/lecture/result/mdul'
  And param userId = 'engbook226-t'
  And param claId = 'fd509e444d05454e9c314d4b639d3c8f'
  And param textbkId = 1150
  And param crculId = 17
  And param tabId = 78639
  And param subMitAt = 'Y'
  And param page = 0
  And param size = 9
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '답안보기 - 해당 목차의 모든 콘텐츠'
  And match response.resultData != null
  * print '응답데이터: ', response


Scenario: 활동 결과 - 학생별 활동 횟수
  Given path '/tch/report/lecture/result/act'
  And param userId = 'engbook226-t'
  And param claId = 'fd509e444d05454e9c314d4b639d3c8f'
  And param textbkId = 1150
  And param crculId = 29
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '활동 결과 - 학생별 활동 횟수'
  And match response.resultData != null
  * print '응답데이터: ', response

Scenario: 활동 결과 - 해당 목차의 활동
  Given path '/stnt/report/lecture/result/act'
  And param userId = 'engbook1203-t'
  And param claId = 'da3cb7f2cf13462d8e4367902bdebcae'
  And param textbkId = 1150
  And param crculId = 18
  And param stntId = 'engbook1203-s1'
  And param tabId = 937946
  And param dtaIemId = 31494
  When method get
  Then status 200
  And match response.success == true
  And match response.resultMessage == '활동 결과 - 해당 목차의 활동'
  And match response.resultData != null
  * print '응답데이터: ', response
