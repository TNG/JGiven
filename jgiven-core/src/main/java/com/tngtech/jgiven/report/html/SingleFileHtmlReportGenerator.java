package com.tngtech.jgiven.report.html;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.json.JsonModelTraverser;
import com.tngtech.jgiven.report.json.ReportModelFileHandler;
import com.tngtech.jgiven.report.model.ReportModel;

/**
 * Writes all scenarios into a single file.
 */
public class SingleFileHtmlReportGenerator implements ReportModelFileHandler {
    private static final Logger log = LoggerFactory.getLogger( SingleFileHtmlReportGenerator.class );
    protected PrintWriter writer;
    protected HtmlWriterUtils utils;

    public void generate( File toDir, String targetFileName, File sourceDir ) throws IOException {
        log.info( "Generating file " + targetFileName + " to directory " + toDir );

        writer = new PrintWriter( new File( toDir, targetFileName ), Charsets.UTF_8.name() );
        utils = new HtmlWriterUtils( writer );

        try {
            writeStart();
            new JsonModelTraverser().traverseModels( sourceDir, this );
            writeEnd();
        } finally {
            ResourceUtil.close( writer );
        }
    }

    public void writeStart() {
        utils.writeHtmlHeader( "JGiven HTML Report" );

        writer.println( "<div class='linklist'>" );
        writer.println( "<h1>Acceptance Tests</h1>" );
    }

    public void writeEnd() {
        writer.println( "</div></body></html>" );
    }

    @Override
    public void handleReportModel( ReportModel model, File file ) {
        new HtmlWriter( writer ).visit( model );
    }

}
