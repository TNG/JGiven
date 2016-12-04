package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.junit.SimpleScenarioTest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * A variant of {@link com.tngtech.jgiven.integration.spring.SpringScenarioTest} works with a single
 * stage type parameter instead of three.
 * 
 * @param <STAGE> the stage class that contains the step definitions
 */
public class SimpleSpringScenarioTest<STAGE> extends SimpleScenarioTest<STAGE> implements BeanFactoryAware {

    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }

}
