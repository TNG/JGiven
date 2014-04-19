package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ScenarioModel;

public class HtmlWriter extends ReportModelVisitor {
    protected final PrintWriter writer;
    protected final HtmlWriterUtils utils;

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

    public static String toString( final ScenarioModel model ) {
        return toString( new Function<PrintWriter, Void>() {
            @Override
            public Void apply( PrintWriter input ) {
                new HtmlWriter( input ).write( model );
                return null;
            }
        } );
    }

    public static String toString( final ReportModel model ) {
        return toString( new Function<PrintWriter, Void>() {
            @Override
            public Void apply( PrintWriter input ) {
                new HtmlWriter( input ).write( model );
                return null;
            }
        } );
    }

    public static String toString( Function<PrintWriter, Void> writeFunction ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter printWriter = null;

        try {
            printWriter = new PrintWriter( new OutputStreamWriter( stream, Charsets.UTF_8.name() ), false );
            writeFunction.apply( printWriter );
            printWriter.flush();
            return stream.toString( Charsets.UTF_8.name() );
        } catch( UnsupportedEncodingException e ) {
            throw Throwables.propagate( e );
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

        String packageName = "";
        String className = reportModel.className;
        if( reportModel.className.contains( "." ) ) {
            packageName = Files.getNameWithoutExtension( reportModel.className );
            className = Files.getFileExtension( reportModel.className );
        }

        if( !Strings.isNullOrEmpty( packageName ) ) {
            writer.println( format( "<div class='packagename'>%s</div>", packageName ) );
        }

        writer.println( format( "<h2>%s</h2>", className ) );

        if( !Strings.isNullOrEmpty( reportModel.description ) ) {
            writer.println( format( "<div class='description'>%s</div>", reportModel.description ) );
        }

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
        ScenarioHtmlWriter scenarioHtmlWriter;
        if( scenarioModel.isCasesAsTable() ) {
            scenarioHtmlWriter = new DataTableScenarioHtmlWriter( writer );
        } else {
            scenarioHtmlWriter = new MultiCaseScenarioHtmlWriter( writer );
        }
        scenarioModel.accept( scenarioHtmlWriter );
    }

    private static PrintWriter getPrintWriter( File file ) {
        try {
            return new PrintWriter( file, Charsets.UTF_8.name() );
        } catch( Exception e ) {
            throw Throwables.propagate( e );
        }
    }

    public static void writeModelToFile( ReportModel model, File file ) {
        PrintWriter printWriter = getPrintWriter( file );
        try {
            HtmlWriter htmlWriter = new HtmlWriter( printWriter );
            htmlWriter.write( model );
        } finally {
            ResourceUtil.close( printWriter );
        }

    }

}
