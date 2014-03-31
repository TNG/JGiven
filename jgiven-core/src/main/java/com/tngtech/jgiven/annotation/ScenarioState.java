package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields to be read and/or written by a scenario stage.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ScenarioState {
    Resolution resolution() default Resolution.AUTO;

    public enum Resolution {
        TYPE, NAME, AUTO;
    }
}
