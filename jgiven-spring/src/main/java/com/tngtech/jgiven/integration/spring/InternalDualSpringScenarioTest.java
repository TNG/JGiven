package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.base.DualScenarioTestBase;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

/**
 * Internal class necessary in order to provide the correct ordering of the {@link org.junit.rules.MethodRule}s. Must be public because of
 * {@link SpringMethodRule}s validations.
 * It should not be used directly. Instead, use {@link SimpleSpringRuleScenarioTest}.
 *
 * @param <GIVEN_WHEN>
 * @param <THEN>
 *
 * @since 0.13.0
 */
public abstract class InternalDualSpringScenarioTest<GIVEN_WHEN, THEN> extends
    DualScenarioTestBase<GIVEN_WHEN, THEN> implements BeanFactoryAware {

    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    InternalDualSpringScenarioTest() {
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.getScenario().setStageCreator(beanFactory.getBean( SpringStageCreator.class ));
    }
}
