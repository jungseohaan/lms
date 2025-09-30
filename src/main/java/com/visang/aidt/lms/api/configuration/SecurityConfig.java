package com.visang.aidt.lms.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Environment env;

    public SecurityConfig(Environment env) {
        this.env = env;
    }

    /**
     * 보안 구성 설정
     * 공통 보안 설정을 먼저 적용하고 'real' 프로파일이 활성화된 경우 추가적인 HSTS 설정 적용
     *
     * @param http HttpSecurity 객체
     * @throws Exception 예외 발생 시
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureCommonSecurity(http);

        if (isRealProfileActive()) {
            configureHsts(http);
        }
    }

    /**
     * 공통 보안 설정 구성
     * 특정 URL 경로에 대한 접근을 허용하고, 개발 중일 때만 CSRF 및 frameOptions 비활성화
     *
     * @param http HttpSecurity 객체
     * @throws Exception 예외 발생 시
     */
    private void configureCommonSecurity(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/visang/metric/prometheus").permitAll()
                .antMatchers(HttpMethod.GET, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/**").permitAll()
                .antMatchers(HttpMethod.PATCH, "/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/**").permitAll()
                .and()
                // 개발 중일 때만 허용 (운영으로 전환 시 주석 해제 필요)
                .csrf().disable()
                .headers().frameOptions().disable();
    }

    /**
     * HTTP Strict Transport Security (HSTS) 설정 구성
     * 'real' 프로파일이 활성화된 경우에만 적용
     *
     * @param http HttpSecurity 객체
     * @throws Exception 예외 발생 시
     */
    private void configureHsts(HttpSecurity http) throws Exception {
        http.headers()
                .httpStrictTransportSecurity()
                .includeSubDomains(true)
                .maxAgeInSeconds(31536000);
    }

    /**
     * 'real' 프로파일 활성화 확인
     *
     * @return 'real' 프로파일이 활성화된 경우 true, 그렇지 않은 경우 false
     */
    private boolean isRealProfileActive() {
        return Arrays.asList(env.getActiveProfiles()).contains("real");
    }
}
