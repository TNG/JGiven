package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tngtech.jgiven.report.model.ReportModel;

public class FrameBasedHtmlReportGenerator extends AbstractHtmlReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( FrameBasedHtmlReportGenerator.class );

    static final String LINKS_FILE_NAME = "links.html";
    static final String TESTCLASSES_FRAME_NAME = "testclasses";

    static class ModelFile {
        ReportModel model;
        File file;
    }

    private final List<ModelFile> models = Lists.newArrayList();

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
            HtmlFileWriter.writeModelToFile( model, targetFile );
            ModelFile modelFile = new ModelFile();
            modelFile.model = model;
            modelFile.file = targetFile;
            models.add( modelFile );
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
        for( ModelFile modelFile : models ) {
            writeFileLink( modelFile );
        }
    }

    private void writeFileLink( ModelFile model ) {
        writer.println( format( "<li><a href='%s' target='%s'>%s</a>",
            model.file.getName(),
            TESTCLASSES_FRAME_NAME,
            model.model.getSimpleClassName() ) );
    }

}
