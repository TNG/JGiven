package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to define an extended description for a step method.
 * This description is added to the report.
 * <h2>EXPERIMENTAL FEATURE</h2>
 * This is an experimental feature. It is not guaranteed that this feature will
 * be present in future versions of JGiven.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD } )
@Documented
public @interface ExtendedDescription {

    /**
     * The extended description of the step.
     */
    String value();

}
