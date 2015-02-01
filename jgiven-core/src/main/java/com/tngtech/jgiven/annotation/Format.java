package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;

/**
 * Allows arguments of step methods to be formatted with an ArgumentFormatter.
 * 
 * @since 0.7.0 this annotation can be put onto other annotations
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE } )
public @interface Format {
    Class<? extends ArgumentFormatter<?>> value() default PrintfFormatter.class;

    /**
     * Optional arguments for the ArgumentFormatter.
     */
    String[] args() default {};
}
