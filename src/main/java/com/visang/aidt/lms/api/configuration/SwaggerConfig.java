package com.visang.aidt.lms.api.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Swagger 설정 클래스
 */
@OpenAPIDefinition(
        info = @Info(
                title = "AIDT LMS API",
                description = "AIDT LMS API",
                version = "v1"
        )
)
@Configuration
@Profile("!real") // 운영 환경에서는 Swagger 비활성화
public class SwaggerConfig {

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }

    // JWT 적용
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components().addSecuritySchemes("JWT", createAPIKeyScheme()))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Visang AIDT LMS API Documentation")
                        .description("비상교육 AIDT LMS API 문서")
                        .version("1.0.0")
                        .license(new License().name("(c) 비상교육. All rights reserved."))
                );
    }
}
