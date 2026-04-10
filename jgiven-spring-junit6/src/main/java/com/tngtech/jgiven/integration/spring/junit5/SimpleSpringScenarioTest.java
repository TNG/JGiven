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
 */
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
