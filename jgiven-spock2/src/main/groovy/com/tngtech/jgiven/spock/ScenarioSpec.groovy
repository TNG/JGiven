package com.tngtech.jgiven.spock

import com.google.common.reflect.TypeToken
import com.tngtech.jgiven.annotation.DoNotIntercept
import com.tngtech.jgiven.impl.Scenario
import com.tngtech.jgiven.junit.JGivenClassRule
import com.tngtech.jgiven.spock.junit.JGivenSpockMethodRule
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.annotation.AnnotationDescription
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.SuperMethodCall
import net.bytebuddy.matcher.ElementMatchers
import org.junit.ClassRule
import org.junit.Rule
import spock.lang.Shared
import spock.lang.Specification

class ScenarioSpec<GIVEN, WHEN, THEN> extends Specification {
    @ClassRule @Shared JGivenClassRule writerRule = new JGivenClassRule()
    @Rule JGivenSpockMethodRule scenarioRule = new JGivenSpockMethodRule(createScenario())

    GIVEN given() {
        getScenario().given()
    }

    WHEN when() {
        getScenario().when()
    }

    THEN then() {
        getScenario().then()
    }

    Scenario<GIVEN, WHEN, THEN> getScenario() {
        (Scenario<GIVEN, WHEN, THEN>) scenarioRule.getScenario()
    }

    Scenario<GIVEN, WHEN, THEN> createScenario() {
        Class<GIVEN> givenClass = addDoNotInterceptToMetaClass(new TypeToken<GIVEN>(getClass()) {}.getRawType())
        Class<WHEN> whenClass = addDoNotInterceptToMetaClass(new TypeToken<WHEN>(getClass()) {}.getRawType())
        Class<THEN> thenClass = addDoNotInterceptToMetaClass(new TypeToken<THEN>(getClass()) {}.getRawType())

        new Scenario<GIVEN, WHEN, THEN>(givenClass, whenClass, thenClass)
    }

    private <T> Class<T> addDoNotInterceptToMetaClass(Class<T> clazz) {
        AnnotationDescription doNotIntercept = AnnotationDescription.Builder.ofType(DoNotIntercept.class).build()
        def clazzA = clazz.metaClass.getTheClass()
        new ByteBuddy()
                .subclass(clazzA)
                .method(ElementMatchers.named("getMetaClass"))
                .intercept(SuperMethodCall.INSTANCE)
                .annotateMethod(doNotIntercept)
                .make()
                .load(clazzA.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded() as Class<T>
    }
}
