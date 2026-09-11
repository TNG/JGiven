package com.tngtech.jgiven.integration.spring.junit6;

import com.tngtech.jgiven.base.DualScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.integration.spring.SpringStageCreator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Base class for Spring and JUnit 6 test with two stage class parameters
 *
 * @param <GIVEN_WHEN> the GIVEN and WHEN stage class
 * @param <THEN>       the THEN stage class
 * @since 1.0.0
 */
@ExtendWith({SpringExtension.class, JGivenSpringExtension.class})
public class DualSpringScenarioTest<GIVEN_WHEN, THEN> extends
        DualScenarioTestBase<GIVEN_WHEN, THEN> implements BeanFactoryAware {

    private Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN> getScenario() {
        return scenario;
    }

    /**
     * @deprecated The {@link JGivenSpringExtension} registered by this class already installs
     * the {@link SpringStageCreator}, so being {@link BeanFactoryAware} is no longer
     * required.
     */
    @Override
    @Deprecated(since = "3.0.0", forRemoval = true)
    public void setBeanFactory(BeanFactory beanFactory) {
        getScenario().setStageCreator(beanFactory.getBean(SpringStageCreator.class));
    }
}
