package com.tngtech.jgiven.integration.spring.test;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import com.tngtech.jgiven.integration.spring.JGivenStageAutoProxyCreator;

/**
 * example that uses {@link JGivenStage} to initialize a class as
 * a spring bean and a JGiven stage (i.e. with automatically attached
 * method interceptors)
 *
 * @see JGivenStageAutoProxyCreator
 *
 */
@JGivenStage
public class AnnotatedStage extends Stage<AnnotatedStage> {
    @Autowired
    @ProvidedScenarioState
    TestBean testBean;

    public AnnotatedStage a_stage_that_is_a_spring_component() {
        return this;
    }

    public AnnotatedStage methods_on_this_component_are_called() {
        return this;
    }

    public void beans_are_injected() {
        Assertions.assertThat( testBean ).isNotNull();
    }
}
