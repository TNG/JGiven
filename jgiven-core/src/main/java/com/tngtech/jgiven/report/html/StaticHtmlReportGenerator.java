package com.tngtech.jgiven.report.html;

import java.io.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.html.PackageTocBuilder.PackageToc;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.Tag;

public class StaticHtmlReportGenerator extends AbstractReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( StaticHtmlReportGenerator.class );

    @Override
    public void generate() {
        writeEnd();
        copyFileToTargetDir( "style.css" );
        copyFileToTargetDir( "default.css" );
        copyFileToTargetDir( "print.css" );
        copyFileToTargetDir( "report.js" );
        copyFileToTargetDir( "fontawesome.css" );
        copyFileToTargetDir( "fontawesome.ttf" );
    }

    protected void copyFileToTargetDir( String fileName ) {
        InputStream stream = null;
        FileOutputStream fileOutputStream = null;
        try {
            stream = this.getClass().getResourceAsStream( "/com/tngtech/jgiven/report/html/" + fileName );
            File file = new File( targetDirectory, fileName );
            fileOutputStream = new FileOutputStream( file );
            ByteStreams.copy( stream, fileOutputStream );
        } catch( FileNotFoundException e ) {
            e.printStackTrace();
        } catch( IOException e ) {
            e.printStackTrace();
        } finally {
            ResourceUtil.close( stream, fileOutputStream );
        }
    }

    public void writeEnd() {
        PackageToc packageToc = new PackageTocBuilder( completeReportModel.getAllReportModels() ).getRootPackageToc();
        HtmlTocWriter tocWriter = new HtmlTocWriter( completeReportModel, packageToc );

        for( ReportModelFile modelFile : completeReportModel.getAllReportModels() ) {
            String targetFileName = Files.getNameWithoutExtension( modelFile.file.getName() ) + ".html";
            File targetFile = new File( targetDirectory, targetFileName );

            ReportModelHtmlWriter modelWriter = ReportModelHtmlWriter.writeModelToFile( modelFile.model, tocWriter, targetFile );
        }

        writeTagFiles( tocWriter );
        writeScenarios( tocWriter, completeReportModel.getFailedScenarios(), "Failed Scenarios", "failed.html" );
        writeScenarios( tocWriter, completeReportModel.getPendingScenarios(), "Pending Scenarios", "pending.html" );
        writeScenarios( tocWriter, completeReportModel.getAllScenarios(), "All Scenarios", "all.html" );

        StatisticsPageHtmlWriter statisticsPageHtmlWriter = new StatisticsPageHtmlWriter( tocWriter,
            completeReportModel.getTotalStatistics() );
        statisticsPageHtmlWriter.write( targetDirectory );

    }

    private void writeScenarios( HtmlTocWriter tocWriter, List<ScenarioModel> scenarios, String name, String fileName ) {
        ReportModel reportModel = new ReportModel();
        reportModel.setScenarios( scenarios );
        reportModel.setClassName( name );
        reportModel.setTagMap( this.completeReportModel.getTagIdMap() );
        ReportModelHtmlWriter.writeModelToFile( reportModel, tocWriter, new File( targetDirectory, fileName ) );
    }

    private void writeTagFiles( HtmlTocWriter tocWriter ) {
        for( Tag tag : completeReportModel.getAllTags() ) {
            writeTagFile( tag, completeReportModel.getScenariosByTag( tag ), tocWriter );
        }
    }

    private void writeTagFile( Tag tag, List<ScenarioModel> value, HtmlTocWriter tocWriter ) {
        try {
            ReportModel reportModel = new ReportModel();
            reportModel.setClassName( tag.getName() );
            if( tag.getValues().isEmpty() ) {
                reportModel.setClassName( reportModel.getClassName() + "." + tag.getValueString() );
            }
            reportModel.setScenarios( value );
            reportModel.setDescription( tag.getDescription() );
            reportModel.setTagMap( completeReportModel.getTagIdMap() );

            String fileName = HtmlTocWriter.tagToFilename( tag );
            File targetFile = new File( targetDirectory, fileName );
            ReportModelHtmlWriter.writeToFile( targetFile, reportModel, tocWriter );

        } catch( Exception e ) {
            log.error( "Error while trying to write HTML file for tag " + tag.getName(), e );
        }
    }

}
