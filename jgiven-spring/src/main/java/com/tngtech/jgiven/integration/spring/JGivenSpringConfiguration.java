package com.tngtech.jgiven.integration.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Scope;

/**
 * {@code @Configuration} class that registers all beans an post-processors
 * for full JGiven Spring support
 *
 * @since 0.9.4
 */
@Configuration
public class JGivenSpringConfiguration {

    @Bean
    @Scope( "prototype" )
    public SpringStepMethodInterceptor springStepMethodInterceptor() {
        return new SpringStepMethodInterceptor();
    }

    @Bean
    @Scope( "prototype" )
    public SpringScenarioExecutor springScenarioExecutor() {
        return new SpringScenarioExecutor();
    }

    /*
     * configure support for {@link JGivenStage} annotation
     */
    @Bean
    @Role( BeanDefinition.ROLE_INFRASTRUCTURE )
    public JGivenStageAutoProxyCreator jGivenStageAutoProxyCreator() {
        return new JGivenStageAutoProxyCreator();
    }

}
