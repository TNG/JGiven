package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

/**
 * Marks a step method to have nested steps. 
 * 
 * @since 0.10.0
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD } )
public @interface NestedSteps {}
