package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.format.PrintfAnnotationFormatter;

/**
 * A special format annotation that uses the formatting
 * known from the String.format method.
 * <p>
 * Note that this uses the default locale returned from Locale.getDefault()
 */
@Documented
@AnnotationFormat( value = PrintfAnnotationFormatter.class )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE } )
public @interface Formatf {

    /**
     * The format string to be used to format the argument.
     */
    String value() default "%s";
}
