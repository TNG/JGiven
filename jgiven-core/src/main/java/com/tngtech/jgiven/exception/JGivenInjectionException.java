package com.tngtech.jgiven.exception;

public class JGivenInjectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JGivenInjectionException( String message, Exception e ) {
        super( message, e );
    }

}
