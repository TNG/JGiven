package com.tngtech.jgiven.examples.pancakes.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.tngtech.jgiven.examples.pancakes.app.SpringConfig;
import com.tngtech.jgiven.integration.spring.EnableJGiven;

@Configuration
@EnableJGiven
@Import( value = SpringConfig.class )
@ComponentScan( basePackages = "com.tngtech.jgiven.examples.pancakes.test")
public class TestSpringConfig {
}
