package com.tngtech.jgiven.report.html5;

import java.io.IOException;

import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.WhenReportGenerator;

public class WhenHtml5ReportGenerator<SELF extends WhenHtml5ReportGenerator<SELF>> extends WhenReportGenerator<SELF> {

    public SELF the_HTML_Report_Generator_is_executed() throws IOException {
        the_report_generator_is_executed_with_format( ReportGenerator.Format.HTML5 );
        return self();
    }

    public SELF the_HTML_Report_Generator_is_executed_with_title( String title ) throws IOException {
        html5ReportConfig.setTitle( title );
        return the_HTML_Report_Generator_is_executed();
    }

    public SELF showing_thumbnails_is_set_to( boolean showThumbnails ) {
        html5ReportConfig.setShowThumbnails( showThumbnails );
        return self();
    }
}
