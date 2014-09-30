package com.tngtech.jgiven.report.text;

import java.io.File;
import java.io.PrintWriter;

import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.json.JsonModelTraverser;
import com.tngtech.jgiven.report.json.ReportModelFileHandler;
import com.tngtech.jgiven.report.model.ReportModel;

public class PlainTextReportGenerator implements ReportModelFileHandler {

    private File toDir;

    @Override
    public void handleReportModel( ReportModel model, File file ) {
        String targetFileName = Files.getNameWithoutExtension( file.getName() ) + ".feature";
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter( new File( toDir, targetFileName ) );

        try {
            model.accept( new PlainTextScenarioWriter( printWriter, false ) );
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    public void generate( File toDir, File sourceDir ) {
        this.toDir = toDir;
        new JsonModelTraverser().traverseModels( sourceDir, this );
    }
}
