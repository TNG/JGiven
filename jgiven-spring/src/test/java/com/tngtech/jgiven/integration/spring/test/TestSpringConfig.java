package com.tngtech.jgiven.integration.spring.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSpringConfig {
    @Bean
    public TestBean testBean() {
        return new TestBean();
    }
}
