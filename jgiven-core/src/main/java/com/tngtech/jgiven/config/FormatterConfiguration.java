package com.tngtech.jgiven.config;

import com.tngtech.jgiven.format.Formatter;

public interface FormatterConfiguration {
    Formatter<?> getFormatter( Class<?> typeToBeFormatted );
}
