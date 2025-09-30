package com.visang.aidt.lms.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.persistence.EntityManagerFactory;

@EnableCaching
@EnableWebMvc
@EnableScheduling
@SpringBootApplication
public class VisangAidtLmsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VisangAidtLmsApiApplication.class, args);
    }

}
