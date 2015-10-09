package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

import com.tngtech.jgiven.format.AnnotationArgumentFormatter;

/**
 * Allows arguments of step methods to be formatted with an {@link AnnotationArgumentFormatter}.
 * This annotation can only appear on custom annotations. 
 * The custom annotation can then be applied to step arguments
 * <p>
 *     For an example usage see the {@link Formatf} annotation
 * </p>
 * @since 0.7.0
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.ANNOTATION_TYPE )
public @interface AnnotationFormat {
    Class<? extends AnnotationArgumentFormatter> value();

}
