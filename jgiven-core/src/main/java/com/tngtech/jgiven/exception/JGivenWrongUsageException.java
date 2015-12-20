package com.tngtech.jgiven.exception;

/**
 * Thrown when JGiven was used in some wrong way.
 */
public class JGivenWrongUsageException extends RuntimeException {
    private static final String COMMON_MESSAGE = ". This exception indicates that you used JGiven in a wrong way. "
            + "Please consult the JGiven documentation at http://jgiven.org/docs and the JGiven API documentation at "
            + "http://jgiven.org/javadoc/%s for further information.";

    public JGivenWrongUsageException( String message ) {
        super( getMessage( message, null ) );
    }

    public JGivenWrongUsageException( String message, Class<?> classWithFurtherInformation ) {
        super( getMessage( message, classWithFurtherInformation ) );
    }

    public JGivenWrongUsageException( String message, Exception e ) {
        super( getMessage( message, null ), e );
    }

    public JGivenWrongUsageException( String message, Class<?> classWithFurtherInformation, Exception e ) {
        super( getMessage( message, classWithFurtherInformation ), e );
    }

    static String getMessage( String message, Class<?> classWithFurtherInformation ) {
        String path = classWithFurtherInformation == null
                ? ""
                : classWithFurtherInformation.getName().replace( '.', '/' ) + ".html";
        return String.format( message + COMMON_MESSAGE, path );
    }
}
