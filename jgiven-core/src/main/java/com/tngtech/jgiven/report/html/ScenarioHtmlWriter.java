package com.tngtech.jgiven.report.html;

import static com.tngtech.jgiven.report.model.ExecutionStatus.FAILED;
import static com.tngtech.jgiven.report.model.ExecutionStatus.SUCCESS;
import static java.lang.String.format;

import java.io.PrintWriter;

import com.google.common.html.HtmlEscapers;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Word;

public class ScenarioHtmlWriter extends ReportModelVisitor {
    final PrintWriter writer;

    ScenarioModel scenarioModel;
    ScenarioCaseModel scenarioCase;
    HtmlWriterUtils utils;

    public ScenarioHtmlWriter( PrintWriter writer ) {
        this.writer = writer;
        this.utils = new HtmlWriterUtils( writer );

    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        this.scenarioModel = scenarioModel;
        writer.println( "<div class='scenario'>" );

        String id = scenarioModel.className + ":" + scenarioModel.description;

        writer.print( format( "<h3 onclick='toggle(\"%s\")'>", id ) );

        writeStatusIcon( scenarioModel.getExecutionStatus() );

        writer.print( " " + WordUtil.capitalize( scenarioModel.description ) );

        int numberOfCases = scenarioModel.getScenarioCases().size();
        if( numberOfCases > 1 ) {
            writer.print( "<span class='badge count'>" + numberOfCases + "</span>" );
        }
        utils.writeDuration( scenarioModel.getDurationInNanos() );

        writer.println( "</h3>" );

        writeTagLine( scenarioModel );
        writer.println( "<div class='scenario-body collapsed' id='" + id + "'>" );
        writer.println( "<div class='scenario-content'>" );
    }

    public void writeStatusIcon( boolean success ) {
        writeStatusIcon( success ? SUCCESS : FAILED );
    }

    public void writeStatusIcon( ExecutionStatus executionStatus ) {
        String iconClass = "icon-block";
        if( executionStatus == ExecutionStatus.FAILED ) {
            iconClass = "icon-cancel";
        } else if( executionStatus == ExecutionStatus.SUCCESS ) {
            iconClass = "icon-ok";
        }

        writer.print( format( "<i class='%s'></i>", iconClass ) );
    }

    private void writeTagLine( ScenarioModel scenarioModel ) {
        writer.print( "<div class='tag-line'>" );
        for( Tag tag : scenarioModel.tags ) {
            printTag( tag );
        }
        writer.println( "</div>" );
    }

    private void printTag( Tag tag ) {
        writer.print( tagToHtml( tag ) );
    }

    public static String tagToHtml( Tag tag ) {
        return format( "<div class='tag tag-%s'><a href='%s'>%s</a></div>",
            tag.getName(), HtmlTocWriter.tagToFilename( tag ), tag.toString() );
    }

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        writer.println( "</div> <!-- scenario-content -->" );

        writer.println( format( "<div class='scenario-footer'><a href='%s.html'>%s</a></div>",
            scenarioModel.className, scenarioModel.className ) );
        writer.println( "</div> <!-- scenario-body --> " );
        writer.println( "</div>" );
    }

    @Override
    public void visit( ScenarioCaseModel scenarioCase ) {
        this.scenarioCase = scenarioCase;
        printCaseHeader( scenarioCase );
        String collapsed = scenarioCase.getExplicitArguments().isEmpty() || scenarioModel.isCasesAsTable() ? "" : " collapsed";
        writer.println( "<ul class='steps" + collapsed + "' id='" + getCaseId() + "'>" );
    }

    private String getCaseId() {
        return scenarioModel.className + ":" + scenarioModel.description + ":" + scenarioCase.caseNr;
    }

    void printCaseHeader( ScenarioCaseModel scenarioCase ) {
        writer.println( format( "<div class='case %sCase'>", scenarioCase.success ? "passed" : "failed" ) );
        if( scenarioModel.getScenarioCases().size() > 1 ) {
            writer.print( format( "<h4 onclick='toggle(\"%s\")'>", getCaseId() ) );
            writeStatusIcon( scenarioCase.success );
            writer.print( format( " Case %d: ", scenarioCase.caseNr ) );

            for( int i = 0; i < scenarioCase.getExplicitArguments().size(); i++ ) {
                if( scenarioModel.getExplicitParameters().size() > i ) {
                    writer.print( scenarioModel.getExplicitParameters().get( i ) + " = " );
                }

                writer.print( scenarioCase.getExplicitArguments().get( i ) );

                if( i < scenarioCase.getExplicitArguments().size() - 1 ) {
                    writer.print( ", " );
                }
            }

            utils.writeDuration( scenarioCase.durationInNanos );
            writer.println( "</h4>" );
        }
    }

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( !scenarioCase.success ) {
            writer.println( "<div class='failed'>Failed: " + scenarioCase.errorMessage + "</div>" );
        }
        writer.println( "</ul>" );
        writer.println( "</div><!-- case -->" );
    }

    @Override
    public void visit( StepModel stepModel ) {
        String extendedId = "extDesc" + System.identityHashCode( stepModel );
        writer.print( "<li>" );
        if( stepModel.hasExtendedDescription() ) {
            writer.print( "<span onmouseover='showExtendedDescription(\"" + extendedId + "\")'>" );
        }

        boolean firstWord = true;
        for( Word word : stepModel.words ) {
            if( !firstWord ) {
                writer.print( ' ' );
            }
            String text = HtmlEscapers.htmlEscaper().escape( word.getValue() );
            String diffClass = diffClass( word );
            if( firstWord && word.isIntroWord() ) {
                writer.print( format( "<span class='introWord'>%s</span>", WordUtil.capitalize( text ) ) );
            } else if( word.isArg() ) {
                printArg( word );
            } else {
                if( word.isDifferent() ) {
                    writer.print( format( "<span class='%s'>%s</span>", diffClass, text ) );
                } else {
                    writer.print( text );
                }
            }
            firstWord = false;
        }

        StepStatus status = stepModel.getStatus();
        if( status != StepStatus.PASSED ) {
            String lowerCase = status.toString().toLowerCase();
            writer.print( format( " <span class='badge %s'>%s</span>", WordUtil.camelCase( lowerCase ), lowerCase.replace( '_', ' ' ) ) );
        }

        utils.writeDuration( stepModel.getDurationInNanos() );

        if( stepModel.hasExtendedDescription() ) {
            writer.print( "</span>" );
            writeExtendedDescription( stepModel, extendedId );
        }
        writer.println( "</li>" );
    }

    private void writeExtendedDescription( StepModel stepModel, String id ) {
        writer.write( "<div id='" + id + "' class='extended-description collapsed'><span class='extended-description-content'>" );
        writer.write( stepModel.getExtendedDescription() );
        writer.write( "<i class='icon-cancel' onclick='toggle(\"" + id + "\")'></i>" );
        writer.write( "</span></div>" );
    }

    private String diffClass( Word word ) {
        return word.isDifferent() ? " diff" : "";
    }

    private void printArg( Word word ) {
        String value = word.getArgumentInfo().isParameter() ? formatCaseArgument( word ) : HtmlEscapers.htmlEscaper().escape(
            word.getFormattedValue() );
        value = escapeToHtml( value );
        String multiLine = value.contains( "<br />" ) ? " multiline" : "";
        String caseClass = word.getArgumentInfo().isParameter() ? "caseArgument" : "argument";
        writer.print( format( "<span class='%s%s%s'>%s</span>", caseClass, multiLine, diffClass( word ), value ) );
    }

    private String escapeToHtml( String value ) {
        return value.replaceAll( "(\r\n|\n)", "<br />" );
    }

    String formatCaseArgument( Word word ) {
        return HtmlEscapers.htmlEscaper().escape( word.getValue() );
    }
}
