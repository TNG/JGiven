package com.tngtech.jgiven.exception;

/**
 * Thrown when JGiven was used in some wrong way.
 */
public class JGivenWrongUsageException extends RuntimeException {
    public JGivenWrongUsageException( String message ) {
        super( message );
    }

    public JGivenWrongUsageException( String message, Exception e ) {
        super( message, e );
    }
}
