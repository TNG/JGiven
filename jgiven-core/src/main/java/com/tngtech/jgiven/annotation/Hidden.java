package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a step method to be hidden in reports. 
 * 
 * This is useful for technical helper methods that have no meaning in the domain world.
 * 
 * You should write helper methods in camelCase so that the name already indicates that
 * the method does not appear in the report.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface Hidden {

}
