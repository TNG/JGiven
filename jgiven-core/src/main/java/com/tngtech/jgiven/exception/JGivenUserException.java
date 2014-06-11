package com.tngtech.jgiven.exception;

import java.lang.reflect.Method;

/**
 * This exception is thrown when JGiven tried to execute a used defined method and that method has thrown an exception.
 */
public class JGivenUserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JGivenUserException( Method method, String methodDescription, Throwable cause ) {
        super( "JGiven caught an exception while trying to execute method " + method + methodDescription + ".", cause );
    }

}
