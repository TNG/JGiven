package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

import com.tngtech.jgiven.format.PrintfFormatter;

/**
 * Step parameters annotated with this annotation will be put into quotes (" ") in reports.
 * 
 * @since 0.7.0
 */
@Documented
@Format( value = PrintfFormatter.class, args = "\"%s\"" )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE } )
public @interface Quoted {}
