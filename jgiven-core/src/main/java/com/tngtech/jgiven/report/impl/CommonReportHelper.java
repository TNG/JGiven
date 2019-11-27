package com.tngtech.jgiven.report.impl;

import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.report.analysis.CaseArgumentAnalyser;
import com.tngtech.jgiven.report.json.ScenarioJsonWriter;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.text.PlainTextReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class CommonReportHelper {
    private static final Logger log = LoggerFactory.getLogger( CommonReportHelper.class );

    public void finishReport(ReportModel model ) {
        if( !Config.config().isReportEnabled() ) {
            return;
        }

        if( model == null || model.getScenarios().isEmpty() ) {
            return;
        }

        new CaseArgumentAnalyser().analyze( model );

        if( Config.config().textReport() ) {
            new PlainTextReporter().write( model ).flush();
        }

        Optional<File> optionalReportDir = Config.config().getReportDir();
        if(optionalReportDir.isPresent() ) {
            setupReportWriter(model, optionalReportDir.get());
        }
    }

    private void setupReportWriter(ReportModel model, File reportDir) {
        if( !reportDir.exists() && !reportDir.mkdirs() ) {
            log.error( "Could not create report directory " + reportDir );
            return;
        }
        File reportFile = new File( reportDir, model.getClassName() + ".json" );
        log.debug( "Writing scenario report to file " + reportFile.getAbsolutePath() );
        new ScenarioJsonWriter( model ).write( reportFile );
    }
}
