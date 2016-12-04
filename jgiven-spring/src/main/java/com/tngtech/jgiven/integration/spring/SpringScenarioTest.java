package com.tngtech.jgiven.integration.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.tngtech.jgiven.junit.ScenarioTest;

/**
 * Base class for {@link SpringStageCreator} based JGiven tests.
 *
 * Needs to be used with the {@link org.springframework.test.context.junit4.SpringJUnit4ClassRunner}.
 * As a JUnit-rule based alternative, consider using {@link SpringRuleScenarioTest}.
 *
 * @param <GIVEN>
 * @param <WHEN>
 * @param <THEN>
 *
 * @since 0.8.0
 */
public class SpringScenarioTest<GIVEN, WHEN, THEN> extends
        ScenarioTest<GIVEN, WHEN, THEN> implements BeanFactoryAware {

    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }
}
