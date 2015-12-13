package com.tngtech.jgiven.exception;

/**
 * Thrown if a parameter in a step method has multiple formatting annotations.
 */
public class AmbiguousFormattingException extends JGivenWrongUsageException {
    private static final long serialVersionUID = 1L;

    public AmbiguousFormattingException( String message ) {
        super( message );
    }
}
