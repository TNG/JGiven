package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

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
@Target( ElementType.PARAMETER )
public @interface Formatf {

    /**
     * The format string to be used to format the argument.
     */
    String value() default "%s";
}
