Feature: 비상교육 AIDT API - BOOKMARK

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"



# ------ TchBmkController ------

  @TchBmkController
  Scenario: 북마크 목록보기
    Given path '/tch/mdul/bmk/list'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param scrnSeCd = '1'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbkId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 공유하기
    Given path '/tch/mdul/bmk/share'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', bkmkId : [241, 242] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 설정
    Given path '/tch/mdul/bmk/save'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', scrnSeCd : 1, claId : '0cc175b9c0f1b6a831c399e269772661', textbkId : 1, tabId : 44, moduleId : 411, crculId : '1', cocnrAt : 'N', pdfUrl : 'pdfUrl1', page : 2 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 삭제
    Given path '/tch/mdul/bmk/delete'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', bkmkId : [241, 242] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 태그 수정
    Given path '/tch/mdul/bmk/tag/modify'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', tagId : 1, tagNm : 't_nm_test', clorNum : 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 태그 삭제
    Given path '/tch/mdul/bmk/tag/delete'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', tagId : 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 태그 등록
    Given path '/tch/mdul/bmk/tag/save'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', bkmkId : 1, claId : '0cc175b9c0f1b6a831c399e269772661', tagNm : 't_nm_test', clorNum : 3, textbk_id : 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 태그 등록
    Given path '/tch/mdul/bmk/tag/tagsave'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', claId : '0cc175b9c0f1b6a831c399e269772661', textbkId : 1, tagNm : 't_nm_test', clorNum : 3 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 태그 삭제
    Given path '/tch/mdul/bmk/tag/tagdel'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', tagId : 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 태그 수정
    Given path '/tch/mdul/bmk/tag/tagmod'
    And json jsonBody = "{ userId : '550e8400-e29b-41d4-a716-446655440000', tagId : 1, tagNm : 't_nm_test', clorNum : 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 (모듈별) 정보
    Given path '/tch/mdul/bmk/info'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    And param tabId = '44'
    And param moduleId = '441'
    And param crculId = '1'
    And param scrnSeCd = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchBmkController
  Scenario: 북마크 태그 목록
    Given path '/tch/mdul/bmk/tag/list'
    And param textbkId = '1'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

