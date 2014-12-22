package com.tngtech.jgiven.report.html5;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.impl.FileGenerator;
import com.tngtech.jgiven.report.json.JsonModelTraverser;
import com.tngtech.jgiven.report.json.ReportModelFileHandler;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

public class Html5ReportGenerator implements ReportModelFileHandler, FileGenerator {
    private static final Logger log = LoggerFactory.getLogger( Html5ReportGenerator.class );
    private static final int MAX_BATCH_SIZE = 100;

    private PrintStream writer;
    private File targetDirectory;
    private MetaData metaData = new MetaData();
    private int caseCountOfCurrentBatch;

    @Override
    public void handleReportModel( ReportModel model, File file ) {
        model.calculateExecutionStatus();

        createWriter();

        caseCountOfCurrentBatch += getCaseCount( model );

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
            File targetFile = new File( targetDirectory, fileName );
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

    public void generate( File toDir, File sourceDir ) throws IOException {
        log.info( "Generating HTML5 report to {}...", toDir );
        this.targetDirectory = new File( toDir, "data" );
        if( !this.targetDirectory.exists() && !this.targetDirectory.mkdirs() ) {
            log.error( "Could not create target directory " + this.targetDirectory );
            return;
        }
        unzipApp( toDir );
        generateAllScenarios( toDir, sourceDir );
        generateMetaData( toDir );
    }

    static class MetaData {
        Date created = new Date();
        List<String> data = Lists.newArrayList();
    }

    private void generateMetaData( File toDir ) throws IOException {
        File metaDataFile = new File( toDir, "metaData.js" );
        log.debug( "Generating " + metaDataFile + "..." );

        String content = "jgivenReport.setMetaData(" + new Gson().toJson( metaData ) + " );";

        Files.write( content, metaDataFile, Charsets.UTF_8 );

    }

    private void generateAllScenarios( File toDir, File sourceDir ) throws IOException {
        new JsonModelTraverser().traverseModels( sourceDir, this );

        closeWriter();
    }

    private void unzipApp( File toDir ) throws IOException {
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
