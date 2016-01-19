package com.tngtech.jgiven.integration.spring;

import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioReportRule;

/**
 * A variant of {@link com.tngtech.jgiven.integration.spring.SpringScenarioTest} works with a single
 * stage type parameter instead of three.
 * 
 * @param <STAGE> the stage class that contains the step definitions
 */
public class SimpleSpringScenarioTest<STAGE> extends SimpleScenarioTestBase<STAGE> implements BeanFactoryAware {

    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( getScenario() );

    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setExecutor( beanFactory.getBean( SpringScenarioExecutor.class ) );
    }

}
