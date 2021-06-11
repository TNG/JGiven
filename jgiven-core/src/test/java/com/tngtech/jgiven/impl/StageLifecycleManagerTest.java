package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.impl.intercept.StepInterceptorImpl;
import org.junit.Test;

public class StageLifecycleManagerTest {

    private final StepInterceptorImpl mockInterceptor = new StepInterceptorImpl(null, null, null);
    private LifecycleMethodContainer methodContainer = new LifecycleMethodContainer();
    private StageLifecycleManager underTest = new StageLifecycleManager(methodContainer, mockInterceptor);


    @Test
    public void exectuesAnnotatedMethodRepeatedly() throws Throwable {
        executeAllLifecycleMethods(underTest, false);
        executeAllLifecycleMethods(underTest, false);

        assertThat(methodContainer.repeatableBeforeMethodInvoked).isEqualTo(2);
        assertThat(methodContainer.repeatableAfterMethodInvoked).isEqualTo(2);
    }

    @Test
    public void exectuesNonAnnotatedMethodOnlyOnce() throws Throwable {
        executeAllLifecycleMethods(underTest, false);
        executeAllLifecycleMethods(underTest, false);

        assertThat(methodContainer.beforeScenarioMethodInvoked).isEqualTo(1);
        assertThat(methodContainer.beforeMethodInvoked).isEqualTo(1);
        assertThat(methodContainer.afterMethodInvoked).isEqualTo(1);
        assertThat(methodContainer.afterScenarioMethodInvoked).isEqualTo(1);
    }

    @Test
    public void executesAllLifecycleMethods() throws Throwable {
        executeAllLifecycleMethods(underTest, false);

        assertAllMethodsHaveBeenExecuted(1);
    }

    @Test
    public void findsLifecycleMethodInSuperclasses() throws Throwable {
        methodContainer = new LifecycleMethodContainer() {
        };
        underTest = new StageLifecycleManager(methodContainer, mockInterceptor);

        executeAllLifecycleMethods(underTest, false);

        assertAllMethodsHaveBeenExecuted(1);
    }

    @Test
    public void noExecutionIfFakeExecutionRequested() throws Throwable {
        executeAllLifecycleMethods(underTest, true);
        assertAllMethodsHaveBeenExecuted(0);
    }

    private void executeAllLifecycleMethods(StageLifecycleManager underTest, boolean dryRun) throws Throwable {
        underTest.executeBeforeScenarioMethods(dryRun);
        underTest.executeBeforeStageMethods(dryRun);
        underTest.executeAfterStageMethods(dryRun);
        underTest.executeAfterScenarioMethods(dryRun);
    }

    private void assertAllMethodsHaveBeenExecuted(int times) {
        assertThat(methodContainer.beforeScenarioMethodInvoked).isEqualTo(times);
        assertThat(methodContainer.beforeMethodInvoked).isEqualTo(times);
        assertThat(methodContainer.repeatableBeforeMethodInvoked).isEqualTo(times);

        assertThat(methodContainer.repeatableAfterMethodInvoked).isEqualTo(times);
        assertThat(methodContainer.afterMethodInvoked).isEqualTo(times);
        assertThat(methodContainer.afterScenarioMethodInvoked).isEqualTo(times);
    }

    private static class LifecycleMethodContainer {
        int beforeScenarioMethodInvoked = 0;
        int afterScenarioMethodInvoked = 0;
        int repeatableBeforeMethodInvoked = 0;
        int repeatableAfterMethodInvoked = 0;
        int beforeMethodInvoked = 0;
        int afterMethodInvoked = 0;

        @BeforeScenario
        private void beforeScenario() {
            beforeScenarioMethodInvoked++;
        }

        @AfterScenario
        private void afterScenario() {
            afterScenarioMethodInvoked++;
        }

        @BeforeStage
        private void beforeMethod() {
            beforeMethodInvoked++;
        }

        @AfterStage
        private void afterMethod() {
            afterMethodInvoked++;
        }

        @BeforeStage(repeatable = true)
        private void repeatableBeforeMethod() {
            repeatableBeforeMethodInvoked++;
        }

        @AfterStage(repeatable = true)
        private void repeatableAfterMethod() {
            repeatableAfterMethodInvoked++;
        }
    }
}
