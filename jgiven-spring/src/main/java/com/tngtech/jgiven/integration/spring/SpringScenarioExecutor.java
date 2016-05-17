package com.tngtech.jgiven.integration.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import com.tngtech.jgiven.impl.ScenarioExecutor;
import com.tngtech.jgiven.impl.StandaloneScenarioExecutor;

/**
 * Main class of JGiven for executing scenarios with spring support.
 * See below on how to configure this bean.
 * <p>
 * Sample Configuration:
 * <pre>
 *	{@literal @}Bean
 *	{@literal @}Scope("prototype")
 *	public SpringScenarioExecutor springScenarioExecutor() {
 *	    return new SpringScenarioExecutor();
 *	}
 * </pre>
 * <p>
 * <strong>The SpringScenarioExecutor is stateful, and thus should use "prototype" scope</strong>
 * @since 0.8.0
 */
public class SpringScenarioExecutor extends StandaloneScenarioExecutor implements ScenarioExecutor {

    private static final Logger log = LoggerFactory.getLogger( SpringScenarioExecutor.class );

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public <T> T createStageClass( Class<T> stepsClass ) {
        try {
            T bean = applicationContext.getBean( stepsClass );
            Advised advised = (Advised) bean;
            Advisor[] advisors = advised.getAdvisors();
            for( Advisor advisor : advisors ) {
                if( advisor.getAdvice() instanceof SpringStepMethodInterceptor ) {
                    SpringStepMethodInterceptor interceptor = (SpringStepMethodInterceptor) advisor.getAdvice();
                    interceptor.setScenarioMethodHandler( methodHandler );
                    interceptor.setStageTransitionHandler( stageTransitionHandler );
                    interceptor.enableMethodHandling( true );
                }
            }
            return bean;
        } catch( NoSuchBeanDefinitionException nbe ) {
            return super.createStageClass( stepsClass );
        } catch( ClassCastException cce ) {
            log.error( "class " + ClassUtils.getShortName( stepsClass )
                    + " is not advised with SpringStepMethodInterceptor. Falling back to cglib based proxy, strange things may happen." );
            return super.createStageClass( stepsClass );
        }
    }

    @Override
    public StageState getStageState( Object stage ) {
        StageState stageState = stages.get( stage.getClass() );
        return stageState != null ? stageState : super.getStageState( stage );
    }

}
