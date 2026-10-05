package com.tngtech.jgiven.integration.spring.junit5.test;

import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * example for a Spring Bean that is used as a step
 * <p>
 * note this bean neither inherits from {@link com.tngtech.jgiven.Stage}
 * nor is annotated with the {@link JGivenStage} annotation, i.e. it is
 * possible (but not recommended) to use unmodified already existing
 * spring beans as stages.
 * <br>
 *
 */
@JGivenStage
class SimpleTestSpringSteps {

    @Autowired
    private TestBean testBean;
    @Autowired
    private ApplicationContext context;

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
        assertThat(testBean).isNotNull();
    }

    public void bean_$_is_reference_to_string_$(String beanName, String expectedContent) {
        assertThat(context.getBean(beanName))
                .asInstanceOf(InstanceOfAssertFactories.type(AtomicReference.class))
                .extracting(AtomicReference::get)
                .isEqualTo(expectedContent);
    }

    @NestedSteps
    public void a_nested_step() {
        this.beans_are_injected();
        this.methods_on_this_component_are_called();
    }
}
