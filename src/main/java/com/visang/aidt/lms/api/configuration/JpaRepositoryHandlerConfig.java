package com.visang.aidt.lms.api.configuration;

import com.visang.aidt.lms.api.repository.validator.UserEventHandler;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class JpaRepositoryHandlerConfig {

    @Bean
    UserEventHandler userEventHandler() {
        return new UserEventHandler();
    }
}
