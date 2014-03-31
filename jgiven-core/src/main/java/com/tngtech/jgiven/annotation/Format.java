package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;

/**
 * Allows arguments of step methods to be formatted with 
 * an ArgumentFormatter
 *
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Format {
    Class<? extends ArgumentFormatter<?>> value() default PrintfFormatter.class;

    /**
     * Optional arguments for the ArgumentFormatter
     */
    String[] args() default {};
}
