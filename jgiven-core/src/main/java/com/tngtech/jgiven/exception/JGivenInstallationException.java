package com.tngtech.jgiven.exception;

public class JGivenInstallationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JGivenInstallationException( String message ) {
        super( message );
    }

    public JGivenInstallationException( String message, Throwable cause ) {
        super( message + ".\n"
                + "This is most likely an issue with your installation or your environment setup and not a JGiven defect.",
            cause );
    }

}
