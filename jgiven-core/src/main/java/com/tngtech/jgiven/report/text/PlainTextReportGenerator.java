package com.tngtech.jgiven.report.text;

import java.io.File;
import java.io.PrintWriter;

import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainTextReportGenerator extends AbstractReportGenerator {

    private static final Logger log = LoggerFactory.getLogger( PlainTextReportGenerator.class );

    public AbstractReportConfig createReportConfig( String... args ) {
        return new PlainTextReportConfig(args);
    }

    public void generate() {
        generateOutputDirectory();
        for( ReportModelFile reportModelFile : completeReportModel.getAllReportModels() ) {
            handleReportModel(reportModelFile.model(), reportModelFile.file());
        }
    }

    private void generateOutputDirectory() {
        var outputDir = config.getTargetDir();
        if( !outputDir.exists()  && !outputDir.mkdirs()) {
            log.error( "Could not create target directory " + outputDir);
            return;
        }
    }

    public void handleReportModel( ReportModel model, File file ) {
        String targetFileName = Files.getNameWithoutExtension( file.getName() ) + ".feature";
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter( new File( config.getTargetDir(), targetFileName ) );

        try {
            model.accept( new PlainTextScenarioWriter( printWriter, false ) );
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

}
