package com.visang.aidt.lms.api;

import com.visang.aidt.lms.api.common.XssFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${airflow.secret.key}")
    private String airflowBatchSecret;

    /**
     * XSS 필터를 등록
     * 모든 URL 패턴에 대해 XssFilter를 적용하며, 필터의 순서를 1로 설정
     *
     * @return XssFilter를 등록하는 FilterRegistrationBean 객체
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilter() {
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssFilter());
        registrationBean.setOrder(1);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    /**
     * 필터 등록을 위한 ServletContextInitializer Bean을 등록
     * 현재는 추가적인 초기화 작업 없이 빈으로 등록
     *
     * @return 빈 등록을 위한 ServletContextInitializer 객체
     */
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
        };
    }

    // /airflow/batch/** 전용 헤더 검사 인터셉터
    @Bean
    public HandlerInterceptor airflowAuthInterceptor() {
        return new HandlerInterceptor() {
            private static final String HEADER_KEY = "X-Airflow-Secret";
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String header = request.getHeader(HEADER_KEY);
                if (header == null || header.isBlank()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unauthorized: missing X-Airflow-Secret header");
                    return false;
                }

                return header.equals(airflowBatchSecret);
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(airflowAuthInterceptor())
                .addPathPatterns("/airflow/batch/**");
    }
}