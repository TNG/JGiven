package com.tngtech.jgiven.annotation;

import com.tngtech.jgiven.impl.params.DefaultCaseDescriptionProvider;
import com.tngtech.jgiven.impl.params.DefaultOrdinalCaseDescriptionProvider;

import java.lang.annotation.*;

/**
 * Use to define a description provider for scenario cases.
 * <p>
 * By default, multiple cases in a parametrized scenario are described 
 * by just listing the parameter names with their corresponding values.
 * Sometimes, however, it is useful to provide an explicit description the provides more semantic background for each case.
 * This annotation can be used to define custom descriptions.
 *
 * The enumeration of arguments starts from index 1.
 *
 * @since v0.16.0
 *
 */
@Documented
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
public @interface OrdinalCaseDescription {
    /**
     * Dummy value to indicate that no value was set
     */
    public static final String NO_VALUE = " - no value - ";

    /**
     * The description of the test case.
     * <p>
     * Placeholders of the form {@code $i} can be used that will be filled with the values of the ith parameter, starting from 1.
     * <p>
     * For example, a value {@code "Hi $1"} will be translated to {@code "Hi JGiven"} if "JGiven" is the value of the first parameter of the test method.
     */
    String value() default NO_VALUE;

    /**
     * A custom implementation of the {@link CaseDescriptionProvider} to provide a case description.
     */
    Class<? extends CaseDescriptionProvider> provider() default DefaultOrdinalCaseDescriptionProvider.class;

    /**
     * Whether or not the arguments should be formatted to string using JGiven formatter.
     * If this is set to true the list of parameter values passed to the CaseDescriptionProvider will be a list of strings,
     * otherwise it will be a list of objects with the original values passed to the method.
     */
    boolean formatValues() default true;
}
