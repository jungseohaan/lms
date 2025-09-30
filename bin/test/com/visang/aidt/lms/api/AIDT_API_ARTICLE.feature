Feature: 비상교육 AIDT API - ARTICLE

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


  @학생 @성공 @TchArticleController @키변경
  Scenario: (학생) 활동결과 목록 조회하기
    Given path '/tch/eval/repl-article'
    And param articleId = '3484'
    When method GET
    Then status 200
    And print response
    And match $.success == true
    And match $ contains { resultCode: #number }
    And match $.resultData.orgArticleId == '3484'
    And match $.resultData.otherList[0].difyNm == "#string"
    And match $.resultData.otherList[0].questionTypeNm == "#string"


  @TchArticleEngController @키변경
  Scenario: 문항교체-평가, 문항교체-과제
    Given path '/tch/eval/repl-article/eng'
    And param articleId = '3'
    When method GET
    Then status 200
    And match $.resultCode == 200
    And match $.resultData.orgArticleId == 3


  # 테스트 데이터를 찾아서 Excamples에 추가 필요
  @TchArticleEngController @키변경
  Scenario Outline: 문항교체-수업
    Given path '/tch/lecture/mdul/qstn/other/eng'
    And param articleId = '<articleId>'
    When method GET
    Then status <status>
    Examples:
      | status | articleId |
      | 200    | 10         |
      | 200    | 11         |
      | 200    | 12         |


  @TchAutoArticleEngController
  Scenario: 자동문항생성-과제
    Given path '/tch/homewk/auto/qstn/extr/eng'
    And param wrterId = 'emaone2-t'
    And param claId = 'd27dff98537f4ff0af3535cf9788efce'
    And param textbookId = 223
    And param eamExmNum = 6
    And param eamGdExmMun = 1
    And param eamAvExmMun = 2
    And param eamBdExmMun = 3
    And param eamScp = '870,872,956'
    When method GET
    Then status 200
    And match $.resultCode == 200
    And match $.success == true


  @TchAutoArticleEngController
  Scenario: 자동문항생성-평가
    Given path '/tch/eval/auto/qstn/extr/eng'
    And param wrterId = 'emaone2-t'
    And param claId = 'd27dff98537f4ff0af3535cf9788efce'
    And param textbookId = 223
    And param eamExmNum = 6
    And param eamGdExmMun = 1
    And param eamAvExmMun = 2
    And param eamBdExmMun = 3
    And param eamScp = '870,872,956'
    When method GET
    Then status 200
    And match $.resultCode == 200
    And match $.success == true


  @TchAutoArticleEngController
  Scenario: 자동문항생성-수업
    Given path '/tch/lecture/auto/qstn/extr/eng'
    And param wrterId = 'emaone2-t'
    And param claId = 'd27dff98537f4ff0af3535cf9788efce'
    And param textbookId = 223
    And param eamExmNum = 6
    And param eamGdExmMun = 1
    And param eamAvExmMun = 2
    And param eamBdExmMun = 3
    And param eamScp = '870,872,956'
    When method GET
    Then status 200
    And match $.resultCode == 200
    And match $.success == true


