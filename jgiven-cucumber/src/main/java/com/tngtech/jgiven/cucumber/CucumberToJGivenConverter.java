package com.tngtech.jgiven.cucumber;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.tngtech.jgiven.cucumber.json.*;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.model.*;

public class CucumberToJGivenConverter {
    private static final Logger log = LoggerFactory.getLogger( CucumberToJGivenConverter.class );

    static class Options {
        File cucumberJsonFile;
        File targetDirectory;
    }

    public static void main( String... args ) throws IOException {
        Iterator<String> it = Arrays.asList( args ).iterator();

        Options options = new Options();

        while( it.hasNext() ) {
            String arg = it.next();
            if( arg.equals( "--targetDir" ) ) {
                if( !it.hasNext() ) {
                    printErrorAndExit( "No target directory given" );
                }
                options.targetDirectory = new File( it.next() );
            } else if( arg.equals( "--help" ) || arg.equals( "-h" ) ) {
                printUsageAndExit();
            } else {
                options.cucumberJsonFile = new File( arg );
            }
        }

        if( !options.cucumberJsonFile.exists() ) {
            printErrorAndExit( "Cucumber file " + options.cucumberJsonFile + " does not exist" );
        }

        if( options.targetDirectory.exists() && !options.targetDirectory.isDirectory() ) {
            printErrorAndExit( "Target directory " + options.targetDirectory + " is not a directory" );
        }

        if( !options.targetDirectory.exists() && !options.targetDirectory.mkdirs() ) {
            printErrorAndExit( "Could not create target directory " + options.targetDirectory );
        }

        new CucumberToJGivenConverter().convert( options );
    }

    private static void printErrorAndExit( String error ) {
        System.err.println( "ERROR: " + error + "\n" );
        printUsageAndExit();

    }

    private static void printUsageAndExit() {
        System.err.println( "Usage: --targetDir <dir> <cucumberJsonFile>" );
        System.exit( 1 );
    }

    public void convert( Options options ) throws IOException {
        log.info( "Converting " + options.cucumberJsonFile + " into folder " + options.targetDirectory );

        for( ReportModel reportModel : convert( options.cucumberJsonFile ) ) {
            File file = new File( options.targetDirectory, escape( reportModel.getClassName() ) + ".json" );
            PrintWriter printWriter = PrintWriterUtil.getPrintWriter( file );

            try {
                new Gson().toJson( reportModel, printWriter );
            } finally {
                ResourceUtil.close( printWriter );
            }
        }
    }

    private String escape( String name ) {
        return name.replaceAll( "[^a-zA-Z0-9_-]", "_" );
    }

    public List<ReportModel> convert( File cucumberJsonReportFile ) throws IOException {
        return convert( CucumberJsonReport.fromFile( cucumberJsonReportFile ) );
    }

    public List<ReportModel> convert( CucumberJsonReport cucumberJsonReport ) {
        List<ReportModel> result = Lists.newArrayListWithExpectedSize( cucumberJsonReport.features.size() );

        for( CucumberFeature cucumberFeature : cucumberJsonReport.features ) {
            result.add( convert( cucumberFeature ) );
        }

        return result;
    }

    private ReportModel convert( CucumberFeature cucumberFeature ) {
        ReportModel reportModel = new ReportModel();

        reportModel.setClassName( cucumberFeature.name );
        reportModel.setDescription( cucumberFeature.description );

        for( CucumberScenario cucumberScenario : cucumberFeature.getScenarios() ) {
            reportModel.addScenarioModel( convert( cucumberScenario ) );
        }

        return reportModel;
    }

    private ScenarioModel convert( CucumberScenario cucumberScenario ) {
        ScenarioModel scenarioModel = new ScenarioModel();

        scenarioModel.setDescription( cucumberScenario.description );
        scenarioModel.setTags( convertTags( cucumberScenario.tags ) );

        addCases( scenarioModel, cucumberScenario );

        return scenarioModel;
    }

    private void addCases( ScenarioModel scenarioModel, CucumberScenario cucumberScenario ) {

        if( cucumberScenario.examples != null ) {
            addCasesByExamples( scenarioModel, cucumberScenario );
        } else {
            scenarioModel.addCase( convertToCase( cucumberScenario ) );
        }

    }

    private void addCasesByExamples( ScenarioModel scenarioModel, CucumberScenario cucumberScenario ) {
        for( int i = 1; i < cucumberScenario.examples.size(); i++ ) {
            ScenarioCaseModel caseModel = convertToCase( cucumberScenario );
            scenarioModel.addCase( caseModel );
        }

    }

    private ScenarioCaseModel convertToCase( CucumberScenario cucumberScenario ) {
        ScenarioCaseModel caseModel = new ScenarioCaseModel();

        caseModel.setSteps( convertSteps( cucumberScenario.steps ) );

        return caseModel;
    }

    private List<StepModel> convertSteps( List<CucumberStep> steps ) {
        List<StepModel> result = Lists.newArrayList();

        if( steps != null ) {
            for( CucumberStep step : steps ) {
                result.add( convert( step ) );
            }
        }

        return result;
    }

    private StepModel convert( CucumberStep step ) {
        StepModel stepModel = new StepModel();

        stepModel.addWords( Word.introWord( step.keyword.trim() ) );
        stepModel.addWords( new Word( step.name ) );

        if( step.doc_string != null ) {
            stepModel.addWords( new Word( step.doc_string.value ) );
        }

        if( step.result != null ) {
            stepModel.setDurationInNanos( step.result.duration );
            stepModel.setStatus( convertStatus( step.result.status ) );
        } else {
            // result can be null when the scenario has examples
            stepModel.setStatus( StepStatus.PASSED );
        }

        return stepModel;
    }

    private StepStatus convertStatus( String status ) {
        if( status.equals( "passed" ) ) {
            return StepStatus.PASSED;
        } else if( status.equals( "failed" ) ) {
            return StepStatus.FAILED;
        }
        return StepStatus.SKIPPED;
    }

    private Set<Tag> convertTags( List<CucumberTag> tags ) {
        Set<Tag> result = Sets.newLinkedHashSet();

        if( tags != null ) {
            for( CucumberTag tag : tags ) {
                result.add( convert( tag ) );
            }
        }

        return result;
    }

    private Tag convert( CucumberTag cucumberTag ) {
        Tag tag = new Tag( cucumberTag.name );
        return tag;
    }
}
