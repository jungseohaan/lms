Feature: 비상교육 AIDT API - JWT 토큰 관리 API 테스트

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려면 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url과 공통 변수를 설정
  Background:
    * url "http://localhost:15000"
    * def userId = 'testuser'
    * def timestamp = System.currentTimeMillis()
    * def validRefreshToken = 'validRefreshToken'
    * def invalidRefreshToken = 'invalidRefreshToken'

  @JWT @생성
  Scenario: JWT 토큰 생성
    Given path '/common/jwt'
    And param timestamp = timestamp
    And param id = userId
    When method get
    Then status 200
    And match response ==
    """
    {
      "accessToken": '#notnull',
      "refreshToken": '#notnull',
      "hmac": '#notnull'
    }
    """

  @JWT @갱신
  Scenario: 유효한 Refresh 토큰으로 JWT 토큰 갱신
    Given path '/common/jwt/refresh'
    And request { "refreshToken": "#(validRefreshToken)" }
    When method post
    Then status 200
    And match response ==
    """
    {
      "accessToken": '#notnull',
      "refreshToken": '#(validRefreshToken)',
      "hmac": '#notnull'
    }
    """

  @JWT @갱신 @예외
  Scenario: 유효하지 않은 Refresh 토큰으로 JWT 토큰 갱신
    Given path '/common/jwt/refresh'
    And request { "refreshToken": "#(invalidRefreshToken)" }
    When method post
    Then status 400
    And match response ==
    """
    {
      "accessToken": null,
      "refreshToken": null,
      "hmac": null
    }
    """
