package com.tngtech.jgiven.integration.spring;

import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.ScenarioExecutor;
import com.tngtech.jgiven.impl.StageCreator;

/**
 * Internal class necessary in order to provide the correct ordering of the {@link org.junit.rules.MethodRule}s. Must be public because of
 * {@link SpringMethodRule}s validations.
 * It should not be used directly. Instead, use {@link SpringRuleScenarioTest}.
 *
 * @param <GIVEN>
 * @param <WHEN>
 * @param <THEN>
 *
 * @since 0.13.0
 */
public abstract class InternalSpringScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> implements BeanFactoryAware {

    @ClassRule
    public static final SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    InternalSpringScenarioTest() {
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.getScenario().getExecutor().setStageCreator((StageCreator) beanFactory.getBean( SpringStageCreator.class ));
    }
}
