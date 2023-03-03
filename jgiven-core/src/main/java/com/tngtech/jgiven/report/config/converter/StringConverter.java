package com.tngtech.jgiven.report.config.converter;

import com.tngtech.jgiven.report.AbstractReportGenerator;

/**
 * Interface to create converter function to specify the to map the incoming command line argument to the object
 * See {@link AbstractReportGenerator#createConfigOptions()} for example definitions of {@link com.tngtech.jgiven.report.config.ConfigOption}
 *
 * If the conversion is not possible, return null. The {@link com.tngtech.jgiven.report.config.ConfigOptionParser} terminates gracefully
 */
public interface StringConverter {
    Object apply (String input);
}
