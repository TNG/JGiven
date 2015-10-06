package com.tngtech.jgiven.format;

import java.lang.annotation.Annotation;

/**
 * Interface for defining a formatter for a custom annotation using the {@link com.tngtech.jgiven.annotation.AnnotationFormat} annotation.
 *
 * @param <T> the type of the custom annotation
 * @see com.tngtech.jgiven.annotation.AnnotationFormat
 */
public interface AnnotationArgumentFormatter<T extends Annotation> {
    /**
     * Format a single argument by taking the given annotation into account.
     * @param argumentToFormat the object to format
     * @param annotation the annotation the parameter was annotated with
     * @return a formatted string
     */
    String format( Object argumentToFormat, T annotation );
}
