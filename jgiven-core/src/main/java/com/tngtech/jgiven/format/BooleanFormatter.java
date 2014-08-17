package com.tngtech.jgiven.format;

/**
 * General formatter to format boolean values.
 */
public class BooleanFormatter implements ArgumentFormatter<Boolean> {

    @Override
    public String format( Boolean b, String... args ) {
        return b ? args[0] : args[1];
    }

}
