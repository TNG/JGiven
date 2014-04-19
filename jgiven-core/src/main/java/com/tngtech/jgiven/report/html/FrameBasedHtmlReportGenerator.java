package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
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
    private final Map<Tag, List<ScenarioModel>> srToScenariosMap = Maps.newHashMap();

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
            HtmlWriter.writeModelToFile( model, targetFile );
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
        Comparator<ModelFile> comparator = new Comparator<ModelFile>() {
            @Override
            public int compare( ModelFile o1, ModelFile o2 ) {
                return o1.model.getSimpleClassName().compareTo( o2.model.getSimpleClassName() );
            }
        };
        Collections.sort( models, comparator );
        writer.println( "<h3>Test Classes</h3>" );
        writer.println( "<ul>" );
        for( ModelFile modelFile : models ) {
            writeFileLink( modelFile );
        }
        writer.println( "</ul>" );

        writeTagFiles();
    }

    private void writeTagFiles() {
        if( srToScenariosMap.isEmpty() ) {
            return;
        }

        List<Tag> sortedTags = Lists.newArrayList( srToScenariosMap.keySet() );
        Collections.sort( sortedTags, new Comparator<Tag>() {
            @Override
            public int compare( Tag o1, Tag o2 ) {
                return o1.toString().compareTo( o2.toString() );
            }
        } );

        writer.println( "<h3>Tags</h3>" );
        writer.println( "<ul>" );
        for( Tag tag : sortedTags ) {
            writeTagFile( tag, srToScenariosMap.get( tag ) );
        }
        writer.println( "</ul>" );
    }

    private void writeFileLink( ModelFile model ) {
        writer.println( format( "<li><a href='%s' target='%s'>%s</a>",
            model.file.getName(),
            TESTCLASSES_FRAME_NAME,
            model.model.getSimpleClassName() ) );
    }

    private void writeTagFile( Tag tag, List<ScenarioModel> value ) {
        try {
            ReportModel reportModel = new ReportModel();
            reportModel.className = tag.getName();
            if( tag.getValue() != null ) {
                reportModel.className += "." + tag.getValueString();
            }
            reportModel.scenarios = value;
            reportModel.description = tag.getDescription();

            String fileName = tagToFilename( tag );
            File targetFile = new File( toDir, fileName );
            HtmlWriter.writeToFile( targetFile, reportModel );

            writer.println( format( "<li><a href='%s' target='%s'>%s</a>",
                fileName,
                TESTCLASSES_FRAME_NAME,
                tag.toString() ) );

        } catch( Exception e ) {
            log.error( "Error while trying to write HTML file for tag " + tag.getName() );
        }
    }

    static String tagToFilename( Tag tag ) {
        String fileName = escape( tag.getName() );
        if( tag.getValue() != null ) {
            if( tag.getValue().getClass().isArray() ) {
                fileName += "-" + escape( Joiner.on( '-' ).join( (String[]) tag.getValue() ) );
            } else {
                fileName += "-" + escape( (String) tag.getValue() );
            }
        }
        return fileName.substring( 0, Math.min( fileName.length(), 255 ) ) + ".html";
    }

    static String escape( String string ) {
        return string.replaceAll( "[^\\p{Alnum}-]", "_" );
    }

    private void addToMap( Tag tag, ScenarioModel scenario ) {
        List<ScenarioModel> list = srToScenariosMap.get( tag );
        if( list == null ) {
            list = Lists.newArrayList();
            srToScenariosMap.put( tag, list );
        }
        list.add( scenario );
    }
}
