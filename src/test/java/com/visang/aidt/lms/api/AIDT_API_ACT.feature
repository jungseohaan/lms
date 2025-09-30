Feature: 비상교육 AIDT API - ACT

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"

  @학생 @성공 @StntActController
  Scenario: (학생) 활동결과 목록 조회하기
    Given path "/stnt/act/mdul/list"
    And param userId = "aidt2"
    And param textbkTabId = "1"
    And param actIemId = "10"
    And param stntId = "430e8400-e29b-41d4-a746-446655440000"
    When method GET
    Then status 200
    Then response.status == "SUCCESS"
    And match $ == {"success":true,"resultMessage":"(학생) 활동결과 목록 조회하기","resultCode":200,"paramData":{"stntId":"430e8400-e29b-41d4-a746-446655440000","textbkTabId":"1","actIemId":"10","userId":"aidt2","subId":0},"resultData":{"actResultList":[{"id":15,"actId":6,"actSttsCd":2,"actSttsNm":"활동완료","actWy":2,"actWyNm":"사진","thumbnail":"upload/1/20231220/Jd2i7sypr.png","actSubmitUrl":"upload/1/article/9/1.html","actSubmitDc":"활동제출 내용","actStDt":"2024-02-17 14:44:21","actEdDt":"2024-05-03 10:16:46","fdbDc":null}]}}
    And match $.success == true
    And match $ contains { resultCode: #number }
    And print response.resultMessage
    And print "상태값 : " + response.status
    And print response.resultMessage == "(학생) 활동결과 목록 조회하기"


  @학생 @성공 @StntActController
  Scenario: (학생) 활동결과 제출하기
    Given path "/stnt/act/mdul/submit"
    And json jsonBody = "{ 'actId' : 6, 'userId' : '430e8400-e29b-41d4-a746-446655440000', 'thumbnail' : 'upload/1/20231220/Jd2i7sypr.png', 'actSubmitUrl' : 'upload/1/article/9/1.html', 'actSubmitDc' : '활동제출 내용' }"
    And request jsonBody
    When method POST
    Then status 200
    Then response.status == "SUCCESS"


  @학생 @성공 @StntActController
  Scenario: (학생) 활동결과상세 조회하기
    Given path "/stnt/act/mdul/detail"
    And param actId = "6"
    And param userId = "430e8400-e29b-41d4-a746-446655440000"
    When method GET
    Then status 200
    And print response.resultData
    Then print response.resultData.id == 15
    Then print response.resultData.actId == 6
    Then print response.resultData.thumbnail == "upload/1/20231220/Jd2i7sypr.png"
    Then print response.resultData.actSubmitUrl == "upload/1/article/9/1.html"
    Then print response.resultData.actSubmitDc == "활동제출 내용"


  @교사 @성공 @TchActController
  Scenario: (교사) 활동도구 내려주기
    Given path "/tch/act/mdul/start"
    And json jsonBody = "{ 'userId' : 'aidt2', 'textbkTabId' : 34, 'actIemId' : 10, 'subId' : 0, 'actWy' : 3 }"
    And request jsonBody
    When method POST
    Then status 200
    Then response.status == "SUCCESS"
    And print response.resultData


  @교사 @성공 @TchActController
  Scenario: (교사) 활동도구 종료하기
    Given path "/tch/act/mdul/end"
    And json jsonBody = "{ 'userId' : 'aidt2', 'actId' : 6 }"
    And request jsonBody
    When method POST
    Then status 200
    Then response.status == "SUCCESS"
    And print response.resultData


  @교사 @성공 @TchActController
  Scenario: (교사) 활동도구 목록 조회하기
    Given path "/tch/act/mdul/list"
    And param textbkTabId = "34"
    And param actIemId = "10"
    And param subId = "0"
    When method GET
    Then status 200
    Then response.status == "SUCCESS"
    And print response.resultData
    And print response.resultData.actWyList.length
    And print response.resultData.actWyList.length == 7


  @교사 @성공 @TchActController
  Scenario: (교사) 활동도구 활동중 체크
    Given path "/tch/act/mdul/activity/check"
    And param textbkTabId = "34"
    And param actIemId = "10"
    And param subId = "0"
    When method GET
    Then status 200
    Then response.status == "SUCCESS"
    And print response.resultData
    And print response.resultData.actInfoList.length
    And print response.resultData.actInfoList[0].id == 9


  @교사 @성공 @TchActController
  Scenario: (교사) 활동도구 제출 현황 조회하기
    Given path "/tch/act/mdul/status"
    And param textbkTabId = "1"
    And param actId = "6"
    And param actIemId = "10"
    And param subId = "0"
    When method GET
    Then status 200
    Then response.status == "SUCCESS"
    And print response.resultData


  @교사 @성공 @TchActController
  Scenario: (교사) 활동 도구 탬목록 조회하기
    Given path "/tch/act/mdul/status/tab/list"
    And param textbkTabId = "34"
    And param actIemId = "10"
    And param subId = "0"
    When method GET
    Then status 200
    Then response.status == "SUCCESS"
    And print response.resultData


  @교사 @실패 @TchActController
  Scenario: (교사) 활동결과 피드백 저장(수정 - 실패 케이스)
    Given path "/tch/act/mdul/fdb/save"
    And json jsonBody = "{ 'id' : 6, 'fdbDc' : 'test' }"
    And request jsonBody
    When method POST
    Then status 200
    Then response.status == "SUCCESS"
    Then print response.status
    And print response.resultData
    # 실패 확인
    Then response.resultData.resultMsg == "실패"


  # 성공 테스트 재수행
  @교사 @성공 @TchActController
  Scenario Outline: (교사) 활동결과 피드백 저장(수정 - 성공 케이스)
    Given path "/tch/act/mdul/fdb/save"
    And json jsonBody = "{ 'id' : <id>, 'fdbDc' : '<fdbDc>' }"
    And request jsonBody
    When method POST
    Then status <status>
    # 성공 확인
    Then response.resultData.resultMsg == "성공"
    Examples:
      | status | id | fdbDc |
      | 200    | 8  | test  |
      | 200    | 9  | test  |


  # XSS 테스트 추가
  @XSS
  Scenario: (학생) 활동결과 목록 조회하기 - XSS 테스트
    Given path "/stnt/act/mdul/list"
    And param userId = "<script>alert('XSS');</script>"
    And param textbkTabId = "1"
    And param actIemId = "10"
    And param stntId = "430e8400-e29b-41d4-a746-446655440000"
    When method GET
    Then status 200
    Then response.status == "SUCCESS"
    And match $.paramData.userId != "<script>alert('XSS');</script>"
    And match $.paramData.userId contains "XSS"