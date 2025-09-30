package com.visang.aidt.lms.api.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

class XssRequestTest {

    private HttpServletRequest request;
    private XssRequestWrapper xssRequestWrapper;

    // 각 테스트 실행 전에 호출되어 필요한 초기 설정을 수행
    @BeforeEach
    public void setUp() {
        request = mock(HttpServletRequest.class);
        xssRequestWrapper = new XssRequestWrapper(request);
    }

    // getParameter 메서드가 스크립트 태그를 인코딩하는지 테스트
    @Test
    public void testGetParameter() {
        when(request.getParameter("param")).thenReturn("<script>alert('xss')</script>");
        String sanitized = xssRequestWrapper.getParameter("param");
        assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", sanitized); // 스크립트 태그가 인코딩되었는지 확인
    }

    // getParameterValues 메서드가 스크립트 태그와 HTML 태그를 인코딩하는지 테스트
    @Test
    public void testGetParameterValues() {
        when(request.getParameterValues("param")).thenReturn(new String[]{"<script>alert('xss')</script>", "<b>bold</b>"});
        String[] sanitizedValues = xssRequestWrapper.getParameterValues("param");

        // null 체크 로직 추가
        assertNotNull("sanitizedValues는 null이 아니어야 함", sanitizedValues);
        assertEquals(2, sanitizedValues.length);
        assertNotNull("첫 번째 요소는 null이 아니어야 함", sanitizedValues[0]);
        assertNotNull("두 번째 요소는 null이 아니어야 함", sanitizedValues[1]);

        assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", sanitizedValues[0]);
        assertEquals("&lt;b&gt;bold&lt;/b&gt;", sanitizedValues[1]); // bold 태그가 인코딩되었는지 확인
    }

    // getHeader 메서드가 스크립트 태그를 인코딩하는지 테스트
    @Test
    public void testGetHeader() {
        when(request.getHeader("header")).thenReturn("<script>alert('xss')</script>");
        String sanitized = xssRequestWrapper.getHeader("header");
        assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", sanitized); // 스크립트 태그가 인코딩되었는지 확인
    }

    // cleanXSS 메서드가 스크립트 태그를 인코딩하는지 테스트
    @Test
    public void testCleanXSS() {
        String input = "<script>alert('xss')</script>";
        String sanitized = xssRequestWrapper.cleanXSS(input);
        assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", sanitized); // 스크립트 태그가 인코딩되었는지 확인
    }

    // HTML 컨텍스트에서 허용된 태그가 인코딩되는지 테스트
    @Test
    public void testEncodeForContext_html() {
        String input = "<b>bold</b>";
        String encoded = xssRequestWrapper.cleanXSS(input);
        assertEquals("&lt;b&gt;bold&lt;/b&gt;", encoded); // bold 태그가 인코딩되었는지 확인
    }

    // JavaScript 컨텍스트에서 스크립트 태그가 인코딩되는지 테스트
    @Test
    public void testEncodeForContext_javascript() {
        String input = "<script>alert('xss')</script>";
        String encoded = xssRequestWrapper.cleanXSS(input);
        assertEquals("&lt;script&gt;alert('xss')&lt;/script&gt;", encoded); // 스크립트 태그가 인코딩되었는지 확인
    }

    // 복잡한 XSS 입력에서 이벤트 핸들러 속성이 인코딩되는지 테스트
    @Test
    public void testComplexXSS() {
        String input = "<b onmouseover='alert(1)'>TEST</b>";
        String sanitized = xssRequestWrapper.cleanXSS(input);
        String expected = "&lt;b onmouseover=\"alert(1)\"&gt;TEST&lt;/b&gt;";
        assertEquals(expected, sanitized); // onmouseover 이벤트가 인코딩되었는지 확인
    }
}
