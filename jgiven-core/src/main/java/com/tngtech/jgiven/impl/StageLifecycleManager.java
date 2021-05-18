package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.exception.JGivenUserException;
import com.tngtech.jgiven.impl.intercept.StepInterceptorImpl;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StageLifecycleManager {
    private static final Logger log = LoggerFactory.getLogger(StageLifecycleManager.class);

    private final Object instance;
    private final StepInterceptorImpl methodInterceptor;
    private final LifecycyleMethodManager<AfterStage> afterStageRegister;
    private final LifecycyleMethodManager<BeforeStage> beforeStageRegister;
    private final LifecycyleMethodManager<BeforeScenario> beforeScenarioRegister;
    private final LifecycyleMethodManager<AfterScenario> afterScenarioRegister;

    StageLifecycleManager(Object instance, StepInterceptorImpl methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
        this.instance = instance;

        afterStageRegister = new LifecycyleMethodManager<>(AfterStage.class, AfterStage::repeatable);
        beforeStageRegister = new LifecycyleMethodManager<>(BeforeStage.class, BeforeStage::repeatable);
        beforeScenarioRegister = new LifecycyleMethodManager<>(BeforeScenario.class, (it) -> false);
        afterScenarioRegister = new LifecycyleMethodManager<>(AfterScenario.class, (it) -> false);
    }

    boolean allAfterStageMethodsHaveBeenExecuted() {
        return afterStageRegister.allMethodsHaveBeenExecuted();
    }

    void executeAfterStageMethods(boolean fakeExecution) throws Throwable {
        executeLifecycleMethods(afterStageRegister, fakeExecution);
    }

    void executeBeforeStageMethods(boolean fakeExecution) throws Throwable {
        executeLifecycleMethods(beforeStageRegister, fakeExecution);
    }

    void executeAfterScenarioMethods(boolean fakeExecution) throws Throwable {
        executeLifecycleMethods(afterScenarioRegister, fakeExecution);
    }

    void executeBeforeScenarioMethods(boolean fakeExecution) throws Throwable {
        executeLifecycleMethods(beforeScenarioRegister, fakeExecution);
    }

    private void executeLifecycleMethods(LifecycyleMethodManager<?> register, boolean fakeExecution) throws Throwable {
        if (fakeExecution) {
            register.fakeExecution();
        } else {
            register.executeMethods();
        }
    }

    private enum StepExecutionState {
        EXECUTED(true),
        REPEATABLE(false),
        NOT_EXECUTED(false);

        private final boolean hasBeenExecuted;

        StepExecutionState(boolean hasBeenExecuted) {
            this.hasBeenExecuted = hasBeenExecuted;
        }

        public boolean toBoolean() {
            return this.hasBeenExecuted;
        }
    }

    private class LifecycyleMethodManager<T extends Annotation> {
        private final Class<T> targetAnnotation;
        private final Map<Method, StepExecutionState> register = new HashMap<>();
        private final Predicate<T> predicateFromT;

        private LifecycyleMethodManager(Class<T> targetAnnotation, Predicate<T> predicateFromAnnotation) {
            this.targetAnnotation = targetAnnotation;
            this.predicateFromT = predicateFromAnnotation;
            fillStageRegister(instance);
        }

        @SuppressWarnings({"unchecked"})
        private void fillStageRegister(Object instance) {
            ReflectionUtil.forEachMethod(instance, instance.getClass(), targetAnnotation,
                (object, method) ->
                    Arrays.stream(method.getDeclaredAnnotations())
                        .filter(annotation -> targetAnnotation.isAssignableFrom(annotation.getClass()))
                        .map(annotation -> (T) annotation)
                        .findFirst()
                        .map(it -> predicateFromT.test(it) ? StepExecutionState.REPEATABLE :
                            StepExecutionState.NOT_EXECUTED)
                        .ifPresent(it -> register.put(method, it))
            );
            log.debug("Added methods '{}' as '{}' methods to the register",
                register.keySet(), targetAnnotation.getSimpleName());
        }

        boolean methodMarkedForExecution(Method method) {
            return !Optional.ofNullable(register.get(method))
                .map(StepExecutionState::toBoolean)
                .orElse(true);
        }

        boolean allMethodsHaveBeenExecuted() {
            return register.values().stream().allMatch(StepExecutionState::toBoolean);
        }

        /**
         * Do everything except method invocation.
         */
        void fakeExecution() throws Throwable {
            prepareMethods();
            doExecutionOn(Stream.of());
        }

        void executeMethods() throws Throwable {
            Stream<Method> methodsToExecute = prepareMethods();
            doExecutionOn(methodsToExecute);
        }

        private Stream<Method> prepareMethods() {
            return register.keySet().stream().filter(this::methodMarkedForExecution)
                .peek(this::markStageAsExecuted);
        }


        private void markStageAsExecuted(Method method) {
            StepExecutionState stepState = register.get(method);
            if (stepState == StepExecutionState.NOT_EXECUTED) {
                register.put(method, StepExecutionState.EXECUTED);
            }
        }

        private void doExecutionOn(Stream<Method> methodsToExecute)
            throws Throwable {
            log.debug("Executing methods annotated with @{}", targetAnnotation.getName());
            boolean previousMethodExecution = methodInterceptor.enableMethodExecution(true);
            try {
                methodInterceptor.enableMethodInterception(false);
                methodsToExecute.forEach(method ->
                    ReflectionUtil.invokeMethod(instance, method,
                        " with annotation @" + targetAnnotation.getName()));
                methodInterceptor.enableMethodInterception(true);
            } catch (JGivenUserException e) {
                throw e.getCause();
            } finally {
                methodInterceptor.enableMethodExecution(previousMethodExecution);
            }
        }
    }
}
