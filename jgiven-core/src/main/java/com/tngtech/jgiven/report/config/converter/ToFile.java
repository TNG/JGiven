package com.tngtech.jgiven.report.config.converter;

import java.io.File;

/**
 * Total conversion function
 */
public class ToFile implements StringConverter{
    public Object apply (String input) {
        return new File(input);
    }
}