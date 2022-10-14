package com.tngtech.jgiven.report.asciidoc;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.AbstractReportModelHandler;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;

public class AsciiDocReportGenerator extends AbstractReportGenerator {

    private final List<String> allFiles = Lists.newArrayList();

    public AbstractReportConfig createReportConfig( String... args ) {
        return new AsciiDocReportConfig( args );
    }

    public void generate() {
        for( ReportModelFile reportModelFile : completeReportModel.getAllReportModels() ) {
            writeReportModelToFile( reportModelFile.model, reportModelFile.file );
        }
        generateIndexFile();
    }

    private void writeReportModelToFile( ReportModel model, File file ) {
        String targetFileName = Files.getNameWithoutExtension( file.getName() ) + ".asciidoc";

        allFiles.add( targetFileName );
        if( !config.getTargetDir().exists() ) {
            config.getTargetDir().mkdirs();
        }
        File targetFile = new File( config.getTargetDir(), targetFileName );
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter( targetFile );

        try {
            new AbstractReportModelHandler().handle( model, new AsciiDocReportModelHandler( printWriter ) );
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    private void generateIndexFile() {
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter( new File( config.getTargetDir(), "index.asciidoc" ) );
        try {
            printWriter.println( "= JGiven Documentation =\n" );
            printWriter.println( ":toc: left\n" );
            printWriter.println( "== Scenarios ==\n" );
            printWriter.println( "=== Classes ===\n" );
            printWriter.println( "include::allClasses.asciidoc[]" );
        } finally {
            ResourceUtil.close( printWriter );
        }

        printWriter = PrintWriterUtil.getPrintWriter( new File( config.getTargetDir(), "allClasses.asciidoc" ) );
        try {
            for( String fileName : allFiles ) {
                printWriter.println( "include::" + fileName + "[]\n" );
            }
        } finally {
            ResourceUtil.close( printWriter );
        }
    }
}
