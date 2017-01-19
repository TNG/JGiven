package com.tngtech.jgiven.integration.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code @Configuration} class that registers all beans an post-processors
 * for full JGiven Spring support
 *
 * @since 0.9.4
 */
@Configuration
public class JGivenSpringConfiguration {

    @Bean
    public SpringStageCreator springStageCreator() {
        return new SpringStageCreator();
    }

    @Bean
    public static JGivenBeanFactoryPostProcessor jGivenPostBeanProcessor() {
        return new JGivenBeanFactoryPostProcessor();
    }

}
