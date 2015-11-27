package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;

/**
 * Factory for creating instances of {@link FieldBasedRowFormatter}
 *
 * @see FieldBasedRowFormatter
 * @see RowFormatterFactory
 * @see com.tngtech.jgiven.annotation.Table
 * @see ToStringRowFormatterFactory
 * @since 0.9.6
 */
public class FieldBasedRowFormatterFactory implements RowFormatterFactory {
    @Override
    public RowFormatter create( Class type, Table tableAnnotation, String parameterName, Annotation[] annotations ) {
        return new FieldBasedRowFormatter( type, tableAnnotation, parameterName, annotations );
    }
}
