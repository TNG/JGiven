package com.tngtech.jgiven.format;

import java.lang.annotation.Annotation;

/**
 * Interface for defining customer argument formatter for a custom annotation.
 *
 * @param <T> the type of the object to format
 */
public interface AnnotationArgumentFormatter<T extends Annotation> {
    /**
     * Format a single argument by taking optional formatter arguments into account.
     * @param argumentToFormat the object to format
     * @param annotation the annotation the parameter was annotated with                        
     * @return a formatted string
     */
    String format( Object argumentToFormat, T annotation );
}
