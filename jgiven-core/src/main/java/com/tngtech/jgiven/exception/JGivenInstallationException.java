package com.tngtech.jgiven.exception;

public class JGivenInstallationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String SUFFIX =
            ".\nThis is most likely an issue with your installation or your environment setup and not a JGiven defect.";

    public JGivenInstallationException( String message ) {
        super( message + SUFFIX );
    }

    public JGivenInstallationException( String message, Throwable cause ) {
        super(message + SUFFIX,
            cause );
    }

}
