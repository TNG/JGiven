package com.tngtech.jgiven.report.json;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.model.*;

public class ReportModelReader implements ReportModelFileHandler {
    private static final Logger log = LoggerFactory.getLogger( ReportModelReader.class );
    private final ReportGenerator.Config config;

    private CompleteReportModel completeModelReport = new CompleteReportModel();

    public ReportModelReader( ReportGenerator.Config config ) {
        this.config = config;
    }

    public CompleteReportModel readDirectory( File sourceDir ) {
        new JsonModelTraverser().traverseModels( sourceDir, this );
        return completeModelReport;
    }

    public void handleReportModel( ReportModelFile modelFile ) {
        if( modelFile.model.getClassName() == null ) {
            log.error( "ClassName in report model is null for file " + modelFile.file + ". Skipping." );
            return;
        }

        if( config.getExcludeEmptyScenarios() ) {
            log.info( "Removing empty scenarios..." );
            removeEmptyScenarios( modelFile.model );
            if( !modelFile.model.getScenarios().isEmpty() ) {
                log.debug( "File " + modelFile.file + " has only empty scenarios. Skipping." );
                completeModelReport.addModelFile( modelFile );
            }
        } else {
            completeModelReport.addModelFile( modelFile );
        }
    }

    void removeEmptyScenarios( ReportModel modelFile ) {
        Iterator<ScenarioModel> scenarios = modelFile.getScenarios().iterator();
        while( scenarios.hasNext() ) {
            ScenarioModel scenarioModel = scenarios.next();
            removeEmptyCase( scenarioModel );
            if( scenarioModel.getScenarioCases().isEmpty() ) {
                scenarios.remove();
            }
        }
    }

    private void removeEmptyCase( ScenarioModel scenarioModel ) {
        Iterator<ScenarioCaseModel> cases = scenarioModel.getScenarioCases().iterator();
        while( cases.hasNext() ) {
            ScenarioCaseModel theCase = cases.next();
            if( theCase.getSteps().isEmpty() ) {
                cases.remove();
            }
        }
    }

}
