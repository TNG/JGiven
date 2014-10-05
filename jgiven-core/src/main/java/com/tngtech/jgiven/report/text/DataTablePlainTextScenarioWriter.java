package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
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

    static class ColumnSpec {
        int width;
        boolean leftAligned;
    }

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        List<List<String>> dataTableModel = getDataTableModel( scenarioModel );
        StringBuilder formatBuilder = new StringBuilder();
        StringBuilder lineBuilder = new StringBuilder();
        List<ColumnSpec> columnWidths = getColumnSpecs( dataTableModel );
        for( ColumnSpec spec : columnWidths ) {
            formatBuilder.append( "| %" );
            if( spec.leftAligned ) {
                formatBuilder.append( "-" );
            }
            formatBuilder.append( spec.width + "s " );
            lineBuilder.append( "+" );
            lineBuilder.append( Strings.repeat( "-", spec.width + 2 ) );
        }
        formatBuilder.append( "|" );
        lineBuilder.append( "+" );

        String formatString = formatBuilder.toString();
        String caseIndent = "    ";
        writer.println( "  Cases:\n" );
        writer.println( caseIndent + String.format( formatString, dataTableModel.get( 0 ).toArray() ) );
        writer.println( caseIndent + lineBuilder );
        for( int nrow = 1; nrow < dataTableModel.size(); nrow++ ) {
            writer.println( caseIndent + String.format( formatString, dataTableModel.get( nrow ).toArray() ) );
        }
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

    private List<ColumnSpec> getColumnSpecs( List<List<String>> dataTableModel ) {
        ColumnSpec[] result = new ColumnSpec[dataTableModel.get( 0 ).size()];
        for( int nrow = 0; nrow < dataTableModel.size(); nrow++ ) {
            List<String> row = dataTableModel.get( nrow );
            for( int ncol = 0; ncol < row.size(); ncol++ ) {
                String value = row.get( ncol );
                int width = value.length();
                ColumnSpec spec = result[ncol];
                if( spec == null ) {
                    spec = new ColumnSpec();
                    result[ncol] = spec;
                }
                if( width > spec.width ) {
                    spec.width = width;
                }

                if( nrow > 0 && Doubles.tryParse( value ) == null ) {
                    spec.leftAligned = true;
                }
            }
        }
        return Lists.newArrayList( result );
    }
}
