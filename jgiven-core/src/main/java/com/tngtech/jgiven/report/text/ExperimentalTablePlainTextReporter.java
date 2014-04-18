package com.tngtech.jgiven.report.text;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

public class ExperimentalTablePlainTextReporter extends PlainTextReporter {

    public ExperimentalTablePlainTextReporter( OutputStream outStream ) throws UnsupportedEncodingException {
        super( outStream, false );
    }

    @Override
    public void visit( StepModel stepModel ) {
        if( currentCaseModel.caseNr > 1 ) {
            return;
        }
        super.visit( stepModel );
    }

    @Override
    protected String wordToString( Word word ) {
        if( word.isArg ) {
            int argIndex = currentCaseModel.arguments.indexOf( word.value );
            if( argIndex > -1 ) {
                return "<" + currentScenarioModel.parameterNames.get( argIndex ) + ">";
            }
        }
        return super.wordToString( word );
    }

    @Override
    protected void printCaseLine( ScenarioCaseModel scenarioCase ) {}

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        StringBuilder builder = new StringBuilder();
        List<Integer> columnWidths = getMaxColumnWidth( scenarioModel );
        for( int width : columnWidths ) {
            builder.append( "| %" + ( width + 2 ) + "s " );
        }
        builder.append( "|" );

        String formatString = builder.toString();
        stream.println( "Examples:\n" );
        stream.println( "  " + String.format( formatString, scenarioModel.parameterNames.toArray() ) );
        for( ScenarioCaseModel c : scenarioModel.getScenarioCases() ) {
            stream.println( "  " + String.format( formatString, c.arguments.toArray() ) );
        }
    }

    private List<Integer> getMaxColumnWidth( ScenarioModel scenarioModel ) {
        List<Integer> result = Lists.newArrayList();
        for( int i = 0; i < scenarioModel.parameterNames.size(); i++ ) {
            int maxWidth = scenarioModel.parameterNames.get( i ).length();
            for( ScenarioCaseModel c : scenarioModel.getScenarioCases() ) {
                if( c.arguments.size() > i ) {
                    int width = c.arguments.get( i ).length();
                    if( width > maxWidth ) {
                        maxWidth = width;
                    }
                }
            }
            result.add( maxWidth );
        }
        return result;
    }

}
