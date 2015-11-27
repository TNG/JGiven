package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.model.DataTable;

/**
 * Formatter that will format arguments as a table.
 * This formatter is used when a parameter is annotated with the {@link com.tngtech.jgiven.annotation.Table}
 * annotation.
 *
 * @see com.tngtech.jgiven.annotation.Table
 */
public interface TableFormatter {

    /**
     * Generates a {@link DataTable} from a given table argument
     *
     * @param tableArgument the actual argument passed to the step method
     * @param tableAnnotation the annotation of the step method parameter
     * @param parameterName the name of the step method parameter
     * @param allAnnotations all annotations of the step method parameter, including {@link com.tngtech.jgiven.annotation.Table}
     * @return a DataTable instance that defines how the table should look in the report
     */
    DataTable format( Object tableArgument, Table tableAnnotation, String parameterName, Annotation... allAnnotations );
}
