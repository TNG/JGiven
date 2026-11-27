package com.tngtech.jgiven.integration.spring.junit6;

import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.ScenarioHolder;
import com.tngtech.jgiven.integration.spring.SpringStageCreatorInstaller;
import com.tngtech.jgiven.junit6.JGivenExtension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * JUnit 5/6 extension that combines {@link JGivenExtension} with Spring support: it installs
 * JGiven's {@link com.tngtech.jgiven.integration.spring.SpringStageCreator} on the scenario
 * of the test instance, in addition to the regular JGiven lifecycle callbacks.
 *
 * <p>The installation has to come from the JUnit extension rather than from the test
 * instance implementing {@link org.springframework.beans.factory.BeanFactoryAware}, because
 * a custom {@link org.springframework.test.context.TestExecutionListeners @TestExecutionListeners}
 * declaration with the default {@link org.springframework.test.context.TestExecutionListeners.MergeMode#REPLACE_DEFAULTS REPLACE_DEFAULTS}
 * merge mode removes Spring's {@code DependencyInjectionTestExecutionListener}, whose
 * {@code BeanFactoryAware} callback is what normally installs the stage creator.
 * See <a href="https://github.com/TNG/JGiven/issues/2193">issue #2193</a>.
 *
 * <p>This extension is registered automatically by the Spring scenario test base
 * classes of this module (e.g. {@link SpringScenarioTest}). Users who do not extend
 * those base classes can register it explicitly via
 * {@code @ExtendWith({SpringExtension.class, JGivenSpringExtension.class})}.
 *
 * @since 3.0.0
 */
public class JGivenSpringExtension extends JGivenExtension {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        super.postProcessTestInstance(testInstance, context);
        installSpringStageCreator(testInstance, context);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        super.beforeEach(context);
        // The scenario may be recreated per test method, and without the
        // DependencyInjectionTestExecutionListener nothing re-installs the
        // stage creator, so do it defensively on every test (issue #2193).
        ScenarioBase scenario = ScenarioHolder.get().getScenarioOfCurrentThread();
        if (scenario != null && context.getTestInstance().isPresent()) {
            installSpringStageCreator(context.getTestInstance().get(), context);
        }
    }

    private void installSpringStageCreator(Object testInstance, ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        SpringStageCreatorInstaller.installSpringStageCreator(testInstance, applicationContext);
    }
}
