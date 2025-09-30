package com.visang.aidt.lms.api.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.encoder.Encode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class XssFilter extends OncePerRequestFilter {

    /**
     * HTTP 요청에 대해 XSS 필터를 적용
     * 요청을 XssRequestWrapper로 래핑하여 필터 체인에 전달
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 필터 체인 실행 중 발생할 수 있는 서블릿 예외
     * @throws IOException      입출력 예외 발생 시 던져짐
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        boolean shouldLog = !"/".equals(requestUri);
        
        if (shouldLog) {
            log.debug("XSS 필터 시작: {}", requestUri);
        }
        
        HttpServletRequest wrappedRequest = new XssRequestWrapper(request);
        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            if (shouldLog) {
                log.debug("XSS 필터 종료: {}", requestUri);
            }
        }
    }
}

@Slf4j
class XssRequestWrapper extends HttpServletRequestWrapper {

    /**
     * HttpServletRequest 객체를 래핑하여 XSS 필터링을 적용
     *
     * @param request 원본 HttpServletRequest 객체
     */
    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 특정 파라미터의 모든 값에 대해 XSS 필터링을 적용한 후 반환
     *
     * @param parameter 요청 파라미터 이름
     * @return 필터링된 파라미터 값 배열
     */
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        String[] encodedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    /**
     * 특정 파라미터의 값을 XSS 필터링한 후 반환
     *
     * @param parameter 요청 파라미터 이름
     * @return 필터링된 파라미터 값
     */
    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return cleanXSS(value);
    }

    /**
     * 특정 헤더의 값을 XSS 필터링한 후 반환
     *
     * @param name 헤더 이름
     * @return 필터링된 헤더 값
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXSS(value);
    }

    /**
     * 요청의 파라미터 맵을 XSS 필터링한 후 반환
     *
     * @return 필터링된 파라미터 맵
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, String[]> sanitizedMap = new HashMap<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] sanitizedValues = new String[entry.getValue().length];
            for (int i = 0; i < entry.getValue().length; i++) {
                sanitizedValues[i] = cleanXSS(entry.getValue()[i]);
            }
            sanitizedMap.put(entry.getKey(), sanitizedValues);
        }
        return sanitizedMap;
    }

    /**
     * 문자열 값에 대해 XSS 필터링을 수행
     *
     * @param value 필터링할 문자열 값
     * @return 필터링된 문자열 값
     */
    String cleanXSS(String value) {
        return Optional.ofNullable(value)
                .map(this::sanitizeHtml)
                .orElse(null);
    }

    /**
     * HTML 콘텐츠를 안전하게 인코딩
     *
     * @param value 인코딩할 문자열 값
     * @return 인코딩된 문자열 값
     */
    private String sanitizeHtml(String value) {
        try {
            String sanitized = Encode.forHtmlContent(value);
            sanitized = encodeAttributes(sanitized);
            return sanitized;
        } catch (IllegalArgumentException e) {
            log.warn("HTML 인코딩 실패 - 잘못된 입력값: {}", e.getMessage());
            return value;
        } catch (OutOfMemoryError e) {
            log.error("HTML 인코딩 실패 - 메모리 부족: {}", e.getMessage());
            return value;
        } catch (StackOverflowError e) {
            log.error("HTML 인코딩 실패 - 스택 오버플로우: {}", e.getMessage());
            return value;
        } catch (Exception e) {
            log.error("HTML 인코딩 중 예상치 못한 오류: {}", e.getMessage(), e);
            return value;
        }
    }

    /**
     * HTML 태그 속성 내의 스크립트를 인코딩하여 XSS 공격을 방지
     *
     * @param value 인코딩할 HTML 콘텐츠
     * @return 인코딩된 HTML 콘텐츠
     */
    private String encodeAttributes(String value) {
        Pattern pattern = Pattern.compile("on(\\w+)=['\"](.*?)['\"]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String event = matcher.group(1);
            String jsCode = matcher.group(2);
            String encodedJsCode = Encode.forJavaScript(jsCode);
            matcher.appendReplacement(sb, "on" + event + "=\"" + encodedJsCode + "\"");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

