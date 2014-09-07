package com.tngtech.jgiven.report.html;

import java.io.PrintWriter;

import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

public class DataTableScenarioHtmlWriter extends ScenarioHtmlWriter {

    public DataTableScenarioHtmlWriter( PrintWriter writer ) {
        super( writer );
    }

    @Override
    public void visit( ScenarioCaseModel scenarioCase ) {
        if( scenarioCase.caseNr == 1 ) {
            super.visit( scenarioCase );
        } else {
            this.scenarioCase = scenarioCase;
        }
    }

    @Override
    void printCaseHeader( ScenarioCaseModel scenarioCase ) {}

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( scenarioCase.caseNr == 1 ) {
            writer.println( "</ul>" );
            writer.println( "<h4>Cases:</h4>" );
            writer.println( "<table class='data-table'>" );
            writer.println( "<tr>" );
            writer.print( "<th>#</th>" );
            for( String param : scenarioModel.parameterNames ) {
                writer.print( "<th>" + param + "</th>" );
            }
            writer.print( "<th>Status</th>" );
            writer.println( "</tr>" );
        }

        writer.println( "<tr>" );
        writer.print( "<td>" + scenarioCase.caseNr + "</td>" );
        for( String arg : scenarioCase.arguments ) {
            writer.print( "<td>" + arg + "</td>" );
        }
        writer.print( "<td>" );

        writeStatusIcon( scenarioCase.success );

        if( !scenarioCase.success ) {
            writer.println( "<div class='failed'>Failed: " + scenarioCase.errorMessage + "</div>" );
        }

        writeDuration( scenarioCase.durationInNanos );

        writer.print( "</td>" );

        writer.println( "</tr>" );
    }

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        writer.println( "</table>" );
        super.visitEnd( scenarioModel );
    }

    @Override
    public void visit( StepModel stepModel ) {
        if( scenarioCase.caseNr == 1 ) {
            super.visit( stepModel );
        }
    }

    @Override
    String formatCaseArgument( Word value ) {
        String paramName = findParameterName( value );
        return "&lt;" + paramName + "&gt;";
    }

    private String findParameterName( Word word ) {
        int index = word.getArgumentInfo().getParameterIndex();
        String paramName = index + "";
        if( index < scenarioModel.parameterNames.size() ) {
            paramName = scenarioModel.parameterNames.get( index );
        }
        return paramName;
    }

}
