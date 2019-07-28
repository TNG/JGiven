package com.tngtech.jgiven.integration.spring.test;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import com.tngtech.jgiven.integration.spring.config.TestSpringConfig;

/**
 * example for a Spring Bean that is used as a step
 * <p>
 * note this bean neither inherits from {@link com.tngtech.jgiven.Stage}
 * nor is annotated with the {@link JGivenStage} annotation, i.e. it is
 * possible (but not recommended) to use unmodified already existing
 * spring beans as stages.
 * <br>
 * See {@link TestSpringConfig#jGivenBeanNameAutoProxyCreator()} on how to setup such beans.
 *
 */
//@Component
@JGivenStage
class SimpleTestSpringSteps {

    @Autowired
    TestBean testBean;

    public SimpleTestSpringSteps a_step_that_is_a_spring_component() {
        return this;
    }

    public SimpleTestSpringSteps methods_on_this_component_are_called() {
        return this;
    }

    public SimpleTestSpringSteps method_with_parameter_is_called( String message ) {
        testBean.sayHello( message );
        return this;
    }

    public void beans_are_injected() {
        Assertions.assertThat( testBean ).isNotNull();
    }

    @NestedSteps
    public void a_nested_step() {
        this.beans_are_injected();
        this.methods_on_this_component_are_called();
    }
}
