package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;

/**
 * Factory for creating ObjectRowFormatter instances
 *
 * @see com.tngtech.jgiven.annotation.Table
 * @since 0.10.0
 */
public interface RowFormatterFactory {
    RowFormatter create( Class<?> type, Table tableAnnotation, String parameterName, Annotation[] annotations,
            FormatterConfiguration configuration );
}
