package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.testng.ITestResult;
import org.testng.annotations.Test;

class IncompatibleMultithreadingChecker {

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void checkIncompatibleMultiThreading(ITestResult paramITestResult) {
        Method testMethod = paramITestResult.getMethod().getConstructorOrMethod().getMethod();
        boolean isMultiThreaded = isMultiThreaded(paramITestResult.getClass(), testMethod);
        boolean hasInjectedStages = hasInjectedStages(paramITestResult.getClass());

        if (isMultiThreaded && hasInjectedStages) {
            throw new JGivenWrongUsageException("JGiven does not support using multi-threading and stage injection"
                + "in TestNG at the same time due to their different lifecycle models. "
                + "Please switch to single threaded execution or provide stages via inheriting from ScenarioTest");
        }
    }

    private boolean isMultiThreaded(Class<?> testClass, Method testMethod) {
        List<Test> testAnnotations = new ArrayList<>();
        testAnnotations.addAll(Collections.singletonList(testClass.getAnnotation(Test.class)));
        testAnnotations.addAll(Collections.singletonList(testMethod.getAnnotation(Test.class)));
        return testAnnotations.stream().anyMatch(test -> !test.singleThreaded());
    }

    private boolean hasInjectedStages(Class<?> testClass) {
        ThreadLocal<Boolean> hasInjectedStage = new ThreadLocal<>();
        ReflectionUtil.forEachSuperClass(testClass, clazz ->
            hasInjectedStage.set(Arrays.stream(clazz.getDeclaredFields())
                .anyMatch(field -> field.isAnnotationPresent(ScenarioStage.class))));
        return hasInjectedStage.get();
    }
}
