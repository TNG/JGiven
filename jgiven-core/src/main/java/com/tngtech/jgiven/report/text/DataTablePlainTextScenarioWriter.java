package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

public class DataTablePlainTextScenarioWriter extends PlainTextScenarioWriter {

    public DataTablePlainTextScenarioWriter( PrintWriter writer, boolean withColor ) {
        super( writer, withColor );
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
        if( word.isArg() && word.getArgumentInfo().isParameter() ) {
            String parameterName = word.getArgumentInfo().getParameterName();
            return "<" + parameterName + ">";
        }
        return super.wordToString( word );
    }

    @Override
    protected void printCaseLine( ScenarioCaseModel scenarioCase ) {}

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( scenarioCase.caseNr == 1 ) {
            super.visitEnd( scenarioCase );
        }
    }

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        writer.println( "  Cases:\n" );

        List<List<String>> dataTableModel = getDataTableModel( scenarioModel );
        PlainTextTableWriter dataTableWriter = new PlainTextTableWriter(writer, withColor);

        String caseIndent = "    ";
        dataTableWriter.writeDataTable( dataTableModel, caseIndent );
    }

    private List<List<String>> getDataTableModel( ScenarioModel scenarioModel ) {
        List<List<String>> result = Lists.newArrayList();

        List<String> headerRow = Lists.newArrayList();
        headerRow.add( "#" );
        headerRow.addAll( scenarioModel.getDerivedParameters() );
        headerRow.add( "Status" );
        result.add( headerRow );

        int i = 1;
        for( ScenarioCaseModel c : scenarioModel.getScenarioCases() ) {
            List<String> row = Lists.newArrayList();
            row.add( "" + ( i++ ) );
            row.addAll( c.getDerivedArguments() );
            row.add( getStatusText( c ) );
            result.add( row );
        }
        return result;
    }

    private String getStatusText( ScenarioCaseModel c ) {
        if( c.success ) {
            return "Success";
        }
        return "Failed: " + c.errorMessage;
    }

}
