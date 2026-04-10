package com.tngtech.jgiven.integration.spring.junit6.config;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJGiven
@ComponentScan(basePackages = "com.tngtech.jgiven.integration.spring.junit6.test")
public class TestSpringConfig {

}
