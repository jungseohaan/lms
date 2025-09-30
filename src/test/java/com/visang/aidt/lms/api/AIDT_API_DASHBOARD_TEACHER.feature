Feature: 비상교육 AIDT API - DASHBOARD

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려면 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url과 공통 변수를 설정
  Background:
    * url "http://localhost:15000"
    * def claId = '308ad54bba8f11ee88c00242ac110002'
    * def textbkId = 1
    * def userId = 'vstea50'
    * def jsonBodyTemplate =
    """
    {
      "userId":"550e8400-e29b-41d4-a716-446655440000",
      "sents":"#{sents}",
      "sentsId":#{sentsId}
    }
    """

# ------ TeacherDashboardController ------

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 자기주도학습 현황
    Given path '/tch/dsbd/status/self-lrn/chapter/list'
    And param claId = claId
    And param textbkId = textbkId
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 자기주도학습 현황 상세
    Given path '/tch/dsbd/status/self-lrn/chapter/detail'
    And param claId = claId
    And param textbkId = textbkId
    And param unitNum = 1
    And param submDt = '20240226'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단원별 학생분포
    Given path '/tch/dsbd/status/chapter-unit/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = textbkId
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단원별 학생분포 상세
    Given path '/tch/dsbd/status/chapter-unit/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = textbkId
    And param metaId = 1560
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 가장 최근 수업 정보 및 요약 현황
    Given path '/tch/dsbd/summary'
    And param userId = userId
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param textbookId = textbkId
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 개념별 이해도
    Given path '/tch/dsbd/status/concept-usd/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 16
    And param metaId = ''
    And param kwgMainId = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 개념별 이해도 상세
    Given path '/tch/dsbd/status/concept-usd/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = textbkId
    And param metaId = 870
    And param kwgMainId = 0
    And param stdDt = '20240313'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 영역별 이해도
    Given path '/tch/dsbd/status/area-usd/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 13
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 영역별 이해도 상세
    Given path '/tch/dsbd/status/area-usd/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = 13
    And param areaId = 1559
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학습맵 이해도
    Given path '/tch/dsbd/status/study-map/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = textbkId
    And param metaId = ''
    And param kwgMainId = ''
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학습맵 이해도 (개념)
    Given path '/tch/dsbd/status/study-map/concept'
    And param userId = userId
    And param claId = claId
    And param textbookId = textbkId
    And param metaId = 870
    And param kwgMainId = 915
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학습맵 이해도 (상세)
    Given path '/tch/dsbd/status/study-map/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = textbkId
    And param metaId = 1559
    And param kwgMainId = 1565
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 자주쓰는문장(등록)
    Given path '/tch/dsbd/oftensent/save'
    * def jsonBody =
    """
    {
      "userId":"550e8400-e29b-41d4-a716-446655440000",
      "sents":"의견을 잘 표현함"
    }
    """
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 자주쓰는문장(수정)
    Given path '/tch/dsbd/oftensent/mod'
    * def jsonBody =
    """
    {
      "userId":"550e8400-e29b-41d4-a716-446655440000",
      "sentsId":1,
      "sents":"자세하게 설명함"
    }
    """
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 자주쓰는문장(삭제)
    Given path '/tch/dsbd/oftensent/del'
    * def jsonBody =
    """
    {
      "userId":"550e8400-e29b-41d4-a716-446655440000",
      "sentsId":1
    }
    """
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 자주쓰는문장(목록)
    Given path '/tch/dsbd/oftensents/list'
    And param userId = 'vstea1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 영역별 그래프 (영어)
    Given path '/tch/dsbd/status/area-achievement/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 영역별 그래프 상세 (영어)
    Given path '/tch/dsbd/status/area-achievement/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    And param code = 'listening'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 학습맵 > 언어 형식 (영어)
    Given path '/tch/dsbd/status/study-map/languageFormat/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 학습맵 > 의사소통 (영어)
    Given path '/tch/dsbd/status/study-map/communication/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 학습맵 > 소재 (영어)
    Given path '/tch/dsbd/status/study-map/material/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 학습맵 > 성취 기준 (영어)
    Given path '/tch/dsbd/status/study-map/achievementStandard/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 (영어)
    Given path '/tch/dsbd/status/vocabulary/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    And param sortGbCd = 'A1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단어/문법/발음 > Vocabulary 그래프 상세 (영어)
    Given path '/tch/dsbd/status/vocabulary/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    And param iemId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 (영어)
    Given path '/tch/dsbd/status/grammar/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    And param sortGbCd = 'A1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단어/문법/발음 > Grammar 그래프 상세 (영어)
    Given path '/tch/dsbd/status/grammar/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 1
    And param iemId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 (영어)
    Given path '/tch/dsbd/status/pronunciation/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 2
    And param sortGbCd = 'A1'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario Outline: 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 (영어)
    Given path '/tch/dsbd/status/pronunciation/list'
    And param userId = userId
    And param claId = claId
    And param textbookId = <textbookId>
    And param unitNum = <unitNum>
    And param sortGbCd = '<sortGbCd>'
    And param page = <page>
    And param size = <size>
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    Examples:
      | userId   | claId                                | textbookId | unitNum | sortGbCd | page | size |
      | vstea50  | 308ad5afba8f11ee88c00242ac110002     | 10         | 2       | A1       | 0    | 10   |
      | vstea51  | 308ad5afba8f11ee88c00242ac110003     | 11         | 3       | A2       | 1    | 20   |
      | vstea52  | 308ad5afba8f11ee88c00242ac110004     | 12         | 4       | A3       | 2    | 30   |

  @TeacherDashboardController
  Scenario: 학급관리 > 홈 대시보드 > 단어/문법/발음 > Pronunciation 그래프 상세 (영어)
    Given path '/tch/dsbd/status/pronunciation/detail'
    And param userId = userId
    And param claId = claId
    And param textbookId = 10
    And param unitNum = 2
    And param iemId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 영역별 상세 조회
    Given path '/tch/dsbd/status/area-achievement/detail/info'
    * def jsonBody =
    """
    {
      "userId":"engreal30-t",
      "textbkId":308,
      "claId":"b4d7ae53e473488c921f35beb5ba98c6",
      "unitNum":"1",
      "evaluationAreaCd":"Material"
    }
    """
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200

  @TeacherDashboardController
  Scenario: 성취기준 상세 조회
    Given path '/tch/dsbd/status/study-map/total'
    * def jsonBody =
    """
    {
      "userId":"engreal79-t",
      "textbkId":335,
      "claId":"a6f63a21e3f84f28a1cfd7ee177925d4",
      "unitNum":9,
      "metaId":26353,
      "studyMapCd":"Material"
    }
    """
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200

# ------ EtcController ------

  @EtcController
  Scenario Outline: 오늘의 기분 조회(기분, 에너지에 따른 목록들)
    Given path '/etc/tdymd/list'
    And param cdtnSeCd = <cdtnSeCd>
    And param enrgSeCd = <enrgSeCd>
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200
    Examples:
      | cdtnSeCd | enrgSeCd |
      | 1        | 1        |
      | 2        | 1        |
      | 3        | 1        |
      | 1        | 2        |
      | 2        | 2        |
      | 3        | 2        |
      | 1        | 3        |
      | 2        | 3        |
      | 3        | 3        |

  @EtcController
  Scenario: 학생 오늘의 기분 insert
    Given path '/etc/tdymd/init'
    * def jsonBody =
    """
    {
      "tdyMdId":1,
      "tdyMdRsn":"그냥",
      "stdtId":"re22mma15-s1",
      "claId":"cc86a331d2824c52b1db68a0bf974dd5"
    }
    """
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200