package com.tngtech.jgiven.format;

/**
 * Formatter to use a Java format string to format arguments.
 */
public class PrintfFormatter implements ArgumentFormatter<Object> {

    @Override
    public String format( Object o, String... args ) {
        return String.format( args[0], o );
    }
}
