package com.tngtech.jgiven.report.impl;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.report.json.ScenarioJsonWriter;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.text.PlainTextReporter;

public class CommonReportHelper {
    private static final Logger log = LoggerFactory.getLogger( CommonReportHelper.class );

    public void finishReport( ReportModel model ) {
        if( !Config.config().isReportEnabled() ) {
            return;
        }

        new CaseArgumentAnalyser().analyze( model );

        if( Config.config().textReport() ) {
            new PlainTextReporter().write( model );
        }

        Optional<File> optionalReportDir = Config.config().getReportDir();
        if( optionalReportDir.isPresent() ) {
            File reportDir = optionalReportDir.get();
            if( !reportDir.exists() && !reportDir.mkdirs() ) {
                log.error( "Could not create report directory " + reportDir );
            }
            File reportFile = new File( reportDir, model.className + ".json" );
            log.info( "Writing scenario report to file " + reportFile.getAbsolutePath() );
            new ScenarioJsonWriter( model ).write( reportFile );
        }
    }

    public static PrintWriter getPrintWriter( File file ) {
        try {
            return new PrintWriter( file, Charsets.UTF_8.name() );
        } catch( Exception e ) {
            throw Throwables.propagate( e );
        }
    }

    public static PrintWriter getPrintWriter( OutputStream outputStream ) {
        try {
            return new PrintWriter( new OutputStreamWriter( outputStream, Charsets.UTF_8.name() ) );
        } catch( UnsupportedEncodingException e ) {
            throw Throwables.propagate( e );
        }
    }
}
