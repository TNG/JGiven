package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Word;

public class HtmlWriter extends ReportModelVisitor {
    protected final PrintWriter writer;
    protected final HtmlWriterUtils utils;
    protected ScenarioModel scenarioModel;
    private ScenarioCaseModel scenarioCase;

    public HtmlWriter( PrintWriter writer ) {
        this.writer = writer;
        this.utils = new HtmlWriterUtils( writer );
    }

    public void writeHtmlHeader( String title ) {
        utils.writeHtmlHeader( title );
    }

    public void writeHtmlFooter() {
        writer.write( "</body></html>" );
    }

    public void write( ScenarioModel model ) {
        writeHtmlHeader( model.className );
        model.accept( this );
        writeHtmlFooter();
    }

    public void write( ReportModel model ) {
        writeHtmlHeader( model.className );
        model.accept( this );
        writeHtmlFooter();

    }

    public static String toString( ScenarioModel model ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter( stream );

        try {
            new HtmlWriter( printWriter ).write( model );
            printWriter.flush();
            return stream.toString();
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    public static void writeToFile( File file, ReportModel model ) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter printWriter = new PrintWriter( file, Charsets.UTF_8.name() );
        try {
            new HtmlWriter( printWriter ).write( model );
            printWriter.flush();
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    @Override
    public void visit( ReportModel reportModel ) {
        writer.println( "<div class='testcase'>" );
        writer.println( "<div class='testcase-header'>" );

        String packageName = "&nbsp;";
        String className = reportModel.className;
        if( reportModel.className.contains( "." ) ) {
            packageName = Files.getNameWithoutExtension( reportModel.className );
            className = Files.getFileExtension( reportModel.className );
        }

        writer.println( format( "<div class='packagename'>%s</div>", packageName ) );
        writer.println( format( "<h2>%s</h2>", className ) );
        writer.println( "</div>" );
        writer.println( "<div class='testcase-content'>" );
    }

    @Override
    public void visitEnd( ReportModel reportModel ) {
        writer.append( "</div>" );
        writer.append( "</div>" );
    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        this.scenarioModel = scenarioModel;

        writer.print( format( "<div class='scenario'><h3>%s", WordUtil.capitalize( scenarioModel.description ) ) );
        for( Tag tag : scenarioModel.tags ) {
            printTag( tag );
        }
        writer.println( "</h3>" );
    }

    private void printTag( Tag tag ) {
        writer.print( format( "<div class='tag tag-%s'><a href='%s'>%s</a></div>",
            tag.name, FrameBasedHtmlReportGenerator.tagToFilename( tag ), tag.toString() ) );
    }

    @Override
    public void visitEnd( ScenarioModel scenarioModel ) {
        writer.println( "</div>" );
    }

    @Override
    public void visit( ScenarioCaseModel scenarioCase ) {
        writer.println( format( "<div class='case %sCase'>", scenarioCase.success ? "passed" : "failed" ) );
        this.scenarioCase = scenarioCase;
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
        writer.println( "<ul class='steps'>" );
    }

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( scenarioCase.success ) {
            writer.println( "<div class='passed'>Passed</div>" );
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
                if( scenarioCase.arguments.contains( word.value ) ) {
                    writer.print( format( "<span class='caseArgument'>%s</span>", text ) );
                } else {
                    writer.print( format( "<span class='argument'>%s</span>", text ) );
                }
            } else {
                writer.print( text );
            }
            firstWord = false;
        }
        writer.println( "</li>" );
    }

}
