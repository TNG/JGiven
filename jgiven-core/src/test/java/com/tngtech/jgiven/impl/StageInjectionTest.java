package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StageInjectionTest {

    private static URLClassLoader createSeparateLoader() throws Exception {
        String classpath = System.getProperty("java.class.path");
        String[] entries = classpath.split(File.pathSeparator);
        URL[] urls = new URL[entries.length];
        for (int i = 0; i < entries.length; i++) {
            urls[i] = new File(entries[i]).toURI().toURL();
        }
        return new URLClassLoader(urls, null);
    }

    @BeforeEach
    void clearByteBuddyCache() {
        CachingStageClassCreator.clearCache();
    }

    /**
     * The production {@link ByteBuddyStageClassCreator} selects a class loading
     * strategy per stage class: {@code WRAPPER} for public classes and
     * {@code INJECTION} (or {@code LOOKUP} as fallback) for package-private
     * classes. The following happy-path tests exercise every demonstration stage
     * class through that default creator, proving that the strategy selection
     * works for each supported stage shape.
     */
    @Test
    void default_creator_works_with_package_private_stage_class() {
        ScenarioExecutor executor = new ScenarioExecutor();
        PackagePrivateStageStep steps = executor.addStage(PackagePrivateStageStep.class);
        executor.startScenario("Test");
        steps.some_step();
    }

    @Test
    void default_creator_executes_BeforeStage_methods() {
        ScenarioExecutor executor = new ScenarioExecutor();
        BeforeStageStep steps = executor.addStage(BeforeStageStep.class);
        executor.startScenario("Test");
        steps.before_stage_was_executed();
    }

    @Test
    void default_creator_executes_AfterStage_methods() {
        ScenarioExecutor executor = new ScenarioExecutor();
        AfterStageStep steps = executor.addStage(AfterStageStep.class);
        NextSteps nextSteps = executor.addStage(NextSteps.class);
        executor.startScenario("Test");
        steps.after_stage_was_not_yet_executed();
        nextSteps.after_stage_was_executed();
    }

    @Test
    void default_creator_executes_BeforeScenario_methods() throws Throwable {
        ScenarioExecutor executor = new ScenarioExecutor();
        BeforeScenarioStep steps = executor.addStage(BeforeScenarioStep.class);
        executor.startScenario("Test");
        steps.some_step();
        executor.finished();

        assertThat(steps.beforeScenarioExecuted).isTrue();
    }

    @Test
    void default_creator_executes_AfterScenario_methods() throws Throwable {
        ScenarioExecutor executor = new ScenarioExecutor();
        AfterScenarioStep steps = executor.addStage(AfterScenarioStep.class);
        executor.startScenario("Test");
        steps.some_step();
        executor.finished();

        assertThat(steps.afterScenarioExecuted).isTrue();
    }

    @Test
    void default_creator_executes_protected_lifecycle_methods() {
        ScenarioExecutor executor = new ScenarioExecutor();
        ProtectedLifecycleStageStep steps = executor.addStage(ProtectedLifecycleStageStep.class);
        executor.startScenario("Test");
        steps.some_step();

        assertThat(steps.beforeStageExecuted).isTrue();
    }

    /**
     * A {@code public} stage class with a package-private step method must
     * still be instrumented with an injection style strategy: the
     * {@code WRAPPER} strategy loads the subclass in a different package
     * namespace and therefore cannot override the package-private method, so
     * interception silently breaks. This is the common JGiven idiom of writing
     * step methods without an explicit {@code public} modifier.
     */
    @Test
    void default_creator_intercepts_package_private_step_of_public_stage_class() {
        ScenarioExecutor executor = new ScenarioExecutor();
        RecordingInterceptor interceptor = new RecordingInterceptor();
        PublicStageWithPackagePrivateStep steps = executor.addStage(PublicStageWithPackagePrivateStep.class);
        ((StageInterceptorInternal) steps).__jgiven_setStepInterceptor(interceptor);
        executor.startScenario("Test");
        steps.some_step();

        assertThat(interceptor.interceptedMethods)
                .contains("some_step");
    }

    @Test
    void wrapper_class_loading_strategy_does_not_intercept_package_private_step_of_public_stage_class() {
        ScenarioExecutor executor = new ScenarioExecutor();
        executor.setStageClassCreator(new WrapperStageClassCreator());
        RecordingInterceptor interceptor = new RecordingInterceptor();
        PublicStageWithPackagePrivateStep steps = executor.addStage(PublicStageWithPackagePrivateStep.class);
        ((StageInterceptorInternal) steps).__jgiven_setStepInterceptor(interceptor);
        executor.startScenario("Test");
        steps.some_step();

        assertThat(interceptor.interceptedMethods)
                .doesNotContain("some_step");
    }

    /**
     * Demonstrates why a non-wrapper strategy is required for package-private
     * stage classes: the generated subclass lives in a different class loader
     * and therefore a different package namespace, so it cannot access its
     * package-private superclass, raising {@link IllegalAccessError}. This is
     * the failure mode that {@code INJECTION}/{@code LOOKUP} exist to avoid.
     * <p>
     * We refrained from creating a test that demonstrates the insufficientness of a pure
     * lookup strategy, because it would require a test in a separate Java module that does
     * not disclose its contents.
     */
    @Test
    void wrapper_class_loading_strategy_fails_with_package_private_stage_class() {
        ScenarioExecutor executor = new ScenarioExecutor();
        executor.setStageClassCreator(new WrapperStageClassCreator());
        assertThatThrownBy(() -> executor.addStage(PackagePrivateStageStep.class))
                .hasRootCauseInstanceOf(IllegalAccessError.class);
    }

    @Test
    void getStageState_returns_state_when_stage_class_identity_differs_from_map_key() throws Exception {
        ScenarioExecutor executor = new ScenarioExecutor();
        BeforeStageStep steps = executor.addStage(BeforeStageStep.class);

        try (URLClassLoader separateLoader = createSeparateLoader()) {
            Class<?> sameClassDifferentLoader = separateLoader.loadClass(
                    "com.tngtech.jgiven.impl.StageInjectionTest$BeforeStageStep");

            assertThat(sameClassDifferentLoader).isNotSameAs(BeforeStageStep.class);
            assertThat(sameClassDifferentLoader.getName()).isEqualTo(BeforeStageStep.class.getName());

            assertThat(executor.getStageState(steps)).isNotNull();
            assertThat(executor.stages.containsKey(BeforeStageStep.class)).isTrue();
            assertThat(executor.stages.containsKey(sameClassDifferentLoader)).isFalse();
        }
    }

    static class WrapperStageClassCreator extends ByteBuddyStageClassCreator {
        @Override
        protected ClassLoadingStrategy<ClassLoader> getClassLoadingStrategy(Class<?> stageClass) {
            return ClassLoadingStrategy.Default.WRAPPER;
        }
    }

    public static class BeforeStageStep {
        public boolean beforeStageExecuted;

        @BeforeStage
        public void setup() {
            beforeStageExecuted = true;
        }

        public void before_stage_was_executed() {
            assertThat(beforeStageExecuted).isTrue();
        }
    }

    public static class AfterStageStep {
        @ProvidedScenarioState
        public boolean afterStageExecuted;

        @AfterStage
        public void setup() {
            afterStageExecuted = true;
        }

        public void after_stage_was_not_yet_executed() {
            assertThat(afterStageExecuted).isFalse();
        }
    }

    public static class NextSteps {
        @ExpectedScenarioState
        public boolean afterStageExecuted;

        public void after_stage_was_executed() {
            assertThat(afterStageExecuted).isTrue();
        }
    }

    public static class BeforeScenarioStep {
        public boolean beforeScenarioExecuted;

        @BeforeScenario
        public void setup() {
            beforeScenarioExecuted = true;
        }

        public void some_step() {
        }
    }

    public static class AfterScenarioStep {
        public boolean afterScenarioExecuted;

        @AfterScenario
        public void teardown() {
            afterScenarioExecuted = true;
        }

        public void some_step() {
        }
    }

    static class PackagePrivateStageStep {
        public void some_step() {
        }
    }

    public static class ProtectedLifecycleStageStep {
        boolean beforeStageExecuted;

        @BeforeStage
        protected void setup() {
            beforeStageExecuted = true;
        }

        public void some_step() {
        }
    }

    /**
     * A {@code public} stage class that declares a package-private step method,
     * the common JGiven idiom that requires an injection style strategy.
     */
    public static class PublicStageWithPackagePrivateStep {
        void some_step() {
        }
    }

    /**
     * Records the names of methods that the ByteBuddy interceptor invokes.
     */
    static class RecordingInterceptor implements StepInterceptor {
        final java.util.List<String> interceptedMethods = new java.util.ArrayList<>();

        @Override
        public Object intercept(Object receiver, java.lang.reflect.Method method, Object[] parameters, Invoker invoker) throws Throwable {
            interceptedMethods.add(method.getName());
            return invoker.proceed();
        }
    }
}
