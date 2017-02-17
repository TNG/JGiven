package com.tngtech.jgiven.report.config.converter;

/**
 * Total conversion function
 */
public class ToBoolean implements StringConverter{
    public Object apply (String input) {
        return Boolean.parseBoolean( input );
    }
}