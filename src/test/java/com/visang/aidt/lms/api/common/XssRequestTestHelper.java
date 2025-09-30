package com.visang.aidt.lms.api.common;

import javax.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.*;

public class XssRequestTestHelper {

    private HttpServletRequest request;
    private XssRequestWrapper xssRequestWrapper;

    public XssRequestTestHelper() {
        request = mock(HttpServletRequest.class);
        xssRequestWrapper = new XssRequestWrapper(request);
    }

    public String getParameter(String param) {
        when(request.getParameter("param")).thenReturn(param);
        return xssRequestWrapper.getParameter("param");
    }

    public String[] getParameterValues(String[] params) {
        when(request.getParameterValues("param")).thenReturn(params);
        return xssRequestWrapper.getParameterValues("param");
    }

    public String getHeader(String header) {
        when(request.getHeader("header")).thenReturn(header);
        return xssRequestWrapper.getHeader("header");
    }

    public String cleanXSS(String input) {
        return xssRequestWrapper.cleanXSS(input);
    }
}
