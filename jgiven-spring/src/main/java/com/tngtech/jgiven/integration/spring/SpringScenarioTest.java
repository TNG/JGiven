package com.tngtech.jgiven.integration.spring;

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
        ScenarioTestBase<GIVEN, WHEN, THEN> implements BeanFactoryAware {

    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( getScenario() );

    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setExecutor( beanFactory.getBean( SpringScenarioExecutor.class ) );
    }
}
