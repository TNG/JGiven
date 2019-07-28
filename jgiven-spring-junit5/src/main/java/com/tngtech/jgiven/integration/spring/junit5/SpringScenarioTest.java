package com.tngtech.jgiven.integration.spring.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tngtech.jgiven.integration.spring.SpringStageCreator;
import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.junit5.ScenarioTest;

/**
 * Base class for Spring 5 and JUnit 5 test with three stage classes
 *
 * @param <GIVEN> the GIVEN stage class
 * @param <WHEN> the WHEN stage class
 * @param <THEN> the THEN stage class
 *
 * @since 1.0.0
 */
@ExtendWith( SpringExtension.class )
public class SpringScenarioTest<GIVEN, WHEN, THEN> extends
        ScenarioTest<GIVEN, WHEN, THEN> implements BeanFactoryAware {

    @Override
    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }
}
