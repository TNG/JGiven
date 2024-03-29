package com.tngtech.jgiven.format;

/**
 * General interface to format Objects to Strings
 */
@FunctionalInterface
public interface ObjectFormatter<T> {
    String format( T o );
}
