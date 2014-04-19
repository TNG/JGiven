package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.PrintWriter;

import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Word;

public class ScenarioHtmlWriter extends ReportModelVisitor {
    final PrintWriter writer;

    ScenarioModel scenarioModel;
    ScenarioCaseModel scenarioCase;

    public ScenarioHtmlWriter( PrintWriter writer ) {
        this.writer = writer;

    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        this.scenarioModel = scenarioModel;

        writer.print( format( "<div class='scenario'><h3>%s", WordUtil.capitalize( scenarioModel.description ) ) );
        for( Tag tag : scenarioModel.tags ) {
            printTag( tag );
        }
        writer.println( "</h3>" );
        writer.println( "<div class='scenario-content'>" );
    }

    private void printTag( Tag tag ) {
        writer.print( format( "<div class='tag tag-%s'><a href='%s'>%s</a></div>",
            tag.getName(), FrameBasedHtmlReportGenerator.tagToFilename( tag ), tag.toString() ) );
    }

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        writer.println( "</div> <!-- scenario-content -->" );

        writer
            .println( format( "<div class='scenario-footer'><a href='%s.html'>%s</a></div>", scenarioModel.className,
                scenarioModel.className ) );
        writer.println( "</div>" );
    }

    @Override
    public void visit( ScenarioCaseModel scenarioCase ) {
        this.scenarioCase = scenarioCase;
        printCaseHeader( scenarioCase );
        writer.println( "<ul class='steps'>" );
    }

    void printCaseHeader( ScenarioCaseModel scenarioCase ) {
        writer.println( format( "<div class='case %sCase'>", scenarioCase.success ? "passed" : "failed" ) );
        if( !scenarioCase.arguments.isEmpty() ) {
            writer.print( format( "<h4>Case %d: ", scenarioCase.caseNr ) );

            for( int i = 0; i < scenarioCase.arguments.size(); i++ ) {
                if( scenarioModel.parameterNames.size() > i ) {
                    writer.print( scenarioModel.parameterNames.get( i ) + " = " );
                }

                writer.print( scenarioCase.arguments.get( i ) );

                if( i < scenarioCase.arguments.size() - 1 ) {
                    writer.print( ", " );
                }
            }
            writer.println( "</h4>" );
        }
    }

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( scenarioCase.success ) {
            writer.println( "<div class='topRight passed'>Passed</div>" );
        } else {
            writer.println( "<div class='failed'>Failed: " + scenarioCase.errorMessage + "</div>" );
        }
        writer.println( "</ul>" );
        writer.println( "</div><!-- case -->" );
    }

    @Override
    public void visit( StepModel stepModel ) {
        writer.print( "<li>" );

        boolean firstWord = true;
        for( Word word : stepModel.words ) {
            if( !firstWord ) {
                writer.print( ' ' );
            }
            String text = word.value;

            if( firstWord && word.isIntroWord ) {
                writer.print( format( "<span class='introWord'>%s</span>", WordUtil.capitalize( text ) ) );
            } else if( word.isArg ) {
                printArg( word );
            } else {
                writer.print( text );
            }
            firstWord = false;
        }
        writer.println( "</li>" );
    }

    private void printArg( Word word ) {
        boolean isCaseArg = scenarioCase.arguments.contains( word.value );
        String value = isCaseArg ? formatCaseArgument( word.value ) : word.value;
        value = escapeToHtml( value );
        String multiLine = value.contains( "<br />" ) ? "multiline" : "";
        String caseClass = isCaseArg ? "caseArgument" : "argument";
        writer.print( format( "<span class='%s %s'>%s</span>", caseClass, multiLine, value ) );
    }

    private String escapeToHtml( String value ) {
        return value.replaceAll( "(\r\n|\n)", "<br />" );
    }

    String formatCaseArgument( String value ) {
        return value;
    }
}
