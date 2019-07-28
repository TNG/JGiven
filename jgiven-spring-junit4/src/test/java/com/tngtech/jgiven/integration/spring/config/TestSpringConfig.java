package com.tngtech.jgiven.integration.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.tngtech.jgiven.integration.spring.EnableJGiven;

@Configuration
@EnableJGiven
@ComponentScan( basePackages = "com.tngtech.jgiven.integration.spring.test" )
public class TestSpringConfig {

}
