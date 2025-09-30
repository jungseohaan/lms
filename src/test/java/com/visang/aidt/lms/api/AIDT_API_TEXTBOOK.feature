Feature: 비상교육 AIDT API - TEXTBOOK

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ TextbookController ------

  @TextbookController
  Scenario: 교과서 커리큘럼 목록 조회
    Given path '/textbook/crcu/list'
    And param textbookIndexId = '1'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TextbookController
  Scenario: 교과서 학습맵 커리큘럼 목록 조회
    Given path '/textbook/meta/crcu/list'
    And param textbookId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


