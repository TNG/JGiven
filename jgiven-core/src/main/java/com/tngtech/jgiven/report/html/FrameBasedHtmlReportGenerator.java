package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.tngtech.jgiven.report.model.ReportModel;

public class FrameBasedHtmlReportGenerator extends AbstractHtmlReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( FrameBasedHtmlReportGenerator.class );

    static final String LINKS_FILE_NAME = "links.html";
    static final String TESTCLASSES_FRAME_NAME = "testclasses";

    public void generate( String toDir, String sourceDir ) throws IOException {
        generate( toDir, LINKS_FILE_NAME, sourceDir );
        copyFileToTargetDir( "index.html" );
    }

    public void generate( File toDir, File sourceDir ) throws IOException {
        generate( toDir, LINKS_FILE_NAME, sourceDir );
        copyFileToTargetDir( "index.html" );
    }

    @Override
    public void handleReportModel( ReportModel model, File file ) {
        String targetFileName = Files.getNameWithoutExtension( file.getName() ) + ".html";
        File targetFile = new File( toDir, targetFileName );
        log.debug( "Writing to file " + targetFile );
        try {
            HtmlFileWriter.writeModelToFile( model, targetFile );
            writeFileLink( targetFile );
        } catch( Exception e ) {
            log.error( "Error while trying to write to file " + file + ". " + e );
            throw Throwables.propagate( e );
        }
    }

    private void writeFileLink( File targetFile ) {
        String fullQualifiedName = Files.getNameWithoutExtension( targetFile.getName() );
        String packageName = Files.getNameWithoutExtension( fullQualifiedName ) + '.';
        String className = Files.getFileExtension( fullQualifiedName );
        if( Strings.isNullOrEmpty( className ) ) {
            className = packageName;
            packageName = "";
        }

        writer.println( format( "<li><a href='%s' target='%s'>%s</a>",
            targetFile.getName(),
            TESTCLASSES_FRAME_NAME,
            className ) );
    }

}
