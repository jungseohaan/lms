Feature: 비상교육 AIDT API - MATERIALS

  All rights reserved by CODEBPLAT.
  이 파일을 실행하기 전에 어플리케이션을 구동해 주십시오. 만약 서버 주소를 변경해서 테스트 하시려먼 아래 Background 부분의 url을 변경해 주십시오.

  # 전역으로 url을 설정
  Background:
    * url "http://localhost:15000"


# ------ PortalController ------

  # 이 API는 현재 사용하지 않음
#  @PortalController
#  Scenario: 교사 학교목록조회
#    Given path '/tch/school/list'
#    And param userId = 'student1'
#    When method GET
#    Then status 200
#    And print response
#    And match response.resultCode == 200


  @PortalController
  Scenario: 교사 학년목록조회
    Given path '/portal/gradeList'
    And param userId = 'portal-t1'
    And param schlNm = '비상초등학교'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @PortalController
  Scenario: 교사 반목록조회
    Given path '/portal/classList'
    And param userId = 'portal-t1'
    And param schlNm = '비상초등학교'
    And param gradeCd = '4'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @PortalController
  Scenario: 교사 교과서목록조회
    Given path '/portal/tcTextbookList'
    And json jsonBody = "{ userId: 'portal-t1', claId: '75a5ea87f15011ee9bb8f220af648621', semester: 'semester01', subject: 'mathematics' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @PortalController
  Scenario: 교사 교과서선택
    Given path '/portal/saveTcTextbook'
    And json jsonBody = "{ userId: 'portal-t1', claId: '75a5ea87f15011ee9bb8f220af648621', semester: 'semester01' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @PortalController
  Scenario: 학생 교과서조회
    Given path '/portal/stTextbookInfo'
    And param userId = 'portal-s11'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ QuestionController ------

  @QuestionController @키변경
  Scenario: 자동문제출제
    Given path '/question/questionList'
    And param articleId = 3484
    And param limitNum = 3
    And param gbCd = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ QuestionEngController ------

  @QuestionEngController @키변경
  Scenario: 자동문제출제
    Given path '/question/questionList/eng'
    And param articleId = 3484
    And param limitNum = 3
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntLesnController ------

  @StntLesnController
  Scenario: (학생)학습종료
    Given path '/stnt/lesn/end'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntLesnController
  Scenario: (학생)학습진도율정보전송
    Given path '/stnt/lesn/prog/rate'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param claId = '0cc175b9c0f1b6a831c399e269772661'
    And param textbkId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntLesnController
  Scenario: (학생) 학생의 학습 과정 기준 수행여부 정보 전송
    Given path '/stnt/lesn/prog/std/check'
    And param userId = 'vsstu586'
    And param claId = '308ad67aba8f11ee88c00242ac110002'
    And param textbkId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntLesnController
  Scenario: (학생) 학생의 학습 과정 기준 수행결과 정보 전송
    Given path '/stnt/lesn/prog/std/rate'
    And param userId = 'vsstu586'
    And param claId = '308ad67aba8f11ee88c00242ac110002'
    And param textbkId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntMdulController ------

  @StntMdulController
  Scenario: 손글씨 저장
    Given path '/stnt/lecture/mdul/note/save'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', resultDetailId: 1, hdwrtCn: 'testtestHHEeessstestest' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulController
  Scenario: 손글씨 호출
    Given path '/stnt/lecture/mdul/note/view'
    And param resultDetailId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulController @키변경
  Scenario: 판서(교사 필기)공유 목록 조회
    Given path '/stnt/lecture/mdul/note/share'
    And param moduleId = 1
    And param tabId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulController
  Scenario: 교사 피드백 보기
    Given path '/stnt/lecture/mdul/fdb/share'
    And param resultDetailId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulController
  Scenario: 우수답안보기
    Given path '/stnt/lecture/mdul/exlt/share'
    And param resultDetailId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntMdulHdwrtController ------

  @StntMdulHdwrtController @키변경
  Scenario: (학생) 평가 손글씨 저장
    Given path '/stnt/eval/mdul/hdwrt/save'
    And json jsonBody = "{ evlId: 366, userId: 'vsstu476', evlResultId: 963, moduleId: 3010, subId: 0, hdwrtCn: 'hdwrtCn1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulHdwrtController @키변경
  Scenario: (학생) 평가 손글씨 조회
    Given path '/stnt/eval/mdul/hdwrt/view'
    And param evlId = 366
    And param userId = 'vsstu476'
    And param evlResultId = 963
    And param moduleId = 3010
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulHdwrtController @키변경
  Scenario: (학생) 평가 손글씨(교사 필기) 학생 공유 조회
    Given path '/stnt/eval/mdul/hdwrt/share-list'
    And param evlId = 1
    And param moduleId = 1
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulHdwrtController @키변경
  Scenario: (학생) 과제 손글씨 저장
    Given path '/stnt/homewk/mdul/hdwrt/save'
    And json jsonBody = "{ taskId: 8, userId: 'student51', taskResultId: 22, moduleId: 2580, subId: 0, hdwrtCn: 'hdwrtCn1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulHdwrtController @키변경
  Scenario: (학생) 과제 손글씨 조회
    Given path '/stnt/homewk/mdul/hdwrt/view'
    And param taskId = 8
    And param userId = 'student51'
    And param taskResultId = 22
    And param moduleId = 2580
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulHdwrtController @키변경
  Scenario: (학생) 과제 손글씨(교사 필기) 학생 공유 조회
    Given path '/stnt/homewk/mdul/hdwrt/share-list'
    And param taskId = 1
    And param moduleId = 1
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntMdulQstnController ------

  @StntMdulQstnController
  Scenario: (학생) 정답저장
    Given path '/stnt/lecture/mdul/qstn/save'
    And json jsonBody = "{ resultDetailId: 47, subMitAnw: '1', subMitAnwUrl: 'subMitAnwUrl1', errata: '1', hntUseAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQstnController @키변경
  Scenario: (학생) 재확인횟수저장
    Given path '/stnt/lecture/mdul/qstn/recheck'
    And json jsonBody = "{ userId: 'men1tmp3-s1', textbkTabId: 6160, setsId: 876, qstnList: [ { articleId: 11365, subId: 0, articleTypeSttsCd: 1 }, { articleId: 11366, subId: 0, articleTypeSttsCd: 1 } ] }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQstnController
  Scenario: (학생) 정답있는모듈상세조회
    Given path '/stnt/lecture/mdul/qstn/view'
    And param tabId = 1
    And param userId = 'student1'
    And param textbkId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQstnController @키변경
  Scenario: (학생) 다른문제풀기
    Given path '/stnt/lecture/mdul/qstn/other'
    And json jsonBody = "{ detailId: 47, articleId: '1433', limitNum: '3' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQstnController
  Scenario: (학생) 정답확인
    Given path '/stnt/lecture/mdul/qstn/answ'
    And param resultDetailId = 47
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQstnController
  Scenario: (학생) 모듈수업결과조회
    Given path '/stnt/lecture/mdul/qstn/result-info'
    And param detailId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ StntMdulQuestController ------

  @StntMdulQuestController @키변경
  Scenario: (학생) 질문보기
    Given path '/stnt/mdul/quest/list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    And param tabId = 1
    And param moduleId = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQuestController @키변경
  Scenario: (학생) 질문하기
    Given path '/stnt/mdul/quest'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', tabId: 1, moduleId: 1, qestnCn: '', rcveId: '550e8400-e29b-41d4-a716-446655440000', anmAt: 'N', otoQestnAt: 'N', textbkId: 1, claId: '0cc175b9c0f1b6a831c399e269772661' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQuestController
  Scenario: (학생) 질문 댓글달기
    Given path '/stnt/mdul/quest/comment'
    And json jsonBody = "{ userId: '550e8400-e29b-41d4-a716-446655440000', qestnId: '', answCn: '', anmAt: 'N', textbkId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQuestController @키변경
  Scenario: (학생) 질문하기(읽음처리)
    Given path '/stnt/mdul/quest/readall'
    And json jsonBody = "{ userId: 'vstea1', tabId: 44, moduleId: 441 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQuestController @키변경
  Scenario: (학생) 질문하기(읽지않은갯수)
    Given path '/stnt/mdul/quest/call'
    And param userId = 'vstea1'
    And param tabId = 44
    And param moduleId = 441
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQuestController @키변경
  Scenario: (교사) 질문하기(읽음처리)
    Given path '/tch/mdul/quest/readall'
    And json jsonBody = "{ userId: 'vsstu1', tabId: 44, moduleId: 441 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @StntMdulQuestController @키변경
  Scenario: (교사) 질문하기(읽지않은갯수)
    Given path '/tch/mdul/quest/call'
    And param userId = 'vsstu1'
    And param tabId = 44
    And param moduleId = 441
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchLesnRscController ------

  @TchLesnRscController
  Scenario: (교사) 수업자료 > 수업자료 목록조회(공유 자료실)
    Given path '/tch/lesn-rsc/list'
    And param textbkId = 16
    And param category = 'textbook'
    And param keyword = ''
    And param curriIdList = '915,917,921'
    And param difyIdList = 'MD04,MD05'
    And param creatorIdList = 'freelancer04,cbstest8'
    And param sortGbCd = 1
    And param scrapAt = 'N'
    And param userId = 're22mma37-t'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: (교사) 수업자료 > 수업자료 목록조회(나의 자료실)
    Given path '/tch/lesn-rsc/my/list'
    And param textbkId = 16
    And param category = 'textbook'
    And param keyword = ''
    And param curriIdList = '915,917,921'
    And param difyIdList = 'MD04,MD05'
    And param myuid = 'cbstest8'
    And param sortGbCd = 1
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController @키변경
  Scenario: (교사) 수업자료 > 수업자료 상세조회
    Given path '/tch/lesn-rsc/info'
    And param setsId = 1
    And param userId = 're22mma37-t'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: (교사) 수업자료 > 모듈(콘텐츠) 목록 조회
    Given path '/tch/lesn-rsc/mdul/list'
    And param articleType = 'question'
    And param keyword = ''
    And param curriIdList = '915, 917, 921'
    And param difyIdList = 'MD04,MD05'
    And param creatorIdList = 'cbstest8,inference'
    And param sortGbCd = 2
    And param scrapAt = 'N'
    And param myuid = ''
    And param textbkId = 16
    And param subjectAbilityList = 'communi,connection,inference,processing,solving'
    And param evaluationAreaList = '0054,0055,0056,0057,0058'
    And param questionTypeList = 'chqz,cnqz,esqz,mcqz,ocqz,oiqz,oxqz,saqz,scqz,tfqz'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: (교사) 수업자료 > 모듈(콘텐츠) 나의 목록 조회
    Given path '/tch/lesn-rsc/mdul/my/list'
    And param articleType = 'question'
    And param keyword = ''
    And param curriIdList = '915, 917, 921'
    And param difyIdList = 'MD04,MD05'
    And param sortGbCd = 2
    And param scrapAt = 'N'
    And param myuid = 'cbstest8'
    And param textbkId = 16
    And param subjectAbilityList = 'communi,connection,inference,processing,solving'
    And param evaluationAreaList = '0054,0055,0056,0057,0058'
    And param questionTypeList = 'chqz,cnqz,esqz,mcqz,ocqz,oiqz,oxqz,saqz,scqz,tfqz'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: (교사) 수업자료 > 모듈(콘텐츠) 상세 조회
    Given path '/tch/lesn-rsc/mdul/info'
    And param id = 6576
    And param myuid = 'cbstest8'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: 스크랩(저장)
    Given path '/tch/lesn-rsc/scrp/save'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', dtaCd: 1, dtaId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: 스크랩(호출)
    Given path '/tch/lesn-rsc/scrp/list'
    And param userId = '430e8400-e29b-41d4-a746-446655440000'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController @키변경
  Scenario: 수업 자료실 세트지 삭제
    Given path '/tch/lesn-rsc/delete'
    And json jsonBody = "{ userId: 'aidt3', setsId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController @키변경
  Scenario: 수업 자료실 모듈(콘텐츠) 삭제
    Given path '/tch/lesn-rsc/mdul/delete'
    And json jsonBody = "{ userId: 'aidt3', articleId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController @키변경
  Scenario: 수업 자료 조회 (출제 범위 보기)
    Given path '/tch/lesn-rsc/exam-scope'
    And param textbkId = 16
    And param setsId = 690
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController @키변경
  Scenario: 콘텐츠 조회 (출제 범위 보기)
    Given path '/tch/lesn-rsc/mdul/exam-scope'
    And param textbkId = 16
    And param articleId = 6880
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: [교사] 수업 자료실 > 수업 자료 목록조회 > 검색 필터
    Given path '/tch/lesn-rsc/search-filter/info'
    And param textbkId = 16
    And param wrterId = 'emaone1-t'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: [교사] 수업 자료실 > 콘텐츠 목록조회 > 검색 필터
    Given path '/tch/lesn-rsc/mdul/search-filter/info'
    And param textbkId = 16
    And param wrterId = 'emaone1-t'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: [교사] 수업 자료실 > 콘텐츠 목록조회 > 검색 필터 : 북마크생성
    Given path '/tch/lesn-rsc/bmk/create'
    And json jsonBody = "{ textbkId: '16', wrterId: 'emaone1-t', creatorId: 'cbstest5' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: [교사] 수업 자료실 > 콘텐츠 목록조회 > 검색 필터 : 북마크 삭제
    Given path '/tch/lesn-rsc/bmk/delete'
    And json jsonBody = "{ textbkId: '16', wrterId: 'emaone1-t', creatorId: 'cbstest5' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchLesnRscController
  Scenario: 스크랩(해제)
    Given path '/tch/lesn-rsc/scrp/delete'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', dtaCd: 1, dtaId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchMdulController ------

  @TchMdulController @키변경
  Scenario: (교사)손글씨 저장
    Given path '/tch/lecture/mdul/note/save'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1, textbkNm: '1', tabId: 1, moduleId: 1, subId: 1, hdwrtCn: '1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


    # 이 API는 현재 사용하지 않음
#  @TchMdulController
#  Scenario: (교사)손글씨 호출
#    Given path '/tch/lecture/mdul/note/view'
#    And param userId = '430e8400-e29b-41d4-a746-446655440000'
#    And param claId = '0cc175b9c0f1b6a831c399e269772661'
#    And param textbkId = 1
#    And param tabId = 1
#    And param moduleId = 0
#    When method GET
#    Then status 200
#    And print response
#    And match response.resultCode == 200


  @TchMdulController
  Scenario: 판서(교사 필기)학생 공유
    Given path '/tch/lecture/mdul/note/share'
    And json jsonBody = "{ notId: 1, noteImgUrl: 'urlurl' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchMdulHdwrtController ------

  @TchMdulHdwrtController @키변경
  Scenario: (교사) 평가 손글씨 저장
    Given path '/tch/eval/mdul/hdwrt/save'
    And json jsonBody = "{ evlId: 1, moduleId: 1, subId: 0, hdwrtCn: '2' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulHdwrtController @키변경
  Scenario: (교사) 평가 손글씨 조회
    Given path '/tch/eval/mdul/hdwrt/view'
    And param evlId = 1
    And param moduleId = 1
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulHdwrtController
  Scenario: (교사) 평가 손글씨(교사 필기) 학생 공유
    Given path '/tch/eval/mdul/hdwrt/share'
    And json jsonBody = "{ evlId: 1, hdwrtId: 1, hdwrtImgUrl: '0' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulHdwrtController @키변경
  Scenario: (교사) 과제 손글씨 저장
    Given path '/tch/homewk/mdul/hdwrt/save'
    And json jsonBody = "{ taskId: 1, moduleId: 1, subId: 0, hdwrtCn: '2' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulHdwrtController @키변경
  Scenario: (교사) 과제 손글씨 조회
    Given path '/tch/homewk/mdul/hdwrt/view'
    And param taskId = 1
    And param moduleId = 1
    And param subId = 0
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulHdwrtController
  Scenario: (교사) 과제 손글씨(교사 필기) 학생 공유
    Given path '/tch/homewk/mdul/hdwrt/share'
    And json jsonBody = "{ taskId: 1, hdwrtId: 1, hdwrtImgUrl: '0' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchMdulQstnController ------

  @TchMdulQstnController @키변경
  Scenario: (교사) 정답보기
    Given path '/tch/lecture/mdul/qstn/answ'
    And json jsonBody = "{ tabId: 1, textbkId: 1, claId: 'claid1', setsId: 2 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulQstnController @키변경
  Scenario: (교사) 다시하기
    Given path '/tch/lecture/mdul/qstn/reset'
    And json jsonBody = "{ setsId: 297, articleId: 3, claId: '308ad694ba8f11ee88c00242ac110002', subId: 0 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulQstnController @키변경
  Scenario: (교사) 제출현황 조회
    Given path '/tch/lecture/mdul/qstn/status'
    And param dtaIemId = 827
    And param subId = 0
    And param tabId = 1
    And param textbkId = 1
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param setsId = 2
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulQstnController
  Scenario: (교사) 우수답안선정
    Given path '/tch/lecture/mdul/qstn/exclnt'
    And json jsonBody = "{ detailId: [1,2,3], fdbExpAt: 'Y' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulQstnController
  Scenario: (교사) 피드백 보내기
    Given path '/tch/lecture/mdul/qstn/fdb'
    And json jsonBody = "{ detailId: '53', stdFdbDc: '테스트 피드백', stdFdbUrl: '' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulQstnController @키변경
  Scenario: (교사) 제출현황,개별답안
    Given path '/tch/lecture/mdul/qstn/indi'
    And param dtaIemId = 827
    And param subId = 0
    And param tabId = 1
    And param textbkId = 1
    And param claId = '1dfd6267b8fb11ee88c00242ac110002'
    And param setsId = 2
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchMdulQstnController
  Scenario: (수업) 문항 자동생성 추천 모듈정보 조회
    Given path '/tch/lecture/auto/qstn/extr'
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



# ------ TchStdController ------

  @TchStdController
  Scenario: 교과목록조회
    Given path '/tch/std/list'
    And param userId = 'vstea60'
    And param claId = '308ad6adba8f11ee88c00242ac110002'
    And param textbkId = 1
    And param tmprStrgAt = 'N'
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchStdController
  Scenario: 교과삭제
    Given path '/tch/std/del'
    And json jsonBody = "{ stdId: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchStdController @키변경
  Scenario: 수업자료 생성
    Given path '/tch/std/create'
    And json jsonBody = "{ wrterId: 'aidt3', claId: '0cc175b9c0f1b6a831c399e269772670', textbkId: 1, stdDatNm: '학습자료명1', eamMth: 3, eamScp: '4,5', setsId: 240 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchStdController
  Scenario: 교과 자료설정 수정(조회)
    Given path '/tch/std/read-info'
    And param stdId = 66
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchStdController @키변경
  Scenario: 마지막 화면의 정보를 저장
    Given path '/tch/std/lastpage/save'
    And json jsonBody = "{ wrterId: 'vstea60', claId: '308ad6adba8f11ee88c00242ac110002', textbkId: 1, setsId: 1, articleId: '1433', pageNum: 1, scrnPageSeCd: 1, scrnSeCd: 1 }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchStdController
  Scenario: 마지막 화면의 정보를 호출
    Given path '/tch/std/lastpage/call'
    And param wrterId = 'vstea60'
    And param claId = '308ad6adba8f11ee88c00242ac110002'
    And param textbkId = 1
    And param scrnSeCd = 1
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200



# ------ TchToolsController ------

  @TchToolsController
  Scenario: 펜툴바 조회
    Given path '/tch/tool/bar/call'
    And param userId = '550e8400-e29b-41d4-a716-446655440000'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchToolsController
  Scenario: 펜툴바 저장하기
    Given path '/tch/tool/bar/save'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', penClor: 'black' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchToolsController
  Scenario: 툴편집(저장)
    Given path '/tch/tool/edit/bar/save'
    And json jsonBody = "{ tolId: '1', userId: '2,3', claId: 'www.naver.com', textbkId: '2', userSeCd: 'Y', monitor: '1', attention: '1', pentool: '1', mathtool: '1', timer: '1', picker: '1', bookmark: '1', hideShow: '1', quiz: '1', opinionBoard: '1', whiteBoard: '1', sbjctCd: '1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchToolsController
  Scenario: 툴편집(호출)
    Given path '/tch/tool/edit/bar/call'
    And param userId = 'vstea1'
    And param claId = '1'
    And param textbkId = '1'
    And param userSeCd = 'T'
    And param sbjctCd = '1'
    When method GET
    Then status 200
    And print response
    And match response.resultCode == 200


  @TchToolsController
  Scenario: 툴편집(초기화)
    Given path '/tch/tool/edit/bar/init'
    And json jsonBody = "{ userId: '430e8400-e29b-41d4-a746-446655440000', claId: '0cc175b9c0f1b6a831c399e269772661', textbkId: 1, userSeCd: 'S', sbjctCd: '1' }"
    And request jsonBody
    When method POST
    Then status 200
    And print response
    And match response.resultCode == 200


