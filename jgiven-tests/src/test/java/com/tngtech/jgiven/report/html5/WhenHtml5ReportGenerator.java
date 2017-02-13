package com.tngtech.jgiven.report.html5;

import java.io.IOException;

import com.tngtech.jgiven.report.WhenReportGenerator;

public class WhenHtml5ReportGenerator<SELF extends WhenHtml5ReportGenerator<SELF>> extends WhenReportGenerator<SELF> {

    public SELF the_HTML_Report_Generator_is_executed() throws IOException {
        createReportGenerator();
        reportGenerator.addFlag( "--format=html5" );
        reportGenerator.generate();
        return self();
    }

    public SELF the_HTML_Report_Generator_is_executed_with_title( String title ) throws IOException {
        reportGenerator.addFlag( "--title=" + title );
        return the_HTML_Report_Generator_is_executed();
    }

    public SELF showing_thumbnails_is_set_to( boolean option ) {
        reportGenerator.addFlag( "--show-thumbnails=" + option );
        return self();
    }
}
