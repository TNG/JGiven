package com.tngtech.jgiven.exception;

/**
 * Thrown when a field cannot be uniquely resolved by the name or type.
 */
public class AmbiguousResolutionException extends JGivenWrongUsageException {
    private static final long serialVersionUID = 1L;

    public AmbiguousResolutionException( String message ) {
        super( message );
    }
}
