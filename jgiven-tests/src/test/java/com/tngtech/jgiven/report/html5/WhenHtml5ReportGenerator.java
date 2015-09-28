package com.tngtech.jgiven.report.html5;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.tngtech.jgiven.report.WhenReportGenerator;

public class WhenHtml5ReportGenerator<SELF extends WhenHtml5ReportGenerator<SELF>> extends WhenReportGenerator<SELF> {

    public SELF the_HTML_Report_Generator_is_executed() {
        Html5ReportGenerator html5ReportGenerator = new Html5ReportGenerator() {
            @Override
            protected void unzipApp( File toDir ) throws IOException {
                try {
                    super.unzipApp( toDir );
                } catch( Exception e ) {
                    // unzipping does not work when testing within the IDE
                    FileUtils.copyDirectory( new File( "jgiven-html5-report/build/app" ), toDir );
                }
            }
        };

        html5ReportGenerator.generate( getCompleteReportModel(), targetReportDir, config );
        return self();
    }

    public SELF the_HTML_Report_Generator_is_executed_with_title( String title ) {
        config.setTitle( title );
        return the_HTML_Report_Generator_is_executed();
    }
}
