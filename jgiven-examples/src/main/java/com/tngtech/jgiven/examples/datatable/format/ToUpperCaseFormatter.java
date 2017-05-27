package com.tngtech.jgiven.examples.datatable.format;

import com.tngtech.jgiven.format.ArgumentFormatter;

public class ToUpperCaseFormatter implements ArgumentFormatter<String> {
    @Override
    public String format( String value, String... args ) {
        if( value == null ) {
            return "";
        }

        return value.toUpperCase();
    }
}
