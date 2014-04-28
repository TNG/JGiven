package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A special format annotation that uses the formatting
 * known from the String.format method.
 * <p>
 * Note that this uses the default locale returned from Locale.getDefault()
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Formatf {
    String value() default "%s";
}
