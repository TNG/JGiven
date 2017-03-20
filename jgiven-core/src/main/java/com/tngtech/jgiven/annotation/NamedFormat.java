package com.tngtech.jgiven.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.format.DefaultObjectFormatter;

/**
 *
 * Allow to specify an identifiable format<br>
 *
 *
 * @See also {@link NamedFormats}
 * @since 0.15.0
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE } )
public @interface NamedFormat {
    /**
     * Specify a name identifying this format
     */
    String name();

    /**
     * Specify an inline {@link Format}
     *
     * <p>
     * Mutually exclusive with {@link #formatAnnotation()}
     * </p>
     */
    Format format() default @Format( DefaultObjectFormatter.class );

    /**
     * Specify a custom format annotation
     *
     * <p>
     * <ul>
     * <li>mutually exclusive with {@link #format()}</li>
     * <li>when set, has precedence over any specified {@link #format()}</li>
     * </ul>
     * </p>
     *
     * @See <a href="http://jgiven.org/userguide/#_custom_formatting_annotations">JGiven User Guide - Custom formatting annotations</a>
     */
    Class<? extends Annotation> formatAnnotation() default Annotation.class;
}
