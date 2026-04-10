package com.tngtech.jgiven.integration.spring.junit5.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.tngtech.jgiven.integration.spring.EnableJGiven;

@Configuration
@EnableJGiven
@ComponentScan( basePackages = "com.tngtech.jgiven.integration.spring.junit5.test" )
public class TestSpringConfig {

}
