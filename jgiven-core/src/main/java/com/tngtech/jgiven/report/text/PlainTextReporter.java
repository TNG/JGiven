package com.tngtech.jgiven.report.text;

import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.fusesource.jansi.Ansi.Color;

import com.google.common.base.Charsets;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

public class PlainTextReporter extends PlainTextWriter {
    private static final boolean COLOR_ENABLED = Config.config().textColorEnabled();

    PlainTextScenarioWriter scenarioWriter;

    public static String toString( ReportModel model ) throws UnsupportedEncodingException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PlainTextReporter textWriter = new PlainTextReporter( new PrintStream( stream, false, Charsets.UTF_8.name() ), false );
        textWriter.write( model );
        return stream.toString( Charsets.UTF_8.name() );
    }

    public PlainTextReporter() {
        this( COLOR_ENABLED );
    }

    public PlainTextReporter( boolean withColor ) {
        this( System.out, withColor );
    }

    public PlainTextReporter( OutputStream outputStream, boolean withColor ) throws UnsupportedEncodingException {
        this( new PrintStream( outputStream, false, Charsets.UTF_8.name() ), withColor );
    }

    private PlainTextReporter( PrintStream stream, boolean withColor ) {
        super( stream, withColor );
    }

    public void write( ReportModel model ) {
        model.accept( this );
    }

    public void write( ScenarioModel scenarioModel ) {
        scenarioModel.accept( this );
    }

    @Override
    public void visit( ReportModel multiScenarioModel ) {
        stream.println();
        String title = withColor( Color.RED, INTENSITY_BOLD, "Test Class: " );
        title += withColor( Color.RED, multiScenarioModel.className );
        println( Color.RED, title );
    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        if( scenarioModel.isCasesAsTable() ) {
            scenarioModel.accept( new DataTablePlainTextScenarioWriter( stream, withColor ) );
        } else {
            scenarioModel.accept( new PlainTextScenarioWriter( stream, withColor ) );
        }
    }

}
