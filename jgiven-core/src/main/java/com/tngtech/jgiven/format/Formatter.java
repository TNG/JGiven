package com.tngtech.jgiven.format;

import java.lang.annotation.Annotation;

/**
 * Interface for defining a global argument formatter.
 * <p>
 * Instances of this formatter can be globally configured with the {@link com.tngtech.jgiven.annotation.JGivenConfiguration}
 * by providing concrete implementation of the {@link com.tngtech.jgiven.config.AbstractJGivenConfiguration} class.
 *
 * @param <T> the type of the object to format
 * @see com.tngtech.jgiven.config.AbstractJGivenConfiguration#setFormatter(Class, Formatter)
 */
public interface Formatter<T> {

    /**
     * Formats a step method argument.
     *
     * @param argumentToFormat the argument object to format
     * @param annotations list of annotations that the method parameter is annotated with
     * @return a formatted string
     */
    String format( T argumentToFormat, Annotation... annotations );
}
