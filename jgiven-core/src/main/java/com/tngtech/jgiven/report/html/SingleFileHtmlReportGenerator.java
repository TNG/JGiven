package com.tngtech.jgiven.report.html;

import java.io.File;

import com.tngtech.jgiven.report.model.ReportModel;

/**
 * Writes all scenarios into a single file.
 */
public class SingleFileHtmlReportGenerator extends AbstractHtmlReportGenerator {

    @Override
    public void handleReportModel( ReportModel model, File file ) {
        new HtmlWriter( writer ).visit( model );
    }

}
