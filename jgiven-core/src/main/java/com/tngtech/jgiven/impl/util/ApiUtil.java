package com.tngtech.jgiven.impl.util;

import com.tngtech.jgiven.exception.JGivenWrongUsageException;

/**
 * This util is used for checking inputs of API methods
 */
public class ApiUtil {

    public static <T> T notNull(T o, String message) {
        if (o == null) {
            throw new JGivenWrongUsageException(message);
        }
        return o;
    }

    public static void isTrue(boolean b, String message) {
        if (!b) {
            throw new JGivenWrongUsageException(message);
        }
    }
}
