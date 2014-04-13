package com.tngtech.jgiven.report.html;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

public class HtmlFileWriter extends HtmlWriter implements Closeable {

    protected final File file;

    public HtmlFileWriter( File file ) {
        super( getPrintWriter( file ) );
        this.file = file;
    }

    private static PrintWriter getPrintWriter( File file ) {
        try {
            return new PrintWriter( file, Charsets.UTF_8.name() );
        } catch( FileNotFoundException e ) {
            throw Throwables.propagate( e );
        } catch( UnsupportedEncodingException e ) {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public void close() {
        writer.close();
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

    public static void writeScenarioModelToFile( ScenarioModel model, File file ) {
        PrintWriter printWriter = getPrintWriter( file );
        try {
            new HtmlWriter( printWriter ).write( model );
        } finally {
            ResourceUtil.close( printWriter );
        }

    }

}
