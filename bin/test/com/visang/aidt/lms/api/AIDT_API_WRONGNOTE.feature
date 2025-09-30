Feature: 비상교육 AIDT API - WRONGNOTE

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ StntWrongnoteController ------

  @StntWrongnoteController
  Scenario: 오답노트 목록조회
    Given path '/stnt/wrong-note/list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param condition = 'name'
    And param keyword = '수플러_샘플_초4_1_240119'
    And param page = '0'
    And param size = '10'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntWrongnoteController
  Scenario: 오답노트 오답 목록 조회
    Given path '/stnt/wrong-note/won-asw/list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param wrtYmd = '20240123'
    And param wonAnwClsfCd = '4'
    And param wonAnwNm = '수플러_샘플_초4_1_240119'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntWrongnoteController
  Scenario: 오답노트 오답 모듈 태그정보 저장(수정)
    Given path '/stnt/wrong-note/won-asw/tag/save'
    And json jsonBody = "{ wonAswId: 1, wonTag: '1', gubun: '1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


