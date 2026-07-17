package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.annotation.*;
import net.bytebuddy.dynamic.loading.ClassInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * Verifies that the actual {@link ByteBuddyStageClassCreator} class loader
 * can successfully instrument the given stage classes, i.e. that the real
 * production {@code getClassLoadingStrategy()} decision (INJECTION vs. WRAPPER
 * based on {@link ClassInjector.UsingReflection#isAvailable()}) produces a
 * usable instrumented subclass for every kind of stage class.
 *
 * <p>Unlike the previous version of these tests, the stage class creator is
 * <em>not</em> overridden to force a particular class loading strategy.
 * The default {@link ByteBuddyStageClassCreator} is used directly, so the
 * tests prove that the real ByteBuddy class loader can instrument the given
 * stage class.</p>
 */
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

    @Test
    public void getStageState_returns_state_when_stage_class_identity_differs_from_map_key() throws Exception {
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
     * Tests that the actual {@link ByteBuddyStageClassCreator} can instrument
     * the given stage classes and that lifecycle methods are executed through
     * the real generated subclass.
     *
     * <p>These tests intentionally use the default {@link ScenarioExecutor},
     * which internally uses a {@link ByteBuddyStageClassCreator} (wrapped in a
     * {@link CachingStageClassCreator}). They therefore exercise the real
     * ByteBuddy class loader and the real {@code getClassLoadingStrategy()}
     * decision rather than a forced strategy override.</p>
     */
    @Nested
    class UsingActualByteBuddyStageClassCreator {

        private ScenarioExecutor newExecutorWithActualCreator() {
            ScenarioExecutor executor = new ScenarioExecutor();
            // Explicitly wire the real ByteBuddyStageClassCreator so the tests
            // document that the actual class loader is the one being exercised.
            executor.setStageClassCreator(new ByteBuddyStageClassCreator());
            return executor;
        }

        @Test
        void actual_class_loader_can_instrument_stage_with_before_scenario_method() throws Throwable {
            ScenarioExecutor executor = newExecutorWithActualCreator();
            BeforeScenarioStep steps = executor.addStage(BeforeScenarioStep.class);
            executor.startScenario("Test");
            steps.some_step();
            executor.finished();

            assertThat(steps.beforeScenarioExecuted).isTrue();
        }

        @Test
        void actual_class_loader_can_instrument_stage_with_after_scenario_method() throws Throwable {
            ScenarioExecutor executor = newExecutorWithActualCreator();
            AfterScenarioStep steps = executor.addStage(AfterScenarioStep.class);
            executor.startScenario("Test");
            steps.some_step();
            executor.finished();

            assertThat(steps.afterScenarioExecuted).isTrue();
        }

        @Test
        void actual_class_loader_can_instrument_stage_with_before_stage_method() {
            ScenarioExecutor executor = newExecutorWithActualCreator();
            BeforeStageStep steps = executor.addStage(BeforeStageStep.class);
            executor.startScenario("Test");
            steps.before_stage_was_executed();
        }

        @Test
        void actual_class_loader_can_instrument_stage_with_after_stage_method() {
            ScenarioExecutor executor = newExecutorWithActualCreator();
            AfterStageStep steps = executor.addStage(AfterStageStep.class);
            NextSteps nextSteps = executor.addStage(NextSteps.class);
            executor.startScenario("Test");
            steps.after_stage_was_not_yet_executed();
            nextSteps.after_stage_was_executed();
        }

        @Test
        void actual_class_loader_can_instrument_stage_with_protected_lifecycle_method() {
            ScenarioExecutor executor = newExecutorWithActualCreator();
            ProtectedLifecycleStageStep steps = executor.addStage(ProtectedLifecycleStageStep.class);
            executor.startScenario("Test");
            steps.some_step();

            assertThat(steps.beforeStageExecuted).isTrue();
        }

        @Test
        void actual_class_loader_can_instrument_package_private_stage_class() {
            // The INJECTION strategy is only available where reflective class
            // injection works. On JVMs where it is unavailable the real
            // ByteBuddyStageClassCreator falls back to the WRAPPER strategy,
            // which cannot load package-private classes; skip in that case.
            assumeThat(ClassInjector.UsingReflection.isAvailable())
                    .withFailMessage("Reflective class injection is not available on this JVM")
                    .isTrue();
            ScenarioExecutor executor = newExecutorWithActualCreator();
            PackagePrivateStageStep steps = executor.addStage(PackagePrivateStageStep.class);
            executor.startScenario("Test");
            steps.some_step();
        }
    }
}
