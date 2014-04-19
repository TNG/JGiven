package com.tngtech.jgiven.report.html;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class HtmlReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( HtmlReportGenerator.class );

    public File sourceDir = new File( "." );
    public File toDir = new File( "." );
    public boolean frames = false;
    public File customCssFile = null;

    public static void main( String... args ) throws IOException {
        HtmlReportGenerator generator = new HtmlReportGenerator();
        parseArgs( generator, args );
        generator.generate();
    }

    static void parseArgs( HtmlReportGenerator generator, String... args ) {
        for( String arg : args ) {
            if( arg.equals( "-h" ) || arg.equals( "--help" ) ) {
                printUsageAndExit();
            } else if( arg.startsWith( "--dir=" ) ) {
                generator.sourceDir = new File( arg.split( "=" )[1] );
            } else if( arg.startsWith( "--todir=" ) ) {
                generator.toDir = new File( arg.split( "=" )[1] );
            } else if( arg.equals( "--frames" ) ) {
                generator.frames = true;
            } else if( arg.startsWith( "--customcss=" ) ) {
                generator.customCssFile = new File( arg.split( "=" )[1] );
            } else {
                printUsageAndExit();
            }
        }
    }

    public void generate() throws IOException {
        if( !toDir.exists() && !toDir.mkdirs() ) {
            log.error( "Could not create target directory " + toDir );
            return;
        }

        new FrameBasedHtmlReportGenerator().generate( toDir, sourceDir );
        new SingleFileHtmlReportGenerator().generate( toDir, "allscenarios.html", sourceDir );

        if( customCssFile != null ) {
            if( !customCssFile.canRead() ) {
                log.info( "Cannot read customCssFile " + customCssFile + " skipping" );
            } else {
                Files.copy( customCssFile, new File( toDir, "custom.css" ) );
            }
        }
    }

    private static void printUsageAndExit() {
        System.err.println( "Options: [--frames] [--dir=<dir>] [--todir=<dir>] [--customcss=<cssfile>]" ); // NOSONAR
        System.exit( 1 );
    }
}
