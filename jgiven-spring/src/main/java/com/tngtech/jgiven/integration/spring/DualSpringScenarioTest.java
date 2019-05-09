package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.junit.DualScenarioTest;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Base class for {@link SpringStageCreator} based JGiven tests.
 *
 * Needs to be used with the {@link org.springframework.test.context.junit4.SpringJUnit4ClassRunner}.
 * As a JUnit-rule based alternative, consider using {@link SpringRuleScenarioTest}.
 *
 * @param <GIVEN_WHEN>
 * @param <THEN>
 *
 * @since 0.8.0
 */
public class DualSpringScenarioTest<GIVEN_WHEN, THEN> extends DualScenarioTest<GIVEN_WHEN, THEN> implements BeanFactoryAware {

    @Override
    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }
}
