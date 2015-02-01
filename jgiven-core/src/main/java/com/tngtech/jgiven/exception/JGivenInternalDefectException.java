package com.tngtech.jgiven.exception;

/**
 * If this exception is thrown there is most likely a bug in JGiven.
 */
public class JGivenInternalDefectException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String COMMON_MESSAGE = ". This is most probably due to an internal defect in JGiven and was not your fault. "
            + "Please consider writing a bug report at http://github.com/TNG/JGiven";

    public JGivenInternalDefectException( String msg ) {
        super( msg + COMMON_MESSAGE );
    }

    public JGivenInternalDefectException( String msg, Exception e ) {
        super( msg + COMMON_MESSAGE, e );
    }
}
