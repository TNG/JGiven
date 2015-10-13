package com.tngtech.jgiven.annotation;

import java.util.List;

/**
 * Provides the description of a scenario case.
 * 
 * @since 0.9.2
 */
public interface CaseDescriptionProvider {

    /**
     * Provides the description of a single scenario case depending on the test parameters and optional additional arguments
     * @param value the value provided by the {@link com.tngtech.jgiven.annotation.CaseDescription} annotation.
     * @param parameterNames the parameter names
     * @param parameterValues the parameter values. Depending on the value of {@link CaseDescription#formatValues()}, this is
     *                        either a list of formatted strings, or a list of the original values passed to the test method
     * @return a description of the case
     */
    String description( String value, List<String> parameterNames, List<?> parameterValues );

}
