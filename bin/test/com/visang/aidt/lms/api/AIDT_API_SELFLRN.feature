Feature: 비상교육 AIDT API - SELFLRN

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ StntSelfLrnAitutorController ------

  @StntSelfLrnAitutorController
  Scenario: 자기주도AI학습 생성
    Given path '/stnt/self-lrn/aitutor/create'
    And json jsonBody = "{ userId: 'qa10-s1', textbkId: 20, stdNm: 'Lesson 1 > pronunciation,Grammar,Reading', enLrngDivIds: '2157,2150,2151', unitNum: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnAitutorController
  Scenario: 자기주도AI학습 다음문제 받기
    Given path '/stnt/self-lrn/aitutor/next-question/receive'
    And json jsonBody = "{ userId: 'qa10-s1', stdId: 403, libtextType1: '어휘' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnAitutorController
  Scenario: 자기주도AI학습 답안 제출
    Given path '/stnt/self-lrn/aitutor/submit/answer'
    And json jsonBody = "{ userId: 'qa10-s1', stdResultId: '758', subMitAnw: '2,3', subMitAnwUrl: 'www.naver.com', errata: '1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnAitutorController
  Scenario: 자기주도AI학습 채팅저장
    Given path '/stnt/self-lrn/aitutor/submit/chat'
    And json jsonBody = "{ userId: 'qa10-s1', stdResultId: 758, chatType: 'aitutor', aiCall: 'aitutor', aiReturn: '문제를 풀어보자' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnAitutorController
  Scenario: 자기주도AI학습이 존재하는 날짜 목록 보기
    Given path '/stnt/self-lrn/aitutor/date'
    And param userId = 'qa10-s1'
    And param stdId = '262'
    And param learningType = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnAitutorController
  Scenario: 자기주도AI학습 초기화 (임시로 푼 문항 삭제)
    Given path '/stnt/self-lrn/aitutor/question-init'
    And param stdId = '262'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntSelfLrnController ------

  @StntSelfLrnController
  Scenario: 학습자료 존재유무 체크
    Given path '/stnt/self-lrn/std-info/check'
    And param userId = 'vsstu1'
    And param textbkId = '1'
    And param claId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 단원 목록 조회
    Given path '/stnt/self-lrn/chapter/list'
    And param userId = 'vsstu1'
    And param textbkId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 단원 개념 목록 조회
    Given path '/stnt/self-lrn/chapter/concept/list'
    And param userId = 'vsstu1'
    And param textbkId = '1'
    And param metaId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 생성
    Given path '/stnt/self-lrn/create'
    And json jsonBody = "{ userId: 'emaone1-s2', textbkId: '16', stdCd: '2', stdNm: '선택학습', stdUsdId: '1450', unitNum: '1', lvlId: 'MD05', lvlNm: '하' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 답안 제출
    Given path '/stnt/self-lrn/submit/answer'
    And json jsonBody = "{ slfResultId: '1', subMitAnw: '2,3', subMitAnwUrl: 'www.naver.com', errata: '2', aiTutUseAt: 'Y', hdwrtCn: '1', hntUseAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 종료 하기
    Given path '/stnt/self-lrn/end'
    And json jsonBody = "{ slfId: '1', edAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 결과 보기
    Given path '/stnt/self-lrn/result/summary'
    And param slfId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 학습내역 목록조회
    Given path '/stnt/self-lrn/lrn/list'
    And param userId = 'vsstu1'
    And param condition = 'name'
    And param keyword = '단원'
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 학습내역 결과보기
    Given path '/stnt/self-lrn/lrn/result'
    And param slfId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 모듈 학습내역 결과 조회
    Given path '/stnt/self-lrn/lrn/result-info'
    And param slfResultId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 (오답시) 유사문항 받기
    Given path '/stnt/self-lrn/similar-question/receive'
    And param slfResultId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: AI 학습 (단원-학습이해도 낮은 지식요인 최대 3개 조회
    Given path '/stnt/self-lrn/usd-low/kwg/list'
    And param userId = 'emaone1-s2'
    And param textbkId = 16
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습-학습역량 표시
    Given path '/stnt/self-lrn/std-cpt/list'
    And param codeGbCd = 'std-cpt'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습-학습방법 및 내용조회
    Given path '/stnt/self-lrn/std-mth/list'
    And param codeGbCd = 'L/C'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습-활동난이도 표시
    Given path '/stnt/self-lrn/act-lvl/list'
    And param codeGbCd = 'act-lvl'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습-답안입력방식 표시
    Given path '/stnt/self-lrn/anw-ipt-ty/list'
    And param codeGbCd = 'anw-ipt-ty'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습-활동유형 표시
    Given path '/stnt/self-lrn/act-ty/list'
    And param codeGbCd = 'act-ty'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: 자기주도학습 단원 목록 조회-영어
    Given path '/stnt/lesn/start'
    And param userId = 'vsstu1'
    And param textbkId = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: [학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 목록 조회하기
    Given path '/stnt/self-lrn/my-word/list'
    And param userId = 'vsstu1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: [학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 플래시카드 start
    Given path '/stnt/self-lrn/my-word/flash/start'
    And param userId = 'vsstu1'
    And param myWordId = '1'
    And param page = 0
    And param size = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: [학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 테스트 start
    Given path '/stnt/self-lrn/my-word/exam/start'
    And param userId = 'vsstu1'
    And param myWordId = '1'
    And param examCd = '1'
    And param page = 0
    And param size = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntSelfLrnController
  Scenario: [학생] 학습관리 > 나의 학습 공간 > 자기주도학습 > 나의 단어장 > 발음연습하기 start
    Given path '/stnt/self-lrn/my-word/articulation/start'
    And param userId = 'vsstu1'
    And param myWordId = '1'
    And param page = 0
    And param size = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntStdAiController ------

  @StntStdAiController
  Scenario: AI학습(초기)
    Given path '/stnt/std/ai/init'
    And param userId = 'vsstu466'
    And param textbookId = '1'
    And param unitNum = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntStdAiController
  Scenario: AI학습(제출-정답확인)
    Given path '/stnt/std/ai/submit'
    And param userId = 'vsstu466'
    And param textbookId = '1'
    And param unitNum = '1'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param id = 1
    And param stdAiId = 1
    And param moduleId = 1
    And param stdUsdId = 1
    And param subMitAnw = '1'
    And param subMitAnwUrl = 'www.naver.com'
    And param errata = 1
    And param aiTutId = '1'
    And param aiTutUseAt = 'Y'
    And param aiTutChtCn = '1'
    And param hdwrtCn = '1'
    And param hntUseAt = 'Y'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntStdAiController
  Scenario: AI학습(종료)
    Given path '/stnt/std/ai/end'
    And json jsonBody = "{ userId: 'vsstu466', stdAiId: 44 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntStdAiController
  Scenario: AI학습(결과보기)
    Given path '/stnt/self-lrn/ai/result'
    And param userId = 'vsstu466'
    And param claId = '308ad54bba8f11ee88c00242ac110002'
    And param textbookId = 1
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntStdAiEngController ------

  @StntStdAiEngController
  Scenario: AI학습(초기)
    Given path '/stnt/std/ai/init/eng'
    And param userId = 'userId1'
    And param claId = 'claId1'
    And param textbkId = 1
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


