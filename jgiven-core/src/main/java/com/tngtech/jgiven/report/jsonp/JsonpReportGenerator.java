package com.tngtech.jgiven.report.jsonp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.json.JsonModelTraverser;
import com.tngtech.jgiven.report.json.ReportModelFileHandler;
import com.tngtech.jgiven.report.model.ReportModel;

public class JsonpReportGenerator implements ReportModelFileHandler {
    private Appendable writer;

    @Override
    public void handleReportModel( ReportModel model, File file ) {
        new Gson().toJson( model, writer );
        try {
            writer.append( "," );
        } catch( IOException e ) {
            throw Throwables.propagate( e );
        }
    }

    public void generate( File toDir, File sourceDir ) throws IOException {
        File targetFile = new File( toDir, "allScenarios.js" );

        PrintStream printStream = new PrintStream( new FileOutputStream( targetFile ), false, "utf-8" );
        this.writer = printStream;

        try {
            this.writer.append( "var allScenarios = [" );
            new JsonModelTraverser().traverseModels( sourceDir, this );
            this.writer.append( "];" );
            printStream.flush();
        } finally {
            ResourceUtil.close( printStream );
        }
    }
}
