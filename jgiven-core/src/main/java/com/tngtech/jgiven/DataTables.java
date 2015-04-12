package com.tngtech.jgiven;

import java.util.Arrays;

import com.google.common.collect.Iterables;

/**
 * A utility class to create data tables.
 */
public class DataTables {

    /**
     * Creates a table, i.e. a list of lists, with {@code numberOfColumns} columns from the given {@code data} including the header.
     * <p>
     * Example:
     * <pre>{@code
     *     table(2,
     *        "name", "age", // header
     *        "Peter", 20,   // first row of data
     *        "Susan", 35);  // second row of data
     * }</pre>
     *
     * @param numberOfColumns the number of columns the resulting table should have
     * @param data            the data of the table represented as a one-dimensional list,
     *                        the number of entries must be a multiple of {@code numberOfColumns}.
     *                        The first {@code numberOfColumns} elements are taken as the header of the table,
     *                        the remaining values as data
     * @return a list of list that contains the same data as {@code data}, but in a two-dimensional form
     * @throws java.lang.IllegalArgumentException if {@code data.length % numberOfColumns != 0} 
     */
    public static Iterable<? extends Iterable<?>> table( int numberOfColumns, Object... data ) {
        if( data.length % numberOfColumns != 0 ) {
            throw new IllegalArgumentException( "The provided number of data elements '" + data.length +
                    "' is not a multiple of " + numberOfColumns );
        }

        return Iterables.partition( Arrays.asList( data ), numberOfColumns );
    }

}
