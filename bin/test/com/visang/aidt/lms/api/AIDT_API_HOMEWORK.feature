Feature: 비상교육 AIDT API - HOMEWORK

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ StntHomewkController ------

  @StntHomewkController
  Scenario: 과제 목록 조회
    Given path '/stnt/homewk/list'
    And param userId = 'student41'
    And param claId = '0cc175b9c0f1b6a831c399e269772669'
    And param textbookId = '1'
    And param condition = ''
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: 과제 정보 조회
    Given path '/stnt/homewk/info'
    And param userId = 'student41'
    And param taskId = '11'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: 과제 정보 수정(시작하기)
    Given path '/stnt/homewk/start'
    And json jsonBody = "{ taskId: 11, userId: 'student41' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: (공통) 과제 응시
    Given path '/stnt/homewk/exam'
    And param taskId = '11'
    And param userId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: (공통) 응시(article)정답처리
    Given path '/stnt/homewk/save'
    And json jsonBody = "{ taskId: 11, userId: 'student41', taskResultId: 42, taskIemId: 2580, subId: 0, errata: 1, subMitAnw: 1, subMitAnwUrl: '', taskTime: '', hntUseAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: 과제 자료 제출하기
    Given path '/stnt/homewk/submit'
    And json jsonBody = "{ taskId: 11, userId: 'student41', submAt: 'N' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: 과제 결과 조회
    Given path '/stnt/homewk/result'
    And param taskId = '11'
    And param userId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: 모듈 과제 결과 조회
    Given path '/stnt/homewk/result-info'
    And param taskResultId = '22'
    And param taskIemId = '2580'
    And param subId = '0'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: 과제 답안 초기화
    Given path '/stnt/homewk/init'
    And json jsonBody = "{ taskId: 7, userId: '430e8400-e29b-41d4-a746-446655440000', taskResultId: 15, taskIemId: 2581, subId: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: (공통) 응시(article) 재확인 횟수저장
    Given path '/stnt/homewk/recheck'
    And json jsonBody = "{ taskId: 7, userId: 'student46', taskResultId: '', taskIemId: 2580, subId: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntHomewkController
  Scenario: 과제 ai tutor 대화 내용 저장
    Given path '/stnt/homewk/aitutor/submit/chat'
    And json jsonBody = "{ taskId: 11, userId: 'student41', taskResultId: 42, taskIemId: 2580, chatOrder: 1, chatType: 'auto', aiCall: '연관된 개념을 알려줘', aiReturn: '튜터러스 랩스 api return 값' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ StntReportHomewkController ------

  @StntReportHomewkController
  Scenario: 과제 리포트 목록조회
    Given path '/stnt/report/homewk/list'
    And param stntId = 'student41'
    And param claId = '0cc175b9c0f1b6a831c399e269772669'
    And param textbkId = '1'
    And param condition = 'gubun'
    And param keyword = '3'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntReportHomewkController
  Scenario: 과제 리포트 결과조회(자세히 보기)
    Given path '/stnt/report/homewk/detail'
    And param taskId = 11
    And param stntId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntReportHomewkController
  Scenario: 과제 리포트 결과조회(결과보기) Header
    Given path '/stnt/report/homewk/summary'
    And param taskId = 11
    And param userId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchHomewkController ------

  @TchHomewkController
  Scenario: 과제 목록 조회
    Given path '/tch/homewk/list'
    And param userId = 'teacher1'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbookId = '1'
    And param tmprStrgAt = 'N'
    And param taskSttsCd = ''
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: 과제 정보 조회
    Given path '/tch/homewk/info'
    And param taskId = '11'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: 과제 정보 조회 (상세 미리보기)
    Given path '/tch/homewk/preview'
    And param taskId = '11'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: 과제 결과 조회 (응시중_응시완료)
    Given path '/tch/homewk/result/status'
    And param taskId = '11'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: 과제 정보 삭제
    Given path '/tch/homewk/delete'
    And json jsonBody = "{ taskId: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: 과제 자료설정 수정(조회)
    Given path '/tch/homewk/read-info'
    And param taskId = '11'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  # 나중에 처리 필요
#  @RequestMapping(value = "/tch/homewk/save", method = {RequestMethod.POST})
#    @Operation(summary = "과제 자료설정 수정(저장)", description = "")
#    //@Parameter(name = "evlId", description = "평가 ID", required = true, schema = @Schema(type = "integer", example = "1"))
#    @io.swagger.v3.oas.annotations.parameters.RequestBody(
#            content = @Content(examples = {
#                    @ExampleObject(name = "파라미터", value = "{" +
#                            "\"taskId\":1," +
#                            "\"taskNm\":\"단원과제수학\"," +
#                            "\"pdEvlStDt\":\"2024-01-17 13:00\"," +
#                            "\"pdEvlEdDt\":\"2024-01-17 14:00\"," +
#                            "\"ntTrnAt\":\"Y\"," +
#                            "\"bbsSvAt\":\"Y\"," +
#                            "\"bbsNm\":\"자료실에 저장 테스트\"," +
#                            "\"tag\":\"고등수학\"," +
#                            "\"cocnrAt\":\"Y\"," +
#                            "\"timStAt\":\"Y\"," +
#                            "\"timTime\":\"60:00\"," +
#                            /*"\"prscrStdSetAt\":\"N\"," +
#                            "\"prscrStdStDt\":\"\"," +
#                            "\"prscrStdEdDt\":\"\"," +
#                            "\"prscrStdNtTrnAt\":\"Y\"," + */
#                            "\"aiTutSetAt\":\"Y\"," +
#                            "\"rwdSetAt\":\"Y\"," +
#                            "\"edGidAt\":\"Y\"," +
#                            "\"edGidDc\":\"자료설정 테스트\","+
#                            "\"slfEvlInfo\":{" +
#                                "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
#                                "\"gbCd\":\"2\"," +
#                                "\"wrterId\":\"1\"," +
#                                "\"wrtDt\":\"1\"," +
#                                "\"slfPerEvlClsfCd\":\"1\"," +
#                                "\"slfPerEvlNm\":\"테스트\"," +
#                                "\"stExposAt\":\"Y\"," +
#                                "\"textbkId\":\"1\"," +
#                                "\"tabId\":\"1\"," +
#                                "\"taskId\":\"1\"," +
#                                "\"evlId\":\"1\"," +
#                                "\"setsId\":\"1\"," +
#                                "\"resultDtlId\":\"1\"," +
#                                "\"tmpltId\":\"1\"," +
#                                "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
#                                "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
#                            "}," +
#                            "\"perEvlInfo\":{" +
#                                "\"userId\":\"430e8400-e29b-41d4-a746-446655440000\"," +
#                                "\"gbCd\":\"2\"," +
#                                "\"wrterId\":\"1\"," +
#                                "\"wrtDt\":\"1\"," +
#                                "\"slfPerEvlClsfCd\":\"2\"," +
#                                "\"slfPerEvlNm\":\"테스트\"," +
#                                "\"stExposAt\":\"Y\"," +
#                                "\"textbkId\":\"1\"," +
#                                "\"tabId\":\"1\"," +
#                                "\"taskId\":\"1\"," +
#                                "\"evlId\":\"1\"," +
#                                "\"setsId\":\"1\"," +
#                                "\"resultDtlId\":\"1\"," +
#                                "\"tmpltId\":\"1\"," +
#                                "\"slfPerEvlInfoList\":[{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}," +
#                                "{\"evlDmi\":\"1\",\"evlIem\":\"1\",\"evlStdrCd\":\"1\",\"evlStdrDc\":\"1\"}]" +
#                            "}" +
#                        "}"
#                    )
#            }
#    ))


  @TchHomewkController
  Scenario: 과제 정보 초기화(개발편의 임시성)
    Given path '/tch/homewk/init'
    And json jsonBody = "{ taskId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: 과제 생성(저장)
    Given path '/tch/homewk/create'
    And json jsonBody = "{ wrterId: 'aidt3', claId: '0cc175b9c0f1b6a831c399e269772662', textbookId: 1, taskNm: '단원과제수학', eamMth: 3, eamTrget: 1, eamExmNum: 0, eamGdExmMun: 0, eamAvUpExmMun: 0, eamAvExmMun: 0, eamAvLwExmMun: 0, eamBdExmMun: 0, eamScp: '4,5', setsId: 240, prscrStdSetAt: 'N', prscrStdStDt: '2024.01.26 09:00', prscrStdEdDt: '2024.01.28 18:00', prscrStdNtTrnAt: 'N', prscrStdPdSet: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: 과제 정보 복사
    Given path '/tch/homewk/copy'
    And json jsonBody = "{ taskId: 8 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchHomewkController
  Scenario: (과제) 문항 자동생성 추천 모듈정보 조회
    Given path '/tch/homewk/auto/qstn/extr'
    And param wrterId = 'vstea1'
    And param claId = '1dfd618eb8fb11ee88c00242ac110002'
    And param textbookId = 1
    And param eamExmNum = 6
    And param eamGdExmMun = 0
    And param eamAvUpExmMun = 3
    And param eamAvExmMun = 1
    And param eamAvLwExmMun = 1
    And param eamBdExmMun = 1
    And param eamScp = '870,872,956'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


# ------ TchReportHomewkController ------

  @TchReportHomewkController
  Scenario: [교사] 학급관리 > 홈 대시보드 > 과제리포트
    Given path '/tch/report/homewk/list'
    And param userId = 'teacher1'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbookId = '1'
    And param condition = 'gubun'
    And param keyword = '3'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: [교사] 학급관리 > 홈 대시보드 > 숙제리포트(자세히보기-공통문항)
    Given path '/tch/report/homewk/result/list'
    And param taskId = 11
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: 과제 리포트 결과 조회(자세히 보기) > 모듈
    Given path '/tch/report/homewk/result/detail/mdul'
    And param taskId = 11
    And param taskIemId = 2580
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: (학생조회)과제 결과 조회
    Given path '/tch/stnt-srch/report/homewk/summary'
    And param taskId = 11
    And param userId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: 과제 리포트 결과 조회(자세히 보기) > 학생
    Given path '/tch/report/homewk/result/detail/stnt'
    And param taskId = 11
    And param taskIemId = 2580
    And param subId = 0
    And param userId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: (교사) 과제 결과 조회 (자세히 보기 : 정오표 수정)
    Given path '/tch/report/homewk/result/errata/mod'
    And json jsonBody = "{ taskId: 11, stntId: 'student41', taskIemId: 2580, subId: 0, errata: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: (교사) 과제 모듈 배점 수정반영
    Given path '/tch/report/homewk/result/errata/appl'
    And json jsonBody = "{ taskId: 11 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: (교사) 과제 결과 조회 (자세히 보기 : 피드백 저장)
    Given path '/tch/report/homewk/result/fdb/mod'
    And json jsonBody = "{ taskId: 11, stntId: 'student41', taskIemId: 2580, subId: 0, fdbDc: '테스트 피드백' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: (교사) 과제 결과 보기(결과보기) : 과제 결과 수정
    Given path '/tch/report/homewk/result/mod'
    And json jsonBody = "{ taskId: 11, stntId: 'student41', taskIemId: 2580, subId: 0, taskResultAnct: 2 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: (교사) 과제 결과 보기(결과보기)
    Given path '/tch/report/homewk/result/summary'
    And param taskId = 11
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: [교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기):목록
    Given path '/tch/report/homewk/result/ind/result'
    And param taskId = 11
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: [교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기):모듈
    Given path '/tch/report/homewk/result/ind/mdul'
    And param taskId = 11
    And param userId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: [교사] 학급관리 > 홈 대시보드 > 과제리포트(결과보기)
    Given path '/tch/report/homewk/result/ind/summary'
    And param taskId = 11
    And param userId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: 학생조회 > 과제 리포트 목록조회
    Given path '/tch/stnt-srch/report/homewk/list'
    And param userId = 'teacher1'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param stntId = 'student41'
    And param textbkId = '1'
    And param condition = 'name'
    And param keyword = ''
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: 학생조회 > 과제 결과 조회(자세히 보기)
    Given path '/tch/stnt-srch/report/homewk/detail'
    And param taskId = 11
    And param stntId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchReportHomewkController
  Scenario: 교사 과제리포트 > 공개처리
    Given path '/tch/report/homewk/open'
    And json jsonBody = "{ userId: 'teacher1', taskId: 11 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



 # ------ TchReportHomewkEngService ------

  @TchReportHomewkEngService
  Scenario: 학생조회 > 과제 결과 조회(자세히 보기) [영어]
    Given path '/tch/stnt-srch/report/homewk/detail/eng'
    And param taskId = 11
    And param stntId = 'student41'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200

