package com.tngtech.jgiven.exception;

/**
 * If this exception is thrown there is most likely a bug in JGiven.
 */
public class JGivenInternalDefectException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JGivenInternalDefectException( String msg ) {
        super( msg
                + ". This is most propably due to an internal defect in JGiven and was not your fault. "
                + "Please consider writing a bug report on github.com/TNG/JGiven" );
    }
}
