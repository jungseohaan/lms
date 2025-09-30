Feature: 비상교육 AIDT API - NOTIFICATION

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ StntNtcnController ------

  @StntNtcnController
  Scenario: (학생)알림보기
    Given path '/stnt/ntcn/list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param page = 0
    And param size = 20
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntNtcnController
  Scenario: (학생) 알림_전송
    Given path '/stnt/ntcn/save'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', rcveId: '430e8400-e29b-41d4-a746-446655440000', textbkId: 1, trgetCd: 'P', ntcnTyCd: 1, trgetTyCd: 1, ntcnCn: '', linkUrl: '', stntNm: '', claId: '0cc175b9c0f1b6a831c399e269772661' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntNtcnController
  Scenario: (학생) 알림 전체 읽음 처리
    Given path '/stnt/ntcn/readall'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchNtcnController ------

  @TchNtcnController
  Scenario: (교사)알림보기
    Given path '/tch/ntcn/list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbookId = '1'
    And param trgetCd = '1'
    And param ntcnTyCd = '1'
    And param page = 0
    And param size = 20
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchNtcnController
  Scenario: 알림_전체 읽음 처리
    Given path '/tch/ntcn/readall'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', trgetCd: 'P', ntcnTyCd: '1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchNtcnController
  Scenario: (교사) 알림_전송
    Given path '/tch/ntcn/save'
    And json jsonBody = { 'userId': '430e8400-e29b-41d4-a746-446655440000', 'rcveId': '430e8400-e29b-41d4-a746-446655440000', 'textbkId': 1, 'trgetCd': 'P', 'ntcnTyCd': 1, 'trgetTyCd': 1, 'ntcnCn': '', 'linkUrl': '', 'stntNm': '', 'claId': '0cc175b9c0f1b6a831c399e269772661' }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchNtcnController
  Scenario: 알림 읽기
    Given path '/tch/ntcn/read'
    And json jsonBody = { 'userId': '430e8400-e29b-41d4-a746-446655440000', 'ntcnId': 1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchNtcnController
  Scenario: (교사/교사) 미확인 알림 유무 체크
    Given path '/tch/ntcn/nt-check'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbookId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



