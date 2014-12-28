package com.tngtech.jgiven.format;

import com.tngtech.jgiven.annotation.Table;

/**
 * Special formatter that will format arguments as a table. 
 * This formatter is used when a parameter is annotated with the {@link com.tngtech.jgiven.annotation.Table}
 * annotation.
 * 
 * @see com.tngtech.jgiven.annotation.Table
 */
public class TableFormatter implements ArgumentFormatter<Object> {

    public final Table tableAnnotation;

    public TableFormatter(Table tableAnnotation) {
        this.tableAnnotation = tableAnnotation;
    }

    @Override
    public String format( Object o, String... args ) {
        // concrete formatting is implemented in JGiven
        return null;
    }
}
