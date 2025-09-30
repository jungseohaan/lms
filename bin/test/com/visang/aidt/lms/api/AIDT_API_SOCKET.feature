Feature: 비상교육 AIDT API - SOCKET

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ CloudLogController ------

  @CloudLogController
  Scenario: 로그 기록
    Given path '/writelog.json'
    And param message = '테스트'
    And param code = 999
    And param logDiv = 'APP'
    And param level = 'ERROR'
    And param userDiv = 'T'
    And param user_idx = 1111
    And param serverName = 'AIDT'
    When method GET
    Then status 200
    And print response
    And match response.result == 0


  @CloudLogController
  Scenario: 로그 수정
    Given path '/updatelog.json'
    And param idx = 1
    And param message = '테스트'
    And param code = 999
    And param logDiv = 'APP'
    And param level = 'ERROR'
    And param userDiv = 'T'
    And param user_idx = 1111
    And param serverName = 'AIDT'
    When method GET
    Then status 200
    And print response
    And match response.result == 0


  @CloudLogController
  Scenario: 로그 조회
    Given path '/readlog.json'
    And param search_sdate = '2020-12-01'
    And param search_edate = '2020-12-31'
    And param search_str = 'API'
    And param logDiv = 'APP'
    And param level = 'ERROR'
    And param userDiv = 'T'
    And param user_id = '430e8400-e29b-41d4-a746-446655440000'
    And param serverName = 'AIDT'
    When method GET
    Then status 200
    And print response
    And match response.result == 0



# ------ MemberController ------

  @MemberController
  Scenario: 학생 클래스 입장
    Given path '/member/_login.json'
    And param userDiv = 'S'
    And param pwd = 'q12345'
    And param uuid = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = 'bcaf4c85c58c4734872e9d925001376b'
    And param semester = 'semester01'
    And param username = 'Smith Aile'
    And param userphone = '1111111111'
    And param ip = '0.0.0.0'
    And param macAddr = '12:34:56:78:90:AB'
    And param device = 'PC'
    And param os = 'Win10'
    And param browser = 'Chrome'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @MemberController
  Scenario: 학생 클래스 퇴장
    Given path '/member/_logout.json'
    And param uuid = '430e8400-e29b-41d4-a746-446655440000'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StudentController ------

  @StudentController
  Scenario: 학생 클래스 입장
    Given path '/student/_joinclass.json'
    And param classid = '364'
    And param user_idx = '1111'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StudentController
  Scenario: 학생 클래스 퇴장
    Given path '/student/_exitclass.json'
    And param classlogid = '128265'
    And param classid = '364'
    And param user_idx = '1111'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StudentController
  Scenario: 학생 클래스 리스트
    Given path '/student/_studentclasslist.json'
    And param user_idx = '1111'
    And param service = 'mathalive'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TeacherController ------

  @TeacherController
  Scenario: 선생님 클래스 오픈
    Given path '/teacher/_openclass.json'
    And param classid = '364'
    And param tch_idx = '1111'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TeacherController
  Scenario: 선생님 클래스 종료
    Given path '/teacher/_closeclass.json'
    And param classlogid = '128272'
    And param classid = '364'
    And param tch_idx = '1111'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TeacherController
  Scenario: 학생 리스트
    Given path '/teacher/_getclassstudents.json'
    And param classid = '364'
    And param tch_idx = '1111'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


