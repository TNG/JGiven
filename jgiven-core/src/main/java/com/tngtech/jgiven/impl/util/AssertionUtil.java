package com.tngtech.jgiven.impl.util;

import com.tngtech.jgiven.exception.JGivenInternalDefectException;

/**
 * A collection of methods to assert certain conditions.
 * If an asserted condition is false a {@link JGivenInternalDefectException} is thrown.
 */
public class AssertionUtil {

    public static void assertNotNull( Object o ) {
        assertNotNull( o, "Expected a value to not be null, but it apparently was null" );
    }

    public static void assertNotNull( Object o, String msg ) {
        if( o == null ) {
            throw new JGivenInternalDefectException( msg );
        }
    }

    public static void assertTrue( boolean condition, String msg ) {
        if( !condition ) {
            throw new JGivenInternalDefectException( msg );
        }
    }

    public static void assertFalse( boolean condition, String msg ) {
        assertTrue( !condition, msg );
    }

}
