package com.tngtech.jgiven.format;

/**
 * Interface for defining custom argument formatter using the {@link com.tngtech.jgiven.annotation.Format} annotation.
 * <p>
 * Note that in many cases it is more convenient to define a custom formatter annotation using the {@link com.tngtech.jgiven.annotation.AnnotationFormat}
 * annotation and defining an {@link com.tngtech.jgiven.format.AnnotationArgumentFormatter}.
 * <p>
 * Alternatively you can also provide a global formatter for a type (see {@link com.tngtech.jgiven.format.Formatter}).
 *
 * @param <T> the type of the object to format
 * @see com.tngtech.jgiven.annotation.Format
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
