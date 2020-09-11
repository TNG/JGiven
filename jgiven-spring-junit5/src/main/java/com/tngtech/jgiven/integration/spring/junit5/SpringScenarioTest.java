package com.tngtech.jgiven.integration.spring.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.integration.spring.SpringStageCreator;
import com.tngtech.jgiven.junit5.JGivenExtension;

/**
 * Base class for Spring 5 and JUnit 5 test with three stage classes
 *
 * @param <GIVEN> the GIVEN stage class
 * @param <WHEN> the WHEN stage class
 * @param <THEN> the THEN stage class
 *
 * @since 1.0.0
 */
@ExtendWith( {SpringExtension.class, JGivenExtension.class} )
public class SpringScenarioTest<GIVEN, WHEN, THEN> extends
        ScenarioTestBase<GIVEN, WHEN, THEN> implements BeanFactoryAware {

    private Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }

    @Override
    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }
}
