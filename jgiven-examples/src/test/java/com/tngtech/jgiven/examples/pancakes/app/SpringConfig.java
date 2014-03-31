package com.tngtech.jgiven.examples.pancakes.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public Cook cook() {
        return new PanCakeCook();
    }
}
