package com.tngtech.jgiven.exception;

/**
 * @see com.tngtech.jgiven.annotation.NotImplementedYet
 */
public class FailIfPassedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FailIfPassedException() {
        super( "Test succeeded, but failIfPassed set to true" );
    }
}
