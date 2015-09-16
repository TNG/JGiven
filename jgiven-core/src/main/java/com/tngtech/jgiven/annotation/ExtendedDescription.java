package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

/**
 * This annotation can be used to define an extended description for a step method or a test method.
 * This description is added to the report.
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
