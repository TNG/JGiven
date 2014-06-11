package com.tngtech.jgiven.exception;

/**
 * This exception is thrown when JGiven tried to execute a used defined method, but the method could not be executed for some reason.
 */
public class JGivenExecutionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JGivenExecutionException( String message, Throwable cause ) {
        super( message, cause );
    }
}
