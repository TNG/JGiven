package com.tngtech.jgiven.report.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.json.JsonModelTraverser;
import com.tngtech.jgiven.report.json.ReportModelFileHandler;
import com.tngtech.jgiven.report.model.ReportModel;

public abstract class AbstractHtmlReportGenerator implements ReportModelFileHandler {
    private static final Logger log = LoggerFactory.getLogger( AbstractHtmlReportGenerator.class );

    protected File toDir;

    protected PrintWriter writer;
    protected HtmlWriterUtils utils;

    public void generate( File toDir, String targetFileName, File sourceDir ) throws IOException {
        if( !toDir.isDirectory() )
            throw new RuntimeException( toDir + " is not existing or is not a directory" );

        log.info( "Generating file " + targetFileName + " to directory " + toDir );

        this.toDir = toDir;
        writer = new PrintWriter( new File( toDir, targetFileName ), Charsets.UTF_8.name() );
        utils = new HtmlWriterUtils( writer );

        try {
            writeStart();
            new JsonModelTraverser().traverseModels( sourceDir, this );
            writeEnd();
        } finally {
            ResourceUtil.close( writer );
        }
        copyFileToTargetDir( "style.css" );
        copyFileToTargetDir( "default.css" );
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
    public abstract void handleReportModel( ReportModel model, File file );

    protected void copyFileToTargetDir( String fileName ) throws FileNotFoundException, IOException {
        InputStream stream = null;
        FileOutputStream fileOutputStream = null;
        try {
            stream = this.getClass().getResourceAsStream( "/com/tngtech/jgiven/report/html/" + fileName );
            File file = new File( toDir, fileName );
            fileOutputStream = new FileOutputStream( file );
            ByteStreams.copy( stream, fileOutputStream );
        } finally {
            ResourceUtil.close( stream, fileOutputStream );
        }
    }

}
