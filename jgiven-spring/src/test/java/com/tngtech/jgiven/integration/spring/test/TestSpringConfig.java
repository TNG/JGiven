package com.tngtech.jgiven.integration.spring.test;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.tngtech.jgiven.integration.spring.JGivenStageAutoProxyCreator;
import com.tngtech.jgiven.integration.spring.SpringScenarioExecutor;
import com.tngtech.jgiven.integration.spring.SpringStepMethodInterceptor;

@Configuration
@ComponentScan(basePackages = "com.tngtech.jgiven.integration.spring.test")
public class TestSpringConfig {

    @Bean
    @Scope("prototype")
    public SpringStepMethodInterceptor springStepMethodInterceptor() {
        return new SpringStepMethodInterceptor();
    }

    @Bean
    @Scope("prototype")
    public SpringScenarioExecutor springScenarioExecutor() {
        return new SpringScenarioExecutor();
    }

    /*
     * configure support for {@link JGivenStage} annotation
     */
    @Bean
    public JGivenStageAutoProxyCreator jGivenStageAutoProxyCreator() {
        return new JGivenStageAutoProxyCreator();
    }

    /*
     * example for non-invasive usage of the {@link SpringStepMethodInterceptor}
     * @return BeanNameAutoProxyCreator that proxies regular spring beans
     */
    @Bean
    public BeanNameAutoProxyCreator jGivenBeanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setBeanNames(new String[]{"simpleTestSpringSteps"});
        beanNameAutoProxyCreator.setInterceptorNames(new String[]{"springStepMethodInterceptor"});
        return beanNameAutoProxyCreator;
    }


}
