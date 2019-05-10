package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.junit.DualScenarioTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * A variant of {@link SpringScenarioTest} works with two
 * stage type parameters instead of three.
 *
 * @param <GIVEN_WHEN> the stage class that contains the step definitions for given and when
 * @param <THEN> the stage class that contains the step definitions for then
 */
public class DualSpringScenarioTest<GIVEN_WHEN,THEN> extends DualScenarioTest<GIVEN_WHEN, THEN> implements BeanFactoryAware {

    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }

}
