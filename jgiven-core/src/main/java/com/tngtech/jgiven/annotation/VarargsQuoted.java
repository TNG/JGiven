package com.tngtech.jgiven.annotation;

import com.tngtech.jgiven.format.PrintfFormatter;
import com.tngtech.jgiven.format.VarargsFormatter;

import java.lang.annotation.*;

/**
 * Varargs step parameters annotated with this annotation will be put into quotes (" ") in reports.
 *
 */
@Documented
@Format( value = VarargsFormatter.class, args = "\"%s\"" )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD } )
public @interface VarargsQuoted {}
