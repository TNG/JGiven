package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.model.*;

public class DataTablePlainTextScenarioWriter extends PlainTextScenarioWriter {

    public DataTablePlainTextScenarioWriter( PrintWriter writer, boolean withColor ) {
        super( writer, withColor );
    }

    @Override
    public void visit( StepModel stepModel ) {
        if( currentCaseModel.getCaseNr() > 1 ) {
            return;
        }
        super.visit( stepModel );
    }

    @Override
    protected String wordToString( Word word ) {
        if( word.isArg() && word.getArgumentInfo().isParameter() ) {
            String parameterName = word.getArgumentInfo().getParameterName();
            return bold( "<" + parameterName + ">" );
        }
        return super.wordToString( word );
    }

    @Override
    protected void printCaseLine( ScenarioCaseModel scenarioCase ) {}

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( scenarioCase.getCaseNr() == 1 ) {
            super.visitEnd( scenarioCase );
        }
    }

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        writer.println( bold( "  Cases:" ) + "\n" );

        DataTable dataTableModel = getDataTableModel( scenarioModel );
        PlainTextTableWriter dataTableWriter = new PlainTextTableWriter( writer, withColor );

        dataTableWriter.writeDataTable( dataTableModel, INDENT );
        writer.println();
    }

    private DataTable getDataTableModel( ScenarioModel scenarioModel ) {
        List<List<String>> result = Lists.newArrayList();

        boolean withDescription = scenarioModel.getCase( 0 ).hasDescription();

        List<String> headerRow = Lists.newArrayList();
        headerRow.add( "#" );
        if( withDescription ) {
            headerRow.add( "Description" );
        }

        headerRow.addAll( scenarioModel.getDerivedParameters() );
        headerRow.add( "Status" );
        result.add( headerRow );

        int i = 1;
        for( ScenarioCaseModel c : scenarioModel.getScenarioCases() ) {
            List<String> row = Lists.newArrayList();
            row.add( "" + ( i++ ) );
            if( withDescription ) {
                row.add( c.getDescription() );
            }
            row.addAll( c.getDerivedArguments() );
            row.add( getStatusText( c ) );
            result.add( row );
        }
        return new DataTable( Table.HeaderType.HORIZONTAL, result );
    }

    private String getStatusText( ScenarioCaseModel c ) {
        if( c.isSuccess() ) {
            return "Success";
        }
        return "Failed: " + c.getErrorMessage();
    }

}
