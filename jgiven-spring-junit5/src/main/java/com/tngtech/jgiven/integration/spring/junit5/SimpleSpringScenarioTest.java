package com.tngtech.jgiven.integration.spring.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.integration.spring.SpringStageCreator;
import com.tngtech.jgiven.junit5.JGivenExtension;

/**
 * Base class for Spring 5 and JUnit 5 test with only one stage class parameter
 *
 * @param <STAGE> the stage class
 *
 * @since 1.0.0
 * @deprecated As of JGiven 3.0.0, use {@link com.tngtech.jgiven.integration.spring.junit6.SimpleSpringScenarioTest SimpleSpringScenarioTest}
 *             from {@code jgiven-spring-junit6} for Spring 7.x compatibility.
 *             This module will continue to support Spring 6.x but is deprecated for future Spring versions.
 */
@Deprecated(since = "3.0.0", forRemoval = false)
@ExtendWith( {SpringExtension.class, JGivenExtension.class} )
public class SimpleSpringScenarioTest<STAGE> extends
        SimpleScenarioTestBase<STAGE> implements BeanFactoryAware {

    private Scenario<STAGE, STAGE, STAGE> scenario = createScenario();

    @Override
    public Scenario<STAGE, STAGE, STAGE> getScenario() {
        return scenario;
    }

    @Override
    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }
}
