package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioReportRule;

/**
 * Base class for {@link SpringScenarioExecutor} based JGiven tests
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
        getScenario().setExecutor( beanFactory.getBean( SpringScenarioExecutor.class ) );
    }
}
