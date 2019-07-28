package com.tngtech.jgiven.integration.spring;

import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;

/**
 * Internal class necessary in order to provide the correct ordering of the {@link org.junit.rules.MethodRule}s. Must be public because of
 * {@link SpringMethodRule}s validations.
 * It should not be used directly. Instead, use {@link SimpleSpringRuleScenarioTest}.
 *
 * @param <STAGE>
 *
 * @since 0.13.0
 */
public abstract class InternalSimpleSpringScenarioTest<STAGE> extends SimpleScenarioTestBase<STAGE> implements BeanFactoryAware {

    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    InternalSimpleSpringScenarioTest() {
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.getScenario().setStageCreator(beanFactory.getBean( SpringStageCreator.class ));
    }
}
