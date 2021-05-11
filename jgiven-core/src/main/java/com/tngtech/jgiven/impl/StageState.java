package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

//TODO This class is still closely linked with the ScenarioExecutor Maybe it should be an inner class
class StageState {
    final Object instance;
    private final LifeCycleMethodRegister<AfterStage> afterStageCalled;
    private final LifeCycleMethodRegister<BeforeStage> beforeStageCalled;
    Object currentChildStage;

    StageState(Object instance) {
        this.instance = instance;
        this.afterStageCalled = new LifeCycleMethodRegister<>(this.instance, AfterStage.class, AfterStage::repeatable);
        beforeStageCalled = new LifeCycleMethodRegister<>(this.instance, BeforeStage.class, BeforeStage::repeatable);
    }

    boolean allBeforeStageMethodsHaveBeenExecuted() {
        return beforeStageCalled.allStageMethodsHaveBeenExecuted();
    }

    boolean allAfterStageMethodsHaveBeenExecuted() {
        return afterStageCalled.allStageMethodsHaveBeenExecuted();
    }

    boolean beforeStageMethodHasBeenExecuted(Method method) {
        return beforeStageCalled.stageMethodHasBeenExecuted(method);
    }

    boolean afterStageMethodHasBeenExecuted(Method method) {
        return afterStageCalled.stageMethodHasBeenExecuted(method);
    }

    boolean beforeStageMethodIsExecutable(Method method) {
        return !beforeStageMethodHasBeenExecuted(method);
    }

    boolean afterStageMethodIsExecutable(Method method) {
        return !afterStageMethodHasBeenExecuted(method);
    }


    void markBeforeStageAsExecuted(Method method) {
        beforeStageCalled.markStageAsExecuted(method);
    }

    void markAfterStageAsExecuted(Method method) {
        afterStageCalled.markStageAsExecuted(method);
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
