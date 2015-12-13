package com.tngtech.jgiven.exception;

/**
 * Thrown when JGiven was used in some wrong way.
 */
public class JGivenWrongUsageException extends RuntimeException {
    private static final String COMMON_MESSAGE = ". This exception indicates that you used JGiven in a wrong way. "
            + "Please consult the JGiven documentation at http://jgiven.org/docs for further information.";

    public JGivenWrongUsageException( String message ) {
        super( message + COMMON_MESSAGE );
    }

    public JGivenWrongUsageException( String message, Exception e ) {
        super( message + COMMON_MESSAGE, e );
    }
}
