package com.tngtech.jgiven.report.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.json.JsonModelTraverser;
import com.tngtech.jgiven.report.json.ReportModelFileHandler;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StatisticsCalculator;
import com.tngtech.jgiven.report.model.Tag;

public class StaticHtmlReportGenerator implements ReportModelFileHandler {
    private static final Logger log = LoggerFactory.getLogger( StaticHtmlReportGenerator.class );

    static class ModelFile {
        ReportModel model;
        File file;
    }

    private final List<ModelFile> models = Lists.newArrayList();
    private final Map<Tag, List<ScenarioModel>> tagMap = Maps.newHashMap();
    private final StatisticsCalculator statisticsCalculator = new StatisticsCalculator();

    private File toDir;

    public void generate( File toDir, File sourceDir ) throws IOException {
        this.toDir = toDir;
        new JsonModelTraverser().traverseModels( sourceDir, this );
        writeEnd();
        copyFileToTargetDir( "style.css" );
        copyFileToTargetDir( "default.css" );
        copyFileToTargetDir( "print.css" );
    }

    protected void copyFileToTargetDir( String fileName ) throws IOException {
        InputStream stream = null;
        FileOutputStream fileOutputStream = null;
        try {
            stream = this.getClass().getResourceAsStream( "/com/tngtech/jgiven/report/html/" + fileName );
            File file = new File( toDir, fileName );
            fileOutputStream = new FileOutputStream( file );
            ByteStreams.copy( stream, fileOutputStream );
        } finally {
            ResourceUtil.close( stream, fileOutputStream );
        }
    }

    @Override
    public void handleReportModel( ReportModel model, File file ) {
        String targetFileName = Files.getNameWithoutExtension( file.getName() ) + ".html";
        File targetFile = new File( toDir, targetFileName );
        log.debug( "Writing to file " + targetFile );
        try {
            ModelFile modelFile = new ModelFile();
            modelFile.model = model;
            modelFile.file = targetFile;
            models.add( modelFile );

            for( ScenarioModel scenario : model.scenarios ) {
                for( Tag tag : scenario.tags ) {
                    addToMap( tag, scenario );
                }
            }

        } catch( Exception e ) {
            log.error( "Error while trying to write to file " + file + ". " + e );
            throw Throwables.propagate( e );
        }
    }

    public void writeEnd() {
        HtmlTocWriter tocWriter = new HtmlTocWriter( tagMap, models );
        for( ModelFile modelFile : models ) {
            HtmlWriter.writeModelToFile( modelFile.model, tocWriter, modelFile.file );
        }

        writeTagFiles( tocWriter );

        writeIndexFile( tocWriter );

    }

    private void writeIndexFile( HtmlTocWriter tocWriter ) {
        File file = new File( toDir, "index.html" );
        PrintWriter printWriter = CommonReportHelper.getPrintWriter( file );
        try {
            HtmlWriter htmlWriter = new HtmlWriter( printWriter );
            htmlWriter.writeHtmlHeader( "Scenarios" );

            ReportModel reportModel = new ReportModel();
            reportModel.className = ".Scenarios";
            htmlWriter.writeHeader( reportModel );

            tocWriter.writeToc( printWriter );
            htmlWriter.visit( reportModel );

            for( Tag tag : tocWriter.getSortedTags() ) {
                printWriter.println( ScenarioHtmlWriter.tagToHtml( tag ) );
            }

            htmlWriter.visitEnd( reportModel );
            htmlWriter.writeHtmlFooter();
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    private void writeTagFiles( HtmlTocWriter tocWriter ) {
        if( tagMap.isEmpty() ) {
            return;
        }

        for( Tag tag : tagMap.keySet() ) {
            writeTagFile( tag, tagMap.get( tag ), tocWriter );
        }
    }

    private void writeTagFile( Tag tag, List<ScenarioModel> value, HtmlTocWriter tocWriter ) {
        try {
            ReportModel reportModel = new ReportModel();
            reportModel.className = tag.getName();
            if( tag.getValue() != null ) {
                reportModel.className += "." + tag.getValueString();
            }
            reportModel.scenarios = value;
            reportModel.description = tag.getDescription();

            String fileName = HtmlTocWriter.tagToFilename( tag );
            File targetFile = new File( toDir, fileName );
            HtmlWriter.writeToFile( targetFile, reportModel, tocWriter );

        } catch( Exception e ) {
            log.error( "Error while trying to write HTML file for tag " + tag.getName() );
        }
    }

    private void addToMap( Tag tag, ScenarioModel scenario ) {
        List<ScenarioModel> list = tagMap.get( tag );
        if( list == null ) {
            list = Lists.newArrayList();
            tagMap.put( tag, list );
        }
        list.add( scenario );
    }
}
