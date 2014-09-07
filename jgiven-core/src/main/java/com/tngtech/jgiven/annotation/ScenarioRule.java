package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields to be Scenario rules.
 *
 * Scenario rules are classes with begin() and after() methods.
 *
 * These methods are called like methods annotated with {@link BeforeScenario} and
 * {@link AfterScenario}
 *
 * <pre>
 * {@literal @}ScenarioRule
 * MyRule rule = new MyRule();
 * </pre>
 *
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ScenarioRule {

}
