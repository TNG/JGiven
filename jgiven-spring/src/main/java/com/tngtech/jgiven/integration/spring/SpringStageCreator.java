package com.tngtech.jgiven.integration.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import com.tngtech.jgiven.impl.ByteBuddyStageCreator;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;

/**
 * Main class of JGiven for executing scenarios with spring support.
 * See below on how to configure this bean.
 * <p>
 * Sample Configuration:
 * <pre>
 *	{@literal @}Bean
 *	{@literal @}Scope("prototype")
 *	public SpringStageCreator springScenarioExecutor() {
 *	    return new SpringStageCreator();
 *	}
 * </pre>
 * <p>
 * <strong>The SpringStageCreator is stateful, and thus should use "prototype" scope</strong>
 * @since 0.8.0
 */
public class SpringStageCreator extends ByteBuddyStageCreator {

    private static final Logger log = LoggerFactory.getLogger( SpringStageCreator.class );

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public <T> T createStage(Class<T> stepsClass, StepInterceptor stepInterceptor ) {
        try {
            T bean = applicationContext.getBean( stepsClass );
            Advised advised = (Advised) bean;
            Advisor[] advisors = advised.getAdvisors();
            for( Advisor advisor : advisors ) {
                if( advisor.getAdvice() instanceof SpringStepMethodInterceptor ) {
                    SpringStepMethodInterceptor interceptor = (SpringStepMethodInterceptor) advisor.getAdvice();
                    interceptor.setStepInterceptor(stepInterceptor);
                }
            }
            return bean;
        } catch( NoSuchBeanDefinitionException nbe ) {
            return super.createStage( stepsClass, stepInterceptor );
        } catch( ClassCastException cce ) {
            log.warn( "Class " + ClassUtils.getShortName( stepsClass )
                    + " is not advised with SpringStepMethodInterceptor. Falling back to cglib based proxy, strange things may happen." );
            return super.createStage( stepsClass, stepInterceptor );
        }
    }

}
