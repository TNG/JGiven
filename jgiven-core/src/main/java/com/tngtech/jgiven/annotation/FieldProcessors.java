package com.tngtech.jgiven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.integration.StageFieldProcessor;

/**
 * Specifies StageFieldProcessors to be used during
 * the execution of a scenario.
 * <p>
 * Must be annotated at the stage class or a super-type of the stage class
 */
@Retention( RetentionPolicy.RUNTIME )
@Inherited
@Target( ElementType.TYPE )
public @interface FieldProcessors {
    Class<? extends StageFieldProcessor>[] value();
}
