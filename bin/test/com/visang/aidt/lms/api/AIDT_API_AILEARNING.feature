Feature: 비상교육 AIDT API - AILEARNING

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ AiLearningController ------

  @AiLearningController
  Scenario: 대상 평가id 조회
    Given path '/batch/ai/remedy-lrn/create/evl/get-target'
    And param fromDt = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: 자동문항생성-AI 처방 학습-평가
    Given path '/batch/ai/remedy-lrn/create/evl/article'
    And param evlId = 514
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: 대상 과제id 조회
    Given path '/batch/ai/remedy-lrn/create/task/get-target'
    And param fromDt = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: 자동문항생성-AI 처방 학습-과제
    Given path '/batch/ai/remedy-lrn/create/task/article'
    And param taskId = 11
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: 자동문항생성-AI 처방 학습-강제실행
    Given path '/batch/ai/remedy-lrn/create/forced-execute'
    And param fromDt = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: (교사) AI 맞춤 학습 생성-공통/개별 문항출제
    Given path '/tch/ai/custom-lrn/create'
    And json jsonBody = "{ wrterId: 'emaone1-t', claId: 'aa28cf360fe34b8d80cf5146229c811a', textbkId: '16', tabId: '999999', crculId: '2', lrnMethod: '2', pdEvlStDt: '2024-05-29 09:00', aiTutSetAt: 'Y', eamTrget: '1', eamExmNum: '25', eamGdExmMun: '5', eamAvUpExmMun: '5', eamAvExmMun: '5', eamAvLwExmMun: '5', eamBdExmMun: '5', pdEvlEdDt: '' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: AI 맞춤 학습 생성-학습자료 존재여부를 체크
    Given path '/tch/ai/custom-lrn/std-info/check'
    And param wrterId = 'emaone1-t'
    And param claId = 'aa28cf360fe34b8d80cf5146229c811a'
    And param textbkId = 16
    And param crculId = 2
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: AI 맞춤 학습 설정 정보 조회(교사)
    Given path '/tch/ai/custom-lrn/set-info'
    And param tabId = 5360
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: AI 맞춤 학습 초기화
    Given path '/tch/ai/custom-lrn/init'
    And json jsonBody = "{ tabId: '5360' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningController
  Scenario: AI 맞춤 학습 설정 정보 조회(학생)
    Given path '/stnt/ai/custom-lrn/set-info'
    And param tabId = 5360
    And param userId = 'mathreal21-s3'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ AiLearningController ------

  @AiLearningEngController
  Scenario: AI맞춤학습-과제,수업(동일, 개별)
    Given path '/tch/ai/custom-lrn/create/eng'
    And json jsonBody = "{ wrterId: 'emaone1-t', tabId: '1', claId: 'aa28cf360fe34b8d80cf5146229c811a', textbkId: '16', crculId: '4', lrnMethod: '2', pdEvlStDt: '2024-05-29 09:00', aiTutSetAt: 'Y', eamTrget: '1', eamExmNum: '15', eamGdExmMun: '5', eamAvExmMun: '5', eamBdExmMun: '5', pdEvlEdDt: '' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningEngController
  Scenario: 대상 평가id 조회
    Given path '/batch/ai/remedy-lrn/create/evl/get-target/eng'
    And param fromDt = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningEngController
  Scenario: 자동문항생성-AI 처방 학습-평가
    Given path '/batch/ai/remedy-lrn/create/evl/article/eng'
    And param evlId = 514
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningEngController
  Scenario: 대상 과제id 조회
    Given path '/batch/ai/remedy-lrn/create/task/get-target/eng'
    And param fromDt = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningEngController
  Scenario: 자동문항생성-AI 처방 학습-과제
    Given path '/batch/ai/remedy-lrn/create/task/article/eng'
    And param taskId = 11
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @AiLearningEngController
  Scenario: 자동문항생성-AI 처방 학습-강제실행
    Given path '/batch/ai/remedy-lrn/create/forced-execute/eng'
    And param fromDt = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



    