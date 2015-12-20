package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.ObjectFormatter;

/**
 * Factory for creating ObjectRowFormatter instances
 *
 * @see com.tngtech.jgiven.annotation.Table
 * @since 0.10.0
 */
public interface RowFormatterFactory {

    /**
     * Creates a new {@link com.tngtech.jgiven.format.table.RowFormatter} instance.
     * 
     * @param parameterType the type of the table parameter
     * @param parameterName the name of the table parameter
     * @param tableAnnotation the {@link com.tngtech.jgiven.annotation.Table} annotation of the parameter
     * @param annotations all annotations of the table parameter including the {@link com.tngtech.jgiven.annotation.Table} annotation
     * @param configuration the formatter configuration
     * @param objectFormatter the standard object formatter that would be used by JGiven
     * @return an instance of {@link com.tngtech.jgiven.format.table.RowFormatter}
     */
    RowFormatter create( Class<?> parameterType, String parameterName, Table tableAnnotation,
            Annotation[] annotations, FormatterConfiguration configuration, ObjectFormatter<?> objectFormatter );
}
