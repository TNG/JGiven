package com.tngtech.jgiven.integration.spring.junit5;

import com.tngtech.jgiven.junit5.DualScenarioTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tngtech.jgiven.integration.spring.SpringStageCreator;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;

/**
 * Base class for Spring 5 and JUnit 5 test with two stage class parameter
 *
 * @param <GIVEN_WHEN> the GIVEN and WHEN stage class
 * @param <THEN> the THEN stage class
 *
 * @since 1.0.0
 */
@ExtendWith( SpringExtension.class )
public class DualSpringScenarioTest<GIVEN_WHEN, THEN> extends
        DualScenarioTest<GIVEN_WHEN, THEN> implements BeanFactoryAware {

    @Override
    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }
}
