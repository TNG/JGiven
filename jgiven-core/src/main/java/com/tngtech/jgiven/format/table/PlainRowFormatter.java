package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.impl.format.ParameterFormattingUtil;

/**
 * Formats each row by just using the default formatting of an object.
 * This results in a table with just one column, where the header is the name of the parameter.
 *
 * @see com.tngtech.jgiven.annotation.Table
 * @since 0.10.0
 */
public class PlainRowFormatter extends RowFormatter {

    private final String columnHeader;
    private final ObjectFormatter objectFormatter;

    public PlainRowFormatter( Class<?> type, Table tableAnnotation, String columnHeader, Annotation[] annotations,
            FormatterConfiguration configuration, ObjectFormatter objectFormatter ) {
        this.columnHeader = columnHeader;
        ParameterFormattingUtil formattingUtil = new ParameterFormattingUtil( configuration );

        this.objectFormatter = objectFormatter;
    }

    @Override
    public List<String> header() {
        return ImmutableList.of( columnHeader );
    }

    @Override
    public List<String> formatRow( Object object ) {
        return ImmutableList.of( objectFormatter.format( object ) );
    }

    /**
     * Factory for creating instances of {@link PlainRowFormatter}
     *
     * @see com.tngtech.jgiven.annotation.Table
     * @since 0.10.0
     */
    public static class Factory implements RowFormatterFactory {
        @Override
        public RowFormatter create( Class<?> parameterType, String parameterName, Table tableAnnotation,
                Annotation[] annotations,
                FormatterConfiguration configuration, ObjectFormatter<?> objectFormatter ) {
            return new PlainRowFormatter( parameterType, tableAnnotation, parameterName, annotations, configuration, objectFormatter );
        }
    }
}
