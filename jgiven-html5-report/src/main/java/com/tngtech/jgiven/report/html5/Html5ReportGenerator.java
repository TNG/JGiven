package com.tngtech.jgiven.report.html5;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ScenarioModel;

public class Html5ReportGenerator extends AbstractReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( Html5ReportGenerator.class );
    private static final int MAX_BATCH_SIZE = 100;

    private PrintStream writer;
    private MetaData metaData = new MetaData();
    private int caseCountOfCurrentBatch;
    private File dataDirectory;

    @Override
    public void generate() {
        log.info( "Generating HTML5 report to {}", new File( this.targetDirectory, "index.html" ).getAbsoluteFile() );
        this.dataDirectory = new File( this.targetDirectory, "data" );
        this.metaData.title = config.getTitle();
        if( !this.dataDirectory.exists() && !this.dataDirectory.mkdirs() ) {
            log.error( "Could not create target directory " + this.dataDirectory );
            return;
        }
        try {
            unzipApp( this.targetDirectory );
            createDataFiles();
            generateMetaData();
            generateTagFile();
            copyCustomFile( config.getCustomCssFile(), new File( this.targetDirectory, "css" ), "custom.css" );
            copyCustomFile( config.getCustomJsFile(), new File( this.targetDirectory, "js" ), "custom.js" );
        } catch( IOException e ) {
            throw Throwables.propagate( e );
        }
    }

    private void copyCustomFile( File file, File targetDirectory, String targetName ) throws IOException {
        if( file != null ) {
            if( !file.canRead() ) {
                log.info( "Cannot read " + file + ", skipping" );
            } else {
                Files.copy( file, new File( targetDirectory, targetName ) );
            }
        }
    }

    private void createDataFiles() {
        for( ReportModelFile file : completeReportModel.getAllReportModels() ) {
            handleReportModel( file.model, file.file );
        }
        closeWriter();
    }

    public void handleReportModel( ReportModel model, File file ) {
        new Html5AttachmentGenerator().generateAttachments( dataDirectory, model );

        createWriter();

        caseCountOfCurrentBatch += getCaseCount( model );

        // do not serialize tags as they are serialized separately
        model.setTagMap( null );

        new Gson().toJson( model, writer );
        writer.append( "," );

        if( caseCountOfCurrentBatch > MAX_BATCH_SIZE ) {
            closeWriter();
        }
    }

    private int getCaseCount( ReportModel model ) {

        int count = 0;
        for( ScenarioModel scenarioModel : model.getScenarios() ) {
            count += scenarioModel.getScenarioCases().size();
        }
        return count;
    }

    private void closeWriter() {
        if( writer != null ) {
            this.writer.append( "]);" );
            writer.flush();
            ResourceUtil.close( writer );
            writer = null;
            log.info( "Written " + caseCountOfCurrentBatch + " scenarios to " + metaData.data.get( metaData.data.size() - 1 ) );
        }
    }

    private void createWriter() {
        if( this.writer == null ) {
            String fileName = "data" + metaData.data.size() + ".js";
            metaData.data.add( fileName );
            File targetFile = new File( dataDirectory, fileName );
            log.debug( "Generating " + targetFile + "..." );
            caseCountOfCurrentBatch = 0;

            try {
                this.writer = new PrintStream( new FileOutputStream( targetFile ), false, "utf-8" );
                this.writer.append( "jgivenReport.addScenarios([" );
            } catch( Exception e ) {
                throw new RuntimeException( "Could not open file " + targetFile + " for writing", e );
            }
        }
    }

    static class MetaData {
        Date created = new Date();
        String title = "JGiven Report";
        List<String> data = Lists.newArrayList();
    }

    private void generateMetaData() throws IOException {
        File metaDataFile = new File( dataDirectory, "metaData.js" );
        log.debug( "Generating " + metaDataFile + "..." );

        String content = "jgivenReport.setMetaData(" + new Gson().toJson( metaData ) + " );";

        Files.write( content, metaDataFile, Charsets.UTF_8 );
    }

    private void generateTagFile() throws IOException {
        File tagFile = new File( dataDirectory, "tags.js" );
        log.debug( "Generating " + tagFile + "..." );

        TagFile tagFileContent = new TagFile();
        tagFileContent.fill( completeReportModel.getTagIdMap() );
        String content = "jgivenReport.setTags(" + new Gson().toJson( tagFileContent ) + " );";

        Files.write( content, tagFile, Charsets.UTF_8 );

    }

    protected void unzipApp( File toDir ) throws IOException {
        String appZipPath = "/" + Html5ReportGenerator.class.getPackage().getName().replace( '.', '/' ) + "/app.zip";

        log.debug( "Unzipping {}...", appZipPath );

        InputStream inputStream = this.getClass().getResourceAsStream( appZipPath );
        ZipInputStream zipInputStream = new ZipInputStream( inputStream );

        ZipEntry entry;
        while( ( entry = zipInputStream.getNextEntry() ) != null ) {
            File file = new File( toDir, entry.getName() );

            if( entry.isDirectory() ) {
                if( !file.exists() ) {
                    log.debug( "Creating directory {}...", file );
                    if( !file.mkdirs() ) {
                        throw new IOException( "Could not create directory " + file );
                    }
                }
                continue;
            }
            log.debug( "Unzipping {}...", file );

            FileOutputStream fileOutputStream = new FileOutputStream( file );

            byte[] buffer = new byte[1024];

            int len;
            while( ( len = zipInputStream.read( buffer ) ) > 0 ) {
                fileOutputStream.write( buffer, 0, len );
            }

            fileOutputStream.close();
        }
    }
}
