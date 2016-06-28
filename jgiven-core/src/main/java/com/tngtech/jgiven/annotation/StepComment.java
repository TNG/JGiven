package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be used to provide an additional comment to the previous step.
 * This optional comment can be used by reporters for the output.
 *
 * A step comment is intended to provide additional information about a specific step
 * invocation, e.g., because it might be surprising that the step is required. If you
 * want to provide a more detailed description of a step method in general, refer to
 * {@link ExtendedDescription}.
 *
 * A method decorated with this annotation is expected to take exactly one argument of
 * type {@link String}.
 *
 * @since 0.12.0
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface StepComment {

}
