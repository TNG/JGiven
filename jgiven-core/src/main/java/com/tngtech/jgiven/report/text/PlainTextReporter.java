package com.tngtech.jgiven.report.text;

import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.fusesource.jansi.Ansi.Color;

import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

/**
 * Generates a plain text report to a PrintStream.
 */
public class PlainTextReporter extends PlainTextWriter {
    private static final boolean COLOR_ENABLED = Config.config().textColorEnabled();

    public static String toString( ReportModel model ) throws UnsupportedEncodingException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter( stringWriter );
        PlainTextReporter textWriter = new PlainTextReporter( printWriter, false );
        try {
            textWriter.write( model );
            return stringWriter.toString();
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    public PlainTextReporter() {
        this( COLOR_ENABLED );
    }

    public PlainTextReporter( boolean withColor ) {
        this( PrintWriterUtil.getPrintWriter( System.out ), withColor );
    }

    public PlainTextReporter( PrintWriter printWriter, boolean withColor ) {
        super( printWriter, withColor );
    }

    public PlainTextReporter write( ReportModel model ) {
        model.accept( this );
        return this;
    }

    /**
     * Closes the underlying PrintWriter
     */
    public void close() {
        writer.close();
    }

    public void write( ScenarioModel scenarioModel ) {
        scenarioModel.accept( this );
    }

    @Override
    public void visit( ReportModel multiScenarioModel ) {
        writer.println();
        String title = withColor( Color.RED, INTENSITY_BOLD, "Test Class: " );
        title += withColor( Color.RED, multiScenarioModel.getClassName() );
        println( Color.RED, title );
    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        if( scenarioModel.isCasesAsTable() ) {
            scenarioModel.accept( new DataTablePlainTextScenarioWriter( writer, withColor ) );
        } else {
            scenarioModel.accept( new PlainTextScenarioWriter( writer, withColor ) );
        }
    }

}
