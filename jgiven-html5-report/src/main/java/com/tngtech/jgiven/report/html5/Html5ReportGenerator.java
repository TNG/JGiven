package com.tngtech.jgiven.report.html5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.impl.util.Version;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;

public class Html5ReportGenerator extends AbstractReportGenerator {
    private static final Logger log = LoggerFactory.getLogger( Html5ReportGenerator.class );
    private static final int MAX_BATCH_SIZE = 100;

    private PrintStream fileStream;
    private MetaData metaData = new MetaData();
    private int caseCountOfCurrentBatch;
    private File dataDirectory;
    private ByteArrayOutputStream byteStream;
    private PrintStream contentStream;
    private Html5ReportConfig specializedConfig;

    public AbstractReportConfig createReportConfig( String... args ) {
        return new Html5ReportConfig( args );
    }

    public void generate() {
        specializedConfig = (Html5ReportConfig) config;
        log.info( "Generating HTML5 report to {}", new File( specializedConfig.getTargetDir(), "index.html" ).getAbsoluteFile() );
        this.dataDirectory = new File( specializedConfig.getTargetDir(), "data" );
        this.metaData.title = specializedConfig.getTitle();
        if( !this.dataDirectory.exists() && !this.dataDirectory.mkdirs() ) {
            log.error( "Could not create target directory " + this.dataDirectory );
            return;
        }
        try {
            unzipApp( config.getTargetDir() );
            createDataFiles();
            generateMetaData();
            generateTagFile();
            copyCustomFile( specializedConfig.getCustomCss(), new File( specializedConfig.getTargetDir(), "css" ), "custom.css" );
            copyCustomFile( specializedConfig.getCustomJs(), new File( specializedConfig.getTargetDir(), "js" ), "custom.js" );
        } catch( IOException e ) {
            throw Throwables.propagate( e );
        }
    }

    private void copyCustomFile( File file, File targetDirectory, String targetName ) throws IOException {
        if( file != null ) {
            if( !file.canRead() ) {
                log.info( "Cannot read " + file + ", skipping" );
            } else {
                targetDirectory.mkdirs();
                if( !targetDirectory.canWrite() ) {
                    String message = "Could not create directory " + targetDirectory;
                    log.error( message );
                    throw new JGivenInstallationException( message );
                }
                Files.copy( file, new File( targetDirectory, targetName ) );
            }
        }
    }

    private void createDataFiles() throws IOException {
        for( ReportModelFile file : completeReportModel.getAllReportModels() ) {
            handleReportModel( file.model, file.file );
        }
        closeWriter();
    }

    public void handleReportModel( ReportModel model, File file ) throws IOException {
        new Html5AttachmentGenerator().generateAttachments( dataDirectory, model );

        createWriter();

        if( caseCountOfCurrentBatch > 0 ) {
            contentStream.append( "," );
        }

        deleteUnusedCaseSteps( model );
        caseCountOfCurrentBatch += getCaseCount( model );

        // do not serialize tags as they are serialized separately
        model.setTagMap( null );

        new Gson().toJson( model, contentStream );

        if( caseCountOfCurrentBatch > MAX_BATCH_SIZE ) {
            closeWriter();
        }
    }

    /**
     * Deletes all steps of scenario cases where a data table
     * is generated to reduce the size of the data file. 
     * In this case only the steps of the first scenario case are actually needed. 
     */
    private void deleteUnusedCaseSteps( ReportModel model ) {

        for( ScenarioModel scenarioModel : model.getScenarios() ) {
            if( scenarioModel.isCasesAsTable() && !hasAttachment( scenarioModel ) ) {
                List<ScenarioCaseModel> cases = scenarioModel.getScenarioCases();
                for( int i = 1; i < cases.size(); i++ ) {
                    ScenarioCaseModel caseModel = cases.get( i );
                    caseModel.setSteps( Collections.<StepModel>emptyList() );
                }
            }
        }
    }

    private boolean hasAttachment( ScenarioModel scenarioModel ) {
        return hasAttachment( scenarioModel.getCase( 0 ) );
    }

    private boolean hasAttachment( ScenarioCaseModel aCase ) {
        for( StepModel model : aCase.getSteps() ) {
            if( model.hasAttachment() ) {
                return true;
            }
        }
        return false;
    }

    private int getCaseCount( ReportModel model ) {

        int count = 0;
        for( ScenarioModel scenarioModel : model.getScenarios() ) {
            count += scenarioModel.getScenarioCases().size();
        }
        return count;
    }

    private void closeWriter() throws IOException {
        if( fileStream != null ) {
            contentStream.append( "]}" );
            contentStream.flush();
            ResourceUtil.close( contentStream );
            String base64String = BaseEncoding.base64().encode( byteStream.toByteArray() );
            this.fileStream.append( "'" + base64String + "'" );
            this.fileStream.append( ");" );
            fileStream.flush();
            ResourceUtil.close( fileStream );
            fileStream = null;
            log.info( "Written " + caseCountOfCurrentBatch + " scenarios to " + metaData.data.get( metaData.data.size() - 1 ) );
        }
    }

    private void createWriter() {
        if( this.fileStream == null ) {
            String fileName = "data" + metaData.data.size() + ".js";
            metaData.data.add( fileName );
            File targetFile = new File( dataDirectory, fileName );
            log.debug( "Generating " + targetFile + "..." );
            caseCountOfCurrentBatch = 0;

            try {
                this.byteStream = new ByteArrayOutputStream();
                // pako client side library expects byte stream to be UTF-8 encoded
                this.contentStream = new PrintStream( new GZIPOutputStream( byteStream ), false, "utf-8" );
                this.contentStream.append( "{\"scenarios\":[" );

                this.fileStream = new PrintStream( new FileOutputStream( targetFile ), false, "utf-8" );
                this.fileStream.append( "jgivenReport.addZippedScenarios(" );
            } catch( Exception e ) {
                throw new RuntimeException( "Could not open file " + targetFile + " for writing", e );
            }
        }
    }

    static class MetaData {
        Date created = new Date();
        String version = Version.VERSION.toString();
        String title = "JGiven Report";
        List<String> data = Lists.newArrayList();
        Boolean showThumbnails = true;
    }

    private void generateMetaData() throws IOException {
        File metaDataFile = new File( dataDirectory, "metaData.js" );
        log.debug( "Generating " + metaDataFile + "..." );

        metaData.showThumbnails = specializedConfig.getShowThumbnails();

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
