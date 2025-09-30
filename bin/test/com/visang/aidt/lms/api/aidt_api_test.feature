#Feature: 샘플 API 테스트
#   # 샘플용으로 제작된 Karate 테스트 스크립트입니다.
#
#  Scenario: 프로필 조회
#    Given url 'http://localhost:15000/v1/api/teachers/1'
#    And def teacherData = { "teacherCode": "1","academyCode": "http://localhost:15000/v1/api/academies/test","name": "홍길동","isManager": true,"loginCnt": 0,"regdate": "2023-11-28T12:00:00Z","regId": "hongildong" }
#    And request teacherData
#    When method get
#    Then status 200

