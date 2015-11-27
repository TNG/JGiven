package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;

/**
 * Factory for creating instances of {@link ToStringRowFormatter}
 *
 * @see com.tngtech.jgiven.annotation.Table
 * @since 0.10.0
 */
public class ToStringRowFormatterFactory implements RowFormatterFactory {
    @Override
    public RowFormatter create( Class type, Table tableAnnotation, String parameterName, Annotation[] annotations ) {
        return new ToStringRowFormatter( type, tableAnnotation, parameterName, annotations );
    }
}
