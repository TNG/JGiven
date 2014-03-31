package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.annotation.ScenarioState.Resolution;

/**
 * Marks fields to be expected by a scenario stage.
 * The annotation behaves exactly like {@link ScenarioState},
 * but better indicates the usage of a field.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ExpectedScenarioState {
    Resolution resolution() default Resolution.AUTO;
}
