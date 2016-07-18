package com.tngtech.jgiven.config;

/**
 * Represents a configuration value 
 */
public enum ConfigValue {
    TRUE,
    FALSE,
    AUTO;

    public static ConfigValue fromString( String value ) {
        if( "false".equalsIgnoreCase( value ) ) {
            return FALSE;
        }

        if( "true".equalsIgnoreCase( value ) ) {
            return TRUE;
        }

        return AUTO;
    }
}
