package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.annotation.AfterStage;
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
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*TODO This class is still closely linked with the ScenarioExecutor Maybe it should be an inner class
    Separation suggestion:
    * make this class only care about all the lifecycle methods, maybe create another variant of the register class
      for the BeforeScenario & AfterScenario Methods
    * create a child class of this as inner class of ScenarioExecutor.
      That class should hold the instance publicly and the child stage.
    * The methodInterceptor and the lifecycle Method execution may become fields herein.
 */
class StageLifecycleManager {
    private final LifeCycleMethodRegister<AfterStage> afterStageCalled;
    private final LifeCycleMethodRegister<BeforeStage> beforeStageCalled;

    StageLifecycleManager(Object instance) {
        this.afterStageCalled = new LifeCycleMethodRegister<>(instance, AfterStage.class, AfterStage::repeatable);
        beforeStageCalled = new LifeCycleMethodRegister<>(instance, BeforeStage.class, BeforeStage::repeatable);
    }

    boolean allBeforeStageMethodsHaveBeenExecuted() {
        return beforeStageCalled.allStageMethodsHaveBeenExecuted();
    }

    boolean allAfterStageMethodsHaveBeenExecuted() {
        return afterStageCalled.allStageMethodsHaveBeenExecuted();
    }

    void executeAfterStageMethods(StepInterceptorImpl methodInterceptor, boolean enableLifecycleExecution)
        throws Throwable {
        afterStageCalled.executeWithInterceptor(methodInterceptor, enableLifecycleExecution);
    }

    void executeBeforeStageMethods(StepInterceptorImpl methodInterceptor, boolean enableLifecycleExecution)
        throws Throwable {
        beforeStageCalled.executeWithInterceptor(methodInterceptor, enableLifecycleExecution);
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

    private static class LifeCycleMethodRegister<T extends Annotation> {
        private static final Logger log = LoggerFactory.getLogger(LifeCycleMethodRegister.class);
        private final Object instance;
        private final Class<T> targetAnnotation;
        private final Map<Method, StepExecutionState> register = new HashMap<>();
        private final Function<T, Boolean> predicateFromT;

        private LifeCycleMethodRegister(Object instance, Class<T> targetAnnotation,
                                        Function<T, Boolean> preditcateFromAnnotation) {
            this.instance = instance;
            this.targetAnnotation = targetAnnotation;
            this.predicateFromT = preditcateFromAnnotation;
            fillStageRegister();
        }

        void markStageAsExecuted(Method method) {
            StepExecutionState stepState = register.get(method);
            if (stepState == StepExecutionState.NOT_EXECUTED) {
                register.put(method, StepExecutionState.EXECUTED);
            }
        }

        //TODO: Perform check inside register.
        boolean stageMethodHasBeenExecuted(Method method) {
            return Optional.ofNullable(register.get(method))
                .map(StepExecutionState::toBoolean)
                .orElse(true);
        }

        @SuppressWarnings({"unchecked"})
        private void fillStageRegister() {
            ReflectionUtil.forEachMethod(instance, instance.getClass(), targetAnnotation,
                (object, method) ->
                    Arrays.stream(method.getDeclaredAnnotations())
                        .filter(annotation -> targetAnnotation.isAssignableFrom(annotation.getClass()))
                        .map(annotation -> (T) annotation)
                        .findFirst()
                        .map(it -> predicateFromT.apply(it) ? StepExecutionState.REPEATABLE :
                            StepExecutionState.NOT_EXECUTED)
                        .ifPresent(it -> register.put(method, it))
            );
            log.debug("Added methods '{}' as '{}' methods", register.keySet(), targetAnnotation.getSimpleName());
        }

        void executeWithInterceptor(StepInterceptorImpl methodInterceptor, boolean enableLifecycleExecution)
            throws Throwable {

            Stream<Method> methods = register.keySet().stream().filter(method -> !stageMethodHasBeenExecuted(method));
            if (!enableLifecycleExecution) {
                methods.forEach(this::markStageAsExecuted);
                return;
            }

            log.debug("Executing methods annotated with @{}", targetAnnotation.getName());
            boolean previousMethodExecution = methodInterceptor.enableMethodExecution(true);
            try {
                methodInterceptor.enableMethodInterception(false);
                methods
                    .filter(method -> !stageMethodHasBeenExecuted(method))
                    .forEach(method -> {
                        markStageAsExecuted(method);
                        ReflectionUtil.invokeMethod(instance, method,
                            " with annotation @" + targetAnnotation.getName());
                    });
                methodInterceptor.enableMethodInterception(true);
            } catch (JGivenUserException e) {
                throw e.getCause();
            } finally {
                methodInterceptor.enableMethodExecution(previousMethodExecution);
            }
        }

        boolean allStageMethodsHaveBeenExecuted() {
            for (StepExecutionState value : register.values()) {
                if (!value.toBoolean()) {
                    return false;
                }
            }
            return true;
        }
    }
}
