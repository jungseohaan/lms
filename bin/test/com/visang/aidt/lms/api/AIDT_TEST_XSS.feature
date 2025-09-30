Feature: 비상교육 AIDT - XSS 방어 테스트 시나리오

  Background:
    * def XssRequestTestHelper = Java.type('com.visang.aidt.lms.api.common.XssRequestTestHelper')
    * def xssRequestTestHelper = new XssRequestTestHelper()

  Scenario: 테스트에 사용할 입력값 정의 및 결과 확인
    * def input = "<script>alert('xss')</script>"
    * def result = xssRequestTestHelper.getParameter(input)
    Then match result == '&lt;script&gt;alert(\'xss\')&lt;/script&gt;'

  Scenario: 배열 입력값 정의 및 결과 확인
    * def inputArray = ["<script>alert('xss')</script>", "<b>bold</b>"]
    * def resultArray = xssRequestTestHelper.getParameterValues(inputArray)
    Then match resultArray == [ '&lt;script&gt;alert(\'xss\')&lt;/script&gt;', '&lt;b&gt;bold&lt;/b&gt;' ]

  Scenario: 헤더 입력값 정의 및 결과 확인
    * def input = "<script>alert('xss')</script>"
    * def result = xssRequestTestHelper.getHeader(input)
    Then match result == '&lt;script&gt;alert(\'xss\')&lt;/script&gt;'

  Scenario: XSS 클리닝 결과 확인
    * def input = "<script>alert('xss')</script>"
    * def result = xssRequestTestHelper.cleanXSS(input)
    Then match result == '&lt;script&gt;alert(\'xss\')&lt;/script&gt;'

  Scenario: HTML 컨텍스트에서 인코딩 결과 확인
    * def input = "<b>bold</b>"
    * def result = xssRequestTestHelper.cleanXSS(input)
    Then match result == '&lt;b&gt;bold&lt;/b&gt;'

  Scenario: JavaScript 컨텍스트에서 인코딩 결과 확인
    * def input = "<script>alert('xss')</script>"
    * def result = xssRequestTestHelper.cleanXSS(input)
    Then match result == '&lt;script&gt;alert(\'xss\')&lt;/script&gt;'

  Scenario: 복잡한 XSS 입력값 정의 및 결과 확인
    * def input = "<b onmouseover='alert(1)'>TEST</b>"
    * def result = xssRequestTestHelper.cleanXSS(input)
    Then match result == '&lt;b onmouseover=\'alert(1)\'&gt;TEST&lt;/b&gt;'
