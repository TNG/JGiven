package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;

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
    public RowFormatter create( Class type, Table tableAnnotation, String parameterName, Annotation[] annotations,
            FormatterConfiguration configuration ) {
        RowFormatterFactory factory = tableAnnotation.objectFormatting() == Table.ObjectFormatting.FIELDS
                ? new FieldBasedRowFormatter.Factory()
                : new PlainRowFormatter.Factory();

        return factory.create( type, tableAnnotation, parameterName, annotations, configuration );
    }
}
