package com.tngtech.jgiven.report.config.converter;

/**
 * Total conversion function
 */
public class ToBoolean implements StringConverter{
    public Object apply (String input) {
        if (input != null){
            if (input.equalsIgnoreCase( "true" )){
                return true;
            }
            if (input.equalsIgnoreCase( "false" )){
                return false;
            }
        }
        return null;
    }
}