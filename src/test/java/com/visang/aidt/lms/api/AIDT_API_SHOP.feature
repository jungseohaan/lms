Feature: 비상교육 AIDT API - SHOP

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ ShopController ------

  @ShopController
  Scenario: 유저 정보 조회
    Given path '/shop/userinfo'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 상점 정보 조회
    Given path '/shop/item-list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param prchsGdsSeCd = 'P'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 상점 상세 요청
    Given path '/shop/item-detail'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param prchsGdsSeCd = 'S'
    And param itemId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 상점 구매 요청
    Given path '/shop/buy-item'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', prchsGdsSeCd: 'P', itemId: 1, rwdSeCd: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 아이템 사용 요청
    Given path '/shop/use-item'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', prchsGdsSeCd: 'P', itemId: 2 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 마이룸
    Given path '/shop/myroom'
    And param userId = 'vstea1'
    And param claId = '1dfd618eb8fb11ee88c00242ac110002'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 상품 위치 변경
    Given path '/shop/change-item-inv'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', prchsGdsSeCd: 'P', prchsId: 1, invSeCd: 2 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 보상지급
    Given path '/shop/reward'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claID: '0cc175b9c0f1b6a831c399e269772661', rewardList: [ { userId: '430e8400-e29b-41d4-a746-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', rwdAmt: 100 }, { userId: 'vstea1', claId: '1dfd618eb8fb11ee88c00242ac110002', rwdAmt: 100 } ] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @ShopController
  Scenario: 문제정보
    Given path '/shop/mdul-info'
    And param userId = '0cc175b9c0f1b6a831c399e269772661'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param tabId = '2'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @ShopController
  Scenario: 유저상태메시지 변경
    Given path '/shop/user-message'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', rprsGdsAnct: '테스트에요1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



