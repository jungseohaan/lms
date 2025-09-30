Feature: 비상교육 AIDT API - USER

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ StntRewardController ------

  @StntRewardController
  Scenario: 리워드 현황 조회
    Given path '/stnt/reward/status'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntRewardController
  Scenario: 학생 메뉴 조회
    Given path '/stnt/menu'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param menuSeCd = '1'
    And param sveSeCd = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntUserController ------

  @StntUserController
  Scenario: 학생 로그인
    Given path '/stnt/login'
    And param stntId = '430e8400-e29b-41d4-a746-446655440000'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntUserController ------

  @StntUserController
  Scenario: 학생리워드 현황 목록 조회
    Given path '/tch/stnt-srch/reward/list'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param menuSeCd = '1'
    And param sveSeCd = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchUserController ------

  @TchUserController
  Scenario: 교사 로그인
    Given path '/tch/login'
    And param tchId = '550e8400-e29b-41d4-a716-446655440000'
    And param subjId = 'M1MAT'
    And param clsNum = '1'
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ UserController ------

  @UserController
  Scenario: 유저 정보 조회
    Given path '/user/info'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @UserController
  Scenario: 콘텐츠 오류 신고 등록
    Given path '/user/clause-agre/save'
    And json jsonBody = "{ dclrId: 'vsstu1', schlCd: '1111', textbkId: 1, dclrTyCd: 3, dclrSeCd: 2, dclrCn: '신고내용테 테스트' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @UserController
  Scenario: [교사/학생] 약관동의 팝업 정보 조회
    Given path '/user/clause-agre/list'
    And param userId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @UserController
  Scenario: [교사/학생] 약관동의 팝업 update
    Given path '/user/conts-err-dclr/save'
    And param userId = '1'
    And param IndvdlinfoColctUseeAgreAt = 'Y'
    And param rcptn_agre_ymd = '20240307'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


