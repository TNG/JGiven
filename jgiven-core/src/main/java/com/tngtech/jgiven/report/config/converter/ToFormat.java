package com.tngtech.jgiven.report.config.converter;

import com.tngtech.jgiven.report.ReportGenerator;

/**
 * Not total, if the format parse fails it returns null
 */
public class ToFormat implements StringConverter {
    public ReportGenerator.Format apply( String input ) {
        return ReportGenerator.Format.fromStringOrNull( input );
    }
}
