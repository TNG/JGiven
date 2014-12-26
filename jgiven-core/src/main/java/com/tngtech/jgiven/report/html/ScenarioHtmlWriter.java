package com.tngtech.jgiven.report.html;

import static com.tngtech.jgiven.report.model.ExecutionStatus.FAILED;
import static com.tngtech.jgiven.report.model.ExecutionStatus.SUCCESS;
import static java.lang.String.format;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.html.HtmlEscapers;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.*;

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

        writer.print( "<span>" );
        writer.print( " " + WordUtil.capitalize( scenarioModel.description ) );
        writer.print( "</span>" );

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

        if( hasMultipleExplicitCases() ) {
            writer.println( "<div class='case-content collapsed' id='" + getCaseId() + "'>" );
        }
        writer.println( "<ul class='steps'>" );
    }

    private boolean hasMultipleExplicitCases() {
        return scenarioModel.getScenarioCases().size() > 1 && !scenarioModel.isCasesAsTable();
    }

    private String getCaseId() {
        return "case" + System.identityHashCode( scenarioCase );
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
        if( hasMultipleExplicitCases() ) {
            writer.println( "</div><!-- case-content -->" );
        }
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

            if( word.isDataTable() ) {
                writeDataTable( word );
            } else {
                String text = HtmlEscapers.htmlEscaper().escape( word.getValue() );
                String diffClass = diffClass( word );
                if( firstWord && word.isIntroWord() ) {
                    writer.print( format( "<span class='introWord'>%s</span>", WordUtil.capitalize( text ) ) );
                } else if( word.isArg() ) {
                    printArg( word );
                } else {
                    if( word.isDifferent() ) {
                        writer.print( format( "<span class='word %s'>%s</span>", diffClass, text ) );
                    } else {
                        writer.print( "<span class='word'>" + text + "</span>" );
                    }
                }
            }
            firstWord = false;
        }

        StepStatus status = stepModel.getStatus();
        if( status != StepStatus.PASSED ) {
            String lowerCase = status.toString().toLowerCase();
            writer.print( format( " <span class='badge %s'>%s</span>", WordUtil.camelCase( lowerCase ), lowerCase.replace( '_', ' ' ) ) );
        }

        if( stepModel.hasExtendedDescription() ) {
            String extendedId = "extDesc" + System.identityHashCode( stepModel );
            if( stepModel.hasExtendedDescription() ) {
                writer.print( " <span class='show-extended-description' onclick='showExtendedDescription(\""
                        + extendedId + "\")'>i</span>" );
            }

            utils.writeDuration( stepModel.getDurationInNanos() );
            writeExtendedDescription( stepModel, extendedId );
        } else {
            utils.writeDuration( stepModel.getDurationInNanos() );
        }
        writer.println( "</li>" );
    }

    private void writeDataTable( Word word ) {
        writer.println( "<table class='data-table'>" );

        boolean header = true;
        for( List<String> row : word.getArgumentInfo().getTableValue() ) {
            writer.println( "<tr>" );

            for( String value : row ) {
                writer.println( header ? "<th>" : "<td>" );

                String escapedValue = escapeToHtml( value );
                String multiLine = value.contains( "<br />" ) ? " multiline" : "";
                writer.print( format( "<span class='%s'>%s</span>", multiLine, escapedValue ) );

                writer.println( header ? "</th>" : "</td>" );
            }

            writer.println( "</tr>" );
            header = false;
        }

        writer.println( "</table>" );
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
        printArgValue( word, value );
    }

    private void printArgValue( Word word, String value ) {
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
