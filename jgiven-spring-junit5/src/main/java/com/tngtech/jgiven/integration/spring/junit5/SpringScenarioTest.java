package com.tngtech.jgiven.integration.spring.junit5;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.integration.spring.SpringStageCreator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Base class for Spring 5 and JUnit 5 test with three stage classes
 *
 * @param <GIVEN> the GIVEN stage class
 * @param <WHEN> the WHEN stage class
 * @param <THEN> the THEN stage class
 *
 * @since 1.0.0
 * @deprecated As of JGiven 3.0.0, use {@link com.tngtech.jgiven.integration.spring.junit6.SpringScenarioTest SpringScenarioTest}
 *             from {@code jgiven-spring-junit6} for Spring 7.x compatibility.
 *             This module will continue to support Spring 6.x but is deprecated for future Spring versions.
 */
@Deprecated(since = "3.0.0", forRemoval = false)
@ExtendWith({SpringExtension.class, JGivenSpringExtension.class})
public class SpringScenarioTest<GIVEN, WHEN, THEN> extends
        ScenarioTestBase<GIVEN, WHEN, THEN> implements BeanFactoryAware {

    private Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }

    /**
     * @deprecated The {@link JGivenSpringExtension} registered by this class already installs
     * the {@link SpringStageCreator}, so being {@link BeanFactoryAware} is no longer
     * required.
     */
    @Override
    @Deprecated(since = "3.0.0", forRemoval = true)
    public void setBeanFactory( BeanFactory beanFactory ) {
        getScenario().setStageCreator( beanFactory.getBean( SpringStageCreator.class ) );
    }
}
