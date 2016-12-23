package com.tngtech.jgiven.exception;

import java.lang.reflect.Field;

/**
 * This exception is thrown if a scenario state has been marked as required,
 * but the state hasn't been provided.
 */
public class JGivenMissingRequiredScenarioStateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JGivenMissingRequiredScenarioStateException( Field field ) {
        super( "The field " + field.getName() + " is required but has not been provided." );
    }

}
