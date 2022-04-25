package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import java.util.Arrays;
import java.util.Optional;
import org.testng.ITestResult;
import org.testng.annotations.Test;

class IncompatibleMultithreadingChecker {

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void checkIncompatibleMultiThreading(ITestResult paramITestResult) {
        boolean isMultiThreaded = isMultiThreaded(paramITestResult.getTestClass().getRealClass());
        boolean hasInjectedStages = hasInjectedStages(paramITestResult.getTestClass().getRealClass());

        if (isMultiThreaded && hasInjectedStages) {
            throw new JGivenWrongUsageException("JGiven does not support using multi-threading and stage injection "
                + "in TestNG at the same time due to their different lifecycle models. "
                + "Please switch to single threaded execution or provide stages via inheriting from ScenarioTest");
        }
    }

    private boolean isMultiThreaded(Class<?> testClass) {
        return Optional.ofNullable(testClass.getAnnotation(Test.class))
            .map(test -> !test.singleThreaded())
            .orElse(false);
    }

    private boolean hasInjectedStages(Class<?> testClass) {
        InjectedStageFinder injectedStageFinder = new InjectedStageFinder();
        ReflectionUtil.forEachSuperClass(testClass, injectedStageFinder);
        return injectedStageFinder.foundInjectedStage;
    }

    private static class InjectedStageFinder implements ReflectionUtil.ClassAction {

        private boolean foundInjectedStage = false;

        @Override
        public void act(Class<?> clazz) {
            foundInjectedStage = foundInjectedStage || thisClassDeclaresInjectedFields(clazz);
        }

        private boolean thisClassDeclaresInjectedFields(Class<?> clazz) {
            return Arrays.stream(clazz.getDeclaredFields())
                .anyMatch(field -> field.isAnnotationPresent(ScenarioStage.class));
        }
    }
}
