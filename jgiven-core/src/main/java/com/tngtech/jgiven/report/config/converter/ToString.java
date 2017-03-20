package com.tngtech.jgiven.report.config.converter;

/**
 * Total conversion function
 */
public class ToString implements StringConverter {
    public Object apply( String input ) {
        return input;
    }
}
