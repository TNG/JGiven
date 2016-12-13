package com.tngtech.jgiven.integration.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import com.tngtech.jgiven.impl.ByteBuddyStageCreator;
import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
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
    public <T> T createStage( Class<T> stageClass, StepInterceptor stepInterceptor ) {
        try {
            T bean = applicationContext.getBean( stageClass );
            ( (StageInterceptorInternal) bean ).setStepInterceptor( stepInterceptor );
            return bean;
        } catch( NoSuchBeanDefinitionException nbe ) {
            return super.createStage( stageClass, stepInterceptor );
        } catch( ClassCastException cce ) {
            log.warn( "Class " + ClassUtils.getShortName( stageClass )
                    + " is not annotated with @JGivenStage. Falling back to default JGiven proxy. Spring features will not be supported for this stage instance.",
                cce );
            return super.createStage( stageClass, stepInterceptor );
        } catch( Exception e ) {
            log.error( "Error while trying to get the Spring bean for stage class "+stageClass, e );
            return null;
        }

    }

}
