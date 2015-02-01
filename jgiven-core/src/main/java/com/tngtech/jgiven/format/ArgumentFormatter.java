package com.tngtech.jgiven.format;

/**
 * Interface for defining customer argument formatter.
 *
 * @param <T> the type of the object to format
 */
public interface ArgumentFormatter<T> {
    /**
     * Format a single argument by taking optional formatter arguments into account.
     * @param argumentToFormat the object to format
     * @param formatterArguments optional arguments for the formatter to control the formatting.
     * @return a formatted string
     */
    String format( T argumentToFormat, String... formatterArguments );
}
