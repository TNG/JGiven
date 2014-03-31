package com.tngtech.jgiven.report.model;

/**
 * A tag represents a Java annotation of a scenario-test
 */
public class Tag {
    public String name;

    /**
     * Guaranteed to be either of type String or of type String[].
     * Can be null.
     */
    public Object value;
}
