package com.visang.aidt.lms.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.topic")
public class NatsTopicConfig {
    private String realtimeSendName;
    private String bulkSendName;
}
