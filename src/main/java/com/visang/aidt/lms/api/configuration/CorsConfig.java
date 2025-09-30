package com.visang.aidt.lms.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * packageName : com.visang.aidt.lms.api.configuration
 * fileName : CorsConfig
 * USER : kil80
 * date : 2024-01-08
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-08         kil80          최초 생성
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*");
    }
}
