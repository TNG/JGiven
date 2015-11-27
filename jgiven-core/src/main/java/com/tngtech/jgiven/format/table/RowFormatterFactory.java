package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;

/**
 * Factory for creating ObjectRowFormatter instances
 *
 * @see com.tngtech.jgiven.annotation.Table
 * @since 0.10.0
 */
public interface RowFormatterFactory<T extends RowFormatter> {
    T create( Class<?> type, Table tableAnnotation, String parameterName, Annotation[] annotations );
}
