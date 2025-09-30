Feature: 비상교육 AIDT API - ENGTEMP

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ HomeworkController ------

  @HomeworkController
  Scenario: (숙제)교과템플릿 시작
    Given path '/stnt/homewk/engtemp/start'
    And json jsonBody = { "tasktId":1397, "engTempId":17, "scriptId":2334, "tmpltActvId":34622 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController @error @쿼리오류
  Scenario: (숙제)교과템플릿 활동 여부
    Given path '/tch/homewk/engtemp/isstudy'
    And json jsonBody = { "taskId":1397, "engTempId":17, "scriptId":2334, "tmpltActvIds":"34622,34618,34619" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController @error @쿼리오류
  Scenario: (숙제)교과템플릿 종료
    Given path '/tch/homewk/engtemp/end'
    And json jsonBody = { "engTempResultId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController @error @쿼리오류 @키변경
  Scenario: (숙제)템플릿 결과 저장
    Given path '/stnt/homewk/engtemp/answer'
    And json jsonBody = { "engTempResultId":1, "libtextId":1, "libtextDialogId":1, "articleId":1, "taskIemId":1, "dfcltLvlYy":1, "anwInptTy":1, "tmpltDtlActvVl":"string", "errata":1, "subMitAnw":"string", "subMitAnwUrl":"string", "notUdstdTf":1, "aitutorRslt":"string", "eakStDt":"string" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController @error @쿼리오류
  Scenario: (숙제)교과템플릿 마감하기
    Given path '/tch/homewk/engtemp/deadline'
    And json jsonBody = { "taskId":1, "engTempId":1, "scriptId":1, "tmpltActvId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController
  Scenario: (숙제)교과템플릿 결과공개 여부 변경
    Given path '/tch/homewk/engtemp/rsltrlsat'
    And json jsonBody = { "engTempResultId":1, "rsltRlsAt":"Y" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController
  Scenario: (숙제)교과템플릿 word 이해못한 인원 카운트(클래스단위)
    Given path '/tch/homewk/engtemp/notudstdcnt'
    And json jsonBody = { "taskId":1341, "engTempId":8, "scriptId":1743, "tmpltActvId":34619 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController
  Scenario: (숙제)교과템플릿 학생답 전달
    Given path '/tch/homewk/engtemp/useransw'
    And json jsonBody = { "engTempResultId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @HomeworkController
  Scenario: (숙제)교과템플릿 제출인원, 총인원, 제출률
    Given path '/tch/homewk/engtemp/sbmtInfo'
    And json jsonBody = { "taskId":1341, "engTempId":8, "scriptId":1743, "tmpltActvId":34622 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ LesnRscController ------

  @LesnRscController
  Scenario: (교과자료)교과템플릿 시작
    Given path '/tch/lesn-rsc/engtemp/start'
    And json jsonBody = { "textbkTabId":5059, "engTempId":17, "scriptId":2334, "tmpltActvId":34619 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController
  Scenario: (교과자료)교과템플릿 활동 여부
    Given path '/tch/lesn-rsc/engtemp/isstudy'
    And json jsonBody = { "textbkTabId":5059, "engTempId":17, "scriptId":2334, "tmpltActvIds":"34618,34619,34622" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController
  Scenario: (교과자료)교과템플릿 종료
    Given path '/tch/lesn-rsc/engtemp/end'
    And json jsonBody = { "textbkTabId":5059, "engTempId":17, "scriptId":2334, "tmpltActvId":34619 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController @키변경
  Scenario: (교과자료)교과템플릿 문제 내려주기
    Given path '/stnt/lesn-rsc/engtemp/question'
    And json jsonBody = { "resultDetailId":9399, "engTempId":17, "scriptId":2334, "tmpltActvId":34618, "libtextId":21321, "libtextDialogId":32131, "pkey":213, "skey":231, "skeys":"1,2,3,4", "articleId":1, "dfcltLvlTy":1, "anwInptTy":1, "tmpltDtlActvVl":"" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController
  Scenario: (교과자료)교과템플릿 결과 저장
    Given path '/stnt/lesn-rsc/engtemp/answer'
    And json jsonBody = [{ "engTempResultDetailId":1, "errata":1, "subMitAnw":"string", "subMitAnwUrl":"string", "notUdstdTf":1, "aitutorRslt":"string", "lastVoiceFileId":1 }, { "engTempResultDetailId":1, "errata":1, "subMitAnw":"string", "subMitAnwUrl":"string", "notUdstdTf":1, "aitutorRslt":"string", "lastVoiceFileId":1 }, { "engTempResultDetailId":1, "errata":1, "subMitAnw":"string", "subMitAnwUrl":"string", "notUdstdTf":1, "aitutorRslt":"string", "lastVoiceFileId":1 }]
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController
  Scenario: (교과자료)교과템플릿 마감하기
    Given path '/tch/lesn-rsc/engtemp/deadline'
    And json jsonBody = { "textbkTabId":5059, "engTempId":17, "scriptId":2334, "tmpltActvId":34619 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController
  Scenario: (교과자료)교과템플릿 word 이해못한 인원 카운트(클래스단위)
    Given path '/tch/lesn-rsc/engtemp/notudstdcnt'
    And json jsonBody = { "textbkTabId":5059, "engTempId":17, "scriptId":2334, "tmpltActvId":34619 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController
  Scenario: (교과자료)교과템플릿 학생답 전달
    Given path '/tch/lesn-rsc/engtemp/useransw'
    And json jsonBody = { "engTempResultId":52 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @LesnRscController
  Scenario: (교과자료)교과템플릿 제출인원별 정답, 총인원, 제출률
    Given path '/tch/lesn-rsc/engtemp/sbmtInfo'
    And json jsonBody = { "textbkTabId":5059, "engTempId":17, "scriptId":2334, "tmpltActvId":34618 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ SelfLrnController ------

  @SelfLrnController
  Scenario: (자기주도)교과템플릿 시작
    Given path '/tch/self-lrn/engtemp/start'
    And json jsonBody = { "stdResultId":1, "engTempId":1, "scriptId":1, "tmpltActvId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @SelfLrnController
  Scenario: (자기주도)교과템플릿 활동 여부
    Given path '/tch/self-lrn/engtemp/isstudy'
    And json jsonBody = { "resultDetailId":1, "engTempId":1, "scriptId":1, "tmpltActvIds":"34604,34609,34610,34612,34613,34614,34615,34616,34617" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @SelfLrnController
  Scenario: (자기주도)교과템플릿 종료
    Given path '/tch/self-lrn/engtemp/end'
    And json jsonBody = { "engTempResultId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @SelfLrnController @error @파라미터오류 @키변경
  Scenario: (자기주도)교과템플릿 결과 저장
    Given path '/stnt/self-lrn/engtemp/answer'
    And json jsonBody = { "engTempResultId":1, "libtextId":1, "libtextDialogId":1, "articleId":1, "taskIemId":1, "dfcltLvlYy":1, "anwInptTy":1, "tmpltDtlActvVl":"string", "errata":1, "subMitAnw":"string", "subMitAnwUrl":"string", "notUdstdTf":1, "aitutorRslt":"string", "eakStDt":"string" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


    # 해당 API는 현재 사용하지 않음
#  @SelfLrnController
#  Scenario: (자기주도)교과템플릿 마감하기
#    Given path '/tch/self-lrn/engtemp/deadline'
#    And json jsonBody = { "stdId":1, "engTempId":1, "scriptId":1, "tmpltActvId":1 }
#    And request jsonBody
#    When method POST
#    Then status 200
#    And print response
#    And match response.resultCode == 200


  @SelfLrnController
  Scenario: (자기주도)교과템플릿 결과공개 여부 변경
    Given path '/tch/self-lrn/engtemp/rsltrlsat'
    And json jsonBody = { "engTempResultId":1, "rsltRlsAt":"Y" }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @SelfLrnController
  Scenario: (자기주도)교과템플릿 word 이해못한 인원 카운트(클래스단위)
    Given path '/tch/self-lrn/engtemp/notudstdcnt'
    And json jsonBody = { "stdId":1, "engTempId":1, "scriptId":1, "tmpltActvId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @SelfLrnController
  Scenario: (자기주도)교과템플릿 학생답 전달
    Given path '/tch/self-lrn/engtemp/useransw'
    And json jsonBody = { "engTempResultId":1 }
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



