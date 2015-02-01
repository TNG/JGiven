package com.tngtech.jgiven.report.json;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.report.model.CompleteReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;

public class ReportModelReader implements ReportModelFileHandler {
    private static final Logger log = LoggerFactory.getLogger( ReportModelReader.class );

    private CompleteReportModel completeModelReport = new CompleteReportModel();

    public CompleteReportModel readDirectory( File sourceDir ) {
        new JsonModelTraverser().traverseModels( sourceDir, this );
        return completeModelReport;
    }

    public void handleReportModel( ReportModelFile modelFile ) {
        if( modelFile.model.getClassName() == null ) {
            log.error( "ClassName in report model is null for file " + modelFile.file + ". Skipping." );
            return;
        }

        completeModelReport.addModelFile( modelFile );
    }

}
