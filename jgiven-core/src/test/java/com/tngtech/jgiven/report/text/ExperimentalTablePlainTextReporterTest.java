package com.tngtech.jgiven.report.text;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Charsets;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

@RunWith( DataProviderRunner.class )
public class ExperimentalTablePlainTextReporterTest {

    @Test
    public void testExperimentalTablePlainTextReporter() throws UnsupportedEncodingException {
        ReportModel model = new ReportModel();
        ScenarioModel scenarioModel = new ScenarioModel();
        scenarioModel.addParameterNames( "param1", "param2" );
        scenarioModel.addCase( getCaseModel( "4", "true" ) );
        scenarioModel.addCase( getCaseModel( "12312312312", "false" ) );
        model.scenarios.add( scenarioModel );

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ExperimentalTablePlainTextReporter reporter = new ExperimentalTablePlainTextReporter( stream );
        model.accept( reporter );
        System.out.println( stream.toString( Charsets.UTF_8.name() ) );
    }

    private ScenarioCaseModel getCaseModel( String arg1, String arg2 ) {
        ScenarioCaseModel caseModel = new ScenarioCaseModel();
        caseModel.addArguments( arg1, arg2 );
        caseModel.addStep( new StepModel().addWords( Word.introWord( "given" ), Word.argWord( arg1 ), new Word( "and" ),
            Word.argWord( "something" ) ) );
        return caseModel;
    }
}
