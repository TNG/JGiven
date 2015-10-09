package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

import com.tngtech.jgiven.impl.params.DefaultCaseDescriptionProvider;

/**
 * Use to define a description provider for scenario cases.
 * <p>
 * By default, multiple cases in a parametrized scenario are described 
 * by just listing the parameter names with their corresponding values.
 * Sometimes, however, it is useful to provide an explicit description the provides more semantic background for each case.
 * 
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface CaseDescription {
    /**
     * Dummy value to indicate that no value was set
     */
    public static final String NO_VALUE = " - no value - ";

    /**
     * A pattern to define the case description
     */
    String value() default NO_VALUE;

    /**
     * An implementation of the {@link com.tngtech.jgiven.annotation.CaseDescriptionProvider} to provide a case description
     */
    Class<? extends CaseDescriptionProvider> provider() default DefaultCaseDescriptionProvider.class;

    /**
     * Whether or not the arguments should be formatted to string using JGiven formatter.
     * If this is set to true the list of parameter values passed to the CaseDescriptionProvider will be a list of strings,
     * otherwise it will be a list of objects with the original values passed to the method.
     */
    boolean formatValues() default true;
}
