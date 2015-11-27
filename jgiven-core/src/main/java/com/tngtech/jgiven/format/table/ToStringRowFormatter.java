package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.format.DefaultFormatter;

/**
 * Formats each row by just using the default formatting of an object.
 * This results in a table with just one column, where the header is the name of the parameter.
 *
 * @see com.tngtech.jgiven.annotation.Table
 * @since 0.10.0
 */
public class ToStringRowFormatter extends RowFormatter {

    private final String columnHeader;

    public ToStringRowFormatter( Class<?> type, Table tableAnnotation, String columnHeader, Annotation[] annotations ) {
        this.columnHeader = columnHeader;
    }

    @Override
    public List<String> header() {
        return ImmutableList.of( columnHeader );
    }

    @Override
    public List<String> formatRow( Object object ) {
        return ImmutableList.of( DefaultFormatter.INSTANCE.format( object ) );
    }

}
