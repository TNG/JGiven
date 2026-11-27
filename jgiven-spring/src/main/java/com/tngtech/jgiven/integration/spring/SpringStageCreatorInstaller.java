package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.ScenarioBase;
import org.springframework.context.ApplicationContext;


/**
 * Utility for installing the {@link SpringStageCreator} on the scenario of a JGiven
 * Spring test instance.
 *
 * <p>Installation used to be triggered only by Spring's
 * {@code DependencyInjectionTestExecutionListener} invoking
 * {@link org.springframework.beans.factory.BeanFactoryAware#setBeanFactory} on the test
 * instance. When a user declares
 * {@link org.springframework.test.context.TestExecutionListeners @TestExecutionListeners}
 * with the default {@link org.springframework.test.context.TestExecutionListeners.MergeMode#REPLACE_DEFAULTS REPLACE_DEFAULTS}
 * merge mode, that listener is replaced and JGiven's stage wiring silently breaks,
 * which is why the installation must happen outside the {@code TestExecutionListener}
 * lifecycle (see <a href="https://github.com/TNG/JGiven/issues/2193">issue #2193</a>).
 *
 * @since 3.0.1
 */
public final class SpringStageCreatorInstaller {

    private SpringStageCreatorInstaller() {
    }

    /**
     * Installs the {@link SpringStageCreator} bean from the given {@link ApplicationContext}
     * on the scenario of the supplied test instance, provided the test instance is a
     * JGiven {@link ScenarioTestBase}.
     *
     * <p>Idempotent and safe to call multiple times.
     *
     * @param testInstance       the JGiven Spring test instance
     * @param applicationContext the Spring {@link ApplicationContext} for the test
     */
    public static void installSpringStageCreator(Object testInstance, ApplicationContext applicationContext) {
        if (testInstance instanceof ScenarioTestBase<?, ?, ?>) {
            install(((ScenarioTestBase<?, ?, ?>) testInstance).getScenario(), applicationContext);
        }
    }

    /**
     * Installs the {@link SpringStageCreator} bean from the given {@link ApplicationContext}
     * on the supplied scenario.
     *
     * @param scenario           the JGiven scenario to configure
     * @param applicationContext the Spring {@link ApplicationContext} to fetch the
     *                           {@link SpringStageCreator} bean from
     */
    public static void installSpringStageCreator(ScenarioBase scenario, ApplicationContext applicationContext) {
        if (scenario != null) {
            install(scenario, applicationContext);
        }
    }

    private static void install(ScenarioBase scenario, ApplicationContext applicationContext) {
        scenario.setStageCreator(applicationContext.getBean(SpringStageCreator.class));
    }
}
