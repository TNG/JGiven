package com.tngtech.jgiven.report.html;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.Tag;

public class FrameBasedHtmlReportGenerator extends AbstractHtmlReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( FrameBasedHtmlReportGenerator.class );

    static final String LINKS_FILE_NAME = "links.html";
    static final String TESTCLASSES_FRAME_NAME = "testclasses";

    static class ModelFile {
        ReportModel model;
        File file;
    }

    private final List<ModelFile> models = Lists.newArrayList();
    private final Map<Tag, List<ScenarioModel>> tagMap = Maps.newHashMap();

    public void generate( File toDir, File sourceDir ) throws IOException {
        generate( toDir, LINKS_FILE_NAME, sourceDir );
        copyFileToTargetDir( "index.html" );
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

    @Override
    public void writeEnd() {
        HtmlTocWriter tocWriter = new HtmlTocWriter( tagMap, models );
        for( ModelFile modelFile : models ) {
            HtmlWriter.writeModelToFile( modelFile.model, tocWriter, modelFile.file );
        }

        writeTagFiles( tocWriter );
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
