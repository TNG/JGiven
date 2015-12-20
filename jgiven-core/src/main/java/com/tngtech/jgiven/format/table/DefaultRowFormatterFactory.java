package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;

/**
 * Default RowFormatterFactory that evaluates the {@link com.tngtech.jgiven.annotation.Table#objectFormatting()}
 * attribute to create a RowFormatter.
 *
 * @see com.tngtech.jgiven.annotation.Table
 * @see com.tngtech.jgiven.format.table.FieldBasedRowFormatter
 * @see com.tngtech.jgiven.format.table.PlainRowFormatter
 * @since 0.10.0
 */
public class DefaultRowFormatterFactory implements RowFormatterFactory {
    @Override
    public RowFormatter create( Class<?> parameterType, String parameterName, Table tableAnnotation,
            Annotation[] annotations, FormatterConfiguration configuration, ObjectFormatter<?> objectFormatter ) {
        Table.ObjectFormatting objectFormatting = tableAnnotation.objectFormatting();
        RowFormatterFactory factory;

        if( objectFormatting == Table.ObjectFormatting.PLAIN
                || !( objectFormatter instanceof DefaultFormatter ) ) {
            factory = new PlainRowFormatter.Factory();
        } else {
            factory = new FieldBasedRowFormatter.Factory();
        }

        return factory.create( parameterType, parameterName, tableAnnotation, annotations, configuration, objectFormatter );
    }
}
