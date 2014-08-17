package com.tngtech.jgiven.report.html;

import java.io.IOException;

import com.tngtech.jgiven.report.ReportGenerator;

/**
 * @deprecated replaced with @{link ReportGenerator}
 */
@Deprecated
public class HtmlReportGenerator {
    public static void main( String[] args ) throws IOException {
        System.err.println( "DEPRECATION WARNING: Please use class " + ReportGenerator.class.getName() + " for report generation" );
        ReportGenerator.main( args );
    }
}
