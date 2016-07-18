package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import com.tngtech.jgiven.config.ConfigValue;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

/**
 * Generates a plain text report to a PrintStream.
 */
public class PlainTextReporter extends PlainTextWriter {
    private static final ConfigValue COLOR_CONFIG = Config.config().textColorEnabled();

    public static String toString( ScenarioModel scenarioModel ) throws UnsupportedEncodingException {
        ReportModel model = new ReportModel();
        model.addScenarioModel( scenarioModel );
        return toString( model );
    }

    public static String toString( ReportModel model ) throws UnsupportedEncodingException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter( stringWriter );
        PlainTextReporter textWriter = new PlainTextReporter( printWriter, ConfigValue.FALSE );
        try {
            textWriter.write( model );
            return stringWriter.toString();
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    public PlainTextReporter() {
        this( COLOR_CONFIG );
    }

    public PlainTextReporter( ConfigValue colorConfig ) {
        this( PrintWriterUtil.getPrintWriter( System.out, colorConfig ), colorConfig );
    }

    public PlainTextReporter( PrintWriter printWriter, ConfigValue colorConfig ) {
        super( printWriter, colorConfig != ConfigValue.FALSE );
    }

    public PlainTextReporter write( ReportModel model ) {
        model.accept( this );
        return this;
    }

    @Override
    public void visit( ReportModel multiScenarioModel ) {
        writer.println();
        String title = bold( "Test Class: " );
        title += multiScenarioModel.getClassName();
        writer.println( title );
    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        if( scenarioModel.isCasesAsTable() ) {
            scenarioModel.accept( new DataTablePlainTextScenarioWriter( writer, withColor ) );
        } else {
            scenarioModel.accept( new PlainTextScenarioWriter( writer, withColor ) );
        }
    }

    public void flush() {
        writer.flush();
    }
}
