package com.tngtech.jgiven.format;

import java.lang.annotation.Annotation;

/**
 * Interface for defining customer argument formatter.
 *
 * @param <T> the type of the object to format
 */
public interface Formatter<T> {

    /**
     * Formats an step method argument.
     * 
     * @param argumentToFormat the argument object to format
     * @param annotations list of annotations that the method parameter is annotated with
     * @return a formatted string
     */
    String format( T argumentToFormat, Annotation... annotations );
}
